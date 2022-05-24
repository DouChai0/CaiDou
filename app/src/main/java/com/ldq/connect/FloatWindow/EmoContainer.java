package com.ldq.connect.FloatWindow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MainWorker.BaseWorker.EnvHook.BasePieHook;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Send_Mixed_Pic;
import com.ldq.connect.QQUtils.FormItem;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.QQMessage_Builder;
import com.ldq.connect.QQUtils.QQSessionUtils;
import com.ldq.connect.Tools.MPositionDialog;

import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;

public class EmoContainer extends ScrollView implements ViewPager.RequestShow{
    ArrayList<String> PicPathList = new ArrayList<>();

    ArrayList<LinearLayout> PicContainer = new ArrayList<>();

    LinearLayout Container;

    String EachName;

    String Path;
    public EmoContainer(Context context,String SearchPath,String EachName) {
        super(context);

        this.EachName = EachName;
        Container = new LinearLayout(context);
        Container.setOrientation(LinearLayout.VERTICAL);
        addView(Container);

        Path = SearchPath;
        UpdataFileList();

    }
    private void UpdataFileList()
    {
        PicPathList.clear();
        ArrayList list = new ArrayList();
        File[] fs = new File(Path).listFiles();
        if (fs == null){
            Utils.ShowToast("路径无效,已强制刷新");
            new Handler(Looper.getMainLooper()).post(()->Init.FlushAll());
            return;
        }
        Arrays.sort(fs,(Comparator<File>) (f1, f2) -> {
            long diff = f1.lastModified() - f2.lastModified();
            if (diff > 0)
                return -1;
            else if (diff == 0)
                return 0;
            else
                return 1;//如果 if 中修改为 返回-1 同时此处修改为返回 1  排序就会是递减
        });
        for(File f: fs)
        {
            if(f.isFile())
            {
                list.add(f.getAbsolutePath());
            }
        }
        AddItem(list);
    }
    private void AddItem(ArrayList AddList)
    {
        PicPathList.addAll(AddList);
    }

    //刷新表情的项目列表,读取缩略图数据
    public void FlushItem()
    {
        int LinePoint = 0;
        LinearLayout l = new LinearLayout(getContext());
        Container.addView(l);
        for(String s : PicPathList)
        {
            if(LinePoint>=4)
            {
                LinePoint=0;
                l = new LinearLayout(getContext());
                Container.addView(l);
            }
            ImageView img = new ImageView(getContext());

            img.setImageDrawable(getCacheBitmap(new File(s).getName(),s));
            img.setScaleType(ImageView.ScaleType.FIT_CENTER);
            img.setTag(s);
            img.setOnClickListener(v->{
                String Path = (String) v.getTag();
                if(QQSessionUtils.GetSessionID() ==1 || QQSessionUtils.GetSessionID() ==0)
                {
                    if (Handler_Send_Mixed_Pic.IsMix){
                        Handler_Send_Mixed_Pic.AddList(Path);
                        return;
                    }
                    if(MConfig.Get_Boolean("Main","MainSwitch","回复带图",false)) {
                        if (BasePieHook.IsNowReplying()) {
                            BasePieHook.AddEditText("[PicUrl="+Path+"]");
                            return;
                        }
                    }
                    if(MConfig.Get_Boolean("Main","MainSwitch","图文消息模式",false))
                    {
                        BasePieHook.AddEditText("[PicUrl="+Path+"]");
                        return;
                    }

                }
                QQMessage.Message_Send_Pic(MHookEnvironment.CurrentSession, QQMessage_Builder.Build_Pic(MHookEnvironment.CurrentSession,Path));
                //QQMessage_Transform.Message_Send_Pic(QQSessionUtils.GetCurrentGroupUin(), QQSessionUtils.GetCurrentFriendUin(),Path);
            });
            img.setOnLongClickListener(v -> {
                String Path = (String) v.getTag();
                ShowMoreDialog(Path);
                return true;
            });
            LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(Utils.dip2px(getContext(),60), Utils.dip2px(getContext(),60));
            params.setMargins(30,30,0,0);
            l.addView(img,params);

            LinePoint+=1;
        }
        Container.requestLayout();
        Container.invalidate();
    }

    //取得或者生成缩略图数据
    public Drawable getCacheBitmap(String Name,String sourcePath)
    {
        if(new File(sourcePath).length() < 50 * 1024)return Drawable.createFromPath(sourcePath);//如果图片小于50kb就不生成缩略图了
        File f = new File(MHookEnvironment.PublicStorageModulePath + "Emo/.thumb/"+Name);
        if(f.exists())
        {
            return Drawable.createFromPath(MHookEnvironment.PublicStorageModulePath + "Emo/.thumb/"+Name);
        }else
        {
            try {
                CreateThumb(Name,sourcePath);
                return Drawable.createFromPath(MHookEnvironment.PublicStorageModulePath + "Emo/.thumb/"+Name);
            } catch (IOException e) {
                MLogCat.Print_Error("CachePic",e);
                return Drawable.createFromPath(sourcePath);
            }
        }
    }

    public void RecycleMem()
    {
        for(LinearLayout l : PicContainer)
        {
            for(int i=0;i<l.getChildCount();i++)
            {
                View s = l.getChildAt(i);
                if(s instanceof ImageView)
                {
                    ImageView img = (ImageView) s;
                    BitmapDrawable b = (BitmapDrawable) img.getDrawable();
                    b.getBitmap().recycle();
                }
            }
        }
        PicContainer.clear();
        Container.removeAllViews();
        System.gc();
    }
    public void CreateThumb(String fileName,String sourcePath) throws IOException {
        Bitmap d = BitmapFactory.decodeFile(sourcePath);
        Bitmap decode = upImageSize(d, Utils.dip2px(getContext(),60), Utils.dip2px(getContext(),60));
        if(!new File(MHookEnvironment.PublicStorageModulePath + "Emo/.thumb/").exists())new File(MHookEnvironment.PublicStorageModulePath + "Emo/.thumb/").mkdirs();
        saveFile(decode,MHookEnvironment.PublicStorageModulePath + "Emo/.thumb/"+fileName);
    }
    public void saveFile(Bitmap bm, String path) throws IOException {
        File dirFile = new File(path);
        File myCaptureFile = new File(path);
        BufferedOutputStream bos = new BufferedOutputStream(new FileOutputStream(myCaptureFile));
        bm.compress(Bitmap.CompressFormat.PNG, 80, bos);
        bos.flush();
        bos.close();
    }
    public static Bitmap upImageSize(Bitmap bmp, int width,int height) {
        if(bmp==null){
            return null;
        }
        // 计算比例
        float scaleX = (float)width / bmp.getWidth();// 宽的比例
        float scaleY = (float)height / bmp.getHeight();// 高的比例
        //新的宽高
        int newW = 0;
        int newH = 0;
        if(scaleX > scaleY){
            newW = (int) (bmp.getWidth() * scaleX);
            newH = (int) (bmp.getHeight() * scaleX);
        }else if(scaleX <= scaleY){
            newW = (int) (bmp.getWidth() * scaleY);
            newH = (int) (bmp.getHeight() * scaleY);
        }
        return Bitmap.createScaledBitmap(bmp, newW, newH, true);
    }


    @Override
    public void requestShow() {
        UpdataFileList();
        FlushItem();
    }

    @Override
    public void BackEvent() {

    }

    @Override
    public void requestExit() {
        RecycleMem();
    }

    @Override
    public void requestOnClick() {

    }
    public void ShowMoreDialog(String EmoPath)
    {
        Activity act = Utils.GetThreadActivity();
        LinearLayout l = new LinearLayout(act);
        l.setOrientation(LinearLayout.VERTICAL);
        l.addView(FormItem.AddListItem(act,"删除该表情", v->{
            new AlertDialog.Builder(act,3)
                    .setTitle("确认")
                    .setMessage("你真的要删除吗?")
                    .setPositiveButton("确认删除", (dialog, which) -> {
                        FileUtils.deleteFile(new File(EmoPath));
                        RecycleMem();
                        UpdataFileList();
                        FlushItem();
                        Init.FlushLayout();
                    }).setNegativeButton("取消", (dialog, which) -> {

                    }).show();
        }));
        //l.addView(FormItem.AddListItem(act,"发送为秀图",v-> PreSendShowImg.PreSendEffectImage(EmoPath)));
        l.addView(FormItem.AddListItem(act,"发送为XML形式",v-> {
            String XMlCode = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><msg serviceID=\"5\" templateID=\"1\" action=\"\" brief=\"[图片表情]\" sourceMsgId=\"0\" url=\"\" flag=\"0\" adverSign=\"0\" multiMsgFlag=\"0\"><item layout=\"0\" advertiser_id=\"0\" aid=\"0\"><image uuid=\"ssssmd5.jpg\" md5=\"ssssmd5\" GroupFiledid=\"0\" filesize=\"2964\" local_path=\"/storage/emulated/0/Tencent/MobileQQ/chatpic/chatimg/13a/Cache_-3e0e5904d24d413a\" minWidth=\"400\" minHeight=\"400\" maxWidth=\"400\" maxHeight=\"400\" /></item><source name=\"\" icon=\"\" action=\"\" appid=\"-1\" /></msg>";
            XMlCode = XMlCode.replace("ssssmd5", DataUtils.getFileMD5(new File(EmoPath)));
            QQMessage.Message_Send_Xml(MHookEnvironment.CurrentSession, QQMessage_Builder.Build_AbsStructMsg(XMlCode));
        }));

        MPositionDialog.CreateDownDialogMeasure(l);
    }
}
