package com.ldq.connect.MainWorker.ProxyHandler;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.PixelFormat;
import android.graphics.drawable.Drawable;
import android.view.Gravity;
import android.view.WindowManager;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;

import java.io.File;
import java.util.ArrayList;

public class Handler_SendStrokeHB {
    static WindowManager mManager;
    static ImageButton button;
    static Activity act;
    @SuppressLint("ResourceType")
    public static void InitAct(Activity act){
        mManager = (WindowManager) act.getSystemService(Context.WINDOW_SERVICE);
        ImageButton btnClick = new ImageButton(act);
        btnClick.setImageAlpha(182);
        btnClick.setId(2366);
        btnClick.setImageDrawable(DLAndLoad());
        btnClick.setScaleType(ImageView.ScaleType.FIT_CENTER);
        btnClick.setOnClickListener(v->ClickToMenu());
        btnClick.getBackground().setAlpha(0);


        button = btnClick;

        WindowManager.LayoutParams layoutParams = new WindowManager.LayoutParams();
        //layoutParams.type = WindowManager.LayoutParams.TYPE_APPLICATION_OVERLAY;
        layoutParams.type = WindowManager.LayoutParams.FIRST_SUB_WINDOW+5;
        layoutParams.flags = WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE;
        layoutParams.format = PixelFormat.RGBA_8888;
        layoutParams.width = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.height = WindowManager.LayoutParams.WRAP_CONTENT;
        layoutParams.gravity = Gravity.RIGHT | Gravity.TOP;


        mManager.addView(btnClick,layoutParams);
        Handler_SendStrokeHB.act = act;

    }

    private static void ClickToMenu() {
        new AlertDialog.Builder(act,3)
                .setTitle("选择操作")
                .setItems(new String[]{"上传本地图案", "打开简易画板"}, (dialog, which) -> {
                    if (which==0)UploadLocal();
                    if (which==1)OpenDrawBoard();
                }).show();


    }
    private static void UploadLocal(){
        File[] fs =  new File(MHookEnvironment.PublicStorageModulePath + "数据目录/一笔画数据/").listFiles();
        if(fs == null){
            Utils.ShowToast("本地没有文件");
            return;
        }
        ArrayList<String> fst = new ArrayList<>();
        for(File f : fs){
            if (f.isFile() && f.getName().endsWith(".json")){
                fst.add(f.getName().substring(0,f.getName().length()-5));
            }
        }
        new AlertDialog.Builder(act,3)
                .setTitle("选择要上传的本地图案")
                .setItems(fst.toArray(new String[0]), (dialog, which) -> {
                    String Path = MHookEnvironment.PublicStorageModulePath + "数据目录/一笔画数据/" + fst.get(which)+".json";
                    String FileContent = FileUtils.ReadFileString(Path);
                    EditDialog(FileContent);
                }).show();
    }
    private static void EditDialog(String text){
        EditText edCode = new EditText(act);
        edCode.setText(text);
        new AlertDialog.Builder(act,3)
                .setTitle("编辑内容")
                .setView(edCode)
                .setNegativeButton("保存", (dialog, which) -> {
                    new Thread(()->Handler_OneStrokeHB_Decode.Upload_HBInfo(edCode.getText().toString())).start();

                }).show();
    }
    private static void OpenDrawBoard(){
        Handler_oneStrokeBoard board = new Handler_oneStrokeBoard(act);
        new AlertDialog.Builder(act,3)
                .setTitle("一笔画画板")
                .setView(board)
                .setNegativeButton("保存", (dialog, which) -> {
                    EditDialog(board.GetFinaleBoardData());
                }).show();
    }

    public static Drawable DLAndLoad(){
        String sClick = MHookEnvironment.PublicStorageModulePath+"配置文件目录/click";
        if(!new File(sClick).exists()){
            HttpUtils.downlaodFile(MHookEnvironment.ServerRoot_CDN+"down/click.png",sClick);
        }
        return Drawable.createFromPath(sClick);
    }
}
