package com.ldq.connect.MainWorker.WidgetHook;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldq.Utils.NameUtils;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MTool.RegexDebugTool;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Change_Widget_ShowText {
    public static void ShowSet(){
        try{
            Context context = Utils.GetThreadActivity();
            String Rules = MConfig.Get_String("Main","TextChange","Rules");
            JSONObject NewObj = new JSONObject(Rules.length()==0?"{}":Rules);
            ArrayList<String> IDList = new ArrayList<>();
            ArrayList<String> List = new ArrayList<>();
            List.add("\u0002"+"+++添加一项新规则+++");

            Iterator<String> its = NewObj.keys();
            while (its.hasNext()){
                String id = its.next();
                JSONObject ruleItem = NewObj.getJSONObject(id);
                String Name = ruleItem.getString("rule");
                IDList.add(id);
                List.add("\u0002"+Name);
            }

            new AlertDialog.Builder(context,3)
                    .setTitle("\u0002"+"选择需要操作的项目")
                    .setItems(List.toArray(new String[0]), (dialog, which) -> {
                        if(which==0)
                        {
                            String ItemID = NameUtils.GetRandomName();
                            EditItem(NewObj,ItemID);
                        }else
                        {
                            EditItem(NewObj,IDList.get(which-1));
                        }
                    }).show();




        }catch (Exception e){
            Utils.ShowToast("发生错误:"+e);
        }
    }
    public static void EditItem(JSONObject Json,String ItemID){
        try{
            Context context = Utils.GetThreadActivity();
            JSONObject Item = Json.has(ItemID) ? Json.getJSONObject(ItemID) : new JSONObject();
            LinearLayout lRoot = new LinearLayout(context);
            lRoot.setBackgroundColor(Color.WHITE);
            lRoot.setOrientation(LinearLayout.VERTICAL);

            TextView NewText1 = new TextView(context);
            NewText1.setText("\u0002"+"设置原文本或规则");
            NewText1.setTextColor(Color.BLACK);
            lRoot.addView(NewText1);

            EditText ed = new EditText(context);
            ed.setText("\u0002"+Item.optString("rule"));
            lRoot.addView(ed);


            TextView NewText2 = new TextView(context);
            NewText2.setText("\u0002"+"设置替换为的文本");
            NewText2.setTextColor(Color.BLACK);
            lRoot.addView(NewText2);

            EditText ed2 = new EditText(context);
            ed2.setText("\u0002"+Item.optString("target"));
            lRoot.addView(ed2);

            CheckBox ch = new CheckBox(context);
            ch.setText("\u0002"+"使用替换模式");
            ch.setChecked(Item.optBoolean("replace"));
            ch.setTextColor(Color.BLACK);
            lRoot.addView(ch);

            CheckBox replaceRegex = new CheckBox(context);
            replaceRegex.setTextColor(Color.BLACK);
            replaceRegex.setText("使用正则替换");
            replaceRegex.setChecked(Item.optBoolean("replaceRegex"));
            lRoot.addView(replaceRegex);


            new AlertDialog.Builder(context,3)
                    .setTitle("设置")
                    .setView(lRoot)
                    .setPositiveButton("保存", (dialog, which) -> {




                        try {
                            Item.put("rule",ed.getText().toString().replace("\u0002",""));
                            Item.put("target",ed2.getText().toString().replace("\u0002",""));
                            Item.put("replace",ch.isChecked());
                            Item.put("replaceRegex",replaceRegex.isChecked());



                            Json.put(ItemID,Item);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        MConfig.Put_String("Main","TextChange","Rules",Json.toString());
                    }).setNegativeButton("删除", (dialog, which) -> {
                        Json.remove(ItemID);
                        MConfig.Put_String("Main","TextChange","Rules",Json.toString());
                    }).show();


        }catch (Exception e){

        }

    }

    public static void Start()  {
        XC_MethodHook hook = new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                CharSequence charSequence = (CharSequence) param.args[0];
                if(charSequence ==null || charSequence.length()==0)return;
                if(charSequence.charAt(0)==2)return;

                String s =  MConfig.Get_String("Main","TextChange","Rules");;
                if(s.length()==0)return;

                try{
                    String CheckString = charSequence.toString();
                    JSONObject NewObj = new JSONObject(s);
                    Iterator<String> itKey = NewObj.keys();


                    while (itKey.hasNext()){
                        String KeyID = itKey.next();
                        JSONObject Item = NewObj.getJSONObject(KeyID);
                        String Rule = Item.optString("rule");
                        String Target = Item.optString("target");
                        if(Item.optBoolean("replaceRegex"))
                        {
                            if(RegexDebugTool.Pattern_Matches(CheckString,Rule))
                            {
                                param.args[0] = CheckString.replaceAll(Rule,Target);
                                return;
                            }
                        }


                        if(CheckString.contains(Rule))
                        {

                            boolean IsReplace = Item.optBoolean("replace");
                            if(IsReplace)
                            {
                                param.args[0] = CheckString.replace(Rule,Target);
                                return;
                            }else
                            {
                                if(CheckString.equals(Rule))
                                {
                                    param.args[0] = Target;
                                    return;
                                }
                            }
                        }


                    }

                }catch (Exception e){

                }


            }
        };

        XposedHelpers.findAndHookMethod(TextView.class,"setText",CharSequence.class,hook);
        try {
            XposedHelpers.findAndHookMethod(Canvas.class,"drawText",
                    String.class, Float.TYPE, Float.TYPE, Paint.class

                    ,hook);
        } catch (Throwable e) {
            e.printStackTrace();
        }

    }
}
