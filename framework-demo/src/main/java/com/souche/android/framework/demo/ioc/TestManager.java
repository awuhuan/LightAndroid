package com.souche.android.framework.demo.ioc;

import android.util.Log;

import com.souche.android.framework.ioc.InjectFields;
import com.souche.android.framework.ioc.annotation.Inject;

/**
 * Created by shenyubao on 14-5-8.
 */
public class TestManager implements InjectFields{
    String name;

    @Inject
    public	TestDateHelper helper;

    @Override
    public void injected() {
        if(	helper.manager!=null){
            Log.v("DH-INFO", "helper.manager!=null");
        }

    }
    public String getName() {
        return name;
    }
    public void setName(String name) {
        this.name = name;
    }
}
