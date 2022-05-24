package com.ldq.connect.MainWorker.QQManagerHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ldq.Utils.MLogCat;
import com.ldq.Utils.Utils;
import com.ldq.connect.JavaPlugin.JavaPluginUtils;
import com.ldq.connect.QQUtils.TroopManager;
import com.ldq.connect.Tools.MyTimePicker;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class TroopManager_Set_Active_Cleaner {
    public static void StartShow(String TroopUin)
    {
        Activity act = Utils.GetThreadActivity();
        MyTimePicker time = new MyTimePicker(act);
        new AlertDialog.Builder(act,3)
                .setTitle("设置活跃检测间隔")
                .setView(time)
                .setNeutralButton("开始检测", (dialog, which) -> {
                    long TimeDelay = System.currentTimeMillis()/1000-time.GetSecond();
                    ArrayList<JavaPluginUtils.GroupMemberInfo> infos = JavaPluginUtils.GetGroupMemberList(TroopUin);
                    HashMap<String,String> UinMap = new HashMap<>();
                    for(JavaPluginUtils.GroupMemberInfo info : infos)
                    {
                        //MLogCat.Print_Debug(info.Join_Time+":"+TimeDelay);
                        //MLogCat.Print_Debug(info.Last_AvtivityTime+":"+TimeDelay);
                        if(info.Join_Time < TimeDelay && info.Last_AvtivityTime < TimeDelay)
                        {
                            String Uin = info.UserUin;
                            String ShowName = info.NickName+"("+info.UserUin+")["+Utils.secondToTime(System.currentTimeMillis()/1000 - info.Last_AvtivityTime)+"]";
                            if(info.IsAdmin)
                            {
                                ShowName = "<管理员>" + ShowName;
                            }

                            ShowName = "[LV"+info.UserLevel+"]"+ShowName;


                            UinMap.put(Uin,ShowName);
                        }
                    }
                    StartShowPerClean(UinMap,TroopUin);
                }).show();

    }
    public static void StartShowPerClean(Map<String,String> UinList,String TroopUin)
    {
        Activity act = Utils.GetThreadActivity();
        ScrollView sc = new ScrollView(act);


        LinearLayout mRootLayout = new LinearLayout(act);
        sc.addView(mRootLayout);
        mRootLayout.setOrientation(LinearLayout.VERTICAL);

        LinearLayout mButtonLayout1 = new LinearLayout(act);
        Button KickAll = new Button(act);
        KickAll.setText("踢出选中(当前群聊)");
        KickAll.setTextSize(14);
        mButtonLayout1.addView(KickAll);

        Button fanxuan = new Button(act);
        fanxuan.setText("反选");
        fanxuan.setTextSize(14);
        mButtonLayout1.addView(fanxuan);
        mRootLayout.addView(mButtonLayout1);
        ArrayList<CheckUserResult> CheckResult = new ArrayList<>();


        Iterator<String> it = UinList.keySet().iterator();
        while (it.hasNext())
        {
            String key = it.next();
            String value = UinList.get(key);
            CheckUserResult result = new CheckUserResult();
            result.UserUin = key;
            result.UserName = value;
            CheckResult.add(result);

        }
        ArrayList<CheckBox> checkList = new ArrayList<>();
        for(CheckUserResult result : CheckResult)
        {
            CheckBox cb = new CheckBox(act);
            cb.setText(result.UserName);
            cb.setTextSize(14);
            mRootLayout.addView(cb,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            checkList.add(cb);
        }

        //注册事件
        KickAll.setOnClickListener(v->{
            ArrayList<String> checkResults = CollectInfos(checkList,CheckResult);
            new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("踢出确认")
                    .setMessage("确定在群聊"+ TroopManager.GetTroopName(TroopUin)+"("+TroopUin+")中踢出"
                            +checkResults.size()+"个成员?"
                    ).setNeutralButton("确定", (dialog, which) -> {
                TroopManager.Group_Kick(TroopUin,checkResults.toArray(new String[0]),false);
                Utils.ShowToast("已执行踢出,请自行刷新查看踢出结果");
            }).setNegativeButton("取消", (dialog, which) -> {

            }).show();
        });


        fanxuan.setOnClickListener(v->{
            for(CheckBox ch : checkList)ch.setChecked(!ch.isChecked());
        });
        try {
            new AlertDialog.Builder(act,2)
                    .setTitle("检测结果(当前群:"+TroopUin+"-"+ TroopManager.GetTroopName(TroopUin)+")")
                    .setView(sc)
                    .show();
        } catch (Exception exception) {
            Utils.ShowToast("显示对话框时发生错误:\n" +exception);
            MLogCat.Print_Error("ShowDialog",exception);
        }
    }

    public static ArrayList<String> CollectInfos(ArrayList<CheckBox> checkBoxs,ArrayList<CheckUserResult> Results)
    {
        ArrayList<String> sResult = new ArrayList<>();
        for(int i=0;i<checkBoxs.size();i++)
        {
            CheckBox ch = checkBoxs.get(i);
            if(ch.isChecked())
            {
                sResult.add(Results.get(i).UserUin);
            }
        }
        return sResult;
    }

    static class CheckUserResult{
        public String UserUin;
        public String UserName;
    }
}
