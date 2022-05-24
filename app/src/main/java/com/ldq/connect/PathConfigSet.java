package com.ldq.connect;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.os.Environment;
import android.text.InputType;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.GlobalConfig;
import com.ldq.connect.MainWorker.BaseWorker.EnvHook.HookSplashAct;
import com.ldq.connect.QQUtils.BaseCall;

import java.io.File;
import java.io.IOException;

public class PathConfigSet {
    public static boolean CheckForConfigPath(boolean IsShowSet){
        int CheckType = (int) GlobalConfig.Get_Long("StorageType",0);
        if(CheckType == 0){
            if(IsShowSet){
                HookSplashAct.StartHook(()->ShowConfigPathSet(HookSplashAct.FirstAct,false));
            }
            return false;
        }else if (CheckType == 1){
            MHookEnvironment.PublicStorageModulePath = Environment.getExternalStorageDirectory() + "/LD_Module/";
        }else if (CheckType == 2){
            MHookEnvironment.PublicStorageModulePath = Environment.getExternalStorageDirectory()+"/Android/media/"+ MHookEnvironment.MAppContext.getPackageName()+"/xcd_"+MHookEnvironment.RndToken.hashCode()+"/";
        }else if (CheckType == 3){
            MHookEnvironment.PublicStorageModulePath = Environment.getExternalStorageDirectory()+"/Android/data/"+ MHookEnvironment.MAppContext.getPackageName()+"/files/.app_"+MHookEnvironment.RndToken.hashCode()+"/";
        }else if (CheckType == 4){
            MHookEnvironment.PublicStorageModulePath = GlobalConfig.Get_String("LocalPath");
        }else {
            return false;
        }
        return true;
    }
    public static void ShowConfigPathSet(Activity act,boolean IsCancelable){
        LinearLayout mRoot = new LinearLayout(act);
        mRoot.setOrientation(LinearLayout.VERTICAL);
        mRoot.setBackgroundColor(Color.WHITE);

        @SuppressLint("ResourceType") Dialog dialog = new Dialog(act,3);
        dialog.setContentView(mRoot);
        dialog.setCancelable(IsCancelable);

        TextView view = new TextView(act);
        view.setText("小菜豆--请设置外部存储目录(设置/脚本/语音等)再继续");
        view.setTextColor(Color.BLACK);
        view.setTextSize(24);
        mRoot.addView(view);

        TextView vStatus = new TextView(act);
        vStatus.setText("当前储存目录:"+GetLastPath());
        mRoot.addView(vStatus);


        //选择组
        RadioGroup group = new RadioGroup(act);

        RadioButton btn_ExtraStorage = new RadioButton(act);
        btn_ExtraStorage.setText("外部存储根目录/LD_Module");
        btn_ExtraStorage.setTextColor(Color.BLACK);
        group.addView(btn_ExtraStorage);

        RadioButton btn_Extra_Media = new RadioButton(act);
        btn_Extra_Media.setText("半私有外部media目录");
        btn_Extra_Media.setTextColor(Color.BLACK);
        group.addView(btn_Extra_Media);

        RadioButton btn_Extra_Pri = new RadioButton(act);
        btn_Extra_Pri.setText("外部私有目录");
        btn_Extra_Pri.setTextColor(Color.BLACK);
        group.addView(btn_Extra_Pri);

        RadioButton btn_User_Self_Path = new RadioButton(act);
        btn_User_Self_Path.setText("自定义目录");
        btn_User_Self_Path.setTextColor(Color.BLACK);
        group.addView(btn_User_Self_Path);
        mRoot.addView(group);

        //编辑器
        EditText edAndShowPath = new EditText(act);
        edAndShowPath.setSingleLine(false);
        mRoot.addView(edAndShowPath);

        //工具栏
        RelativeLayout toolbar = new RelativeLayout(act);

        Button btnSave = new Button(act);
        btnSave.setText("保存当前设置并重启QQ");

        RelativeLayout.LayoutParams param = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        param.addRule(RelativeLayout.ALIGN_PARENT_RIGHT,1);
        toolbar.addView(btnSave,param);
        mRoot.addView(toolbar);

        if (!IsCancelable){
            TextView viewAlarm = new TextView(act);
            view.setTextColor(Color.BLACK);
            view.setTextSize(24);
            viewAlarm.setText("看样子你有可能是第一次使用本模块,在这里先说明,本模块基础功能完全免费,如果你是从别人那里买来的,并且那人并没有教你如何使用,代表你可能被倒卖的人圈钱了,你可以选择反馈到作者那里," +
                    "也可以选择自己想办法处理,具体就看你了,我也无法帮你太多哈");
            mRoot.addView(viewAlarm);
        }

        //初始化设置显示
        if (GlobalConfig.Get_Long("StorageType",0) == 0){
            if (GlobalConfig.Get_Boolean("ChangeToPrivate",false)){
                btn_Extra_Media.setChecked(true);
                edAndShowPath.setText(Environment.getExternalStorageDirectory()+"/Android/media/"+ MHookEnvironment.MAppContext.getPackageName()+"/xcd_"+MHookEnvironment.RndToken.hashCode()+"/");
            }else {
                btn_ExtraStorage.setChecked(true);
                edAndShowPath.setText(Environment.getExternalStorageDirectory() + "/LD_Module/");
            }
            edAndShowPath.setInputType(InputType.TYPE_NULL);
        }else if (GlobalConfig.Get_Long("StorageType",0) == 1){
            btn_ExtraStorage.setChecked(true);
            edAndShowPath.setText(Environment.getExternalStorageDirectory() + "/LD_Module/");
            edAndShowPath.setInputType(InputType.TYPE_NULL);
        }else if (GlobalConfig.Get_Long("StorageType",0) == 2){
            btn_Extra_Media.setChecked(true);
            edAndShowPath.setText(Environment.getExternalStorageDirectory()+"/Android/media/"+ MHookEnvironment.MAppContext.getPackageName()+"/xcd_"+MHookEnvironment.RndToken.hashCode()+"/");
            edAndShowPath.setInputType(InputType.TYPE_NULL);
        }else if (GlobalConfig.Get_Long("StorageType",0) == 3){
            btn_Extra_Pri.setChecked(true);
            edAndShowPath.setText(Environment.getExternalStorageDirectory()+"/Android/data/"+ MHookEnvironment.MAppContext.getPackageName()+"/files/.app_"+MHookEnvironment.RndToken.hashCode()+"/");
            edAndShowPath.setInputType(InputType.TYPE_NULL);
        }else if (GlobalConfig.Get_Long("StorageType",0) == 4){
            btn_User_Self_Path.setChecked(true);
            edAndShowPath.setText(GlobalConfig.Get_String("LocalPath"));
        }

        //初始化按钮事件
        btn_ExtraStorage.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed() && isChecked){
                edAndShowPath.setText(Environment.getExternalStorageDirectory() + "/LD_Module/");
                edAndShowPath.setInputType(InputType.TYPE_NULL);
            }
        });
        btn_Extra_Media.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed() && isChecked){
                edAndShowPath.setText(Environment.getExternalStorageDirectory()+"/Android/media/"+ MHookEnvironment.MAppContext.getPackageName()+"/xcd_"+MHookEnvironment.RndToken.hashCode()+"/");
                edAndShowPath.setInputType(InputType.TYPE_NULL);
            }
        });
        btn_Extra_Pri.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed() && isChecked){
                edAndShowPath.setText(Environment.getExternalStorageDirectory()+"/Android/data/"+ MHookEnvironment.MAppContext.getPackageName()+"/files/.app_"+MHookEnvironment.RndToken.hashCode()+"/");
                edAndShowPath.setInputType(InputType.TYPE_NULL);
            }
        });
        btn_User_Self_Path.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if (buttonView.isPressed() && isChecked){
                btn_User_Self_Path.setChecked(true);
                edAndShowPath.setText(GlobalConfig.Get_String("LocalPath"));
                edAndShowPath.setInputType(InputType.TYPE_CLASS_TEXT);
            }
        });

        btnSave.setOnClickListener(v -> {
            String SetPath = "";
            if (btn_ExtraStorage.isChecked()) SetPath = Environment.getExternalStorageDirectory() + "/LD_Module/";
            else if (btn_Extra_Media.isChecked()) SetPath = Environment.getExternalStorageDirectory()+"/Android/media/"+ MHookEnvironment.MAppContext.getPackageName()+"/xcd_"+MHookEnvironment.RndToken.hashCode()+"/";
            else if (btn_Extra_Pri.isChecked()) SetPath = Environment.getExternalStorageDirectory()+"/Android/data/"+ MHookEnvironment.MAppContext.getPackageName()+"/files/.app_"+MHookEnvironment.RndToken.hashCode()+"/";
            else if (btn_User_Self_Path.isChecked()) SetPath = edAndShowPath.getText().toString();

            if (btn_ExtraStorage.isChecked()){
                if(act.checkSelfPermission("android.permission.WRITE_EXTERNAL_STORAGE") == PackageManager.PERMISSION_DENIED){
                    Utils.ShowToast("请授权写入文件权限后再保存");
                    act.requestPermissions(new String[]{"android.permission.WRITE_EXTERNAL_STORAGE"},0);
                }else {
                    new File(SetPath).mkdirs();
                    if (CheckForStoragePermission(SetPath)){
                        GlobalConfig.Put_Long("StorageType",1);
                        dialog.dismiss();
                        BaseCall.ExitQQAnyWays();
                    }else {
                        Utils.ShowToast("权限不足,无法存储配置文件数据");
                    }

                }
            }else if (btn_Extra_Media.isChecked()){
                new File(SetPath).mkdirs();
                if (CheckForStoragePermission(SetPath)){
                    GlobalConfig.Put_Long("StorageType",2);
                    dialog.dismiss();
                    BaseCall.ExitQQAnyWays();
                }else {
                    Utils.ShowToast("权限不足,无法存储配置文件数据");
                }
            }else if (btn_Extra_Pri.isChecked()){
                new File(SetPath).mkdirs();
                if (CheckForStoragePermission(SetPath)){
                    GlobalConfig.Put_Long("StorageType",3);
                    dialog.dismiss();
                    BaseCall.ExitQQAnyWays();
                }else {
                    Utils.ShowToast("权限不足,无法存储配置文件数据");
                }
            }else if (btn_User_Self_Path.isChecked()){
                if (!SetPath.endsWith("/")) SetPath += "/";
                new File(SetPath).mkdirs();
                if (CheckForStoragePermission(SetPath)){
                    GlobalConfig.Put_Long("StorageType",4);
                    GlobalConfig.Put_String("LocalPath",SetPath);
                    dialog.dismiss();
                    BaseCall.ExitQQAnyWays();
                }else {
                    Utils.ShowToast("权限不足,请确定此目录是否可写或QQ是否被给予存储权限");
                }
            }else {
                Utils.ShowToast("配置设置有误?");
            }
        });

        dialog.show();
    }
    private static boolean CheckForStoragePermission(String Path){
        String LocalPath = new File(Path).getAbsolutePath();
        if (!LocalPath.equals("/")) LocalPath += "/";
        File fPath = new File(LocalPath);
        if (fPath.isFile())return false;

        File NewCheckedFile = new File(LocalPath,"CheckFile");
        if(NewCheckedFile.exists()){
            NewCheckedFile.delete();
            return !NewCheckedFile.exists();
        }else {
            try {
                NewCheckedFile.createNewFile();
                if (!NewCheckedFile.exists())return false;
                NewCheckedFile.delete();
                return !NewCheckedFile.exists();
            } catch (IOException e) {
                return false;
            }
        }
    }
    private static String GetLastPath(){
        int CheckType = (int) GlobalConfig.Get_Long("StorageType",0);
        if(CheckType == 0)return "未设定";

        return "";
    }
}
