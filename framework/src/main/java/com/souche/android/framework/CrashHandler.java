package com.souche.android.framework;

import android.content.Context;
import android.os.Debug;
import android.os.Environment;
import android.util.Log;

import com.souche.android.framework.ioc.IocContainer;
import com.souche.android.framework.util.FileUtil;
import com.souche.android.framework.activity.ActivityTack;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Date;


/**
 * Created by shenyubao on 14-5-8.
 */
public class CrashHandler implements Thread.UncaughtExceptionHandler {
    public static final String TAG = "CrashHandler";
    private static final String OOM = "java.lang.OutOfMemoryError";
    private static final String HPROF_FILE_PATH = Environment.getExternalStorageDirectory().getPath() + "/data.hprof";

    private static CrashHandler sCrashHandler;

    private CrashHandler() {
    }

    public synchronized static CrashHandler getInstance() {
        if (sCrashHandler == null) {
            sCrashHandler = new CrashHandler();
        }
        return sCrashHandler;
    }

    public static boolean isOOM(Throwable throwable) {
        Log.d(TAG, "getName:" + throwable.getClass().getName());
        if (OOM.equals(throwable.getClass().getName())) {
            return true;
        } else {
            Throwable cause = throwable.getCause();
            if (cause != null) {
                return isOOM(cause);
            }
            return false;
        }
    }

    public CrashHandler init() {
        Thread.setDefaultUncaughtExceptionHandler(this);
        return this;
    }

    public void uploadLast(Context context, String url, String name) {
        File cachefile = FileUtil.getCacheDir();
        final File file = new File(cachefile, "error.log");
        if (file.exists()) {
            //TODO:网络上传
//            new DhNet(url).upload(name, file, new NetTask(context) {
//                @Override
//                public void doInUI(Response response, Integer transfer) {
//                    if (transfer == NetTask.TRANSFER_DOUI) {
//                        file.delete();
//                    }
//                }
//            });
        }
    }

    public void uncaughtException(Thread thread, Throwable throwable) {
        if (isOOM(throwable)) {
            try {
                Debug.dumpHprofData(HPROF_FILE_PATH);
            } catch (Exception e) {
                Log.e(TAG, "couldn’t dump hprof", e);
            }
        }

        File logdir = FileUtil.getCacheDir();
        try {
            FileWriter fw = new FileWriter(new File(logdir, "error.log"), true);
            fw.write(new Date() + "\n");
            StackTraceElement[] stackTrace = throwable.getStackTrace();
            fw.write(throwable.getMessage() + "\n");
            for (int i = 0; i < stackTrace.length; i++) {
                fw.write("file:" + stackTrace[i].getFileName() + " class:"
                        + stackTrace[i].getClassName() + " method:"
                        + stackTrace[i].getMethodName() + " line:"
                        + stackTrace[i].getLineNumber() + "\n");
            }
            fw.write("\n");
            fw.flush();
            fw.close();
        } catch (IOException e) {
            Log.e("crash handler", "load file failed...", e.getCause());
        }
        ActivityTack.getInstanse().exit(IocContainer.getShare().getApplicationContext());
        android.os.Process.killProcess(android.os.Process.myPid());
        System.exit(1);
    }
}
