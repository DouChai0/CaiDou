package com.ldq.Utils;


import com.ldq.connect.MHookEnvironment;

import org.bouncycastle.math.ec.ECPoint;
import org.json.JSONObject;
import org.pzone.crypto.SM2;
import org.pzone.crypto.SM3;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.URL;
import java.net.URLConnection;
import java.nio.charset.StandardCharsets;

public class SMToolHelper {
    public static final SM2 sm2Tool = new SM2();
    private static final ECPoint PublicKey = sm2Tool.importPublicKey(DataUtils.hexToByteArray("04B20594A97BBC79664B883E47E924934E2AFB1553B085E2CA8596C091A279C18F2D9894888DEA59AAD3B35452847CD5BB5367C26EF72032150F6727B1AAA146EF"));
    public static String PackSendData(String OPName,String OPData,String OPKey) throws Exception {
        JSONObject json = new JSONObject();
        json.put("OPName",OPName);
        json.put("OPData",OPData);
        json.put("OPKey",DataUtils.bytesToHex(sm2Tool.encrypt(OPKey,PublicKey)));
        json.put("time",System.currentTimeMillis());

        String Pack = DataUtils.bytesToHex(json.toString().getBytes(StandardCharsets.UTF_8));
        String Hash = DataUtils.bytesToHex(SM3.hash(Pack.getBytes(StandardCharsets.UTF_8))) + Pack.hashCode();
        String Signature1 = DataUtils.bytesToHex(sm2Tool.encrypt(Hash,PublicKey));
        String Signature2 = DataUtils.bytesToHex(sm2Tool.encrypt(OPData.hashCode()+"",PublicKey));

        JSONObject FinalJson = new JSONObject();
        FinalJson.put("data",Pack);
        FinalJson.put("sign",Signature1);
        FinalJson.put("sign2",Signature2);
        String FinalString = DataUtils.bytesToHex(FinalJson.toString().getBytes(StandardCharsets.UTF_8));
        return FinalString;
    }
    public static String GetDataFromServer(String OPCode,String OPData){
        try{
            String RamdomKey = NameUtils.getRandomString(16);
            String PackSend = PackSendData(OPCode,OPData,RamdomKey);
            String BackData = HTTPGet(MHookEnvironment.ServerRoot_API +"submit?v="+PackSend);
            JSONObject json = new JSONObject(BackData);
            BackData = AES.Decrypt(json.getString("data"),RamdomKey);
            return BackData;
        }catch (Exception e){

            return null;
        }


    }
    public static String HTTPGet(String url) {
        StringBuffer buffer = new StringBuffer();
        Thread mThread = new Thread(()->{
            InputStreamReader isr = null;
            try {
                URL urlObj = new URL(url);
                URLConnection uc = urlObj.openConnection();
                uc.setConnectTimeout(10000);
                uc.setReadTimeout(10000);
                isr = new InputStreamReader(uc.getInputStream(), "utf-8");
                BufferedReader reader = new BufferedReader(isr);
                String line;
                while ((line = reader.readLine()) != null) {
                    buffer.append(line + "\n");
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                try {
                    if (null != isr) {
                        isr.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        });
        mThread.start();
        try {
            mThread.join();
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        if(buffer.length()==0)return buffer.toString();
        buffer.delete(buffer.length()-1,buffer.length());
        return buffer.toString();
    }

}
