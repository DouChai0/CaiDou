package com.ldq.Utils;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.widget.LinearLayout;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.net.CookieHandler;
import java.net.CookieManager;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLConnection;
import java.util.concurrent.atomic.AtomicLong;

public class HttpUtils
{
    static CookieManager manager = new CookieManager();
    static {
        CookieHandler.setDefault(manager);
    }
    public static String getHtmlResourceByUrl(String url) {
        StringBuffer buffer = new StringBuffer();
        Thread mThread = new Thread(()->{
            InputStreamReader isr = null;
            try {
                URL urlObj = new URL(url);
                URLConnection uc = urlObj.openConnection();
                uc.setConnectTimeout(10000);
                uc.setReadTimeout(10000);
                isr = new InputStreamReader(uc.getInputStream(), "utf-8");
                BufferedReader reader = new BufferedReader(isr); //缓冲
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

    public static String getHtmlResourceByUrl_Agent(String url) {
        StringBuffer buffer = new StringBuffer();
        Thread mThread = new Thread(()->{
            InputStreamReader isr = null;
            try {
                URL urlObj = new URL(url);
                HttpURLConnection uc = (HttpURLConnection) urlObj.openConnection();
                uc.setConnectTimeout(10000);
                uc.setReadTimeout(10000);
                uc.setRequestProperty("user-agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36");

                isr = new InputStreamReader(uc.getInputStream(), "utf-8");

                BufferedReader reader = new BufferedReader(isr); //缓冲
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
    public static String PostData(String Url,byte[] PostData)
    {
        try {
            if(Thread.currentThread().getName().equals("main"))
            {
                StringBuilder bu = new StringBuilder();
                Thread th = new Thread(()->{
                    String Ret = PostData(Url,PostData);
                    bu.append(Ret);
                });
                th.start();
                th.join();
                return bu.toString();
            }
            URL urlObjUrl=new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) urlObjUrl.openConnection();

            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream os=conn.getOutputStream();


            os.write(PostData);
            os.flush();
            os.close();
            InputStream iStream=conn.getInputStream();
            byte[] b=new byte[1024];
            int len;
            StringBuilder sb=new StringBuilder();
            while ((len=iStream.read(b))!=-1) {
                sb.append(new String(b,0,len));
            }
            return sb.toString();
        } catch (Exception e) {
            MLogCat.Print_Error("PostData",e);
        }
        return null;
    }
    public static String PostDataPY(String Url,byte[] PostData)
    {
        try {
            if(Thread.currentThread().getName().equals("main"))
            {
                StringBuilder bu = new StringBuilder();
                Thread th = new Thread(()->{
                    String Ret = PostDataPY(Url,PostData);
                    bu.append(Ret);
                });
                th.start();
                th.join();
                return bu.toString();
            }
            URL urlObjUrl=new URL(Url);
            HttpURLConnection conn = (HttpURLConnection) urlObjUrl.openConnection();
            conn.addRequestProperty("Referer","http://peiyin.xunfei.cn/");
            conn.addRequestProperty("Origin","http://peiyin.xunfei.cn");
            conn.addRequestProperty("Content-Length",""+PostData.length);
            conn.addRequestProperty("Content-type","application/json");
            conn.addRequestProperty("User-Agent","Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/89.0.4389.114 Safari/537.36");
            conn.setRequestMethod("POST");
            conn.setDoOutput(true);
            conn.setDoInput(true);
            OutputStream os=conn.getOutputStream();


            os.write(PostData);
            os.flush();
            os.close();
            InputStream iStream=conn.getInputStream();
            byte[] b=new byte[1024];
            int len;
            StringBuilder sb=new StringBuilder();
            while ((len=iStream.read(b))!=-1) {
                sb.append(new String(b,0,len));
            }
            return sb.toString();
        } catch (Exception e) {
            MLogCat.Print_Error("PostData",e);
        }
        return null;
    }
    public static long GetFileLength(String Url)
    {
        AtomicLong mLong = new AtomicLong();
        Thread mThread = new Thread(()->{
            InputStreamReader isr = null;
            try {
                URL urlObj = new URL(Url);
                URLConnection uc = urlObj.openConnection();

                mLong.set(uc.getContentLengthLong());
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
        return mLong.get();
    }
    public static int downlaodFile(String urlStr, String path){
        File f = new File(path);
        return downlaodFile(urlStr,f.getParentFile().getAbsolutePath()+"/",f.getName());
    }
    public static int downlaodFile(String urlStr, String path, String fileName){
        //MLogCat.Print_Debug(urlStr);
        if(Thread.currentThread().getName().equals("main"))
        {
            Thread t = new Thread(()->downlaodFile(urlStr,path,fileName));
            t.setName("DOWN_THREAD");
            t.start();
            try {
                t.join();
            } catch (Throwable e) {
                e.printStackTrace();
            }
            return 0;
        }
        InputStream input = null;
        try {
            FileUtil fileUtil = new FileUtil();
            if (fileUtil.isFileExist(path + fileName)) {
                return 1;
            } else {
                input = getInputStearmFormUrl(urlStr);
                File resultFile = fileUtil.write2SDFromInput(path,fileName,input);
                if (resultFile == null)
                    return -1;
            }
        } catch (Exception e) {
            new File(path,fileName).delete();
            return -1;
        }
        finally {
            try {
                input.close();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        return  0;
    }

    private static URL url = null;

    public static InputStream getInputStearmFormUrl(String urlStr) throws IOException {
        url = new URL(urlStr);
        HttpURLConnection urlConn = (HttpURLConnection) url.openConnection();
        urlConn.setConnectTimeout(1000);
        InputStream input = urlConn.getInputStream();
        return input;
    }


    public String get(String url)
    {
        StringBuffer buffer = new StringBuffer();
        InputStreamReader isr = null;
        try {
            URL urlObj = new URL(url);
            URLConnection uc = urlObj.openConnection();
            uc.setConnectTimeout(10000);
            uc.setReadTimeout(10000);
            isr = new InputStreamReader(uc.getInputStream(), "utf-8");
            BufferedReader reader = new BufferedReader(isr); //缓冲
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
        if(buffer.length()==0)return buffer.toString();
        buffer.delete(buffer.length()-1,buffer.length());
        return buffer.toString();
    }

    public static void ProgressDownload(String url, String filepath, Runnable callback, Context context)
    {
        AlertDialog al = new AlertDialog.Builder(context,3).create();
        al.setTitle("下载中...");
        LinearLayout layout = new LinearLayout(context);
        layout.setOrientation(LinearLayout.VERTICAL);
        File mSaveFile = new File(filepath);
        if(!mSaveFile.getParentFile().exists())mSaveFile.getParentFile().mkdirs();

        TextView mFileName = new TextView(context);
        mFileName.setTextColor(Color.BLACK);
        mFileName.setTextSize(18);
        mFileName.setText("文件名:"+mSaveFile.getName());
        layout.addView(mFileName);

        TextView mAllSize = new TextView(context);
        mAllSize.setTextSize(18);
        mAllSize.setTextColor(Color.BLACK);
        layout.addView(mAllSize);

        TextView mDownedSize = new TextView(context);
        mDownedSize.setTextSize(18);
        mDownedSize.setTextColor(Color.BLACK);
        layout.addView(mDownedSize);

        al.setCancelable(false);
        al.setView(layout);
        al.show();


        new Thread(()->{
            try{
                URL u = new URL(url);
                URLConnection conn = u.openConnection();
                long msize = conn.getContentLengthLong();
                long readed = 0;
                new Handler(Looper.getMainLooper()).post(()->mAllSize.setText("文件大小:"+((int)msize/1024)+"KB"));
                long finalReaded = readed;
                new Handler(Looper.getMainLooper()).post(()->mDownedSize.setText("当前已下载:"+((int) finalReaded /1024)+"KB"));

                conn.setConnectTimeout(10000);
                conn.setReadTimeout(10000);

                InputStream inp = conn.getInputStream();
                byte[] buffer = new byte[1024];

                FileOutputStream fos = new FileOutputStream(filepath);

                int readthis;
                while ((readthis=inp.read(buffer))!=-1)
                {
                    readed+=readthis;
                    long finalReaded1 = readed;
                    new Handler(Looper.getMainLooper()).post(()->mDownedSize.setText("当前已下载:"+((int) finalReaded1 /1024)+"KB"));
                    fos.write(buffer,0,readthis);
                }
                fos.close();
                inp.close();
                new Handler(Looper.getMainLooper()).post(()->al.dismiss());
                callback.run();
            }
            catch (Throwable th)
            {
                Utils.ShowToast("下载失败:\n"+th);
                new File(filepath).delete();
                new Handler(Looper.getMainLooper()).post(()->al.dismiss());
            }


        }).start();


    }
}
