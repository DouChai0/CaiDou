package com.ldq.connect.MainWorker.ProxyHandler;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Handler_Hide_Font_And_Bubble
{
    public static void _caller(Object ChatMsg)
    {
        try{
            if(MConfig.Get_Boolean("Main","MainSwitch","全局屏蔽气泡",false))
            {
                MField.SetField(ChatMsg,"vipBubbleID",(long)0);
                MField.SetField(ChatMsg,"vipBubbleDiyTextId",0);
                MField.SetField(ChatMsg,"vipSubBubbleId",0);
            }

            if(MConfig.Get_Boolean("Main","MainSwitch","全局屏蔽字体",false))
            {
                MMethod.CallMethod(ChatMsg,"saveExtInfoToExtStr",void.class,new Class[]{String.class,String.class},
                        "vip_font_id","0"
                );
                MMethod.CallMethod(ChatMsg,"saveExtInfoToExtStr",void.class,new Class[]{String.class,String.class},
                        "vip_sub_font_id","0"
                );
                MMethod.CallMethod(ChatMsg,"saveExtInfoToExtStr",void.class,new Class[]{String.class,String.class},
                        "font_animation_played","1"
                );
            }
        }catch (Throwable th)
        {
            MLogCat.Print_Error("FuckFontAndBubble",th);
        }
    }
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.vas.font.api.FontManagerConstants"), "parseMagicFont",
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","全局屏蔽字体",false))
                        {
                            param.setResult(false);
                        }
                    }
                });

    }

}
