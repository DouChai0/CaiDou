package com.ldq.connect.MainWorker.WidgetHook;

import android.text.SpannableStringBuilder;
import android.text.SpannedString;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.QQUtils.TroopManager;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashSet;

public class Hook_Show_At_And_Time_Common
{
    public static void _call(View BaseItem,Object ChatMsg)
    {
        try{
            if(MConfig.Get_Boolean("Main","MainSwitch","显示艾特",false))
            {
                if(ChatMsg.getClass().getName().contains("MessageForText") || ChatMsg.getClass().getName().contains("MessageForLongTextMsg")
                        || ChatMsg.getClass().getName().contains("MessageForMixedMsg"))
                {

                    HashSet<String> mAtInfo = new HashSet();
                    String AtInfoShowList = "";
                    String mStr = MField.GetField(ChatMsg,ChatMsg.getClass(),"extStr",String.class);
                    JSONObject mJson = new JSONObject(mStr);

                    mStr = mJson.optString("troop_at_info_list");
                    if(!TextUtils.isEmpty(mStr))
                    {
                        ArrayList AtList3 = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"),"getTroopMemberInfoFromExtrJson",ArrayList.class,new Class[]{String.class},mStr);
                        String Group = MField.GetField(ChatMsg,ChatMsg.getClass(),"frienduin",String.class);

                        if(AtList3!=null )
                        {
                            for(Object mStrObj : AtList3)
                            {
                                long mLongData = MField.GetField(mStrObj,mStrObj.getClass(),"uin",long.class);
                                if(mLongData==0)
                                {
                                    mAtInfo.add("AtQQ:全体成员");
                                    continue;
                                }
                                mAtInfo.add("AtQQ:"+ TroopManager.GetMemberName(Group,""+mLongData)+"("+mLongData+")");
                            }
                            if(!mAtInfo.isEmpty())
                            {
                                for(String mShowInfo : mAtInfo) AtInfoShowList = AtInfoShowList + mShowInfo + "\n";
                                AtInfoShowList = AtInfoShowList.substring(0,AtInfoShowList.length()-1);
                                MMethod.CallMethod(BaseItem,"setTailMessage",void.class,new Class[]{boolean.class,CharSequence.class, MClass.loadClass("android.view.View$OnClickListener")},true,AtInfoShowList+" ",null);
                            }
                        }
                    }
                }
            }

        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("ShowAtInfo", Log.getStackTraceString(th));
        }
    }
    public static void _caller_time(View BaseItem,Object ChatMsg)
    {
        try{
            if(MConfig.Get_Boolean("Main","MainSwitch","显示时间",false))
            {
                String senderuin = MField.GetField(ChatMsg,ChatMsg.getClass(),"senderuin",String.class);
                long time = MField.GetField(ChatMsg,ChatMsg.getClass(),"time",long.class);
                CharSequence ItemText = GetItemContent(BaseItem);

                Date d = new Date(time*1000);

                SimpleDateFormat f=new SimpleDateFormat("MM-dd HH:mm:ss");

                if(ItemText instanceof SpannedString)
                {
                    SpannedString sp = (SpannedString) ItemText;
                    SpannableStringBuilder bu = new SpannableStringBuilder(sp);
                    if(bu.length()!=0)bu.append("\n");
                    bu.append("QQ:"+senderuin);
                    bu.append("  Time:"+f.format(d));
                    bu.append(" ");
                    ItemText = bu;
                }
                else
                {
                    if(!TextUtils.isEmpty(ItemText))ItemText = ItemText + "\n";
                    ItemText = ItemText+"QQ:"+senderuin;

                    ItemText = ItemText+"  Time:"+f.format(d);
                    ItemText = ItemText+" ";
                }


                MMethod.CallMethod(BaseItem,"setTailMessage",void.class,new Class[]{boolean.class,CharSequence.class, MClass.loadClass("android.view.View$OnClickListener")},true,ItemText,null);
            }

        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("ShowTimeInfo", Log.getStackTraceString(th));
        }
    }
    public static CharSequence GetItemContent(Object ItemObj)
    {
        try {
            TextView t = (TextView) FieldTable.BaseChatItemLayout_EdTailView().get(ItemObj);
            if(t!=null)
            {
                CharSequence Text = t.getText();
                return Text;
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return "";
    }
}
