package com.souche.android.framework.cache;

import com.souche.android.framework.db.annotation.Column;

/**
 * Created by shenyubao on 14-5-9.
 */
public class Cache {
    public Cache() {
    }

    @Column(pk=true)
    public Integer id;
    public String key;
    public String result;
    public Long updateTime;



    public Long getUpdateTime() {
        return updateTime;
    }

    public void setUpdateTime(Long updateTime) {
        this.updateTime = updateTime;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }
}
