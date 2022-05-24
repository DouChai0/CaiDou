package com.ldq.connect.HookConfig;

import com.ldq.connect.MHookEnvironment;

import java.io.File;

public class FlagUtils {
    private static boolean RemoveFlag;
    public static boolean isRemoveConfig()
    {
        if(RemoveFlag)return RemoveFlag;
        File f = new File(MHookEnvironment.PublicStorageModulePath + "reset");
        if(f.exists())
        {
            f.delete();
            RemoveFlag = true;
            return true;
        }
        return false;
    }
}
