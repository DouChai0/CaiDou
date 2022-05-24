package com.ldq.connect.QQUtils;

import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;

import com.ldq.HookHelper.DexTable.MethodTable;
import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.MHookEnvironment;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class QQMessage_Transform {
    public static void Message_Send_Text(String GroupUin, String UserUin, String Message_Content, ArrayList mAtList)
    {
        try {
            Object _SessionInfo = QQMessage_Builder.Build_SessionInfo(GroupUin,UserUin);
            QQMessage.Message_Send_Text(_SessionInfo,Message_Content,mAtList);
        } catch (Exception e) {
            MLogCat.Print_Error("SendMsgError", Log.getStackTraceString(e));
        }
    }
    public static void Message_Send_Pic(String GroupUin,String UserUin, String Path) {
        try{
            Object _SessionInfo = QQMessage_Builder.Build_SessionInfo(GroupUin,UserUin);
            Object PICMsgRecord = QQMessage_Builder.Build_Pic(_SessionInfo,Path);
            QQMessage.Message_Send_Pic(_SessionInfo,PICMsgRecord);
        }
        catch (Exception ex)
        {
            MLogCat.Print_Error("SendPICError",ex);
        }
    }
    public static void Group_Send_Ptt(String GroupUin,String UserUin,String PttPath) {
        if(!PttPath.contains("com.tencent.mobileqq"))
        {
            File m = new File(PttPath);
            String CopyTo = Environment.getExternalStorageDirectory() +  "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/"+BaseInfo.GetCurrentUin()+"/ptt/"+m.getName();
            FileUtils.copy(PttPath,CopyTo,4096);
            PttPath = CopyTo;
        }
        Object Session = QQMessage_Builder.Build_SessionInfo(GroupUin,UserUin);
        QQMessage.Group_Send_Ptt(Session,PttPath);
    }
    public static void Message_Send_Mix(String GroupUin, String UserUin, List Message) throws Exception {
        if(GroupUin.contains("&")){
            String[] Cut = GroupUin.split("&");

            Method InvokeMethod = MethodTable.MessageRecordFactory_BuildMixedMsg();
            Object MixMessageRecord = InvokeMethod.invoke(null,MHookEnvironment.AppInterface,Cut[1],BaseInfo.GetCurrentUin(),10014);
            MField.SetField(MixMessageRecord,"msgElemList",Message);
            MixMessageRecord = MMethod.CallMethod(MixMessageRecord,"rebuildMixedMsg",MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[0]);
            Object _Session = QQMessage_Builder.Build_SessionInfo(GroupUin,UserUin);
            QQMessage.Message_Send_Mix(_Session,MixMessageRecord);
        }else
        {
            Method InvokeMethod = MethodTable.MessageRecordFactory_BuildMixedMsg();
            Object MixMessageRecord = InvokeMethod.invoke(null,MHookEnvironment.AppInterface,GroupUin,BaseInfo.GetCurrentUin(),1);
            MField.SetField(MixMessageRecord,"msgElemList",Message);
            MixMessageRecord = MMethod.CallMethod(MixMessageRecord,"rebuildMixedMsg",MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[0]);
            Object _Session = QQMessage_Builder.Build_SessionInfo(GroupUin,UserUin);
            QQMessage.Message_Send_Mix(_Session,MixMessageRecord);
        }

    }
    public static void Message_Add_Mix(String GroupUin, String UserUin, List Message) throws Exception {
        Method InvokeMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a",MClass.loadClass("com.tencent.mobileqq.data.MessageForMixedMsg"),new Class[]{
                MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                String.class,
                String.class,
                int.class
        });
        Object MixMessageRecord = InvokeMethod.invoke(null,MHookEnvironment.AppInterface,GroupUin,BaseInfo.GetCurrentUin(),1);
        MField.SetField(MixMessageRecord,"msgElemList",Message);
        MField.SetField(MixMessageRecord,"frienduin",GroupUin);;
        if(TextUtils.isEmpty(UserUin))
        {
            MField.SetField(MixMessageRecord,"istroop",1);;
        }
        else
        {
            MField.SetField(MixMessageRecord,"frienduin",UserUin);;
        }
        MixMessageRecord = MMethod.CallMethod(MixMessageRecord,"rebuildMixedMsg",MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[0]);
        BaseCall.AddMsg(MixMessageRecord);
    }

}
