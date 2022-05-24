package com.ldq.connect.MTool;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextWatcher;
import android.text.style.BackgroundColorSpan;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldq.Utils.Utils;

import java.util.concurrent.atomic.AtomicBoolean;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegexDebugTool {
    public static void ShowRegexDebug(String RegexText){
        Context context = Utils.GetThreadActivity();
        LinearLayout l = new LinearLayout(context);
        l.setOrientation(LinearLayout.VERTICAL);

        EditText ed = new EditText(context);
        ed.setHint("这里输入正则文本");
        ed.setSingleLine();
        ed.setText(RegexText);
        l.addView(ed);



        EditText CheckText = new EditText(context);
        CheckText.setHint("这里输入处理的文本");
        l.addView(CheckText);

        TextView t = new TextView(context);
        t.setTextColor(Color.BLACK);
        t.setText("匹配状态:"+("".matches(RegexText)?"匹配":"未匹配"));
        l.addView(t);

        ed.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(IsAlive.get())return;
                t.setText("匹配状态:"+(Pattern_Matches(CheckText.getText().toString(),s.toString())?"匹配":"未匹配"));
                OnCheckChange(CheckText,s.toString(),CheckText.getText().toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });

        CheckText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if(IsAlive.get())return;
                t.setText("匹配状态:"+(Pattern_Matches(s.toString(),ed.getText().toString())?"匹配":"未匹配"));
                OnCheckChange(CheckText,ed.getText().toString(),s.toString());
            }

            @Override
            public void afterTextChanged(Editable s) {

            }
        });







        AlertDialog al = new AlertDialog.Builder(context,3)
                .setView(l)
                .setCancelable(false).create();
        al.setButton(AlertDialog.BUTTON_POSITIVE, "关闭", (dialog, which) -> al.dismiss());
        al.show();
    }
    static AtomicBoolean IsAlive = new AtomicBoolean();
    public static void OnCheckChange(EditText ShowEd,String Regextext,String CheckText){
        IsAlive.getAndSet(true);
        try{
            CharSequence charSequence  = ShowEd.getText();
            SpannableString builder;
            if(charSequence instanceof SpannableString){
                builder = (SpannableString) charSequence;
            }else
            {
                builder = new SpannableString(ShowEd.getText());
            }
            BackgroundColorSpan[] spans = builder.getSpans(0,builder.length(),BackgroundColorSpan.class);
            for(BackgroundColorSpan span:spans)builder.removeSpan(span);

            Pattern pt = Pattern.compile(Regextext);
            Matcher matcher = pt.matcher(CheckText);
            boolean ColorNick = false;






            while (matcher.find()){
                    int start = matcher.start();
                    int end = matcher.end();
                    builder.setSpan(new BackgroundColorSpan(ColorNick ? Color.GREEN : Color.RED),start,end, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ColorNick= !ColorNick;
            }

            //MLogCat.Print_Debug(Selection.getSelectionStart(ShowEd.getText())+"");
            //Selection.setSelection(builder,Selection.getSelectionStart(ShowEd.getText()));




            int Start = ShowEd.getSelectionStart();
            ShowEd.setText(builder);
            ShowEd.setSelection(Start);

        }catch (Exception e){

        }
        finally {
            IsAlive.getAndSet(false);
        }

    }
    public static boolean Pattern_Matches(String raw,String Regex){
        try{
            Pattern pt = Pattern.compile(Regex);
            Matcher matcher = pt.matcher(raw);
            return matcher.find();
        }catch (Exception e){
            return false;
        }

    }
}
