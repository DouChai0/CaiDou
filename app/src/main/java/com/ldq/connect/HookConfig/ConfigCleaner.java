package com.ldq.connect.HookConfig;

import android.os.Environment;

import com.ldq.Utils.FileUtils;
import com.ldq.connect.MHookEnvironment;

import java.io.File;

public class ConfigCleaner {
    public static void StartClean(){
        String Path = Environment.getExternalStorageDirectory()+"/Android/data/com.tencent.mobileqq/files/ClearSet";
        if(new File(Path).exists()){
            new File(Path).delete();
            Clean0();
        }else {
            Path = Environment.getExternalStorageDirectory()+"/Android/media/com.tencent.mobileqq/ClearSet";
            if(new File(Path).exists()){
                new File(Path).delete();
                Clean0();
            }
        }
    }
    private static void Clean0(){
        FileUtils.deleteFile(new File(MHookEnvironment.PublicStorageModulePath + "配置文件目录"));
        FileUtils.deleteFile(new File(MHookEnvironment.PublicStorageModulePath + "LotData"));
        FileUtils.deleteFile(new File(MHookEnvironment.PublicStorageModulePath + "back.png"));
        FileUtils.deleteFile(new File(MHookEnvironment.PublicStorageModulePath + "ficon.png"));
        FileUtils.deleteFile(new File(MHookEnvironment.PublicStorageModulePath + "repeat.png"));
    }
}
