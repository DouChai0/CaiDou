package com.ldq.connect.MainWorker.BaseWorker.EnvHook;

import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MTaskThread;
import com.ldq.Utils.Utils;
import com.ldq.connect.FloatWindow.WindowHandler;
import com.ldq.connect.JavaPlugin.JavaPlugin;
import com.ldq.connect.MainWorker.BaseWorker.SettingInjectHook.Hook_For_Guild_Toolbar_Inject;
import com.ldq.connect.MainWorker.BaseWorker.SettingInjectHook.Hook_For_Inject_QQ_Settings_Inject;
import com.ldq.connect.MainWorker.BaseWorker.SettingInjectHook.Hook_For_MainAddButton_Inject;
import com.ldq.connect.MainWorker.BaseWorker.SettingInjectHook.Hook_For_Troop_Set_Inject;
import com.ldq.connect.MainWorker.LittileHook.Hook_Allow_Scan_Pic_QRCode;
import com.ldq.connect.MainWorker.LittileHook.Hook_Auto_Close_MiniApp_Ads;
import com.ldq.connect.MainWorker.LittileHook.Hook_Change_At_Someone;
import com.ldq.connect.MainWorker.LittileHook.Hook_Change_Dev_Info;
import com.ldq.connect.MainWorker.LittileHook.Hook_Convert_BigEmo_To_Little;
import com.ldq.connect.MainWorker.LittileHook.Hook_Convert_FlashPic_To_Common;
import com.ldq.connect.MainWorker.LittileHook.Hook_Disable_At_Notify;
import com.ldq.connect.MainWorker.LittileHook.Hook_Disable_EffectPic_Show;
import com.ldq.connect.MainWorker.LittileHook.Hook_Disable_QQ_Auto_Update;
import com.ldq.connect.MainWorker.LittileHook.Hook_Disable_Reply_At;
import com.ldq.connect.MainWorker.LittileHook.Hook_Disable_Resend_Msg_Limit;
import com.ldq.connect.MainWorker.LittileHook.Hook_Dpi_Change;
import com.ldq.connect.MainWorker.LittileHook.Hook_For_Paiyipai;
import com.ldq.connect.MainWorker.LittileHook.Hook_For_Prevent_TroopFile_Delete;
import com.ldq.connect.MainWorker.LittileHook.Hook_For_Set_Random_Dice;
import com.ldq.connect.MainWorker.LittileHook.Hook_Hide_Diy_Or_Protect_Zip;
import com.ldq.connect.MainWorker.LittileHook.Hook_Ignore_Phone_Call_State;
import com.ldq.connect.MainWorker.LittileHook.Hook_OneClick_20Like;
import com.ldq.connect.MainWorker.LittileHook.Hook_Redirect_Download_Path;
import com.ldq.connect.MainWorker.LittileHook.Hook_Refuse_Download_Big_CardPic;
import com.ldq.connect.MainWorker.LittileHook.Hook_Remove_QZone_Ad;
import com.ldq.connect.MainWorker.LittileHook.Hook_Rename_BaseApk;
import com.ldq.connect.MainWorker.LittileHook.Hook_Show_Hide_Emo;
import com.ldq.connect.MainWorker.LittileHook.Hook_Unlock_Guild_Data;
import com.ldq.connect.MainWorker.LittileHook.Hook_Unlock_Troop_Name_Limit;
import com.ldq.connect.MainWorker.LittileHook.Hook_Unlock_Website_Limit;
import com.ldq.connect.MainWorker.LittileHook.Hook_Upload_Transparent_Pic;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Count_Input_Data;
import com.ldq.connect.MainWorker.ProxyHook.Hook_Add_Message_Local;
import com.ldq.connect.MainWorker.ProxyHook.Hook_For_All_Msg_Proxy;
import com.ldq.connect.MainWorker.ProxyHook.Hook_For_Guild_Msg_Proxy;
import com.ldq.connect.MainWorker.ProxyHook.Hook_For_Message_Item_Build_Common;
import com.ldq.connect.MainWorker.ProxyHook.Hook_For_Message_Item_Builder_Guild;
import com.ldq.connect.MainWorker.ProxyHook.Hook_For_PbSend_Message;
import com.ldq.connect.MainWorker.ProxyHook.Hook_For_Troop_Event;
import com.ldq.connect.MainWorker.ProxyHook.Hook_For_Troop_Event_Push;
import com.ldq.connect.MainWorker.ProxyHook.Hook_For_Troop_Msg_Proxy;
import com.ldq.connect.MainWorker.ProxyHook.Hook_For_WebView_Load;
import com.ldq.connect.MainWorker.ProxyHook.Hook_Prevent_Revoke_Msg_Common;
import com.ldq.connect.MainWorker.ProxyHook.Hook_Prevent_Revoke_Msg_Guild;
import com.ldq.connect.MainWorker.ProxyHook.Hook_Request_Join_Troop;
import com.ldq.connect.MainWorker.QQRedBagHelper.Hook_For_VoiceRedbag;
import com.ldq.connect.MainWorker.QQRedBagHelper.Hook_RedPacket_DoParse;
import com.ldq.connect.MainWorker.QQRedBagHelper.Hook_Redbag_For_Self;
import com.ldq.connect.MainWorker.QQRedBagHelper.VoiceUploader;
import com.ldq.connect.MainWorker.SystemHook.Hook_For_Cache_File_Observe;
import com.ldq.connect.MainWorker.WidgetHandler.Handler_Avatar_Long_Click_Common;
import com.ldq.connect.MainWorker.WidgetHandler.Handler_For_Repeat_Msg_Common;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Avatar_Long_Click_Guild;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Change_Widget_ShowText;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Clean_Msg_Long_Click_Common;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Disable_Friend_PopEmo;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Disable_Other_Font;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Disable_Raw_Repeat_Icon;
import com.ldq.connect.MainWorker.WidgetHook.Hook_For_Common_Msg_Long_Click;
import com.ldq.connect.MainWorker.WidgetHook.Hook_For_Guild_Msg_Long_Click;
import com.ldq.connect.MainWorker.WidgetHook.Hook_For_JiaHao_Panel;
import com.ldq.connect.MainWorker.WidgetHook.Hook_For_Main_Slide_Side;
import com.ldq.connect.MainWorker.WidgetHook.Hook_For_Troop_Slide_App;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Hide_Main_App_Entry;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Hide_QQ_Top_Toast;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Photo_Pre_Send;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Show_Accurate_Unmute_Time;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Show_Full_Msg_Count;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Unlock_Input_Length_Limit;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Voice_Record_Item_Builder;

public class StartBaseHook {
    //Hook集体初始化,不规范但是没法改了
    public static void MainHook()
    {
        try{

            Hook_Dpi_Change.Start();



            Hook_For_Inject_QQ_Settings_Inject.StartInject();

            Hook_For_Guild_Toolbar_Inject.Start();

            Hook_Change_Dev_Info.Start();




            Hook_For_MainAddButton_Inject.Start();

            BaseApplicationHook.start();

            Hook_For_Troop_Set_Inject.Start();

            Hook_For_Message_Item_Build_Common.Start();

            JavaPlugin.mTask.init();

            BasePieHook.Start();

            Hook_Convert_FlashPic_To_Common.Start();

            Handler_For_Repeat_Msg_Common.InitIcon();

            Hook_Hide_Main_App_Entry.Start();




            Handler_Avatar_Long_Click_Common.InitHook();

            Hook_For_Troop_Msg_Proxy.Start();

            MTaskThread.InitClockThread();

            Hook_Redirect_Download_Path.Start();

            Hook_Prevent_Revoke_Msg_Common.Start();

            Hook_Allow_Scan_Pic_QRCode.Start();

            Hook_Upload_Transparent_Pic.Start();

            Hook_Hide_Diy_Or_Protect_Zip.Start();

            Hook_Refuse_Download_Big_CardPic.Start();

            Hook_Add_Message_Local.Start();

            Hook_Show_Full_Msg_Count.Start();

            Hook_Voice_Record_Item_Builder.Start();

            Hook_Hide_QQ_Top_Toast.Start();

            Hook_For_All_Msg_Proxy.Start();

            Hook_For_Troop_Event.Start();

            Hook_For_Common_Msg_Long_Click.Start();

            Hook_Show_Accurate_Unmute_Time.Start();

            Hook_Rename_BaseApk.Start();

            Hook_Hide_Diy_Or_Protect_Zip.Start();

            Hook_Auto_Close_MiniApp_Ads.Start();

            Hook_For_Paiyipai.Start();

            Hook_Unlock_Troop_Name_Limit.Start();

            Hook_Photo_Pre_Send.Start();

            WindowHandler.StartSwitchHook();

            Hook_Disable_Raw_Repeat_Icon.Start();

            Hook_Redbag_For_Self.Start();

            Hook_Disable_Reply_At.Start();

            Hook_Show_Hide_Emo.Start();

            Hook_OneClick_20Like.Start();

            //TitleHook.Start();



            Hook_Disable_EffectPic_Show.Start();

            Hook_Disable_Other_Font.Start();

            Hook_For_Troop_Slide_App.Start();

            Hook_Disable_Resend_Msg_Limit.Start();

            Hook_For_PbSend_Message.Start();

            Hook_Change_At_Someone.Start();

            Hook_Disable_At_Notify.Start();

            Hook_Redbag_For_Self.Start();

            Hook_Convert_BigEmo_To_Little.Start();

            Hook_For_Main_Slide_Side.HookForGetSetData();

            Hook_Unlock_Input_Length_Limit.Start();

            Hook_Ignore_Phone_Call_State.Start();

            Hook_For_Set_Random_Dice.Start();

            Hook_Request_Join_Troop.Start();

            Hook_Disable_Friend_PopEmo.Start();

            Hook_For_Troop_Event_Push.Start();

            Hook_Clean_Msg_Long_Click_Common.Start();

            Hook_For_Cache_File_Observe.Start();

            Hook_For_VoiceRedbag.Start();

            Hook_Change_Widget_ShowText.Start();

            Hook_Remove_QZone_Ad.Start();

            Handler_Count_Input_Data.Init();


            Hook_Prevent_Revoke_Msg_Guild.Start();

            Hook_For_Message_Item_Builder_Guild.Start();

            Hook_For_Guild_Msg_Proxy.Start();

            Hook_Avatar_Long_Click_Guild.Start();

            Hook_For_Guild_Msg_Long_Click.Start();

            Hook_Disable_QQ_Auto_Update.Start();

            Hook_RedPacket_DoParse.Start();

            VoiceUploader.TestHooker();


            Hook_For_Prevent_TroopFile_Delete.Start();

            //Hook_Virtual_Location.StartHook();

            Hook_For_JiaHao_Panel.Start();

            Hook_Unlock_Guild_Data.Start();


        }catch (Throwable th) {
            MLogCat.Print_Error("MainHook",th);
            Utils.ShowToast("Main Hook Error:\n"+th);
        }



    }
    //非主进程的时候加载的Hook项目
    public static void ExtraHook()
    {
        try{
            Hook_Dpi_Change.Start();

            Hook_Change_Dev_Info.Start();

            Hook_Redirect_Download_Path.Start();

            Hook_Auto_Close_MiniApp_Ads.Start();

            Hook_Redbag_For_Self.Start();

            Hook_Unlock_Website_Limit.Start();

            Hook_Ignore_Phone_Call_State.Start();

            Hook_Remove_QZone_Ad.Start();

            Hook_For_WebView_Load.Start();

            Hook_For_Prevent_TroopFile_Delete.Start();

            //Hook_For_Virtual_Location_Set.Start();

            //Hook_Virtual_Location.StartHook();

        }catch (Throwable th)
        {
            MLogCat.Print_Error("ExtraHook",th);
        }
    }
}
