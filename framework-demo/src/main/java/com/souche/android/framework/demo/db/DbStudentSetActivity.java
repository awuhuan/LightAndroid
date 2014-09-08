package com.souche.android.framework.demo.db;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.souche.android.framework.activity.BaseActivity;
import com.souche.android.framework.db.DBProxy;
import com.souche.android.framework.demo.R;
import com.souche.android.framework.demo.db.bean.Student;
import com.souche.android.framework.ioc.annotation.Inject;
import com.souche.android.framework.ioc.annotation.InjectExtra;
import com.souche.android.framework.ioc.annotation.InjectView;
import com.souche.android.framework.util.ViewUtil;

import java.util.Date;

/**
 * Created by shenyubao on 14-5-10.
 */
public class DbStudentSetActivity extends BaseActivity {
    @InjectView(id = R.id.name)
    EditText nameV;
    @InjectView(id = R.id.num)
    EditText numV;
    @InjectView(id = R.id.sex)
    EditText sexV;
    @InjectView(id = R.id.dangyuan)
    EditText dangyuanV;
    @InjectView(id = R.id.age)
    EditText ageV;
    @InjectView(id = R.id.create_time)
    TextView createTimeV;
    @InjectView(id = R.id.save, click = "onSave")
    View saveV;
    @Inject
    DBProxy db;
    @InjectExtra(name = "id")
    Long id;
    Student student;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.db_set_activity);
        if(id!=null&&id!=0){
            student=db.load(Student.class, id);
            ViewUtil.bindView(nameV, student.getName());
            ViewUtil.bindView(numV, student.getNum());
            ViewUtil.bindView(sexV, student.getSex());
            ViewUtil.bindView(dangyuanV, student.isDangyuang()?"1":"0");
            ViewUtil.bindView(ageV, student.getAge());
            ViewUtil.bindView(createTimeV, student.getCreateTime(),"toTime");

        }
    }

    /**
     * 保存
     * @param v
     */
    public void onSave(View v) {
        boolean isnew=false;
        if(student == null){
            student=new Student();
            isnew=true;
        }
        student.setId(Long.parseLong(numV.getText().toString()));
        student.setName(nameV.getText().toString());
        student.setNum(numV.getText().toString());
        student.setSex(Integer.parseInt(sexV.getText().toString()));
        student.setAge(Integer.parseInt(ageV.getText().toString()));
        student.setDangyuang(dangyuanV.getText().toString().equals("1")?true:false);
        student.setCreateTime(new Date());
        if(isnew){
            db.save(student);
        }else{
            db.update(student);
        }
        finish();
    }
}
