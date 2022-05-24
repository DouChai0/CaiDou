package com.ldq.connect.MainWorker.QQManagerHelper;

import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.TroopManager;

public class TroopManager_Handler_Simple {
    public static void _caller(Object ChatMsg)
    {
        try{
            if(MConfig.Get_Boolean("Main","MainSwitch","辅助群管",false))
            {
                if(TroopManager_Handler_WhiteBlack_Checker.Check_While(ChatMsg))return;
                //贴图禁言
                int flag = MField.GetField(ChatMsg,"msgtype",int.class);
                if(flag==-2058)//如果为贴图就进行处理
                {
                    String mUin =  MField.GetField(ChatMsg,"senderuin",String.class);
                    String TroopUin =  MField.GetField(ChatMsg,"frienduin",String.class);
                    if(!mUin.equals(BaseInfo.GetCurrentUin()))
                    {
                        if(MConfig.Troop_Get_Boolean("TroopGuard","Simple",TroopUin,"StickPicOpen",false))
                        {
                            long time =  MConfig.Troop_Get_Long("TroopGuard","Simple",TroopUin,"StickPic",0);
                            if(time!=0)
                            {
                                TroopManager_Handler_Mute_Log.AddSelfForbiddenStatus(TroopUin,mUin,time,"贴图禁言");
                                Forbidden_Check(TroopUin,mUin, (int) time);
                            }
                        }
                    }
                }

                //秀图禁言
                if (ChatMsg.getClass().getName().contains("MessageForTroopEffectPic")){
                    String mUin =  MField.GetField(ChatMsg,"senderuin",String.class);
                    String TroopUin =  MField.GetField(ChatMsg,"frienduin",String.class);
                    if(!mUin.equals(BaseInfo.GetCurrentUin()))
                    {
                        if(MConfig.Troop_Get_Boolean("TroopGuard","Simple",TroopUin,"EffectPicOpen",false))
                        {
                            long time =  MConfig.Troop_Get_Long("TroopGuard","Simple",TroopUin,"EffectPic",0);
                            if(time!=0)
                            {
                                TroopManager_Handler_Mute_Log.AddSelfForbiddenStatus(TroopUin,mUin,time,"秀图禁言");
                                Forbidden_Check(TroopUin,mUin, (int) time);
                            }
                        }
                    }
                }
            }
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("辅助群管",th);
        }
    }
    public static void Forbidden_Check(String GroupUin,String UserUin,int Time)
    {
        try{
            if(TroopManager.IsGroupOwner(GroupUin))
            {
                TroopManager.Group_Forbidden(GroupUin, UserUin, Time);
            }
            else if(TroopManager.IsGroupAdmin(GroupUin))
            {
                if(!TroopManager.IsGroupAdmin(GroupUin,UserUin))
                {
                    TroopManager.Group_Forbidden(GroupUin, UserUin, Time);
                }
            }
        }catch (Throwable th)
        {
            MLogCat.Print_Error("Forbidden",th);
        }

    }
}
