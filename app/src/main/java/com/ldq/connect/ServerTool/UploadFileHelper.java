package com.ldq.connect.ServerTool;

import com.ldq.Utils.HttpUtils;
import com.ldq.connect.MHookEnvironment;

import org.json.JSONObject;

import java.io.File;
import java.io.FileInputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;

public class UploadFileHelper {
    public static void UploadFile(String UploadKey,String UploadFilePath) throws Exception {
        long FileSize = new File(UploadFilePath).length();
        int UploadBlockCount = (int) (FileSize / (1024 * 1024) + 1);
        FileInputStream fInp = new FileInputStream(UploadFilePath);
        for(int i=0;i<UploadBlockCount;i++){
            byte[] buffer = new byte[1024 * 1024];
            int ReadByte = fInp.read(buffer);

            URL u = new URL(MHookEnvironment.ServerRoot_API +"upload");
            HttpURLConnection uc = (HttpURLConnection) u.openConnection();
            uc.setDoOutput(true);
            uc.setRequestMethod("POST");
            uc.addRequestProperty("UploadKey",UploadKey);
            OutputStream outWrite = uc.getOutputStream();
            outWrite.write(buffer,0,ReadByte);
            outWrite.flush();
            outWrite.close();

            InputStream inp =uc.getInputStream();
            byte[] GetBuffer = new byte[1024];
            while (inp.read(GetBuffer)!=-1);
            inp.close();
        }

        String Result = HttpUtils.getHtmlResourceByUrl(MHookEnvironment.ServerRoot_API +"upload?key="+UploadKey+"&size="+FileSize);
        JSONObject json = new JSONObject(Result);
        if(json.getInt("Code")==1){
            throw new UploadFailedException("服务器内部通信错误");
        }
        if(json.getInt("Code")==2){
            throw new UploadFailedException("上传的文件已损坏");
        }
        if(json.getInt("Code")==3){
            throw new UploadFailedException("服务器内部错误");
        }

    }
    static class UploadFailedException extends Exception{
        public UploadFailedException(String message){
            super(message);
        }
    }
}
