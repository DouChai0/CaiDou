package com.ldq.connect.MainWorker.LittileHook;

import android.app.AlertDialog;
import android.content.Context;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_Set_Random_Dice {
    static int CurrentRamdonDict = -1;
    public static void Start()
    {
        //选择发送Dialog Create
        try{
            XposedHelpers.findAndHookMethod("com.tencent.mobileqq.emoticonview.sender.PicEmoticonInfoSender", MHookEnvironment.mLoader, "sendMagicEmoticon",
                    MClass.loadClass("com.tencent.common.app.business.BaseQQAppInterface"),
                    Context.class,
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"),
                    MClass.loadClass("com.tencent.mobileqq.data.Emoticon"),
                    MClass.loadClass("com.tencent.mobileqq.emoticon.StickerInfo")
                    , new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if(MConfig.Get_Boolean("Main","MainSwitch","自定义骰子猜拳",false))
                            {
                                String Name = MField.GetField(param.args[3],"name");

                                if(Name.contains("骰子") && CurrentRamdonDict==-1)
                                {
                                    param.setResult(null);
                                    ShowTouziSelectDialog(param);
                                }

                                if(Name.equals("猜拳") && CurrentRamdonDict==-1)
                                {
                                    param.setResult(null);
                                    ShowCaiquanSelectDialog(param);
                                }
                            }
                        }
                    }
            );

            Method Hook2 = MMethod.FindMethod("com.tencent.mobileqq.magicface.drawable.PngFrameUtil","a",int.class,new Class[]{int.class});
            XposedBridge.hookMethod(Hook2, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    if(CurrentRamdonDict==-1)return;
                    if(CurrentRamdonDict==666)
                    {
                        CurrentRamdonDict=-1;
                        return;
                    }
                    int MaxValue = (int) param.args[0];
                    if(MaxValue==6 || MaxValue ==3)
                    {
                        param.setResult(CurrentRamdonDict);
                        CurrentRamdonDict = -1;
                    }
                }
            });

            XposedHelpers.findAndHookMethod("com.tencent.mobileqq.emoticon.api.impl.EmojiManagerServiceImpl", MHookEnvironment.mLoader, "getMagicFaceSendAccessControl", new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    param.setResult(true);
                }
            });
        }catch (Exception e)
        {
            MLogCat.Print_Error("RandomDictInit",e);
        }

    }
    public static void ShowCaiquanSelectDialog(XC_MethodHook.MethodHookParam params)
    {
        final String[] SelectItem = new String[]{"石头","剪刀","布"};

        new AlertDialog.Builder(Utils.GetThreadActivity(),3)
                .setTitle("设置猜拳的内容")
                .setItems(SelectItem, (dialog, which) -> {
                    CurrentRamdonDict = which;
                    ReInvokeMethod(params);
                }).setNegativeButton("随机", (dialog, which) -> {
                     CurrentRamdonDict=666;
                     ReInvokeMethod(params);
                }).show();
    }
    public static void ReInvokeMethod(XC_MethodHook.MethodHookParam params)
    {
        try {
            XposedBridge.invokeOriginalMethod(params.method,params.thisObject,params.args);
        } catch (Exception e) {
            MLogCat.Print_Error("DictReinvokeError",e);
        }
    }
    public static void ShowTouziSelectDialog(XC_MethodHook.MethodHookParam params)
    {
        final String[] SelectItem = new String[]{"1","2","3","4","5","6"};
        new AlertDialog.Builder(Utils.GetThreadActivity(),3)
                .setTitle("设置骰子的点数")
                .setItems(SelectItem, (dialog, which) -> {
                    CurrentRamdonDict = which;
                    ReInvokeMethod(params);
                }).setNegativeButton("随机", (dialog, which) -> {
            CurrentRamdonDict=666;
            ReInvokeMethod(params);
        }).show();

    }
}
