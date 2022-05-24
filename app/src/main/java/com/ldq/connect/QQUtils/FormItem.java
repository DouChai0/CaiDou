package com.ldq.connect.QQUtils;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CompoundButton;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MMethod;

import java.lang.reflect.Field;

public class FormItem {
    public static View AddListItem(Context context, String Title, View.OnClickListener listener) {
        try {
            Class mClass = MClass.loadClass("com.tencent.mobileqq.widget.FormSimpleItem");
            ViewGroup mSetItem = MClass.CallConstrutor(mClass, new Class[]{Context.class}, context);
            CharSequence mChar = Title;
            MMethod.CallMethod(mSetItem, mClass, "setLeftText", void.class, new Class[]{CharSequence.class}, mChar);

            TextView t2 = new TextView(context);
            Field f = FieldTable.FindFirstField(mSetItem.getClass(),TextView.class);
            if(f!=null){

                //花里胡哨炫彩字体创建添加

                f.setAccessible(true);

                TextView source = (TextView) f.get(mSetItem);

                t2.setTextSize(0,source.getTextSize());

                t2.setLayoutParams(source.getLayoutParams());
                t2.setText(Title);
                t2.setTextColor(Color.BLACK);
                mSetItem.removeView(source);
                t2.setId(source.getId());



                mSetItem.addView(t2);
                f.set(mSetItem,t2);
            }

            mSetItem.setBackgroundColor(Color.WHITE);
            mSetItem.setOnClickListener(listener);
            mSetItem.setAlpha(1);
            return mSetItem;
        } catch (Exception e) {
            return null;
        }
    }
    public static View AddCommonListItem(Context context, String Title, View.OnClickListener listener) {
        try {
            Class mClass = MClass.loadClass("com.tencent.mobileqq.widget.FormSimpleItem");
            ViewGroup mSetItem = MClass.CallConstrutor(mClass, new Class[]{Context.class}, context);
            CharSequence mChar = Title;
            MMethod.CallMethod(mSetItem, mClass, "setLeftText", void.class, new Class[]{CharSequence.class}, mChar);
            mSetItem.setOnClickListener(listener);
            return mSetItem;
        } catch (Exception e) {
            return null;
        }
    }
    public static View AddCheckItem(Context context, String Title, CompoundButton.OnCheckedChangeListener listener, boolean CheckStatus)
    {
        try{
            Class mClass = MClass.loadClass("com.tencent.mobileqq.widget.FormSwitchItem");
            ViewGroup mSetItem = MClass.CallConstrutor(mClass,new Class[]{Context.class},context);
            CharSequence mChar = Title;
            MMethod.CallMethod(mSetItem,mClass,"setText",void.class,new Class[]{CharSequence.class},mChar);
            MMethod.CallMethod(mSetItem,mClass,"setOnCheckedChangeListener",void.class,new Class[]{MClass.loadClass("android.widget.CompoundButton$OnCheckedChangeListener")},listener);
            MMethod.CallMethod(mSetItem,mClass,"setChecked",void.class,new Class[]{boolean.class},CheckStatus);

            TextView t2 = new TextView(context);
            Field f = FieldTable.FindFirstField(mSetItem.getClass(),TextView.class);
            if(f!=null){

                //花里胡哨炫彩字体创建添加
                f.setAccessible(true);

                TextView source = (TextView) f.get(mSetItem);
                t2.setTextSize(0,source.getTextSize());

                t2.setLayoutParams(source.getLayoutParams());
                t2.setText(Title);
                t2.setTextColor(Color.BLACK);
                mSetItem.removeView(source);
                t2.setId(source.getId());


                mSetItem.removeView(source);
                mSetItem.addView(t2);
                f.set(mSetItem,t2);
            }

            //TextView t = MField.GetFirstField(mSetItem,mClass,TextView.class);
            //t.setTextColor(Color.BLACK);
            mSetItem.setBackgroundColor(Color.WHITE);
            mSetItem.setAlpha(1);

            return mSetItem;
        }
        catch (Exception e)
        {
            return null;
        }
    }
    public static View AddMultiItem(Context context, String Line1, String SecondLine, CompoundButton.OnCheckedChangeListener listener)
    {
        try{
            ViewGroup mMultiItem = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.widget.FormMultiLineSwitchItem"),new Class[]{Context.class},context);
            MMethod.CallMethod(mMultiItem,MClass.loadClass("com.tencent.mobileqq.widget.FormMultiLineSwitchItem"),"setSecendLineText",void.class,
                    new Class[]{String.class},SecondLine);
            CharSequence mChar = Line1;
            MMethod.CallMethod(mMultiItem, MClass.loadClass("com.tencent.mobileqq.widget.FormSwitchItem"),"setText",void.class,new Class[]{CharSequence.class},mChar);
            MMethod.CallMethod(mMultiItem, MClass.loadClass("com.tencent.mobileqq.widget.FormSwitchItem"),"setOnCheckedChangeListener",void.class,
                    new Class[]{MClass.loadClass("android.widget.CompoundButton$OnCheckedChangeListener")},listener);

            TextView t2 = new TextView(context);
            Field f = FieldTable.FindFirstField(mMultiItem.getClass().getSuperclass(),TextView.class);
            if(f!=null){

                //花里胡哨炫彩字体创建添加
                f.setAccessible(true);

                TextView source = (TextView) f.get(mMultiItem);
                t2.setTextSize(0,source.getTextSize());

                t2.setLayoutParams(source.getLayoutParams());
                t2.setText(Line1);
                t2.setTextColor(Color.BLACK);
                mMultiItem.removeView(source);
                t2.setId(source.getId());


                mMultiItem.removeView(source);
                mMultiItem.addView(t2);
                f.set(mMultiItem,t2);
            }
            mMultiItem.setBackgroundColor(Color.WHITE);
            mMultiItem.setAlpha(1);
            return mMultiItem;

        }
        catch (Exception ex)
        {
            return null;
        }

    }
    public static View AddMultiItem(Context context, String Line1, String SecondLine, CompoundButton.OnCheckedChangeListener listener,boolean Checked)
    {
        try{
            ViewGroup mMultiItem = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.widget.FormMultiLineSwitchItem"),new Class[]{Context.class},context);
            MMethod.CallMethod(mMultiItem,MClass.loadClass("com.tencent.mobileqq.widget.FormMultiLineSwitchItem"),"setSecendLineText",void.class,
                    new Class[]{String.class},SecondLine);
            CharSequence mChar = Line1;
            MMethod.CallMethod(mMultiItem, MClass.loadClass("com.tencent.mobileqq.widget.FormSwitchItem"),"setText",void.class,new Class[]{CharSequence.class},mChar);
            MMethod.CallMethod(mMultiItem, MClass.loadClass("com.tencent.mobileqq.widget.FormSwitchItem"),"setOnCheckedChangeListener",void.class,
                    new Class[]{MClass.loadClass("android.widget.CompoundButton$OnCheckedChangeListener")},listener);
            MMethod.CallMethod(mMultiItem, MClass.loadClass("com.tencent.mobileqq.widget.FormSwitchItem"),"setChecked",void.class,
                    new Class[]{boolean.class},Checked);
            mMultiItem.setAlpha(1);

            TextView t2 = new TextView(context);
            Field f = FieldTable.FindFirstField(mMultiItem.getClass(),TextView.class);


            if(f!=null){

                //花里胡哨炫彩字体创建添加
                f.setAccessible(true);

                TextView source = (TextView) f.get(mMultiItem);
                t2.setTextSize(0,source.getTextSize());

                t2.setLayoutParams(source.getLayoutParams());
                t2.setText(Line1);
                t2.setTextColor(Color.BLACK);
                mMultiItem.removeView(source);
                t2.setId(source.getId());


                mMultiItem.removeView(source);
                mMultiItem.addView(t2);
            }
            mMultiItem.setBackgroundColor(Color.WHITE);
            return mMultiItem;

        }
        catch (Exception ex)
        {
            return null;
        }

    }
    public static View AddMultiItem(Context context, String Line, String SecondLine)
    {
        try{
            ViewGroup mMultiItem = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.widget.FormMutiItem"),new Class[]{Context.class},context);
            CharSequence mChar = Line;
            MMethod.CallMethod(mMultiItem,MClass.loadClass("com.tencent.mobileqq.widget.FormMutiItem"),"setFirstLineText",void.class,
                    new Class[]{CharSequence.class},mChar);
            mChar = SecondLine;
            MMethod.CallMethod(mMultiItem,MClass.loadClass("com.tencent.mobileqq.widget.FormMutiItem"),"setSecondLineText",void.class,
                    new Class[]{CharSequence.class},mChar);
            mMultiItem.setAlpha(1);

            TextView t2 = new TextView(context);
            Field f = FieldTable.FindFirstField(mMultiItem.getClass(),TextView.class);

            Field TrueLayout = FieldTable.FindFirstField(mMultiItem.getClass(), LinearLayout.class);
            TrueLayout.setAccessible(true);
            LinearLayout ll = (LinearLayout) TrueLayout.get(mMultiItem);
            if(f!=null){

                //花里胡哨炫彩字体创建添加
                f.setAccessible(true);

                TextView source = (TextView) f.get(mMultiItem);
                t2.setTextSize(0,source.getTextSize());

                t2.setLayoutParams(source.getLayoutParams());
                t2.setText(Line);
                t2.setTextColor(Color.BLACK);
                mMultiItem.removeView(source);
                t2.setId(source.getId());



                ll.removeView(source);


                ll.addView(t2,0);
            }
            mMultiItem.setBackgroundColor(Color.WHITE);
            return mMultiItem;

        }
        catch (Exception ex)
        {
            return null;
        }
    }
}
