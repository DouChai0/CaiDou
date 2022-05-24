package com.ldq.connect.MainWorker.ProxyHook;


import android.util.Log;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.JavaPlugin.JavaPlugin;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_ExitAndKick_Log;
import com.ldq.connect.MainWorker.QQManagerHelper.TroopManager_Handler_Mute_Continue;
import com.ldq.connect.MainWorker.QQManagerHelper.TroopManager_Handler_Mute_Log;
import com.ldq.connect.MainWorker.QQManagerHelper.TroopManager_Handler_WhiteBlack_Checker;
import com.ldq.connect.MainWorker.QQManagerHelper.TroopManager_Set_Alarm_Group;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.QQTools;
import com.ldq.connect.QQUtils.TroopManager;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Random;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_Troop_Event
{
    public static HashSet<String> mExitList = new HashSet<>();
    public static HashMap<String,String> tipCache = new HashMap<>();
    public static HashMap<String,String> nickCache = new HashMap<>();
    public static void Start() throws ClassNotFoundException {

        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.troop.api.impl.TroopCreateInfoServiceImpl", MHookEnvironment.mLoader, "deleteInInviteList",
                String.class, String.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);

                        String GroupUin = (String) param.args[0];
                        String UserUin = (String) param.args[1];
                        //MLogCat.Print_Info("Troop Event Exit",GroupUin+"-"+UserUin);
                        if(mExitList.contains(GroupUin+"-"+UserUin)) return;
                        mExitList.add(GroupUin+"-"+UserUin);

                        JavaPlugin.BshOnTroopEvent(GroupUin,UserUin,1);

                        TroopManager_Set_Alarm_Group.CheckExit(GroupUin,UserUin);
                        Handler_ExitAndKick_Log._caller(GroupUin,UserUin,"1");
                        //Handler_TroopExit_Tip._caller(String.valueOf(GroupUin),String.valueOf(UserUin),2,"");
                    }
                }
        );
        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.troop.api.impl.TroopCreateInfoServiceImpl", MHookEnvironment.mLoader, "isInInviteList",
                String.class,
                String.class,
                new XC_MethodHook() {
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if(!((boolean) param.getResult()))
                        {
                            String GroupUin = String.valueOf(param.args[0]);
                            String UserUin = (String) param.args[1];
                            TroopManager_Handler_WhiteBlack_Checker.JoinInHandler(GroupUin,UserUin,0,null);
                            //MLogCat.Print_Info("Troop Event Join",GroupUin+"-"+UserUin);
                            if(mExitList.contains(GroupUin+"-"+UserUin)) mExitList.remove(GroupUin+"-"+UserUin);

                            if (tipCache.containsKey(GroupUin+"-"+UserUin)){
                                String opUin = tipCache.get(GroupUin+"-"+UserUin);
                                tipCache.remove(opUin);

                                QQMessage.AddTip("成员"+ (nickCache.get(UserUin) == null ? TroopManager.GetMemberName(GroupUin,UserUin) : nickCache.get(UserUin))
                                        +"("+UserUin+")由"+TroopManager.GetMemberName(GroupUin,opUin)+"("+opUin+")同意加入本群",System.currentTimeMillis()/1000,(long) Math.abs(new Random().nextInt()),new Random().nextInt(),GroupUin,""
                                );
                            }
                            JavaPlugin.BshOnTroopEvent(GroupUin,UserUin,2);
                            TroopManager_Handler_Mute_Continue.OnCallBackJoinTroop(GroupUin,UserUin);

                        }

                    }
                }
        );
        ForbiddenEventCheck();
        JoinNotifyTip();
    }
    private static void JoinNotifyTip(){
        try{
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.service.message.codec.decoder.TroopAddMemberBroadcastDecoder"), "a",
                    "com.tencent.mobileqq.app.QQAppInterface",int.class,String.class,String.class,long.class,MClass.loadClass("msf.msgcomm.msg_comm$MsgHead"), new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String opUin = (String) param.args[3];
                    String memberUin = (String) param.args[2];
                    long troopUin = (long) param.args[4];

                    tipCache.put(troopUin+"-"+memberUin,opUin);

                    Object head = param.args[5];
                    Object nick = MField.GetField(head,"auth_nick");
                    String nick_str = MField.GetField(nick,"value");
                    nickCache.put(memberUin,nick_str);
                }
            });
        }catch (Exception e){
            MLogCat.Print_Error("Join_Event_Decoder",e);
        }
    }
    private static void ForbiddenEventCheck()
    {
        try{
            Method mHook = MMethod.FindMethod("com.tencent.mobileqq.troop.utils.TroopGagMgr","a",void.class,new Class[]{
                    String.class,
                    long.class,
                    long.class,
                    int.class,
                    String.class,
                    String.class,
                    boolean.class
            });
            XposedBridge.hookMethod(mHook, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    String GroupUin = (String) param.args[0];
                    long TimeRest = (long) param.args[2];
                    String AdminUin = (String) param.args[4];
                    String Target = (String) param.args[5];

                    TroopManager_Handler_Mute_Log.Handler_Forbidden_Event_Log(GroupUin,Target,AdminUin,TimeRest,"默认禁言记录");

                    if(!AdminUin.equals(BaseInfo.GetCurrentUin()))
                    {
                        TroopManager_Handler_Mute_Continue.LockMode(GroupUin,Target,System.currentTimeMillis()+TimeRest*1000);
                    }else
                    {
                        if(TimeRest==0)
                            MConfig.Troop_Put_Long("TroopGuard","ForbiddenLock",GroupUin,Target,0);
                    }


                    /*
                    MLogCat.Print_Info("TroopGag","Groupuin="
                            +GroupUin+";Time="
                            +TimeRest+";AdminUin="+AdminUin
                            +";Target="+Target
                            );

                     */


                    //显示设置禁言的管理
                    try{
                        if (MConfig.Get_Boolean("Main","MainSwitch","显示禁言",false)) {
                            if(!AdminUin.equals(BaseInfo.GetCurrentUin())) {
                                    QQTools.HighLightItem[] mItems = new QQTools.HighLightItem[2];
                                    mItems[0] = new QQTools.HighLightItem();
                                    String Name1 = TroopManager.GetMemberName(GroupUin,Target);
                                    mItems[0].Uin = Target;
                                    mItems[0].Start = 0;
                                    mItems[0].End = Name1.length();
                                    String ShowText = Name1+"被";

                                    mItems[1] = new QQTools.HighLightItem();
                                    mItems[1].Uin = AdminUin;
                                    mItems[1].Start = ShowText.length();
                                    Name1 = TroopManager.GetMemberName(GroupUin,AdminUin);

                                    mItems[1].End = ShowText.length()+Name1.length();
                                    ShowText = ShowText + Name1;
                                    if(TimeRest==0) ShowText = ShowText + "解除禁言";
                                    else ShowText = ShowText + "禁言" + Utils.secondToTime(TimeRest);
                                    QQTools.AddClickItem(GroupUin,ShowText,mItems);
                                    param.setResult(null);
                            }

                        }
                    }
                    catch (Throwable th) {
                        MLogCat.Print_Error("ShowForbidden",th);
                    }



                    MHookEnvironment.mTask.PostTask(()->JavaPlugin.BshOnTroopEvent2(GroupUin, Target,AdminUin,TimeRest));
                    if(Target.equals(BaseInfo.GetCurrentUin()))return;
                    if(MConfig.Get_Boolean("TroopGuard","ForbiddenContinueOpen",GroupUin,false))
                    {
                        if(TimeRest==0) {
                            MConfig.Put_Long("TroopGuard","ForbiddenContinueTime",GroupUin+"&"+Target,System.currentTimeMillis());
                        }
                        else {
                            MConfig.Put_Long("TroopGuard","ForbiddenContinueTime",GroupUin+"&"+Target,System.currentTimeMillis()+TimeRest*1000);
                        }
                    }

                }
            });
            mHook = MMethod.FindMethod("com.tencent.mobileqq.troop.utils.TroopGagMgr","a",void.class,new Class[]{
                    String.class,
                    String.class,
                    long.class,
                    long.class,
                    int.class,
                    boolean.class,
                    boolean.class
            });
            XposedBridge.hookMethod(mHook, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);

                    String GroupUin = (String) param.args[0];
                    String AdminUin = (String) param.args[1];
                    long TimeRest = (long) param.args[3];
                    boolean b = (boolean) param.args[5];


                    if(!b) {
                        TroopManager_Handler_Mute_Log.Handler_Forbidden_Event_Log(GroupUin,BaseInfo.GetCurrentUin(),AdminUin,TimeRest,"默认禁言记录");
                    }


                    if(b) {
                        MHookEnvironment.mTask.PostTask(()->JavaPlugin.BshOnTroopEvent2(GroupUin, "",AdminUin,TimeRest));
                    }else {
                        MHookEnvironment.mTask.PostTask(()->JavaPlugin.BshOnTroopEvent2(GroupUin, BaseInfo.GetCurrentUin(),AdminUin,TimeRest));
                    }


                    try {
                        if (MConfig.Get_Boolean("Main","MainSwitch","显示禁言",false)) {
                            param.setResult(null);
                            if(b) {
                                QQTools.HighLightItem[] mItems = new QQTools.HighLightItem[1];
                                String Name1 = TroopManager.GetMemberName(GroupUin,AdminUin);
                                String ShowText = "";

                                mItems[0] = new QQTools.HighLightItem();
                                mItems[0].Uin = AdminUin;
                                mItems[0].Start = ShowText.length();
                                mItems[0].End = ShowText.length()+Name1.length();


                                ShowText = ShowText + Name1;
                                if(TimeRest==0) ShowText = ShowText + "关闭了全员禁言";
                                else            ShowText = ShowText + "开启了全员禁言";


                                QQTools.AddClickItem(GroupUin,ShowText,mItems);
                                return;
                            }
                            QQTools.HighLightItem[] mItems = new QQTools.HighLightItem[1];
                            String Name1 = TroopManager.GetMemberName(GroupUin,AdminUin);
                            String ShowText = "你被";

                            mItems[0] = new QQTools.HighLightItem();
                            mItems[0].Uin = AdminUin;
                            mItems[0].Start = ShowText.length();
                            mItems[0].End = ShowText.length()+Name1.length();


                            ShowText = ShowText + Name1;
                            if(TimeRest==0) ShowText = ShowText + "解除禁言";
                            else            ShowText = ShowText + "禁言" + Utils.secondToTime(TimeRest);
                            QQTools.AddClickItem(GroupUin,ShowText,mItems);
                        }
                    }
                    catch (Throwable th) {
                        MLogCat.Print_Error("ShowForbidden",th);
                    }
                }
            });
        }
        catch (Throwable th) {
            MLogCat.Print_Error("Forbidden",th);
        }

    }

}
