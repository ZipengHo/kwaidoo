package tiny.service.cmn;

import com.leavay.nio.crpc.CRpcClient;
import com.leavay.nio.crpc.TsRequest;

/**
 * 调用端（Consumer）Hook，通常设定在注册表的Consumer Hook中，也可以在某个特定请求的TsOption中
 * 
 * 通常可用于熔断、调用统计、动态追踪等
 */
public interface TsHook
{
    /**
     * 执行微服务远程调用前
     * 
     * 这个调用甚至比判断是否追踪更早，因此可以在这里实现动态干预是否追踪本次服务的调用链
     * 
     * @param target ：调用的目标机连接对象
     * @param request ：调用请求
     */
    public void before(CRpcClient target, TsRequest request);
    
    /**
     * 执行微服务远程调用之后
     * 
     * @param target ：调用的目标机连接对象
     * @param request ：调用请求
     * @param result ：远程返回的结果
     */
    public void after(CRpcClient target, TsRequest request, Object result);
    
    /**
     * 执行微服务远程调用出错时
     * 
     * @param target ：调用的目标机连接对象
     * @param request ：调用请求
     * @param error ：出错异常
     */
    public void failed(CRpcClient target, TsRequest request, Throwable error);
    
    public static void handleBefore(TsHook hook, CRpcClient target, TsRequest request)
    {
        if (hook != null)
            hook.before(target, request);
    }
    
    public static void handleAfter(TsHook hook, CRpcClient target, TsRequest request, Object result)
    {
        if (hook != null)
            hook.after(target, request, result);
    }
    
    public static void handleError(TsHook hook, CRpcClient target, TsRequest request, Throwable error)
    {
        if (hook != null)
            hook.failed(target, request, error);
    }
}
