package com.ldq.connect.MTool.QQCleaner;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Environment;
import android.os.Handler;
import android.os.Looper;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.FormItem;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

public class QQCleaner
{
    static ArrayList<mShowInfo> mSettings = new ArrayList<>();
    static
    {
        mSettings.add(getInfo("厘米秀图片缓存", Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/cache/cmshow/"));
        mSettings.add(getInfo("发送文件临时缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/cache/share/"));
        mSettings.add(getInfo("游戏中心下载安装包",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/com.tencent.gamecenter.wadl/"));
        mSettings.add(getInfo("QQ内拍照缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/files/ae/camera/capture/"));
        mSettings.add(getInfo("QQ钱包图片缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/files/QWallet/"));
        mSettings.add(getInfo("头像缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/files/tencent/MobileQQ/"));
        mSettings.add(getInfo("空间背景图片",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/qzone/facade/"));
        mSettings.add(getInfo("空间图片",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/qzone/imageV2/"));
        mSettings.add(getInfo("空间动画缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/qzone/zip_cache/"));
        mSettings.add(getInfo("diy名片缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/vas/lottie/"));
        mSettings.add(getInfo("表情图片缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/.emotionsm/"));
        mSettings.add(getInfo("头像挂件缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/.pendant/"));
        mSettings.add(getInfo("普通名片缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/.profilecard/"));
        mSettings.add(getInfo("推荐表情缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/.sticker_recommended_pics/"));
        mSettings.add(getInfo("群入场动画",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/.troop/enter_effects/"));
        mSettings.add(getInfo("vip标志",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/.vipicon/"));
        mSettings.add(getInfo("语音文件缓存(仅当前号码)",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/<QQNumber>/ptt/"));
        mSettings.add(getInfo("聊天图片",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/chatpic/"));
        mSettings.add(getInfo("图片缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/diskcache/"));
        mSettings.add(getInfo("头像缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/head/"));
        mSettings.add(getInfo("热门表情缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/hotpic/"));
        mSettings.add(getInfo("图片缓存2",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/photo/"));
        mSettings.add(getInfo("广告缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/qbosssplahAD/"));
        mSettings.add(getInfo("音乐缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/qqmusic/"));
        mSettings.add(getInfo("视频缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/shortvideo/"));
        mSettings.add(getInfo("缩略图缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/thumb/"));
        mSettings.add(getInfo("群头像缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/troopphoto/tmp/"));
        mSettings.add(getInfo("QQ收藏表情缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/QQ_Favorite/"));
        mSettings.add(getInfo("json卡片缓存",MHookEnvironment.AppPath+"/files/ArkApp/Cache/"));
        mSettings.add(getInfo("涂鸦缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/Scribble/"));
        mSettings.add(getInfo("标准缓存目录",MHookEnvironment.AppPath+"/com.tencent.mobileqq/cache/"));
        mSettings.add(getInfo("标准缓存目录+",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Cache/"));

        mSettings.add(getInfo("字体缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/.font_info/"));
        //mSettings.add(getInfo("挂件缓存","/storage/emulated/0/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/.pendant/"));
        mSettings.add(getInfo("厘米秀缓存",Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/.apollo/dress/"));
        mSettings.add(getInfo("聊天气泡缓存",MHookEnvironment.AppPath+"/files/bubble_info/"));
    }
    static mShowInfo getInfo(String showName,String path)
    {
        mShowInfo mInfo = new mShowInfo();
        mInfo.showName = showName;
        mInfo.CleanPath = path;
        return mInfo;
    }

    static class mShowInfo
    {
        String showName;
        String CleanPath;
        View mShowView;
        boolean checked;
    }
    static HashMap<String,mShowInfo> mItemList = new HashMap();
    @SuppressLint("ResourceType")
    public static void ShowDialog(Context mContext)
    {
        try{
            AlertDialog mDialog = new AlertDialog.Builder(mContext,3).create();

            TextView mTextview = new TextView(mContext);
            mTextview.setText("QQ缓存文件清理");
            mTextview.setTextColor(Color.parseColor("#008000"));
            mTextview.setTextSize(34);
            mTextview.setBackgroundColor(Color.parseColor("#00FFFF"));
            mTextview.setGravity(Gravity.CENTER);
            mDialog.setCustomTitle(mTextview);

            RelativeLayout mRelative = new RelativeLayout(mContext);
            mRelative.setId(1122334455);





            LinearLayout mLinear = new LinearLayout(mContext);



            Button mBtnClean = new Button(mContext);
            RelativeLayout.LayoutParams btnparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
            //btnparams.addRule(RelativeLayout.BELOW,1122334466);
            mBtnClean.setId(1234566);
            btnparams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,1);
            mBtnClean.setText("清理选中");
            mBtnClean.setOnClickListener(v -> {
                AlertDialog mClearprogress = new AlertDialog.Builder(v.getContext(),3).create();
                mClearprogress.setTitle("正在清理中....");
                mClearprogress.setCancelable(false);
                LinearLayout mLinear1 = new LinearLayout(v.getContext());
                mLinear1.setOrientation(LinearLayout.VERTICAL);
                mClearprogress.setView(mLinear1);
                TextView mShow1 = new TextView(v.getContext());
                mShow1.setText("正在清理:null");
                mShow1.setTextSize(12);
                mShow1.setTextColor(Color.BLACK);
                TextView mShow2 = new TextView(v.getContext());
                mShow2.setText("相关目录:null");
                mShow2.setTextSize(12);
                mShow2.setTextColor(Color.BLACK);
                mLinear1.addView(mShow1);
                mLinear1.addView(mShow2);
                new Thread(()->{
                    for(mShowInfo info : mSettings)
                    {
                        if(info.checked)
                        {
                            new Handler(Looper.getMainLooper()).post(()->mShow1.setText("正在清理:"+info.showName));
                            if(info.CleanPath.contains("<QQNumber>"))
                            {
                                new Handler(Looper.getMainLooper()).post(()->mShow2.setText("相关目录:"+info.CleanPath.replace("<QQNumber>", BaseInfo.GetCurrentUin())));
                                FileUtils.deleteFile(new File(info.CleanPath.replace("<QQNumber>", BaseInfo.GetCurrentUin())));
                            }
                            else
                            {
                                new Handler(Looper.getMainLooper()).post(()->mShow2.setText("相关目录:"+info.CleanPath));
                                FileUtils.deleteFile(new File(info.CleanPath));
                            }


                        }
                    }
                    new Handler(Looper.getMainLooper()).post(()->mClearprogress.dismiss());
                    new Handler(Looper.getMainLooper()).post(()->mDialog.dismiss());
                    Utils.ShowToast("清理完成");
                }).start();


                mClearprogress.show();
            });

            mRelative.addView(mBtnClean,btnparams);



            ScrollView TheItemView = new ScrollView(mContext);
            TheItemView.setId(1122334466);
            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);
            params.addRule(RelativeLayout.ABOVE,1234566);
            mRelative.addView(TheItemView,params);
            TheItemView.addView(mLinear);
            mLinear.setOrientation(LinearLayout.VERTICAL);
            long mIndexSize = FileUtils.getDirSize(MHookEnvironment.MAppContext.getDatabasePath(BaseInfo.GetCurrentUin()+"-IndexQQMsg.db"));;

            mIndexSize += FileUtils.getDirSize(MHookEnvironment.MAppContext.getDatabasePath("slowtable_"+ BaseInfo.GetCurrentUin()+".db"));;
            mIndexSize += FileUtils.getDirSize(MHookEnvironment.MAppContext.getDatabasePath(BaseInfo.GetCurrentUin()+".db"));;


            mLinear.addView(FormItem.AddMultiItem(mContext,"聊天记录(仅当前号码,不清理)", DataUtils.bytes2kb(mIndexSize)));
            for(int i=0;i<mSettings.size();i++)
            {
                mShowInfo tInfo = mSettings.get(i);
                View mView = FormItem.AddMultiItem(mContext,tInfo.showName,"正在计算",(buttonView, isChecked) -> {
                    mShowInfo mInfo = (mShowInfo) ((View)buttonView.getParent()).getTag();
                    mInfo.checked = isChecked;
                });
                tInfo.mShowView = mView;
                mView.setTag(tInfo);
                mLinear.addView(mView);
            }



            mDialog.setView(mRelative);
            mDialog.show();







            new Thread(()->CalculateThread(mContext)).start();
        }
        catch (Exception e)
        {
            Utils.ShowToast("创建对话框失败"+e);
        }

    }

    public static void CalculateThread(Context mContext)
    {
        for(mShowInfo mInfo :mSettings)
        {
            String Path = mInfo.CleanPath.replace("<QQNumber>", BaseInfo.GetCurrentUin());
            View nView = mInfo.mShowView;
            String CalcResult = FileUtils.getDirSizeString(Path);
            try {
                MMethod.CallMethod(nView,nView.getClass(),"setSecendLineText",void.class,new Class[]{String.class},CalcResult);
            } catch (Exception e) {
                continue;
            }
        }
    }

}
