package com.souche.android.framework.db.annotation;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

/**
 * Created by shenyubao on 14-5-9.
 */
@Target(ElementType.FIELD)
@Retention(RetentionPolicy.RUNTIME)
public @interface Column {
    /**
     * 对应列
     * @return
     */
    public String name() default "";
    /**
     * 是否是主键
     * @return
     */
    public boolean pk() default false;

    /**
     * 主键是否自增,只对long,int 等数字有效
     * @return
     */
    public boolean auto() default true;
}
