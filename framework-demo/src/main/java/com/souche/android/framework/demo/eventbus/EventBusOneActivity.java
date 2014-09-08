package com.souche.android.framework.demo.eventbus;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.souche.android.framework.activity.BaseActivity;
import com.souche.android.framework.demo.R;
import com.souche.android.framework.dialog.IDialog;
import com.souche.android.framework.eventbus.Event;
import com.souche.android.framework.eventbus.EventBus;
import com.souche.android.framework.eventbus.OnEventListener;
import com.souche.android.framework.ioc.annotation.Inject;
import com.souche.android.framework.ioc.annotation.InjectView;
import com.souche.android.framework.util.ViewUtil;

/**
 * Created by shenyubao on 14-5-9.
 */
public class EventBusOneActivity extends BaseActivity {

    public static final String log_tag = "EVENT_DEMO";

    @Inject
    EventBus bus;

    @InjectView(id = R.id.button1, click = "toFire")
    View toFire;

    @InjectView(id = R.id.button2, click = "toFire")
    View toFire2;

    @Inject
    IDialog dialoger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_bus_one);
        //手动,注册事件1我在onCreate中注册需要在finish取消注册
        bus.registerListener(Events.event_test1, EventBusOneActivity.class.getSimpleName(), new OnEventListener() {
            @Override
            public Boolean doInBg(Event event) {
                super.doInBg(event);
                Log.v(log_tag, "我是在后台线程处理的请勿操作UI,我接受到的参数是" + event.getParams()[0]);
                return false;
            }
        });
        ViewUtil.bindView(findViewById(R.id.tips), "这是通过编码方式的");
    }

    @Override
    protected void onResume() {
        super.onResume();
        //手动,注册事件2,我在onResume中注册需要在onStop取消注册
        bus.registerListener(Events.event_test2, EventBusOneActivity.class.getSimpleName(), new OnEventListener() {
            @Override
            public Boolean doInUI(Event event) {
                super.doInUI(event);
                dialoger.showToastShort(EventBusOneActivity.this, "我可以接受到先前的用户,我是在UI线程处理的,我接受到的参数1是" + event.getParams()[0] + "我接受到的参数2是" + event.getParams()[1]);

                //返回值表示是否继续迭代事件,如果为true,会继续处理前面没处理的事件,false不处理
//     			return true;
                return false;
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        //这里取消注册事件
        bus.unregisterListener(Events.event_test2, EventBusOneActivity.class.getSimpleName());
    }

    @Override
    public void finish() {
        super.finish();
        //这里取消注册事件
        bus.unregisterListener(Events.event_test1, EventBusOneActivity.class.getSimpleName());
    }

    public void toFire(View v) {
        switch (v.getId()) {
            case R.id.button1: {
                Intent it = new Intent(this, EventBusSecondActivity.class);
                startActivity(it);
                break;
            }
            case R.id.button2: {
                bus.fireEvent(Events.event_test1, "我是在本界面触发的");
                break;
            }
            default:
                break;
        }
    }
}
