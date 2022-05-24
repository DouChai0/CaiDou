package com.ldq.connect.FloatWindow;

import android.app.Activity;
import android.content.Context;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.NameUtils;
import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.QQMessage_Builder;

import java.util.ArrayList;

import de.robv.android.xposed.XposedBridge;

public class EmoPreviewHolder extends ScrollView {
    ArrayList<String> DirectUrl;
    volatile boolean IsDestroy = false;

    ArrayList<LinearLayout> ImageContainer = new ArrayList<>();
    LinearLayout RootContainer;
    ArrayList<String> DownloadUrl;

    public EmoPreviewHolder(Context context, ArrayList<String> DirectImageList,ArrayList<String> TrueDownUrlList) {
        super(context);
        DirectUrl = new ArrayList<>(DirectImageList);
        DownloadUrl = TrueDownUrlList;
        RootContainer = new LinearLayout(context);
        RootContainer.setOrientation(LinearLayout.VERTICAL);
        addView(RootContainer);
        new Thread(this::ImagePreLoadThread).start();
    }
    public void ImageClick(String DLPath)
    {
        String LocalPath = MHookEnvironment.PublicStorageModulePath+"Cache/"+ NameUtils.getRandomString(16)+".image";
        HttpUtils.ProgressDownload(DLPath,LocalPath,()->{
            QQMessage.Message_Send_Pic(MHookEnvironment.CurrentSession, QQMessage_Builder.Build_Pic(MHookEnvironment.CurrentSession,LocalPath));
        },getContext());

    }
    public void Destory()
    {
        try{
            IsDestroy = true;
            for(LinearLayout l : ImageContainer)
            {
                for(int i=0;i<l.getChildCount();i++)
                {
                    View v = l.getChildAt(i);
                    if(v instanceof ImageView)
                    {
                        ((ImageView)v).getDrawable().setCallback(null);
                    }
                }
                l.destroyDrawingCache();
            }
            ImageContainer.clear();
            ImageContainer = null;
        }catch (Throwable th)
        {
            XposedBridge.log(th);
        }


    }

    public void ImagePreLoadThread()
    {
        Activity act = Utils.GetThreadActivity();
        for(int i=0;i<DirectUrl.size();i++)
        {
            if(IsDestroy) break;
            String Direct = DirectUrl.get(i);
            String Download = DownloadUrl.get(i);
            String LocalSavePath = MHookEnvironment.PublicStorageModulePath + "Cache/"+Direct.hashCode()+".preImage";

            HttpUtils.downlaodFile(Direct,LocalSavePath);
            if(IsDestroy) break;
            act.runOnUiThread(()->LoadImageFromPath(LocalSavePath,Download));

        }
    }
    int LoadedImageCount = 0;
    public void LoadImageFromPath(String LocalPath,String Download)
    {
        if((LoadedImageCount%4) ==0)
        {
            LinearLayout l = new LinearLayout(getContext());
            ImageView img = new ImageView(getContext());
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Drawable dr = Drawable.createFromPath(LocalPath);
            img.setImageDrawable(dr);
            img.setTag(LocalPath);
            img.setOnClickListener(v->ImageClick(Download));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(getContext(),60), Utils.dip2px(getContext(),60));
            params.setMargins(30,30,0,0);
            l.addView(img,params);
            ImageContainer.add(l);
            RootContainer.addView(l);

            LoadedImageCount++;
        }else
        {
            LinearLayout l = ImageContainer.get(ImageContainer.size()-1);
            ImageView img = new ImageView(getContext());
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            Drawable dr = Drawable.createFromPath(LocalPath);
            img.setImageDrawable(dr);
            img.setTag(LocalPath);
            img.setOnClickListener(v->ImageClick(Download));

            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(getContext(),60), Utils.dip2px(getContext(),60));
            params.setMargins(30,30,0,0);
            l.addView(img,params);
            LoadedImageCount++;
        }
    }
}
