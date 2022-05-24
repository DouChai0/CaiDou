package com.ldq.connect.MainWorker.WidgetHook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.util.SparseArray;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.QQUtils.QQSessionUtils;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_JiaHao_Panel {
    private static SparseArray sArray = null;
    private static HashMap<String,Object> cachesArray = new HashMap<>();

    private static ArrayList<String> cachesShowedArray;

    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.pluspanel.loader.PlusPanelAppLoader"), "c",
                int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (sArray == null){
                            Object factory = MField.GetFirstField(param.thisObject,MClass.loadClass("com.tencent.mobileqq.activity.aio.pluspanel.loader.PlusPanelAppLoader"),MClass.loadClass("com.tencent.mobileqq.pluspanel.appinfo.AppInfoFactory"));
                            sArray = MField.GetField(factory,"a",SparseArray.class);

                            for(int i=0;i<sArray.size();i++){
                                try{
                                    Object value = sArray.valueAt(i);
                                    String name = MMethod.CallMethod(value,"getTitle",String.class,new Class[0]);
                                    cachesArray.put(name,value);
                                }catch (Exception e){

                                }
                            }
                        }
                    }
                });

        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.pluspanel.loader.PlusPanelAppLoader"), "a", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                if (cachesArray.isEmpty())return;
                List ret = (List) param.getResult();
                cachesShowedArray = new ArrayList<>();
                for(int i=0;i<ret.size();i++){
                    try{
                        Object value = ret.get(i);
                        String name = MMethod.CallMethod(value,"getTitle",String.class,new Class[0]);
                        cachesShowedArray.add(name);
                    }catch (Exception e){
                    }
                }

                List<String> list_hide = MConfig.Get_List("QQHelper","JiaHao","Hide");

                Iterator it = ret.iterator();
                while (it.hasNext()){
                    Object item = it.next();
                    String name = MMethod.CallMethod(item,"getTitle",String.class,new Class[0]);
                    if (list_hide.contains(name))it.remove();
                }

                ArrayList<String> nameList = new ArrayList<>();
                for (Object panelInfo : ret){
                    String name = MMethod.CallMethod(panelInfo,"getTitle",String.class,new Class[0]);
                    nameList.add(name);
                }
                List<String> list_show = MConfig.Get_List("QQHelper","JiaHao","Show");
                for (String name : list_show){
                    if (!nameList.contains(list_show)){
                        Object panel = cachesArray.get(name);
                        MMethod.CallMethod(panel,"init",void.class,new Class[0]);
                        MField.SetField(panel,"appid",sArray.keyAt(sArray.indexOfValue(panel)));
                        MField.SetField(panel,"uinType", QQSessionUtils.GetSessionID());
                        ret.add(panel);
                    }
                }


            }
        });


    }
    public static void StartShow(Context context){
        if (sArray == null){
            Utils.ShowToast("未获取到相关信息,请打开一次加号再试");
            return;
        }
        HashMap<String,Integer> tArr = new HashMap<>();
        for(int i=0;i<sArray.size();i++){
            try{
                int key = sArray.keyAt(i);
                Object value = sArray.valueAt(i);
                String name = MMethod.CallMethod(value,"getTitle",String.class,new Class[0]);
                tArr.put(name,key);
            }catch (Exception e){

            }
        }
        ArrayList<String> key = new ArrayList<>();
        for(String sKey : tArr.keySet()){
            key.add(sKey);
        }

        List<String> list_show = MConfig.Get_List("QQHelper","JiaHao","Show");

        boolean[] bcheck = new boolean[key.size()];
        for(int i=0;i<key.size();i++){
            bcheck[i] = list_show.contains(key.get(i));
        }
        new AlertDialog.Builder(context,3)
                .setTitle("选择需要强制显示的加号菜单项目")
                .setMultiChoiceItems(key.toArray(new String[0]), bcheck, (dialog, which, isChecked) -> {

                }).setNegativeButton("保存", (dialog, which) -> {
            ArrayList<String> newSet = new ArrayList<>();
            for(int i=0;i<bcheck.length;i++){
                if (bcheck[i]){
                    newSet.add(key.get(i));
                }
                MConfig.Put_List("QQHelper","JiaHao","Show",newSet);
            }

                }).show();
    }
    public static void StartShowHide(Context context){
        if (cachesShowedArray == null){
            Utils.ShowToast("未获取到相关信息,请打开一次加号再试");
            return;
        }

        List<String> list_show = MConfig.Get_List("QQHelper","JiaHao","Hide");

        boolean[] bcheck = new boolean[cachesShowedArray.size()];
        for(int i=0;i<cachesShowedArray.size();i++){
            bcheck[i] = list_show.contains(cachesShowedArray.get(i));
        }
        new AlertDialog.Builder(context,3)
                .setTitle("选择需要强制隐藏的加号菜单项目")
                .setMultiChoiceItems(cachesShowedArray.toArray(new String[0]), bcheck, (dialog, which, isChecked) -> {

                }).setNegativeButton("保存", (dialog, which) -> {

            ArrayList<String> newSet = new ArrayList<>();
            for(int i=0;i<bcheck.length;i++){
                if (bcheck[i]){
                    newSet.add(cachesShowedArray.get(i));
                }
                MConfig.Put_List("QQHelper","JiaHao","Hide",newSet);
            }
                }).show();
    }
}
