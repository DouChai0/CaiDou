package com.ldq.connect.QQUtils;

import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.ldq.HookHelper.DexTable.ClassTable;
import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.Utils.DataUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.MHookEnvironment;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.UUID;

public class QQMessage_Builder
{
    public static Object Build_AbsStructMsg(String msg)
    {
        try{
            Method BuildStructMsg = MMethod.FindMethod("com.tencent.mobileqq.structmsg.TestStructMsg","a",
                    MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"),new Class[]{String.class});
            Object msgData = BuildStructMsg.invoke(null,new Object[]{msg});
            return msgData;
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("Build_Struct", Log.getStackTraceString(th));
            return null;
        }


    }
    public static Object Build_ArkAppMsg(String msg)
    {
        try{
            Method med = MMethod.FindMethod("com.tencent.mobileqq.data.ArkAppMessage","fromAppXml",
                    boolean.class,new Class[]{String.class});
            Constructor cons = MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage").getConstructor();
            Object _ArkAppMsg = cons.newInstance();
            med.invoke(_ArkAppMsg,new Object[]{msg});
            return _ArkAppMsg;
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("Build_Arkapp", Log.getStackTraceString(th));
            return null;
        }
    }
    public static Object Build_SessionInfo(String GroupUin,String UserUin) {
        try{
            if(GroupUin.contains("&")){
                String[] Cut = GroupUin.split("&");
                if(Cut.length>1){
                    return Build_SessionInfo_Guild(Cut[0],Cut[1],!UserUin.isEmpty());
                }
                return null;
            }
            Object mObj = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),new Class[0]);
            if(TextUtils.isEmpty(UserUin))
            {
                FieldTable.SessionInfo_isTroop().set(mObj,1);
                FieldTable.SessionInfo_friendUin().set(mObj,GroupUin);
                FieldTable.SessionInfo_TroopCode().set(mObj,GroupUin);

            }
            else if (TextUtils.isEmpty(GroupUin))
            {
                FieldTable.SessionInfo_isTroop().set(mObj,0);
                FieldTable.SessionInfo_friendUin().set(mObj,UserUin);
            }
            else
            {
                FieldTable.SessionInfo_isTroop().set(mObj,1000);
                FieldTable.SessionInfo_friendUin().set(mObj,UserUin);
                FieldTable.SessionInfo_InTroopUin().set(mObj,GroupUin);
                FieldTable.SessionInfo_TroopCode().set(mObj,GroupUin);
            }
            return mObj;

        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("ConstructSessionError",Log.getStackTraceString(th));
            return null;
        }
    }
    public static Object Build_SessionInfo_Guild(String ChannelID,String ChannelCharID,boolean IsDirectMsg) {
        try{
            if(IsDirectMsg){
                Object mObj = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),new Class[0]);
                FieldTable.SessionInfo_isTroop().set(mObj,10014);
                FieldTable.SessionInfo_ChannelID().set(mObj,ChannelID);
                FieldTable.SessionInfo_ChannelCharID().set(mObj,ChannelCharID);
                FieldTable.SessionInfo_CodeName().set(mObj,ChannelCharID);
                Bundle bundle = MField.GetField(mObj,"J",Bundle.class);
                bundle.putInt("guild_direct_message_flag",1);
                return mObj;
            }else {
                Object mObj = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),new Class[0]);
                FieldTable.SessionInfo_isTroop().set(mObj,10014);
                FieldTable.SessionInfo_ChannelID().set(mObj,ChannelID);
                FieldTable.SessionInfo_ChannelCharID().set(mObj,ChannelCharID);
                return mObj;
            }
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("ConstructSessionError",Log.getStackTraceString(th));
            return null;
        }
    }
    public static Object CopyReplyMessage(Object source)
    {
        try{
            Method InvokeMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a",MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText"),new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    String.class,
                    int.class,
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"),
                    String.class
            });
            String sFriend = MField.GetField(source,"frienduin",String.class);
            Object sourceMsg = MField.GetField(source,"mSourceMsgInfo",MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"));
            return InvokeMethod.invoke(null,MHookEnvironment.AppInterface,sFriend,2,sourceMsg,"");
        }catch (Throwable th)
        {
            MLogCat.Print_Error("BuildReplyTextError",Log.getStackTraceString(th));
            return null;
        }

    }
    public static Object Build_Pic(Object SessionInfo,String PicPath)
    {
        try{
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    String.class
            });
            Object PICMsg = CallMethod.invoke(null,
                    MHookEnvironment.AppInterface,SessionInfo,PicPath
            );


            MField.SetField(PICMsg,"md5", DataUtils.getFileMD5(new File(PicPath)));
            MField.SetField(PICMsg,"uuid", DataUtils.getFileMD5(new File(PicPath))+".jpg");
            MField.SetField(PICMsg,"localUUID", UUID.randomUUID().toString());
            MMethod.CallMethod(PICMsg,"prewrite",void.class,new Class[0]);
            return PICMsg;
        }
        catch (Exception e)
        {
            MLogCat.Print_Error("BuildPICMsgError",Log.getStackTraceString(e));
            return null;
        }
    }
    public static Object Build_Text(String GroupUin,String Text)
    {
        try{
            Method InvokeMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a",MClass.loadClass("com.tencent.mobileqq.data.MessageForText"),new Class[]{
                    MClass.loadClass("com.tencent.common.app.AppInterface"),
                    String.class,
                    String.class,
                    String.class,
                    int.class,
                    byte.class,
                    byte.class,
                    short.class,
                    String.class
            });
            Object TextMessageRecord = InvokeMethod.invoke(null,MHookEnvironment.AppInterface,"",GroupUin,BaseInfo.GetCurrentUin(),1,(byte)0,(byte)0,(short)0,Text);
            return TextMessageRecord;
        }
        catch (Exception e)
        {
            MLogCat.Print_Error("BuildTextError",Log.getStackTraceString(e));
            return null;
        }
    }
    public static Object Build_AtInfo(String Useruin,String AtText,short StartPos)
    {
        try {
            Object AtInfoObj = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.data.AtTroopMemberInfo"),new Class[0]);
            if(Useruin.isEmpty())return null;
            if(Useruin.equals("0"))
            {
                MField.SetField(AtInfoObj,"flag",(byte)1);
                MField.SetField(AtInfoObj,"startPos",StartPos);
                MField.SetField(AtInfoObj,"textLen",(short)AtText.length());
            }
            else
            {
                MField.SetField(AtInfoObj,"uin",Long.parseLong(Useruin));
                MField.SetField(AtInfoObj,"startPos",StartPos);
                MField.SetField(AtInfoObj,"textLen",(short)AtText.length());
            }
            return AtInfoObj;
        }catch (Exception e)
        {
            MLogCat.Print_Error("BuildAtInfo",Log.getStackTraceString(e));
            return null;
        }
    }
    public static Object Build_ReplyText(Object SourceMsg,String MessageContent,String GroupUin)
    {
        try{
            int isTroop = MField.GetField(SourceMsg,"istroop",int.class);

            String Uins = MField.GetField(SourceMsg,"senderuin",String.class);
            Object Appinterface = BaseInfo.GetAppInterface();
            Method SourceInfo = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.reply.ReplyMsgUtils","a",MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"),
                    new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                            int.class,long.class,String.class
                    }
            );

            Object SourceInfoObj = SourceInfo.invoke(null,Appinterface,SourceMsg,0,Long.parseLong(Uins), com.ldq.connect.QQUtils.TroopManager.GetTroopName(MField.GetField(SourceMsg,"frienduin",String.class)));


            Method BuildMsg = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a",MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText"),
                    new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            String.class,int.class,
                            MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"),
                            String.class
                    }
            );

            Object Builded = BuildMsg.invoke(null,Appinterface,GroupUin,isTroop,SourceInfoObj,MessageContent);


            return Builded;
        }catch (Exception e)
        {
            MLogCat.Print_Error("ReplyMessageBuilder",e);
            return null;
        }
    }
    public static Object Build_Voice(Object Session,String VoicePath){
        try{
            return MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.activity.ChatActivityFacade"),"a",new Class[]{
                    ClassTable.QQAppinterFace(),String.class,ClassTable.BaseSessionInfo(),int.class,int.class
            },BaseInfo.GetAppInterface(),VoicePath,Session,-3,0);
        }catch (Exception e){
            MLogCat.Print_Error("Build_Voice",e);
        }
        return null;

    }


}
