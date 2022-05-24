package com.ldq.connect.MainWorker.QQManagerHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.NDirDataBase;
import com.ldq.connect.MHookEnvironment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Calendar;

public class TroopManager_Set_Mute_Log {
    Context context;
    int NowPage = 1;
    int AllPage = 0;


    String sTroopUin;
    public void StartDialog(String TroopUin)
    {
        sTroopUin = TroopUin;

        Activity act = Utils.GetThreadActivity();
        context = act;
        ScrollView sc = new ScrollView(act);
        sc.setBackgroundColor(Color.WHITE);
        LinearLayout mRoot = new LinearLayout(act);
        mRoot.setBackgroundColor(Color.WHITE);
        sc.addView(mRoot);

        mRoot.setOrientation(LinearLayout.VERTICAL);

        sRoot = mRoot;
        FlushShow();




        @SuppressLint("ResourceType") Dialog d = new Dialog(act,2);
        d.setContentView(sc);
        d.show();
    }
    LinearLayout sRoot;
    public void SelectDialog()
    {
        DatePicker picker = new DatePicker(context);

        new AlertDialog.Builder(context,3)
                .setTitle("选择日期")
                .setView(picker)
                .setNegativeButton("确定", (dialog, which) -> {
                    SetDay.set(picker.getYear(),picker.getMonth(),picker.getDayOfMonth());
                    NowPage = 1;
                    FlushShow();
                })
                .show();
    }
    public void AddToolBar()
    {
        LinearLayout bar = new LinearLayout(context);
        Button btnSearch = new Button(context);
        btnSearch.setText("搜索对象");
        btnSearch.setOnClickListener(v->SearchResult());
        bar.addView(btnSearch);


        Button btnSelect = new Button(context);
        btnSelect.setText("选择日期");
        btnSelect.setOnClickListener(v->SelectDialog());
        bar.addView(btnSelect);

        Button btnUp = new Button(context);
        btnUp.setText("上一页");
        btnUp.setOnClickListener(v->{
            if(NowPage==1)return;
            NowPage--;
            FlushShow();
        });
        bar.addView(btnUp);

        Button btnDown = new Button(context);
        btnDown.setText("下一页");
        btnDown.setOnClickListener(v->{
            if(NowPage>=AllPage)return;
            NowPage++;
            FlushShow();
        });
        bar.addView(btnDown);

        TextView vs = new TextView(context);
        vs.setText(NowPage + "/" + AllPage);
        vs.setTextColor(Color.BLACK);
        bar.addView(vs);

        sRoot.addView(bar);

    }
    public void FlushShow()
    {
        String Daycut = ""+SetDay.get(Calendar.YEAR)+SetDay.get(Calendar.DAY_OF_YEAR);
        int ToDayCount = NDirDataBase.GetLineCount("ForbiddenLog",sTroopUin,Daycut);
        AllPage = ToDayCount/10+1;

        sRoot.removeAllViews();
        AddToolBar();

        if(ToDayCount >0)
        {
            try {
                ArrayList<String> List = NDirDataBase.ReadLine("ForbiddenLog",sTroopUin,Daycut,(NowPage-1)*10);
                for(int i=0;i<List.size();i++)
                {
                    String s = List.get(i);
                    s = DataUtils.HexToString(s);

                    JSONObject json = new JSONObject(s);

                    String ShowText = "用户:" + json.optString("uname") + "("+json.getString("u") +")\n";
                    ShowText = ShowText + "被操作:" + (json.optLong("time")==0 ? "解禁" : "禁言"+Utils.secondToTime(json.optLong("time")))+"\n";
                    ShowText = ShowText + "操作者:" + json.optString("aname") + "(" + json.getString("a")+")\n";
                    ShowText = ShowText + "记录理由:" + json.getString("r")+"\n";
                    ShowText = ShowText + "记录时间:" + Utils.MillionToTimeStr(json.optLong("today"))+"\n";

                    TextView t = new TextView(context);
                    t.setTextColor(Color.BLACK);
                    t.setText(ShowText);
                    t.setTextSize(20);
                    sRoot.addView(t);
                }
            } catch (JSONException e) {
                Utils.ShowToast(Log.getStackTraceString(e));
            }
        }
    }
    Calendar SetDay = Calendar.getInstance();

    public Calendar DayCutToDay(String DayCut) {
        if (DayCut.length() < 4) return null;
        String Year = DayCut.substring(0, 4);
        String Day = DayCut.substring(4);
        Calendar cl = Calendar.getInstance();

        cl.set(Integer.parseInt(Year), 0, 1, 0, 0, 0);
        cl.add(Calendar.DAY_OF_YEAR, Integer.parseInt(Day));
        return cl;
    }
    public void SearchResult()
    {
        EditText ed = new EditText(context);
        new AlertDialog.Builder(context,3)
                .setTitle("输入要搜索的QQ号")
                .setView(ed)
                .setNegativeButton("搜索被禁言", (dialog, which) -> {
                    SearchAndFlushView(ed.getText().toString(),false);
                })
                .setNeutralButton("搜索管理", (dialog, which) -> {
                    SearchAndFlushView(ed.getText().toString(),true);
                }).show();
    }
    public void SearchAndFlushView(String Text,boolean IsAdmin)
    {
        String Path = MHookEnvironment.PublicStorageModulePath + "LotData/ForbiddenLog/"+sTroopUin+"/";
        File[] fs = new File(Path).listFiles();
        if(fs!=null)
        {
            sRoot.removeAllViews();
            AddToolBar();
            for(File f : fs)
            {
                try{
                    if(f.isDirectory())
                    {
                        String Daycut = f.getName();

                        int Count = NDirDataBase.GetLineCount("ForbiddenLog",sTroopUin,Daycut);

                        for(int i=0;i<(Count-1)/10+1;i++)
                        {
                            ArrayList<String> List = NDirDataBase.ReadLine("ForbiddenLog",sTroopUin,Daycut,i*10);
                            for(String s : List)
                            {
                                s = DataUtils.HexToString(s);
                                JSONObject json = new JSONObject(s);
                                if((IsAdmin ? json.getString("a") : json.getString("u")).equals(Text))
                                {
                                    String ShowText = "用户:" + json.optString("uname") + "("+json.getString("u") +")\n";
                                    ShowText = ShowText + "被操作:" + (json.optLong("time")==0 ? "解禁" : "禁言"+Utils.secondToTime(json.optLong("time")))+"\n";
                                    ShowText = ShowText + "操作者:" + json.optString("aname") + "(" + json.getString("a")+")\n";
                                    ShowText = ShowText + "记录理由:" + json.getString("r")+"\n";
                                    ShowText = ShowText + "记录时间:" + Utils.MillionToTimeStr(json.optLong("today"))+"\n";

                                    TextView t = new TextView(context);
                                    t.setTextColor(Color.BLACK);
                                    t.setText(ShowText);
                                    t.setTextSize(20);
                                    sRoot.addView(t);
                                }
                            }
                        }
                    }
                }catch (Exception e)
                {

                }

            }
        }
    }
}
