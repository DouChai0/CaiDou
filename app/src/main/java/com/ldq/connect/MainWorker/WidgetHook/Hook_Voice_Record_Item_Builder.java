package com.ldq.connect.MainWorker.WidgetHook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Fast_Menu;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.ServerTool.UserStatus;
import com.ldq.connect.Tools.MPositionDialog;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Calendar;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Voice_Record_Item_Builder
{
    public static int mSpeakID;
    public static void Start()
    {
        mSpeakID = MHookEnvironment.MAppContext.getResources().getIdentifier("press_to_speak_iv","id", MHookEnvironment.MAppContext.getPackageName());
        XposedHelpers.findAndHookConstructor("com.tencent.mobileqq.activity.aio.audiopanel.PressToSpeakPanel", MHookEnvironment.mLoader, Context.class, AttributeSet.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if(!MConfig.Get_Boolean("Main","MainSwitch","语音保存发送",false))return;

                RelativeLayout RLayout = (RelativeLayout) param.thisObject;
                TextView tView = new TextView(RLayout.getContext());
                tView.setTextColor(Color.RED);
                tView.setTextSize(14);
                tView.setText("单击发送保存的语音");

                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                params.addRule(RelativeLayout.ABOVE,mSpeakID);
                params.addRule(RelativeLayout.ALIGN_PARENT_TOP,1);
                params.addRule(RelativeLayout.CENTER_IN_PARENT,1);
                ViewGroup.MarginLayoutParams mpa = params;
                mpa.topMargin = 10;
                RLayout.addView(tView,params);
                tView.setOnClickListener(vclick ->{
                    ShowVoiceSelectWindow(Utils.GetThreadActivity(),tView);

                });


            }
        });
    }
    public static void ShowVoiceSelectWindow(Activity mContext, View AlignView)
    {

        ScrollView sView = new ScrollView(mContext);
        LinearLayout MList = new LinearLayout(mContext);

        sView.addView(MList,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
        MList.setOrientation(LinearLayout.VERTICAL);
        MList.setBackgroundColor(Color.BLACK);
        MList.getBackground().setAlpha(120);


        UpdateVoiceList(mContext,MList,MHookEnvironment.PublicStorageModulePath + "Voice/");



        MPositionDialog.CreateUpDialog(sView);

        /*
        PopupWindow mWindow = new PopupWindow(sView, Utils.dip2px(mContext,300), Utils.dip2px(mContext,300));
        mWindow.setFocusable(true);
        mWindow.setOutsideTouchable(true);
        mWindow.showAtLocation(mContext.getWindow().getDecorView(), Gravity.CENTER, 0, 0);

         */

    }
    private static String NowPath;
    public static void LocalSearch(Context mContext, LinearLayout ListLayout)
    {
        ListLayout.removeAllViews();
        EditText ed = new EditText(mContext);

        ed.setHint("输入要搜索的名字");
        ed.setTextSize(24);
        ListLayout.addView(ed);

        TextView t = new TextView(mContext);
        t.setText("点击搜索");
        t.setTextSize(24);
        t.setBackgroundColor(Color.BLACK);
        t.setTextColor(Color.RED);
        ListLayout.addView(t);

        LinearLayout l = new LinearLayout(mContext);
        l.setOrientation(LinearLayout.VERTICAL);
        ListLayout.addView(l);

        t.setOnClickListener(v -> {
            String Name = ed.getText().toString();
            l.removeAllViews();
            SrarchForList(mContext,l,MHookEnvironment.PublicStorageModulePath + "Voice/",Name);
        });
    }
    public static void SrarchForList(Context mContext,LinearLayout ll,String Path,String SearchName)
    {
        for(File f:new File(Path).listFiles())
        {
            if(f.isDirectory())
            {
                SrarchForList(mContext,ll,f.getAbsolutePath(),SearchName);
            }
            else
            {
                if(f.isFile())
                {
                    if(f.getName().contains(SearchName))
                    {
                        TextView mView = Handler_Fast_Menu.AddClickTextView(mContext,"[文件]"+f.getName()+"->"+Path.substring(23));
                        mView.setTag(f);
                        mView.setTextSize(18);
                        mView.setOnClickListener(xx ->{
                            File m = (File) xx.getTag();
                            try {
                                String CopyTo = Environment.getExternalStorageDirectory() +  "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/"+ BaseInfo.GetCurrentUin()+"/ptt/"+m.getName();
                                FileUtils.copy(m.getAbsolutePath(),CopyTo,4096);
                                QQMessage.Group_Send_Ptt(MHookEnvironment.CurrentSession ,CopyTo);
                            } catch (Exception e) {
                                Utils.ShowToast("发送失败");
                            }
                        });
                        ll.addView(mView);
                    }
                }
            }
        }



    }
    public static void UpdateVoiceList(Context mContext, LinearLayout ListLayout,String Path)
    {
        int[] checkTick = new int[1];
        NowPath = Path;
        ArrayList<File> mListFiles = ListVoiceFile(NowPath);
        ListLayout.removeAllViews();
        TextView mTitle = new TextView(mContext);
        mTitle.setTextSize(20);
        mTitle.setTextColor(Color.WHITE);
        mTitle.setText("本地语音列表,点击发送,长按进入搜索");
        mTitle.setOnLongClickListener(v -> {
            LocalSearch(mContext, ListLayout);
            return true;
        });
        ListLayout.addView(mTitle);

        for(File mGetFile : mListFiles)
        {
            if(mGetFile.isDirectory())
            {
                TextView mView = Handler_Fast_Menu.AddClickTextView(mContext,"[目录]"+mGetFile.getName());
                mView.setTextSize(18);
                mView.setTag(mGetFile);
                mView.setOnClickListener(xx ->{
                    File m = (File) xx.getTag();
                    UpdateVoiceList(xx.getContext(), ListLayout,m.getAbsolutePath()+"/");
                });
                ListLayout.addView(mView);
            }
            else if (mGetFile.isFile())
            {
                TextView mView = Handler_Fast_Menu.AddClickTextView(mContext,"[文件]"+mGetFile.getName());
                mView.setTag(mGetFile);
                mView.setTextSize(18);
                mView.setOnClickListener(xx ->{
                    File m = (File) xx.getTag();
                    try {
                        String CopyTo = Environment.getExternalStorageDirectory() +  "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/"+ BaseInfo.GetCurrentUin()+"/ptt/"+m.getName();
                        FileUtils.copy(m.getAbsolutePath(),CopyTo,4096);
                        QQMessage.Group_Send_Ptt(MHookEnvironment.CurrentSession ,CopyTo);
                    } catch (Exception e) {
                        Utils.ShowToast("发送失败");
                    }
                });
                mView.setOnLongClickListener(mx ->{
                    if(!UserStatus.CheckIsDonator())return false;
                    AlertDialog dialog = new AlertDialog.Builder(mContext,AlertDialog.THEME_HOLO_LIGHT).create();
                    dialog.setTitle("在线分享语音(彩蛋功能)");
                    LinearLayout layout = new LinearLayout(mContext);
                    layout.setOrientation(LinearLayout.VERTICAL);
                    TextView tip1 = new TextView(mContext);
                    tip1.setTextSize(15);
                    tip1.setTextColor(Color.BLACK);
                    tip1.setText("语音显示的名字");
                    layout.addView(tip1);
                    EditText ed = new EditText(mContext);
                    ed.setText(mGetFile.getName());
                    layout.addView(ed);



                    dialog.setButton(AlertDialog.BUTTON_NEUTRAL,"确定",(tx,v) -> {
                        String VoiceText = ed.getText().toString();
                        if(TextUtils.isEmpty(VoiceText))VoiceText= Utils.GetNowTime();
                        try{
                            String ShareVoicePath = mGetFile.getAbsolutePath();
                            File mF = new File(ShareVoicePath);
                            if(mF.length()>1024*1024)
                            {
                                Utils.ShowToast("音频文件过大,最大不能超过1mb");
                                return;
                            }

                            String mBuild = Utils.AddStringTo(BaseInfo.GetCurrentUin()) + VoiceText;
                            byte[] FileContent = FileUtils.ReadFileByte(ShareVoicePath);
                            byte[] buildByte = mBuild.getBytes(StandardCharsets.UTF_8);
                            String mSize = Utils.AddStringTo(""+FileContent.length);
                            byte[] mPostData = new byte[(int) (11+FileContent.length+buildByte.length)];
                            mPostData[0]=1;
                            byte[] mSizeByte = mSize.getBytes();
                            System.arraycopy(mSizeByte,0,mPostData,1,10);

                            System.arraycopy(FileContent,0,mPostData,11,FileContent.length);

                            System.arraycopy(buildByte,0,mPostData,11+FileContent.length,buildByte.length);
                            Thread mNetWorkThread = new Thread(()->{
                                String mRetrun =  HttpUtils.PostData("http://i.ldqhub.top",mPostData);
                                Utils.ShowToast(mRetrun);
                            });
                            mNetWorkThread.start();
                            mNetWorkThread.join();
                        }
                        catch (Throwable th)
                        {
                            Utils.ShowToast("分享失败");
                        }
                    });
                    dialog.setView(layout);
                    dialog.show();
                    return true;
                });
                ListLayout.addView(mView);
            }

        }
    }
    public static ArrayList<File> ListVoiceFile(String mPath)
    {
        File f = new File(mPath);
        if(!f.exists())return new ArrayList<>();
        if(f.isDirectory())
        {
            ArrayList<File> dic = new ArrayList<>();
            ArrayList<File> file = new ArrayList<>();
            File[] fs = f.listFiles();
            for(File mCheckf : fs)
            {
                if(mCheckf.isFile()) file.add(mCheckf);
                if(mCheckf.isDirectory())dic.add(mCheckf);
            }
            ArrayList<File> ret = new ArrayList();
            ret.addAll(dic);
            ret.addAll(file);
            return ret;
        }else
        {
            return new ArrayList<>();
        }
    }
    public static String GetTimeText()
    {
        Calendar cl = Calendar.getInstance();
        String str = "";
        str = cl.get(Calendar.YEAR)+""+cl.get(Calendar.MONTH)+cl.get(Calendar.DAY_OF_MONTH)+cl.get(Calendar.HOUR_OF_DAY)+cl.get(Calendar.MINUTE)+cl.get(Calendar.SECOND);
        return str;
    }

}

