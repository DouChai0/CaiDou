package com.ldq.connect.QQUtils;

import android.text.TextUtils;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookInstance.HookRecallMsg;
import com.ldq.connect.MHookEnvironment;

import java.io.File;
import java.lang.reflect.Method;

public class MessageRecoreFactory {
    public static Object CopyToMacketFaceMessage(Object SourceObj) throws Exception {
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                int.class
        });
        Object mMessageRecord = CallMethod.invoke(null,-2007);
        MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass().getSuperclass().getSuperclass(),"initInner",void.class,
                new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                BaseInfo.GetCurrentUin(), MField.GetField(SourceObj,SourceObj.getClass(),"frienduin",4),BaseInfo.GetCurrentUin(),"[原创表情]",System.currentTimeMillis()/1000,-2007,
                MField.GetField(SourceObj,SourceObj.getClass(),"istroop",4),System.currentTimeMillis()/1000
        );
        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"msgData",MField.GetField(SourceObj,SourceObj.getClass(),"msgData",byte[].class),byte[].class);
        String strName = MField.GetField(SourceObj,SourceObj.getClass(),"sendFaceName",3);
        if(strName!=null)
        {
            MField.SetField(mMessageRecord,mMessageRecord.getClass(),"sendFaceName",strName,String.class);
        }
        MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass(),"doParse",void.class,new Class[0]);
        Object finalRecord=MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.service.message.MessageRecordFactory"),"a",MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                new Class[]{MClass.loadClass("com.tencent.mobileqq.data.MessageRecord")},mMessageRecord
        );
        return finalRecord;
    }

    public static Object CopyToTYMessage(Object SourceObj) throws Exception {
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a",MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                int.class
        });
        Object mMessageRecord = CallMethod.invoke(null,-7001);
        MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass().getSuperclass().getSuperclass(),"initInner",void.class,
                new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                BaseInfo.GetCurrentUin(),MField.GetField(SourceObj,SourceObj.getClass(),"frienduin",4),BaseInfo.GetCurrentUin(),"[涂鸦]",System.currentTimeMillis()/1000,-7001,
                MField.GetField(SourceObj,SourceObj.getClass(),"istroop",4),System.currentTimeMillis()/1000
        );

        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"combineFileUrl",MField.GetField(SourceObj,SourceObj.getClass(),"combineFileUrl",String.class),String.class);
        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"combineFileMd5",MField.GetField(SourceObj,SourceObj.getClass(),"combineFileMd5",String.class),String.class);

        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"gifId",MField.GetField(SourceObj,SourceObj.getClass(),"gifId",int.class),int.class);

        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"offSet",MField.GetField(SourceObj,SourceObj.getClass(),"offSet",int.class),int.class);
        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"fileUploadStatus",MField.GetField(SourceObj,SourceObj.getClass(),"fileUploadStatus",int.class),int.class);
        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"fileDownloadStatus",MField.GetField(SourceObj,SourceObj.getClass(),"fileDownloadStatus",int.class),int.class);
        //MField.SetField(mMessageRecord,mMessageRecord.getClass(),"localFildPath",MField.GetField(SourceObj,SourceObj.getClass(),"localFildPath",3),3);
        String mPath = MField.GetField(SourceObj,SourceObj.getClass(),"localFildPath",3);
        if(TextUtils.isEmpty(mPath))
        {
            String mName = ""+System.currentTimeMillis();
            MHookEnvironment.mTask.PostTaskAndWait(()->{
                        try {
                            HttpUtils.downlaodFile(MField.GetField(SourceObj,SourceObj.getClass(),"combineFileUrl",3), MHookEnvironment.MAppContext.getExternalCacheDir().getPath()+"/",mName);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
            );
            MField.SetField(mMessageRecord,mMessageRecord.getClass(),"localFildPath",MHookEnvironment.MAppContext.getExternalCacheDir().getPath()+"/"+mName,String.class);
        }
        else
        {
            MField.SetField(mMessageRecord,mMessageRecord.getClass(),"localFildPath",mPath,String.class);
        }
        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"extStr",MField.GetField(SourceObj,SourceObj.getClass(),"extStr",String.class),String.class);
        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"msg","[涂鸦]",String.class);
        MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass(),"prewrite",void.class,new Class[0]);
        MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass(),"parse",void.class,new Class[0]);
        Object finalRecord=MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.service.message.MessageRecordFactory"),"a",MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                new Class[]{MClass.loadClass("com.tencent.mobileqq.data.MessageRecord")},mMessageRecord
        );
        return finalRecord;
    }
    public static Object CopyToTYMessage(Object SourceObj,String GroupUin) throws Exception {
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a",MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                int.class
        });
        Object mMessageRecord = CallMethod.invoke(null,-7001);
        MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass().getSuperclass().getSuperclass(),"initInner",void.class,
                new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                BaseInfo.GetCurrentUin(),GroupUin,BaseInfo.GetCurrentUin(),"[涂鸦]",System.currentTimeMillis()/1000,-7001,
                MField.GetField(SourceObj,SourceObj.getClass(),"istroop",4),System.currentTimeMillis()/1000
        );

        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"combineFileUrl",MField.GetField(SourceObj,SourceObj.getClass(),"combineFileUrl",String.class),String.class);

        //MLogCat.Print_Debug(MField.GetField(SourceObj,SourceObj.getClass(),"combineFileUrl",String.class));
        //MLogCat.Print_Debug(MField.GetField(SourceObj,SourceObj.getClass(),"localFildPath",String.class));
        //MLogCat.Print_Debug(MField.GetField(SourceObj,SourceObj.getClass(),"combineFileMd5",String.class));
        //MLogCat.Print_Debug(""+MField.GetField(SourceObj,SourceObj.getClass(),"offSet",int.class));

        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"combineFileMd5",MField.GetField(SourceObj,SourceObj.getClass(),"combineFileMd5",String.class),String.class);

        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"gifId",MField.GetField(SourceObj,SourceObj.getClass(),"gifId",int.class),int.class);

        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"offSet",MField.GetField(SourceObj,SourceObj.getClass(),"offSet",int.class),int.class);
        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"fileUploadStatus",MField.GetField(SourceObj,SourceObj.getClass(),"fileUploadStatus",int.class),int.class);
        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"fileDownloadStatus",MField.GetField(SourceObj,SourceObj.getClass(),"fileDownloadStatus",int.class),int.class);
        String mPath = MField.GetField(SourceObj,SourceObj.getClass(),"localFildPath",3);
        if(TextUtils.isEmpty(mPath))
        {
            String mName = ""+System.currentTimeMillis();
            MHookEnvironment.mTask.PostTaskAndWait(()->{
                        try {
                            HttpUtils.downlaodFile(MField.GetField(SourceObj,SourceObj.getClass(),"combineFileUrl",3), MHookEnvironment.MAppContext.getExternalCacheDir().getPath()+"/",mName);
                        } catch (Throwable e) {
                            e.printStackTrace();
                        }
                    }
            );
            MField.SetField(mMessageRecord,mMessageRecord.getClass(),"localFildPath",MHookEnvironment.MAppContext.getExternalCacheDir().getPath()+"/"+mName,String.class);
        }
        else
        {
            MField.SetField(mMessageRecord,mMessageRecord.getClass(),"localFildPath",mPath,String.class);
        }
        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"extStr",MField.GetField(SourceObj,SourceObj.getClass(),"extStr",String.class),String.class);

        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"msg","[涂鸦]",String.class);
        MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass(),"prewrite",void.class,new Class[0]);
        MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass(),"parse",void.class,new Class[0]);
        Object finalRecord=MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.service.message.MessageRecordFactory"),"a",MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                new Class[]{MClass.loadClass("com.tencent.mobileqq.data.MessageRecord")},mMessageRecord
        );
        return finalRecord;
    }


    public static Object mBuildTYMessage(HookRecallMsg.TYSave mSave, String GroupUin, String UserUin, String mPath) throws Exception {
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a",MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                int.class
        });
        Object mMessageRecord = CallMethod.invoke(null,-7001);
        String CurrentUin = UserUin;
        int isTroop = 0;
        if(!TextUtils.isEmpty(GroupUin))
        {
            if(TextUtils.isEmpty(UserUin))
            {
                isTroop=1;
                CurrentUin = GroupUin;
            }
            else
            {
                isTroop=1000;
            }
        }
        if(isTroop==1000)
        {
            MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass().getSuperclass().getSuperclass(),"initInner",void.class,
                    new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                    BaseInfo.GetCurrentUin(),GroupUin,UserUin,"[涂鸦]",System.currentTimeMillis()/1000,-7001,
                    isTroop,System.currentTimeMillis()/1000
            );
        }
        else
        {
            MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass().getSuperclass().getSuperclass(),"initInner",void.class,
                    new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                    BaseInfo.GetCurrentUin(),CurrentUin,BaseInfo.GetCurrentUin(),"[涂鸦]",System.currentTimeMillis()/1000,-7001,
                    isTroop,System.currentTimeMillis()/1000
            );
        }




        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"gifId",mSave.GIFId,int.class);

        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"offSet",mSave.offSet,int.class);
        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"fileUploadStatus",0,int.class);
       // MField.SetField(mMessageRecord,mMessageRecord.getClass(),"fileDownloadStatus",MField.GetField(SourceObj,SourceObj.getClass(),"fileDownloadStatus",int.class),int.class);

        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"localFildPath",mPath,String.class);

        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"combineFileMd5", DataUtils.getFileMD5(new File(mPath)),String.class);

        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"combineFileUrl",mSave.CombineUrlPath,String.class);
        MField.SetField(mMessageRecord,mMessageRecord.getClass(),"msg","[涂鸦]",String.class);
        MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass(),"prewrite",void.class,new Class[0]);
        MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass(),"parse",void.class,new Class[0]);
        Object finalRecord=MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.service.message.MessageRecordFactory"),"a",MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                new Class[]{MClass.loadClass("com.tencent.mobileqq.data.MessageRecord")},mMessageRecord
        );
        return finalRecord;
    }
    public static Object Build_RawMessageRecord_Troop(String GroupUin,int Type) throws Exception {
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                int.class
        });
        Object mMessageRecord = CallMethod.invoke(null,Type);
        MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass().getSuperclass().getSuperclass(),"init",void.class,
                new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                BaseInfo.GetCurrentUin(),GroupUin,BaseInfo.GetCurrentUin(),"",System.currentTimeMillis()/1000,Type,
                1,System.currentTimeMillis()/1000
        );
        return mMessageRecord;
    }
    public static Object Build_RawMessageRecord_Friend(String Uin,int Type) throws Exception {
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a", MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),new Class[]{
                int.class
        });
        Object mMessageRecord = CallMethod.invoke(null,Type);
        MMethod.CallMethod(mMessageRecord,mMessageRecord.getClass().getSuperclass().getSuperclass(),"init",void.class,
                new Class[]{String.class,String.class,String.class,String.class,long.class,int.class,int.class,long.class},
                BaseInfo.GetCurrentUin(),Uin,BaseInfo.GetCurrentUin(),"",System.currentTimeMillis()/1000,Type,
                0,System.currentTimeMillis()/1000
        );
        MField.SetField(mMessageRecord,"issend",1);
        return mMessageRecord;
    }
    public static Object Build_FlashChat(Object SourceChat)
    {
        try{
            Method ArkChatObj = MMethod.FindMethod("com.tencent.mobileqq.service.message.MessageRecordFactory","a",
                    MClass.loadClass("com.tencent.mobileqq.data.MessageForArkFlashChat"),
                    new Class[]{
                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                            String.class,String.class,int.class,
                            MClass.loadClass("com.tencent.mobileqq.data.ArkFlashChatMessage")
                    }
            );

            Object sArk = MField.GetField(SourceChat,"ark_app_message");
            int isTroop = MField.GetField(SourceChat,"istroop",int.class);
            String FriendUin = MField.GetField(SourceChat,"frienduin",String.class);
            Object NewChat = ArkChatObj.invoke(null,MHookEnvironment.AppInterface,FriendUin,BaseInfo.GetCurrentUin(),isTroop,sArk);
            return NewChat;
        }catch (Exception e)
        {
            MLogCat.Print_Error("Build_ArkChat",e);
            return null;
        }

    }
    public static Object Build_CopyToAntSticker(Object Res)
    {
        try {
            String GroupUin = MField.GetField(Res,"frienduin",String.class);
            int isTroop = MField.GetField(Res,"istroop",int.class);
            Object sObj;
            if(isTroop==1)
            {
                sObj=Build_RawMessageRecord_Troop(GroupUin,-8018);
            }
            else
            {
                sObj=Build_RawMessageRecord_Friend(GroupUin,-8018);
            }

            MField.SetField(sObj,"sevrId",MField.GetField(Res,"sevrId",int.class));
            MField.SetField(sObj,"stickerId",MField.GetField(Res,"stickerId",String.class));
            MField.SetField(sObj,"text",MField.GetField(Res,"text",String.class));
            MField.SetField(sObj,"packId", "1");
            MField.SetField(sObj,"msgVia",MField.GetField(Res,"msgVia",int.class));
            return sObj;
        } catch (Exception exception) {
            exception.printStackTrace();
            return null;
        }

    }
}
