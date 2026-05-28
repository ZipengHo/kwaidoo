package tiny.service.cmn;

import java.net.URI;
import java.util.List;
import java.util.function.Function;

import com.leavay.common.nio.ws.CWsUtil;
import com.leavay.common.util.MppContext;
import com.leavay.common.util.ToolBasic;
import com.leavay.common.util.ToolUtilities;
import com.leavay.ms.tool.CmnUtil;
import com.leavay.nio.crpc.CRpcAdapter;
import com.leavay.nio.crpc.CRpcClientFactory;
import com.leavay.nio.crpc.CRpcClientWrapper;
import com.leavay.nio.crpc.CRpcServer;
import com.leavay.nio.crpc.CRpcUtil;

import tiny.service.cmn.member.TsConsumer;
import tiny.service.cmn.member.TsMember;
import tiny.service.cmn.member.TsMemberCache;
import tiny.service.md.TsAlarm;

/**
 * 整个TiniService（小微服务）集群中有三种角色，分别是：首领（Chief）、随从（Servant）、成员（Member）
 * 
 * 首领+随从构成管理层，所有成员面向管理层获取服务，以多名随从+首领的方式提供容灾能力
 * 
 * 首领（Chief）：提供集群代理管理、服务注册表增删、随从列表服务以及随从（Servant）所有服务
 * 随从（Servant）：以DAO仆从跟随首领，提供管理层列表查询、随从列表、成员负载缓存等服务
 * 成员（Member）：寻找管理层获取服务、路由等，提供最终的小微服务调用
 * 消费者（Consumer）：纯消费微服务，本身不作为微代理接入，不提供服务，也不上报状态，但可以调用微服务
 *                                          【注】：consumer比较特殊，启动了就不能启动member、servant、chief
 * 
 * 首领+随从 组成了公务组，负责对所有成员提供集群相关服务
 *
 *  首领同时具有随从以及成员的角色
 *  随从同时具有成员的角色
 *  成员则不具备任何管理功能
 */
public class TsHelper
{
    protected static boolean _isChief = false;
    protected static boolean _isServant = false;
    protected static boolean _isMember = false;
    protected static boolean _isConsumer = false;
    
    protected static boolean _chiefStarted = false;
    protected static boolean _servantStarted = false;
    protected static boolean _memberStarted = false;
    protected static boolean _consumerStarted = false;
    
    
    public static boolean isChief()
    {
        return _isChief;
    }

    public static boolean isServant()
    {
        return _isServant;
    }

    public static boolean isMember()
    {
        return _isMember;
    }

    public static boolean isConsumer()
    {
        return _isConsumer;
    }

    public static boolean isChiefStarted()
    {
        return _chiefStarted;
    }

    public static boolean isServantStarted()
    {
        return _servantStarted;
    }

    public static boolean isMemberStarted()
    {
        return _memberStarted;
    }

    public static boolean isStarted()
    {
        // 不论chief、servant、member，最后都是要启动member的
        return isMemberStarted();
    }
    
    static URI _uri = null;
    static String _uriTxt = null;
    public synchronized static URI getMyUri()
    {
        if (_uri == null)
        {
            String uri = null;
            try
            {
                uri = MppContext.getString(TsConst.CONF_MY_URI, "").toLowerCase(); // 强转小写
                CmnUtil.assertNotEmpty(uri, "Please define my URI in config of '"+TsConst.MODULE_CHIEF+"'");

                _uriTxt = uri;
                _uri = CWsUtil.buildURI(uri);
            } catch (Throwable err)
            {
                throw new RuntimeException("Please check config of Tiny Service : " + TsConst.CONF_MY_URI+"="+uri, err);
            }
        }
        
        return _uri;
    }
    public synchronized static String getMyUriText()
    {
        if (_uriTxt == null)
        {
            _uriTxt = getMyUri().toString();
        }
        return _uriTxt;
    }
    
    protected static Object lockChief = new Object();
    protected static URI _chiefUri = null;
    public static URI getChiefUri()
    {
        if (isChief())
            return getMyUri();
        
        synchronized (lockChief)
        {
            if (_chiefUri == null)
            {
                String uri = null;
                try
                {
                    uri = MppContext.getString(TsConst.CONF_CHIEF_URI, "").toLowerCase();
                    CmnUtil.assertNotEmpty(uri, "Chief URI is NULL");
    
                    _chiefUri = CWsUtil.buildURI(uri);
                } catch (Throwable err)
                {
                    throw new RuntimeException("Please check config of Tiny Service : " + TsConst.CONF_CHIEF_URI+"="+uri, err);
                }
            }
            
            return _chiefUri;
        }
    }
    
    static CRpcClientWrapper<TsChiefIntf> _chiefIntf = new CRpcClientWrapper<TsChiefIntf>(TsChiefIntf.class)
    {
        public CRpcAdapter getAdapter()
        {
            return CRpcAdapter.get();
        }
    };
    
    public static TsChiefIntf getChiefIntf()
    {
        synchronized (lockChief)
        {
            if (_chiefIntf.getUri() == null)
                _chiefIntf.init(getChiefUri());
            
            return _chiefIntf.getIntf(true);
        }
    }
    
    public static CRpcClientWrapper<TsChiefIntf> getChiefIntfWrapper()
    {
        return _chiefIntf;
    }
    
    protected static TsClusterIntf _localServant = null;
    public static TsClusterIntf getLocalServantIntf()
    {
        return _localServant;
    }
    
    /**
     * 提供微服务集群管理组的远程接口
     * 尝试随机返回一个可达的随从，如果随从都不可达则返回首领接口
     */
    public static TsClusterIntf prepareServantIntf()
    {
        if (isServantStarted())
        {
            return getLocalServantIntf();
        }
        
        // 找一个可连通的随从
        List<String> lstSvnt = TsMemberCache.get().peekServantList(true);
        for (String agtUri : lstSvnt)
        {
            try
            {
                TsClusterIntf intf = CRpcAdapter.get().openService(CmnUtil.buildWsUri(agtUri), TsClusterIntf.class, TsConst.getMemberContactTimeout());
                intf.ping();
                TsMemberCache.get().reportHealth(agtUri);
                return intf;
            } catch (Throwable err)
            {
                if (CRpcUtil.isRemoteUnhealthyException(err))
                {
                    ToolUtilities.error(TsConst.LOG, "Some servant is unhealthy : " + agtUri, err);
                    TsMemberCache.get().reportUnhealth(agtUri);
                }
            }
        }
        
        // 最后找不到随从上报，只好返回首领
        return getChiefIntf();
    }
    
    // 通过RPC调用随从（或首领）提供的微服务集群（TsClusterIntf）相关服务
    public static <T> T callServant(Function<TsClusterIntf, T> func) throws Exception
    {
        if (isServantStarted())
        {
            return func.apply(getLocalServantIntf());
        }
        
        // 找一个可连通的随从
        List<String> lstSvnt = TsMemberCache.get().peekServantList(true);
        for (String agtUri : lstSvnt)
        {
            try
            {
                TsClusterIntf intf = CRpcAdapter.get().openService(CmnUtil.buildWsUri(agtUri), TsClusterIntf.class, TsConst.getMemberContactTimeout(), TsConst.getMemberContactTimeout());
                T t = func.apply(intf);
                
                TsMemberCache.get().reportHealth(agtUri);
                return t;
            } catch (Throwable err)
            {
                if (CRpcUtil.isRemoteUnhealthyException(err))
                {
                    ToolUtilities.error(TsConst.LOG, "Some servant is invalid : " + agtUri, err);
                    TsMemberCache.get().reportUnhealth(agtUri);
                }
            }
        }
        
        // 最后找不到随从上报，只好返回首领
        return func.apply(getChiefIntf());
    }
    
    public static void verifyMyUri()
    {
        URI myUri = TsHelper.getMyUri();
        
        if (ToolBasic.isLocalHost(myUri.getHost()))
            throw new RuntimeException("Please set absolute address as my URI : " + TsConst.CONF_MY_URI);
        
        if (CRpcServer.getNioPort() != myUri.getPort())
        {
            ToolUtilities.warnAndOutput(TsConst.LOG, "Please check config about NIO port(RPC). It is unmatched with my URI : " + myUri+" != "+CRpcServer.getNioPort());
        }
        
        try
        {
            CRpcClientFactory.getInstance().connectClient(myUri, 2000);
        } catch (Throwable err)
        {
            throw new RuntimeException("Please check configuration of Tiny Service. Failed to connect my URI : " + myUri, err);
        }
    }
    
    /**
     * 开启微服务对象，该对象可以反复使用、多线程并发使用，内部会适时更新路由、重连等
     * 
     * @param intfClass 微服务接口类（接口标识）
     * @param option 参数设定：路由ID、超时时长、路由算法、是否追踪等
     */
    public static <T> T openService(Class<T> intfClass, TsOption option) throws Exception
    {
        if (_isConsumer)
            return TsConsumer.get().openService(intfClass, option);
        else
            return TsMember.get().openService(intfClass, option);
    }

    public void reportAlarm(TsAlarm alarm) throws Exception
    {
        TsMember.get().reportAlarm(alarm);
    }
}
