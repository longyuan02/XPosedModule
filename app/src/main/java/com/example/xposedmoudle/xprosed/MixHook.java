package com.example.xposedmoudle.xprosed;

import android.content.res.XModuleResources;

import com.example.xposedmoudle.R;

import de.robv.android.xposed.IXposedHookInitPackageResources;
import de.robv.android.xposed.IXposedHookZygoteInit;
import de.robv.android.xposed.callbacks.XC_InitPackageResources;

public class MixHook implements IXposedHookInitPackageResources, IXposedHookZygoteInit {
    private static String MODULE_PATH = null;

    @Override
    public void handleInitPackageResources(XC_InitPackageResources.InitPackageResourcesParam resparam) throws Throwable {
        /**
         * 参数createInstance(String path, XResources origRes)
         * path 路径:在那个apk中加载
         * resparam:设置资源文件
         */
//        if(){}判断目标包名
        XModuleResources modRes = XModuleResources.createInstance(MODULE_PATH, resparam.res);
        resparam.res.addResource(modRes, R.drawable.ic_launcher_foreground);
        resparam.res.addResource(modRes, R.drawable.ic_launcher_background);
    }

    @Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        MODULE_PATH = startupParam.modulePath;
    }
}
