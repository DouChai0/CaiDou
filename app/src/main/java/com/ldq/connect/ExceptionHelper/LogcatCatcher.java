package com.ldq.connect.ExceptionHelper;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;

import de.robv.android.xposed.XposedBridge;

public class LogcatCatcher {
    public static void StartCatch()
    {
        new Thread(()->{
            try {
                Process proc = Runtime.getRuntime().exec("logcat");
                InputStreamReader reader = new InputStreamReader(proc.getInputStream());
                BufferedReader mReader = new BufferedReader(reader);
                String Line;
                XposedBridge.log("小菜豆->Logcat_Start_Catch");
                while ((Line = mReader.readLine())!=null){
                    StringPool_Logcat.Add(Line);
                }

            } catch (IOException e) {
                StringPool_Logcat.Add("Can't execute logcat.");
            }
        }).start();
    }
}
