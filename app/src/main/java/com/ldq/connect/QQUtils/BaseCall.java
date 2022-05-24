package com.ldq.connect.QQUtils;

import android.os.Handler;
import android.os.Looper;

import com.ldq.HookHelper.DexTable.MethodTable;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MainWorker.BaseWorker.EnvHook.BasePieHook;

import java.lang.reflect.Method;

public class BaseCall {
    public static void AddMsg(Object MessageRecord)
    {
        try{
            if(MHookEnvironment.AppInterface==null)return;
            Method InvokeMethod = MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade","a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                    String.class
            });
            Object MessageFacade = MMethod.CallMethod(MHookEnvironment.AppInterface,MHookEnvironment.AppInterface.getClass(),"getMessageFacade",
                    MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"),new Class[0]
            );
            InvokeMethod.invoke(MessageFacade,MessageRecord,BaseInfo.GetCurrentUin());
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("AddMsg",th);
        }

    }
    public static void AddAndSendMsg(Object MessageRecord)
    {
        try {
            Object MessageFacade = MMethod.CallMethod(MHookEnvironment.AppInterface,MHookEnvironment.AppInterface.getClass(),"getMessageFacade",
                    MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"),new Class[0]
            );
            Method mMethod = MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade","a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                    MClass.loadClass("com.tencent.mobileqq.app.BusinessObserver")
            });

            mMethod.invoke(MessageFacade,MessageRecord,null);

        } catch (Exception e) {
            MLogCat.Print_Error("AddAndSendMsg", e);
        }

    }
    public static void DelayRemoveMessage(Object MessageRecord)
    {
        new Handler(Looper.getMainLooper())
                .postDelayed(()->{
                    try {
                        RemoveMessage(MessageRecord);
                    } catch (Exception exception) {
                        MLogCat.Print_Error("DelayMessageRecord",exception);
                    }
                },300);
    }
    public static void RemoveMessage(Object MessageRecord) throws Exception {

        int isTroop = MField.GetField(MessageRecord,"istroop",int.class);
        if (isTroop == 10014){
            QQGuild_Manager.Guild_Revoke(MessageRecord);
            return;
        }

        Object MessageFacade = MMethod.CallMethod(MHookEnvironment.AppInterface,MHookEnvironment.AppInterface.getClass(),"getMessageFacade",
                MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"),new Class[0]
        );
        if(MessageRecord.getClass().toString().contains("MessageForTroopFile"))
        {
            RevokeTroopFile(MessageRecord);
        }

        Object MsgCache = MMethod.CallMethod(MHookEnvironment.AppInterface,MHookEnvironment.AppInterface.getClass(),"getMsgCache",
                MClass.loadClass("com.tencent.mobileqq.service.message.MessageCache"),new Class[0]);

        MMethod.CallMethod(MsgCache,"b",void.class,new Class[]{boolean.class},true);
        MethodTable.MessageFacade_RevokeMessage().invoke(MessageFacade,MessageRecord);

    }
    private static void RevokeTroopFile(Object MessageRecord)
    {
        try{
            Object RevokeHelper = BasePieHook.GetRevokeHelper();
            MMethod.CallMethod(RevokeHelper,"a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForTroopFile")
            },MessageRecord);
        }catch (Exception ex)
        {
            MLogCat.Print_Error("RevokeTroopFile",ex);
        }
    }
    public static void ExitQQAnyWays() {
        try{
            Object Appinterface = BaseInfo.GetAppInterface();
            MField.SetField(Appinterface,"bReceiveMsgOnExit",false);
            MMethod.CallMethod(Appinterface,"exit",void.class,new Class[]{
                    boolean.class
            },false);
        }catch (Exception e)
        {
            MLogCat.Print_Error("ErrorOnExitQQ",e);
        }


    }
}
