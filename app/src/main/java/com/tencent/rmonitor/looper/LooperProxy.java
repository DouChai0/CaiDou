package com.tencent.rmonitor.looper;

import android.os.Handler;
import android.os.Looper;

import com.ldq.connect.ExceptionHelper.CatchInstance;

public class LooperProxy implements Runnable{
    public static void Proxy(){
        new Handler(Looper.getMainLooper())
                .post(new LooperProxy());
    }

    @Override
    public void run() {
        while (true){
            try {
                Looper.loop();
                break;
            }catch (Exception e){
                CatchInstance.ICatchEx(Thread.currentThread(),e);
            }catch (Error th){
                CatchInstance.ThreadToast("小菜豆->QQ主线程Error错误,即将退出:\n"+th);
                CatchInstance.ICatchEx(Thread.currentThread(),th);

                throw th;
            }catch (Throwable th){
                CatchInstance.ThreadToast("小菜豆->QQ主线程未知错误,即将退出:\n"+th);
                CatchInstance.ICatchEx(Thread.currentThread(),th);
                throw th;
            }
        }
    }
}
