package com.ldq.connect.MainWorker.QQManagerHelper;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MField;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.ConfigPathBuilder;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.JavaPlugin.JavaPluginUtils;
import com.ldq.connect.MainWorker.ProxyHook.Hook_Request_Join_Troop;
import com.ldq.connect.QQUtils.TroopManager;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;

public class TroopManager_Handler_WhiteBlack_Checker {
    static JSONObject jWhileList;
    static JSONObject jBlackList;
    static {
        try{
            jWhileList = new JSONObject(FileUtils.ReadFileString(ConfigPathBuilder.Get_Current_Path_Set() +"WhileList.json"));
        }catch (Exception e){
            jWhileList = new JSONObject();
        }

        try{
            jBlackList = new JSONObject(FileUtils.ReadFileString(ConfigPathBuilder.Get_Current_Path_Set()+"BlackList.json"));
        }catch (Exception e){
            jBlackList = new JSONObject();
        }
    }
    private static void Save0_While(){
        FileUtils.WriteFileByte(ConfigPathBuilder.Get_Current_Path_Set()+"WhileList.json",jWhileList.toString().getBytes(StandardCharsets.UTF_8));
    }
    private static void Save0_Black(){
        FileUtils.WriteFileByte(ConfigPathBuilder.Get_Current_Path_Set()+"BlackList.json",jBlackList.toString().getBytes(StandardCharsets.UTF_8));
    }
    public static boolean Check_While(Object ChatMsg){
        try {
            int IsTroop = MField.GetField(ChatMsg,"istroop",int.class);
            if(IsTroop==1){
                String TroopUin = MField.GetField(ChatMsg,"frienduin",String.class);
                String UserUin = MField.GetField(ChatMsg,"senderuin",String.class);
                return Check_While(TroopUin,UserUin);
            }
            return false;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }
    public static boolean Check_While(String TroopUin,String UserUin){
        if(MConfig.Get_Boolean("Main","MainSwitch","群黑白名单",false)){
            try {

                JSONObject mArray = jWhileList.has(TroopUin) ? jWhileList.getJSONObject(TroopUin) : new JSONObject();
                return mArray.has(UserUin);
            } catch (JSONException e) {
                e.printStackTrace();
                return false;
            }
        }else {
            return false;
        }


    }
    public static boolean Check_Black(String TroopUin,String UserUin){
        if(!MConfig.Get_Boolean("Main","MainSwitch","群黑白名单",false)){
            return false;
        }
        try {
            JSONObject mArray = jBlackList.has(TroopUin) ? jBlackList.getJSONObject(TroopUin) : new JSONObject();
            return mArray.has(UserUin);
        } catch (JSONException e) {
            e.printStackTrace();
            return false;
        }
    }
    public static void ShowSet(String TroopUin){
        Context context = Utils.GetThreadActivity();

        new AlertDialog.Builder(context,3)
                .setTitle("提示")
                .setMessage("选择你要查看的名单")
                .setNegativeButton("黑名单", (dialog, which) -> {
                    ShowBlackList(TroopUin,context);
                }).setPositiveButton("白名单", (dialog, which) -> {
                    ShowWhileList(TroopUin,context);
                }).show();
    }
    public static void SetWhileListStatus(String TroopUin,String UserUin,boolean IsSet){
        try {
            JSONObject mArray = jWhileList.has(TroopUin) ? jWhileList.getJSONObject(TroopUin) : new JSONObject();
            if(IsSet) mArray.put(UserUin,true);
            else mArray.remove(UserUin);
            jWhileList.put(TroopUin,mArray);

            Save0_While();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
    public static void SetBlackListStatus(String TroopUin,String UserUin,boolean IsSet){
        try {
            JSONObject mArray = jBlackList.has(TroopUin) ? jBlackList.getJSONObject(TroopUin) : new JSONObject();
            if(IsSet) mArray.put(UserUin,true);
            else mArray.remove(UserUin);
            jBlackList.put(TroopUin,mArray);

            Save0_Black();


        } catch (JSONException e) {
            e.printStackTrace();
        }
    }


    private static void ShowBlackList(String TroopUin,Context mContext){
        try{
            JSONObject mArray = jBlackList.has(TroopUin) ? jBlackList.getJSONObject(TroopUin) : new JSONObject();
            Iterator<String> its = mArray.keys();
            ScrollView sc = new ScrollView(mContext);
            LinearLayout mRootList = new LinearLayout(mContext);
            mRootList.setOrientation(LinearLayout.VERTICAL);
            sc.addView(mRootList);

            LinearLayout ToolBar = new LinearLayout(mContext);
            Button btnSetUser = new Button(mContext);
            btnSetUser.setText("添加群内成员为黑名单");

            ToolBar.addView(btnSetUser);

            Button btnSetExtraUser = new Button(mContext);
            btnSetExtraUser.setText("添加QQ号为黑名单");
            ToolBar.addView(btnSetExtraUser);
            mRootList.addView(ToolBar);


            ArrayList<CheckBox> bList = new ArrayList<>();
            while (its.hasNext()){
                String UserUin = its.next();
                CheckBox chUser = new CheckBox(mContext);
                chUser.setTextColor(Color.BLACK);
                chUser.setText(TroopManager.GetMemberName(TroopUin,UserUin)+"("+UserUin+")");
                chUser.setChecked(true);
                chUser.setTag(UserUin);
                mRootList.addView(chUser);
                bList.add(chUser);
            }


            btnSetExtraUser.setOnClickListener(v -> {
                EditText edInputQQNumber = new EditText(mContext);
                new AlertDialog.Builder(mContext,3)
                        .setTitle("添加QQ号")
                        .setView(edInputQQNumber)
                        .setNegativeButton("确定", (dialog, which) -> {
                            String QQUin = edInputQQNumber.getText().toString();
                            CheckBox chNewUser = new CheckBox(mContext);
                            chNewUser.setTextColor(Color.BLACK);
                            chNewUser.setText(TroopManager.GetMemberName(TroopUin,QQUin)+"("+QQUin+")");
                            chNewUser.setChecked(true);
                            chNewUser.setTag(QQUin);
                            mRootList.addView(chNewUser);
                            bList.add(chNewUser);
                        }).show();
            });

            btnSetUser.setOnClickListener(v -> {
                ArrayList<JavaPluginUtils.GroupMemberInfo> memberInfos = JavaPluginUtils.GetGroupMemberList(TroopUin);
                LinearLayout l = new LinearLayout(mContext);
                ScrollView scList = new ScrollView(mContext);
                scList.addView(l);
                l.setOrientation(LinearLayout.VERTICAL);
                ArrayList<CheckBox> SavedCheckBoxList = new ArrayList<>();

                for(JavaPluginUtils.GroupMemberInfo info : memberInfos){
                    CheckBox NewCheck = new CheckBox(mContext);
                    NewCheck.setTextColor(Color.BLACK);
                    NewCheck.setText(info.NickName + "("+info.UserUin+")");
                    NewCheck.setTag(info.UserUin);
                    l.addView(NewCheck);
                    SavedCheckBoxList.add(NewCheck);
                }

                new AlertDialog.Builder(mContext,3)
                        .setTitle("选择需要添加进黑名单的用户")
                        .setView(scList)
                        .setNegativeButton("确定", (dialog, which) -> {
                            for (CheckBox chUin : SavedCheckBoxList){
                                if(chUin.isChecked()){
                                    String QQUin = (String) chUin.getTag();
                                    CheckBox chNewUser = new CheckBox(mContext);
                                    chNewUser.setTextColor(Color.BLACK);
                                    chNewUser.setText(TroopManager.GetMemberName(TroopUin,QQUin)+"("+QQUin+")");
                                    chNewUser.setChecked(true);
                                    chNewUser.setTag(QQUin);
                                    mRootList.addView(chNewUser);
                                    bList.add(chNewUser);
                                }
                            }
                        }).show();
            });

            new AlertDialog.Builder(mContext,3)
                    .setTitle("设置黑名单列表")
                    .setView(sc)
                    .setNegativeButton("保存", (dialog, which) -> {
                        try{
                            JSONObject NewJSONObj = new JSONObject();
                            for(CheckBox chBox : bList){
                                String QQUin = (String) chBox.getTag();
                                boolean CheckStatus = chBox.isChecked();
                                if(CheckStatus){
                                    NewJSONObj.put(QQUin,true);
                                }

                            }

                            jBlackList.put(TroopUin,NewJSONObj);
                            Save0_Black();
                        }catch (Exception e){
                            Utils.ShowToast("保存失败,发生错误:\n"+e);
                        }

                    }).show();
        }catch (Exception e){
            Utils.ShowToast("发生错误:\n"+e);
        }
    }

    private static void ShowWhileList(String TroopUin,Context mContext){
        try{
            JSONObject mArray = jWhileList.has(TroopUin) ? jWhileList.getJSONObject(TroopUin) : new JSONObject();
            Iterator<String> its = mArray.keys();
            ScrollView sc = new ScrollView(mContext);
            LinearLayout mRootList = new LinearLayout(mContext);
            mRootList.setOrientation(LinearLayout.VERTICAL);
            sc.addView(mRootList);

            LinearLayout ToolBar = new LinearLayout(mContext);
            Button btnSetUser = new Button(mContext);
            btnSetUser.setText("添加群内成员为白名单");

            ToolBar.addView(btnSetUser);

            Button btnSetExtraUser = new Button(mContext);
            btnSetExtraUser.setText("添加QQ号为白名单");
            ToolBar.addView(btnSetExtraUser);
            mRootList.addView(ToolBar);


            ArrayList<CheckBox> bList = new ArrayList<>();
            while (its.hasNext()){
                String UserUin = its.next();
                CheckBox chUser = new CheckBox(mContext);
                chUser.setTextColor(Color.BLACK);
                chUser.setText(TroopManager.GetMemberName(TroopUin,UserUin)+"("+UserUin+")");
                chUser.setChecked(true);
                chUser.setTag(UserUin);
                mRootList.addView(chUser);
                bList.add(chUser);
            }


            btnSetExtraUser.setOnClickListener(v -> {
                EditText edInputQQNumber = new EditText(mContext);
                new AlertDialog.Builder(mContext,3)
                        .setTitle("添加QQ号")
                        .setView(edInputQQNumber)
                        .setNegativeButton("确定", (dialog, which) -> {
                            String QQUin = edInputQQNumber.getText().toString();
                            CheckBox chNewUser = new CheckBox(mContext);
                            chNewUser.setTextColor(Color.BLACK);
                            chNewUser.setText(TroopManager.GetMemberName(TroopUin,QQUin)+"("+QQUin+")");
                            chNewUser.setChecked(true);
                            chNewUser.setTag(QQUin);
                            mRootList.addView(chNewUser);
                            bList.add(chNewUser);
                        }).show();
            });

            btnSetUser.setOnClickListener(v -> {
                ArrayList<JavaPluginUtils.GroupMemberInfo> memberInfos = JavaPluginUtils.GetGroupMemberList(TroopUin);
                LinearLayout l = new LinearLayout(mContext);
                ScrollView scList = new ScrollView(mContext);
                scList.addView(l);
                l.setOrientation(LinearLayout.VERTICAL);
                ArrayList<CheckBox> SavedCheckBoxList = new ArrayList<>();
                for(JavaPluginUtils.GroupMemberInfo info : memberInfos){
                    CheckBox NewCheck = new CheckBox(mContext);
                    NewCheck.setTextColor(Color.BLACK);
                    NewCheck.setText(info.NickName + "("+info.UserUin+")");
                    NewCheck.setTag(info.UserUin);
                    l.addView(NewCheck);
                    SavedCheckBoxList.add(NewCheck);
                }

                new AlertDialog.Builder(mContext,3)
                        .setTitle("选择需要添加进白名单的用户")
                        .setView(scList)
                        .setNegativeButton("确定", (dialog, which) -> {
                            for (CheckBox chUin : SavedCheckBoxList){
                                if(chUin.isChecked()){
                                    String QQUin = (String) chUin.getTag();
                                    CheckBox chNewUser = new CheckBox(mContext);
                                    chNewUser.setTextColor(Color.BLACK);
                                    chNewUser.setText(TroopManager.GetMemberName(TroopUin,QQUin)+"("+QQUin+")");
                                    chNewUser.setChecked(true);
                                    chNewUser.setTag(QQUin);
                                    mRootList.addView(chNewUser);
                                    bList.add(chNewUser);
                                }
                            }
                        }).show();
            });

            new AlertDialog.Builder(mContext,3)
                    .setTitle("设置白名单列表")
                    .setView(sc)
                    .setNegativeButton("保存", (dialog, which) -> {
                        try{
                            JSONObject NewJSONObj = new JSONObject();
                            for(CheckBox chBox : bList){
                                String QQUin = (String) chBox.getTag();
                                boolean CheckStatus = chBox.isChecked();
                                if(CheckStatus){
                                    NewJSONObj.put(QQUin,true);
                                }

                            }

                            jWhileList.put(TroopUin,NewJSONObj);
                            Save0_While();
                        }catch (Exception e){
                            Utils.ShowToast("保存失败,发生错误:\n"+e);
                        }

                    }).show();
        }catch (Exception e){
            Utils.ShowToast("发生错误:\n"+e);
        }
    }
    /*
    入群请求处理,Type 0为入群通知,1为入群请求
     */
    public static void JoinInHandler(String TroopUin,String UserUin,int Type,Object InvokeParam){
        if(Check_Black(TroopUin,UserUin)){
            if(Type == 0){
                TroopManager.Group_Kick(TroopUin,UserUin,false);
            }else if(Type==1){
                Hook_Request_Join_Troop.Handler_RefuseRequest(InvokeParam,"黑名单",false);
            }
        }

    }
    public static void SendMessageHandler(String TroopUin,String UserUin){
        if(Check_Black(TroopUin,UserUin)){
            TroopManager.Group_Kick(TroopUin,UserUin,false);
        }
    }
}
