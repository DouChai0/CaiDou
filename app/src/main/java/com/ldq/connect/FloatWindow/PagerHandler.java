package com.ldq.connect.FloatWindow;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;

public class PagerHandler {
    static boolean IsShowing = false;
    static TitlePageView mPager = null;
    static RelativeLayout.LayoutParams mParams = null;


    public static void ClearAll()
    {
        IsShowing = false;
        mPager = null;
        mParams = null;
    }
    public static void AddAndShowPager(Context context)
    {
        if(WindowHandler.MoveStatus)return;
        if(IsShowing)
        {

            Init.v.removeView(mPager);
            Init.mBackButton.setVisibility(View.GONE);
            IsShowing = false;
            Init.SetDIsFocusable();
            return;
        }

        IsShowing = true;
        Init.mBackButton.setVisibility(View.VISIBLE);
        if(mPager==null)
        {
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            params.addRule(RelativeLayout.BELOW,2355);
            TitlePageView pager = new TitlePageView(context);
            pager.FlushTitleBar();

            Init.v.addView(pager,params);
            mParams = params;
            mPager = pager;
        }else
        {
            Init.v.addView(mPager,mParams);
        }
        Init.MoveToRight(false);
        /*
        if(WindowHandler.IsInRight())
        {
            Init.MoveToRight(false);
        }else{
            Init.MoveToRight(true);
        }

         */
        Init.SetFocusable();

    }
    public static void SendBackEventToChild()
    {
        mPager.SendBackEvent();
    }
    public static void FlushShow()
    {
        mPager.FlushTitleBar();
    }
}

