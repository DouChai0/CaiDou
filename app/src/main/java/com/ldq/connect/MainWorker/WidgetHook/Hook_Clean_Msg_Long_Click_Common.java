package com.ldq.connect.MainWorker.WidgetHook;

import android.app.AlertDialog;
import android.content.Context;
import android.widget.EditText;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_Clean_Msg_Long_Click_Common {
    public static void ShowSet(){
        Context context = Utils.GetThreadActivity();
        EditText ed = new EditText(context);
        ed.setText(MConfig.Get_String("Main","QQCleaner","BubbleClear"));

        new AlertDialog.Builder(context,3)
                .setTitle("设置规则(用|分隔)")
                .setView(ed)
                .setNeutralButton("确定", (dialog, which) -> {
                    MConfig.Put_String("Main","QQCleaner","BubbleClear",ed.getText().toString());
                }).show();
    }
    public static void Start() throws Exception {
        Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenu"),"a",void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem")
        });
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String Name = MField.GetField(param.args[0],"a",String.class);
                String sSet = MConfig.Get_String("Main","QQCleaner","BubbleClear");
                String[] s = sSet.split("\\|");
                for(String item : s)
                {
                    if(item.equals(Name))
                    {
                        param.setResult(null);
                        return;
                    }
                }
            }
        });
    }

}
