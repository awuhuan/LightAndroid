package com.souche.android.framework.demo;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

import com.souche.android.framework.activity.BaseActivity;
import com.souche.android.framework.demo.db.DbStudentListActivity;
import com.souche.android.framework.demo.eventbus.EventBusActivity;
import com.souche.android.framework.demo.ioc.IocTestActivity;
import com.souche.android.framework.demo.ioc.TestManager;
import com.souche.android.framework.demo.net.NetTestActivity;
import com.souche.android.framework.ioc.annotation.Inject;
import com.souche.android.framework.ioc.annotation.InjectView;
import com.souche.android.framework.util.JSONUtil;

import org.json.JSONObject;

/**
 * Created by shenyubao on 14-5-10.
 */
public class MainActivity extends BaseActivity {
    @InjectView(id=R.id.ioctest,click="toTest")
    View toIocTest;
    @InjectView(id=R.id.dbtest,click="toTest")
    View toDbTest;
    @InjectView(id=R.id.nettest,click="toTest")
    View toAdapterTest;
    @InjectView(id=R.id.eventtest,click="toTest")
    View toeventTest;


    @Inject(tag="manager2")//这里获取到的对象是TestManagerMM
    TestManager managermm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    public void toTest(View v) {
        Intent it=new Intent();
        switch (v.getId()) {
            case R.id.ioctest:
                it.setClass(this, IocTestActivity.class);
                //传递数据
                it.putExtra("str", "这段文本来自"+this.getClass().getSimpleName());
                it.putExtra("int", 1000);
                JSONObject jo=new JSONObject();
                JSONUtil.put(jo, "name", "tengzhinei");
                it.putExtra("jo", jo.toString());
                break;
            case R.id.dbtest:
                it.setClass(this, DbStudentListActivity.class);
                break;
            case R.id.nettest:
                it.setClass(this, NetTestActivity.class);
                break;
//            case R.id.adaptertest:
//                it.setClass(this, AdapterTestMainActivity.class);
//                break;
            case R.id.eventtest:{
                it.setClass(this, EventBusActivity.class);
                break;
            }
//            case R.id.perferencetest:{
//                it.setClass(this, PerferenceTestActivity.class);
//                break;
//            }
//            case R.id.othertest:{
//                it.setClass(this, OtherMain.class);
//                break;
//            }
            default:
                break;
        }
        startActivity(it);
    }
}
