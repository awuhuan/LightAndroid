package com.souche.android.framework.eventbus;

import com.souche.android.framework.eventbus.annotation.OnEvent;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by shenyubao on 14-5-9.
 */
public class InjectOnEventListener extends OnEventListener {
    public Object obj;
    public Method method;
    public OnEvent onEvent;

    public InjectOnEventListener(Object obj, Method method, OnEvent onEvent) {
        super();
        this.obj = obj;
        this.method = method;
        this.onEvent = onEvent;
    }

    @Override
    public Boolean doInBg(Event event) {
        if (!onEvent.ui()) {
            try {
                Object result = method.invoke(obj, event.getParams());
                if (result != null && result instanceof Boolean) {
                    return (Boolean) result;
                }

            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return super.doInBg(event);
    }

    @Override
    public Boolean doInUI(Event event) {
        if (onEvent.ui()) {
            try {
                Object result = method.invoke(obj, event.getParams());
                if (result != null && result instanceof Boolean) {
                    return (Boolean) result;
                }
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            }
        }
        return super.doInBg(event);
    }
}
