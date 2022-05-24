package com.ldq.connect.MainWorker.QQRedBagHelper;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.GlobalConfig;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MTool.RegexDebugTool;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.GuildUtils;
import com.ldq.connect.QQUtils.QQMessage_Builder;
import com.ldq.connect.QQUtils.QQMessage_Transform;
import com.ldq.connect.QQUtils.QQTicketUtils;
import com.ldq.connect.QQUtils.TroopManager;

import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.Locale;
import java.util.Random;

public class Handler_HB_Opener {
    private static final String grap_hb_url = "https://mqq.tenpay.com/cgi-bin/hongbao/qpay_hb_na_grap.cgi?ver=2.0&chv=3";
    private static final String hb_pre_grap = "https://mqq.tenpay.com/cgi-bin/hongbao/hb_pre_grap.cgi?ver=2.0&chv=3";
    static int KeyIndex = -1;
    public static void OpenTargetRedPack(String AuthKey,String Channel,String listid,String skey,int grouptype,String groupuin, String SenderUin,String Desc){
        try{
            StringBuilder postData = new StringBuilder();
            postData.append("listid=").append(listid)
                    .append("&authkey=").append(AuthKey)
                    .append("&hb_from=0")
                    .append("&grouptype=").append(grouptype)
                    .append("&trans_seq=").append(KeyIndex)
                    .append("&groupuin=").append(groupuin)
                    .append("&pay_flag=").append(0)
                    .append("&groupid=").append(groupuin)
                    .append("&channel=").append(Channel)
                    .append("&name=").append(URLEncoder.encode(BaseInfo.getSelfName()))
                    .append("&uin=").append(BaseInfo.GetCurrentUin())
                    .append("&senderuin=").append(SenderUin);


            String data = EncText(postData.toString(),"hb_pre_grapver=2.0&chv=3");
            Intent NewIntent = MClass.CallConstrutor(MClass.loadClass("mqq.app.NewIntent"),
                    new Class[]{Context.class,Class.class},
                    MField.GetField(null,MClass.loadClass("mqq.app.MobileQQ"),"sMobileQQ",1),
                    MClass.loadClass("com.tencent.mobileqq.qwallet.servlet.GdtAdServlet"));
            NewIntent.putExtra("cmd", "trpc.qqhb.qqhb_proxy.Handler.sso_handle");
            Object QQHBRequest = MClass.CallConstrutor(MClass.loadClass("tencent.im.qqwallet.QWalletHbPreGrab$QQHBRequest"),new Class[0]);
            MMethod.CallMethod(MField.GetField(QQHBRequest,"cgiName"),"set",void.class,new Class[]{String.class},"hb_pre_grap");
            MMethod.CallMethod(MField.GetField(QQHBRequest,"reqText"),"set",void.class,new Class[]{String.class},data);
            MMethod.CallMethod(MField.GetField(QQHBRequest,"random"),"set",void.class,new Class[]{String.class},Integer.toString(KeyIndex));
            MMethod.CallMethod(MField.GetField(QQHBRequest,"enType"),"set",void.class,new Class[]{int.class},0);

            NewIntent.putExtra("data",
                    (byte[]) (MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.utils.WupUtil"),"a",byte[].class,new Class[]{byte[].class},
                            new Object[]{MMethod.CallMethod(QQHBRequest,"toByteArray",byte[].class,new Class[0])})));
            MMethod.CallMethod(NewIntent,"setObserver",void.class,new Class[]{
                    MClass.loadClass("mqq.observer.BusinessObserver")
            }, Proxy.newProxyInstance(MHookEnvironment.mLoader, new Class[]{
                    MClass.loadClass("mqq.observer.BusinessObserver")
            }, (proxy, method, args) -> {
                Bundle bundle = (Bundle) args[2];
                byte[] dataaaa = bundle.getByteArray("data");
                Object HBReply = MClass.CallConstrutor(MClass.loadClass("tencent.im.qqwallet.QWalletHbPreGrab$QQHBReply"),new Class[0]);
                MMethod.CallMethod(HBReply,MClass.loadClass("com.tencent.mobileqq.pb.MessageMicro"),"mergeFrom",new Class[]{byte[].class},dataaaa);
                String Result = MMethod.CallMethod(MField.GetField(HBReply,"rspText"),"get",String.class,new Class[0]);
                Result = DecText(Result,"hb_pre_grap");
                JSONObject grapJSON = new JSONObject(Result);
                //MLogCat.Print_Debug(Result);

                if (grapJSON.optString("retcode").equals("0") && grapJSON.has("pre_grap_token")){
                    String preCode = grapJSON.getString("pre_grap_token");


                    StringBuilder postd = new StringBuilder();
                    postd.append("authkey=").append(AuthKey)
                            .append("&agreement=").append(0)
                            .append("&groupid=").append(groupuin)
                            .append("&channel=").append(Channel)
                            .append("&pre_grap_token=").append(URLEncoder.encode(preCode))
                            .append("&listid=").append(listid)
                            .append("&skey_type=").append(skey.length() > 12 ? "0" : "2")
                            .append("&grouptype=").append(grouptype)
                            .append("&groupuin=").append(SenderUin)
                            .append("&name=").append(URLEncoder.encode(BaseInfo.getSelfName()))
                            .append("&skey=").append(skey)
                            .append("&uin=").append(BaseInfo.GetCurrentUin());

                    String Resulta = POSTForGrapCgi(postd.toString(),skey);
                    DecodeJson(Resulta,groupuin,SenderUin,"[专属]"+Desc);
                }
                return null;
            }));

            MMethod.CallMethod(MHookEnvironment.AppInterface,"startServlet",void.class,new Class[]{
                    MClass.loadClass("mqq.app.NewIntent")},NewIntent);
        }catch (Throwable th){
            MLogCat.Print_Error("Grap_Lucky_RedPack",th);
        }
    }
    public static void OpenLuckyRedPack(String AuthKey,String Channel,String listid,String skey,int grouptype,String groupuin,String SenderUin,String Desc){
        try{
            StringBuilder postData = new StringBuilder();
            postData.append("listid=").append(listid)
                    .append("&authkey=").append(AuthKey)
                    .append("&hb_from=0")
                    .append("&grouptype=").append(grouptype)
                    .append("&trans_seq=").append(KeyIndex)
                    .append("&groupuin=").append(groupuin)
                    .append("&pay_flag=").append(0)
                    .append("&groupid=").append(groupuin)
                    .append("&channel=").append(Channel)
                    .append("&name=").append(URLEncoder.encode(BaseInfo.getSelfName()))
                    .append("&uin=").append(BaseInfo.GetCurrentUin())
                    .append("&senderuin=").append(SenderUin);


            String data = EncText(postData.toString(),"hb_pre_grapver=2.0&chv=3");
            Intent NewIntent = MClass.CallConstrutor(MClass.loadClass("mqq.app.NewIntent"),
                    new Class[]{Context.class,Class.class},
                    MField.GetField(null,MClass.loadClass("mqq.app.MobileQQ"),"sMobileQQ",1),
                    MClass.loadClass("com.tencent.mobileqq.qwallet.servlet.GdtAdServlet"));
            NewIntent.putExtra("cmd", "trpc.qqhb.qqhb_proxy.Handler.sso_handle");
            Object QQHBRequest = MClass.CallConstrutor(MClass.loadClass("tencent.im.qqwallet.QWalletHbPreGrab$QQHBRequest"),new Class[0]);
            MMethod.CallMethod(MField.GetField(QQHBRequest,"cgiName"),"set",void.class,new Class[]{String.class},"hb_pre_grap");
            MMethod.CallMethod(MField.GetField(QQHBRequest,"reqText"),"set",void.class,new Class[]{String.class},data);
            MMethod.CallMethod(MField.GetField(QQHBRequest,"random"),"set",void.class,new Class[]{String.class},Integer.toString(KeyIndex));
            MMethod.CallMethod(MField.GetField(QQHBRequest,"enType"),"set",void.class,new Class[]{int.class},0);

            NewIntent.putExtra("data",
                    (byte[]) (MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.utils.WupUtil"),"a",byte[].class,new Class[]{byte[].class},
                            new Object[]{MMethod.CallMethod(QQHBRequest,"toByteArray",byte[].class,new Class[0])})));
            MMethod.CallMethod(NewIntent,"setObserver",void.class,new Class[]{
                    MClass.loadClass("mqq.observer.BusinessObserver")
            }, Proxy.newProxyInstance(MHookEnvironment.mLoader, new Class[]{
                    MClass.loadClass("mqq.observer.BusinessObserver")
            }, (proxy, method, args) -> {
                Bundle bundle = (Bundle) args[2];
                byte[] dataaaa = bundle.getByteArray("data");
                Object HBReply = MClass.CallConstrutor(MClass.loadClass("tencent.im.qqwallet.QWalletHbPreGrab$QQHBReply"),new Class[0]);
                MMethod.CallMethod(HBReply,MClass.loadClass("com.tencent.mobileqq.pb.MessageMicro"),"mergeFrom",new Class[]{byte[].class},dataaaa);
                String Result = MMethod.CallMethod(MField.GetField(HBReply,"rspText"),"get",String.class,new Class[0]);
                Result = DecText(Result,"hb_pre_grap");
                JSONObject grapJSON = new JSONObject(Result);
               // MLogCat.Print_Debug(Result);

                if (grapJSON.optString("retcode").equals("0") && grapJSON.has("pre_grap_token")){
                    String preCode = grapJSON.getString("pre_grap_token");


                    StringBuilder postd = new StringBuilder();
                    postd.append("authkey=").append(AuthKey)
                            .append("&hb_from=0")
                            .append("&groupid=").append(groupuin)
                            .append("&agreement=").append(0)
                            .append("&pay_flag=").append(0)
                            .append("&channel=").append(1)
                            .append("&pre_grap_token=").append(URLEncoder.encode(preCode))
                            .append("&senderuin=").append(SenderUin)
                            .append("&listid=").append(listid)
                            .append("&skey_type=").append(skey.length() > 12 ? "0" : "2")
                            .append("&grouptype=").append(grouptype)
                            .append("&groupuin=").append(groupuin)
                            .append("&name=").append(URLEncoder.encode(BaseInfo.getSelfName()))
                            .append("&skey=").append(skey)
                            .append("&uin=").append(BaseInfo.GetCurrentUin());

                    String re = POSTForGrapCgi(postd.toString(),skey);
                    DecodeJson(re,groupuin,SenderUin,"[拼手气/普通]"+Desc);
                }
                return null;
            }));

            MMethod.CallMethod(MHookEnvironment.AppInterface,"startServlet",void.class,new Class[]{
                    MClass.loadClass("mqq.app.NewIntent")},NewIntent);

        }catch (Throwable th){
            MLogCat.Print_Error("Grap_Lucky_RedPack",th);
        }
    }
    public static void OpenLuckyRedPack_Guild(String AuthKey,String listid,String skey,int grouptype,String SenderUin,String Desc
    ,String GuildID,String ChannelID
    ){
        try{
            StringBuilder postData = new StringBuilder();
            postData.append("authkey=").append(AuthKey)
                    .append("&hb_from=0")
                    .append("&pay_flag=").append(0)
                    .append("&groupid=").append(ChannelID)
                    .append("&channel=").append(1)
                    .append("&senderuin=").append(SenderUin)
                    .append("&listid=").append(listid)
                    .append("&grouptype=").append(grouptype)
                    .append("&sub_guild_id=").append(ChannelID)
                    .append("&trans_seq=").append(KeyIndex)
                    .append("&groupuin=").append(GuildID)
                    .append("&tinyid=").append(BaseInfo.GetCurrentTinyID())
                    .append("&guild_id=").append(GuildID)
                    .append("&name=").append(URLEncoder.encode(BaseInfo.getSelfName()))
                    .append("&uin=").append(BaseInfo.GetCurrentUin());


            String data = EncText(postData.toString(),"hb_pre_grapver=2.0&chv=3");
            Intent NewIntent = MClass.CallConstrutor(MClass.loadClass("mqq.app.NewIntent"),
                    new Class[]{Context.class,Class.class},
                    MField.GetField(null,MClass.loadClass("mqq.app.MobileQQ"),"sMobileQQ",1),
                    MClass.loadClass("com.tencent.mobileqq.qwallet.servlet.GdtAdServlet"));
            NewIntent.putExtra("cmd", "trpc.qqhb.qqhb_proxy.Handler.sso_handle");
            Object QQHBRequest = MClass.CallConstrutor(MClass.loadClass("tencent.im.qqwallet.QWalletHbPreGrab$QQHBRequest"),new Class[0]);
            MMethod.CallMethod(MField.GetField(QQHBRequest,"cgiName"),"set",void.class,new Class[]{String.class},"hb_pre_grap");
            MMethod.CallMethod(MField.GetField(QQHBRequest,"reqText"),"set",void.class,new Class[]{String.class},data);
            MMethod.CallMethod(MField.GetField(QQHBRequest,"random"),"set",void.class,new Class[]{String.class},Integer.toString(KeyIndex));
            MMethod.CallMethod(MField.GetField(QQHBRequest,"enType"),"set",void.class,new Class[]{int.class},0);

            NewIntent.putExtra("data",
                    (byte[]) (MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.utils.WupUtil"),"a",byte[].class,new Class[]{byte[].class},
                            new Object[]{MMethod.CallMethod(QQHBRequest,"toByteArray",byte[].class,new Class[0])})));
            MMethod.CallMethod(NewIntent,"setObserver",void.class,new Class[]{
                    MClass.loadClass("mqq.observer.BusinessObserver")
            }, Proxy.newProxyInstance(MHookEnvironment.mLoader, new Class[]{
                    MClass.loadClass("mqq.observer.BusinessObserver")
            }, (proxy, method, args) -> {
                Bundle bundle = (Bundle) args[2];
                byte[] dataaaa = bundle.getByteArray("data");
                Object HBReply = MClass.CallConstrutor(MClass.loadClass("tencent.im.qqwallet.QWalletHbPreGrab$QQHBReply"),new Class[0]);
                MMethod.CallMethod(HBReply,MClass.loadClass("com.tencent.mobileqq.pb.MessageMicro"),"mergeFrom",new Class[]{byte[].class},dataaaa);
                String Result = MMethod.CallMethod(MField.GetField(HBReply,"rspText"),"get",String.class,new Class[0]);
                Result = DecText(Result,"hb_pre_grap");
                JSONObject grapJSON = new JSONObject(Result);
                //MLogCat.Print_Debug(Result);

                if (grapJSON.optString("retcode").equals("0") && grapJSON.has("pre_grap_token")){
                    String preCode = grapJSON.getString("pre_grap_token");


                    StringBuilder postd = new StringBuilder();
                    postd.append("authkey=").append(AuthKey)
                            .append("&hb_from=0")
                            .append("&agreement=").append(0)
                            .append("&pay_flag=").append(0)
                            .append("&groupid=").append(ChannelID)
                            .append("&channel=").append(1)
                            .append("&pre_grap_token=").append(URLEncoder.encode(preCode))
                            .append("&senderuin=").append(SenderUin)
                            .append("&listid=").append(listid)
                            .append("&skey_type=").append(skey.length() > 12 ? "0" : "2")
                            .append("&grouptype=").append(grouptype)
                            .append("&sub_guild_id=").append(ChannelID)
                            .append("&groupuin=").append(SenderUin)
                            .append("&tinyid=").append(BaseInfo.GetCurrentTinyID())
                            .append("&guild_id=").append(GuildID)
                            .append("&name=").append(URLEncoder.encode(BaseInfo.getSelfName()))
                            .append("&skey=").append(skey)
                            .append("&uin=").append(BaseInfo.GetCurrentUin());

                    String re = POSTForGrapCgi(postd.toString(),skey);
                    DecodeJson(re,GuildID,SenderUin,"[拼手气/普通]"+Desc);
                }
                return null;
            }));

            MMethod.CallMethod(MHookEnvironment.AppInterface,"startServlet",void.class,new Class[]{
                    MClass.loadClass("mqq.app.NewIntent")},NewIntent);

        }catch (Throwable th){
            MLogCat.Print_Error("Grap_Lucky_RedPack",th);
        }
    }
    public static void PreOpenPassedRedPack(String AuthKey,String GroupID,String Channel,String listid,String skey,int grouptype,String groupuin,String answer,
                                            String QQNamey,String sender){
        InvokerLoop loopOnce = new InvokerLoop() {
            @Override
            public void MakeCall(Object record) {
                try{
                    long msgUID = MField.GetField(record,"msgUid",long.class);
                    msgUID = (int) (msgUID & -1);
                    long msgSeq = MField.GetField(record,"msgseq",long.class);
                    OpenPasswdRedPack(AuthKey,GroupID,Channel,listid,skey,grouptype,groupuin,answer,QQNamey,msgUID,msgSeq,1,sender);
                }catch (Exception e){
                    MLogCat.Print_Error("PasswdRedPack",e);
                }
            }
        };
        HBQueue.add(loopOnce);
        String Rule = GlobalConfig.Get_String("Change_Key_Rule");
        if (!Rule.isEmpty() && RegexDebugTool.Pattern_Matches(answer,Rule)){
            QQMessage_Transform.Message_Send_Text(groupuin,"",GlobalConfig.Get_String("Change_Key"),new ArrayList());
        }else {
            QQMessage_Transform.Message_Send_Text(groupuin,"","\u0001"+answer+"\u0001",new ArrayList());
        }

    }
    public static void OpenPasswdRedPack(String AuthKey,String GroupID,String Channel,String listid,String skey,int grouptype,String groupuin,String answer,
                                                String QQName,long msgUID,long msg_seq,int count,String sender){
        try{
            if(count > 3)return;
            StringBuilder postData = new StringBuilder();
            postData.append("authkey=").append(AuthKey)
                    .append("&agreement=0")
                    .append("&groupid=").append(GroupID)
                    .append("&channel=").append(Channel)
                    .append("&listid=").append(listid)
                    .append("&skey_type=").append(skey.length() > 12 ? "0" : "2")
                    .append("&grouptype=").append(grouptype)
                    .append("&answer=").append(URLEncoder.encode(answer))
                    .append("&groupuin=").append(groupuin)
                    .append("&name=").append(URLEncoder.encode(BaseInfo.getSelfName()))
                    .append("&skey=").append(skey)
                    .append("&uin=").append(BaseInfo.GetCurrentUin())
                    .append("&msg_id=").append(msgUID)
                    .append("&msg_md5=").append(DataUtils.getDataMD5(answer.getBytes(StandardCharsets.UTF_8)).toLowerCase(Locale.ROOT))
                    .append("&msg_seq=").append(msg_seq);


            String decResult = POSTForGrapCgi(postData.toString(),skey);
            if (decResult.contains("系统繁忙")){
                new Handler(Looper.getMainLooper())
                        .postDelayed(()->OpenPasswdRedPack(AuthKey, GroupID, Channel, listid, skey, grouptype, groupuin, answer, QQName, msgUID, msg_seq, count+1,sender),500);
                return;
            }
            DecodeJson(decResult,groupuin,sender,"[口令]"+answer);
        }catch (Throwable th){
            MLogCat.Print_Error("PasswdRedPacket",th);
        }
    }
    public static void PreOpenVoiceRedPack(String AuthKey,String listID,String skey,String Desc,String GroupUin,String SenderUin){
        try{
            VoiceUploader uploader = new VoiceUploader();
            String LocalPath = MHookEnvironment.MAppContext.getExternalCacheDir()+"/"+Utils.GetNowTime22()+".slk";
            Hook_For_VoiceRedbag.VoiceSaveTOFile(Desc,LocalPath);
            Object record = QQMessage_Builder.Build_Voice(QQMessage_Builder.Build_SessionInfo(GroupUin,""),LocalPath);
            uploader.Init(record,LocalPath, MConfig.Get_Boolean("Main","MainSwitch","语音红包不发语音",false));
            uploader.StartForKey(() -> {
                try{
                    Object voiceInfo = MClass.CallConstrutor(MClass.loadClass("Wallet.GroupVoiceInfo"),new Class[]{
                            long.class,long.class,byte[].class
                    },Long.parseLong(GroupUin),0,uploader.getLocalMD5());
                    Object c2CVoiceInfo = MClass.CallConstrutor(MClass.loadClass("Wallet.C2CVoiceInfo"),new Class[]{String.class},uploader.getUUID());
                    String SimpDesc = Desc.replaceAll("[,，。、 ]", "");

                    Object VoiceMatchStatus = MClass.CallConstrutor(MClass.loadClass("Wallet.VoiceMatchStatus"),new Class[]{
                            int.class,int.class,int.class,String.class
                    },1,0,0,MMethod.CallMethod(
                            MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.qwallet.hb.grap.voice.impl.VoiceRecognizer"),"a",MClass.loadClass("com.tencent.mobileqq.qwallet.hb.grap.voice.impl.VoiceRecognizer"),new Class[0])
                    ,"b",String.class,new Class[]{MClass.loadClass("mqq.app.AppRuntime")},MHookEnvironment.AppInterface));

                    Object VoiceRedPackMatchReq = MClass.CallConstrutor(MClass.loadClass("Wallet.VoiceRedPackMatchReq"),new Class[]{
                            long.class,String.class,String.class,long.class,String.class,long.class,int.class,
                            MClass.loadClass("Wallet.GroupVoiceInfo"),int.class,MClass.loadClass("Wallet.C2CVoiceInfo"),
                            String.class,MClass.loadClass("Wallet.VoiceMatchStatus")
                    },Long.parseLong(BaseInfo.GetCurrentUin()),listID,SimpDesc,Long.parseLong(SenderUin),uploader.getUUID(),((Integer)MMethod.CallMethod(null,MClass.loadClass("com.tencent.common.config.AppSetting"),"d",int.class,new Class[0])).longValue(),1,
                            voiceInfo,0,c2CVoiceInfo,"8.8.68",
                            VoiceMatchStatus);

                    Object request2 = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.qwallet.impl.servlet.request.VoiceRedPackMatchRequest"),new Class[]{
                            MClass.loadClass("Wallet.VoiceRedPackMatchReq")
                    },VoiceRedPackMatchReq);

                    MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.qwallet.impl.servlet.QWalletCommonServlet"),
                            "a",void.class,new Class[]{
                                    MClass.loadClass("com.tencent.mobileqq.qwallet.impl.servlet.QWalletCommonRequest"),
                                    MClass.loadClass("mqq.observer.BusinessObserver")}
                                    ,request2,Proxy.newProxyInstance(MHookEnvironment.mLoader, new Class[]{
                                    MClass.loadClass("mqq.observer.BusinessObserver")
                            }, new InvocationHandler() {
                                @Override
                                public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                                    MLogCat.Print_Debug(args[0]);
                                    MLogCat.Print_Debug(args[1]);
                                    Bundle bundle = (Bundle) args[2];
                                    OpenVoiceRegPack(AuthKey,listID,skey,GroupUin,SenderUin,Desc);
                                    return null;
                                }
                            }));
                }catch (Exception e){
                    MLogCat.Print_Error("VoiceRedpackOpenInn",e);
                }

            });
        }catch (Exception e){

        }


    }
    private static void OpenVoiceRegPack(String AuthKey,String listid,String skey,String groupuin,String senderuin,String desc){

        StringBuilder postd = new StringBuilder();
        postd.append("authkey=").append(AuthKey)
                .append("&agreement=").append(0)
                .append("&groupid=").append(groupuin)
                .append("&channel=").append(65536)
                .append("&listid=").append(listid)
                .append("&skey_type=").append(skey.length() > 12 ? "0" : "2")
                .append("&grouptype=").append(1)
                .append("&groupuin=").append(groupuin)
                .append("&name=").append(URLEncoder.encode(BaseInfo.getSelfName()))
                .append("&skey=").append(skey)
                .append("&uin=").append(BaseInfo.GetCurrentUin());

        new Thread(()->{
            try{
                URL u = new URL(grap_hb_url);
                HttpURLConnection con = (HttpURLConnection) u.openConnection();
                con.setRequestProperty("user-agent","okhttp/3.12.10");
                con.setDoOutput(true);
                OutputStream out = con.getOutputStream();

                String post = "req_text="+EncText(postd.toString(),"https://mqq.tenpay.com/cgi-bin/hongbao/qpay_hb_na_grap.cgi?ver2.0&chv=3");
                String skeyType = "2&random="+KeyIndex;
                if (skey.startsWith("v")&& skey.length() > 12){
                    skeyType = "0";
                }
                SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
                post += "&skey_type="+skeyType+"&msgno="+getMsgNo(BaseInfo.GetCurrentUin());
                post += "&skey="+skey;

                out.write(post.getBytes(StandardCharsets.UTF_8));
                out.flush();

                InputStream insp = con.getInputStream();
                String ret = new String(DataUtils.readAllBytes(insp));

                String decResult = DecText(ret,"https://mqq.tenpay.com/cgi-bin/hongbao/qpay_hb_na_grap.cgi?");
                DecodeJson(decResult,groupuin,senderuin,"[语音]"+desc);

            }catch (Throwable e){
                MLogCat.Print_Error("Lucky_Pack_Grap",e);
            }

        }).start();

    }
    public static void OpenOnePenchRedPack(String AuthKey,String Channel,String listid,String skey,int grouptype,String groupuin,String SenderUin){
        try{
            if (Thread.currentThread().getName().equals("main")){
                new Thread(()->OpenOnePenchRedPack(AuthKey, Channel, listid, skey, grouptype, groupuin, SenderUin)).start();
                return;
            }
            StringBuilder postData = new StringBuilder();
            postData.append("listid=").append(listid)
                    .append("&authkey=").append(AuthKey)
                    .append("&hb_from=0")
                    .append("&grouptype=").append(grouptype)
                    .append("&trans_seq=").append(KeyIndex)
                    .append("&groupuin=").append(groupuin)
                    .append("&pay_flag=").append(0)
                    .append("&groupid=").append(groupuin)
                    .append("&channel=").append(Channel)
                    .append("&name=").append(URLEncoder.encode(BaseInfo.getSelfName()))
                    .append("&uin=").append(BaseInfo.GetCurrentUin())
                    .append("&senderuin=").append(SenderUin);


            String data = EncText(postData.toString(),"hb_pre_grapver=2.0&chv=3");
            Intent NewIntent = MClass.CallConstrutor(MClass.loadClass("mqq.app.NewIntent"),
                    new Class[]{Context.class,Class.class},
                    MField.GetField(null,MClass.loadClass("mqq.app.MobileQQ"),"sMobileQQ",1),
                    MClass.loadClass("com.tencent.mobileqq.qwallet.servlet.GdtAdServlet"));
            NewIntent.putExtra("cmd", "trpc.qqhb.qqhb_proxy.Handler.sso_handle");
            Object QQHBRequest = MClass.CallConstrutor(MClass.loadClass("tencent.im.qqwallet.QWalletHbPreGrab$QQHBRequest"),new Class[0]);
            MMethod.CallMethod(MField.GetField(QQHBRequest,"cgiName"),"set",void.class,new Class[]{String.class},"hb_pre_grap");
            MMethod.CallMethod(MField.GetField(QQHBRequest,"reqText"),"set",void.class,new Class[]{String.class},data);
            MMethod.CallMethod(MField.GetField(QQHBRequest,"random"),"set",void.class,new Class[]{String.class},Integer.toString(KeyIndex));
            MMethod.CallMethod(MField.GetField(QQHBRequest,"enType"),"set",void.class,new Class[]{int.class},0);

            NewIntent.putExtra("data",
                    (byte[]) (MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.utils.WupUtil"),"a",byte[].class,new Class[]{byte[].class},
                            new Object[]{MMethod.CallMethod(QQHBRequest,"toByteArray",byte[].class,new Class[0])})));
            MMethod.CallMethod(NewIntent,"setObserver",void.class,new Class[]{
                    MClass.loadClass("mqq.observer.BusinessObserver")
            }, Proxy.newProxyInstance(MHookEnvironment.mLoader, new Class[]{
                    MClass.loadClass("mqq.observer.BusinessObserver")
            }, (proxy, method, args) -> {
                Bundle bundle = (Bundle) args[2];
                byte[] dataaaa = bundle.getByteArray("data");
                Object HBReply = MClass.CallConstrutor(MClass.loadClass("tencent.im.qqwallet.QWalletHbPreGrab$QQHBReply"),new Class[0]);
                MMethod.CallMethod(HBReply,MClass.loadClass("com.tencent.mobileqq.pb.MessageMicro"),"mergeFrom",new Class[]{byte[].class},dataaaa);
                String Result = MMethod.CallMethod(MField.GetField(HBReply,"rspText"),"get",String.class,new Class[0]);
                Result = DecText(Result,"hb_pre_grap");
                JSONObject grapJSON = new JSONObject(Result);
                //MLogCat.Print_Debug(Result);

                if (grapJSON.optString("retcode").equals("0") && grapJSON.has("pre_grap_token")){
                    String preCode = grapJSON.getString("pre_grap_token");


                    StringBuilder postd = new StringBuilder();
                    postd.append("authkey=").append(AuthKey)
                            .append("&pay_flag=").append(0)
                            .append("&channel=").append(1)
                            .append("&grap_step=").append(0)
                            .append("&skey_type=").append(skey.length() > 12 ? "0" : "2")
                            .append("&grouptype=").append(grouptype)
                            .append("&groupuin=").append(SenderUin)
                            .append("&uin=").append(BaseInfo.GetCurrentUin())
                            .append("&hb_from=137")
                            .append("&hb_from_type=100")
                            .append("&agreement=").append(0)
                            .append("&groupid=").append(groupuin)
                            .append("&pre_grap_token=").append(URLEncoder.encode(preCode))
                            .append("&senderuin=").append(SenderUin)
                            .append("&listid=").append(listid)
                            .append("&name=").append(URLEncoder.encode(BaseInfo.getSelfName()))
                            .append("&skey=").append(skey);

                    String result = POSTForGrapCgi(postd.toString(),skey);

                    JSONObject send_object = new JSONObject(result).getJSONObject("send_object");
                    String OnePanURL = send_object.getString("act_url");
                    //MLogCat.Print_Debug("ACTURL="+OnePanURL);

                    new Thread(()->{
                        Handler_OneStrokeHBHelper helper = new Handler_OneStrokeHBHelper(OnePanURL);
                        if (helper.decode()){
                            String sedKey = helper.getResultToken();

                            StringBuilder poste = new StringBuilder();
                            poste.append("authkey=").append(AuthKey)
                                    .append("&hb_from=137")
                                    .append("&hb_from_type=").append(100)
                                    .append("&skin_id=").append(0)
                                    .append("&feedsid=").append(URLEncoder.encode(helper.getFeedSid()))
                                    .append("&agreement=").append(0)
                                    .append("&groupid=").append(groupuin)
                                    .append("&viewTag=").append("grapH5CommonHb")
                                    .append("&channel=").append(1)
                                    .append("&fromHBList=").append(false)
                                    .append("&grap_step=").append(1)
                                    .append("&senderuin=").append(SenderUin)
                                    .append("&token=").append(URLEncoder.encode(sedKey))
                                    .append("&listid=").append(listid)
                                    .append("&skey_type=").append(skey.length() > 12 ? "0" : "2")
                                    .append("&grouptype=").append(grouptype)
                                    .append("&groupuin=").append(groupuin)
                                    .append("&domain=").append("h5.qianbao.qq.com")
                                    .append("&name=").append(URLEncoder.encode(BaseInfo.getSelfName()))
                                    .append("&skey=").append(skey)
                                    .append("&uin=").append(BaseInfo.GetCurrentUin());


                            String grap = POSTForGrapCgi(poste.toString(),skey);
                            DecodeJson(grap,groupuin,SenderUin,"[一笔画]");
                        }else {
                            MLogCat.Print_Debug("Can't open RedPacket:"+listid);
                        }
                    }).start();




                }
                return null;
            }));

            MMethod.CallMethod(MHookEnvironment.AppInterface,"startServlet",void.class,new Class[]{
                    MClass.loadClass("mqq.app.NewIntent")},NewIntent);







        }catch (Throwable th){
            MLogCat.Print_Error("Grap_Lucky_RedPack",th);
        }
    }
    private static String POSTForGrapCgi(String PostData,String skey){
        try{
            if (Thread.currentThread().getName().equals("main")){
                StringBuilder builder = new StringBuilder();
                Thread newThread = new Thread(()->{
                    builder.append(POSTForGrapCgi(PostData,skey));
                });
                newThread.start();
                newThread.join();
                return builder.toString();
            }
            URL u = new URL(grap_hb_url);
            HttpURLConnection con = (HttpURLConnection) u.openConnection();
            con.setRequestProperty("user-agent","okhttp/3.12.10");
            con.setDoOutput(true);
            OutputStream out = con.getOutputStream();

            String post = "req_text="+EncText(PostData,"https://mqq.tenpay.com/cgi-bin/hongbao/qpay_hb_na_grap.cgi?ver2.0&chv=3");
            String skeyType = "2&random="+KeyIndex;
            if (skey.startsWith("v")&& skey.length() > 12){
                skeyType = "0";
            }
            SimpleDateFormat format = new SimpleDateFormat("yyyyMMddHHmmss");
            post += "&skey_type="+skeyType+"&msgno="+getMsgNo(BaseInfo.GetCurrentUin());
            post += "&skey="+skey;

            out.write(post.getBytes(StandardCharsets.UTF_8));
            out.flush();

            InputStream insp = con.getInputStream();
            String ret = new String(DataUtils.readAllBytes(insp));

            String decResult = DecText(ret,"https://mqq.tenpay.com/cgi-bin/hongbao/qpay_hb_na_grap.cgi?");
            return decResult;
        }catch (Exception e){
            return null;
        }

    }
    public static void DecodeJson(String JSONData,String TroopUin,String SenderUin,String Desc){
        try{
            JSONObject json = new JSONObject(JSONData);
            try{
                String skey = json.getString("skey");
                String skeyTime = json.getString("skey_expire");
                long endTime = System.currentTimeMillis() + Long.parseLong(skeyTime) * 1000;
                int mkeyIndex = Integer.parseInt(json.getString("trans_seq"));

                SaveSkey(skey,endTime,mkeyIndex);
            }catch (Exception e){

            }
            if (json.optString("retmsg").equals("ok")){

                if (json.has("recv_object")){
                    JSONObject recvData = json.getJSONObject("recv_object");
                    Object Money = recvData.get("amount");
                    int getMoney;
                    if (Money instanceof String){
                        getMoney = Integer.parseInt((String)Money);
                    }else {
                        getMoney = (int)Money;
                    }
                    HB_Sum(getMoney,TroopUin,SenderUin,Desc);
                }



            }
        }catch (Exception e){

        }

    }
    static int index = 1;
    public static String getMsgNo(String str) {
        String format = new SimpleDateFormat("yyyyMMddHHmmss").format(new Date());
        StringBuilder sb = new StringBuilder();
        sb.append(str);
        sb.append(format);
        int i = index;
        index = i + 1;
        String valueOf = String.valueOf(i);
        int length = (28 - sb.length()) - valueOf.length();
        for (int i2 = 0; i2 < length; i2++) {
            sb.append("0");
        }
        sb.append(valueOf);
        return sb.toString();
    }
    private static String DecText(String text,String url){
        try{
            Object EncRequest = MClass.CallConstrutor(MClass._loadClass("com.tenpay.sdk.basebl.EncryptRequest"),
                    new Class[]{Context.class}, MHookEnvironment.MAppContext);
            Object encResult = MMethod.CallMethod(EncRequest,"decypt",MClass.loadClass("com.tenpay.sdk.basebl.DecytBean"),
                    new Class[]{String.class,String.class,int.class,String.class},
                    BaseInfo.GetCurrentUin(),url,
                    KeyIndex,text);
            return MField.GetField(encResult,"decryptStr");

        }catch (Throwable th){
            MLogCat.Print_Error("RequestEncoder",th);
            return null;
        }
    }
    private static String EncText(String text,String URL){
        try{
            Object EncRequest = MClass.CallConstrutor(MClass._loadClass("com.tenpay.sdk.basebl.EncryptRequest"),
                    new Class[]{Context.class}, MHookEnvironment.MAppContext);
            String psKey = QQTicketUtils.GetPsKey("tenpay.com");
            Object encResult = MMethod.CallMethod(EncRequest,"encypt",MClass.loadClass("com.tenpay.sdk.basebl.EncryptRequest$Encrypt"),
                    new Class[]{String.class,String.class,int.class,String.class,String.class},
                    BaseInfo.GetCurrentUin(),URL,
                    KeyIndex,text,psKey);
            return MField.GetField(encResult,"encText");

        }catch (Throwable th){
            MLogCat.Print_Error("RequestEncoder",th);
            return null;
        }


    }
    public static String getSkey(){
        long time = GlobalConfig.Get_Long("SkeyTime",0);
        if (time < System.currentTimeMillis())return QQTicketUtils.GetSkey();
        return GlobalConfig.Get_String("skey");
    }
    private static int getKeyIndex(){
        int index = (int) GlobalConfig.Get_Long("keyIndex",-1);
        long time = GlobalConfig.Get_Long("SkeyTime",0);
        if(time < System.currentTimeMillis())return new Random().nextInt(16);
        if (index > -1)return index;
        return new Random().nextInt(16);

    }
    public static void SaveSkey(String skey,long time,int KeyeIndex){
        KeyIndex = KeyeIndex;
        GlobalConfig.Put_Long("keyIndex",KeyeIndex);
        GlobalConfig.Put_Long("SkeyTime",time);
        GlobalConfig.Put_String("skey",skey);
    }
    public static void InitKeyIndex(){
        if (KeyIndex == -1)KeyIndex = getKeyIndex();
    }
    public static void NotifyMessageRecv(Object MessageObj){
        try{
            if (!Hook_RedPacket_DoParse.IsAvailable()){
                HBQueue.clear();
                return;
            }
            String SenderUin = MField.GetField(MessageObj,"senderuin",String.class);
            String clzName = MessageObj.getClass().getName();
            if (clzName.contains("MessageForText") && SenderUin.equals(BaseInfo.GetCurrentUin())){
                if (HBQueue.size() > 0){
                    Iterator<InvokerLoop> its = HBQueue.iterator();
                    while (its.hasNext()){
                        InvokerLoop loop = its.next();
                        its.remove();
                        new Handler(Looper.getMainLooper())
                                .postDelayed(()-> loop.MakeCall(MessageObj),new Random().nextInt(100)+100);

                    }
                }
            }

        }catch (Exception e){

        }
    }
    static LinkedList<InvokerLoop> HBQueue =new LinkedList<>();
    interface InvokerLoop{
        void MakeCall(Object record);
    }
    private static void HB_Sum(int Count,String TroopUin,String UserUin,String DescText){
        long AllCount = GlobalConfig.Get_Long("HBCount",0);
        AllCount += Count;
        GlobalConfig.Put_Long("HBCount",AllCount);

        String ShowText = "抢到" + Count/100 + "."+((Count % 100) < 10 ? ("0"+(Count % 100)) : (Count % 100));
        ShowText = ShowText + "\n("+DescText+")";
        if (TroopUin.length() > 10){
            ShowText = ShowText + "\n来自频道:"+ GuildUtils.GetGuildName(TroopUin)+"("+TroopUin+")";
        }else {
            ShowText = ShowText + "\n来自群聊:"+ TroopManager.GetTroopName(TroopUin)+"("+TroopUin+")";
        }

        ShowText = ShowText + "\n来自用户:" + TroopManager.GetMemberName(TroopUin,UserUin)+"("+UserUin+")";
        ShowText = ShowText + "\n共抢到:" + AllCount/100 + "."+((AllCount % 100) < 10 ? ("0"+(AllCount % 100)) : (AllCount % 100));
        Utils.ShowToast(ShowText);


        String Rule = GlobalConfig.Get_String("Change_Key_Rule");
        if (!Rule.isEmpty() && RegexDebugTool.Pattern_Matches(DescText,Rule))return;

        String AutoReply = GlobalConfig.Get_String("Auto_Reply");
        if (!AutoReply.isEmpty() && TroopUin.length() < 11){
            if (!AutoReply.contains("|")){
                QQMessage_Transform.Message_Send_Text(TroopUin,"",AutoReply,new ArrayList());
            }else {
                String[] s = AutoReply.split("\\|");
                String word = s[new Random().nextInt(s.length)];
                QQMessage_Transform.Message_Send_Text(TroopUin,"",word,new ArrayList());
            }
        }

    }
}
