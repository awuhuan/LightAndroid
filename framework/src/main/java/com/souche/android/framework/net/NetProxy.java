package com.souche.android.framework.net;

import android.app.Dialog;
import android.text.TextUtils;
import android.util.Log;
import android.widget.TextView;

import com.souche.android.framework.Const;
import com.souche.android.framework.cache.CacheManager;
import com.souche.android.framework.dialog.IDialog;
import com.souche.android.framework.ioc.IocContainer;
import com.souche.android.framework.net.upload.CancelException;
import com.souche.android.framework.net.upload.FileInfo;
import com.souche.android.framework.net.upload.PostFile;
import com.souche.android.framework.net.upload.ProgressMultipartEntity;
import com.souche.android.framework.util.NetworkUtils;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.cookie.Cookie;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.util.EntityUtils;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.net.SocketTimeoutException;
import java.net.UnknownHostException;
import java.nio.charset.Charset;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeoutException;

/**
 * Created by shenyubao on 14-5-10.
 */
public class NetProxy {
    public static final String METHOD_GET = "GET";
    public static final String METHOD_POST = "POST";
    static ExecutorService executorService;
    // 最后一次访问网络花费的时间
    private static int lastSpeed = 10;
    public int TRANSFER_UPLOADING = -40000;
    Boolean isCanceled = false;
    Future<?> feture;
    NetTask task;
    //缓存管理
    String dialogerMsg;
    CacheManager cacheManager;
    CachePolicy cachePolicy = CachePolicy.POLICY_NOCACHE;
    GlobalParams globalParams;
    private String url = null;
    private Map<String, Object> params = new HashMap<String, Object>();
    private String method = "POST";

    public NetProxy() {
        this(null);
    }

    public NetProxy(String url) {
        this(url, null);
    }

    public NetProxy(String url, Map<String, Object> params) {
        super();
        if (url != null) {
            this.url = url.trim();
        }
        if (params != null) {
            this.params.putAll(params);
        }
    }

    /**
     * 线程池里跑runnable
     *
     * @param runnable
     * @return
     */
    public static Future<?> executeRunalle(Runnable runnable) {
        if (executorService == null) {
            executorService = Executors.newFixedThreadPool(Const.net_pool_size);
        }
        return executorService.submit(runnable);
    }

    public static int getLastSpeed() {
        return lastSpeed;
    }

    public static void setLastSpeed(int lastSpeed) {
        NetProxy.lastSpeed = lastSpeed;
    }

    /**
     * 获取全部的cookie
     *
     * @return
     */
    public static List<Cookie> getCookies() {
        return HttpManager.getCookieStore().getCookies();
    }


    /**
     * 获取cookie的值
     *
     * @param key
     * @return
     */
    public static String getCookie(String key) {
        List<Cookie> cookies = getCookies();
        for (Iterator<Cookie> iterator = cookies.iterator(); iterator.hasNext(); ) {
            Cookie cookie = iterator.next();
            if (cookie.getName().equals(key)) {
                return cookie.getValue();
            }
        }
        return null;
    }

    /**
     * 清空cookie
     */
    public static void clearCookies() {
        HttpManager.getCookieStore().clear();
    }

    /**
     * 清空
     */
    public void clean() {
        params = new HashMap<String, Object>();
        if (globalParams != null) {
            Map<String, String> globalparams = globalParams.getGlobalParams();
            this.params.putAll(globalparams);
        }
    }

    /**
     * 使用缓存
     *
     * @param policy
     */
    public void useCache(CachePolicy policy) {
        this.cachePolicy = policy;
        if (cachePolicy != CachePolicy.POLICY_NOCACHE) {
            if (cacheManager == null) {
                cacheManager = IocContainer.getShare().get(CacheManager.class);
            }
        }
    }

    /**
     * 使用緩存
     */
    public void useCache(Boolean userCache) {
        if (userCache) {
            this.cachePolicy = CachePolicy.POLICY_ON_NET_ERROR;
            if (cachePolicy != CachePolicy.POLICY_NOCACHE) {
                if (cacheManager == null) {
                    cacheManager = IocContainer.getShare().get(CacheManager.class);
                }
            }
        } else {
            this.cachePolicy = CachePolicy.POLICY_NOCACHE;
        }
    }

    /**
     * 使用緩存
     */
    public void useCache() {
        this.cachePolicy = CachePolicy.POLICY_ON_NET_ERROR;
        if (cachePolicy != CachePolicy.POLICY_NOCACHE) {
            if (cacheManager == null) {
                cacheManager = IocContainer.getShare().get(CacheManager.class);
            }
        }
    }

    public NetProxy fixURl(String tag, Object value) {
        if (value != null) {
            this.url = this.url.replace("<" + tag + ">", value.toString());
        }
        return this;
    }

    /**
     * 添加参数
     *
     * @param key
     * @param value
     * @return
     */
    public NetProxy addParam(String key, Object value) {
        if (value instanceof TextView) {
            TextView text = (TextView) value;
            this.params.put(key.trim(), text.getText().toString());
        } else {
            if (key == null) {
                return this;
            }
            this.params.put(key.trim(), value);
        }
        return this;
    }

    /**
     * 添加参数
     *
     * @param params
     * @return
     */
    public NetProxy addParams(Map<String, Object> params) {
        this.params.putAll(params);
        return this;
    }

    /**
     * get方法访问
     *
     * @param task
     * @return
     */
    public NetProxy doGet(NetTask task) {
        this.method = METHOD_GET;
        execuse(task);
        return this;
    }



    /**
     * Get方法访问 TODO:增加缓存，网络状态，重试的判断
     * @return
     */
    public Object doGetSync() {
        try {
            this.method = METHOD_GET;
            return NetUtil.sync(this.url, this.method, this.params);
        } catch (IOException ex) {
            Log.e("network", ex.toString());
            return null;
        }
    }

    /**
     * get方法访问
     *
     * @param task
     * @return
     */
    public NetProxy doGet(boolean dialog, NetTask task) {
        this.method = METHOD_GET;
        return dialog ? execuseInDialog(task) : execuse(task);
    }

    /**
     * get方法访问 ,同时打开对话框
     *
     * @param task
     * @return
     */
    public NetProxy doGetInDialog(NetTask task) {
        this.method = METHOD_GET;
        execuseInDialog(task);
        return this;
    }

    /**
     * post方法访问
     *
     * @param task
     * @return
     */
    public NetProxy doPost(NetTask task) {
        this.method = METHOD_POST;
        execuse(task);
        return this;
    }

    /**
     * post方法访问 ,同时打开对话框
     *
     * @param task
     * @return
     */
    public NetProxy doPostInDialog(NetTask task) {
        this.method = METHOD_POST;
        execuseInDialog(task);
        return this;
    }

    /**
     * 执行网络访问
     *
     * @param task
     * @return
     */
    public NetProxy execuse(NetTask task) {
        this.task = task;
        boolean isCacheOk = false;
        //添加全局参数
        globalParams = IocContainer.getShare().get(GlobalParams.class);
        if (globalParams != null) {
            Map<String, String> globalparams = globalParams.getGlobalParams();
            params.putAll(globalparams);
        }

        // 在加载前加载缓存
        if (cachePolicy == CachePolicy.POLICY_CACHE_ONLY
                || cachePolicy == CachePolicy.POLICY_CACHE_AndRefresh
                || cachePolicy == CachePolicy.POLICY_BEFORE_AND_AFTER_NET) {
            if (cacheManager != null) {
                String result = cacheManager.get(url, params);
                if (result != null) {
                    Response response = new Response(result);
                    response.setCache(true);
                    try {
                        NetProxy.this.task.doInBackground(response);
                        NetProxy.this.task.doInUI(response, NetTask.TRANSFER_DOUI_ForCache);
                        // 缓存有数据就返回
                        isCacheOk = true;
                        if (NetProxy.this.task.dialog != null) {
                            NetProxy.this.task.dialog.dismiss();
                        }
                    } catch (Exception e) {
                        Log.e("NET_EXCEPTION", e.toString());
                    }

                    if (cachePolicy == CachePolicy.POLICY_CACHE_ONLY) {
                        return this;
                    }
                }
            }
        }

        final boolean isCacheOkf = isCacheOk;// 是否使用了缓存
        this.feture = executeRunalle(new Runnable() {
            @Override
            public void run() {
                boolean netAvailable = NetworkUtils.isNetworkAvailable();
                if (netAvailable) {
                    if (lastSpeed < HttpManager.DEFAULT_SOCKET_TIMEOUT) {
                        HttpManager.longTimeOut();
                    } else {
                        // 网络不好
                        HttpManager.shortTimeOut();
                    }
                    String url = NetProxy.this.url;
                    Map<String, Object> params = NetProxy.this.params;

                    try {
                        long begin = System.currentTimeMillis();
                        String result = NetUtil.sync(url, NetProxy.this.method, params);
                        long end = System.currentTimeMillis();
                        lastSpeed = (int) ((end - begin) / 1000);
                        Log.i("netproxy", NetProxy.this.url + " method: " + method + " params: " + params + " result: " + result);
                        Response response = new Response(result);
                        response.setCache(false);
                        String code = response.getCode();
                        NetProxy.this.task.transfer(response, NetTask.TRANSFER_CODE);
                        try {
                            NetProxy.this.task.doInBackground(response);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        if (code == null) {
                            if (cacheManager != null && cachePolicy != CachePolicy.POLICY_NOCACHE) {
                                if (response.jo != null) {
                                    cacheManager.create(url, params, response.result);
                                }
                            }
                        }

                        if (!isCanceled) {
                            // 当没有使用缓存或者缓存策略是网后更新
                            if (!isCacheOkf
                                    || cachePolicy == CachePolicy.POLICY_BEFORE_AND_AFTER_NET) {
                                NetProxy.this.task.transfer(response, NetTask.TRANSFER_DOUI);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                        onNetError(e, isCacheOkf);
                    }
                } else {
                    onNoNet(isCacheOkf);
                }
            }
        });
        return this;
    }

    /**
     * 在没有网的时候的处理
     */
    private void onNoNet(Boolean hasUserCache) {
        if (cachePolicy == CachePolicy.POLICY_ON_NET_ERROR) {
            if (cacheManager != null) {
                String result = cacheManager.get(url, params);
                if (result != null) {
                    Response response = new Response(result);
                    response.setCache(true);
                    NetProxy.this.task.doInBackground(response);
                    NetProxy.this.task.transfer(response, NetTask.TRANSFER_DOUI_ForCache);
                }
            }
        }

        String errorjson = "{'code':11001,'message':'没有可用的网络'}";
        Response response = new Response(errorjson);
        NetProxy.this.task.transfer(response, NetTask.TRANSFER_DOERROR);
    }

    /**
     * 处理网路异常
     *
     * @param e
     */
    private void onNetError(Exception e, Boolean hasUserCache) {
        lastSpeed = HttpManager.DEFAULT_SOCKET_TIMEOUT + 1;
        // 网络访问出错
        if (e instanceof UnknownHostException) {
            Log.e("netProxy", "域名不对可能是没有配置网络权限");
        }
        boolean isFromCache = false;
        if (cacheManager != null && cachePolicy == CachePolicy.POLICY_ON_NET_ERROR) {
            String result = cacheManager.get(url, params);
            if (result != null) {
                isFromCache = true;
                Response response = new Response(result);
                response.setCache(true);
                NetProxy.this.task.doInBackground(response);
                NetProxy.this.task.transfer(response, NetTask.TRANSFER_DOUI_ForCache);
            }
        }
        String errorjson;
        //网络超时
        if(e instanceof SocketTimeoutException){
            errorjson = "{'code':11110,'message':'网络超时'}";
            Response response = new Response(errorjson);
            response.addData("e", e);
            NetProxy.this.task.transfer(response, NetTask.TRANSFER_TIMEOUT);
            return ;
        }
        // 同时提示网络问题

        if (isFromCache) {
            errorjson = "{'code':11001,'message':'没有可用的网络(netErrorButCache)'}";
        } else {
            errorjson = "{'code':11001,'message':'没有可用的网络(netError)'}";
        }
        Response response = new Response(errorjson);
        response.addData("e", e);
        NetProxy.this.task.transfer(response, NetTask.TRANSFER_DOERROR);
    }

    /**
     * 执行同时打开对话框
     *
     * @param task
     * @return
     */
    public NetProxy execuseInDialog(NetTask task) {
        String msg = dialogerMsg;
        if (TextUtils.isEmpty(msg)) {
            msg = method.toUpperCase().equals(METHOD_GET) ? "加载中..." : "提交中...";
        }
        IDialog dialoger = IocContainer.getShare().get(IDialog.class);
        if (dialoger != null) {
            Dialog dialog = dialoger.showProgressDialog(task.mContext, msg);
            task.dialog = dialog;
        }
        execuse(task);
        return this;
    }

    /**
     * 取消访问 如果访问没有开始就永远不会启动访问<br/>
     * 如果访问已经启动 如果isInterrupt 为 true 则访问会被打断 , 否则 会线程继续运行 取消时必定会调用 task
     * 的onCancel方法
     *
     * @return
     */
    public Boolean cancel(Boolean isInterrupt) {
        this.isCanceled = true;
        if (feture != null) {
            feture.cancel(isInterrupt);
        }
        if (task != null) {
            task.onCancelled();
        }
        return true;
    }

    /**
     * 当网络访问没启动或被取消都返回 false
     *
     * @return
     */
    public Boolean isCanceled() {
        if (isCanceled != null) {
            return isCanceled;
        }
        return false;
    }

    /**
     * 文件上传, 支持大文件的上传 和文件的上传进度更新 task inui response 的bundle参数 uploading true
     * 上传中,false 上传完毕 ; process 上传进度 0-100 cancel 方法可以取消上传
     *
     * @param name
     * @param file
     * @param task
     */
    public void upload(final String name, final File file, NetTask task) {
        this.task = task;
        this.feture = executeRunalle(new Runnable() {
            @Override
            public void run() {
                String url = NetProxy.this.url;
                HttpPost httpPost = new HttpPost(url);
                final long fileLen = file.length();
                ProgressMultipartEntity mulentity = new ProgressMultipartEntity();
                mulentity.setProgressListener(new ProgressMultipartEntity.ProgressListener() {
                    @Override
                    public void transferred(long num) {
                        Response response = new Response("{code:'100000'}");
                        response.addData("uploading", true);
                        response.addData("length", num);
                        response.addData("total", fileLen);
                        NetProxy.this.task.transfer(response, TRANSFER_UPLOADING);
                    }

                    @Override
                    public boolean isCanceled() {
                        return NetProxy.this.isCanceled();
                    }
                });

                FileBody fileBody = new FileBody(file);
                mulentity.addPart(name, fileBody);
                try {
                    if (params != null) {
                        for (String key : params.keySet()) {
                            if (params.get(key) != null) {
                                mulentity.addPart(key, new StringBody(params.get(key).toString(), Charset.forName("UTF-8"))
                                );
                            }
                        }
                    }

                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
                httpPost.setEntity(mulentity);
                HttpResponse response;
                try {
                    response = HttpManager.execute(httpPost);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        HttpEntity rentity = response.getEntity();
                        String result = EntityUtils.toString(rentity);
                        Response myresponse = new Response(result);
                        myresponse.addData("uploading", false);
                        myresponse.addData("proccess", 100);
                        NetProxy.this.task.transfer(myresponse, NetTask.TRANSFER_DOUI);
                    }
                } catch (Exception e) {
                    if (e.getCause() instanceof UnknownHostException) {
                        Log.e("NetProxy", "域名不对可能是没有配置网络权限");
                    }
                    if (e instanceof CancelException) {
                        String errorjson = "{'message':'上传任务已被取消','code':'11002'}";
                        Response myresponse = new Response(errorjson);
                        myresponse.addData("e", e);
                        NetProxy.this.task.transfer(myresponse,NetTask.TRANSFER_DOERROR);
                    } else {
                        String errorjson = "{'message':'上传失败','code':'11002'}";
                        Response myresponse = new Response(errorjson);
                        myresponse.addData("e", e);
                        NetProxy.this.task.transfer(myresponse, NetTask.TRANSFER_DOERROR);
                    }
                }
            }
        });
    }

    /**
     * 小文件上传支持其他附加信息 不支持cookie
     *
     * @param fileInfo
     * @param task
     */
    public void upload(final FileInfo fileInfo, NetTask task) {
        this.task = task;
        this.feture = executeRunalle(new Runnable() {
            public void run() {
                try {
                    String result = PostFile.getInstance().post(getUrl(),
                            params, fileInfo);
                    Log.d("NetProxy", NetProxy.this.url + " method:" + method + " params: " + params + " result: " + result);
                    Response response = new Response(result);
                    response.setCache(false);
                    // 获取错误码
                    String code = response.getCode();
                    if (code != null) {
                        NetProxy.this.task.transfer(response, NetTask.TRANSFER_DOERROR);
                    }
                    NetProxy.this.task.doInBackground(response);
                    if (!isCanceled) {
                        NetProxy.this.task.transfer(response, NetTask.TRANSFER_DOUI);
                    }
                } catch (Exception e) {
                    String errorjson = "{'code':11001,'message':'没有可用的网络(netErrorButCache)'}";
                    Response response = new Response(errorjson);
                    response.addData("e", e);
                    NetProxy.this.task.transfer(response, NetTask.TRANSFER_DOERROR);
                }
            }
        });
    }

    public void setDialogerMsg(String dialogerMsg) {
        this.dialogerMsg = dialogerMsg;
    }


    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public Map<String, Object> getParams() {
        return params;
    }

    public void setParams(Map<String, Object> params) {
        this.params = params;
    }

    public String getMethod() {
        return method;
    }

    public void setMethod(String method) {
        this.method = method;
    }

    public GlobalParams getGlobalParams() {
        return globalParams;
    }

    public void setGlobalParams(GlobalParams globalParams) {
        this.globalParams = globalParams;
    }
}
