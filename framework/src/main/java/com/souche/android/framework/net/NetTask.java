package com.souche.android.framework.net;

import android.app.Dialog;
import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.souche.android.framework.Const;
import com.souche.android.framework.ioc.IocContainer;

/**
 * Created by shenyubao on 14-5-10.
 */
public abstract class NetTask {
    /**
     * 线程ui传递
     */
    public static final int TRANSFER_DOUI = -400;
    /**
     * 利用缓存
     */
    public static final int TRANSFER_DOUI_ForCache = -403;

    /**
     * 线程取消传递
     */
    public static final int TRANSFER_DOCANCEL = -401;

    /**
     * 线程错误传递
     */
    public static final int TRANSFER_DOERROR = -800;

    public static final int TRANSFER_DOERROR_ForCache = -801;

    /**
     * 网络超时
     */
    public static final int TRANSFER_TIMEOUT = -803;

    /**
     * code code處理
     */
    public static final int TRANSFER_CODE=-802;

    /**
     *
     */
    Context mContext;

    Response cacheResponse;

    Response response;

    public Dialog dialog;

    public NetTask(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public Response getResponse() {
        return response;
    }

    static Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            NetTask task = (NetTask) msg.obj;
            Response response = task.getResponse();
            switch (msg.what) {
                case TRANSFER_DOCANCEL:
                    try {
                        task.onCancelled();
                    } catch (Exception e) {
                        if (!Const.net_error_try) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                case TRANSFER_DOERROR:
                    // 网络访问失败时关闭对话框
                    if (task.dialog != null) {
                        task.dialog.dismiss();
                        task.dialog = null;
                    }
                    try {
//                        task.onError(response);
                        task.doInUI(response, msg.what);
                    } catch (Exception e) {
                        if (!Const.net_error_try) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                case TRANSFER_DOERROR_ForCache:
                    try {
                        task.onError(task.cacheResponse);
                    } catch (Exception e) {
                        if (!Const.net_error_try) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                case TRANSFER_DOUI:
                    if (task.dialog != null) {
                        task.dialog.dismiss();
                        task.dialog = null;
                    }
                    try {
                        task.doInUI(response, msg.what);
                    } catch (Exception e) {
                        if (!Const.net_error_try) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                case TRANSFER_DOUI_ForCache:
                    try {
                        task.doInUI(task.cacheResponse, msg.what);
                    } catch (Exception e) {
                        if (!Const.net_error_try) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                case TRANSFER_CODE:{
                    try {
                        task.OnCode(response);
                    } catch (Exception e) {
                        if (!Const.net_error_try) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }
                case TRANSFER_TIMEOUT:{
                    if (task.dialog != null) {
                        task.dialog.dismiss();
                        task.dialog = null;
                    }
                    try {
                        task.doInUI(response, msg.what);
                    } catch (Exception e) {
                        if (!Const.net_error_try) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
                }
                default:
                    try {
                        task.doInUI(response, msg.what);
                    } catch (Exception e) {
                        if (!Const.net_error_try) {
                            throw new RuntimeException(e);
                        }
                    }
                    break;
            }
        }
    };

    public void doInBackground(Response response) {

    }

    /**
     * 错误处理
     */
    public void onError(Response response) {
        if (response.code != null) {
            if (response.msg != null) {
//				IDialog dialoger = IocContainer.getShare().get(IDialog.class);
//				dialoger.showToastShort(mContext, response.msg);//
            }
        }
    }

    /**
     * 任务取消时运行
     */
    public void onCancelled() {

    }


    /**
     * 后台code处理默认调用全局的code处理
     * @param response
     */
    public void OnCode(Response response){
//		CodeHandler codehandler=IocContainer.getShare().get(CodeHandler.class);
//		if(codehandler!=null){
//			if(mContext==null){
//				mContext=IocContainer.getShare().getApplicationContext();
//			}
//			codehandler.onCode(mContext, response, response.code);
//		}
    }

    /**
     * 前台ui处理 transfer
     *
     * @param response
     */
    public abstract void doInUI(Response response, Integer transfer);

    /**
     * 线程传递 将数据由后台线程传到前台 <br/>
     * 自己调用时不可 what 必须>0 防止和系统自定的冲突
     */
    public void transfer(Response response, Integer what) {
        Message msg = handler.obtainMessage();
        if (response.isCache) {
            this.cacheResponse = response;
        } else {
            this.response = response;
        }
        msg.what = what;
        msg.obj = this;
        handler.sendMessage(msg);
    }


}
