package com.ldq.connect.MainWorker.QQRedBagHelper;


import android.util.Log;

import com.ldq.Utils.StringUtils;
import com.ldq.Utils.Utils;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQTicketUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;

public class Handler_OneStrokeHBHelper {
    private String GameID;
    private String Pre_Code;
    private String listid;
    private String CacheURL;
    private String GameCreateUin;
    private String FeedSid;
    private boolean IsInnObject;


    public Handler_OneStrokeHBHelper(String URL){
        CacheURL = URL;
        GameID = StringUtils.GetOneStringMiddleMix(URL,"gameId%3D","%26");
        GameCreateUin = StringUtils.GetOneStringMiddleMix(CacheURL,"uin%3D","%26");
        int percode = CacheURL.indexOf("pre_code=");
        Pre_Code = CacheURL.substring(percode+9);
        listid = StringUtils.GetOneStringMiddleMix(CacheURL,"listid=","&");
        FeedSid = URLDecoder.decode(StringUtils.GetOneStringMiddleMix(CacheURL,"feedsid=","&"));

        try{
            if(Long.parseLong(GameID) < 10000){
                IsInnObject = true;
            }
        }catch (Exception e){

        }
    }
    private String mToken;
    public boolean decode(){
        try{
            String Info = GetHBInfo(GameID);
            JSONObject NewJson = new JSONObject(Info);
            if(NewJson.getInt("ret")==0){
                JSONObject NewObj = NewJson.getJSONObject("data");
                JSONArray connect = new JSONArray(NewObj.getString("connects"));
                JSONArray vertexes = new JSONArray(NewObj.getString("vertexes"));
                String Token = GetHBSign(connect);
                if(Token.isEmpty()){
                    return false;
                }
                mToken = Token;
                return true;

            }else{

            }
        }catch (Exception e){

        }
        return false;
    }
    public String getResultToken(){
        return mToken;
    }
    public String getFeedSid()
    {
        return FeedSid;
    }
    private String GetHBSign(JSONArray ArrData){
        try{
            String URLHttp = "https://h5.qianbao.qq.com/oneStrokePaintHb/cgi/checkOneStrokePaint?g_tk="+ QQTicketUtils.GetG_TK("qianbao.qq.com");
            URL u = new URL(URLHttp);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.addRequestProperty("referer",CacheURL);
            connection.addRequestProperty("origin","https://h5.qianbao.qq.com");
            connection.addRequestProperty("content-type","application/json;charset=UTF-8");
            connection.addRequestProperty("user-agent","Mozilla/5.0 (Linux; Android 12; LE2120 Build/SKQ1; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/89.0.4389.72 MQQBrowser/6.2 TBS/045913 Mobile Safari/537.36 V1_AND_SQ_8.8.55_2390_YYB_D QQ/8.8.55.6900 NetType/WIFI WebP/0.3.0 Pixel/1440 StatusBarHeight/128 SimpleUISwitch/0 QQTheme/0 InMagicWin/0 StudyMode/0 CurrentMode/0 CurrentFontScale/1.0");
            connection.addRequestProperty("cookie","appid=10011");
            connection.addRequestProperty("cookie","app_key=ZmsiXB930O2pwx8j");
            connection.addRequestProperty("cookie","uin="+ BaseInfo.GetCurrentUinO2());
            connection.addRequestProperty("cookie","p_uin="+ BaseInfo.GetCurrentUinO2());
            connection.addRequestProperty("cookie","skey="+ QQTicketUtils.GetSkey());
            connection.addRequestProperty("cookie","p_skey="+ QQTicketUtils.GetPsKey("qianbao.qq.com"));

            connection.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("feedSid",FeedSid);
            json.put("preCode",Pre_Code);
            json.put("oneStrokeId",GameID);
            json.put("oneStrokeHbOrbit",ArrData);

            json.put("grapUin",BaseInfo.GetCurrentUin());
            json.put("hbListId",listid);
            if(IsInnObject){
                json.put("sendUin","");
                json.put("strokeType",0);
            }else {
                json.put("sendUin",GameCreateUin);
                json.put("strokeType",1);
            }


            OutputStream Write = connection.getOutputStream();
            Write.write(json.toString().getBytes(StandardCharsets.UTF_8));
            Write.flush();

            InputStream inp = connection.getInputStream();
            String Result = new String(Utils.readAllBytes(inp));
            JSONObject jsonRet = new JSONObject(Result);

            return jsonRet.getString("data");

        }catch (Exception e){
            return "";
        }
    }
    private String GetHBInfo(String mGameID){

        try{
            try{
                if (Long.parseLong(mGameID) < 10000){
                    return GetLocalHBInfo();
                }
            }catch (Exception e){

            }

            String URLHttp = "https://h5.qianbao.qq.com/sendRedpack/cgi/getOneStrokeSubject?g_tk="+ QQTicketUtils.GetG_TK("qianbao.qq.com");
            URL u = new URL(URLHttp);
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.addRequestProperty("referer",CacheURL);
            connection.addRequestProperty("origin","https://h5.qianbao.qq.com");
            connection.addRequestProperty("content-type","application/json;charset=UTF-8");
            connection.addRequestProperty("user-agent","Mozilla/5.0 (Linux; Android 12; LE2120 Build/SKQ1; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/89.0.4389.72 MQQBrowser/6.2 TBS/045913 Mobile Safari/537.36 V1_AND_SQ_8.8.55_2390_YYB_D QQ/8.8.55.6900 NetType/WIFI WebP/0.3.0 Pixel/1440 StatusBarHeight/128 SimpleUISwitch/0 QQTheme/0 InMagicWin/0 StudyMode/0 CurrentMode/0 CurrentFontScale/1.0");
            connection.addRequestProperty("cookie","appid=10011");
            connection.addRequestProperty("cookie","app_key=ZmsiXB930O2pwx8j");
            connection.addRequestProperty("cookie","uin="+ BaseInfo.GetCurrentUinO2());
            connection.addRequestProperty("cookie","p_uin="+ BaseInfo.GetCurrentUinO2());
            connection.addRequestProperty("cookie","skey="+ QQTicketUtils.GetSkey());
            connection.addRequestProperty("cookie","p_skey="+ QQTicketUtils.GetPsKey("qianbao.qq.com"));

            connection.setDoOutput(true);

            JSONObject json = new JSONObject();
            json.put("uin",GameCreateUin);
            json.put("gameId",mGameID);
            OutputStream Write = connection.getOutputStream();
            Write.write(json.toString().getBytes(StandardCharsets.UTF_8));
            Write.flush();

            InputStream inp = connection.getInputStream();
            String Result = new String(Utils.readAllBytes(inp));
            return Result;

        }catch (Exception e){
            Utils.ShowToast(Log.getStackTraceString(e));
            return "{}";
        }
    }
    private String GetLocalHBInfo() throws Exception{
        URL u = new URL(CacheURL);
        HttpURLConnection connection = (HttpURLConnection) u.openConnection();
        connection.addRequestProperty("referer",CacheURL);
        connection.addRequestProperty("origin","https://h5.qianbao.qq.com");
        connection.addRequestProperty("content-type","application/json;charset=UTF-8");
        connection.addRequestProperty("user-agent","Mozilla/5.0 (Linux; Android 12; LE2120 Build/SKQ1; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/89.0.4389.72 MQQBrowser/6.2 TBS/045913 Mobile Safari/537.36 V1_AND_SQ_8.8.55_2390_YYB_D QQ/8.8.55.6900 NetType/WIFI WebP/0.3.0 Pixel/1440 StatusBarHeight/128 SimpleUISwitch/0 QQTheme/0 InMagicWin/0 StudyMode/0 CurrentMode/0 CurrentFontScale/1.0");
        connection.addRequestProperty("cookie","appid=10011");
        connection.addRequestProperty("cookie","app_key=ZmsiXB930O2pwx8j");
        connection.addRequestProperty("cookie","uin="+ BaseInfo.GetCurrentUinO2());
        connection.addRequestProperty("cookie","p_uin="+ BaseInfo.GetCurrentUinO2());
        connection.addRequestProperty("cookie","skey="+ QQTicketUtils.GetSkey());
        connection.addRequestProperty("cookie","p_skey="+ QQTicketUtils.GetPsKey("qianbao.qq.com"));


        InputStream inp = connection.getInputStream();
        String Result = new String(Utils.readAllBytes(inp));
        Result = StringUtils.GetOneStringMiddleMix(Result,"window._servData = ",";");
        JSONObject NewJson = new JSONObject();
        JSONObject reJson = new JSONObject(Result);
        NewJson.put("ret",0);
        NewJson.put("data",reJson.getJSONArray("oneStrokeSubjects").getJSONObject(0));

        return NewJson.toString();
    }
}
