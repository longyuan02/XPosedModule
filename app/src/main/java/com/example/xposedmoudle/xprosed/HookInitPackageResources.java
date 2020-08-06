package com.example.xposedmoudle.xprosed;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.example.xposedmoudle.R;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;
import de.robv.android.xposed.callbacks.XC_LayoutInflated;

public class HookInitPackageResources implements IXposedHookInitPackageResources {
    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam initPackageResourcesParam) throws Throwable {
        if (initPackageResourcesParam.packageName.equals("com.example.xposedmoudle")) {
            //更改资源文件 strings  colors style 图片等等
            initPackageResourcesParam.res.setReplacement("com.example.xposedmoudle", "string", "main_name", "111");
            XposedBridge.log("pring======" + initPackageResourcesParam.packageName + "**" + initPackageResourcesParam.res.getResourceEntryName(R.string.main_name));
            type2(initPackageResourcesParam);
        }
    }

    private void type1(XC_InitPackageResources.InitPackageResourcesParam initPackageResourcesParam) {
        initPackageResourcesParam.res.hookLayout(R.layout.activity_main, new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam layoutInflatedParam) throws Throwable {
                //layoutInflatedParam 返回对象是 androidx.appcompat.widget.ContentFrameLayout
                //在接收的时候如果使用需要androidx包,api版本低的会存在位置对象的错误,所以一下采用view来接收
                View linearLayout = (View) layoutInflatedParam.view;
                LinearLayout ln = linearLayout.findViewById(R.id.ln_main);
                TextView textView = new TextView(linearLayout.getContext());
                textView.setText(R.string.main_name);
                textView.setTextColor(Color.GREEN);
                //删除动作 需要注意
//                    ln.removeViewAt(0);
                LinearLayout.LayoutParams addLn = new LinearLayout.LayoutParams(100, 100);
                ln.addView(textView, addLn);
            }
        });
    }

    private void type2(XC_InitPackageResources.InitPackageResourcesParam initPackageResourcesParam) {
        initPackageResourcesParam.res.hookLayout(initPackageResourcesParam.packageName, "layout", "activity_main", new XC_LayoutInflated() {
            @Override
            public void handleLayoutInflated(LayoutInflatedParam layoutInflatedParam) throws Throwable {
                View linearLayout = (View) layoutInflatedParam.view;
                LinearLayout ln = linearLayout.findViewById(R.id.ln_main);
                ln.setOrientation(LinearLayout.VERTICAL);
                TextView textView = new TextView(linearLayout.getContext());
                textView.setText(R.string.main_name);
                textView.setTextColor(Color.GREEN);
                LinearLayout.LayoutParams addLn = new LinearLayout.LayoutParams(100, 100);
                ln.addView(textView, addLn);
            }
        });
    }

}
