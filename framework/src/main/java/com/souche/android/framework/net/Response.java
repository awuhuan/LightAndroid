package com.souche.android.framework.net;

import android.text.TextUtils;

import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;
import com.souche.android.framework.Const;
import com.souche.android.framework.util.JSONUtil;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by shenyubao on 14-5-10.
 */
public class Response {
    /**
     * 返回的纯文本
     */
    public String result;

    /**
     * 是否缓存的数据
     */
    public boolean isCache;

    /**
     * 数据暂存
     */
    public Map<String, Object> data;

    // 操作是否成功
    public Boolean success;

    // 消息
    public String msg;

    // 错误码
    public String code;

    // 分页时当前页
    public int current;

    // 分页时总页
    public int total;

    // 获取默认返回jsonarray jo对象的缓存 不一定有值
    JSONObject jo;

    public Response(String result) {
        data = new HashMap<String, Object>();
        this.result = result;
        this.success = false;
        // 默认处理结果为 true
        // 有返回success code 登按 返回结果
        if (!TextUtils.isEmpty(result)) {
            // json对象
            if (result.trim().startsWith("{")) {
                try {
                    jo = new JSONObject(result);
                    if (jo.has(Const.response_msg)) {
                        msg = jo.getString(Const.response_msg);
                    }
                    if (jo.has(Const.response_code)) {
                        code = JSONUtil.getString(jo, Const.response_code);
                        if (code.equals(Const.CODE_SUCCESS)) {
                            success = true;
                        } else {
                            success = false;
                        }
                    }


                } catch (JSONException e) {
                    e.printStackTrace();
                }
            } else if (result.trim().startsWith("[")) {
                //不处理
            }
        }
    }

    /**
     * 添加传递数据 基本在后台线程添加 前台用getBundle 获取
     *
     * @param key
     * @param obj
     */
    public void addData(String key, Object obj) {
        data.put(key, obj);
    }

    /**
     * 获取传递数据
     *
     * @param key
     * @return
     */
    @SuppressWarnings("unchecked")
    public <T> T getData(String key) {
        return (T) data.get(key);
    }

    /**
     * 返回纯文本
     *
     * @return
     */
    public String plain() {
        return result;
    }

    /**
     * 将返回的数据转换为jsonobject
     *
     * @return
     */
    public JSONObject jSON() {
        return jo;
    }

    /**
     * 将返回的数据转换为jsonArray
     *
     * @return
     */
    public JSONArray jSONArray() {
        try {
            return new JSONArray(this.result);
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * 解析json结果 为bean
     *
     * @return
     */
    public <T> T model(Class<T> clazz) {
        Gson gson = new Gson();
        T obj = gson.fromJson(result, clazz);
        return obj;
    }

    /**
     * 解析json结果 为 bean list
     *
     * @return
     */
    public <T> List<T> list(final Class<T> clazz) {
        Gson gson = new Gson();
        Type type = new ParameterizedType() {
            public Type getRawType() {
                return ArrayList.class;
            }

            public Type getOwnerType() {
                return null;
            }

            public Type[] getActualTypeArguments() {
                Type[] type = new Type[1];
                type[0] = clazz;
                return type;
            }
        };
        List<T> list = gson.fromJson(result, type);
        return list;
    }

    /**
     * 获取json结果 其中的某个属性 为 jsonarray
     *
     * @param prefix
     * @return
     */
    public JSONArray jSONArrayFrom(String prefix) {
        if (jo != null) {
            return JSONUtil.getJSONArray(jo, prefix);
        } else {
            return jSONArray();
        }
    }


    /**
     * 获取json结果 对象中的某个属性 为对象 prefix data
     *
     * @param prefix
     * @return
     */
    public <T> T modelFrom(String prefix) {
        if (jo != null) {
            String str = JSONUtil.getString(jo, prefix);
            Gson gson = new Gson();
            Type type = new TypeToken<T>() {
            }.getType();
            T obj = gson.fromJson(str, type);
            return obj;
        }
        return null;
    }

    /**
     * 解析json结果 为bean
     *
     * @return
     */
    public <T> T modelFrom(Class<T> clazz, String prefix) {
        String str = JSONUtil.getString(jo, prefix);
        Gson gson = new Gson();
        T obj = gson.fromJson(str, clazz);
        return obj;
    }

    public <T> T modelFromData() {
        return modelFrom(Const.response_data);
    }

    public <T> T modelFromData(Class<T> clazz) {
        return modelFrom(clazz, Const.response_data);
    }

    /**
     * 获取json结果 对象中的某个属性 为Map list
     *
     * @param prefix
     * @return
     */
    public <T> List<T> listFrom(final Class clazz, String prefix) {
        if (jo != null) {
            String str = JSONUtil.getString(jo, prefix);
            Gson gson = new Gson();
            Type type = new ParameterizedType() {
                public Type getRawType() {
                    return ArrayList.class;
                }

                public Type getOwnerType() {
                    return null;
                }

                public Type[] getActualTypeArguments() {
                    Type[] type = new Type[1];
                    type[0] = clazz;
                    return type;
                }
            };
            List<T> list = gson.fromJson(str, type);
            return list;
        }
        return null;
    }

    public <T> List<T> listFromData(Class<T> clazz) {
        return listFrom(clazz, Const.response_data);
    }


    public boolean isCache() {
        return isCache;
    }

    public void setCache(boolean isCache) {
        this.isCache = isCache;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    public Map<String, Object> getData() {
        return data;
    }

    public void setData(Map<String, Object> data) {
        this.data = data;
    }

    public Boolean isSuccess() {
        return success;
    }


    public String getMsg() {
        return msg;
    }

    public void setMsg(String msg) {
        this.msg = msg;
    }

    public String getCode() {
        return code;
    }

    public void setCode(String code) {
        this.code = code;
    }

    public JSONObject getJo() {
        return jo;
    }

    public void setJo(JSONObject jo) {
        this.jo = jo;
    }
}
