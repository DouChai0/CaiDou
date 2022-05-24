package com.ldq.connect.MainWorker.BaseWorker.BaseMenu;

import android.app.Activity;
import android.app.AlertDialog;
import android.graphics.Color;
import android.os.Environment;
import android.text.TextUtils;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.ScrollView;

import com.ldq.Utils.Utils;
import com.ldq.connect.FloatWindow.Init;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.JavaPlugin.JavaPlugin;
import com.ldq.connect.MTool.AutoMessage;
import com.ldq.connect.MTool.AutoResendMessage;
import com.ldq.connect.MTool.FriendObserve.FriendDeleteInfoShow;
import com.ldq.connect.MTool.OutputTroopOrFriendList;
import com.ldq.connect.MTool.QQCleaner.QQCleaner;
import com.ldq.connect.MainWorker.LittileHook.Hook_Change_At_Someone;
import com.ldq.connect.MainWorker.LittileHook.Hook_Change_Dev_Info;
import com.ldq.connect.MainWorker.LittileHook.Hook_Disable_At_Notify;
import com.ldq.connect.MainWorker.LittileHook.Hook_Dpi_Change;
import com.ldq.connect.MainWorker.LittileHook.Hook_Redirect_Download_Path;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Avoid_Some_Message;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Message_split;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Message_tail;
import com.ldq.connect.MainWorker.QQManagerHelper.TroopManager_Set_Alarm_Group;
import com.ldq.connect.MainWorker.QQRedBagHelper.AutoRedPackSetting;
import com.ldq.connect.MainWorker.QQRedBagHelper.Hook_For_VoiceRedbag;
import com.ldq.connect.MainWorker.WidgetHandler.Handler_Repeat_Icon_Set;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Change_Widget_ShowText;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Clean_Msg_Long_Click_Common;
import com.ldq.connect.MainWorker.WidgetHook.Hook_For_JiaHao_Panel;
import com.ldq.connect.MainWorker.WidgetHook.Hook_For_Main_Slide_Side;
import com.ldq.connect.QQUtils.FormItem;
import com.ldq.connect.QQUtils.QQTools;
import com.ldq.connect.ServerTool.UserStatus;
import com.ldq.connect.Tools.MPositionDialog;
import com.ldq.connect.Tools.TroopOpen;

public class Menu_Main {
    public static void ShowSet(){
        Activity mAct = Utils.GetThreadActivity();
        ScrollView sc = new ScrollView(mAct);


        LinearLayout mLL = new LinearLayout(mAct);
        sc.addView(mLL);


        mLL.setOrientation(LinearLayout.VERTICAL);
        mLL.setBackgroundColor(Color.BLACK);


        mLL.addView(FormItem.AddListItem(mAct, "Java脚本", v -> JavaPlugin.ShowSettings(mAct)));
        mLL.addView(FormItem.AddListItem(mAct,"打开群聊/好友界面", v-> TroopOpen.OpenDialog()));
        mLL.addView(FormItem.AddListItem(mAct, "屏蔽净化", v -> FlushToCleanSystem()));
        mLL.addView(FormItem.AddListItem(mAct, "QQ辅助", v -> QQHelper()));
        mLL.addView(FormItem.AddListItem(mAct, "聊天辅助", v -> ChatHelper()));
        mLL.addView(FormItem.AddListItem(mAct, "群管相关", v -> TroopManagerHelper()));
        mLL.addView(FormItem.AddListItem(mAct, "自动任务", v -> AutoTask()));
        mLL.addView(FormItem.AddListItem(mAct, "娱乐功能", v -> JoyHelper()));
        mLL.addView(FormItem.AddListItem(mAct, "加入群/QQ频道/TG群组", v -> {
            new AlertDialog.Builder(mAct,3)
                    .setTitle("选择需要加入的群聊")
                    .setItems(new String[]{"请注意,群聊是交流群,不是官方群,不保证消息真实,请自己判断" ,
                            "神兽园", "隐居地","频道-豆市场","TG群组"}, (dialog, which) -> {
                        if(which==1)QQTools.OpenTroopCard("501000603");
                        if(which==2)QQTools.OpenTroopCard("208754811");
                        if(which==3)QQTools.OpenInQQ("https://qun.qq.com/qqweb/qunpro/share?_wv=3&_wwv=128&inviteCode=1zFfRH&from=246610&biz=ka");
                        if(which==4)QQTools.OpenLink("https://t.me/+IdGdMAc-0OBmMjY1");

                    }).show();
        }));
        mLL.addView(FormItem.AddListItem(mAct, "缓存清理", v -> QQCleaner.ShowDialog(mAct)));
        mLL.addView(FormItem.AddListItem(mAct, "高级功能", v -> AdvHelper()));
        mLL.addView(FormItem.AddListItem(mAct, "调试功能", v -> Menu_DebugUtils.ShowDebugDialog()));


        MPositionDialog.CreateDownDialogMeasure(sc);
    }
    private static void AdvHelper(){
        Activity mAct = Utils.GetThreadActivity();
        ScrollView sc = new ScrollView(mAct);

        LinearLayout mLL = new LinearLayout(mAct);
        sc.addView(mLL);

        mLL.setOrientation(LinearLayout.VERTICAL);
        mLL.setBackgroundColor(Color.WHITE);


        mLL.addView(FormItem.AddListItem(mAct, "修改QQDpi", v-> Hook_Dpi_Change.ShowDpiDialog(mAct)));
        if(UserStatus.CheckIsDonator())
        {
            mLL.addView(FormItem.AddCheckItem(mAct, "便捷群发",(a,b)-> Put_Boolean("便捷群发",b),Get_Boolean("便捷群发",false)));
        }
        View v = FormItem.AddMultiItem(mAct, "辅助悬浮窗","点击修改大小",(a,b)-> Put_Boolean("悬浮窗Ex",b),Get_Boolean("悬浮窗Ex",false));
        v.setOnClickListener(vxx-> Init.ShowSizeChangeDialog());
        mLL.addView(v);

        if(UserStatus.CheckIsDonator())
        {
            mLL.addView(FormItem.AddListItem(mAct, "用户信息导出", va -> OutputTroopOrFriendList.StartDialog(va.getContext())));
            mLL.addView(FormItem.AddCheckItem(mAct, "一笔画红包助手",(a,b)-> Put_Boolean("一笔画红包助手",b),Get_Boolean("一笔画红包助手",false)));

            if(Hook_For_VoiceRedbag.IsAvaliable)
            {
                mLL.addView(FormItem.AddCheckItem(mAct, "领语音红包不发语音",(a,b)-> Put_Boolean("语音红包不发语音",b),Get_Boolean("语音红包不发语音",false)));
                mLL.addView(FormItem.AddCheckItem(mAct, "使用TTS辅助领取语音红包",(a,b)-> Put_Boolean("TTS辅助红包",b),Get_Boolean("TTS辅助红包",false)));
            }
            mLL.addView(FormItem.AddListItem(mAct, "自动抢红包设置",vx-> AutoRedPackSetting.ShowSet()));
        }






        MPositionDialog.CreateDownDialogMeasure(sc);
    }
    private static void JoyHelper(){
        Activity mAct = Utils.GetThreadActivity();
        ScrollView sc = new ScrollView(mAct);

        LinearLayout mLL = new LinearLayout(mAct);
        sc.addView(mLL);

        mLL.setOrientation(LinearLayout.VERTICAL);
        mLL.setBackgroundColor(Color.WHITE);


        mLL.addView(FormItem.AddCheckItem(mAct, "专属红包可发自己", (a,b)-> Put_Boolean("专属发自己",b),Get_Boolean("专属发自己",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "解除群名片输入表情限制",  (a,b)-> Put_Boolean("解除表情限制",b),Get_Boolean("解除表情限制",false)));
        if(UserStatus.CheckIsDonator())
        {
            mLL.addView(FormItem.AddCheckItem(mAct, "生僻字红包显示答案", (a,b)-> Put_Boolean("生僻字",b),Get_Boolean("生僻字",false)));
        }
        mLL.addView(FormItem.AddCheckItem(mAct, "贴图放大", (a,b)-> Put_Boolean("贴图放大",b),Get_Boolean("贴图放大",false)));
        mLL.addView(FormItem.AddListItem(mAct, "修改设备型号", v-> Hook_Change_Dev_Info.StartDialog(mAct)));
        mLL.addView(FormItem.AddCheckItem(mAct, "拍一拍连拍", (a,b)-> Put_Boolean("拍一拍连拍",b),Get_Boolean("拍一拍连拍",false)));
        mLL.addView(FormItem.AddListItem(mAct, "文本自定义", v-> Hook_Change_Widget_ShowText.ShowSet()));

        mLL.addView(FormItem.AddCheckItem(mAct, "记录消息条数", (a,b)-> Put_Boolean("记录消息条数",b),Get_Boolean("记录消息条数",false)));
        mLL.addView(FormItem.AddListItem(mAct,"设置消息小尾巴",v-> Handler_Message_tail.ShowSet(mAct)));
        mLL.addView(FormItem.AddListItem(mAct,"设置单字替换",v-> Handler_Message_split.ShowSet(mAct)));

        MPositionDialog.CreateDownDialogMeasure(sc);
    }
    private static void AutoTask(){
        Activity mAct = Utils.GetThreadActivity();
        ScrollView sc = new ScrollView(mAct);

        LinearLayout mLL = new LinearLayout(mAct);
        sc.addView(mLL);

        mLL.setOrientation(LinearLayout.VERTICAL);
        mLL.setBackgroundColor(Color.WHITE);

        mLL.addView(FormItem.AddCheckItem(mAct, "自动打卡", (a,b)->Put_Boolean("自动打卡",b),Get_Boolean("自动打卡",false)));
        if(UserStatus.CheckIsDonator())
        {
            mLL.addView(FormItem.AddCheckItem(mAct, "定时发送消息", (a,b)-> Put_Boolean("自动消息",b),Get_Boolean("自动消息",false)));
            mLL.addView(FormItem.AddListItem(mAct,"查看所有定时消息",v-> AutoMessage.ShowAllAutoMessageSetList(mAct)));
            mLL.addView(FormItem.AddListItem(mAct,"消息自动转发",v-> AutoResendMessage.StartSet()));
        }

        MPositionDialog.CreateDownDialogMeasure(sc);
    }
    private static void TroopManagerHelper(){
        Activity mAct = Utils.GetThreadActivity();
        ScrollView sc = new ScrollView(mAct);

        LinearLayout mLL = new LinearLayout(mAct);
        sc.addView(mLL);

        mLL.setOrientation(LinearLayout.VERTICAL);
        mLL.setBackgroundColor(Color.WHITE);

        mLL.addView(FormItem.AddCheckItem(mAct, "简易辅助群管",(a,b)-> Put_Boolean("辅助群管",b),Get_Boolean("辅助群管",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "群管便捷菜单", (a,b)-> Put_Boolean("便捷菜单",b),Get_Boolean("便捷菜单",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "启用群黑白名单", (a,b)-> Put_Boolean("群黑白名单",b),Get_Boolean("群黑白名单",false)));

        if(UserStatus.CheckIsDonator())
        {
            mLL.addView(FormItem.AddCheckItem(mAct, "群管助手", (a,b)-> Put_Boolean("群管助手",b),Get_Boolean("群管助手",false)));
            mLL.addView(FormItem.AddListItem(mAct, "设置警告组", v-> TroopManager_Set_Alarm_Group.ShowAlarmSetDialogGroup()));
        }


        MPositionDialog.CreateDownDialogMeasure(sc);
    }
    private static void ChatHelper(){
        Activity mAct = Utils.GetThreadActivity();
        ScrollView sc = new ScrollView(mAct);

        LinearLayout mLL = new LinearLayout(mAct);
        sc.addView(mLL);

        mLL.setOrientation(LinearLayout.VERTICAL);
        mLL.setBackgroundColor(Color.WHITE);


        mLL.addView(FormItem.AddCheckItem(mAct, "闪照显示为普通图片",  (a,b)-> Put_Boolean("闪照破解",b),Get_Boolean("闪照破解",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "表情显示为普通样式", (a,b)-> Put_Boolean("表情转换",b),Get_Boolean("表情转换",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "带图回复消息", (a,b)-> Put_Boolean("回复带图",b),Get_Boolean("回复带图",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "开启图文消息模式", (a,b)-> Put_Boolean("图文消息模式",b),Get_Boolean("图文消息模式",false)));
        mLL.addView(FormItem.AddListItem(mAct, "艾特显示修改", v-> Hook_Change_At_Someone.StartDialog()));
        mLL.addView(FormItem.AddCheckItem(mAct, "防止消息撤回",(a,b)-> Put_Boolean("防撤回",b),Get_Boolean("防撤回",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "撤回时保留自己的消息", (a,b)-> Put_Boolean("保留撤回",b),Get_Boolean("保留撤回",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "自定义骰子猜拳", (a,b)-> Put_Boolean("自定义骰子猜拳",b),Get_Boolean("自定义骰子猜拳",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "XMl_JSON消息复制", (a,b)-> Put_Boolean("卡片消息复制",b),Get_Boolean("卡片消息复制",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "XMl_JSON消息长按发送", (a,b)-> Put_Boolean("卡片消息发送",b),Get_Boolean("卡片消息发送",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "消息复读+1", (a,b)-> Put_Boolean("消息复读",b),Get_Boolean("消息复读",false)));
        mLL.addView(FormItem.AddListItem(mAct, "复读按钮样式修改", v-> Handler_Repeat_Icon_Set.ShowSettingDialog(mAct)));
        mLL.addView(FormItem.AddCheckItem(mAct, "语音转发",(a,b)-> Put_Boolean("转发语音",b),Get_Boolean("转发语音",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "语音保存发送", (a,b)-> Put_Boolean("语音保存发送",b),Get_Boolean("语音保存发送",false)));
        mLL.addView(FormItem.AddListItem(mAct, "语音显示和时长修改", v-> Handler_Avoid_Some_Message.ShowChangeVoiceDialog(mAct)));
        mLL.addView(FormItem.AddCheckItem(mAct, "在消息下显示艾特对象",(a,b)-> Put_Boolean("显示艾特",b),Get_Boolean("显示艾特",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "在消息下显示发送者和时间", (a,b)-> Put_Boolean("显示时间",b),Get_Boolean("显示时间",false)));
        //mLL.addView(FormItem.AddCheckItem(mAct, "群设置添加禁言查看入口", (a,b)-> BaseConfig.SetBool("显示禁言入口",b),BaseConfig.GetBoolean("显示禁言入口",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "base.apk上传重命名", (a,b)-> Put_Boolean("上传重命名",b),Get_Boolean("上传重命名",true)));
        mLL.addView(FormItem.AddCheckItem(mAct, "显示禁言的管理", (a,b)-> Put_Boolean("显示禁言",b),Get_Boolean("显示禁言",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "强制显示拍一拍账号",(a,b)-> Put_Boolean("显示拍一拍",b),Get_Boolean("显示拍一拍",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "回复去除自动艾特",(a,b)-> Put_Boolean("去除艾特",b),Get_Boolean("去除艾特",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "发送大表情转为发送小表情",(a,b)-> Put_Boolean("发送小表情",b),Get_Boolean("发送小表情",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "替换状态消息发送者为10000",(a,b)-> Put_Boolean("替换状态",b),Get_Boolean("替换状态",false)));


        MPositionDialog.CreateDownDialogMeasure(sc);
    }
    private static void QQHelper() {
        Activity mAct = Utils.GetThreadActivity();
        ScrollView sc = new ScrollView(mAct);

        LinearLayout mLL = new LinearLayout(mAct);
        sc.addView(mLL);

        mLL.setOrientation(LinearLayout.VERTICAL);
        mLL.setBackgroundColor(Color.WHITE);
        mLL.addView(FormItem.AddListItem(mAct,"好友删除监测",v-> FriendDeleteInfoShow.ShowSet(mAct)));
        mLL.addView(FormItem.AddCheckItem(mAct, "消息列表显示完整消息数量", (a,b)-> Put_Boolean("显示消息数量",b),Get_Boolean("显示消息数量",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "解除QQ对部分风险网址的限制",  (a,b)->Put_Boolean("解除网址限制",b),Get_Boolean("解除网址限制",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "自动跳过小程序广告", (a,b)-> Put_Boolean("跳过广告",b),Get_Boolean("跳过广告",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "一键20赞", (a,b)-> Put_Boolean("一键20赞",b),Get_Boolean("一键20赞",false)));

        View RedictDownload = FormItem.AddMultiItem(mAct, "下载文件重定向", "点击可以设置路径", (buttonView, isChecked) -> {
            if (isChecked) {
                Put_Boolean("下载重定向", true);
                if (TextUtils.isEmpty(MConfig.Get_String("Main","MainSet","DownloadRedirect"))) {
                    MConfig.Put_String("Main","MainSet","DownloadRedirect", Environment.getExternalStorageDirectory()+"/Download/MobileQQ/");
                }
            } else {
                Put_Boolean("下载重定向", false);
            }
        }, Get_Boolean("下载重定向", false));


        RedictDownload.setOnClickListener(v -> Hook_Redirect_Download_Path.ShowSettinng(mAct));
        mLL.addView(RedictDownload);
        mLL.addView(FormItem.AddCheckItem(mAct, "忽略部分通话状态", (a,b)-> Put_Boolean("忽略通话状态",b),Get_Boolean("忽略通话状态",false)));




        MPositionDialog.CreateDownDialogMeasure(sc);


    }
    private static void Put_Boolean(String SwitchName,boolean Value){
        MConfig.Put_Boolean("Main","MainSwitch",SwitchName,Value);
    }
    private static boolean Get_Boolean(String SwitchName,boolean DefValue){
        return MConfig.Get_Boolean("Main","MainSwitch",SwitchName,DefValue);
    }
    private static void FlushToCleanSystem() {
        Activity mAct = Utils.GetThreadActivity();
        ScrollView sc = new ScrollView(mAct);

        LinearLayout mLL = new LinearLayout(mAct);
        sc.addView(mLL);

        mLL.setOrientation(LinearLayout.VERTICAL);
        mLL.setBackgroundColor(Color.WHITE);

        mLL.addView(FormItem.AddCheckItem(mAct, "屏蔽下拉小程序", (a,b)-> Put_Boolean("屏蔽下拉小程序",b),Get_Boolean("屏蔽下拉小程序",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "隐藏群聊侧滑", (a,b)-> Put_Boolean("隐藏侧滑",b),Get_Boolean("隐藏侧滑",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "群聊侧滑替换为成员探查器", (a,b)-> Put_Boolean("替换侧滑",b),Get_Boolean("替换侧滑",false)));
        if(!Hook_For_Main_Slide_Side.NotSupport)
        {
            mLL.addView(FormItem.AddListItem(mAct, "主界面侧滑净化", v-> Hook_For_Main_Slide_Side.ShowSetDialog()));
        }
        mLL.addView(FormItem.AddCheckItem(mAct, "屏蔽部分卡屏消息", (a,b)-> Put_Boolean("屏蔽卡屏",b),Get_Boolean("屏蔽卡屏",true)));
        mLL.addView(FormItem.AddCheckItem(mAct, "阻止窗口抖动加载", (a,b)-> Put_Boolean("屏蔽窗口抖动",b),Get_Boolean("屏蔽窗口抖动",true)));
        mLL.addView(FormItem.AddCheckItem(mAct, "屏蔽DIY名片异常压缩包",  (a,b)-> Put_Boolean("屏蔽名片炸弹",b),Get_Boolean("屏蔽名片炸弹",true)));
        mLL.addView(FormItem.AddCheckItem(mAct, "屏蔽DIY名片显示",  (a,b)-> Put_Boolean("屏蔽DIY名片",b),Get_Boolean("屏蔽DIY名片",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "屏蔽卡屏大文件耗流量", (a,b)-> Put_Boolean("屏蔽耗流量卡片",b),Get_Boolean("屏蔽耗流量卡片",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "屏蔽涂鸦消息显示",  (a,b)-> Put_Boolean("屏蔽涂鸦",b),Get_Boolean("屏蔽涂鸦",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "在非QQ界面屏蔽QQ的提示",(a,b)-> Put_Boolean("屏蔽部分提示",b),Get_Boolean("屏蔽部分提示",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "全局屏蔽气泡",(a,b)-> Put_Boolean("全局屏蔽气泡",b),Get_Boolean("全局屏蔽气泡",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "全局屏蔽字体", (a,b)-> Put_Boolean("全局屏蔽字体",b),Get_Boolean("全局屏蔽字体",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "屏蔽秀图展示", (a,b)-> Put_Boolean("屏蔽秀图",b),Get_Boolean("屏蔽秀图",false)));
        mLL.addView(FormItem.AddListItem(mAct,"屏蔽艾特通知",v-> Hook_Disable_At_Notify.ShowAtSettings()));
        mLL.addView(FormItem.AddListItem(mAct,"长按消息菜单净化",v-> Hook_Clean_Msg_Long_Click_Common.ShowSet()));
        mLL.addView(FormItem.AddCheckItem(mAct, "屏蔽好友热播", (a,b)-> Put_Boolean("屏蔽热播",b),Get_Boolean("屏蔽热播",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "屏蔽更新提醒", (a,b)-> Put_Boolean("屏蔽更新提醒",b),Get_Boolean("屏蔽更新提醒",false)));
        mLL.addView(FormItem.AddCheckItem(mAct, "屏蔽聊天界面相机按钮", (a,b)-> Put_Boolean("屏蔽聊天相机",b),Get_Boolean("屏蔽聊天相机",false)));
        //mLL.addView(FormItem.AddListItem(mAct,"设置强制显示加号菜单",v-> Hook_For_JiaHao_Panel.StartShow(mAct)));
        mLL.addView(FormItem.AddListItem(mAct,"设置强制隐藏加号菜单",v-> Hook_For_JiaHao_Panel.StartShowHide(mAct)));

        MPositionDialog.CreateDownDialogMeasure(sc);
    }
}
