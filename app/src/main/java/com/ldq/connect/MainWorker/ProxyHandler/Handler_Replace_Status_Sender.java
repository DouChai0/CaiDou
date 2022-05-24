package com.ldq.connect.MainWorker.ProxyHandler;

import com.ldq.Utils.MField;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;

public class Handler_Replace_Status_Sender {
    public static void caller(Object ChatMsg){
        if(MConfig.Get_Boolean("Main","MainSwitch","替换状态",false)){
            if (ChatMsg.getClass().getName().contains("MessageForUniteGrayTip")){
                try {
                    MField.SetField(ChatMsg,"senderuin","10000");
                    MMethod.CallMethod(ChatMsg,"prewrite",void.class,new Class[0]);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }

    }

}
