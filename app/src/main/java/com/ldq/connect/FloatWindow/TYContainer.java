package com.ldq.connect.FloatWindow;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookInstance.HookRecallMsg;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseCall;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.FormItem;
import com.ldq.connect.QQUtils.MessageRecoreFactory;
import com.ldq.connect.QQUtils.QQSessionUtils;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.io.ObjectOutputStream;

public class TYContainer extends ScrollView implements ViewPager.RequestShow{
    LinearLayout sRootLayout;
    public TYContainer(Context context) {
        super(context);
        sRootLayout = new LinearLayout(context);
        sRootLayout.setOrientation(LinearLayout.VERTICAL);
        addView(sRootLayout);
    }
    private void FlushShow()
    {
        File f = new File(MHookEnvironment.PublicStorageModulePath + "涂鸦保存/");
        if(!f.exists())f.mkdirs();
        for(File fs : f.listFiles())
        {
            if(fs.isFile())
            {
                View s = FormItem.AddListItem(getContext(), fs.getName(), v -> {
                    try {
                        HookRecallMsg.TYSave mSave = null;

                        String CachePath = "";
                        int GIFId = 0;

                        try{
                            ObjectInputStream oInp = new ObjectInputStream(new FileInputStream(fs));
                            mSave = (HookRecallMsg.TYSave) oInp.readObject();
                            CachePath = Environment.getExternalStorageDirectory()+
                                    "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/Scribble/ScribbleCache/" + DataUtils.getDataMD5(mSave.sData);
                            FileUtils.WriteFileByte(CachePath,mSave.sData);
                            FileUtils.WriteFileByte(CachePath+"_data",mSave.sShowData);
                            GIFId = mSave.GIFId;
                        }catch (Exception ex)
                        {
                            MLogCat.Print_Error("TYContainer",Log.getStackTraceString(ex));
                            CachePath = Environment.getExternalStorageDirectory()+
                                    "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/Scribble/ScribbleCache/"+ DataUtils.getFileMD5(fs);
                            FileUtils.copy(fs.getAbsolutePath(),CachePath,4096);

                        }


                        Object TYRecord;
                        if(BaseInfo.IsCurrentGroup())
                        {
                            TYRecord= MessageRecoreFactory.mBuildTYMessage(mSave, BaseInfo.GetCurrentGroupUin(),"",CachePath);
                        }
                        else
                        {
                            TYRecord= MessageRecoreFactory.mBuildTYMessage(mSave, QQSessionUtils.GetCurrentGroupUin(), QQSessionUtils.GetCurrentFriendUin(),CachePath);
                        }

                        BaseCall.AddAndSendMsg(TYRecord);
                        MLogCat.Print_Info("TYSend","Path="+CachePath);
                    } catch (Exception e) {
                        MLogCat.Print_Error("TYBuildSend",e);
                        Utils.ShowToast("发送失败");
                    }
                });
                s.setOnLongClickListener(v -> {
                    Context context = Utils.GetThreadActivity();
                    LinearLayout ll = new LinearLayout(context);
                    ll.setOrientation(LinearLayout.VERTICAL);


                    TextView tx = new TextView(context);
                    tx.setText("需要显示的名字");
                    tx.setTextColor(Color.BLACK);
                    ll.addView(tx);

                    EditText ed = new EditText(context);
                    ed.setText(fs.getName());
                    ll.addView(ed);

                    new AlertDialog.Builder(context,3)
                            .setTitle("选择一项操作")
                            .setView(ll).setNeutralButton("尝试恢复图像描述数据", (dialog, which) -> {
                                try{
                                    HookRecallMsg.TYSave mSave = null;
                                    ObjectInputStream oInp = new ObjectInputStream(new FileInputStream(fs));
                                    mSave = (HookRecallMsg.TYSave) oInp.readObject();

                                    String MD5 = DataUtils.getDataMD5(mSave.sData);

                                    String CachePath = Environment.getExternalStorageDirectory()+
                                            "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/Scribble/ScribbleCache/" +MD5+"_data";

                                    if(new File(CachePath).exists())
                                    {
                                        byte[] EmDat = FileUtils.ReadFileByte(CachePath);
                                        mSave.sShowData = EmDat;

                                        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
                                        ObjectOutputStream objOut = new ObjectOutputStream(bOut);
                                        objOut.writeObject(mSave);
                                        objOut.close();

                                        FileUtils.WriteFileByte(fs.getAbsolutePath(),bOut.toByteArray());
                                        Utils.ShowToast("已恢复描述数据");
                                    }else
                                    {
                                        Utils.ShowToast("未找到描述数据");
                                    }
                                }catch (Exception ex)
                                {
                                    Utils.ShowToast("文件损坏");

                                }
                            })
                            .show();
                    return true;
                });
                sRootLayout.addView(s,new LinearLayout.LayoutParams(Utils.dip2px(getContext(),300), ViewGroup.LayoutParams.WRAP_CONTENT));
            }
        }
    }


    @Override
    public void requestShow() {
        FlushShow();
    }

    @Override
    public void BackEvent() {

    }

    @Override
    public void requestExit() {
        sRootLayout.removeAllViews();
    }

    @Override
    public void requestOnClick() {

    }
}
