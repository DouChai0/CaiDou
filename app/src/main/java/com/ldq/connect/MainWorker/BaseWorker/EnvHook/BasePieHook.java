package com.ldq.connect.MainWorker.BaseWorker.EnvHook;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.text.Editable;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.ldq.HookHelper.DexTable.ClassTable;
import com.ldq.HookHelper.DexTable.FieldTable;
import com.ldq.HookHelper.DexTable.MethodTable;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Count_Input_Data;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_Fast_Menu;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.QQMessage_Builder;
import com.ldq.connect.QQUtils.TroopManager;

import java.lang.reflect.Method;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class BasePieHook {
    static EditText ed = null;
    static Object NowChatPie = null;
    public static void Start()
    {
        try {
            XposedBridge.hookMethod(MethodTable.BaseChatPie_InitData(), new XC_MethodHook(99) {
                @Override
                protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                    super.afterHookedMethod(param);

                    try{
                        //SpringAct.StartShow();
                        //MLogCat.Print_Debug("Cat"+Log.getStackTraceString(new Throwable()));
                        //获取界面类
                        Object chatPie = param.thisObject;

                        MHookEnvironment.AppInterface = FieldTable.BaseChatPie_QQAppinterFace().get(param.thisObject);
                        MHookEnvironment.CurrentSession = FieldTable.BaseChatPie_SessionInfo().get(param.thisObject);

                        //new Handler(Looper.getMainLooper()).postDelayed(()->DebugUtils.PrintAllField2(MHookEnvironment.CurrentSession),1000);


                        NowChatPie = chatPie;
                        //获取QQ一个内置的取出界面ViewGroup的方法,调用后返回界面的ViewGroup
                        //MLogCat.Print_Debug(Log.getStackTraceString(new Throwable()));

                        Method m = MethodTable.BaseChatPie_GetAIORootViewGroup();

                        ViewGroup viewGroup= aGroup;
                        if(m!=null)viewGroup = (ViewGroup) m.invoke(param.thisObject);



                        //取得基础的QQAppInterface和SessionInfo,用于后面手动发送消息时使用


                        //如果未取出则ViewGroup则直接返回
                        if (viewGroup == null) return;
                        Context ctx = viewGroup.getContext();

                        //加载群便捷菜单
                        Handler_Fast_Menu._caller((Activity) ctx,viewGroup);
                        //取出QQ发送按钮的ID值
                        int fun_btn = ctx.getResources().getIdentifier("fun_btn", "id", ctx.getPackageName());
                        //获得QQ的发送按钮对象
                        View sendBtn = viewGroup.findViewById(fun_btn);

                        //获取输入框对象
                        ed = viewGroup.findViewById(ctx.getResources().getIdentifier("input", "id", ctx.getPackageName()));

                        if(MConfig.Get_Boolean("Main","MainSwitch","记录消息条数",false)){
                            ed.setHint(Handler_Count_Input_Data.Get_Count());
                        }

                        //重写长按事件


                        if(MConfig.Get_Boolean("Main","MainSwitch","卡片消息发送",false))
                        {
                            ViewGroup finalViewGroup = viewGroup;
                            sendBtn.setOnLongClickListener(new View.OnLongClickListener() {
                                @Override
                                public boolean onLongClick(View view) {
                                    try
                                    {
                                        Context ctx = view.getContext();
                                        //取出QQ的输入框对象并取出其中的内容
                                        EditText input = finalViewGroup.findViewById(ctx.getResources().getIdentifier("input", "id", ctx.getPackageName()));

                                        //转换为大表情,如果装换成功则不再进行卡片消息替换
                                        if(QQMessage.ConvertAndSendAntMsg(input.getText().toString()))
                                        {
                                            input.setText("");
                                            return true;
                                        }

                                        String inputText = input.getText().toString();
                                        //空则返回
                                        if(inputText.isEmpty()) return false;
                                        //通过判断文本头部来确定是json代码还是xml代码,是的话就转换为卡片发送并清空输入框
                                        //不是的话则调用默认的处理方法
                                        if(inputText.startsWith("<?xml")) {
                                            input.setText("");//清空
                                            //创建消息对象
                                            QQMessage.Message_Send_Xml(MHookEnvironment.CurrentSession,QQMessage_Builder.Build_AbsStructMsg(inputText));
                                            return true;
                                        }
                                        if(inputText.startsWith("{"))
                                        {
                                            input.setText("");
                                            //创建消息对象
                                            QQMessage.Message_Send_ArkApp(MHookEnvironment.CurrentSession,QQMessage_Builder.Build_ArkAppMsg(inputText));
                                            return true;
                                        }
                                    }
                                    catch (Throwable e)
                                    {
                                        MLogCat.Print_Error("SendBtnLongClick",Log.getStackTraceString(e));
                                        //MLogcat.e("LongClickError",Log.getStackTraceString(e));
                                    }
                                    return false;
                                }
                            });
                        }

                    }
                    catch(Throwable e)
                    {
                        Toast.makeText(MHookEnvironment.MAppContext, "小菜豆,基础对象初始化失败:\n"+Log.getStackTraceString(e),Toast.LENGTH_LONG).show();
                    }

                }
            });

            //获取BaseChatPie创建时取得的界面ViewGroup创建信息
            XposedHelpers.findAndHookConstructor(MClass.loadClass("com.tencent.mobileqq.activity.aio.core.BaseChatPie"),
                    MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                    ViewGroup.class,
                    MClass.loadClass("com.tencent.mobileqq.app.BaseActivity"),
                    Context.class, new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            aGroup = (ViewGroup) param.args[1];
                        }
                        protected void afterHookedMethod(MethodHookParam param) throws Throwable {

                        }
                    }
            );
        } catch (Exception e) {
            MLogCat.Print_Error("BaseChatPie", Log.getStackTraceString(e));
        }
    }
    public static void Add_At_Text(String TroopUin,String Uin){
        //只有在群聊的时候才会添加艾特信息,私聊时不进行添加
        if(NowChatPie.getClass().getName().equals("com.tencent.mobileqq.activity.aio.core.TroopChatPie")){
            try {
                MMethod.CallMethod(NowChatPie,"a",void.class,new Class[]{String.class,String.class,boolean.class,int.class},
                        Uin, TroopManager.GetMemberName(TroopUin,Uin),false,1
                        );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }
    static ViewGroup aGroup;

    //取得QQ的撤回帮助类,以帮助在撤回文件消息的时候同时删除群文件
    public static Object GetRevokeHelper()
    {
        try{
            Object obj = MClass.CallConstrutor(ClassTable.RevokeHelper(),new Class[]{
                    ClassTable.BaseChatPie()
            },NowChatPie);
            return obj;
        }catch (Exception ex)
        {
            return null;
        }

    }
    @SuppressLint("ResourceType")
    public static void _caller_copy(RelativeLayout RL, Object ChatMsg)
    {
        try{
            if(RL==null)return;
            String MessageRecordNameSimple = ChatMsg.getClass().getSimpleName();
            if(MConfig.Get_Boolean("Main","MainSwitch","卡片消息复制",false))
            {
                try{
                    if(MessageRecordNameSimple.equals("MessageForArkApp")
                            || MessageRecordNameSimple.equals("MessageForStructing")
                            || MessageRecordNameSimple.equals("MessageForTroopPobing")
                    )
                    {
                        //复制卡片消息的标题
                        TextView tv=RL.findViewById(445566);
                        if(tv==null)
                        {


                            //长按标签,位于Parent顶部中央,最大化
                            RelativeLayout.LayoutParams RLP = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT);
                            RLP.width= ViewGroup.LayoutParams.MATCH_PARENT;

                            tv = new TextView(RL.getContext());

                            RL.addView(tv,RLP);

                            tv.setText("长按复制卡片代码");
                            tv.setGravity(Gravity.CENTER);//居中显示
                            tv.setTextColor(Color.RED);
                            tv.setId(445566);
                        }


                        tv.setTag(ChatMsg);//保存消息对象
                        tv.setOnLongClickListener(new View.OnLongClickListener() {
                            @Override
                            public boolean onLongClick(View view) {

                                Object ChatMessage = view.getTag();
                                try{
                                    if(ChatMessage.getClass().getSimpleName().equals("MessageForArkApp"))
                                    {
                                        Object  ArkAppMsg= MField.GetField(ChatMessage,ChatMessage.getClass(),"ark_app_message", MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"));
                                        String json= MMethod.CallMethod(ArkAppMsg,MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"),"toAppXml",String.class,new Class[0],new Object[0]);
                                        Utils.SetTextClipboard(json);
                                        Toast.makeText(MHookEnvironment.MAppContext,"已复制",Toast.LENGTH_LONG).show();
                                    }
                                    else if(ChatMessage.getClass().getSimpleName().equals("MessageForStructing") || ChatMessage.getClass().getSimpleName().equals("MessageForTroopPobing"))
                                    {
                                        Object Structing = MField.GetField(ChatMessage,ChatMessage.getClass(),"structingMsg",MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"));
                                        String xml=MMethod.CallMethod(Structing,MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"),"getXml",String.class,new Class[0],new Object[0]);
                                        Utils.SetTextClipboard(xml);
                                        Toast.makeText(MHookEnvironment.MAppContext,"已复制",Toast.LENGTH_LONG).show();
                                    }
                                }
                                catch (Throwable e)
                                {
                                    MLogCat.Print_Error("CopyXml",Log.getStackTraceString(e));
                                }
                                return false;
                            }
                        });
                    }

                }
                catch (Exception e)
                {
                    MLogCat.Print_Error("CopyXml2",Log.getStackTraceString(e));
                }
            }


        }catch (Throwable th)
        {
            MLogCat.Print_Error("CopyXML1",Log.getStackTraceString(th));
        }
    }
    //在QQ的数据框指定位置插入文本而不移动光标位置
    public static void AddEditText(String Text) {
        if(ed!=null)
        {
            int pos = ed.getSelectionStart();
            Editable e = ed.getText();
            e.insert(pos,Text);
            ed.setText(e);
            ed.setSelection(pos+Text.length());
        }
    }

    static Method IsNowReplyingMethod = null;
    //判断当前输入框是否是正在回复某一条消息的状态
    public static boolean IsNowReplying()
    {
        try {
            Object HelperProvider = FieldTable.BaseChatPie_HelperProvider().get(NowChatPie);
            //MLogCat.Print_Debug(HelperProvider.getClass().getName());


            if(IsNowReplyingMethod==null)
            {
                for(Method sm : HelperProvider.getClass().getSuperclass().getSuperclass().getDeclaredMethods())
                {
                    if(sm.getName().equals("a") && sm.getParameterCount()==1 && sm.getParameterTypes()[0]==int.class)
                    {
                        if(sm.getReturnType()!= Dialog.class && sm.getReturnType()!=void.class && sm.getReturnType()!=boolean.class)
                        {
                            IsNowReplyingMethod = sm;
                            break;
                        }
                    }
                }
            }


            Object ReplyHelper = IsNowReplyingMethod.invoke(HelperProvider,119);
            Object SourceInfo = MMethod.CallMethod(ReplyHelper,"a",MClass.loadClass("com.tencent.mobileqq.data.MessageForReplyText$SourceMsgInfo"),new Class[0]);
            return SourceInfo!=null;
        } catch (Exception e) {
            MLogCat.Print_Error("GetReplyInfo",e);
            return false;
        }

    }
}
