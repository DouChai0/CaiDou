package com.ldq.connect.MainWorker.LittileHook;

import com.ldq.Utils.MClass;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;

import java.net.URLDecoder;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Unlock_Website_Limit {
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.smtt.sdk.WebView"), "loadUrl", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                String loadUrl = (String) param.args[0];
                //MLogCat.Print_Debug(Log.getStackTraceString(new Throwable()));
                if(MConfig.Get_Boolean("Main","MainSwitch","解除网址限制",false))
                {

                    if(loadUrl.startsWith("https://c.pc.qq.com/middlem.html?") || loadUrl.startsWith("https://c.pc.qq.com/index.html?"))
                    {
                        String RedictUrl = GetStringMiddle(loadUrl,"url=","&");
                        if(RedictUrl!=null)
                        {
                            String SourceUrl = URLDecoder.decode(RedictUrl);
                            param.args[0] = SourceUrl;

                            Utils.ShowToast("已解除对网址"+SourceUrl+"的限制");
                        }
                    }
                }
            }
        });
    }
    private static String GetStringMiddle(String str,String before,String after)
    {
        int index1 = str.indexOf(before);
        if(index1==-1)return null;
        int index2 = str.indexOf(after,index1+before.length());
        if(index2==-1)return null;
        return str.substring(index1+before.length(),index2);
    }
}
