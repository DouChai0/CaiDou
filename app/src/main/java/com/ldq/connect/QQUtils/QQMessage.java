package com.ldq.connect.QQUtils;

import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.ldq.HookHelper.DexTable.ClassTable;
import com.ldq.HookHelper.DexTable.MethodTable;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;

import java.io.File;
import java.lang.reflect.Method;
import java.util.ArrayList;

public class QQMessage {
    public static void Message_Send_ArkApp(Object _Session,Object ArkAppInfo)
    {
        try{
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",
                    boolean.class,new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                            MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage")
                    });
            CallMethod.invoke(null, MHookEnvironment.AppInterface,_Session,ArkAppInfo);
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("ArkAppSend", Log.getStackTraceString(th));
        }
    }
    public static void Message_Send_Sticker(int AntiID,Object SessionInfo)
    {
        try{

            if(ClassTable.NowQQVersion < 5865)
            {
                Method m = MMethod.FindMethod("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack","sendAniSticker",
                        boolean.class,new Class[]{int.class,MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo")}
                );
                m.invoke(null,AntiID,SessionInfo);
            }else
            {
                Method m = MMethod.FindMethod("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack","sendAniSticker",
                        boolean.class,new Class[]{int.class,MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"),int.class}
                );
                m.invoke(null,AntiID,SessionInfo,0);
            }

        }catch (Exception es)
        {
            MLogCat.Print_Error("SendStickerEmo",es);
        }
    }

    public static boolean ConvertAndSendAntMsg(String WillSend)
    {
        try{
            if(WillSend.length()>3)return false;
            Object ParseResult;
            if(MConfig.Get_Boolean("Main","MainSwitch","发送小表情",false))
            {
                ParseResult = MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack"),
                        "parseMsgForAniSticker",MClass.loadClass("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack$AniStickerTextParseResult"),
                        new Class[]{String.class,MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo")},
                        "Convert"+WillSend,MHookEnvironment.CurrentSession);
            }else
            {
                ParseResult = MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack"),
                        "parseMsgForAniSticker",MClass.loadClass("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack$AniStickerTextParseResult"),
                        new Class[]{String.class,MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo")},
                        WillSend,MHookEnvironment.CurrentSession);
            }
            if(ParseResult !=null)
            {
                boolean b = MField.GetField(ParseResult,"configAniSticker",boolean.class);
                if(b)
                {
                    int ID = MField.GetField(ParseResult,"emoLocalId",int.class);
                    if(ID>0)
                    {
                            if(ClassTable.NowQQVersion < 5865)
                            {
                                boolean Result = MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack"),
                                        "sendAniSticker",boolean.class,new Class[]{int.class,MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo")},
                                        ID,MHookEnvironment.CurrentSession);
                                return Result;
                            }else {
                                boolean Result = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.emoticonview.AniStickerSendMessageCallBack"),
                                        "sendAniSticker", boolean.class, new Class[]{int.class, MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"), int.class},
                                        ID, MHookEnvironment.CurrentSession, 0);
                                return Result;
                            }
                    }
                }
            }
            return false;
        }catch (Throwable e)
        {
            MLogCat.Print_Error("SendAntMsg",e);
            return false;
        }

    }
    public static void Message_Send_Xml(Object _Session,Object XmlInfo)
    {
        try{
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",
                    void.class,new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                            MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg")
                    });
            CallMethod.invoke(null, MHookEnvironment.AppInterface,_Session,XmlInfo);
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("ArkAppSend", Log.getStackTraceString(th));
        }
    }
    public static String GetArkOrXmlString(Object MessageData)
    {
        try{
            if(MessageData.getClass().getSimpleName().contains("AbsStructMsg"))
            {

            }
            else if(MessageData.getClass().getSimpleName().contains("ArkAppMessage"))
            {

            }
            else
            {
                return "";
            }
        }catch (Throwable th)
        {
            MLogCat.Print_Error("GetCardInfo",Log.getStackTraceString(th));
        }
        return "";
    }
    public static void Message_Send_Text(Object SessionInfo, String Message_Content, ArrayList mAtList)
    {
        try {
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    Context.class,
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    String.class,
                    ArrayList.class
            });
            CallMethod.invoke(null,MHookEnvironment.AppInterface, MHookEnvironment.MAppContext,SessionInfo,Message_Content,mAtList);
        } catch (Exception e) {
            MLogCat.Print_Error("SendMsgError",Log.getStackTraceString(e));
        }
    }
    public static void Message_Send_Pic(Object _SessionInfo,Object PicObject)
    {
        try {
            Method hookMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForPic"),
                    int.class
            });
            hookMethod.invoke(null,
                    MHookEnvironment.AppInterface,_SessionInfo,PicObject,0
            );
        } catch (Exception e) {
            MLogCat.Print_Error("SendPicMsgError",Log.getStackTraceString(e));
        }
    }
    public static void Group_Send_Ptt(Object SessionInfo,String PttPath){
        try{
            Method CallMethod = MethodTable.ChatActivityFacade_SendPttByPath();
            CallMethod.invoke(null,MHookEnvironment.AppInterface, SessionInfo,PttPath);
        }catch (Exception e)
        {
            MLogCat.Print_Error("SendPtt",e);
        }

    }
    public static void Message_Send_ShakeWindow(String GroupUin)  throws Exception {
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a",MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                int.class
        });
        Object MessageRecord =CallMethod.invoke(null,-2020);
        MField.SetField(MessageRecord,MessageRecord.getClass(),"msg","窗口抖动",String.class);
        Object mShakeParam = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.data.ShakeWindowMsg"),new Class[0]);
        MField.SetField(mShakeParam,mShakeParam.getClass(),"mReserve",0);
        MField.SetField(mShakeParam,mShakeParam.getClass(),"mType",0);
        MField.SetField(MessageRecord,MessageRecord.getClass(),"mShakeWindowMsg",mShakeParam);
        MMethod.CallMethod(MessageRecord,MessageRecord.getClass(),"initInner",void.class,
                new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                BaseInfo.GetCurrentUin(),GroupUin,BaseInfo.GetCurrentUin(),"[窗口抖动]",System.currentTimeMillis()/1000,-2020,
                1,System.currentTimeMillis()/1000
        );
        MMethod.CallMethod(MessageRecord,MessageRecord.getClass(),"prewrite",void.class,new Class[0]);
        BaseCall.AddAndSendMsg(MessageRecord);
    }
    public static void AddRevokeTip(String TipText,long Time,long Seq,long msgUid,String Group,String UserUin)
    {
        try{

            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                    int.class
            });

            Object MessageRecord =CallMethod.invoke(null,-2031);
            if(TextUtils.isEmpty(UserUin))
            {
                MMethod.CallMethod(MessageRecord,MessageRecord.getClass(),"init",void.class,
                        new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                        BaseInfo.GetCurrentUin(),Group,BaseInfo.GetCurrentUin(),TipText,Time,-2031,
                        1,Time
                );
            }
            else if(TextUtils.isEmpty(Group))
            {
                MMethod.CallMethod(MessageRecord,MessageRecord.getClass(),"init",void.class,
                        new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                        BaseInfo.GetCurrentUin(),UserUin,BaseInfo.GetCurrentUin(),TipText,Time,-2031,
                        0,Time
                );
            }else{
                MMethod.CallMethod(MessageRecord,MessageRecord.getClass(),"init",void.class,
                        new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                        BaseInfo.GetCurrentUin(),UserUin,Group,TipText,Time,-2031,
                        1000,Time
                );
            }
            MField.SetField(MessageRecord,"msgUid",msgUid);
            MField.SetField(MessageRecord,"shmsgseq",Seq);
            MField.SetField(MessageRecord,"isread",true);
            MField.SetField(MessageRecord,"senderuin","10000");
            BaseCall.AddMsg(MessageRecord);
        }
        catch (Throwable th)
        {

        }

    }
    public static void AddTip(String TipText,long Time,long Seq,long msgUid,String Group,String UserUin)
    {
        try{

            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                    int.class
            });

            Object MessageRecord =CallMethod.invoke(null,-2030);
            if(TextUtils.isEmpty(UserUin))
            {
                MMethod.CallMethod(MessageRecord,MessageRecord.getClass(),"init",void.class,
                        new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                        BaseInfo.GetCurrentUin(),Group,BaseInfo.GetCurrentUin(),TipText,Time,-2030,
                        1,Time
                );
            }
            else
            {
                MMethod.CallMethod(MessageRecord,MessageRecord.getClass(),"init",void.class,
                        new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                        BaseInfo.GetCurrentUin(),UserUin,BaseInfo.GetCurrentUin(),TipText,Time,-2030,
                        0,Time
                );
            }
            MField.SetField(MessageRecord,"msgUid",msgUid);
            MField.SetField(MessageRecord,"shmsgseq",Seq);
            MField.SetField(MessageRecord,"isread",true);
            BaseCall.AddMsg(MessageRecord);
        }
        catch (Throwable th)
        {

        }

    }
    public static void Message_Send_Effect_Pic(String GroupUin,String PicPath,int Type){
        try{
            Object MessagePicRecord = com.ldq.connect.QQUtils.QQMessage_Builder.Build_Pic(com.ldq.connect.QQUtils.QQMessage_Builder.Build_SessionInfo(GroupUin,""),PicPath);
            Object Resp1 = MClass.CallConstrutor(MClass.loadClass("localpb.richMsg.RichMsg$PicRec"),new Class[0]);

            MMethod.CallMethod(Resp1,Resp1.getClass(),"mergeFrom",new Class[]{byte[].class},new Object[]{MField.GetField(MessagePicRecord,"msgData",byte[].class)});


            Object RespType = MClass.CallConstrutor(MClass.loadClass("tencent.im.msg.hummer.resv3.CustomFaceExtPb$ResvAttr"),new Class[0]);
            Object ShowType = MField.GetField(RespType,"msg_image_show",MClass.loadClass("tencent.im.msg.hummer.resv3.CustomFaceExtPb$AnimationImageShow"));
            Object Int32_ShowType = MField.GetField(ShowType,"int32_effect_id",MClass.loadClass("com.tencent.mobileqq.pb.PBInt32Field"));
            MMethod.CallMethod(Int32_ShowType,"set",void.class,new Class[]{int.class},Integer.valueOf(Type+40000));
            MMethod.CallMethod(ShowType,ShowType.getClass(),"setHasFlag",void.class,new Class[]{boolean.class},true);


            byte[] PICDatas = MMethod.CallMethod(RespType,"toByteArray",byte[].class,new Class[0]);


            Object oobj = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro"),new Class[]{byte[].class},PICDatas);
            Object PBField = MField.GetField(Resp1,"bytes_pb_reserved",MClass.loadClass("com.tencent.mobileqq.pb.PBBytesField"));
            MMethod.CallMethod(PBField,"set",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro")},oobj);


            PICDatas = MMethod.CallMethod(Resp1,"toByteArray",byte[].class,new Class[0]);

            Object MessageRecord = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.data.MessageForTroopEffectPic"),new Class[0]);

            MMethod.CallMethod(MessageRecord,MessageRecord.getClass(),"init",void.class,
                    new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                    BaseInfo.GetCurrentUin(),GroupUin,BaseInfo.GetCurrentUin(),"[秀图]",System.currentTimeMillis()/1000,-5015,
                    1,System.currentTimeMillis()/1000
            );
            MField.SetField(MessageRecord,MessageRecord.getClass(),"msgData",PICDatas,byte[].class);
            MField.SetField(MessageRecord,MessageRecord.getClass(),"msgUid",(long)((long)(MField.GetField(MessagePicRecord,"msgUid",long.class))+1),long.class);
            MField.SetField(MessageRecord,MessageRecord.getClass(),"shmsgseq",MField.GetField(MessagePicRecord,"shmsgseq",long.class),long.class);
            MField.SetField(MessageRecord,MessageRecord.getClass(),"msgUid",MField.GetField(MessagePicRecord,"msgUid",long.class),long.class);
            MMethod.CallMethod(MessageRecord,"doParse",void.class,new Class[0]);
            MField.SetField(MessageRecord,MessageRecord.getClass(),"msgtype",-5015,int.class);
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForPic"),
                    int.class
            });

            CallMethod.invoke(null,MHookEnvironment.AppInterface,QQMessage_Builder.Build_SessionInfo(GroupUin,""),MessageRecord,0);
        }catch (Exception e){
            MLogCat.Print_Error("EffectPic",e);
        }

    }

    public static void PreDownloadPic(Object PicMessage){
        try{
            MMethod.CallMethod(PicMessage,"checkType",void.class,new Class[0]);
            String FilePath = MMethod.CallMethod(PicMessage,"getFilePath",String.class,new Class[]{String.class
            },"chatimg");
            File f = new File(FilePath);
            if(!f.exists()){
                String PicMd5 = MField.GetField(PicMessage,"md5",String.class);
                PicMd5  = "http://gchat.qpic.cn/gchatpic_new/0/0-0-"+PicMd5+"/0?term=2";
                HttpUtils.downlaodFile(PicMd5,FilePath);
                MField.SetField(PicMessage,"path",FilePath);
            }
        }catch (Exception e){
            MLogCat.Print_Error("PreDownloadPic",e);
        }

    }
    //图文消息发送
    public static void Message_Send_Mix(Object _Session, Object Record) throws Exception {
        Method mMethod = MMethod.FindMethod("com.tencent.mobileqq.replymsg.ReplyMsgSender","a",void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                MClass.loadClass("com.tencent.mobileqq.data.MessageForMixedMsg"),
                MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                int.class
        });
        Object Call = MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.replymsg.ReplyMsgSender"),"a",MClass.loadClass("com.tencent.mobileqq.replymsg.ReplyMsgSender"),
                new Class[0],new Object[0]
        );
        mMethod.invoke(Call,MHookEnvironment.AppInterface,Record,_Session,0);
    }
    //回复消息发送
    public static void Message_Send_Reply(String GroupUin,Object SourceMessageRecord,String MessageContent)
    {
        try{
            String Uins = MField.GetField(SourceMessageRecord,"senderuin",String.class);
            Object Appinterface = BaseInfo.GetAppInterface();
            Method SourceInfo = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.reply.ReplyMsgUtils","a",MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"),
                    new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                            int.class,long.class,String.class
                    }
            );
            Object SourceInfoObj = SourceInfo.invoke(null,Appinterface,SourceMessageRecord,0,Long.parseLong(Uins), com.ldq.connect.QQUtils.TroopManager.GetTroopName(GroupUin));

            Method BuildMsg = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a",MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText"),
                    new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            String.class,int.class,
                            MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"),
                            String.class
                    }
            );


            Object Builded = BuildMsg.invoke(null,Appinterface,GroupUin,1,SourceInfoObj,MessageContent);



            Object Call = MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.replymsg.ReplyMsgSender"),"a",MClass.loadClass("com.tencent.mobileqq.replymsg.ReplyMsgSender"),
                    new Class[0],new Object[0]
            );

            Method mMethod = MMethod.FindMethod("com.tencent.mobileqq.replymsg.ReplyMsgSender","a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"),
                    int.class,
                    int.class,
                    boolean.class
            });

            Object Session = com.ldq.connect.QQUtils.QQMessage_Builder.Build_SessionInfo(GroupUin,"");
            mMethod.invoke(Call,Appinterface,Builded ,Session,2,0,false);
        }catch (Throwable th)
        {
            MLogCat.Print_Error("SendReplyMsg",th);
        }
    }
    public static void QQ_Forawrd_File(Object SessionInfo,Object ChatMessage){
        try{
            Object Instance = MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.multimsg.MultiMsgManager"),"a",MClass.loadClass("com.tencent.mobileqq.multimsg.MultiMsgManager"),new Class[0]);
            MMethod.CallMethod(Instance,"a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    int.class,
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo")
            },ChatMessage,MHookEnvironment.AppInterface,0,SessionInfo);
        }catch (Exception e){
            MLogCat.Print_Error("ForawrdTroopFile",e);
        }
    }
    public static void QQ_Forward_ShortVideo(Object _SessionInfo,Object ChatMessage){
        try{
            MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.activity.ChatActivityFacade"),"a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForShortVideo")
            },MHookEnvironment.AppInterface,_SessionInfo,ChatMessage);
        }catch (Exception e){
            MLogCat.Print_Error("Forward",e);
        }
    }
    public static void Copy_And_Send_PokeMsg(Object raw){
        try{
            Object PokeEmo = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.data.MessageForPokeEmo"),new Class[0]);
            MField.SetField(PokeEmo,"msgtype",-5018);
            MField.SetField(PokeEmo,"pokeemoId",13);
            MField.SetField(PokeEmo,"pokeemoPressCount",MField.GetField(raw,"pokeemoPressCount"));
            MField.SetField(PokeEmo,"emoIndex",MField.GetField(raw,"emoIndex"));
            MField.SetField(PokeEmo,"summary",MField.GetField(raw,"summary"));
            MField.SetField(PokeEmo,"emoString",MField.GetField(raw,"emoString"));
            MField.SetField(PokeEmo,"emoCompat",MField.GetField(raw,"emoCompat"));
            MMethod.CallMethod(PokeEmo,"initMsg",void.class,new Class[0]);
            String friendInfo = MField.GetField(raw,"frienduin",String.class);
            int istroop = MField.GetField(raw,"istroop",int.class);
            MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.service.message.MessageRecordFactory"),
                    "a",void.class,new Class[]{MClass.loadClass("com.tencent.common.app.AppInterface"),ClassTable.MessageRecord(),String.class,String.class,int.class},
                    MHookEnvironment.AppInterface,PokeEmo,friendInfo,BaseInfo.GetCurrentUin(),istroop);
            BaseCall.AddAndSendMsg(PokeEmo);

        }catch (Exception e){


            MLogCat.Print_Error("Copy_And_Send_PokeMsg",e);
        }

    }

}
