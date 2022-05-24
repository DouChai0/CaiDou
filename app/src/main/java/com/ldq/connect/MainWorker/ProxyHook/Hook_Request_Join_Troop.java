package com.ldq.connect.MainWorker.ProxyHook;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.JavaPlugin.JavaPlugin;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MainWorker.QQManagerHelper.TroopManager_Handler_WhiteBlack_Checker;

import java.lang.reflect.Method;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_Request_Join_Troop {
    public static void Start() throws Exception {
        Method methodHandleSystemMsg = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.app.message.SystemMessageProcessor"),
                "a",void.class,new Class[]{
                        int.class,int.class,MClass.loadClass("tencent.mobileim.structmsg.structmsg$StructMsg"),
                        int.class,
                        MClass.loadClass("com.tencent.mobileqq.systemmsg.MessageForSystemMsg"),
                        int.class,boolean[].class,
                }
                );
        XposedBridge.hookMethod(methodHandleSystemMsg, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                int MessageID = (int) param.args[3];
                //MLogCat.Print_Debug(""+MessageID);
                if(MessageID == 1)//Request Join Troop
                {
                    DecodeAndDispatchMessage(param.args[2]);
                }
            }
        });
    }
    static ConcurrentHashMap<Long,Object> CacheIDMap = new ConcurrentHashMap<>();
    private static void DecodeAndDispatchMessage(Object StructMsg)
    {
        try{
            //解析Uin和Id
            long Request_Uin = DecodePBLongField(MField.GetField(StructMsg,"req_uin"));
            long Request_ID = DecodePBLongField(MField.GetField(StructMsg,"msg_seq"));
            if(CacheIDMap.containsKey(Request_ID))return;
            CacheIDMap.put(Request_ID,StructMsg);

            //解析请求信息
            Object SystemMsg = MField.GetField(StructMsg,"msg");
            String source = DecodePBStringOrByteField(MField.GetField(SystemMsg,"msg_source"));
            String answer = DecodePBStringOrByteField(MField.GetField(SystemMsg,"msg_qna"));
            String TroopRequestText = DecodePBStringOrByteField(MField.GetField(SystemMsg,"msg_additional"));
            String TroopCode = DecodePBStringOrByteField(MField.GetField(SystemMsg,"group_code"));
            /*
            MLogCat.Print_Info("RequestJoinTroop","TroopUin:" + TroopCode
                    + "   UserUin:" + Request_Uin
                    + "   Source:" + source
                    + "   RequestAnswer:" + answer
                    + "   FullAnswer:" + TroopRequestText
            );

             */

            TroopManager_Handler_WhiteBlack_Checker.JoinInHandler(TroopCode,Request_Uin+"",1,StructMsg);

            JavaPlugin.Packer_JoinTroopRequest(TroopCode,Request_Uin+"",source,answer,TroopRequestText,StructMsg);

        }catch (Throwable e)
        {
            MLogCat.Print_Error("DecodeJoinTroopMsg",e);
        }

    }
    public static void Handler_AcceptRequest(Object StructMsg)
    {
        try{
            int msg_type = (int) DecodePBLongField(MField.GetField(StructMsg,"msg_type"));
            long msg_seq = DecodePBLongField(MField.GetField(StructMsg,"msg_seq"));
            long req_uin = DecodePBLongField(MField.GetField(StructMsg,"req_uin"));

            Object SystemMsg = MField.GetField(StructMsg,"msg");
            int sub_type = (int) DecodePBLongField(MField.GetField(SystemMsg,"sub_type"));
            int src_id = (int) DecodePBLongField(MField.GetField(SystemMsg,"src_id"));
            int sub_src_id = (int) DecodePBLongField(MField.GetField(SystemMsg,"sub_src_id"));
            int group_msg_type = (int) DecodePBLongField(MField.GetField(SystemMsg,"group_msg_type"));


            List ActionInfoList = DecodePBListField(MField.GetField(SystemMsg,"actions"));
            Object IMessageHandler = MMethod.CallMethod(MHookEnvironment.AppInterface,
                    MClass.loadClass("mqq.app.AppRuntime"),"getRuntimeService",
                    new Class[]{Class.class,String.class},
                    MClass.loadClass("com.tencent.mobileqq.msg.api.IMessageHandler"),"");
            Method m_sendGroupSystemMsgAction = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.msg.api.IMessageHandler"),
                    "sendGroupSystemMsgAction",void.class,new Class[]{
                            int.class,long.class,long.class,int.class,int.class,int.class,int.class,
                            MClass.loadClass("tencent.mobileim.structmsg.structmsg$SystemMsgActionInfo"),int.class
                    });
            Object action_info = MField.GetField(ActionInfoList.get(1),"action_info");

            m_sendGroupSystemMsgAction.invoke(IMessageHandler,
                    msg_type,msg_seq,req_uin,sub_type,src_id,sub_src_id,group_msg_type,
                    action_info,1
                    );

        }catch (Exception e)
        {
            MLogCat.Print_Error("Handler_Accept_Request0",e);
        }
    }
    public static void Handler_RefuseRequest(Object StructMsg,String Reason,boolean isBlack)
    {
        try{
            int msg_type = (int) DecodePBLongField(MField.GetField(StructMsg,"msg_type"));
            long msg_seq = DecodePBLongField(MField.GetField(StructMsg,"msg_seq"));
            long req_uin = DecodePBLongField(MField.GetField(StructMsg,"req_uin"));

            Object SystemMsg = MField.GetField(StructMsg,"msg");
            int sub_type = (int) DecodePBLongField(MField.GetField(SystemMsg,"sub_type"));
            int src_id = (int) DecodePBLongField(MField.GetField(SystemMsg,"src_id"));
            int sub_src_id = (int) DecodePBLongField(MField.GetField(SystemMsg,"sub_src_id"));
            int group_msg_type = (int) DecodePBLongField(MField.GetField(SystemMsg,"group_msg_type"));


            List ActionInfoList = DecodePBListField(MField.GetField(SystemMsg,"actions"));
            Object IMessageHandler = MMethod.CallMethod(MHookEnvironment.AppInterface,
                    MClass.loadClass("mqq.app.AppRuntime"),"getRuntimeService",
                    new Class[]{Class.class,String.class},
                    MClass.loadClass("com.tencent.mobileqq.msg.api.IMessageHandler"),"");
            Method m_sendGroupSystemMsgAction = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.msg.api.IMessageHandler"),
                    "sendGroupSystemMsgAction",void.class,new Class[]{
                            int.class,long.class,long.class,int.class,int.class,int.class,int.class,
                            MClass.loadClass("tencent.mobileim.structmsg.structmsg$SystemMsgActionInfo"),int.class
                    });
            Object action_info = MField.GetField(ActionInfoList.get(0),"action_info");
            MMethod.CallMethod(MField.GetField(action_info,"blacklist"),"set",void.class,new Class[]{
                    boolean.class
            },isBlack);

            MMethod.CallMethod(MField.GetField(action_info,"msg"),"set",void.class,new Class[]{
                    String.class
            },Reason);

            m_sendGroupSystemMsgAction.invoke(IMessageHandler,
                    msg_type,msg_seq,req_uin,sub_type,src_id,sub_src_id,group_msg_type,
                    action_info,0
            );

        }catch (Exception e)
        {
            MLogCat.Print_Error("Handler_Accept_Request0",e);
        }
    }
    private static List DecodePBListField(Object Field)
    {
        try{
            for(Method m : Field.getClass().getDeclaredMethods())
            {
                if(m.getName().equals("get") && m.getParameterCount()==0)
                {

                    Object Value = m.invoke(Field);
                    return (List) Value;
                }
            }
            return null;
        }catch (Exception e)
        {
            MLogCat.Print_Error("Tag1",e);
            return null;
        }
    }
    public static long DecodePBLongField(Object Field)
    {
        try{
            for(Method m : Field.getClass().getDeclaredMethods())
            {
                if(m.getName().equals("get"))
                {
                    Object Value = m.invoke(Field);
                    if(Value instanceof Long)return (long) Value;
                    if(Value instanceof Integer)return (Integer)Value;

                }
            }
            return 0;
        }catch (Exception e)
        {
            return 0;
        }
    }
    private static String DecodePBStringOrByteField(Object Field)
    {
        try{
            for(Method m : Field.getClass().getDeclaredMethods())
            {
                if(m.getName().equals("get"))
                {
                    Object Value = m.invoke(Field);

                    if(Value.getClass().getName().contains("ByteStringMicro"))
                    {
                        byte[] b = MField.GetField(Value,"bytes",byte[].class);
                        return new String(b);
                    }else
                    {
                        return Value+"";
                    }

                }
            }
            return "";
        }catch (Exception e)
        {
            return "";
        }
    }
}
