package tiny.service.cmn.member;

import java.util.concurrent.locks.ReentrantLock;

import com.leavay.common.util.ToolUtilities;
import com.leavay.ms.tool.CmnUtil;
import com.leavay.nio.crpc.CRpcClient;
import com.leavay.nio.crpc.CRpcProxyCaller;
import com.leavay.nio.crpc.CRpcRequest;
import com.leavay.nio.crpc.CRpcUtil;
import com.leavay.nio.crpc.TsRequest;

import cmn.util.AutoLock;
import tiny.service.cmn.TsHelperOp;
import tiny.service.cmn.TsHook;
import tiny.service.cmn.TsOption;
import tiny.service.cmn.TsRegEntryInfo;
import tiny.service.error.TsProviderHookException;
import tiny.service.track.bean.TsTrackBean;

public class TsRpcProxyCaller extends CRpcProxyCaller
{
    protected Class intfClass; // 必不为空
    protected TsOption option; // 必不为空
    
    /**
     * @param intfClass 不得为空
     * @param option 可以传入空表示默认值
     */
    public TsRpcProxyCaller(Class intfClass, TsOption option)
    {
        this.intfClass = CmnUtil.assertNotNull(intfClass, "NULL service interface class");
        // 默认必须有一个参数设定
        this.option = option==null?TsOption.getDefault():option;
        
        setTimeout(TsOption.getInvokeTimeout(option));
    }

    public TsOption getOption()
    {
        return option;
    }
    
    // 动态分配目标URI
    protected volatile long lastTime = -1;
    public CRpcClient getClient()
    {
        long now = System.currentTimeMillis();
        if (_localClient == null || now - lastTime >= getOption().getRerouteInterval())
        {
            // 触发重连（内部还会再次校验时间，避免并发同时触发多次重路由）
            try
            {
                reconnect(now);
            } catch (Exception exp)
            {
                ToolUtilities.throwRuntimeException(exp);
            }
        }

        return super.getClient();
    }
    
    // 算路由、重路由、重连
    ReentrantLock _lockSetupClient = new ReentrantLock(); // 只锁要修改client的动作，其它并发依然允许使用老的_localClient
    public void reconnect(long now) throws Exception
    {
        try (AutoLock lc = AutoLock.lock(_lockSetupClient))
        {
            // 时效内，不重连
            if (_localClient != null && now - lastTime <= getOption().getRerouteInterval())
                return;
                
            CRpcClient newClient = TsMember.get().allocRpcClient(intfClass, getOption());
            CmnUtil.assertNotNull(newClient, "Failed to allocate client for service : " + intfClass+", Route Option="+getOption());
            
            setClient(newClient);
            lastTime = System.currentTimeMillis();
        }
    }
    
    public long getConnectTimeout()
    {
        return TsOption.getConnectTimeout(getOption());
    }
    
    public void setConnectTimeout(long connectTimeout)
    {
        throw new RuntimeException("Denied this operation");
    }
    
    public boolean calcTrackEnable(TsRegEntryInfo regEntry)
    {
        // 优先判断当前调用栈，以父请求为准（如果有父调用）
        TsRequest parentReq = TsProvider.getCurrentStack();
        if (parentReq != null && parentReq.isTrackEnable())
            return true;
        
        if (getOption().isEnableTrack())
            return true;
        
        return regEntry.isEnableTrack();
    }

    /**
     * 重载父类执行入口，从这里走不一样的分支
     * 
     * Hook在这一层掌管
     */
    public Object invoke(CRpcRequest req) throws Exception
    {
        TsRequest tsReq = (TsRequest)req;

        TsRegEntryInfo regEntry = TsMemberCache.get().getRegistryEntry(tsReq.getClassName());
        CmnUtil.assertNotNull(regEntry, "There isn't valid service registry for : "+ tsReq.getClassName());
        
        TsHook hook = getHook(regEntry, tsReq);
        CRpcClient client = null;
        try
        {
            TsHook.handleBefore(hook, client, tsReq);// 触发hook
            
            // 连接远端，适时自动重算路由（需要的时候）
            client = getClient();
            
            // 执行
            Object ret = invokeTsRpc(client, regEntry, tsReq);
            
            TsHook.handleAfter(hook, client, tsReq, ret);  // 触发hook
            
            return ret;
        }catch (Throwable err)
        {
            TsHook.handleError(hook, client, tsReq, err);// 触发hook
            
            return ToolUtilities.throwException(err);
        }
    }
    
    // 真正执行远程调用（RPC方法调用）
    public Object invokeTsRpc(CRpcClient client, TsRegEntryInfo regEntry, TsRequest tsReq) throws Exception
    {
        TsTrackBean startBean = null;
        
        // 增加虚拟负载（只增不减比较符合实际需要）
        boolean trackEnable = tsReq.isTrackEnable();
        TsMemberCache.get().incVirtualLoading(client.getUri(), 1);
        try
        {
            // 如果请求本身没有带追踪标志，则计算一次是否追踪
            if (!trackEnable)
                trackEnable = calcTrackEnable(regEntry);
            
            if (trackEnable)
            {
                tsReq.setTrackEnable(true);
                startBean = TsMember.get().reportTrackStart(TsProvider.getCurrentStack(), tsReq, getClient());
            }
            
            Object ret = super.realInvoke(client, tsReq);
            
            if (trackEnable)
                TsMember.get().reportTrackEnd(tsReq, client, startBean.getStartTime());

            return ret;
        } catch (Throwable err)
        {
            if (CRpcUtil.isRemoteUnhealthyException(err))
                TsMemberCache.get().reportUnhealth(client.getUri().toString());
            
            // 发送异常追踪
            if (trackEnable)
                TsMember.get().reportTrackError(startBean, err);
            
            return CmnUtil.throwException(err);
        }
    }

    public TsHook getHook(TsRegEntryInfo regEntry, TsRequest tsReq) throws TsProviderHookException
    {
        TsHook localHook = getOption().getHook();
        if (localHook != null)
            return localHook;
        
        if (regEntry == null)
            return null;
        
        String sHook = regEntry.getConsumerHook();
        if (CmnUtil.isStringEmpty(sHook))
            return null;
        
        try
        {
            return (TsHook)TsHelperOp.createObjectInstance(sHook);
        } catch (ClassCastException castErr)
        {
            throw new TsProviderHookException("'" +sHook+"' is not a correct consumer hook class(Inherit from TsHook)", castErr);
        }
        catch (Throwable err)
        {
            throw new TsProviderHookException("Failed to create instance of consumer hook : " + sHook, err);
        }
    }
}
