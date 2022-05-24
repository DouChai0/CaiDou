package com.ldq.connect.MainWorker.LittileHook;

import android.content.Context;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.connect.HookConfig.MConfig;

import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Remove_QZone_Ad {
    static AtomicBoolean IsLoad = new AtomicBoolean();
    static AtomicBoolean IsLoadQWallet = new AtomicBoolean();
    public static void Start() throws ClassNotFoundException {

        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.pluginsdk.PluginStatic"), "getOrCreateClassLoaderByPath",
                Context.class, String.class, String.class, boolean.class, new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        String sName = (String) param.args[1];
                        if(sName.equals("qzone_plugin.apk") && !IsLoad.getAndSet(true)) {
                            ClassLoader pluginLoader = (ClassLoader) param.getResult();
                            if(pluginLoader!=null){
                                try{
                                    XposedHelpers.findAndHookMethod("com.qzone.module.feedcomponent.ui.FeedViewBuilder", pluginLoader,
                                            "setFeedViewData",
                                            Context.class,
                                            XposedHelpers.findClass("com.qzone.proxy.feedcomponent.ui.AbsFeedView",pluginLoader),
                                            XposedHelpers.findClass("com.qzone.proxy.feedcomponent.model.BusinessFeedData",pluginLoader),
                                            boolean.class,boolean.class,
                                            new XC_MethodHook() {
                                                @Override
                                                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                                    super.beforeHookedMethod(param);
                                                    if(MConfig.Get_Boolean("Main","MainSwitch","屏蔽热播",false)){
                                                        if((int)MField.GetField(param.args[2],"isAdFeeds")==1)param.setResult(null);
                                                        Object Child = MField.GetField(param.args[2],"cellOperationInfo");

                                                        Map<Integer,String> map = MField.GetField(Child,"busiParam");
                                                        if(map.containsKey(194))param.setResult(null);
                                                    }
                                                }
                                            }
                                    );
                                }catch (Throwable th){
                                    MLogCat.Print_Error("PLuginHooker",th);
                                }

                            }
                        }else if (sName.equals("qwallet_plugin.apk") && !IsLoadQWallet.getAndSet(true)){
                            /*
                            ClassLoader loader = (ClassLoader) param.getResult();
                            if (loader != null){
                                XposedHelpers.findAndHookMethod("com.tenpay.sdk.activity.GrapHbActivity", loader, "sendGrapHbRequest",
                                        String.class,boolean.class,String.class, new XC_MethodHook() {
                                            @Override
                                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                                super.afterHookedMethod(param);
                                                MLogCat.Print_Debug("PL------------START---------------");

                                                //DebugUtils.PrintAllField_Count(param.thisObject,2);
                                                MLogCat.Print_Debug("PL------------END---------------");

                                            }
                                        }
                                );
                                XposedHelpers.findAndHookMethod("com.tenpay.sdk.paynet.NetHelper", loader, "with",
                                        Context.class,String.class,Map.class,String.class, new XC_MethodHook() {
                                            @Override
                                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                                super.beforeHookedMethod(param);
                                                MLogCat.Print_Debug("PN------------START---------------");
                                                MLogCat.Print_Debug(Log.getStackTraceString(new Throwable()));
                                                MLogCat.Print_Debug(param.args[0]);
                                                MLogCat.Print_Debug(param.args[1]);
                                                MLogCat.Print_Debug(param.args[2]);
                                                MLogCat.Print_Debug(param.args[3]);

                                                MLogCat.Print_Debug("PN------------END---------------");

                                            }
                                        }
                                );

                                XposedHelpers.findAndHookMethod("com.tenpay.sdk.activity.TenpayUtilActivity", loader, "saveDefaultSkey",
                                        long.class,String.class,int.class, new XC_MethodHook() {
                                            @Override
                                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                                super.beforeHookedMethod(param);
                                                MLogCat.Print_Debug("PSAVE------------START---------------");
                                                MLogCat.Print_Debug(Log.getStackTraceString(new Throwable()));
                                                MLogCat.Print_Debug(param.args[0]);
                                                MLogCat.Print_Debug(param.args[1]);
                                                MLogCat.Print_Debug(param.args[2]);

                                                MLogCat.Print_Debug("PSAVE------------END---------------");

                                            }
                                        }
                                );

                            }

                             */
                        }
                    }
                }
        );

    }
}
