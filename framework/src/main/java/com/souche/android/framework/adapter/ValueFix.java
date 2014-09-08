package com.souche.android.framework.adapter;

import com.nostra13.universalimageloader.core.DisplayImageOptions;

/**
 * Created by shenyubao on 14-5-9.
 */
public interface ValueFix {
    public Object fix(Object o, String type);

    public DisplayImageOptions imageOptions(String type);
}
