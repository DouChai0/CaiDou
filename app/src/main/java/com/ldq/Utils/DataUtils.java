package com.ldq.Utils;

import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.text.DecimalFormat;

public class DataUtils {
    public static String bytesToHex(byte[] bytes) {
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < bytes.length; i++)
        {
            String hex = Integer.toHexString(bytes[i] & 0xFF);
            if(hex.length() < 2)
            {
                sb.append(0);
            }
            sb.append(hex);
        }
        return sb.toString();
    }
    private static final  byte hexToByte(String inHex){
        return (byte)Integer.parseInt(inHex,16);
    }
    public static byte[] hexToByteArray(String inHex)
    {
        int hexlen = inHex.length();
        byte[] result;
        if(hexlen % 2 == 1)
        {
            //奇数
            hexlen++;
            result = new byte[(hexlen / 2)];
            inHex = "0" + inHex;
        }
        else
        {
            //偶数
            result = new byte[(hexlen / 2)];
        }
        int j = 0;
        for(int i = 0; i < hexlen; i += 2)
        {
            result[j] = hexToByte(inHex.substring(i, i + 2));
            j++;
        }
        return result;
    }
    public static String HexToString(String inHex)
    {
        byte[] b = hexToByteArray(inHex);
        return b==null ? "":new String(b);
    }


    private static final int GB = 1024 * 1024 *1024;
    //定义MB的计算常量
    private static final int MB = 1024 * 1024;
    //定义KB的计算常量
    private static final int KB = 1024;
    public static String bytes2kb(long bytes){
        DecimalFormat format = new DecimalFormat("###.00");
        if (bytes / GB >= 1){
            return format.format((double)bytes / GB) + "GB";
        }
        else if (bytes / MB >= 1){
            return format.format((double)bytes / MB) + "MB";
        }
        else if (bytes / KB >= 1){
            return format.format((double)bytes / KB) + "KB";
        }else {
            return bytes + "字节";
        }
    }

    public static void mFileToPcm(String Path,String OutputPath)
    {
        MediaExtractor me = new MediaExtractor();
        try{
            me.setDataSource(Path);
            me.selectTrack(0);
            MediaFormat inputFormat = me.getTrackFormat(0);// 设置轨道0
            if (!inputFormat.getString(MediaFormat.KEY_MIME).startsWith("audio")) {
                MLogCat.Print_Error("FileToPCMError","Not a valid audio file");
                return;
            }



        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("FileToPCMError",th);
        }
    }
    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16).toUpperCase();
    }
    public static String getDataMD5(byte[] b) {
        MessageDigest digest = null;
        ByteArrayInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new ByteArrayInputStream(b);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16).toUpperCase();
    }

    public static long getLongData(byte[] bArr, int i) {
        if (bArr == null) {
            return 0;
        }
        return ((((long) bArr[i]) & 255) << 24) + ((((long) bArr[i + 1]) & 255) << 16) + ((((long) bArr[i + 2]) & 255) << 8) + ((((long) bArr[i + 3]) & 255));
    }
    public static byte[] readAllBytes(InputStream insp) throws IOException {
        byte[] buffer = new byte[1024];
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();
        int read;
        while ((read = insp.read(buffer))!= -1){
            bOut.write(buffer,0,read);
        }
        return bOut.toByteArray();
    }
}
