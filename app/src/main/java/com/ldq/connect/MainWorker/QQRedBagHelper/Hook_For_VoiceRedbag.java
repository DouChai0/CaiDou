package com.ldq.connect.MainWorker.QQRedBagHelper;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MediaUtils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MTool.SilkCodec;
import com.ldq.connect.MTool.TTSVoiceHelper;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_VoiceRedbag {
    public static boolean IsAvaliable = true;
    public static void Start() throws ClassNotFoundException {
        try{
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.qwallet.temp.impl.QWalletTempImpl"),
                    "addAndSendMessage", MClass.loadClass("mqq.app.AppRuntime"),
                    MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                    MClass.loadClass("com.tencent.mobileqq.app.BaseMessageObserver")
                    , new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if(MConfig.Get_Boolean("Main","MainSwitch","语音红包不发语音",false))
                            {
                                param.setResult(null);
                            }
                        }
                    }
            );

            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.qwallet.hb.grap.voice.impl.VoiceRedPacketHelperImpl"), "onRecorderEnd",
                    String.class, MClass.loadClass("com.tencent.mobileqq.utils.RecordParams$RecorderParam"), double.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            if(!MConfig.Get_Boolean("Main","MainSwitch","TTS辅助红包",false))return;
                            try{
                                IsInVoiceRegPacket.getAndSet(true);
                                Object params = param.args[1];

                                if(MField.GetField(param.thisObject,"isCancel",boolean.class)) return;
                                Object Ptt = MField.GetField(params,"f",Object.class);


                                HashMap hash = MField.GetField(param.thisObject,"redPacketCacheMap",HashMap.class);
                                Object WalletMsg = hash.get(Ptt);
                                Object Elem = MField.GetField(WalletMsg,"mQQWalletRedPacketMsg");
                                Object ChildElem = MField.GetField(Elem,"elem");

                                String Msg = MField.GetField(ChildElem,"c", String.class);
                                Msg = Msg.replace("[QQ红包]","")
                                        .replace(",","")
                                        .replace("，","")
                                        .replace("。","");



                                InjectToVoice(Msg);

                            }finally {

                            }


                        }
                    });

            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.pttlogic.api.impl.PttBufferImpl"),
                    "flush", String.class, new XC_MethodHook() {
                        @Override
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                            super.afterHookedMethod(param);
                            if(!MConfig.Get_Boolean("Main","MainSwitch","TTS辅助红包",false))return;
                            if(IsInVoiceRegPacket.get()){
                                IsInVoiceRegPacket.getAndSet(false);
                                String OutputPath = (String) param.args[0];
                                //Utils.ShowToast(OutputPath);
                                FileUtils.copy(MHookEnvironment.PublicStorageModulePath + "Cache/Test.silk",OutputPath,4096);
                            }
                        }
                    }
            );
        }catch (Throwable e){
            IsAvaliable = false;
        }



    }
    static String NowCheckWord = "";
    static AtomicBoolean IsInVoiceRegPacket = new AtomicBoolean();

    public static void InjectToVoice(String CheckerVoice){
        TTSVoiceHelper helper = new TTSVoiceHelper();
        helper.InitTTS();
        helper.ConvertToFile(MHookEnvironment.PublicStorageModulePath + "Cache/Test.wav",CheckerVoice);

        MediaUtils.MP3ToPCM(MHookEnvironment.PublicStorageModulePath+"Cache/Test.wav",MHookEnvironment.PublicStorageModulePath+"Cache/Test.pcm");

        byte[] b = SilkCodec.ConvertDataToSilk(FileUtils.ReadFileByte(MHookEnvironment.PublicStorageModulePath+"Cache/Test.pcm"));
        FileUtils.WriteFileByte(MHookEnvironment.PublicStorageModulePath+"Cache/Test.silk",b);
    }
    public static void VoiceSaveTOFile(String text,String localPath){
        TTSVoiceHelper helper = new TTSVoiceHelper();
        helper.InitTTS();
        helper.ConvertToFile(MHookEnvironment.PublicStorageModulePath + "Cache/Test.wav",text);

        MediaUtils.MP3ToPCM(MHookEnvironment.PublicStorageModulePath+"Cache/Test.wav",MHookEnvironment.PublicStorageModulePath+"Cache/Test.pcm");

        byte[] b = SilkCodec.ConvertDataToSilk(FileUtils.ReadFileByte(MHookEnvironment.PublicStorageModulePath+"Cache/Test.pcm"));
        FileUtils.WriteFileByte(localPath,b);
    }
}
