package com.ldq.connect.QQUtils;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.JavaPlugin.JavaPluginUtils;
import com.ldq.connect.MHookEnvironment;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedHelpers;

public class QQTools {
    public static void OpenTroopCard(String GroupUin) {
        try{
            Uri u = Uri.parse("mqq://card/show_pslcard?src_type=internal&version=1&uin="+GroupUin+"&card_type=group&source=qrcode");
            Intent in = new Intent(Intent.ACTION_VIEW,u);
            in.setPackage("com.tencent.mobileqq");
            MHookEnvironment.MAppContext.startActivity(in);
            /*
            Intent intent = new Intent();
            intent.putExtra("troop_uin",GroupUin);
            Method Invoke = MMethod.FindMethod("com.tencent.mobileqq.activity.PublicFragmentActivity","a",void.class,new Class[]{Context.class,Intent.class,Class.class});
            Invoke.invoke(null, Utils.GetThreadActivity(),intent, MClass.loadClass("com.tencent.mobileqq.troop.troopcard.VisitorTroopCardFragment"));

             */
        }
        catch (Exception ex)
        {
            MLogCat.Print_Error("OpenTroopCard", Log.getStackTraceString(ex));
        }
    }
    public static void OpenLink(String Link) {
        try{
            Uri u = Uri.parse(Link);
            Intent in = new Intent(Intent.ACTION_VIEW,u);
            MHookEnvironment.MAppContext.startActivity(in);
        }
        catch (Exception ex)
        {
            MLogCat.Print_Error("OpenTroopCard", Log.getStackTraceString(ex));
        }
    }
    public static void OpenInQQ(String Link)
    {
        Context context = Utils.GetThreadActivity();
        Intent intent = new Intent();
        intent.putExtra("url", Link);
        intent.putExtra("PARAM_PLUGIN_INTERNAL_ACTIVITIES_ONLY", false);
        intent.putExtra("leftViewText", "返回");
        intent.setClassName(context, "com.tencent.mobileqq.activity.QQBrowserActivity");
        context.startActivity(intent);
    }
    public static void OpenInQQ2(String Link)
    {
        Context context = Utils.GetThreadActivity();
        Intent intent = new Intent();
        intent.putExtra("url", Link);
        intent.setClassName(context, "com.tencent.mobileqq.activity.QQBrowserActivity");
        context.startActivity(intent);
    }
    public static void RequestJoin(Object SearchResult) throws Exception {
        Object GuildData = MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.guild.mainframe.startpanel.rightpart.discoversearch.data.GuildDiscoverGuildData"),
                "a",new Class[]{MClass.loadClass("com.tencent.mobileqq.qqguildsdk.data.IGProSearchGuildInfo"),String.class,boolean.class},
                SearchResult,"豆市场",false);



    }
    public static void OpenUserCard(String UserUin) {
        try{
            Uri u = Uri.parse("mqqapi://card/show_pslcard?src_type=internal&source=sharecard&version=1&uin="+UserUin);
            Intent in = new Intent(Intent.ACTION_VIEW,u);
            in.setPackage("com.tencent.mobileqq");
            MHookEnvironment.MAppContext.startActivity(in);
            /*
            Intent intent = new Intent();
            intent.putExtra("troop_uin",GroupUin);
            Method Invoke = MMethod.FindMethod("com.tencent.mobileqq.activity.PublicFragmentActivity","a",void.class,new Class[]{Context.class,Intent.class,Class.class});
            Invoke.invoke(null, Utils.GetThreadActivity(),intent, MClass.loadClass("com.tencent.mobileqq.troop.troopcard.VisitorTroopCardFragment"));

             */
        }
        catch (Exception ex)
        {
            MLogCat.Print_Error("OpenTroopCard", Log.getStackTraceString(ex));
        }
    }

    public static void CheckSign(String GroupUin,String UserUin){
        try{
            Object TroopHandle = MMethod.CallMethod(MHookEnvironment.AppInterface,MHookEnvironment.AppInterface.getClass(),"getBusinessHandler",
                    XposedHelpers.findClass("com.tencent.mobileqq.app.BusinessHandler",MHookEnvironment.mLoader),
                    new Class[]{String.class},
                    new Object[]{MField.GetField(null,MClass.loadClass("com.tencent.mobileqq.app.BusinessHandlerFactory"),"TROOP_CLOCKIN_HANDLER",1)});
            Method med = MMethod.FindMethod(TroopHandle.getClass(),"a",void.class,new Class[]{String.class,String.class,int.class,boolean.class});
            med.setAccessible(true);
            med.invoke(TroopHandle,GroupUin,UserUin,0,true);
        }catch (Exception e)
        {
            MLogCat.Print_Error("打卡失败",e);
        }
    }
    public static class HighLightItem{
        public int Start;
        public int End;
        public String Uin;
    }
    public static void AddClickItem(String GroupUin,String Items,HighLightItem[] ItemsClick)
    {
        try {
            Object ItemObject = com.ldq.connect.QQUtils.MessageRecoreFactory.Build_RawMessageRecord_Troop(GroupUin,-2030);
            MField.SetField(ItemObject,"msg",Items);
            for(HighLightItem item : ItemsClick)
            {
                Bundle b = new Bundle();
                b.putInt("key_action",5);
                b.putString("troop_mem_uin",item.Uin);
                b.putBoolean("need_update_nick", true);
                MMethod.CallMethod(ItemObject,"addHightlightItem",void.class,new Class[]{
                        int.class,int.class,Bundle.class
                },item.Start,item.End,b);
            }
            MField.SetField(ItemObject,"isread",true);
            BaseCall.AddMsg(ItemObject);
        } catch (Exception e) {
            MLogCat.Print_Error("AddClickItemError",e);
        }
    }
    static boolean InitSilk = false;
    public static void PCMToSilk(String PcmPath,String OutputPath)
    {
        try{
            InputStream ins = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.utils.SilkCodecWrapper"),new Class[]{Context.class},MHookEnvironment.MAppContext);

        }catch (Throwable th)
        {
            MLogCat.Print_Error("EncodeSilk Error",th);
        }
    }
    public static String GetUserName(String UserUin)
    {
        try {
            return MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.utils.ContactUtils"),"a",String.class,new Class[]{
                    MClass.loadClass("mqq.app.AppRuntime"),String.class,boolean.class
            },MHookEnvironment.AppInterface,UserUin,true);
        } catch (Exception e) {
            MLogCat.Print_Error("GetUserName2",e);
            return "";
        }
    }

    public static String GetPicPath(Object ChatMsg) throws Exception {
        MMethod.CallMethod(ChatMsg,"checkType",void.class,new Class[0]);
        String Path = MMethod.CallMethod(ChatMsg,"getFilePath",String.class,new Class[]{String.class},"chatimg");
        if(TextUtils.isEmpty(Path))
        {
            Path = MMethod.CallMethod(ChatMsg,"getFilePath",String.class,new Class[]{String.class},"chatraw");
        }
        if(TextUtils.isEmpty(Path))
        {
            Path = MMethod.CallMethod(ChatMsg,"getFilePath",String.class,new Class[]{String.class},"chatthumb");
        }
        if(TextUtils.isEmpty(Path))
        {
            String PicMd5 = MField.GetField(ChatMsg,"md5",String.class);
            String PicPath  = "http://gchat.qpic.cn/gchatpic_new/0/0-0-"+PicMd5+"/0?term=2";
            MHookEnvironment.mTask.PostTaskAndWait(() -> {
                        try {
                            HttpUtils.downlaodFile(PicPath, MHookEnvironment.PublicStorageModulePath + "Cache/", PicMd5);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
            Path = MHookEnvironment.PublicStorageModulePath + "Cache/"+PicMd5;
        }
        return Path;
    }

    public static long GetServerTime() throws Exception {
        return MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.msf.core.NetConnInfoCenter"),"getServerTime",long.class,new Class[0]);
    }
    public static void SaveExtraFlag(Object ChatMsg,String Name,String Value)
    {
        try {
            MMethod.CallMethod(ChatMsg,"saveExtInfoToExtStr",void.class,new Class[]{String.class,String.class},Name,Value);
        } catch (Exception e) {
            MLogCat.Print_Error("SaveFlag",e);
        }
    }
    public static String GetExtraFlag(Object ChatMsg,String Name)
    {
        try {
            return MMethod.CallMethod(ChatMsg,"getExtInfoFromExtStr",String.class,new Class[]{String.class},Name);
        } catch (Exception e) {
            MLogCat.Print_Error("SaveFlag",e);
            return "";
        }
    }

    public static Object getRuntimeService(Class Clz) throws Exception {
        Method Invoked = null;
        for(Method fs : MHookEnvironment.AppInterface.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredMethods())
        {
            if(fs.getName().equals("getRuntimeService"))
            {
                Invoked = fs;
                break;
            }
        }
        Object MessageFacade = Invoked.invoke(MHookEnvironment.AppInterface,Clz,"");
        return MessageFacade;
    }
    public static Object getBusinessHandler(String Clz) throws Exception {
        Method Invoked = null;
        for(Method fs : MHookEnvironment.AppInterface.getClass().getSuperclass().getSuperclass().getDeclaredMethods())
        {
            if(fs.getName().equals("getBusinessHandler"))
            {
                Invoked = fs;
                break;
            }
        }
        Object MessageFacade = Invoked.invoke(MHookEnvironment.AppInterface,Clz);
        return MessageFacade;
    }
    public static void UpdateMessage(Object MessageRecord)
    {
        try{
            Method Invoked = null;
            for(Method fs : MHookEnvironment.AppInterface.getClass().getSuperclass().getSuperclass().getSuperclass().getDeclaredMethods())
            {
                if(fs.getName().equals("getRuntimeService"))
                {
                    Invoked = fs;
                    break;
                }
            }
            Object MessageFacade = Invoked.invoke(MHookEnvironment.AppInterface,MClass.loadClass("com.tencent.mobileqq.msg.api.IMessageFacade"),"");
            String frienduin = MField.GetField(MessageRecord,"frienduin",String.class);
            int istroop = MField.GetField(MessageRecord,"istroop",int.class);
            long msgseq = MField.GetField(MessageRecord,"uniseq",long.class);
            byte[] brs = MField.GetField(MessageRecord,"msgData",byte[].class);
            MMethod.CallMethod(MessageFacade,"updateMsgContentByUniseq",void.class,new Class[]{String.class,int.class,long.class,byte[].class},frienduin,istroop,msgseq,brs);
        }catch (Throwable th)
        {
            MLogCat.Print_Error("UpdateError",th);
        }
    }
    public static void SendLike(String QQUin,int Count)
    {
        byte[] sCookie = {12, 24, 0, 1, 6, 1, 49, 22, 1, 49};
        long Selfuin = Long.parseLong(BaseInfo.GetCurrentUin());
        long TargetUin = Long.parseLong(QQUin);

        try {
            Method m = MMethod.FindMethod("com.tencent.mobileqq.app.CardHandler","a",void.class,new Class[]{
                    long.class,long.class,byte[].class,int.class,int.class,int.class
            });
            Object CardHandler = MMethod.CallMethod(MHookEnvironment.AppInterface,MHookEnvironment.AppInterface.getClass(),"getBusinessHandler",
                    XposedHelpers.findClass("com.tencent.mobileqq.app.BusinessHandler",MHookEnvironment.mLoader),
                    new Class[]{String.class},
                    new Object[]{MField.GetField(null,MClass.loadClass("com.tencent.mobileqq.app.BusinessHandlerFactory"),"CARD_HANLDER",1)});

            m.invoke(CardHandler,Selfuin,TargetUin,sCookie,IsFriends(QQUin)?1:10,Count,0);
        } catch (Exception exception) {
            MLogCat.Print_Error("SendLike",exception);
        }
    }
    public static boolean IsFriends(String uin)
    {
        try {
            Object FriendsManager = MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.friend.api.impl.FriendDataServiceImpl"),"getService",
                    MClass.loadClass("com.tencent.mobileqq.friend.api.impl.FriendDataServiceImpl"),new Class[]{MClass.loadClass("mqq.app.AppRuntime")},BaseInfo.GetAppInterface()
                    );
            return MMethod.CallMethod(FriendsManager,"isFriend",boolean.class,new Class[]{String.class},uin);
        } catch (Exception exception) {
            MLogCat.Print_Error("CheckIsFriend",exception);
            return false;
        }
    }
    public static boolean IsJoinInTroop(String uin)
    {
        try {
            ArrayList<JavaPluginUtils.GroupInfo> info = JavaPluginUtils.GetGroupInfo();
            for(JavaPluginUtils.GroupInfo mInfo : info)
            {
                if(mInfo.GroupUin.equals(uin))return true;
            }
            return false;
        } catch (Exception exception) {
            MLogCat.Print_Error("CheckIsFriend",exception);
            return false;
        }
    }
    public static ArrayList User_GetFriendList() {
        try {
            Object FriendManager = MMethod.CallMethod(MHookEnvironment.AppInterface,"getManager",
                    XposedHelpers.findClass("mqq.manager.Manager",MHookEnvironment.mLoader),
                    new Class[]{int.class},
                    new Object[]{MField.GetField(null,MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"FRIENDS_MANAGER",int.class)}
            );
            Object mService = MField.GetFirstField(FriendManager,FriendManager.getClass(),MClass.loadClass("com.tencent.mobileqq.friend.api.IFriendDataService"));


            ArrayList friendList = MMethod.CallMethod(mService,"getAllFriends", List.class,new Class[0]);
            return  friendList;
        } catch (Throwable throwable) {
            MLogCat.Print_Error("GetFriendList",throwable);
        }
        return null;
    }
    public static ArrayList User_GetFriendList2() {
        try {
            Object FriendManager = MMethod.CallMethod(MHookEnvironment.AppInterface,"getManager",
                    XposedHelpers.findClass("mqq.manager.Manager",MHookEnvironment.mLoader),
                    new Class[]{int.class},
                    new Object[]{MField.GetField(null,MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"FRIENDS_MANAGER",int.class)}
            );
            Object mService = MField.GetFirstField(FriendManager,FriendManager.getClass(),MClass.loadClass("com.tencent.mobileqq.friend.api.IFriendDataService"));


            ArrayList friendList = MMethod.CallMethod(mService,"getAllFriends", List.class,new Class[]{boolean.class},true);
            return  friendList;
        } catch (Throwable throwable) {
            MLogCat.Print_Error("GetFriendList",throwable);
        }
        return null;
    }
    public static void SendPai(String TroopUin,String UserUin) {
        try{
            Method m = MMethod.FindMethod("com.tencent.mobileqq.paiyipai.PaiYiPaiHandler","a",void.class,new Class[]{String.class,String.class,int.class});
            Object Handler = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.paiyipai.PaiYiPaiHandler"),new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface")
            },MHookEnvironment.AppInterface);

            if(TextUtils.isEmpty(TroopUin))
            {
                m.invoke(Handler,UserUin,UserUin,0);
            }else
            {
                m.invoke(Handler,UserUin,TroopUin,1);
            }
        }catch (Throwable th)
        {
            MLogCat.Print_Error("Paiyipai",th);
        }
    }
    public static int  DecodeAntEmoCode(int EmoCode)
    {
        try{
            String s = FileUtils.ReadFileString(MHookEnvironment.AppPath + "/files/qq_emoticon_res/face_config.json");
            JSONObject j = new JSONObject(s);
            JSONArray arr = j.getJSONArray("sysface");
            for(int i=0;i<arr.length();i++)
            {
                JSONObject obj = arr.getJSONObject(i);
                if(obj.has("AniStickerType"))
                {
                    if(obj.optString("QSid").equals(EmoCode+""))
                    {
                        String sId = obj.getString("AQLid");
                        return (Integer.parseInt(sId));
                    }

                }
            }
            return 0;
        }catch (Exception e)
        {
            return 0;
        }
    }
    public static boolean IsSticker(int i)
    {
        try{
            String s = FileUtils.ReadFileString(MHookEnvironment.AppPath + "/files/qq_emoticon_res/face_config.json");
            JSONObject j = new JSONObject(s);
            JSONArray arr = j.getJSONArray("sysface");
            for(int ix=0;ix<arr.length();ix++)
            {
                JSONObject obj = arr.getJSONObject(ix);
                if(obj.optString("AQLid").equals(""+i))
                {
                    if(obj.has("AniStickerType"))return true;
                }
            }
            return false;
        }catch (Exception e)
        {
            return false;
        }
    }
    public static Object getQRoundApi(Class clz){
        try{
            Class QRouteClz = MClass._loadClass("com.tencent.mobileqq.qroute.QRoute");
            for (Method m : QRouteClz.getDeclaredMethods()){
                if (m.getName().equals("api")){
                    m.setAccessible(true);
                    return m.invoke(null,clz);
                }
            }
            return null;
        }catch (Exception e){
            return null;
        }
    }
}
