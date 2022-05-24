package com.ldq.connect.MainWorker.WidgetHook;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;

import com.ldq.Utils.MClass;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Chat_Background {
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.aio.ChatBackground"), "a", Context.class,
                String.class,int.class,MClass.loadClass("com.tencent.mobileqq.activity.aio.ChatBackground"),SharedPreferences.class,String.class
                , new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                /*
                String CurrentUin = QQSessionUtils.GetCurrentGroupUin();
                if(CurrentUin.equals("960081813") || CurrentUin.equals("239704249")){
                    Drawable dr = MField.GetField(param.args[3],"c",Drawable.class);
                    if(dr!=null){
                        Bitmap bitmapDrawable = ((BitmapDrawable)dr).getBitmap();
                        Bitmap.Config bitmapConfig = bitmapDrawable.getConfig();
                        Bitmap NewBitmap = bitmapDrawable.copy(bitmapConfig,true);
                        if (bitmapConfig == null) {
                            bitmapConfig = Bitmap.Config.ARGB_8888;
                        }
                        Canvas canvas = new Canvas(NewBitmap);
                        Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);//消除锯齿
                        paint.setDither(true); //获取跟清晰的图像采样
                        paint.setFilterBitmap(true);//过滤一些
                        paint.setColor(Color.YELLOW);
                        paint.setTextSize(50);
                        paint.setShadowLayer(5f, 0f, 1f, Color.BLUE);//阴影制作半径，x偏移量，y偏移量，阴影颜色
                        canvas.drawText("本群常犯群规",0,1000,paint);
                        canvas.drawText("1.复读,复读只要复读均有几率禁言,无论第几次",0, Utils.dip2px(MHookEnvironment.MAppContext,20)+1000,paint);
                        canvas.drawText("2.贴图.贴图必被禁言,不要以为偷偷贴就没事",0, 2*Utils.dip2px(MHookEnvironment.MAppContext,20)+1000,paint);
                        canvas.drawText("3.开脚本,只要开了,就算你马上关也会被处理",0, 3*Utils.dip2px(MHookEnvironment.MAppContext,20)+1000,paint);
                        canvas.drawText("4.询问查询等违法违禁东西,就算你只是问,一样违规",0, 4*Utils.dip2px(MHookEnvironment.MAppContext,20)+1000,paint);

                        MField.SetField(param.args[3],"c",new BitmapDrawable(null,NewBitmap));
                    }
                }

                 */
            }
        });
    }
    public static Bitmap drawTextToBitmap(Context mContext, Bitmap bit, String mText, String str3) {

        try {
            Resources resources = mContext.getResources();
            float scale = 20;
            // Bitmap bitmap = BitmapFactory.decodeResource(resources, resourceId);
            Bitmap bitmap = bit;
            Bitmap.Config bitmapConfig = bitmap.getConfig();
            if (bitmapConfig == null) {
                bitmapConfig = Bitmap.Config.ARGB_8888;
            }
            bitmap = bitmap.copy(bitmapConfig, true);
            Canvas canvas = new Canvas(bitmap);
            Paint paint = new Paint(Paint.ANTI_ALIAS_FLAG);//消除锯齿
            paint.setDither(true); //获取跟清晰的图像采样
            paint.setFilterBitmap(true);//过滤一些
            paint.setColor(Color.YELLOW);
            paint.setTextSize((int) (2 * scale));
            paint.setShadowLayer(1f, 0f, 1f, Color.DKGRAY);//阴影制作半径，x偏移量，y偏移量，阴影颜色
            Rect bounds = new Rect();
            paint.getTextBounds(mText, 0, mText.length(), bounds);
            int y = (bitmap.getHeight() + bounds.height()) / 4;
            int x = 0;
            //Toast.makeText(MyCaremaActivity.this, "x" + bitmap.getHeight() + "y" + y + "y*sace" + y * scale + "sace" + scale, Toast.LENGTH_LONG).show();
            //Log.v("===位置", "x" + bitmap.getHeight() + "y" + y + "y*sace" + y * scale + "sace" + scale);
            canvas.drawText(mText, x * scale, y * scale, paint);
            canvas.drawText(str3, x * scale, y * scale + 25, paint);
//       canvas.drawText(mText, x * scale, 210, paint);
//       canvas.drawText(str3, x * scale,210 + 25 , paint);
            //Log.v("===合成图片", "====ok" + mText);
            return bitmap;
        } catch (Exception e) {
            // TODO: handle exception
            return null;
        }
    }
}
