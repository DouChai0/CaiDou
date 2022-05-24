package com.ldq.connect.MainWorker.BaseWorker.SettingInjectHook;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.Utils;
import com.ldq.connect.MTool.AutoMessage;
import com.ldq.connect.QQUtils.QQSessionUtils;
import com.ldq.connect.ServerTool.UserStatus;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_Guild_Toolbar_Inject {
    public static void Start(){
        try{
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.guild.widget.GuildChannelDetailHeadViewNew"), "a",
                    new XC_MethodHook() {
                        @SuppressLint("ResourceType")
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            View vItem = MField.GetField(param.thisObject,"i", LinearLayout.class);
                            LinearLayout l = (LinearLayout) vItem.getParent();
                            if (l.findViewById(88956) != null)return;
                            Context ctx = l.getContext();
                            int FaceID = ctx.getResources().getIdentifier("guild_setting_icon", "drawable", ctx.getPackageName());

                            RelativeLayout mLayout = new RelativeLayout(ctx);
                            View vPic = new View(ctx);
                            vPic.setBackgroundResource(FaceID);
                            RelativeLayout.LayoutParams ImageViewParam = new RelativeLayout.LayoutParams(Utils.dip2px(ctx,32),Utils.dip2px(ctx,32));
                            ImageViewParam.addRule(RelativeLayout.CENTER_HORIZONTAL,1);
                            mLayout.addView(vPic,ImageViewParam);
                            mLayout.setId(88956);

                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            params.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,1);
                            params.addRule(RelativeLayout.CENTER_HORIZONTAL,1);


                            TextView t = new TextView(ctx);
                            t.setTextColor(Color.BLACK);
                            t.setText("小菜豆");
                            mLayout.addView(t,params);

                            if(UserStatus.CheckIsDonator()){
                                l.addView(mLayout,new LinearLayout.LayoutParams(Utils.dip2px(ctx,48), ViewGroup.LayoutParams.MATCH_PARENT));

                                mLayout.setOnClickListener(v-> ShowChannelInfos());
                            }

                        }
                    }
            );
        }catch (Throwable e){

        }
    }
    public static void ShowChannelInfos(){
        String ChannelID = QQSessionUtils.GetCurrentGuildID();
        String ChannelCharID = QQSessionUtils.GetCurrentChannelCharID();
        String[] Lists = new String[]{"定时消息"};
        Context context = Utils.GetThreadActivity();
        new AlertDialog.Builder(context,3)
                .setItems(Lists, (dialog, which) -> {
                    switch (which){
                        case 0:{
                            AutoMessage.ShowSetAutoMessageDialog(context,ChannelID+"&"+ChannelCharID,true);
                        }
                    }
                }).show();
    }
}
