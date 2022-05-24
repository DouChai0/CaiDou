package com.ldq.connect.HookConfig;

import com.ldq.connect.MHookEnvironment;

public class MixUtils {
    public static String MixName(String Name){
        return ""+(Name.hashCode() + MHookEnvironment.RndToken).hashCode()+MHookEnvironment.RndToken.hashCode();
    }
}
