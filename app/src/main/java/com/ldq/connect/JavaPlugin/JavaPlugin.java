package com.ldq.connect.JavaPlugin;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.DebugUtils;
import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.TroopManager;
import com.ldq.connect.Tools.MPositionDialog;

import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import bsh.BshMethod;
import bsh.EvalError;
import bsh.Interpreter;
import bsh.NameSpace;
import bsh.Primitive;
import bsh.classpath.DiscreteFilesClassLoader;


public class JavaPlugin
{
    public static final PluginTaskList mTask= new PluginTaskList();

    public static final int VERSION_PLUGIN = 10;

    public static class PluginData {

        public String Name = "";
        public String Path = "";
        public String Auther = "";
        public String PluginID = Math.random() + "";
        public Interpreter Instance = new Interpreter();
        public LinkedHashMap<String, String> GroupMenuList = new LinkedHashMap<>();
        public LinkedHashMap<String, String> GroupUserClickMenu = new LinkedHashMap<>();


        public boolean EncFlag = false;

        public boolean Flag_Signing = false;

        public ArrayList<String> mList;
        public boolean Use_White_Mode = false;
        public boolean Loaded = false;

        public String ItemFunctionName = "";

        boolean isShowAlerm = false;

        public String PluginSignCheck = "";

        public boolean IsAvailableUin(String TroopUin,String UserUin){
            if (TextUtils.isEmpty(TroopUin))return true;
            if(TextUtils.isEmpty(UserUin)){
                if (TroopUin.equals("239704249") || TroopUin.equals("960081813"))return false;
                if(Use_White_Mode){
                    return mList.contains(TroopUin);
                }else{
                    return !mList.contains(TroopUin);
                }
            }else{
                return true;
            }
        }


    }
    public static PluginData PluginIdToData(String PluginID) {
        for(PluginData ListData : PluginList)
        {
            if(ListData.PluginID.equals(PluginID))
            {
                return ListData;
            }
        }
        return null;
    }
    public volatile static int TaskCount = 0;
    public volatile static int AllTaskCount = 0;
    public static String NowTaskName;


    public static void ShowTaskListForJavaPlugin() {
        new AlertDialog.Builder(Utils.GetThreadActivity(),AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("队列状态")
                .setMessage("队列任务数:"+TaskCount
                        +"\n当前任务摘要:"+NowTaskName
                        +"\n队列已添加任务总数:"+AllTaskCount
                ).show();
    }
    public static PluginData PluginPathToData(String Path) {
        for(PluginData ListData : PluginList)
        {
            if(ListData.Path.equals(Path))
            {
                return ListData;
            }
        }
        return null;
    }
    public static List<PluginData> PluginList = Collections.synchronizedList(new ArrayList<>());

    public static void ShowSettings(Context mContext){
        try {
            //Dialog mDialog = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.utils.DialogUtil"),"a", MClass.loadClass("com.tencent.mobileqq.utils.QQCustomDialog"),new Class[]{Context.class,int.class},mContext,0);
            ScrollView scroll = new ScrollView(mContext);
            LinearLayout mLL = new LinearLayout(mContext);
            mLL.setBackgroundColor(Color.WHITE);

            scroll.addView(mLL);
            mLL.setOrientation(LinearLayout.VERTICAL);

            TextView t = new TextView(mContext);
            t.setTextColor(Color.BLACK);
            t.setText("脚本存放目录:"+MHookEnvironment.PublicStorageModulePath + "Plugin");
            mLL.addView(t);

            t = new TextView(mContext);
            t.setTextColor(Color.BLACK);
            t.setText("点击脚本名字可以进入更多设置");
            mLL.addView(t);

            List<PluginData> mFileList = JavaPluginUtils.GetFilePluginList();
            for(PluginData data : mFileList)
            {
                View addView = JavaSourceView.GetPluginView(mContext,
                        data.Name,
                        PluginPathToData(data.Path)==null?"加载":PluginPathToData(data.Path).Loaded ?"取消" : "正在加载..",
                        vx -> {
                            PluginData ID = (PluginData) vx.getTag();
                            if(((Button)vx).getText().toString().contains("正在加载.."))
                            {
                                Utils.ShowToastShort("你是要赶着投胎吗?");
                                return;
                            }
                            if (PluginPathToData(ID.Path)==null)
                            {
                                try{
                                    ((Button)vx).setText("正在加载..");
                                    new Thread(()->{
                                        try{
                                            CheckAndLoad(ID);
                                            Utils.ShowToastShort("已加载");
                                            data.Loaded = true;
                                            new Handler(Looper.getMainLooper()).post(()->((Button)vx).setText("取消"));
                                        }catch (Exception e)
                                        {
                                            Utils.ShowToast("加载失败:"+e);
                                        }

                                    }).start();
                                } catch (Throwable th)
                                {
                                    Utils.ShowToast("加载失败:"+th);
                                    ((Button)vx).setText("加载");
                                }
                            }else
                            {
                                try{
                                    Unload(PluginPathToData(ID.Path).PluginID);
                                    Utils.ShowToast("已取消加载");
                                    ((Button)vx).setText("加载");
                                }
                                catch (Throwable th)
                                {
                                    Utils.ShowToast("加载失败:"+th);

                                }
                            }

                        },
                        data,
                        onclick -> JavaSourceView.ShowPluginInfoView(data.Path,mContext),
                        (x)->{
                            return true;
                        }
                );
                mLL.addView(addView);
            }
            scroll.setBackgroundColor(Color.WHITE);

            LoopStart:
            for(PluginData mLoadedData : PluginList)
            {
                for(PluginData data : mFileList)
                {
                    if(data.Path.equals(mLoadedData.Path))continue LoopStart;

                }

                View addView = JavaSourceView.GetPluginView_RED(mContext,
                        mLoadedData.Name,
                        PluginPathToData(mLoadedData.Path)==null?"加载":PluginPathToData(mLoadedData.Path).Loaded ?"取消" : "正在加载..",
                        vx -> {
                            PluginData ID = (PluginData) vx.getTag();
                            if(((Button)vx).getText().toString().contains("正在加载.."))
                            {
                                Utils.ShowToastShort("你是要赶着投胎吗?");
                                return;
                            }
                            if (PluginPathToData(ID.Path)==null)
                            {
                                try{
                                    ((Button)vx).setText("正在加载..");
                                    MHookEnvironment.mTask.PostTask(()->{
                                        CheckAndLoad(ID);
                                        Utils.ShowToastShort("已加载");
                                        mLoadedData.Loaded = true;
                                        new Handler(Looper.getMainLooper()).post(()->((Button)vx).setText("取消"));
                                    });

                                }
                                catch (Throwable th)
                                {
                                    Utils.ShowToast("加载失败:"+th);
                                    ((Button)vx).setText("加载");
                                }
                            }else
                            {
                                try{
                                    Unload(PluginPathToData(ID.Path).PluginID);
                                    Utils.ShowToast("已取消加载");
                                    ((Button)vx).setText("加载");
                                }
                                catch (Throwable th)
                                {
                                    Utils.ShowToast("加载失败:"+th);

                                }
                            }

                        },
                        mLoadedData,
                        onclick -> JavaSourceView.ShowPluginInfoView(mLoadedData.Path,mContext),
                        (x)-> false
                );


                mLL.addView(addView);
            }

            MPositionDialog.CreateUpDialogMea(scroll);


        } catch (Exception e) {
            Utils.ShowToast("界面显示错误,错误详情: \n");
            MLogCat.Print_Error("GetPluginList",Log.getStackTraceString(e));
        }


    }
    public static void Unload(String PluginID) {
        for(PluginData mInter : PluginList)
        {
            if(mInter.PluginID.equalsIgnoreCase(PluginID))
            {
                PluginList.remove(mInter);
                NameSpace space = mInter.Instance.getNameSpace();
                try{
                    BshMethod m = space.getMethod("onUnload",new Class[0]);
                    if(m!=null)
                    {
                        m.invoke(new Object[0],mInter.Instance);
                        Thread.sleep(1000);
                    }

                }
                catch (Throwable ex)
                {
                    MLogCat.Print_PluginError("调用到Unload时发生错误",(ex instanceof EvalError ? ((EvalError)ex).Get_Raw_Infos() : ex.toString()));

                }
                space.clear();

                break;
            }
        }
    }
    public static void CheckAndLoad(PluginData mData){
        for(PluginData ListData : PluginList)
        {
            if(ListData.Path.equals(mData.Path))
            {
                return;
            }
        }
        File mFile = new File(mData.Path);
        if(mFile.getName().endsWith(".java"))
        {
            load(mData);
        }
    }

    public static void loadExtra(String JavaContent , Interpreter mInterpreter ,boolean IsInit,String PluginID,String Path) throws Exception {
        mInterpreter.set("context", MHookEnvironment.MAppContext);
        mInterpreter.set("MyUin", BaseInfo.GetCurrentUin()+"");
        mInterpreter.set("PluginID",PluginID);
        mInterpreter.set("AppPath",MHookEnvironment.PublicStorageModulePath+"Plugin");
        mInterpreter.set("SDKVer",VERSION_PLUGIN);
        mInterpreter.set("loader", MHookEnvironment.mLoader);
        if(IsInit)
        {
            NameSpace space = mInterpreter.getNameSpace();
            //初始化基础方法
            JavaPluginMethod mMethodControl = new JavaPluginMethod();

            PluginData pdata = PluginIdToData(PluginID);
            mMethodControl.MyList = pdata.mList;
            mMethodControl.mListMode_White = pdata.Use_White_Mode;
            pdata.PluginSignCheck = "";


            mMethodControl.PluginID = PluginID;
            space.setMethod("sendFakeMsg",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SendFakeMsg", String.class, String.class, String.class),mMethodControl));
            space.setMethod("sendFakeCard",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SendFakeCardMsg", String.class, String.class, String.class),mMethodControl));
            space.setMethod("sendMsg",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SendCommonMessage",String.class,String.class,String.class),mMethodControl));
            space.setMethod("sendMsg",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SendCommonMessageReply", String.class, String.class, String.class, Object.class),mMethodControl));

            space.setMethod("sendPic",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SendPicMsg",String.class,String.class,String.class),mMethodControl));
            space.setMethod("sendCard",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SendCardMsg",String.class,String.class,String.class),mMethodControl));
            space.setMethod("sendShake",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SendShake",String.class),mMethodControl));
            space.setMethod("sendShow",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SendShow",String.class,String.class,int.class),mMethodControl));
            space.setMethod("sendVoice",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SendVoide",String.class,String.class,String.class),mMethodControl));
            space.setMethod("sendTip",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SendTip",Object.class,String.class),mMethodControl));
            space.setMethod("sendReply",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_Send_Reply", String.class, Object.class, String.class),mMethodControl));


            space.setMethod("getGroupList",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_GetGroupList_Strict"),mMethodControl));
            space.setMethod("getGroupMemberList",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_GetGroupMemberList",String.class),null));
            space.setMethod("getForbiddenList",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_GetGroupForbiddenList",String.class),null));
            space.setMethod("setCard",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SetCard",String.class,String.class,String.class),mMethodControl));
            space.setMethod("setTitle",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SetTitle",String.class,String.class,String.class),mMethodControl));


            space.setMethod("AddItem",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_AddGroupMenu",String.class,String.class,String.class),null));
            space.setMethod("RemoveItem",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_RemoveGroupMenu", String.class, String.class),null));

            space.setMethod("AddUserItem",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_AddUserMenu", String.class, String.class),mMethodControl));
            space.setMethod("RemoveUserItem",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_RemoveUserMenu", String.class),mMethodControl));
            //space.setMethod("AddUserItem",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_AddUserMenu",String.class,String.class,String.class),null));


            space.setMethod("Toast",new BshMethod(JavaPluginMethod.class.getMethod("Toast", Object.class),null));
            //space.setMethod("load",new BshMethod(Interpreter.class.getMethod("source",String.class),mInterpreter));

            space.setMethod("revokeMsg",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_RevokeMsg",Object.class),mMethodControl));
            space.setMethod("Forbidden",new BshMethod(JavaPluginMethod.class.getMethod("Group_Forbidden",String.class,String.class,int.class),mMethodControl));
            space.setMethod("Kick",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_KickMember",String.class,String.class,boolean.class),mMethodControl));



            space.setMethod("GetActivity",new BshMethod(JavaPluginMethod.class.getMethod("GetActiveActivity"),null));



            space.setMethod("load",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_LoadCommon",String.class),mMethodControl));

            space.setMethod("getSkey",new BshMethod(JavaPluginMethod.class.getMethod("GetSkey"),mMethodControl));
            space.setMethod("getPskey",new BshMethod(JavaPluginMethod.class.getMethod("GetPSkey",String.class),mMethodControl));
            space.setMethod("getSuperkey",new BshMethod(JavaPluginMethod.class.getMethod("GetSuperKey"),mMethodControl));
            space.setMethod("getPT4Token",new BshMethod(JavaPluginMethod.class.getMethod("getPT4Token",String.class),mMethodControl));

            space.setMethod("getString",new BshMethod(JavaPluginMethod.class.getMethod("GetStringConfig", String.class, String.class),mMethodControl));
            space.setMethod("putString",new BshMethod(JavaPluginMethod.class.getMethod("PutStringConfig", String.class, String.class, String.class),mMethodControl));

            space.setMethod("setFlag",new BshMethod(JavaPluginMethod.class.getMethod("SetPluginFlag", String.class),mMethodControl));
            space.setMethod("IncludeFile",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_IncludeFile", String.class),mMethodControl));

            space.setMethod("GetChatType",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_GetChatType"),mMethodControl));
            space.setMethod("GetGroupUin",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_GetChatGroupUin"),mMethodControl));
            space.setMethod("GetFriendUin",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_GetChatFriendUin"),mMethodControl));
            space.setMethod("Pai",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SendPaiyipai", String.class, String.class),mMethodControl));

            space.setMethod("setItemCallback",new BshMethod(JavaPluginMethod.class.getMethod("SetItemCallBack", String.class),mMethodControl));



            space.setMethod("sendLike",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SendLike", String.class, int.class),null));
            space.setMethod("sendAntEmo",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_SendAntEmo", String.class, String.class, int.class, int.class),null));

            space.setMethod("HandleRequest",new BshMethod(JavaPluginMethod.class.getMethod("Plugin_Handler_TroopRequest", Object.class, boolean.class, String.class, boolean.class),null));


            space.setMethod("Decode_RealUin",new BshMethod(JavaPluginMethod.class.getMethod("Try_Get_User_True_Uin", String.class, String.class),null));
            space.setMethod("Channel_Send_Check",new BshMethod(JavaPluginMethod.class.getMethod("Try_sendCheck", String.class, String.class, int.class),null));

        }
        PluginData pdata = PluginIdToData(PluginID);
        String SignText = pdata.PluginSignCheck;
        String GenerSign = DataUtils.getDataMD5(JavaContent.getBytes());
        SignText = SignText + GenerSign;
        pdata.PluginSignCheck = DataUtils.getDataMD5(SignText.getBytes());

        mInterpreter.eval(JavaContent,Path);




    }
    public static void load(PluginData mData) {
        if(mData == null)return;
        for(PluginData ListData : PluginList)
        {
            if(ListData.Path.equals(mData.Path))
            {
                return;
            }
        }
        Interpreter mInterpreter = new Interpreter();
        try {
            //初始化基础成员
            mInterpreter.set("PluginID",mData.PluginID);
            mInterpreter.setClassLoader(MHookEnvironment.mLoader);
            //mInterpreter.setClassLoader(ClassLoader.getSystemClassLoader());

            mData.Instance = mInterpreter;
            mData.Use_White_Mode = MConfig.Get_Boolean("Plugin","WhileMode",new File(mData.Path).getName(),false);
            mData.mList = (ArrayList<String>) MConfig.Get_List("Plugin","TroopBWList",new File(mData.Path).getName());
            PluginList.add(mData);
            loadExtra(FileUtils.ReadFileString(mData.Path),mData.Instance,true,mData.PluginID,mData.Path);
        } catch (Exception evalError) {
            Utils.ShowToast("脚本加载时发生错误\n错误信息:\n"+(evalError instanceof EvalError ? ((EvalError)evalError).Get_Raw_Infos() : evalError.toString()));
            MLogCat.Print_PluginError("脚本加载时发生错误错误","相关文件->"+mData.Path+"\n错误信息:\n"+ (evalError instanceof EvalError ? ((EvalError)evalError).Get_Raw_Infos() : evalError.toString()));
        }
    }
    public static class MessageData {
        public String MessageContent;
        public String GroupUin;
        public String UserUin;
        public int MessageType;
        public boolean IsGroup;
        public boolean IsChannel;
        public String SenderNickName;
        public Object SessionInfo;
        public Object AppInterface;
        public long MessageTime;
        public String[] AtList;
        public ArrayList mAtList;
        public Object msg;
        public String[] PicList;
        public String AdminUin;

        public boolean IsSend;

        public String FileName;
        public String FileUrl;
        public long FileSize;
        public String LocalPath;
        public String md5;

        public String ReplyTo;

        public String ChannelID;
        public String GuildID;


    }
    public static MessageData DecodeMessage(Object ChatMsg) {
        try{
            MessageData data = new MessageData();
            String ClzName = ChatMsg.getClass().getSimpleName();
            String cParam = "";
            boolean Hit =false;
            if(ClzName.equalsIgnoreCase("MessageForText")
                    || ClzName.equalsIgnoreCase("MessageForLongTextMsg")
                    || ClzName.equalsIgnoreCase("MessageForFoldMsg"))
            {
                cParam = MField.GetField(ChatMsg,"msg",String.class);
                Hit=true;
            }
            if(ClzName.equalsIgnoreCase("MessageForPic"))
            {
                String PicMd5 = MField.GetField(ChatMsg,"md5",String.class);
                String PicPath = MField.GetField(ChatMsg,"bigMsgUrl",String.class);
                if(TextUtils.isEmpty(PicPath))
                {
                    PicPath  = "http://gchat.qpic.cn/gchatpic_new/0/0-0-"+PicMd5+"/0?term=2";
                }
                cParam = "[PicUrl="+PicPath+"]";
                Hit=true;
            }
            if(Hit) {
                ArrayList mAtList = new ArrayList();
                String mStr = MField.GetField(ChatMsg, ChatMsg.getClass(), "extStr", String.class);
                try{
                    JSONObject mJson = new JSONObject(mStr);
                    mStr = mJson.optString("troop_at_info_list");
                    ArrayList AtList3 = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"), "getTroopMemberInfoFromExtrJson", ArrayList.class, new Class[]{String.class}, mStr);
                    if (AtList3 != null) {
                        for (Object mAtInfo : AtList3) {
                            mAtList.add("" + (long) MField.GetField(mAtInfo, "uin", long.class));
                        }
                    }
                }catch (Exception e)
                {

                }

                data.MessageContent = cParam;
                data.AppInterface = MHookEnvironment.AppInterface;
                data.IsGroup = ((int) MField.GetField(ChatMsg, "istroop", int.class)) == 1;
                if (((int) MField.GetField(ChatMsg, "istroop", int.class)) == 0) {
                    data.GroupUin = "";
                    data.UserUin = MField.GetField(ChatMsg, "senderuin", String.class);
                } else if (((int) MField.GetField(ChatMsg, "istroop", int.class)) == 1) {
                    data.GroupUin = MField.GetField(ChatMsg, "frienduin", String.class);
                    data.UserUin = MField.GetField(ChatMsg, "senderuin", String.class);
                } else {
                    boolean IsOnline = MMethod.CallMethod(ChatMsg, "isSendFromLocal", boolean.class, new Class[0]);
                    if (IsOnline) {
                        data.GroupUin = MField.GetField(ChatMsg, "senderuin", String.class);
                        data.UserUin = BaseInfo.GetCurrentUin();
                    } else {
                        data.GroupUin = MField.GetField(ChatMsg, "senderuin", String.class);
                        data.UserUin = MField.GetField(ChatMsg, "frienduin", String.class);
                    }
                }
                data.MessageType = 1;
                data.MessageTime = MField.GetField(ChatMsg, "time", long.class);
                
                data.IsSend = MMethod.CallMethod(ChatMsg, "isSendFromLocal", boolean.class, new Class[0]);


                data.mAtList = mAtList;
                data.AtList = (String[]) mAtList.toArray(new String[0]);
                data.msg = ChatMsg;
                data.SenderNickName = TroopManager.GetMemberName(data.GroupUin, data.UserUin);
                return data;
            }

            if(ClzName.equalsIgnoreCase("MessageForMixedMsg")) {
                List mEleList = MField.GetField(ChatMsg, "msgElemList", List.class);
                String mConnectMsg = "";
                ArrayList OnChangePic = new ArrayList();
                for (Object MessageRecord : mEleList) {
                    if (MessageRecord.getClass().getSimpleName().equalsIgnoreCase("MessageForText") || MessageRecord.getClass().getSimpleName().equalsIgnoreCase("MessageForLongTextMsg")) {
                        String str = MField.GetField(MessageRecord, "msg", String.class);
                        if(!TextUtils.isEmpty(str))mConnectMsg = mConnectMsg +str;


                    } else if (MessageRecord.getClass().getSimpleName().equalsIgnoreCase("MessageForPic")) {

                        String PicMd5 = MField.GetField(MessageRecord, "md5", String.class);

                        String PicPath = MField.GetField(MessageRecord, "bigMsgUrl", String.class);

                        if (TextUtils.isEmpty(PicPath)) {
                            PicPath = "http://gchat.qpic.cn/gchatpic_new/0/0-0-" + PicMd5 + "/0?term=2";
                        }
                        OnChangePic.add(PicPath);
                        mConnectMsg = mConnectMsg + "[PicUrl=" + PicPath + "]";
                    }
                }
                ArrayList mAtList = new ArrayList();
                String mStr = MField.GetField(ChatMsg, ChatMsg.getClass(), "extStr", String.class);

                try{
                    JSONObject mJson = new JSONObject(mStr);
                    mStr = mJson.optString("troop_at_info_list");
                    ArrayList AtList3 = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"), "getTroopMemberInfoFromExtrJson", ArrayList.class, new Class[]{String.class}, mStr);


                    if (AtList3 != null) {
                        for (Object mAtInfo : AtList3) {
                            mAtList.add("" + (long) MField.GetField(mAtInfo, "uin", long.class));
                        }
                    }
                }catch (Exception ex)
                {

                }


                data.MessageContent = mConnectMsg;
                data.AppInterface = MHookEnvironment.AppInterface;
                data.IsGroup = ((int) MField.GetField(ChatMsg, "istroop", int.class)) == 1;
                if (((int) MField.GetField(ChatMsg, "istroop", int.class)) == 0) {
                    data.GroupUin = "";
                    data.UserUin = MField.GetField(ChatMsg, "senderuin", String.class);
                } else if (((int) MField.GetField(ChatMsg, "istroop", int.class)) == 1) {
                    data.GroupUin = MField.GetField(ChatMsg, "frienduin", String.class);
                    data.UserUin = MField.GetField(ChatMsg, "senderuin", String.class);
                } else {
                    boolean IsOnline = MMethod.CallMethod(ChatMsg, "isSendFromLocal", boolean.class, new Class[0]);
                    if (IsOnline) {
                        data.GroupUin = MField.GetField(ChatMsg, "senderuin", String.class);
                        data.UserUin = BaseInfo.GetCurrentUin();
                    } else {
                        data.GroupUin = MField.GetField(ChatMsg, "senderuin", String.class);
                        data.UserUin = MField.GetField(ChatMsg, "frienduin", String.class);
                    }
                }
                data.MessageType = 3;
                data.MessageTime = MField.GetField(ChatMsg, "time", long.class);
                data.mAtList = mAtList;
                data.AtList = (String[]) mAtList.toArray(new String[0]);
                data.SenderNickName = TroopManager.GetMemberName(data.GroupUin, data.UserUin);
                data.msg = ChatMsg;
                data.PicList = (String[]) OnChangePic.toArray(new String[0]);
                data.IsSend = MMethod.CallMethod(ChatMsg, "isSendFromLocal", boolean.class, new Class[0]);
                return data;
            }

            if(ClzName.equalsIgnoreCase("MessageForStructing") || ClzName.equalsIgnoreCase("MessageForArkApp")) {

                if (ClzName.equalsIgnoreCase("MessageForStructing")) {
                    Object Structing = MField.GetField(ChatMsg, ChatMsg.getClass(), "structingMsg", MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"));
                    String xml = MMethod.CallMethod(Structing, MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"), "getXml", String.class, new Class[0], new Object[0]);
                    data.MessageContent = xml;
                }
                if (ClzName.equalsIgnoreCase("MessageForArkApp")) {
                    Object ArkAppMsg = MField.GetField(ChatMsg, ChatMsg.getClass(), "ark_app_message", MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"));
                    String json = MMethod.CallMethod(ArkAppMsg, MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"), "toAppXml", String.class, new Class[0], new Object[0]);
                    data.MessageContent = json;
                }


                data.AppInterface = MHookEnvironment.AppInterface;
                data.IsGroup = ((int) MField.GetField(ChatMsg, "istroop", int.class)) == 1;
                if (((int) MField.GetField(ChatMsg, "istroop", int.class)) == 0) {
                    data.GroupUin = "";
                    data.UserUin = MField.GetField(ChatMsg, "senderuin", String.class);
                } else if (((int) MField.GetField(ChatMsg, "istroop", int.class)) == 1) {
                    data.GroupUin = MField.GetField(ChatMsg, "frienduin", String.class);
                    data.UserUin = MField.GetField(ChatMsg, "senderuin", String.class);
                } else {
                    boolean IsOnline = MMethod.CallMethod(ChatMsg, "isSendFromLocal", boolean.class, new Class[0]);
                    if (IsOnline) {
                        data.GroupUin = MField.GetField(ChatMsg, "senderuin", String.class);
                        data.UserUin = BaseInfo.GetCurrentUin();
                    } else {
                        data.GroupUin = MField.GetField(ChatMsg, "senderuin", String.class);
                        data.UserUin = MField.GetField(ChatMsg, "frienduin", String.class);
                    }
                }

                data.MessageType = 2;
                data.MessageTime = MField.GetField(ChatMsg, "time", long.class);

                data.SenderNickName = TroopManager.GetMemberName(data.GroupUin, data.UserUin);
                data.msg = ChatMsg;
                data.IsSend = MMethod.CallMethod(ChatMsg, "isSendFromLocal", boolean.class, new Class[0]);
                return data;
            }
            if(ClzName.equalsIgnoreCase("MessageForPtt"))
            {

                data.MessageContent = "[语音]MD5="+ MField.GetField(ChatMsg,"md5",String.class);
                data.FileUrl = "https://grouptalk.c2c.qq.com"+
                        MField.GetField(ChatMsg,"directUrl",String.class);
                data.LocalPath = MField.GetField(ChatMsg,"fullLocalPath",String.class);
                data.msg = ChatMsg;

                data.AppInterface = MHookEnvironment.AppInterface;
                data.IsGroup = ((int) MField.GetField(ChatMsg, "istroop", int.class)) == 1;
                if (((int) MField.GetField(ChatMsg, "istroop", int.class)) == 0) {
                    data.GroupUin = "";
                    data.UserUin = MField.GetField(ChatMsg, "senderuin", String.class);
                } else if (((int) MField.GetField(ChatMsg, "istroop", int.class)) == 1) {
                    data.GroupUin = MField.GetField(ChatMsg, "frienduin", String.class);
                    data.UserUin = MField.GetField(ChatMsg, "senderuin", String.class);
                } else {
                    boolean IsOnline = MMethod.CallMethod(ChatMsg, "isSendFromLocal", boolean.class, new Class[0]);
                    if (IsOnline) {
                        data.GroupUin = MField.GetField(ChatMsg, "senderuin", String.class);
                        data.UserUin = BaseInfo.GetCurrentUin();
                    } else {
                        data.GroupUin = MField.GetField(ChatMsg, "senderuin", String.class);
                        data.UserUin = MField.GetField(ChatMsg, "frienduin", String.class);
                    }
                }
                data.SenderNickName = TroopManager.GetMemberName(data.GroupUin, data.UserUin);
                data.MessageTime = MField.GetField(ChatMsg, "time", long.class);
                data.IsSend = MMethod.CallMethod(ChatMsg, "isSendFromLocal", boolean.class, new Class[0]);
               data.MessageType = 4;

               if(data.IsSend)return null;
                return data;
            }
            if(ClzName.equalsIgnoreCase("MessageForTroopFile"))
            {
                data.MessageContent = "[文件]"+ MField.GetField(ChatMsg,"fileName",String.class);


                data.FileUrl = MField.GetField(ChatMsg,"url",String.class);
                data.FileName = MField.GetField(ChatMsg,"fileName",String.class);
                data.FileSize = MField.GetField(ChatMsg,"fileSize",long.class);

                data.msg = ChatMsg;

                data.AppInterface = MHookEnvironment.AppInterface;
                data.IsGroup = ((int) MField.GetField(ChatMsg, "istroop", int.class)) == 1;
                if (((int) MField.GetField(ChatMsg, "istroop", int.class)) == 0) {
                    data.GroupUin = "";
                    data.UserUin = MField.GetField(ChatMsg, "senderuin", String.class);
                } else if (((int) MField.GetField(ChatMsg, "istroop", int.class)) == 1) {
                    data.GroupUin = MField.GetField(ChatMsg, "frienduin", String.class);
                    data.UserUin = MField.GetField(ChatMsg, "senderuin", String.class);
                } else {
                    boolean IsOnline = MMethod.CallMethod(ChatMsg, "isSendFromLocal", boolean.class, new Class[0]);
                    if (IsOnline) {
                        data.GroupUin = MField.GetField(ChatMsg, "senderuin", String.class);
                        data.UserUin = BaseInfo.GetCurrentUin();
                    } else {
                        data.GroupUin = MField.GetField(ChatMsg, "senderuin", String.class);
                        data.UserUin = MField.GetField(ChatMsg, "frienduin", String.class);
                    }
                }
                data.MessageType = 5;

                data.SenderNickName = TroopManager.GetMemberName(data.GroupUin, data.UserUin);

                data.MessageTime = MField.GetField(ChatMsg, "time", long.class);
                data.IsSend = MMethod.CallMethod(ChatMsg, "isSendFromLocal", boolean.class, new Class[0]);
                return data;
            }
            if(ClzName.equalsIgnoreCase("MessageForReplyText"))
            {
                Object SourceInfo = MField.GetField(ChatMsg,"mSourceMsgInfo");
                if(SourceInfo==null)return null;
                long SourceUin = MField.GetField(SourceInfo,"mSourceMsgSenderUin");
                data.MessageContent =  MField.GetField(ChatMsg,"msg",String.class);

                data.msg = ChatMsg;

                data.AppInterface = MHookEnvironment.AppInterface;
                data.IsGroup = ((int) MField.GetField(ChatMsg, "istroop", int.class)) == 1;
                if (((int) MField.GetField(ChatMsg, "istroop", int.class)) == 0) {
                    data.GroupUin = "";
                    data.UserUin = MField.GetField(ChatMsg, "senderuin", String.class);
                } else if (((int) MField.GetField(ChatMsg, "istroop", int.class)) == 1) {
                    data.GroupUin = MField.GetField(ChatMsg, "frienduin", String.class);
                    data.UserUin = MField.GetField(ChatMsg, "senderuin", String.class);
                } else {
                    boolean IsOnline = MMethod.CallMethod(ChatMsg, "isSendFromLocal", boolean.class, new Class[0]);
                    if (IsOnline) {
                        data.GroupUin = MField.GetField(ChatMsg, "senderuin", String.class);
                        data.UserUin = BaseInfo.GetCurrentUin();
                    } else {
                        data.GroupUin = MField.GetField(ChatMsg, "senderuin", String.class);
                        data.UserUin = MField.GetField(ChatMsg, "frienduin", String.class);
                    }
                }
                data.MessageType = 6;

                data.SenderNickName = TroopManager.GetMemberName(data.GroupUin, data.UserUin);

                data.MessageTime = MField.GetField(ChatMsg, "time", long.class);
                data.IsSend = MMethod.CallMethod(ChatMsg, "isSendFromLocal", boolean.class, new Class[0]);
                data.ReplyTo = ""+SourceUin;
                return data;
            }


            return null;
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("PluginMessageBuilder",th);
            return null;
        }
    }
    public static void BshOnRevokeMsg(Object ChatMsg,String AdminUin) {
        for(PluginData mInterpreter : PluginList) {
            try{
                if(!mInterpreter.Loaded)continue;
                mInterpreter.Instance.set("context", MHookEnvironment.MAppContext);
                mInterpreter.Instance.set("MyUin", BaseInfo.GetCurrentUin()+"");
                mInterpreter.Instance.set("PluginID",mInterpreter.PluginID);
                mInterpreter.Instance.set("SDKVer",VERSION_PLUGIN);


                NameSpace space = mInterpreter.Instance.getNameSpace();
                MessageData mData = DecodeMessage(ChatMsg);
                if(mData==null)return;

                if(mData.IsGroup)
                {
                    if (!mInterpreter.IsAvailableUin(mData.GroupUin,""))continue;
                }

                String senderuin = MField.GetField(ChatMsg,"senderuin",String.class);
                if(!AdminUin.equals(senderuin))
                {
                    mData.AdminUin = AdminUin;
                }
                else
                {
                    mData.AdminUin = "";
                }
                if(mData!=null)
                {
                    BshMethod mMethod = space.getMethod("onRevokeMsg",new Class[]{Object.class});
                    if(mMethod!=null)
                    {
                        mMethod.invoke(new Object[]{mData},mInterpreter.Instance);
                    }
                }


            }
            catch (Throwable th)
            {
                MLogCat.Print_PluginError("调用到脚本onRevoke发生错误",th instanceof EvalError ? ((EvalError)th).Get_Raw_Infos() : th.toString());
            }



        }
    }
    public static void BshOnMsg(Object ChatMsg) {
        try
        {
            //循环调用所有的BSH实例
            for(PluginData mInterpreter : PluginList)
            {
                if(!mInterpreter.Loaded)continue;
                //全局消息处理方法
                mInterpreter.Instance.set("context", MHookEnvironment.MAppContext);
                mInterpreter.Instance.set("MyUin", BaseInfo.GetCurrentUin()+"");
                mInterpreter.Instance.set("PluginID",mInterpreter.PluginID);
                mInterpreter.Instance.set("SDKVer",VERSION_PLUGIN);






                NameSpace space = mInterpreter.Instance.getNameSpace();
                try{
                    BshMethod mMethod = space.getMethod("Callback_OnRawMsg",new Class[]{Object.class});
                    if(mMethod!=null)
                    {
                        JavaPlugin.NowTaskName = "调用:"+mInterpreter.Name+"的OnRawMsg消息回调";
                        mMethod.invoke(new Object[]{ChatMsg},mInterpreter.Instance);
                    }
                }
                catch (Exception th)
                {
                    MLogCat.Print_PluginError("调用到脚本onRawmsg发生错误",th instanceof EvalError ? ((EvalError)th).Get_Raw_Infos() : th.toString());
                }

                MessageData mData = DecodeMessage(ChatMsg);

                if(mData==null)continue;
                if(mData.IsGroup)
                {
                    if(!mInterpreter.IsAvailableUin(mData.GroupUin,""))continue;
                }


                //普通消息处理
                try{
                    if(mData.MessageType == 1)
                    {
                        BshMethod mMethod = space.getMethod("onMsg",new Class[]{Object.class});
                        if(mMethod!=null)
                        {
                            JavaPlugin.NowTaskName = "调用:"+mInterpreter.Name+"的OnMsg消息回调";
                            mMethod.invoke(new Object[]{mData},mInterpreter.Instance);
                        }
                    }
                }
                catch (Throwable th)
                {
                    MLogCat.Print_PluginError("调用到脚本onMsg发生错误",th instanceof EvalError ? ((EvalError)th).Get_Raw_Infos() : th.toString());
                }

                //混合消息
                try{
                    if(mData.MessageType == 3)
                    {
                        BshMethod mMethod = space.getMethod("onMsg",new Class[]{Object.class});
                        if(mMethod!=null)
                        {
                            JavaPlugin.NowTaskName = "调用:"+mInterpreter.Name+"的OnMsg消息回调";
                            mMethod.invoke(new Object[]{mData},mInterpreter.Instance);
                        }
                        mMethod = space.getMethod("OnChangedMixMsg",new Class[]{Object.class});
                        if(mMethod!=null)
                        {
                            JavaPlugin.NowTaskName = "调用:"+mInterpreter.Name+"的OnChangedMixMsg消息回调";
                            mMethod.invoke(new Object[]{mData},mInterpreter.Instance);
                        }
                    }
                }
                catch (Throwable th)
                {
                    MLogCat.Print_PluginError("调用到脚本onMsg发生错误",th instanceof EvalError ? ((EvalError)th).Get_Raw_Infos() : th.toString());
                }


                try{
                    if(mData.MessageType == 2 || mData.MessageType == 4 ||mData.MessageType == 5 ||mData.MessageType == 6 )
                    {
                        JavaPlugin.NowTaskName = "调用:"+mInterpreter.Name+"的OnMsg消息回调";
                        BshMethod mMethod = space.getMethod("onMsg",new Class[]{Object.class});
                        if(mMethod!=null) mMethod.invoke(new Object[]{mData},mInterpreter.Instance);
                    }
                }
                catch (Throwable th)
                {
                    MLogCat.Print_PluginError("调用到脚本onMsg发生错误",th instanceof EvalError ? ((EvalError)th).Get_Raw_Infos() : th.toString());

                }
            }
        }
        catch (Throwable th)
        {
            Utils.ShowToast(th.toString());
        }
        finally {
            JavaPlugin.NowTaskName = "";
            JavaPlugin.TaskCount--;
        }
    }
    public static void BshOnExitTroop(String GroupUin,String UserUin,String OPUin){
        try{
            for(PluginData mInterpreter : PluginList)
            {
                if(!mInterpreter.Loaded)continue;
                if (!mInterpreter.IsAvailableUin(GroupUin,""))continue;;
                if (!TroopManager.IsInTroop(GroupUin,UserUin))return;

                int Type = 0;
                if (OPUin.equals("0")){
                    Type = 1;
                }else if (TroopManager.IsInTroop(GroupUin, OPUin)){
                    Type = 2;
                }else {
                    Type = 1;
                }



                NameSpace space = mInterpreter.Instance.getNameSpace();
                try{
                    BshMethod mMethod = space.getMethod("onMemberExit",new Class[]{
                            String.class,String.class,int.class,String.class
                    });
                    if(mMethod !=null)
                    {
                        mMethod.invoke(new Object[]{GroupUin,UserUin,Type, OPUin},mInterpreter.Instance);
                    }
                }
                catch (Throwable th)
                {
                    Utils.ShowToast("调用到"+mInterpreter.Name+"时发生错误");
                    MLogCat.Print_PluginError("调用到脚本onMemberExit发生错误",th);
                }
            }
        }
        catch (Throwable th)
        {
            Utils.ShowToast("Invoke to java ERROR\n"+th);
            MLogCat.Print_Error("JavaPluginInvoker",th);
        }
    }
    public static void BshOnTroopEvent(String GroupUin,String UserUin,int EventType) {
        try{
            for(PluginData mInterpreter : PluginList)
            {
                if(!mInterpreter.Loaded)continue;
                if (!mInterpreter.IsAvailableUin(GroupUin,""))continue;;


                NameSpace space = mInterpreter.Instance.getNameSpace();
                try{
                    BshMethod mMethod = space.getMethod("OnTroopEvent",new Class[]{
                            String.class,String.class,int.class
                    });
                    if(mMethod !=null)
                    {
                        mMethod.invoke(new Object[]{GroupUin,UserUin,EventType},mInterpreter.Instance);
                    }
                }
                catch (Throwable th)
                {
                    Utils.ShowToast("调用到"+mInterpreter.Name+"时发生错误");
                    MLogCat.Print_PluginError("调用到脚本onTroopEvent发生错误",th);
                }
            }
        }
        catch (Throwable th)
        {
            Utils.ShowToast("Invoke to java ERROR\n"+th);
            MLogCat.Print_Error("JavaPluginInvoker",th);
        }
    }
    public static void BshOnTroopEvent2(String GroupUin,String UserUin,String Admin,long Time) {
        try{
            for(PluginData mInterpreter : PluginList)
            {
                if(!mInterpreter.Loaded)continue;

                if (!mInterpreter.IsAvailableUin(GroupUin,""))continue;;

                mInterpreter.Instance.set("context", MHookEnvironment.MAppContext);
                mInterpreter.Instance.set("MyUin", BaseInfo.GetCurrentUin()+"");
                mInterpreter.Instance.set("PluginID",mInterpreter.PluginID);
                mInterpreter.Instance.set("SDKVer",VERSION_PLUGIN);
                NameSpace space = mInterpreter.Instance.getNameSpace();
                try{
                    BshMethod mMethod = space.getMethod("OnTroopEvent",new Class[]{
                            String.class,String.class,String.class
                    });
                    if(mMethod !=null)
                    {
                        mMethod.invoke(new Object[]{GroupUin,UserUin,Admin},mInterpreter.Instance);
                    }

                    mMethod = space.getMethod("OnTroopEvent",new Class[]{
                            String.class,String.class,String.class,long.class
                    });
                    if(mMethod !=null)
                    {
                        mMethod.invoke(new Object[]{GroupUin,UserUin,Admin,Time},mInterpreter.Instance);
                    }

                }
                catch (Throwable th)
                {
                    Utils.ShowToast("调用到"+mInterpreter.Name+"时发生错误:OnTroopEvent");
                    MLogCat.Print_PluginError("调用到脚本onTroopEvent发生错误",th instanceof EvalError ? ((EvalError)th).Get_Raw_Infos() : th.toString());
                }
            }
        }
        catch (Throwable th)
        {
            Utils.ShowToast("Invoke to java ERROR\n"+th);
            MLogCat.Print_Error("JavaPluginInvoker",th);
        }
    }
    public static void InitDexDir() {
        File mDexDir = new File(MHookEnvironment.MAppContext.getFilesDir()+"/LD_DexPath");
        if(mDexDir.exists()) FileUtils.deleteFile(mDexDir);
        DiscreteFilesClassLoader.dexDir = mDexDir;
    }
    public static String CallForGetMsg(String Content,String GroupUin,int Type) {
            for(PluginData mInterpreter : PluginList)
            {
                if(!mInterpreter.Loaded)continue;



                if (!mInterpreter.IsAvailableUin(GroupUin,""))continue;;


                try {
                    mInterpreter.Instance.set("context", MHookEnvironment.MAppContext);
                    mInterpreter.Instance.set("MyUin", BaseInfo.GetCurrentUin()+"");
                    mInterpreter.Instance.set("PluginID",mInterpreter.PluginID);
                    mInterpreter.Instance.set("SDKVer",VERSION_PLUGIN);
                } catch (EvalError evalError) {
                    evalError.printStackTrace();
                }

                NameSpace space = mInterpreter.Instance.getNameSpace();
                try{
                    BshMethod mMethod = space.getMethod("getMsg",new Class[]{
                            String.class
                    });
                    if(mMethod !=null)
                    {
                        Object pr = mMethod.invoke(new Object[]{Content},mInterpreter.Instance);
                        if(pr instanceof Primitive) continue;
                        String returnText = (String) pr;
                        if(returnText!=null)return returnText;
                    }
                    mMethod = space.getMethod("getMsg",new Class[]{
                            String.class,String.class,int.class
                    });
                    if(mMethod !=null)
                    {
                        Object pr =  mMethod.invoke(new Object[]{Content,GroupUin,Type},mInterpreter.Instance);
                        if(pr instanceof Primitive) continue;
                        String returnText = (String) pr;
                        if(returnText!=null)return returnText;
                    }

                }
                catch (Throwable th)
                {
                    Utils.ShowToast("调用到"+mInterpreter.Name+"时发生错误:getMsg");
                    MLogCat.Print_PluginError("调用到脚本getMsg发生错误",th instanceof EvalError ? ((EvalError)th).Get_Raw_Infos() : th.toString());
                }

            }
            return null;
    }
    public static void CheckAutoLoadPlugin() {
        CheckPluginStatus();
        ArrayList<String> mList = (ArrayList<String>) MConfig.Get_List("Plugin","Plugin","AutoLoad");
        for(String mLoadList : mList)
        {
            if(mList.contains(".."))continue;

            new Thread(()->{
                PluginData mData = new PluginData();
                mData.Path = mLoadList;
                mData.Name = new File(mLoadList).getName();
                CheckAndLoad(mData);
                mData.Loaded = true;
            }).start();

        }
    }
    public static void CheckPluginStatus() {
        ArrayList<String> mList = (ArrayList<String>) MConfig.Get_List("Plugin","Plugin","AutoLoad");
        Iterator<String> it = mList.iterator();
        while (it.hasNext())
        {
            String fName = it.next();
            if(!new File(fName).exists())it.remove();
        }
        MConfig.Put_List("Plugin", "Plugin", "AutoLoad", mList);
    }
    public static void Packer_JoinTroopRequest(String TroopUin,String UserUin,String source,String Answer,String text,Object structMsg) {
        for(PluginData mInterpreter : PluginList)
        {
            if(!mInterpreter.Loaded)continue;
            if (!mInterpreter.IsAvailableUin(TroopUin,""))continue;;

            try {
                mInterpreter.Instance.set("context", MHookEnvironment.MAppContext);
                mInterpreter.Instance.set("MyUin", BaseInfo.GetCurrentUin()+"");
                mInterpreter.Instance.set("PluginID",mInterpreter.PluginID);
                mInterpreter.Instance.set("SDKVer",VERSION_PLUGIN);
            } catch (EvalError evalError) {
                evalError.printStackTrace();
            }

            NameSpace space = mInterpreter.Instance.getNameSpace();
            try{
                BshMethod method = space.getMethod("onRequestJoin",new Class[]{Object.class});
                if(method!=null)
                {
                    JavaPluginMethod.RequestInfo requestInfo = new JavaPluginMethod.RequestInfo();
                    requestInfo.Answer = Answer;
                    requestInfo.GroupUin = TroopUin;
                    requestInfo.UserUin = UserUin;
                    requestInfo.RequestSource = source;
                    requestInfo.RequestText = text;
                    requestInfo.source = structMsg;

                    method.invoke(new Object[]{requestInfo},mInterpreter.Instance);
                }
            }
            catch (Throwable th)
            {
                Utils.ShowToast("调用到"+mInterpreter.Name+"时发生错误:onRequestJoin");
                MLogCat.Print_PluginError("调用到脚本onRequestJoin发生错误",th instanceof EvalError ? ((EvalError)th).Get_Raw_Infos() : th.toString());
            }

        }

        JavaPluginMethod.RequestInfo requestInfo = new JavaPluginMethod.RequestInfo();
        requestInfo.Answer = Answer;
        requestInfo.GroupUin = TroopUin;
        requestInfo.UserUin = UserUin;
        requestInfo.RequestSource = source;
        requestInfo.RequestText = text;
        requestInfo.source = structMsg;
    }
    public static void OnGuildMessage(Object Message,boolean IsSendFromLocal){
        MessageData mData = GuildMessageDecoder(Message, IsSendFromLocal);
            try {
                //循环调用所有的BSH实例
                for(PluginData mInterpreter : PluginList)
                {




                    if(!mInterpreter.Loaded)continue;

                    NameSpace mspace = mInterpreter.Instance.getNameSpace();
                    try{
                        BshMethod mMethod = mspace.getMethod("Callback_OnRawMsg",new Class[]{Object.class});
                        if(mMethod!=null)
                        {
                            JavaPlugin.NowTaskName = "调用:"+mInterpreter.Name+"的OnRawMsg消息回调";
                            mMethod.invoke(new Object[]{Message},mInterpreter.Instance);
                        }
                    }
                    catch (Exception th)
                    {
                        MLogCat.Print_PluginError("调用到脚本onRawmsg发生错误",th instanceof EvalError ? ((EvalError)th).Get_Raw_Infos() : th.toString());
                    }
                    if (mData == null)continue;
                    //全局消息处理方法
                    mInterpreter.Instance.set("context", MHookEnvironment.MAppContext);
                    mInterpreter.Instance.set("MyUin", BaseInfo.GetCurrentUin()+"");
                    mInterpreter.Instance.set("PluginID",mInterpreter.PluginID);
                    mInterpreter.Instance.set("SDKVer",VERSION_PLUGIN);
                    mInterpreter.Instance.set("MyChannelTinyID",BaseInfo.GetCurrentTinyID());

                    if(mData.IsChannel)
                    {
                        if (!mInterpreter.IsAvailableUin(mData.GroupUin,""))continue;;
                    }

                    NameSpace space = mInterpreter.Instance.getNameSpace();

                    //普通消息处理
                    try{
                        if(mData.MessageType == 1)
                        {
                            BshMethod mMethod = space.getMethod("onMsg",new Class[]{Object.class});
                            if(mMethod!=null)
                            {
                                JavaPlugin.NowTaskName = "调用:"+mInterpreter.Name+"的OnMsg消息回调";
                                mMethod.invoke(new Object[]{mData},mInterpreter.Instance);
                            }
                        }
                    }
                    catch (Throwable th)
                    {
                        MLogCat.Print_PluginError("调用到脚本onMsg发生错误",th instanceof EvalError ? ((EvalError)th).Get_Raw_Infos() : th.toString());
                    }

                    //混合消息
                    try{
                        if(mData.MessageType == 3)
                        {
                            BshMethod mMethod = space.getMethod("onMsg",new Class[]{Object.class});
                            if(mMethod!=null)
                            {
                                JavaPlugin.NowTaskName = "调用:"+mInterpreter.Name+"的OnMsg消息回调";
                                mMethod.invoke(new Object[]{mData},mInterpreter.Instance);
                            }
                            mMethod = space.getMethod("OnChangedMixMsg",new Class[]{Object.class});
                            if(mMethod!=null)
                            {
                                JavaPlugin.NowTaskName = "调用:"+mInterpreter.Name+"的OnChangedMixMsg消息回调";
                                mMethod.invoke(new Object[]{mData},mInterpreter.Instance);
                            }
                        }
                    }
                    catch (Throwable th)
                    {
                        MLogCat.Print_PluginError("调用到脚本onMsg发生错误",th instanceof EvalError ? ((EvalError)th).Get_Raw_Infos() : th.toString());
                    }

                    try{
                        if(mData.MessageType == 2 || mData.MessageType == 4 ||mData.MessageType == 5 ||mData.MessageType == 6 )
                        {
                            JavaPlugin.NowTaskName = "调用:"+mInterpreter.Name+"的OnMsg消息回调";
                            BshMethod mMethod = space.getMethod("onMsg",new Class[]{Object.class});
                            if(mMethod!=null) mMethod.invoke(new Object[]{mData},mInterpreter.Instance);
                        }
                    }
                    catch (Throwable th)
                    {
                        MLogCat.Print_PluginError("调用到脚本onMsg发生错误",th instanceof EvalError ? ((EvalError)th).Get_Raw_Infos() : th.toString());

                    }


                }
            }
            catch (Throwable th)
            {
                Utils.ShowToast(th.toString());
            }
            finally {
                JavaPlugin.NowTaskName = "";
                JavaPlugin.TaskCount--;
            }
    }
    public static MessageData GuildMessageDecoder(Object Message,boolean IsSendFromLocal){
        try{
            MessageData data = new MessageData();
            String Type = Message.getClass().getSimpleName();
            if(Type.contains("MessageForText") || Type.contains("MessageForLongTextMsg") ||
                    Type.contains("MessageForPic") ||Type.contains("MessageForArkApp") ||
                    Type.contains("MessageForMixedMsg")
            )
            {
                JSONObject ChannelData = MField.GetField(Message,"mExJsonObject");
                String GUILD_ID = ChannelData.optString("GUILD_ID");
                String ChannelID = MField.GetField(Message,"frienduin");
                String SenderID = MField.GetField(Message,"senderuin");
                data.GroupUin = GUILD_ID + "&" + ChannelID;
                data.IsChannel = true;
                data.IsGroup = ChannelData.has("GUILD_MSG_FROM_NICK");
                data.SenderNickName = ChannelData.optString("GUILD_MSG_FROM_NICK");
                data.IsSend = IsSendFromLocal;
                data.UserUin = SenderID;
                data.msg = Message;
                data.AppInterface = MHookEnvironment.AppInterface;


                data.ChannelID = ChannelID;
                data.GuildID = GUILD_ID;


                data.MessageTime = MField.GetField(Message,"time",long.class);

                if(Type.equals("MessageForText")){
                    String Content = MField.GetField(Message,"msg",String.class);
                    data.MessageContent = Content;

                    data.MessageType = 1;

                    String mStr = MField.GetField(Message,Message.getClass(),"extStr",String.class);
                    JSONObject mJson = new JSONObject(mStr);
                    mStr = mJson.optString("guild_at_info_list");
                    ArrayList AtList3 = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"),"getTroopMemberInfoFromExtrJson",ArrayList.class,new Class[]{String.class},mStr);
                    if(AtList3!=null && AtList3.size()>0){
                        ArrayList<String> AtUinList = new ArrayList<>();
                        for(Object s : AtList3){
                            AtUinList.add(""+(MField.GetField(s,"uin",long.class)));
                        }
                        data.AtList = AtUinList.toArray(new String[0]);
                        data.mAtList = AtUinList;
                    }else
                    {
                        data.AtList = new String[0];
                        data.mAtList = new ArrayList();
                    }
                    if(data.MessageContent.equals("test")){
                        DebugUtils.PrintAllField_Count(Message,3);
                    }
                    return data;
                }
                if(Type.equals("MessageForPic")){
                    String MD5 = MField.GetField(Message,"md5",String.class);
                    MD5 = "http://gchat.qpic.cn/gchatpic_new/0/0-0-"+MD5+"/0?term=2";
                    data.MessageType = 1;
                    data.MessageContent = "[PicUrl="+MD5+"]";
                    data.PicList = new String[]{"[PicUrl="+MD5+"]"};
                    data.AtList = new String[0];
                    data.mAtList = new ArrayList();
                    return data;
                }
                if(Type.equals("MessageForArkApp")){
                    Object ArkAppMsg = MField.GetField(Message, Message.getClass(), "ark_app_message", MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"));
                    String json = MMethod.CallMethod(ArkAppMsg, MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"), "toAppXml", String.class, new Class[0], new Object[0]);
                    data.MessageContent = json;
                    data.MessageType = 2;
                    data.AtList = new String[0];
                    data.mAtList = new ArrayList();
                    return data;
                }
                if(Type.equals("MessageForMixedMsg")){
                    String SummaryData = "";
                    ArrayList<String> PicList = new ArrayList<>();

                    ArrayList Elems = MField.GetField(Message,"msgElemList");
                    for(Object Item : Elems){
                        if(Item.getClass().getSimpleName().equals("MessageForText")){
                            SummaryData+=MField.GetField(Item,"msg",String.class);
                        }else if (Item.getClass().getSimpleName().equals("MessageForPic")){
                            String PicMD5 = MField.GetField(Item,"md5",String.class);
                            PicMD5 = "http://gchat.qpic.cn/gchatpic_new/0/0-0-"+PicMD5+"/0?term=2";
                            SummaryData+="[PicUrl="+PicMD5+"]";
                            PicList.add(PicMD5);
                        }
                    }
                    data.MessageContent = SummaryData;
                    data.MessageType = 3;
                    data.PicList = PicList.toArray(new String[0]);
                    data.AtList = new String[0];
                    data.mAtList = new ArrayList();
                    return data;
                }
            }
        }catch (Exception e){
            MLogCat.Print_Error("PluginMessageDecoder:"+Message.getClass().getName(),e);
        }
        return null;

    }
}
