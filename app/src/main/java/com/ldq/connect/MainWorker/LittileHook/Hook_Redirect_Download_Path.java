package com.ldq.connect.MainWorker.LittileHook;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.widget.EditText;

import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;

import java.io.File;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Redirect_Download_Path {
    public static void Start()
    {
        //QQ下载重定向Hook,这个是QQ的一个文件系统工具类,这个方法的作用是装换一个路径为外部私有路径,判断为QQ下载的文件路径则替换为重定向目录
        XposedHelpers.findAndHookMethod("com.tencent.mobileqq.vfs.VFSAssistantUtils", MHookEnvironment.mLoader, "getSDKPrivatePath", String.class, new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                String Path = (String) param.args[0];
                String Result = (String) param.getResult();
                if(MConfig.Get_Boolean("Main","MainSwitch","下载重定向",false))
                {
                    if(Result.contains("/Tencent/QQfile_recv/"))
                    {
                        if(new File(Result).exists() && new File(Result).isFile())return;//如果下载的文件已经存在则不替换,防止与QQ文件数据库出错而导致无法下载的问题
                        String End = Path.substring(Path.lastIndexOf("/Tencent/QQfile_recv/")+"/Tencent/QQfile_recv/".length());
                        String Start = MConfig.Get_String("Main","MainSet","DownloadRedirect");

                        if(TextUtils.isEmpty(Start))Start = Environment.getExternalStorageDirectory()+"/Download/MobileQQ/";
                        if(!Start.endsWith("/"))Start = Start + "/";
                        param.setResult(Start+End);
                        return;
                    }
                }
            }
        });
    }
    public static void ShowSettinng(Context context)
    {
        EditText ed = new EditText(context);
        ed.setTextSize(16);
        ed.setTextColor(Color.BLACK);
        ed.setText(MConfig.Get_String("Main","MainSet","DownloadRedirect"));
        new AlertDialog.Builder(context,3)
                .setTitle("设置重定向的路径")
                .setView(ed)
                .setNegativeButton("保存", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String Path = ed.getText().toString();
                        File ff = new File(Path);
                        if(!ff.exists())
                        {
                            ff.mkdirs();
                        }
                        if(!ff.exists() || ff.isFile())
                        {
                            Utils.ShowToast("保存失败,设置的文件夹不存在且无法被创建");
                            return;
                        }
                        MConfig.Put_String("Main","MainSet","DownloadRedirect", ff.getAbsolutePath());
                        Utils.ShowToast("重启QQ生效");
                    }
                }).show();
    }
}
