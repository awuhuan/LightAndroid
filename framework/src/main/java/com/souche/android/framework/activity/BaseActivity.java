package com.souche.android.framework.activity;

import android.app.Activity;
import android.os.Bundle;

import com.souche.android.framework.Const;
import com.souche.android.framework.eventbus.EventBus;
import com.souche.android.framework.eventbus.EventInjectUtil;
import com.souche.android.framework.ioc.InjectUtil;
import com.souche.android.framework.ioc.IocContainer;

/**
 * Created by shenyubao on 14-5-8.
 */
public class BaseActivity extends Activity {
    private ActivityTack tack = ActivityTack.getInstanse();
    private EventBus eventBus;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        tack.addActivity(this);
        eventBus = IocContainer.getShare().get(EventBus.class);
        EventInjectUtil.inject(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        EventInjectUtil.inject(this); //TODO:测试是否存在多次绑定
    }

    @Override
    protected void onStop() {
        super.onStop();
        EventInjectUtil.unInject(this);
    }

    @Override
    public void finish() {
        super.finish();
        tack.removeActivity(this);
    }

    @Override
    public void setContentView(int layoutResID) {
        super.setContentView(layoutResID);
        if (Const.auto_inject) {
            InjectUtil.inject(this);
        }
    }
}
