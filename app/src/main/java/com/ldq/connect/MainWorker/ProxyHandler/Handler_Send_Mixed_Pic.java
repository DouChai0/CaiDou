package com.ldq.connect.MainWorker.ProxyHandler;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.QQMessage_Transform;
import com.ldq.connect.QQUtils.QQSessionUtils;
import com.ldq.connect.Tools.ImageConverter;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;

public class Handler_Send_Mixed_Pic {
    public static boolean IsMix = false;
    public static ArrayList<String> sList = new ArrayList<>();
    public static void AddList(String path){
        sList.add(path);
        if (sList.size() == 1){
            Utils.ShowToast("再发送一张图片即可");
            return;
        }
        IsMix = false;
        Bitmap p1 = BitmapFactory.decodeFile(sList.get(0));
        Bitmap p2 = BitmapFactory.decodeFile(sList.get(1));
        ImageConverter.compositePicture(Utils.GetThreadActivity(),p1,p2,bitmap -> {
            String cachePath = MHookEnvironment.PublicStorageModulePath+"Cache/"+Utils.GetNowTime22()+".png";
            new File(MHookEnvironment.PublicStorageModulePath+"Cache/").mkdirs();
            try {
                FileOutputStream fOut = new FileOutputStream(cachePath);
                bitmap.compress(Bitmap.CompressFormat.PNG,100,fOut);
                fOut.close();
                QQMessage_Transform.Message_Send_Pic(QQSessionUtils.GetCurrentGroupUin(),QQSessionUtils.GetCurrentFriendUin(),cachePath);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });


    }
}
