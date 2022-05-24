package com.ldq.Utils;

import android.app.Activity;
import android.app.ActivityManager;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.res.ColorStateList;
import android.os.Handler;
import android.os.Looper;
import android.os.UserHandle;
import android.text.TextUtils;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Toast;

import com.github.megatronking.stringfog.annotation.StringFogIgnore;
import com.ldq.connect.MHookEnvironment;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Map;

@StringFogIgnore
public class Utils {
    public static String GetNowTime()
    {
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
        return (df.format(day));
    }
    public static String GetNowTime22()
    {
        Date day=new Date();
        SimpleDateFormat df = new SimpleDateFormat("yyyyMMddHHmmss");
        return (df.format(day));
    }
    public static <T> void ShowToast(T str)
    {
        new Handler(Looper.getMainLooper()).post(()->Toast.makeText(MHookEnvironment.MAppContext,String.valueOf(str),Toast.LENGTH_LONG).show());
    }
    public static byte[] readAllBytes(InputStream inp) throws IOException {
        ByteArrayOutputStream bOut = new ByteArrayOutputStream();

        byte[] buffer = new byte[1024];
        int Read;
        while ((Read = inp.read(buffer))!=-1){
            bOut.write(buffer,0,Read);
        }
        inp.close();
        return bOut.toByteArray();
    }
    public static void ShowToastShort(String str)
    {
        new Handler(Looper.getMainLooper()).post(()->Toast.makeText(MHookEnvironment.MAppContext,str,Toast.LENGTH_SHORT).show());
    }
    public static int GetUserID()
    {
        try {
            return MMethod.CallMethod(null,UserHandle.class,"myUserId",int.class,new Class[0]);
        } catch (Exception e) {
            return 0;
        }
    }

    public static int dip2px(Context context, float dpValue) {
        if(dpValue>0)
        {
            final float scale = context.getResources().getDisplayMetrics().density;
            return (int) (dpValue * scale + 0.5f);
        }else
        {
            float f = -dpValue;
            final float scale = context.getResources().getDisplayMetrics().density;
            return -(int) (f * scale + 0.5f);
        }

    }

    public static int dip2sp(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density /
                context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (dpValue * scale + 0.5f);
    }

    public static int px2sp(Context context, float pxValue) {
        final float fontScale = context.getResources().getDisplayMetrics().scaledDensity;
        return (int) (pxValue / fontScale + 0.5f);
    }

    public static String AddStringTo(String t)
    {
        if(t.length()>=10)return t;
        while (t.length()<10)
        {
            t = "0"+t;
        }
        return t;
    }

    public static Activity CacheBaseShowActivity;
    public static Activity GetThreadActivity()
    {
        try
        {
            Class clazz = Class.forName("android.app.ActivityThread");
            Object ActivityThread = MField.GetField(null,clazz, "sCurrentActivityThread",1);
            Field activitiesField = clazz.getDeclaredField("mActivities");//全局Activity
            activitiesField.setAccessible(true);
            Map activities = (Map) activitiesField.get(ActivityThread);
            for(Object activityRecord: activities.values())
            {
                Class activityRecordClass = activityRecord.getClass();
                Field pausedField = activityRecordClass.getDeclaredField("paused");
                pausedField.setAccessible(true);
                if(!pausedField.getBoolean(activityRecord))//如果Activity处于暂停状态就不取出
                {
                    Field activityField = activityRecordClass.getDeclaredField("activity");
                    activityField.setAccessible(true);
                    Activity act = (Activity) activityField.get(activityRecord);
                    if(act.getClass().getName().contains("SplashActivity"))CacheBaseShowActivity = act;
                    return act;
                }
            }
        }
        catch(Throwable e)
        {
            e.printStackTrace();
        }
        return null;
    }
    public static void SetTextClipboard(String str)
    {
        ClipboardManager manager = (ClipboardManager) MHookEnvironment.MAppContext.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData data = ClipData.newPlainText("text",str);
        manager.setPrimaryClip(data);
        Thread.currentThread();
    }
    public static void SetSecureFlags(Window willSetWindow)
    {

        try {
            int fl = WindowManager.LayoutParams.FLAG_SECURE;
            final WindowManager.LayoutParams attrs = willSetWindow.getAttributes();
            attrs.flags = (attrs.flags&~fl) | (fl&fl);

            int mForcedWindowFlags = MField.GetField(willSetWindow,"mForcedWindowFlags",int.class);
            mForcedWindowFlags |= fl;
            MField.SetField(willSetWindow,"mForcedWindowFlags",mForcedWindowFlags);
            MMethod.CallMethod(willSetWindow,"dispatchWindowAttributesChanged",void.class,new Class[]{
                    MClass.loadClass("android.view.WindowManager$LayoutParams")
            },attrs);
        } catch (Exception e) {
            willSetWindow.addFlags(WindowManager.LayoutParams.FLAG_SECURE);
            e.printStackTrace();
        }
    }
    public static boolean isTopActivity(String packageName)//检测制定包名是不是在当前最顶层,用于判断应用是不是前台显示
    {
        ActivityManager __am = (ActivityManager) MHookEnvironment.MAppContext.getApplicationContext().getSystemService(Context.ACTIVITY_SERVICE);
        List<ActivityManager.RunningAppProcessInfo> __list = __am.getRunningAppProcesses();
        if(__list.size() == 0) return false;
        for(ActivityManager.RunningAppProcessInfo __process:__list)
        {
            if(__process.importance == ActivityManager.RunningAppProcessInfo.IMPORTANCE_FOREGROUND &&  __process.processName.equals(packageName))
            {
                return true;
            }
        }
        return false;
    }
    public static String secondToTime(long second)
    {
        if(second == 0) return "0秒";
        long days = second / 86400;
        second = second % 86400;
        long hours = second / 3600;
        second = second % 3600;
        long minutes = second / 60;
        second = second % 60;
        return(days == 0 ? "" : days + "天") + (hours == 0 ? "" : hours + "小时") + (minutes == 0 ? "" : minutes + "分钟") + (second == 0 ? "" : second + "秒");
    }
    public static ColorStateList createColorStateList(int selected, int normal) {
        int[] colors = new int[] { selected,  normal};
        int[][] states = new int[2][];
        states[0] = new int[] { android.R.attr.state_pressed , android.R.attr.state_enabled};
        states[1] = new int[] { android.R.attr.state_enabled };
        ColorStateList colorList = new ColorStateList(states, colors);
        return colorList;
    }
    public static String getCurrentCallStacks() {
        StackTraceElement[] elements=Thread.currentThread().getStackTrace();
        StringBuilder builder = new StringBuilder();
        for(StackTraceElement ele : elements)
        {
            builder.append(ele.getClassName()).append(".")
                    .append(ele.getMethodName()).append("() (")
                    .append(ele.getFileName()).append(":")
                    .append(ele.getLineNumber()).append(")")
                    .append("\n");
        }
        return builder.toString();
    }
    public static String MillionToTimeStr(long Mill)
    {
        Date d = new Date(Mill);
        SimpleDateFormat f=new SimpleDateFormat("MM-dd HH:mm:ss");
        return f.format(d);
    }

    public static int getStatusBarHeight(Context context) {
        int result = 0;
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            result = context.getResources().getDimensionPixelSize(resourceId);
        }
        return result;
    }

    public static String FileIDGetDown(String FileID) {
        String Ret = HttpUtils.getHtmlResourceByUrl(MHookEnvironment.ServerRoot_API + "getDownURL?fileid=" + FileID);
        try {
            JSONObject retJson = new JSONObject(Ret);
            String URIPath = retJson.getString("DownURL");
            if (TextUtils.isEmpty(URIPath)) {
                return "";
            }
            if (URIPath.startsWith("/")) URIPath = URIPath.substring(1);
            return MHookEnvironment.ServerRoot_CDN + URIPath;
        } catch (JSONException e) {
            return "";
        }
    }


}
