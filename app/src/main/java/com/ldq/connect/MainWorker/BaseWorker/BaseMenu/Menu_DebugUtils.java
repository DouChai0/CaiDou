package com.ldq.connect.MainWorker.BaseWorker.BaseMenu;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.GlobalConfig;
import com.ldq.connect.PathConfigSet;
import com.ldq.connect.QQUtils.FormItem;
import com.ldq.connect.QQUtils.QQTools;
import com.ldq.connect.ServerTool.UserStatus;


public class Menu_DebugUtils {
    public static void ShowDebugDialog(){
        Context context = Utils.GetThreadActivity();
        @SuppressLint("ResourceType") Dialog dialog = new Dialog(context,3);
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackgroundColor(Color.WHITE);
        dialog.setContentView(l);

        l.addView(FormItem.AddCheckItem(context, "关闭阻止闪退(重启QQ生效)", (buttonView, isChecked) -> GlobalConfig.Put_Boolean("关闭阻止闪退",isChecked),GlobalConfig.Get_Boolean("关闭阻止闪退",false)));
        l.addView(FormItem.AddListItem(context,"获取用户状态",v->{
            new AlertDialog.Builder(context,3)
                    .setTitle("STATUS INFO")
                    .setMessage(UserStatus.GetStatus())
                    .show();
        }));

        l.addView(FormItem.AddListItem(context, "设置存储路径", v -> PathConfigSet.ShowConfigPathSet(Utils.GetThreadActivity(),true)));
        l.addView(FormItem.AddListItem(context,"查看云黑列表",v->{
            String BlackList = HttpUtils.getHtmlResourceByUrl("https://cdn.haonb.cc/static/s.txt");
            String[] UinCut = BlackList.split("<>");
            new AlertDialog.Builder(context,3)
                    .setTitle("点击查看原因")
                    .setItems(UinCut, (dialog1, which) -> {
                        QQTools.OpenLink("https://cdn.haonb.cc/yun/"+UinCut[which]+"/");
                    }).show();
        }));

        l.addView(FormItem.AddListItem(context,"查询适配版本信息",v->{
            String LastVersion = HttpUtils.getHtmlResourceByUrl("https://api.haonb.cc/getUpdateList");
            new AlertDialog.Builder(context,3)
                    .setTitle("适配版本信息")
                    .setMessage(LastVersion)
                    .setNegativeButton("确定", (dialog12, which) -> {

                    }).show();
        }));

        l.addView(FormItem.AddListItem(context,"查询用户信息",v->{
            EditText ed = new EditText(context);
            new AlertDialog.Builder(context,3)
                    .setTitle("请输入用户账号")
                    .setView(ed)
                    .setNegativeButton("确定", (dialog13, which) -> {
                        String Uin = ed.getText().toString();
                        String UserInfo = HttpUtils.getHtmlResourceByUrl("https://api.haonb.cc/getUinInfo?uin="+Uin);
                        new AlertDialog.Builder(context,3)
                                .setTitle("用户信息")
                                .setMessage(UserInfo)
                                .setNegativeButton("确定", (dialog12, which22) -> {

                                }).show();
                    }).show();
        }));
        l.addView(FormItem.AddListItem(context,"闪退测试",v-> {
            new Thread(()->{throw new RuntimeException("线程闪退测试");}).start();
            throw new RuntimeException("闪退测试");

        }));

        dialog.show();
    }
}
