package com.ldq.connect;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.widget.TextView;

import com.ldq.Utils.Utils;
import com.ldq.connect.ServerTool.UserStatus;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

public class mAboutDialog extends AlertDialog {
    public Context mContext;
    @SuppressLint("ResourceType")
    public mAboutDialog(Context context) {
        super(context,3);
        mContext = context;
    }

    public void Init(String mContent)
    {
        setTitle("关于");


        TextView mView = new TextView(mContext);
        mView.setBackgroundColor(Color.WHITE);
        //显示动态的关于信息,并且创建图像加载器,让其可以正常显示图片
        mView.setText(Html.fromHtml(mContent, Html.FROM_HTML_MODE_LEGACY, source -> {
            final InputStream[] is = {null};
            final Drawable[] d = {null};
            try
            {
                Thread mThread = new Thread(() -> {
                    try {
                        is[0] = (InputStream) new URL(source).getContent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    d[0] = Drawable.createFromStream(is[0], "src");
                    d[0].setBounds(0, 0, d[0].getIntrinsicWidth()*5, d[0].getIntrinsicHeight()*5);
                    try {
                        is[0].close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                });
                mThread.start();
                mThread.join();
                return d[0];
            }
            catch(Exception e)
            {
                return null;
            }
        },null));
        mView.setMovementMethod(LinkMovementMethod.getInstance());
        setView(mView);
        show();
    }
    public void InitAutherInfo()
    {
        setTitle("赞助信息");
        TextView mView = new TextView(mContext);
        mView.setBackgroundColor(Color.WHITE);
        //显示动态的关于信息,并且创建图像加载器,让其可以正常显示图片
        if(UserStatus.CheckIsDonator())
        {
            mView.setText(Html.fromHtml("<img src=\"https://tsupdata-1251707849.cos.ap-chengdu.myqcloud.com/qrcode.jpg\"></img>", Html.FROM_HTML_MODE_LEGACY, source -> {
                final InputStream[] is = {null};
                final Drawable[] d = {null};
                try
                {
                    Thread mThread = new Thread(() -> {
                        try {
                            is[0] = (InputStream) new URL(source).getContent();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        d[0] = Drawable.createFromStream(is[0], "src");
                        float width = (float) (MHookEnvironment.MAppContext.getResources().getDisplayMetrics().widthPixels*0.8);
                        float height = width * ((float) d[0].getIntrinsicHeight() / (float) d[0].getIntrinsicWidth());
                        d[0].setBounds(0, 0,(int) width,(int) height);
                        try {
                            is[0].close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    mThread.start();
                    mThread.join();
                    return d[0];
                }
                catch(Exception e)
                {
                    return null;
                }
            },null));
        }else {
            mView.setText(Html.fromHtml("<img src=\"https://tsupdata-1251707849.cos.ap-chengdu.myqcloud.com/qrcode.jpg\"></img>", Html.FROM_HTML_MODE_LEGACY, source -> {
                final InputStream[] is = {null};
                final Drawable[] d = {null};
                try
                {
                    Thread mThread = new Thread(() -> {
                        try {
                            is[0] = (InputStream) new URL(source).getContent();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        d[0] = Drawable.createFromStream(is[0], "src");
                        float width = (float) (MHookEnvironment.MAppContext.getResources().getDisplayMetrics().widthPixels*0.8);
                        float height = width * (float) ((float) d[0].getIntrinsicHeight() / (float) d[0].getIntrinsicWidth());
                        d[0].setBounds(0, 0,(int) width,(int) height);
                        try {
                            is[0].close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    mThread.start();
                    mThread.join();
                    return d[0];
                }
                catch(Exception e)
                {
                    return null;
                }
            },null));
        }

        mView.setMovementMethod(LinkMovementMethod.getInstance());
        setView(mView);
        show();
    }
    public static void OpenAlipay()
    {
        String str = "alipayqr://platformapi/startapp?saId=10000007&qrcode=https%3A%2F%2Fqr.alipay.com%2Ffkx17385svezdcsll97mb2a";
        Intent i = new Intent(Intent.ACTION_VIEW, Uri.parse(str));
        Utils.GetThreadActivity().startActivity(i);
    }
}
