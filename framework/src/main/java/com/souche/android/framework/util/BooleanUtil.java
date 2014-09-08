package com.souche.android.framework.util;

/**
 * Created by shenyubao on 14-5-16.
 */
public class BooleanUtil {
    public static boolean isTrue(Object value){
        if (value != null && value.toString().toLowerCase().equals("true")){
            return true;
        }else {
            return false;
        }
    }

    public static boolean isFalse(Object value){
        if (value != null && value.toString().toLowerCase().equals("false")){
            return true;
        }else {
            return false;
        }
    }
}
