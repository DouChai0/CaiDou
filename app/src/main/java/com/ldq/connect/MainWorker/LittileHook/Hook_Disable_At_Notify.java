package com.ldq.connect.MainWorker.LittileHook;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.JavaPlugin.JavaPluginUtils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.GuildUtils;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Disable_At_Notify {
    public static void ShowAtSettings()
    {
        List<String> mDisabledAtMe = MConfig.Get_List("Main","DisableNotify","DisableAtMe");
        List<String> mDisabledAtAll = MConfig.Get_List("Main","DisableNotify","DisableAtAll");

        Activity act = Utils.GetThreadActivity();
        new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("设置需要屏蔽的群聊")
                .setNeutralButton("设置屏蔽艾特我的群聊", (dialog, which) -> {
                    try{
                        Button btnNotSelect = new Button(act);
                        btnNotSelect.setText("反选");


                        ScrollView sc = new ScrollView(act);
                        LinearLayout Container = new LinearLayout(act);
                        sc.addView(Container);
                        Container.addView(btnNotSelect);

                        Container.setOrientation(LinearLayout.VERTICAL);

                        ArrayList<CheckBox> sCheckBoxs = new ArrayList<>();
                        ArrayList<JavaPluginUtils.GroupInfo> infos = JavaPluginUtils.GetGroupInfo();
                        for(JavaPluginUtils.GroupInfo sInfo : infos)
                        {
                            CheckBox ch = new CheckBox(act);
                            ch.setChecked(mDisabledAtMe.contains(sInfo.GroupUin));
                            ch.setText(sInfo.GroupName+"("+sInfo.GroupUin+")");
                            ch.setTag(sInfo.GroupUin);
                            ch.setTextColor(Color.BLACK);
                            sCheckBoxs.add(ch);
                            Container.addView(ch);
                        }

                        ArrayList<GuildUtils.GuildInfo> GuildList = GuildUtils.GetGuildList();
                        for(GuildUtils.GuildInfo info : GuildList){


                            CheckBox ch = new CheckBox(act);
                            ch.setChecked(mDisabledAtMe.contains(info.GuildID));
                            ch.setText(info.GuildName+"("+info.GuildID+")");
                            ch.setTag(info.GuildID);
                            ch.setTextColor(Color.BLACK);
                            sCheckBoxs.add(ch);
                            Container.addView(ch);
                        }

                        btnNotSelect.setOnClickListener(v->{
                            for(CheckBox ch : sCheckBoxs)
                            {
                                ch.setChecked(!ch.isChecked());
                            }
                        });

                        new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT)
                                .setTitle("设置屏蔽艾特我的群聊")
                                .setView(sc)
                                .setNeutralButton("确定", (dialog1, which1) -> {
                                    ArrayList SelectTroop = new ArrayList<>();
                                    for(CheckBox ch : sCheckBoxs)
                                    {
                                        if(ch.isChecked())
                                        {
                                            SelectTroop.add((String) ch.getTag());
                                        }
                                    }
                                    MConfig.Put_List("Main","DisableNotify","DisableAtMe",SelectTroop);
                                    //TroopConfig.SetList("BanAtMe",SelectTroop);
                                }).show();
                    }catch ( Exception ex)
                    {
                        Utils.ShowToast("发生错误:"+ex);
                    }

                }).setPositiveButton("设置屏蔽艾特全体的群聊", (dialog, which) -> {
            try{
                Button btnNotSelect = new Button(act);
                btnNotSelect.setText("反选");


                ScrollView sc = new ScrollView(act);
                LinearLayout Container = new LinearLayout(act);
                sc.addView(Container);
                Container.addView(btnNotSelect);

                Container.setOrientation(LinearLayout.VERTICAL);

                ArrayList<CheckBox> sCheckBoxs = new ArrayList<>();
                ArrayList<JavaPluginUtils.GroupInfo> infos = JavaPluginUtils.GetGroupInfo();


                for(JavaPluginUtils.GroupInfo sInfo : infos)
                {
                    CheckBox ch = new CheckBox(act);
                    ch.setChecked(mDisabledAtAll.contains(sInfo.GroupUin));
                    ch.setText(sInfo.GroupName+"("+sInfo.GroupUin+")");
                    ch.setTag(sInfo.GroupUin);
                    ch.setTextColor(Color.BLACK);
                    sCheckBoxs.add(ch);
                    Container.addView(ch);
                }

                ArrayList<GuildUtils.GuildInfo> GuildList = GuildUtils.GetGuildList();
                for(GuildUtils.GuildInfo info : GuildList){


                    CheckBox ch = new CheckBox(act);
                    ch.setChecked(mDisabledAtAll.contains(info.GuildID));
                    ch.setText(info.GuildName+"("+info.GuildID+")");
                    ch.setTag(info.GuildID);
                    ch.setTextColor(Color.BLACK);
                    sCheckBoxs.add(ch);
                    Container.addView(ch);
                }

                btnNotSelect.setOnClickListener(v->{
                    for(CheckBox ch : sCheckBoxs)
                    {
                        ch.setChecked(!ch.isChecked());
                    }
                });

                new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("设置屏蔽全体的群聊")
                        .setView(sc)
                        .setNeutralButton("确定", (dialog1, which1) -> {
                            ArrayList SelectTroop = new ArrayList<>();
                            for(CheckBox ch : sCheckBoxs)
                            {
                                if(ch.isChecked())
                                {
                                    SelectTroop.add((String) ch.getTag());
                                }
                            }
                            MConfig.Put_List("Main","DisableNotify","DisableAtAll",SelectTroop);
                        }).show();
            }catch ( Exception ex)
            {
                Utils.ShowToast("发生错误:"+ex);
            }
        }).show();

    }

    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"), "notifyMessageReceived",
                MClass.loadClass("com.tencent.imcore.message.Message"), boolean.class,boolean.class, new XC_MethodHook(100) {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Object Message = param.args[0];
                        int isTroop = MField.GetField(Message,"istroop",int.class);
                        String FriendUin = MField.GetField(Message,"senderuin");

                        int NeedAdd = MHookEnvironment.MVersionInt > 7540 ? 1 : 0;




                        if(isTroop==10014){
                            boolean NeedShow = MMethod.CallMethod(Message,"needNotification",boolean.class,new Class[0]);
                            if(NeedShow){
                                int sr = MField.GetField(Message,"bizType",int.class);
                                //String GuildID = MField.GetField(Message,"frienduin",String.class);

                                JSONObject ChannelData = MField.GetField(Message,"mExJsonObject");
                                String GuildID__ = ChannelData.optString("GUILD_ID");;
                                if(sr==22 + NeedAdd || sr==24+NeedAdd)
                                {
                                    List<String> mDisabledAtMe = MConfig.Get_List("Main","DisableNotify","DisableAtMe");
                                    if(mDisabledAtMe.contains(GuildID__))param.setResult(null);
                                }else if(sr==13+NeedAdd || sr==12+NeedAdd ||sr == 5)
                                {
                                    if (FriendUin.equals("3108909995"))param.setResult(null);
                                    List<String> mDisabledAtAll = MConfig.Get_List("Main","DisableNotify","DisableAtAll");
                                    if(mDisabledAtAll.contains(GuildID__))param.setResult(null);

                                }
                            }
                        }

                        if(isTroop==1)
                        {
                            boolean NeedShow = MMethod.CallMethod(Message,"needNotification",boolean.class,new Class[0]);
                            if(NeedShow)
                            {
                                int sr = MField.GetField(Message,"bizType",int.class);
                                String TroopUin = MField.GetField(Message,"frienduin",String.class);
                                if(sr==22+NeedAdd || sr==24+NeedAdd)
                                {
                                    List<String> mDisabledAtMe = MConfig.Get_List("Main","DisableNotify","DisableAtMe");
                                    if(mDisabledAtMe.contains(TroopUin))param.setResult(null);


                                }else if(sr==13+NeedAdd || sr==12+NeedAdd ||sr == 5)
                                {
                                    if (FriendUin.equals("3108909995"))param.setResult(null);
                                    List<String> mDisabledAtAll = MConfig.Get_List("Main","DisableNotify","DisableAtAll");
                                    if(mDisabledAtAll.contains(TroopUin))param.setResult(null);
                                }
                            }
                        }
                    }
                }
        );
    }
}
