package com.ldq.connect.MainWorker.ProxyHandler;

import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.TroopManager;

import java.util.Random;

public class Handler_TroopExit_Tip {
    public static void _caller(String TroopUin,String UserUin,int type,String OPUin)
    {
        if(type==3 && Long.parseLong(OPUin) > 0)
        {
            QQMessage.AddTip("成员"+ TroopManager.GetMemberName(TroopUin,UserUin)
                    +"("+UserUin+")被"+TroopManager.GetMemberName(TroopUin,OPUin)+"("+OPUin+")踢出了本群",System.currentTimeMillis()/1000,(long) Math.abs(new Random().nextInt()),new Random().nextInt(),TroopUin,""
            );
        }else if (type==2)
        {
            QQMessage.AddTip("成员"+ TroopManager.GetMemberName(TroopUin,UserUin)
                    +"("+UserUin+")退出了本群",System.currentTimeMillis()/1000,(long) Math.abs(new Random().nextInt()),new Random().nextInt(),TroopUin,""
            );
        }
    }
}
