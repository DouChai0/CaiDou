package com.ldq.connect.MainWorker.QQRedBagHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.GlobalConfig;
import com.ldq.connect.QQUtils.FormItem;

@SuppressLint("ResourceType")
public class AutoRedPackSetting {
    public static void ShowSet(){
        Activity act = Utils.GetThreadActivity();
        Dialog fullScreen = new Dialog(act,3);
        ScrollView sc = new ScrollView(act);
        sc.setBackgroundColor(Color.WHITE);
        LinearLayout mRoot = new LinearLayout(act);
        sc.addView(mRoot);
        mRoot.setOrientation(LinearLayout.VERTICAL);
        mRoot.setBackgroundColor(Color.WHITE);

        TextView tView = new TextView(act);
        tView.setTextColor(Color.RED);
        tView.setText("本功能不进行维护和任何帮助支持,失效就是失效了,不会进行任何修复");

        mRoot.addView(FormItem.AddCheckItem(act, "自动抢拼手气/普通红包", (buttonView, isChecked) -> GlobalConfig.Put_Boolean("Auto_Lucky_Packet",isChecked), GlobalConfig.Get_Boolean("Auto_Lucky_Packet",false)));
        mRoot.addView(FormItem.AddCheckItem(act, "自动抢拼手气/普通红包(频道)", (buttonView, isChecked) -> GlobalConfig.Put_Boolean("Auto_Lucky_Packet_Channel",isChecked), GlobalConfig.Get_Boolean("Auto_Lucky_Packet_Channel",false)));
        mRoot.addView(FormItem.AddCheckItem(act, "自动抢口令红包", (buttonView, isChecked) -> GlobalConfig.Put_Boolean("Auto_Passwd_Packet",isChecked), GlobalConfig.Get_Boolean("Auto_Passwd_Packet",false)));
        mRoot.addView(FormItem.AddListItem(act,"替换口令关键词",v->{
            EditText ed = new EditText(act);
            ed.setHint("遇到关键词则进行口令替换(正则)");
            ed.setText(GlobalConfig.Get_String("Change_Key_Rule"));
            new AlertDialog.Builder(act,3)
                    .setTitle("替换口令关键词")
                    .setView(ed)
                    .setNegativeButton("确定", (dialog, which) -> {
                        GlobalConfig.Put_String("Change_Key_Rule",ed.getText().toString());
                    }).show();

        }));

        mRoot.addView(FormItem.AddListItem(act,"替换口令内容",v->{
            EditText ed = new EditText(act);
            ed.setHint("设置口令替换发送的内容");
            ed.setText(GlobalConfig.Get_String("Change_Key"));
            new AlertDialog.Builder(act,3)
                    .setTitle("替换口令")
                    .setView(ed)
                    .setNegativeButton("确定", (dialog, which) -> {
                        GlobalConfig.Put_String("Change_Key",ed.getText().toString());
                    }).show();
        }));

        mRoot.addView(FormItem.AddListItem(act,"设置群聊白名单",v->{
            EditText ed = new EditText(act);
            ed.setHint("输入群号,或频道ID(长的那个),可以用|分割");
            ed.setText(GlobalConfig.Get_String("Auto_While_Troop"));
            new AlertDialog.Builder(act,3)
                    .setTitle("设置群聊白名单")
                    .setView(ed)
                    .setNegativeButton("确定 ", (dialog, which) -> {
                        GlobalConfig.Put_String("Auto_While_Troop",ed.getText().toString());
                    }).show();
        }));
        mRoot.addView(FormItem.AddListItem(act,"设置关键词白名单",v->{
            EditText ed = new EditText(act);
            ed.setHint("关键词,使用正则表达式匹配,具体自己发挥");
            ed.setText(GlobalConfig.Get_String("Auto_While_Word"));
            new AlertDialog.Builder(act,3)
                    .setTitle("设置关键词白名单")
                    .setView(ed)
                    .setNegativeButton("确定 ", (dialog, which) -> {
                        GlobalConfig.Put_String("Auto_While_Word",ed.getText().toString());
                    }).show();
        }));

        mRoot.addView(FormItem.AddListItem(act,"设置抢包延迟",v->{
            EditText ed = new EditText(act);
            ed.setHint("单位为秒");
            ed.setText(String.valueOf(GlobalConfig.Get_Long("Auto_Time",0)));
            new AlertDialog.Builder(act,3)
                    .setTitle("设置抢包延迟")
                    .setView(ed)
                    .setNegativeButton("确定 ", (dialog, which) -> {
                        GlobalConfig.Put_Long("Auto_Time",Long.parseLong(ed.getText().toString()));
                    }).show();
        }));
        mRoot.addView(FormItem.AddListItem(act,"设置自动回复",v->{
            EditText ed = new EditText(act);
            ed.setHint("可以使用|分割随机回复");
            ed.setText(GlobalConfig.Get_String("Auto_Reply"));
            new AlertDialog.Builder(act,3)
                    .setTitle("设置自动回复")
                    .setView(ed)
                    .setNegativeButton("确定 ", (dialog, which) -> {
                        GlobalConfig.Put_String("Auto_Reply",ed.getText().toString());
                    }).show();
        }));

        mRoot.addView(FormItem.AddListItem(act,"清除缓存",v->Handler_HB_Opener.SaveSkey("",0,-1)));
        fullScreen.setContentView(sc);
        fullScreen.show();



    }
}
