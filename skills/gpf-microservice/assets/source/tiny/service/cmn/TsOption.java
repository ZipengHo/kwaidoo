package tiny.service.cmn;

import java.io.Serializable;
import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.regex.Pattern;

import com.leavay.common.util.ToolBasic;
import com.leavay.common.util.ToolUtilities;
import com.leavay.ms.tool.CmnUtil;

import tiny.service.cmn.member.TsMemberCache;
import tiny.service.error.TsNoMatchedRegistryException;
import tiny.service.error.TsNoValidProviderException;
import tiny.service.plugin.TsPlugin;

/**
 * 在开启微服务接口时传入，作为微服务远程调用对象的参数设置
 * 主要支持：
 *  1、路由设定： 通过给定路由ID或者给出正则表达式，来过滤选取匹配的路由
 *  2、追踪设定
 *  3、触发器Hook，利用hook可以实现熔断等功能
 *  4、参数设定，如各种超时时长、重算路由时间间隔等
 *  5、通过重载可实现个性化路由算法、动态路由等功能
 * 
 * 如果路由表里没有设定路由信息，则无条件通过
 * 如果同时指定路由ID和正则表达式，则是【或】逻辑，通常不这样用
 * 
 */
public class TsOption implements Serializable
{
    private static final long serialVersionUID = 5685794049303392977L;

    /*路由ID*/
    String routeID;
    
    /*路由正则表达式*/
    String routeExp;
    
    boolean enableTrack = TsPlugin.getPlugin().isDefaultTrackEnable();
    
    /**
     * 连接远端超时时长
     */
    long connectTimeout = TsConst.getConnectTimeout();
    
    /**
     * 接口调用超时时长
     */
    long invokeTimeout = TsConst.getInvokeTimeout();
    
    /**
     * 微服务对象内部，高频调用时，每隔多久重新计算一次路由
     * 微服务对象open开以后可以一直使用而无需重开，但是每次调用都会考虑是否需要重新计算路由，重新连接新的目标机
     */
    long rerouteInterval = TsConst.getServiceRerouteInterval();
    
    int retryTime = TsConst.getRetryTime();

    long retryInterval = TsConst.getRetryInterval();
    
    
    TsHook hook = null;
    
    public final static TsOption _default = new TsOption();
    
    public static TsOption getDefault()
    {
        return _default;
    }
    
    public static TsOption build()
    {
        return new TsOption();
    }
    
    // 构建ID式路由过滤器
    public static TsOption buildId(String routeID)
    {
        return new TsOption().setRouteID(routeID);
    }
    
    // 构建正则表达式路由过滤器
    public static TsOption buildExp(String routeRegExp)
    {
        return new TsOption().setRouteExp(routeRegExp);
    }
    
    public String getRouteID()
    {
        return routeID;
    }

    public TsOption setRouteID(String routeID)
    {
        this.routeID = routeID;
        return this;
    }

    public String getRouteExp()
    {
        return routeExp;
    }

    public TsOption setRouteExp(String routeExp)
    {
        this.routeExp = routeExp;
        return this;
    }
    
    public boolean hasRouteID()
    {
        return !CmnUtil.isStringEmpty(routeID);
    }
    
    public boolean hasRouteExp()
    {
        return !CmnUtil.isStringEmpty(routeExp);
    }

    public TsHook getHook()
    {
        return hook;
    }

    public TsOption setHook(TsHook hook)
    {
        this.hook = hook;
        return this;
    }

    public boolean isEnableTrack()
    {
        return enableTrack;
    }

    public TsOption setEnableTrack(boolean enableTrack)
    {
        this.enableTrack = enableTrack;
        return this;
    }

    public int getRetryTime()
    {
        return retryTime;
    }

    /**
     * 当重试（重新尝试分配服务提供者时）sleep间隔时间
     * 默认重试2次，那么总共就是执行3次
     */
    public TsOption setRetryTime(int retryTime)
    {
        this.retryTime = retryTime;
        return this;
    }

    public long getRetryInterval()
    {
        return retryInterval;
    }

    /** 当重试（重新尝试分配服务提供者时）sleep间隔时间，默认1000毫秒*/
    public TsOption setRetryInterval(long retryInterval)
    {
        this.retryInterval = retryInterval;
        return this;
    }

    public long getRerouteInterval()
    {
        return rerouteInterval;
    }

    /**
     * 每隔多少ms就重新计算一次路由，如果小于零，则每次都会重算路由
     */
    public TsOption setRerouteInterval(long rerouteInterval)
    {
        this.rerouteInterval = rerouteInterval;
        return this;
    }

    public static long getRerouteInterval(TsOption option)
    {
        return option==null?TsConst.getServiceRerouteInterval():option.getRerouteInterval();
    }

    /**
     * 判断给定的代理（Provider）是否满足服务注册以及请求中的路由信息的联合约束
     * 1、如果该服务没有路由设定（表示不限制路由），那么所有公共代理都可以提供该服务（不推荐这样设定）
     * 2、如果服务注册中设定了路由表，那么该路由表需要和请求中的路由信息比对，匹配的才能提供服务
     * 3、如果服务路由表中没有限定路由ID，而Option也没有设定路由ID（或表达式），则认为匹配
     *       
     * 表达式样例：任意字符数字 =^[A-Za-z0-9]+$
     * 
     * @param agentID ：代理RDN（或者第三方服务注册ID）
     * @param registry ：服务注册信息
     * @param req ：请求
     */
    public boolean isRouteMatch(TsRegAgentPath registryPath)
    {
        // 注册表里Entry的路由ID
        String entryRt = CmnUtil.getString(registryPath==null?null:registryPath.getRouteID(), "").trim();
        
        // 没有路由设定，则默认匹配那些没有设定路由ID的服务
        if (!hasRouteID() && !hasRouteExp())
        {
            // 没有限定路由，同时也没有过滤器，则认为匹配
            if (CmnUtil.isStringEmpty(entryRt))
                return true;
        }
        
        if (hasRouteID())
        {
            // Route ID匹配
            String reqRt = CmnUtil.getString(getRouteID(), "").trim();
    
            return CmnUtil.isStringEqual(entryRt, reqRt);
        } else if (hasRouteExp())
        {
            // Route Pattern Regex正则表达式匹配
            if (Pattern.matches(getRouteExp(), CmnUtil.getString(entryRt, "")))
                return true;
        }

        return false;
    }
    
    
    public long getConnectTimeout()
    {
        return connectTimeout;
    }

    public TsOption setConnectTimeout(long connectTimeout)
    {
        this.connectTimeout = connectTimeout;
        return this;
    }
    
    public static long getConnectTimeout(TsOption option)
    {
        return option==null?TsConst.getConnectTimeout():option.getConnectTimeout();
    }

    public long getInvokeTimeout()
    {
        return invokeTimeout;
    }

    public TsOption setInvokeTimeout(long invokeTimeout)
    {
        this.invokeTimeout = invokeTimeout;
        return this;
    }
    
    public static long getInvokeTimeout(TsOption option)
    {
        return option==null?TsConst.getInvokeTimeout():option.getInvokeTimeout();
    }

    public String toString()
    {
        return CmnUtil.getNameAndLabel(getRouteID(), getRouteExp());
    }
    
    /**
     * 根据服务注册表，计算出符合路由且有loading的代理列表
    // 传入路由ID，或者传入路由表达式，两者以与关系进行计算
    // 如果没有指定路由，但是传入了限定路由，则报错
     * 
     * 注：option不可以为空，必须有
     * 
     * @return Key=AgentUri, Value=Loading
     */
    public Map<String, Integer> prepareValidRoute(String serviceKey) throws TsNoMatchedRegistryException, TsNoValidProviderException
    {
        TsPlugin tsPlugin = TsPlugin.getPlugin();
        
        TsRegEntryInfo regEntry = TsMemberCache.get().getRegistryEntry(serviceKey);
        if (CmnUtil.isObjectEmpty(regEntry))
            throw new TsNoMatchedRegistryException("There isn't registried entry for service : " + serviceKey);
        
        Map<String, Integer> mapRet = new HashMap<String, Integer>();
        
        if (CmnUtil.isObjectEmpty(regEntry.getRouteTable()))
        {
            if (hasRouteID() || hasRouteExp())
                throw new TsNoMatchedRegistryException("RouteID or expression mismatch with NULL route table ["+serviceKey+"] : " + this+" -> " + serviceKey);
            
            //没有特定路由设定，则表示全网均可
            mapRet = TsMemberCache.get().peekLoadingMap();
            if (CmnUtil.isObjectEmpty(mapRet))
                throw new TsNoValidProviderException("There isn't any alive agents");
        }else
        {
            // 有路由设定的，则以路由表为基准，遍历查找匹配的代理
            for (TsRegAgentPath rtPath : regEntry.getRouteTable())
            {
                Integer phyLoading = TsMemberCache.get().getPhyLoading(rtPath.getAgent());
                if (phyLoading == null) // 物理负载获取不到，说明最近一次同步里没有该代理（视为不存在）
                    continue;
                
                // 路由匹配
                if (isRouteMatch(rtPath))
                    mapRet.put(rtPath.getAgent(), phyLoading);
            }
            if (CmnUtil.isObjectEmpty(mapRet))
                throw new TsNoValidProviderException("There isn't any matched agents, please check route setting for ["+serviceKey+"]. Current Route Filter="+this+", Registry=" + regEntry);
        }
        
        // 黑名单排除
        if (!CmnUtil.isObjectEmpty(regEntry.getExcludeAgents()))
        {
            for (String exclude : regEntry.getExcludeAgents())
                mapRet.remove(exclude);
        }

        if (CmnUtil.isObjectEmpty(mapRet))
            throw new TsNoMatchedRegistryException("There isn't any matched agents. Please check exclude setting ["+serviceKey+"]= " + ToolBasic.logString(regEntry.getExcludeAgents(), 20, false));
        
        for (Entry<String, Integer> ent : mapRet.entrySet())
        {
            String agtUri = ent.getKey();
            int loading = ent.getValue();
            
            // 如果是同进程，加权
            if (agtUri.equals(TsHelper.getMyUri()))
                loading -= tsPlugin.getLocalProcessRight();
            else
            {
                // 如果有相同IP，加权
                for (String myIp : ToolUtilities.getCurrentIPList())
                    if (agtUri.indexOf(myIp) > 0)
                        loading -= tsPlugin.getLocalMachineRight();
            }

            // 将本地虚拟loading加入计算
            loading += TsMemberCache.get().getVirtualLoading(ent.getKey());
            
            ent.setValue(loading);
        }

        List<String> lstRemoved = new LinkedList<String>();
        for (String s : TsMemberCache.get().getUnhealthList())
            if (mapRet.remove(s) != null)
                lstRemoved.add(s);
        
        if (CmnUtil.isObjectEmpty(mapRet))
            throw new TsNoValidProviderException("There isn't valid and healthy provider  ["+serviceKey+"]. Unhealth List : " + ToolBasic.logString(lstRemoved, false));
       
        
        return mapRet;
    }
}
