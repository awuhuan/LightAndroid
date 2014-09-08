package com.souche.android.framework.cache;

import com.souche.android.framework.db.DBProxy;
import com.souche.android.framework.db.SqlProxy;
import com.souche.android.framework.ioc.IocContainer;
import com.souche.android.framework.util.MD5;

import java.util.Calendar;
import java.util.Date;
import java.util.Map;

/**
 * Created by shenyubao on 14-5-10.
 */
public class CacheManager {
    DBProxy db;

    public CacheManager() {
        super();
        db = IocContainer.getShare().get(DBProxy.class);
    }

    /**
     * 创建缓存
     *
     * @param url
     * @param params
     * @param result
     */
    public void create(String url, Map<String, Object> params, String result) {
        delete(url, params);
        Cache cache = new Cache();
        cache.setKey(buildKey(url, params));
        cache.setResult(result);
        cache.setUpdateTime(System.currentTimeMillis());
        if (cache != null) {
            db.save(cache);
        }
    }

    /**
     * 创建缓存
     * @param key
     * @param value
     */
    public void create(String key, String value) {
        delete(key);
        Cache cache = new Cache();
        cache.setKey(key);
        cache.setResult(value);
        cache.setUpdateTime(System.currentTimeMillis());
        if (cache != null) {
            db.save(cache);
        }
    }

    /**
     * 获取缓存
     *
     * @param url
     * @param params
     */
    public String get(String url, Map<String, Object> params) {
        Cache cache = db.queryFrist(Cache.class, "key = ?", buildKey(url, params));
        if (cache != null) {
            return cache.getResult();
        }
        return null;
    }

    /**
     * 获取缓存
     * @param key
     * @return
     */
    public String get(String key) {
        Cache cache = db.queryFrist(Cache.class, "key = ?", key);
        if (cache != null) {
            return cache.getResult();
        }
        return null;
    }

    /**
     * 删除缓存
     *
     * @param url
     * @param params
     */
    public void delete(String url, Map<String, Object> params) {
        db.execProxy(SqlProxy.delete(Cache.class, "key = ?", buildKey(url, params)));
    }

    /**
     * 删除缓存
     * @param key
     */
    public void delete(String key) {
        db.execProxy(SqlProxy.delete(Cache.class, "key = ?", key));
    }

    /**
     * 删除多少天前的缓存
     *
     * @param dayAgo
     */
    public void deleteByDate(Integer dayAgo) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(new Date());
        calendar.add(Calendar.DAY_OF_MONTH, -dayAgo);
        Date time = calendar.getTime();
        db.execProxy(SqlProxy.delete(Cache.class, "key < ?", time.getTime()));
    }

    private String buildKey(String url, Map<String, Object> params) {
        if (params != null) {
            url += params.toString();
        }
        try {
            return MD5.encryptMD5(url);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return url;
    }

}
