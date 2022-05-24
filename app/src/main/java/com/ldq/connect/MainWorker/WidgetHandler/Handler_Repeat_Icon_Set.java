package com.ldq.connect.MainWorker.WidgetHandler;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.JavaPlugin.JavaPluginUtils;

import java.util.ArrayList;
import java.util.List;

public class Handler_Repeat_Icon_Set {
    public static void ShowSettingDialog(Context context)
    {
        @SuppressLint("ResourceType") Dialog d = new Dialog(context,2);
        LinearLayout mRootView = new LinearLayout(context);
        mRootView.setOrientation(LinearLayout.VERTICAL);
        mRootView.setBackgroundColor(Color.WHITE);
        d.setContentView(mRootView);;

        TextView t = new TextView(context);
        t.setTextSize(20);
        t.setTextColor(Color.BLACK);
        t.setText("基础样式:(0.气泡右上角,1.气泡右下角,2.气泡右边外部)");
        mRootView.addView(t);

        EditText eStyle = new EditText(context);
        eStyle.setText(""+MConfig.Get_Long("Main","Repeat","Style",0));
        eStyle.setTextSize(20);
        eStyle.setTextColor(Color.BLACK);
        mRootView.addView(eStyle);

        t = new TextView(context);
        t.setTextSize(20);
        t.setTextColor(Color.BLACK);
        t.setText("DistanceToHorizontally (dp)");
        mRootView.addView(t);

        EditText eMarginTop = new EditText(context);
        eMarginTop.setText(""+ MConfig.Get_Long("Main","Repeat","r2",-5));
        eMarginTop.setTextSize(20);
        eMarginTop.setTextColor(Color.BLACK);
        mRootView.addView(eMarginTop);

        t = new TextView(context);
        t.setTextSize(20);
        t.setTextColor(Color.BLACK);
        t.setText("DistanceToVertically(dp)");
        mRootView.addView(t);

        EditText eMarginRight = new EditText(context);
        eMarginRight.setText(""+MConfig.Get_Long("Main","Repeat","r1",-5));
        eMarginRight.setTextSize(20);
        eMarginRight.setTextColor(Color.BLACK);
        mRootView.addView(eMarginRight);

        t = new TextView(context);
        t.setTextSize(20);
        t.setTextColor(Color.BLACK);
        t.setText("viewSize(dp)");
        mRootView.addView(t);


        EditText eSize = new EditText(context);
        eSize.setText(""+MConfig.Get_Long("Main","Repeat","Size",35));
        eSize.setTextSize(20);
        eSize.setTextColor(Color.BLACK);
        mRootView.addView(eSize);

        CheckBox chDoubleClick = new CheckBox(context);
        chDoubleClick.setTextColor(Color.BLACK);
        chDoubleClick.setText("双击复读");
        chDoubleClick.setChecked(MConfig.Get_Boolean("Main","Repeat","DoubleClick",false));
        mRootView.addView(chDoubleClick);


        Button btnSave = new Button(context);
        btnSave.setText("保存");
        btnSave.setTextSize(20);
        btnSave.setOnClickListener(v->{
            MConfig.Put_Long("Main","Repeat","Style",Integer.parseInt(eStyle.getText().toString()));
            MConfig.Put_Long("Main","Repeat","r1",Integer.parseInt(eMarginRight.getText().toString()));
            MConfig.Put_Long("Main","Repeat","r2",Integer.parseInt(eMarginTop.getText().toString()));
            MConfig.Put_Long("Main","Repeat","Size",Integer.parseInt(eSize.getText().toString()));
            MConfig.Put_Boolean("Main","Repeat","DoubleClick",chDoubleClick.isChecked());
            d.dismiss();
        });
        mRootView.addView(btnSave);


        Button btnChange = new Button(context);
        btnChange.setText("设置复读黑名单");
        btnChange.setTextSize(20);
        btnChange.setOnClickListener(v->ReplyBlackTroopSet(context));
        mRootView.addView(btnChange);


        d.show();

    }

    private static void ReplyBlackTroopSet(Context context)
    {
        ArrayList<JavaPluginUtils.GroupInfo> l = JavaPluginUtils.GetGroupInfo();
        List<String> BlackList = MConfig.Get_List("Main","Repeat","Black");
        String[] ShowList = new String[l.size()];
        boolean[] bl = new boolean[l.size()];
        for(int i=0;i<l.size();i++)
        {
            JavaPluginUtils.GroupInfo info = l.get(i);
            String Uin = info.GroupUin;
            ShowList[i]= info.GroupName+"("+info.GroupUin+")";
            if(BlackList.contains(Uin))bl[i]=true;
        }

        new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("设置关闭复读功能的群聊")
                .setMultiChoiceItems(ShowList, bl, (dialog, which, isChecked) -> {

                }).setNeutralButton("确定", (dialog, which) -> {
                    ArrayList<String> sBlackList = new ArrayList<>();
                    for(int i=0;i<bl.length;i++)
                    {
                        if(bl[i])
                        {
                            sBlackList.add(l.get(i).GroupUin);
                        }
                    }
                    MConfig.Put_List("Main","Repeat","Black",sBlackList);
                }).show();
    }
}
