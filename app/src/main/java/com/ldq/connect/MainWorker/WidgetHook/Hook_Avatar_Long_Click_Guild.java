package com.ldq.connect.MainWorker.WidgetHook;

import android.app.Activity;
import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.JavaPlugin.JavaPluginMethod;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQGuild_Manager;
import com.ldq.connect.QQUtils.QQGuild_Utils;
import com.ldq.connect.Tools.MyTimePicker;

import org.json.JSONObject;

import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Avatar_Long_Click_Guild {
    public static void Start(){
        try{
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.guild.chatpie.msgviewbuild.builder.BaseGuildMsgViewBuild"), "m", View.class, new XC_MethodHook() {

                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    //MLogCat.Print_Debug("aaaaaaa");
                    if (MConfig.Get_Boolean("Main","MainSwitch","便捷菜单",false)){
                        param.setResult(null);

                        ArrayList<String> PList = new ArrayList<>();
                        PList.add("艾特");

                        Object mHolder = MMethod.CallMethod(param.thisObject,"i",Object.class,new Class[]{View.class},param.args[0]);
                        Object mChatMsg = MField.GetField(mHolder,"q",MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"));
                        String TinyID = MField.GetField(mChatMsg,"senderuin",String.class);
                        JSONObject ChannelData = MField.GetField(mChatMsg,"mExJsonObject");
                        String GuildIO = ChannelData.optString("GUILD_ID");


                        PList.add("复制TinyID号");
                        PList.add("复制头像链接");
                        if(QQGuild_Utils.Get_Guild_Creator(GuildIO).equals(BaseInfo.GetCurrentTinyID()))
                        {
                            PList.add("尝试探测QQ号");

                            PList.add("撤回消息");
                            PList.add("禁言");
                            PList.add("踢出");
                        }


                        Activity context = Utils.GetThreadActivity();
                        new AlertDialog.Builder(context,3)
                                .setTitle("选择操作项目")
                                .setItems(PList.toArray(new String[0]), (dialog, which) -> {
                                    try{
                                        switch (PList.get(which)){
                                            case "艾特":{
                                                MMethod.CallMethod(param.thisObject,"n",void.class,new Class[]{View.class},param.args[0]);
                                                break;
                                            }
                                            case "尝试探测QQ号":{
                                                new Thread(()->{
                                                    Object Holder = null;
                                                    try {
                                                        String CheckResult = JavaPluginMethod.Try_Get_User_True_Uin(GuildIO,TinyID);
                                                        String CheckToast;
                                                        if(TextUtils.isEmpty(CheckResult)){
                                                            CheckToast = "未能探测到该用户的QQ号";
                                                        }else
                                                        {
                                                            CheckToast = "该用户的QQ号探测结果为:"+CheckResult;
                                                        }
                                                        new Handler(Looper.getMainLooper()).post(()->{
                                                            new AlertDialog.Builder(context,3)
                                                                    .setTitle("探测结果")
                                                                    .setMessage("探测TinyID:"+TinyID+"\n"+CheckToast)
                                                                    .show();
                                                        });


                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                }).start();

                                                break;
                                            }
                                            case "复制TinyID号":{
                                                Utils.SetTextClipboard(TinyID);
                                                Utils.ShowToast("TinyID已复制到剪贴板");
                                                break;
                                            }
                                            case "复制头像链接":{
                                                String Link = QQGuild_Utils.Get_Face_Url_(TinyID,GuildIO);
                                                Utils.SetTextClipboard(Link);
                                                Utils.ShowToast("头像链接已复制到剪贴板");
                                                break;
                                            }
                                            case "撤回消息":{
                                                Object Holder = MMethod.CallMethod(param.thisObject,"i",Object.class,new Class[]{View.class},param.args[0]);
                                                Object ChatMessage = MField.GetField(Holder,"q",MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"));
                                                QQGuild_Manager.Guild_Revoke(ChatMessage);
                                                break;
                                            } case "禁言":{


                                                AlertDialog mAlert = new AlertDialog.Builder(context, 3).create();
                                                mAlert.setTitle("禁言时间");
                                                LinearLayout mLL = new LinearLayout(context);
                                                mAlert.setView(mLL);
                                                mLL.setOrientation(LinearLayout.VERTICAL);

                                                MyTimePicker mPicker = new MyTimePicker(context);
                                                mLL.addView(mPicker);

                                                mAlert.setButton(AlertDialog.BUTTON_POSITIVE, "确定", (dialog1, which1) -> {
                                                    try {
                                                        QQGuild_Manager.Guild_Forbidden(GuildIO,TinyID,mPicker.GetSecond());
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }

                                                });
                                                mAlert.show();
                                                break;
                                            } case "踢出":{


                                                AlertDialog mAlert = new AlertDialog.Builder(context, 3).create();
                                                mAlert.setButton(AlertDialog.BUTTON_POSITIVE, "取消", (dialog1, which1) -> {

                                                });
                                                mAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "踢出", (dialog1, which1) -> {
                                                    QQGuild_Manager.Guild_Kick_User(GuildIO,TinyID,false);
                                                });
                                                mAlert.setButton(AlertDialog.BUTTON_NEUTRAL, "踢出并拒绝加入", (dialog1, which1) -> {
                                                    QQGuild_Manager.Guild_Kick_User(GuildIO,TinyID,true);
                                                });
                                                mAlert.setTitle("你真的要踢出该用户吗?");
                                                mAlert.show();
                                                break;
                                            }
                                        }
                                    }catch (Exception e){
                                        MLogCat.Print_Error("LongClickItem",e);
                                    }

                                }).show();
                    }
                }
            });
        }catch (Throwable th){
            MLogCat.Print_Error("Hook",th);
        }

    }
}
