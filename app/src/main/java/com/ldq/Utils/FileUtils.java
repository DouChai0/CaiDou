package com.ldq.Utils;

import com.ldq.connect.MHookEnvironment;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

public class FileUtils
{
    public static byte[] ReadFileByte(String Path)
    {
        try{
            InputStream input= new FileInputStream(Path);
            ByteArrayOutputStream output = new ByteArrayOutputStream();
            byte[] buffer = new byte[4096];
            int n;
            while(-1 != (n = input.read(buffer)))
            {
                output.write(buffer, 0, n);
            }
            return output.toByteArray();
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static String ReadFileString(String Path)
    {
        byte[] bts = ReadFileByte(Path);
        return bts== null ? "":new String(bts);
    }
    public static void WriteFileByte(String path,byte[] data)
    {
        try
        {
            if(data==null)return;
            String AboPath = path.substring(0,path.lastIndexOf(File.separatorChar));
            File pathFile = new File(AboPath);
            if(!pathFile.exists())
            {
                pathFile.mkdirs();
            }
            File file = new File(path);
            FileOutputStream fos = new FileOutputStream(file);
            if(!file.exists())
            {
                file.createNewFile();
            }
            fos.write(data);
            fos.flush();
        }
        catch(IOException ioe)
        {
            ioe.printStackTrace();
        }
    }
    public static long getDirSize(File file) {
        //判断文件是否存在
        if (file.exists()) {
            //如果是目录则递归计算其内容的总大小
            if (file.isDirectory()) {
                File[] children = file.listFiles();
                long size = 0;
                if(children==null)return 0;
                for (File f : children)
                    size += getDirSize(f);
                return size;
            } else {//如果是文件则直接返回其大小,以“兆”为单位
                long size = file.length();
                return size;
            }
        } else {
            return 0;
        }
    }
    public static String getDirSizeString(String Path) {

        long Size = getDirSize(new File(Path));
        return DataUtils.bytes2kb(Size);
    }
    public static void deleteFile(File file){
        //判断文件不为null或文件目录存在
        if (file == null || !file.exists()){
            //System.out.println("文件删除失败,请检查文件路径是否正确");
            return;
        }
        if(file.isFile())
        {
            file.delete();
            return;
        }
        //取得这个目录下的所有子文件对象
        File[] files = file.listFiles();
        if(files == null)return;
        //遍历该目录下的文件对象
        for (File f: files){
            //打印文件名
            String name = file.getName();
            //判断子目录是否存在子目录,如果是文件则删除
            if (f.isDirectory()){
                deleteFile(f);
            }else {
                f.delete();
            }
        }
        //删除空文件夹  for循环已经把上一层节点的目录清空。
        file.delete();
    }
    public static String GetPath(String RawPath)
    {
        String mRaw = RawPath.toLowerCase();

        if(mRaw.startsWith("http:") || mRaw.startsWith("https:"))
        {
            String mRandomPathName = (""+Math.random()).substring(2);
            String mRandomPath = MHookEnvironment.PublicStorageModulePath + "Cache/";
            HttpUtils.downlaodFile(RawPath,mRandomPath,mRandomPathName);
            return mRandomPath + mRandomPathName;
        }
        else
        {
            return RawPath;
        }
    }
    public static void copy(String source, String dest, int bufferSize)
    {

        try
        {

            File f = new File(dest);
            f=f.getParentFile();
            if(!f.exists())f.mkdirs();

            File aaa = new File(dest);
            if(aaa.exists()) aaa.delete();

            InputStream in = new FileInputStream(new File(source));
            OutputStream out = new FileOutputStream(new File(dest));
            byte[] buffer = new byte[bufferSize];
            int len;
            while((len = in .read(buffer)) > 0)
            {
                out.write(buffer, 0, len);
            }
            in.close();
            out.close();
        }
        catch(Exception e)
        {}
        finally
        {}
    }
}
