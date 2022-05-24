package com.ldq.connect.FloatWindow;
import android.app.Activity;
import android.os.Handler;
import android.os.Looper;
import android.view.MotionEvent;
import android.view.View;
import android.view.WindowManager;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MMethod;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MTool.FriendObserve.FriendsObserve;

import java.util.concurrent.atomic.AtomicBoolean;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class WindowHandler {
    WindowManager mManager;
    WindowManager.LayoutParams mParam;
    View BoundView;
    public WindowHandler(View mAddVier, WindowManager manager, WindowManager.LayoutParams param,View TouchView)
    {
        mManager = manager;
        mParam = param;
        TouchView.setOnTouchListener(new FloatingOnTouchListener());
        BoundView = mAddVier;
    }
    public static volatile boolean MoveStatus = false;
    public static boolean IsInRight()
    {
        return Save_x>0;
    }
    static int Save_x = 0;

    private class FloatingOnTouchListener implements View.OnTouchListener {
        private int x;
        private int y;
        @Override
        public boolean onTouch(View view, MotionEvent event) {
            switch (event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    x = (int) event.getRawX();
                    y = (int) event.getRawY();
                    break;
                case MotionEvent.ACTION_MOVE:
                    int nowX = (int) event.getRawX();
                    int nowY = (int) event.getRawY();
                    int movedX = nowX - x;
                    int movedY = nowY - y;
                    x = nowX;
                    y = nowY;
                    mParam.x = mParam.x + movedX;
                    mParam.y = mParam.y + movedY;

                    // 更新悬浮窗控件布局
                    mManager.updateViewLayout(BoundView, mParam);

                    MConfig.Put_Long("FloatWindow","Location","x",mParam.x);
                    MConfig.Put_Long("FloatWindow","Location","y",mParam.y);

                    Save_x  =mParam.x;
                    if(Math.abs(movedX) > 2 || Math.abs(movedY)>2)
                    MoveStatus = true;
                    return true;
                case MotionEvent.ACTION_UP:
                    new Handler(Looper.getMainLooper()).postDelayed(()->MoveStatus=false,50);
                    break;
                default:
                    break;
            }
            return false;
        }
    }

    public static volatile boolean ShowForHelper = false;
    public static void StartSwitchHook() throws ClassNotFoundException {
        /*
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.ChatFragment"),
                "onHiddenChanged", boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        boolean i = (boolean) param.args[0];

                        if (i) {
                            ShowAIO = true;
                            Init.HideWindow();
                        } else {
                            ShowAIO = false;
                        }
                    }
                }
        );

         */

        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.SplashActivity"),
                "openAIO", boolean.class,int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);

                        FriendsObserve.onResume();
                    }
                }
        );



        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.ChatFragment"),
                "onHiddenChanged", boolean.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        boolean i = (boolean) param.args[0];
                        ShowForHelper = i;
                        if (!i) PauseFlag.getAndSet(true);
                    }
                }
        );




        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.ChatFragment"),
                "onPause",new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Init.HideWindow();
                        PauseFlag.getAndSet(true);
                    }
                }
        );

        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.ChatFragment"),
                "onResume",new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Activity act = MMethod.CallMethod(param.thisObject,"getActivity",MClass.loadClass("androidx.fragment.app.FragmentActivity"),new Class[0]);
                        new Handler(Looper.getMainLooper()).postDelayed(()->{
                            if(!PauseFlag.get())
                            {
                                    Init.ShowWindow(act);

                            }
                        },1000);
                        PauseFlag.getAndSet(false);

                    }
                }
        );
    }
    static AtomicBoolean PauseFlag = new AtomicBoolean();
}