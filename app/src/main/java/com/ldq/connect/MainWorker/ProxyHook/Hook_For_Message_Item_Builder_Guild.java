package com.ldq.connect.MainWorker.ProxyHook;

import android.annotation.SuppressLint;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.RelativeLayout;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.MainWorker.BaseWorker.EnvHook.BasePieHook;
import com.ldq.connect.MainWorker.WidgetHandler.Handler_For_Repeat_Msg_Common;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.QQMessage_Builder;
import com.ldq.connect.QQUtils.QQTools;

import org.json.JSONObject;

import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

@SuppressLint("ResourceType")
public class Hook_For_Message_Item_Builder_Guild {
    public static void Start(){
        try{
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.guild.chatpie.msgviewbuild.builder.BaseGuildMsgViewBuild"), "a",
                    int.class, int.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"), View.class, ViewGroup.class,
                    MClass.loadClass("com.tencent.mobileqq.activity.aio.OnLongClickAndTouchListener"), new XC_MethodHook() {
                        @SuppressLint("ResourceType")
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            OnCopyArkappMsg((ViewGroup) param.args[3],(ViewGroup) param.args[4],param.args[2]);
                            //MLogCat.Print_Debug(param.args[4].getClass().getName());
                            if (!MConfig.Get_Boolean("Main","MainSwitch","消息复读",false))return;
                            Object ChatMsg = param.args[2];


                            String MessageRecordName = ChatMsg.getClass().getName();
                            //MLogCat.Print_Debug(MessageRecordName);
                            ViewGroup RL = (ViewGroup) param.args[3];
                            if(RL==null)return;
                            if(     MessageRecordName.contains("MessageForText")       || MessageRecordName.contains("MessageForLongTextMsg") ||
                                    MessageRecordName.contains("MessageForPic")  || MessageRecordName.contains("MessageForMixedMsg")  ||
                                    MessageRecordName.contains("MessageForShortVideo") || MessageRecordName.contains("MessageForArkApp") ||
                                    MessageRecordName.contains("MessageForReplyText") || MessageRecordName.contains("MessageForAniSticker")
                            ){
                               ImageButton imageButton =RL.findViewById(23333);
                                if(imageButton==null)
                                {
                                    //不存在则创建
                                    imageButton = new ImageButton(RL.getContext());
                                    imageButton.setImageBitmap(Handler_For_Repeat_Msg_Common.RepeatIconBitmap);
                                    imageButton.setBackgroundColor(Color.TRANSPARENT);


                                    RelativeLayout.LayoutParams llparam = new RelativeLayout.LayoutParams(Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","Size",35)), Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","Size",35)));
                                    imageButton.setAdjustViewBounds(true);
                                    imageButton.getBackground().setAlpha(100);
                                    imageButton.setMaxHeight(Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","Size",35)));
                                    imageButton.setMaxWidth(Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","Size",35)));
                                    imageButton.setId(23333);
                                    imageButton.setTag(ChatMsg);
                                    imageButton.setOnClickListener(view -> {
                                        try{
                                            Object ChatMessageRecord = view.getTag();
                                            String ClassName = ChatMessageRecord.getClass().getSimpleName();
                                            switch (ClassName){
                                                case "MessageForText":
                                                case "MessageForLongTextMsg":{
                                                    String mStr = MField.GetField(ChatMessageRecord,ChatMessageRecord.getClass(),"extStr",String.class);
                                                    JSONObject mJson = new JSONObject(mStr);
                                                    mStr = mJson.optString("guild_at_info_list");
                                                    ArrayList AtList3 = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"),"getTroopMemberInfoFromExtrJson",ArrayList.class,new Class[]{String.class},mStr);
                                                    String nowMsg =  MField.GetField(ChatMessageRecord,ChatMessageRecord.getClass(),"msg",String.class);
                                                    QQMessage.Message_Send_Text(MHookEnvironment.CurrentSession,nowMsg,AtList3);
                                                    break;
                                                }
                                                case "MessageForPic":{

                                                    QQMessage.Message_Send_Pic(MHookEnvironment.CurrentSession,ChatMessageRecord);
                                                    break;
                                                }
                                                case "MessageForMixedMsg":{
                                                    MixedMsg_Preload(ChatMessageRecord);
                                                    QQMessage.Message_Send_Mix(MHookEnvironment.CurrentSession,ChatMessageRecord);
                                                    break;
                                                }
                                                case "MessageForShortVideo":{
                                                    QQMessage.QQ_Forward_ShortVideo(MHookEnvironment.CurrentSession,ChatMessageRecord);
                                                    break;
                                                }
                                                case "MessageForArkApp":{
                                                    Object  ArkAppMsg= MField.GetField(ChatMessageRecord,ChatMessageRecord.getClass(),"ark_app_message", MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"));
                                                    String json= MMethod.CallMethod(ArkAppMsg, MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"),"toAppXml",String.class,new Class[0],new Object[0]);

                                                    Method med = MMethod.FindMethod("com.tencent.mobileqq.data.ArkAppMessage","fromAppXml",boolean.class,new Class[]{
                                                            String.class
                                                    });
                                                    Constructor cons = MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage").getConstructor();
                                                    Object _ArkAppMsg = cons.newInstance();
                                                    med.invoke(_ArkAppMsg,new Object[]{json});
                                                    //获取发送xml消息的方法
                                                    med = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",boolean.class,new Class[]{
                                                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                                                            MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                                                            MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage")
                                                    });
                                                    //调用发送消息的方法
                                                    med.invoke(null,new Object[]{MHookEnvironment.AppInterface, MHookEnvironment.CurrentSession,_ArkAppMsg});
                                                    break;
                                                } case "MessageForReplyText":{
                                                    Object Call = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.replymsg.ReplyMsgSender"),"a", MClass.loadClass("com.tencent.mobileqq.replymsg.ReplyMsgSender"),
                                                            new Class[0],new Object[0]
                                                    );
                                                    Method mMethod = MMethod.FindMethod("com.tencent.mobileqq.replymsg.ReplyMsgSender","a",void.class,new Class[]{
                                                            MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                                                            MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"),
                                                            MClass.loadClass("com.tencent.mobileqq.activity.aio.BaseSessionInfo"),
                                                            int.class,
                                                            int.class,
                                                            boolean.class
                                                    });
                                                    mMethod.invoke(Call, MHookEnvironment.AppInterface,ChatMessageRecord , MHookEnvironment.CurrentSession,0,0,false);
                                                    break;
                                                } case "MessageForAniSticker":{
                                                    int ServID = MField.GetField(ChatMessageRecord,"sevrId",int.class);
                                                    ServID = QQTools.DecodeAntEmoCode(ServID);
                                                    QQMessage.Message_Send_Sticker(ServID,MHookEnvironment.CurrentSession);
                                                    break;
                                                }
                                            }
                                        }
                                        catch (Throwable e)
                                        {
                                            MLogCat.Print_Error("RepeatError",e);
                                            //MLogcat.e("Repeat Error",Log.getStackTraceString(e));
                                        }
                                    });

                                    //imageButton.setOnLongClickListener(new mLongClick());
                                    RL.addView(imageButton,llparam);
                                }
                                else
                                {
                                    imageButton.setVisibility(View.VISIBLE);
                                    imageButton.setTag(ChatMsg); //设置消息对象
                                }
                                RelativeLayout.LayoutParams llparam = (RelativeLayout.LayoutParams) imageButton.getLayoutParams();
                                View mView = null;

                                if(MessageRecordName.contains("MessageForPic"))
                                {
                                    mView = GetViewName("RelativeLayout",RL,1);
                                    //MLogCat.Print_Debug(mView);
                                }
                                else if(MessageRecordName.contains("MessageForText")||
                                        MessageRecordName.contains("MessageForLongTextMsg") ||
                                        MessageRecordName.contains("MessageForFoldMsg"))
                                {
                                    mView = GetViewName("ETTextView",RL);
                                    //MLogCat.Print_Debug(mView);
                                }
                                else if(MessageRecordName.contains("MessageForMixedMsg"))
                                {
                                    mView = GetViewName("GuildMixedMsgLinearLayout",RL);
                                    //MLogCat.Print_Debug(mView);

                                }else if(MessageRecordName.contains("MessageForShortVideo")){
                                    mView = GetViewName("RelativeLayout",RL,1);
                                }else if(MessageRecordName.contains("MessageForArkApp")){
                                    mView = GetViewName("ArkAppRootLayout",RL);
                                }else if(MessageRecordName.contains("MessageForReplyText"))
                                {
                                    mView = GetViewName("SelectableLinearLayout",RL);
                                }else if(MessageRecordName.contains("MessageForAniSticker"))
                                {
                                    mView = GetViewName("FrameLayout",RL);
                                }
                                if(mView!=null) {
                                    mView.requestLayout();
                                    imageButton.requestLayout();


                                    llparam.addRule(RelativeLayout.RIGHT_OF,mView.getId());
                                    llparam.addRule(RelativeLayout.ALIGN_TOP,mView.getId());
                                    ViewGroup.MarginLayoutParams mLParam = llparam;
                                    mLParam.topMargin = mView.getHeight()/2 - imageButton.getHeight()/2;
                                    mLParam.leftMargin = -Utils.dip2px(RL.getContext(), 22);



                                    imageButton.setLayoutParams(llparam);
                                }
                            }
                        }
                    }
            );
            SetTest();
        }catch (Throwable e){

        }
    }
    public static void MixedMsg_Preload(Object MixedMsg) throws Exception {
        ArrayList Item = MField.GetField(MixedMsg,"msgElemList");
        for(Object Elem : Item){
            if(Elem.getClass().getSimpleName().equals("MessageForPic")){
                QQMessage.PreDownloadPic(Elem);
            }
        }
    }
    public static void OnCopyArkappMsg(ViewGroup ChatLayout,ViewGroup RootLayout,Object ChatMsg){
        try{
            BasePieHook._caller_copy((RelativeLayout) ChatLayout,ChatMsg);
        }catch (Throwable e){
            MLogCat.Print_Error("Guild_Copy_Xml",e);
        }
    }
    public static void SetTest() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.guild.chatpie.helper.GuildInputBarCommonComponent"), "o", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Button btnSend = MField.GetField(param.thisObject,"h",Button.class);

                EditText InputBox = MField.GetFirstField(param.thisObject,param.thisObject.getClass(),MClass.loadClass("com.tencent.widget.XEditTextEx"));
                if(MConfig.Get_Boolean("Main","MainSwitch","卡片消息发送",false)){
                    btnSend.setOnLongClickListener(new View.OnLongClickListener() {
                        @Override
                        public boolean onLongClick(View v) {
                            String Input = InputBox.getText().toString();
                            if(QQMessage.ConvertAndSendAntMsg(Input))
                            {
                                InputBox.setText("");
                                return true;
                            }
                            if(Input.startsWith("<?xml"))
                            {
                                InputBox.setText("");//清空
                                //创建消息对象
                                QQMessage.Message_Send_Xml(MHookEnvironment.CurrentSession, QQMessage_Builder.Build_AbsStructMsg(Input));
                                return true;
                            }
                            if(Input.startsWith("{"))
                            {
                                InputBox.setText("");
                                //创建消息对象
                                QQMessage.Message_Send_ArkApp(MHookEnvironment.CurrentSession,QQMessage_Builder.Build_ArkAppMsg(Input));
                                return true;
                            }
                            return false;
                        }
                    });
                }

            }
        });
    }
    public static View GetViewNamePrint(String Name, ViewGroup vg)
    {
        for(int i=0;i<vg.getChildCount();i++)
        {
            MLogCat.Print_Debug(vg.getChildAt(i).getClass().getSimpleName());
            if(vg.getChildAt(i).getClass().getSimpleName().contains(Name))
            {
                return vg.getChildAt(i);
            }
        }
        return null;
    }
    public static View GetViewName(String Name, ViewGroup vg,int Count)
    {
        int Count_ = 0;
        for(int i=0;i<vg.getChildCount();i++)
        {
            if(vg.getChildAt(i).getClass().getSimpleName().contains(Name))
            {
                if(Count_<Count){
                    Count_++;
                    continue;
                }
                return vg.getChildAt(i);
            }
        }
        return null;
    }
    public static View GetViewName(String Name, ViewGroup vg)
    {
        for(int i=0;i<vg.getChildCount();i++)
        {
            if(vg.getChildAt(i).getClass().getSimpleName().contains(Name))
            {
                return vg.getChildAt(i);
            }
        }
        return null;
    }
}
