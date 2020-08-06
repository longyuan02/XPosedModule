package com.example.xposedmoudle;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

public class MyApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();
        this.registerActivityLifecycleCallbacks(new ActivityLifecycleCallbacks() {

            @Override
            public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
                Log.d(activity.getClass().getSimpleName(), "======onActivityCreated");
            }

            @Override
            public void onActivityStarted(Activity activity) {
                Log.d(activity.getClass().getSimpleName(), "======onActivityStarted");
            }

            @Override
            public void onActivityResumed(Activity activity) {
                Log.d(activity.getClass().getSimpleName(), "======onActivityResumed");
            }

            @Override
            public void onActivityPaused(Activity activity) {
                Log.d(activity.getClass().getSimpleName(), "======onActivityPaused");
            }

            @Override
            public void onActivityStopped(Activity activity) {
                Log.d(activity.getClass().getSimpleName(), "======onActivityStopped");
            }

            @Override
            public void onActivitySaveInstanceState(Activity activity, Bundle outState) {

            }

            @Override
            public void onActivityDestroyed(Activity activity) {
                Log.d(activity.getClass().getSimpleName(), "======onActivityDestroyed");
            }
        });

    }

}
