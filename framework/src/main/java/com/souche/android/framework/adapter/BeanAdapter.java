package com.souche.android.framework.adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.souche.android.framework.ioc.IocContainer;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * Created by shenyubao on 14-5-10.
 */
public abstract class BeanAdapter<T> extends BaseAdapter {
    private final Object mLock = new Object();
    public List<T> mVaules = null;
    public Map<Integer, InViewClickListener> canClickItem;
    public boolean isReuse = true;
    public Context mContext;
    public Class jumpClazz;
    public String jumpKey;
    public String jumpAs;
    public ValueFix fixer;
    protected int mResource;
    protected int mDropDownResource;
    protected boolean mNotifyOnChange = true;
    private LayoutInflater mInflater;

    public BeanAdapter(Context context, int mResource, boolean isViewReuse) {
        super();
        this.mResource = mResource;
        isReuse = isViewReuse;
        this.mDropDownResource = mResource;
        mContext = context;
        mInflater = LayoutInflater.from(context);
        mVaules = new ArrayList<T>();
        fixer = IocContainer.getShare().get(ValueFix.class);
    }

    public BeanAdapter(Context context, int mResource) {
        this(context, mResource, true);
    }

    public Class getJumpClazz() {
        return jumpClazz;
    }

    public String getJumpKey() {
        return jumpKey;
    }

    public String getJumpAs() {
        return jumpAs;
    }

    public void setJump(Class jumpClazz, String jumpkey, String as) {
        this.jumpClazz = jumpClazz;
        this.jumpKey = jumpkey;
        this.jumpAs = as;
    }

    @SuppressWarnings("unchecked")
    public <T> List<T> getValues() {
        return (List<T>) mVaules;
    }

    public void add(T one) {
        synchronized (mLock) {
            mVaules.add(one);
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }

    public void addAll(List<T> ones) {
        synchronized (mLock) {
            mVaules.addAll(ones);
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }

    public void insert(int index, T one) {
        synchronized (mLock) {
            mVaules.add(index, one);
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }

    public void remove(int index) {
        synchronized (mLock) {
            mVaules.remove(index);
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }

    public void clear() {
        synchronized (mLock) {
            mVaules.clear();
        }
        if (mNotifyOnChange)
            notifyDataSetChanged();
    }

    public void setNotifyOnChange(boolean notifyOnChange) {
        mNotifyOnChange = notifyOnChange;
    }

    public int getCount() {
        return mVaules.size();
    }

    public Object getItem(int position) {
        return mVaules.get(position);
    }

    @SuppressWarnings("unchecked")
    public <T> T getTItem(int position) {
        return (T) mVaules.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public String getTItemId(int position) {

        return position + "";
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View view;
        if (isReuse) {
            if (convertView == null) {
                view = mInflater.inflate(mResource, parent, false);
            } else {
                view = convertView;
            }

        } else {
            if (convertView != null) {
                parent.removeView(convertView);
            }
            view = mInflater.inflate(mResource, parent, false);
        }
        bindView(view, position, mVaules.get(position));
        bindInViewListener(view, position, mVaules.get(position));
        return view;
    }

    public View getDropDownView(int position, View convertView, ViewGroup parent) {
        View view;
        if (convertView == null) {
            view = mInflater.inflate(mDropDownResource, parent, false);
        } else {
            view = convertView;
        }
        bindView(view, position, mVaules.get(position));
        bindInViewListener(view, position, mVaules.get(position));
        return view;
    }

    public abstract void bindView(View itemV, int position, T jo);


    private void bindInViewListener(final View itemV, final Integer position,
                                    final Object valuesMap) {
        if (canClickItem != null) {
            for (Integer key : canClickItem.keySet()) {
                View inView = itemV.findViewById(key);
                final InViewClickListener inviewListener = canClickItem
                        .get(key);
                if (inView != null && inviewListener != null) {
                    inView.setOnClickListener(new View.OnClickListener() {

                        public void onClick(View v) {
                            inviewListener.OnClickListener(itemV, v, position,
                                    valuesMap);
                        }
                    });
                }
            }
        }
    }

    public void setOnInViewClickListener(Integer key,
                                         InViewClickListener inViewClickListener) {
        if (canClickItem == null)
            canClickItem = new HashMap<Integer, InViewClickListener>();
        canClickItem.put(key, inViewClickListener);
    }

    public int getmDropDownResource() {
        return mDropDownResource;
    }

    public void setmDropDownResource(int mDropDownResource) {
        this.mDropDownResource = mDropDownResource;
    }


    public interface InViewClickListener {
        public void OnClickListener(View parentV, View v, Integer position,
                                    Object values);
    }


    public class ViewHolder {
        Map<Integer, View> views;

        public ViewHolder() {
            super();
            views = new HashMap<Integer, View>();
        }

        public void put(Integer id, View v) {
            views.put(id, v);
        }

        public View get(Integer id) {
            return views.get(id);
        }

    }
}
