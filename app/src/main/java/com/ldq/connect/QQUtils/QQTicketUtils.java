package com.ldq.connect.QQUtils;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;

public class QQTicketUtils
{
    public static Object GetAppRuntime() throws Exception {
        Object sApplication = MMethod.CallMethod(null, MClass.loadClass("com.tencent.common.app.BaseApplicationImpl"),"getApplication",
                MClass.loadClass("com.tencent.common.app.BaseApplicationImpl"),new Class[0],new Object[0]
        );
        Object AppRuntime = MMethod.CallMethod(sApplication,sApplication.getClass(),"getRuntime", MClass.loadClass("mqq.app.AppRuntime"),new Class[0],new Object[0]);
        return AppRuntime;
    }
    public static Object GetTicketManager() throws Exception {
        return MMethod.CallMethod(GetAppRuntime(),"getManager", MClass.loadClass("mqq.manager.Manager"),new Class[]{int.class},2);
    }
    public static String GetSkey()
    {
        try{
            Object TickManager = GetTicketManager();
            return MMethod.CallMethod(TickManager,"getSkey",String.class,new Class[]{String.class},BaseInfo.GetCurrentUin());
        }
        catch (Exception e)
        {
            MLogCat.Print_Error("GetSkey",e);
            return "";
        }
    }
    public static String GetPsKey(String Domain)
    {
        try{
            Object TickManager = GetTicketManager();
            return MMethod.CallMethod(TickManager,"getPskey",String.class,new Class[]{String.class,String.class},BaseInfo.GetCurrentUin(),Domain);
        }
        catch (Exception e)
        {
            MLogCat.Print_Error("GetPsKey",e);
            return "";
        }
    }
    public static String GetG_TK(String Url){
        String p_skey = GetPsKey(Url);
        int hash = 5381;
        for(int i = 0; i < p_skey.length(); ++i)
        {
            hash += (hash << 5) + p_skey.charAt(i);
        }
        return String.valueOf(hash & 0x7fffffff);
    }
    public static String GetSuperKey()
    {
        try{
            Object TickManager = GetTicketManager();
            return MMethod.CallMethod(TickManager,"getSuperkey",String.class,new Class[]{String.class},BaseInfo.GetCurrentUin());
        }
        catch (Exception e)
        {
            MLogCat.Print_Error("GetSuperKey",e);
            return "";
        }
    }
    public static String getPt4Token(String Domain)
    {
        try{
            Object TickManager = GetTicketManager();
            return MMethod.CallMethod(TickManager,"getPt4Token",String.class,new Class[]{String.class,String.class},BaseInfo.GetCurrentUin(),Domain);
        }
        catch (Exception e)
        {
            MLogCat.Print_Error("getPt4Token",e);
            return "";
        }
    }
    public static String getBKN()
    {
        int hash = 5381;
        String Skey = GetSkey();
        byte[] b = Skey.getBytes();
        for (int i = 0, len = b.length; i < len; ++i)
            hash += (hash << 5) + (int)b[i];
        return ""+(hash & 2147483647);
    }
}
