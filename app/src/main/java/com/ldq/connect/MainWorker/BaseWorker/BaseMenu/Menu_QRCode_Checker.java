package com.ldq.connect.MainWorker.BaseWorker.BaseMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Handler;
import android.os.Looper;
import android.view.Window;
import android.widget.ImageView;

import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;

import java.io.File;

public class Menu_QRCode_Checker {
    public static long StartTime;
    public static void StartCheck()
    {
        Menu_Main.ShowSet();
    }
    public static void ShowQRCode(){
        Activity act = Utils.GetThreadActivity();
        AlertDialog al = new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT)
                .setCancelable(false)
                .create();

        //下载赞助二维码到缓存目录
        if(!new File(MHookEnvironment.MAppContext.getCacheDir()+"/s.s.qr").exists())
        {
            HttpUtils.downlaodFile("https://tsupdata-1251707849.cos.ap-chengdu.myqcloud.com/%E6%94%B6%E6%AC%BE%E7%A0%813.jpg", MHookEnvironment.MAppContext.getCacheDir()+"/","s.s.qr");
        }


        //解析赞助二维码到ImageView上面
        Bitmap b = BitmapFactory.decodeFile(MHookEnvironment.MAppContext.getCacheDir()+"/s.s.qr");
        ImageView img = new ImageView(act);
        img.setImageBitmap(b);
        img.setMaxHeight(800);
        img.setMaxWidth(600);

        al.setView(img);




        al.setOnDismissListener(dialog -> {
            Menu_Main.ShowSet();
        });

        al.setCancelable(false);
        al.setCanceledOnTouchOutside(false);
        Window w = al.getWindow();
        try {
            MMethod.CallMethod(w,"setCloseOnTouchOutside",void.class,new Class[]{boolean.class},false);
        } catch (Exception e) {
            e.printStackTrace();
        }


        al.show();



        //在5秒之后才允许关闭赞助二维码
        new Handler(Looper.getMainLooper()).postDelayed(()->{
            al.setCancelable(true);
            al.setCanceledOnTouchOutside(true);
        },5000);

    }

}
