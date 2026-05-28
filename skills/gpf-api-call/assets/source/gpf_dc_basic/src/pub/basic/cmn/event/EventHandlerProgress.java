package basic.cmn.event;

import java.io.Serializable;

import basic.cmn.dto.event.EventOutBoxDto;

public class EventHandlerProgress implements Serializable{
    String runableKey;
    EventOutBoxDto eventOutBox;
    Long poolingTime;
    Long startTime;
    Long endTime;
    Long cost;
    String status;

    public String getRunableKey() {
        return runableKey;
    }

    public EventHandlerProgress setRunableKey(String runableKey) {
        this.runableKey = runableKey;
        return this;
    }

    public EventOutBoxDto getEventOutBox() {
        return eventOutBox;
    }

    public EventHandlerProgress setEventOutBox(EventOutBoxDto eventOutBox) {
        this.eventOutBox = eventOutBox;
        return this;
    }

    public Long getPoolingTime() {
        return poolingTime;
    }

    public EventHandlerProgress setPoolingTime(Long poolingTime) {
        this.poolingTime = poolingTime;
        return this;
    }

    public Long getStartTime() {
        return startTime;
    }

    public EventHandlerProgress setStartTime(Long startTime) {
        this.startTime = startTime;
        return this;
    }

    public Long getEndTime() {
        return endTime;
    }

    public EventHandlerProgress setEndTime(Long endTime) {
        this.endTime = endTime;
        return this;
    }

    public Long getCost() {
        return cost;
    }

    public EventHandlerProgress setCost(Long cost) {
        this.cost = cost;
        return this;
    }

    public String getStatus() {
        return status;
    }

    public EventHandlerProgress setStatus(String status) {
        this.status = status;
        return this;
    }
}
