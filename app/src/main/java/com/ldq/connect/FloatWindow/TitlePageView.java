package com.ldq.connect.FloatWindow;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.View;
import android.view.ViewGroup;
import android.widget.HorizontalScrollView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.ServerTool.UserStatus;

public class TitlePageView extends LinearLayout {
    LinearLayout TitleBar;
    ViewPager body;

    int AddCount=0;
    public TitlePageView(Context context) {
        super(context);

        HorizontalScrollView mScroll = new HorizontalScrollView(context);
        TitleBar = new LinearLayout(context);
        mScroll.addView(TitleBar,new LayoutParams(Utils.dip2px(getContext(),300), ViewGroup.LayoutParams.WRAP_CONTENT));


        body = new ViewPager(context);
        setOrientation(VERTICAL);
        addView(mScroll);
        addView(body,new LayoutParams(Utils.dip2px(getContext(),300), ViewGroup.LayoutParams.WRAP_CONTENT));
    }

    public void AddItem(String BarText,View BodyView)
    {
        TextView t = new TextView(getContext()){
            private final int sroke_width = 5;
            @Override
            protected void onDraw(Canvas canvas) {
                Paint paint = new Paint();
                paint.setColor(Color.BLUE);
                canvas.drawLine(0, 0, this.getWidth() - sroke_width, 0, paint);
                canvas.drawLine(0, 0, 0, this.getHeight() - sroke_width, paint);
                canvas.drawLine(this.getWidth() - sroke_width, 0, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
                canvas.drawLine(0, this.getHeight() - sroke_width, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
                super.onDraw(canvas);
            }
        };
        AddCount +=1;

        t.setTextColor(Color.BLUE);
        t.setTextSize(24);
        t.setBackgroundColor(Color.WHITE);
        t.setText(GetShowingText(BarText));
        t.setTag(getChildCount()-1);
        TitleBar.addView(t);

        body.addView(BodyView);

        final int CheckCount = AddCount-1;

        t.setOnClickListener(v -> {
            body.PageTo(CheckCount);
        });
        t.setOnLongClickListener(v -> {
            body.SendLongClickEvent();
            return true;
        });




    }
    public void ShowAllChild()
    {
        body.ShowAllChild();
    }
    public void SendBackEvent()
    {
        body.SendBackEnent();
    }

    private String GetShowingText(String text)
    {
        if(text.length()>4)
        {
            return text.substring(0,2)+"..";
        }
        return text;
    }
    public void FlushTitleBar()
    {
        TitleBar.removeAllViews();
        body.removeAllViews();
        AddCount = 0;


        if(MConfig.Get_Boolean("FloatWindow","Item","Voice",true))
        {
            AddItem("语音",new VoiceHolder(getContext()));
        }


        if(MConfig.Get_Boolean("FloatWindow","Item","ConvertVoice",true))
        {
            if(UserStatus.CheckIsDonator())
            {
                AddItem("转语音",new ConvertToVoice(getContext()));
            }
        }


        if(MConfig.Get_Boolean("FloatWindow","Item","tuya",true))
        {
            AddItem("涂鸦",new TYContainer(getContext()));
        }

        if(MConfig.Get_Boolean("FloatWindow","Item","SearchImage",true))
        {
            AddItem("搜表情",new OnlineEmoContainer(getContext()));
        }
        EmoHelper.SearchAndAddItem(this);
    }

}
