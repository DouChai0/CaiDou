package com.ldq.connect.MainWorker.QQManagerHelper;

import com.ldq.Utils.MField;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.QQUtils.TroopManager;

public class TroopManager_Handler_Mute_Continue {

    public static void OnCallBackJoinTroop(String GroupUin, String UserUin) {
        if (MConfig.Get_Boolean("TroopGuard","ForbiddenContinueOpen",GroupUin,false)) {
            long EndTime = MConfig.Get_Long("TroopGuard","ForbiddenContinueTime",GroupUin+"&"+UserUin,0);
            if (EndTime > System.currentTimeMillis()) {
                try {
                    TroopManager.Group_Forbidden(GroupUin, UserUin, (int) ((EndTime - System.currentTimeMillis()) / 1000));
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }
    }
    public static void CheckIsLocked(Object ChatMsg)
    {
        try{
            int isTroop = MField.GetField(ChatMsg,"istroop",int.class);
            if(isTroop==1)
            {
                String GroupUin = MField.GetField(ChatMsg,"frienduin",String.class);
                String UserUin = MField.GetField(ChatMsg,"senderuin",String.class);

                long LockTime = MConfig.Troop_Get_Long("TroopGuard","ForbiddenLock",GroupUin,UserUin,0);
                if(LockTime!=0 && LockTime - System.currentTimeMillis()>0)
                {
                    long RestTime = LockTime - System.currentTimeMillis();
                    TroopManager.Group_Forbidden(GroupUin,UserUin, (int) RestTime/1000);
                }else
                {
                    if(LockTime!=0) MConfig.Troop_Put_Long("TroopGuard","ForbiddenLock",GroupUin,UserUin,0);
                }
            }

        }catch (Exception e)
        {

        }

    }

    public static void LockMode(String GroupUin,String UserUin,long TimeStamp)
    {
        long LockTime = MConfig.Troop_Get_Long("TroopGuard","ForbiddenLock",GroupUin,UserUin,0);
        if(LockTime - System.currentTimeMillis()>0)
        {
            long RestTime = LockTime - System.currentTimeMillis();
            TroopManager.Group_Forbidden(GroupUin,UserUin, (int) RestTime/1000);
        }else {
            if (LockTime!=0)
            {
                MConfig.Troop_Put_Long("TroopGuard","ForbiddenLock",GroupUin,UserUin,0);
            }
        }
    }
}
