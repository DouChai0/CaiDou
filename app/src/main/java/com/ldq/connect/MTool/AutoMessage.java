package com.ldq.connect.MTool;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.View;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.ConfigPathBuilder;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.JavaPlugin.JavaPluginMethod;
import com.ldq.connect.QQUtils.FormItem;
import com.ldq.connect.QQUtils.QQSessionUtils;
import com.ldq.connect.QQUtils.TroopManager;
import com.ldq.connect.Tools.MyTimePicker;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Method;
import java.net.URLDecoder;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Iterator;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class AutoMessage {
    static JSONObject TaskInfo;
    public static void InitTaskInfo()
    {
        String str = FileUtils.ReadFileString(ConfigPathBuilder.Get_Current_Path_Set() + "自动消息.json");
        if(TextUtils.isEmpty(str))
        {
            TaskInfo = new JSONObject();
        }else {
            try {
                TaskInfo = new JSONObject(str);
            } catch (JSONException e) {
                TaskInfo = new JSONObject();
            }
        }
        try{
            StartHook();
        }catch (Exception ex)
        {
            MLogCat.Print_Error("HookForChatSettings",ex);
        }
        new Thread(AutoMessage::TimeThread).start();
    }
    private static void StartHook() throws Exception {
        Method hookMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatSettingActivity","doOnCreate",boolean.class,new Class[]{Bundle.class});
        XposedBridge.hookMethod(hookMethod, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                if(!MConfig.Get_Boolean("Main","MainSwitch","自动消息",false))return;

                Activity act = (Activity) param.thisObject;

                View mGetItem = (View) FieldTable.ChatSettingActivity_ASimpleItem().get(param.thisObject);
                if(mGetItem!=null)
                {
                    LinearLayout mRootView = (LinearLayout) mGetItem.getParent();
                    if(mRootView!=null)
                    {
                        View mSetItem = FormItem.AddCommonListItem(act,"自动消息", vx -> {
                            ShowSetAutoMessageDialog(act, QQSessionUtils.GetCurrentFriendUin(),false);
                        });
                        mRootView.addView(mSetItem,6);
                    }
                }
            }
        });


    }
    public static void ShowAllAutoMessageSetList(Context context)
    {
        Iterator<String> its = TaskInfo.keys();
        ArrayList<String> UinList = new ArrayList<>();
        while (its.hasNext())
        {
            try{
                String sUin = its.next();
                JSONArray array = TaskInfo.getJSONArray(sUin);
                if(array.length()>0) UinList.add(sUin);
            }catch (Exception ignored) { }
        }
        new AlertDialog.Builder(context,3)
                .setItems(UinList.toArray(new String[0]), (dialog, which) -> {
                    String uin = UinList.get(which);
                    try {
                        JSONArray mArray = TaskInfo.getJSONArray(uin);
                        JSONObject obj = mArray.getJSONObject(0);

                        ShowSetAutoMessageDialog(context,uin, obj.optBoolean("isTroop"));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }).show();

    }
    public static void ShowSetAutoMessageDialog(Context context,String Uin,boolean IsTroop)
    {
        ArrayList<String> TaskListName = new ArrayList<>();
        if(TaskInfo.has(Uin))
        {
            try {
                JSONArray array = TaskInfo.getJSONArray(Uin);
                for(int i=0;i<array.length();i++) {
                    JSONObject obj = array.getJSONObject(i);
                    String Name = URLDecoder.decode(obj.optString("TaskName"), "UTF-8");
                    TaskListName.add(Name);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        TaskListName.add("---添加一项新任务---");

        String[] strList = TaskListName.toArray(new String[0]);
        new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT)
                .setItems(strList, (dialog, which) -> {
                    if(which==strList.length-1)
                    {
                        AddNewTask(context,Uin,IsTroop);
                    }else
                    {
                        EditNowTask(context,Uin,which);
                    }
                }).show();
    }
    private static void AddNewTask(Context context,String Uin,boolean IsTroop)
    {
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);

        TextView t1 = new TextView(context);
        t1.setText("发送的内容");
        t1.setTextSize(16);
        t1.setTextColor(Color.BLACK);
        l.addView(t1);

        EditText ed = new EditText(context);
        ed.setHint("这里输入发送的内容,支持[PicUrl=图片地址]来插入图片,如果是群聊,可以输入[fall]来禁言全体,[ufall]来解禁全体,[atall]来艾特全体\n" +
                "消息是每天发送的,如果QQ运行在后台则会准时发送,如果QQ掉了后台过了时间则会在QQ打开的时候尝试发送一次(可以关闭下面的尝试重新发送来关闭)");
        ed.setGravity(Gravity.TOP);
        l.addView(ed);

        TextView t2 = new TextView(context);
        t2.setText("选择发送的时间");
        t2.setTextColor(Color.BLACK);
        t2.setTextSize(16);
        l.addView(t2);

        MyTimePicker TimePicker = new MyTimePicker(context,8,0,0);
        l.addView(TimePicker);

        CheckBox check = new CheckBox(context);
        check.setChecked(true);
        check.setText("尝试重新发送");
        check.setTextSize(16);
        check.setTextColor(Color.BLACK);
        l.addView(check);

        Calendar cal = Calendar.getInstance();
        long Time = cal.get(Calendar.HOUR_OF_DAY)*3600+cal.get(Calendar.MINUTE)*60+cal.get(Calendar.SECOND);
        int ToDay = cal.get(Calendar.DAY_OF_YEAR);


        new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("设置任务详情")
                .setView(l)
                .setNegativeButton("保存", (dialog, which) -> {
                    JSONArray mArray;
                    try{
                        mArray = TaskInfo.getJSONArray(Uin);
                    }catch (Exception e)
                    {
                        mArray = new JSONArray();
                    }

                    JSONObject mObj = new JSONObject();
                    try {
                        mObj.put("TaskName",ed.getText().toString());
                        mObj.put("TaskDelayTime",TimePicker.GetSecond());
                        mObj.put("isTroop",IsTroop);
                        mObj.put("IsRepeat",check.isChecked());


                        if(Time>TimePicker.GetSecond())
                        {
                            mObj.put("LastSend",ToDay);
                        }
                        mArray.put(mObj);

                        TaskInfo.put(Uin,mArray);
                        SaveTaskInfoToFile();
                    } catch (JSONException e) {
                        Utils.ShowToast("添加任务过程中发生错误:\n"+e);
                    }
                }).show();



    }
    private static void EditNowTask(Context context,String Uin,int which)
    {
        if (TaskInfo.has(Uin)
        ) {
            try{
                JSONArray mArray = TaskInfo.getJSONArray(Uin);
                JSONObject obj = mArray.getJSONObject(which);

                LinearLayout l = new LinearLayout(context);
                l.setOrientation(LinearLayout.VERTICAL);

                TextView t1 = new TextView(context);
                t1.setText("发送的内容");
                t1.setTextSize(16);
                t1.setTextColor(Color.BLACK);
                l.addView(t1);

                EditText ed = new EditText(context);
                ed.setHint("这里输入发送的内容,支持[PicUrl=图片地址]来插入图片,如果是群聊,可以输入[fall]来禁言全体,[ufall]来解禁全体\n" +
                        "消息是每天发送的,如果QQ运行在后台则会准时发送,如果QQ掉了后台过了时间则会在QQ打开的时候尝试发送一次(可以关闭下面的尝试重新发送来关闭)");
                ed.setGravity(Gravity.TOP);
                ed.setText(obj.optString("TaskName"));
                l.addView(ed);

                TextView t2 = new TextView(context);
                t2.setText("选择发送的时间");
                t2.setTextColor(Color.BLACK);
                t2.setTextSize(16);
                l.addView(t2);

                long Time = obj.optLong("TaskDelayTime");
                int hour=(int) (Time/3600);
                Time = Time %3600;
                int minute = (int) (Time/60);
                Time = Time %60;
                int second = (int) Time;

                MyTimePicker TimePicker = new MyTimePicker(context,hour ,minute,second);
                l.addView(TimePicker);

                CheckBox check = new CheckBox(context);
                check.setChecked(obj.optBoolean("IsRepeat"));
                check.setText("尝试重新发送");
                check.setTextSize(16);
                check.setTextColor(Color.BLACK);
                l.addView(check);


                new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT)
                        .setTitle("设置任务详情")
                        .setView(l)
                        .setNegativeButton("保存", (dialog, which1) -> {

                            Calendar cal = Calendar.getInstance();
                            long Time1 = cal.get(Calendar.HOUR_OF_DAY)*3600+cal.get(Calendar.MINUTE)*60+cal.get(Calendar.SECOND);
                            int ToDay1 = cal.get(Calendar.DAY_OF_YEAR);

                            JSONArray mArray1;
                            try{
                                mArray1 = TaskInfo.getJSONArray(Uin);
                            }catch (Exception e)
                            {
                                mArray1 = new JSONArray();
                            }

                            JSONObject mObj = new JSONObject();
                            try {
                                mObj.put("TaskName",ed.getText().toString());
                                mObj.put("TaskDelayTime",TimePicker.GetSecond());
                                mObj.put("isTroop",obj.optBoolean("isTroop"));
                                mObj.put("IsRepeat",check.isChecked());

                                if(Time1>TimePicker.GetSecond())
                                {
                                    mObj.put("LastSend",ToDay1);
                                }

                                mArray1.put(which,mObj);

                                TaskInfo.put(Uin, mArray1);
                                SaveTaskInfoToFile();
                            } catch (JSONException e) {
                                Utils.ShowToast("添加任务过程中发生错误:\n"+e);
                            }
                        })
                        .setNeutralButton("删除", (dialog, which12) -> {
                            JSONArray mArray1;
                            try{
                                mArray1 = TaskInfo.getJSONArray(Uin);
                                mArray1.remove(which);
                                TaskInfo.put(Uin, mArray1);
                                SaveTaskInfoToFile();
                            }catch (Exception e)
                            {

                            }
                        })
                        .show();

            }catch (Exception ex)
            {
                Utils.ShowToast("加载任务时发生错误:\n"+ex);
            }


        }
    }
    private static void SaveTaskInfoToFile()
    {
        FileUtils.WriteFileByte(ConfigPathBuilder.Get_Current_Path_Set() + "自动消息.json",TaskInfo.toString().getBytes());
    }

    private static void TimeThread()
    {
        while (true)
        {
            try {
                Thread.sleep(1000);
                StartCheckAndSendMessage();

            } catch (InterruptedException e) {
                MLogCat.Print_Error("AutoMessageDispatch",e);
            }
        }
    }
    private static void StartCheckAndSendMessage()
    {
        if(!MConfig.Get_Boolean("Main","MainSwitch","自动消息",false))return;
        Calendar cal = Calendar.getInstance();
        long Time = cal.get(Calendar.HOUR_OF_DAY)*3600+cal.get(Calendar.MINUTE)*60+cal.get(Calendar.SECOND);
        int ToDay = cal.get(Calendar.DAY_OF_YEAR);
        Iterator<String> it = TaskInfo.keys();
        while (it.hasNext())
        {
            String uin = it.next();
            try{
                JSONArray mArray = TaskInfo.getJSONArray(uin);
                for(int i=0;i<mArray.length();i++)
                {
                    JSONObject obj = mArray.getJSONObject(i);

                    int LastSendDay = obj.optInt("LastSend");
                    if(LastSendDay==ToDay)continue;

                    long StartTime = obj.getLong("TaskDelayTime");
                    boolean IsRepeat = obj.getBoolean("IsRepeat");
                    if(IsRepeat)
                    {
                        if(StartTime<=Time)
                        {
                            HandlerMessage(obj.optString("TaskName"),uin,obj.optBoolean("isTroop"));
                            obj.put("LastSend",ToDay);
                            mArray.put(i,obj);
                            TaskInfo.put(uin,mArray);
                            SaveTaskInfoToFile();
                            continue;
                        }
                    }else
                    {
                        if(Math.abs(Time - StartTime)<30)
                        {
                            HandlerMessage(obj.optString("TaskName"),uin,obj.optBoolean("isTroop"));
                            obj.put("LastSend",ToDay);
                            mArray.put(i,obj);
                            TaskInfo.put(uin,mArray);
                            SaveTaskInfoToFile();
                            continue;
                        }
                    }
                }
            }catch (Exception e)
            {
                MLogCat.Print_Error("AutoMessageHandler",e);
            }
        }
    }
    private static void HandlerMessage(String Message,String Uin,boolean IsTroop){
        try{
            if(TextUtils.isEmpty(Message))return;
            if(IsTroop)
            {

                if(Message.contains("[fall]"))
                {
                    TroopManager.Group_Forbidden_All(Uin,true);
                    Message = Message.replace("[fall]","");
                }

                if(Message.contains("[ufall]"))
                {
                    TroopManager.Group_Forbidden_All(Uin,false);
                    Message = Message.replace("[ufall]","");
                }
                Message = Message.replace("[atall]","[AtQQ=0]");

                JavaPluginMethod.Plugin_SendCommonMessageNoStress(Uin,"",Message);
            }else
            {
                JavaPluginMethod.Plugin_SendCommonMessageNoStress("",Uin,Message);
            }
        }catch (Exception ex)
        {
            MLogCat.Print_Error("HandlerAutoMessageError,In:"+Message,ex);
        }

    }

}
