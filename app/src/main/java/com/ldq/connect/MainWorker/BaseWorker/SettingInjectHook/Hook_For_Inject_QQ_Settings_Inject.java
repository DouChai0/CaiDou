package com.ldq.connect.MainWorker.BaseWorker.SettingInjectHook;

import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;
import static android.view.ViewGroup.LayoutParams.WRAP_CONTENT;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.GlobalConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MainWorker.BaseWorker.BaseMenu.Menu_QRCode_Checker;
import com.ldq.connect.Tools.GradientTextView;

import java.lang.reflect.Field;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_Inject_QQ_Settings_Inject {
    public static void StartInject() {
        //注入项目到QQ的设置界面中
        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.activity.QQSettingSettingActivity", MHookEnvironment.mLoader, "doOnCreate", Bundle.class,
                new XC_MethodHook(199) {
                    @SuppressLint("ResourceType")
                    @Override
                    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                        super.afterHookedMethod(param);
                        new Handler(Looper.getMainLooper())
                                .postDelayed(()->{
                                    try{
                                        Activity act = (Activity) param.thisObject;
                                        View mItemView = MField.GetFirstField(param.thisObject,param.thisObject.getClass(), MHookEnvironment.mLoader.loadClass("com.tencent.mobileqq.widget.FormSimpleItem"));
                                        ViewGroup mRootGroup = (ViewGroup) mItemView.getParent();


                                        Class SimpleItemClass =MHookEnvironment.mLoader.loadClass("com.tencent.mobileqq.widget.FormSimpleItem");
                                        View mSetItem = MClass.CallConstrutor(SimpleItemClass,new Class[]{Context.class},act);


                                        CharSequence mChar = "小菜豆";
                                        MMethod.CallMethod(mSetItem,SimpleItemClass,"setLeftText",void.class,new Class[]{CharSequence.class},mChar);

                                        TextView t2 = new GradientTextView(act);

                                        Field f = FieldTable.FindFirstField(mSetItem.getClass(),TextView.class);
                                        ViewGroup vg = (ViewGroup) mSetItem;
                                        if(f!=null){
                                            //花里胡哨炫彩字体创建添加
                                            f.setAccessible(true);

                                            TextView source = (TextView) f.get(mSetItem);
                                            t2.setTextSize(0,source.getTextSize());
                                            RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
                                            params.addRule(RelativeLayout.CENTER_HORIZONTAL,1);

                                            t2.setLayoutParams(source.getLayoutParams());
                                            t2.setText("小菜豆");
                                            t2.setId(88995);


                                            vg.removeView(source);
                                            vg.addView(t2);
                                            f.set(mSetItem,t2);
                                        }

                                        String rDexVer = GlobalConfig.Get_String("VersionName");


                                        TextView tVersion = new TextView(act);
                                        tVersion.setText(""+rDexVer);
                                        RelativeLayout.LayoutParams sParam = new RelativeLayout.LayoutParams(WRAP_CONTENT,WRAP_CONTENT);
                                        sParam.addRule(RelativeLayout.CENTER_HORIZONTAL,1);
                                        sParam.addRule(RelativeLayout.BELOW,88995);

                                        MMethod.CallMethod(mSetItem,SimpleItemClass,"setRightText",void.class,new Class[]{CharSequence.class},"终极版");


                                        mRootGroup.addView(mSetItem,0, new ViewGroup.LayoutParams(MATCH_PARENT, WRAP_CONTENT));
                                        mSetItem.setOnClickListener(v -> Menu_QRCode_Checker.StartCheck());
                                    }catch (Throwable th){
                                        Utils.ShowToast("Inject Setting Error:\n"+th);
                                    }
                                },100);

                    }
                });

    }
}
