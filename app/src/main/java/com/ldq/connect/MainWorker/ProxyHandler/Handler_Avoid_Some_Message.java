package com.ldq.connect.MainWorker.ProxyHandler;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.QQUtils.BaseInfo;

public class Handler_Avoid_Some_Message {
    public static void _Handle_ShakeWindow(Object ChatMsg)
    {
        try{
            if(MConfig.Get_Boolean("Main","MainSwitch","屏蔽窗口抖动",true))
            {
                String ClassName = ChatMsg.getClass().getSimpleName();
                if(ClassName.contains("MessageForShakeWindow"))
                {
                    MField.SetField(ChatMsg,"isread",true);
                }
            }
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("ShakeWindow_Handler",th);
        }
    }

    public static void _Handle_PIC(Object ChatMsg)
    {
        if(ChatMsg.getClass().getSimpleName().equals("MessageForPic"))
        {
            try {
                if(MConfig.Get_Boolean("Main","MainSwitch","表情转换",false))
                {
                    //MField.SetField(ChatMsg,"imageType",0);
                    Object Obj = MField.GetField(ChatMsg,"picExtraData");
                    MField.SetField(Obj,"imageBizType",0);

                }
            }
            catch (Throwable th)
            {

            }

        }
    }
    public static void _Handle_VoiceTime(Object ChatMsg)
    {
        if(ChatMsg.getClass().getSimpleName().equals("MessageForPtt"))
        {
            try {
                String Sender = MField.GetField(ChatMsg, ChatMsg.getClass(),"senderuin", String.class);
                if(Sender.equals(BaseInfo.GetCurrentUin()))
                {
                    if(!TextUtils.isEmpty(MConfig.Get_String("Main","VoiceChange","text")))
                    {
                        MField.SetField(ChatMsg,ChatMsg.getClass(),"sttText",MConfig.Get_String("Main","VoiceChange","text"),String.class);
                        MMethod.CallMethod(ChatMsg,"prewrite",void.class,new Class[0]);
                    }
                    if(MConfig.Get_Long("Main","VoiceChange","time",0)!=0)
                    {
                        MField.SetField(ChatMsg,ChatMsg.getClass(),"voiceLength",(int)MConfig.Get_Long("Main","VoiceChange","time",0),int.class);
                        MMethod.CallMethod(ChatMsg,"prewrite",void.class,new Class[0]);
                    }
                }
            } catch (Throwable e) {
                MLogCat.Print_Error("VoiceTimeAndTextChange",e);
            }
        }
    }
    public static void ShowChangeVoiceDialog(Context mContext)
    {
        AlertDialog dialog = new AlertDialog.Builder(mContext,AlertDialog.THEME_HOLO_LIGHT).create();
        dialog.setTitle("设置语音信息(保存发送有效)");
        LinearLayout layout = new LinearLayout(mContext);
        layout.setOrientation(LinearLayout.VERTICAL);
        TextView tip1 = new TextView(mContext);
        tip1.setTextSize(15);
        tip1.setTextColor(Color.BLACK);
        tip1.setText("语音显示文本");
        layout.addView(tip1);
        EditText ed = new EditText(mContext);
        ed.setText(MConfig.Get_String("Main","VoiceChange","text"));
        layout.addView(ed);

        TextView tip2 = new TextView(mContext);
        tip2.setTextSize(15);
        tip2.setTextColor(Color.BLACK);
        tip2.setText("语音时长(秒)");
        layout.addView(tip2);

        EditText e2 = new EditText(mContext);
        e2.setText(MConfig.Get_Long("Main","VoiceChange","time",0)+"");

        layout.addView(e2);
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL,"确定",(tx,v) -> {
            String VoiceText = ed.getText().toString();
            String VoiceTime = e2.getText().toString();
            try{
                long Time = Long.parseLong(VoiceTime);
                MConfig.Put_Long("Main","VoiceChange","time",Time);
                MConfig.Put_String("Main","VoiceChange","text",VoiceText);
            }
            catch (Throwable th)
            {
                Utils.ShowToast("设置失败");
            }
        });
        dialog.setView(layout);
        dialog.show();
    }
}
