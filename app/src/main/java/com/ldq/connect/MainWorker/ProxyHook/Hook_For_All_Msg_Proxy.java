package com.ldq.connect.MainWorker.ProxyHook;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.JavaPlugin.JavaPlugin;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MTool.AutoResendMessage;
import com.ldq.connect.MainWorker.QQRedBagHelper.Handler_HB_Opener;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_For_All_Msg_Proxy extends XC_MethodHook {
    public static void Start()
    {
        try{
            Method hookMethod = MMethod.FindMethod("com.tencent.imcore.message.BaseMessageManager","a",void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"),
                MClass.loadClass("com.tencent.mobileqq.persistence.EntityManager"),
                boolean.class,boolean.class,boolean.class,boolean.class,
                MClass.loadClass("com.tencent.imcore.message.BaseMessageManager$AddMessageContext")
        });
            XposedBridge.hookMethod(hookMethod,new Hook_For_All_Msg_Proxy());
        }catch (Throwable th)
        {
            MLogCat.Print_Error("MessageHandler",th);
        }
    }
    @Override
    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
        super.beforeHookedMethod(param);
        Object MessageRecord = param.args[0];
        try{


            int isGroup = MField.GetField(MessageRecord,"istroop",int.class);
            String MessggeName = MessageRecord.getClass().getSimpleName();
            if(MessggeName.equals("MessageForSystemMsg"))return;
            if (isGroup == 1){
                Handler_HB_Opener.NotifyMessageRecv(MessageRecord);
            }
            if(isGroup==0 || isGroup==1000)
            {
                JavaPlugin.TaskCount++;
                JavaPlugin.AllTaskCount++;
                JavaPlugin.mTask.PostTask(()-> JavaPlugin.BshOnMsg(MessageRecord));

                MHookEnvironment.mTask.PostTask(()-> AutoResendMessage.onMessage(MessageRecord));
            }
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("MessageHandlerHookDispatch",th);
        }
    }
}
