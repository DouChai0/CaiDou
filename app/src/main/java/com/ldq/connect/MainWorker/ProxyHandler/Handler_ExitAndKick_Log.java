package com.ldq.connect.MainWorker.ProxyHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.Utils;
import com.ldq.connect.JavaPlugin.JavaPlugin;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.TroopManager;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.InputStream;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.concurrent.Executor;
import java.util.concurrent.Executors;

public class Handler_ExitAndKick_Log {
    static HashMap<String, JSONObject> TroopLogMap = new HashMap<>();
    static HashMap<String,Long> CacheUinSet = new HashMap<>();
    public static synchronized void _caller(String TroopUin,String UserUin,String OPUin){
        try{
            String sUinSet = TroopUin + "-" + UserUin;
            if (CacheUinSet.containsKey(sUinSet)){
                long Time = CacheUinSet.get(sUinSet);
                if (Time + 30 * 1000 > System.currentTimeMillis())return;
            }
            JavaPlugin.mTask.PostTask(()->JavaPlugin.BshOnExitTroop(TroopUin,UserUin,OPUin));

            CacheUinSet.put(sUinSet, System.currentTimeMillis());
            TryLoadTroopInfoFromDisk(TroopUin);
            JSONObject item = TroopLogMap.get(TroopUin);
            JSONArray itemArr = item.getJSONArray("UList");
            JSONObject NewItem = new JSONObject();
            NewItem.put("TroopUin",TroopUin);
            NewItem.put("UserUin",UserUin);
            NewItem.put("OPUin",OPUin);
            NewItem.put("UserName", TroopManager.GetMemberName(TroopUin,UserUin));
            NewItem.put("OPName",TroopManager.GetMemberName(TroopUin,OPUin));
            NewItem.put("Time",System.currentTimeMillis());
            itemArr.put(NewItem);
            item.put("UList",itemArr);
            TroopLogMap.put(TroopUin,item);

            FlushTroopDataToFile(TroopUin);
        }catch (Exception e){
            MLogCat.Print_Error("KickLog",e);
        }
    }
    public static void OpenLogView(String TroopUin){
        Activity act = Utils.GetThreadActivity();
        ScrollView sc = new ScrollView(act);
        sc.setBackgroundColor(Color.WHITE);
        LinearLayout lRoot = new LinearLayout(act);
        sc.addView(lRoot);
        lRoot.setBackgroundColor(Color.WHITE);
        lRoot.setOrientation(LinearLayout.VERTICAL);
        @SuppressLint("ResourceType") Dialog dialog = new Dialog(act,3);
        dialog.setContentView(sc);

        LinearLayout toolBar = new LinearLayout(act);
        Button loadSelect = new Button(act);
        loadSelect.setText("选择查看的日期");
        toolBar.addView(loadSelect);
        loadSelect.setOnClickListener(v->{
            File[] fileList = new File(MHookEnvironment.PublicStorageModulePath + "数据目录/退群记录/"+TroopUin+"/").listFiles();
            if (fileList == null){
                Utils.ShowToast("没有日志文件");
                return;
            }
            ArrayList<String> dateList = new ArrayList<>();
            for (File file : fileList){
                String FileName = file.getName();
                if (FileName.endsWith(".json")){
                    dateList.add(FileName.substring(0,FileName.length()-5));
                }
            }

            new AlertDialog.Builder(act,3)
                    .setTitle("选择查看的日期")
                    .setItems(dateList.toArray(new String[0]), (dialog1, which) -> {
                        lRoot.removeAllViews();
                        lRoot.addView(toolBar);
                        ShowCheckPath(MHookEnvironment.PublicStorageModulePath + "数据目录/退群记录/"+TroopUin+"/"+dateList.get(which)+".json",lRoot);
                    }).show();
        });

        lRoot.addView(toolBar);
        ShowCheckPath(MHookEnvironment.PublicStorageModulePath + "数据目录/退群记录/"+TroopUin+"/"+GetDayTime()+".json",lRoot);

        dialog.show();
    }
    private static void ShowCheckPath(String Path,LinearLayout lRoot){
        try{
            JSONObject readJson = new JSONObject(FileUtils.ReadFileString(Path));
            JSONArray UinItems = readJson.getJSONArray("UList");
            for (int i=0;i<UinItems.length();i++){
                try{
                    JSONObject item = UinItems.getJSONObject(i);
                    String OPUin = item.getString("OPUin");
                    String OPName = item.getString("OPName");
                    long Time = item.getLong("Time");
                    SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
                    String TimeLine;
                    if(OPUin.isEmpty() || Long.parseLong(OPUin) == 0){
                        TimeLine = format.format(new Date(Time)) + " 自己退群";
                    }else if (Long.parseLong(OPUin) == 1){
                        TimeLine = format.format(new Date(Time)) + " 未知原因离群";
                    }
                    else {
                        TimeLine = format.format(new Date(Time)) + " 被"+OPName+"("+OPUin+")踢出";
                    }
                    String UserUin = item.getString("UserUin");
                    String UserName = item.getString("UserName");
                    lRoot.addView(createViewItem(lRoot.getContext(),UserUin,UserName,TimeLine));
                }catch (Exception e){ }
            }
        }catch (Exception e){

        }
    }
    static Executor pool = Executors.newFixedThreadPool(16);
    @SuppressLint("ResourceType")
    private static View createViewItem(Context context, String UserUin, String Name, String SecLine){
        RelativeLayout relativeLayout = new RelativeLayout(context);


        RelativeLayout.LayoutParams aParam1 = new RelativeLayout.LayoutParams(Utils.dip2px(context,40),Utils.dip2px(context,40));

        ImageView Avatar = new ImageView(context);
        ImageAvatarLoader(Avatar,UserUin);
        relativeLayout.addView(Avatar,aParam1);
        Avatar.setId(159753);

        RelativeLayout.LayoutParams MainTitle = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        MainTitle.addRule(RelativeLayout.RIGHT_OF,159753);
        TextView tView = new TextView(context);
        tView.setTextSize(16);
        tView.setText(Name+"("+UserUin+")");
        tView.setId(15599);
        tView.setTextColor(Color.BLACK);
        relativeLayout.addView(tView,MainTitle);


        RelativeLayout.LayoutParams SubTitle = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        SubTitle.addRule(RelativeLayout.RIGHT_OF,159753);
        SubTitle.addRule(RelativeLayout.BELOW,15599);

        TextView SubTitleView = new TextView(context);
        SubTitleView.setText(SecLine);
        SubTitleView.setTextSize(10);
        relativeLayout.addView(SubTitleView,SubTitle);
        return relativeLayout;
    }
    /**线程加载网络中的头像**/
    private static void ImageAvatarLoader(ImageView image,String Uin){
        String OnlinePath = "https://q1.qlogo.cn/g?b=qq&nk="+Uin + "&s=160";
        pool.execute(()->{
            try {
                URL u = new URL(OnlinePath);
                InputStream st = u.openStream();
                Drawable dr = Drawable.createFromStream(st,Uin);
                new Handler(Looper.getMainLooper()).post(()->image.setImageDrawable(dr));
            } catch (Exception e) {
                e.printStackTrace();
            }
        });
    }
    private static void TryLoadTroopInfoFromDisk(String TroopUin){
        try{
            if(TroopLogMap.containsKey(TroopUin)){
                JSONObject j2 = TroopLogMap.get(TroopUin);
                String CacheDayTime = j2.getString("DayTime");
                String CurrentDayTime = GetDayTime();
                if(!CacheDayTime.equals(CurrentDayTime)){
                    JSONObject NewJson = new JSONObject();
                    NewJson.put("DayTime",CurrentDayTime);
                    NewJson.put("UList",new JSONArray());
                    TroopLogMap.put(TroopUin,NewJson);
                }
            }else {
                String Path = MHookEnvironment.PublicStorageModulePath + "数据目录/退群记录/"+TroopUin+"/"+GetDayTime()+".json";
                if (new File(Path).exists()){
                    JSONObject ReadJson = new JSONObject(FileUtils.ReadFileString(Path));
                    TroopLogMap.put(TroopUin,ReadJson);
                }else {
                    JSONObject NewJson = new JSONObject();
                    NewJson.put("DayTime",GetDayTime());
                    NewJson.put("UList",new JSONArray());
                    TroopLogMap.put(TroopUin,NewJson);
                }
            }
        }catch (Exception e){
            TroopLogMap.put(TroopUin,new JSONObject());
        }

    }
    private static void FlushTroopDataToFile(String TroopUin){
        if(TroopLogMap.containsKey(TroopUin)){
            try {
                JSONObject ItemJson = TroopLogMap.get(TroopUin);
                String DayTime = ItemJson.getString("DayTime");
                String Path = MHookEnvironment.PublicStorageModulePath + "数据目录/退群记录/"+TroopUin+"/"+DayTime+".json";
                File dicPath = new File(Path).getParentFile();
                if (!dicPath.exists())dicPath.mkdirs();

                FileUtils.WriteFileByte(Path,ItemJson.toString().getBytes(StandardCharsets.UTF_8));
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }
    private static String GetDayTime(){
        SimpleDateFormat format = new SimpleDateFormat("yyyyMMdd");
        return format.format(new Date());
    }
}
