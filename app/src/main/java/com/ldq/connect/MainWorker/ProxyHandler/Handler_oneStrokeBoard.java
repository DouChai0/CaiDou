package com.ldq.connect.MainWorker.ProxyHandler;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.ldq.Utils.FileUtils;
import com.ldq.Utils.NameUtils;
import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.QQTicketUtils;

import org.json.JSONArray;
import org.json.JSONObject;

import java.io.FileOutputStream;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Calendar;
import java.util.LinkedList;
import java.util.concurrent.atomic.AtomicInteger;

public class Handler_oneStrokeBoard extends LinearLayout {
    View Board;
    Canvas drawer;
    Context context;
    Paint psColor;
    Paint RedRound;
    Paint BlueRound;
    class DrawPath{
        int from_x;
        int from_y;
        int to_x;
        int to_y;
    }
    class DrawPoint{
        int draw_x;
        int draw_y;
    }
    boolean outPutMode = false;
    boolean IsPointMode = false;
    boolean IsCutAuto = false;
    boolean IsSingleLineMode = false;
    AtomicInteger PathCounter = new AtomicInteger();
    LinkedList<DrawPath> drawPaths = new LinkedList<>();
    LinkedList<DrawPoint> drawPoints = new LinkedList<>();

    LinkedList<Integer> Steps_Path = new LinkedList<>();

    String ReplaceURL = null;

    public Handler_oneStrokeBoard(Context context) {
        super(context);
        this.context = context;
        LinearLayout toolBar = new LinearLayout(context);
        setOrientation(VERTICAL);
        addView(toolBar);

        RadioGroup group = new RadioGroup(context);
        group.setOrientation(HORIZONTAL);

        RadioButton btnDrawBack = new RadioButton(context);
        group.addView(btnDrawBack);
        btnDrawBack.setText("线条");

        btnDrawBack.setChecked(true);
        btnDrawBack.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                IsPointMode = false;
            }
        });
        btnDrawBack.setTextColor(Color.BLACK);


        RadioButton btnDrawPoint = new RadioButton(context);
        group.addView(btnDrawPoint);
        btnDrawPoint.setText("点位");
        btnDrawPoint.setTextColor(Color.BLACK);
        btnDrawPoint.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                IsPointMode = true;
            }
        });


        toolBar.addView(group);

        CheckBox ch = new CheckBox(context);
        ch.setText("不连续");
        ch.setTextColor(Color.BLACK);
        ch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            IsCutAuto = isChecked;
        });
        toolBar.addView(ch);

        CheckBox chSingle = new CheckBox(context);
        chSingle.setText("点线模式");
        chSingle.setTextColor(Color.BLACK);
        chSingle.setOnCheckedChangeListener((buttonView, isChecked) -> {
            IsSingleLineMode = isChecked;
        });
        toolBar.addView(chSingle);



        LinearLayout endToolbar = new LinearLayout(context);

        Button btnCutLine = new Button(context);
        btnCutLine.setText("切断线条");
        btnCutLine.setOnClickListener(v->{
            LastPoint_x = -1;
            LastPoint_y = -1;
        });
        endToolbar.addView(btnCutLine);
        addView(endToolbar);

        Button btnBack = new Button(context);
        btnBack.setText("撤销");
        btnBack.setOnClickListener(v->{
            if(IsPointMode){
                if(drawPoints.size() > 0){
                    drawPoints.removeLast();
                }
            }else {
                if(Steps_Path.size() > 0){
                    int count = Steps_Path.getLast();
                    Steps_Path.removeLast();
                    for(int i=0;i<count;i++){
                        if(drawPaths.size() >0){
                            drawPaths.removeLast();
                        }
                    }
                    Board.invalidate();

                }
            }


        });
        endToolbar.addView(btnBack);

        Button btnChangePic = new Button(context);
        btnChangePic.setText("更改封面");
        btnChangePic.setOnClickListener(v->{
            EditText ed = new EditText(context);
            if(ReplaceURL !=null)ed.setText(ReplaceURL);
            new AlertDialog.Builder(context,3)
                    .setTitle("设置你需要设置封面的网址")
                    .setView(ed)
                    .setNegativeButton("确定", (dialog, which) -> {
                        ReplaceURL = ed.getText().toString();
                    }).show();
        });
        endToolbar.addView(btnChangePic);


        psColor = new Paint();
        psColor.setColor(Color.parseColor("#FFCCD3"));
        psColor.setStrokeWidth(16);

        RedRound = new Paint();
        RedRound.setColor(Color.parseColor("#FA3468"));
        RedRound.setStrokeWidth(20);

        BlueRound = new Paint();
        BlueRound.setColor(Color.parseColor("#D2E9FF"));
        BlueRound.setStrokeWidth(8);

        InitDrawer();
    }
    float LastPoint_x = -1;
    float LastPoint_y = -1;

    Bitmap drView;
    public void InitDrawer(){
        Board = new View(context){
            @Override
            protected void onDraw(Canvas canvas) {
                super.onDraw(canvas);
                if (outPutMode){
                    canvas.drawColor(Color.WHITE);
                }else {
                    canvas.drawColor(Color.parseColor("#D2E9FF"));
                }


                for (DrawPath path : drawPaths) {
                    canvas.drawLine(path.from_x, path.from_y, path.to_x, path.to_y, psColor);
                }
                for(DrawPoint point : drawPoints){
                    canvas.drawCircle(point.draw_x,point.draw_y,20,RedRound);
                    canvas.drawCircle(point.draw_x,point.draw_y,8,BlueRound);
                }


            }

            @Override
            public boolean dispatchTouchEvent(MotionEvent event) {
                switch (event.getAction()){
                    case MotionEvent.ACTION_DOWN:{
                        if (IsSingleLineMode){
                            if(LastPoint_x == -1 || LastPoint_y == -1){
                                LastPoint_x = event.getX();
                                LastPoint_y = event.getY();
                            }else{
                                DrawPath NewPath = new DrawPath();
                                NewPath.from_x = (int) LastPoint_x;
                                NewPath.from_y = (int) LastPoint_y;


                                float point_to_x = event.getX();
                                float point_to_y = event.getY();

                                NewPath.to_x = (int) point_to_x;
                                NewPath.to_y = (int) point_to_y;

                                LastPoint_x = point_to_x;
                                LastPoint_y = point_to_y;

                                drawPaths.addLast(NewPath);
                                Steps_Path.addLast(1);
                                Board.invalidate();

                            }
                            return false;
                        }


                        if(IsPointMode){
                            DrawPoint point = new DrawPoint();
                            point.draw_x = (int) event.getX();
                            point.draw_y = (int) event.getY();

                            drawPoints.addLast(point);
                            invalidate();
                            return false;
                        }else {
                            if(LastPoint_x == -1)LastPoint_x = event.getX();
                            if(LastPoint_y == -1)LastPoint_y = event.getY();
                            PathCounter.getAndSet(0);
                            return true;
                        }

                    }
                    case MotionEvent.ACTION_UP:{
                        if (IsCutAuto){
                            LastPoint_x = -1;
                            LastPoint_y = -1;
                        }else {
                            LastPoint_x = event.getX();
                            LastPoint_y = event.getY();
                        }

                        Steps_Path.addLast(PathCounter.getAndSet(0));
                        break;
                    }
                    case MotionEvent.ACTION_MOVE:{
                        if (IsPointMode || IsSingleLineMode){

                        }else {
                            DrawPath NewPath = new DrawPath();
                            NewPath.from_x = (int) LastPoint_x;
                            NewPath.from_y = (int) LastPoint_y;


                            float point_to_x = event.getX();
                            float point_to_y = event.getY();

                            NewPath.to_x = (int) point_to_x;
                            NewPath.to_y = (int) point_to_y;

                            LastPoint_x = point_to_x;
                            LastPoint_y = point_to_y;

                            drawPaths.addLast(NewPath);
                            PathCounter.getAndIncrement();
                        }

                        invalidate();
                        break;
                    }
                }
                return super.dispatchTouchEvent(event);
            }
        };
        addView(Board, 750,750);
    }
    public Bitmap convertViewToBitmap(View view) {
        outPutMode = true;
        invalidate();
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        outPutMode = false;
        invalidate();
        return bitmap;
    }
    public String GetFinaleBoardData(){
        try{
            JSONObject retJson = new JSONObject();
            JSONArray mArray = new JSONArray();
            for(DrawPath path : drawPaths){
                JSONObject item = new JSONObject();
                item.put("x1",path.from_x);
                item.put("y1",path.from_y);
                item.put("x2",path.to_x);
                item.put("y2",path.to_y);
                mArray.put(item);
            }


            JSONArray pointArr = new JSONArray();
            for(DrawPoint point :drawPoints){
                JSONObject item = new JSONObject();
                item.put("x",point.draw_x);
                item.put("y",point.draw_y);
                pointArr.put(item);

                JSONObject item2 = new JSONObject();
                item2.put("x1",point.draw_x-1);
                item2.put("y1",point.draw_y-1);
                item2.put("x2",point.draw_x);
                item2.put("y2",point.draw_y);
                mArray.put(item2);

                item2 = new JSONObject();
                item2.put("x1",point.draw_x);
                item2.put("y1",point.draw_y);
                item2.put("x2",point.draw_x+1);
                item2.put("y2",point.draw_y+1);
                mArray.put(item2);


            }
            retJson.put("connect",mArray);
            retJson.put("vertex",pointArr);
            retJson.put("vertexCount",pointArr.length());
            if(ReplaceURL == null){
                String CacheUrl = MHookEnvironment.PublicStorageModulePath + "Cache/"+System.currentTimeMillis()+".png";


                FileOutputStream out = new FileOutputStream(CacheUrl);
                drView = convertViewToBitmap(Board);
                drView.compress(Bitmap.CompressFormat.PNG,100,out);
                out.flush();
                out.close();
                StringBuilder builder = new StringBuilder();
                Thread uploader = new Thread(()->{
                    builder.append(UploadToServer(CacheUrl));
                });
                uploader.start();
                uploader.join();
                retJson.put("img",builder.toString());

            }else {
                retJson.put("img",ReplaceURL);
            }



            return retJson.toString();
        }catch (Exception e){
            Utils.ShowToast("Oops,发生了一个不应该发生的错误,什么情况:"+e);
            return null;
        }
    }
    private static String UploadToServer(String LocalPath){
        try {
            String  uToken = "----WebKitFormBoundary"+ NameUtils.getRandomString(16);
            URL u = new URL("https://h5.qianbao.qq.com/upload/cgi/upload?g_tk="+ QQTicketUtils.GetG_TK("qianbao.qq.com"));
            HttpURLConnection connection = (HttpURLConnection) u.openConnection();
            connection.setDoOutput(true);

            connection.addRequestProperty("referer","https://h5.qianbao.qq.com/sendRedpack/index");
            connection.addRequestProperty("origin","https://h5.qianbao.qq.com");
            connection.addRequestProperty("content-type","multipart/form-data; boundary="+uToken);
            connection.addRequestProperty("user-agent","Mozilla/5.0 (Linux; Android 12; LE2120 Build/SKQ1; wv) AppleWebKit/537.36 (KHTML, like Gecko) Version/4.0 Chrome/89.0.4389.72 MQQBrowser/6.2 TBS/045913 Mobile Safari/537.36 V1_AND_SQ_8.8.55_2390_YYB_D QQ/8.8.55.6900 NetType/WIFI WebP/0.3.0 Pixel/1440 StatusBarHeight/128 SimpleUISwitch/0 QQTheme/0 InMagicWin/0 StudyMode/0 CurrentMode/0 CurrentFontScale/1.0");
            connection.addRequestProperty("cookie","appid=10011");
            connection.addRequestProperty("cookie","app_key=ZmsiXB930O2pwx8j");
            connection.addRequestProperty("cookie","uin="+ BaseInfo.GetCurrentUinO2());
            connection.addRequestProperty("cookie","p_uin="+ BaseInfo.GetCurrentUinO2());
            connection.addRequestProperty("cookie","skey="+ QQTicketUtils.GetSkey());
            connection.addRequestProperty("cookie","p_skey="+ QQTicketUtils.GetPsKey("qianbao.qq.com"));

            OutputStream out = connection.getOutputStream();
            out.write(("--"+uToken+"\r\n" +
                    "Content-Disposition: form-data; name=\"image\"; filename=\"image.png\"\r\n" +
                    "Content-Type: image/png\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            out.write(FileUtils.ReadFileByte(LocalPath));
            out.write(("\r\n--"+uToken+"\r\n" +
                    "Content-Disposition: form-data; name=\"bucket\"\r\n" +
                    "\r\n" +
                    "qpay-onestroke-redpack\r\n" +
                    "--"+uToken+"\r\n" +
                    "Content-Disposition: form-data; name=\"key\"\r\n\r\n").getBytes(StandardCharsets.UTF_8));
            Calendar cl = Calendar.getInstance();
            String Path = "/"+cl.get(Calendar.YEAR)+(cl.get(Calendar.MONTH)+1)+cl.get(Calendar.DAY_OF_MONTH)+"/oneStrokeSubject/"+System.currentTimeMillis()
                    +".png";
            out.write((Path+"\r\n--"+uToken+"--"
            ).getBytes(StandardCharsets.UTF_8));
            out.flush();

            InputStream insp = connection.getInputStream();
            Utils.readAllBytes(insp);

            return "https://qpay-onestroke-redpack-1251316161.cos.ap-guangzhou.myqcloud.com"+Path;




        }catch (Exception e){
            //Utils.ShowToast(Log.getStackTraceString(e));
            Utils.ShowToast("封面上传失败");
            return "null";
        }
    }
}
