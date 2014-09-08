package com.souche.android.framework.activity;

import android.os.Bundle;
import android.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.souche.android.framework.Const;
import com.souche.android.framework.ioc.InjectUtil;

/**
 * Created by shenyubao on 14-5-16.
 */
public class BaseFragment extends Fragment {
    private ActivityTack tack = ActivityTack.getInstanse();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //TODO:纳入ActivityTrack
    }

    @Override
    public void onStop() {
        super.onStop();
        //TODO:纳入ActivityTrack
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = super.onCreateView(inflater, container, savedInstanceState);
        return view;
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        if (Const.auto_inject) {
            InjectUtil.inject(this);
        }
    }
}
