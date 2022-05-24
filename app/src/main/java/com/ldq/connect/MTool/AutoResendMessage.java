package com.ldq.connect.MTool;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.ConfigPathBuilder;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseCall;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.MessageRecoreFactory;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.QQMessage_Builder;
import com.ldq.connect.Tools.SelectUserTroopDialog;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;

public class AutoResendMessage {
    private static String AutoResendMessageSetPath = ConfigPathBuilder.Get_Current_Path_Set() + "AutoRepeat.json";
    private static JSONObject ItemListObject;
    static {
        try{
            ItemListObject = new JSONObject(FileUtils.ReadFileString(AutoResendMessageSetPath));
        }catch (Exception e){
            ItemListObject = new JSONObject();
        }
    }

    public static void StartSet(){
        Context context = Utils.GetThreadActivity();
        AlertDialog.Builder al = new AlertDialog.Builder(context,3);
        al.setTitle("设置一项转发任务");
        ArrayList<String> mList = new ArrayList<>();
        mList.add("+++添加一项新任务+++");
        ArrayList<String> KeyList = new ArrayList<>();

        Iterator<String> its = ItemListObject.keys();
        while (its.hasNext()){
            try{
                String Keys = its.next();
                JSONObject j2 = ItemListObject.getJSONObject(Keys);
                String Name = j2.optString("name");


                KeyList.add(Keys);
                mList.add(Name);
            }catch (Exception e){

            }
        }
        al.setItems(mList.toArray(new String[0]), (dialog, which) -> {
            if(which==0)EditItem("");
            else EditItem(KeyList.get(which-1));
        }).show();



    }
    private static void EditItem(String ItemID){
        try{
            Context context = Utils.GetThreadActivity();
            LinearLayout mRoot = new LinearLayout(context);
            mRoot.setBackgroundColor(Color.WHITE);
            mRoot.setOrientation(LinearLayout.VERTICAL);

            JSONObject ItemObj = TextUtils.isEmpty(ItemID) ? new JSONObject() : ItemListObject.getJSONObject(ItemID);
            String keys = TextUtils.isEmpty(ItemID) ? Utils.GetNowTime22() : ItemID;


            TextView t1 = new TextView(context);
            t1.setText("当前设置ID:"+keys);
            mRoot.addView(t1);

            TextView t2 = new TextView(context);
            t2.setText("设置名字");
            t2.setTextColor(Color.BLACK);
            mRoot.addView(t2);

            EditText edName = new EditText(context);
            edName.setSingleLine();
            edName.setText(ItemObj.optString("name"));
            mRoot.addView(edName);

            TextView t3 = new TextView(context);
            t3.setText("设置转发规则(留空为全部转发,使用正则匹配,只对文本消息和卡片消息生效)");
            t3.setTextColor(Color.BLACK);
            //mRoot.addView(t3);

            EditText edRule = new EditText(context);
            edRule.setText(ItemObj.optString("rule"));
            //mRoot.addView(edRule);

            CheckBox chSelfMessage = new CheckBox(context);
            chSelfMessage.setTextColor(Color.BLACK);
            chSelfMessage.setText("自己消息也转发");
            chSelfMessage.setChecked(ItemObj.optBoolean("ResendMyself"));
            mRoot.addView(chSelfMessage);


            Button btnSetMode = new Button(context);
            btnSetMode.setText("设置转发格式(仅文字,图片,图文)");
            btnSetMode.setOnClickListener(v->{
                EditText edForm = new EditText(context);
                edForm.setText(ItemObj.optString("Form"));
                edForm.setHint("转发消息时加上格式,如果留空则直接转发原始消息\n" +
                        "变量[MSG]原始消息内容,[TUin]转发来源群号,[Uin]转发来源QQ号,[TName]转发来源群名,[Name]转发来源QQ名字,[Time]转发时间" );

                new AlertDialog.Builder(context,3)
                        .setTitle("设置转发格式")
                        .setView(edForm)
                        .setNegativeButton("保存设置", (dialog, which) -> {
                            try {
                                ItemObj.put("Form",edForm.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }
                        })
                        .show();
            });
           // mRoot.addView(btnSetMode);

            CheckBox chWhileAndBlackList = new CheckBox(context);
            chWhileAndBlackList.setChecked(ItemObj.optBoolean("whileMode"));
            chWhileAndBlackList.setTextColor(Color.BLACK);
            chWhileAndBlackList.setText("使用白名单模式");

            mRoot.addView(chWhileAndBlackList);

            Button btnInputList = new Button(context);
            btnInputList.setText("设置名单");
            btnInputList.setOnClickListener(v -> {
                EditText edInput = new EditText(context);
                edInput.setText(ItemObj.optString("SetList"));
                edInput.setHint("輸入QQ號,用|分割");

                new AlertDialog.Builder(context,3)
                        .setTitle(chWhileAndBlackList.isChecked() ? "设置白名单" : "设置黑名单")
                        .setView(edInput)
                        .setNeutralButton("保存设置",(a,b)->{
                            try {
                                ItemObj.put("SetList",edInput.getText().toString());
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }).show();
            });
            mRoot.addView(btnInputList);

            Button btn = new Button(context);
            btn.setText("设置转发的消息类型");
            btn.setOnClickListener(v->{
                String[] s = new String[]{
                  "纯文字消息","回复消息","图片消息","图文消息","卡片消息","涂鸦消息","语音消息","文件消息"
                };
                boolean[] bChecked = new boolean[8];
                int IRepeatType = ItemObj.optInt("RepeatType");
                bChecked[0] = (IRepeatType & 1)>0;
                bChecked[1] = (IRepeatType & (1<<1))>0;
                bChecked[2] = (IRepeatType & (1<<2))>0;
                bChecked[3] = (IRepeatType & (1<<3))>0;
                bChecked[4] = (IRepeatType & (1<<4))>0;
                bChecked[5] = (IRepeatType & (1<<5))>0;
                bChecked[6] = (IRepeatType & (1<<6))>0;
                bChecked[7] = (IRepeatType & (1<<7))>0;

                new AlertDialog.Builder(context,3)
                        .setMultiChoiceItems(s, bChecked, (dialog, which, isChecked) -> {

                        }).setNeutralButton("保存", (dialog, which) -> {
                            int Sum = 0;
                            Sum |= bChecked[0]?(1<<0):0;
                            Sum |= bChecked[1]?(1<<1):0;
                            Sum |= bChecked[2]?(1<<2):0;
                            Sum |= bChecked[3]?(1<<3):0;
                            Sum |= bChecked[4]?(1<<4):0;
                            Sum |= bChecked[5]?(1<<5):0;
                            Sum |= bChecked[6]?(1<<6):0;
                            Sum |= bChecked[7]?(1<<7):0;

                    try {
                        ItemObj.put("RepeatType",Sum);
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }

                }).show();
            });
            mRoot.addView(btn);




            JSONArray CacheSelectSource = ItemObj.has("source") ? ItemObj.getJSONArray("source") : new JSONArray();
            JSONArray CacheSelectTarget = ItemObj.has("target") ? ItemObj.getJSONArray("target") : new JSONArray();

            Button btnSelectSource = new Button(context);
            btnSelectSource.setText("选择转发来源群聊");
            btnSelectSource.setOnClickListener(v->{
                try{
                    SelectUserTroopDialog select = new SelectUserTroopDialog(context,true,true,false);
                    ArrayList<String> sTroop = new ArrayList<>();
                    ArrayList<String> sUser = new ArrayList<>();
                    for(int i=0;i<CacheSelectSource.length();i++){
                        JSONObject j2 = CacheSelectSource.getJSONObject(i);
                        if(j2.optInt("type")==1) sTroop.add(j2.optString("uin"));
                        else if (j2.optInt("type")==2)sUser.add(j2.optString("uin"));
                    }

                    select.SetSelectedFriend(sUser);
                    select.SetSelectedTroop(sTroop);

                    select.StartShow(result -> {
                        try{
                            for(int i=0;i<CacheSelectSource.length();i++)CacheSelectSource.remove(CacheSelectSource.length()-1);
                            for (SelectUserTroopDialog.SelectResult mItem : result){
                                JSONObject NewItem = new JSONObject();
                                if(mItem.Type == 1){
                                    NewItem.put("type",1);
                                    NewItem.put("uin", mItem.TroopUin);
                                    CacheSelectSource.put(NewItem);
                                }else if(mItem.Type ==2){
                                    NewItem.put("type",2);
                                    NewItem.put("uin",mItem.Uin);
                                    CacheSelectSource.put(NewItem);
                                }
                            }
                        }catch (Exception e){

                        }


                    });
                }catch (Exception e){
                    Utils.ShowToast("Show Error : "+ Log.getStackTraceString(e));
                }

            });
            mRoot.addView(btnSelectSource);

            Button btnSelectTarget = new Button(context);
            btnSelectTarget.setText("选择转发到的群聊");
            btnSelectTarget.setOnClickListener(v->{
                try{
                    SelectUserTroopDialog select = new SelectUserTroopDialog(context,true,true,false);
                    ArrayList<String> sTroop = new ArrayList<>();
                    ArrayList<String> sUser = new ArrayList<>();
                    for(int i=0;i<CacheSelectTarget.length();i++){
                        JSONObject j2 = CacheSelectTarget.getJSONObject(i);
                        if(j2.optInt("type")==1) sTroop.add(j2.optString("uin"));
                        else if (j2.optInt("type")==2)sUser.add(j2.optString("uin"));
                    }

                    select.SetSelectedFriend(sUser);
                    select.SetSelectedTroop(sTroop);

                    select.StartShow(result -> {
                        try{
                            for(int i=0;i<CacheSelectTarget.length();i++)CacheSelectTarget.remove(CacheSelectTarget.length()-1);
                            for (SelectUserTroopDialog.SelectResult mItem : result){
                                JSONObject NewItem = new JSONObject();
                                if(mItem.Type == 1){
                                    NewItem.put("type",1);
                                    NewItem.put("uin", mItem.TroopUin);
                                    CacheSelectTarget.put(NewItem);
                                }else if(mItem.Type ==2){
                                    NewItem.put("type",2);
                                    NewItem.put("uin",mItem.Uin);
                                    CacheSelectTarget.put(NewItem);
                                }
                            }
                        }catch (Exception e){

                        }


                    });
                }catch (Exception e){
                    Utils.ShowToast("Show Error : "+ Log.getStackTraceString(e));
                }
            });
            mRoot.addView(btnSelectTarget);



            new AlertDialog.Builder(context,3)
                    .setView(mRoot)
                    .setTitle("设置转发设置")
                    .setNegativeButton("保存设置", (dialog, which) -> {
                        try {
                            ItemObj.put("name",edName.getText().toString());
                            ItemObj.put("rule",edRule.getText().toString());
                            ItemObj.put("source",CacheSelectSource);
                            ItemObj.put("target",CacheSelectTarget);
                            ItemObj.put("whileMode",chWhileAndBlackList.isChecked());
                            ItemObj.put("ResendMyself",chSelfMessage.isChecked());

                            ItemListObject.put(keys,ItemObj);

                            Save0();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }

                    }).setNeutralButton("删除该项设置", (dialog, which) -> {
                        ItemListObject.remove(keys);

                        Save0();

                    }).show();
        }catch (Exception e){
            Utils.ShowToast("发生了一个不应该发生的异常:"+e);
        }


    }
    public static void onMessage(Object ChatMsg){
        try{
            String TroopUin = MField.GetField(ChatMsg,"frienduin",String.class);
            String SenderUin = MField.GetField(ChatMsg,"senderuin",String.class);
            int Type = MField.GetField(ChatMsg,"istroop",int.class);
            if(Type==1){

                //检测是否为需要转发的消息
                JSONObject j2 = CheckSource(TroopUin,SenderUin,Type);
                if(j2!=null){
                    MLogCat.Print_Debug("Hit J2");

                    //获取转发的目标
                    JSONArray TargetArray = j2.getJSONArray("target");
                    for(int i=0;i<TargetArray.length();i++){
                        JSONObject Item = TargetArray.getJSONObject(i);
                        if(Item.optInt("type")==1){
                            SendMessage(ChatMsg,"",Item.optString("uin"),j2.optInt("RepeatType"));
                        }else if(Item.optInt("type")==2){
                            SendMessage(ChatMsg,Item.optString("uin"),"",j2.optInt("RepeatType"));
                        }

                    }
                }
            }else if (Type ==0){
                JSONObject j2 = CheckSource("",SenderUin,Type);
                if(j2!=null){


                    //获取转发的目标
                    JSONArray TargetArray = j2.getJSONArray("target");

                    for(int i=0;i<TargetArray.length();i++){
                        JSONObject Item = TargetArray.getJSONObject(i);
                        if(Item.optInt("type")==1){
                            SendMessage(ChatMsg,"",Item.optString("uin"),j2.optInt("RepeatType"));
                        }else if(Item.optInt("type")==2){
                            SendMessage(ChatMsg,Item.optString("uin"),"",j2.optInt("RepeatType"));
                        }

                    }
                }
            }

        } catch (Exception exception) {
            MLogCat.Print_Error("AutoRes",exception);
        } finally {
        }


    }
    private static void SendMessage(Object obj,String UserUin,String TroopUin,int Typeaa){
        try{
            String ClassName = obj.getClass().getName();

            if(((Typeaa&1)>0)&&(ClassName.contains("MessageForText") || ClassName.contains("MessageForLongTextMsg") ||ClassName.contains("MessageForFoldMsg")))
            {
                ArrayList AtList1 = MField.GetField(obj,obj.getClass(),"atInfoTempList",ArrayList.class);
                ArrayList AtList2 = MField.GetField(obj,obj.getClass(),"atInfoList",ArrayList.class);
                String mStr = MField.GetField(obj,obj.getClass(),"extStr",String.class);
                JSONObject mJson = new JSONObject(mStr);
                mStr = mJson.optString("troop_at_info_list");
                ArrayList AtList3 = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"),"getTroopMemberInfoFromExtrJson",ArrayList.class,new Class[]{String.class},mStr);
                if(AtList1==null)AtList1=AtList2;
                if(AtList1==null)AtList1=AtList3;
                String nowMsg =  MField.GetField(obj,obj.getClass(),"msg",String.class);
                QQMessage.Message_Send_Text(QQMessage_Builder.Build_SessionInfo(TroopUin,UserUin),nowMsg,AtList1);
            } else if(((Typeaa& (1<<2))>0)&&ClassName.contains("MessageForPic"))
            {
                QQMessage.Message_Send_Pic(QQMessage_Builder.Build_SessionInfo(TroopUin,UserUin),obj);
            }else if(((Typeaa& (1<<6))>0)&&ClassName.contains("MessageForPtt"))
            {
                String CacheStr;
                if(MMethod.CallMethod(obj,"isSendFromLocal",boolean.class,new Class[0]))
                {
                    CacheStr = MMethod.CallMethod(obj,obj.getClass(),"getLocalFilePath",String.class,new Class[0],new Object[0]);
                }else
                {
                    String DownloadUrl = "https://grouptalk.c2c.qq.com"+
                            MField.GetField(obj,"directUrl",String.class);

                    CacheStr = Environment.getExternalStorageDirectory() +  "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/"+ BaseInfo.GetCurrentUin()+"/ptt/"+DownloadUrl.hashCode();

                    HttpUtils.downlaodFile(DownloadUrl,CacheStr);

                }



                QQMessage.Group_Send_Ptt(QQMessage_Builder.Build_SessionInfo(TroopUin,UserUin),CacheStr);
            } else if(((Typeaa& (1<<3))>0)&&ClassName.contains("MessageForMixedMsg"))
            {
                QQMessage.Message_Send_Mix(QQMessage_Builder.Build_SessionInfo(TroopUin,UserUin),obj);
            }else if(((Typeaa& (1<<1))>0)&&ClassName.contains("MessageForReplyText"))
            {
                Object Call = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.replymsg.ReplyMsgSender"),"a", MClass.loadClass("com.tencent.mobileqq.replymsg.ReplyMsgSender"),
                        new Class[0],new Object[0]
                );
                Method mMethod = MMethod.FindMethod("com.tencent.mobileqq.replymsg.ReplyMsgSender","a",void.class,new Class[]{
                        MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                        MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                        MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"),
                        int.class,
                        int.class,
                        boolean.class
                });
                mMethod.invoke(Call, MHookEnvironment.AppInterface,obj , QQMessage_Builder.Build_SessionInfo(TroopUin,UserUin),0,0,false);
            }else if(((Typeaa&(1<<5))>0)&&ClassName.contains("MessageForScribble")) {
                if(TextUtils.isEmpty(UserUin))
                {
                    Object WillingSend = MessageRecoreFactory.CopyToTYMessage(obj,TroopUin);
                    BaseCall.AddAndSendMsg(WillingSend);
                }
            } else if(((Typeaa& (1<<4))>0)&&ClassName.contains("MessageForArkApp"))
            {

                Object  ArkAppMsg= MField.GetField(obj,obj.getClass(),"ark_app_message", MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"));
                String json= MMethod.CallMethod(ArkAppMsg, MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"),"toAppXml",String.class,new Class[0],new Object[0]);

                Method med = MMethod.FindMethod("com.tencent.mobileqq.data.ArkAppMessage","fromAppXml",boolean.class,new Class[]{
                        String.class
                });
                Constructor cons = MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage").getConstructor();
                Object _ArkAppMsg = cons.newInstance();
                med.invoke(_ArkAppMsg,new Object[]{json});
                //获取发送xml消息的方法
                med = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",boolean.class,new Class[]{
                        MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                        MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                        MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage")
                });
                //调用发送消息的方法
                med.invoke(null,new Object[]{MHookEnvironment.AppInterface, QQMessage_Builder.Build_SessionInfo(TroopUin,UserUin),_ArkAppMsg});
            }
            else if(((Typeaa& (1<<4))>0)&&  (ClassName.contains("MessageForStructing") ||ClassName.contains("MessageForTroopPobing")))
            {
                Object Structing = MField.GetField(obj,obj.getClass(),"structingMsg", MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"));
                String xml= MMethod.CallMethod(Structing, MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"),"getXml",String.class,new Class[0],new Object[0]);
                Method BuildStructMsg = MMethod.FindMethod("com.tencent.mobileqq.structmsg.TestStructMsg","a",
                        MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"),new Class[]{String.class});
                Object msgData = BuildStructMsg.invoke(null,new Object[]{xml});
                //获取发送xml消息的方法
                Method SendStructMsg = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",
                        void.class,new Class[]{
                                MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                                MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                                MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg")
                        });
                //调用发送消息的方法
                SendStructMsg.invoke(null,new Object[]{MHookEnvironment.AppInterface,QQMessage_Builder.Build_SessionInfo(TroopUin,UserUin),msgData});
            }else if(((Typeaa& (1<<7))>0) && (ClassName.contains("MessageForFile") || ClassName.contains("MessageForTroopFile"))){
                QQMessage.QQ_Forawrd_File(QQMessage_Builder.Build_SessionInfo(TroopUin,UserUin),obj);
            }
        }catch (Exception e){
            MLogCat.Print_Error("MsgResend",e);
        }

    }

    private static JSONObject CheckSource(String Uin,String UserUin,int Type){
        try{
            if(Type==1)
            {
                Iterator<String> ItemKeys = ItemListObject.keys();
                while (ItemKeys.hasNext()){
                    String key = ItemKeys.next();
                    JSONObject ItemInfo = ItemListObject.getJSONObject(key);
                    JSONArray mArray = ItemInfo.getJSONArray("source");
                    if(!ItemInfo.optBoolean("ResendMyself") && UserUin.equals(BaseInfo.GetCurrentUin()))continue;


                    MainLoop:
                    for(int i=0;i<mArray.length();i++){
                        JSONObject OpenItem = mArray.getJSONObject(i);
                        if(OpenItem.optInt("type")==1 && OpenItem.optString("uin").equals(Uin))
                        {

                            if(ItemInfo.optBoolean("whileMode"))
                            {
                                String[] ListCut = ItemInfo.optString("SetList").split("\\|");
                                for(String s : ListCut)
                                {
                                    if(s.equals(UserUin))return ItemInfo;
                                }
                            }else
                            {
                                String[] ListCut = ItemInfo.optString("SetList").split("\\|");
                                for(String s : ListCut)
                                {
                                    if(s.equals(UserUin))break MainLoop;
                                }
                                return ItemInfo;
                            }
                            break;
                        }
                    }
                }
            }else if (Type==0){
                Iterator<String> ItemKeys = ItemListObject.keys();
                while (ItemKeys.hasNext()){
                    String key = ItemKeys.next();
                    JSONObject ItemInfo = ItemListObject.getJSONObject(key);
                    JSONArray mArray = ItemInfo.getJSONArray("source");
                    for(int i=0;i<mArray.length();i++){
                        JSONObject OpenItem = mArray.getJSONObject(i);
                        if(OpenItem.optInt("type")==2 && OpenItem.optString("uin").equals(UserUin))
                        {
                            return OpenItem;
                        }

                    }
                }
            }


        }catch (Exception e){
            MLogCat.Print_Error("AutoResendChecker",e);

        }
        return null;
    }
    private static void Save0(){
        try{
            FileUtils.WriteFileByte(AutoResendMessageSetPath,ItemListObject.toString().getBytes());
        }finally {

        }
    }
}
