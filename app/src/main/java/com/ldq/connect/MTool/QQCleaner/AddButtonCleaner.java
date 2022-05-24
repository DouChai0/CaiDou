package com.ldq.connect.MTool.QQCleaner;

import android.app.AlertDialog;

import com.ldq.Utils.MField;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

public class AddButtonCleaner {
    static List CacheList = null;
    public static void StartClean(List CleanList) {
        CacheList = CleanList;
        List<String> NowSetData = MConfig.Get_List("Main","QQCleaner","AddButton");
        Iterator it = CleanList.iterator();
        while (it.hasNext()) {
            Object ItemObj = it.next();
            try{
                String SingerData = ""+MField.GetField(ItemObj,"title",String.class);
                SingerData = SingerData + "("+MField.GetField(ItemObj,"id",int.class)+")";
                if(NowSetData.contains(SingerData))
                {
                    it.remove();
                }
            }catch (Exception ex)
            {

            }

        }
    }
    public static void StartShowSet()
    {
        if(CacheList==null)
        {
            Utils.ShowToast("请先打开一次加号菜单再进行设置");
            return;
        }
        List<String> NowSetData = MConfig.Get_List("Main","QQCleaner","AddButton");
        ArrayList<String> NewestListData = new ArrayList();

        for(Object ItemObj : CacheList)
        {
            try{
                String SingerData = ""+MField.GetField(ItemObj,"title",String.class);
                SingerData = SingerData + "("+MField.GetField(ItemObj,"id",int.class)+")";
                NewestListData.add(SingerData);
            }catch (Exception ex)
            {

            }
        }

        HashSet<String> ShowAll = new HashSet<>(NewestListData);
        ShowAll.addAll(NowSetData);

        String[] ShowInfo = ShowAll.toArray(new String[0]);
        boolean[] bl = new boolean[ShowInfo.length];
        for(int i=0;i<ShowInfo.length;i++)
        {
            if(NowSetData.contains(ShowInfo[i]))bl[i]=true;
        }
        new AlertDialog.Builder(Utils.GetThreadActivity(),3)
                .setMultiChoiceItems(ShowInfo, bl, (dialog, which, isChecked) -> {

                }).setNeutralButton("保存", (dialog, which) -> {
                    ArrayList<String> NewSet = new ArrayList<>();
                    for(int i=0;i<bl.length;i++) {
                        if(bl[i]) {
                            NewSet.add(ShowInfo[i]);
                        }
                    }
                    MConfig.Put_List("Main","QQCleaner","AddButton",NewSet);
                }).show();

    }
}
