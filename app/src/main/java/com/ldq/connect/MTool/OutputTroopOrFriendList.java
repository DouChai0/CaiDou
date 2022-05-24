package com.ldq.connect.MTool;

import android.app.AlertDialog;
import android.content.Context;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MField;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.JavaPlugin.JavaPluginUtils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQTools;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.concurrent.atomic.AtomicInteger;

public class OutputTroopOrFriendList {
    public static void StartDialog(Context context){
        new AlertDialog.Builder(context,3)
                .setTitle("导出数据")
                .setMessage("选择你要导出的数据")
                .setNegativeButton("好友列表", (dialog, which) -> {
                    ChoiceFriend(context);
                }).setNeutralButton("群成员列表", (dialog, which) -> {
                    ChoiceTroopMemberList(context);

        }).setPositiveButton("群列表", (dialog, which) -> {
            ChoiceTroopList(context);

        }).show();
    }
    public static void ChoiceFriend(Context context){
        new AlertDialog.Builder(context,3)
                .setTitle("导出好友列表")
                .setMessage("选择要导出成为的格式")
                .setNeutralButton("JSON格式", (dialog, which) -> {
                    try{
                        ArrayList mArr = QQTools.User_GetFriendList2();
                        JSONObject jResult = new JSONObject();
                        jResult.put("OutputUin", BaseInfo.GetCurrentUin_Direct());
                        jResult.put("Count",jResult.length());

                        JSONArray DataArray = new JSONArray();


                        for(Object ItemObj : mArr){
                            try{
                                JSONObject Item = new JSONObject();
                                String uin = MField.GetField(ItemObj,"uin",String.class);
                                String Name = MMethod.CallMethod(ItemObj,"getFriendName",String.class,new Class[0]);
                                String Nick = MMethod.CallMethod(ItemObj,"getFriendNickWithoutUin",String.class,new Class[0]);

                                Item.put("uin",uin);
                                Item.put("name",Name);
                                Item.put("nick",Nick);
                                DataArray.put(Item);
                            }catch (Exception e){

                            }
                        }
                        jResult.put("list",DataArray);

                        String Path = MHookEnvironment.PublicStorageModulePath+"导出数据/好友"+Utils.GetNowTime22()+".json";
                        FileUtils.WriteFileByte(Path,jResult.toString().getBytes());
                        new AlertDialog.Builder(context,3)
                                .setTitle("导出完成")
                                .setMessage("已导出到:"+Path)
                                .show();
                    }catch (Exception e) {
                        Utils.ShowToast("发生了一个错误:\n"+e);
                    }

                }).setNegativeButton("txt格式", (dialog, which) -> {
            StringBuilder Output = new StringBuilder("QQ账号\tQQ名字\t备注名字\n");
            ArrayList mArr = QQTools.User_GetFriendList2();
            for(Object ItemObj : mArr){
                try{
                    String uin = MField.GetField(ItemObj,"uin",String.class);
                    String Name = MMethod.CallMethod(ItemObj,"getFriendName",String.class,new Class[0]);
                    String Nick = MMethod.CallMethod(ItemObj,"getFriendNickWithoutUin",String.class,new Class[0]);
                    Output.append(uin).append("\t").append(Name).append("\t").append(Nick).append("\n");
                }catch (Exception e){

                }
            }

            String Path = MHookEnvironment.PublicStorageModulePath+"导出数据/好友"+Utils.GetNowTime22()+".txt";

            FileUtils.WriteFileByte(Path,Output.toString().getBytes());
            new AlertDialog.Builder(context,3)
                    .setTitle("导出完成")
                    .setMessage("已导出到:"+Path)
                    .show();





        }).show();
    }
    public static void ChoiceTroopList(Context context){
        new AlertDialog.Builder(context,3)
                .setTitle("导出群列表")
                .setMessage("选择导出的格式")
                .setNegativeButton("JSON格式", (dialog, which) -> {
                    try{
                        ArrayList<JavaPluginUtils.GroupInfo> infos = JavaPluginUtils.GetGroupInfo();
                        JSONObject jResult = new JSONObject();
                        jResult.put("OutputUin",BaseInfo.GetCurrentUin_Direct());
                        jResult.put("count",infos.size());

                        JSONArray ItemArray = new JSONArray();

                        for(JavaPluginUtils.GroupInfo Item : infos){
                            JSONObject sObj = new JSONObject();
                            sObj.put("uin",Item.GroupUin);
                            sObj.put("name",Item.GroupName);
                            sObj.put("owner",Item.GroupOwner);

                            ItemArray.put(sObj);
                        }
                        jResult.put("list",ItemArray);

                        String Path = MHookEnvironment.PublicStorageModulePath+"导出数据/群列表"+Utils.GetNowTime22()+".json";
                        FileUtils.WriteFileByte(Path,jResult.toString().getBytes());
                        new AlertDialog.Builder(context,3)
                                .setTitle("导出完成")
                                .setMessage("已导出到:"+Path)
                                .show();
                    }catch (Exception e){
                        Utils.ShowToast("导出过程发生错误:\n"+e);
                    }


                }).setNeutralButton("txt格式", (dialog, which) -> {
                    StringBuilder builder = new StringBuilder("群号\t群主QQ\t群名\n");
                    ArrayList<JavaPluginUtils.GroupInfo> infos = JavaPluginUtils.GetGroupInfo();

                    for (JavaPluginUtils.GroupInfo Item : infos){
                        builder.append(Item.GroupUin).append("\t")
                                .append(Item.GroupOwner).append("\t")
                                .append(Item.GroupName).append("\n");

                    }
            String Path = MHookEnvironment.PublicStorageModulePath+"导出数据/群列表"+Utils.GetNowTime22()+".txt";
            FileUtils.WriteFileByte(Path,builder.toString().getBytes());
            new AlertDialog.Builder(context,3)
                    .setTitle("导出完成")
                    .setMessage("已导出到:"+Path)
                    .show();

                }).show();
    }
    public static void ChoiceTroopMemberList(Context context){
        ArrayList<JavaPluginUtils.GroupInfo> GroupList = JavaPluginUtils.GetGroupInfo();
        String[] ShowList = new String[GroupList.size()];

        for(int i=0;i<GroupList.size();i++) ShowList[i] = GroupList.get(i).GroupName+"("+GroupList.get(i).GroupUin+")";
        AtomicInteger Checked = new AtomicInteger();
        new AlertDialog.Builder(context,3).setTitle("群成员列表导出")
                .setSingleChoiceItems(ShowList, 0, (dialog, which) -> {
                    Checked.getAndSet(which);
                }).setNegativeButton("JSON格式", (dialog, which) -> {
                    try{
                        String Uin = GroupList.get(Checked.get()).GroupUin;
                        ArrayList<JavaPluginUtils.GroupMemberInfo> memberList = JavaPluginUtils.GetGroupMemberList(Uin);
                        JSONObject sResult = new JSONObject();
                        sResult.put("OutputUin",BaseInfo.GetCurrentUin_Direct());
                        sResult.put("TroopUin",Uin);
                        sResult.put("count",memberList.size());

                        JSONArray Items = new JSONArray();

                        for(JavaPluginUtils.GroupMemberInfo infos : memberList){
                            JSONObject NewItem = new JSONObject();
                            NewItem.put("uin",infos.UserUin);
                            NewItem.put("name",infos.UserName);
                            NewItem.put("nick",infos.NickName);
                            NewItem.put("IsAdmin",infos.IsAdmin);
                            Items.put(NewItem);
                        }

                        sResult.put("list",Items);

                        String Path = MHookEnvironment.PublicStorageModulePath + "导出数据/群成员列表-"+Uin + "-"+Utils.GetNowTime22()+".json";
                        FileUtils.WriteFileByte(Path,sResult.toString().getBytes());
                        new AlertDialog.Builder(context, 3)
                                .setTitle("导出结果")
                                .setMessage("已导出到:"+Path)
                                .setNeutralButton("确定", (dialog1, which1) -> {

                                }).show();

                    }catch (Exception e){
                        Utils.ShowToast("发生错误:\n" + e);
                    }

                }).setNeutralButton("txt格式", (dialog, which) -> {
                    StringBuilder NewBuilder = new StringBuilder("成员号码\t成员名字\t成员群名片\n");

            String Uin = GroupList.get(Checked.get()).GroupUin;
            ArrayList<JavaPluginUtils.GroupMemberInfo> memberList = JavaPluginUtils.GetGroupMemberList(Uin);


            for(JavaPluginUtils.GroupMemberInfo infos : memberList){
                NewBuilder.append(infos.UserUin).append("\t")
                        .append(infos.UserName).append("\t")
                        .append(infos.NickName).append("\n");
            }


            String Path = MHookEnvironment.PublicStorageModulePath + "导出数据/群成员列表-"+Uin + "-"+Utils.GetNowTime22()+".txt";
            FileUtils.WriteFileByte(Path,NewBuilder.toString().getBytes());
            new AlertDialog.Builder(context, 3)
                    .setTitle("导出结果")
                    .setMessage("已导出到:"+Path)
                    .setNeutralButton("确定", (dialog1, which1) -> {

                    }).show();
                }).show();
    }
}
