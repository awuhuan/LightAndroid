package com.souche.android.framework.demo;

import android.app.Application;

import com.souche.android.framework.Const;
import com.souche.android.framework.db.DBProxy;
import com.souche.android.framework.demo.ioc.TestDateHelper;
import com.souche.android.framework.demo.ioc.TestManagerMM;
import com.souche.android.framework.dialog.DialogImpl;
import com.souche.android.framework.dialog.IDialog;
import com.souche.android.framework.ioc.Instance;
import com.souche.android.framework.ioc.IocContainer;
import com.souche.android.framework.net.download.DownLoadManager;

/**
 * Created by shenyubao on 14-5-9.
 */
public class DemoApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        //一些常量的配置
        Const.netadapter_page_no = "p";
        Const.netadapter_step = "step";
//        Const.response_total = "totalRows";
        Const.response_data = "data";
        Const.netadapter_step_default = 7;
        Const.netadapter_json_timeline = "pubdate";
        Const.DATABASE_VERSION = 20;
        Const.net_pool_size = 30;
        Const.net_error_try = true;

        //IOC的初始化
        IocContainer.getShare().initApplication(this);

        //注册对话框
        IocContainer.getShare().bind(DialogImpl.class).to(IDialog.class)
                .scope(Instance.InstanceScope.SCOPE_SINGLETON);

        //注册下载器
        IocContainer.getShare().bind(DownLoadManager.class).to(DownLoadManager.class)
                .scope(Instance.InstanceScope.SCOPE_SINGLETON);

        //使用名字获取
        IocContainer.getShare().bind(TestManagerMM.class)
                .name("testmm")
                .scope(Instance.InstanceScope.SCOPE_SINGLETON);

        //注册原型模式
        IocContainer.getShare().bind(TestDateHelper.class)
                .to(TestDateHelper.class)
                .scope(Instance.InstanceScope.SCOPE_PROTOTYPE).perpare(new Instance.PerpareAction() {
            @Override
            public void perpare(Object o) {
                TestDateHelper helper = (TestDateHelper) o;
                helper.setName("我是在初始化是提供名字的");
            }
        });

        //配置ValueFix对象基本每个项目都有自己的实现
        IocContainer.getShare().bind(DemoValueFixer.class)
                .scope(Instance.InstanceScope.SCOPE_SINGLETON);
        //数据库初始化
        DBProxy db = IocContainer.getShare().get(DBProxy.class);
        db.init("dhdbname", Const.DATABASE_VERSION);
    }
}
