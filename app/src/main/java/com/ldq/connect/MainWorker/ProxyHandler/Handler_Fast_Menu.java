package com.ldq.connect.MainWorker.ProxyHandler;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.PopupWindow;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.Utils.DataUtils;
import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookInstance.HookRecallMsg;
import com.ldq.connect.JavaPlugin.JavaPlugin;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseCall;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.FormItem;
import com.ldq.connect.QQUtils.MessageRecoreFactory;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.TroopManager;
import com.ldq.connect.Tools.MPositionDialog;

import java.io.File;
import java.io.FileInputStream;
import java.io.ObjectInputStream;
import java.util.Iterator;
import java.util.Set;

import bsh.BshMethod;
import bsh.NameSpace;


//群聊右上角Handler注册点在BaseChatPieHook中
//快捷本地语音发送注册点在
public class Handler_Fast_Menu implements View.OnLongClickListener{
    public static void _caller(Activity mAct, ViewGroup v)
    {
        int mID = mAct.getResources().getIdentifier("ivTitleBtnRightImage","id",mAct.getPackageName());
        View mView = v.findViewById(mID);
        ThemView = mView;
        if(mView!=null)
        {
            mView.setOnLongClickListener(new Handler_Fast_Menu());
        }
        mPluginView = new ScrollView(mAct);
        mPluginItem = new LinearLayout(mAct);


        mID = mAct.getResources().getIdentifier("title","id",mAct.getPackageName());
        mView = v.findViewById(mID);
        if(mView!=null)
        {
            mView.setOnLongClickListener(new Handler_Fast_Menu());
        }
    }
    public static View ThemView;
    public static ScrollView mPluginView;
    public static LinearLayout mPluginItem;
    public static TextView AddClickTextView(Context context, String Title)
    {
        //ImageButton imBtn = new ImageButton(context)
        TextView mView = new TextView(context);
        mView.setClickable(true);
        mView.setSingleLine(true);

        mView.setBackgroundColor(Color.BLACK);
        mView.getBackground().setAlpha(150);
        ColorStateList mList = createColorStateList(Color.parseColor("#ffa020"),Color.GREEN);
        mView.setTextColor(mList);
        mView.setText(Title);
        return mView;
        //mView.setBackgroundColor(mList.);

    }
    public static ColorStateList createColorStateList(int selected,int normal) {
        int[] colors = new int[] { selected,  normal};
        int[][] states = new int[2][];
        states[0] = new int[] { android.R.attr.state_pressed , android.R.attr.state_enabled};
        states[1] = new int[] { android.R.attr.state_enabled };
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }

    @Override
    public boolean onLongClick(View v) {

        try{

            ScrollView sc = new ScrollView(v.getContext());
            PopupWindow mWindow = new PopupWindow(sc, Utils.dip2px(v.getContext(),300), Utils.dip2px(v.getContext(),300));

            LinearLayout mLayout = new LinearLayout(v.getContext());
            sc.addView(mLayout);
            mLayout.setBackgroundColor(Color.GRAY);
            mLayout.setOrientation(LinearLayout.VERTICAL);
            mLayout.getBackground().setAlpha(120);
            TextView mView;


            if(BaseInfo.IsCurrentGroup()) {
                //添加各种处理项
                mView = AddClickTextView(v.getContext(),"窗口抖动");
                mView.setTextSize(20);
                mView.setOnClickListener(v1 -> {
                    try {
                        QQMessage.Message_Send_ShakeWindow(BaseInfo.GetCurrentGroupUin());
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                mLayout.addView(mView);

                File f = new File(MHookEnvironment.PublicStorageModulePath + "涂鸦保存/");
                if(f.exists() && f.listFiles().length>0)
                {
                    mView = AddClickTextView(v.getContext(),"发送涂鸦");
                    mView.setTextSize(20);
                    mView.setOnClickListener(v1 -> {
                        try {
                            ShowSendDialog(v.getContext());
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    });
                    mLayout.addView(mView);
                }

                //只有有权限才显示需要权限的设置项
                if(TroopManager.IsGroupAdmin(BaseInfo.GetCurrentGroupUin()))
                {
                    mView = AddClickTextView(v.getContext(),"全员禁言");
                    mView.setTextSize(20);
                    mView.setOnClickListener(v1 -> {
                        try {
                            TroopManager.Group_Forbidden_All(BaseInfo.GetCurrentGroupUin(),true);
                        } catch (Exception e) {

                        }
                    });

                    mLayout.addView(mView);




                    mView = AddClickTextView(v.getContext(),"全员解禁");
                    mView.setTextSize(20);
                    mView.setOnClickListener(v1 -> {
                        try {
                            TroopManager.Group_Forbidden_All(BaseInfo.GetCurrentGroupUin(),false);
                        } catch (Exception e) {

                        }
                    });

                    mLayout.addView(mView);

                }
            }
            if(BaseInfo.IsCurrentChannel()){
                mView = AddClickTextView(v.getContext(),"查看频道信息");
                mView.setTextSize(20);
                mView.setOnClickListener(v1 -> {
                    try {
                        String Message = "频道ID:" + FieldTable.SessionInfo_ChannelID().get(MHookEnvironment.CurrentSession);
                        Message+="\n子频道ID:" + FieldTable.SessionInfo_ChannelCharID().get(MHookEnvironment.CurrentSession);
                        Message+="\n子频道名称:" + MField.GetField(MHookEnvironment.CurrentSession,"e",String.class);

                        new AlertDialog.Builder(Utils.GetThreadActivity(),3)
                                .setTitle("频道信息")
                                .setMessage(Message)
                                .setNegativeButton("复制频道ID", (dialog, which) -> {
                                    try {
                                        Utils.SetTextClipboard((String) FieldTable.SessionInfo_ChannelID().get(MHookEnvironment.CurrentSession));
                                        Utils.ShowToast("已复制");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }

                                })
                                .setNeutralButton("复制子频道ID:", (dialog, which) -> {
                                    try {
                                        Utils.SetTextClipboard((String)FieldTable.SessionInfo_ChannelCharID().get(MHookEnvironment.CurrentSession));
                                        Utils.ShowToast("已复制");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                })
                                .show();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                mLayout.addView(mView);
            }
            {
                mView = AddClickTextView(v.getContext(),"发送复合图片");
                mView.setTextSize(20);
                mView.setOnClickListener(v1 -> {
                    try {
                        Handler_Send_Mixed_Pic.IsMix = true;
                        Handler_Send_Mixed_Pic.sList.clear();
                        Utils.ShowToast("请发送两张图片以供合成");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                });
                mLayout.addView(mView);
            }



            //显示脚本添加的一些项目
            if(JavaPlugin.PluginList.size()!=0)
            {
                boolean isAdd = false;
                for(JavaPlugin.PluginData mPlugData : JavaPlugin.PluginList)
                {
                    //调用Item回调
                    try{
                        if(!TextUtils.isEmpty(mPlugData.ItemFunctionName))
                        {
                            NameSpace nameSpace = mPlugData.Instance.getNameSpace();
                            BshMethod method =nameSpace.getMethod(mPlugData.ItemFunctionName,new Class[]{String.class});
                            if(method!=null)
                            {
                                method.invoke(new Object[]{BaseInfo.GetCurrentGroupUin()},mPlugData.Instance);
                            }
                        }
                    }catch (Throwable th)
                    {

                    }
                    if(mPlugData.GroupMenuList.size()!=0)
                    {
                        Set<String> mSet = mPlugData.GroupMenuList.keySet();
                        Iterator<String> it = mSet.iterator();
                        while (it.hasNext())
                        {
                            String Keystr = it.next();
                            String InvokeMethod = mPlugData.GroupMenuList.get(Keystr);
                            int Index = Keystr.lastIndexOf("->>");
                            String ShowName = Keystr.substring(0,Index);
                            mView = AddClickTextView(v.getContext(),ShowName+"("+mPlugData.Name+")");
                            mView.setTextSize(20);
                            String TagName = InvokeMethod+"<!><!>"+mPlugData.PluginID;//保存脚本ID和存储的方法名字
                            mView.setTag(TagName);
                            mView.setOnClickListener(v1 -> {
                                try {
                                    mWindow.dismiss();
                                    String[] mTagCut = ((String)v1.getTag()).split("<!><!>");
                                    if(mTagCut.length>=2)
                                    {
                                        //获取保存的方法名字并调用
                                        JavaPlugin.PluginData mGetData = JavaPlugin.PluginIdToData(mTagCut[1]);
                                        if(mGetData!=null)
                                        {
                                            NameSpace nameSpace = mGetData.Instance.getNameSpace();
                                            BshMethod method =nameSpace.getMethod(mTagCut[0],new Class[]{String.class});
                                            if(method!=null)
                                            {
                                                method.invoke(new Object[]{BaseInfo.GetCurrentGroupUin()},mGetData.Instance);
                                            }
                                            method =nameSpace.getMethod(mTagCut[0],new Class[]{String.class,String.class,int.class});
                                            if(method!=null)
                                            {
                                                int SessionType = (int) FieldTable.SessionInfo_isTroop().get(MHookEnvironment.CurrentSession);
                                                String GroupUin;
                                                String UserUin;
                                                if(SessionType==1)
                                                {
                                                    GroupUin = (String) FieldTable.SessionInfo_friendUin().get(MHookEnvironment.CurrentSession);
                                                    UserUin = "";
                                                    method.invoke(new Object[]{GroupUin,UserUin,1},mGetData.Instance);
                                                }
                                                else if (SessionType==0)
                                                {
                                                    GroupUin = "";
                                                    UserUin = (String) FieldTable.SessionInfo_friendUin().get(MHookEnvironment.CurrentSession);
                                                    method.invoke(new Object[]{GroupUin,UserUin,2},mGetData.Instance);
                                                }
                                                else if(SessionType == 1000)
                                                {
                                                    GroupUin = (String) FieldTable.SessionInfo_InTroopUin().get(MHookEnvironment.CurrentSession);
                                                    UserUin = (String) FieldTable.SessionInfo_friendUin().get(MHookEnvironment.CurrentSession);
                                                    method.invoke(new Object[]{GroupUin,UserUin,3},mGetData.Instance);
                                                }else if (SessionType == 10014){
                                                    GroupUin = FieldTable.SessionInfo_ChannelID().get(MHookEnvironment.CurrentSession) + "&" +
                                                            FieldTable.SessionInfo_ChannelCharID().get(MHookEnvironment.CurrentSession);
                                                    UserUin = "";
                                                    method.invoke(new Object[]{GroupUin,UserUin,1},mGetData.Instance);
                                                }


                                            }

                                        }
                                    }
                                } catch (Throwable e) {
                                    Utils.ShowToast("脚本在执行快捷菜单时发生错误:\n"+e);
                                }
                            });
                            //只有在真正存在添加选择时才显示
                            if(!isAdd)
                            {
                                TextView ThemView = AddClickTextView(v.getContext(),"以下为脚本添加的选项");
                                ThemView.setTextColor(Color.BLUE);
                                ThemView.setTextSize(20);
                                ThemView.setOnClickListener(null);
                                mLayout.addView(ThemView);
                                isAdd=true;
                            }
                            mLayout.addView(mView);
                        }
                    }
                }
            }


            if(mLayout.getChildCount()>0)
            {
                mWindow.setFocusable(true);
                mWindow.setOutsideTouchable(true);
                mWindow.showAsDropDown(ThemView);
            }
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("FastMenu",th);

        }

        return true;
    }
    public static void ShowSendDialog(Context mContext)
    {
        LinearLayout l = new LinearLayout(mContext);
        l.setOrientation(LinearLayout.VERTICAL);
        File ff = new File(MHookEnvironment.PublicStorageModulePath + "涂鸦保存/");
        for(File sF : ff.listFiles())
        {
            if(sF.isFile())
            {
                View s = FormItem.AddListItem(mContext, sF.getName(), v -> {
                    try {
                        HookRecallMsg.TYSave mSave = null;

                        String CachePath = "";
                        int GIFId = 0;

                        try{
                            ObjectInputStream oInp = new ObjectInputStream(new FileInputStream(sF));
                            mSave = (HookRecallMsg.TYSave) oInp.readObject();
                            CachePath = Environment.getExternalStorageDirectory()+
                                    "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/Scribble/ScribbleCache/" + DataUtils.getDataMD5(mSave.sData);
                            FileUtils.WriteFileByte(CachePath,mSave.sData);
                            FileUtils.WriteFileByte(CachePath+"_data",mSave.sShowData);
                            GIFId = mSave.GIFId;
                        }catch (Exception ex)
                        {
                            MLogCat.Print_Error("FASTMenu",Log.getStackTraceString(ex));
                            CachePath = Environment.getExternalStorageDirectory()+
                                    "/Android/data/com.tencent.mobileqq/Tencent/MobileQQ/Scribble/ScribbleCache/"+ DataUtils.getFileMD5(sF);
                            FileUtils.copy(sF.getAbsolutePath(),CachePath,4096);

                        }


                        Object TYRecord;
                        if(BaseInfo.IsCurrentGroup())
                        {
                            TYRecord= MessageRecoreFactory.mBuildTYMessage(mSave, BaseInfo.GetCurrentGroupUin(),"",CachePath);
                        }
                        else
                        {
                            TYRecord= MessageRecoreFactory.mBuildTYMessage(mSave,"" , BaseInfo.GetCurrentGroupUin(),CachePath);
                        }

                        BaseCall.AddAndSendMsg(TYRecord);
                        MLogCat.Print_Info("TYSend","Path="+CachePath);
                    } catch (Exception e) {
                        MLogCat.Print_Error("TYBuildSend",e);
                        Utils.ShowToast("发送失败");
                    }
                });
                l.addView(s);
            }
        }
        MPositionDialog.CreateDownDialog(l);
    }
}
