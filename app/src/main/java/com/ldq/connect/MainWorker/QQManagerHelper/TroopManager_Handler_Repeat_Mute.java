package com.ldq.connect.MainWorker.QQManagerHelper;

import android.text.TextUtils;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.TroopManager;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;

public class TroopManager_Handler_Repeat_Mute {

    public static void _caller(Object ChatMsg)
    {
        try{



            String GroupUin  = MField.GetField(ChatMsg,"frienduin",String.class);
            String CheckMessageCount = MConfig.Troop_Get_String("TroopGuard","RepeatForbidden",GroupUin,"Rules");
            if (TextUtils.isEmpty(CheckMessageCount))return;

            String UserUin = MField.GetField(ChatMsg,"senderuin",String.class);

            if (UserUin.startsWith("28541"))return;
            if(TroopManager_Handler_WhiteBlack_Checker.Check_While(GroupUin,UserUin))return;

            String MessageContent = "";
            String ClassName = ChatMsg.getClass().getName();
            if(ClassName.contains("MessageForText") || ClassName.contains("MessageForLongTextMsg"))
            {
                MessageContent = MField.GetField(ChatMsg,"msg",String.class);
            }
            else if(ClassName.contains("MessageForPic"))
            {
                MessageContent = MField.GetField(ChatMsg,"md5",String.class);
            }
            else if(ClassName.contains("MessageForPtt"))
            {
                MessageContent = MField.GetField(ChatMsg,"md5",String.class);
            }else if(ClassName.contains("MessageForMixedMsg"))
            {
                MessageContent = MMethod.CallMethod(ChatMsg,"getSummaryMsg",String.class,new Class[0]);
            }
            else if(ClassName.contains("MessageForReplyText"))
            {
                MessageContent = "[回复]"+MField.GetField(ChatMsg,"msg",String.class);
            }
            else if(ClassName.contains("MessageForScribble"))
            {
                MessageContent = MField.GetField(ChatMsg,"combineFileMd5",String.class);
            }else if(ClassName.contains("MessageForMarketFace"))
            {
                Object MessageContenta = MField.GetField(ChatMsg,"mMarkFaceMessage");
                if(MessageContenta!=null)
                {
                    MessageContent = ""+ MField.GetField(ChatMsg,"msg",String.class) + MField.GetField(MessageContenta,"index",long.class);
                }else
                {
                    MessageContent = MField.GetField(ChatMsg,"msg",String.class);
                }
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
            else if(ClassName.contains("MessageForTroopEffectPic"))
            {
                MessageContent = MField.GetField(ChatMsg,"md5",String.class);
            }
            else if(ClassName.contains("MessageForArkFlashChat"))
            {
                MessageContent = MField.GetField(ChatMsg,"msg",String.class);
            }

            MessageContent = MessageContent.trim();

            if(!TextUtils.isEmpty(MessageContent))
            {
                int Time = CheckMessage(GroupUin,UserUin,MessageContent);
                if(Time!=0)
                {
                    if(UserUin.equals(BaseInfo.GetCurrentUin()))return;
                    TroopManager_Handler_Mute_Log.AddSelfForbiddenStatus(GroupUin,UserUin,Time,"复读禁言:"+MessageContent);
                    TroopManager.Group_Forbidden(GroupUin,UserUin,Time);
                }
            }


        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("RepeatForbidden",th);
        }
    }

    static HashMap<String, LinkedList> TroopMap = new HashMap<>();
    static class UserHash{
        public String Uin;
        public String MessageContent;
    }
    public synchronized static int CheckMessage(String GroupUin,String UserUin,String Message)
    {
        String CheckMessageCount = MConfig.Troop_Get_String("TroopGuard","RepeatForbidden",GroupUin,"Rules");
        int CountD;
        if(TextUtils.isEmpty(CheckMessageCount))return 0;

        CountD = Integer.parseInt(CheckMessageCount);

        int CheckMessageSkipCount = (int) MConfig.Troop_Get_Long("TroopGuard","RepeatForbidden",GroupUin,"RulesCross",0);



        if(CountD != 0)
        {
            if(!TroopMap.containsKey(GroupUin))TroopMap.put(GroupUin,new LinkedList<>());
            LinkedList<UserHash> str = TroopMap.get(GroupUin);
            Iterator<UserHash> it = str.iterator();
            int Count = 1;


            while (it.hasNext())
            {
                UserHash MessageContent = it.next();

                if(Message.equals(MessageContent.MessageContent))Count++;
            }



            if(str.size()>=CountD+CheckMessageSkipCount)str.remove();


            UserHash has = new UserHash();
            has.Uin = UserUin;
            has.MessageContent = Message;
            str.add(has);


            //随机抽取判断是否需要
            if(Count>CountD) {
                if(MConfig.Troop_Get_Boolean("TroopGuard","RepeatForbidden",GroupUin,"Random",false)) {
                    if(Math.random()<0.5) {
                        int RandomCount = CountD - 1;
                        int Get = (int) (Math.random()*RandomCount);
                        ArrayList<String> NewChecker=new ArrayList<>();

                        it = str.iterator();
                        while (it.hasNext()) {
                            UserHash MessageContent = it.next();
                            if(Message.equals(MessageContent.MessageContent)) {
                                NewChecker.add(MessageContent.Uin);
                            }
                        }
                        if(Get<NewChecker.size()) {
                            int Time = (int) MConfig.Troop_Get_Long("TroopGuard","RepeatForbidden",GroupUin,"time",0);
                            TroopManager_Handler_Mute_Log.AddSelfForbiddenStatus(GroupUin,UserUin,Time,"复读禁言(随机):"+Message);
                            TroopManager.Group_Forbidden(GroupUin,NewChecker.get(Get),Time);
                        }
                    }
                }
                int Time = (int) MConfig.Troop_Get_Long("TroopGuard","RepeatForbidden",GroupUin,"time",0);
                return Time;
            }
        }
        return 0;
    }
}
