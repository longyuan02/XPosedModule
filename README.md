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

```<manifest xmlns:android="http://schemas.android.com/apk/res/android"
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
</manifest>```

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
 com.example.xposedmoudle.xprosed.HookLoadPackage
```
继承了IXposedHookLoadPackage接口，重写了handleLoadPackage方法，
判断app包名，如果是的，XposedHelpers.findAndHookMethod()，
hook掉onCreate()方法，XC_MethodHook()重写afterHookedMethod，
当onCreate()执行后会回调这个方法，在这里获得TextView对象，
把文字修改”，接着运行，安装后需要重启设备。

### 问题汇总
```
Class ref in pre-verified class resolved to unexpected implementation
报错场景：插件开发中，先在插件中引用某jar包后，将插件放入宿主运行，结果报此错；
原因分析：宿主与插件引用了相同的jar包，造成重复引用。去掉后问题解决！

# part2
## 核心类
[官方文档链接]:https://api.xposed.info/reference/de/robv/android/xposed/XposedHelpers.html
上一篇说到如何集成Xposed框架,接下来就是要实现相关功能,初步了解基本使用

+ 核心接口:
    IXposedHookInitPackageResources-->应用加载完成初始化资源时调用
    IXposedHookLoadPackage  -->加载应用时调用
    IXposedHookZygoteInit  --> 系统启动是加载

    XposedHelpers -->构造帮助类,辅助<IXposedHookLoadPackage>通过反射获得实体类,方法,属性并对其进行操作

+ demo场景:
为方便测试,本文将操作页面集成在一个demo中包名:"com.example.xposedmoudle",目标对象"MainActivity"
```
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
                            //通过hook对象拿到目标对象实体
                            Class c = loadPackageParam.classLoader.loadClass("com.example.xposedmoudle.MainActivity");
                            //通过反射获取textview属性
                            Field fild = c.getDeclaredField("tv");
                            //允许访问私有属性
                            fild.setAccessible(true);
                            //得到私有属性textview
                            TextView tv = (TextView) fild.get(param.thisObject);
                            tv.setText("测试");
                        }
                    });
        }
    }
}

```
+ IXposedHookInitPackageResources
```
实现 IXposedHookInitPackageResources接口
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
```
+ IXposedHookZygoteInit
```
@Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        //获取加载apk
        MODULE_PATH = startupParam.modulePath;
    }
```


+ 实现对activity生命周期的拦截
通过源码追动可以找到ActivityThread 持有Instrumetation实力,他哦难过Instrumentation进行activity生命周期的调用,思路是自定义一个Instrumentation类,替换掉系统的持有.
```
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
            mInstumentationField.set(sCurrentActivityThrad, this);
        } catch (Exception e) {
            Log.e(TAG, "======" + e.getMessage().toString());
        }
    }
    //重写注意super.callActivityOnPause(activity);不能删掉 可以理解为目标Activity的onPause方法
    //按照代码块执行顺序执行-->限制性log 在执行Activity的onPause
    @Override
    public void callActivityOnPause(Activity activity) {
        Log.e("======", "hook--->");
        super.callActivityOnPause(activity);
    }
}

```
在使用的位置new 出对象,传入上下文
框架还在进一步探索中,欢迎大家交流指正
## 核心类
[官方文档链接]:https://api.xposed.info/reference/de/robv/android/xposed/XposedHelpers.html
上一篇说到如何集成Xposed框架,接下来就是要实现相关功能,初步了解基本使用

+ 核心接口:
    IXposedHookInitPackageResources-->应用加载完成初始化资源时调用
    IXposedHookLoadPackage  -->加载应用时调用
    IXposedHookZygoteInit  --> 系统启动是加载

    XposedHelpers -->构造帮助类,辅助<IXposedHookLoadPackage>通过反射获得实体类,方法,属性并对其进行操作

+ demo场景:
为方便测试,本文将操作页面集成在一个demo中包名:"com.example.xposedmoudle",目标对象"MainActivity"
```
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
                            //通过hook对象拿到目标对象实体
                            Class c = loadPackageParam.classLoader.loadClass("com.example.xposedmoudle.MainActivity");
                            //通过反射获取textview属性
                            Field fild = c.getDeclaredField("tv");
                            //允许访问私有属性
                            fild.setAccessible(true);
                            //得到私有属性textview
                            TextView tv = (TextView) fild.get(param.thisObject);
                            tv.setText("测试");
                        }
                    });
        }
    }
}

```
+ IXposedHookInitPackageResources
```
实现 IXposedHookInitPackageResources接口
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
```
+ IXposedHookZygoteInit
```
@Override
    public void initZygote(StartupParam startupParam) throws Throwable {
        //获取加载apk
        MODULE_PATH = startupParam.modulePath;
    }
```


+ 实现对activity生命周期的拦截
通过源码追动可以找到ActivityThread 持有Instrumetation实力,他哦难过Instrumentation进行activity生命周期的调用,思路是自定义一个Instrumentation类,替换掉系统的持有.
```
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
            mInstumentationField.set(sCurrentActivityThrad, this);
        } catch (Exception e) {
            Log.e(TAG, "======" + e.getMessage().toString());
        }
    }
    //重写注意super.callActivityOnPause(activity);不能删掉 可以理解为目标Activity的onPause方法
    //按照代码块执行顺序执行-->限制性log 在执行Activity的onPause
    @Override
    public void callActivityOnPause(Activity activity) {
        Log.e("======", "hook--->");
        super.callActivityOnPause(activity);
    }
}

```
工程实现类:MyInstrumetation

在使用的位置new 出对象,传入上下文
框架还在进一步探索中,欢迎大家交流指正




## 三星S9手机root    
密码：1qaZ2wsX   
[samsung](https://www.114shouji.com/show-17650-1-1.html):https://www.114shouji.com/show-17650-1-1.html   



[资料](https://blog.csdn.net/coder_pig/article/details/8003128工程5#t2)
[地址](https://github.com/longyuan02/XPosedModule.git)
[三星]http://www.romleyuan.com/lec/read?id=153