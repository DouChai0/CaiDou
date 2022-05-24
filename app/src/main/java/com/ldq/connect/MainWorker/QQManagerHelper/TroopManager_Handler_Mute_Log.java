package com.ldq.connect.MainWorker.QQManagerHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.View;
import android.widget.LinearLayout;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.HookConfig.NDirDataBase;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.FormItem;
import com.ldq.connect.QQUtils.QQSessionUtils;
import com.ldq.connect.QQUtils.TroopManager;

import org.json.JSONObject;

import java.util.Calendar;
import java.util.WeakHashMap;

public class TroopManager_Handler_Mute_Log {
    static WeakHashMap<String,String> WeakStatusChecker = new WeakHashMap<>();
    public synchronized static String CheckSelfForbiddenStatus(String TroopUin,String UserUin,long Time)
    {
        //计算时间并读取储存的原因
        Calendar cl = Calendar.getInstance();
        int Ses = cl.get(Calendar.SECOND)/20;
        String Ts = ""+cl.get(Calendar.DAY_OF_MONTH) + cl.get(Calendar.HOUR_OF_DAY) + cl.get(Calendar.MINUTE);
        String CheckString = TroopUin + ":" + UserUin + ":" + Time + ":" + Ses + ":" + Ts;

        if(WeakStatusChecker.containsKey(CheckString))
        {
            String s = WeakStatusChecker.get(CheckString);
            WeakStatusChecker.remove(CheckString);
            return s;
        }
        return "默认禁言记录";
    }
    public static void AddSelfForbiddenStatus(String TroopUin,String UserUin,long Time,String Reason)
    {
        //计算时间并储存原因保证禁言不会被多次记录或者缺掉记录
        Calendar cl = Calendar.getInstance();
        int Ses = cl.get(Calendar.SECOND)/20;
        String Ts = ""+cl.get(Calendar.DAY_OF_MONTH) + cl.get(Calendar.HOUR_OF_DAY) + cl.get(Calendar.MINUTE);
        String CheckString = TroopUin + ":" + UserUin + ":" + Time + ":" + Ses + ":" + Ts;


        WeakStatusChecker.put(CheckString,Reason);
    }
    public static void Handler_Forbidden_Event_Log(String TroopUin,String UserUin,String Admin,long MuteTime,String Reason) {
        try {
            if(!MConfig.Get_Boolean("TroopGuard","ForbiddenLogOpen",TroopUin,false))return;
            JSONObject Line = new JSONObject();
            Line.put("t", TroopUin);
            Line.put("u",UserUin);
            Line.put("a",Admin);
            Line.put("aname",TroopManager.GetMemberName(TroopUin,Admin));
            Line.put("uname",TroopManager.GetMemberName(TroopUin,UserUin));
            Line.put("time",MuteTime);
            if(Admin.equals(BaseInfo.GetCurrentUin()))
            {
                Line.put("r",CheckSelfForbiddenStatus(TroopUin,UserUin,MuteTime));
            }else
            {
                Line.put("r",Reason);
            }
            Line.put("today",System.currentTimeMillis());
            String OnPushData = DataUtils.bytesToHex(Line.toString().getBytes());

            Calendar time = Calendar.getInstance();
            String Daycut = ""+time.get(Calendar.YEAR)+time.get(Calendar.DAY_OF_YEAR);


            NDirDataBase.SaveData("ForbiddenLog",TroopUin,Daycut,OnPushData);
        } catch (Exception e)
        {
            MLogCat.Print_Error("ForbiddenPush",e);
        }
    }
    public static void ShowBaseDialog()
    {
        Activity act = Utils.GetThreadActivity();
        LinearLayout l = new LinearLayout(act);
        l.setOrientation(LinearLayout.VERTICAL);

        View v = FormItem.AddCheckItem(act, "开启后开启本群的所有禁言数据统计(未完成)", (buttonView, isChecked) -> MConfig.Put_Boolean("TroopGuard","ForbiddenLogOpen",QQSessionUtils.GetCurrentGroupUin(),isChecked), MConfig.Get_Boolean("TroopGuard","ForbiddenLogOpen",QQSessionUtils.GetCurrentGroupUin(),false));
        l.addView(v);

        v = FormItem.AddListItem(act,"查看本群的禁言记录",z -> new TroopManager_Set_Mute_Log().StartDialog(QQSessionUtils.GetCurrentGroupUin()));
        l.addView(v);

        new AlertDialog.Builder(act,3)
                .setView(l)
                .setTitle("禁言记录")
                .show();

    }
}
