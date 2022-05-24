package com.ldq.connect.FloatWindow;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.text.Html;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.MField;
import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.FormItem;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.ArrayList;
import java.util.List;

public class EmoHelper {
    public static void SearchAndAddItem(TitlePageView mPager)
    {
        File emoPath = new File(MHookEnvironment.PublicStorageModulePath + "Emo/");
        if(!emoPath.exists())emoPath.mkdirs();

        for(File f : emoPath.listFiles())
        {
            if(f.isDirectory() && !f.getName().startsWith("."))
            {
                mPager.AddItem(f.getName(),new EmoContainer(mPager.getContext(),f.getAbsolutePath()+"/",f.getName()));
            }
        }
    }

    public static void ShowSavePICDialog(String LocalPath,String OnLinePath,String md5)
    {
        File emoPath = new File(MHookEnvironment.PublicStorageModulePath + "Emo/");
        if(!emoPath.exists())emoPath.mkdirs();
        ArrayList<String> NameList = new ArrayList<>();
        for(File f : emoPath.listFiles())
        {
            if(f.isDirectory())
            {
                if(f.getName().startsWith("."))continue;
                NameList.add(f.getName());
            }
        }
        NameList.add("++添加一项++");

        String[] strItems = NameList.toArray(new String[0]);
        Activity context = Utils.GetThreadActivity();
        AlertDialog.Builder al =
        new AlertDialog.Builder(context,3);
        al.setItems(strItems, (dialog, which) -> {
            if(which==strItems.length-1)
            {
                EditText e = new EditText(context);
                e.setTextSize(20);
                new AlertDialog.Builder(context,3)
                        .setView(e)
                        .setTitle("输入名字")
                        .setPositiveButton("确定", (dialog1, which1) -> {
                            String Name = e.getText().toString();
                            if(TextUtils.isEmpty(Name))
                            {
                                Utils.ShowToast("名字不能为空");
                                return;
                            }

                            new File(MHookEnvironment.PublicStorageModulePath + "Emo/"+Name).mkdirs();
                            String SavePath = MHookEnvironment.PublicStorageModulePath + "Emo/"+Name+"/"+ md5;
                            if(!TextUtils.isEmpty(LocalPath))
                            {
                                FileUtils.copy(LocalPath,SavePath,4096);
                                Utils.ShowToast("已经保存到路径:"+SavePath);
                            }
                            else
                            {
                                HttpUtils.downlaodFile(OnLinePath,SavePath);
                                Utils.ShowToast("已经保存到路径:"+SavePath);
                            }
                            PagerHandler.FlushShow();
                        }).show();
            }else
            {
                String SavePath = MHookEnvironment.PublicStorageModulePath + "Emo/"+strItems[which]+"/"+ md5;
                if(!TextUtils.isEmpty(LocalPath))
                {
                    FileUtils.copy(LocalPath,SavePath,4096);
                    Utils.ShowToast("已经保存到路径:"+SavePath);
                }
                else
                {
                    HttpUtils.downlaodFile(OnLinePath,SavePath);
                    Utils.ShowToast("已经保存到路径:"+SavePath);
                }

            }
        }).show();
    }
    public static void MixedMsgSaveHelper(Object MixedMsg)
    {
        try{
            ArrayList<String> l = new ArrayList();
            ArrayList Elem = (ArrayList) MField.GetField(MixedMsg,"msgElemList", List.class);
            for(Object MessageRecord : Elem)
            {
                if(MessageRecord.getClass().getName().contains("MessageForPic"))
                {
                    l.add(MField.GetField(MessageRecord,"md5",String.class));
                }
            }
            if(l.size()==0)
            {
                Utils.ShowToast("该消息未包含任何图片");
            }else
            {
                if(l.size()==1)
                {
                    PreSavePic(l.get(0));
                }else
                {
                    Activity mContext = Utils.GetThreadActivity();
                    LinearLayout ls = new LinearLayout(mContext);
                    ls.setOrientation(LinearLayout.VERTICAL);
                    for(String md5 : l)
                    {
                        ls.addView(FormItem.AddListItem(mContext, md5, v -> PreSavePic(md5)));
                    }

                    new AlertDialog.Builder(mContext,3)
                            .setTitle("请选择你想要保存的图片")
                            .setView(ls)
                            .show();
                }
            }

        }catch (Exception e)
        {
            Utils.ShowToast("解析图片失败:\n"+e);
        }
    }
    private static void PreSavePic(String md5)
    {
        AlertDialog al = new AlertDialog.Builder(Utils.GetThreadActivity(),3)
                .setTitle("是否保存这张图片?")
                .setNegativeButton("保存", (dialog, which) -> {
                    ShowSavePICDialog("","https://gchat.qpic.cn/gchatpic_new/0/0-0-"+md5+"/0?term=2",md5);
                }).create();

        TextView mView = new TextView(al.getContext());
        mView.setBackgroundColor(Color.WHITE);
        //显示动态的关于信息,并且创建图像加载器,让其可以正常显示图片
        mView.setText(Html.fromHtml("<img src=\"https://gchat.qpic.cn/gchatpic_new/0/0-0-"+md5+"/0?term=2\"></img>", Html.FROM_HTML_MODE_LEGACY, source -> {
            final InputStream[] is = {null};
            final Drawable[] d = {null};
            try
            {
                Thread mThread = new Thread(() -> {
                    try {
                        is[0] = (InputStream) new URL(source).getContent();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    d[0] = Drawable.createFromStream(is[0], "src");
                    d[0].setBounds(0, 0, Utils.dip2px(al.getContext(),100), Utils.dip2px(al.getContext(),100));
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
        },null));
        mView.setMovementMethod(LinkMovementMethod.getInstance());
        al.setView(mView);
        al.show();


    }
}
