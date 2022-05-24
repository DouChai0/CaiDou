package com.ldq.connect.FloatWindow;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.WindowManager;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.ServerTool.UserStatus;

import java.io.File;

public class Init {
    static WindowManager m = null;
    static WindowManager.LayoutParams sParam = null;
    static RelativeLayout v = null;
    static boolean Added = false;
    static ImageView mBackButton;

    static Activity CurrentActivity = null;


    public static Drawable WindowIcon;
    public static Drawable BackDrawable;

    public static boolean IsInMainWindow = true;


    public static void InitRes()
    {
        if(new File(MHookEnvironment.PublicStorageModulePath + "ficon.png").exists())
        {
            WindowIcon = Drawable.createFromPath(MHookEnvironment.PublicStorageModulePath + "ficon.png");
        }
        else
        {
            HttpUtils.downlaodFile("https://tsupdata-1251707849.cos.ap-chengdu.myqcloud.com/ficon.png",MHookEnvironment.PublicStorageModulePath + "ficon.png");
            WindowIcon = Drawable.createFromPath(MHookEnvironment.PublicStorageModulePath + "ficon.png");
        }
        if(new File(MHookEnvironment.PublicStorageModulePath + "back.png").exists())
        {
            BackDrawable = Drawable.createFromPath(MHookEnvironment.PublicStorageModulePath + "back.png");
        }
        else
        {
            HttpUtils.downlaodFile("https://tsupdata-1251707849.cos.ap-chengdu.myqcloud.com/back.png",MHookEnvironment.PublicStorageModulePath + "back.png");
            BackDrawable = Drawable.createFromPath(MHookEnvironment.PublicStorageModulePath + "back.png");
        }
        if(WindowIcon==null || BackDrawable==null)
        {
            Utils.ShowToast("悬浮窗资源加载失败");
        }
    }
    public static void FlushAll()
    {
        HideWindow();
        StartFWindow();
        PagerHandler.ClearAll();
    }
    public static void MoveToRight(boolean IsSet)
    {
        if(IsSet)
        {
            if(mButton!=null)
            {
                RelativeLayout.LayoutParams sParam = (RelativeLayout.LayoutParams) mButton.getLayoutParams();
                sParam.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,1);
                mButton.setLayoutParams(sParam);

                RelativeLayout.LayoutParams mParam = (RelativeLayout.LayoutParams) BackButton.getLayoutParams();
                mParam.removeRule(RelativeLayout.RIGHT_OF);
                mParam.addRule(RelativeLayout.LEFT_OF,2355);
                BackButton.setLayoutParams(mParam);
            }
        }
        else
        {
            if(mButton!=null)
            {
                RelativeLayout.LayoutParams sParam = (RelativeLayout.LayoutParams) mButton.getLayoutParams();
                sParam.removeRule(RelativeLayout.ALIGN_PARENT_RIGHT);
                mButton.setLayoutParams(sParam);

                RelativeLayout.LayoutParams mParam = (RelativeLayout.LayoutParams) BackButton.getLayoutParams();
                mParam.removeRule(RelativeLayout.LEFT_OF);
                mParam.addRule(RelativeLayout.RIGHT_OF,2355);
                BackButton.setLayoutParams(mParam);
            }
        }

    }
    static ImageButton mButton = null;
    static ImageButton BackButton = null;
    private static void RemoveAllAddWindow(){
        try{
            m.removeViewImmediate(v);
        }catch (Exception e){

        }
    }
    @SuppressLint("ResourceType")
    private static void StartFWindow()
    {
        try{
            if(WindowIcon==null)
            {
                Utils.ShowToast("资源文件未加载,你可以尝试允许QQ存储权限后重启QQ");
                return;
            }
            RemoveAllAddWindow();


            Activity sMainAct = MainAct;
            CurrentActivity = sMainAct;
        /*
        if(!Settings.canDrawOverlays(sMainAct))
        {
            Utils.ShowToast("未授予悬浮窗权限,请授予权限后再试");
            sMainAct.startActivityForResult(new Intent(Settings.ACTION_MANAGE_OVERLAY_PERMISSION, Uri.parse("package:" + sMainAct.getPackageName())), 0);
            return;
        }

         */
            WindowManager manager = (WindowManager) sMainAct.getSystemService(Context.WINDOW_SERVICE);

            RelativeLayout mLayout = new RelativeLayout(sMainAct);


            ImageButton ClickButton = new ImageButton(sMainAct);
            ClickButton.setImageDrawable(WindowIcon);
            ClickButton.getBackground().setAlpha(0);
            ClickButton.setOnClickListener(v -> PagerHandler.AddAndShowPager(sMainAct));
            ClickButton.setOnLongClickListener(v -> true);
            ClickButton.setScaleType(ImageView.ScaleType.FIT_CENTER);
            ClickButton.setId(2355);
            ClickButton.setOnLongClickListener(v-> {
                if(!WindowHandler.MoveStatus){
                    if(MConfig.Get_Boolean("Main","MainSwitch","图文消息模式",false)){
                        MConfig.Put_Boolean("Main","MainSwitch","图文消息模式",false);
                        Utils.ShowToast("已关闭图文模式");
                    }else {
                        MConfig.Put_Boolean("Main","MainSwitch","图文消息模式",true);
                        Utils.ShowToast("已开启图文模式");
                    }
                }

                return true;
            });
            mLayout.addView(ClickButton,new RelativeLayout.LayoutParams(Utils.dip2px(sMainAct,MConfig.Get_Long("FloatWindow","Item","Size",60)), Utils.dip2px(sMainAct,MConfig.Get_Long("FloatWindow","Item","Size",60))));
            mButton = ClickButton;
            //MoveToRight(false);

            ImageButton img = new ImageButton(sMainAct);

            img.setImageAlpha(182);
            img.setId(2366);
            img.setImageDrawable(BackDrawable);
            img.getBackground().setAlpha(0);
            img.setOnClickListener(v -> ClickToBackEvent());
            img.setVisibility(View.GONE);
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            mBackButton = img;
            BackButton = img;

            RelativeLayout.LayoutParams sParams = new RelativeLayout.LayoutParams(Utils.dip2px(sMainAct,40), Utils.dip2px(sMainAct,40));
            sParams.addRule(RelativeLayout.ALIGN_BOTTOM,2355);
            sParams.addRule(RelativeLayout.RIGHT_OF,2355);
            mLayout.addView(img,sParams);



            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            //layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            layoutParams.type = WindowManager.LayoutParams.FIRST_SUB_WINDOW+5;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;

            layoutParams.x = (int) MConfig.Get_Long("FloatWindow","Location","x",0);
            layoutParams.y = (int) MConfig.Get_Long("FloatWindow","Location","y",0);

            manager.addView(mLayout,layoutParams);
            Added = true;




            m=manager;
            v=mLayout;
            sParam = layoutParams;

            new WindowHandler(mLayout,manager,layoutParams,ClickButton);

        }catch (Throwable th)
        {
            //Utils.ShowToast("悬浮窗初始化发生错误:\n"+th);
        }

    }

    public static void ClickToBackEvent()
    {
        PagerHandler.SendBackEventToChild();
    }

    static volatile Activity MainAct;
    public static void ShowWindow(Activity act)
    {

        IsInMainWindow = false;
        if(!MConfig.Get_Boolean("Main","MainSwitch","悬浮窗Ex",false))return;
        if(v==null || MainAct != act)
        {
            MainAct = act;
            FlushAll();
        }else
        {
            if (!Added)
            {
                try {
                    m.addView(v, sParam);
                }catch (Throwable ex)
                {
                    v = null;
                }
                Added = true;
            }
        }
    }
    public static void HideWindow()
    {
        IsInMainWindow = true;
        if(!MConfig.Get_Boolean("Main","MainSwitch","悬浮窗Ex",false))return;
        if(v!=null)
        {
            if(Added)
            {
                try{
                    m.removeViewImmediate(v);
                }catch (Throwable ex)
                {
                    v = null;
                }
                Added = false;
            }

        }
    }
    public static void SetFocusable()
    {
        sParam.flags = WindowManager.LayoutParams.FLAG_NOT_TOUCH_MODAL;
        m.updateViewLayout(v, sParam);
    }
    public static void SetDIsFocusable()
    {
        sParam.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        m.updateViewLayout(v, sParam);
    }
    public static void FlushLayout()
    {
        m.updateViewLayout(v, sParam);
        HideWindow();
        ShowWindow(MainAct);
    }
    public static void ShowSizeChangeDialog()
    {
        Activity act = Utils.GetThreadActivity();
        LinearLayout l = new LinearLayout(act);
        l.setOrientation(LinearLayout.VERTICAL);

        TextView tView = new TextView(act);
        tView.setText("大小修改");
        tView.setTextSize(16);
        tView.setTextColor(Color.BLACK);
        l.addView(tView);

        EditText ed = new EditText(act);
        ed.setTextSize(24);
        ed.setText(""+MConfig.Get_Long("FloatWindow","Item","Size",60));
        l.addView(ed);

        TextView t2 = new TextView(act);
        t2.setText("设置项目");
        t2.setTextColor(Color.BLACK);
        t2.setTextSize(16);
        l.addView(t2);

        CheckBox ch = new CheckBox(act);
        ch.setText("语音");
        ch.setChecked(MConfig.Get_Boolean("FloatWindow","Item","Voice",true));
        ch.setTextColor(Color.BLACK);
        ch.setTextSize(16);
        l.addView(ch);

        CheckBox ch2 = new CheckBox(act);
        ch2.setChecked(MConfig.Get_Boolean("FloatWindow","Item","OnlineVoice",true));
        ch2.setText("在线");
        ch2.setTextColor(Color.BLACK);
        ch2.setTextSize(16);
        //l.addView(ch2);

        CheckBox ch3 = new CheckBox(act);
        if(UserStatus.CheckIsDonator())
        {
            ch3.setChecked(MConfig.Get_Boolean("FloatWindow","Item","ConvertVoice",true));
            ch3.setText("转语音");
            ch3.setTextColor(Color.BLACK);
            ch3.setTextSize(16);
            l.addView(ch3);
        }

        CheckBox ch4 = new CheckBox(act);
        ch4.setChecked(MConfig.Get_Boolean("FloatWindow","Item","tuya",true));
        ch4.setText("涂鸦");
        ch4 .setTextColor(Color.BLACK);
        ch4.setTextSize(16);
        l.addView(ch4);



        CheckBox ch5 = new CheckBox(act);
        ch5.setChecked(MConfig.Get_Boolean("FloatWindow","Item","SearchImage",true));
        ch5.setText("搜表情");
        ch5.setTextColor(Color.BLACK);
        ch5.setTextSize(16);
        l.addView(ch5);


        CheckBox ch6= new CheckBox(act);
        ch6.setChecked(MConfig.Get_Boolean("FloatWindow","Item","ShareImage",true));
        ch6.setText("分享表情");
        ch6.setTextColor(Color.BLACK);
        ch6.setTextSize(16);
        //l.addView(ch6);

        new AlertDialog.Builder(ed.getContext(),3)
                .setView(l)
                .setTitle("设置(刷新生效)")
                .setNegativeButton("保存", (dialog, which) -> {
                    try{
                        int size = Integer.parseInt(ed.getText().toString());
                        if(size<0 || size > 500)
                        {
                            Utils.ShowToast("输入错误");
                            return;
                        }
                        MConfig.Put_Long("FloatWindow","Item","Size",size);
                        MConfig.Put_Boolean("FloatWindow","Item","Voice",ch.isChecked());
                        MConfig.Put_Boolean("FloatWindow","Item","OnlineVoice",ch2.isChecked());
                        MConfig.Put_Boolean("FloatWindow","Item","ConvertVoice",ch3.isChecked());
                        MConfig.Put_Boolean("FloatWindow","Item","tuya",ch4.isChecked());
                        MConfig.Put_Boolean("FloatWindow","Item","SearchImage",ch5.isChecked());
                        MConfig.Put_Boolean("FloatWindow","Item","ShareImage",ch6.isChecked());

                        Init.FlushAll();
                    }catch (Exception e)
                    {
                        Utils.ShowToast("输入错误");
                    }
                }).show();
    }
}
