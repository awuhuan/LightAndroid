package com.souche.android.framework.eventbus;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import com.souche.android.framework.ioc.IocContainer;
import com.souche.android.framework.thread.Task;
import com.souche.android.framework.thread.ThreadWorker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

/**
 * Created by shenyubao on 14-5-9.
 */
public class EventBus {
    static SharedPreferences listenerFireTime = IocContainer.getShare().getApplicationContext().getSharedPreferences("EventBusTime", Context.MODE_WORLD_WRITEABLE);
    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == 0x10000 && msg.obj != null && msg.obj instanceof Map) {
                @SuppressWarnings("unchecked")
                Map<String, Object> map = (Map<String, Object>) msg.obj;
                OnEventListener listener = (OnEventListener) map.get("listener");
                Event event = (Event) map.get("event");
                listener.doInUI(event);
                listenerFireTime.edit().putLong(listener.getListenerName(), System.currentTimeMillis()).commit();
            }
        }
    };

    Map<String, EventQueue> eventQueues = new HashMap<String, EventQueue>();
    Map<String, List<OnEventListener>> eventListeners = new HashMap<String, List<OnEventListener>>();

    public void clearEvents(String name) {
        EventQueue queue = eventQueues.get(name);
        if (queue == null) {
            queue = new EventQueue();
            eventQueues.put(name, queue);
        }
        queue.clearEvents();
    }

    /**
     * 发布事件
     */
    public void fireEvent(String name, Object... params) {
        Event event = new Event();
        event.setName(name);
        event.setEventTime(System.currentTimeMillis());
        event.setParams(params);
        fireEvent(event);
    }

    /**
     * 发布事件
     */
    public void fireEvent(final Event event) {
        if (event != null) {
            EventQueue queue = eventQueues.get(event.getName());
            if (queue == null) {
                queue = new EventQueue();
                eventQueues.put(event.getName(), queue);
            }
            queue.addEvent(event);
        }

        final String eventname = event.getName();
        //在主线程里
        if (Looper.myLooper() == Looper.getMainLooper()) {
            ThreadWorker.execuse(false, new Task(IocContainer.getShare().getApplicationContext()) {
                @Override
                public void doInBackground() {
                    super.doInBackground();
                    List<OnEventListener> list = eventListeners.get(eventname);
                    for (int i = 0; list != null && i < list.size(); i++) {
                        OnEventListener listener = list.get(i);
                        listener.doInBg(event);
                    }
                }

                @Override
                public void doInUI(Object obj, Integer what) {
                    List<OnEventListener> list = eventListeners.get(eventname);
                    for (int i = 0; list != null && i < list.size(); i++) {
                        OnEventListener listener = list.get(i);
                        listener.doInUI(event);
                        listenerFireTime.edit().putLong(eventname + listener.getListenerName(), System.currentTimeMillis()).commit();
                    }
                }
            });
        } else {
            List<OnEventListener> list = eventListeners.get(eventname);
            for (int i = 0; list != null && i < list.size(); i++) {
                OnEventListener listener = list.get(i);
                listener.doInBg(event);
                Map<String, Object> map = new HashMap<String, Object>();
                map.put("listener", listener);
                map.put("event", event);
                handler.sendMessage(handler.obtainMessage(0x10000, map));
            }
        }
    }

    public void registerListener(final String name,final String listenerName,final OnEventListener listener){
        listener.setEventName(name);
        listener.setListenerName(listenerName);
        List<OnEventListener> listeners=eventListeners.get(name);
        if(listeners==null){
            listeners=new ArrayList<OnEventListener>();
            eventListeners.put(name, listeners);
        }
        listeners.add(listener);
        //触发已发生的时间
        ThreadWorker.execuse(false, new Task(IocContainer.getShare().getApplicationContext()) {
            @Override
            public void doInBackground() {
                super.doInBackground();
                EventQueue queue=eventQueues.get(name);
                if(queue==null) return;
                Long time=listenerFireTime.getLong(name+listenerName, 0);
                List<Event> events=queue.getEvents(time);
                if(events!=null){
                    for (int i = 0; i <events.size(); i++) {
                        Event event=events.get(i);
                        if(!listener.doInBg(event)){
                            break;
                        }
                    }
                }
            }
            @Override
            public void doInUI(Object obj, Integer what) {
                EventQueue queue=eventQueues.get(name);
                if(queue==null) return;
                Long time=listenerFireTime.getLong(name+listenerName, 0);
                List<Event> events=queue.getEvents(time);
                if(events!=null){
                    for (int i = 0; i <events.size(); i++) {
                        Event event=events.get(i);
                        if(!listener.doInUI(event)){
                            break;
                        }
                    }
                }
                listenerFireTime.edit().putLong(name+listenerName, System.currentTimeMillis()).commit();
            }
        });
    }

    /**
     * 移除监听
     * @param eventName
     * @param listenerName
     */
    public void unregisterListener(String eventName,String listenerName){
        List<OnEventListener> listerners=eventListeners.get(eventName);
        List<OnEventListener> listernerstoremove=new ArrayList<OnEventListener>();
        for (Iterator<OnEventListener> iterator = listerners.iterator(); iterator
                .hasNext();) {
            OnEventListener onEventListener = iterator.next();
            if(onEventListener.getListenerName().equals(listenerName)){
                listernerstoremove.add(onEventListener);
            }
        }
        for (Iterator<OnEventListener> iterator = listernerstoremove.iterator(); iterator
                .hasNext();) {
            OnEventListener onEventListener = iterator.next();
            listerners.remove(onEventListener);
        }
    }

    /**
     * 更新事件时间为最新时间
     * @param eventName
     * @param listenerName
     */
    public void clearEventTime(String eventName,String listenerName){
        listenerFireTime.edit().putLong(eventName+listenerName, System.currentTimeMillis()).commit();
    }

    /**
     * 移出监听
     * @param eventName
     * @param listener
     */
    public void unregisterListener(String eventName,OnEventListener listener){
        List<OnEventListener> listerners=eventListeners.get(eventName);
        listerners.remove(listener);
    }
}
