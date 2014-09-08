package com.souche.android.framework.demo.net;

import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.souche.android.framework.activity.BaseActivity;
import com.souche.android.framework.net.CachePolicy;
import com.souche.android.framework.demo.R;
import com.souche.android.framework.dialog.IDialog;
import com.souche.android.framework.ioc.IocContainer;
import com.souche.android.framework.ioc.annotation.Inject;
import com.souche.android.framework.ioc.annotation.InjectAssert;
import com.souche.android.framework.ioc.annotation.InjectView;
import com.souche.android.framework.net.NetProxy;
import com.souche.android.framework.net.NetTask;
import com.souche.android.framework.net.Response;
import com.souche.android.framework.net.download.DownLoadManager;
import com.souche.android.framework.net.download.DownloadTask;
import com.souche.android.framework.util.FileUtil;

import org.json.JSONObject;

import java.io.File;

/**
 * Created by shenyubao on 14-5-10.
 */
public class NetTestActivity extends BaseActivity {

    @InjectView(id = R.id.gettest, click = "onTestGet")
    View getTestV;
    @InjectView(id = R.id.getdialogtest, click = "onTestGetDialoge")
    View getDialogTestV;
    @InjectView(id = R.id.posttest, click = "onTestPost")
    View postTestV;
    @InjectView(id = R.id.postdialogtest, click = "onTestPost")
    View postDialogeTestV;

    @InjectView(id = R.id.cache_only, click = "onTestCache")
    View cacheOnlyTestV;
    @InjectView(id = R.id.cache_refresh, click = "onTestCache")
    View cacheRefreshTestV;
    @InjectView(id = R.id.cache_net_error, click = "onTestCache")
    View cacheNetErrorTestV;
    @InjectView(id = R.id.cache_b_a, click = "onTestCache")
    View cacheBaTestV;

    @InjectView(id = R.id.upload, click = "onUpload")
    View upload;

    @InjectView(id = R.id.resultV)
    TextView resultV;

    @InjectView(id = R.id.btnDownload, click = "onDownload")
    Button btnDownload;


    @Inject
    IDialog dialoger;
    JSONObject jodate;

    //注入文件,因为注入文件时是在新线程里,所以建议在之前的页面就注入一次,不然文件大了会在使用时还没拷贝完成
    @InjectAssert(path = "anzhi.apk")
    File apkFile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.net_test_activyty);
    }

    public NetTestActivity getActivity() {
        return this;
    }

    /**
     * get测试
     *
     * @param v
     */
    public void onTestGet(View v) {
        NetProxy net = new NetProxy("http://youxianpei.c.myduohuo.com/mobile_index_adbjsonview?id=63");
        //添加参数
        net.addParam("key1", "key1");
        net.doGet(new NetTask(this) {
            @Override
            public void onError(Response response) {
                super.onError(response);
                //错误处理,出错后会先关闭对话框然后调用这个方法,默认不处理
            }

            @Override
            public void doInBackground(Response response) {
                super.doInBackground(response);
                //后台处理信息
                //可以向UI层传递数据
                response.addData("keyBundle", "传递的数据");
                transfer(response, 100);
            }

            @Override
            public void doInUI(Response response, Integer transfer) {
                if (transfer == 100) {
                    dialoger.showToastShort(getActivity(), response.getData("keyBundle").toString());
                } else {
                    resultV.setText(response.plain());
                }

            }
        });
    }

    /**
     * 带进度条的Android测试
     *
     * @param v
     */
    public void onTestGetDialoge(View v) {
        NetProxy net = new NetProxy("http://youxianpei.c.myduohuo.com/mobile_index_adbjsonview?id=63");
        net.doGetInDialog(new NetTask(this) {
            @Override
            public void doInUI(Response response, Integer transfer) {
                resultV.setText(response.plain());


            }
        });
    }

    /**
     * post测试
     *
     * @param v
     */
    public void onTestPost(View v) {
        NetProxy net = new NetProxy("http://youxianpei.c.myduohuo.com/mobile_index_adbjsonview?id=63");
        NetTask task = new NetTask(this) {
            @Override
            public void doInUI(Response response, Integer transfer) {
                resultV.setText(response.plain());
            }
        };
        if (v.getId() == R.id.posttest) {
            net.doPost(task);
        } else if (v.getId() == R.id.postdialogtest) {
            net.doPostInDialog(task);
        }
    }

    public void onTestCache(View v) {
        switch (v.getId()) {
            case R.id.cache_only: {
                NetProxy net = new NetProxy();
                net.setUrl("http://youxianpei.c.myduohuo.com/mobile_index_adbjsonview?id=63&temp=cache_only");
                net.useCache(CachePolicy.POLICY_CACHE_ONLY);
                net.doGet(new NetTask(getActivity()) {
                    @Override
                    public void doInUI(Response response, Integer transfer) {
                        resultV.setText(response.plain());
                        if (!response.isCache()) {
                            dialoger.showToastShort(getActivity(), "这次访问还没有缓存,访问了网络");
                        } else {
                            dialoger.showToastShort(getActivity(), "这次访问已有缓存,只用了缓存");
                        }
                    }
                });
            }
            break;
            case R.id.cache_net_error: {
                NetProxy net = new NetProxy();
                net.setUrl("http://youxianpei.c.myduohuo.com/mobile_index_adbjsonview?id=63&temp=cache_net_error");
                net.useCache(CachePolicy.POLICY_ON_NET_ERROR);
                net.doGet(new NetTask(getActivity()) {
                    @Override
                    public void doInUI(Response response, Integer transfer) {
                        resultV.setText(response.plain());
                        if (!response.isCache()) {
                            dialoger.showToastShort(getActivity(), "这次访问不是使用的缓存,断开网络试试看");
                        } else {
                            dialoger.showToastShort(getActivity(), "网络访问失败,这次使用的是缓存");
                        }
                    }
                });
            }
            break;
            case R.id.cache_refresh: {
                NetProxy net = new NetProxy();
                net.setUrl("http://youxianpei.c.myduohuo.com/mobile_index_adbjsonview?id=63&temp=cache_refresh");
                net.useCache(CachePolicy.POLICY_CACHE_AndRefresh);
                net.doGet(new NetTask(getActivity()) {
                    @Override
                    public void doInUI(Response response, Integer transfer) {
                        resultV.setText(response.plain());
                        if (!response.isCache()) {
                            dialoger.showToastShort(getActivity(), "这次访问还没有缓存");
                        } else {
                            dialoger.showToastShort(getActivity(), "现在使用的是缓存,并尝试更新缓存");
                        }
                    }
                });
            }
            break;
            case R.id.cache_b_a: {
                NetProxy net = new NetProxy();
                net.setUrl("http://youxianpei.c.myduohuo.com/mobile_index_adbjsonview?id=63&temp=cache_b_a");
                net.useCache(CachePolicy.POLICY_BEFORE_AND_AFTER_NET);
                net.doGet(new NetTask(getActivity()) {
                    @Override
                    public void doInUI(Response response, Integer transfer) {
                        resultV.setText(response.plain());
                        if (!response.isCache()) {
                            dialoger.showToastShort(getActivity(), "第二网络访问结果");
                        } else {
                            dialoger.showToastShort(getActivity(), "第一次是用的是缓存");
                        }
                    }
                });
            }
            break;
            default:
                break;
        }
    }

    public void onDownload(final View v) {
        DownLoadManager downLoadManager = IocContainer.getShare().get(DownLoadManager.class);
        String url = "http://img.my.csdn.net/uploads/201209/21/1348212837_9643.jpg";
        String path = FileUtil.getDir() + "/temp/1.jpg";
        Log.v("DOWNLOAD",path);
        String code = "pic1";
        downLoadManager.download(code, url, null, path);
        downLoadManager.regeisterCallBack(code, new DownLoadManager.DownLoadCallBack() {
            @Override
            public void onStop(DownloadTask task) {
                dialoger.showToastLong(v.getContext(), "停止");
            }

            @Override
            public void onPersent(DownloadTask task, float persent) {
                Log.v("DOWNLOAD", String.valueOf(persent));
            }

            @Override
            public void onEnd(DownloadTask task) {
                dialoger.showToastLong(v.getContext(), "下载完成");
            }
        });

    }

    public void onUpload(final View v) {
        NetProxy net = new NetProxy("http://www.duohuo.net");
        net.addParam("key1", "参数1").addParam("key2", "参数1").upload("fileName", apkFile, new NetTask(this) {
            @Override
            public void doInUI(Response response, Integer transfer) {
                if (response.isSuccess()) {
                    Boolean uploading = response.getData("uploading");
                    if (!uploading) {
                        dialoger.showToastLong(v.getContext(), "上传完成");
                    } else {
                        //已上传大小
                        long length = Long.parseLong(response.getData("length").toString());
                        //文件总大小
                        long total = Long.parseLong(response.getData("total").toString());
                    }
                }
            }
        });
    }


}
