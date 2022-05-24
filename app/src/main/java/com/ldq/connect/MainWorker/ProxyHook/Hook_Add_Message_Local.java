package com.ldq.connect.MainWorker.ProxyHook;

import android.text.TextUtils;
import android.util.Log;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.FloatWindow.Init;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.JavaPlugin.JavaPlugin;
import com.ldq.connect.JavaPlugin.JavaPluginMethod;
import com.ldq.connect.JavaPlugin.JavaPluginUtils;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Message_split;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Message_tail;
import com.ldq.connect.QQUtils.BaseCall;
import com.ldq.connect.QQUtils.QQMessage_Builder;
import com.ldq.connect.QQUtils.QQMessage_Transform;
import com.ldq.connect.QQUtils.TroopManager;

import java.lang.reflect.Method;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_Add_Message_Local
{
    public static void Start() {
        try{
            Method InvokeMethod = MMethod.FindMethod("com.tencent.imcore.message.BaseQQMessageFacade","a",void.class,new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                    MClass.loadClass("com.tencent.mobileqq.app.BusinessObserver")
            });
            XposedBridge.hookMethod(InvokeMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    Object AddMsg = param.args[0];
                    String Name = AddMsg.getClass().getName();
                    int istroop = MField.GetField(AddMsg,"istroop",int.class);
                    Handler_Message_tail.Callback(AddMsg);
                    Handler_Message_split.Callback(AddMsg);

                    if(MConfig.Get_Boolean("Main","MainSwitch","回复带图",false)) {
                        if(Name.contains("MessageForReplyText") && (istroop==1 || istroop==0))
                        {
                            //取出原始消息内容
                            String Text = MField.GetField(AddMsg,"msg",String.class);
                            if(TextUtils.isEmpty(Text))
                            {
                                Text = MField.GetField(AddMsg,"sb",CharSequence.class)+"";
                            }
                            //如果包含特定的图片代码则会解析为Mix消息
                            if(Text.contains("[PicUrl=") && Text.contains("]"))
                            {
                                String GroupUin = "";
                                String UserUin = "";
                                if(istroop==1)
                                {
                                    GroupUin = MField.GetField(AddMsg,"frienduin",String.class);
                                    UserUin = "";
                                }
                                else
                                {
                                    GroupUin = "";
                                    UserUin = MField.GetField(AddMsg,"frienduin",String.class);
                                }


                                MField.SetField(AddMsg,"msg"," ");
                                MMethod.CallMethod(AddMsg,"prewrite",void.class,new Class[0]);

                                JavaPluginMethod.mMessageInfoList[] mWillSendInfo = JavaPluginUtils.InitMessageData(Text);
                                ArrayList RecordList = new ArrayList();

                                RecordList.add(QQMessage_Builder.CopyReplyMessage(AddMsg));

                                ArrayList AtInfo = new ArrayList();
                                String TextMsg = "";
                                int length = 0;
                                for(JavaPluginMethod.mMessageInfoList mElement : mWillSendInfo)
                                {
                                    if(mElement.MessageType==1)
                                    {
                                        Object mTextMsgData = QQMessage_Builder.Build_Text(GroupUin,mElement.Message);
                                        length+=mElement.Message.length();
                                        TextMsg = TextMsg + mElement.Message;
                                        //MLogCat.Print_Debug(""+mTextMsgData);
                                        RecordList.add(mTextMsgData);
                                    }
                                    else if(mElement.MessageType == 2)
                                    {
                                        Object mPicMsgData = QQMessage_Builder.Build_Pic(QQMessage_Builder.Build_SessionInfo(GroupUin,UserUin), FileUtils.GetPath(mElement.Message));
                                        //MLogCat.Print_Debug(""+mPicMsgData);
                                        RecordList.add(mPicMsgData);
                                    }
                                    else if (mElement.MessageType == 3)
                                    {
                                        String AtText = "@"+ TroopManager.GetMemberName(GroupUin,mElement.Message)+" ";
                                        if(mElement.Message.equals("0"))
                                        {
                                            AtText = "@全体成员 ";
                                        }

                                        TextMsg = TextMsg + AtText;
                                        Object mTextMsg = QQMessage_Builder.Build_Text(GroupUin,AtText);
                                        AtInfo.add(QQMessage_Builder.Build_AtInfo(mElement.Message,AtText, (short) length));
                                        length+=AtText.length();
                                        RecordList.add(mTextMsg);
                                    }
                                }


                                //发送消息并返回
                                QQMessage_Transform.Message_Send_Mix(GroupUin,UserUin,RecordList);
                                param.setResult(null);
                                return;
                            }
                        }
                    }

                    if(MConfig.Get_Boolean("Main","MainSwitch","图文消息模式",false)) {
                        if (Name.contains("MessageForText") && (istroop == 0 || istroop == 1)) {
                            String Text = MField.GetField(AddMsg, "msg", String.class);
                            if (Text.contains("[PicUrl") && Text.contains("]")) {
                                String GroupUin = "";
                                String UserUin = "";
                                if (istroop == 1) {
                                    GroupUin = MField.GetField(AddMsg, "frienduin", String.class);
                                    UserUin = "";
                                } else {
                                    GroupUin = "";
                                    UserUin = MField.GetField(AddMsg, "frienduin", String.class);
                                }

                                JavaPluginMethod.mMessageInfoList[] mWillSendInfo = JavaPluginUtils.InitMessageData(Text);
                                ArrayList RecordList = new ArrayList();

                                ArrayList AtInfo = new ArrayList();
                                String TextMsg = "";
                                int length = 0;
                                for (JavaPluginMethod.mMessageInfoList mElement : mWillSendInfo) {
                                    if (mElement.MessageType == 1) {
                                        Object mTextMsgData = QQMessage_Builder.Build_Text(GroupUin, mElement.Message);
                                        length += mElement.Message.length();
                                        TextMsg = TextMsg + mElement.Message;
                                        RecordList.add(mTextMsgData);
                                    } else if (mElement.MessageType == 2) {
                                        Object mPicMsgData = QQMessage_Builder.Build_Pic(QQMessage_Builder.Build_SessionInfo(GroupUin, UserUin), FileUtils.GetPath(mElement.Message));
                                        RecordList.add(mPicMsgData);
                                    } else if (mElement.MessageType == 3) {
                                        String AtText = "@" + TroopManager.GetMemberName(GroupUin, mElement.Message) + " ";
                                        if (mElement.Message.equals("0")) {
                                            AtText = "@全体成员 ";
                                        }

                                        TextMsg = TextMsg + AtText;
                                        Object mTextMsg = QQMessage_Builder.Build_Text(GroupUin, AtText);
                                        AtInfo.add(QQMessage_Builder.Build_AtInfo(mElement.Message, AtText, (short) length));
                                        length += AtText.length();
                                        RecordList.add(mTextMsg);
                                    }
                                }
                                if(RecordList.size()==1 && RecordList.get(0).getClass().getName().contains("MessageForPic"))
                                {
                                    QQMessage_Transform.Message_Send_Pic(GroupUin,UserUin, FileUtils.GetPath(mWillSendInfo[0].Message));
                                }
                                else
                                {
                                    QQMessage_Transform.Message_Send_Mix(GroupUin, UserUin, RecordList);
                                }

                                param.setResult(null);
                                return;
                            }
                        }
                    }
                    if(Name.contains("MessageForFoldMsg")) {
                        param.setResult(null);
                        //MField.SetField(AddMsg,"msg",MessageContent);
                        BaseCall.AddMsg(AddMsg);
                    }
                    if(Name.contains("MessageForText") || Name.contains("MessageForReplyText")) {
                        String MessageContent = MField.GetField(AddMsg,"msg",String.class);
                        String Bake = MessageContent;
                        if(MessageContent.endsWith("\u0000\u0000"))
                        {
                            MessageContent = MessageContent.substring(0,MessageContent.length()-2);
                            MField.SetField(AddMsg,"msg",MessageContent);
                            return;
                        }
                        String frienduin = MField.GetField(AddMsg,"frienduin",String.class);
                        int istroop2 = MField.GetField(AddMsg,"istroop",int.class);
                        MessageContent = JavaPlugin.CallForGetMsg(MessageContent,frienduin,istroop2==1?1:2);

                        if(MessageContent!=null && MessageContent instanceof String)
                        {
                            if(MessageContent.equals("StopToSend"))
                            {
                                param.setResult(null);
                                //MField.SetField(AddMsg,"msg",MessageContent);
                                //BaseCall.AddMsg(AddMsg);
                            }else if (!MessageContent.equals(Bake))
                            {
                                MField.SetField(AddMsg,"msg",MessageContent);
                            }

                        }
                    }
                }
            });
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("AddMsgHook",th);
        }

    }
}
