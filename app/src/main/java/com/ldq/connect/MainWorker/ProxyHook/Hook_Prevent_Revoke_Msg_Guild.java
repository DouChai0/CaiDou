package com.ldq.connect.MainWorker.ProxyHook;

import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.widget.LinearLayout;

import com.ldq.HookHelper.DexTable.ClassTable;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.QQTools;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Prevent_Revoke_Msg_Guild {
    public static void Start() throws ClassNotFoundException {
        try{

            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.guild.message.eventflow.api.impl.GuildEventFlowServiceImpl"),
                    "handleDeleteEvent", ClassTable.MessageRecord(),
                    MClass.loadClass("tencent.im.group_pro_proto.common.common$Event"), String.class, String.class, long.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if(MConfig.Get_Boolean("Main","MainSwitch","防撤回",false)){
                                param.setResult(null);
                                Object Event = param.args[1];
                                Object OPInfo = MField.GetField(Event,"op_info");
                                Object TinyID = MField.GetField(OPInfo,"operator_tinyid");
                                long TinyID_Long = MMethod.CallMethod(TinyID,"get",long.class,new Class[0]);
                                Object record = param.args[0];
                                String result = MMethod.CallMethod(record,"getExtInfoFromExtStr",String.class,new Class[]{
                                        String.class
                                },"isShowTipFrom");
                                if (TextUtils.isEmpty(result)){
                                    MMethod.CallMethod(record,"saveExtInfoToExtStr",void.class,new Class[]{
                                            String.class,String.class
                                    },"isShowTipFrom","true");
                                    MMethod.CallMethod(record,"prewrite",void.class,new Class[0]);
                                    AddGrayMessageBelow(param.args[0],String.valueOf(TinyID_Long));
                                }

                            }
                        }
                    }
            );

            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.guild.message.GuildRoamMessageEventFlowProcessor"), "a",
                    MClass.loadClass("com.tencent.qphone.base.remote.ToServiceMsg"),MClass.loadClass("tencent.im.group_pro_proto.synclogic.synclogic$ChannelMsgRsp"),
                   ArrayList.class,String.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if(MConfig.Get_Boolean("Main","MainSwitch","防撤回",false)){
                                param.setResult(null);
                            }

                        }
                    });

        }catch (Throwable th){
            XposedBridge.log(th);
        }
    }
    static HashSet CacheShmsgseq = new HashSet();
    public static void AddGrayMessageBelow(Object RawMessageRecord,String Tid){
        try {
            long shmsgseq = MField.GetField(RawMessageRecord,"shmsgseq",long.class);
            if(CacheShmsgseq.contains(shmsgseq))return;
            CacheShmsgseq.add(shmsgseq);
            shmsgseq++;

            Object GrayMessageRecord = MClass.CallConstrutor(MClass._loadClass("com.tencent.mobileqq.guild.message.msgtype.MessageForGuildRevokeGrayTip"),
                    new Class[0]);
            String QQUin = MField.GetField(RawMessageRecord,"senderuin",String.class);
            String frienduin = MField.GetField(RawMessageRecord,"frienduin",String.class);

            MMethod.CallMethod(GrayMessageRecord,"init",void.class,
                    new Class[]{ClassTable.MessageRecord(),String.class,boolean.class,String.class,String.class,String.class,boolean.class},
                    RawMessageRecord,"已阻止"+ QQTools.GetExtraFlag(RawMessageRecord,"GUILD_MSG_FROM_NICK")+"撤回消息",
                    false,"","",Tid,false
            );
            MField.SetField(GrayMessageRecord,"shmsgseq",shmsgseq);

            Object MessageFacade = MMethod.CallMethod(MHookEnvironment.AppInterface,MHookEnvironment.AppInterface.getClass(),"getMessageFacade",
                    MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"),new Class[0]);

            Object BaseMsgProxy_1 = MMethod.CallMethod(MessageFacade,"a",MClass.loadClass("com.tencent.imcore.message.BaseMessageManager"),
                    new Class[]{int.class},10014);
            Object BaseMsgProxy = MMethod.CallMethod(BaseMsgProxy_1,"a",MClass.loadClass("com.tencent.imcore.message.BaseMsgProxy"),new Class[]{int.class},10014);


            MMethod.CallMethod(BaseMsgProxy,"a",void.class,new Class[]{
                    String.class,int.class,
                    ClassTable.MessageRecord(),
                    MClass.loadClass("com.tencent.mobileqq.app.proxy.ProxyListener"),
                    boolean.class,boolean.class,boolean.class},
                    frienduin,10014,GrayMessageRecord,MClass.CallConstrutor(MClass.loadClass("com.tencent.imcore.message.SimpleProxyListener"),new Class[0]),
                    true,true,true);
            new Handler(Looper.getMainLooper()).post(()->{
                try {
                    Object Facade = QQTools.getRuntimeService(MClass._loadClass("com.tencent.mobileqq.msg.api.IMessageFacade"));
                    MMethod.CallMethod(Facade,"setChangeAndNotify",void.class,new Class[]{Object.class},GrayMessageRecord);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            });



        } catch (Exception e) {
            //MLogCat.Print_Error("Add Gray RevokeMessage",e);
        }
    }

}
