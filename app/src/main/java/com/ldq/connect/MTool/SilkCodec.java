package com.ldq.connect.MTool;

import android.content.Context;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.MHookEnvironment;

import java.io.ByteArrayOutputStream;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;

public class SilkCodec {
    public static byte[] ConvertDataToSilk(byte[] b){
        try{
            Object wechatNsWrapper = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.utils.SilkCodecWrapper"),
                    new Class[]{Context.class}, MHookEnvironment.MAppContext);

            Object audioCompositeProcessor = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.qqaudio.audioprocessor.AudioCompositeProcessor"),
                    new Class[0]);
            MMethod.CallMethod(audioCompositeProcessor,"a",void.class,
                    new Class[]{MClass.loadClass("com.tencent.mobileqq.qqaudio.audioprocessor.IAudioProcessor")},
                    wechatNsWrapper);

            MMethod.CallMethod(audioCompositeProcessor,"a",void.class,
                    new Class[]{int.class,int.class,int.class},
                    16000,16000,1);

            byte[] MixData = new byte[6400];
            int Mix = b.length;

            int Pos = 0;
            ByteArrayOutputStream outData = new ByteArrayOutputStream();
            outData.write(2);
            outData.write("#!SILK_V3".getBytes(StandardCharsets.UTF_8));
            while (Mix>0){
                int GetLength = Math.min(Mix, 6400);
                Mix -= GetLength;


                System.arraycopy(b,Pos,MixData,0,GetLength);
                Object ProcessData = MMethod.CallMethod(audioCompositeProcessor,"a",MClass.loadClass("com.tencent.mobileqq.qqaudio.audioprocessor.IAudioProcessor$ProcessData"),new Class[]{
                        byte[].class,int.class,int.class
                },MixData,0,GetLength);

                Pos+=GetLength;
                if(ProcessData!=null)
                {
                    byte[] gProcessResult = MField.GetField(ProcessData,"c",byte[].class);
                    int length = MField.GetField(ProcessData,"a",int.class);

                    outData.write(Arrays.copyOf(gProcessResult,length));
                }
            }

            MMethod.CallMethod(audioCompositeProcessor,"a",void.class,new Class[0]);
            return outData.toByteArray();


        }catch (Exception e){
            MLogCat.Print_Error("SilkCodec",e);
            return null;
        }
    }
}
