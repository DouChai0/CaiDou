package com.ldq.connect.MainWorker.ProxyHook;

import android.util.Log;

import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.JavaPlugin.JavaPlugin;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseCall;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.QQTools;
import com.ldq.connect.QQUtils.TroopManager;

import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Random;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Prevent_Revoke_Msg_Common
{
    public static void Start()
    {
        try{
            Method RevokeMethod = MMethod.FindMethod("com.tencent.imcore.message.QQMessageFacade","a",void.class,new Class[]{
                    ArrayList.class,boolean.class
            });
            XposedBridge.hookMethod(RevokeMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    ArrayList msgList = (ArrayList) param.args[0];
                    if(msgList==null || msgList.isEmpty())return;


                    String GroupUin = (String) FieldTable.RevokeMsgInfo_GroupUin().get(msgList.get(0));
                    String OpUin = (String) FieldTable.RevokeMsgInfo_OpUin().get(msgList.get(0));
                    String sender = (String) FieldTable.RevokeMsgInfo_Sender().get(msgList.get(0));
                    int istroop = (int) FieldTable.RevokeMsgInfo_IsTroop().get(msgList.get(0));
                    long shmsgseq = (long) FieldTable.RevokeMsgInfo_shmsgseq().get(msgList.get(0));
                    String FriendUin;
                    if(istroop == 1){
                        FriendUin = GroupUin;
                    }else if(istroop == 0){
                        if(OpUin.equals(BaseInfo.GetCurrentUin())){
                            FriendUin = GroupUin;
                        }else{
                            FriendUin = OpUin;
                        }
                    } else{
                        if(OpUin.equals(BaseInfo.GetCurrentUin())){
                            FriendUin = GroupUin;
                        }else{
                            FriendUin = OpUin;
                        }

                    }
                    Object mRawmsg = GetMessageByTimeSeq(FriendUin, istroop, shmsgseq);


                    boolean isSendFromLocal = false;
                    if(mRawmsg!=null)
                    {
                        JavaPlugin.BshOnRevokeMsg(mRawmsg, OpUin);
                        isSendFromLocal = MMethod.CallMethod(mRawmsg,"isSendFromLocal",boolean.class,new Class[0]);
                    }//这里是保留撤回的代码

                    /*
                    MLogCat.Print_Debug("Revoke_Debug:GroupUin->"+GroupUin);
                    MLogCat.Print_Debug("Revoke_Debug:OpUin->"+OpUin);
                    MLogCat.Print_Debug("Revoke_Debug:sender->"+sender);
                    MLogCat.Print_Debug("Revoke_Debug:istroop->"+istroop);
                    MLogCat.Print_Debug("Revoke_Debug:shmsgseq->"+shmsgseq);
                    MLogCat.Print_Debug("Revoke_Debug:mRawmsg->"+mRawmsg);

                     */



                    if(OpUin.equals(BaseInfo.GetCurrentUin()))
                    {
                        if(MConfig.Get_Boolean("Main","MainSwitch","保留撤回",false))
                        {
                            if(istroop==1) {
                                param.setResult(null);
                                long msgUid = (long) FieldTable.RevokeMsgInfo_msgUID().get(msgList.get(0));
                                long MsgTime = (long) FieldTable.RevokeMsgInfo_msgTime().get(msgList.get(0));

                                Object Rawmsg = GetMessageByTimeSeq(GroupUin, istroop, shmsgseq);
                                if (Rawmsg != null) {
                                    if (Rawmsg.getClass().getName().contains("MessageForTroopFile")) return;
                                    String senderuin = MField.GetField(Rawmsg,"senderuin",String.class);
                                    if(senderuin.equals(OpUin))
                                    {
                                        String tip = "已保留"+ TroopManager.GetMemberName(GroupUin, senderuin)+"("+senderuin+")撤回消息";
                                        QQMessage.AddRevokeTip(tip, MsgTime + 1, shmsgseq, msgUid + new Random().nextInt(), GroupUin, "");
                                    }
                                    else
                                    {
                                        String tip = "已保留"+ TroopManager.GetMemberName(GroupUin,OpUin)+"("+ OpUin +")撤回"+ TroopManager.GetMemberName(GroupUin,senderuin)+"("+senderuin+")的消息";
                                        QQMessage.AddRevokeTip(tip, MsgTime + 1, shmsgseq, msgUid + new Random().nextInt(), GroupUin, "");
                                    }
                                }
                            }else{
                                param.setResult(null);
                                long msgUid = (long) FieldTable.RevokeMsgInfo_msgUID().get(msgList.get(0));
                                long MsgTime = (long) FieldTable.RevokeMsgInfo_msgTime().get(msgList.get(0));

                                Object Rawmsg = GetMessageByTimeSeq(GroupUin, istroop, shmsgseq);
                                if (Rawmsg != null) {
                                    if (Rawmsg.getClass().getName().contains("MessageForTroopFile")) return;
                                    String tip = "已保留上一条撤回消息";
                                    QQMessage.AddRevokeTip(tip, MsgTime + 1, shmsgseq, msgUid + new Random().nextInt(), "", GroupUin);
                                }
                            }
                        }
                        return;
                    }



                    if(!MConfig.Get_Boolean("Main","MainSwitch","防撤回",false))return;


                    //这里是防撤回的代码


                    try{
                        long msgUid = (long) FieldTable.RevokeMsgInfo_msgUID().get(msgList.get(0));
                        long MsgTime = (long) FieldTable.RevokeMsgInfo_msgTime().get(msgList.get(0));
                        if(istroop!=1) {
                            //MLogCat.Print_Debug(OpUin + ":" + GroupUin + ":" + istroop);
                            if(istroop==1000)
                            {
                                param.setResult(null);
                                if(mRawmsg==null) {
                                    QQMessage.AddRevokeTip("没收到对方撤回的消息", MsgTime + 1, shmsgseq, msgUid + new Random().nextInt(), sender,FriendUin );
                                }else
                                {
                                    QQMessage.AddRevokeTip("已阻止撤回上一条消息", MsgTime + 1, shmsgseq, msgUid + new Random().nextInt(), sender, FriendUin);
                                }
                                return;

                            }
                            Object Rawmsg = GetMessageByTimeSeq(FriendUin, istroop, shmsgseq);
                            if (QQTools.IsFriends(OpUin)) {
                                if (Rawmsg == null) {
                                    QQMessage.AddRevokeTip("没收到对方撤回的消息", MsgTime + 1, shmsgseq, msgUid + new Random().nextInt(), "", OpUin);

                                } else {
                                    if(Rawmsg.getClass().getName().contains("MessageForFile"))return;

                                    //if(!TextUtils.isEmpty(QQTools.GetExtraFlag(mRawmsg,"IsRevokeTip")))return;
                                    //QQTools.SaveExtraFlag(mRawmsg,"IsRevokeTip","true");
                                    if (istroop == 0) {
                                        QQMessage.AddRevokeTip("已阻止对方撤回消息", (long)MField.GetField(Rawmsg,"time",long.class) , MField.GetField(Rawmsg,"msgseq",long.class), (long)MField.GetField(Rawmsg,"msgUid",long.class)+1, "", OpUin);
                                    } else {
                                        QQMessage.AddRevokeTip("已阻止对方撤回消息", (long)MField.GetField(Rawmsg,"time",long.class), MField.GetField(Rawmsg,"msgseq",long.class), (long)MField.GetField(Rawmsg,"msgUid",long.class)+1, GroupUin, OpUin);
                                    }

                                }
                            }
                        }
                        else
                        {
                            if(!OpUin.equals(BaseInfo.GetCurrentUin()))
                            {
                                Object Rawmsg = GetMessageByTimeSeq(GroupUin, 1, shmsgseq);
                                if (Rawmsg != null) {
                                    if (Rawmsg.getClass().getName().contains("MessageForTroopFile")) return;
                                    String senderuin = MField.GetField(Rawmsg,"senderuin",String.class);
                                    if(senderuin.equals(OpUin))
                                    {
                                        String tip = "已阻止"+ TroopManager.GetMemberName(GroupUin, senderuin)+"("+senderuin+")撤回消息";
                                        QQMessage.AddRevokeTip(tip, MsgTime + 1, shmsgseq, msgUid + new Random().nextInt(), GroupUin, "");
                                    }
                                    else
                                    {
                                        String tip = "已阻止"+ TroopManager.GetMemberName(GroupUin,OpUin)+"("+ OpUin +")撤回"+ TroopManager.GetMemberName(GroupUin,senderuin)+"("+senderuin+")的消息";
                                        QQMessage.AddRevokeTip(tip, MsgTime + 1, shmsgseq, msgUid + new Random().nextInt(), GroupUin, "");
                                    }
                                }
                            }

                        }
                    }
                    catch (Throwable th)
                    {
                        MLogCat.Print_Error("RevokeMsg",th);
                    }


                    param.setResult(null);
                }
            });

            RevokeMethod = MMethod.FindMethod("com.tencent.imcore.message.BaseMessageManager","a",void.class,new Class[]{
                    ArrayList.class
            });
            XposedBridge.hookMethod(RevokeMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);


                    ArrayList msgList = (ArrayList) param.args[0];
                    if(msgList==null || msgList.isEmpty())return;


                    String GroupUin = (String) FieldTable.RevokeMsgInfo_GroupUin().get(msgList.get(0));
                    String OpUin = (String) FieldTable.RevokeMsgInfo_OpUin().get(msgList.get(0));
                    String sender = (String) FieldTable.RevokeMsgInfo_Sender().get(msgList.get(0));
                    int istroop = (int) FieldTable.RevokeMsgInfo_IsTroop().get(msgList.get(0));
                    long shmsgseq = (long) FieldTable.RevokeMsgInfo_shmsgseq().get(msgList.get(0));


                    String FriendUin;
                    if(istroop == 1 || istroop == 0){
                        FriendUin = GroupUin;
                    }else{
                        FriendUin = sender;
                    }
                    Object mRawmsg = GetMessageByTimeSeq(FriendUin, istroop, shmsgseq);

                    if(mRawmsg!=null)
                    {
                        JavaPlugin.BshOnRevokeMsg(mRawmsg, OpUin);
                    }
                    if(OpUin.equals(BaseInfo.GetCurrentUin()))
                    {
                        if(MConfig.Get_Boolean("Main","MainSwitch","保留撤回",false))
                        {
                            if(istroop==1 || (istroop==0 && !mRawmsg.getClass().getName().contains("MessageForTroopFile"))
                                    || (istroop==1000 && !mRawmsg.getClass().getName().contains("MessageForTroopFile"))) {
                                param.setResult(null);
                            }
                        }
                    }


                }
            });

            RevokeMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.helper.AIORevokeMsgHelper","c",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")
            });
            XposedBridge.hookMethod(RevokeMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);

                        if(MConfig.Get_Boolean("Main","MainSwitch","保留撤回",false))
                        {
                            param.setResult(null);
                            BaseCall.RemoveMessage(param.args[0]);
                        }


                }
            });

            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.msg.api.impl.MessageFacadeImpl"), "removeMsgByUniseq",String.class, int.class, long.class, boolean.class, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String callback = Log.getStackTraceString(new Throwable());
                    if(MConfig.Get_Boolean("Main","MainSwitch","防撤回",false)){
                        if (callback.contains("com.tencent.imcore.message.BaseMessageManager")){
                            if (callback.contains("com.tencent.mobileqq.troop.utils.TroopTipsMsgMgr")){
                                param.setResult(null);
                            }
                        }
                    }
                }
            });


        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("RevokeMsg",th);
        }

    }

    public static Object GetMessageByTimeSeq(String uin,int istroop,long msgseq)
    {
        try{
            if(MHookEnvironment.AppInterface==null)return null;
            Object MessageFacade = MMethod.CallMethod(MHookEnvironment.AppInterface, MHookEnvironment.AppInterface.getClass(),"getMessageFacade",
                    MClass.loadClass("com.tencent.imcore.message.QQMessageFacade"),new Class[0]
            );
            Object obj = MMethod.CallMethod(MessageFacade,"c", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                    String.class,int.class,long.class
            },uin,istroop,msgseq);
            return obj;
        }
        catch (Exception ex)
        {
            MLogCat.Print_Error("GetRecordError",ex);
            return null;
        }


    }
}
