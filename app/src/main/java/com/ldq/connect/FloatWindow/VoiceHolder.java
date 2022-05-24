package com.ldq.connect.FloatWindow;

import android.content.Context;
import android.text.Editable;
import android.text.TextWatcher;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ldq.Utils.Utils;
import com.ldq.connect.MHookEnvironment;

import java.io.File;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Locale;

public class VoiceHolder extends LinearLayout implements ViewPager.RequestShow{
    String NowPath;
    LinkedList<String> EnterDic = new LinkedList();
    LinearLayout MList;
    ScrollView mMainLayout;

    public VoiceHolder(Context context) {
        super(context);
        NowPath = MHookEnvironment.PublicStorageModulePath + "Voice/";
        if(!new File(NowPath).exists())new File(NowPath).mkdirs();

        setOrientation(VERTICAL);
        mMainLayout = new ScrollView(context);
        SearchInit();




        MList = new LinearLayout(context);
        MList.setOrientation(LinearLayout.VERTICAL);

        mMainLayout.addView(MList);
        addView(mMainLayout,new LayoutParams(Utils.dip2px(getContext(),300), Utils.dip2px(getContext(),270)));


        FlushList();
    }


    public void FlushList()
    {
        //刷新显示列表

        MList.removeAllViews();

        File[] mList = SortFiles(new File(NowPath).listFiles());

        for(File fItem : mList)
        {
            if(!fItem.getName().toLowerCase(Locale.ROOT).endsWith(".bak"))
            {
                MList.addView(new VoiceItem(getContext(),fItem.getName(),fItem.getAbsolutePath(),fItem.isDirectory(),this));
            }

        }

    }
    static String SearchText = "";

    private void SearchInit()
    {
        EditText SearchBox = new EditText(getContext());
        SearchBox.setText(SearchText);
        SearchBox.setTextSize(20);
        SearchBox.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                SearchTextChange(s.toString());
            }
        });
        addView(SearchBox,new LayoutParams(Utils.dip2px(getContext(),300), Utils.dip2px(getContext(),30)));
    }
    private void SearchTextChange(String t)
    {
        if(t.isEmpty())
        {
            BackToUp();
            return;
        }
        MList.removeAllViews();
        EnterDic.clear();
        EnterDic.addLast(MHookEnvironment.PublicStorageModulePath + "Voice/");
        SearchHandler(MHookEnvironment.PublicStorageModulePath + "Voice/",t);
    }
    private void SearchHandler(String Path,String sName)
    {
        File[] f = new File(Path).listFiles();
        if(f!=null)
        {
            for(File s : f)
            {
                if(s.isDirectory())
                {
                    SearchHandler(s.getAbsolutePath(),sName);
                }
                if(s.isFile())
                {
                    if(s.getName().contains(sName))
                    {
                        OnSearchResule(s);
                    }
                }
            }
        }
    }
    private void OnSearchResule(File path)
    {
        MList.addView(new VoiceItem(getContext(),path.getName(),path.getAbsolutePath(),path.isDirectory(),this));
    }
    private File[] SortFiles(File[] fs)
    {
        ArrayList<File> dics = new ArrayList<>();
        ArrayList<File> files = new ArrayList<>();
        for(File f : fs)
        {
            if(f.isDirectory())
            {
                dics.add(f);
                continue;
            }
            if(f.isFile())
            {
                files.add(f);
            }
        }
        dics.addAll(files);
        return dics.toArray(new File[0]);
    }
    public void ChangeToFile(String Path)
    {
        //更改显示目录
        if(!Path.endsWith("/"))Path=Path+"/";

        EnterDic.addLast(NowPath);

        NowPath = Path;

        MList.removeAllViews();
        FlushList();
    }
    public void BackToUp()
    {
        if(EnterDic.size()<1)return;
        NowPath = EnterDic.pollLast();

        MList.removeAllViews();
        FlushList();
    }

    @Override
    public void requestShow()
    {
        FlushList();
        invalidate();

        //MLogCat.Print_Debug("ShowSuccess");
    }

    @Override
    public void BackEvent() {
        BackToUp();
    }

    @Override
    public void requestExit() {
        MList.removeAllViews();
    }

    @Override
    public void requestOnClick() {

    }
}
