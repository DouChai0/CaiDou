package com.ldq.connect.MainWorker.WidgetHook;

import android.app.AlertDialog;

import com.ldq.HookHelper.DexTable.MethodTable;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;

import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_For_Main_Slide_Side {
    public static boolean NotSupport = false;
    static ArrayList<String> ChcheData = null;
    public static void HookForGetSetData() {
        try{
            Class clz = MClass.loadClass("com.tencent.mobileqq.activity.qqsettingme.config.QQSettingMeMenuConfigBean");
            Method mFound = null;
            for(Method m : clz.getDeclaredMethods())
            {
                    if(m.getReturnType().isArray() && m.getParameterCount()==0) {
                        mFound = m;
                    }
            }
            XposedBridge.hookMethod(mFound, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    try{
                        Object ArrayData = param.getResult();
                        //获取信息
                        ArrayList<String> sourceInfo = new ArrayList<>();
                        for(int i=0;i<Array.getLength(ArrayData);i++)
                        {
                            Object ItemData = Array.get(ArrayData,i);

                            Object Title = MField.GetFirstField(ItemData,ItemData.getClass(),MClass.loadClass("com.tencent.mobileqq.activity.qqsettingme.config.QQSettingMeBizBean$Title"));
                            String Signer = ""+MField.GetField(Title,"a",String.class);
                            Signer = Signer + "("+MField.GetField(ItemData,"a",String.class)+")";
                            sourceInfo.add(Signer);
                        }
                        ChcheData=sourceInfo;


                        //清理项目
                        int length = Array.getLength(ArrayData);
                        List<String> Cleaner = MConfig.Get_List("Main","QQCleaner","侧滑清理");
                        for(int i=0;i<Array.getLength(ArrayData);i++) {
                            Object ItemData = Array.get(ArrayData,i);
                            Object Title = MField.GetFirstField(ItemData,ItemData.getClass(),MClass.loadClass("com.tencent.mobileqq.activity.qqsettingme.config.QQSettingMeBizBean$Title"));
                            String Signer = ""+MField.GetField(Title,"a",String.class);
                            Signer = Signer + "("+MField.GetField(ItemData,"a",String.class)+")";
                            if(Cleaner.contains(Signer))
                            {
                                length--;
                                Array.set(ArrayData,i,null);
                            }
                        }
                        Object sResult = Array.newInstance(MClass.loadClass("com.tencent.mobileqq.activity.qqsettingme.config.QQSettingMeBizBean"),length);
                        int Poison = 0;
                        for(int i=0;i<Array.getLength(ArrayData);i++)
                        {
                            Object ItemData = Array.get(ArrayData,i);
                            if(ItemData!=null)
                            {
                                Array.set(sResult,Poison,ItemData);
                                Poison++;
                            }
                        }
                        param.setResult(sResult);
                    }catch (Exception e)
                    {
                        MLogCat.Print_Error("侧滑加载错误",e);
                    }

                }
            });

            XposedBridge.hookMethod(MethodTable.QQSettingMe_InitVipInfoAd(), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    List<String> Cleaner = MConfig.Get_List("Main","QQCleaner","侧滑清理");
                    if(Cleaner.contains("开通会员(d_vip_identity)"))param.setResult(null);
                }
            });
        }catch (Throwable th)
        {
            NotSupport = true;
        }


    }
    public static void ShowSetDialog()
    {
        List<String> NowSetData = MConfig.Get_List("Main","QQCleaner","侧滑清理");
        if(ChcheData==null) {
            Utils.ShowToast("未获取到侧滑信息,可能是未加载完成或者未适配QQ,请查看error.log是否有侧滑加载错误字样,有的话请反馈到作者");
            return;
        }
        String[] ShowInfo = ChcheData.toArray(new String[0]);


        boolean[] bl = new boolean[ShowInfo.length];
        for(int i=0;i<ShowInfo.length;i++)
        {
            if(NowSetData.contains(ShowInfo[i]))bl[i]=true;
        }
        new AlertDialog.Builder(Utils.GetThreadActivity(),3)
                .setMultiChoiceItems(ShowInfo, bl, (dialog, which, isChecked) -> {

                }).setNeutralButton("保存", (dialog, which) -> {
            ArrayList<String> NewSet = new ArrayList<>();
            for(int i=0;i<bl.length;i++)
            {
                if(bl[i])
                {
                    NewSet.add(ShowInfo[i]);
                }
            }
            MConfig.Put_List("Main","QQCleaner","侧滑清理",NewSet);
        }).show();
    }

}
