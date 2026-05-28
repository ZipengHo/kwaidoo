package basic.cmn.dto.event;

import java.io.Serializable;
import java.util.LinkedHashMap;
import java.util.Map;

import basic.cmn.event.TraceContext;

/**
 * 事件实例，描述事件 “实际是什么样”（运行时产生）。
 */
public class EventDto implements Serializable{
    /**
     * 事件编码
     */
    String eventCode;
    /**
     * 事件源
     */
    String eventSource;
    /**
     * 事件负载
     */
    Map<String,Object> payload;
    /**
     * 事件元数据
     */
    Map<String,Object> metadata;
    /**
     * 事件发布时间
     */
    Long publishTime = System.currentTimeMillis();

    public String getEventCode() {
        return eventCode;
    }

    public EventDto setEventCode(String eventCode) {
        this.eventCode = eventCode;
        return this;
    }

    public String getEventSource() {
        return eventSource;
    }

    public EventDto setEventSource(String eventSource) {
        this.eventSource = eventSource;
        return this;
    }

    public Map<String, Object> getPayload() {
        return payload;
    }

    public EventDto setPayload(Map<String, Object> payload) {
        this.payload = payload;
        return this;
    }

    public Map<String, Object> getMetadata() {
        return metadata;
    }

    public EventDto setMetadata(Map<String, Object> metadata) {
        this.metadata = metadata;
        return this;
    }

    public Long getPublishTime() {
        return publishTime;
    }

    public EventDto setPublishTime(Long publishTime) {
        this.publishTime = publishTime;
        return this;
    }

    public EventDto addPayload(String key, Object value) {
        if (payload == null) {
            payload = new LinkedHashMap<>();
        }
        payload.put(key, value);
        return this;
    }

    public Object getPayloadValue(String key) {
        if (payload == null) {
            return null;
        }
        return payload.get(key);
    }

    public EventDto setTraceParent(TraceContext traceContext) {
        if(metadata == null) {
            metadata = new LinkedHashMap<>();
        }
        metadata.put("traceparent", traceContext.toString());
        return this;
    }

    public EventDto setCurrentTraceParent(TraceContext traceContext) {
        if(metadata == null) {
            metadata = new LinkedHashMap<>();
        }
        metadata.put("traceparent", traceContext.getCurrentTraceParent());
        return this;
    }

    public TraceContext getTraceParent() {
        if(metadata == null) {
            return null;
        }
        String traceParent = (String)metadata.get("traceparent");
        TraceContext traceContext = new TraceContext(traceParent);
        return traceContext;
    }
}
