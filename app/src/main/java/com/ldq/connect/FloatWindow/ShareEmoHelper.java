package com.ldq.connect.FloatWindow;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import androidx.annotation.NonNull;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.FileUtils;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.NameUtils;
import com.ldq.Utils.SMToolHelper;
import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.ServerTool.EncEnv;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.File;
import java.util.ArrayList;

public class ShareEmoHelper extends LinearLayout implements ViewPager.RequestShow {
    LinearLayout UinList;
    ScrollView scPicContainer;

    ScrollView scList;

    int Pages = 1;

    boolean PicShowMode = false;


    public ShareEmoHelper(@NonNull Context context) {
        super(context);
        scList = new ScrollView(context);
        UinList = new LinearLayout(context);
        UinList.setOrientation(LinearLayout.VERTICAL);
        scList.addView(UinList);

        addView(scList,new LayoutParams(Utils.dip2px(getContext(),300), Utils.dip2px(getContext(),300)));

        scPicContainer = new ScrollView(getContext());
    }

    public void FlushPage(){
        try{
            if(PicShowMode){

            }else{
                UinList.removeAllViews();
                String List = SMToolHelper.GetDataFromServer(EncEnv.Bundle_Get_List,"123456");
                //Utils.ShowToast(List);
                JSONObject Items = new JSONObject(List);
                JSONArray ItemList = Items.getJSONArray("data");
                for(int i=0;i<ItemList.length();i++){
                    JSONObject BundleInfo = ItemList.getJSONObject(i);
                    String Name = BundleInfo.getString("Name");
                    String Uin = BundleInfo.getString("Uin");
                    String BundleID = BundleInfo.getString("BundleID");
                    long AllSize = BundleInfo.getLong("Size");
                    int PicCount = BundleInfo.getInt("PicCount");
                    TextView NewView = new TextView(getContext());
                    NewView.setTextColor(Color.BLACK);
                    NewView.setText("["+Uin+"]"+Name);
                    NewView.setTextSize(20);
                    NewView.setOnClickListener(v->{
                        new AlertDialog.Builder(getContext(),3)
                                .setTitle("选择操作")
                                .setMessage("当前表情集大小"+ DataUtils.bytes2kb(AllSize)+",上传者账号为:"+Uin+",共有图片"+
                                        PicCount+"张,选择是预览还是直接下载")
                                .setNeutralButton("预览图片", (dialog, which) -> {
                                    ShowPrePicView(BundleID);
                                }).setNegativeButton("下载图集", (dialog, which) -> {
                                    Request_Download(BundleID,Name);
                                }).show();
                    });
                    UinList.addView(NewView);
                }
                UinList.requestLayout();
                UinList.invalidate();
            }
        }catch (Exception e){
            Utils.ShowToast("发生错误:"+e);
        }

    }
    public void ShowPrePicView(String BundleID){
        try{
            String BundlePicList = SMToolHelper.GetDataFromServer(EncEnv.Bundle_Get_Content,BundleID);
            JSONObject Result = new JSONObject(BundlePicList);
            JSONArray PicList = Result.getJSONArray("data");
            ArrayList<String> thumbs = new ArrayList<>();
            ArrayList<String> DownUrl = new ArrayList<>();
            for(int i=0;i<PicList.length();i++){
                JSONObject NewItem = PicList.getJSONObject(i);
                String thumbPath = MHookEnvironment.ServerRoot_CDN + NewItem.getString("thumbURI");
                String PicPath = MHookEnvironment.ServerRoot_CDN + NewItem.getString("downURI");

                if(TextUtils.isEmpty(thumbPath)){
                    thumbs.add(PicPath);
                }else{
                    thumbs.add(thumbPath);
                }
                DownUrl.add(PicPath);
            }
            removeAllViews();

            EmoPreviewHolder preView = new EmoPreviewHolder(getContext(),thumbs,DownUrl);
            if(scPicContainer.getChildCount()!=0)scPicContainer.removeAllViews();
            scPicContainer.addView(preView);

            addView(scPicContainer, Utils.dip2px(getContext(),300), Utils.dip2px(getContext(),300));
            PicShowMode = true;
        }catch (Exception e){
            Utils.ShowToast("发生错误:"+e);
        }

    }
    public void Request_Download(String BundleID,String BundleName){
        EditText edText = new EditText(getContext());
        edText.setText(BundleName);
        new AlertDialog.Builder(getContext(),3)
                .setTitle("请输入保存的分类名字")
                .setView(edText)
                .setNegativeButton("确定下载", (dialog, which) -> {
                    ProgressDialog prog = new ProgressDialog(getContext(),3);
                    prog.setTitle("正在下载.");
                    prog.setCancelable(false);
                    prog.show();
                    String SavePath = edText.getText().toString();
                    String Path = MHookEnvironment.PublicStorageModulePath+"Emo/"+SavePath+"/";
                    if(!new File(Path).exists())new File(Path).mkdirs();
                    new Thread(()->{
                        try{
                            new Handler(Looper.getMainLooper()).post(()->prog.setMessage("正在获取图集信息..."));
                            String BundlePicList = SMToolHelper.GetDataFromServer(EncEnv.Bundle_Get_Content,BundleID);
                            JSONObject Result = new JSONObject(BundlePicList);
                            JSONArray PicList = Result.getJSONArray("data");

                            for(int i=0;i<PicList.length();i++){
                                JSONObject NewItem = PicList.getJSONObject(i);
                                int finalI = i;
                                new Handler(Looper.getMainLooper()).post(()->prog.setMessage("正在下载:"+(finalI +1)+"/"+PicList.length()));
                                String DownUrl = MHookEnvironment.ServerRoot_CDN+NewItem.getString("downURI");

                                String CachePath = MHookEnvironment.PublicStorageModulePath+"Cache/"+ NameUtils.getRandomString(16);
                                HttpUtils.downlaodFile(DownUrl,CachePath);

                                String MD5 = DataUtils.getFileMD5(new File(CachePath));
                                FileUtils.copy(CachePath,Path+MD5,1024);
                            }
                            Utils.ShowToast("下载完成");
                            new Handler(Looper.getMainLooper()).post(()->Init.FlushAll());

                        }catch (Exception e){

                        }finally {
                            new Handler(Looper.getMainLooper()).post(()->prog.dismiss());
                        }
                        }).start();
                }).show();
    }


    @Override
    public void requestShow() {
        FlushPage();
    }

    @Override
    public void BackEvent() {
        if(PicShowMode){
            PicShowMode = false;
            removeAllViews();
            addView(scList,new LayoutParams(Utils.dip2px(getContext(),300), Utils.dip2px(getContext(),300)));
            FlushPage();
        }
    }

    @Override
    public void requestExit() {

    }

    @Override
    public void requestOnClick() {

    }
}
