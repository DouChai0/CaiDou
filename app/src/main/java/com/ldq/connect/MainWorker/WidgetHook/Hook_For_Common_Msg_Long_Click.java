package com.ldq.connect.MainWorker.WidgetHook;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.FloatWindow.EmoHelper;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.HookInstance.HookRecallMsg;
import com.ldq.connect.JavaPlugin.JavaPluginUtils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseCall;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.FormItem;
import com.ldq.connect.QQUtils.MessageRecoreFactory;
import com.ldq.connect.QQUtils.QQBaseMessageHelper;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.QQMessage_Builder;
import com.ldq.connect.QQUtils.QQMessage_Transform;
import com.ldq.connect.QQUtils.QQSendMultiMsg;

import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.ObjectOutputStream;
import java.lang.reflect.Array;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_For_Common_Msg_Long_Click
{

    public static void Start() throws ClassNotFoundException {
        //涂鸦转发
        try{
            Method med = GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.ScribbleItemBuilder"),"a");
            XposedBridge.hookMethod(med, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object arr = param.getResult();
                    Object ret = Array.newInstance(arr.getClass().getComponentType(),Array.getLength(arr)+2);
                    System.arraycopy(arr, 0, ret, 2, Array.getLength(arr));
                    Object MenuItem = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"),new Class[]{
                            int.class,String.class
                    },1888,"转发");

                    MField.SetField(MenuItem,"c",Integer.MAX_VALUE);
                    Array.set(ret,0,MenuItem);

                    MenuItem = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"),new Class[]{
                            int.class,String.class
                    },2333,"保存");
                    MField.SetField(MenuItem,"c",Integer.MAX_VALUE-2);
                    Array.set(ret,1,MenuItem);



                    param.setResult(ret);
                }
            });

            Method InvokeMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.ScribbleItemBuilder","a",void.class,new Class[]{
                    int.class,Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")
            });
            XposedBridge.hookMethod(InvokeMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    int InvokeID = (int) param.args[0];
                    Context mContext = (Context) param.args[1];
                    Object ChatMsg = param.args[2];
                    if(InvokeID==1888)
                    {
                        try{
                            ArrayList<JavaPluginUtils.GroupInfo> GroupList = JavaPluginUtils.GetGroupInfo();
                            String[] ItemName = new String[GroupList.size()];
                            boolean[] ItemCheck = new boolean[GroupList.size()];
                            for(int i=0;i<GroupList.size();i++) ItemName[i] = GroupList.get(i).GroupName+"("+GroupList.get(i).GroupUin+")";
                            AlertDialog.Builder mBuilder = new AlertDialog.Builder(mContext,AlertDialog.THEME_HOLO_LIGHT);
                            mBuilder.setTitle("选择转发到的群");
                            mBuilder.setMultiChoiceItems(ItemName, ItemCheck, new DialogInterface.OnMultiChoiceClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which, boolean isChecked) {

                                }
                            });
                            mBuilder.setPositiveButton("确定转发", (dialog, which) -> {
                                try{
                                    for(int i=0;i<ItemCheck.length;i++)
                                    {
                                        if(ItemCheck[i]==true)
                                        {

                                            String GroupUin = GroupList.get(i).GroupUin;
                                            Object WillingSend = MessageRecoreFactory.CopyToTYMessage(ChatMsg,GroupUin);
                                            BaseCall.AddAndSendMsg(WillingSend);
                                        }
                                    }
                                }
                                catch (Throwable th)
                                {
                                    Utils.ShowToast("发送失败"+th);
                                }

                            });
                            mBuilder.show();
                        }
                        catch (Throwable th)
                        {
                            Utils.ShowToast("发送失败"+th);
                        }

                    }
                    if(InvokeID==2333)
                    {
                        EditText med = new EditText(mContext);
                        med.setTextColor(Color.BLACK);
                        med.setTextSize(20);
                        new AlertDialog.Builder(mContext,3)
                                .setTitle("输入保存的文件名")
                                .setView(med)
                                .setNegativeButton("确定保存", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        try {
                                            String mPath = MField.GetField(ChatMsg, ChatMsg.getClass(), "localFildPath", String.class);
                                            String MD5 = MField.GetField(ChatMsg, ChatMsg.getClass(), "combineFileMd5", String.class);
                                            String LocalCachePath = Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/Scribble/ScribbleCache/"+MD5;


                                            String FileName = med.getText().toString();
                                            if (TextUtils.isEmpty(mPath) && !new File(LocalCachePath).exists()) {
                                                String mName = "" + System.currentTimeMillis();
                                                MHookEnvironment.mTask.PostTaskAndWait(() -> {
                                                            try {
                                                                HttpUtils.downlaodFile(MField.GetField(ChatMsg, ChatMsg.getClass(), "combineFileUrl", 3), MHookEnvironment.MAppContext.getExternalCacheDir().getPath() + "/", mName);
                                                            } catch (Exception e) {
                                                                e.printStackTrace();
                                                            }
                                                        }
                                                );
                                                mPath = MHookEnvironment.MAppContext.getExternalCacheDir().getPath() + "/" + mName;
                                            }

                                            if(TextUtils.isEmpty(mPath) && new File(LocalCachePath).exists())
                                            {
                                                mPath = LocalCachePath;
                                            }

                                            HookRecallMsg.TYSave mSave = new HookRecallMsg.TYSave();
                                            mSave.GIFId = MField.GetField(ChatMsg, "gifId", int.class);
                                            mSave.CombineUrlPath = MField.GetField(ChatMsg, "combineFileUrl", String.class);
                                            mSave.offSet = MField.GetField(ChatMsg, "offSet", int.class);
                                            //Utils.ShowToast(mPath);
                                            mSave.sData = FileUtils.ReadFileByte(mPath);
                                            if (new File(mPath + "_data").exists())
                                            {
                                                mSave.sShowData = FileUtils.ReadFileByte(mPath + "_data");
                                            }

                                            if(mSave.sData.length<10)
                                            {
                                                Utils.ShowToast("保存失败,缺失数据文件");
                                                return;
                                            }

                                            ByteArrayOutputStream mout = new ByteArrayOutputStream();
                                            ObjectOutputStream oop = new ObjectOutputStream(mout);
                                            oop.writeObject(mSave);
                                            String Path = MHookEnvironment.PublicStorageModulePath + "涂鸦保存/"+FileName;
                                            FileUtils.WriteFileByte(Path,mout.toByteArray());
                                            oop.close();
                                            Utils.ShowToast("已保存到"+Path);
                                        }catch (Throwable th)
                                        {
                                            MLogCat.Print_Error("SaveTY",th);
                                        }

                                    }
                                }).show();
                    }
                }
            });

            med = GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.PttItemBuilder"),"a");
            XposedBridge.hookMethod(med, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object ret = param.getResult();
                    Object MenuItem;
                    if(MConfig.Get_Boolean("Main","MainSwitch","语音保存发送",false))
                    {
                        Object arr = param.getResult();
                        ret = Array.newInstance(arr.getClass().getComponentType(),Array.getLength(arr)+1);
                        System.arraycopy(arr, 0, ret, 1, Array.getLength(arr) );
                        MenuItem = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"),new Class[]{
                                int.class,String.class
                        },1889,"保存语音");
                        MField.SetField(MenuItem,"c",Integer.MAX_VALUE);
                        Array.set(ret,0,MenuItem);
                    }

                    if(MConfig.Get_Boolean("Main","MainSwitch","转发语音",false))
                    {
                        MenuItem = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"),new Class[]{
                                int.class,String.class
                        },2488,"转发");
                        MField.SetField(MenuItem,"c",Integer.MAX_VALUE-3);
                        Object ret2 = Array.newInstance(ret.getClass().getComponentType(),Array.getLength(ret)+1);
                        System.arraycopy(ret, 0, ret2, 1, Array.getLength(ret));
                        Array.set(ret2,0,MenuItem);
                        param.setResult(ret2);
                        return;
                    }
                    param.setResult(ret);
                }
            });



            InvokeMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.PttItemBuilder","a",void.class,new Class[]{
                    int.class,Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")
            });
            XposedBridge.hookMethod(InvokeMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    int InvokeID = (int) param.args[0];
                    Context mContext = (Context) param.args[1];
                    Object ChatMsg = param.args[2];
                    if(InvokeID==1889)
                    {
                        AlertDialog dialog = new AlertDialog.Builder(mContext,AlertDialog.THEME_HOLO_LIGHT).create();
                        dialog.setTitle("请输入保存的文件名");
                        EditText medit = new EditText(mContext);
                        medit.setTextSize(22);
                        medit.setTextColor(Color.BLACK);
                        dialog.setView(medit);
                        dialog.setButton(AlertDialog.BUTTON_NEUTRAL,"确定",(vvv,dx) -> {
                            String Text = medit.getText().toString();
                            String PttPath = null;
                            try {
                                PttPath = MMethod.CallMethod(ChatMsg,ChatMsg.getClass(),"getLocalFilePath",String.class,new Class[0],new Object[0]);
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
                    if(InvokeID==2488)
                    {
                        String PttPath;
                        try {
                            QQBaseMessageHelper.RecallCallback callback = new QQBaseMessageHelper.RecallCallback() {
                                @Override
                                public void OnCallback() {
                                    for(QQBaseMessageHelper.UinClass uin: SelectResult)
                                    {
                                        //Utils.ShowToast(uin.SelectGroupUin);
                                        QQMessage_Transform.Group_Send_Ptt(uin.SelectGroupUin,uin.SelectUin,ExtraData);
                                    }

                                }
                            };
                            PttPath = MMethod.CallMethod(ChatMsg,ChatMsg.getClass(),"getLocalFilePath",String.class,new Class[0],new Object[0]);
                            callback.ExtraData = PttPath;
                            QQBaseMessageHelper.StartRecallMessage(callback,"转发语音");
                        } catch (Exception e) {
                            Utils.ShowToast("发生错误"+e);
                        }
                    }
                }
            });


            //图片的

            med = GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.BasePicItemBuilder"),"a");
            XposedBridge.hookMethod(med, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                        Object arr = param.getResult();
                        Object ret = Array.newInstance(arr.getClass().getComponentType(),Array.getLength(arr)+2);
                        System.arraycopy(arr, 0, ret, 2, Array.getLength(arr));
                        Object MenuItem = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"),new Class[]{
                                int.class,String.class
                        },1890,"更多");
                        MField.SetField(MenuItem,"c",Integer.MAX_VALUE-10);
                        Array.set(ret,0,MenuItem);

                        Object MenuItem2 = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"),new Class[]{
                                int.class,String.class
                        },1891,"分类保存");
                        MField.SetField(MenuItem2,"c",Integer.MAX_VALUE-20);
                        Array.set(ret,1,MenuItem2);

                        if(MConfig.Get_Boolean("Main","MainSwitch","便捷群发",false))
                        {
                            MenuItem = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"),new Class[]{
                                    int.class,String.class
                            },2488,"便捷群发");
                            MField.SetField(MenuItem,"c",Integer.MAX_VALUE-3);
                            Object ret2 = Array.newInstance(ret.getClass().getComponentType(),Array.getLength(ret)+1);
                            System.arraycopy(ret, 0, ret2, 1, Array.getLength(ret));
                            Array.set(ret2,0,MenuItem);
                            param.setResult(ret2);
                            return;
                        }

                        param.setResult(ret);

                }
            });



            InvokeMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.BasePicItemBuilder","a",void.class,new Class[]{
                    int.class,Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")
            });
            XposedBridge.hookMethod(InvokeMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    int InvokeID = (int) param.args[0];
                    Context mContext = (Context) param.args[1];
                    Object obj = param.args[2];
                    String LocalPath = MMethod.CallMethod(obj,"getFilePath",String.class,new Class[]{String.class},"chatimg");
                    if(InvokeID==1890)
                    {
                        Dialog mDialog = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.utils.DialogUtil"),"a", MClass.loadClass("com.tencent.mobileqq.utils.QQCustomDialog"),new Class[]{Context.class,int.class},mContext,0);
                        LinearLayout ll = new LinearLayout(mContext);
                        ll.setOrientation(LinearLayout.VERTICAL);
                        MMethod.CallMethod(mDialog,mDialog.getClass(),"setView",mDialog.getClass(),new Class[]{View.class},ll);

                        ll.addView(FormItem.AddListItem(mContext,"秀图(不一定可用)",vx->{
                            new AlertDialog.Builder(mContext,3)
                                    .setTitle("选择类型")
                                    .setItems(new String[]{"秀图", "幻影", "抖动", "生日", "爱你", "征友"},
                                            (dialog, which) -> {
                                                 EditText ed = new EditText(mContext);
                                                 ed.setText("1");
                                                 new AlertDialog.Builder(mContext,3)
                                                         .setTitle("输入次数")
                                                         .setView(ed)
                                                         .setNeutralButton("确定", (dialog1, which1) -> {
                                                             int Count = Integer.parseInt(ed.getText().toString());
                                                             for(int i=0;i<Count;i++){
                                                                 QQMessage.Message_Send_Effect_Pic(BaseInfo.GetCurrentGroupUin(),LocalPath,which);
                                                             }
                                                         }).show();
                                            }).show();
                        }));
                        ll.addView(FormItem.AddListItem(mContext,"复制图片链接", vx -> {
                            try{
                                String PicMd5 = MField.GetField(obj,"md5",String.class);
                                String PicPath = MField.GetField(obj,"bigMsgUrl",String.class);
                                if(TextUtils.isEmpty(PicPath))
                                {
                                    PicPath  = "http://gchat.qpic.cn/gchatpic_new/0/0-0-"+PicMd5+"/0?term=2";
                                }

                                Utils.SetTextClipboard(PicPath);
                                Utils.ShowToast("已复制");
                            }
                            catch (Throwable th)
                            {
                                Utils.ShowToast("发送失败"+th);
                            }
                        }));
                        ll.addView(FormItem.AddListItem(mContext,"复制图片MD5", vx -> {
                            try{
                                String PicMd5 = MField.GetField(obj,"md5",String.class);

                                Utils.SetTextClipboard(PicMd5);
                                Utils.ShowToast("已复制md5");
                            }
                            catch (Throwable th)
                            {
                                Utils.ShowToast("发送失败"+th);
                            }
                        }));

                        ll.addView(FormItem.AddListItem(mContext,"发送XML形式", vx -> {
                            try{
                                String PicMd5 = MField.GetField(obj,"md5",String.class);

                                String XMlCode = "<?xml version='1.0' encoding='UTF-8' standalone='yes' ?><msg serviceID=\"5\" templateID=\"1\" action=\"\" brief=\"[图片表情]\" sourceMsgId=\"0\" url=\"\" flag=\"0\" adverSign=\"0\" multiMsgFlag=\"0\"><item layout=\"0\" advertiser_id=\"0\" aid=\"0\"><image uuid=\"ssssmd5.jpg\" md5=\"ssssmd5\" GroupFiledid=\"0\" filesize=\"2964\" local_path=\"/storage/emulated/0/Tencent/MobileQQ/chatpic/chatimg/13a/Cache_-3e0e5904d24d413a\" minWidth=\"400\" minHeight=\"400\" maxWidth=\"400\" maxHeight=\"400\" /></item><source name=\"\" icon=\"\" action=\"\" appid=\"-1\" /></msg>";
                                XMlCode = XMlCode.replace("ssssmd5",PicMd5);
                                QQMessage.Message_Send_Xml(MHookEnvironment.CurrentSession, QQMessage_Builder.Build_AbsStructMsg(XMlCode));
                            }
                            catch (Throwable th)
                            {
                                Utils.ShowToast("发送失败"+th);
                            }
                        }));


                        mDialog.show();
                    }
                    else if(InvokeID==1891)
                    {
                        EmoHelper.ShowSavePICDialog("","http://gchat.qpic.cn/gchatpic_new/0/0-0-"+ MField.GetField(obj,"md5",String.class)+"/0?term=2",
                                MField.GetField(obj,"md5",String.class)
                                );
                    }
                    if(InvokeID==2488)
                    {
                        try{
                            String PicMd5 = MField.GetField(obj,"md5",String.class);
                            String PicPath = MField.GetField(obj,"bigMsgUrl",String.class);
                            if(TextUtils.isEmpty(PicPath))
                            {
                                PicPath  = "http://gchat.qpic.cn/gchatpic_new/0/0-0-"+PicMd5+"/0?term=2";
                            }

                            new QQSendMultiMsg().ShowDialog("[PicUrl="+PicPath+"]");
                        }
                        catch (Throwable th)
                        {
                            Utils.ShowToast("Error"+th);
                        }
                    }
                }
            });


            //图文的

            med = GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.MixedMsgItemBuilder"),"a");
            XposedBridge.hookMethod(med, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object ret = param.getResult();
                    Object MenuItem;
                    if(MConfig.Get_Boolean("Main","MainSwitch","图片收藏",false))
                    {
                        Object arr = param.getResult();
                        ret = Array.newInstance(arr.getClass().getComponentType(),Array.getLength(arr)+1);
                        System.arraycopy(arr, 0, ret, 1, Array.getLength(arr));
                        MenuItem = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"),new Class[]{
                                int.class,String.class
                        },1999,"分类保存");
                        MField.SetField(MenuItem,"c",Integer.MAX_VALUE-10);
                        Array.set(ret,0,MenuItem);

                        param.setResult(ret);
                    }

                    if(MConfig.Get_Boolean("Main","MainSwitch","便捷群发",false))
                    {
                        MenuItem = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"),new Class[]{
                                int.class,String.class
                        },2488,"便捷群发");
                        MField.SetField(MenuItem,"c",Integer.MAX_VALUE-3);
                        Object ret2 = Array.newInstance(ret.getClass().getComponentType(),Array.getLength(ret)+1);
                        System.arraycopy(ret, 0, ret2, 1, Array.getLength(ret));
                        Array.set(ret2,0,MenuItem);
                        param.setResult(ret2);
                        return;
                    }


                }
            });



            InvokeMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.MixedMsgItemBuilder","a",void.class,new Class[]{
                    int.class,Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")
            });
            XposedBridge.hookMethod(InvokeMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    int InvokeID = (int) param.args[0];
                    Context mContext = (Context) param.args[1];
                    Object obj = param.args[2];
                    if(InvokeID==1999) {
                        EmoHelper.MixedMsgSaveHelper(obj);
                    }
                    if(InvokeID==2488) {
                        List list = MField.GetField(obj,"msgElemList",List.class);
                        String summary = "";
                        for(Object record : list) {
                            if(record.getClass().getName().contains("MessageForText") || record.getClass().getName().contains("MessageForLongText")) {
                                summary = summary + MField.GetField(record,"msg",String.class);
                            }
                            else if(record.getClass().getName().contains("MessageForPic")) {
                                String PicMd5 = MField.GetField(record,"md5",String.class);
                                String PicPath = MField.GetField(record,"bigMsgUrl",String.class);
                                if(TextUtils.isEmpty(PicPath)) {
                                    PicPath  = "http://gchat.qpic.cn/gchatpic_new/0/0-0-"+PicMd5+"/0?term=2";
                                }
                                summary = summary + "[PicUrl="+PicPath+"]";
                            }
                        }
                        new QQSendMultiMsg().ShowDialog(summary);
                    }
                }
            });

            //文本的

            med = GetItemBuilderMenuBuilder(MClass.loadClass("com.tencent.mobileqq.activity.aio.item.TextItemBuilder"),"a");
            XposedBridge.hookMethod(med, new XC_MethodHook() {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);
                    Object ret = param.getResult();
                    Object MenuItem;
                    if(MConfig.Get_Boolean("Main","MainSwitch","便捷群发",false))
                    {
                        MenuItem = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenuItem"),new Class[]{
                                int.class,String.class
                        },2488,"便捷群发");
                        MField.SetField(MenuItem,"c",Integer.MAX_VALUE-3);
                        Object ret2 = Array.newInstance(ret.getClass().getComponentType(),Array.getLength(ret)+1);
                        System.arraycopy(ret, 0, ret2, 1, Array.getLength(ret));
                        Array.set(ret2,0,MenuItem);
                        param.setResult(ret2);
                        return;
                    }


                }
            });



            InvokeMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.item.TextItemBuilder","a",void.class,new Class[]{
                    int.class,Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage")
            });
            XposedBridge.hookMethod(InvokeMethod, new XC_MethodHook() {
                @Override
                protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                    super.beforeHookedMethod(param);
                    int InvokeID = (int) param.args[0];
                    Object obj = param.args[2];
                    if(InvokeID==2488) {
                        String contain = MField.GetField(obj,"msg",String.class);
                        String AtInfoShowList = "";
                        String mStr = MField.GetField(obj,obj.getClass(),"extStr",String.class);
                        JSONObject mJson = new JSONObject(mStr);
                        mStr = mJson.optString("troop_at_info_list");
                        ArrayList<AtPosclass> AtPos = new ArrayList();
                        if(!TextUtils.isEmpty(mStr)) {
                            ArrayList AtList3 = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"),"getTroopMemberInfoFromExtrJson",ArrayList.class,new Class[]{String.class},mStr);
                            if(AtList3!=null ) {
                                for(Object mStrObj : AtList3) {
                                    long mLongData = MField.GetField(mStrObj,mStrObj.getClass(),"uin",long.class);
                                    if(mLongData==0) {
                                        AtPosclass pos = new AtPosclass();
                                        pos.Length = MField.GetField(mStrObj,"textLen",short.class);
                                        pos.StartPos = MField.GetField(mStrObj,"startPos",short.class);
                                        pos.uin = ""+mLongData;
                                        AtPos.add(pos);
                                        continue;
                                    }
                                }
                            }
                        }
                        if(AtPos.size()==0) {
                            AtInfoShowList  = contain;
                        }else {
                            int NowPos = 0;
                            for(AtPosclass poss : AtPos) {
                                AtInfoShowList = AtInfoShowList + contain.substring(NowPos,poss.StartPos);
                                AtInfoShowList = AtInfoShowList + "[AtQQ=0]";
                                NowPos = NowPos + (poss.StartPos-NowPos);
                                NowPos = NowPos + poss.Length;
                            }
                            if(NowPos<contain.length())AtInfoShowList = AtInfoShowList + contain.substring(NowPos);
                        }


                        new QQSendMultiMsg().ShowDialog(AtInfoShowList);
                    }
                }
            });
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("RecallHook",th);
        }


    }
    static class AtPosclass{
        public short StartPos;
        public short Length;
        public String uin;
    }
    public static Method GetItemBuilderMenuBuilder(Class clz,String MethodName)
    {
        for(Method med : clz.getDeclaredMethods())
        {
            if(med.getName().equals(MethodName))
            {
                if(med.getParameterTypes().length == 1)
                {
                    if(med.getParameterTypes()[0]== View.class)
                    {
                        Class ReturnClz = med.getReturnType();
                        if(ReturnClz.isArray())
                        {
                            return med;
                        }
                    }
                }
            }
        }
        return null;
    }
}
