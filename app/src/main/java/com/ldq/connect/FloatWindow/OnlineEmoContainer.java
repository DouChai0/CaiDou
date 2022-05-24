package com.ldq.connect.FloatWindow;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.StringUtils;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;

import org.json.JSONException;
import org.json.JSONObject;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;

public class OnlineEmoContainer extends ScrollView implements ViewPager.RequestShow {
    EditText SearchBox;
    LinearLayout mRootView;

    OnlineEmoHolder ImageContainer;//图像容器
    public OnlineEmoContainer(Context context) {
        super(context);
        mRootView = new LinearLayout(context);
        mRootView.setOrientation(LinearLayout.VERTICAL);
        addView(mRootView);

        RegisterEvent();

    }
    public void RegisterEvent()
    {
        //搜索框
        SearchBox = new EditText(getContext());
        SearchBox.setHint("长按可切换搜索来源");
        SearchBox.setTextSize(20);
        SearchBox.setOnLongClickListener(v -> {
            SetSearchSource();
            return true;
        });
        SearchBox.setSingleLine(true);
        SearchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        SearchBox.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH)
            {
                StartSearchEvent(v.getText().toString());
            }
            return false;
        });
        mRootView.addView(SearchBox,new LinearLayout.LayoutParams(Utils.dip2px(getContext(),300), Utils.dip2px(getContext(),30)));
    }
    int Checked = 0;
    private void SetSearchSource()
    {
        Activity act = Utils.GetThreadActivity();
        String[] st = new String[]{"斗图吧","DIY斗图","堆糖","百度图片"};
        Checked = (int) MConfig.Get_Long("FloatWindow","SearchImage","type",0);

        new AlertDialog.Builder(act,3)
                .setSingleChoiceItems(st, (int) MConfig.Get_Long("FloatWindow","SearchImage","type",0), (dialog, which) -> {
                    Checked = which;
                }).setNeutralButton("保存", (dialog, which) -> {
                    MConfig.Put_Long("FloatWindow","SearchImage","type",Checked);
                }).show();
    }
    private void StartSearchEvent(String SearchText)
    {
        if(ImageContainer!=null)
        {
            ImageContainer.Destory();
            mRootView.removeView(ImageContainer);
        }
        ArrayList<String> Result=null;
        if(MConfig.Get_Long("FloatWindow","SearchImage","type",0)==0)Result = Search_Doutula(SearchText);
        if(MConfig.Get_Long("FloatWindow","SearchImage","type",0)==1)Result = Search_DIYDoutu(SearchText);
        if(MConfig.Get_Long("FloatWindow","SearchImage","type",0)==2)Result = Search_DuiTang(SearchText);
        if(MConfig.Get_Long("FloatWindow","SearchImage","type",0)==3)Result = Search_Baidu(SearchText);

        if(Result!=null)
        {
            ImageContainer = new OnlineEmoHolder(getContext(),Result);
            mRootView.addView(ImageContainer);
        }

    }
    private ArrayList<String> Search_Doutula(String SearchText)
    {
        String SearchResultText = HttpUtils.getHtmlResourceByUrl("https://www.doutula.com/search?keyword="+ URLEncoder.encode(SearchText));
        String[] textResult = StringUtils.GetStringMiddleMix(SearchResultText,
                "<img referrerpolicy=\"no-referrer\" src=\"//static.doutula.com/img/loader.gif?33\" data-backup=\"",
                "\" class=\"");
        return new ArrayList<>(Arrays.asList(textResult));
    }
    private ArrayList<String> Search_DIYDoutu(String SearchText)
    {
        String SearchResultText = HttpUtils.getHtmlResourceByUrl("https://www.diydoutu.com/tag/"+ URLEncoder.encode(SearchText));
        String[] textResult = StringUtils.GetStringMiddleMix(SearchResultText,
                "\" data-src=\"",
                "\" alt=\"");
        return new ArrayList<>(Arrays.asList(textResult));
    }
    private ArrayList<String> Search_DuiTang(String SearchText)
    {
        String SearchResultText = HttpUtils.getHtmlResourceByUrl_Agent("https://www.duitang.com/search/?kw="+ URLEncoder.encode(SearchText)+"&type=feed");


        String[] textResult = StringUtils.GetStringMiddleMix(SearchResultText,
                "data-iid=\"\" src=\"",
                "\" height=");
        return new ArrayList<>(Arrays.asList(textResult));
    }
    private ArrayList<String> Search_Baidu(String SearchText)
    {
        String SearchResultText = HttpUtils.getHtmlResourceByUrl_Agent("https://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&fp=result&queryWord="
                + URLEncoder.encode(SearchText)
                +"&word=" + URLEncoder.encode(SearchText)
                +"&pn=0&rn=30&logid=11711916591504654063"
        );
        if(SearchResultText.contains("spider access"))
        {
            JSONObject NewJson = null;
            try {
                NewJson = new JSONObject(SearchResultText);
            } catch (JSONException e) {
                e.printStackTrace();
            }
            SearchResultText = HttpUtils.getHtmlResourceByUrl_Agent("https://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&fp=result&queryWord="
                    + URLEncoder.encode(SearchText)
                    +"&word=" + URLEncoder.encode(SearchText)
                    +"&pn=0&rn=30&logid="+NewJson.optString("bfe_log_id")
            );
            if(SearchResultText.contains("spider access"))
            {
                SearchResultText = HttpUtils.getHtmlResourceByUrl_Agent("https://image.baidu.com/search/acjson?tn=resultjson_com&ipn=rj&fp=result&queryWord="
                        + URLEncoder.encode(SearchText)
                        +"&word=" + URLEncoder.encode(SearchText)
                        +"&pn=0&rn=30&logid="+NewJson.optString("bfe_log_id")
                );
            }
        }
        String[] textResult = StringUtils.GetStringMiddleMix(SearchResultText,
                "\"middleURL\":\"",
                "\"");
        return new ArrayList<>(Arrays.asList(textResult));
    }
    @Override
    public void requestShow() {

    }

    @Override
    public void BackEvent() {

    }

    @Override
    public void requestExit() {

    }

    @Override
    public void requestOnClick() {

    }

}
