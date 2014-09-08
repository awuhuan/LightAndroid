package com.souche.android.framework.demo.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.souche.android.framework.activity.BaseActivity;
import com.souche.android.framework.demo.R;
import com.souche.android.framework.ioc.annotation.InjectView;

/**
 * Created by shenyubao on 14-5-9.
 */
public class EventBusActivity extends BaseActivity {
    @InjectView(id = R.id.button1, click = "toTest")
    View toTest;
    @InjectView(id = R.id.button2, click = "toTest")
    View toTest2;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eventbus_test_main);
    }

    public void toTest(View v) {
        Intent it = new Intent();
        switch (v.getId()) {
            case R.id.button1:
                it.setClass(this, EventBusOneActivity.class);
                break;
            case R.id.button2:
                it.setClass(this, EventBusAnnActivity.class);
                break;
            default:
                break;
        }
        startActivity(it);

    }
}
