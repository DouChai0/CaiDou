package com.ldq.connect.MainWorker.QQManagerHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.ConfigPathBuilder;
import com.ldq.connect.JavaPlugin.JavaPluginMethod;
import com.ldq.connect.QQUtils.BaseCall;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.FormItem;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.QQMessage_Builder;
import com.ldq.connect.QQUtils.TroopManager;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

public class TroopManager_Set_Alarm_Group {
    public static final int ALARM_NO_ALARM = 0;
    public static final int ALARM_REJECT = 1;
    public static final int ALARM_CHECKED_AND_PUNISH = 2;
    static JSONObject AlarmMap;
    static {
        try{
            AlarmMap = new JSONObject(FileUtils.ReadFileString(ConfigPathBuilder.Get_Current_Path_Set() + "AlarmLog.json"));
        }catch (Exception e) {
            AlarmMap = new JSONObject();
        }
    }
    private static void ShowGroupItemSetDialog(String ItemGroupName)
    {
        try{
            Context context = Utils.GetThreadActivity();
            JSONObject groupObj = AlarmMap.has(ItemGroupName)?AlarmMap.getJSONObject(ItemGroupName):new JSONObject();

            LinearLayout mRoot = new LinearLayout(context);
            mRoot.setBackgroundColor(Color.WHITE);
            mRoot.setOrientation(LinearLayout.VERTICAL);

            TextView tSetID = new TextView(context);
            tSetID.setText("组ID:"+ItemGroupName);
            mRoot.addView(tSetID);

            TextView tSetName = new TextView(context);
            tSetName.setText("警告组名字");
            tSetName.setTextColor(Color.BLACK);
            mRoot.addView(tSetName);

            EditText edName = new EditText(context);
            edName.setText(groupObj.optString("name"));
            mRoot.addView(edName);

            TextView tSetCount = new TextView(context);
            tSetCount.setTextColor(Color.BLACK);
            tSetCount.setText("设置警告次数");
            mRoot.addView(tSetCount);

            EditText edAlarmCount = new EditText(context);
            edAlarmCount.setText(""+groupObj.optLong("AlarmCount"));
            mRoot.addView(edAlarmCount);


            CheckBox chSetAlarmForbidden = new CheckBox(context);
            chSetAlarmForbidden.setTextColor(Color.BLACK);
            chSetAlarmForbidden.setText("警告时禁言");


            mRoot.addView(chSetAlarmForbidden);

            EditText edForbiddenTime = new EditText(context);
            edForbiddenTime.setText(""+groupObj.optLong("ForbiddenTime"));
            mRoot.addView(edForbiddenTime);
            chSetAlarmForbidden.setOnCheckedChangeListener((v,a)->edForbiddenTime.setVisibility(a?View.VISIBLE:View.GONE));
            chSetAlarmForbidden.setChecked(groupObj.optBoolean("AlarmForbidden"));


            CheckBox chSetAlarmRemove = new CheckBox(context);
            chSetAlarmRemove.setText("警告时撤回");
            chSetAlarmRemove.setChecked(groupObj.optBoolean("AlarmRemove"));
            chSetAlarmRemove.setTextColor(Color.BLACK);
            mRoot.addView(chSetAlarmRemove);

            CheckBox chSetAlarmCallback = new CheckBox(context);
            chSetAlarmCallback.setText("警告时回复");
            chSetAlarmCallback.setChecked(groupObj.optBoolean("AlarmRe"));
            chSetAlarmCallback.setTextColor(Color.BLACK);
            mRoot.addView(chSetAlarmCallback);

            EditText edAlarmCallback = new EditText(context);
            edAlarmCallback.setHint("[R]回复违规消息,[@]艾特违规人,[@S]隐藏艾特违规人,[QQ]违规人QQ,[N]违规人名字");
            edAlarmCallback.setText(DataUtils.HexToString(groupObj.optString("REMsg")));
            mRoot.addView(edAlarmCallback);
            chSetAlarmCallback.setOnCheckedChangeListener((v,a)->edAlarmCallback.setVisibility(a?View.VISIBLE:View.GONE));


            CheckBox chRemoveWhenFull = new CheckBox(context);
            chRemoveWhenFull.setText("在警告满时清除警告记录");
            chRemoveWhenFull.setTextColor(Color.BLACK);
            chRemoveWhenFull.setChecked(groupObj.optBoolean("ClearWhenFull"));
            mRoot.addView(chRemoveWhenFull);

            CheckBox chRemoveWhenExit = new CheckBox(context);
            chRemoveWhenExit.setText("在用户离开群聊时清除警告记录");
            chRemoveWhenExit.setTextColor(Color.BLACK);
            chRemoveWhenExit.setChecked(groupObj.optBoolean("ClearWhenExit"));
            mRoot.addView(chRemoveWhenExit);







            new AlertDialog.Builder(context,3)
                    .setTitle("设置警告组")
                    .setView(mRoot)
                    .setNeutralButton("保存", (dialog, which) -> {
                        try{
                            groupObj.put("name",edName.getText().toString());
                            groupObj.put("AlarmForbidden",chSetAlarmForbidden.isChecked());
                            groupObj.put("ForbiddenTime",Integer.parseInt(edForbiddenTime.getText().toString()));
                            groupObj.put("AlarmRemove",chSetAlarmRemove.isChecked());
                            groupObj.put("AlarmRe",chSetAlarmCallback.isChecked());
                            groupObj.put("REMsg",DataUtils.bytesToHex(edAlarmCallback.getText().toString().getBytes()));
                            groupObj.put("ClearWhenFull",chRemoveWhenFull.isChecked());
                            groupObj.put("ClearWhenExit",chRemoveWhenExit.isChecked());
                            groupObj.put("AlarmCount",Integer.parseInt(edAlarmCount.getText().toString()));

                            AlarmMap.put(ItemGroupName,groupObj);
                            Flush();
                            Utils.ShowToast("已保存");
                        }catch (Exception e)
                        {
                            Utils.ShowToast("保存的时候发生错误:"+e);
                        }
                    }).setNegativeButton("删除", (dialog, which) -> {
                        AlarmMap.remove(ItemGroupName);
                        Flush();
                    }).show();



        }catch (Exception e0){

        }
    }
    public static void ShowAlarmSetDialogGroup() {
        //基础组创建
        Context context = Utils.GetThreadActivity();
        ScrollView sc = new ScrollView(context);

        LinearLayout mRoot = new LinearLayout(context);
        sc.addView(mRoot);
        mRoot.setOrientation(LinearLayout.VERTICAL);
        LinearLayout ToolBar = new LinearLayout(context);
        LinearLayout GroupListSetList = new LinearLayout(context);
        GroupListSetList.setOrientation(LinearLayout.VERTICAL);

        mRoot.addView(ToolBar);
        mRoot.addView(GroupListSetList);

        AlertDialog dialog = new AlertDialog.Builder(context,3)
                .setTitle("警告组设定")
                .setView(sc)
                .show();

        //工具栏
        Button btnAdd = new Button(context);
        btnAdd.setText("添加一项");
        btnAdd.setOnClickListener(v-> GroupListSetList.addView(FormItem.AddListItem(context,"新加项", vx-> {
                    ShowGroupItemSetDialog("ID_" + Utils.GetNowTime22());
                    dialog.dismiss();
                })));
        ToolBar.addView(btnAdd);


        Button btnFlush = new Button(context);
        btnFlush.setText("刷新显示");
        btnFlush.setOnClickListener(v->{
            dialog.dismiss();
            ShowAlarmSetDialogGroup();
        });
        ToolBar.addView(btnFlush);

        //显示创建的组
        Iterator<String> its = AlarmMap.keys();
        while (its.hasNext())
        {
            String key = its.next();
            try{
                JSONObject groupObj = AlarmMap.getJSONObject(key);
                View GroupNameList = FormItem.AddListItem(context,groupObj.optString("name"),v->{
                    ShowGroupItemSetDialog(key);
                    dialog.dismiss();
                });
                GroupListSetList.addView(GroupNameList);
            }catch (Exception e)
            {

            }
        }


    }
    public static HashMap<String,String> GetKey_Name_List()
    {
        HashMap<String,String> returnMap = new HashMap<>();
        Iterator<String> keys = AlarmMap.keys();
        while (keys.hasNext())
        {
            String key = keys.next();
            try{
                JSONObject groupObj = AlarmMap.getJSONObject(key);
                String Name = groupObj.optString("name");
                if(!TextUtils.isEmpty(Name))
                {
                    returnMap.put(key,Name);
                }
            }catch (Exception e)
            {

            }
        }
        return returnMap;
    }


    //仅检测不处理用户通知状态
    public static boolean CheckUserAlarmStatus(String TroopUin,String UserUin)
    {
        return false;
    }

    //检测并处理用户通知窗台
    public synchronized static int CheckAndNotifyUserAlarmStatus(String TroopUin, String UserUin, String AlarmGroup, Object ChatObj)
    {
        try{
            if(!AlarmMap.has(AlarmGroup))return ALARM_NO_ALARM;
            if(UserUin.equalsIgnoreCase(BaseInfo.GetCurrentUin()))return ALARM_NO_ALARM;
            JSONObject GroupObj = AlarmMap.getJSONObject(AlarmGroup);
            JSONObject Use_List_Obj = GroupObj.has("UsesList")?GroupObj.getJSONObject("UsesList"):new JSONObject();
            JSONObject Use_Obj = Use_List_Obj.has(TroopUin+":"+UserUin)?Use_List_Obj.getJSONObject(TroopUin+":"+UserUin):new JSONObject();

            long AlarmCount_Config = GroupObj.optLong("AlarmCount");
            long Uses_AlarmCount = Use_Obj.optLong("AlarmCount");

            Use_Obj.put("AlarmCount",++Uses_AlarmCount);
            //如果达到了禁言数量
            if(Uses_AlarmCount > AlarmCount_Config)
            {
                //如果是警告满了则清理就进行清理
                if(GroupObj.optBoolean("ClearWhenFull")) {
                    Use_Obj.remove("AlarmCount");
                }
                Use_List_Obj.put(TroopUin+":"+UserUin,Use_Obj);
                GroupObj.put("UsesList",Use_List_Obj);
                Flush();
                return ALARM_CHECKED_AND_PUNISH;
            }
            if(GroupObj.optBoolean("AlarmRemove"))
            {
                BaseCall.RemoveMessage(ChatObj);
            }

            if(GroupObj.optBoolean("AlarmForbidden"))
            {
                long Time = GroupObj.optLong("ForbiddenTime");
                TroopManager.Group_Forbidden(TroopUin,UserUin, (int) Time);
            }

            if(GroupObj.optBoolean("AlarmRe"))
            {
                String WillSendMsg = DataUtils.HexToString(GroupObj.optString("REMsg"));
                if(!TextUtils.isEmpty(WillSendMsg)) Fixed_Message_Sender(TroopUin,BaseInfo.GetCurrentUin(),WillSendMsg,ChatObj);
            }

            Use_List_Obj.put(TroopUin+":"+UserUin,Use_Obj);
            GroupObj.put("UsesList",Use_List_Obj);
            Flush();


            return ALARM_REJECT;
        }catch (Exception e)
        {
            return ALARM_NO_ALARM;
        }
    }
    public synchronized static void CheckExit(String TroopUin,String UserUin)
    {
        try{
            Iterator<String> its = AlarmMap.keys();
            while (its.hasNext())
            {
                String AlarmID = its.next();
                JSONObject o = AlarmMap.getJSONObject(AlarmID);
                if(o.optBoolean("ClearWhenExit"))
                {
                    JSONObject Use_List_Obj = o.has("UsesList")?o.getJSONObject("UsesList"):new JSONObject();
                    if(Use_List_Obj.has(TroopUin+":"+UserUin))
                    {
                        Use_List_Obj.remove(TroopUin+":"+UserUin);
                        o.put("UsesList",Use_List_Obj);
                        AlarmMap.put(AlarmID,o);
                    }
                }
            }
            Flush();
        }catch (Exception e)
        {

        }
    }
    private static void Fixed_Message_Sender(String TroopUin,String UserUin,String MessageContent,Object Chatmsg)
    {
        //[R]回复违规消息,[@]艾特违规人,[@S]隐藏艾特违规人(不可和艾特共存),[QQ]违规人QQ,[N]违规人名字
        try{
            String ToUin = null;
            try {
                ToUin = MField.GetField(Chatmsg,"senderuin",String.class);
            } catch (Exception exception) {
                exception.printStackTrace();
            }
            MessageContent = MessageContent.replace("[QQ]",ToUin);
            MessageContent = MessageContent.replace("[N]",TroopManager.GetMemberName(TroopUin,ToUin));
            if(MessageContent.contains("[R]"))
            {
                MessageContent = MessageContent.replace("[R]","");
                MessageContent = MessageContent.replace("[@S]","");
                MessageContent = MessageContent.replace("[@]","@"+TroopManager.GetMemberName(TroopUin,ToUin));
                QQMessage.Message_Send_Reply(TroopUin,Chatmsg,MessageContent);
            }else
            {
                if(MessageContent.contains("[@]"))
                {
                    MessageContent = MessageContent.replace("[@S]","");
                    MessageContent = MessageContent.replace("[@]","[AtQQ="+ToUin+"]");
                    JavaPluginMethod.Plugin_SendCommonMessageNoStress(TroopUin,"",MessageContent);
                }else if (MessageContent.contains("[@S]"))
                {
                    MessageContent = MessageContent.replace("[@S]","");
                    MessageContent = MessageContent.replace("[@]","");
                    MessageContent = MessageContent + " ";

                    Object AtObj = QQMessage_Builder.Build_AtInfo(ToUin," ", (short) (MessageContent.length()-2));
                    ArrayList NewList = new ArrayList();
                    NewList.add(AtObj);
                    QQMessage.Message_Send_Text(QQMessage_Builder.Build_SessionInfo(TroopUin,""),MessageContent,NewList);
                }else
                {
                    JavaPluginMethod.Plugin_SendCommonMessageNoStress(TroopUin,"",MessageContent);
                }
            }
        }catch (Exception e)
        {
            MLogCat.Print_Error("FixMsgSender",e);
        }

    }
    private static void Flush() {
        FileUtils.WriteFileByte(ConfigPathBuilder.Get_Current_Path_Set() + "AlarmLog.json",AlarmMap.toString().getBytes());
    }
}
