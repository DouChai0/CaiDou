package com.ldq.connect.MainWorker.QQManagerHelper;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.text.InputType;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MTool.RegexDebugTool;
import com.ldq.connect.QQUtils.BaseCall;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.FormItem;
import com.ldq.connect.QQUtils.TroopManager;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

public class TroopManager_Set_DenyWord {
    public static final int FLAG_FORBIDDEN = 1;
    public static final int FLAG_KICK = 1<<1;
    public static final int FLAG_REMOVE = 1<<2;
    public static final int FLAG_ALARM = 1<<3;
    public static final int FLAG_KICK_AND_BAN = 1<<4;

    public static final int EXTRA_FLAG_USE_REGEX = 1;
    public static class CheckWordResult{
        public int CheckMode;
        public int ExtraTime;
        public int ExtraMode;

        public String sReason;
    }
    static ConcurrentHashMap<String,LinkedList<String>> CacheTroopWordMap = new ConcurrentHashMap<>();


    static String TempSelectAlarmGroup;
    public static void GetAddWordDialog(String TroopUin,AlertDialog all)
    {
        TempSelectAlarmGroup = null;
        Activity act = Utils.GetThreadActivity();
        ScrollView sc = new ScrollView(act);
        LinearLayout mRoot =  new LinearLayout(act);
        sc.addView(mRoot);
        mRoot.setOrientation(LinearLayout.VERTICAL);

        EditText Content = new EditText(act);
        Content.setTextSize(16);
        mRoot.addView(Content);

        CheckBox chIsKick = new CheckBox(act);
        chIsKick.setText("触发时踢出");
        chIsKick.setTextColor(Color.BLACK);
        mRoot.addView(chIsKick);

        CheckBox chIsForbidden = new CheckBox(act);
        chIsForbidden.setText("触发时禁言");
        chIsForbidden.setTextColor(Color.BLACK);
        mRoot.addView(chIsForbidden);





        EditText edForbiddenTime = new EditText(act);
        edForbiddenTime.setHint("设置禁言的时间,单位:秒");
        edForbiddenTime.setVisibility(View.GONE);
        edForbiddenTime.setInputType(InputType.TYPE_CLASS_NUMBER);
        mRoot.addView(edForbiddenTime);
        chIsForbidden.setOnCheckedChangeListener((buttonView, isChecked) -> {
            edForbiddenTime.setVisibility(isChecked ? View.VISIBLE:View.GONE);
        });

        CheckBox chRevoke = new CheckBox(act);
        chRevoke.setText("触发时撤回");
        chRevoke.setTextColor(Color.BLACK);
        mRoot.addView(chRevoke);



        CheckBox chIsKickBlack = new CheckBox(act);
        chIsKickBlack.setText("触发时踢出并禁止加入");
        chIsKickBlack.setTextColor(Color.BLACK);
        mRoot.addView(chIsKickBlack);

        CheckBox chIsAlarm = new CheckBox(act);
        chIsAlarm.setText("启用警告机制");
        chIsAlarm.setTextColor(Color.BLACK);
        //mRoot.addView(chIsAlarm);

        CheckBox chIsUseRegex = new CheckBox(act);
        chIsUseRegex.setText("使用正则表达式匹配");
        chIsUseRegex.setTextColor(Color.BLACK);
        mRoot.addView(chIsUseRegex);

        Button btnRegex = new Button(act);
        btnRegex.setText("正则调试");
        btnRegex.setOnClickListener(vaa->{
            RegexDebugTool.ShowRegexDebug(Content.getText().toString());
        });
        mRoot.addView(btnRegex);

        Button btnSelectAlarm = new Button(act);
        btnSelectAlarm.setText("选择警告组("+TempSelectAlarmGroup+")");
        btnSelectAlarm.setOnClickListener(v->{
            HashMap<String,String> map = TroopManager_Set_Alarm_Group.GetKey_Name_List();
            ArrayList<String> KeyList = new ArrayList<>();

            KeyList.addAll(map.keySet());
            String[] Names = new String[KeyList.size()];
            for(int i=0;i<KeyList.size();i++) Names[i] = map.get(KeyList.get(i));


            new AlertDialog.Builder(act,3)
                    .setTitle("选择需要使用的警告组")
                    .setItems(Names, (dialog, which) -> {
                        TempSelectAlarmGroup = KeyList.get(which);
                    }).show();
        });

        mRoot.addView(btnSelectAlarm);


        new AlertDialog.Builder(act,3)
                .setTitle("添加一项新的违禁词状态")
                .setView(sc)
                .setNeutralButton("保存", (dialog, which) -> {
                    String Summary = "";
                    int Flags = 0;
                    Summary = DataUtils.bytesToHex(Content.getText().toString().getBytes());

                    if(chIsKick.isChecked()) Flags |= FLAG_KICK;
                    if(chIsKickBlack.isChecked()) Flags |= FLAG_KICK_AND_BAN;
                    if(chRevoke.isChecked()) Flags |= FLAG_REMOVE;
                    if(chIsAlarm.isChecked()) Flags |= FLAG_ALARM;
                    if(chIsForbidden.isChecked()) Flags |= FLAG_FORBIDDEN;

                    Summary = Summary + "::" + Flags;  //1
                    Summary = Summary + "::" + TempSelectAlarmGroup;  // 2

                    Summary = Summary + "::" + (edForbiddenTime.getText().toString().isEmpty() ? "0" : edForbiddenTime.getText().toString());//禁言时间  // 3

                    int ExtraFlags = 0;
                    if(chIsUseRegex.isChecked()) ExtraFlags |= EXTRA_FLAG_USE_REGEX;
                    Summary = Summary + "::" + ExtraFlags; // 4

                    AddTroopBanWord(TroopUin,Summary);
                    all.dismiss();


                }).show();
    }

    public static void AddTroopBanWord(String TroopUin,String WordData) {
        LinkedList l;
        if(CacheTroopWordMap.containsKey(TroopUin))
        {
            l = CacheTroopWordMap.get(TroopUin);
        }else
        {
            l = new LinkedList();
        }
        l.add(WordData);
        FlushCacheData(TroopUin);
    }
    public static void ShowDenyWordForTroop(String TroopUin)
    {
        UpdataCheckWordCache(TroopUin);
        Activity act = Utils.GetThreadActivity();
        ScrollView sc = new ScrollView(act);
        LinearLayout l = new LinearLayout(act);
        sc.addView(l);
        l.setOrientation(LinearLayout.VERTICAL);


        ArrayList<String> ShowList = new ArrayList<>();
        LinkedList<String> CacheSetList = new LinkedList<>();


        AlertDialog al = new AlertDialog.Builder(act,3)
                .setTitle("当前群聊设置的违禁词")
                .setView(sc)
                .setNeutralButton("保存", (dialog, which) -> {
                    Iterator<String> it = CacheSetList.iterator();
                    while (it.hasNext())
                    {
                        if(it.next()==null)it.remove();
                    }
                    CacheTroopWordMap.put(TroopUin,CacheSetList);
                    FlushCacheData(TroopUin);
                }).setOnCancelListener(dialog -> {
                    Iterator<String> it = CacheSetList.iterator();
                    while (it.hasNext())
                    {
                        if(it.next()==null)it.remove();
                    }
                    CacheTroopWordMap.put(TroopUin,CacheSetList);
                    FlushCacheData(TroopUin);
                })
                .create();
        l.addView(FormItem.AddListItem(act,"添加一个违禁词",v->GetAddWordDialog(TroopUin,al)));



        //获取缓存的数据并解析加载显示
        if(CacheTroopWordMap.containsKey(TroopUin))
        {
            LinkedList<String> CurrentMap = CacheTroopWordMap.get(TroopUin);
            for(String SSet : CurrentMap)
            {
                String[] Cut = SSet.split("::");
                if(Cut.length>4)
                {
                    String ShowInfoData = new String(DataUtils.hexToByteArray(Cut[0]));
                    ShowList.add(ShowInfoData);

                    CacheSetList.add(SSet);
                }
            }
        }
        for(int i=0;i<ShowList.size();i++)
        {
            int CheckPos = i;
            l.addView(FormItem.AddListItem(act,ShowList.get(i),v->{
                String NowStringPos = CacheSetList.get(CheckPos);
                if(NowStringPos==null)return;

                String[] Cut = NowStringPos.split("::");
                if(Cut.length>4)
                {
                    LinearLayout mRoot =  new LinearLayout(act);
                    mRoot.setOrientation(LinearLayout.VERTICAL);

                    EditText Content = new EditText(act);
                    Content.setTextSize(16);
                    Content.setText(DataUtils.HexToString(Cut[0]));
                    mRoot.addView(Content);

                    int Flagsaa = Integer.parseInt(Cut[1]);

                    CheckBox chIsKick = new CheckBox(act);
                    chIsKick.setText("触发时踢出");
                    chIsKick.setTextColor(Color.BLACK);
                    chIsKick.setChecked((Flagsaa & FLAG_KICK) > 0);
                    mRoot.addView(chIsKick);

                    CheckBox chIsForbidden = new CheckBox(act);
                    chIsForbidden.setText("触发时禁言");
                    chIsForbidden.setChecked((Flagsaa & FLAG_FORBIDDEN) > 0);
                    chIsForbidden.setTextColor(Color.BLACK);
                    mRoot.addView(chIsForbidden);

                    EditText edForbiddenTime = new EditText(act);
                    edForbiddenTime.setHint("设置禁言的时间,单位:秒");
                    if((Flagsaa & FLAG_FORBIDDEN)== 0)edForbiddenTime.setVisibility(View.GONE);
                    edForbiddenTime.setText(Cut[3]);
                    edForbiddenTime.setInputType(InputType.TYPE_CLASS_NUMBER);
                    mRoot.addView(edForbiddenTime);
                    chIsForbidden.setOnCheckedChangeListener((buttonView, isChecked) -> {
                        edForbiddenTime.setVisibility(isChecked ? View.VISIBLE:View.GONE);
                    });

                    CheckBox chIsRemove = new CheckBox(act);
                    chIsRemove.setText("触发时撤回");
                    chIsRemove.setChecked((Flagsaa & FLAG_REMOVE) > 0);
                    chIsRemove.setTextColor(Color.BLACK);
                    mRoot.addView(chIsRemove);





                    CheckBox chIsKickBlack = new CheckBox(act);
                    chIsKickBlack.setText("触发时踢出并禁止加入");
                    chIsKickBlack.setTextColor(Color.BLACK);
                    chIsKickBlack.setChecked((Flagsaa & FLAG_KICK_AND_BAN) >0);
                    mRoot.addView(chIsKickBlack);


                    TempSelectAlarmGroup = Cut[2];
                    Button btnSelectAlarm = new Button(act);
                    btnSelectAlarm.setText("选择警告组("+TempSelectAlarmGroup+")");
                    btnSelectAlarm.setOnClickListener(vx->{
                        HashMap<String,String> map = TroopManager_Set_Alarm_Group.GetKey_Name_List();
                        ArrayList<String> KeyList = new ArrayList<>();

                        KeyList.addAll(map.keySet());
                        String[] Names = new String[KeyList.size()];

                        for(int ix=0;ix<KeyList.size();ix++) Names[ix] = map.get(KeyList.get(ix));

                        new AlertDialog.Builder(act,3)
                                .setTitle("选择需要使用的警告组")
                                .setItems(Names, (dialog, which) -> {
                                    TempSelectAlarmGroup = KeyList.get(which);
                                }).show();
                    });

                    mRoot.addView(btnSelectAlarm);

                    CheckBox chIsUseRegex = new CheckBox(act);
                    chIsUseRegex.setText("使用正则表达式匹配");
                    chIsUseRegex.setChecked((Integer.parseInt(Cut[4]) & EXTRA_FLAG_USE_REGEX)>0);
                    chIsUseRegex.setTextColor(Color.BLACK);
                    mRoot.addView(chIsUseRegex);

                    Button btnRegex = new Button(act);
                    btnRegex.setText("正则调试");
                    btnRegex.setOnClickListener(vaa->{
                        RegexDebugTool.ShowRegexDebug(Content.getText().toString());
                    });
                    mRoot.addView(btnRegex);


                    new AlertDialog.Builder(act,3)
                            .setTitle("设置当前违禁词处理状态")
                            .setView(mRoot)
                            .setNeutralButton("保存", (dialog, which) -> {
                                String Summary = "";
                                int Flags = 0;
                                Summary = DataUtils.bytesToHex(Content.getText().toString().getBytes());

                                if(chIsKick.isChecked()) Flags |= FLAG_KICK;
                                if(chIsKickBlack.isChecked()) Flags |= FLAG_KICK_AND_BAN;
                                if(chIsRemove.isChecked()) Flags |= FLAG_REMOVE;
                                if(chIsForbidden.isChecked()) Flags |= FLAG_FORBIDDEN;

                                Summary = Summary + "::" + Flags;  //1
                                Summary = Summary + "::" + TempSelectAlarmGroup;  // 2

                                Summary = Summary + "::" + (edForbiddenTime.getText().toString().isEmpty() ? "0" : edForbiddenTime.getText().toString());//禁言时间  // 3

                                int ExtraFlags = 0;
                                if(chIsUseRegex.isChecked()) ExtraFlags |= EXTRA_FLAG_USE_REGEX;
                                Summary = Summary + "::" + ExtraFlags; // 4

                                CacheSetList.set(CheckPos,Summary);

                                al.cancel();
                            }).setPositiveButton("删除", (dialog, which) -> {
                                CacheSetList.set(CheckPos,null);
                                al.cancel();
                            }).
                            show();

                }
            }));
        }




        al.show();
    }
    public static void FlushCacheData(String TroopUin)
    {
        if(CacheTroopWordMap.containsKey(TroopUin)) {
            LinkedList mList = CacheTroopWordMap.get(TroopUin);
            MConfig.Put_List("TroopGuard", "BanWord", TroopUin, mList);
        }
    }
    public static void UpdataCheckWordCache(String TroopUin)
    {
        if(!CacheTroopWordMap.containsKey(TroopUin))
        {
            List MList = MConfig.Get_List("TroopGuard","BanWord",TroopUin);

            if(MList==null || !(MList instanceof  LinkedList)) {
                CacheTroopWordMap.put(TroopUin,new LinkedList<>());
            }else {
                CacheTroopWordMap.put(TroopUin, (LinkedList<String>) MList);
            }
        }
    }
    public synchronized static CheckWordResult[] DenyWordChecker(String TroopUin,String UserUin,String CheckWord,Object CheckGroup)
    {
        if(TroopManager_Handler_WhiteBlack_Checker.Check_While(TroopUin,UserUin))return new CheckWordResult[0];
        UpdataCheckWordCache(TroopUin);
        ArrayList<CheckWordResult> mResult = new ArrayList<>();
        LinkedList<String> l = CacheTroopWordMap.get(TroopUin);

        if(l==null)return new CheckWordResult[0];



        for (String WordSet : l) {

            try {
                String[] SetCut = WordSet.split("::");
                if (SetCut.length > 4) {
                    String WordContent = SetCut[0];
                    if(TextUtils.isEmpty(WordContent))continue;
                    int SetMode = Integer.parseInt(SetCut[1]);
                    String AlarmSetMode = SetCut[2];

                    int SetExtraFlag = SetCut[3].length()==0?0:Integer.parseInt(SetCut[3]);
                    int ExtraFlag = Integer.parseInt(SetCut[4]);

                    WordContent = new String(DataUtils.hexToByteArray(WordContent));
                    if ((ExtraFlag & EXTRA_FLAG_USE_REGEX) > 0) {
                        if (RegexDebugTool.Pattern_Matches(CheckWord,WordContent)) {

                            if(!TextUtils.isEmpty(AlarmSetMode) && !AlarmSetMode.equalsIgnoreCase("null"))
                            {
                                int Result = TroopManager_Set_Alarm_Group.CheckAndNotifyUserAlarmStatus(TroopUin,UserUin,AlarmSetMode,CheckGroup);
                                if(Result == TroopManager_Set_Alarm_Group.ALARM_REJECT)return new CheckWordResult[0];
                            }

                            CheckWordResult[] Check = InitUserCheckResult(SetMode, SetExtraFlag,"触发违禁词规则:"+CheckWord+"("+WordContent+")");

                            mResult.addAll(Arrays.asList(Check));
                        }
                    } else {
                        if (CheckWord.contains(WordContent)) {

                            if(!TextUtils.isEmpty(AlarmSetMode) && !AlarmSetMode.equalsIgnoreCase("null"))
                            {
                                int Result = TroopManager_Set_Alarm_Group.CheckAndNotifyUserAlarmStatus(TroopUin,UserUin,AlarmSetMode,CheckGroup);
                                if(Result == TroopManager_Set_Alarm_Group.ALARM_REJECT)return new CheckWordResult[0];
                            }

                            CheckWordResult[] Check = InitUserCheckResult(SetMode, SetExtraFlag,"触发违禁词:"+CheckWord+"("+WordContent+")");
                            mResult.addAll(Arrays.asList(Check));
                        }
                    }

                }
            } catch (Exception ex) {
                MLogCat.Print_Error("WordDenyHandler", "SetInfo \"" + WordSet + "\" Decode Error:" + ex);
            }
        }
        return mResult.toArray(new CheckWordResult[0]);
    }
    public static CheckWordResult[] InitUserCheckResult(int SetMode,int SetExtraFlag,String FromReason)
    {
        ArrayList<CheckWordResult> NewRet = new ArrayList<>();

        if((SetMode & FLAG_KICK)>0)
        {
            CheckWordResult result = new CheckWordResult();
            result.CheckMode = 1;
            NewRet.add(result);
        }

        if((SetMode & FLAG_KICK_AND_BAN)>0)
        {
            CheckWordResult result = new CheckWordResult();
            result.CheckMode = 2;
            NewRet.add(result);
        }

        if((SetMode & FLAG_FORBIDDEN)>0)
        {
            CheckWordResult result = new CheckWordResult();
            result.CheckMode = 3;
            result.ExtraTime = SetExtraFlag;
            result.sReason = FromReason;
            NewRet.add(result);
        }

        if((SetMode & FLAG_REMOVE) > 0)
        {
            CheckWordResult result = new CheckWordResult();
            result.CheckMode = 4;
            NewRet.add(result);
        }

        if((SetMode & FLAG_ALARM)>0)
        {
            CheckWordResult result = new CheckWordResult();
            result.CheckMode = 6;
            NewRet.add(result);
        }
        return NewRet.toArray(new CheckWordResult[0]);
    }
    public static void MessageChecker(Object ChatMsg)
    {
        try{
            int iIsTroop = MField.GetField(ChatMsg,"istroop",int.class);
            if(iIsTroop==1)
            {
                String TroopUin = MField.GetField(ChatMsg,"frienduin",String.class);
                String UserUin = MField.GetField(ChatMsg,"senderuin",String.class);


                if(UserUin.equals(BaseInfo.GetCurrentUin()))return;//检测是否为自己QQ
                if (UserUin.startsWith("28541"))return;
                if(TroopManager_Handler_WhiteBlack_Checker.Check_While(TroopUin,UserUin))return;//检测是否为白名单

                String ClassName = ChatMsg.getClass().getName();
                String MessageContent = "";
                if(ClassName.contains("MessageForText") || ClassName.contains("MessageForLongTextMsg")
                        || ClassName.contains("MessageForFoldedMsg")//只检测三种类型的消息
                ) {
                    MessageContent = MField.GetField(ChatMsg,"msg",String.class);
                }else if(ClassName.contains("MessageForMixedMsg"))
                {
                    MessageContent = MMethod.CallMethod(ChatMsg,"getSummaryMsg",String.class,new Class[0]);
                }
                else if(ClassName.contains("MessageForReplyText"))
                {
                    MessageContent = "[回复]"+MField.GetField(ChatMsg,"msg",String.class);
                }else if(ClassName.contains("MessageForArkApp"))
                {
                    Object  ArkAppMsg= MField.GetField(ChatMsg,ChatMsg.getClass(),"ark_app_message", MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"));
                    String json= MMethod.CallMethod(ArkAppMsg, MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"),"toAppXml",String.class,new Class[0],new Object[0]);
                    MessageContent = json;
                }
                else if(ClassName.contains("MessageForStructing") ||ClassName.contains("MessageForTroopPobing"))
                {
                    Object Structing = MField.GetField(ChatMsg,ChatMsg.getClass(),"structingMsg", MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"));
                    String xml= MMethod.CallMethod(Structing, MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"),"getXml",String.class,new Class[0],new Object[0]);
                    MessageContent = xml;
                }


                if(!TextUtils.isEmpty(MessageContent))
                {
                    CheckWordResult[] result = DenyWordChecker(TroopUin,UserUin,MessageContent,ChatMsg);

                    for(CheckWordResult Word : result)
                    {
                        switch (Word.CheckMode)
                        {
                            case 1:{
                                TroopManager.Group_Kick(TroopUin,UserUin,false);
                                break;
                            }
                            case 2:{
                                TroopManager.Group_Kick(TroopUin,UserUin,true);
                                break;
                            }
                            case 3:{
                                TroopManager_Handler_Mute_Log.AddSelfForbiddenStatus(TroopUin,UserUin,Word.ExtraTime,Word.sReason);
                                TroopManager.Group_Forbidden(TroopUin,UserUin,Word.ExtraTime);
                                break;
                            }
                            case 4:{
                                BaseCall.DelayRemoveMessage(ChatMsg);
                                break;
                            }
                            case 6:{

                            }
                        }
                    }
                }
            }
        }catch (Exception e)
        {
            MLogCat.Print_Error("MessageChecker",e);
        }


    }
}
