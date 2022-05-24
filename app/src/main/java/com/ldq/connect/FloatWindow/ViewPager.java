package com.ldq.connect.FloatWindow;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;

import com.github.megatronking.stringfog.annotation.StringFogIgnore;
import com.ldq.Utils.Utils;

@StringFogIgnore
public class ViewPager extends ViewGroup {
    private int NowPage = 0;
    public ViewPager(Context context) {
        super(context);
        setBackgroundColor(Color.WHITE);
        getBackground().setAlpha(180);
    }


    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
        measureChildren(widthMeasureSpec,heightMeasureSpec);
        setMeasuredDimension(Utils.dip2px(getContext(),300), Utils.dip2px(getContext(),300));
    }

    @Override
    protected void onLayout(boolean changed, int left, int top, int right, int bottom) {
        View v;
        for(int i=0;i<getChildCount();i++)
        {
            v = getChildAt(i);
            if(i==NowPage)
            {
                v.setVisibility(VISIBLE);
                v.layout(0,0,right,bottom-top);
                v.invalidate();
            }
            else
            {
                v.setVisibility(GONE);
            }
        }
    }
    public void PageTo(int page)
    {
        if(page!=NowPage)
        {
            View v = getChildAt(page);
            v.setVisibility(VISIBLE);
            SendExitEvent(NowPage);
            NowPage = page;
            ShowAllChild();
            requestLayout();
            v.invalidate();
            Init.FlushLayout();
        }
    }
    public void SendLongClickEvent(){
        View v = getChildAt(NowPage);
        if(v!=null)
        {
            ((RequestShow)v).requestOnClick();
        }
    }
    public void SendExitEvent(int Count)
    {
        View v = getChildAt(Count);
        if(v!=null)
        {
            ((RequestShow)v).requestExit();
        }
        Init.FlushLayout();
    }
    public void ShowAllChild()
    {
        View v = getChildAt(NowPage);
        if(v!=null)
        {
            ((RequestShow)v).requestShow();
        }
    }
    public void SendBackEnent()
    {
        View v = getChildAt(NowPage);
        if(v!=null)
        {
            ((RequestShow)v).BackEvent();
        }
    }
    public interface RequestShow{
        void requestShow();
        void BackEvent();
        void requestExit();
        void requestOnClick();
    }
}
