package com.ldq.connect.MTool.FriendObserve;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.QQUtils.FormItem;

import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class FriendDeleteInfoShow {
    static Executor pool = Executors.newFixedThreadPool(16);
    public static void ShowSet(Context context){
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackgroundColor(Color.WHITE);

        l.addView(FormItem.AddCheckItem(context,"开启通知记录",(a,b)-> MConfig.Put_Boolean("Main","FriendNotify","好友监测通知",b),
              MConfig.Get_Boolean("Main","FriendNotify","好友监测通知",true)));
        l.addView(FormItem.AddListItem(context,"查看删除记录",v->ShowList(context)));


        new AlertDialog.Builder(context,3)
                .setTitle("设置好友删除通知")
                .setView(l).show();
    }
    public static void ShowList(Context mContext){
        ScrollView sc = new ScrollView(mContext);
        LinearLayout l = new LinearLayout(mContext);
        sc.addView(l);
        l.setOrientation(LinearLayout.VERTICAL);
        l.setBackgroundColor(Color.WHITE);

        List<String> DeletedFriendList = DeletedFriendStore.GetCacheDeleteList();
        for(String Uin : DeletedFriendList){
            l.addView(vCreateItem(mContext,Uin,DeletedFriendStore.GetUinSavedName(Uin)+"("+Uin+")",DeletedFriendStore.GetUinTime(Uin)),0);
        }

        TextView Delete = new TextView(mContext);
        Delete.setText("长按清空记录");
        Delete.setTextColor(Color.BLACK);
        Delete.setGravity(Gravity.CENTER);

        l.addView(Delete);

        @SuppressLint("ResourceType") Dialog d = new Dialog(mContext,3);
        d.setContentView(sc);

        d.show();

        Delete.setOnLongClickListener(v -> {
            new AlertDialog.Builder(mContext,3)
                    .setTitle("提示")
                    .setMessage("是否确认清空记录?")
                    .setNegativeButton("确认", (dialog, which) -> {
                        DeletedFriendStore.ClearAll();
                        d.dismiss();
                    }).setPositiveButton("取消", (dialog, which) -> {

            }).show();
            return true;
        });


    }
    @SuppressLint("ResourceType")
    public static View vCreateItem(Context context, String Uin,String UinLine, String TimeLine){
        RelativeLayout relativeLayout = new RelativeLayout(context);


        RelativeLayout.LayoutParams aParam1 = new RelativeLayout.LayoutParams(Utils.dip2px(context,40),Utils.dip2px(context,40));

        ImageView Avatar = new ImageView(context);
        ImageAvatarLoader(Avatar,Uin);
        relativeLayout.addView(Avatar,aParam1);
        Avatar.setId(159753);

        RelativeLayout.LayoutParams MainTitle = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        MainTitle.addRule(RelativeLayout.RIGHT_OF,159753);
        TextView tView = new TextView(context);
        tView.setTextSize(16);
        tView.setText(UinLine);
        tView.setId(15599);
        tView.setTextColor(Color.BLACK);
        relativeLayout.addView(tView,MainTitle);


        RelativeLayout.LayoutParams SubTitle = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        SubTitle.addRule(RelativeLayout.RIGHT_OF,159753);
        SubTitle.addRule(RelativeLayout.BELOW,15599);

        TextView SubTitleView = new TextView(context);
        SubTitleView.setText(TimeLine);
        SubTitleView.setTextSize(10);
        relativeLayout.addView(SubTitleView,SubTitle);
        return relativeLayout;
    }
    /**线程加载网络中的头像**/
    private static void ImageAvatarLoader(ImageView image,String Uin){
        String OnlinePath = "https://q1.qlogo.cn/g?b=qq&nk="+Uin + "&s=160";
        pool.execute(()->{
            try {
                URL u = new URL(OnlinePath);
                InputStream st = u.openStream();
                Drawable dr = Drawable.createFromStream(st,Uin);
                new Handler(Looper.getMainLooper()).post(()->image.setImageDrawable(dr));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
}
