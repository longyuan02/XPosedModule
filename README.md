[TOC]
# XposedDemo
### 开发环境准备
        AS4.0
        手机api 19
        root工具:360超级ROOT
        相关补充资源: https://pan.baidu.com/s/1sN3Ux0wpbzZqD6AtE_bBpQ 提取码: pkcp
        [各种系统版本的XPosed框架资料下载](http://www.95bmf.com/archives/288)
### 一、Xposed简介
    Xposed是一款优秀的android java层 hook 框架。它允许你在不修改apk源码的情况下，通过编写自己的模块来改变apk的行为。它的优点是采用了插件机制，模块能够适用不同版本的框架和rom。模块改变apk行为的操作发生在内存中，对源apk不进行任何修改。你只需要安装编写的模块并重启相应的设备即可。
Xposed 官网：http://repo.xposed.info/
Xposed 项目 github 地址：https://github.com/rovo89
Xposed 官方教程 :https://github.com/rovo89/XposedBridge/wiki/Development-tutorial
#### Xposed工作原理
Xposed是一个很厉害的框架,可以在不改动app的情况下对app进行修改,比如更换元素,加载拦截等   

    Android基于Linux，第一个启动的进程自然是init进程，该进程会启动所有Android进程的父进程——Zygote(孵化)进程，该进程的启动配置在/init.rc脚本中，而Zygote进程对应的执行文件是/system/bin/app_process，该文件完成类库的加载以及一些函数的调用工作。在Zygote进程创建后，再fork出SystemServer进程和其他进程。而Xposed Framework呢，就是用自己实现的app_process替换掉了系统原本提供的app_process，加载一个额外的jar包，然后入口从原来的：com.android.internal.osZygoteInit.main()被替换成了：de.robv.android.xposed.XposedBridge.main()，然后创建的Zygote进程就变成Hook的Zygote进程了，而后面Fork出来的进程也是被Hook过的。这个Jar包在：/data/data/de.rbov.android.xposed.installer/bin/XposedBridge.jar

## 创建一个工程
+ Step 1：新建一个工程，然后修改下AndroidManifest.xml，增加下面的代码：
<pre><code>   
<manifest xmlns:android="http://schemas.android.com/apk/res/android"
    package="com.example.xposedmoudle">
    <application
        android:allowBackup="true"
        android:icon="@mipmap/ic_launcher"
        android:label="@string/app_name"
        android:roundIcon="@mipmap/ic_launcher_round"
        android:supportsRtl="true"
        android:theme="@style/AppTheme">
        <activity android:name=".MainActivity">
            <intent-filter>
                <action android:name="android.intent.action.MAIN" />
                <category android:name="android.intent.category.LAUNCHER" />
            </intent-filter>
        </activity>
        <!--是否作为模块-->
        <meta-data
            android:name="xposedmodule"
            android:value="true" />
        <meta-data
            android:name="xposeddescription"
            android:value="hellow xposed" />
        <!--这个版本对应的是引入的jar版本-->
        <meta-data
            android:name="xposedminversion"
            android:value="53" />
    </application>
</manifest>
</code></pre>

+ step2 引入依赖
在build.gradle(app)中添加依赖
注:只参与编译,不参与打包
<pre><code>
dependencies {
    implementation 'androidx.appcompat:appcompat:1.1.0'
    implementation 'androidx.constraintlayout:constraintlayout:1.1.3'
    ** compileOnly files('libs/api-53.jar')**
    **compileOnly files('libs/XposedBridgeApi-30.jar')**
    testImplementation 'junit:junit:4.12'
    androidTestImplementation 'androidx.test.ext:junit:1.1.1'
    androidTestImplementation 'androidx.test.espresso:espresso-core:3.2.0'
}
<code></pre>
+ step3 实现XPsoed入口类
    <pre><code>
    package com.example.xposedmoudle;
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
    </code></pre>

+ step4 在app/src/main/asserts文件夹创建一个xposed_init文件将入口文件完整名写入,每一行执行一个入口 类
```
 com.example.xposedmoudle.HookLoadPackage
```
继承了IXposedHookLoadPackage接口，重写了handleLoadPackage方法，
判断app包名，如果是的，XposedHelpers.findAndHookMethod()，
hook掉onCreate()方法，XC_MethodHook()重写afterHookedMethod，
当onCreate()执行后会回调这个方法，在这里获得TextView对象，
把文字修改”，接着运行，安装后需要重启设备。




[资料](https://blog.csdn.net/coder_pig/article/details/8003128工程5#t2)
[地址](https://github.com/longyuan02/XPosedModule.git)