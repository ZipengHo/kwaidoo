package tiny.service.cmn;

import com.leavay.nio.crpc.TsRequest;

/**
 * 设定在服务注册表中，在调用的执行端（即服务提供者一侧）执行
 * 
 * 通常可用于限流、降级等
 */
public interface TsProviderHook
{
    public void before(TsRequest request);
    public void after(TsRequest request, Object result);
    public void failed(TsRequest request, Throwable error);
}
