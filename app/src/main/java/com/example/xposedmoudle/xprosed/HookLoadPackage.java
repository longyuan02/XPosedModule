package com.example.xposedmoudle.xprosed;

import android.os.Bundle;
import android.widget.TextView;

import java.lang.reflect.Field;

import de.robv.android.xposed.IXposedHookLoadPackage;
import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;
import de.robv.android.xposed.callbacks.XC_LoadPackage;

public class HookLoadPackage implements IXposedHookLoadPackage {
    @Override
    public void handleLoadPackage(final XC_LoadPackage.LoadPackageParam loadPackageParam) throws Throwable {
        if (loadPackageParam.packageName.equals("com.example.xposedmoudle")) {
            XposedHelpers.findAndHookMethod("com.example.xposedmoudle.MainActivity", loadPackageParam.classLoader, "onCreate", Bundle.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                        }

                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            Class c = loadPackageParam.classLoader.loadClass("com.example.xposedmoudle.MainActivity");
                            Field fild = c.getDeclaredField("tv");
                            fild.setAccessible(true);
                            TextView tv = (TextView) fild.get(param.thisObject);
                            tv.setText("测试");
                        }
                    });
        }
    }
}
