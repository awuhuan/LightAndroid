package com.souche.android.framework.adapter;

import com.souche.android.framework.net.Response;

/**
 * Created by shenyubao on 14-5-10.
 */
public interface INetAdapter {
    public String getTag();

    public void refresh();

    public Boolean hasMore();

    public void showNext();

    public boolean isLoding();

    public void showNextInDialog();

    public void setOnLoadSuccess(LoadSuccessCallBack loadSuccessCallBack);

    public void removeOnLoadSuccess(LoadSuccessCallBack loadSuccessCallBack);

    public void setOnTempLoadSuccess(LoadSuccessCallBack loadSuccessCallBack);

    public interface LoadSuccessCallBack {
        public void callBack(Response response);
    }
}
