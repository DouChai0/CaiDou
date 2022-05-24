package com.ldq.connect.FloatWindow;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.View;
import android.view.inputmethod.EditorInfo;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ldq.Utils.SMToolHelper;
import com.ldq.Utils.Utils;
import com.ldq.connect.ServerTool.EncEnv;

import org.json.JSONArray;
import org.json.JSONObject;


public class OnlineVoiceHolder extends LinearLayout  implements ViewPager.RequestShow{
    private boolean SearchMode;
    private String NowSelectUin;
    private LinearLayout SearchLayout;
    private LinearLayout mItemLayout;
    private TextView tPageView;

    int Page = 1;
    int MaxPage = 1;

    boolean SwitchTuyaMode = false;


    public OnlineVoiceHolder(Context context) {
        super(context);
        setOrientation(VERTICAL);
        SearchMode = false;
        NowSelectUin = "";
        SearchLayout = new LinearLayout(context);

        Button btnUp = new Button(context);
        btnUp.setText("上一页");
        btnUp.setOnClickListener(v->PageBack());
        SearchLayout.addView(btnUp);

        Button btnDown = new Button(context);
        btnDown.setText("下一页");
        btnDown.setOnClickListener(v-> PageNext());
        SearchLayout.addView(btnDown);

        CheckBox ch = new CheckBox(context);
        ch.setText("显示涂鸦");
        ch.setTextColor(Color.BLACK);
        ch.setOnCheckedChangeListener((buttonView, isChecked) -> {
            SwitchTuyaMode =  isChecked;
            Page = 1;
            FlushShow();
        });
        SearchLayout.addView(ch);

        tPageView = new TextView(context);
        tPageView.setTextColor(Color.BLACK);
        tPageView.setTextSize(20);
        tPageView.setText("0/0");
        SearchLayout.addView(tPageView);
        BuildSearchBox();

        ScrollView sc = new ScrollView(context);
        addView(sc,new LayoutParams(Utils.dip2px(getContext(),300), Utils.dip2px(getContext(),230)));
        mItemLayout = new LinearLayout(context);
        mItemLayout.setOrientation(VERTICAL);
        sc.addView(mItemLayout);



        addView(SearchLayout,new LayoutParams(Utils.dip2px(getContext(),300), Utils.dip2px(getContext(),40)));



    }
    private void BuildSearchBox()
    {
        EditText SearchBox = new EditText(getContext());
        SearchBox.setTextSize(20);
        SearchBox.setSingleLine(true);
        SearchBox.setImeOptions(EditorInfo.IME_ACTION_SEARCH);
        SearchBox.setOnEditorActionListener((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_SEARCH)
            {
                StartSearch(v.getText().toString());
            }
            return false;
        });
        addView(SearchBox,new LayoutParams(Utils.dip2px(getContext(),300), Utils.dip2px(getContext(),30)));
    }
    private void StartSearch(String KeyWord)
    {
        if(TextUtils.isEmpty(KeyWord))return;
        SearchMode = true;
        NowSelectUin = "";

        mItemLayout.removeAllViews();
        try{
            String SearchResult = SMToolHelper.GetDataFromServer(EncEnv.Voice_Search,KeyWord);

            JSONObject RetObj = new JSONObject(SearchResult);

            JSONArray mResultArray = RetObj.getJSONArray("data");
            for(int i=0;i<mResultArray.length();i++)
            {
                JSONObject item = mResultArray.getJSONObject(i);
                String Name = item.getString("Name");
                String key = item.getString("FileID");
                String uin = item.getString("Uin");

                View mView = new OnlineVoiceItem(getContext(),Name,key,uin,false,this);
                mItemLayout.addView(mView);
            }

        }catch (Exception ex)
        {
            Utils.ShowToast("发生错误:\n"+ex);
        }



    }
    private void FlushShow() {
        if(TextUtils.isEmpty(NowSelectUin))
        {
            mItemLayout.removeAllViews();
            SearchMode = false;

            if(!SwitchTuyaMode)
            {
                try{
                    JSONObject PushData = new JSONObject();
                    PushData.put("Page",Page);

                    String PushResult = SMToolHelper.GetDataFromServer(EncEnv.Voice_Get_Uin_List,PushData.toString());


                    JSONObject mResult = new JSONObject(PushResult);
                    JSONArray ResultArray = mResult.getJSONArray("Data");

                    for(int i=0;i<ResultArray.length();i++)
                    {
                        JSONObject item = ResultArray.getJSONObject(i);
                        String Uin = item.getString("Uin");
                        String name = item.optString("Name");
                        View mView;
                        if(TextUtils.isEmpty(name))
                        {
                            mView = new OnlineVoiceItem(getContext(),Uin,Uin,Uin,true,this);
                        }else
                        {
                            mView = new OnlineVoiceItem(getContext(),Uin +"-"+name,Uin,Uin,true,this);
                        }
                        mItemLayout.addView(mView);

                    }

                    MaxPage = mResult.optInt("MaxPage");
                    tPageView.setText(Page+"/"+MaxPage);
                }catch (Exception ex)
                {
                    Utils.ShowToast("发生错误:\n"+ex);
                }
            }else
            {
                try{
                    JSONObject PushData = new JSONObject();
                    PushData.put("Page",Page);

                    String PushResult = SMToolHelper.GetDataFromServer(EncEnv.Tuya_Get_Uin_List,PushData.toString());


                    JSONObject mResult = new JSONObject(PushResult);
                    JSONArray ResultArray = mResult.getJSONArray("Data");

                    for(int i=0;i<ResultArray.length();i++)
                    {
                        JSONObject item = ResultArray.getJSONObject(i);
                        String Uin = item.getString("Uin");
                        String name = item.optString("Name");
                        View mView;
                        if(TextUtils.isEmpty(name))
                        {
                            mView = new OnlineVoiceItem(getContext(),Uin,Uin,Uin,true,this);
                        }else
                        {
                            mView = new OnlineVoiceItem(getContext(),Uin +"-"+name,Uin,Uin,true,this);
                        }
                        mItemLayout.addView(mView);

                    }

                    MaxPage = mResult.optInt("MaxPage");
                    tPageView.setText(Page+"/"+MaxPage);
                }catch (Exception ex)
                {
                    Utils.ShowToast("发生错误:\n"+ex);
                }
            }

        }else
        {
            if(SwitchTuyaMode)
            {
                try{
                    mItemLayout.removeAllViews();

                    String Result = SMToolHelper.GetDataFromServer(EncEnv.Tuya_Get_Tuya_List,NowSelectUin);

                    JSONObject NewObj = new JSONObject(Result);
                    JSONArray NewArray = NewObj.getJSONArray("data");
                    for(int i=0;i<NewArray.length();i++){
                        JSONObject ItemInfo = NewArray.getJSONObject(i);
                        String DownloadUrl = ItemInfo.getString("FileID");
                        String Name = ItemInfo.getString("Name");
                        mItemLayout.addView(new OnlineVoiceItem(getContext(),Name,DownloadUrl,NowSelectUin,false,this,true));
                    }

                    tPageView.setText(""+NowSelectUin);
                }catch (Exception ex)
                {
                    Utils.ShowToast("发生错误:\n"+ex);
                }
            }else
            {
                try{
                    mItemLayout.removeAllViews();

                    String Result = SMToolHelper.GetDataFromServer(EncEnv.Voice_Get_Voice_List,NowSelectUin);
                    JSONObject ResultJson = new JSONObject(Result);
                    JSONArray mResultArray = ResultJson.getJSONArray("data");
                    for(int i=0;i<mResultArray.length();i++)
                    {
                        JSONObject item = mResultArray.getJSONObject(i);
                        String DownUrl = item.getString("FileID");
                        String Name = item.optString("Name");

                        View mView = new OnlineVoiceItem(getContext(),Name,DownUrl,NowSelectUin,false,this);
                        mItemLayout.addView(mView);
                    }

                    tPageView.setText(""+NowSelectUin);
                }catch (Exception ex)
                {
                    Utils.ShowToast("发生错误:\n"+ex);
                }
            }

        }
    }
    public void ShowUin(String uin)
    {
        mItemLayout.removeAllViews();
        NowSelectUin = uin;

        FlushShow();
    }
    private void PageNext()
    {
        if(Page==MaxPage || SearchMode)return;

        Page++;
        FlushShow();
    }
    private void PageBack()
    {
        if(Page==1|| SearchMode)return;
        Page--;
        FlushShow();
    }


    @Override
    public void requestShow() {
        if(SearchMode)
        {
            SearchMode = false;
            NowSelectUin = "";
            FlushShow();
        }else
        {
            FlushShow();
        }

    }

    @Override
    public void BackEvent() {
        if(!TextUtils.isEmpty(NowSelectUin))
        {
            NowSelectUin="";
            FlushShow();
        }
    }

    @Override
    public void requestExit() {
        mItemLayout.removeAllViews();
    }

    @Override
    public void requestOnClick() {

    }
}
