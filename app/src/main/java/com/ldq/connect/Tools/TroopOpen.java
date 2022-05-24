package com.ldq.connect.Tools;

import android.app.Activity;
import android.app.AlertDialog;
import android.widget.EditText;

import com.ldq.Utils.Utils;
import com.ldq.connect.QQUtils.QQTools;

public class TroopOpen {
    public static void OpenDialog()
    {
        Activity act = Utils.GetThreadActivity();
        EditText ed = new EditText(act);
        ed.setTextSize(20);

        new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT)
                .setView(ed)
                .setTitle("打开群聊/好友界面")
                .setNegativeButton("打开好友界面", (dialog, which) -> {
                    String Uin = ed.getText().toString();
                    QQTools.OpenUserCard(Uin);
                }).setNeutralButton("打开群聊界面", (dialog, which) -> {
            String Uin = ed.getText().toString();
            QQTools.OpenTroopCard(Uin);
                }).show();
    }
}
