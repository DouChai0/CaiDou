package com.ldq.connect.MTool;

import com.ldq.Utils.MTaskThread;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQTools;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class AutoCheckSign {
    public static boolean CheckSignOpen(String TroopUin)
    {
        List<String> SignList = MConfig.Get_List("Troop","AutoSign","List");
        return SignList.contains(TroopUin);
    }
    public static void AddSignList(String TroopUin) {

        List<String> SignList = MConfig.Get_List("Troop","AutoSign","List");
        if(SignList.contains(TroopUin))return;
        SignList.add(TroopUin);
        MConfig.Put_List("Troop","AutoSign","List",SignList);

        ArrayList mArgList = new ArrayList();
        mArgList.add(TroopUin);


        MTaskThread.AddThreadClock(TroopUin, TaskArgs -> {
            if(!MConfig.Get_Boolean("Main","MainSwitch","自动打卡",false))return;
            String TroopUin1 = (String) TaskArgs.get(0);
            Calendar NowTime = GetGMT8Time();//使用东八区时间进行打卡,使国外用户也能准时打卡
            String NowDays = NowTime.get(Calendar.DAY_OF_MONTH)+"";
            if(!MConfig.Get_String("Troop","AutoSign",TroopUin1).equals(NowDays))
            {
                try {
                    MHookEnvironment.mTask.PostTask(()-> QQTools.CheckSign(TroopUin1, BaseInfo.GetCurrentUin()),1);
                    MHookEnvironment.mTask.PostTask(()-> QQTools.CheckSign(TroopUin1, BaseInfo.GetCurrentUin()),10*1000);
                    MHookEnvironment.mTask.PostTask(()-> QQTools.CheckSign(TroopUin1, BaseInfo.GetCurrentUin()),60*1000);
                    MHookEnvironment.mTask.PostTask(()-> QQTools.CheckSign(TroopUin1, BaseInfo.GetCurrentUin()),300*1000);
                    MConfig.Put_String("Troop","AutoSign",TroopUin1,NowDays);
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        },mArgList);
    }
    public static void RemoveCheck(String TroopUin)
    {
        List<String> SignList = MConfig.Get_List("Troop","AutoSign","List");
        SignList.remove(TroopUin);
        MConfig.Put_List("Troop","AutoSign","List",SignList);

    }
    public static void InitCheckTask()
    {
        List<String> SignList = MConfig.Get_List("Troop","AutoSign","List");
        for(String Troop :SignList)
        {
            ArrayList mArgList = new ArrayList();
            mArgList.add(Troop);
            MTaskThread.AddThreadClock(Troop, TaskArgs -> {
                if(!MConfig.Get_Boolean("Main","MainSwitch","自动打卡",false))return;
                String TroopUin1 = (String) TaskArgs.get(0);
                Calendar NowTime = GetGMT8Time();//使用东八区时间进行打卡,使国外用户也能准时打卡
                String NowDays = NowTime.get(Calendar.DAY_OF_MONTH)+"";
                if(!MConfig.Get_String("Troop","AutoSign",TroopUin1).equals(NowDays))
                {
                    try {
                        MHookEnvironment.mTask.PostTask(()-> QQTools.CheckSign(TroopUin1, BaseInfo.GetCurrentUin()),1);
                        MHookEnvironment.mTask.PostTask(()-> QQTools.CheckSign(TroopUin1, BaseInfo.GetCurrentUin()),10*1000);
                        MHookEnvironment.mTask.PostTask(()-> QQTools.CheckSign(TroopUin1, BaseInfo.GetCurrentUin()),60*1000);
                        MHookEnvironment.mTask.PostTask(()-> QQTools.CheckSign(TroopUin1, BaseInfo.GetCurrentUin()),300*1000);
                        MConfig.Put_String("Troop","AutoSign",TroopUin1,NowDays);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            },mArgList);
        }
    }
    private static Calendar GetGMT8Time()
    {
        Calendar cal = Calendar.getInstance();

        int zoneOffset = cal.get(Calendar.ZONE_OFFSET);

        int dstOffset = cal.get(Calendar.DST_OFFSET);

        cal.add(Calendar.MILLISECOND, -(zoneOffset + dstOffset));

        cal.add(Calendar.MILLISECOND, +8*3600*1000);
        return cal;
    }

}
