package com.ldq.connect.MTool;

import android.speech.tts.TextToSpeech;
import android.speech.tts.UtteranceProgressListener;

import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;

import java.util.HashMap;
import java.util.concurrent.atomic.AtomicBoolean;

public class TTSVoiceHelper {
    TextToSpeech tSpeech = null;
    public void InitTTS(){
        tSpeech = new TextToSpeech(MHookEnvironment.CacheActContext,new TTSListener());
        tSpeech.setSpeechRate(1.0f);
        tSpeech.setPitch(1f);
        tSpeech.setOnUtteranceProgressListener(new UtteranceProgressListener() {
            @Override
            public void onStart(String utteranceId) {

            }

            @Override
            public void onDone(String utteranceId) {
                LockCount--;
            }

            @Override
            public void onError(String utteranceId) {

            }
        });
    }
    public void ConvertToFile(String Path,String Text){
        while (!InitResult.get()) {
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        HashMap<String, String> myHashRender = new HashMap<>();
        myHashRender.put(TextToSpeech.Engine.KEY_PARAM_UTTERANCE_ID,Text);

        tSpeech.synthesizeToFile(Text,myHashRender,Path);

        Lock();
    }
    private boolean InitSuccess;

    volatile int LockCount = 0;
    private void Lock()
    {
        LockCount++;
        int  LockCounta =0;
        while (LockCount!=0)
        {
            LockCounta++;
            if(LockCounta>100)break;
            try {
                Thread.sleep(10);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }
    AtomicBoolean InitResult = new AtomicBoolean();

    public class TTSListener implements TextToSpeech.OnInitListener
    {
        @Override
        public void onInit(int status) {
            InitResult.getAndSet(true);
            if(status != TextToSpeech.SUCCESS)
            {
                Utils.ShowToast("TTS引擎初始化失败");
                InitSuccess = false;
            }else
            {
                InitSuccess = true;
            }
        }
    }
}
