package tiny.service.cmn;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import com.leavay.common.util.ToolUtilities;
import com.leavay.common.util.cjson.CJson;

/**
 * 服务注册表
 * 
 * 是否追踪，主要由如下几个因素决定，按默认false，逐级判断是否为true的逻辑，排序如下：
 *      1、请求中是否开启追踪（TsHook可以干预请求对象）
 *      2、当前调用栈是否存在父调用，如果父调用开启追踪则遗传追踪标志
 *      3、注册表对于该服务是否开启了追踪
 *      4、最后看看插件中默认全局是否开启追踪
 */
public class TsRegEntryInfo implements Serializable
{
    private static final long serialVersionUID = 2637950349069501160L;
    public String regRdn;
    public String serviceKey;
    public String serviceLabel;
    public String desc;
    public String serviceVersion;
    public String serviceImplement; // 用于接口映射实现类
    public String localSimulator; // 用于接口仿真调试
    public String restfulPerm; //restful调用时的必要权限

    public String consumerHook; //在consumer侧，执行该hook对应的类，这个类必须派生于TsProviderHook，可以带有getMe方法作为singleton对象
    
    public String providerHook; //在provider侧，执行该hook对应的类，这个类必须派生于TsProviderHook，可以带有getMe方法作为singleton对象
    
    public List<TsRegAgentPath> routeTable;
    public List<String> excludeAgents;
    
    public boolean enableTrack=false; // 是否追踪该服务，这是全局设定，每次发起调用可通过option单独设定（默认false）
    
    public boolean isSystemFixed=false; // 系统虚拟出来，固定不可修改的服务
    
    public String getRegRdn()
    {
        return regRdn;
    }
    public void setRegRdn(String regRdn)
    {
        this.regRdn = regRdn;
    }
    public String getServiceKey()
    {
        return serviceKey;
    }
    public void setServiceKey(String serviceKey)
    {
        this.serviceKey = serviceKey;
    }
    
    public String getServiceLabel()
    {
        return serviceLabel;
    }
    public void setServiceLabel(String serviceLabel)
    {
        this.serviceLabel = serviceLabel;
    }
    public String getDesc()
    {
        return desc;
    }
    public void setDesc(String desc)
    {
        this.desc = desc;
    }
    public String getServiceVersion()
    {
        return serviceVersion;
    }
    public void setServiceVersion(String serviceVersion)
    {
        this.serviceVersion = serviceVersion;
    }
    public String getLocalSimulator()
    {
        return localSimulator;
    }
    public void setLocalSimulator(String localSimulator)
    {
        this.localSimulator = localSimulator;
    }
    public List<TsRegAgentPath> getRouteTable()
    {
        return routeTable;
    }

    public String getRestfulPerm() {
        return restfulPerm;
    }

    public void setRestfulPerm(String restfulPerm) {
        this.restfulPerm = restfulPerm;
    }

    public void setRouteTable(List<TsRegAgentPath> includeAgents)
    {
        this.routeTable = includeAgents;
    }
    
    public void addRoutePath(TsRegAgentPath agt)
    {
        if (routeTable == null)
            routeTable = new ArrayList();
        routeTable.add(agt);
    }
    
    
    public void removeRoutePath(String agent)
    {
        if (routeTable == null)
            return;
        
        for (Iterator<TsRegAgentPath> it = routeTable.iterator(); it.hasNext();)
        {
            TsRegAgentPath org = it.next();
            if (ToolUtilities.isStringEqual(org.getAgent(), agent))
                it.remove();
        }
    }
    
    /*
     * 比较特殊，用新的路由表去覆盖现有的，但是新的路由表中出现的agent，会先全删除
     */
    public void overwriteRouteTable(List<TsRegAgentPath> rTable)
    {
        for (TsRegAgentPath newP : rTable)
            removeRoutePath(newP.getAgent());
        
        for (TsRegAgentPath newP : rTable)
            addRoutePath(newP);
    }
    
    public void removeRoutePath(TsRegAgentPath agt)
    {
        if (routeTable != null)
        {
            routeTable.remove(agt);
            if (routeTable.isEmpty())
                routeTable = null;
        }
    }
    
    public List<String> getExcludeAgents()
    {
        return excludeAgents;
    }
    public void setExcludeAgents(List<String> excludeAgents)
    {
        this.excludeAgents = excludeAgents;
    }
    
    public void addExcludeAgent(String agt)
    {
        if (excludeAgents == null)
            excludeAgents = new ArrayList();
        excludeAgents.add(agt);
    }
    
    public boolean isContainExclude(String agtID)
    {
        return excludeAgents != null && excludeAgents.contains(agtID);
    }
    
    public void removeExcludeAgent(String agt)
    {
        if (excludeAgents != null)
        {
            excludeAgents.remove(agt);
            if (excludeAgents.isEmpty())
                excludeAgents = null;
        }
    }

    public String getServiceImplement()
    {
        return serviceImplement;
    }
    
    public void setServiceImplement(String serviceImplement)
    {
        this.serviceImplement = serviceImplement;
    }
        
    public boolean isSystemFixed()
    {
        return isSystemFixed;
    }
    
    public void setSystemFixed(boolean isSystemFixed)
    {
        this.isSystemFixed = isSystemFixed;
    }
    
    public String getProviderHook()
    {
        return providerHook;
    }
    
    public void setProviderHook(String providerHook)
    {
        this.providerHook = providerHook;
    }
    
    public String getConsumerHook()
    {
        return consumerHook;
    }
    
    public void setConsumerHook(String consumerHook)
    {
        this.consumerHook = consumerHook;
    }
    
    public boolean isEnableTrack()
    {
        return enableTrack;
    }
    
    public void setEnableTrack(boolean enableTrack)
    {
        this.enableTrack = enableTrack;
    }
    
    public String toJson()
    {
        return CJson.toJson(this);
    }
    
    public static TsRegEntryInfo fromJson(String jsonText) throws Exception
    {
        return (TsRegEntryInfo) CJson.fromJson(jsonText);
    }
    
    public String toString()
    {
        return getServiceKey()+"->"+ToolUtilities.logString(getRouteTable(), false)+" !-" + ToolUtilities.logString(getExcludeAgents(), false);
    }
}
