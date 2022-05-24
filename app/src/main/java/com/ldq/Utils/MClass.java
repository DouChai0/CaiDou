package com.ldq.Utils;

import com.ldq.connect.MHookEnvironment;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.HashMap;

public final class MClass
{
    public static ClassLoader classloader;
    public static HashMap<String,Class> NameMap = new HashMap<>();
    public static Class loadClass(String ClassName) throws ClassNotFoundException {
        if(NameMap.containsKey(ClassName))
        {
            return NameMap.get(ClassName);
        }
        if(classloader==null)
        {
            if(MHookEnvironment.mLoader!=null)
            {
                Method m;

                classloader = MHookEnvironment.mLoader;
            }
            else
            {
                classloader = MClass.class.getClass().getClassLoader();
            }
        }
        switch (ClassName)
        {
            case "void": return void.class;
            case "int": return int.class;
            case "long":return long.class;
            case "short":return short.class;
            case "byte":return byte.class;
            case "boolean":return boolean.class;
            case "float":return float.class;
            case "double":return double.class;
            default:
            {
                Class mClz = classloader.loadClass(ClassName);
                NameMap.put(ClassName,mClz);
                return mClz;
            }
        }
    }
    public static Class _loadClass(String ClassName) {
        try{
            if(NameMap.containsKey(ClassName))
            {
                return NameMap.get(ClassName);
            }
            if(classloader==null)
            {
                if(MHookEnvironment.mLoader!=null)
                {
                    classloader = MHookEnvironment.mLoader;
                }
                else
                {
                    classloader = MClass.class.getClass().getClassLoader();
                }
            }
            switch (ClassName)
            {
                case "void": return void.class;
                case "int": return int.class;
                case "long":return long.class;
                case "short":return short.class;
                case "byte":return byte.class;
                case "boolean":return boolean.class;
                case "float":return float.class;
                case "double":return double.class;
                default:
                {
                    Class mClz = classloader.loadClass(ClassName);
                    NameMap.put(ClassName,mClz);
                    return mClz;
                }
            }
        }catch (Throwable th)
        {
            MLogCat.Print_Error("ClassLoader","Can't find class "+ClassName);
        }
        return null;

    }
    public static <T> T CallConstrutor(Class CallClass,Class[] ParamsTYPE,Object... params) throws Exception {
        Constructor cons = CallClass.getDeclaredConstructor(ParamsTYPE);
        cons.setAccessible(true);
        return (T) cons.newInstance(params);
    }
}
