package cell.basic.cmn.event;

import org.springframework.core.ResolvableType;

import com.leavay.nio.crpc.RpcMap;

import basic.cmn.dto.event.EventDto;
import basic.cmn.event.EventHandlerInitParameter;

/**
 * 事件订阅者
 */
public interface EventHandler<T extends EventHandlerInitParameter> {
    /**
     * 获取事件处理静态参数类型
     * @return 事件处理静态参数类型
     */
    default Class<T> getInitParameterType() {
        return (Class<T>) ResolvableType.forClass(getClass())
                .as(EventHandler.class)
                .getGeneric(0)
                .resolve();
    }
    /**
     * 处理事件
     *
     * @param initParameter 事件处理静态参数
     * @param context      上下文
     * @param event        事件数据传输对象（DTO）
     * @return 处理结果
     * @throws Exception 处理事件时发生的异常
     */
    public Object onEvent(T initParameter, RpcMap<Object> context, EventDto event) throws Exception;
}
