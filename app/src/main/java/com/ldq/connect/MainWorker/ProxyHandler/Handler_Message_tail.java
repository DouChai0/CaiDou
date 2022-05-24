package com.ldq.connect.MainWorker.ProxyHandler;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.JavaPlugin.JavaPluginUtils;
import com.ldq.connect.QQUtils.GuildUtils;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class Handler_Message_tail {
    @SuppressLint("ResourceType")
    public static void ShowSet(Context context){
        try{
            RelativeLayout mRoot = new RelativeLayout(context);
            EditText ed = MClass.CallConstrutor(MClass.loadClass("com.tencent.widget.XEditTextEx"),new Class[]{Context.class},context);
            ed.setEditableFactory(MField.GetField(null,MClass.loadClass("com.tencent.mobileqq.text.QQTextBuilder"),"EMOCTATION_FACORY",1));
            ed.setId(2333);
            ed.setHint("支持变量{MSG},如果没加则会把消息加在最后面\n" +
                    "变量{TIME},当前时间,格式yyyy-MM-dd HH:mm:ss");
            ed.setGravity(Gravity.NO_GRAVITY);
            ed.setSingleLine(false);
            ed.setText(MConfig.Get_String("Plugin","Message_tail","Rule"));
            mRoot.addView(ed,new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));


            LinearLayout toolBar = new LinearLayout(context);

            Button btnSelectBack = new Button(context);
            btnSelectBack.setText("反选");


            toolBar.addView(btnSelectBack);
            toolBar.setId(6666);
            RelativeLayout.LayoutParams toolParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            toolParam.addRule(RelativeLayout.BELOW,2333);
            mRoot.addView(toolBar,toolParam);

            ScrollView sc = new ScrollView(context);
            LinearLayout troopList = new LinearLayout(context);
            troopList.setOrientation(LinearLayout.VERTICAL);
            sc.addView(troopList);
            RelativeLayout.LayoutParams listParam = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            listParam.addRule(RelativeLayout.BELOW,6666);
            listParam.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,1);
            mRoot.addView(sc,listParam);

            List<String> OPENTroopUin = MConfig.Get_List("Plugin","Message_tail","Open");

            ArrayList<CheckBox> chBoxs = new ArrayList<>();

            btnSelectBack.setOnClickListener(v->{
                for (CheckBox chBox : chBoxs){
                    chBox.setChecked(!chBox.isChecked());
                }
            });
            //添加私聊选项
            CheckBox chPrivate = new CheckBox(context);
            chPrivate.setText("所有私聊(-1)");
            chPrivate.setTag("-1");
            chPrivate.setTextColor(Color.BLACK);

            if (OPENTroopUin.contains("-1"))chPrivate.setChecked(true);
            chBoxs.add(chPrivate);
            troopList.addView(chPrivate);


            List<JavaPluginUtils.GroupInfo> infos = JavaPluginUtils.GetGroupInfo();
            for (JavaPluginUtils.GroupInfo info : infos){
                CheckBox box = new CheckBox(context);
                if (OPENTroopUin.contains(info.GroupUin))box.setChecked(true);
                box.setTextColor(Color.BLACK);
                box.setText(info.GroupName+"("+info.GroupUin+")");
                box.setTag(info.GroupUin);
                troopList.addView(box);
                chBoxs.add(box);
            }

            List<GuildUtils.GuildInfo> guildinfos = GuildUtils.GetGuildList();
            for (GuildUtils.GuildInfo guild : guildinfos){
                CheckBox box = new CheckBox(context);
                box.setText(guild.GuildName+"("+guild.GuildID+")");
                if (OPENTroopUin.contains(guild.GuildID))box.setChecked(true);
                box.setTag(guild.GuildID);
                box.setTextColor(Color.BLACK);
                troopList.addView(box);
                chBoxs.add(box);
            }


            new AlertDialog.Builder(context,3)
                    .setTitle("设置消息小尾巴")
                    .setView(mRoot)
                    .setNegativeButton("保存", (dialog, which) -> {
                        ArrayList<String> NewList = new ArrayList<>();
                        for (CheckBox chBox : chBoxs){
                            if (chBox.isChecked()){
                                NewList.add((String) chBox.getTag());
                            }
                        }
                        MConfig.Put_List("Plugin","Message_tail","Open",NewList);
                        MConfig.Put_String("Plugin","Message_tail","Rule",ed.getText().toString());
                    }).show();
        }catch (Exception e){
            Utils.ShowToast("无法创建布局:\n"+e);
        }
    }
    public static void Callback(Object Message)
    {
        List<String> TroopConfig = MConfig.Get_List("Plugin","Message_tail","Open");
        if (TroopConfig.size() > 0){
            if (Message.getClass().getName().contains("MessageForText") ||Message.getClass().getName().contains("MessageForLongTextMsg") ||
                    Message.getClass().getName().contains("MessageForReplyText")){
                try{
                    int Type = MField.GetField(Message,"istroop",int.class);
                    String FriendUin = MField.GetField(Message, "frienduin",String.class);

                    if ((Type == 1 && TroopConfig.contains(FriendUin)) || ((Type != 1 && Type != 10014) && TroopConfig.contains("-1"))){
                        String msg = MField.GetField(Message,"msg",String.class);

                        String Rule = MConfig.Get_String("Plugin","Message_tail","Rule");
                        if (!Rule.contains("{MSG}")){
                            msg = Rule + msg;
                        }else {
                            msg = Rule.replace("{MSG}",msg);
                        }
                        msg = msg.replace("{TIME}",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                        MField.SetField(Message,"msg",msg);
                        if (Message.getClass().getName().contains("MessageForReplyText")){
                            MField.SetField(Message,Message.getClass(),"sb",msg,CharSequence.class);
                        }

                        MMethod.CallMethod(Message,"prewrite",void.class,new Class[0]);
                        MMethod.CallMethod(Message,"doParse",void.class,new Class[0]);
                    }else if (Type == 10014){
                        JSONObject ChannelData = MField.GetField(Message,"mExJsonObject");
                        String GUILD_ID = ChannelData.optString("GUILD_ID");
                        if (TroopConfig.contains(GUILD_ID)){
                            String msg = MField.GetField(Message,"msg",String.class);

                            String Rule = MConfig.Get_String("Plugin","Message_tail","Rule");
                            if (!Rule.contains("{MSG}")){
                                msg = Rule + msg;
                            }else {
                                msg = Rule.replace("{MSG}",msg);
                            }
                            msg = msg.replace("{TIME}",new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date()));
                            MField.SetField(Message,"msg",msg);
                            MMethod.CallMethod(Message,"prewrite",void.class,new Class[0]);
                        }
                    }

                }catch (Exception e){
                    MLogCat.Print_Error("Message_Tail",e);
                }


            }
        }
    }

}
