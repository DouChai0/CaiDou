package com.ldq.connect.MainWorker.ProxyHandler;

import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.QQUtils.BaseInfo;

import org.json.JSONObject;

public class Handler_Expand_StickPic {
    public static void Caller_Handler(Object MessageRecord)
    {
        try
        {
            if(!MConfig.Get_Boolean("Main","MainSwitch","贴图放大",true))return;

            if(MessageRecord.getClass().getName().contains("MessageForPic"))
            {
                String ExJson = MField.GetField(MessageRecord,"extStr",String.class);
                if(ExJson==null)return;
                JSONObject mJson = new JSONObject(ExJson);
                if(mJson.has("sticker_info"))
                {
                    String Sender = MField.GetField(MessageRecord, MessageRecord.getClass(),"senderuin", String.class);
                    if(!Sender.equals(BaseInfo.GetCurrentUin()))return;
                    JSONObject stJson = new JSONObject(mJson.getString("sticker_info"));


                    double d = stJson.getDouble("width");
                    double h = stJson.getDouble("height");
                    if(d>h)
                    {
                        h = 0.5 / (d / h);
                        stJson.put("width",0.5);
                        stJson.put("height",h);
                    }
                    else
                    {
                        d = 0.5 / (h / d);
                        stJson.put("width",d);
                        stJson.put("height",0.5);
                    }
                    MMethod.CallMethod(MessageRecord,"saveExtInfoToExtStr",void.class,new Class[]{String.class,String.class},"sticker_info",stJson.toString());
                    MMethod.CallMethod(MessageRecord,"doParse",void.class,new Class[0]);
                }
            }
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("贴图放大",th);
        }

    }
}
