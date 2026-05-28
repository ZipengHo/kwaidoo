package cell.basic.cmn.event;

import java.util.List;
import java.util.Map;

import org.nutz.dao.Cnd;

import com.kwaidoo.ms.tool.CmnUtil;

import bap.cells.BasicCell;
import basic.cmn.dto.event.EventDefinitionDto;
import basic.cmn.dto.event.EventDto;
import basic.cmn.event.JsonSchemaDto;
import basic.cmn.event.SchemaType;
import cell.cdao.IDao;
import cell.cdao.IDaoService;
import cell.gpf.adur.data.IFormMgr;
import cmn.util.JsonUtil;
import gpf.adur.data.Form;
import gpf.adur.data.ResultSet;
import gpf.dc.basic.i18n.GpfDCBasicI18n;
import gpf.dc.util.DtoConvertUtil;

public class CEventRegistry extends BasicCell implements IEventRegistry {
    @Override
    public EventDefinitionDto createEventDefinition(EventDefinitionDto eventMeta) throws Exception {
        IFormMgr formMgr = IFormMgr.get();
        Form form = DtoConvertUtil.convertToForm(eventMeta);
        try(IDao dao = IDaoService.newIDao()){
            form = formMgr.createForm(dao,form);
            dao.commit();
            return DtoConvertUtil.convertToDto(form, EventDefinitionDto.class,true);
        }
    }

    @Override
    public EventDefinitionDto updateEventDefinition(EventDefinitionDto eventMeta) throws Exception {
        IFormMgr formMgr = IFormMgr.get();
        Form form = DtoConvertUtil.convertToForm(eventMeta);
        try(IDao dao = IDaoService.newIDao()){
            form = formMgr.updateForm(dao,form);
            dao.commit();
            return DtoConvertUtil.convertToDto(form, EventDefinitionDto.class,true);
        }
    }

    @Override
    public EventDefinitionDto queryEventDefinition(String eventUuid) throws Exception {
        try(IDao dao = IDaoService.newIDao()){
            Form form = IFormMgr.get().queryForm(dao, EventDefinitionDto.FormModelId,eventUuid);
            if(form == null){
                return null;
            }
            return DtoConvertUtil.convertToDto(form, EventDefinitionDto.class,true);
        }
    }

    @Override
    public EventDefinitionDto queryEventDefinitionByCode(String eventCode) throws Exception {
        try(IDao dao = IDaoService.newIDao()){
            Form form = IFormMgr.get().queryFormByCode(dao, EventDefinitionDto.FormModelId,eventCode);
            if(form == null){
                return null;
            }
            return DtoConvertUtil.convertToDto(form, EventDefinitionDto.class,true);
        }
    }

    @Override
    public void deleteEventDefinition(String eventUuid) throws Exception {
        try(IDao dao = IDaoService.newIDao()){
            IFormMgr.get().deleteForm(dao, EventDefinitionDto.FormModelId,eventUuid);
            dao.commit();
        }
    }

    @Override
    public void validateEvent(EventDefinitionDto eventDefinition, EventDto event) throws Exception {
        JsonSchemaDto payloadSchema = eventDefinition.getEventSchemaDto();
        if(payloadSchema == null){
            return;
        }
        //校验事件负载是否符合事件定义的JSON Schema
        Map<String,Object> payload = event.getPayload();
        //根据payloadSchema校验payload是否符合JSON Schema
        List<String> required = payloadSchema.getRequired();
        for(String key : payloadSchema.getProperties().keySet()){
            Object value = payload.get(key);
            JsonSchemaDto valueSchema = payloadSchema.getProperties().get(key);
            validateValueSchema(payload,key,value,required,valueSchema);
        }
    }
    /**
     * 校验事件负载是否符合事件定义的JSON Schema
     * @param key 事件负载字段名
     * @param value 事件负载字段值
     * @param required 事件定义的必填字段列表
     * @param valueSchema 事件定义的JSON Schema
     * @throws Exception 校验失败时抛出异常
     */
    protected void validateValueSchema(Object payload,String key, Object value, List<String> required, JsonSchemaDto valueSchema) throws Exception {
        //校验必填字段是否存在
        if(required != null && required.contains(key) && value == null){
            throw new IllegalArgumentException(GpfDCBasicI18n.getString("事件负载{1}缺少必填字段{2}", JsonUtil.toJson(payload),key));
        }
        SchemaType type = valueSchema.getTypeEnum();
        if(type == null){
            throw new Exception(GpfDCBasicI18n.getString("事件负载{1}字段{2}类型{3}不支持校验", JsonUtil.toJson(payload),key,valueSchema.getType()));
        }
        switch (type){
            case Object:
                if(!(value instanceof Map)){
                    throw new IllegalArgumentException(GpfDCBasicI18n.getString("事件负载{1}字段{2}类型{3}不支持校验", JsonUtil.toJson(payload),key,type.getValue()));
                }
                //校验对象属性是否符合JSON Schema
                Map<String,Object> valueMap = (Map<String,Object>)value;
                for(String subKey : valueSchema.getProperties().keySet()){
                    JsonSchemaDto subValueSchema = valueSchema.getProperties().get(subKey);
                    validateValueSchema(valueMap,subKey,valueMap.get(subKey),subValueSchema.getRequired(),subValueSchema);
                }
                break;
            case Array:
                if(!(value instanceof List)){
                    throw new IllegalArgumentException(GpfDCBasicI18n.getString("事件负载{1}字段{2}类型{3}不支持校验", JsonUtil.toJson(payload),key,type.getValue()));
                }
                //校验数组元素是否符合JSON Schema
                if(!CmnUtil.isCollectionEmpty(valueSchema.getItems())){
                    JsonSchemaDto subValueSchema = valueSchema.getItems().get(0);
                    for(Object item : (List<?>)value){
                        validateValueSchema(value,key,item,subValueSchema.getRequired(),subValueSchema);
                    }
                }
                break;
            case String:
                if(!(value instanceof String)){
                    throw new IllegalArgumentException(GpfDCBasicI18n.getString("事件负载{1}字段{2}类型{3}不支持校验", JsonUtil.toJson(payload),key,type.getValue()));
                }
                break;
            case Number:
                if(!(value instanceof Number)){
                    throw new IllegalArgumentException(GpfDCBasicI18n.getString("事件负载{1}字段{2}类型{3}不支持校验", JsonUtil.toJson(payload),key,type.getValue()));
                }
                break;
            case Integer:
                if(!(value instanceof Integer)){
                    throw new IllegalArgumentException(GpfDCBasicI18n.getString("事件负载{1}字段{2}类型{3}不支持校验", JsonUtil.toJson(payload),key,type.getValue()));
                }
                break;
            case Boolean:
                if(!(value instanceof Boolean)){
                    throw new IllegalArgumentException(GpfDCBasicI18n.getString("事件负载{1}字段{2}类型{3}不支持校验", JsonUtil.toJson(payload),key,type.getValue()));
                }
                break;
            case Null:
                break;
            default:
                throw new IllegalArgumentException(GpfDCBasicI18n.getString("事件负载{1}字段{2}类型{3}不支持校验", JsonUtil.toJson(payload),key,type.getValue()));
        }
    }

    @Override
    public ResultSet<EventDefinitionDto> queryEventDefinitionPages(Cnd cnd, int pageNo, int pageSize) throws Exception {
        try(IDao dao = IDaoService.newIDao()){
            ResultSet<Form> rs = IFormMgr.get().queryFormPage(dao, EventDefinitionDto.FormModelId,cnd,pageNo,pageSize,true,true);
            List<EventDefinitionDto> list = DtoConvertUtil.convertToDtos(rs.getDataList(), EventDefinitionDto.class);
            ResultSet<EventDefinitionDto> rs2 = new ResultSet<>();
            rs2.setTotalCount(rs.getTotalCount());
            rs2.setDataList(list);
            return rs2;
        }
    }
}
