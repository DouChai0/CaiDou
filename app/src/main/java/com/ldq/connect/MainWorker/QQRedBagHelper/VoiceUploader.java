package com.ldq.connect.MainWorker.QQRedBagHelper;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQTools;

import java.util.HashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class VoiceUploader {
    public static void TestHooker(){
        XposedHelpers.findAndHookMethod(MClass._loadClass("com.tencent.mobileqq.transfile.GroupPttUploadProcessor"), "sendMsg",
              new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        String localPath = MField.GetField(param.thisObject,"mPttFilePath",String.class);
                        if (pathList.containsKey(localPath)){
                            OnSuccess callback = pathList.get(localPath);
                            pathList.remove(localPath);
                            callback.OnSuccess();
                            if(MConfig.Get_Boolean("Main","MainSwitch","语音红包不发语音",false)){
                                param.setResult(null);
                            }

                        }
                    }
                });
    }
    Object Processer;
    String Local;
    public void Init(Object messageRecord,String LocalPath,boolean IsSend){
        try{
            Local =LocalPath;
            Object TransferRequest = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.transfile.TransferRequest"),new Class[0]);
            MField.SetField(TransferRequest,"mBusiType",1002);
            MField.SetField(TransferRequest,"mFileType",2);
            MField.SetField(TransferRequest,"mIsUp",true);
            MField.SetField(TransferRequest,"mLocalPath",LocalPath);
            MField.SetField(TransferRequest,"mPeerUin",MField.GetField(messageRecord,"frienduin",String.class));
            MField.SetField(TransferRequest,"mPttUploadPanel",5);
            MField.SetField(TransferRequest,"mRec",messageRecord);
            MField.SetField(TransferRequest,"mSelfUin", BaseInfo.GetCurrentUin());
            MField.SetField(TransferRequest,"mUinType", 1);
            MField.SetField(TransferRequest,"needSendMsg",IsSend);
            MField.SetField(TransferRequest,"mUniseq",MField.GetField(messageRecord,"uniseq",long.class));

            Object PttUploadHelper = QQTools.getQRoundApi(MClass._loadClass("com.tencent.mobileqq.transfile.api.IPttTransProcessorHelper"));
            Object BaseFileController = QQTools.getRuntimeService(MClass._loadClass("com.tencent.mobileqq.transfile.api.ITransFileController"));


            Object PttUploadProgresser = MMethod.CallMethod(PttUploadHelper,"createPttTransProcessor",MClass.loadClass("com.tencent.mobileqq.transfile.BaseTransProcessor"),new Class[]{
                    MClass.loadClass("com.tencent.mobileqq.transfile.BaseTransFileController"),MClass.loadClass("com.tencent.mobileqq.transfile.TransferRequest")
            },BaseFileController,TransferRequest);
            Processer = PttUploadProgresser;
        }catch (Exception e){
            MLogCat.Print_Error("VoiceUploader",e);
        }
    }
    public void StartForKey(OnSuccess success) throws Exception {
        pathList.put(Local,success);
        MMethod.CallMethod(Processer,"start",void.class,new Class[0]);

    }
    public byte[] getLocalMD5() throws Exception {
        return MField.GetField(Processer,"mLocalMd5",byte[].class);
    }
    public String getUUID() throws Exception {
        String resID = MField.GetField(Processer,"mResid",String.class);
        if (resID == null){
            return MField.GetField(Processer,"mUuid",String.class);
        }
        return resID;
    }
    static HashMap<String,OnSuccess> pathList = new HashMap<>();
    interface OnSuccess{
        void OnSuccess();
    }
}
