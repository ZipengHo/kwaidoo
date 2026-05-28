package cell.basic.cmn.event;

import bap.cells.Cells;
import basic.cmn.dto.event.EventDefinitionDto;
import basic.cmn.dto.event.EventDto;
import cell.CellIntf;
import gpf.adur.data.ResultSet;

import org.nutz.dao.Cnd;

/**
 * 事件注册服务接口
 */
public interface IEventRegistry extends CellIntf{

    static IEventRegistry get(){
        return Cells.get(IEventRegistry.class);
    }
    /**
     * 注册事件契约
     * @param eventDefine 事件契约
     * @return 注册结果
     */
    EventDefinitionDto createEventDefinition(EventDefinitionDto eventDefine) throws Exception;

    /**
     * 更新事件契约（仅允许兼容修改）
     * @param eventDefine 事件契约
     * @return 更新结果
     */
    EventDefinitionDto updateEventDefinition(EventDefinitionDto eventDefine) throws Exception;

    /**
     * 查询事件契约详情
     * @param eventUuid 事件ID
     * @return 事件契约详情
     */
    EventDefinitionDto queryEventDefinition(String eventUuid) throws Exception;
    /**
     * 查询事件契约详情（通过事件代码）
     * @param eventCode 事件代码
     * @return 事件契约详情
     */
    EventDefinitionDto queryEventDefinitionByCode(String eventCode) throws Exception;
    /**
     * 删除事件契约
     * @param eventUuid 事件ID
     */
    void deleteEventDefinition(String eventUuid) throws Exception;

    /**
     * 验证事件是否符合事件契约
     *
     * @param event 事件
     */
    void validateEvent(EventDefinitionDto eventDefinition, EventDto event) throws Exception;
    /**
     * 查询符合条件的事件列表
     * @param cnd 查询条件
     * @param pageNo 页码
     * @param pageSize 每页数量
     * @return 事件列表
     */
    ResultSet<EventDefinitionDto> queryEventDefinitionPages(Cnd cnd, int pageNo, int pageSize) throws Exception;
}
