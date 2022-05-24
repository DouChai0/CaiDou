package com.ldq.connect.HookConfig;

import android.text.TextUtils;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MLogCat;
import com.ldq.connect.MHookEnvironment;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class NDirDataBase {
    static final String LotDir = MHookEnvironment.PublicStorageModulePath + "LotData/";
    static {
        File f = new File(LotDir);
        if(f.exists())if(f.isFile())f.delete();
        if(!f.exists())f.mkdirs();
    }
    public synchronized static void SaveData(String Lot1,String Dir,String DayCut,String mData)
    {
        String SPath = LotDir+Lot1+"/"+Dir+"/"+DayCut+"/";
        if(!new File(SPath).exists())new File(SPath).mkdirs();
        String LootJSON = FileUtils.ReadFileString(SPath+"LootJSON.json");
        //计数文件更新
        JSONObject LootJSONObj = new JSONObject();
        if(!TextUtils.isEmpty(LootJSON))
        {
            try {
                LootJSONObj = new JSONObject(LootJSON);
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
        //数据文件更新
        try{
            int NowPos = LootJSONObj.optInt("DataCount") / 10;
            LootJSONObj.put("DataCount",LootJSONObj.optInt("DataCount")+1);



            String PosData = FileUtils.ReadFileString(SPath+NowPos+".dat");
            JSONObject DataJSON;
            if(!TextUtils.isEmpty(PosData))
            {
                DataJSON = new JSONObject(PosData);
            }else
            {
                DataJSON = new JSONObject();
            }

            JSONArray newArray = DataJSON.optJSONArray("data");
            if(newArray==null)newArray = new JSONArray();

            newArray.put(mData);
            DataJSON.put("data",newArray);

            FileUtils.WriteFileByte(SPath+NowPos+".dat",DataJSON.toString().getBytes());

            FileUtils.WriteFileByte(SPath+"LootJSON.json",LootJSONObj.toString().getBytes());

        }catch (Exception e)
        {
            MLogCat.Print_Error("LogDataError",e);
        }
    }
    public synchronized static ArrayList<String> ReadLine(String Lot1,String Dir,String DayCut,int StartLine) throws JSONException {
        String SPath = LotDir+Lot1+"/"+Dir+"/"+DayCut+"/";
        if(!new File(SPath).exists())return new ArrayList<>();
        //取得记录总数,如果取得数目大于了记录总数直接返回空结果
        int LineCount = GetLineCount(Lot1, Dir, DayCut);
        if (LineCount < StartLine) return new ArrayList<>();

        //计算要读取的行数,每次最多读取十行
        int NeedReadLine = Math.min(LineCount - StartLine, 10);
        ArrayList<String> retArray = new ArrayList<>();


        JSONObject ChcheObj;
        JSONArray ChcheArray = null;
        int CacheFileNumber = -1;
        for (int i=0;i<NeedReadLine;i++) {
            int NPos = StartLine + i;
            int FilePos = NPos / 10;
            if (CacheFileNumber != FilePos)
            {
                ChcheObj  = new JSONObject(FileUtils.ReadFileString(SPath+FilePos+".dat"));
                ChcheArray = ChcheObj.getJSONArray("data");
                CacheFileNumber = FilePos;
            }
            if(NPos % 10>=ChcheArray.length())continue;
            String s = ChcheArray.getString(NPos % 10);
            retArray.add(s);
        }
        return retArray;


    }

    public synchronized static int GetLineCount(String Lot1,String Dir,String DayCut)
    {
        String SPath = LotDir+Lot1+"/"+Dir+"/"+DayCut+"/";
        if(!new File(SPath).exists())return 0;
        String LootJSON = FileUtils.ReadFileString(SPath+"LootJSON.json");
        try{
            return new JSONObject(LootJSON).optInt("DataCount");
        }catch (Exception e)
        {
            return 0;
        }
    }
}
