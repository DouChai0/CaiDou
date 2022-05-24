package com.ldq.connect.MainWorker.WidgetHandler;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MainWorker.BaseWorker.EnvHook.BasePieHook;
import com.ldq.connect.QQUtils.BaseCall;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.FormItem;
import com.ldq.connect.QQUtils.QQSessionUtils;
import com.ldq.connect.QQUtils.QQTools;
import com.ldq.connect.QQUtils.TroopManager;
import com.ldq.connect.Tools.MyTimePicker;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Handler_Avatar_Long_Click_Common implements View.OnLongClickListener{
    public View.OnLongClickListener SourceListener;
    public Object mMsgObj;

    public Handler_Avatar_Long_Click_Common(Object MessageObj, View.OnLongClickListener source)
    {
        mMsgObj = MessageObj;
        SourceListener = source;
    }
    @Override
    public boolean onLongClick(View v) {
        if (!MConfig.Get_Boolean("Main","MainSwitch","便捷菜单",false)) {
            if (SourceListener == null) return false;
            if(SourceListener.getClass().equals(Handler_Avatar_Long_Click_Common.class)) return false;


            return SourceListener.onLongClick(v);
        }
        try {
            String ClickUin = MField.GetField(mMsgObj, mMsgObj.getClass(), "senderuin", String.class);
            if(QQSessionUtils.GetSessionID()==1000){
                if(MMethod.CallMethod(mMsgObj,"isSendFromLocal",boolean.class,new Class[0]))
                {
                    ClickUin = BaseInfo.GetCurrentUin();
                }else
                {
                    ClickUin = QQSessionUtils.GetCurrentFriendUin();
                }

            }

            String GroupUin = MField.GetField(mMsgObj, mMsgObj.getClass(), "frienduin", String.class);



            LinearLayout mLLLayout = new LinearLayout(v.getContext());

            AlertDialog al =new AlertDialog.Builder(v.getContext(),AlertDialog.THEME_HOLO_LIGHT)
                    .setTitle("操作:"+ TroopManager.GetMemberName(GroupUin,ClickUin)+"("+ClickUin+")")
                    .setView(mLLLayout)
                    .create();

            mLLLayout.setOrientation(LinearLayout.VERTICAL);

            mLLLayout.setBackgroundColor(Color.BLACK);
            mLLLayout.getBackground().setAlpha(150);
            String UserUin = ClickUin;
            if(QQSessionUtils.GetSessionID()==1)
            {

                mLLLayout.addView(FormItem.AddListItem(v.getContext(),"艾特", vx -> {
                    BasePieHook.Add_At_Text(GroupUin, UserUin);
                    al.dismiss();
                }));
            }

            if (ClickUin.equals(BaseInfo.GetCurrentUin()) || ((TroopManager.IsGroupOwner(GroupUin)) || (TroopManager.IsGroupAdmin(GroupUin) && !TroopManager.IsGroupAdmin(GroupUin,ClickUin)))) {
                mLLLayout.addView(FormItem.AddListItem(v.getContext(),"撤回", vx -> {
                    try {
                        BaseCall.RemoveMessage(mMsgObj);
                    } catch (Exception e) {
                        MLogCat.Print_Error("撤回失败",e);
                    }
                    al.dismiss();
                }));
            }

            if (!ClickUin.equals(BaseInfo.GetCurrentUin()) && ((TroopManager.IsGroupOwner(GroupUin)) || (TroopManager.IsGroupAdmin(GroupUin) && !TroopManager.IsGroupAdmin(GroupUin,ClickUin)))) {
                String finalClickUin = ClickUin;
                mLLLayout.addView(FormItem.AddListItem(v.getContext(),"踢出", vx -> {
                    AlertDialog mAlert = new AlertDialog.Builder(vx.getContext(), 3).create();
                    mAlert.setButton(AlertDialog.BUTTON_POSITIVE, "取消", (dialog, which) -> {

                    });
                    mAlert.setButton(AlertDialog.BUTTON_NEGATIVE, "踢出", (dialog, which) -> {
                        TroopManager.Group_Kick(GroupUin, finalClickUin,false);
                    });
                    mAlert.setButton(AlertDialog.BUTTON_NEUTRAL, "踢出并拒绝加入", (dialog, which) -> {
                        TroopManager.Group_Kick(GroupUin,finalClickUin,true);
                    });
                    mAlert.setTitle("你真的要踢出"+ TroopManager.GetMemberName(GroupUin,finalClickUin)+"("+finalClickUin+")吗?");
                    mAlert.show();
                    al.dismiss();
                }));


                String finalClickUin1 = ClickUin;
                mLLLayout.addView(FormItem.AddListItem(v.getContext(),"禁言", vvvv ->{
                    AlertDialog mAlert = new AlertDialog.Builder(vvvv.getContext(), 3).create();
                    mAlert.setTitle("禁言时间");
                    LinearLayout mLL = new LinearLayout(vvvv.getContext());
                    mAlert.setView(mLL);
                    mLL.setOrientation(LinearLayout.VERTICAL);

                    MyTimePicker mPicker = new MyTimePicker(vvvv.getContext());
                    mLL.addView(mPicker);

                    CheckBox ch = new CheckBox(vvvv.getContext());
                    ch.setText("锁定模式");
                    ch.setTextColor(Color.BLACK);

                    mLL.addView(ch);

                    mAlert.setButton(AlertDialog.BUTTON_POSITIVE, "确定", (dialog, which) -> {
                        try {
                            if(ch.isChecked())
                            {
                                MConfig.Troop_Put_Long("TroopGuard","ForbiddenLock",GroupUin,finalClickUin1,System.currentTimeMillis()+mPicker.GetSecond()*1000);
                            }
                            TroopManager.Group_Forbidden(GroupUin, finalClickUin1,mPicker.GetSecond());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                    mAlert.show();
                    al.dismiss();

                }));
            }


            mLLLayout.addView(FormItem.AddListItem(v.getContext(),"复制QQ", vv -> {
                String mUin = null;
                try {
                    mUin = MField.GetField(mMsgObj, mMsgObj.getClass(), "senderuin", String.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mUin != null) {
                    Utils.SetTextClipboard(mUin);
                    Utils.ShowToast("已复制");
                }
                al.dismiss();
            }));


            if(TroopManager.IsGroupAdmin(GroupUin))
            {
                String finalClickUin2 = ClickUin;
                mLLLayout.addView(FormItem.AddListItem(v.getContext(),"修改群名片", vx -> {
                    AlertDialog mAlert = new AlertDialog.Builder(vx.getContext(), 3).create();
                    mAlert.setTitle("请输入需要修改的名片");
                    LinearLayout mLL = new LinearLayout(vx.getContext());
                    mAlert.setView(mLL);
                    mLL.setOrientation(LinearLayout.VERTICAL);
                    EditText mText = new EditText(vx.getContext());
                    mText.setTextSize(16);
                    mText.setTextColor(Color.BLACK);
                    mText.setText(TroopManager.GetMemberName(GroupUin, finalClickUin2));
                    mLL.addView(mText);
                    mAlert.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                String Name = mText.getText().toString();
                                TroopManager.Group_ChangeName(GroupUin, finalClickUin2,Name);
                            } catch (Exception e) {
                                MLogCat.Print_Error("修改名片",e);
                            }

                        }
                    });

                    mAlert.show();
                    al.dismiss();
                }));




                /*
                if(MConfig.Get_Boolean("Main","MainSwitch","群黑白名单",false))
                {
                    if(TroopManager_Handler_WhiteBlack_Checker.Check_While(GroupUin,UserUin)){
                        mLLLayout.addView(FormItem.AddListItem(v.getContext(),"删除白名单", vx -> {
                            TroopManager_Handler_WhiteBlack_Checker.SetWhileListStatus(GroupUin,UserUin,false);
                            al.dismiss();
                        }));
                    }else{
                        mLLLayout.addView(FormItem.AddListItem(v.getContext(),"添加白名单", vx -> {
                            TroopManager_Handler_WhiteBlack_Checker.SetWhileListStatus(GroupUin,UserUin,true);
                            al.dismiss();
                        }));
                    }

                    if(TroopManager_Handler_WhiteBlack_Checker.Check_Black(GroupUin,UserUin)){
                        mLLLayout.addView(FormItem.AddListItem(v.getContext(),"删除黑名单", vx -> {
                            TroopManager_Handler_WhiteBlack_Checker.SetBlackListStatus(GroupUin,UserUin,false);
                            al.dismiss();
                        }));
                    }else{
                        mLLLayout.addView(FormItem.AddListItem(v.getContext(),"添加黑名单", vx -> {
                            TroopManager_Handler_WhiteBlack_Checker.SetBlackListStatus(GroupUin,UserUin,true);
                            al.dismiss();
                        }));
                    }
                }

                 */
            }






            if(TroopManager.IsGroupOwner(GroupUin))
            {
                String finalClickUin3 = ClickUin;
                mLLLayout.addView(FormItem.AddListItem(v.getContext(),"修改头衔", vv -> {
                    AlertDialog mAlert = new AlertDialog.Builder(v.getContext(), 3).create();
                    LinearLayout mLL = new LinearLayout(vv.getContext());
                    mAlert.setView(mLL);
                    mLL.setOrientation(LinearLayout.VERTICAL);

                    TextView mView = new TextView(vv.getContext());
                    mView.setText("在下方输入要修改的头衔");
                    mView.setTextSize(30);
                    mView.setTextColor(Color.BLACK);
                    mLL.addView(mView);

                    EditText mText = new EditText(vv.getContext());
                    mText.setTextColor(Color.BLACK);
                    mText.setTextSize(30);
                    mText.setText(TroopManager.Group_GetUserTitle(GroupUin, finalClickUin3));
                    mLL.addView(mText);

                    Button btn = new Button(vv.getContext());
                    btn.setText("确定");
                    btn.setOnClickListener(vxx -> {
                        String mStr = mText.getText().toString();
                        try {
                            TroopManager.Group_Change_Title(GroupUin, finalClickUin3, mStr);
                            mAlert.dismiss();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    });
                    mLL.addView(btn);
                    mAlert.show();
                    al.dismiss();
                }));
            }

            mLLLayout.addView(FormItem.AddListItem(v.getContext(),"复制群名片", vv -> {
                String mUin = null;
                try {
                    mUin = MField.GetField(mMsgObj, mMsgObj.getClass(), "senderuin", String.class);
                } catch (Exception e) {
                    e.printStackTrace();
                }
                if (mUin != null) {
                    Utils.SetTextClipboard(TroopManager.GetMemberName(GroupUin,mUin));
                    Utils.ShowToast("已复制");
                }
                al.dismiss();
            }));

            if (!TextUtils.isEmpty(TroopManager.Group_GetUserTitle(GroupUin,ClickUin))){
                mLLLayout.addView(FormItem.AddListItem(v.getContext(),"复制成员头衔", vv -> {
                    String mUin = null;
                    try {
                        mUin = MField.GetField(mMsgObj, mMsgObj.getClass(), "senderuin", String.class);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    if (mUin != null) {
                        Utils.SetTextClipboard(TroopManager.Group_GetUserTitle(GroupUin,mUin));
                        Utils.ShowToast("已复制");
                    }
                    al.dismiss();
                }));
            }



            al.show();



            return true;
        } catch (Throwable th) {
            MLogCat.Print_Error("LongClickError", th);
        }
        return false;


    }
    public static void InitHook() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.vas.avatar.VasAvatar"),
                "setOnLongClickListener",
                MClass.loadClass("android.view.View$OnLongClickListener"),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(!MConfig.Get_Boolean("Main","MainSwitch","便捷菜单",false))return;

                        try{
                            View.OnLongClickListener mWillSet = (View.OnLongClickListener) param.args[0];
                            if(mWillSet==null)
                            {
                                param.setResult(null);
                                return;
                            }
                            View.OnLongClickListener mListener = MField.GetFirstField(param.thisObject,param.thisObject.getClass(),MClass.loadClass("android.view.View$OnLongClickListener"));


                            if(mListener!=null)
                            {
                                if(mListener.getClass().equals(Handler_Avatar_Long_Click_Common.class)) {
                                    if(mWillSet.getClass().equals(mListener.getClass()))return;

                                    MField.SetField(mListener,mListener.getClass(),"SourceListener",mWillSet);
                                    param.setResult(null);
                                    return;
                                }
                            }
                        }
                        catch (Throwable th)
                        {
                            MLogCat.Print_Error("AvatarError", th);
                        }

                    }
                }
        );
    }
    public static void _caller(RelativeLayout RL,Object ChatMsg) {
        if(MConfig.Get_Boolean("Main","MainSwitch","便捷菜单",false))
        {
            try{
                View vv = Handler_For_Repeat_Msg_Common.GetViewName("VasAvatar",RL);
                View.OnLongClickListener pOldListener = MField.GetField(vv,vv.getClass(),
                        "a|f", MClass.loadClass("android.view.View$OnLongClickListener")
                );
                vv.setOnLongClickListener(new Handler_Avatar_Long_Click_Common(ChatMsg,pOldListener));
            }
            catch (Throwable e)
            {
                MLogCat.Print_Error("Avatar",e);
            }
        }
    }
    public static void _caller_multimsg(RelativeLayout RL,Object ChatMsg) {
        try{
            String UserUin = MField.GetField(ChatMsg,"senderuin",String.class);
            View vv = Handler_For_Repeat_Msg_Common.GetViewName("VasAvatar",RL);
            if(vv!=null) {
                vv.setOnClickListener(v -> QQTools.OpenUserCard(UserUin));
            }
        }catch (Throwable th)
        {
            MLogCat.Print_Error("AvatarClick",th);
        }

    }
}
