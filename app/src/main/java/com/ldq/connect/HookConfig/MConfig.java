package com.ldq.connect.HookConfig;

import android.text.TextUtils;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.FileUtils;

import org.json.JSONObject;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class MConfig {
    private static HashMap<String, HashMap<String,JSONObject>> PreloadConfig = new HashMap<>();
    private static HashMap<String,HashMap<String,JSONObject>> PreWriteData = new HashMap<>();
    static {
        Thread thread = new Thread(MConfig::Thread_Flush_Data);
        thread.setName("LD_Config_Data_Save_Thread");
        thread.start();
    }
    public static String Get_String(String PluginName,String PathName,String Key){
        JSONObject K_Json = Get_Path_JSONData(PluginName, PathName);
        return K_Json.optString(Key);
    }
    public static void Put_String(String PluginName,String PathName,String Key,String Value){
        try{
            JSONObject K_Json = Get_Path_JSONData(PluginName, PathName);
            if(TextUtils.isEmpty(Value)){
                K_Json.remove(Key);
            }else{
                K_Json.put(Key,Value);
            }

            PutDataToCache(PluginName,PathName,K_Json,true);
        }catch (Exception e){

        }
    }
    public static boolean Get_Boolean(String PluginName,String PathName,String Key,boolean DefValue){
        JSONObject K_Json = Get_Path_JSONData(PluginName, PathName);
        return K_Json.optBoolean(Key,DefValue);
    }
    public static void Put_Boolean(String PluginName,String PathName,String Key,boolean Value){
        try{
            JSONObject K_Json = Get_Path_JSONData(PluginName, PathName);
            K_Json.put(Key,Value);
            PutDataToCache(PluginName,PathName,K_Json,true);
        }catch (Exception e){ }
    }
    public static long Get_Long(String PluginName,String PathName,String Key,long DefValue){
        JSONObject K_Json = Get_Path_JSONData(PluginName, PathName);
        return K_Json.optLong(Key,DefValue);
    }
    public static void Put_Long(String PluginName,String PathName,String Key,long Value){
        try{
            JSONObject K_Json = Get_Path_JSONData(PluginName, PathName);
            K_Json.put(Key,Value);
            PutDataToCache(PluginName,PathName,K_Json,true);
        }catch (Exception e){ }
    }
    public static List Get_List(String PluginName,String PathName,String Key){
        try{
            JSONObject K_Json = Get_Path_JSONData(PluginName, PathName);
            String Data = K_Json.optString(Key);
            if(TextUtils.isEmpty(Data))return new ArrayList();
            ObjectInputStream objInp = new ObjectInputStream(new ByteArrayInputStream(DataUtils.hexToByteArray(Data)));
            return (List) objInp.readObject();
        }catch (Exception e){
            return new ArrayList();
        }
    }
    public static void Put_List(String PluginName, String PathName, String Key, List Value){
        try{
            JSONObject K_Json = Get_Path_JSONData(PluginName, PathName);
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            ObjectOutputStream out = new ObjectOutputStream(bOut);
            out.writeObject(Value);

            K_Json.put(Key,DataUtils.bytesToHex(bOut.toByteArray()));
            PutDataToCache(PluginName,PathName,K_Json,true);
        }catch (Exception e){

        }
    }



    private synchronized static JSONObject Get_Path_JSONData(String PluginName,String PathName){
        HashMap<String,JSONObject> NewMap = Get_Plugin_Maps(PluginName);
        if(!NewMap.containsKey(PathName)){
            String LoadPath = ConfigPathBuilder.Get_Current_Path_Set() + PluginName + "/" + PathName+".json";
            if(!new File(LoadPath).exists())return new JSONObject();
            try{
                JSONObject LoadJson = new JSONObject(FileUtils.ReadFileString(LoadPath));
                PutDataToCache(PluginName,PathName,LoadJson,false);
                return LoadJson;
            }catch (Exception e){
                return new JSONObject();
            }
        }
        return NewMap.get(PathName);
    }
    private static HashMap Get_Plugin_Maps(String PluginName){
        HashMap get = PreloadConfig.get(PluginName);
        return get == null ? new HashMap() : get;
    }
    private synchronized static void PutDataToCache(String PluginName,String PathName,JSONObject JsonData,boolean IsFlushToFile) {
        HashMap<String, JSONObject> Readed = PreloadConfig.get(PluginName);
        if (Readed == null) Readed = new HashMap<>();
        Readed.put(PathName, JsonData);
        PreloadConfig.put(PluginName, Readed);

        if (IsFlushToFile) {
            HashMap<String, JSONObject> Writeable = PreWriteData.get(PluginName);
            if (Writeable == null) Writeable = new HashMap<>();
            Writeable.put(PathName, JsonData);
            PreWriteData.put(PluginName, Writeable);
        }
    }
    private static void Thread_Flush_Data(){
        while (true){
            try {
                Thread.sleep(1000);
                synchronized (MConfig.class){
                    if(!PreWriteData.isEmpty()){
                        for(String PluginName : PreWriteData.keySet()){
                            String Path = ConfigPathBuilder.Get_Current_Path_Set()+PluginName;
                            File fDir = new File(Path);
                            if(!fDir.exists())fDir.mkdirs();

                            HashMap<String,JSONObject> PItem = PreWriteData.get(PluginName);
                            for(String ItemPath : PItem.keySet()){
                                JSONObject Item = PItem.get(ItemPath);
                                FileUtils.WriteFileByte(Path+"/"+ItemPath+".json",Item.toString().getBytes(StandardCharsets.UTF_8));
                            }
                        }
                        PreWriteData.clear();
                    }
                }
            }catch (InterruptedException e){
                break;
            }catch (Exception ignored){

            }
        }
    }


    public static boolean Troop_Get_Boolean(String PluginName,String PathName,String TroopUin,String Key,boolean DefValue){
        JSONObject K_Json = Get_Path_JSONData(PluginName, PathName);
        JSONObject VaItem = K_Json.optJSONObject(TroopUin);
        if(VaItem==null)VaItem = new JSONObject();

        return VaItem.optBoolean(Key,DefValue);
    }
    public static long Troop_Get_Long(String PluginName,String PathName,String TroopUin,String Key,long DefValue){
        JSONObject K_Json = Get_Path_JSONData(PluginName, PathName);
        JSONObject VaItem = K_Json.optJSONObject(TroopUin);
        if(VaItem==null)VaItem = new JSONObject();

        return VaItem.optLong(Key,DefValue);
    }

    public static void Troop_Put_Boolean(String PluginName,String PathName,String TroopUin,String Key,boolean Value){
        try{
            JSONObject K_Json = Get_Path_JSONData(PluginName, PathName);
            JSONObject VaItem = K_Json.optJSONObject(TroopUin);
            if(VaItem==null)VaItem = new JSONObject();

            VaItem.put(Key,Value);
            K_Json.put(TroopUin,VaItem);
            PutDataToCache(PluginName,PathName,K_Json,true);
        }catch (Exception e){

        }
    }

    public static void Troop_Put_Long(String PluginName,String PathName,String TroopUin,String Key,long Value){
        try{
            JSONObject K_Json = Get_Path_JSONData(PluginName, PathName);
            JSONObject VaItem = K_Json.optJSONObject(TroopUin);
            if(VaItem==null)VaItem = new JSONObject();

            VaItem.put(Key,Value);
            K_Json.put(TroopUin,VaItem);
            PutDataToCache(PluginName,PathName,K_Json,true);
        }catch (Exception e){

        }
    }


    public static void Troop_Put_String(String PluginName,String PathName,String TroopUin,String Key,String Value){
        try{
            JSONObject K_Json = Get_Path_JSONData(PluginName, PathName);
            JSONObject VaItem = K_Json.optJSONObject(TroopUin);
            if(VaItem==null)VaItem = new JSONObject();

            VaItem.put(Key,Value);
            K_Json.put(TroopUin,VaItem);
            PutDataToCache(PluginName,PathName,K_Json,true);
        }catch (Exception e){

        }
    }

    public static String  Troop_Get_String(String PluginName,String PathName,String TroopUin,String Key){
        JSONObject K_Json = Get_Path_JSONData(PluginName, PathName);
        JSONObject VaItem = K_Json.optJSONObject(TroopUin);
        if(VaItem==null)VaItem = new JSONObject();
        return VaItem.optString(Key);
    }

}
