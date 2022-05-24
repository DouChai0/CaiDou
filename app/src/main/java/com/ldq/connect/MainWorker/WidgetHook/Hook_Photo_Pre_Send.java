package com.ldq.connect.MainWorker.WidgetHook;

import android.content.Context;
import android.os.Bundle;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MMethod;
import com.ldq.connect.FloatWindow.Init;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MainWorker.BaseWorker.EnvHook.BasePieHook;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Send_Mixed_Pic;
import com.ldq.connect.QQUtils.QQSessionUtils;

import java.lang.reflect.Method;
import java.util.List;
import java.util.Locale;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Photo_Pre_Send {
    public static void Start() throws Exception {
        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.activity.aio.photo.PhotoListPanel", MHookEnvironment.mLoader,
                "a", MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie"),
                List.class, boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(QQSessionUtils.GetSessionID() ==1 || QQSessionUtils.GetSessionID() ==0)
                        {
                            List<String> l = (List) param.args[1];

                            if (Handler_Send_Mixed_Pic.IsMix){
                                for(String str : l)
                                {
                                    if (str.toLowerCase(Locale.ROOT).endsWith(".mp4"))continue;
                                    Handler_Send_Mixed_Pic.AddList(str);
                                }
                                param.setResult(true);
                                return;
                            }
                            if(MConfig.Get_Boolean("Main","MainSwitch","图文消息模式",false))
                            {
                                for(String str : l)
                                {
                                    if (str.toLowerCase(Locale.ROOT).endsWith(".mp4"))continue;
                                    BasePieHook.AddEditText("[PicUrl="+str+"]");
                                    param.setResult(true);
                                    MMethod.CallMethod(param.thisObject,"a",void.class,new Class[]{boolean.class},true);
                                }


                            }else if(MConfig.Get_Boolean("Main","MainSwitch","回复带图",false))
                            {
                                if(BasePieHook.IsNowReplying())
                                {
                                    for(String str : l)
                                    {
                                        if (str.toLowerCase(Locale.ROOT).endsWith(".mp4"))continue;
                                        BasePieHook.AddEditText("[PicUrl="+str+"]");
                                        param.setResult(true);
                                        MMethod.CallMethod(param.thisObject,"a",void.class,new Class[]{boolean.class},true);
                                    }


                                }
                            }
                        }

                    }
                });

        Method m = MMethod.FindMethod("com.tencent.mobileqq.emoticonview.sender.CustomEmotionSenderUtil","sendCustomEmotion",void.class,new Class[]{
                MClass.loadClass("com.tencent.common.app.business.BaseQQAppInterface"),
                Context.class,
                MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"),
                String.class,
                boolean.class,
                boolean.class,
                boolean.class,
                String.class,
                MClass.loadClass("com.tencent.mobileqq.emoticon.StickerInfo"),
                String.class,
                Bundle.class
        });

        XposedBridge.hookMethod(m,new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(QQSessionUtils.GetSessionID() ==1 || QQSessionUtils.GetSessionID() ==0)
                        {
                            String Path = (String) param.args[3];
                            if (Handler_Send_Mixed_Pic.IsMix){
                                Handler_Send_Mixed_Pic.AddList(Path);
                                param.setResult(true);
                                return;
                            }

                            if(MConfig.Get_Boolean("Main","MainSwitch","图文消息模式",false))
                            {
                                BasePieHook.AddEditText("[PicUrl="+Path+"]");
                                param.setResult(true);
                            }else if(MConfig.Get_Boolean("Main","MainSwitch","回复带图",false))
                            {
                                if(BasePieHook.IsNowReplying())
                                {
                                    BasePieHook.AddEditText("[PicUrl="+Path+"]");
                                    param.setResult(true);
                                }
                            }
                        }

                    }
                }
        );
    }
}
