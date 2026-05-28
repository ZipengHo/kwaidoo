package basic.cmn.dto.event;

import gpf.adur.data.DataType;
import gpf.anotation.FieldMeta;

import java.io.Serializable;
/**
 * 事件发件箱
 *
 */
public class EventOutBoxDto extends EventServiceDto implements Serializable{
    public final static String FormModelId = "gpf.md.basic.EventOutBox";
    public final static String FieldCode_EventCode = "shih4Jian4Bian1Ma3";
    public final static String sEventCode = "事件编码";
    public final static String FieldCode_EventSource = "shih4Jian4Yuan2";
    public final static String sEventSource = "事件源";
    public final static String FieldCode_Payload = "shih4Jian4Fu4Zai3";
    public final static String sPayload = "事件负载";
    public final static String FieldCode_Metadata = "shih4Jian4Yuan2Shu4Jyu4";
    public final static String sMetadata = "事件元数据";
    public final static String FieldCode_PublishTime = "fa1Bu4Shih2Jian1";
    public final static String sPublishTime = "发布时间";
    public final static String FieldCode_SubscriptionCode = "ding4Yue4Bian1Ma3";
    public final static String sSubscriptionCode = "订阅编码";
    @FieldMeta(code = FieldCode_EventCode,name = sEventCode, dataType = DataType.Relate, assocModel = EventDefinitionDto.class)
    String eventCode;
    @FieldMeta(code = FieldCode_EventSource,name = sEventSource, dataType = DataType.Text)
    String eventSource;
    @FieldMeta(code = FieldCode_Payload,name = sPayload, dataType = DataType.Text)
    String payload;
    @FieldMeta(code = FieldCode_Metadata,name = sMetadata, dataType = DataType.Text)
    String metadata;
    @FieldMeta(code = FieldCode_PublishTime,name = sPublishTime, dataType = DataType.Date)
    Long publishTime;
    @FieldMeta(code = FieldCode_SubscriptionCode,name = sSubscriptionCode, dataType = DataType.Text)
    String subscriptionCode;

    public String getEventCode() {
        return eventCode;
    }

    public EventOutBoxDto setEventCode(String eventCode) {
        this.eventCode = eventCode;
        return this;
    }

    public String getEventSource() {
        return eventSource;
    }

    public EventOutBoxDto setEventSource(String eventSource) {
        this.eventSource = eventSource;
        return this;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public EventOutBoxDto setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
        return this;
    }

    public String getPayload() {
        return payload;
    }

    public EventOutBoxDto setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    public String getMetadata() {
        return metadata;
    }

    public EventOutBoxDto setMetadata(String metadata) {
        this.metadata = metadata;
        return this;
    }

    public String getSubscriptionCode() {
            return subscriptionCode;
    }

    public EventOutBoxDto setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
        return this;
    }
}
