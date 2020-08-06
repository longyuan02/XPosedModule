package com.example.xposedmoudle.replace;

import android.app.Activity;
import android.app.Instrumentation;
import android.content.Context;
import android.os.Looper;
import android.util.Log;

import java.lang.reflect.Field;
import java.lang.reflect.Method;

public class MyInstrumetation extends Instrumentation {
    private static final boolean DEBUG = false;
    private static final String TAG = "MyInstrumentation";
    private Instrumentation mInstrumentation;
    private Context mContext;

    public MyInstrumetation(Context context) {
        this.mContext = context;
        init();
    }

    private void init() {
        try {
            Class<?> activityThreadClass = null;
            activityThreadClass = Class.forName("android.app.ActivityThread");
            Method currentActivityThreadMethod = activityThreadClass.getMethod("currentActivityThread");
            Object sCurrentActivityThrad = currentActivityThreadMethod.invoke(null);
            //获得ActivityThread实例
            Class<?> asClass = sCurrentActivityThrad.getClass();
            Looper mLooper = (Looper) asClass.getMethod("getLooper").invoke(sCurrentActivityThrad);
            Field mInstumentationField = asClass.getDeclaredField("mInstrumentation");
            mInstumentationField.setAccessible(true);
            mInstrumentation = (Instrumentation) mInstumentationField.get(sCurrentActivityThrad);
            Class<? extends Instrumentation> mInstrumentationClass = mInstrumentation.getClass();
//            Field mThread = mInstrumentationClass.getDeclaredField("mThread");
//            mThread.setAccessible(true);
//            mThread.set(mInstrumentation, sCurrentActivityThrad);
//            Field mMessageQueue = mInstrumentationClass.getDeclaredField("mMessageQueue");
//            mMessageQueue.setAccessible(true);
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
//                mMessageQueue.set(mInstrumentation, mLooper.getQueue());
//            } else {
////                Field mQueueField = mLooper.getClass().getField("mQueue");
////                mQueueField.setAccessible(true);
////                mQueueField.set(mInstrumentation, mQueueField.get(mLooper));
//            }
            mInstumentationField.set(sCurrentActivityThrad, this);
        } catch (Exception e) {
            Log.e(TAG, "======" + e.getMessage().toString());
        }
    }

    @Override
    public void callActivityOnPause(Activity activity) {
        Log.e("======", "hook--->");
        super.callActivityOnPause(activity);
        callActivityOnResume(activity);

    }
}
