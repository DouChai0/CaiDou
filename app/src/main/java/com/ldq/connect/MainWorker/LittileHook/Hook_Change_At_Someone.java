package com.ldq.connect.MainWorker.LittileHook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.SpannableString;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.Utils.MClass;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.QQUtils.QQSessionUtils;
import com.ldq.connect.QQUtils.TroopManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Change_At_Someone {
    public static void StartDialog()
    {
        Activity act = Utils.GetThreadActivity();

        LinearLayout l = new LinearLayout(act);
        l.setOrientation(LinearLayout.VERTICAL);
        TextView t = new TextView(act);
        t.setText("在下面输入需要修改成为的内容,支持变量 [Name] 显示的群名称,[UName] 真实QQ名称,[Uin] QQ号 ,留空则为不修改");
        t.setTextSize(18);
        t.setTextColor(Color.BLACK);

        l.addView(t);

        EditText ed = new EditText(act);
        ed.setTextSize(14);
        ed.setText(MConfig.Get_String("Main","AtChange","Text"));
        l.addView(ed);

        new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("艾特显示修改")
                .setView(l)
                .setNeutralButton("保存", (dialog, which) -> {
                    MConfig.Put_String("Main","AtChange","Text",ed.getText().toString());
                }).show();
    }

    public static void Start() throws ClassNotFoundException {
        XC_MethodHook xc_methodHook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String Text = MConfig.Get_String("Main","AtChange","Text");
                if(TextUtils.isEmpty(Text))return;
                String QQUin = ""+param.args[3];
                String TroopUin = QQSessionUtils.GetCurrentGroupUin();

                if(TextUtils.isEmpty(TroopUin))return;
                String UserShowName = TroopManager.GetMemberName(TroopUin,QQUin);
                String UserTrueName = TroopManager.GetMemberTrueName(TroopUin,QQUin);

                Text = Text.replace("[Name]",UserShowName)
                        .replace("[UName]",UserTrueName)
                        .replace("[Uin]",QQUin);
                EditText editText = (EditText)param.args[6];
                Object  NewInst;
                try{
                    NewInst = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.troop.text.AtTroopMemberSpan"),
                            new Class[]{Context.class,String.class,String.class,int.class, Paint.class},
                            param.args[1],QQUin,Text,(int)((editText.getWidth() - editText.getPaddingLeft()) - editText.getPaddingRight()),editText.getPaint()
                    );
                }catch (Exception e)
                {
                    NewInst = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.troop.text.AtTroopMemberSpan"),
                            new Class[]{Context.class,String.class,String.class,int.class, Paint.class,boolean.class},
                            param.args[1],QQUin,Text,(int)((editText.getWidth() - editText.getPaddingLeft()) - editText.getPaddingRight()),editText.getPaint(),false
                    );
                }


                Object ColorNick = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.text.ColorNickText"),new Class[]{
                        CharSequence.class,int.class
                }, Text,16);
                SpannableString ss = new SpannableString((CharSequence) FieldTable.ColorNickText_Text().get(ColorNick));
                ss.setSpan(NewInst,0,Text.length(),33);
                param.setResult(ss);
            }
        };
        try{
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.troop.text.AtTroopMemberSpan"), "a",
                    MClass.loadClass("mqq.app.AppRuntime"), Context.class, String.class, String.class, String.class, boolean.class, EditText.class,
                    boolean.class,boolean.class,boolean.class,xc_methodHook);
        }catch (Throwable th)
        {
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.troop.text.AtTroopMemberSpan"), "a",
                    MClass.loadClass("mqq.app.AppRuntime"), Context.class, String.class, String.class, String.class, boolean.class, EditText.class,
                    boolean.class,xc_methodHook);
        }


    }
}
