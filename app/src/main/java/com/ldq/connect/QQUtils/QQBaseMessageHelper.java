package com.ldq.connect.QQUtils;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;

import com.ldq.HookHelper.DexTable.ClassTable;
import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.HookHelper.DexTable.MethodTable;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.connect.MHookEnvironment;

import java.util.ArrayList;
import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class QQBaseMessageHelper {
    public abstract static class RecallCallback
    {
        public UinClass[] SelectResult;
        public String ExtraData;
        public abstract void OnCallback();
    }
    public static class UinClass
    {
        public int SelectType;
        public String SelectUin;
        public String SelectGroupUin;
    }
    static HashMap<String,RecallCallback> CallBackMap = new HashMap<>();
    static {
        try{
            XposedBridge.hookMethod(MethodTable.ForwardBaseOption_ShowConfirmDialog(), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Bundle bundle = (Bundle) FieldTable.ForwardBaseOption_SelectActivityBundle().get(param.thisObject);
                    //DebugUtils.PrintBundle(bundle);
                    if(!bundle.getString("callback","").isEmpty())
                    {
                        param.setResult(null);

                        RecallCallback cback = CallBackMap.get(bundle.getString("callback"));
                        ArrayList<UinClass> UList = new ArrayList<>();
                        ArrayList ResultList = bundle.getParcelableArrayList("forward_multi_target");
                        /*
                        String s = "";
                        Set<String> set = bundle.keySet();
                        for (String key :set){
                            s=s + key+":"+bundle.get(key)+ "\n";
                        }
                        Utils.ShowToast(""+s);

                         */
                        if(ResultList!=null)
                        {
                            for(Object Result : ResultList)
                            {
                                UinClass u = new UinClass();
                                u.SelectType = MField.GetField(Result,"uinType",int.class);
                                u.SelectUin = MField.GetField(Result,"uin",String.class);
                                u.SelectGroupUin = MField.GetField(Result,"groupUin",String.class);

                                if(u.SelectType==1)u.SelectUin="";

                                if(u.SelectType==1 && TextUtils.isEmpty(u.SelectGroupUin)){
                                    u.SelectGroupUin = MField.GetField(Result,"uin",String.class);
                                }
                                UList.add(u);
                            }

                        }else
                        {
                            UinClass u = new UinClass();
                            u.SelectType = bundle.getInt("uintype");
                            if(u.SelectType==1){
                                u.SelectGroupUin = bundle.getString("uin");
                            }else{
                                u.SelectUin = bundle.getString("uin");
                                u.SelectGroupUin = bundle.getString("troop_uin");
                            }


                            if(u.SelectType==1)u.SelectUin = "";

                            if(u.SelectType==1 && TextUtils.isEmpty(u.SelectGroupUin))u.SelectGroupUin = bundle.getString("troop_uin");
                            UList.add(u);
                        }

                        cback.SelectResult = UList.toArray(new UinClass[0]);
                        cback.OnCallback();

                        CallBackMap.remove(bundle.getString("callback"));
                        Activity mAct = (Activity) FieldTable.ForwardBaseOption_SelectActivity().get(param.thisObject);
                        mAct.finish();
                    }
                }
            });
        }catch (Throwable th)
        {
            MLogCat.Print_Error("Helper_RecallMessage_Error",th);
        }
    }
    public static void StartRecallMessage(RecallCallback recallCallback,String Title)
    {
        String CallbackKey = Math.random()+"";
        Intent intent = new Intent(MHookEnvironment.MAppContext, ClassTable.ForwardRecentActivity());
        intent.putExtra("selection_mode", 0);
        intent.putExtra("direct_send_if_dataline_forward", false);
        intent.putExtra("forward_text", Title);
        intent.putExtra("forward_type", -1);
        intent.putExtra("caller_name", "ChatActivity");
        intent.putExtra("k_smartdevice", false);
        intent.putExtra("k_dataline", false);
        intent.putExtra("k_forward_title", Title);
        intent.putExtra("callback",CallbackKey);
        CallBackMap.put(CallbackKey,recallCallback);

        MHookEnvironment.MAppContext.startActivity(intent);
    }
}
