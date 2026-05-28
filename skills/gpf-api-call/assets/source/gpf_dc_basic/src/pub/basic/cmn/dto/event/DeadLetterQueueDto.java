package basic.cmn.dto.event;

import gpf.adur.data.DataType;
import gpf.anotation.FieldMeta;

import java.io.Serializable;
/**
 * 死信队列
 *
 */
public class DeadLetterQueueDto extends EventServiceDto implements Serializable{
    public final static String FormModelId = "gpf.md.basic.DeadLetterQueue";
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
    public final static String FieldCode_FailReason = "shih1Bai4Yuan2Yin1";
    public final static String sFailReason = "失败原因";
    public final static String FieldCode_CreateTime = "chuang4Jian4Shih2Jian1";
    public final static String sCreateTime = "创建时间";
    @FieldMeta(code = FieldCode_EventCode,name = sEventCode, dataType = DataType.Text)
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
    @FieldMeta(code = FieldCode_FailReason,name = sFailReason, dataType = DataType.Text)
    String failReason;
    @FieldMeta(code = FieldCode_CreateTime,name = sCreateTime, dataType = DataType.Date)
    Long createTime;

    public String getEventCode() {
        return eventCode;
    }

    public DeadLetterQueueDto setEventCode(String eventCode) {
        this.eventCode = eventCode;
        return this;
    }

    public String getEventSource() {
        return eventSource;
    }

    public DeadLetterQueueDto setEventSource(String eventSource) {
        this.eventSource = eventSource;
        return this;
    }

    public String getPayload() {
        return payload;
    }

    public DeadLetterQueueDto setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    public String getMetadata() {
        return metadata;
    }

    public DeadLetterQueueDto setMetadata(String metadata) {
        this.metadata = metadata;
        return this;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public DeadLetterQueueDto setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
        return this;
    }

    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    public DeadLetterQueueDto setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
        return this;
    }

    public String getFailReason() {
        return failReason;
    }

    public DeadLetterQueueDto setFailReason(String failReason) {
        this.failReason = failReason;
        return this;
    }

    public Long getCreateTime() {
        return createTime;
    }

    public DeadLetterQueueDto setCreateTime(Long createTime) {
        this.createTime = createTime;
        return this;
    }
}
