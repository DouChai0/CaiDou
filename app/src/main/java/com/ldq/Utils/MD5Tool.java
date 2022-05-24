package com.ldq.Utils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class MD5Tool {
    private static MessageDigest md;
    static {
        try {
            //初始化摘要对象
            md = MessageDigest.getInstance("md5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
    public static void main(String[] args) throws IOException {
        System.out.println(generateMD5ForString("test"));
        System.out.println(generateMD5ForFile(new File("1.jpg")));
 
    }
    //获得字符串的md5值
    public static String generateMD5ForString(String str){
        //更新摘要数据
        md.update(str.getBytes());
        //生成摘要数组
        byte[] digest = md.digest();
        //清空摘要数据，以便下次使用
        md.reset();
        return formatByteArrayTOString(digest);
    }
    //获得文件的md5值
    public static String generateMD5ForFile(File file) throws IOException {
        //创建文件输入流
        FileInputStream fis = new FileInputStream(file);
        //将文件中的数据写入md对象
        byte[] buffer = new byte[1024];
        int len=-1;
        while ((len = fis.read(buffer)) != -1) {
            md.update(buffer, 0, len);
        }
        fis.close();
        //生成摘要数组
        byte[] digest = md.digest();
        //清空摘要数据，以便下次使用
        md.reset();
        return formatByteArrayTOString(digest);
    }
    //将摘要字节数组转换为md5值
    public static String formatByteArrayTOString(byte[] digest) {
        //创建sb用于保存md5值
        StringBuffer sb = new StringBuffer();
        int temp;
        for (int i=0;i<digest.length;i++) {
            //将数据转化为0到255之间的数据
            temp=digest[i]&0xff;
            if (temp < 16) {
                sb.append(0);
            }
            //Integer.toHexString(temp)将10进制数字转换为16进制
            sb.append(Integer.toHexString(temp));
        }
        return sb.toString();
    }
}