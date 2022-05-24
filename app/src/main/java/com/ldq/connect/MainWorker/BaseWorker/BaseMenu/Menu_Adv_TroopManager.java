package com.ldq.connect.MainWorker.BaseWorker.BaseMenu;

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
import com.ldq.connect.MainWorker.QQManagerHelper.TroopManager_Handler_Mute_Log;
import com.ldq.connect.MainWorker.QQManagerHelper.TroopManager_Set_Active_Cleaner;
import com.ldq.connect.MainWorker.QQManagerHelper.TroopManager_Set_DenyWord;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQSessionUtils;
import com.ldq.connect.QQUtils.TroopManager;

import java.util.ArrayList;

public class Menu_Adv_TroopManager {
    public static void ShowSettingForManager() {
        Activity act = Utils.GetThreadActivity();

        new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT)
                .setItems(new String[]{"重复加群检测(建议都点进成员列表刷新一次)","禁言记录开关","活跃时间筛查","违禁词设置"}, (dialog, which) -> {
                    if(which==0)CheckForAddMoreTroopDialog();
                    if(which==1) TroopManager_Handler_Mute_Log.ShowBaseDialog();
                    if(which==2) TroopManager_Set_Active_Cleaner.StartShow(QQSessionUtils.GetCurrentGroupUin());
                    if(which==3) TroopManager_Set_DenyWord.ShowDenyWordForTroop(QQSessionUtils.GetCurrentGroupUin());
                }).show();
    }
    private static void CheckForAddMoreTroopDialog() {
        Activity act = Utils.GetThreadActivity();
        ArrayList<JavaPluginUtils.GroupInfo> l = JavaPluginUtils.GetGroupInfo();
        String[] TroopList = new String[l.size()];
        for(int i=0;i<l.size();i++)
        {
            TroopList[i] = l.get(i).GroupName+"("+l.get(i).GroupUin+")";
        }

        new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("选择要对比的群聊(当前群:"+BaseInfo.GetCurrentGroupUin()+"-"+ TroopManager.GetTroopName(BaseInfo.GetCurrentGroupUin())+")")
                .setItems(TroopList, (dialog, which) -> {
                    StartCheckForUser(BaseInfo.GetCurrentGroupUin(), l.get(which).GroupUin);
                }).show();
    }
    static class CheckUserResult{
        public String UserUin;
        public String UserName;
    }
    private static void StartCheckForUser(String CheckTarget1,String CheckTarget2) {
        ArrayList<JavaPluginUtils.GroupMemberInfo> TroopInfo1 = JavaPluginUtils.GetGroupMemberList(CheckTarget1);
        ArrayList<JavaPluginUtils.GroupMemberInfo> TroopInfo2 = JavaPluginUtils.GetGroupMemberList(CheckTarget2);
        ArrayList<CheckUserResult> CheckResult = new ArrayList<>();

        Loop11:
        for(int i=0;i<TroopInfo1.size();i++)
        {
            String Uin = TroopInfo1.get(i).UserUin;
            if(Uin.equals("2854196310"))continue;
            if(TroopInfo1.get(i).IsAdmin)continue;

            for(int i2=0;i2<TroopInfo2.size();i2++)
            {
                if(TroopInfo2.get(i2).IsAdmin)continue;
                if(Uin.equals(TroopInfo2.get(i2).UserUin))
                {
                    CheckUserResult ch = new CheckUserResult();
                    ch.UserUin = Uin;
                    ch.UserName = TroopInfo1.get(i).NickName+"|"+TroopInfo2.get(i2).NickName;
                    CheckResult.add(ch);
                    continue Loop11;
                }
            }
        }


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

        Button KickOther = new Button(act);
        KickOther.setText("踢出选中(目标群聊)");
        KickOther.setTextSize(14);
        mButtonLayout1.addView(KickOther);
        mRootLayout.addView(mButtonLayout1);

        LinearLayout mButtonLayout2 = new LinearLayout(act);
        Button KickRandom = new Button(act);
        KickRandom.setTextSize(14);
        KickRandom.setText("踢出选中(随机一群)");
        mButtonLayout2.addView(KickRandom);

        Button Copy = new Button(act);
        Copy.setText("复制选中QQ号");
        Copy.setTextSize(14);
        mButtonLayout2.addView(Copy);
        mRootLayout.addView(mButtonLayout2);

        LinearLayout mButtonLayout3 = new LinearLayout(act);
        Button fanxuan = new Button(act);
        fanxuan.setText("反选");
        fanxuan.setTextSize(14);
        mButtonLayout3.addView(fanxuan);
        mRootLayout.addView(mButtonLayout3);

        ArrayList<CheckBox> checkList = new ArrayList<>();
        for(CheckUserResult result : CheckResult)
        {
            CheckBox cb = new CheckBox(act);
            cb.setText(result.UserUin+"::"+result.UserName);
            cb.setTextSize(14);
            mRootLayout.addView(cb,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));
            checkList.add(cb);
        }

        //注册事件
        KickAll.setOnClickListener(v->{
            ArrayList<String> checkResults = CollectInfos(checkList,CheckResult);
            new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("踢出确认")
                    .setMessage("确定在群聊"+TroopManager.GetTroopName(CheckTarget1)+"("+CheckTarget1+")中踢出"
                            +checkResults.size()+"个成员?"
                    ).setNeutralButton("确定", (dialog, which) -> {
                        TroopManager.Group_Kick(CheckTarget1,checkResults.toArray(new String[0]),false);
                        Utils.ShowToast("成功");
                    }).setNegativeButton("取消", (dialog, which) -> {

                    }).show();
        });

        KickOther.setOnClickListener(v->{
            ArrayList<String> checkResults = CollectInfos(checkList,CheckResult);
            new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("踢出确认")
                    .setMessage("确定在群聊"+TroopManager.GetTroopName(CheckTarget2)+"("+CheckTarget2+")中踢出"
                            +checkResults.size()+"个成员?"
                    ).setNeutralButton("确定", (dialog, which) -> {
                TroopManager.Group_Kick(CheckTarget2,checkResults.toArray(new String[0]),false);
                Utils.ShowToast("成功");
            }).setNegativeButton("取消", (dialog, which) -> {

            }).show();
        });

        KickRandom.setOnClickListener(v->{
            ArrayList<String> checkResults = CollectInfos(checkList,CheckResult);
            new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("踢出确认")
                    .setMessage("确定在群聊"+TroopManager.GetTroopName(CheckTarget1)+"("+CheckTarget1+")和" +
                            TroopManager.GetTroopName(CheckTarget2)+"("+CheckTarget2+")"+
                            "中随机踢出"
                            +checkResults.size()+"个成员?"
                    ).setNeutralButton("确定", (dialog, which) -> {
                        ArrayList<String> r1 = new ArrayList<>();
                        ArrayList<String> r2 = new ArrayList<>();
                        for(String strUin: checkResults)
                        {
                            if(Math.random()>0.5)
                            {
                                r1.add(strUin);
                            }else
                            {
                                r2.add(strUin);
                            }
                        }
                TroopManager.Group_Kick(CheckTarget1,r1.toArray(new String[0]),false);
                TroopManager.Group_Kick(CheckTarget2,r2.toArray(new String[0]),false);


                Utils.ShowToast("成功");
            }).setNegativeButton("取消", (dialog, which) -> {

            }).show();
        });

        Copy.setOnClickListener(v->{
            ArrayList<String> checkResults = CollectInfos(checkList,CheckResult);
            String copy = "";
            for(String sUin : checkResults)copy = copy + sUin+"\n";
            Utils.SetTextClipboard(copy);
            Utils.ShowToast("复制成功");
        });

        fanxuan.setOnClickListener(v->{
            for(CheckBox ch : checkList)ch.setChecked(!ch.isChecked());
        });
        try {
            new AlertDialog.Builder(act,2)
                    .setTitle("重复加群的成员列表(当前群:"+CheckTarget1+"-"+ TroopManager.GetTroopName(CheckTarget1)+")")
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
}
