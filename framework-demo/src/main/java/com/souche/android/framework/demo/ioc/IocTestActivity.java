package com.souche.android.framework.demo.ioc;

import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.souche.android.framework.activity.BaseActivity;
import com.souche.android.framework.demo.R;
import com.souche.android.framework.dialog.IDialog;
import com.souche.android.framework.ioc.IocContainer;
import com.souche.android.framework.ioc.annotation.Inject;
import com.souche.android.framework.ioc.annotation.InjectAssert;
import com.souche.android.framework.ioc.annotation.InjectExtra;
import com.souche.android.framework.ioc.annotation.InjectResource;
import com.souche.android.framework.ioc.annotation.InjectView;
import com.souche.android.framework.util.ViewUtil;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by shenyubao on 14-5-8.
 */
public class IocTestActivity extends BaseActivity {

    // 注入文本
    @InjectAssert(path = "testtext.json")
    String testassert;

    // 注入Json
    @InjectAssert(path = "testtext.json")
    JSONObject jo;

    // 注入文件
    @InjectAssert(path = "anzhi.apk")
    File apkFile;

    // 注入视图
    @InjectView(id = R.id.asserttext)
    TextView testassertV;

    // 注入控件
    @InjectView(id = R.id.resstring)
    TextView resstrV;

    // 注入控件与事件
    @InjectView(id = R.id.assertFile, click = "toInstal")
    View instalApkV;

    // 注入控件
    @InjectView(id = R.id.child_layout)
    ViewGroup childLayoutV;

    //注入布局文件
    @InjectView(layout = R.layout.ioc_head)
    View headV;

    //在其他视图中查看
    @InjectView(id = R.id.intext, inView = "headV")
    TextView childTextV;

    // 注入字串
    @InjectResource(string = R.string.app_name)
    String appname;

    // 注入颜色(这里不能为int因为int有默认值0 有值的属性会被忽略,防止重复注入)
    @InjectResource(color = R.color.link)
    Integer colorLink;

    // 注入图片
    @InjectResource(drawable = R.drawable.ic_launcher)
    Drawable icDraw;

    // 注入dimen
    @InjectResource(dimen = R.dimen.testdimen)
    Float dime;

    // 接受传入的字符串
    @InjectExtra(name = "str", def = "默认值")
    String extra;

    // 接受传入的数字
    @InjectExtra(name = "int", def = "1")
    Integer extraint;

    // 传入Json
    @InjectExtra(name = "jo")
    JSONObject extrajo;

    //标准-单例注入
    @Inject
    IDialog dialoger;

    //注入Tag
    @Inject(tag = "manager1")
    TestManager manager1;
    @Inject(tag = "manager1")
    TestManager manager1copy;
    @Inject(tag = "manager2")
    TestManager manager2;
    @Inject(tag = "manager2")
    TestManager manager2copy;

    //根据名字注入对象
    @Inject(name="testmm")
    TestManager managermm;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.ioc_test_activity);

        ViewGroup.LayoutParams params = new ViewGroup.LayoutParams(ViewGroup.LayoutParams.FILL_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        childLayoutV.addView(headV, params);
        childTextV.setText("我在注入的布局里");

        testassertV.setText("assert text: " + testassert + "assert jo:" + jo);
        resstrV.setTextColor(colorLink);
        resstrV.setText(appname + "  textsize:" + dime);
        resstrV.setTextSize(dime);

        //绑定图像
        ViewUtil.bindView(findViewById(R.id.imageView1), icDraw);

        manager1.setName("第一个对象");
        manager2.setName("第二个对象");
        ViewUtil.bindView(findViewById(R.id.inject_stand), "manager1:" + manager1.getName() + " manager1copy:" + manager1copy.getName() + " manager2:" + manager2.getName() + " manager2copy:" + manager2copy.getName());

        //通过编码的方式获取对象
        TestManager testmanager = IocContainer.getShare().get(TestManager.class, "manager2");

        //通过接口获取单例
        IDialog d = IocContainer.getShare().get(IDialog.class);
        d.showToastShort(this, testmanager.getName());

        //测试事件，单例模式
        findViewById(R.id.button1).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
				IocContainer.getShare().get("testmm");
                dialoger.showToastShort(IocTestActivity.this, managermm.getName());
            }
        });

        //测试事件，原型模式
        findViewById(R.id.button2).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TestDateHelper helper = IocContainer.getShare().get(TestDateHelper.class);
                dialoger.showToastShort(IocTestActivity.this, helper.getName());
            }
        });
    }

    /**
     * 视图事件,安装事件
     *
     * @param v
     */
    public void toInstal(View v) {
        if (apkFile == null) {
            dialoger.showToastLong(this, "文件拷贝中..");
            return;
        }
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + apkFile.getAbsolutePath()), "application/vnd.android.package-archive");
        startActivity(i);
    }
}
