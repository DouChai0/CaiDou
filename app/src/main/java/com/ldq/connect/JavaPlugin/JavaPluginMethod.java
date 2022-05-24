package com.ldq.connect.JavaPlugin;


import android.app.Activity;
import android.app.AlertDialog;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.FloatWindow.Init;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MainWorker.ProxyHook.Hook_Request_Join_Troop;
import com.ldq.connect.QQUtils.BaseCall;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.MessageRecoreFactory;
import com.ldq.connect.QQUtils.QQApiUtils;
import com.ldq.connect.QQUtils.QQGuild_Manager;
import com.ldq.connect.QQUtils.QQGuild_Utils;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.QQMessage_Builder;
import com.ldq.connect.QQUtils.QQMessage_Transform;
import com.ldq.connect.QQUtils.QQSessionUtils;
import com.ldq.connect.QQUtils.QQTicketUtils;
import com.ldq.connect.QQUtils.QQTools;
import com.ldq.connect.QQUtils.TroopManager;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.Random;
import java.util.Set;


public class JavaPluginMethod
{
    public static class RequestInfo{
        public String GroupUin;
        public String UserUin;
        public String Answer;
        public String RequestText;
        public String RequestSource;

        public Object source;
    }
    public static class mMessageInfoList
    {
        public String Message;
        public int MessageType;
        public String ExtraData;
    }
    static
    {
        File mF = new File(MHookEnvironment.PublicStorageModulePath + "Cache/");
        if(!mF.exists())mF.mkdirs();
    }
    public String PluginID;
    public ArrayList<String> MyList;
    public boolean mListMode_White;
    boolean Permission_Skey = false;
    boolean Show_Dialog = false;
    String PluginFlag = null;
    public static void Plugin_SendLike(String UserUin,int Count)
    {
        QQTools.SendLike(UserUin,Count);
    }
    public static void Plugin_SendAntEmo(String GroupUin,String UserUin,int SevrID,int StickerID) throws Exception {
        Object sObj;

        if(TextUtils.isEmpty(GroupUin))
        {
            sObj = MessageRecoreFactory.Build_RawMessageRecord_Friend(UserUin,-8018);
        }else
        {
            sObj = MessageRecoreFactory.Build_RawMessageRecord_Troop(GroupUin,-8018);
        }

        MField.SetField(sObj,"sevrId",SevrID);
        MField.SetField(sObj,"stickerId",""+StickerID);
        MField.SetField(sObj,"text","/");
        MField.SetField(sObj,"packId", "1");
        MField.SetField(sObj,"msgVia",0);

        BaseCall.AddAndSendMsg(sObj);
    }
    public static void Plugin_Handler_TroopRequest(Object SObj,boolean IsAccept,String Reason,boolean IsBan)
    {
        if(SObj instanceof RequestInfo)
        {
            RequestInfo infos = (RequestInfo) SObj;
            if(IsAccept)
            {
                Hook_Request_Join_Troop.Handler_AcceptRequest(infos.source);
            }else
            {
                Hook_Request_Join_Troop.Handler_RefuseRequest(infos.source,Reason,IsBan);
            }
        }
    }
    public static void Plugin_SendCommonMessageNoStress(String GroupUin,String UserUin,String MessageContent)
    {
        try{
            mMessageInfoList[] mWillSendInfo = JavaPluginUtils.InitMessageData(MessageContent);
            ArrayList RecordList = new ArrayList();
            ArrayList AtInfo = new ArrayList();
            String TextMsg = "";
            boolean HitMixmsg = false;
            int length = 0;
            for(mMessageInfoList mElement : mWillSendInfo)
            {
                if(mElement.MessageType==1)
                {
                    Object mTextMsgData = QQMessage_Builder.Build_Text(GroupUin,mElement.Message);
                    length+=mElement.Message.length();
                    TextMsg = TextMsg + mElement.Message;
                    RecordList.add(mTextMsgData);
                }
                else if(mElement.MessageType == 2)
                {
                    Object mPicMsgData = QQMessage_Builder.Build_Pic(QQMessage_Builder.Build_SessionInfo(GroupUin,UserUin), FileUtils.GetPath(mElement.Message));
                    RecordList.add(mPicMsgData);
                    HitMixmsg = true;
                }
                else if (mElement.MessageType == 3)
                {
                    String AtText = "@"+ TroopManager.GetMemberName(GroupUin,mElement.Message)+" ";
                    if(mElement.Message.equals("0"))
                    {
                        AtText = "@全体成员 ";
                    }

                    TextMsg = TextMsg + AtText;
                    Object mTextMsg = QQMessage_Builder.Build_Text(GroupUin,AtText);
                    AtInfo.add(QQMessage_Builder.Build_AtInfo(mElement.Message,AtText, (short) length));
                    length+=AtText.length();
                    RecordList.add(mTextMsg);
                }
            }
            if(HitMixmsg) {
                QQMessage_Transform.Message_Send_Mix(GroupUin,UserUin,RecordList);
            }
            else {
                QQMessage_Transform.Message_Send_Text(GroupUin,UserUin,TextMsg,AtInfo);
            }

        }
        catch (Exception ex)
        {
            Utils.ShowToast("E1000:"+Log.getStackTraceString(ex));
        }

    }
    private boolean Check_Is_Permission(String GroupUin){
        if(TextUtils.isEmpty(GroupUin))return true;
        if(mListMode_White)
        {
            if(!MyList.contains(GroupUin))return false;
        }
        else
        {
            if(MyList.contains(GroupUin))return false;
        }
        return true;
    }

    public void Plugin_SendPaiyipai(String GroupUin,String UserUin)
    {
        if(Check_Is_Permission(GroupUin)){
            QQTools.SendPai(GroupUin,UserUin);
        }



    }
    public void Plugin_SendCommonMessage(String GroupUin,String UserUin,String MessageContent)
    {
        try{
            if(mListMode_White)
            {
                if(!MyList.contains(GroupUin))return;
            }
            else
            {
                if(MyList.contains(GroupUin))return;
            }
            mMessageInfoList[] mWillSendInfo = JavaPluginUtils.InitMessageData(MessageContent);
            ArrayList RecordList = new ArrayList();
            ArrayList AtInfo = new ArrayList();
            String TextMsg = "";
            boolean HitMixmsg = false;
            int length = 0;
            for(mMessageInfoList mElement : mWillSendInfo)
            {
                if(mElement.MessageType==1)
                {
                    Object mTextMsgData = QQMessage_Builder.Build_Text(GroupUin,mElement.Message);
                    length+=mElement.Message.length();
                    TextMsg = TextMsg + mElement.Message;
                    RecordList.add(mTextMsgData);
                }
                else if(mElement.MessageType == 2)
                {
                    Object mPicMsgData = QQMessage_Builder.Build_Pic(QQMessage_Builder.Build_SessionInfo(GroupUin,UserUin), FileUtils.GetPath(mElement.Message));
                    RecordList.add(mPicMsgData);
                    HitMixmsg = true;
                }
                else if (mElement.MessageType == 3)
                {
                    String AtText = "@"+ TroopManager.GetMemberName(GroupUin,mElement.Message)+" ";
                    if(mElement.Message.equals("0"))
                    {
                        AtText = "@全体成员 ";
                    }
                    TextMsg = TextMsg + AtText;
                    Object mTextMsg = QQMessage_Builder.Build_Text(GroupUin,AtText);
                    AtInfo.add(QQMessage_Builder.Build_AtInfo(mElement.Message,AtText, (short) length));
                    length+=AtText.length();
                    RecordList.add(mTextMsg);
                }
            }
            if(HitMixmsg)
            {
                QQMessage_Transform.Message_Send_Mix(GroupUin,UserUin,RecordList);
            }
            else
            {
                QQMessage_Transform.Message_Send_Text(GroupUin,UserUin,TextMsg+"\u0000"+"\u0000",AtInfo);
            }

        }
        catch (Exception ex)
        {
            Utils.ShowToast("E1000:"+Log.getStackTraceString(ex));
        }

    }
    public void Plugin_SendCommonMessageReply(String GroupUin,String UserUin,String MessageContent,Object Rawmsg)
    {
        try{
            if(mListMode_White)
            {
                if(!MyList.contains(GroupUin))return;
            }
            else
            {
                if(MyList.contains(GroupUin))return;
            }
            if(Rawmsg==null)
            {
                Plugin_SendCommonMessage(GroupUin,UserUin,MessageContent);
                return;
            }
            Object replyinfo;
            if(Rawmsg instanceof JavaPlugin.MessageData)
            {
                JavaPlugin.MessageData sData = (JavaPlugin.MessageData) Rawmsg;
                replyinfo = QQMessage_Builder.Build_ReplyText(sData.msg," ",GroupUin);
                MMethod.CallMethod(replyinfo,"prewrite",void.class,new Class[0]);
            }else
            {
                return;
            }



            mMessageInfoList[] mWillSendInfo = JavaPluginUtils.InitMessageData(MessageContent);
            ArrayList RecordList = new ArrayList();

            RecordList.add(replyinfo);

            ArrayList AtInfo = new ArrayList();
            String TextMsg = "";
            boolean HitMixmsg = false;
            int length = 0;
            for(mMessageInfoList mElement : mWillSendInfo)
            {
                if(mElement.MessageType==1)
                {
                    Object mTextMsgData = QQMessage_Builder.Build_Text(GroupUin,mElement.Message);
                    length+=mElement.Message.length();
                    TextMsg = TextMsg + mElement.Message;
                    RecordList.add(mTextMsgData);
                }
                else if(mElement.MessageType == 2)
                {
                    Object mPicMsgData = QQMessage_Builder.Build_Pic(QQMessage_Builder.Build_SessionInfo(GroupUin,UserUin), FileUtils.GetPath(mElement.Message));
                    RecordList.add(mPicMsgData);
                    HitMixmsg = true;
                }
                else if (mElement.MessageType == 3)
                {
                    String AtText = "@"+ TroopManager.GetMemberName(GroupUin,mElement.Message)+"";

                    if(mElement.Message.equals("0"))
                    {
                        AtText = "@全体成员 ";
                    }

                    TextMsg = TextMsg + AtText;
                    Object mTextMsg = QQMessage_Builder.Build_Text(GroupUin,AtText);
                    AtInfo.add(QQMessage_Builder.Build_AtInfo(mElement.Message,AtText, (short) length));
                    length+=AtText.length();
                    RecordList.add(mTextMsg);
                }
            }
            if(HitMixmsg)
            {
                QQMessage_Transform.Message_Send_Mix(GroupUin,UserUin,RecordList);
            }
            else
            {
                QQMessage_Transform.Message_Send_Text(GroupUin,UserUin,TextMsg,AtInfo);
            }

        }
        catch (Exception ex)
        {
            Utils.ShowToast("E1000:"+Log.getStackTraceString(ex));
        }

    }

    public void Plugin_SendCardMsg(String GroupUin,String UserUin,String XmlData) throws Exception {
        if(mListMode_White)
        {
            if(!MyList.contains(GroupUin))return;
        }
        else
        {
            if(MyList.contains(GroupUin))return;
        }
            if(XmlData.startsWith("{"))
            {
                QQMessage.Message_Send_ArkApp(QQMessage_Builder.Build_SessionInfo(GroupUin, UserUin), QQMessage_Builder.Build_ArkAppMsg(XmlData));
            }
            else
            {
                QQMessage.Message_Send_Xml(QQMessage_Builder.Build_SessionInfo(GroupUin, UserUin), QQMessage_Builder.Build_AbsStructMsg(XmlData));
            }

    }
    public void Plugin_SendPicMsg(String GroupUin,String UserUin,String Path)
    {
        if(mListMode_White)
        {
            if(!MyList.contains(GroupUin))return;
        }
        else
        {
            if(MyList.contains(GroupUin))return;
        }
        String mSource = FileUtils.GetPath(Path);
        QQMessage_Transform.Message_Send_Pic(GroupUin,UserUin,mSource);
    }

    public void Plugin_SendVoide(String GroupUin,String UserUin,String Path)
    {
        try {
            if(mListMode_White)
            {
                if(!MyList.contains(GroupUin))return;
            }
            else
            {
                if(MyList.contains(GroupUin))return;
            }
            File m = new File(Path);
            String CopyTo = Environment.getExternalStorageDirectory() +  "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/"+ BaseInfo.GetCurrentUin()+"/ptt/"+m.getName();
            FileUtils.copy(Path,CopyTo,4096);
            Object ConstructSession = QQMessage_Builder.Build_SessionInfo(GroupUin,UserUin);
            QQMessage.Group_Send_Ptt(ConstructSession ,CopyTo);
            //QQCall.Group_Send_Ptt(GroupUin,Path);
        } catch (Exception e) {
            Utils.ShowToast("E0003:"+e.toString());
        }
    }
    public static <T> void Toast(T ToastDatas)
    {
        Utils.ShowToast(""+ToastDatas);
    }
    public static String Plugin_AddGroupMenu(String mShowName,String mInvokeMethod,String mPluginID)
    {
        for(JavaPlugin.PluginData Data : JavaPlugin.PluginList)
        {
            if(Data.PluginID.equalsIgnoreCase(mPluginID))
            {
                String Ramdom = Math.random()+"";
                Set<String> mKeys = Data.GroupMenuList.keySet();
                for(String mSet : mKeys)
                {
                    String Fl = Data.GroupMenuList.get(mSet);
                    if(Fl.equals(mInvokeMethod))return "";
                }
                Data.GroupMenuList.put(mShowName+"->>"+Ramdom,mInvokeMethod);
                if(!Data.isShowAlerm)
                {
                    Data.isShowAlerm = true;
                    //Utils.ShowToast("脚本  "+Data.Name+"  添加了群聊便捷菜单,可以长按群聊标题或者群聊右上角进行查看");
                }
                return Ramdom;
            }
        }
        return "";
    }
    public String Plugin_AddUserMenu(String mShowName,String mInvokeMethod)
    {
        for(JavaPlugin.PluginData Data : JavaPlugin.PluginList)
        {
            if(Data.PluginID.equalsIgnoreCase(PluginID))
            {
                String Ramdom = Math.random()+"";
                Set<String> mKeys = Data.GroupUserClickMenu.keySet();
                for(String mSet : mKeys)
                {
                    String Fl = Data.GroupUserClickMenu.get(mSet);
                    if(Fl.equals(mInvokeMethod))return "";
                }
                Data.GroupUserClickMenu.put(mShowName+"->>"+Ramdom,mInvokeMethod);
                if(!Data.isShowAlerm)
                {
                    Data.isShowAlerm = true;
                    //Utils.ShowToast("脚本  "+Data.Name+"  添加了群聊便捷菜单,可以长按群聊标题或者群聊右上角进行查看");
                }
                return Ramdom;
            }
        }
        return "";
    }
    public void Plugin_RemoveUserMenu(String ItemID)
    {
        for(JavaPlugin.PluginData Data : JavaPlugin.PluginList)
        {
            if(Data.PluginID.equalsIgnoreCase(PluginID))
            {
                Set<String> mSet = Data.GroupUserClickMenu.keySet();
                for(String mShowSet : mSet)
                {
                    if(mShowSet.endsWith("->>"+ItemID))
                    {
                        Data.GroupUserClickMenu.remove(mShowSet);
                    }

                }
            }
        }
    }


    public static void Plugin_RemoveGroupMenu(String mPluginID,String ItemID)
    {
        for(JavaPlugin.PluginData Data : JavaPlugin.PluginList)
        {
            if(Data.PluginID.equalsIgnoreCase(mPluginID))
            {
                Set<String> mSet = Data.GroupMenuList.keySet();
                for(String mShowSet : mSet)
                {
                    if(mShowSet.endsWith("->>"+ItemID))
                    {
                        Data.GroupMenuList.remove(mShowSet);
                    }

                }
            }
        }
    }
    public int Plugin_GetChatType()
    {
        if(Init.IsInMainWindow)return -1;
        return QQSessionUtils.GetSessionID();
    }
    public String Plugin_GetChatGroupUin()
    {
        return QQSessionUtils.GetCurrentGroupUin();
    }
    public String Plugin_GetChatFriendUin()
    {
        return QQSessionUtils.GetCurrentFriendUin();
    }

    public void Plugin_SendShake(String GroupUin)
    {
        try
        {
            if(mListMode_White)
            {
                if(!MyList.contains(GroupUin))return;
            }
            else
            {
                if(MyList.contains(GroupUin))return;
            }
            QQMessage.Message_Send_ShakeWindow(GroupUin);
        } catch (Exception e) {
            Utils.ShowToast("抖动错误"+Log.getStackTraceString(e));
        }
    }
    public void Plugin_SendShow(String GroupUin,String PicPath,int type)
    {
        try {
            if(mListMode_White)
            {
                if(!MyList.contains(GroupUin))return;
            }
            else
            {
                if(MyList.contains(GroupUin))return;
            }
            QQMessage.Message_Send_Effect_Pic(GroupUin,PicPath,type);
        } catch (Exception e) {
            Utils.ShowToast(""+e);
        }
    }
    public void Plugin_SetCard(String GroupUin,String UserUin,String Card)
    {
        try{
            if(mListMode_White)
            {
                if(!MyList.contains(GroupUin))return;
            }
            else
            {
                if(MyList.contains(GroupUin))return;
            }
            TroopManager.Group_ChangeName(GroupUin,UserUin,Card);
        }
        catch (Exception ex)
        {
            Utils.ShowToast(""+ex);
        }
    }
    public void Plugin_SetTitle(String GroupUin,String UserUin,String Card)
    {
        try{
            if(mListMode_White) {
                if(!MyList.contains(GroupUin))return;
            }
            else {
                if(MyList.contains(GroupUin))return;
            }
            TroopManager.Group_Change_Title(GroupUin,UserUin,Card);
        }
        catch (Exception ex)
        {
            Utils.ShowToast(""+ex);
        }
    }
    public static Activity GetActiveActivity()
    {
        return Utils.GetThreadActivity();
    }
    public static ArrayList Plugin_GetGroupList()
    {
        return JavaPluginUtils.GetGroupInfo();
    }
    public ArrayList Plugin_GetGroupList_Strict()
    {
        ArrayList<JavaPluginUtils.GroupInfo> info = JavaPluginUtils.GetGroupInfo();
        if(mListMode_White)
        {
            Iterator<JavaPluginUtils.GroupInfo> it = info.iterator();
            while (it.hasNext())
            {
                JavaPluginUtils.GroupInfo i = it.next();
                if(!MyList.contains(i.GroupUin))it.remove();
            }
        }else
        {
            Iterator<JavaPluginUtils.GroupInfo> it = info.iterator();
            while (it.hasNext())
            {
                JavaPluginUtils.GroupInfo i = it.next();
                if(MyList.contains(i.GroupUin))it.remove();
            }
        }
        return info;
    }

    public static ArrayList Plugin_GetGroupMemberList(String GroupUin)
    {
        return JavaPluginUtils.GetGroupMemberList(GroupUin);
    }
    public static ArrayList Plugin_GetGroupForbiddenList(String GroupUin)
    {
        return JavaPluginUtils.GetGroupForbiddenList(GroupUin);
    }
    public void Group_Forbidden(String GroupUin,String UserUin,int time)
    {
        if(mListMode_White)
        {
            if(!MyList.contains(GroupUin))return;
        }
        else
        {
            if(MyList.contains(GroupUin))return;
        }
        if(GroupUin.contains(":")){
            String[] CutString = GroupUin.split(":");
            if (TextUtils.isEmpty(UserUin)){
                QQGuild_Manager.Guild_ForbiddenAll(CutString[0],time);
            }else{
                QQGuild_Manager.Guild_Forbidden(CutString[0],UserUin,time);
            }

            return;
        }else if (GroupUin.length() > 10){
            if (TextUtils.isEmpty(UserUin)){
                QQGuild_Manager.Guild_ForbiddenAll(GroupUin,time);
            }else{
                QQGuild_Manager.Guild_Forbidden(GroupUin,UserUin,time);
            }
            return;
        }
        if(TextUtils.isEmpty(UserUin)) {
            try {
                TroopManager.Group_Forbidden_All(GroupUin,time ==0? false : true);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        else
        {
            try {
                TroopManager.Group_Forbidden(GroupUin,UserUin,time);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    public void Plugin_KickMember(String GroupUin,String UserUin,boolean flag)
    {
        if(mListMode_White)
        {
            if(!MyList.contains(GroupUin))return;
        }
        else
        {
            if(MyList.contains(GroupUin))return;
        }
        if(GroupUin.contains(":")){
            String[] CutString = GroupUin.split(":");
            QQGuild_Manager.Guild_Kick_User(CutString[0],UserUin,flag);
            return;
        }else if (GroupUin.length() > 12){
            QQGuild_Manager.Guild_Kick_User(GroupUin,UserUin,flag);
            return;
        }
        TroopManager.Group_Kick(GroupUin,UserUin,flag);
    }
    public void Plugin_RevokeMsg(Object MessageRecord)
    {
        try {
            if(MessageRecord instanceof JavaPlugin.MessageData)
            {
                JavaPlugin.MessageData mData = (JavaPlugin.MessageData) MessageRecord;
                if(mListMode_White)
                {
                    if(!MyList.contains(mData.GroupUin))return;
                }
                else
                {
                    if(MyList.contains(mData.GroupUin))return;
                }
                MHookEnvironment.mTask.PostTask(()-> {
                    try {
                        BaseCall.RemoveMessage(mData.msg);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },2000);
            }
            else
            {
                MHookEnvironment.mTask.PostTask(()-> {
                    try {
                        BaseCall.RemoveMessage(MessageRecord);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                },2000);
            }

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void Plugin_SendTip(Object MsgObj,String str)
    {
        if(MsgObj instanceof JavaPlugin.MessageData)
        {
            try{
                JavaPlugin.MessageData mData = (JavaPlugin.MessageData) MsgObj;
                if(mListMode_White) {
                    if(!MyList.contains(mData.GroupUin))return;
                }
                else {
                    if(MyList.contains(mData.GroupUin))return;
                }
                Object Chatmsg = mData.msg;
                String GroupUin = mData.GroupUin;
                String sender = mData.UserUin;
                long shmsgseq = MField.GetField(Chatmsg,"shmsgseq",long.class);
                long msgUid = MField.GetField(Chatmsg,"msgUid",long.class);
                long MsgTime = MField.GetField(Chatmsg,"time",long.class);
                if(mData.IsGroup)
                {
                    QQMessage.AddRevokeTip(str, MsgTime + 1, shmsgseq, msgUid + new Random().nextInt(), GroupUin, "");
                }
                else
                {
                    QQMessage.AddRevokeTip(str, MsgTime + 1, shmsgseq, msgUid + new Random().nextInt(), GroupUin, sender);
                }

            }catch (Throwable th)
            {

            }

        }
    }
    public void Plugin_LoadCommon(String Path)
    {
        JavaPluginUtils.CommonLoadCheck(Path,PluginID);
    }
    public boolean CheckForSign()
    {
        JavaPlugin.PluginData mData = JavaPlugin.PluginIdToData(PluginID);

        if(MConfig.Get_Boolean("Plugin","SignChecker",mData.PluginSignCheck,false))
        {
            return true;
        }
        return false;
    }
    public void SaveForCheckedSign()
    {
        JavaPlugin.PluginData mData = JavaPlugin.PluginIdToData(PluginID);
        MConfig.Put_Boolean("Plugin","SignChecker",mData.PluginSignCheck,true);
    }
    public int GetCheckID()
    {
        JavaPlugin.PluginData mData = JavaPlugin.PluginIdToData(PluginID);
        return mData.PluginSignCheck.hashCode();
    }
    public String GetSkey(){
        if(Show_Dialog)return "";
        if(CheckForSign())return QQTicketUtils.GetSkey();
        if(Permission_Skey)return QQTicketUtils.GetSkey();

        Show_Dialog = true;
        JavaPlugin.PluginData mData = JavaPlugin.PluginIdToData(PluginID);
        String MDialog = "当前加载的脚本:"+mData.Name+" 正在获取你当前登录QQ的Skey,使用该Key可以进行信息获取,状态修改等操作,请确认你的脚本来源正确且没有恶意代码再允许此操作\n\n脚本ID:"+GetCheckID();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Utils.GetThreadActivity(), AlertDialog.THEME_HOLO_LIGHT).setTitle("权限确认").setMessage(MDialog);
        boolean[] Check = new boolean[1];
        new Handler(Looper.getMainLooper()).post(()-> mBuilder.setNegativeButton("拒绝", (dialog, which) -> Check[0] = true).setPositiveButton("允许", (dialog, which) -> {
            Check[0] = true;
            Permission_Skey = true;
            SaveForCheckedSign();
        }).setOnDismissListener(dialog -> Check[0] = true).show());
        while (Check[0]==false)
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Show_Dialog = false;
        if(Permission_Skey) return QQTicketUtils.GetSkey();
        return "";
    }
    public String GetPSkey(String Dom)
    {
        if(Show_Dialog)return "";
        if(CheckForSign())return  QQTicketUtils.GetPsKey(Dom);
        if(Permission_Skey)return QQTicketUtils.GetPsKey(Dom);
        Show_Dialog = true;
        JavaPlugin.PluginData mData = JavaPlugin.PluginIdToData(PluginID);
        String MDialog = "当前加载的脚本:"+mData.Name+" 正在获取你当前登录QQ的P_Skey,使用该Key可以进行信息获取,状态修改等操作,请确认你的脚本来源正确且没有恶意代码再允许此操作\n\n脚本ID:"+GetCheckID();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Utils.GetThreadActivity(), AlertDialog.THEME_HOLO_LIGHT).setTitle("权限确认").setMessage(MDialog);
        boolean[] Check = new boolean[1];

        new Handler(Looper.getMainLooper()).post(()-> mBuilder.setNegativeButton("拒绝", (dialog, which) -> Check[0] = true).setPositiveButton("允许", (dialog, which) -> {
            Check[0] = true;
            Permission_Skey = true;
            SaveForCheckedSign();
        }).setOnDismissListener(dialog -> Check[0] = true).show());

        while (Check[0]==false)
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Show_Dialog = false;
        if(Permission_Skey) return QQTicketUtils.GetPsKey(Dom);
        return "";
    }
    public String GetSuperKey()
    {
        if(Show_Dialog)return "";
        if(CheckForSign())return QQTicketUtils.GetSuperKey();
        if(Permission_Skey)return QQTicketUtils.GetSuperKey();
        Show_Dialog = true;
        JavaPlugin.PluginData mData = JavaPlugin.PluginIdToData(PluginID);
        String MDialog = "当前加载的脚本:"+mData.Name+" 正在获取你当前登录QQ的Superkey,使用该Key可以进行信息获取,状态修改等操作,请确认你的脚本来源正确且没有恶意代码再允许此操作\n\n脚本ID:"+GetCheckID();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Utils.GetThreadActivity(), AlertDialog.THEME_HOLO_LIGHT).setTitle("权限确认").setMessage(MDialog);
        boolean[] Check = new boolean[1];
        new Handler(Looper.getMainLooper()).post(()-> mBuilder.setNegativeButton("拒绝", (dialog, which) -> Check[0] = true).setPositiveButton("允许", (dialog, which) -> {
            Check[0] = true;
            Permission_Skey = true;
            SaveForCheckedSign();
        }).setOnDismissListener(dialog -> Check[0] = true).show());
        while (Check[0]==false)
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Show_Dialog = false;
        if(Permission_Skey) return QQTicketUtils.GetSuperKey();
        return "";

    }
    public String getPT4Token(String Dom)
    {
        if(Show_Dialog)return "";
        if(CheckForSign())return QQTicketUtils.getPt4Token(Dom);
        if(Permission_Skey)return QQTicketUtils.getPt4Token(Dom);
        Show_Dialog = true;
        JavaPlugin.PluginData mData = JavaPlugin.PluginIdToData(PluginID);
        String MDialog = "当前加载的脚本:"+mData.Name+" 正在获取你当前登录QQ的PT4Token,使用该Key可以进行信息获取,状态修改等操作,请确认你的脚本来源正确且没有恶意代码再允许此操作\n\n脚本ID:"+GetCheckID();
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(Utils.GetThreadActivity(), AlertDialog.THEME_HOLO_LIGHT).setTitle("权限确认").setMessage(MDialog);
        boolean[] Check = new boolean[1];
        new Handler(Looper.getMainLooper()).post(()-> mBuilder.setNegativeButton("拒绝", (dialog, which) -> Check[0] = true).setPositiveButton("允许", (dialog, which) -> {
            Check[0] = true;
            Permission_Skey = true;
            SaveForCheckedSign();
        }).setOnDismissListener(dialog -> Check[0] = true).show());
        while (Check[0]==false)
        {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        Show_Dialog = false;
        if(Permission_Skey) return QQTicketUtils.getPt4Token(Dom);
        return "";
    }
    public void PutStringConfig(String ConfigName,String Item,String Value)
    {

        if(PluginFlag==null)
        {
            JavaPlugin.PluginData mData = JavaPlugin.PluginIdToData(PluginID);
            PluginFlag = DataUtils.bytesToHex((""+mData.Name).getBytes(StandardCharsets.UTF_8));
        }
        JavaPluginConfigs.PutConfig(PluginFlag,ConfigName,Item,Value);
    }
    public String GetStringConfig(String ConfigName,String Item)
    {
        if(PluginFlag==null)
        {
            JavaPlugin.PluginData mData = JavaPlugin.PluginIdToData(PluginID);
            PluginFlag = DataUtils.bytesToHex((""+mData.Name).getBytes(StandardCharsets.UTF_8));
        }
        return JavaPluginConfigs.GetConfig(PluginFlag,ConfigName,Item);
    }
    public void SetPluginFlag(String S)
    {
        String Flag = "->"+ S + "<";
        PluginFlag = DataUtils.bytesToHex((""+S.hashCode()+"-"+Flag.hashCode()).getBytes(StandardCharsets.UTF_8));
    }

    public void SetItemCallBack(String functionName)
    {
        JavaPlugin.PluginData mData = JavaPlugin.PluginIdToData(PluginID);
        if(!mData.isShowAlerm)
        {
            mData.isShowAlerm = true;
            //Utils.ShowToast("脚本  "+mData.Name+"  添加了群聊便捷菜单,可以长按群聊标题或者群聊右上角进行查看");
        }
        mData.ItemFunctionName = functionName;
    }

    public void Plugin_Send_Reply(String GroupUin,Object SourceObj,String MessageText)
    {
        if(mListMode_White)
        {
            if(!MyList.contains(GroupUin))return;
        }
        else
        {
            if(MyList.contains(GroupUin))return;
        }
        if(SourceObj instanceof JavaPlugin.MessageData)
        {
            JavaPlugin.MessageData mData = (JavaPlugin.MessageData) SourceObj;
            QQMessage.Message_Send_Reply(GroupUin,mData.msg,MessageText);

        }

    }

    public void Plugin_IncludeFile(String File)
    {

    }
    public void Plugin_SendFakeMsg(String GroupUin,String UserUin,String MessageContent)
    {
        try{
            if(mListMode_White)
            {
                if(!MyList.contains(GroupUin))return;
            }
            else
            {
                if(MyList.contains(GroupUin))return;
            }
            mMessageInfoList[] mWillSendInfo = JavaPluginUtils.InitMessageData(MessageContent);
            ArrayList RecordList = new ArrayList();
            ArrayList AtInfo = new ArrayList();
            String TextMsg = "";
            boolean HitMixmsg = false;
            int length = 0;
            for(mMessageInfoList mElement : mWillSendInfo)
            {
                if(mElement.MessageType==1)
                {
                    Object mTextMsgData = QQMessage_Builder.Build_Text(GroupUin,mElement.Message);
                    length+=mElement.Message.length();
                    TextMsg = TextMsg + mElement.Message;
                    RecordList.add(mTextMsgData);
                }
                else if(mElement.MessageType == 2)
                {
                    Object mPicMsgData = QQMessage_Builder.Build_Pic(QQMessage_Builder.Build_SessionInfo(GroupUin,UserUin), FileUtils.GetPath(mElement.Message));
                    RecordList.add(mPicMsgData);
                    HitMixmsg = true;
                }
                else if (mElement.MessageType == 3)
                {
                    String AtText = "@"+ TroopManager.GetMemberName(GroupUin,mElement.Message)+" ";
                    if(mElement.Message.equals("0"))
                    {
                        AtText = "@全体成员 ";
                    }

                    TextMsg = TextMsg + AtText;
                    Object mTextMsg = QQMessage_Builder.Build_Text(GroupUin,AtText);
                    AtInfo.add(QQMessage_Builder.Build_AtInfo(mElement.Message,AtText, (short) length));
                    length+=AtText.length();
                    RecordList.add(mTextMsg);
                }
            }
            QQMessage_Transform.Message_Add_Mix(GroupUin,UserUin,RecordList);

        }
        catch (Exception ex)
        {
            Utils.ShowToast("E1000:"+Log.getStackTraceString(ex));
        }
    }
    public void Plugin_SendFakeCardMsg(String GroupUin,String UserUin,String XmlData) throws Exception {
        if(mListMode_White)
        {
            if(!MyList.contains(GroupUin))return;
        }
        else
        {
            if(MyList.contains(GroupUin))return;
        }
        if(XmlData.startsWith("{"))
        {
            Object MessageRecord = QQMessage_Builder.Build_ArkAppMsg(XmlData);
            MField.SetField(MessageRecord,"frienduin",GroupUin);;
            if(TextUtils.isEmpty(UserUin))
            {
                MField.SetField(MessageRecord,"istroop",1);;
            }
            else
            {
                MField.SetField(MessageRecord,"frienduin",UserUin);;
            }

            BaseCall.AddMsg(MessageRecord);
        }
        else
        {
            Object MessageRecord = QQMessage_Builder.Build_AbsStructMsg(XmlData);
            MField.SetField(MessageRecord,"frienduin",GroupUin);;
            if(TextUtils.isEmpty(UserUin))
            {
                MField.SetField(MessageRecord,"istroop",1);;
            }
            else
            {
                MField.SetField(MessageRecord,"frienduin",UserUin);;
            }
            BaseCall.AddMsg(MessageRecord);
        }

    }
    public static boolean Try_sendCheck(String ChannelID,String QQUin,int Count){
        return QQApiUtils.SendCard(QQTicketUtils.GetSkey(),QQTicketUtils.GetPsKey("qun.qq.com"),QQUin,ChannelID,1);
    }
    public static String Try_Get_User_True_Uin(String ChannelID,String TinyID){
        try{
            if(TinyID.equals(BaseInfo.GetCurrentTinyID()))return BaseInfo.GetCurrentUin();
            String FaceID = QQGuild_Utils.Get_Face_Url(TinyID,ChannelID);
            //MLogCat.Print_Debug(FaceID);
            String AllMemberInfo = QQApiUtils.Try_Get_UserList(QQTicketUtils.GetSkey(),QQTicketUtils.GetPsKey("qun.qq.com"),ChannelID);
            //MLogCat.Print_Debug(AllMemberInfo);
            JSONObject NewJson = new JSONObject(AllMemberInfo);


            JSONArray NewArray = NewJson.getJSONObject("response").getJSONArray("members");
            for(int i=0;i<NewArray.length();i++){
                JSONObject Item = NewArray.getJSONObject(i);

                JSONArray UserList = Item.getJSONArray("rpt_member_list");
                for(int j=0;j<UserList.length();j++){
                    JSONObject UserItem = UserList.getJSONObject(j);
                    String FaceUrl = UserItem.getString("face_url");
                    if(QQGuild_Utils.Check_Face_Url_Same(FaceUrl,FaceID)){
                        return UserItem.getString("uin");
                    }
                }
            }

            NewArray = NewJson.getJSONObject("response").getJSONArray("rpt_msg_normal_member_list");
                for(int j=0;j<NewArray.length();j++) {
                    JSONObject UserItem = NewArray.getJSONObject(j);
                    String FaceUrl = UserItem.getString("face_url");
                    if (QQGuild_Utils.Check_Face_Url_Same(FaceUrl, FaceID)) {
                        return UserItem.getString("uin");
                    }
                }
            return "";
        }catch (Exception e){
            MLogCat.Print_Error("Get_True_Uin",e);
            return "";
        }

    }


}