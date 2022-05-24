package com.ldq.connect.FloatWindow;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.text.TextUtils;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.SMToolHelper;
import com.ldq.Utils.Utils;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQMessage_Transform;
import com.ldq.connect.QQUtils.QQSessionUtils;
import com.ldq.connect.ServerTool.EncEnv;
import com.ldq.connect.ServerTool.UploadFileHelper;

import org.json.JSONObject;

import java.io.File;

public class VoiceItem extends LinearLayout {
    TextView title;
    LinearLayout mClickButton;

    String show;
    String SavePath;
    VoiceHolder mHolder;

    public VoiceItem(Context context, String ShowText, String Path, boolean isDic, VoiceHolder holder) {
        super(context);
        mHolder = holder;
        if(isDic)
        {
            show = ShowText;
            SavePath = Path;
            setOrientation(VERTICAL);
            title = new TextView(context);
            title.setText("[目录]"+show);
            title.setTextColor(Color.BLACK);
            title.setTextSize(22);
            addView(title);

            title.setOnClickListener(v->{
                mHolder.ChangeToFile(SavePath);//如果是目录被点击就刷新
            });

        }
        else
        {
            show = ShowText;

            SavePath = Path;

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
        btnDelete.setOnClickListener(v-> {
            File fCir = new File(SavePath);

            if(BaseInfo.IsCurrentGroup())
            {
                if (fCir.length() > 1024 * 1024){
                    Utils.ShowToast("当前语音大于1M,可能对方无法正常播放");
                }
                QQMessage_Transform.Group_Send_Ptt(BaseInfo.GetCurrentGroupUin(),"",SavePath);
            }
            else
            {
                QQMessage_Transform.Group_Send_Ptt(QQSessionUtils.GetCurrentGroupUin(), QQSessionUtils.GetCurrentFriendUin(),SavePath);
            }

        });
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
        btnDelete.setText("删除");
        btnDelete.setHeight(Utils.dip2px(getContext(),30));
        btnDelete.setWidth(Utils.dip2px(getContext(),40));
        btnDelete.setTextColor(Color.RED);
        btnDelete.setTextSize(20);
        btnDelete.setOnClickListener(v-> new AlertDialog.Builder(v.getContext(),3)
                .setTitle("是否删除语音")
                .setMessage("语音名字:"+show+"\n路径:"+SavePath+"\n是否删除?")
                .setNegativeButton("确定删除", (dialog, which) -> {
                    FileUtils.deleteFile(new File(SavePath));
                    mHolder.FlushList();
                }).setPositiveButton("不删除", (dialog, which) -> {

                }).show());

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
        btnDelete.setText("更名");
        btnDelete.setHeight(Utils.dip2px(getContext(),30));
        btnDelete.setWidth(Utils.dip2px(getContext(),40));
        btnDelete.setTextColor(Color.RED);
        btnDelete.setTextSize(20);
        btnDelete.setOnClickListener(v-> {
            EditText ed = new EditText(v.getContext());
            ed.setTextSize(26);
            ed.setTextColor(Color.BLACK);
            ed.setText(show);

            new AlertDialog.Builder(v.getContext(),3)
                    .setTitle("输入你想要修改的名字")
                    .setView(ed)
                    .setPositiveButton("确定", (dialog, which) -> {
                        File r = new File(SavePath);
                        r.renameTo(new File(r.getParentFile().getAbsolutePath(),ed.getText().toString()));
                        mHolder.FlushList();
                    }).show();
        });
        mClickButton.addView(btnDelete,params);

    }
    public void HideButton()
    {
        mClickButton.setVisibility(GONE);
    }
}
