package com.ldq.connect.MainWorker.ProxyHandler;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuItem;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.ConfigPathBuilder;
import com.ldq.connect.MHookEnvironment;

import org.json.JSONException;
import org.json.JSONObject;

import java.nio.charset.StandardCharsets;
import java.util.Calendar;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Handler_Count_Input_Data {
    static JSONObject Save_Input_Data;
    public static void Init(){
        try{
            Save_Input_Data = new JSONObject(FileUtils.ReadFileString(ConfigPathBuilder.Get_Current_Path_Set() +"CountWord.json"));
        }catch (Exception e){
            Save_Input_Data = new JSONObject();
            try {
                Save_Input_Data.put("Start_Time",System.currentTimeMillis());
            } catch (JSONException jsonException) {
                jsonException.printStackTrace();
            }
        }


        Save_File();

        try{
            XposedHelpers.findAndHookMethod("com.tencent.mobileqq.activity.aio.helper.FullScreenInputHelper$5", MHookEnvironment.mLoader,
                    "onCreateActionMode", ActionMode.class, Menu.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            Menu m = (Menu) param.args[1];
                            m.add(0,6688,196608,"查看输入统计数据");
                        }
                    });

            XposedHelpers.findAndHookMethod("com.tencent.mobileqq.activity.aio.helper.FullScreenInputHelper$5", MHookEnvironment.mLoader,
                    "onActionItemClicked", ActionMode.class, MenuItem.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            MenuItem item = (MenuItem) param.args[1];
                            if(item.getItemId()==6688){
                                param.setResult(true);
                                ShowSummaryCount();
                            }
                        }
                    });
        }catch (Throwable th){
            MLogCat.Print_Error("HookForInputBox",th);
        }
    }
    public static void ShowSummaryCount(){
        try{
            String ShowText = "今日统计数据:\n";
            JSONObject Submit = Save_Input_Data.has("today") ? Save_Input_Data.getJSONObject("today") : new JSONObject();
            ShowText+="字数:"+Submit.optInt("word")+"\n";
            ShowText+="消息条数:"+Submit.optInt("count")+"\n";
            ShowText+="图片:"+Submit.optInt("pic")+"\n";
            ShowText+="卡片:"+Submit.optInt("card")+"\n";
            ShowText+="语音:"+Submit.optInt("voice")+"\n";
            ShowText+="回复:"+Submit.optInt("reply")+"\n";

            ShowText+="\n\n记录开始至今:\n";
            Submit = Save_Input_Data.has("summary") ? Save_Input_Data.getJSONObject("summary") : new JSONObject();

            ShowText+="字数:"+Submit.optInt("word")+"\n";
            ShowText+="消息条数:"+Submit.optInt("count")+"\n";
            ShowText+="图片:"+Submit.optInt("pic")+"\n";
            ShowText+="卡片:"+Submit.optInt("card")+"\n";
            ShowText+="语音:"+Submit.optInt("voice")+"\n";
            ShowText+="回复:"+Submit.optInt("reply")+"\n";

            new AlertDialog.Builder(Utils.GetThreadActivity(),3)
                    .setTitle("统计数据")
                    .setMessage(ShowText).show();


        }catch (Exception e){
            Utils.ShowToast(Log.getStackTraceString(e));
            Utils.ShowToast("显示错误");
        }


    }

    public static String Get_Count(){
        try{
            JSONObject Submit = Save_Input_Data.has("today") ? Save_Input_Data.getJSONObject("today") : new JSONObject();
            String s = "";
            s+=Submit.optInt("count")+"条|";
            s+=Submit.optInt("word")+"字|";
            s+=Submit.optInt("pic")+"张图";
            return s;
        }catch (Exception e){
            //Utils.ShowToast(Log.getStackTraceString(e));
            return "获取失败";
        }

    }
    public static void Count_Line(){
        try{
            JSONObject Submit = Save_Input_Data.has("summary") ? Save_Input_Data.getJSONObject("summary") : new JSONObject();
            Submit.put("count",Submit.optInt("count")+1);
            Save_Input_Data.put("summary",Submit);

            Submit = Save_Input_Data.has("today") ? Save_Input_Data.getJSONObject("today") : new JSONObject();
            Submit.put("count",Submit.optInt("count")+1);
            Save_Input_Data.put("today",Submit);

        }catch (Exception e){
        }

    }
    public static void Count_Text(String Text){
        try{
            JSONObject Submit = Save_Input_Data.has("summary") ? Save_Input_Data.getJSONObject("summary") : new JSONObject();
            Submit.put("word",Submit.optInt("word")+Text.length());
            Submit.put("rtext",Submit.optInt("rtext")+1);
            Save_Input_Data.put("summary",Submit);

            Submit = Save_Input_Data.has("today") ? Save_Input_Data.getJSONObject("today") : new JSONObject();
            Submit.put("word",Submit.optInt("word")+Text.length());
            Submit.put("rtext",Submit.optInt("rtext")+1);
            Save_Input_Data.put("today",Submit);

        }catch (Exception e){
        }
    }
    public static void Count_Pic(){
        try{
            JSONObject Submit = Save_Input_Data.has("summary") ? Save_Input_Data.getJSONObject("summary") : new JSONObject();
            Submit.put("pic",Submit.optInt("pic")+1);
            Save_Input_Data.put("summary",Submit);

            Submit = Save_Input_Data.has("today") ? Save_Input_Data.getJSONObject("today") : new JSONObject();
            Submit.put("pic",Submit.optInt("pic")+1);
            Save_Input_Data.put("today",Submit);

        }catch (Exception e){
        }
    }
    public static void Count_Card(){
        try{
            JSONObject Submit = Save_Input_Data.has("summary") ? Save_Input_Data.getJSONObject("summary") : new JSONObject();
            Submit.put("card",Submit.optInt("card")+1);
            Save_Input_Data.put("summary",Submit);

            Submit = Save_Input_Data.has("today") ? Save_Input_Data.getJSONObject("today") : new JSONObject();
            Submit.put("card",Submit.optInt("card")+1);
            Save_Input_Data.put("today",Submit);

        }catch (Exception e){
        }
    }
    public static void Count_Voice(){
        try{
            JSONObject Submit = Save_Input_Data.has("summary") ? Save_Input_Data.getJSONObject("summary") : new JSONObject();
            Submit.put("voice",Submit.optInt("voice")+1);
            Save_Input_Data.put("summary",Submit);

            Submit = Save_Input_Data.has("today") ? Save_Input_Data.getJSONObject("today") : new JSONObject();
            Submit.put("voice",Submit.optInt("voice")+1);
            Save_Input_Data.put("today",Submit);

        }catch (Exception e){
        }
    }public static void Count_Reply(){
        try{
            JSONObject Submit = Save_Input_Data.has("summary") ? Save_Input_Data.getJSONObject("summary") : new JSONObject();
            Submit.put("reply",Submit.optInt("reply")+1);
            Save_Input_Data.put("summary",Submit);

            Submit = Save_Input_Data.has("today") ? Save_Input_Data.getJSONObject("today") : new JSONObject();
            Submit.put("reply",Submit.optInt("reply")+1);
            Save_Input_Data.put("today",Submit);

        }catch (Exception e){
        }
    }

    public static void Save_File(){
        try{
            String s = Save_Input_Data.toString();

            int Time = Save_Input_Data.optInt("day");
            int Today = Calendar.getInstance().get(Calendar.DAY_OF_YEAR);

            if(Time!=Today){
                Save_Input_Data.put("day",Today);
                Save_Input_Data.put("today",new JSONObject());
            }


            FileUtils.WriteFileByte(ConfigPathBuilder.Get_Current_Path_Set() +"CountWord.json",s.getBytes(StandardCharsets.UTF_8));



        }catch (Exception e){

        }finally {
            new Handler(Looper.getMainLooper()).postDelayed(Handler_Count_Input_Data::Save_File,30 * 1000);
        }
    }

    public static void Count_ChatMessage(Object ChatMsg){
        try{
            boolean IsSend = MMethod.CallMethod(ChatMsg,"isSendFromLocal",boolean.class,new Class[0]);
            if(IsSend){
                Count_Line();
                String Class = ChatMsg.getClass().getName();
                if(Class.contains("MessageForReplyText")){
                    Count_Reply();
                }
                if(Class.contains("MessageForText") || Class.contains("MessageForLongTextMsg") || Class.contains("MessageForReplyText")){
                    String MessageContent = MField.GetField(ChatMsg, "msg",String.class);
                    Count_Text(MessageContent);
                }else if(Class.contains("MessageForPic")){
                    Count_Pic();
                }else if(Class.contains("MessageForStructing") || Class.contains("MessageForArkApp")){
                    Count_Card();
                }else if(Class.contains("MessageForPtt")){
                    Count_Voice();
                }
            }
        }catch (Exception e){

        }
    }
}
