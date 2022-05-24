package com.ldq.connect.MTool.FriendObserve;


import com.ldq.Utils.FileUtils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseInfo;

import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

public class DeletedFriendStore {
    public static final int STATUS_DELETED = 1;
    public static final int STATUS_RECEIVE = 2;
    public static final int STATUS_PER_DELETE = 3;
    public static final int STATUS_FINAL_DELETE = 4;

    static JSONObject CacheUinStatus;
    public static void InitUinData(String Uin){
        try{
            CacheUinStatus = new JSONObject(FileUtils.ReadFileString(MHookEnvironment.PublicStorageModulePath +"配置文件目录/delete_"+ Uin));
        }catch (Exception e){
            CacheUinStatus = new JSONObject();
        }
    }
    public static List<String> GetCacheDeleteList(){
        try{
            ArrayList<String> UinList = new ArrayList<>();
            Iterator<String> itsDelete = CacheUinStatus.keys();

            while (itsDelete.hasNext()){
                String Uin = itsDelete.next();
                JSONObject j1 = CacheUinStatus.getJSONObject(Uin);
                if(j1.optInt("status")==STATUS_FINAL_DELETE)
                {
                    UinList.add(Uin);
                }
            }
            return UinList;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }
    public static void ClearAll(){
        CacheUinStatus = new JSONObject();
        FileUtils.WriteFileByte(MHookEnvironment.PublicStorageModulePath +"配置文件目录/delete_"+ BaseInfo.GetCurrentUin(),CacheUinStatus.toString().getBytes());
    }

    public static List<String> GetAllUinList(){
        try{
            ArrayList<String> UinList = new ArrayList<>();
            Iterator<String> itsDelete = CacheUinStatus.keys();

            while (itsDelete.hasNext()){
                String Uin = itsDelete.next();
                UinList.add(Uin);
            }
            return UinList;
        }catch (Exception e){
            return new ArrayList<>();
        }
    }

    public static synchronized void SetUinStatus(String Uin,int Status,long StartTime,long Endtime,String Name){
        try{
            if(Status == 0)
            {
                CacheUinStatus.remove(Uin);

            }else
            {
                JSONObject j2Store = CacheUinStatus.has(Uin) ? CacheUinStatus.getJSONObject(Uin) : new JSONObject();
                j2Store.put("status",Status);
                j2Store.put("start",StartTime!=0?StartTime : j2Store.optLong("start"));
                j2Store.put("end",Endtime!=0 ? Endtime : j2Store.optLong("end"));
                j2Store.put("name",!Name.isEmpty() ? Name : j2Store.optString("name"));

                CacheUinStatus.put(Uin,j2Store);
            }
            FileUtils.WriteFileByte(MHookEnvironment.PublicStorageModulePath +"配置文件目录/delete_"+ BaseInfo.GetCurrentUin(),CacheUinStatus.toString().getBytes());

        }catch (Exception e){

        }
    }
    public static String GetUinTime(String Uin){
        try{
            JSONObject jr = CacheUinStatus.has(Uin) ? CacheUinStatus.getJSONObject(Uin) : new JSONObject();
            long Start = jr.optLong("start");
            long End = jr.optLong("end");

            SimpleDateFormat sFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm");
            return sFormat.format(new Date(Start)) + " 到 "+ sFormat.format(End);
        }catch (Exception e){
            return "null";
        }
    }
    public static int GetUinStatus(String Uin){
        try{
            JSONObject jr = CacheUinStatus.has(Uin) ? CacheUinStatus.getJSONObject(Uin) : new JSONObject();
            return jr.optInt("status");
        }catch (Exception e){
            return 0;
        }
    }
    public static String GetUinSavedName(String Uin){
        try{
            JSONObject jr = CacheUinStatus.has(Uin) ? CacheUinStatus.getJSONObject(Uin) : new JSONObject();
            return jr.getString("name");
        }catch (Exception e){
            return Uin;
        }
    }
}
