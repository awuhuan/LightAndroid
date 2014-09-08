package com.souche.android.framework.dialog;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.TextView;
import android.widget.Toast;

import com.souche.android.framework.ioc.IocContainer;

/**
 * Created by shenyubao on 14-5-8.
 */
public class DialogImpl implements IDialog {
    public Dialog showDialog(Context context, String title, String msg, final DialogCallBack dialogCallBack) {
        AlertDialog.Builder builder = new AlertDialog.Builder(context)
                .setTitle(title).setMessage(msg).setNegativeButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if (dialogCallBack != null) {
                            dialogCallBack.onClick(IDialog.YES);
                        }
                    }
                });
        if (dialogCallBack != null) {
            builder.setPositiveButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    if (dialogCallBack != null) {
                        dialogCallBack.onClick(IDialog.CANCLE);
                    }
                }
            });
        }

        return builder.show();
    }

    public Dialog showItemDialog(Context context, String title,
                                 CharSequence[] items, final DialogCallBack callback) {
        Dialog dialog =
                new AlertDialog.Builder(context)
                        .setTitle(title).setItems(items, new DialogInterface.OnClickListener() {

                    public void onClick(DialogInterface dialog, int which) {
                        if (callback != null) {
                            callback.onClick(which);
                        }
                    }
                }).show();
        return dialog;

    }

    public Dialog showProgressDialog(Context context, String title, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setTitle(title);
        progressDialog.setMessage(msg);
        progressDialog.show();
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    public Dialog showProgressDialog(Context context, String msg) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.setMessage(msg);
//        progressDialog.dismiss();   //TODO:不甚了解
        progressDialog.show();
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    public Dialog showProgressDialog(Context context) {
        ProgressDialog progressDialog = new ProgressDialog(context);
        progressDialog.show();
        progressDialog.setCancelable(true);
        return progressDialog;
    }

    public void showToastLong(Context context, String msg) {
        //使用同一个toast避免 toast重复显示
        Toast toast = IocContainer.getShare().get(Toast.class);
        toast.setDuration(Toast.LENGTH_LONG);
        TextView textView = new TextView(context);
        textView.setText(msg);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(15, 10, 15, 10);
        textView.setBackgroundResource(android.R.drawable.toast_frame);
        toast.setView(textView);
        toast.show();
    }

    public void showToastShort(Context context, String msg) {
        //使用同一个toast避免 toast重复显示
        Toast toast = IocContainer.getShare().get(Toast.class);
        toast.setDuration(Toast.LENGTH_SHORT);
        TextView textView = new TextView(context);
        textView.setText(msg);
        textView.setTextColor(Color.WHITE);
        textView.setPadding(15, 10, 15, 10);
        textView.setBackgroundResource(android.R.drawable.toast_frame);
        toast.setView(textView);
        toast.show();
    }

    public void showToastType(Context context, String msg, String type) {
        showToastLong(context, msg);
    }

    public Dialog showDialog(Context context, int icon, String title, String msg,
                             DialogCallBack callback) {
        return showDialog(context, title, msg, callback);
    }

    public Dialog showAdapterDialoge(Context context, String title, ListAdapter adapter,
                                     AdapterView.OnItemClickListener itemClickListener) {
//		Dialog dialog=new ListDialog(context, title, adapter, itemClickListener);
//		dialog.show();
        return null;
    }

    class DialogOnItemClickListener implements AdapterView.OnItemClickListener {
        Dialog dialog;

        public void setDialog(Dialog dialog) {
            this.dialog = dialog;
        }

        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2,
                                long arg3) {

        }


    }
}
