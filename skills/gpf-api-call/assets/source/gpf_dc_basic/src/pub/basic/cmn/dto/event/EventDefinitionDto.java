package basic.cmn.dto.event;

import cmn.util.JsonUtil;
import com.kwaidoo.ms.tool.CmnUtil;

import basic.cmn.event.JsonSchemaDto;
import gpf.adur.data.DataType;
import gpf.anotation.FieldMeta;

import java.io.Serializable;

/**
 * 静态契约，描述事件 “应该是什么样”（设计时定义）。
 */
public class EventDefinitionDto extends EventServiceDto implements Serializable {
    public final static String FormModelId = "gpf.md.basic.EventDefinition";
    public final static String FieldCode_EventName = "shih4Jian4Ming2Cheng1";
    public final static String sEventName = "事件名称";
    public final static String FieldCode_EventDesc = "shih4Jian4Shuo1Ming2";
    public final static String sEventDesc = "事件说明";
    public final static String FieldCode_EventSource = "shih4Jian4Yuan2";
    public final static String sEventSource = "事件源";
    public final static String FieldCode_EventType = "shih4Jian4Lei4Sing2";
    public final static String sEventType = "事件类型";
    public final static String FieldCode_EventSchema = "shih4Jian4Schema";
    public final static String sEventSchema = "事件Schema";
//    public final static String FieldCode_Status = "jhuang4Tai4";
//    public final static String sStatus = "状态";
    public final static String FieldCode_EffectTime = "sheng1Siao4Shih2Jian1";
    public final static String sEffectTime = "生效时间";
    @FieldMeta(code = FieldCode_EventName,name = sEventName, dataType = DataType.Text)
    String eventName;
    @FieldMeta(code = FieldCode_EventDesc,name = sEventDesc, dataType = DataType.Text)
    String eventDesc;
    @FieldMeta(code = FieldCode_EventSource,name = sEventSource, dataType = DataType.Text)
    String eventSource;
    @FieldMeta(code = FieldCode_EventType,name = sEventType, dataType = DataType.Text)
    String eventType;
    @FieldMeta(code = FieldCode_EventSchema,name = sEventSchema, dataType = DataType.Text)
    String eventSchema;
//    @FieldMeta(code = FieldCode_Status,name = sStatus, dataType = DataType.Text)
//    String status;
    @FieldMeta(code = FieldCode_EffectTime,name = sEffectTime, dataType = DataType.Date)
    Long effectTime;

    public String getEventSource() {
        return eventSource;
    }

    public EventDefinitionDto setEventSource(String eventSource) {
        this.eventSource = eventSource;
        return this;
    }

    public String getEventType() {
        return eventType;
    }

    public EventDefinitionDto setEventType(String eventType) {
        this.eventType = eventType;
        return this;
    }

    public String getEventSchema() {
        return eventSchema;
    }

    public EventDefinitionDto setEventSchema(String eventSchema) {
        this.eventSchema = eventSchema;
        return this;
    }

//    public String getStatus() {
//        return status;
//    }
//
//    public EventDefinitionDto setStatus(String status) {
//        this.status = status;
//        return this;
//    }

    public Long getEffectTime() {
        return effectTime;
    }

    public EventDefinitionDto setEffectTime(Long effectTime) {
        this.effectTime = effectTime;
        return this;
    }

    public String getEventName() {
        return eventName;
    }

    public EventDefinitionDto setEventName(String eventName) {
        this.eventName = eventName;
        return this;
    }

    public String getEventDesc() {
        return eventDesc;
    }

    public EventDefinitionDto setEventDesc(String eventDesc) {
        this.eventDesc = eventDesc;
        return this;
    }

    public EventDefinitionDto setEventSchema(JsonSchemaDto eventSchema) {
        if(eventSchema == null) {
            this.eventSchema = null;
        }else{
            this.eventSchema = JsonUtil.toPrettyJson(eventSchema);
        }
        return this;
    }

    public JsonSchemaDto getEventSchemaDto() {
        if(CmnUtil.isStringEmpty(eventSchema)){
            return null;
        }
        return JsonUtil.fromJson(eventSchema,JsonSchemaDto.class);
    }
}
