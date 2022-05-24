package com.ldq.connect.MainWorker.WidgetHandler;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.QQUtils.TroopManager;

import org.json.JSONObject;

import java.util.ArrayList;


public class Handler_Repeat_Icon_Long_Click implements View.OnLongClickListener {
    @Override
    public boolean onLongClick(View v) {
        try{
            Object obj = v.getTag();
            if(obj.getClass().getName().contains("MessageForText") || obj.getClass().getName().contains("MessageForReplyText"))
            {
                LinearLayout l = new LinearLayout(v.getContext());
                l.setOrientation(LinearLayout.VERTICAL);

                TextView t1 = new TextView(v.getContext());
                t1.setText("发送者");
                t1.setTextSize(16);
                t1.setTextColor(Color.BLACK);
                l.addView(t1);

                EditText ed = new EditText(v.getContext());
                String mSender =  MField.GetField(obj,obj.getClass(),"senderuin",String.class);
                ed.setText(mSender);
                ed.setTextSize(16);
                l.addView(ed);

                TextView t2 = new TextView(v.getContext());
                t2.setText( "消息内容");
                t2.setTextColor(Color.BLACK);
                t2.setTextSize(16);
                l.addView(t2);

                EditText msgChange = new EditText(v.getContext());
                msgChange.setText(MField.GetField(obj,obj.getClass(),"msg",String.class));
                msgChange.setTextSize(16);
                l.addView(msgChange);

                new AlertDialog.Builder(v.getContext(),AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("修改消息内容(仅自己看到)")
                        .setView(l)
                        .setNeutralButton("保存", (dialog, which) -> {
                            String Uin = ed.getText().toString();
                            String Content = msgChange.getText().toString();
                            try {
                                MField.SetField(obj,"msg",Content);
                                MField.SetField(obj,"senderuin",Uin);

                                MMethod.CallMethod(obj,"doParse",void.class,new Class[0]);
                                MMethod.CallMethod(obj,"prewrite",void.class,new Class[0]);
                            } catch (Exception exception) {
                                Utils.ShowToast("修改失败:\n"+exception);
                            }

                        }).show();

                return true;
            }
            if(obj.getClass().getName().contains("MessageForText") || obj.getClass().getName().contains("MessageForLongTextMsg") ||obj.getClass().getName().contains("MessageForFoldMsg"))
            {

                //创建对话框
                Dialog mDialog = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.utils.DialogUtil"),"a", MClass.loadClass("com.tencent.mobileqq.utils.QQCustomDialog"),new Class[]{Context.class,int.class},v.getContext(),0);

                //创建列表框架
                LinearLayout mLL = new LinearLayout(v.getContext());
                mLL.setOrientation(LinearLayout.VERTICAL);
                String mSender =  MField.GetField(obj,obj.getClass(),"senderuin",String.class);


                //创建显示发送者QQ的
                EditText mShowUinView = new EditText(v.getContext());//textview可以复制,应该好一点
                mShowUinView.setTextColor(Color.BLACK);
                mShowUinView.setText("发送者QQ:"+mSender);
                mShowUinView.setFocusableInTouchMode(false);//不可编辑
                mShowUinView.setTag(mSender);
                mShowUinView.setOnLongClickListener(new View.OnLongClickListener() {
                    @Override
                    public boolean onLongClick(View v) {
                        Utils.SetTextClipboard((String) v.getTag());
                        Utils.ShowToast("已复制QQ到剪贴板");
                        return true;
                    }
                });
                mLL.addView(mShowUinView);


                /*
                TextView TShowBubble = new TextView(v.getContext());
                TShowBubble.setTextColor(Color.BLACK);
                TShowBubble.setTextSize(16);
                long BubbleID = MField.GetField(obj,"vipBubbleID",long.class);
                int BubbleIDFinal = (int) (BubbleID & 0xFFFFFFFF);
                TShowBubble.setText("气泡ID:"+BubbleIDFinal);
                mLL.addView(TShowBubble);

                TShowBubble = new TextView(v.getContext());
                TShowBubble.setTextColor(Color.BLACK);
                TShowBubble.setTextSize(16);
                String JSON = MField.GetField(obj,obj.getClass(),"extStr",String.class);
                int FontID = 0;
                if(!TextUtils.isEmpty(JSON))
                {
                    JSONObject mJson = new JSONObject(JSON);
                    if(mJson.has("vip_font_id"))
                    {
                        String StrID = mJson.optString("vip_font_id");
                        if(!TextUtils.isEmpty(StrID))
                        {
                            FontID = Integer.parseInt(StrID);
                        }
                    }
                }

                TShowBubble.setText("字体ID:"+FontID);
                mLL.addView(TShowBubble);

                 */






                //获取并显示艾特对象
                /*
                这两个项目在消息刚发送或者接收的时候是存在数据的
                但是如果消息是从数据库取出来的,就永远成为了null
                所以得使用其他方式获取

                 */
                ArrayList AtList1 = MField.GetField(obj,obj.getClass(),"atInfoTempList",ArrayList.class);
                ArrayList AtList2 = MField.GetField(obj,obj.getClass(),"atInfoList",ArrayList.class);


                /*
                这个文本只要消息接收就一直存在
                可以通过解析里面的艾特对象来获取内容
                QQ正好提供了一个现成的方法来解析
                就可以直接使用了

                 */

                String mStr = MField.GetField(obj,obj.getClass(),"extStr",String.class);
                JSONObject mJson = new JSONObject(mStr);
                mStr = mJson.optString("troop_at_info_list");
                ArrayList AtList3 = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"),"getTroopMemberInfoFromExtrJson",ArrayList.class,new Class[]{String.class},mStr);



                if(AtList1==null)AtList1 = AtList2;
                if(AtList1==null)AtList1 = AtList3;
                if(AtList1!=null)
                {
                    for(int i=0;i<AtList1.size();i++)
                    {
                        //枚举艾特对象,并创建一个新的TextView来显示对象
                        Object mAtObj = AtList1.get(i);
                        long mLongData = MField.GetField(mAtObj,mAtObj.getClass(),"uin",long.class);
                        TextView mView = new TextView(v.getContext());
                        mView.setTextColor(Color.BLACK);
                        if(mLongData==0)
                        {
                            mView.setText("艾特:全体成员");
                        }
                        else
                        {
                            String mSUin = String.valueOf(mLongData);
                            String Group = MField.GetField(obj,obj.getClass(),"frienduin",String.class);

                            mView.setText("艾特QQ:"+ TroopManager.GetMemberName(Group,mSUin)+"("+mLongData+")");
                        }
                        mLL.addView(mView);
;                    }
                }
                MMethod.CallMethod(mDialog,mDialog.getClass(),"setView",mDialog.getClass(),new Class[]{View.class},mLL);
                mDialog.show();




            }
            if(obj.getClass().getName().contains("MessageForReplyText")){

            }
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("LongClickError2",th);
        }

        return true;
    }
}
