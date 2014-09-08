package com.souche.android.framework.demo.db.bean;

import com.souche.android.framework.db.annotation.Column;
import com.souche.android.framework.db.annotation.NoColumn;

import java.util.Date;

/**
 * Created by shenyubao on 14-5-9.
 */
public class Student {
    @Column(pk=true)
    public Long id;
    public String name;
    public String num;
    @Column(name="create_time")
    public Date createTime;
    public int age;
    public int sex;
    public boolean dangyuang;
    @NoColumn
    public String temp;


    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getNum() {
        return num;
    }

    public void setNum(String num) {
        this.num = num;
    }

    public Date getCreateTime() {
        return createTime;
    }

    public void setCreateTime(Date createTime) {
        this.createTime = createTime;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public int getSex() {
        return sex;
    }

    public void setSex(int sex) {
        this.sex = sex;
    }

    public boolean isDangyuang() {
        return dangyuang;
    }

    public void setDangyuang(boolean dangyuang) {
        this.dangyuang = dangyuang;
    }

    public String getTemp() {
        return temp;
    }

    public void setTemp(String temp) {
        this.temp = temp;
    }
}
