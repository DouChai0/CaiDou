package com.ldq.connect.JavaPlugin;

import android.text.TextUtils;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MField;
import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.TroopManager;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class JavaPluginUtils
{
    public static String AuthorName = "";
    public static List<JavaPlugin.PluginData> GetDefPluginList()
    {
        List<JavaPlugin.PluginData> TheReturnList = new ArrayList<>();
        return TheReturnList;
    }
    public static List<JavaPlugin.PluginData> GetFilePluginList()
    {
        List<JavaPlugin.PluginData> TheReturnList = new ArrayList<>();
        //从路径中查找java脚本文件
        String Path = MHookEnvironment.PublicStorageModulePath + "Plugin/";
        File mPath = new File(Path);
        if(mPath.isFile())mPath.delete();
        if(!mPath.exists())mPath.mkdirs();

        File[] FileList = mPath.listFiles();
        if(FileList!=null)
        {
            for(File mFile : FileList)
            {
                if(mFile.isFile())
                {
                    if(mFile.getName().endsWith(".java"))//判断后缀
                    {
                        JavaPlugin.PluginData theAddData = new JavaPlugin.PluginData();
                        String TheFilePath = mFile.getAbsolutePath();
                        theAddData.Path = TheFilePath;
                        theAddData.Name = mFile.getName();
                        TheReturnList.add(theAddData);

                    }


                }
            }
        }

        return TheReturnList;
    }

    public static class GroupInfo
    {
        public String GroupUin;
        public String GroupName;
        public String GroupOwner;
        public String[] AdminList;
        public Object sourceInfo;
    }



    public static GroupInfo GetGroupInfo(String GroupUin)
    {
        ArrayList<GroupInfo> mInfo = GetGroupInfo();
        for(GroupInfo sInfo : mInfo)
        {
            if(sInfo.GroupUin.equals(GroupUin))
            {
                return sInfo;
            }
        }
        return null;
    }
    public static ArrayList<GroupInfo> GetGroupInfo()
    {
        ArrayList mArray = TroopManager.Group_GetList();
        if(mArray!=null)
        {
            ArrayList<GroupInfo> mReBack = new ArrayList<>();
            for(Object mInfo : mArray)
            {
                try{
                    GroupInfo mShowInfo = new GroupInfo();
                    mShowInfo.GroupName = MField.GetField(mInfo,"troopname",String.class);
                    mShowInfo.GroupOwner = MField.GetField(mInfo,"troopowneruin",String.class);
                    String AdminList =  MField.GetField(mInfo,"Administrator",String.class);
                    mShowInfo.AdminList = AdminList.split("\\|");
                    mShowInfo.GroupUin = MField.GetField(mInfo,"troopuin",String.class);
                    mShowInfo.sourceInfo = mInfo;
                    mReBack.add(mShowInfo);
                }
                catch (Exception ex)
                {
                    continue;
                }
            }
            return mReBack;
        }
        else
        {
            return new ArrayList<>();
        }
    }
    public static ArrayList<GroupMemberInfo> GetGroupMemberList(String GroupUin)
    {
        List mGetList = TroopManager.Group_GetUserList(GroupUin);
        ArrayList<GroupMemberInfo> mReBackList = new ArrayList<>();
        if(mGetList!=null)
        {
            GroupInfo gInfo = GetGroupInfo(GroupUin);
            if(gInfo==null)gInfo = new GroupInfo();
            List<String> sList = Arrays.asList(gInfo.AdminList);
            ArrayList<String> sAdminList = new ArrayList<>(sList);
            sAdminList.add(gInfo.GroupOwner);
            for(Object mInfo : mGetList)
            {


                try{
                    GroupMemberInfo mSetInfo = new GroupMemberInfo();
                    mSetInfo.Join_Time = MField.GetField(mInfo,"join_time",long.class);
                    mSetInfo.Last_AvtivityTime = MField.GetField(mInfo,"last_active_time",long.class);
                    mSetInfo.UserUin = MField.GetField(mInfo,"memberuin",String.class);
                    //if(mSetInfo.UserUin.equals("184757891"))DebugUtils.PrintAllField(mInfo);
                    //if(mSetInfo.UserUin.equals("1071791"))DebugUtils.PrintAllField(mInfo);

                    mSetInfo.UserName = MField.GetField(mInfo,"friendnick",String.class);
                    mSetInfo.NickName = MField.GetField(mInfo,"troopnick",String.class);
                    if(TextUtils.isEmpty(mSetInfo.NickName))mSetInfo.NickName = mSetInfo.UserName;

                    mSetInfo.UserLevel =  MField.GetField(mInfo,"newRealLevel",int.class);
                    mSetInfo.sourceInfo = mInfo;
                    if(sAdminList.contains(mSetInfo.UserUin))mSetInfo.IsAdmin = true;
                    mReBackList.add(mSetInfo);

                }
                catch (Exception ex)
                {
                    continue;
                }
            }

        }
        return mReBackList;
    }

    public static ArrayList GetGroupForbiddenList(String GroupUin)
    {
        ArrayList mList = TroopManager.GetForbiddenList(GroupUin);
        ArrayList mReBackList = new ArrayList();
        for(Object TheList : mList)
        {
            try {
                String UserUin = MField.GetField(TheList,"a",String.class);
                long TimeStamp = MField.GetField(TheList,"a|b",long.class);
                GroupBanInfo mAddInfo = new GroupBanInfo();
                mAddInfo.UserUin = UserUin;
                mAddInfo.UserName = TroopManager.GetMemberName(GroupUin,UserUin);
                mAddInfo.Endtime = TimeStamp*1000;
                mReBackList.add(mAddInfo);
            } catch (Exception e) {
                continue;
            }

        }
        return mReBackList;
    }

    public static class GroupMemberInfo
    {
        public String UserUin;
        public String UserName;
        public String NickName;
        public int UserLevel;
        public long Join_Time;
        public long Last_AvtivityTime;
        public Object sourceInfo;

        public boolean IsAdmin;
    }

    public static class GroupBanInfo
    {
        public String UserUin;
        public String UserName;
        public long Endtime;
    }
    public static void CommonLoadCheck(String Path,String PluginID)
    {
        String TempPath = Path;
        JavaPlugin.PluginData mData = JavaPlugin.PluginIdToData(PluginID);
        if(!new File(TempPath).exists())
        {
            TempPath = MHookEnvironment.PublicStorageModulePath+"Plugin/"+TempPath;
            if(!new File(TempPath).exists()){
                Utils.ShowToast("调用load加载的文件"+Path+"不存在");
                return;
            }

        }
        try {
            JavaPlugin.loadExtra(new String(FileUtils.ReadFileByte(TempPath)),mData.Instance,false,PluginID,TempPath);
        } catch (Exception e) {
            Utils.ShowToast("加载错误:\n"+e);
        }
    }
    public static JavaPluginMethod.mMessageInfoList[] InitMessageData(String MessageData)
    {
        if(MessageData.indexOf("[")==-1)
        {
            JavaPluginMethod.mMessageInfoList[] mList = new JavaPluginMethod.mMessageInfoList[1];
            mList[0] = new JavaPluginMethod.mMessageInfoList();
            mList[0].Message = MessageData;
            mList[0].MessageType = 1;
            return mList;
        }
        ArrayList mList = new ArrayList();
        int index1 = -1;
        int index2 = -1;
        int LastEndPos = 0;
        index1 = MessageData.indexOf("[");
        if(index1!=-1) index2 = MessageData.indexOf("]",index1+1);
        while (index1 !=-1)
        {
            if(index2!=-1)
            {
                String mTextStr = MessageData.substring(LastEndPos,index1);
                if(!TextUtils.isEmpty(mTextStr))
                {
                    JavaPluginMethod.mMessageInfoList mTextInfo = new JavaPluginMethod.mMessageInfoList();
                    mTextInfo.Message = mTextStr;
                    mTextInfo.MessageType = 1;
                    mList.add(mTextInfo);
                }
                String ExtraData = MessageData.substring(index1+1,index2);
                LastEndPos = index2+1;
                JavaPluginMethod.mMessageInfoList mInfo = new JavaPluginMethod.mMessageInfoList();
                if(ExtraData.startsWith("PicUrl="))
                {
                    mInfo.MessageType = 2;
                    mInfo.Message = ExtraData.substring(7);
                    mList.add(mInfo);
                }
                else if (ExtraData.startsWith("AtQQ="))
                {
                    mInfo.MessageType = 3;
                    mInfo.Message = ExtraData.substring(5);
                    mList.add(mInfo);
                }
                else
                {
                    mInfo.MessageType = 1;
                    mInfo.Message = "["+ExtraData+"]";
                    mList.add(mInfo);
                }
            }
            else
            {
                break;
            }
            index1 = MessageData.indexOf("[",LastEndPos);
            if(index1!=-1)index2 = MessageData.indexOf("]",index1+1);
        }
        if(LastEndPos< MessageData.length())
        {
            JavaPluginMethod.mMessageInfoList mTextInfo = new JavaPluginMethod.mMessageInfoList();
            mTextInfo.Message = MessageData.substring(LastEndPos);
            mTextInfo.MessageType = 1;
            mList.add(mTextInfo);
        }
        return (JavaPluginMethod.mMessageInfoList[]) mList.toArray(new JavaPluginMethod.mMessageInfoList[0]);
    }
}
