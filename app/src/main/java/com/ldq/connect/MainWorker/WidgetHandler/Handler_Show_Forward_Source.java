package com.ldq.connect.MainWorker.WidgetHandler;

import android.app.Activity;
import android.view.View;
import android.widget.TextView;

import com.ldq.Utils.MField;
import com.ldq.connect.QQUtils.QQTools;
import com.ldq.connect.QQUtils.TroopManager;

public class Handler_Show_Forward_Source {
    public static void _caller(View v,Object ChatMsg)
    {
        try{
            Activity thisActivity = (Activity) v.getContext();
            if(thisActivity.getClass().getSimpleName().equals("MultiForwardActivity"))
            {
                int isTroop = MField.GetField(ChatMsg,ChatMsg.getClass() ,"istroop", int.class);
                if(isTroop==1)
                {
                    String Troop = MField.GetField(ChatMsg,ChatMsg.getClass() ,"frienduin", String.class);
                    View mRootView = thisActivity.getWindow().getDecorView();
                    int titleid = thisActivity.getResources().getIdentifier("title", "id", thisActivity.getPackageName());
                    View mtitleView = mRootView.findViewById(titleid);
                    if(mtitleView instanceof TextView)
                    {
                        TextView mView = (TextView) mtitleView;
                        mView.setText(""+ TroopManager.GetTroopName(Troop).replace("\n","")+"("+Troop+")");
                        mView.setWidth(8000);
                        mView.setOnClickListener(v1 -> QQTools.OpenTroopCard(Troop));
                    }
                }

            }

        }
        catch (Throwable th)
        {

        }
    }
}
