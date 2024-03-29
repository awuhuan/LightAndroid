package com.souche.android.framework.ioc.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by shenyubao on 14-5-8.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface InjectResource {
    public int drawable() default 0;
    public int string() default 0;
    public int color() default 0;
    public int dimen() default 0;
}
