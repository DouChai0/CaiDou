package com.ldq.connect.MainWorker.ProxyHandler;

import android.app.Dialog;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.StringUtils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.QQUtils.TroopManager;

import org.json.JSONObject;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;

public class Handler_Avoid_Stuck_Msg {
    static class SaveMessage {
        String senderuin;
        String msg;
        Object MessageRecord;
    }
    public static void _caller_boom(XC_MethodHook.MethodHookParam mParam,Object ChatMsg, View v)
    {
        if (MConfig.Get_Boolean("Main","MainSwitch","屏蔽卡屏",true)) {
            try{
                if (ChatMsg.getClass().getName().contains("MessageForLongTextMsg")) {
                    String GroupUin = MField.GetField(ChatMsg, ChatMsg.getClass(),"frienduin", String.class);
                    String UserUin = MField.GetField(ChatMsg, ChatMsg.getClass(),"senderuin", String.class);
                    String msgstr = MField.GetField(ChatMsg, ChatMsg.getClass(),"msg", String.class);
                    if (msgstr.length() > 8192) {
                        TextView vw = new TextView(v.getContext());
                        vw.setTextColor(Color.RED);

                        SaveMessage msg = new SaveMessage();
                        msg.msg = msgstr;
                        msg.senderuin = UserUin;
                        msg.MessageRecord = ChatMsg;
                        String sGer = "";
                        sGer = GroupUin;
                        if(sGer.equals(msg.senderuin))
                        {
                            vw.setText("来自对方的消息已被蒸发,长按查看");
                        }
                        else
                        {
                            vw.setText("来自"+ TroopManager.GetMemberName(sGer,msg.senderuin)+"("+msg.senderuin+")的消息已经蒸发,长按寻找踪迹");
                        }

                        vw.setTag(msg);//使用Tag来存储原始消息的内容
                        vw.setOnLongClickListener(
                                view -> {
                                    SaveMessage str = (SaveMessage) view.getTag();
                                    ScrollView SV = new ScrollView(view.getContext());
                                    Dialog dialog = new Dialog(view.getContext());
                                    EditText et = new EditText(view.getContext());
                                    SV.addView(et);
                                    dialog.setContentView(SV);
                                    dialog.setTitle("消息内容来自:" + str.senderuin);
                                    if(str.msg.length()>20000)
                                    {
                                        et.setText(str.msg.substring(0,20000));
                                    }
                                    else
                                    {
                                        et.setText(str.msg);
                                    }

                                    dialog.show();
                                    return false;
                                }
                        );
                        mParam.setResult(vw);//直接返回自己设置的View
                        //MLog.WriteLine(msgstr.substring(0, 64));
                    }
                    for (Field field : ChatMsg.getClass().getSuperclass().getSuperclass().getSuperclass().getSuperclass().getDeclaredFields()) {
                        //第二类长消息判断
                        if (field.getName().equals("mExJsonObject")) {
                            field.setAccessible(true);
                            JSONObject json = (JSONObject) field.get(ChatMsg);
                            //如果确定是第二类长消息,直接删除View
                            if (json.optString("long_text_recv_state").equals("3")) {
                                TextView vw = new TextView(v.getContext());
                                vw.setTextColor(Color.RED);
                                vw.setText("这条消息由于时间过于长久或者为卡屏已被处理,长按查看具体内容");
                                SaveMessage msg = new SaveMessage();
                                msg.msg = msgstr;
                                msg.senderuin = MField.GetField(ChatMsg,ChatMsg.getClass() ,"senderuin", String.class);
                                vw.setTag(msg);
                                vw.setOnLongClickListener(
                                        view -> {
                                            SaveMessage str = (SaveMessage) view.getTag();
                                            ScrollView SV = new ScrollView(view.getContext());
                                            Dialog dialog = new Dialog(view.getContext());
                                            EditText et = new EditText(view.getContext());
                                            SV.addView(et);
                                            dialog.setContentView(SV);
                                            dialog.setTitle("消息内容来自:" + str.senderuin);
                                            if(str.msg.length()>20000)
                                            {
                                                et.setText(str.msg.substring(0,20000));
                                            }
                                            else
                                            {
                                                et.setText(str.msg);
                                            }
                                            dialog.show();
                                            return false;
                                        }
                                );
                                mParam.setResult(vw);//直接返回自己设置的View
                            }
                        }
                    }
                }
                if (ChatMsg.getClass().getName().contains("MessageForStructing")){
                    Object Structing = MField.GetField(ChatMsg,ChatMsg.getClass(),"structingMsg", MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"));
                    String xml= MMethod.CallMethod(Structing,MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"),"getXml",String.class,new Class[0],new Object[0]);

                    if (StringUtils.Count(xml,"button") > 50){
                        TextView vw = new TextView(v.getContext());
                        vw.setTextColor(Color.RED);
                        String GroupUin = MField.GetField(ChatMsg, ChatMsg.getClass(),"frienduin", String.class);
                        String UserUin = MField.GetField(ChatMsg, ChatMsg.getClass(),"senderuin", String.class);
                        String msgstr = MField.GetField(ChatMsg, ChatMsg.getClass(),"msg", String.class);

                        SaveMessage msg = new SaveMessage();
                        msg.msg = msgstr;
                        msg.senderuin = UserUin;
                        msg.MessageRecord = ChatMsg;
                        String sGer = "";
                        sGer = GroupUin;
                        if(sGer.equals(msg.senderuin))
                        {
                            vw.setText("来自对方的消息已被蒸发,长按查看");
                        }
                        else
                        {
                            vw.setText("来自"+ TroopManager.GetMemberName(sGer,msg.senderuin)+"("+msg.senderuin+")的消息已经蒸发,xml may be malicious.");
                        }

                        mParam.setResult(vw);
                    }

                }
            }
            catch (Throwable e) {
                MLogCat.Print_Error("FuckBoom", e);
            }
        }

    }


}
