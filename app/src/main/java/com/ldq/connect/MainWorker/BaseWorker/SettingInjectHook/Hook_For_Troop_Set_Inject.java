package com.ldq.connect.MainWorker.BaseWorker.SettingInjectHook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.os.Bundle;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MTool.AutoCheckSign;
import com.ldq.connect.MTool.AutoMessage;
import com.ldq.connect.MainWorker.BaseWorker.BaseMenu.Menu_Adv_TroopManager;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_ExitAndKick_Log;
import com.ldq.connect.MainWorker.QQManagerHelper.TroopManager_Handler_WhiteBlack_Checker;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.FormItem;
import com.ldq.connect.QQUtils.TroopManager;
import com.ldq.connect.ServerTool.UserStatus;
import com.ldq.connect.Tools.MyTimePicker;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_For_Troop_Set_Inject extends XC_MethodHook {
    public static LinearLayout l;
    public static void Start() {
        try{

            //在群聊设置里面设置的Hook项目
            Method hookMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.troopsetting.activity.TroopSettingActivity","doOnCreate",boolean.class,new Class[]{Bundle.class});
            XposedBridge.hookMethod(hookMethod, new XC_MethodHook(99) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    try{
                        View mGetItem = MField.GetFirstField(param.thisObject,param.thisObject.getClass(), MClass.loadClass("com.tencent.mobileqq.widget.QFormSimpleItem"));
                        if(mGetItem!=null)
                        {
                            LinearLayout mRootView = (LinearLayout) mGetItem.getParent();
                            if(mRootView!=null)
                            {
                                String GroupUin = GetTroopUinFromObject(param.thisObject);
                                View mSetItem = FormItem.AddCommonListItem(mRootView.getContext(),"群聊小助手", vx -> Show_Content_View(GroupUin));
                                mRootView.addView(mSetItem,14);
                            }
                        }
                    }
                    catch (Throwable th)
                    {
                        MLogCat.Print_Error("AddSettingsError",th);
                    }
                }
            });
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("TroopSettingsHook",th);
        }

    }
    public static void Show_Content_View(String TroopUin){
        try{
            Context context = Utils.GetThreadActivity();
            LinearLayout mRootView = new LinearLayout(context);
            mRootView.setOrientation(LinearLayout.VERTICAL);


            //辅助群管
            if(MConfig.Get_Boolean("Main","MainSwitch","辅助群管",false)) {
                if(TroopManager.IsGroupAdmin(TroopUin))
                {
                    View mSetItem = FormItem.AddListItem(mRootView.getContext(),"简易管理设置", vx -> {
                        new AlertDialog.Builder(mRootView.getContext(),AlertDialog.THEME_HOLO_LIGHT)
                                .setView(GetSettingView(mRootView.getContext(),TroopUin)).show();
                    });
                    mRootView.addView(mSetItem);
                }

            }



            //自动打卡
            if(MConfig.Get_Boolean("Main","MainSwitch","自动打卡",false)) {
                View mSetItem = FormItem.AddCheckItem(mRootView.getContext(),"小菜豆自动打卡",(vx, checked) -> {
                    if(checked)
                    {
                        AutoCheckSign.AddSignList(TroopUin);
                    }else {
                            AutoCheckSign.RemoveCheck(TroopUin);
                      }
                    }, AutoCheckSign.CheckSignOpen(TroopUin));
                mRootView.addView(mSetItem);
            }


            //查看本群禁言数据 按钮
            {
                View mSetItem = FormItem.AddListItem(mRootView.getContext(),"查看本群禁言数据", vx -> {
                    Intent intent = new Intent();
                    intent.putExtra("troopuin",TroopUin);
                    intent.setClassName(context,"com.tencent.mobileqq.activity.TroopGagActivity");
                    context.startActivity(intent);
                });
                mRootView.addView(mSetItem);
            }

            //查看本群活跃数据 按钮
            {
                mRootView.addView(FormItem.AddListItem(context,"查看本群活跃数据",v->{
                    Intent intent = new Intent();
                    intent.putExtra("url", "https://qun.qq.com/m/qun/activedata/active.html?_wv=3&_bid=128&gc=" + TroopUin + "&src=2");
                    intent.putExtra("PARAM_PLUGIN_INTERNAL_ACTIVITIES_ONLY", false);
                    intent.putExtra("leftViewText", "返回");
                    intent.setClassName(context, "com.tencent.mobileqq.activity.QQBrowserActivity");
                    context.startActivity(intent);

                }));
            }{
                mRootView.addView(FormItem.AddListItem(context,"查看本群信用星级",v->{
                    Intent intent = new Intent();
                    intent.putExtra("url", "https://qqweb.qq.com/m/business/qunlevel/index.html?gc=" + TroopUin);
                    intent.putExtra("PARAM_PLUGIN_INTERNAL_ACTIVITIES_ONLY", false);
                    intent.putExtra("leftViewText", "返回");
                    intent.setClassName(context, "com.tencent.mobileqq.activity.QQBrowserActivity");
                    context.startActivity(intent);

                }));
            }

            {

                mRootView.addView(FormItem.AddListItem(context,"设置本群退群/踢出日志",v->Handler_ExitAndKick_Log.OpenLogView(TroopUin)));

            }

            //群管助手按钮
            if(MConfig.Get_Boolean("Main","MainSwitch","群管助手",false) && UserStatus.CheckIsDonator()) {
                View mSetItem = FormItem.AddListItem(mRootView.getContext(),"群管助手", vx -> {
                    Menu_Adv_TroopManager.ShowSettingForManager();
                });
                mRootView.addView(mSetItem);

            }
            //自动消息按钮
            if(MConfig.Get_Boolean("Main","MainSwitch","自动消息",false) && UserStatus.CheckIsDonator()) {
                        View mSetItem = FormItem.AddListItem(mRootView.getContext(),"定时发送消息", vx -> {
                            AutoMessage.ShowSetAutoMessageDialog(context,TroopUin,true);
                        });
                        mRootView.addView(mSetItem);
            }

            //群管黑白名单按钮
            {
                mRootView.addView(FormItem.AddListItem(context,"设置群管黑白名单",v->{
                    TroopManager_Handler_WhiteBlack_Checker.ShowSet(TroopUin);
                }));
            }


            new AlertDialog.Builder(context,3)
                    .setView(mRootView)
                    .show();
        }catch (Exception e){
            Utils.ShowToast("发生错误:\n"+e);
        }
    }
    public static View GetSettingView(Context mContext, String TroopUin) {
        LinearLayout ll = new LinearLayout(mContext);
        ll.setOrientation(LinearLayout.VERTICAL);



        ll.addView(FormItem.AddMultiItem(mContext,"禁言继承","禁言状态退群再加自动补上",(buttonView, isChecked) -> MConfig.Put_Boolean("TroopGuard","ForbiddenContinueOpen",TroopUin,isChecked),MConfig.Get_Boolean("TroopGuard","ForbiddenContinueOpen",TroopUin,false)));


        View vv = FormItem.AddCheckItem(mContext,"贴图禁言",(buttonView, isChecked) -> MConfig.Troop_Put_Boolean("TroopGuard","Simple",TroopUin,"StickPicOpen",isChecked),MConfig.Troop_Get_Boolean("TroopGuard","Simple",TroopUin,"StickPicOpen",false));
        vv.setOnClickListener(v -> {
            AlertDialog mAlert = new AlertDialog.Builder(v.getContext(), 3).create();
            mAlert.setTitle("禁言时间");
            LinearLayout mLL = new LinearLayout(v.getContext());
            mAlert.setView(mLL);
            mLL.setOrientation(LinearLayout.VERTICAL);
            long TimeRest = MConfig.Troop_Get_Long("TroopGuard","Simple",TroopUin,"StickPic",0);
            int day = (int) (TimeRest / (3600*24));
            TimeRest = TimeRest % (3600*24);
            int hours = (int) (TimeRest / (3600));
            TimeRest = TimeRest % (3600);
            int minute = (int) (TimeRest / (60));
            TimeRest = TimeRest % (60);

            MyTimePicker mPicker = new MyTimePicker(v.getContext(),day,hours,minute, (int) TimeRest);
            mLL.addView(mPicker);

            mAlert.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        MConfig.Troop_Put_Long("TroopGuard","Simple",TroopUin,"StickPic",mPicker.GetSecond());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            mAlert.show();
        });
        ll.addView(vv);

        vv = FormItem.AddCheckItem(mContext,"秀图禁言",(buttonView, isChecked) -> MConfig.Troop_Put_Boolean("TroopGuard","Simple",TroopUin,"EffectPicOpen",isChecked),MConfig.Troop_Get_Boolean("TroopGuard","Simple",TroopUin,"EffectPicOpen",false));
        vv.setOnClickListener(v -> {
            AlertDialog mAlert = new AlertDialog.Builder(v.getContext(), 3).create();
            mAlert.setTitle("禁言时间");
            LinearLayout mLL = new LinearLayout(v.getContext());
            mAlert.setView(mLL);
            mLL.setOrientation(LinearLayout.VERTICAL);
            long TimeRest = MConfig.Troop_Get_Long("TroopGuard","Simple",TroopUin,"EffectPic",0);
            int day = (int) (TimeRest / (3600*24));
            TimeRest = TimeRest % (3600*24);
            int hours = (int) (TimeRest / (3600));
            TimeRest = TimeRest % (3600);
            int minute = (int) (TimeRest / (60));
            TimeRest = TimeRest % (60);

            MyTimePicker mPicker = new MyTimePicker(v.getContext(),day,hours,minute, (int) TimeRest);
            mLL.addView(mPicker);

            mAlert.setButton(AlertDialog.BUTTON_POSITIVE, "确定", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    try {
                        MConfig.Troop_Put_Long("TroopGuard","Simple",TroopUin,"EffectPic",mPicker.GetSecond());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                }
            });
            mAlert.show();
        });
        ll.addView(vv);

        vv = FormItem.AddMultiItem(mContext,"复读禁言","点击打开设置");
        vv.setOnClickListener(v -> {
            AlertDialog mAlert = new AlertDialog.Builder(v.getContext(), 3).create();
            mAlert.setTitle("设置复读禁言");
            LinearLayout mLL = new LinearLayout(v.getContext());
            mAlert.setView(mLL);
            mLL.setOrientation(LinearLayout.VERTICAL);
            TextView t = new TextView(v.getContext());
            t.setText("设置复读数量(0为关闭)");
            t.setTextColor(Color.BLACK);
            t.setTextSize(20);
            mLL.addView(t);

            EditText ed_1 = new EditText(v.getContext());
            ed_1.setTextSize(20);
            ed_1.setText(MConfig.Troop_Get_String("TroopGuard","RepeatForbidden",TroopUin,"Rules"));
            mLL.addView(ed_1);

            t = new TextView(v.getContext());
            t.setText("设置复读消息间隔数(不知道是啥就保持0)");
            t.setTextColor(Color.BLACK);
            t.setTextSize(20);
            mLL.addView(t);


            EditText ed_2 = new EditText(v.getContext());
            ed_2.setTextSize(20);
            ed_2.setText(MConfig.Troop_Get_Long("TroopGuard","RepeatForbidden",TroopUin,"RulesCross",0)+"");
            mLL.addView(ed_2);

            CheckBox chs = new CheckBox(v.getContext());
            chs.setText("嘿嘿嘿");
            chs.setTextColor(Color.BLACK);
            chs.setChecked(MConfig.Troop_Get_Boolean("TroopGuard","RepeatForbidden",TroopUin,"Random",false));
            mLL.addView(chs);




            long TimeRest = MConfig.Troop_Get_Long("TroopGuard","RepeatForbidden",TroopUin,"time",0);
            int day = (int) (TimeRest / (3600*24));
            TimeRest = TimeRest % (3600*24);
            int hours = (int) (TimeRest / (3600));
            TimeRest = TimeRest % (3600);
            int minute = (int) (TimeRest / (60));
            TimeRest = TimeRest % (60);

            MyTimePicker mPicker = new MyTimePicker(v.getContext(),day,hours,minute, (int) TimeRest);
            mLL.addView(mPicker);


            mAlert.setButton(AlertDialog.BUTTON_POSITIVE, "确定", (dialog, which) -> {
                try {
                    MConfig.Troop_Put_Long("TroopGuard","RepeatForbidden",TroopUin,"time",mPicker.GetSecond());
                    MConfig.Troop_Put_String("TroopGuard","RepeatForbidden",TroopUin,"Rules",ed_1.getText().toString());
                    MConfig.Troop_Put_Long("TroopGuard","RepeatForbidden",TroopUin,"RulesCross",Long.parseLong(ed_2.getText().toString()));
                    MConfig.Troop_Put_Boolean("TroopGuard","RepeatForbidden",TroopUin,"Random",chs.isChecked());

                } catch (Exception e) {
                    e.printStackTrace();
                }

            });
            mAlert.show();
        });
        ll.addView(vv);

        return ll;
    }
    public static String GetTroopUinFromObject(Object TroopObj) throws Exception {
        Object data = FieldTable.TroopSettingActivity_TroopInfoData().get(TroopObj);
        String Uin = MField.GetField(data,"troopUin",String.class);
        return Uin;
    }
    public static boolean isTroopAdmin(Object TroopObj) throws Exception {
        Object data = FieldTable.TroopSettingActivity_TroopInfoData().get(TroopObj);
        String Admin = MField.GetField(data,"Administrator",String.class);
        String Ownen = MField.GetField(data,"troopowneruin",String.class);
        String MyUin = BaseInfo.GetCurrentUin();
        if(!MyUin.equals(Ownen) && !Admin.contains(MyUin))return false;
        return true;
    }
}
