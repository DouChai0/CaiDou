package com.ldq.connect.FloatWindow;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Voice_Record_Item_Builder;
import com.ldq.connect.QQUtils.QQMessage_Transform;
import com.ldq.connect.QQUtils.QQSessionUtils;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;

public class ConvertToVoice extends ScrollView implements ViewPager.RequestShow {
    LinearLayout RootView;

    LinearLayout CheckBox;
    EditText InputText;

    static TextToSpeech tSpeech = null;
    EditText InputIDText;
    public ConvertToVoice(Context context) {
        super(context);
        RootView = new LinearLayout(context);
        addView(RootView);
        RootView.setOrientation(LinearLayout.VERTICAL);
        TextView textView = new TextView(context);
        textView.setTextSize(20);
        textView.setTextColor(Color.BLACK);
        textView.setText("输入要转换的文字:");
        RootView.addView(textView);


        InputText = new EditText(context);
        RootView.addView(InputText);

        CheckBox = new LinearLayout(context);

        Button BtnTry = new Button(context);
        BtnTry.setText("试听");
        BtnTry.setOnClickListener(v->{
            if(!InitSuccess)return;
            String Path = OutputToCache(InputText.getText().toString());
            PlayVoice(Path);
        });
        CheckBox.addView(BtnTry);

        Button BtnSave = new Button(context);
        BtnSave.setText("保存");
        BtnSave.setOnClickListener(v->{
            if(!InitSuccess)return;
            String Path = OutputToCache(InputText.getText().toString());
            SaveVoiceFile(Path);
        });
        CheckBox.addView(BtnSave);

        Button BtnSend = new Button(context);
        BtnSend.setText("发送");
        BtnSend.setOnClickListener(v->{
            if(!InitSuccess)return;
            String Path = OutputToCache(InputText.getText().toString());
            QQMessage_Transform.Group_Send_Ptt(QQSessionUtils.GetCurrentGroupUin(), QQSessionUtils.GetCurrentFriendUin(),Path);
        });
        CheckBox.addView(BtnSend);
        RootView.addView(CheckBox);

        TextView Show2 = new TextView(context);
        Show2.setText("在线讯飞配音");
        Show2.setTextColor(Color.BLACK);
        RootView.addView(Show2);

        EditText InputID = new EditText(context);
        InputID.setHint("输入配音ID,长按显示部分备选");
        InputID.setOnLongClickListener(new OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                ShowIDChoiceDialog();
                return true;
            }
        });
        RootView.addView(InputID);
        InputIDText = InputID;

        LinearLayout OnlineCheckBox = new LinearLayout(context);

        Button BtnTry2 = new Button(context);
        BtnTry2.setText("试听");
        BtnTry2.setOnClickListener(v->{
            Context context2 = Utils.GetThreadActivity();
            String Input = InputID.getText().toString();
            if(TextUtils.isEmpty(Input))
            {
                Utils.ShowToast("请输入ID");
                return;
            }
            String Link = OnlineTTSHelper.GetVoiceDownloadLink(Integer.parseInt(Input),"[te50][n0]"+InputText.getText().toString());
            String TmpPath = MHookEnvironment.PublicStorageModulePath + "Cache/"+Math.random();
            HttpUtils.ProgressDownload(Link,TmpPath,()->{
                PlayVoice(TmpPath);
            },context2);
        });
        OnlineCheckBox.addView(BtnTry2);

        Button BtnSave2 = new Button(context);
        BtnSave2.setText("保存");
        BtnSave2.setOnClickListener(v->{
            Context context2 = Utils.GetThreadActivity();
            String Input = InputID.getText().toString();
            if(TextUtils.isEmpty(Input))
            {
                Utils.ShowToast("请输入ID");
                return;
            }
            String Link = OnlineTTSHelper.GetVoiceDownloadLink(Integer.parseInt(Input),"[te50][n0]"+InputText.getText().toString());
            String TmpPath = MHookEnvironment.PublicStorageModulePath + "Cache/"+Math.random();
            HttpUtils.ProgressDownload(Link,TmpPath,()->{
                SaveVoiceFile(TmpPath);
            },context2);
        });
        OnlineCheckBox.addView(BtnSave2);

        Button BtnSend2 = new Button(context);
        BtnSend2.setText("发送");
        BtnSend2.setOnClickListener(v->{
            Context context2 = Utils.GetThreadActivity();
            String Input = InputID.getText().toString();
            if(TextUtils.isEmpty(Input))
            {
                Utils.ShowToast("请输入ID");
                return;
            }
            String Link = OnlineTTSHelper.GetVoiceDownloadLink(Integer.parseInt(Input),"[te50][n0]"+InputText.getText().toString());
            String TmpPath = MHookEnvironment.PublicStorageModulePath + "Cache/"+Math.random();
            HttpUtils.ProgressDownload(Link,TmpPath,()->{
                QQMessage_Transform.Group_Send_Ptt(QQSessionUtils.GetCurrentGroupUin(), QQSessionUtils.GetCurrentFriendUin(),TmpPath);
            },context2);
        });
        OnlineCheckBox.addView(BtnSend2);
        RootView.addView(OnlineCheckBox);


    }
    private void ShowIDChoiceDialog()
    {
        try {
            JSONObject mJson = new JSONObject(OnlineTTSHelper.SpeakerMaps);
            ArrayList<String> NameList = new ArrayList<>();
            JSONArray array = mJson.getJSONArray("data");
            for(int i=0;i<array.length();i++)
            {
                JSONObject obj = array.getJSONObject(i);
                NameList.add(obj.getString("name")
                        +"("+obj.getString("desc")
                        +"-"+obj.getString("style")+")"

                );
            }
            new AlertDialog.Builder(Utils.GetThreadActivity(),3)
                    .setItems(NameList.toArray(new String[0]), new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            try {
                                JSONObject json = array.getJSONObject(which);
                                InputIDText.setText(json.getString("id"));
                            } catch (JSONException e) {
                                e.printStackTrace();
                            }

                        }
                    }).show();
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }
    public void PlayVoice(String Path)
    {

        try {
            MediaPlayer mediaPlayer = new MediaPlayer();
            mediaPlayer.setDataSource(Path);
            mediaPlayer.setLooping(false);;
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }
    public String OutputToCache(String ConvertText)
    {
        HashMap<String, String> myHashRender = new HashMap<>();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,ConvertText);
        String RandomName = ""+Math.random()+".mp3";

        tSpeech.synthesizeToFile(ConvertText,myHashRender,MHookEnvironment.PublicStorageModulePath + "Cache/"+RandomName);
        Lock();

        return MHookEnvironment.PublicStorageModulePath + "Cache/"+RandomName;
    }

    volatile int LockCount = 0;
    private void Lock()
    {
        LockCount++;
        int  LockCounta =0;
        while (LockCount!=0)
        {
            LockCounta++;
            if(LockCounta>100)break;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    public void SaveVoiceFile(String Path)
    {
        AlertDialog dialog = new AlertDialog.Builder(getContext(),AlertDialog.THEME_HOLO_LIGHT).create();
        dialog.setTitle("请输入保存的文件名");
        EditText medit = new EditText(getContext());
        medit.setTextSize(22);
        medit.setTextColor(Color.BLACK);
        dialog.setView(medit);
        dialog.setButton(AlertDialog.BUTTON_NEUTRAL,"确定",(vvv,dx) -> {
            String Text = medit.getText().toString();
            String PttPath = null;
            try {
                PttPath = Path;
                String FilePath = MHookEnvironment.PublicStorageModulePath + "Voice/";
                if(!new File(FilePath).exists()) new File(FilePath).mkdirs();
                FilePath = FilePath + Text;
                if(new File(FilePath).exists()) FilePath = FilePath+"-"+ Hook_Voice_Record_Item_Builder.GetTimeText();
                FileUtils.WriteFileByte(FilePath, FileUtils.ReadFileByte(PttPath));
                Utils.ShowToast("已保存到"+FilePath);
            } catch (Exception e) {
                Utils.ShowToast("保存发生错误"+e);
            }

        });
        dialog.show();
    }
    boolean InitSuccess = false;


    @Override
    public void requestShow() {
        tSpeech = new TextToSpeech(Utils.GetThreadActivity(),new TTSListener());
        tSpeech.setSpeechRate(1.0f);
        tSpeech.setPitch(1f);
        tSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                LockCount--;
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }

    @Override
    public void BackEvent() {

    }

    @Override
    public void requestExit() {

    }

    @Override
    public void requestOnClick() {

    }

    public class TTSListener implements TextToSpeech.OnInitListener
    {

        @Override
        public void onInit(int status) {
            if(status != TextToSpeech.SUCCESS)
            {
                Utils.ShowToast("TTS引擎初始化失败");
                CheckBox.setEnabled(false);
                InitSuccess = false;
            }else
            {
                CheckBox.setEnabled(true);
                InitSuccess = true;
            }
        }
    }
}
