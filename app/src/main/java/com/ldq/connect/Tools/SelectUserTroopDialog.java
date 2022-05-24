package com.ldq.connect.Tools;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.ldq.Utils.MField;
import com.ldq.connect.JavaPlugin.JavaPluginUtils;
import com.ldq.connect.QQUtils.QQTools;

import java.util.ArrayList;
import java.util.List;

public class SelectUserTroopDialog {
    private boolean ShowTroop;
    private boolean ShowFriend;
    private boolean ShowTroopMember;
    private Context mContext;
    public SelectUserTroopDialog(Context context,boolean UseTroop,boolean UseFriend,boolean FriendInTroop){
        ShowTroop = UseTroop;
        ShowFriend = UseFriend;
        ShowTroopMember = FriendInTroop;


        mContext = context;
    }

    private List<String> SelectTroop = new ArrayList<>();
    private List<String> SelectFriend = new ArrayList<>();
    public void SetSelectedTroop(ArrayList<String> SelectTroop){
        this.SelectTroop = SelectTroop;
    }
    public void SetSelectedFriend(ArrayList<String> SelectFriend){
        this.SelectFriend = SelectFriend;
    }
    @SuppressLint("ResourceType")
    public void StartShow(OnSelectCallback callback){
        RelativeLayout mRoot = new RelativeLayout(mContext);

        RadioGroup SelectBar = new RadioGroup(mContext);
        SelectBar.setOrientation(LinearLayout.HORIZONTAL);
        SelectBar.setId(88999);
        mRoot.addView(SelectBar);

        RadioButton chSelectTroop = new RadioButton(mContext);
        chSelectTroop.setTextColor(Color.BLACK);
        chSelectTroop.setText("选择群聊");
        //chSelectTroop.setChecked(true);
        SelectBar.addView(chSelectTroop);

        RadioButton chSelectFriend = new RadioButton(mContext);
        chSelectFriend.setTextColor(Color.BLACK);
        chSelectFriend.setText("选择好友");
        SelectBar.addView(chSelectFriend);

        ScrollView scList = new ScrollView(mContext);
        RelativeLayout.LayoutParams parmas = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        parmas.addRule(RelativeLayout.BELOW,88999);
        parmas.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM,1);
        mRoot.addView(scList,parmas);


        LinearLayout TroopSelectView = new LinearLayout(mContext);
        TroopSelectView.setOrientation(LinearLayout.VERTICAL);
        ArrayList<CheckBox> TroopSelectBoxList = new ArrayList<>();
        ArrayList<JavaPluginUtils.GroupInfo> TroopList = JavaPluginUtils.GetGroupInfo();
        for(JavaPluginUtils.GroupInfo info : TroopList){
            CheckBox ch = new CheckBox(mContext);
            ch.setText(info.GroupName+"("+info.GroupUin+")");
            if(SelectTroop.contains(info.GroupUin))ch.setChecked(true);
            ch.setTag(info.GroupUin);
            ch.setTextColor(Color.BLACK);
            TroopSelectView.addView(ch);
            TroopSelectBoxList.add(ch);
        }


        LinearLayout FriendSelectView = new LinearLayout(mContext);
        FriendSelectView.setOrientation(LinearLayout.VERTICAL);
        ArrayList<CheckBox> FriendSelectBoxList = new ArrayList<>();
        List list = QQTools.User_GetFriendList();
        for(Object sInfo : list)
        {
            try{
                String Uin = MField.GetField(sInfo,"uin",String.class);
                String Name = MField.GetField(sInfo,"name",String.class);
                CheckBox ch = new CheckBox(mContext);
                if(SelectFriend.contains(Uin))ch.setChecked(true);
                ch.setTag(Uin);
                ch.setChecked(SelectFriend.contains(Uin));
                ch.setText(Name+"("+Uin+")");
                ch.setTextColor(Color.BLACK);


                FriendSelectBoxList.add(ch);
                FriendSelectView.addView(ch);
            }catch (Exception e){

            }
        }

        scList.addView(TroopSelectView);

        chSelectTroop.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(buttonView.isPressed() && isChecked){
                scList.removeAllViews();
                scList.addView(TroopSelectView);
            }
        });

        chSelectFriend.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(buttonView.isPressed() && isChecked){
                scList.removeAllViews();
                scList.addView(FriendSelectView);
            }
        });

        new AlertDialog.Builder(mContext,3)
                .setTitle("选择需要的群聊和好友")
                .setView(mRoot)
                .setNegativeButton("确定选择", (dialog, which) -> {
                    ArrayList<SelectResult> mResult = new ArrayList<>();
                    for(CheckBox ch :TroopSelectBoxList){
                        if(ch.isChecked()) {
                            SelectResult NewResult = new SelectResult();
                            NewResult.TroopUin = (String) ch.getTag();
                            NewResult.Type = 1;
                            mResult.add(NewResult);
                        }
                    }

                    for(CheckBox ch : FriendSelectBoxList){
                        if(ch.isChecked()){
                            SelectResult NewResult = new SelectResult();
                            NewResult.Uin = (String) ch.getTag();
                            NewResult.Type = 2;
                            mResult.add(NewResult);
                        }
                    }
                    new Handler(Looper.getMainLooper()).post(()-> callback.onSelected(mResult));
                }).show();
    }
    public class SelectResult{
        public String Uin;
        public String TroopUin;
        public int Type;
    }
    public interface OnSelectCallback{
        void onSelected(ArrayList<SelectResult> result);
    }
}
