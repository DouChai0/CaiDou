package com.ldq.connect.ExceptionHelper;

import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;

import java.io.ByteArrayOutputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.util.Random;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

public class CatcherPacker {
    public static CatcherPacker getInstance(){
        return new CatcherPacker();
    }


    ZipOutputStream zOut;
    ByteArrayOutputStream bArr = new ByteArrayOutputStream();
    String Path;
    private CatcherPacker(){
        String FileName = "CrashReport_"+ Utils.GetNowTime22()+new Random().nextInt() +".zip";
        Path = MHookEnvironment.PublicStorageModulePath + "Log/"+FileName;
        try {
            zOut = new ZipOutputStream(new FileOutputStream(Path));
        } catch (FileNotFoundException e) {
            zOut = new ZipOutputStream(bArr);
        }
    }
    public void AddStackTrace(String Track){
        try {
            ZipEntry newTrack = new ZipEntry("StackTrace.log");
            zOut.putNextEntry(newTrack);
            zOut.write(Track.getBytes(StandardCharsets.UTF_8));
            zOut.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }
    public void AddDeviceInfo(String DeviceInfo){
        try {
            ZipEntry newTrack = new ZipEntry("DeviceInfo.log");
            zOut.putNextEntry(newTrack);
            zOut.write(DeviceInfo.getBytes(StandardCharsets.UTF_8));
            zOut.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void AddLogcatInfo(String Logcat){
        try {
            ZipEntry newTrack = new ZipEntry("LogCat.log");
            zOut.putNextEntry(newTrack);
            zOut.write(Logcat.getBytes(StandardCharsets.UTF_8));
            zOut.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void AddXposedLog(String LogInfo){
        try {
            ZipEntry newTrack = new ZipEntry("Xposed_Log.log");
            zOut.putNextEntry(newTrack);
            zOut.write(LogInfo.getBytes(StandardCharsets.UTF_8));
            zOut.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void AddXposedErr(String LogInfo){
        try {
            ZipEntry newTrack = new ZipEntry("Xposed_Err.log");
            zOut.putNextEntry(newTrack);
            zOut.write(LogInfo.getBytes(StandardCharsets.UTF_8));
            zOut.closeEntry();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    public void CloseAll(){
        try {
            zOut.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public String getPath(){
        return Path;
    }
}
