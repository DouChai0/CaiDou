package com.ldq.connect.MainWorker.QQRedBagHelper;

import android.os.Handler;
import android.os.Looper;
import android.util.Log;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.GlobalConfig;
import com.ldq.connect.MTool.RegexDebugTool;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.ServerTool.UserStatus;

import org.json.JSONObject;

import java.util.HashSet;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_RedPacket_DoParse {

    private static HashSet<String> RedPacketIDCache = new HashSet<>();


    //普通,幸运,一笔画 1  专属 1024  口令 32  语音 65536
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass._loadClass("com.tencent.imcore.message.decoder.WalletMsgDecoder"), "a", "com.tencent.imcore.message.Message",
                "com.tencent.mobileqq.app.message.IMessageManager", "mqq.app.AppRuntime", new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (!UserStatus.CheckIsDonator())return;
                        if (!IsAvailable())return;

                        if ((int)MField.GetField(param.args[0],"msgtype",int.class) == -2025 && MField.GetField(param.args[0],"msgData",byte[].class) != null){
                            Object QWalletMsg = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.data.MessageForQQWalletMsg"),new Class[0]);
                            MField.SetField(QWalletMsg,"msgData",MField.GetField(param.args[0],"msgData",byte[].class));
                            MMethod.CallMethod(QWalletMsg,"parse",void.class,new Class[0]);

                            Object RedPacketItem = MField.GetField(QWalletMsg,"mQQWalletRedPacketMsg");
                            if (RedPacketItem != null){
                               Handler_HB_Opener.InitKeyIndex();
                                boolean IsOpen = MField.GetField(RedPacketItem,"isOpened");
                                if (!IsOpen){
                                    //long msgUID = MField.GetField(param.thisObject,"msgUid",long.class);
                                    long msgUID = 72057595870650235L;

                                    String TroopUin = MField.GetField(param.args[0],"frienduin",String.class);
                                    if (TroopUin == null){
                                        MLogCat.Print_Debug(Utils.GetNowTime());
                                        MLogCat.Print_Debug(Log.getStackTraceString(new Throwable()));
                                        return;
                                    }
                                    int isTroop = MField.GetField(param.args[0],"istroop",int.class);
                                    String SenderUin = MField.GetField(param.args[0],"senderuin",String.class);

                                    String AuthKey = MField.GetField(RedPacketItem,"authkey");
                                    String RedPacketID = MField.GetField(RedPacketItem,"redPacketId");
                                    String RedPacketIndex = MField.GetField(RedPacketItem,"redPacketIndex");
                                    int RedType = MField.GetField(RedPacketItem,"redChannel");

                                    Object elem = MField.GetField(RedPacketItem,"elem");
                                    String RedPacketDesc = MField.GetField(elem,"c",String.class);
                                    String ID = MField.GetField(elem,"m",String.class);

                                    if (RedPacketIDCache.contains(RedPacketID))return;
                                    RedPacketIDCache.add(RedPacketID);
                                    //MLogCat.Print_Debug("RedType:"+SenderUin+"->"+RedType+"->"+RedPacketDesc);

                                    long DelayTime = GlobalConfig.Get_Long("Auto_Time",0);
                                    if (DelayTime > 0){
                                        new Handler(Looper.getMainLooper()).postDelayed(()->{
                                            RedPacketChecker(RedType,AuthKey,RedPacketID,TroopUin,SenderUin,RedPacketDesc,false,"");
                                        },DelayTime * 1000);
                                    }else {
                                        new Thread(()-> RedPacketChecker(RedType,AuthKey,RedPacketID,TroopUin,SenderUin,RedPacketDesc,false,"")).start();

                                    }


                                }

                            }
                        }

                    }
                });

        XposedHelpers.findAndHookMethod(MClass._loadClass("com.tencent.mobileqq.guild.message.decoder.GuildWalletMsgDecoder"),
                "a",MClass.loadClass("com.tencent.imcore.message.Message"),MClass.loadClass("com.tencent.mobileqq.app.message.IMessageManager"),
                MClass.loadClass("mqq.app.AppRuntime"),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if (!IsAvailable())return;
                        if ((int)MField.GetField(param.args[0],"msgtype",int.class) == -2025 && MField.GetField(param.args[0],"msgData",byte[].class) != null){
                            Object QWalletMsg = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.data.MessageForQQWalletMsg"),new Class[0]);
                            MField.SetField(QWalletMsg,"msgData",MField.GetField(param.args[0],"msgData",byte[].class));
                            MMethod.CallMethod(QWalletMsg,"parse",void.class,new Class[0]);

                            Object RedPacketItem = MField.GetField(QWalletMsg,"mQQWalletRedPacketMsg");
                            if (RedPacketItem != null){
                                Handler_HB_Opener.InitKeyIndex();
                                boolean IsOpen = MField.GetField(RedPacketItem,"isOpened");
                                if (!IsOpen){

                                    JSONObject ChannelData = MField.GetField(param.args[0],"mExJsonObject");
                                    if (ChannelData == null){
                                        ChannelData = new JSONObject((String) MField.GetField(param.args[0],"extStr",String.class));
                                    }
                                    String GUILD_ID = ChannelData.optString("GUILD_ID");
                                    String ChannelID = MField.GetField(param.args[0],"frienduin");
                                    String SenderID = MField.GetField(param.args[0],"senderuin");

                                    String AuthKey = MField.GetField(RedPacketItem,"authkey");
                                    String RedPacketID = MField.GetField(RedPacketItem,"redPacketId");
                                    String RedPacketIndex = MField.GetField(RedPacketItem,"redPacketIndex");
                                    int RedType = MField.GetField(RedPacketItem,"redChannel");

                                    Object elem = MField.GetField(RedPacketItem,"elem");
                                    String RedPacketDesc = MField.GetField(elem,"c",String.class);
                                    String ID = MField.GetField(elem,"m",String.class);

                                    if (RedPacketIDCache.contains(RedPacketID))return;
                                    RedPacketIDCache.add(RedPacketID);
                                    //MLogCat.Print_Debug("RedType:"+SenderID+"->"+RedType+"->"+RedPacketDesc);

                                    long DelayTime = GlobalConfig.Get_Long("Auto_Time",0);
                                    if (DelayTime > 0){
                                        new Handler(Looper.getMainLooper()).postDelayed(()->{
                                            RedPacketChecker(RedType,AuthKey,RedPacketID,GUILD_ID,SenderID,RedPacketDesc,true,ChannelID);
                                        },DelayTime * 1000);
                                    }else {
                                        RedPacketChecker(RedType,AuthKey,RedPacketID,GUILD_ID,SenderID,RedPacketDesc,true,ChannelID);
                                    }


                                }

                            }
                        }
                    }
                });

    }
    public static boolean IsAvailable(){
        return GlobalConfig.Get_Boolean("Auto_Lucky_Packet", false)
                || GlobalConfig.Get_Boolean("Auto_Passwd_Packet", false)
                || GlobalConfig.Get_Boolean("Auto_Lucky_Packet_Channel", false);
    }
    private static void RedPacketChecker(int Type,String AuthKey,String RedPacketID,String TroopUin,String SenderUin,String RedPacketDesc,boolean IsChannel,String ChannelID){
        if(TroopUin.equals("208754811") || TroopUin.equals("960081813") || TroopUin.equals("239704249"))return;
        if (RegexDebugTool.Pattern_Matches(RedPacketDesc,GlobalConfig.Get_String("Auto_While_Word"))){
            String WhileList = GlobalConfig.Get_String("Auto_While_Troop");
            if (WhileList.contains(TroopUin)){
                if(!IsChannel){
                    if (Type == 1){
                        if (RedPacketDesc.equals("一笔画")){
                        }else {
                            //拼手气,普通红包
                            if(GlobalConfig.Get_Boolean("Auto_Lucky_Packet",false)){
                                Handler_HB_Opener.OpenLuckyRedPack(AuthKey,"1",RedPacketID,Handler_HB_Opener.getSkey(),1,TroopUin,SenderUin,RedPacketDesc);
                            }
                        }
                    }else if (Type == 32) {
                        //口令红包
                        if (GlobalConfig.Get_Boolean("Auto_Passwd_Packet", false)) {
                            Handler_HB_Opener.PreOpenPassedRedPack(AuthKey, TroopUin, "32", RedPacketID, Handler_HB_Opener.getSkey(), 1, TroopUin, RedPacketDesc, BaseInfo.getSelfName(), SenderUin);
                        }
                    }
                }else {
                    if (Type == 1){
                        //拼手气,普通红包
                        if(GlobalConfig.Get_Boolean("Auto_Lucky_Packet_Channel",false)){
                            Handler_HB_Opener.OpenLuckyRedPack_Guild(AuthKey,RedPacketID,Handler_HB_Opener.getSkey(),11,SenderUin,RedPacketDesc,TroopUin,ChannelID);
                        }
                    }
                }
            }
        }


    }
}
