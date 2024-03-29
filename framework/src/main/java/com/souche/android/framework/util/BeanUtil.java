package com.souche.android.framework.util;

import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Map;

/**
 * Created by shenyubao on 14-5-10.
 */
public class BeanUtil {

    /**
     * 从Map转Bean
     *
     * @param map
     * @param obj
     * @return
     */
    public static Object convertMap2Bean(Map<String, String> map, Object obj) {
        Class clazz = obj.getClass();

        Field[] fields = clazz.getDeclaredFields();
        for (Field field : fields) {
            String fieldName = field.getName();
            String propertyName = fieldName;
            String value = map.get(propertyName);
            try {
                field.setAccessible(true);
                field.set(obj,value);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            }

        }
        return obj;
    }


    /**
     * 对象copy只拷贝public的属性
     *
     * @param from
     * @param to
     */
    public static void copyBeanWithOutNull(Object from, Object to) {
        Class<?> beanClass = from.getClass();
        Method[] methodList = beanClass.getDeclaredMethods();
        Field[] fields = beanClass.getFields();
        for (int i = 0; i < fields.length; i++) {
            Field field = fields[i];
            field.setAccessible(true);
            try {
                Object value = field.get(from);
                if (value != null) {
                    field.set(to, value);
                }
            } catch (Exception e) {
            }
        }
//		for (int i = 0; i < methodList.length; i++) {
//			Method method=methodList[i];
//			if(method.toString().startsWith("public")){
//				if(method.getName().startsWith("get")||method.getName().startsWith("is")){
//					String name=method.getName().substring(3);
//					if(method.getName().startsWith("is")){
//						name=method.getName().substring(2);
//					}
//					try {
//						Object value=method.invoke(from);
//						if(value!=null){
//							String methodName="set"+name;
//							Method setMethod = beanClass.getDeclaredMethod(methodName,method.getReturnType());
//							setMethod.invoke(to, value);
//						}
//					} catch (Exception e) {
//						e.printStackTrace();
//					}
//				}
//			}
//		}


    }

    public static Field getDeclaredField(Class clazz, String name) {
        try {
            return clazz.getDeclaredField(name);
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        }
        return null;
    }


    /**
     * 获取属性
     *
     * @param o
     * @param field
     * @return
     */
    public static Object getProperty(Object o, String field) {
        try {
            Field f = o.getClass().getDeclaredField(field);
            return f.get(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 添加屬性
     *
     * @param o
     * @param field
     * @param value
     */
    public static void setProperty(Object o, String field, Object value) {
        try {
            Field f = o.getClass().getDeclaredField(field);
            f.setAccessible(true);
            f.set(o, value);
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

}
