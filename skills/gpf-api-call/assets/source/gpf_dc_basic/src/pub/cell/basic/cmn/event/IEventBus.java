package cell.basic.cmn.event;

import bap.cells.Cells;
import basic.cmn.dto.event.DeadLetterQueueDto;
import basic.cmn.dto.event.EventDto;
import basic.cmn.dto.event.EventProcessingLogDto;
import basic.cmn.dto.event.EventSubscriptionDto;
import basic.cmn.event.EventHandlerProgress;
import cell.ServiceCellIntf;
import com.leavay.nio.crpc.RpcMap;
import gpf.adur.data.ResultSet;
import org.nutz.dao.Cnd;

import java.util.List;

/**
 * 事件总线服务接口
 */
public interface IEventBus extends ServiceCellIntf {

    static IEventBus get(){
        return Cells.get(IEventBus.class);
    }
    /**
     * 发布事件，仅在同步事件中使用
     * @param runtimeContext 上下文
     * @param event 事件
     * @return 发布结果
     */
    public void publish(RpcMap<Object> runtimeContext, EventDto event) throws Exception;

    /**
     * 发布事件
     * @param event 事件
     * @throws Exception
     */
    public void publish(EventDto event) throws Exception;

    /**
     * 订阅事件
     * @param subscriptionDto 事件订阅配置
     * @return 运行时订阅ID
     */
    public EventSubscriptionDto subscribe(EventSubscriptionDto subscriptionDto);
    /**
     * 取消订阅事件
     * @param subscriptionDto 事件订阅配置
     */
    public void unsubscribe(EventSubscriptionDto subscriptionDto);

    /**
     * 查询运行时订阅分页列表
     * @param keyword 搜索关键词
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 运行时订阅分页结果集
     * @throws Exception 查询过程中可能抛出的异常
     */
    public ResultSet<EventSubscriptionDto> queryRuntimeSubscriptionPage(String keyword, int pageNo, int pageSize)throws Exception;

    /**
     * 获取运行时订阅列表
     * @param eventCode 事件编码
     * @return 运行时订阅列表
     * @throws Exception 查询过程中可能抛出的异常
     */
    public List<EventSubscriptionDto> getRuntimeSubscriptions(String eventCode)throws Exception;
    /**
     * 运行时订阅特定数据实例的事件
     * @param subscription 事件订阅配置
     * @return 运行时订阅ID
     */
    public EventSubscriptionDto createEventSubscription(EventSubscriptionDto subscription) throws Exception;

    /**
     * 查询运行时订阅配置
     * @param subscriptionUuid 订阅编码
     * @return 事件订阅配置
     */
    public EventSubscriptionDto queryEventSubscription(String subscriptionUuid) throws Exception;

    /**
     * 查询运行时订阅配置
     * @param subscriptionCode 订阅编码
     * @return 事件订阅配置
     */
    public EventSubscriptionDto queryEventSubscriptionByCode(String subscriptionCode) throws Exception;
    /**
     * 更新运行时订阅配置
     *
     * @param subscription 事件订阅配置
     * @return
     */
    public EventSubscriptionDto updateEventSubscription(EventSubscriptionDto subscription) throws Exception;

    /**
     * 取消订阅特定运行时订阅ID
     * @param subscriptionUuid 订阅编码
     */
    public void deleteEventSubscription(String subscriptionUuid) throws Exception;

    /**
     * 查询事件订阅分页列表
     * @param cnd 查询条件
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 事件订阅分页结果集
     * @throws Exception 查询过程中可能抛出的异常
     */
    ResultSet<EventSubscriptionDto> queryEventSubscriptionPage(Cnd cnd, int pageNo, int pageSize) throws Exception;

    /**
     * 查询死信队列分页列表
     * @param cnd 查询条件
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 死信队列分页结果集
     * @throws Exception 查询过程中可能抛出的异常
     */
    public ResultSet<DeadLetterQueueDto> queryDeadLetterQueuePage(Cnd cnd, int pageNo, int pageSize) throws Exception;

    public void deleteDeadLetterQueue(String deadLetterQueueUuid) throws Exception;

    /**
     * 重试死信队列中的事件
     * @param deadLetterQueueDto 死信队列DTO
     */
    public void retryDeadLetterQueue(DeadLetterQueueDto deadLetterQueueDto) throws Exception;

    /**
     * 查询事件处理日志分页列表
     * @param cnd 查询条件
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 事件处理日志分页结果集
     * @throws Exception 查询过程中可能抛出的异常
     */
    public ResultSet<EventProcessingLogDto> queryEventProcessingLogPage(Cnd cnd, int pageNo, int pageSize) throws Exception;
    /**
     * 查询事件处理日志详情
     * @param eventOutBoxCode 事件出队框编码
     * @return 事件处理日志详情
     * @throws Exception 查询过程中可能抛出的异常
     */
    public EventProcessingLogDto queryEventProcessingLogByEventOutBoxCode(String eventOutBoxCode) throws Exception;


    /**
     * 查询事件处理进度分页列表
     * @param keyword 搜索关键词
     * @param statusList 状态列表
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 事件处理进度分页结果集
     * @throws Exception 查询过程中可能抛出的异常
     */
    public ResultSet<EventHandlerProgress> queryEventHandlerProgressPage(String keyword, List<String> statusList, int pageNo, int pageSize) throws Exception;
    /**
     * 终止事件处理进度
     * @param runnableKey 运行时键值
     * @throws Exception 终止过程中可能抛出的异常
     */
    public void killEventHandlerProgress(String runnableKey) throws Exception;

}
