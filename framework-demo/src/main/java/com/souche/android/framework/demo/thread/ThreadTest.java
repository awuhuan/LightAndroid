package com.souche.android.framework.demo.thread;

import android.os.Bundle;
import android.view.View;

import com.souche.android.framework.activity.BaseActivity;
import com.souche.android.framework.demo.R;
import com.souche.android.framework.demo.db.bean.Student;
import com.souche.android.framework.dialog.IDialog;
import com.souche.android.framework.ioc.annotation.Inject;
import com.souche.android.framework.ioc.annotation.InjectView;
import com.souche.android.framework.thread.Task;
import com.souche.android.framework.thread.ThreadWorker;

public class ThreadTest extends BaseActivity {

    @Inject
    IDialog dialoger;

    @InjectView(id = R.id.button1, click = "onTest")
    View test;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_thread_test);
    }

    public void onTest(View v) {
        ThreadWorker.execuse(true, new Task(this) {
            Student student;

            @Override
            public void doInBackground() {
                super.doInBackground();
                //后台处理
                student = new Student();
                transfer("线程间的交互", 100);
                try {
                    Thread.sleep(4000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }

            @Override
            public void doInUI(Object obj, Integer what) {
                if (student != null) {
                    if (what == 100) {
                        dialoger.showToastShort(ThreadTest.this, obj.toString());
                    } else {
                        dialoger.showToastShort(ThreadTest.this, "处理完成");
                    }
                }
            }
        });
    }
}
