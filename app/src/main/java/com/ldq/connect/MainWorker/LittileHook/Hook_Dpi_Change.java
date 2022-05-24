package com.ldq.connect.MainWorker.LittileHook;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Color;
import android.util.DisplayMetrics;
import android.widget.EditText;

import com.github.megatronking.stringfog.annotation.StringFogIgnore;
import com.ldq.Utils.FileUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.ConfigPathBuilder;
import com.ldq.connect.HookConfig.FlagUtils;
import com.ldq.connect.MHookEnvironment;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@StringFogIgnore
public class Hook_Dpi_Change {
    static {
        if(FlagUtils.isRemoveConfig()) {
            new File(ConfigPathBuilder.Get_Current_Path_Set() + "DpiSet.txt").delete();
        } else {
            if(new File(ConfigPathBuilder.Get_Current_Path_Set() + "DpiSet.txt").exists()) {
                String SetStr = FileUtils.ReadFileString(ConfigPathBuilder.Get_Current_Path_Set() + "DpiSet.txt");
                try{
                    SetDPI = Integer.parseInt(SetStr);
                } catch (Exception e) {
                    SetDPI=0;
                }
            }
            else {
                SetDPI=0;
            }
        }
    }
    public static int SetDPI;
    @SuppressLint("WrongConstant")
    public static void Start() throws ClassNotFoundException {

        float scale = 1.0f / 160;
        if(SetDPI!=0)
        {
            try{
                XposedHelpers.findAndHookMethod("android.app.ContextImpl", MHookEnvironment.mLoader, "setResources",
                        Resources.class,
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                                Resources res = (Resources) param.args[0];
                                int setDpi = SetDPI;
                                if(setDpi>1500)setDpi=1500;

                                if(setDpi!=0)
                                {
                                    DisplayMetrics metrics = res.getDisplayMetrics();
                                    metrics.densityDpi = setDpi;
                                    metrics.density = setDpi * scale;
                                }
                            }
                        });

                XposedHelpers.findAndHookMethod(Resources.class,  "updateConfiguration",
                        Configuration.class, DisplayMetrics.class, MClass.loadClass("android.content.res.CompatibilityInfo"),
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                                int setDpi = SetDPI;
                                if(setDpi>1500)setDpi=1500;

                                if(setDpi!=0)
                                {
                                    DisplayMetrics metrics = (DisplayMetrics) param.args[1];
                                    if(metrics == null)return;
                                    metrics.densityDpi = setDpi;
                                    metrics.density = setDpi * scale;


                                    Configuration conf = (Configuration) param.args[0];
                                    conf.densityDpi = setDpi;
                                    //metrics.scaledDensity = setDpi;
                                }
                            }
                        });

                XposedHelpers.findAndHookMethod(Resources.class,  "getDisplayMetrics",
                        new XC_MethodHook() {
                            @Override
                            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                                super.afterHookedMethod(param);
                                int setDpi = SetDPI;
                                if(setDpi>1500)setDpi=1500;

                                if(setDpi!=0)
                                {
                                    DisplayMetrics metrics = (DisplayMetrics) param.getResult();
                                    if(metrics == null)return;
                                    metrics.densityDpi = setDpi;
                                    metrics.density = setDpi * scale;
                                    //metrics.scaledDensity = setDpi;
                                }
                            }
                        });

                XposedHelpers.findAndHookMethod("android.content.res.ResourcesImpl", MHookEnvironment.mLoader, "updateConfiguration",
                        Configuration.class, DisplayMetrics.class, MClass.loadClass("android.content.res.CompatibilityInfo"),
                        new XC_MethodHook() {
                            @Override
                            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                                super.beforeHookedMethod(param);
                                int setDpi = SetDPI;
                                if(setDpi>1500)setDpi=1500;

                                if(setDpi!=0)
                                {
                                    DisplayMetrics metrics = (DisplayMetrics) param.args[1];
                                    if(metrics == null)return;
                                    metrics.densityDpi = setDpi;
                                    metrics.density = setDpi * scale;


                                    Configuration conf = (Configuration) param.args[0];
                                    conf.densityDpi = setDpi;
                                    //metrics.scaledDensity = setDpi;
                                }
                            }
                        });


            }catch (Throwable e){
                MLogCat.Print_Error("DPI Hook Error",e);
            }

        }
    }
    public static void ShowDpiDialog(Context mContext)
    {
        EditText ed = new EditText(mContext);
        ed.setTextColor(Color.BLACK);
        ed.setTextSize(20);
        int setDpi = SetDPI;
        if(setDpi!=0)
        {
            ed.setText(""+setDpi);
        }
        else
        {
            ed.setText(""+mContext.getResources().getDisplayMetrics().densityDpi);
        }
        new AlertDialog.Builder(mContext,AlertDialog.THEME_HOLO_LIGHT)
                .setTitle("在下面输入你的DPI")
                .setView(ed)
                .setNegativeButton("确定", (dialog, which) -> {
                    String Dpi = ed.getText().toString();
                    int d = 0;
                    if(d<0)d = -d;
                    if(d>1500)d=1500;
                    d = Integer.parseInt(Dpi);
                    try{
                        AlertDialog al = new AlertDialog.Builder(mContext,3).create();
                        al.setTitle("警告");
                        al.setMessage("---请截图此窗口再继续操作---\n\nDPI是QQ的界面大小设置,如果修改错误可能导致QQ显示异常,如果发生显示异常,请删除文件 "+ConfigPathBuilder.Get_Current_Path_Set() + "DpiSet.txt  即可恢复正常DPI,如果确定了解风险并截图了" +
                                "请点击确定,点击确定后将会自动重启QQ");
                        int finalD = d;
                        al.setButton(AlertDialog.BUTTON_NEGATIVE,"确定",(a, b)->{
                            FileUtils.WriteFileByte(ConfigPathBuilder.Get_Current_Path_Set() + "DpiSet.txt",(""+finalD).getBytes());
                            System.exit(0);
                        });
                        al.show();
                    }
                    catch (Throwable th)
                    {
                        Utils.ShowToast("保存失败");
                    }
                }).show();
    }
    /*
    public static void ShowDpiCalcelDialog(Activity mContext)
    {
        if(new File(MHookEnvironment.PublicStorageModulePath + "DPIChanged").exists())
        {
            ScrollView sView = new ScrollView(mContext);
            LinearLayout MList = new LinearLayout(mContext);

            sView.addView(MList,new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT));
            MList.setBackgroundColor(Color.BLACK);
            MList.getBackground().setAlpha(150);


            TextView tView = new TextView(mContext);
            tView.setTextColor(Color.WHITE);
            tView.setTextSize(Utils.px2sp(mContext,RawDpi/4));
            tView.setText("你刚刚修改了DPI设置,如果出现显示异常请长按此文本重置DPI设置,如果没有异常直接返回即可");
            tView.setOnLongClickListener(ff -> {
                new File(ConfigPathBuilder.Get_Current_Path_Set() + "DpiSet.txt").delete();
                System.exit(0);
                return true;
            });
            MList.addView(tView);
            PopupWindow mWindow = new PopupWindow(sView, (int) (rawWidth /1.2),(int) (rawHeight/1.2));
            mWindow.setFocusable(true);
            mWindow.setOutsideTouchable(false);
            new File(MHookEnvironment.PublicStorageModulePath + "DPIChanged").delete();
            mWindow.showAtLocation(mContext.getWindow().getDecorView(), Gravity.CENTER, 0, 0);

        }

    }

     */
}
