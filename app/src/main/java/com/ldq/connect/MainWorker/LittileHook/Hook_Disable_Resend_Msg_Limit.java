package com.ldq.connect.MainWorker.LittileHook;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.connect.HookConfig.MConfig;

import java.util.Collection;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Disable_Resend_Msg_Limit {
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.ForwardTroopListFragment"), "a",
                MClass.loadClass("com.tencent.mobileqq.selectmember.ResultRecord"),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","转发无限制",false))
                        {
                            LinkedHashMap mMap = (LinkedHashMap) MField.GetField(param.thisObject,"a", Map.class);
                            if(mMap.size()==9)
                            {
                                mMap.put("10000",null);
                            }
                        }

                    }
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","转发无限制",false))
                        {
                            LinkedHashMap mMap = (LinkedHashMap) MField.GetField(param.thisObject,"a", Map.class);
                            if(mMap.size()==11)
                            {
                                mMap.remove("10000");
                            }
                        }

                    }
                }
        );

        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.ForwardFriendListActivity"), "a",
                MClass.loadClass("com.tencent.mobileqq.selectmember.ResultRecord"),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","转发无限制",false))
                        {
                            LinkedHashMap mMap = (LinkedHashMap) MField.GetField(param.thisObject,"a", Map.class);
                            if(mMap.size()==9)
                            {
                                mMap.put("10000",null);
                            }
                        }

                    }
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","转发无限制",false))
                        {
                            LinkedHashMap mMap = (LinkedHashMap) MField.GetField(param.thisObject,"a", Map.class);
                            if(mMap.size()==11)
                            {
                                mMap.remove("10000");
                            }
                        }

                    }
                }
        );

        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.ForwardRecentActivity"), "add2ForwardTargetList",
                MClass.loadClass("com.tencent.mobileqq.selectmember.ResultRecord"),
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","转发无限制",false))
                        {
                            LinkedHashMap mMap = (LinkedHashMap) MField.GetField(param.thisObject,"mForwardTargetMap", Map.class);
                            Collection s = mMap.values();
                            Object[] sObj = s.toArray();
                            if(mMap.size()==9)
                            {
                                mMap.put("10000",sObj[sObj.length-1]);
                            }
                        }

                    }
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        if(MConfig.Get_Boolean("Main","MainSwitch","转发无限制",false))
                        {
                            LinkedHashMap mMap = (LinkedHashMap) MField.GetField(param.thisObject,"mForwardTargetMap", Map.class);
                            if(mMap.size()==11)
                            {
                                mMap.remove("10000");
                            }
                        }

                    }
                }
        );


        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.selectmember.SelectedAndSearchBar"), "a",
                List.class, boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        List l = (List) param.args[0];
                        if(l.size()==11)
                        {
                            Iterator it = l.iterator();
                            while (it.hasNext())
                            {
                                Object o = it.next();
                                if(o==null)it.remove();
                            }
                        }
                    }
                });



    }
}
