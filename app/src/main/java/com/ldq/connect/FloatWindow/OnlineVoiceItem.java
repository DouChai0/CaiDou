package com.ldq.connect.FloatWindow;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.os.Environment;
import android.util.Log;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.FileUtils;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.NameUtils;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookInstance.HookRecallMsg;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Voice_Record_Item_Builder;
import com.ldq.connect.QQUtils.BaseCall;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.MessageRecoreFactory;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.QQMessage_Builder;
import com.ldq.connect.QQUtils.QQSessionUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;

public class OnlineVoiceItem extends LinearLayout{
    TextView title;
    LinearLayout mClickButton;

    String show;
    String mFileID;
    OnlineVoiceHolder mHolder;

    String Uploader;

    boolean IsTuya;
    public OnlineVoiceItem(Context context, String ShowText, String DownloadUrl,String uin, boolean isDic, OnlineVoiceHolder holder) {
        this(context,ShowText,DownloadUrl,uin,isDic,holder,false);
    }

    public OnlineVoiceItem(Context context, String ShowText, String FileID,String uin, boolean isDic, OnlineVoiceHolder holder,boolean IsTuya) {
        super(context);
        mHolder = holder;
        this.IsTuya = IsTuya;
        if(isDic)
        {
            show = ShowText;

            this.mFileID = FileID;
            Uploader = uin;

            setOrientation(VERTICAL);
            title = new TextView(context);
            title.setText("[用户]"+show);
            title.setTextColor(Color.BLACK);
            title.setTextSize(22);
            addView(title);


            title.setOnClickListener(v->{
                mHolder.ShowUin(Uploader);
            });

        }
        else
        {
            show = ShowText;

            this.mFileID = FileID;
            Uploader = uin;

            setOrientation(VERTICAL);
            title = new TextView(context);
            title.setText("[文件]"+show);
            title.setTextColor(Color.BLACK);
            title.setTextSize(22);
            addView(title);

            mClickButton = new LinearLayout(context);
            mClickButton.setVisibility(GONE);
            addView(mClickButton);

            title.setOnClickListener(v->{
                if(mClickButton.getVisibility()==GONE)
                {
                    mClickButton.setVisibility(VISIBLE);
                }else{
                    mClickButton.setVisibility(GONE);
                }
            });

            CreateButtons();
        }

    }

    private final int sroke_width = 3;
    @Override
    protected void onDraw(Canvas canvas) {
        Paint paint = new Paint();
        paint.setColor(Color.BLACK);
        canvas.drawLine(0, 0, this.getWidth() - sroke_width, 0, paint);
        canvas.drawLine(0, 0, 0, this.getHeight() - sroke_width, paint);
        canvas.drawLine(this.getWidth() - sroke_width, 0, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
        canvas.drawLine(0, this.getHeight() - sroke_width, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
        super.onDraw(canvas);
    }

    private void CreateButtons()
    {
        LayoutParams params = new LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.setMargins(30,0,0,0);
        TextView btnDelete = new TextView(getContext()){
            @Override
            protected void onDraw(Canvas canvas) {
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                canvas.drawLine(0, 0, this.getWidth() - sroke_width, 0, paint);
                canvas.drawLine(0, 0, 0, this.getHeight() - sroke_width, paint);
                canvas.drawLine(this.getWidth() - sroke_width, 0, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
                canvas.drawLine(0, this.getHeight() - sroke_width, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
                super.onDraw(canvas);
            }
        };
        btnDelete.setBackgroundColor(Color.GREEN);
        btnDelete.setText("发送");
        btnDelete.setHeight(Utils.dip2px(getContext(),30));
        btnDelete.setWidth(Utils.dip2px(getContext(),40));
        btnDelete.setTextColor(Color.RED);
        btnDelete.setTextSize(20);
        btnDelete.setOnClickListener(v-> ShowAndSendVoiceDialog(mFileID,Uploader,show,getContext(),IsTuya ? 2: 1));
        mClickButton.addView(btnDelete,params);




        btnDelete = new TextView(getContext()){
            @Override
            protected void onDraw(Canvas canvas) {
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                canvas.drawLine(0, 0, this.getWidth() - sroke_width, 0, paint);
                canvas.drawLine(0, 0, 0, this.getHeight() - sroke_width, paint);
                canvas.drawLine(this.getWidth() - sroke_width, 0, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
                canvas.drawLine(0, this.getHeight() - sroke_width, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
                super.onDraw(canvas);
            }
        };
        btnDelete.setBackgroundColor(Color.GREEN);
        btnDelete.setText("伪发送");
        btnDelete.setHeight(Utils.dip2px(getContext(),30));
        btnDelete.setWidth(Utils.dip2px(getContext(),40));
        btnDelete.setTextColor(Color.RED);
        btnDelete.setTextSize(20);
        btnDelete.setOnClickListener(vaa-> {

        });
        //mClickButton.addView(btnDelete,params);

        btnDelete = new TextView(getContext()){
            @Override
            protected void onDraw(Canvas canvas) {
                Paint paint = new Paint();
                paint.setColor(Color.BLACK);
                canvas.drawLine(0, 0, this.getWidth() - sroke_width, 0, paint);
                canvas.drawLine(0, 0, 0, this.getHeight() - sroke_width, paint);
                canvas.drawLine(this.getWidth() - sroke_width, 0, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
                canvas.drawLine(0, this.getHeight() - sroke_width, this.getWidth() - sroke_width, this.getHeight() - sroke_width, paint);
                super.onDraw(canvas);
            }
        };
        btnDelete.setHeight(Utils.dip2px(getContext(),30));
        btnDelete.setWidth(Utils.dip2px(getContext(),80));
        btnDelete.setTextColor(Color.RED);
        btnDelete.setTextSize(20);
        btnDelete.setBackgroundColor(Color.GREEN);

        if(!IsTuya)
        {
            btnDelete.setText("查看此人上传");
            btnDelete.setOnClickListener(v-> {
                mHolder.ShowUin(Uploader);
            });
        }else
        {
            btnDelete.setText("虚拟发送");
            btnDelete.setOnClickListener(v-> {
                String RndName = NameUtils.getRandomString(8);
                String DownloadUrl = Utils.FileIDGetDown(mFileID);
                String SavePath = MHookEnvironment.PublicStorageModulePath + "Cache/"+RndName;
                HttpUtils.ProgressDownload(DownloadUrl,
                        SavePath,()->{
                            try {
                                TYSend(QQSessionUtils.GetCurrentGroupUin(), QQSessionUtils.GetCurrentFriendUin(),SavePath,true);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },getContext());
            });
        }

        mClickButton.addView(btnDelete,params);
    }

    public static void ShowAndSendVoiceDialog(String FileID,String QQUin,String Desc,Context mContext,int Type)
    {
        AlertDialog al = new AlertDialog.Builder(mContext,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("选择")
                .create();
        al.setMessage("上传者:"+QQUin
                +"\n"+"名字:"+Desc
                +"\n"+"是保存还是发送?"
        );
        al.setButton(AlertDialog.BUTTON_NEUTRAL,"保存",(xx,ss) ->{
            String mSavePath = Desc;
            if(Type ==1)
            {
                String DownloadUrl = Utils.FileIDGetDown(FileID);
                if(new File(MHookEnvironment.PublicStorageModulePath + "Voice/"+mSavePath).exists())mSavePath = mSavePath+"-"+ Hook_Voice_Record_Item_Builder.GetTimeText();
                String finalMSavePath = mSavePath;
                HttpUtils.ProgressDownload(DownloadUrl,
                        MHookEnvironment.PublicStorageModulePath + "Voice/"+mSavePath,()-> Utils.ShowToast("已保存到"+MHookEnvironment.PublicStorageModulePath + "Voice/"+ finalMSavePath),mContext);
            }else
            {
                String DownloadUrl = Utils.FileIDGetDown(FileID);
                if(new File(MHookEnvironment.PublicStorageModulePath + "涂鸦保存/"+mSavePath).exists())mSavePath = mSavePath+"-"+ Hook_Voice_Record_Item_Builder.GetTimeText();
                String finalMSavePath = mSavePath;
                HttpUtils.ProgressDownload(DownloadUrl,
                        MHookEnvironment.PublicStorageModulePath + "涂鸦保存/"+mSavePath,()-> Utils.ShowToast("已保存到"+MHookEnvironment.PublicStorageModulePath + "涂鸦保存/"+ finalMSavePath),mContext);
            }


        });
        al.setButton(AlertDialog.BUTTON_NEGATIVE,"发送",(xx,ss) ->{
            if(Type==1)
            {
                String Name = NameUtils.getRandomString(8);
                String mSavePath = Environment.getExternalStorageDirectory() +  "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/"+ BaseInfo.GetCurrentUin()+"/ptt/"+Name;
                if(new File(mSavePath).exists()) {
                    try {
                        QQMessage.Group_Send_Ptt(QQMessage_Builder.Build_SessionInfo(QQSessionUtils.GetCurrentGroupUin(), QQSessionUtils.GetCurrentFriendUin()),mSavePath);
                    } catch (Exception e) {
                        Utils.ShowToast("发送失败");
                    }
                }
                else
                {
                    String DownloadUrl = Utils.FileIDGetDown(FileID);
                    HttpUtils.ProgressDownload(DownloadUrl,
                            mSavePath,()->{
                                try {
                                    QQMessage.Group_Send_Ptt(QQMessage_Builder.Build_SessionInfo(QQSessionUtils.GetCurrentGroupUin(), QQSessionUtils.GetCurrentFriendUin()),mSavePath);
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            },mContext);
                }
            }else
            {
                String RndName = NameUtils.getRandomString(8);
                String SavePath = MHookEnvironment.PublicStorageModulePath + "Cache/"+RndName;
                String DownloadUrl = Utils.FileIDGetDown(FileID);
                HttpUtils.ProgressDownload(DownloadUrl,
                        SavePath,()->{
                            try {
                                TYSend(QQSessionUtils.GetCurrentGroupUin(), QQSessionUtils.GetCurrentFriendUin(),SavePath,false);
                            } catch (Exception e) {
                                e.printStackTrace();
                            }
                        },mContext);
            }

        });
        al.show();
    }
    public void HideButton()
    {
        mClickButton.setVisibility(GONE);
    }

    public static void TYSend(String GroupUin,String UserUin,String TYPath,boolean VTSend)
    {
        try {
            HookRecallMsg.TYSave mSave = null;

            String CachePath = "";
            int GIFId = 0;

            try{
                ObjectInputStream oInp = new ObjectInputStream(new FileInputStream(TYPath));
                mSave = (HookRecallMsg.TYSave) oInp.readObject();
                CachePath = Environment.getExternalStorageDirectory()+
                        "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/Scribble/ScribbleCache/" + DataUtils.getDataMD5(mSave.sData);
                FileUtils.WriteFileByte(CachePath,mSave.sData);
                FileUtils.WriteFileByte(CachePath+"_data",mSave.sShowData);
                GIFId = mSave.GIFId;
            }catch (Exception ex)
            {
                MLogCat.Print_Error("OnlineVoiceItem",Log.getStackTraceString(ex));
                CachePath = Environment.getExternalStorageDirectory()+
                        "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/Scribble/ScribbleCache/"+ DataUtils.getFileMD5(new File(TYPath));
                FileUtils.copy(TYPath,CachePath,4096);

            }


            Object TYRecord = MessageRecoreFactory.mBuildTYMessage(mSave, GroupUin,UserUin,CachePath);



            if(VTSend)
            {
                BaseCall.AddMsg(TYRecord);
            }else
            {
                BaseCall.AddAndSendMsg(TYRecord);
            }

            MLogCat.Print_Info("TYSend","Path="+CachePath);
        } catch (Exception e) {
            MLogCat.Print_Error("TYBuildSend",e);
            Utils.ShowToast("发送失败");
        }
    }
}
