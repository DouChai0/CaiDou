package com.ldq.connect.ServerTool.SpringActHelper;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Looper;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.VideoView;

import com.ldq.Utils.MLogCat;
import com.ldq.Utils.SMToolHelper;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.GlobalConfig;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQTools;
import com.ldq.connect.ServerTool.UserStatus;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;

public class ShowSpringDialog {
    public static void PreStartDialog(){
        if (!GlobalConfig.Get_Boolean("IsSeen_Activity_Tip",false)){
            Activity act = Utils.GetThreadActivity();
            AlertDialog newAlert = new AlertDialog.Builder(act,3)
                    .setTitle("写在前面")
                    .setMessage("你即将参与答题活动,请阅读一下几点说明,10秒后可关闭\n" +
                            "1.答题为10分钟一题,中途可随意退出进入,不会自动提交答案\n" +
                            "2.每个人在十分钟之内只能提交一次答案,同一个设备/QQ/IP都视为同一个人,提交后会马上得到是否正确的提示\n" +
                            "3.第一个回答正确的人可以获得一份奖励,同时后面的人停止作答,并可以查看此人以及之前所有人的答题情况\n" +
                            "4.若10分钟无人答出,则该题结束,进入下一题的答题过程\n" +
                            "5.如果回答正确却没有获得奖励,请用 答题的QQ 联系作者获得帮助\n" +
                            "6.活动过程如果发生捣乱/攻击等情况,将对相关人员进行拉黑处理")
                    .setCancelable(false)
                    .setOnDismissListener(dialog -> {
                        GlobalConfig.Put_Boolean("IsSeen_Activity_Tip",true);
                        StartChecker();
                    })
                    .show();

            new Handler(Looper.getMainLooper()).postDelayed(()->{
                newAlert.setCancelable(true);
            },10000);
        }else {
            StartChecker();
        }

    }
    @SuppressLint("ResourceType")
    public static void StartChecker(){
        Activity act = Utils.GetThreadActivity();
        Dialog fullScreenDialog = new Dialog(act,3);

        ScrollView sc = new ScrollView(act);
        sc.setBackgroundColor(Color.WHITE);
        LinearLayout mRoot = new LinearLayout(act);
        sc.addView(mRoot);
        mRoot.setOrientation(LinearLayout.VERTICAL);
        mRoot.setBackgroundColor(Color.WHITE);

        LinearLayout bodyLinear = new LinearLayout(act);
        bodyLinear.setOrientation(LinearLayout.VERTICAL);
        mRoot.addView(bodyLinear);

        EditText inputBox = new EditText(act);
        inputBox.setHint("这里输入回答的答案");
        mRoot.addView(inputBox);

        LinearLayout endToolBar = new LinearLayout(act);
        endToolBar.setOrientation(LinearLayout.VERTICAL);

        Button btnFlush = new Button(act);
        btnFlush.setText("刷新题目");
        btnFlush.setOnClickListener(v->{
            String NewTitle = SMToolHelper.GetDataFromServer("Get_Title","0");
            try {
                JSONObject NewJson = new JSONObject(NewTitle);
                int code = NewJson.optInt("code");
                if (code == 1){
                    Utils.ShowToast(NewJson.optString("msg"));
                }else if (code == 2){
                    UpdateTitleInfo(bodyLinear,NewJson.getJSONObject("title").toString());
                }else {
                    Utils.ShowToast("未知错误:"+code);
                }
            } catch (Exception e) {
                Utils.ShowToast("网络异常");
            }
        });
        endToolBar.addView(btnFlush);

        Button btnSubmit = new Button(act);
        btnSubmit.setText("提交答案");
        btnSubmit.setOnClickListener(v->{
            try{
                JSONObject SubMit = new JSONObject();
                SubMit.put("TitleID",QUID);
                SubMit.put("Ans",inputBox.getText());
                SubMit.put("DevID", UserStatus.GetCurrentDevID());
                SubMit.put("Uin", BaseInfo.GetCurrentUin());
                SubMit.put("SubmitTime",System.currentTimeMillis());
                SubMit.put("Name",BaseInfo.getSelfName());

                String ret = SMToolHelper.GetDataFromServer("Ans_Submit",SubMit.toString());
                JSONObject retJson = new JSONObject(ret);
                int code = retJson.optInt("code");
                if (code == 1){
                    Utils.ShowToast(retJson.optString("msg"));
                }else if (code == 2){
                    Utils.ShowToast("恭喜你成为本题第一个回答正确的人");
                    QQTools.OpenInQQ(retJson.getString("HBPath"));
                    //QQMessage_Transform.Message_Send_Text("",BaseInfo.GetCurrentUin(),retJson.getString("HBPath"),new ArrayList());
                }else {
                    Utils.ShowToast("未知错误:"+code);
                }
            }catch (Exception e){
                Utils.ShowToast("错误:"+e);
            }
        });

        endToolBar.addView(btnSubmit);

        Button btnShowT = new Button(act);
        btnShowT.setText("查看答题情况");
        endToolBar.addView(btnShowT);
        mRoot.addView(endToolBar);
        btnShowT.setOnClickListener(vx->{
            String result = SMToolHelper.GetDataFromServer("Get_List","1");
            try{
                JSONArray mArray = new JSONArray(result);
                ArrayList<String> shwoName = new ArrayList<>();
                for(int i=0;i<mArray.length();i++){
                    JSONObject obj = mArray.getJSONObject(i);
                    shwoName.add(obj.getString("title"));
                }
                new AlertDialog.Builder(vx.getContext(),3)
                        .setTitle("选择要查看的题目")
                        .setItems(shwoName.toArray(new String[0]), (dialog, which) -> {
                            try {
                                JSONObject obj = mArray.getJSONObject(which);
                                String key = obj.getString("key");

                                String keyEnd = SMToolHelper.GetDataFromServer("Get_Answers",key);

                                JSONArray NewArr = new JSONArray(keyEnd);
                                StringBuilder showText = new StringBuilder();
                                for (int i = 0;i<NewArr.length();i++){
                                    JSONObject item = NewArr.getJSONObject(i);
                                    showText.append(item.getString("Name"))
                                            .append("(").append(item.getString("uin")).append(")")
                                            .append("->").append(item.getString("ans")).append("\n");
                                }

                                new AlertDialog.Builder(vx.getContext(),3)
                                        .setTitle("该问题的回答情况")
                                        .setMessage(showText)
                                        .setNegativeButton("确定", (dialog1, which1) -> {

                                        }).show();
                            } catch (JSONException e) {
                                Utils.ShowToast("发生错误:"+e);
                            }

                        }).show();
            }catch (Exception e){
                Utils.ShowToast("发生错误："+e);
            }

        });

        fullScreenDialog.setContentView(sc);
        fullScreenDialog.show();
        Utils.SetSecureFlags(fullScreenDialog.getWindow());
    }
    static String QUID;
    public static void UpdateTitleInfo(LinearLayout contentView,String jsonData){
        MLogCat.Print_Debug(jsonData);
        contentView.removeAllViews();
        try{
            JSONObject NewQus = new JSONObject(jsonData);
            int RetCode = NewQus.optInt("code");
            if (RetCode == 1){
                Utils.ShowToast("提示:"+NewQus.optString("msg"));
            }else if (RetCode == 2){
                int qusType = NewQus.getInt("type");
                QUID = NewQus.getString("id");
                if (qusType == 1){
                    contentView.addView(getCommonTextTitleItem(contentView.getContext(),NewQus.getString("text")));
                }else if (qusType == 2){
                    contentView.addView(getPicItemView(contentView.getContext(),NewQus.getString("text"),NewQus.getString("extra")));
                }else if (qusType == 3){
                    contentView.addView(getVoiceItemView(contentView.getContext(),NewQus.getString("text"),NewQus.getString("extra")));
                }else if (qusType == 4){
                    contentView.addView(getVideoItemView(contentView.getContext(),NewQus.getString("text"),NewQus.getString("extra")));
                }

            }
        }catch (Exception e){
            Utils.ShowToast("发生错误:"+e);
        }

    }
    public static View getCommonTextTitleItem(Context context,String s){
        TextView textView = new TextView(context);
        textView.setTextSize(24);
        textView.setTextColor(Color.BLACK);
        textView.setText(s);
        return textView;
    }
    public static View getPicItemView(Context context,String s,String pic){
        TextView mView = new TextView(context);
        mView.setBackgroundColor(Color.WHITE);
        String richText = "<h1>"+s+"</h1><img src=\""+pic+"\"></img>";
        mView.setText(Html.fromHtml(richText, Html.FROM_HTML_MODE_LEGACY, source -> {
            if(source.startsWith("http")){
                final InputStream[] is = {null};
                final Drawable[] d = {null};
                try
                {
                    MLogCat.Print_Debug(source);
                    Thread mThread = new Thread(() -> {
                        try {
                            is[0] = (InputStream) new URL(source).getContent();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                        d[0] = Drawable.createFromStream(is[0], "src");
                        d[0].setBounds(0, 0, Utils.dip2px(context,128), Utils.dip2px(context,128));
                        try {
                            is[0].close();
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    });
                    mThread.start();
                    mThread.join();
                    return d[0];
                }
                catch(Exception e)
                {
                    return null;
                }
            }else {
                return Drawable.createFromPath(source);
            }

        },null));
        mView.setMovementMethod(LinkMovementMethod.getInstance());
        return mView;
    }
    public static View getVoiceItemView(Context context,String s,String voiceURL){
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);
        TextView title = (TextView) getCommonTextTitleItem(context,s);
        l.addView(title);

        LinearLayout toolBar = new LinearLayout(context);
        Button play = new Button(context);
        play.setText("播放");
        toolBar.addView(play);

        Button end = new Button(context);
        end.setText("停止");
        toolBar.addView(end);
        MediaPlayer player = new MediaPlayer();
        play.setOnClickListener(v->{
            try{
                MLogCat.Print_Debug(voiceURL);
                player.setDataSource(voiceURL);
                player.prepare();
                player.setLooping(false);
                player.start();

            }catch (Exception e){
                Utils.ShowToast("播放失败:"+e);
            }
        });

        end.setOnClickListener(v->{
            if (player.isPlaying()){
                player.stop();
            }
        });

        l.addView(toolBar);
        return l;
    }
    public static View getVideoItemView(Context context,String s,String VideoURL){
        try{
            LinearLayout mRoot = new LinearLayout(context);
            mRoot.setOrientation(LinearLayout.VERTICAL);

            mRoot.addView(getCommonTextTitleItem(context,s+"(如果无法播放可手动打开网址:"+VideoURL+")"));


            VideoView video = new VideoView(context);


            mRoot.addView(video,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT));

            LinearLayout toolBar = new LinearLayout(context);
            Button play = new Button(context);
            play.setText("播放");
            toolBar.addView(play);

            Button end = new Button(context);
            end.setText("停止");
            toolBar.addView(end);
            play.setOnClickListener(v->{
                try{
                    MLogCat.Print_Debug(VideoURL);
                    video.setVideoPath(VideoURL);
                    video.requestFocus();
                    video.start();
                }catch (Exception e){
                    Utils.ShowToast("播放失败:"+e);
                }
            });

            end.setOnClickListener(v->{
                if (video.isPlaying()){
                    video.pause();
                }
            });
            mRoot.addView(toolBar);
            return mRoot;


        }catch (Exception e){
            Utils.ShowToast("无法加载视频:"+VideoURL);
            return new LinearLayout(context);
        }

    }
}
