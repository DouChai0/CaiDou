package com.ldq.connect.MainWorker.BaseWorker.SettingInjectHook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.widget.LinearLayout;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.connect.MTool.QQCleaner.AddButtonCleaner;
import com.ldq.connect.MainWorker.BaseWorker.BaseMenu.Menu_QRCode_Checker;
import com.ldq.connect.MainWorker.WidgetHandler.Handler_For_Repeat_Msg_Common;
import com.ldq.connect.Tools.TroopOpen;

import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_MainAddButton_Inject {
    static Object ItemCache=null;
    public static void Start() throws ClassNotFoundException {
        //右上角加号添加项目的Hook
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.widget.PopupMenuDialog"), "createAndAttachItemsView",
                Activity.class, List.class, LinearLayout.class, boolean.class, new XC_MethodHook(99) {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        List mMenu = (List) param.args[1];
                        AddButtonCleaner.StartClean(mMenu);
                        if(ItemCache!=null && mMenu.contains(ItemCache))return;

                        Object mAddItem = MClass.CallConstrutor(mMenu.get(0).getClass(), new Class[]{
                                int.class, String.class, String.class, int.class
                        }, 2333, "小菜豆", "点击打开菜单", 3);

                        Drawable drawable = new BitmapDrawable(Handler_For_Repeat_Msg_Common.RepeatIconBitmap);
                        MField.SetField(mAddItem,mAddItem.getClass(),"drawable", drawable, Drawable.class);
                        ItemCache = mAddItem;
                        mMenu.add(0, mAddItem);
                        /*
                        if(MConfig.Get_Boolean("Main","MainSwitch","便捷加群",false))
                        {
                            Object mAddTroop = MClass.CallConstrutor(mMenu.get(0).getClass(), new Class[]{
                                    int.class, String.class, String.class, int.class
                            }, 2888, "打开群/好友界面", "便捷加群", 3);
                            MField.SetField(mAddTroop,mAddTroop.getClass(),"drawable", drawable, Drawable.class);
                            mMenu.add(0, mAddTroop);

                        }

                         */
                    }
                }
        );
        //右上角加号被点击的Hook,使用ID来标注点击的项目
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.widget.PopupMenuDialog"), "onClick",View.class, new XC_MethodHook() {
            @SuppressLint("ResourceType")
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                View v = (View) param.args[0];
                if(v.getId()==2333)
                {
                    Menu_QRCode_Checker.StartCheck();
                }else if (v.getId()==2888)
                {
                    TroopOpen.OpenDialog();
                }
            }
        });
    }
}
