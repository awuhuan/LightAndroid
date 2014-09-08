package com.souche.android.framework.demo.eventbus;

import android.os.Bundle;
import android.view.View;

import com.souche.android.framework.activity.BaseActivity;
import com.souche.android.framework.demo.R;
import com.souche.android.framework.dialog.IDialog;
import com.souche.android.framework.eventbus.EventBus;
import com.souche.android.framework.ioc.annotation.Inject;
import com.souche.android.framework.ioc.annotation.InjectView;

/**
 * Created by shenyubao on 14-5-9.
 */
public class EventBusSecondActivity extends BaseActivity {

    @InjectView(id = R.id.button1, click = "fireEvent")
    View eventFire1;
    @InjectView(id = R.id.button2, click = "fireEvent")
    View eventFire2;
    @InjectView(id = R.id.button3, click = "fireEvent")
    View eventFire3;
    @InjectView(id = R.id.button4, click = "fireEvent")
    View eventFire4;

    @Inject
    EventBus bus;

    @Inject
    IDialog dialoger;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.event_bus_second);
    }

    public void fireEvent(View v) {
        switch (v.getId()) {
            case R.id.button1:
                bus.fireEvent(Events.event_test1, "事件1 这个是参数");
                dialoger.showToastShort(this, "");
                break;
            case R.id.button2:
                bus.fireEvent(Events.event_test2, "事件2 这个是参数", "可以传多个参数");
                break;
            case R.id.button3:
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        bus.fireEvent(Events.event_test1, "事件1我不是在主线程触发的");
                    }
                }).start();
                break;
            case R.id.button4:
                new Thread(new Runnable() {

                    @Override
                    public void run() {
                        bus.fireEvent(Events.event_test2, "事件2我不是在主线程触发的", "可以传多个参数");
                    }
                }).start();

                break;
            default:
                break;
        }

    }
}
