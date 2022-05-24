package com.ldq.connect.MainWorker.ProxyHook;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.JavaPlugin.JavaPlugin;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MTool.AutoResendMessage;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Avoid_Some_Message;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Count_Input_Data;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Expand_StickPic;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Hide_Font_And_Bubble;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Hide_Tuya;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Replace_Status_Sender;
import com.ldq.connect.MainWorker.QQManagerHelper.TroopManager_Handler_Mute_Continue;
import com.ldq.connect.MainWorker.QQManagerHelper.TroopManager_Handler_Repeat_Mute;
import com.ldq.connect.MainWorker.QQManagerHelper.TroopManager_Handler_Simple;
import com.ldq.connect.MainWorker.QQManagerHelper.TroopManager_Set_DenyWord;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_For_Troop_Msg_Proxy extends XC_MethodHook {
    public static void Start()
    {
        try{
            Method hookMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.data.TroopAndDiscMsgProxy","a", void.class,new Class[]{
                    String.class,
                    int.class,
                    MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                    boolean.class
            });
            XposedBridge.hookMethod(hookMethod,new Hook_For_Troop_Msg_Proxy());
        }catch (Throwable th)
        {
            MLogCat.Print_Error("GetMessage",th);

        }

    }
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        Object ChatMsg = param.args[2];

        try{
            Handler_Count_Input_Data.Count_ChatMessage(ChatMsg);

            TroopManager_Handler_Repeat_Mute._caller(ChatMsg);

            TroopManager_Handler_Mute_Continue.CheckIsLocked(ChatMsg);

            Handler_Hide_Font_And_Bubble._caller(ChatMsg);

            Handler_Avoid_Some_Message._Handle_ShakeWindow(ChatMsg);

            Handler_Avoid_Some_Message._Handle_VoiceTime(ChatMsg);

            JavaPlugin.TaskCount++;
            JavaPlugin.AllTaskCount++;
            JavaPlugin.mTask.PostTask(()->JavaPlugin.BshOnMsg(ChatMsg));

            MHookEnvironment.mTask.PostTask(()-> TroopManager_Set_DenyWord.MessageChecker(ChatMsg));


            TroopManager_Handler_Simple._caller(ChatMsg);

            Handler_Hide_Tuya._caller(ChatMsg);

            Handler_Expand_StickPic.Caller_Handler(ChatMsg);

            Handler_Avoid_Some_Message._Handle_PIC(ChatMsg);

            Handler_Replace_Status_Sender.caller(ChatMsg);

            MHookEnvironment.mTask.PostTask(()-> AutoResendMessage.onMessage(ChatMsg));

            //Test(ChatMsg);

        }catch (Throwable th)
        {
            MLogCat.Print_Error("TroopMsgDispatch",th);
        }

    }
    public static void Test(Object ChatMsg) throws Exception {
        if(ChatMsg.getClass().getName().contains("MessageForUniteGrayTip"))
        {
            String s = MField.GetField(ChatMsg,"extStr",String.class);
            MLogCat.Print_Debug(s);
        }
    }
}
