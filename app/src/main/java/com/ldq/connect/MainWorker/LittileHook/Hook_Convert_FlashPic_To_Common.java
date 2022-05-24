package com.ldq.connect.MainWorker.LittileHook;

import android.text.TextUtils;
import android.util.Log;
import android.view.View;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseInfo;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Convert_FlashPic_To_Common {
    public static void Start()
    {
        try{
            Method CheckFlashPicStatus = MMethod.FindMethod("com.tencent.mobileqq.app.FlashPicHelper","a",boolean.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.MessageRecord")
            });
            XposedBridge.hookMethod(CheckFlashPicStatus, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    boolean result = (boolean) param.getResult();
                    if(MConfig.Get_Boolean("Main","MainSwitch","闪照破解",false))
                    {
                        if(result)
                        {
                            Object MessageRecord = param.args[0];
                            String UserUin = MField.GetField(MessageRecord, MessageRecord.getClass(),"senderuin", String.class);
                            if(!UserUin.equals(BaseInfo.GetCurrentUin()))param.setResult(false);
                            MMethod.CallMethod(MessageRecord,"saveExtInfoToExtStr",void.class,new Class[]{
                                    String.class,String.class
                            },"flashpicflag","1");
                        }
                    }
                }
            });

            XposedHelpers.findAndHookMethod("com.tencent.mobileqq.activity.aio.item.typesupplier.PicTypeSupplier", MHookEnvironment.mLoader, "get",
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                    new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            if(MConfig.Get_Boolean("Main","MainSwitch","闪照破解",false))
                            {
                                int re = (int) param.getResult();
                                if(re==66)
                                {
                                    Object MessageRecord = param.args[1];
                                    MMethod.CallMethod(MessageRecord,"saveExtInfoToExtStr",void.class,new Class[]{
                                            String.class,String.class
                                    },"flashpicflag","1");
                                    param.setResult(1);
                                }

                            }


                        }
                    }
            );


        }catch (Throwable th)
        {
            MLogCat.Print_Error("FlashPic", Log.getStackTraceString(th));
        }
    }
    public static void _caller(View BaseItem,Object ChatMsg)
    {
        try{
            if(MConfig.Get_Boolean("Main","MainSwitch","闪照破解",false))
            {
                if(ChatMsg.getClass().getName().contains("MessageForPic"))
                {
                    String Extstr = MField.GetField(ChatMsg,"extStr",String.class);
                    if(Extstr!=null && Extstr.contains("flashpicflag"))
                    {
                        String Flag = MMethod.CallMethod(ChatMsg,"getExtInfoFromExtStr",String.class,new Class[]{String.class},
                                "flashpicflag"
                        );
                        if(!TextUtils.isEmpty(Flag))
                        {
                            if(Flag.equals("1"))
                            {
                                MMethod.CallMethod(BaseItem,"setTailMessage",void.class,new Class[]{boolean.class,CharSequence.class, MClass.loadClass("android.view.View$OnClickListener")},true,"闪照 ",null);
                            }
                        }
                    }
                }

            }
        }catch (Throwable th)
        {
            MLogCat.Print_Error("FlashPic",Log.getStackTraceString(th));
        }

    }

}
