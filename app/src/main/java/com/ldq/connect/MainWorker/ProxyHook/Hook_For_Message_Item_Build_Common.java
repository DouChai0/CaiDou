package com.ldq.connect.MainWorker.ProxyHook;

import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.MainWorker.BaseWorker.EnvHook.BasePieHook;
import com.ldq.connect.MainWorker.LittileHook.Hook_Convert_FlashPic_To_Common;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Avoid_Some_Message;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Avoid_Stuck_Msg;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Hide_Font_And_Bubble;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Hide_Tuya;
import com.ldq.connect.MainWorker.WidgetHandler.Handler_Avatar_Long_Click_Common;
import com.ldq.connect.MainWorker.WidgetHandler.Handler_For_Repeat_Msg_Common;
import com.ldq.connect.MainWorker.WidgetHandler.Handler_Show_Forward_Source;
import com.ldq.connect.MainWorker.WidgetHook.Hook_Show_At_And_Time_Common;

import java.lang.reflect.Method;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_For_Message_Item_Build_Common extends XC_MethodHook {
    public static void Start(){
        try{
            Method HookMethod = MMethod.FindMethod("com.tencent.mobileqq.activity.aio.ChatAdapter1","getView", View.class,new Class[]{
                    int.class,
                    View.class,
                    ViewGroup.class
            });
            XposedBridge.hookMethod(HookMethod,new Hook_For_Message_Item_Build_Common());
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("ListAdapterHook", Log.getStackTraceString(th));
        }


    }
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        ViewGroup vg = (ViewGroup) param.args[2]; // 取出ViewGroup,用于获取Context
        List list = MField.GetField(param.thisObject, param.thisObject.getClass(),"a", List.class);
        if(list==null)return;
        Object MessageRecord = list.get((int) param.args[0]);
        try{
            Handler_Avoid_Stuck_Msg._caller_boom(param,MessageRecord,vg);
        }catch (Throwable th)
        {
            MLogCat.Print_Error("ViewHookDispatch",Log.getStackTraceString(th));
        }

    }

    @Override
    protected void afterHookedMethod(MethodHookParam param) throws Throwable {
        super.afterHookedMethod(param);
        try{
            Object mGetView = param.getResult();
            RelativeLayout mLayout;
            if(mGetView instanceof RelativeLayout)
            {
                mLayout = (RelativeLayout) mGetView;
            }
            else
            {
                return;
            }
            List MessageRecoreList = MField.GetField(param.thisObject,param.thisObject.getClass() ,"a", List.class);
            if(MessageRecoreList==null)return;
            Object ChatMsg = MessageRecoreList.get((int) param.args[0]);

            String ActivityName = mLayout.getContext().getClass().getName();

            ClearItem(mLayout);

            //FileObserve.ItemChatMsgChecker(ChatMsg);

            if(ActivityName.contains("MultiForwardActivity"))
            {
                Hook_Show_At_And_Time_Common._call(mLayout,ChatMsg);

                BasePieHook._caller_copy(mLayout,ChatMsg);

                Hook_Show_At_And_Time_Common._caller_time(mLayout,ChatMsg);

                Handler_For_Repeat_Msg_Common._caller(mLayout,ChatMsg);

                Handler_Show_Forward_Source._caller(mLayout,ChatMsg);

                Handler_Avatar_Long_Click_Common._caller_multimsg(mLayout,ChatMsg);
            }
            else
            {
                Hook_Show_At_And_Time_Common._call(mLayout,ChatMsg);

                Hook_Convert_FlashPic_To_Common._caller(mLayout,ChatMsg);

                BasePieHook._caller_copy(mLayout,ChatMsg);

                Handler_For_Repeat_Msg_Common._caller(mLayout,ChatMsg);

                Handler_Avatar_Long_Click_Common._caller(mLayout,ChatMsg);

                Handler_Hide_Tuya._call_layout(mLayout,ChatMsg);

                Hook_Show_At_And_Time_Common._caller_time(mLayout,ChatMsg);

                Handler_Avoid_Some_Message._Handle_PIC(ChatMsg);

                Handler_Hide_Font_And_Bubble._caller(ChatMsg);

                //MLogCat.Print_Debug(ChatMsg.toString());
            }
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("ViewHookDispatch",Log.getStackTraceString(th));
        }
    }
    public static void ClearItem(Object ItemObj)
    {
        try {
            TextView t = (TextView) FieldTable.BaseChatItemLayout_EdTailView().get(ItemObj);
            if(t!=null)
            {
                String Text = t.getText().toString();
                if(Text.endsWith(" "))
                {
                    MMethod.CallMethod(ItemObj,"setTailMessage",void.class,new Class[]{boolean.class,CharSequence.class, MClass.loadClass("android.view.View$OnClickListener")},true,"",null);
                    MMethod.CallMethod(ItemObj,"setTailMessage",void.class,new Class[]{boolean.class,CharSequence.class, MClass.loadClass("android.view.View$OnClickListener")},false,"",null);
                }
            }
        } catch (Throwable e) {
            MLogCat.Print_Error("ClearItem0",e);
        }
    }
}
