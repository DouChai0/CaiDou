package com.ldq.connect.MTool.QQCleaner;

import com.ldq.connect.MainWorker.SystemHook.Hook_For_Cache_File_Observe;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ChileFileChecker {
    public static void Check_Path_JustFile(File Path){
        if(Path.isFile()){
            Check_File(Path);
            return;
        }
        if(Path.isDirectory()) {
            File[] list = Path.listFiles();

            if(list != null){
                for(File f : list){
                    if(f.isFile()) {
                        Check_File(f);
                    }else if (f.isDirectory()){
                        Check_Path_JustFile(f);
                    }
                }
            }
            list = Path.listFiles();
            if(list==null || list.length==0){
                Path.delete();
            }
        }

    }
    public static void Check_File(File f){
        if(System.currentTimeMillis() - f.lastModified() > 24 * 3600 * 1000){
            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm");
            Hook_For_Cache_File_Observe.ClearLog(f.getAbsolutePath()+"[Last Modified:"+format.format(new Date(f.lastModified()))+"]");
            f.delete();
        }
    }
}
