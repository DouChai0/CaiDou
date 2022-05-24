package com.ldq.connect.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.animation.TranslateAnimation;
import android.widget.EditText;
import android.widget.ScrollView;

import com.ldq.Utils.Utils;

public class MPositionDialog {
    public static void CreateUpDialog(View Content)
    {
        Activity mAct = Utils.GetThreadActivity();

        ScrollView sc = new ScrollView(mAct);
        sc.addView(Content);

        AlertDialog mDialog = new AlertDialog.Builder(mAct,3).create();
        mDialog.setView(new EditText(mAct));

        Window mWindow = mDialog.getWindow();
        mWindow.setGravity(Gravity.TOP);

        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mWindow.clearFlags(WindowManager.LayoutParams.FLAG_ALT_FOCUSABLE_IM);
        mDialog.show();


        Display dis = mAct.getWindowManager().getDefaultDisplay();



        mDialog.getWindow().getDecorView().setPadding(0,0,0,0);


        TranslateAnimation ts = new TranslateAnimation(0,0,-dis.getHeight(),0);
        ts.setDuration(400);


        mDialog.setContentView(sc);
        sc.startAnimation(ts);
        mDialog.getWindow().setLayout(dis.getWidth(), (int) (dis.getHeight()*0.8));

    }
    public static void CreateUpDialogMea(View Content)
    {
        Activity mAct = Utils.GetThreadActivity();

        ScrollView sc = new ScrollView(mAct);
        sc.addView(Content);

        AlertDialog mDialog = new AlertDialog.Builder(mAct,3).create();

        Window mWindow = mDialog.getWindow();
        mWindow.setGravity(Gravity.TOP);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.show();


        Display dis = mAct.getWindowManager().getDefaultDisplay();



        mDialog.getWindow().getDecorView().setPadding(0,0,0,0);


        TranslateAnimation ts = new TranslateAnimation(0,0,-dis.getHeight(),0);
        ts.setDuration(400);

        RefreshView(Content);
        int mHeight = Content.getMeasuredHeight();
        if(mHeight> (int) (dis.getHeight()*0.8))
        {
            mHeight = (int) (dis.getHeight()*0.8);
        }


        mDialog.setContentView(sc);
        sc.startAnimation(ts);
        mDialog.getWindow().setLayout(dis.getWidth(), mHeight+80);

    }

    public static void CreateDownDialog(View Content)
    {
        Activity mAct = Utils.GetThreadActivity();

        ScrollView sc = new ScrollView(mAct);
        sc.addView(Content);

        AlertDialog mDialog = new AlertDialog.Builder(mAct,3).create();

        Window mWindow = mDialog.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.show();


        Display dis = mAct.getWindowManager().getDefaultDisplay();



        mDialog.getWindow().getDecorView().setPadding(0,0,0,0);


        TranslateAnimation ts = new TranslateAnimation(0,0,dis.getHeight(),0);
        ts.setDuration(400);


        mDialog.setContentView(sc);
        RefreshView(Content);
        int mHeight = (int) (dis.getHeight()*0.8);
        sc.startAnimation(ts);
        mDialog.getWindow().setLayout(dis.getWidth(), mHeight);
    }
    public static Dialog CreateDownDialogMeasure(View Content)
    {
        Activity mAct = Utils.GetThreadActivity();

        ScrollView sc = new ScrollView(mAct);
        sc.addView(Content);

        AlertDialog mDialog = new AlertDialog.Builder(mAct,3).create();

        Window mWindow = mDialog.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.show();


        Display dis = mAct.getWindowManager().getDefaultDisplay();



        mDialog.getWindow().getDecorView().setPadding(0,0,0,0);


        TranslateAnimation ts = new TranslateAnimation(0,0,dis.getHeight(),0);
        ts.setDuration(400);


        mDialog.setContentView(sc);
        RefreshView(Content);

        int mHeight = Content.getMeasuredHeight();
        if(mHeight> (int) (dis.getHeight()*0.8))
        {
            mHeight = (int) (dis.getHeight()*0.8);
        }
        //int mHeight = (int) (dis.getHeight()*0.8);
        sc.startAnimation(ts);
        mDialog.getWindow().setLayout(dis.getWidth(), mHeight);
        //mDialog.getWindow().addFlags(WindowManager.LayoutParams.FLAG_BLUR_BEHIND);


        return mDialog;
    }
    public static void CreateDownDialogMeasure(View Content, DialogInterface.OnDismissListener disss)
    {
        Activity mAct = Utils.GetThreadActivity();

        ScrollView sc = new ScrollView(mAct);
        sc.addView(Content);

        AlertDialog mDialog = new AlertDialog.Builder(mAct,3).create();

        Window mWindow = mDialog.getWindow();
        mWindow.setGravity(Gravity.BOTTOM);
        mDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        mDialog.show();
        mDialog.setOnDismissListener(disss);


        Display dis = mAct.getWindowManager().getDefaultDisplay();



        mDialog.getWindow().getDecorView().setPadding(0,0,0,0);


        TranslateAnimation ts = new TranslateAnimation(0,0,dis.getHeight(),0);
        ts.setDuration(400);


        mDialog.setContentView(sc);
        RefreshView(Content);

        int mHeight = Content.getMeasuredHeight();
        if(mHeight> (int) (dis.getHeight()*0.8))
        {
            mHeight = (int) (dis.getHeight()*0.8);
        }
        //int mHeight = (int) (dis.getHeight()*0.8);
        sc.startAnimation(ts);
        mDialog.getWindow().setLayout(dis.getWidth(), mHeight);
    }

    public static void RefreshView(View v)
    {
        int width = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        int height = View.MeasureSpec.makeMeasureSpec(0,
                View.MeasureSpec.UNSPECIFIED);
        v.measure(width, height);
    }
}
