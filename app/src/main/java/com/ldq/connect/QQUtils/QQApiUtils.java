package com.ldq.connect.QQUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.HashMap;

public class QQApiUtils {
    static HashMap<String,HashMap<Long,String>> CacheMap;

    public static String Try_Get_UserList(String Skey,String PsKey,String ChannelID){
        try{
            String url = "https://qun.qq.com/qunpro/passcheck/trpc/passCard/GetGuildMembers?bkn="+getBKN2233(PsKey);
            HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();

            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.addRequestProperty("Cookie","uin="+GenuralUin(BaseInfo.GetCurrentUin()));
            http.addRequestProperty("Cookie","p_uin="+GenuralUin(BaseInfo.GetCurrentUin()));
            http.addRequestProperty("Cookie","skey="+Skey);
            http.addRequestProperty("Cookie","p_skey="+PsKey);
            http.addRequestProperty("Cookie","qq_locale_id=2052");
            http.addRequestProperty("Content-Type","application/json");
            http.addRequestProperty("Referer","https://qun.qq.com/qunpro/passcheck/index/member-grant?_wv=16777219&_wwv=129&_cwv=9&guildId="+ChannelID+"&guildName=&userType=2");
            http.addRequestProperty("user-agent","Mozilla/5.0 (Linux; Android 10; LE2120 Build/RKQ1; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/89.0.4389.72 MQQBrowser/6.2 TBS/045827 Mobile Safari/537.36 V1_AND_SQ_8.8.50_2324_YYB_D A_8085000 QQ/8.8.50.6735 NetType/WIFI WebP/0.3.0 Pixel/1440 StatusBarHeight/128 SimpleUISwitch/0 QQTheme/100041 InMagicWin/0 StudyMode/0 CurrentMode/0 CurrentFontScale/1.0");




            JSONObject Request = new JSONObject();
            /*
            JSONArray Request_Array = new JSONArray();
            JSONObject Item = new JSONObject();
            Item.put("uin",UserUin);
            Item.put("num",1);
            Request_Array.put(Item);

             */

            Request.put("guild_id",ChannelID);
            Request.put("uint32_get_num",5000);
            Request.put("role_id_index","2");
            //Request.put("role",1000);
            byte[] Arr = Request.toString().getBytes(StandardCharsets.UTF_8);

            OutputStream out = http.getOutputStream();
            out.write(Arr);
            out.flush();
            out.close();

            InputStream ins = http.getInputStream();
            byte[] Buffer = new byte[1024];
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            int Read;
            while ((Read = ins.read(Buffer))!=-1){
                bOut.write(Buffer,0,Read);
            }
            return new String(bOut.toByteArray());


        }catch (Exception e){
            e.printStackTrace();
            return null;
        }
    }
    public static boolean SendCard(String Skey,String PsKey,String UserUin,String ChannelID,int Count){
        try{
            String url = "https://qun.qq.com/qunpro/passcheck/trpc/passCard/GrantSpecifiedMember?bkn="+getBKN2233(PsKey);
            HttpURLConnection http = (HttpURLConnection) new URL(url).openConnection();

            http.setDoOutput(true);
            http.setRequestMethod("POST");
            http.addRequestProperty("Cookie","uin="+GenuralUin(BaseInfo.GetCurrentUin()));
            http.addRequestProperty("Cookie","p_uin="+GenuralUin(BaseInfo.GetCurrentUin()));
            http.addRequestProperty("Cookie","skey="+Skey);
            http.addRequestProperty("Cookie","p_skey="+PsKey);
            http.addRequestProperty("Cookie","qq_locale_id=2052");
            http.addRequestProperty("Content-Type","application/json");
            http.addRequestProperty("Referer","https://qun.qq.com/qunpro/passcheck/index/member-grant?_wv=16777219&_wwv=129&_cwv=9&guildId="+ChannelID+"&guildName=&userType=2");
            http.addRequestProperty("user-agent","Mozilla/5.0 (Linux; Android 10; LE2120 Build/RKQ1; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/89.0.4389.72 MQQBrowser/6.2 TBS/045827 Mobile Safari/537.36 V1_AND_SQ_8.8.50_2324_YYB_D A_8085000 QQ/8.8.50.6735 NetType/WIFI WebP/0.3.0 Pixel/1440 StatusBarHeight/128 SimpleUISwitch/0 QQTheme/100041 InMagicWin/0 StudyMode/0 CurrentMode/0 CurrentFontScale/1.0");




            JSONObject Request = new JSONObject();
            JSONArray Request_Array = new JSONArray();
            JSONObject Item = new JSONObject();
            Item.put("uin",UserUin);
            Item.put("num",Count);
            Request_Array.put(Item);

            Request.put("guild_id",ChannelID);
            Request.put("grants",Request_Array);
            byte[] Arr = Request.toString().getBytes(StandardCharsets.UTF_8);

            OutputStream out = http.getOutputStream();
            out.write(Arr);
            out.flush();
            out.close();

            InputStream ins = http.getInputStream();
            byte[] Buffer = new byte[1024];
            ByteArrayOutputStream bOut = new ByteArrayOutputStream();
            int Read;
            while ((Read = ins.read(Buffer))!=-1){
                bOut.write(Buffer,0,Read);
            }

            String ResultA = bOut.toString();
            JSONObject put = new JSONObject(ResultA);
            if(put.getJSONObject("response").optInt("success_num")>=1){
                return true;
            }
            return true;


        }catch (Exception e){
            e.printStackTrace();
            return false;
        }


    }
    public static String GenuralUin(String Uin){
        String s = Uin;
        while (s.length()<10)s = "0"+s;
        return "0"+s;
    }
    public static String getBKN2233(String aarr) {
        String skey = aarr;
        int t = 5381;
        for(int i = 0; i < skey.length(); i++) t += (t << 5) + skey.charAt(i);
        return String.valueOf(t & 2147483647);
    }
}
