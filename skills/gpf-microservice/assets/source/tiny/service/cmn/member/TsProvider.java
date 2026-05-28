package tiny.service.cmn.member;

import java.util.LinkedList;
import java.util.List;

import com.leavay.common.util.ToolUtilities;
import com.leavay.ms.tool.CmnUtil;
import com.leavay.nio.crpc.CRpcRequest;
import com.leavay.nio.crpc.TsRequest;

import bap.cells.Cells;
import tiny.service.cmn.TsHelper;
import tiny.service.cmn.TsHelperOp;
import tiny.service.cmn.TsProviderHook;
import tiny.service.cmn.TsRegEntryInfo;
import tiny.service.error.TsNoProvideServiceException;
import tiny.service.error.TsProviderHookException;
import tiny.service.error.TsServiceExecuteException;

/**
 * 作为微服务提供者，提供TS请求调用的执行，和CRPC深度整合

 * 在CRPC的TsRpcExecuter中会通过反射硬耦合到这里来，接管RPC的调用执行逻辑
 *
 */
public class TsProvider
{
    private static TsProvider _me = null;

    // 只支持单线程内追踪
    private static ThreadLocal<List<TsRequest>> _callStack = new ThreadLocal<List<TsRequest>>();
    
    public synchronized static TsProvider get()
    {
        if (_me == null)
        {
            _me = new TsProvider();
        }

        return _me;
    }

    protected TsProvider()
    {
    } 
    
    private static void pushStack(TsRequest req)
    {
        List<TsRequest> lstStack = _callStack.get();
        if (lstStack == null)
            _callStack.set(lstStack = new LinkedList<TsRequest>());

        lstStack.add(0, req);
    }
    
    private static TsRequest popStack()
    {
        List<TsRequest> lstStack = _callStack.get();
        if (lstStack != null)
            return lstStack.remove(0);
        return null;
    }
    
    // 获取当前调用栈的最近一个元素，得到父调用请求
    public static TsRequest getCurrentStack()
    {
        List<TsRequest> lstStack = _callStack.get();
        if (!CmnUtil.isObjectEmpty(lstStack))
            return lstStack.get(0);

        return null;
    }
    
    /**
     * 这个方法名会被TsRpcExecuter反射调用，切记保持其唯一性
     * 从CRPC底层，反射调用到这里来
     */
    private static Object __executeRpcRequest(CRpcRequest req) throws Exception
    {
        return get().executeRpcRequest(req);
    }
    
    public Object executeRpcRequest(CRpcRequest req) throws Exception
    {
        TsRequest tsReq = (TsRequest)req;
        
        String srvClass = req.getClassName();
        TsProviderHook hook = null;

        // 先尝试查找接口映射的服务，没有就直接new Instance
        TsRegEntryInfo regInfo = TsMemberCache.get().getRegistryEntry(req.getClassName());
        if (regInfo != null)
        {
            if (!ToolUtilities.isStringEmpty(regInfo.getServiceImplement()))
                srvClass = regInfo.getServiceImplement();
            
            // 获取Hook
            hook = getHook(regInfo.getProviderHook());
        }
        
        if (hook != null)
            hook.before(tsReq); // 执行Hook
        
        if (tsReq.isTrackEnable())
            pushStack(tsReq); // 记录调用链，追踪父子调用关系
        try
        {
            // 微服务有点不一样，优先以注册表里的实现类为准，如果不存在才用细胞工厂构建服务细胞
            Object o = TsHelperOp.createObjectInstance(srvClass);
            if (o == null)
            {
                if (Cells.isValidCell(req.getClassName()))
                    o = Cells.get(req.getClassName());
            }
            
            if (o == null)
                throw new TsNoProvideServiceException("Not support this service : " + srvClass+" <"+TsHelper.getMyUri()+">");
            
            // Callback类型的入参需要转换成proxy
            Object ret = ToolUtilities.callFunction(o, req.getFunction(), req.getParams());
            
            if (hook != null)
                hook.after(tsReq, ret); // 执行Hook
            
            return ret;
        }catch(Throwable err)
        {
            if (hook != null)
                hook.failed(tsReq, err); // 执行Hook
            
            throw new TsServiceExecuteException("Failed to execute service function [" + srvClass+"::"+req.getFunction() + "]"+" <"+TsHelper.getMyUri()+">", err, tsReq.isCompressExceptionStack());
        } finally
        {
            if (tsReq.isTrackEnable())
                popStack();
        }
    }
    
    public TsProviderHook getHook(String hookClass) throws TsProviderHookException
    {
        if (CmnUtil.isStringEmpty(hookClass))
            return null;
        
        try
        {
            return (TsProviderHook)TsHelperOp.createObjectInstance(hookClass);
        } catch (ClassCastException castErr)
        {
            throw new TsProviderHookException("'" +hookClass+"' is not a correct provider hook class(Inherit from TsProviderHook)", castErr);
        }
        catch (Throwable err)
        {
            throw new TsProviderHookException("Failed to create instance of provider hook : " + hookClass, err);
        }
    }
}
