package com.ldq.connect;

import android.app.AlertDialog;
import android.os.Handler;
import android.os.Looper;

import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.GlobalConfig;

public class SpringAct {
    private static String text = "为了迎接春节的到来,小菜豆在春节将进行以下活动:\n" +
            "1.答题赢红包,在主菜单最选择 春节答题活动 即可参与到答题活动,有小小的红包奖励(1.31 8:00 - 16:00)\n" +
            "2.赞助功能体验,在1.31-2.3这段时间内所有用户皆可体验使用全部功能\n" +
            "3.特殊功能开放,将在春节限时开放 自动领红包 功能,于1.31晚上20:00开始直到作者Play游戏卡带结束一周目后关闭(嘿嘿)";
    public static void StartShow(){
        new Handler(Looper.getMainLooper())
                .postDelayed(()->{
                    if (!GlobalConfig.Get_Boolean("GlobalNotifyShow",false)){
                        GlobalConfig.Put_Boolean("GlobalNotifyShow",true);
                        new AlertDialog.Builder(Utils.GetThreadActivity(),3)
                                .setTitle("小菜豆--公告")
                                .setMessage(text)
                                .setNegativeButton("确定", (dialog, which) -> {

                                }).show();
                    }
                },2000);


    }
}
