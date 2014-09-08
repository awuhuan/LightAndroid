package com.souche.android.framework.eventbus;

import com.souche.android.framework.eventbus.annotation.OnEvent;
import com.souche.android.framework.ioc.IocContainer;

import java.lang.reflect.Method;

/**
 * Created by shenyubao on 14-5-9.
 */
public class EventInjectUtil {
    /**
     * 注入时间监听
     *
     * @param obj
     */
    public static void inject(Object obj) {
        EventBus eventBus = IocContainer.getShare().get(EventBus.class);
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            OnEvent onEvent = method.getAnnotation(OnEvent.class);
            if (onEvent == null) continue;
            OnEventListener listener = new InjectOnEventListener(obj, method, onEvent);
            if (!onEvent.onBefore()) {
                eventBus.clearEventTime(onEvent.name(), obj.getClass().getSimpleName() + "." + method.getName());
            }
            eventBus.registerListener(onEvent.name(), obj.getClass().getSimpleName() + "." + method.getName(), listener);
        }
    }

    /**
     * 取消注册监听
     *
     * @param obj
     */
    public static void unInject(Object obj) {
        EventBus eventBus = IocContainer.getShare().get(EventBus.class);
        Method[] methods = obj.getClass().getDeclaredMethods();
        for (int i = 0; i < methods.length; i++) {
            Method method = methods[i];
            OnEvent onEvent = method.getAnnotation(OnEvent.class);
            if (onEvent == null) continue;
            if (onEvent.autoUnRegist()) {
                eventBus.unregisterListener(onEvent.name(), obj.getClass().getSimpleName() + "." + method.getName());
            }
        }
    }
}
