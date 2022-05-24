package com.ldq.connect.HookConfig;

import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseInfo;

public class ConfigPathBuilder {
    private static String ConfigRootPath;
    static {
        ConfigRootPath = MHookEnvironment.PublicStorageModulePath + "配置文件目录/";
    }
    public static String Get_Current_Path_Set(){
        boolean Open_Uin_Separate = GlobalConfig.Get_Boolean("Separate_Config",false);
        if(!Open_Uin_Separate) return ConfigRootPath + "Global/";
        String CurrentUin = BaseInfo.GetCurrentUin();
        return ConfigRootPath + CurrentUin + "/";
    }
}
