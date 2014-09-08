package com.souche.android.framework.ioc;

/**
 * Created by shenyubao on 14-5-8.
 */
public class IocException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public IocException() {
    }

    public IocException(String msg) {
        super(msg);
    }
}
