package basic.cmn.dto.event;

import gpf.adur.data.DataType;
import gpf.anotation.FieldMeta;
import gpf.dc.basic.fe.enums.EnumUtil;

import java.io.Serializable;

import basic.cmn.event.EventInvokeMode;

/**
 * 静态订阅列表
 */
public class EventSubscriptionDto extends EventServiceDto implements Serializable {
    public final static String FormModelId = "gpf.md.basic.EventSubscription";
    public final static String FieldCode_EventCode = "shih4Jian4Bian1Ma3";
    public final static String sEventCode = "事件编码";
    public final static String FieldCode_EventSourceRegex = "shih4Jian4Yuan2Guo4Lyu4Jheng4Ze2";
    public final static String sEventSourceRegex = "事件源过滤正则";
    public final static String FieldCode_Subscriber = "ding4Yue4Jhe3";
    public final static String sSubscriber = "订阅者";
    public final static String FieldCode_EventHandler = "shih4Jian4Chu3Li3Ci4";
    public final static String sEventHandler = "事件处理器";
    public final static String FieldCode_EventHandlerParam = "shih4Jian4Chu3Li3Ci4Can1Shu4";
    public final static String sEventHandlerParam = "事件处理器参数";
    public final static String FieldCode_HandlerDesc = "chu3Li3Shuo1Ming2";
    public final static String sHandlerDesc = "处理说明";
    public final static String FieldCode_PayloadMappingRule = "payloadYing4She4Guei1Ze2";
    public final static String sPayloadMappingRule = "Payload映射规则";
    public final static String FieldCode_InvokeMode = "diao4Yong4Fang1Shih4";
    public final static String sInvokeMode = "调用方式";
    public final static String FieldCode_RetryTimes = "jhong4Shih4Cih4Shu4";
    public final static String sRetryTimes = "重试次数";
    @FieldMeta(code = FieldCode_EventCode,name = sEventCode, dataType = DataType.Relate, assocModel = EventDefinitionDto.class)
    String eventCode;
    @FieldMeta(code = FieldCode_EventSourceRegex,name = sEventSourceRegex, dataType = DataType.Text)
    String eventSourceRegex;
    @FieldMeta(code = FieldCode_Subscriber,name = sSubscriber, dataType = DataType.Text)
    String subscriber;
    @FieldMeta(code = FieldCode_EventHandler,name = sEventHandler, dataType = DataType.Text)
    String eventHandler;
    @FieldMeta(code = FieldCode_EventHandlerParam,name = sEventHandlerParam, dataType = DataType.Text)
    String eventHandlerParam;
    @FieldMeta(code = FieldCode_HandlerDesc,name = sHandlerDesc, dataType = DataType.Text)
    String handlerDesc;
    @FieldMeta(code = FieldCode_PayloadMappingRule,name = sPayloadMappingRule, dataType = DataType.Text)
    String payloadMappingRule;
    @FieldMeta(code = FieldCode_InvokeMode,name = sInvokeMode, dataType = DataType.Text)
    String invokeMode;
    @FieldMeta(code = FieldCode_RetryTimes,name = sRetryTimes, dataType = DataType.Long)
    Long retryTimes;
    /**
     * 是否临时订阅
     */
    boolean isTemporary = false;

    public String getEventCode() {
        return eventCode;
    }

    public EventSubscriptionDto setEventCode(String eventCode) {
        this.eventCode = eventCode;
        return this;
    }

    public String getSubscriber() {
        return subscriber;
    }

    public EventSubscriptionDto setSubscriber(String subscriber) {
        this.subscriber = subscriber;
        return this;
    }

    public String getEventHandler() {
        return eventHandler;
    }

    public EventSubscriptionDto setEventHandler(String eventHandler) {
        this.eventHandler = eventHandler;
        return this;
    }

    public String getEventHandlerParam() {
        return eventHandlerParam;
    }

    public EventSubscriptionDto setEventHandlerParam(String eventHandlerParam) {
        this.eventHandlerParam = eventHandlerParam;
        return this;
    }

    public String getPayloadMappingRule() {
        return payloadMappingRule;
    }

    public EventSubscriptionDto setPayloadMappingRule(String payloadMappingRule) {
        this.payloadMappingRule = payloadMappingRule;
        return this;
    }

    public String getInvokeMode() {
        return invokeMode;
    }

    public EventSubscriptionDto setInvokeMode(String invokeMode) {
        this.invokeMode = invokeMode;
        return this;
    }

    public Long getRetryTimes() {
        return retryTimes;
    }

    public EventSubscriptionDto setRetryTimes(Long retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public String getHandlerDesc() {
        return handlerDesc;
    }

    public EventSubscriptionDto setHandlerDesc(String handlerDesc) {
        this.handlerDesc = handlerDesc;
        return this;
    }

    public String getEventSourceRegex() {
        return eventSourceRegex;
    }

    public EventSubscriptionDto setEventSourceRegex(String eventSourceRegex) {
        this.eventSourceRegex = eventSourceRegex;
        return this;
    }

    public EventSubscriptionDto setInvokeMode(EventInvokeMode invokeMode) {
        this.invokeMode = invokeMode.name();
        return this;
    }
    public EventInvokeMode getInvokeModeEnum() {
        return EnumUtil.getEnumByName(EventInvokeMode.class,invokeMode);
    }

    public boolean isTemporary() {
        return isTemporary;
    }

    public EventSubscriptionDto setTemporary(boolean temporary) {
        isTemporary = temporary;
        return this;
    }
}
