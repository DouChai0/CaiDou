package com.ldq.connect.JavaPlugin;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.QQUtils.GuildUtils;

import java.io.File;
import java.util.ArrayList;

public class JavaSourceView
{
    public static View GetPluginView(Context context, String Line1,String BtnText, View.OnClickListener onLoad, Object Tag)
    {
        LinearLayout llayout = new LinearLayout(context);
        TextView tView = new TextView(context);
        tView.setText(Line1);
        tView.setTextColor(Color.BLACK);
        tView.setTextSize(18);
        llayout.addView(tView,new LinearLayout.LayoutParams(Utils.dip2px(context,180), Utils.dip2px(context,40)));
        Button btn = new Button(context);
        btn.setText(BtnText);
        btn.setOnClickListener(onLoad);
        btn.setTag(Tag);
        llayout.addView(btn,new LinearLayout.LayoutParams(Utils.dip2px(context,60), Utils.dip2px(context,40)));
        llayout.setLayoutParams(new LinearLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        return llayout;
    }
    public static View GetPluginView(Context context, String Line1, String BtnText, View.OnClickListener onLoad, Object Tag, View.OnClickListener TextViewListener
    )
    {
        return GetPluginView(context,Line1,BtnText,onLoad,Tag,TextViewListener,null);
    }
    @SuppressLint("ResourceType")
    public static View GetPluginView(Context context, String Line1, String BtnText, View.OnClickListener onLoad, Object Tag, View.OnClickListener TextViewListener,
                                     View.OnLongClickListener mLongClick
    )
    {
        RelativeLayout RLayout = new RelativeLayout(context);

        Button btn = new Button(context);
        btn.setText(BtnText);
        btn.setTextSize(20);
        btn.setOnClickListener(onLoad);
        btn.setTag(Tag);
        btn.setId(888888);
        RelativeLayout.LayoutParams mParam0 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mParam0.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,1);
        RLayout.addView(btn,mParam0);




        TextView tView = new TextView(context);
        tView.setText(Line1);
        tView.setTextColor(Color.BLACK);
        tView.setTextSize(20);
        tView.setId(456456);
        tView.setOnClickListener(TextViewListener);
        tView.setOnLongClickListener(mLongClick);
        RelativeLayout.LayoutParams mParam1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mParam1.addRule(RelativeLayout.ALIGN_PARENT_LEFT,1);
        mParam1.addRule(RelativeLayout.LEFT_OF,888888);

        RLayout.addView(tView,mParam1);



        return RLayout;
    }

    @SuppressLint("ResourceType")
    public static View GetPluginView_RED(Context context, String Line1, String BtnText, View.OnClickListener onLoad, Object Tag, View.OnClickListener TextViewListener,
                                     View.OnLongClickListener mLongClick
    )
    {
        RelativeLayout RLayout = new RelativeLayout(context);

        Button btn = new Button(context);
        btn.setText(BtnText);
        btn.setTextSize(20);
        btn.setOnClickListener(onLoad);
        btn.setTag(Tag);
        btn.setId(8888889);
        RelativeLayout.LayoutParams mParam0 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mParam0.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,1);
        RLayout.addView(btn,mParam0);




        TextView tView = new TextView(context);
        tView.setText(Line1);
        tView.setTextColor(Color.RED);
        tView.setTextSize(20);
        tView.setId(456456);
        tView.setOnClickListener(TextViewListener);
        tView.setOnLongClickListener(mLongClick);
        RelativeLayout.LayoutParams mParam1 = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        mParam1.addRule(RelativeLayout.ALIGN_PARENT_LEFT,1);
        mParam1.addRule(RelativeLayout.LEFT_OF,8888889);

        RLayout.addView(tView,mParam1);



        return RLayout;
    }
    public static void ShowPluginInfoView(String mPath,Context context)
    {
        try{
            File mfile = new File(mPath);
            if(!mfile.exists())return;
            AlertDialog dialog = new AlertDialog.Builder(context,3).create();
            dialog.setTitle("加载设置");
            LinearLayout l = new LinearLayout(context);
            l.setOrientation(LinearLayout.VERTICAL);


            TextView t = new TextView(context);
            t.setText("这里的设置都是基于完整路径保存的,修改了脚本名字后就需要重新设置了");
            l.addView(t);


            CheckBox check = new CheckBox(context);
            check.setTextColor(Color.BLACK);
            check.setText("启动QQ自动加载");
            l.addView(check);
            ArrayList<String> LoadPaths = (ArrayList<String>) MConfig.Get_List("Plugin","Plugin","AutoLoad");
            if(LoadPaths.contains(mfile.getAbsolutePath()))
            {
                check.setChecked(true);
            }
            check.setOnCheckedChangeListener((buttonView, isChecked) -> {
                if(isChecked) {
                    ArrayList<String> mList = (ArrayList<String>) MConfig.Get_List("Plugin","Plugin","AutoLoad");
                    if(!mList.contains(mfile.getAbsolutePath())) {
                        mList.add(mfile.getAbsolutePath());
                        MConfig.Put_List("Plugin", "Plugin", "AutoLoad", mList);
                    }
                }
                else
                {
                    ArrayList<String> mList = (ArrayList<String>) MConfig.Get_List("Plugin","Plugin","AutoLoad");
                    if(mList.contains(mfile.getAbsolutePath()))
                    {
                        mList.remove(mfile.getAbsolutePath());
                        MConfig.Put_List("Plugin", "Plugin", "AutoLoad", mList);
                    }
                }
            });

            CheckBox checked_List = new CheckBox(context);
            checked_List.setText("勾选使用白名单模式(不选为黑名单模式)");
            checked_List.setTextColor(Color.BLACK);
            checked_List.setChecked(MConfig.Get_Boolean("Plugin","WhileMode",mfile.getName(),false));
            checked_List.setOnCheckedChangeListener((buttonView, isChecked) -> MConfig.Put_Boolean("Plugin","WhileMode",mfile.getName(),isChecked));


            l.addView(checked_List);




            Button mSetBtn = (Button) AddWhileAndBlackList(mfile,context);
            l.addView(mSetBtn);


            dialog.setView(l);
            dialog.show();
            return;

        }
        catch (Exception ex)
        {
            Utils.ShowToast("显示详情失败");
        }
    }
    public static View AddWhileAndBlackList(File mFileList,Context mContext)
    {
        Button btnCheck = new Button(mContext);
        btnCheck.setText("设置群聊黑白名单");
        btnCheck.setTextColor(Color.BLACK);
        btnCheck.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder a = new AlertDialog.Builder(mContext,AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("设置黑白名单");


                ArrayList<String> mSet = (ArrayList<String>) MConfig.Get_List("Plugin","TroopBWList",mFileList.getName());

                ArrayList<String> SaveInfo = new ArrayList<>();
                ArrayList<String> ShowInfo = new ArrayList<>();
                ArrayList<String> CheckItemName = new ArrayList<>();



                ArrayList<JavaPluginUtils.GroupInfo> gInfo = JavaPluginUtils.GetGroupInfo();
                for(int i=0;i<gInfo.size();i++)
                {
                    ShowInfo.add(gInfo.get(i).GroupName+"("+gInfo.get(i).GroupUin+")");
                    if(mSet.contains(gInfo.get(i).GroupUin)) {
                       CheckItemName.add(gInfo.get(i).GroupName+"("+gInfo.get(i).GroupUin+")");
                    }
                    SaveInfo.add(gInfo.get(i).GroupUin);
                }


                ArrayList<GuildUtils.GuildInfo> Guilds = GuildUtils.GetGuildList();
                for(GuildUtils.GuildInfo GuildInfo : Guilds){
                    String GuildName = GuildInfo.GuildName;
                    String GuildUin = GuildInfo.GuildID;
                    ArrayList<GuildUtils.ChannelInfo> channels = GuildUtils.GetChannelInfo(GuildUin);
                    for(GuildUtils.ChannelInfo channelInfo : channels){
                        String ChannelName = channelInfo.ChannelName;
                        String ChannelID = GuildUin+"&"+channelInfo.ChannelUin;
                        ShowInfo.add(GuildName+"->"+ChannelName + "("+ChannelID+")");
                        if(mSet.contains(ChannelID)){
                            CheckItemName.add(GuildName+"->"+ChannelName + "("+ChannelID+")");
                        }
                        SaveInfo.add(ChannelID);

                    }
                }
                String[] showList = ShowInfo.toArray(new String[0]);
                boolean[] checks = new boolean[showList.length];
                for(int i =0;i<showList.length;i++){
                    if(CheckItemName.contains(showList[i])){
                        checks[i] =true;
                    }
                }


                a.setMultiChoiceItems(showList, checks, (dialog, which, isChecked) -> {

                });


                a.setPositiveButton("保存", (dialog, which) -> {

                    ArrayList<String> newList = new ArrayList<>();
                    for(int i=0;i<checks.length;i++)
                    {
                        if(checks[i])
                        {
                            newList.add(SaveInfo.get(i));
                        }
                    }
                    MConfig.Put_List("Plugin","TroopBWList",mFileList.getName(),newList);
                    Utils.ShowToast("已保存,重新加载脚本生效");
                });
                a.show();

            }
        });

        return btnCheck;
    }
}
