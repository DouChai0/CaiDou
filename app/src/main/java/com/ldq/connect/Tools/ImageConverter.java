package com.ldq.connect.Tools;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Matrix;
import android.os.Handler;
import android.os.Looper;

import com.ldq.Utils.Utils;

public class ImageConverter {
    public interface ConvertSuccess{
        void onSuccess(Bitmap bitmap);
    }
    public static void compositePicture(Context context,Bitmap imgScr, Bitmap imgBack, ConvertSuccess callback) {
        ProgressDialog dialog = new ProgressDialog(context,3);
        dialog.setCancelable(false);
        dialog.setTitle("正在处理");
        dialog.show();
        new Thread(()->{
            try{
                Bitmap img1 = imgScr;
                Bitmap img2 = imgBack;
                new Handler(Looper.getMainLooper()).post(()->dialog.setMessage("正在处理尺寸..(1/6)"));

                int width = Math.min(img2.getWidth(), img1.getWidth());
                int height = Math.min(img2.getHeight(), img1.getHeight());
                //预处理Bitmap
                Matrix matrix = new Matrix();
                matrix.postScale(width, height);
                img1 = Bitmap.createScaledBitmap(img1,width,height,true);
                img2 = Bitmap.createScaledBitmap(img2,width,height,true);
                img1 = img1.copy(Bitmap.Config.ARGB_8888,true);
                img2 = img2.copy(Bitmap.Config.ARGB_8888,true);

                new Handler(Looper.getMainLooper()).post(()->dialog.setMessage("正在使前景更亮..(2/6)"));
                // 使Bitmap1更亮
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int color = img1.getPixel(x,y);
                        int[] argb = getARGB(color);

                        int gray = (int) (argb[1] * 0.299 + 0.578 * argb[2] + 0.114 * argb[3]);

                        gray = 120 + gray / 2;
                        img1.setPixel(x,y,getCARGB(255,255-gray,255-gray,255-gray));
                    }
                }
                new Handler(Looper.getMainLooper()).post(()->dialog.setMessage("正在使背景更暗..(3/6)"));
                // 使Bitmap2更暗
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int color = img2.getPixel(x,y);
                        int[] argb = getARGB(color);

                        int gray = (int) (argb[1] * 0.299 + 0.578 * argb[2] + 0.114 * argb[3]);
                        gray = gray /2;
                        img2.setPixel(x,y,getCARGB(255,gray,gray,gray));
                    }
                }

                new Handler(Looper.getMainLooper()).post(()->dialog.setMessage("正在创建新图层..(4/6)"));
                Bitmap NewImage = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888);


                new Handler(Looper.getMainLooper()).post(()->dialog.setMessage("正在叠加图层..(5/6)"));
                //填充前景
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int gray = Color.red(img1.getPixel(x,y)) +  Color.red(img2.getPixel(x,y));
                        gray = Math.min(gray, 255);
                        int alpha = 255;
                        NewImage.setPixel(x,y,getCARGB(alpha, gray,gray,gray));
                    }
                }
                new Handler(Looper.getMainLooper()).post(()->dialog.setMessage("正在填充透明度..(6/6)"));
                //填充背景透明
                for (int x = 0; x < width; x++) {
                    for (int y = 0; y < height; y++) {
                        int pixel = NewImage.getPixel(x,y);
                        int pixel_red = Color.red(pixel);

                        int bit_background = img2.getPixel(x,y);
                        NewImage.setPixel(x,y,getCARGB(pixel_red, (int) (255 / ((float)pixel_red /(float)Color.red(bit_background))), (int) (255 / ((float)pixel_red /(float)Color.red(bit_background))), (int) (255 / ((float)pixel_red /(float)Color.red(bit_background)))));
                    }
                }

                new Handler(Looper.getMainLooper()).post(()->  callback.onSuccess(NewImage));
            }catch (Throwable e){
                Utils.ShowToast("处理图片时发生错误:\n"+e);
            }finally {
                new Handler(Looper.getMainLooper()).post(()-> dialog.dismiss());
            }
        }).start();



    }
    public static int[] getARGB(int cARGB) {
        int alpha = (cARGB >> 24)& 0xff; //透明度通道
        int red = (cARGB >> 16) &0xff;
        int green = (cARGB >> 8) &0xff;
        int blue = cARGB & 0xff;
        return new int[]{alpha, red, green, blue};
    }
    public static int getCARGB(int alpha, int red, int green, int blue) {
        return (alpha << 24) | (red<< 16) | (green << 8) | blue;
    }

}
