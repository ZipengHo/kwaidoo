package basic.cmn.dto.event;

import gpf.adur.data.DataType;
import gpf.anotation.FieldMeta;

import java.io.Serializable;
/**
 * 事件处理记录
 *
 */
public class EventProcessingLogDto extends EventServiceDto implements Serializable{
    public final static String FormModelId = "gpf.md.basic.EventProcessingLog";
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
    public final static String FieldCode_InvokeMode = "diao4Yong4Fang1Shih4";
    public final static String sInvokeMode = "调用方式";
    public final static String FieldCode_SubscriptionCode = "ding4Yue4Bian1Ma3";
    public final static String sSubscriptionCode = "订阅编码";
    public final static String FieldCode_HandleStartTime = "chu3Li3Kai1Shih3Shih2Jian1";
    public final static String sHandleStartTime = "处理开始时间";
    public final static String FieldCode_HandleEndTime = "chu3Li3Jie2Shu4Shih2Jian1";
    public final static String sHandleEndTime = "处理结束时间";
    public final static String FieldCode_HandleLog = "jhih2Sing2Rih4Jhih4";
    public final static String sHandleLog = "执行日志";
    public final static String FieldCode_ErrorMsg = "cuo4Wu4Sin4Si1";
    public final static String sErrorMsg = "错误信息";
    public final static String FieldCode_HandleCost = "chu3Li3Hao4Shih2";
    public final static String sHandleCost = "处理耗时";
    public final static String FieldCode_RetryTimes = "jhong4Shih4Cih4Shu4";
    public final static String sRetryTimes = "重试次数";
    public final static String FieldCode_TraceId = "jhuei1Zong1ID";
    public final static String sTraceId = "追踪ID";
    public final static String FieldCode_ParentSpanID = "shang4You2Fu2Wu4SpanID";
    public final static String sParentSpanID = "上游服务SpanID";
    public final static String FieldCode_CurrentSpanID = "dang1Cian2Fu2Wu4SpanID";
    public final static String sCurrentSpanID = "当前服务SpanID";
    public final static String FieldCode_OutBoxCode = "fa1Jian4Bian1Ma3";
    public final static String sOutBoxCode = "发件编码";
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
    @FieldMeta(code = FieldCode_InvokeMode,name = sInvokeMode, dataType = DataType.Text)
    String invokeMode;
    @FieldMeta(code = FieldCode_SubscriptionCode,name = sSubscriptionCode, dataType = DataType.Text)
    String subscriptionCode;
    @FieldMeta(code = FieldCode_HandleStartTime,name = sHandleStartTime, dataType = DataType.Date)
    Long handleStartTime;
    @FieldMeta(code = FieldCode_HandleEndTime,name = sHandleEndTime, dataType = DataType.Date)
    Long handleEndTime;
    @FieldMeta(code = FieldCode_HandleLog,name = sHandleLog, dataType = DataType.Text)
    String handleLog;
    @FieldMeta(code = FieldCode_ErrorMsg,name = sErrorMsg, dataType = DataType.Text)
    String errorMsg;
    @FieldMeta(code = FieldCode_HandleCost,name = sHandleCost, dataType = DataType.Long)
    Long handleCost;
    @FieldMeta(code = FieldCode_RetryTimes,name = sRetryTimes, dataType = DataType.Long)
    Long retryTimes;
    @FieldMeta(code = FieldCode_TraceId,name = sTraceId, dataType = DataType.Text)
    String traceId;
    @FieldMeta(code = FieldCode_ParentSpanID,name = sParentSpanID, dataType = DataType.Text)
    String parentSpanID;
    @FieldMeta(code = FieldCode_CurrentSpanID,name = sCurrentSpanID, dataType = DataType.Text)
    String currentSpanID;
    @FieldMeta(code = FieldCode_OutBoxCode,name = sOutBoxCode, dataType = DataType.Text)
    String outBoxCode;

    public String getEventCode() {
        return eventCode;
    }

    public EventProcessingLogDto setEventCode(String eventCode) {
        this.eventCode = eventCode;
        return this;
    }

    public String getEventSource() {
        return eventSource;
    }

    public EventProcessingLogDto setEventSource(String eventSource) {
        this.eventSource = eventSource;
        return this;
    }

    public String getPayload() {
        return payload;
    }

    public EventProcessingLogDto setPayload(String payload) {
        this.payload = payload;
        return this;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public EventProcessingLogDto setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
        return this;
    }

    public String getInvokeMode() {
        return invokeMode;
    }

    public EventProcessingLogDto setInvokeMode(String invokeMode) {
        this.invokeMode = invokeMode;
        return this;
    }

    public String getSubscriptionCode() {
        return subscriptionCode;
    }

    public EventProcessingLogDto setSubscriptionCode(String subscriptionCode) {
        this.subscriptionCode = subscriptionCode;
        return this;
    }

    public Long getHandleStartTime() {
        return handleStartTime;
    }

    public EventProcessingLogDto setHandleStartTime(Long handleStartTime) {
        this.handleStartTime = handleStartTime;
        return this;
    }

    public Long getHandleEndTime() {
        return handleEndTime;
    }

    public EventProcessingLogDto setHandleEndTime(Long handleEndTime) {
        this.handleEndTime = handleEndTime;
        return this;
    }

    public String getHandleLog() {
        return handleLog;
    }

    public EventProcessingLogDto setHandleLog(String handleLog) {
        this.handleLog = handleLog;
        return this;
    }

    public String getErrorMsg() {
        return errorMsg;
    }

    public EventProcessingLogDto setErrorMsg(String errorMsg) {
        this.errorMsg = errorMsg;
        return this;
    }

    public Long getHandleCost() {
        return handleCost;
    }

    public EventProcessingLogDto setHandleCost(Long handleCost) {
        this.handleCost = handleCost;
        return this;
    }

    public Long getRetryTimes() {
        return retryTimes;
    }

    public EventProcessingLogDto setRetryTimes(Long retryTimes) {
        this.retryTimes = retryTimes;
        return this;
    }

    public String getTraceId() {
        return traceId;
    }

    public EventProcessingLogDto setTraceId(String traceId) {
        this.traceId = traceId;
        return this;
    }

    public String getParentSpanID() {
        return parentSpanID;
    }

    public EventProcessingLogDto setParentSpanID(String parentSpanID) {
        this.parentSpanID = parentSpanID;
        return this;
    }

    public String getCurrentSpanID() {
        return currentSpanID;
    }

    public EventProcessingLogDto setCurrentSpanID(String currentSpanID) {
        this.currentSpanID = currentSpanID;
        return this;
    }

    public String getMetadata() {
        return metadata;
    }

    public EventProcessingLogDto setMetadata(String metadata) {
        this.metadata = metadata;
        return this;
    }

    public String getOutBoxCode() {
        return outBoxCode;
    }

    public EventProcessingLogDto setOutBoxCode(String outBoxCode) {
        this.outBoxCode = outBoxCode;
        return this;
    }
}
