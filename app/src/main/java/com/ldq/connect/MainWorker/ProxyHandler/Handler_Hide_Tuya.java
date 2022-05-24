package com.ldq.connect.MainWorker.ProxyHandler;

import android.text.TextUtils;
import android.view.View;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQTools;

public class Handler_Hide_Tuya {
    public static void _caller(Object ChatMsg)
    {
        try{
            if(MConfig.Get_Boolean("Main","MainSwitch","屏蔽涂鸦",false))
            {
               if(ChatMsg.getClass().getSimpleName().equals("MessageForScribble"))
                {
                    String mUin =  MField.GetField(ChatMsg,"senderuin",String.class);
                    if(!mUin.equals(BaseInfo.GetCurrentUin()))
                    {
                        QQTools.SaveExtraFlag(ChatMsg,"Tuya_Url", MField.GetField(ChatMsg,"combineFileUrl"));
                        MField.SetField(ChatMsg,"combineFileUrl","");

                        QQTools.SaveExtraFlag(ChatMsg,"MD5", MField.GetField(ChatMsg,"combineFileMd5"));
                        MField.SetField(ChatMsg,"combineFileMd5","");

                        QQTools.SaveExtraFlag(ChatMsg,"MgifId",""+ MField.GetField(ChatMsg,"gifId"));
                        MField.SetField(ChatMsg,"gifId",0);

                        MField.SetField(ChatMsg,"fileDownloadStatus",0);
                        MMethod.CallMethod(ChatMsg,"prewrite",void.class,new Class[0]);
                    }

                }
            }

        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("屏蔽涂鸦",th);
        }
    }
    public static void _call_layout(View v ,Object ChatMsg)
    {
        if(MConfig.Get_Boolean("Main","MainSwitch","屏蔽涂鸦",false))
        {
            if(ChatMsg.getClass().getSimpleName().equals("MessageForScribble"))
            {
                try {
                    String GetM = QQTools.GetExtraFlag(ChatMsg,"Tuya_Url");
                    String GetMD5 = QQTools.GetExtraFlag(ChatMsg,"MD5");
                    if(!TextUtils.isEmpty(GetM))
                    {
                        int GetGifID = Integer.parseInt(QQTools.GetExtraFlag(ChatMsg,"MgifId"));
                        MMethod.CallMethod(v,"setTailMessage",void.class,new Class[]{boolean.class,CharSequence.class, MClass.loadClass("android.view.View$OnClickListener")},true,"被屏蔽的涂鸦,点击这里恢复显示 ", (View.OnClickListener) v1 -> {
                            try {
                                QQTools.SaveExtraFlag(ChatMsg,"Tuya_Url","");
                                MField.SetField(ChatMsg,"combineFileUrl",GetM);
                                MField.SetField(ChatMsg,"combineFileMd5",GetMD5);
                                MField.SetField(ChatMsg,"gifId",GetGifID);
                                Utils.ShowToast("已经恢复,点击感叹号即可正常显示");
                                //QQTools.UpdateMessage(ChatMsg);
                            } catch (Exception e) {
                                MLogCat.Print_Error("receTuya",e);
                            }
                        });
                    }

                } catch (Exception e) {
                    MLogCat.Print_Error("receTuya",e);
                }
            }
        }
    }
}