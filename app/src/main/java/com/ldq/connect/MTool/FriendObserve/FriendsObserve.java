package com.ldq.connect.MTool.FriendObserve;

import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;

import androidx.core.app.NotificationCompat;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.HookConfig.MixUtils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQTools;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class FriendsObserve {
    static long rCacheTime = System.currentTimeMillis();
    static volatile int nCount = 0;
    public static void onResume(){
        if(System.currentTimeMillis() - rCacheTime > 10 * 60 * 1000 || nCount > 20){
            nCount++;
            rCacheTime = System.currentTimeMillis();
            OnObserveEvent();
        }
    }


    public static void StartCheck(){
        new Thread(()->{
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            OnObserveEvent();

        }).start();
    }
    public static void RefreshFriendsInfo(){
        String s = FileUtils.ReadFileString(MHookEnvironment.AppPath+"/app_"+MHookEnvironment.RndToken+"/"+ MixUtils.MixName("FRIEND") +"/list_"+BaseInfo.GetCurrentUin_Direct());
        DeletedFriendStore.InitUinData(BaseInfo.GetCurrentUin_Direct());
        try{
            mSavedFriendList = new JSONObject(s);
        }catch (Exception e){
            mSavedFriendList = new JSONObject();
        }finally {
            CacheJSONObject = null;
        }
    }
    private static String CacheUin;
    private static void CheckForUinUpdate(){
        String DirectUin = BaseInfo.GetCurrentUin_Direct();
        if (!DirectUin.equals(CacheUin)){
            CacheUin = DirectUin;
            RefreshFriendsInfo();
        }
    }

    static List<String> RemoveUin = Collections.synchronizedList(new ArrayList<>());
    public static void AddRemoveUin(String Uin){
        RemoveUin.add(Uin);
    }
    public synchronized static void OnObserveEvent(){
        CheckForUinUpdate();
        if(MConfig.Get_Boolean("Main","FriendNotify","好友监测通知",true)&&CacheNewJson()){
            try{
                First_Check_PreDeleteFriendCheck();
                long LastCheckTime = MConfig.Get_Long("Main","FriendNotify","LastCheckTime",0);
                JSONObject mCheckResult = new JSONObject();

                ArrayList<String> SavedFriendList = new ArrayList<>();
                ArrayList<String> CacheFriendList = new ArrayList<>();

                Iterator<String> s = CacheJSONObject.keys();
                while (s.hasNext())CacheFriendList.add(s.next());
                s = mSavedFriendList.keys();
                while (s.hasNext())SavedFriendList.add(s.next());

                for(String CheckSavedFriend : SavedFriendList){
                    if(!CacheFriendList.contains(CheckSavedFriend)){
                        JSONObject ResultItem = new JSONObject();
                        ResultItem.put("type",1);
                        mCheckResult.put(CheckSavedFriend,ResultItem);

                        DeletedFriendStore.SetUinStatus(CheckSavedFriend,DeletedFriendStore.STATUS_DELETED,LastCheckTime,System.currentTimeMillis(),
                                mSavedFriendList.getString(CheckSavedFriend));
                    }
                }
                UpdateSavedFriendList();
                MConfig.Put_Long("Main","FriendNotify","LastCheckTime",System.currentTimeMillis());

                //Last_Check_PreDeleteFriendCheck();
            }catch (Exception e){
                MLogCat.Print_Error("CheckFriendStatus",e);
            }
        }
    }
    public static void First_Check_PreDeleteFriendCheck(){
        ArrayList<String> CacheFriendList = new ArrayList<>();
        Iterator<String> s = CacheJSONObject.keys();
        while (s.hasNext())CacheFriendList.add(s.next());
        for (String Uin : CacheFriendList){
            if(DeletedFriendStore.GetUinStatus(Uin)==DeletedFriendStore.STATUS_PER_DELETE
                    || DeletedFriendStore.GetUinStatus(Uin)==DeletedFriendStore.STATUS_DELETED
            ){
                DeletedFriendStore.SetUinStatus(Uin,0,0,0,"");
            }
        }




        List<String> SavedUin = DeletedFriendStore.GetAllUinList();


        long LastCheckTime = MConfig.Get_Long("Main","FriendNotify","LastCheckTime",0);

        for(String Uins : SavedUin){
            if(DeletedFriendStore.GetUinStatus(Uins)==DeletedFriendStore.STATUS_PER_DELETE){
                DeletedFriendStore.SetUinStatus(Uins,DeletedFriendStore.STATUS_FINAL_DELETE,0,0,"");
                SendRemoveNotify(Uins,LastCheckTime,System.currentTimeMillis(),DeletedFriendStore.GetUinSavedName(Uins));
                AddRemoveUin(Uins);
            }
        }

        SavedUin = DeletedFriendStore.GetAllUinList();
        for(String Uins : SavedUin){
            if(DeletedFriendStore.GetUinStatus(Uins)==DeletedFriendStore.STATUS_DELETED){
                DeletedFriendStore.SetUinStatus(Uins,DeletedFriendStore.STATUS_PER_DELETE,0,0,"");
            }
        }


    }
    public static void CreateNotify(){
        NotificationChannel channel = new NotificationChannel("delete_friend_notify","好友删除通知", NotificationManager.IMPORTANCE_DEFAULT);
        channel.setSound(null,null);
        NotificationManager manager = (NotificationManager) MHookEnvironment.MAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
        manager.createNotificationChannel(channel);


    }

    public static void SendRemoveNotify(String Uin,long Time,long TimeTo,String Name){
        NotificationManager manager = (NotificationManager) MHookEnvironment.MAppContext.getSystemService(Context.NOTIFICATION_SERVICE);
        int id = MHookEnvironment.MAppContext.getResources().getIdentifier("qq_setting_me_bg","drawable","com.tencent.mobileqq");

        SimpleDateFormat format = new SimpleDateFormat("MM-dd HH:mm");
        Notification notify = new NotificationCompat.Builder(Utils.GetThreadActivity(),"delete_friend_notify")
                .setContentTitle("好友删除通知")
                .setContentText(Name+"("+Uin+")在"+format.format(new Date(Time))+"到"+format.format(new Date(TimeTo))+"时删除了好友")
                .setWhen(System.currentTimeMillis())
                .setSmallIcon(id)
                .setPriority(NotificationCompat.PRIORITY_DEFAULT)
                .build();
        manager.notify("好友删除通知",23333,notify);

    }
    private static void UpdateSavedFriendList(){


        mSavedFriendList = CacheJSONObject;

        Iterator<String> Coll = CacheJSONObject.keys();
        while (Coll.hasNext()){
            try{
                String Uin = Coll.next();
                if(!mSavedFriendList.has(Uin)){
                    mSavedFriendList.put(Uin,CacheJSONObject.get(Uin));
                }
            }catch (Exception e){

            }
        }

        Iterator<String> ItRemoves = RemoveUin.iterator();
        while (ItRemoves.hasNext()){
            String Uin = ItRemoves.next();
            ItRemoves.remove();

            mSavedFriendList.remove(Uin);
        }




        try{
            String Path = MHookEnvironment.AppPath+"/app_"+MHookEnvironment.RndToken+"/"+ MixUtils.MixName("FRIEND") +"/list_"+BaseInfo.GetCurrentUin();

            FileUtils.WriteFileByte(Path,mSavedFriendList.toString().getBytes());
        }catch (Exception e){
            MLogCat.Print_Error("UpdateSavedFriendList",e);
        }

    }

    static JSONObject CacheJSONObject;
    static JSONObject mSavedFriendList;
    public static boolean CacheNewJson(){
        InitCacheFriendInfo();
        if(LastError.get()!=0)
        {
            MLogCat.Print_Error("InitFriendInfo",LastError.get()+"");
        }
        return LastError.get()==0;
    }
    static AtomicInteger LastError = new AtomicInteger();
    private static void InitCacheFriendInfo(){
        ArrayList  mArr = QQTools.User_GetFriendList2();
        JSONObject UpdateCache = new JSONObject();
        if(mArr == null || mArr.size() < 1)
        {
            LastError.getAndSet(0x6);
            return;
        }

        for(Object ItemObj : mArr){
            try{
                String uin = MField.GetField(ItemObj,"uin",String.class);
                String Name = MField.GetField(ItemObj,"name",String.class);
                UpdateCache.put(uin,Name);
            }catch (Exception e){

            }
        }
        CacheJSONObject = UpdateCache;
        LastError.getAndSet(0);
    }
}
