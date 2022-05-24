package com.ldq.Utils;

import android.util.Log;

import com.ldq.connect.MHookEnvironment;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.OutputStreamWriter;

import de.robv.android.xposed.XposedBridge;

public class MLogCat
{
    static String Log_Path = MHookEnvironment.PublicStorageModulePath + "Log/";
    static String Error_Path = MHookEnvironment.PublicStorageModulePath + "Log/error.log";
    static String Info_Path = MHookEnvironment.PublicStorageModulePath + "Log/info.log";
    static String Debug_Path = MHookEnvironment.PublicStorageModulePath + "Log/debug.log";
    static String Crash_Path =  MHookEnvironment.PublicStorageModulePath + "Log/Crash-Track.log";
    static String Plugin_Path =  MHookEnvironment.PublicStorageModulePath + "Plugin/Error-Track.log";
    static String Status_Path = MHookEnvironment.PublicStorageModulePath + "Plugin/Log_Status.log";
    static {
        try{
            File f = new File(Error_Path);
            if(f.length()>=64*1024*1024) f.delete();
            f = new File(Info_Path);
            if(f.length()>=64*1024*1024) f.delete();
            f = new File(Debug_Path);
            if(f.length()>=64*1024*1024) f.delete();
            f = new File(Plugin_Path);
            if(f.length()>=64*1024*1024) f.delete();


            new File(MHookEnvironment.PublicStorageModulePath + "Cache/").mkdirs();
        }catch (Throwable th)
        {
            XposedBridge.log(th);
        }

    }
    static FileOutputStream FOut;
    public static void Print_Status(String Tag,String info)
    {

    }
    public static void Print_Status0(String text)
    {
        try{
            if(FOut !=null)
            {
                FOut.write(text.getBytes());
                FOut.write("\n".getBytes());
                return;
            }
            File f = new File(Status_Path);
            if(!f.exists()) f.mkdirs();
            f = new File(Status_Path);
            FileOutputStream fos = new FileOutputStream(f,true);
            FOut = fos;
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(text);
            bw.newLine();
        }
        catch (Exception e)
        {

        }
    }
    public static void Print_Error(String Tag,String info)
    {
        try{
            File f = new File(Log_Path);
            if(!f.exists()) f.mkdirs();
            f = new File(Error_Path);
            FileOutputStream fos = new FileOutputStream(f,true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(Tag+":"+info);
            bw.newLine();
            bw.flush();
            bw.close();
            osw.close();
            fos.close();
        }
        catch (Exception e)
        {

        }
        finally {
            XposedBridge.log("ERROR-"+Tag+":"+info);
        }
    }
    public static void Print_Error(String Tag,Throwable info)
    {
        Print_Error(Tag, Log.getStackTraceString(info));
    }
    public static void Print_Info(String Tag,String info)
    {
        try{
            File f = new File(Log_Path);
            if(!f.exists()) f.mkdirs();
            f = new File(Info_Path);
            FileOutputStream fos = new FileOutputStream(f,true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(Tag+":"+info);
            bw.newLine();
            bw.flush();
            bw.close();
            osw.close();
            fos.close();
        }
        catch (Exception e)
        {

        }
        finally {
            XposedBridge.log("INFO-"+Tag+":"+info);
        }
    }
    public static <T> void Print_Debug(T info){
        Print_Debug(String.valueOf(info));
    }

    public static void Print_Debug(String info)
    {
        try{
            File f = new File(Log_Path);
            if(!f.exists()) f.mkdirs();
            f = new File(Debug_Path);
            FileOutputStream fos = new FileOutputStream(f,true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(info);
            bw.newLine();
            bw.flush();
            bw.close();
            osw.close();
            fos.close();
        }
        catch (Exception e)
        {

        }
        finally {
            //XposedBridge.log("DEUBG:"+info);
        }
    }

    public static void Print_PluginError(String info,Throwable th)
    {
        Print_PluginError(info,Log.getStackTraceString(th));
    }
    public synchronized static void Print_PluginError(String Tag,String info)
    {
        try{
            File f = new File(Log_Path);
            if(!f.exists()) f.mkdirs();
            f = new File(Plugin_Path);
            FileOutputStream fos = new FileOutputStream(f,true);
            OutputStreamWriter osw = new OutputStreamWriter(fos);
            BufferedWriter bw = new BufferedWriter(osw);
            bw.write(Tag+":"+info);
            bw.newLine();
            bw.flush();
            bw.close();
            osw.close();
            fos.close();
        }
        catch (Exception e)
        {

        }
        finally {
            XposedBridge.log("INFO-"+Tag+":"+info);
        }
    }
    public static void Print_MIX(String Info)
    {

    }
    public static void Print_Crash(String info)
{
    try{
        File f = new File(Log_Path);
        if(!f.exists()) f.mkdirs();
        f = new File(Crash_Path);
        FileOutputStream fos = new FileOutputStream(f,true);
        OutputStreamWriter osw = new OutputStreamWriter(fos);
        BufferedWriter bw = new BufferedWriter(osw);
        bw.write(info);
        bw.newLine();
        bw.flush();
        bw.close();
        osw.close();
        fos.close();
    }
    catch (Exception e)
    {

    }
    finally {
        XposedBridge.log("Crash:"+info);
    }
}
}
