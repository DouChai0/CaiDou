package com.ldq.connect.JavaPlugin;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MLogCat;
import com.ldq.connect.MHookEnvironment;

import org.json.JSONObject;

import java.io.File;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;
import java.util.Set;

public class JavaPluginConfigs
{
    private volatile static HashMap<String, HashMap> mMap = new HashMap<>();
    //private volatile static AtomicBoolean IsReplace = new AtomicBoolean();
    static {
        File ConfigPath = new File(MHookEnvironment.PublicStorageModulePath + "PluginConfig/");
        if(!ConfigPath.exists())ConfigPath.mkdirs();
        Thread t = new Thread(JavaPluginConfigs::SaveThread);
        t.setName("JavaPluginConfigSaveDataThread");
        t.start();
    }
    public static void PutConfig(String PluginID,String ConfigName,String Item,String Content)
    {
        try{
            HashMap<String,JSONObject> PluginMap;
            if(mMap.containsKey(PluginID))
            {
                PluginMap = mMap.get(PluginID);
            }
            else
            {
                PluginMap = new HashMap<>();
                File mPlugin = new File(MHookEnvironment.PublicStorageModulePath + "PluginConfig/"+PluginID+"/"+ConfigName+".json");
                if(mPlugin.exists())
                {
                    String FileContent = FileUtils.ReadFileString(mPlugin.getAbsolutePath());
                    JSONObject mJson = new JSONObject(FileContent);
                    PluginMap.put(ConfigName,mJson);
                }
            }
            JSONObject mJson;
            if(PluginMap.containsKey(ConfigName))
            {
                mJson = PluginMap.get(ConfigName);
            }
            else
            {
                mJson = new JSONObject();
            }
            if(Content==null)
            {
                mJson.remove(Item);
            }
            else
            {
                mJson.put(Item, DataUtils.bytesToHex(Content.getBytes(StandardCharsets.UTF_8)));
            }

            PluginMap.put(ConfigName,mJson);
            mMap.put(PluginID,PluginMap);
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("PluginConfig",th);
        }

    }
    public static String GetConfig(String PluginID,String ConfigName,String Item)
    {
        try{
            HashMap<String,JSONObject> PluginMap;
            if(mMap.containsKey(PluginID))
            {
                PluginMap = mMap.get(PluginID);
            }
            else
            {
                PluginMap = new HashMap<>();
                File mPlugin = new File(MHookEnvironment.PublicStorageModulePath + "PluginConfig/"+PluginID+"/"+ConfigName+".json");
                if(mPlugin.exists())
                {
                    String FileContent = FileUtils.ReadFileString(mPlugin.getAbsolutePath());
                    JSONObject mJson = new JSONObject(FileContent);
                    PluginMap.put(ConfigName,mJson);
                }
            }
            JSONObject mJson;
            if(PluginMap.containsKey(ConfigName))
            {
                mJson = PluginMap.get(ConfigName);
            }
            else
            {
                mJson = new JSONObject();
            }
            if(mJson.has(Item))
            {
                String str = mJson.getString(Item);
                str = new String(DataUtils.hexToByteArray(str),StandardCharsets.UTF_8);
                return str;
            }
            return null;
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("PluginConfig",th);
            return null;
        }
    }
    public static void SaveThread()
    {
        while (true)
        {
            try {
                Thread.sleep(1000);
                Set<String> it = mMap.keySet();
                for(String Keys : it)
                {
                    File Path = new File(MHookEnvironment.PublicStorageModulePath + "PluginConfig/"+Keys+"/");
                    if(!Path.exists())Path.mkdirs();


                    HashMap<String,JSONObject> mPluginMap = mMap.get(Keys);


                    Set<String> PluginChangedMap = mPluginMap.keySet();
                    for (String ConfigName : PluginChangedMap)
                    {
                        //MLogCat.Print_Debug(ConfigName);
                        JSONObject mJson = mPluginMap.get(ConfigName);
                        FileUtils.WriteFileByte(MHookEnvironment.PublicStorageModulePath + "PluginConfig/"+Keys+"/"+ConfigName+".json",mJson.toString().getBytes(StandardCharsets.UTF_8));
                    }
                }
                mMap.clear();
            } catch (Exception e) {
                MLogCat.Print_Error("SavePluginConfigToFile",e);
            }
        }
    }
}
