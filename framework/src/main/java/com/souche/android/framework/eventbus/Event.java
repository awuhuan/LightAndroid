package com.souche.android.framework.eventbus;

/**
 * Created by shenyubao on 14-5-9.
 */
public class Event {
    /**
     * 事件名
     */
    public String name;

    /**
     * 参数
     */
    public Object[] params;

    /**
     * 事件发生时间
     */
    public long eventTime;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Object[] getParams() {
        return params;
    }

    public void setParams(Object[] params) {
        this.params = params;
    }

    public long getEventTime() {
        return eventTime;
    }

    public void setEventTime(long eventTime) {
        this.eventTime = eventTime;
    }
}
