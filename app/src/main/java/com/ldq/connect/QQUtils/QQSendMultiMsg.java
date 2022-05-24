package com.ldq.connect.QQUtils;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MField;
import com.ldq.Utils.Utils;
import com.ldq.connect.JavaPlugin.JavaPluginMethod;
import com.ldq.connect.JavaPlugin.JavaPluginUtils;
import com.ldq.connect.MHookEnvironment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class QQSendMultiMsg {
    int DelayTime = 400;
    ArrayList<String> SelectTroop = new ArrayList<>();
    ArrayList<String> SelectFriend = new ArrayList<>();
    Context context;
    public void ShowDialog(String SourceMsg)
    {


        Activity act = Utils.GetThreadActivity();
        context = act;
        LinearLayout mRoot = new LinearLayout(act);
        mRoot.setOrientation(LinearLayout.VERTICAL);

        LinearLayout TopButton = new LinearLayout(act);
        Button btnSelectTroop = new Button(act);
        btnSelectTroop.setText("选择发送群聊");
        btnSelectTroop.setOnClickListener(v->ShowSelectTroop());
        TopButton.addView(btnSelectTroop);

        Button btnSelectFriend = new Button(act);
        btnSelectFriend.setText("选择发送好友");
        btnSelectFriend.setOnClickListener(v-> {
            try {
                ShowSelectFriend();
            } catch (Exception exception) {
                Utils.ShowToast("显示对话框失败:\n"+exception);
            }
        });
        TopButton.addView(btnSelectFriend);

        Button btnSelectDelay = new Button(act);
        btnSelectDelay.setText("选择发送间隔");
        btnSelectDelay.setOnClickListener(v->ShowChangeTime());
        TopButton.addView(btnSelectDelay);

        mRoot.addView(TopButton);

        LinearLayout SaveOptToolBar = new LinearLayout(context);


        Button btnSave = new Button(context);
        btnSave.setText("保存当前选择配置");
        btnSave.setOnClickListener(v->PreSaveOpt());
        SaveOptToolBar.addView(btnSave);

        Button btnLoad = new Button(context);
        btnLoad.setText("加载已经保存的配置");
        btnLoad.setOnClickListener(v->PreLoadOpt());
        SaveOptToolBar.addView(btnLoad);

        mRoot.addView(SaveOptToolBar);


        EditText Input = new EditText(act);
        Input.setText(SourceMsg);
        Input.setTextSize(14);
        mRoot.addView(Input);

        new AlertDialog.Builder(act,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("群发消息确认")
                .setView(mRoot)
                .setNeutralButton("发送", (dialog, which) -> {
                    new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT)
                            .setTitle("确定发送?")
                            .setMessage("即将向"+SelectTroop+"群聊和"+SelectFriend+"好友发送消息,间隔延迟"+DelayTime+"毫秒")
                            .setNeutralButton("确定发送", (dialog1, which1) -> {
                                StartSend(Input.getText().toString());
                            }).setPositiveButton("取消", (dialog12, which12) -> {

                            }).show();

                }).show();
    }
    private void PreLoadOpt()
    {
        File[] f = new File(MHookEnvironment.PublicStorageModulePath + "Config/forward_set/").listFiles();
        ArrayList<String> NameList = new ArrayList<>();
        if(f!=null)
        {
            for(File fs : f)
            {
                if(fs.getName().equals(".") || fs.getName().equals(".."))continue;
                NameList.add(fs.getName().substring(0,fs.getName().lastIndexOf(".")));
            }
        }
        new AlertDialog.Builder(context,3)
                .setTitle("选择一个已保存的配置")
                .setItems(NameList.toArray(new String[0]), (dialog, which) -> {
                    String Path = MHookEnvironment.PublicStorageModulePath + "Config/forward_set/" + NameList.get(which)+".json";
                    String Data = FileUtils.ReadFileString(Path);
                    if(TextUtils.isEmpty(Data))
                    {
                        Utils.ShowToast("加载配置失败");
                        return;
                    }
                    try {
                        JSONObject obj = new JSONObject(Data);
                        JSONArray arrUser = obj.getJSONArray("User");
                        JSONArray arrTroop = obj.getJSONArray("Troop");

                        SelectTroop = new ArrayList<>();
                        SelectFriend = new ArrayList<>();

                        for(int i=0;i<arrUser.length();i++)
                        {
                            String sUin = arrUser.getString(i);
                            SelectFriend.add(sUin);
                        }

                        for(int i=0;i<arrTroop.length();i++)
                        {
                            String sUin = arrTroop.getString(i);
                            SelectTroop.add(sUin);
                        }

                        CheckAndRemoveInvalidUin();


                    } catch (JSONException e) {
                        Utils.ShowToast("加载配置失败");
                    }
                }).show();
    }
    private void CheckAndRemoveInvalidUin()
    {
        ArrayList<JavaPluginUtils.GroupInfo> infos = JavaPluginUtils.GetGroupInfo();
        ArrayList<String> CheckList = new ArrayList<>();
        for(JavaPluginUtils.GroupInfo sInfo : infos)CheckList.add(sInfo.GroupUin);
        ArrayList<String> CheckTroopResult = new ArrayList<>();
        for(String uins : SelectTroop)
        {
            if(!CheckList.contains(uins))
            {
                CheckTroopResult.add(uins);
            }
        }
        ArrayList<String> CheckUserResult = new ArrayList<>();

        CheckList = new ArrayList<>();
        List list = QQTools.User_GetFriendList();
        for(Object sInfo : list)
        {
            String Uin;
            try {
                Uin = MField.GetField(sInfo,"uin",String.class);
                CheckList.add(Uin);
            } catch (Exception exception) {
                continue;
            }
        }
        for(String uins : SelectFriend)
        {
            if(!CheckList.contains(uins))
            {
                CheckUserResult.add(uins);
            }
        }
        for(String removeUin : CheckUserResult)SelectFriend.remove(removeUin);
        for(String removeUin : CheckTroopResult)SelectTroop.remove(removeUin);

        String AlarmMessage = "";
        if(CheckUserResult.size()>0)
        {
            AlarmMessage = "以下配置文件中的好友已经失效,将不会进行发送:\n";
            for(String s : CheckUserResult)
            {
                AlarmMessage = AlarmMessage +  s + "\n";
            }
            AlarmMessage = AlarmMessage + "\n";
        }

        if(CheckTroopResult.size()>0)
        {
            AlarmMessage = AlarmMessage + "以下配置文件中的群聊已经失效,将不会进行发送:\n";
            for(String s : CheckTroopResult)
            {
                AlarmMessage = AlarmMessage +  s + "\n";
            }
            AlarmMessage = AlarmMessage + "\n";
        }

        if(!TextUtils.isEmpty(AlarmMessage))
        {
            new AlertDialog.Builder(context,3)
                    .setTitle("提示")
                    .setMessage(AlarmMessage)
                    .setNeutralButton("确定", (dialog, which) -> {

                    }).show();
        }



    }
    private void PreSaveOpt()
    {
        EditText ed = new EditText(context);
        ed.setTextSize(24);
        ed.setTextColor(Color.BLACK);

        new AlertDialog.Builder(context,3)
                .setView(ed)
                .setTitle("输入配置的名字")
                .setNeutralButton("保存", (dialog, which) -> {
                    String Name = ed.getText().toString();
                    JSONObject jSave = new JSONObject();
                    JSONArray preUser = new JSONArray();
                    JSONArray preTroop = new JSONArray();
                    for(String sUin:SelectFriend) preUser.put(sUin);
                    for(String sUin:SelectTroop) preTroop.put(sUin);
                    try {
                        jSave.put("User",preUser);
                        jSave.put("Troop",preTroop);

                        FileUtils.WriteFileByte(MHookEnvironment.PublicStorageModulePath + "Config/forward_set/"+Name+".json",jSave.toString().getBytes());
                    } catch (JSONException e) {
                        Utils.ShowToast("发生错误");
                    }

                }).show();
    }
    private void StartSend(String Content)
    {
        ProgressDialog pg = new ProgressDialog(context,ProgressDialog.THEME_HOLO_LIGHT);
        pg.setCancelable(true);
        pg.setTitle("消息群发");
        pg.setMessage("正在准备消息...");
        pg.show();
        new Thread(()->{
            try{
                for(String TroopUin : SelectTroop)
                {
                    new Handler(Looper.getMainLooper()).post(()->pg.setMessage("发送群聊:"+TroopUin));
                    JavaPluginMethod.Plugin_SendCommonMessageNoStress(TroopUin,"",Content);
                    Thread.sleep(DelayTime);
                }
                for(String UserUin : SelectFriend)
                {
                    new Handler(Looper.getMainLooper()).post(()->pg.setMessage("发送好友:"+UserUin));
                    JavaPluginMethod.Plugin_SendCommonMessageNoStress("",UserUin,Content);
                    Thread.sleep(DelayTime);
                }

                new Handler(Looper.getMainLooper()).post(()->pg.dismiss());
            }catch (Exception ex)
            {
                Utils.ShowToast("发生错误:\n"+ex);
                new Handler(Looper.getMainLooper()).post(()->pg.dismiss());
            }

        }).start();
    }
    private void ShowChangeTime()
    {
        EditText ed = new EditText(context);
        ed.setText(""+DelayTime);
        ed.setTextSize(20);

        new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("设置发送延迟(毫秒)")
                .setView(ed)
                .setNeutralButton("确定", (dialog, which) -> {
                    String s = ed.getText().toString();
                    DelayTime = Integer.parseInt(s);
                }).show();
    }
    private void ShowSelectTroop()
    {

        Button btnNotSelect = new Button(context);
        btnNotSelect.setText("反选");


        ScrollView sc = new ScrollView(context);
        LinearLayout Container = new LinearLayout(context);
        sc.addView(Container);
        Container.addView(btnNotSelect);

        Container.setOrientation(LinearLayout.VERTICAL);

        ArrayList<CheckBox> sCheckBoxs = new ArrayList<>();
        ArrayList<JavaPluginUtils.GroupInfo> infos = JavaPluginUtils.GetGroupInfo();
        for(JavaPluginUtils.GroupInfo sInfo : infos)
        {
            CheckBox ch = new CheckBox(context);
            ch.setChecked(SelectTroop.contains(sInfo.GroupUin));
            ch.setText(sInfo.GroupName+"("+sInfo.GroupUin+")");
            ch.setTag(sInfo.GroupUin);
            ch.setTextColor(Color.BLACK);
            sCheckBoxs.add(ch);
            Container.addView(ch);
        }

        btnNotSelect.setOnClickListener(v->{
            for(CheckBox ch : sCheckBoxs)
            {
                ch.setChecked(!ch.isChecked());
            }
        });

        new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("设置发送的群聊")
                .setView(sc)
                .setNeutralButton("确定", (dialog, which) -> {
                    SelectTroop = new ArrayList<>();
                    for(CheckBox ch : sCheckBoxs)
                    {
                        if(ch.isChecked())
                        {
                            SelectTroop.add((String) ch.getTag());
                        }
                    }
                }).show();
    }
    private void ShowSelectFriend() throws Exception {
        Button btnNotSelect = new Button(context);
        btnNotSelect.setText("反选");


        ScrollView sc = new ScrollView(context);
        LinearLayout Container = new LinearLayout(context);
        sc.addView(Container);
        Container.addView(btnNotSelect);

        Container.setOrientation(LinearLayout.VERTICAL);

        ArrayList<CheckBox> sCheckBoxs = new ArrayList<>();
        List list = QQTools.User_GetFriendList();
        for(Object sInfo : list)
        {
            String Uin = MField.GetField(sInfo,"uin",String.class);
            String Name = MField.GetField(sInfo,"name",String.class);
            CheckBox ch = new CheckBox(context);
            ch.setChecked(SelectFriend.contains(Uin));
            ch.setText(Name+"("+Uin+")");
            ch.setTag(Uin);
            ch.setTextColor(Color.BLACK);
            sCheckBoxs.add(ch);
            Container.addView(ch);
        }

        btnNotSelect.setOnClickListener(v->{
            for(CheckBox ch : sCheckBoxs)
            {
                ch.setChecked(!ch.isChecked());
            }
        });

        new AlertDialog.Builder(context,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("设置发送的好友")
                .setView(sc)
                .setNeutralButton("确定", (dialog, which) -> {
                    SelectFriend = new ArrayList<>();
                    for(CheckBox ch : sCheckBoxs)
                    {
                        if(ch.isChecked())
                        {
                            SelectFriend.add((String) ch.getTag());
                        }
                    }
                }).show();
    }

}
