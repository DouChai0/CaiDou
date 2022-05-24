package com.ldq.connect.MainWorker.ProxyHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.StringUtils;
import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MainWorker.ProxyHook.Hook_For_WebView_Load;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQTicketUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLDecoder;
import java.nio.charset.StandardCharsets;
import java.util.concurrent.atomic.AtomicBoolean;

public class Handler_OneStrokeHB_Decode {
    private static final String GrabHBJavaScript = "javascript:var aaasss = {\n" +
            "    gotoTenPayView: 1,\n" +
            "\tcloseWebView: 1,\n" +
            "    come_from: 1,\n" +
            "    app_info: \"appid#1344242394|bargainor_id#1000030201|channel#banner\",\n" +
            "    action: \"graphb\",\n" +
            "    params: {\n" +
            "        listid: \"{Replace_listid}\",\n" +
            "        feedsid: \"{Replace_feedsid}\",\n" +
            "        token: \"{Replace_token}\",\n" +
            "        uin: \"{Replace_uin}\"\n" +
            "    }\n" +
            "};\n" +
            "window.mqq.invoke(\"qw_pay\", \"qWalletBridge\", aaasss,null);";
    static AtomicBoolean IsShow = new AtomicBoolean();
    static String GameID;
    static String Pre_Code;
    static String listid;
    static String CacheURL;
    static String GameCreateUin;
    static WindowManager mManager;
    static ImageButton button;
    static String FeedSid;
    static Activity activeAct;
    static boolean IsInnObject;
    public static void InitAvailable(String URL){
        CacheURL = URL;
    }
    @SuppressLint("ResourceType")
    public static void ActivityAvailable(Activity act){

        if(!IsShow.getAndSet(true) && !TextUtils.isEmpty(CacheURL)){
            GameID = StringUtils.GetOneStringMiddleMix(CacheURL,"gameId%3D","%26");
            try{
                if(Long.parseLong(GameID) < 10000){
                    IsInnObject = true;
                }
            }catch (Exception e){

            }
            GameCreateUin = StringUtils.GetOneStringMiddleMix(CacheURL,"uin%3D","%26");
            int percode = CacheURL.indexOf("pre_code=");
            Pre_Code = CacheURL.substring(percode+9);
            listid = StringUtils.GetOneStringMiddleMix(CacheURL,"listid=","&");
            FeedSid = URLDecoder.decode(StringUtils.GetOneStringMiddleMix(CacheURL,"feedsid=","&"));


            mManager = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
            ImageButton btnClick = new ImageButton(act);
            btnClick.setImageAlpha(182);
            btnClick.setId(2366);
            btnClick.setImageDrawable(DLAndLoad());
            btnClick.setScaleType(ImageView.ScaleType.FIT_CENTER);
            btnClick.setOnClickListener(v->ClickToMenu());
            btnClick.getBackground().setAlpha(0);


            button = btnClick;

            WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
            //layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
            layoutParams.type = WindowManager.LayoutParams.FIRST_SUB_WINDOW+5;
            layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
            layoutParams.format = PixelFormat.RGBA_8888;
            layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
            layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;

            activeAct = act;
            mManager.addView(btnClick,layoutParams);




        }

    }
    public static void ClickToMenu(){
        new AlertDialog.Builder(activeAct,3)
                .setTitle("选择操作")
                .setItems(new String[]{"打开该红包","保存到列表","保存到本地"}, (dialog, which) -> {
                    if(which == 0)OpenOneStrokeHB();
                    if(which == 1)SaveToList();
                    if(which == 2)SaveToLocal();
                }).setNegativeButton("关闭", (dialog, which) -> {

                }).show();
    }
    private static void SaveToList(){
        new Thread(()->{
            try{
                String GetData = GetHBInfo(GameID);
                JSONObject NewJson = new JSONObject(GetData);
                if (NewJson.getInt("ret")!=0){
                    Utils.ShowToast("获取矩阵信息失败");
                    return;
                }
                JSONObject SaveMatrix = new JSONObject();
                JSONObject NewObj = NewJson.getJSONObject("data");
                JSONArray connect = new JSONArray(NewObj.getString("connects"));
                SaveMatrix.put("connect",connect);
                JSONArray vertexes = new JSONArray(NewObj.getString("vertexes"));
                SaveMatrix.put("vertex",vertexes);
                SaveMatrix.put("img",NewObj.getString("imgUrl").replace("\\/","/"));

                SaveMatrix.put("vertexCount",NewObj.getString("vertexCount"));

                Upload_HBInfo(SaveMatrix.toString());


            }
            catch (Exception e){
                Utils.ShowToast("发生错误:"+e);
            }

        }).start();
    }
    private static void SaveToLocal(){
        new Thread(()->{
            try{
                String GetData = GetHBInfo(GameID);
                JSONObject NewJson = new JSONObject(GetData);
                if (NewJson.getInt("ret")!=0){
                    Utils.ShowToast("获取矩阵信息失败");
                    return;
                }
                JSONObject SaveMatrix = new JSONObject();
                JSONObject NewObj = NewJson.getJSONObject("data");
                JSONArray connect = new JSONArray(NewObj.getString("connects"));
                SaveMatrix.put("connect",connect);
                JSONArray vertexes = new JSONArray(NewObj.getString("vertexes"));
                SaveMatrix.put("vertex",vertexes);
                SaveMatrix.put("img",NewObj.getString("imgUrl").replace("\\/","/"));

                SaveMatrix.put("vertexCount",NewObj.getString("vertexCount"));

                new Handler(Looper.getMainLooper())
                        .post(()->{
                            EditText edName = new EditText(activeAct);
                            new AlertDialog.Builder(activeAct,3)
                                    .setTitle("输入保存的名字")
                                    .setView(edName)
                                    .setNegativeButton("保存", (dialog, which) -> {
                                        String Name = edName.getText().toString();
                                        if(Name.isEmpty())
                                        {
                                            Utils.ShowToast("不能输入空名字");
                                            return;
                                        }

                                        String Path = MHookEnvironment.PublicStorageModulePath + "数据目录/一笔画数据/"+Name+".json";
                                        FileUtils.WriteFileByte(Path,SaveMatrix.toString().getBytes(StandardCharsets.UTF_8));
                                        Utils.ShowToast("已保存");
                                    }).show();
                        });

            }
            catch (Exception e){
                Utils.ShowToast("发生错误:"+e);
            }

        }).start();
    }
    private static void OpenOneStrokeHB(){
        new Thread(()->{
            try{
                String Info = GetHBInfo(GameID);
                JSONObject NewJson = new JSONObject(Info);
                if(NewJson.getInt("ret")==0){
                    JSONObject NewObj = NewJson.getJSONObject("data");
                    JSONArray connect = new JSONArray(NewObj.getString("connects"));
                    JSONArray vertexes = new JSONArray(NewObj.getString("vertexes"));
                    String Token = GetHBSign(connect);
                    if(Token.isEmpty()){
                        Utils.ShowToast("打开失败,红包可能无解");
                        return;
                    }

                    String NewParseData = GrabHBJavaScript.replace("{Replace_listid}",listid)
                            .replace("{Replace_feedsid}",FeedSid)
                            .replace("{Replace_token}",Token)
                            .replace("{Replace_uin}",BaseInfo.GetCurrentUin());
                    //Utils.ShowToast("JSBridgePush:"+NewParseData);
                    Hook_For_WebView_Load.JSBridge_LoadURL(NewParseData);

                }else{
                    Utils.ShowToast("打开失败,无法获取矩阵信息");
                }
            }catch (Exception e){
                Utils.ShowToast("打开失败");
            }
        }).start();


    }
    public static Drawable DLAndLoad(){
        String sClick = MHookEnvironment.PublicStorageModulePath+"配置文件目录/click";
        if(!new File(sClick).exists()){
            HttpUtils.downlaodFile(MHookEnvironment.ServerRoot_CDN+"down/click.png",sClick);
        }
        return Drawable.createFromPath(sClick);
    }
    public static void DestroyFloatWindow(){
        IsShow.getAndSet(false);
        CacheURL = null;
    }
    private static String GetHBSign(JSONArray ArrData){
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
    private static String GetHBInfo(String mGameID){

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
    private static String GetLocalHBInfo() throws Exception{
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
    public static void Upload_HBInfo(String UploadInfo){
        try{
            JSONObject SaveJson = new JSONObject(UploadInfo);
            JSONObject PostData = new JSONObject();
            PostData.put("connects",SaveJson.getJSONArray("connect"));
            PostData.put("vertexes",SaveJson.getJSONArray("vertex"));
            PostData.put("vertexCount",SaveJson.getJSONArray("vertex").length());
            JSONObject emptyJson = new JSONObject();
            PostData.put("img",emptyJson);
            PostData.put("imgUrl",SaveJson.getString("img"));
            PostData.put("id",String.valueOf(System.currentTimeMillis()));

            String URLHttp = "https://h5.qianbao.qq.com/sendRedpack/cgi/saveOneStrokeSubject?g_tk="+ QQTicketUtils.GetG_TK("qianbao.qq.com");
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

            OutputStream Write = connection.getOutputStream();
            Write.write(PostData.toString().getBytes(StandardCharsets.UTF_8));
            Write.flush();

            InputStream inp = connection.getInputStream();
            String Result = new String(Utils.readAllBytes(inp));
            JSONObject jRet = new JSONObject(Result);
            if(jRet.optInt("ret")==0){
                Utils.ShowToast("已保存");
            }else {
                Utils.ShowToast("保存失败,代码:"+jRet.optInt("ret"));
            }


        }catch (Exception e){
            Utils.ShowToast("发生错误:"+e);
        }
    }
}
