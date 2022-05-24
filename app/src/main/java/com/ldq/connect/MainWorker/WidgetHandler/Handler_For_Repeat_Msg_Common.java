package com.ldq.connect.MainWorker.WidgetHandler;

import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.ldq.HookHelper.DexTable.ClassTable;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.FloatWindow.EmoContainer;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.MHookEnvironment;
import com.ldq.connect.QQUtils.BaseCall;
import com.ldq.connect.QQUtils.BaseInfo;
import com.ldq.connect.QQUtils.MessageRecoreFactory;
import com.ldq.connect.QQUtils.QQMessage;
import com.ldq.connect.QQUtils.QQTools;

import org.json.JSONObject;

import java.io.File;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.List;

public class Handler_For_Repeat_Msg_Common
{
    public static Bitmap RepeatIconBitmap;
    public static void InitIcon()
    {
        try{
            File mFile = new File(MHookEnvironment.PublicStorageModulePath + "repeat.png");
            if(!mFile.exists())
            {
                HttpUtils.downlaodFile("https://tsupdata-1251707849.cos.ap-chengdu.myqcloud.com/repeat.png",mFile.getAbsolutePath());
            }
            if(mFile.length() > 160 * 1024){
                Bitmap DecodeRawBitmap  = BitmapFactory.decodeFile(mFile.getAbsolutePath());
                DecodeRawBitmap = EmoContainer.upImageSize(DecodeRawBitmap,128,128);
                RepeatIconBitmap = DecodeRawBitmap;
            }else{
                RepeatIconBitmap = BitmapFactory.decodeFile(mFile.getAbsolutePath());
            }

            if(RepeatIconBitmap==null)
            {
                throw new Exception("Drawable is null!!");
            }
        }
        catch (Exception ex)
        {
            Utils.ShowToast("复读图标无法加载,请确认复读图标文件"+MHookEnvironment.PublicStorageModulePath + "repeat.png是否存在并且没有限制访问");
        }
    }
    static long LastClickTime;
    @SuppressLint("ResourceType")
    public static void _caller(RelativeLayout RL,Object ChatMsg)
    {
        try{
            Context context = RL.getContext();
            if(context != null){
                if(MHookEnvironment.CacheActContext != context){
                    MHookEnvironment.CacheActContext = context;
                }
            }
            if (!MConfig.Get_Boolean("Main","MainSwitch","消息复读",false))return;

            boolean isSendFromLocal = MMethod.CallMethod(ChatMsg,"isSendFromLocal",boolean.class,new Class[0]); //是否是自己发的消息
            if(!isSendFromLocal)
            {
                isSendFromLocal = BaseInfo.GetCurrentUin().equals(MField.GetField(ChatMsg,"senderuin",String.class));
            }
            String MessageRecordName = ChatMsg.getClass().getName();


            int istroop = MField.GetField(ChatMsg, ChatMsg.getClass(),"istroop", int.class);//是否为群组

            if(     MessageRecordName.contains("MessageForText")       || MessageRecordName.contains("MessageForLongTextMsg") ||
                    MessageRecordName.contains("MessageForPic")        || MessageRecordName.contains("MessageForPtt") ||
                    MessageRecordName.contains("MessageForMixedMsg")   || MessageRecordName.contains("MessageForReplyText") ||
                    MessageRecordName.contains("MessageForFoldMsg")    || MessageRecordName.contains("MessageForScribble")||
                    MessageRecordName.contains("MessageForMarketFace") || MessageRecordName.contains("MessageForArkApp")||
                    MessageRecordName.contains("MessageForStructing")  || MessageRecordName.contains("MessageForAniSticker") ||
                    MessageRecordName.contains("MessageForTroopEffectPic") || MessageRecordName.contains("MessageForArkFlashChat") ||
                    MessageRecordName.contains("MessageForShortVideo") || MessageRecordName.contains("MessageForPokeEmo")

            )
            {
                ImageButton imageButton =RL.findViewById(23333);
                if(imageButton==null)
                {
                    //不存在则创建
                    imageButton = new ImageButton(RL.getContext());
                    imageButton.setImageBitmap(RepeatIconBitmap);
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
                            if(MConfig.Get_Boolean("Main","Repeat","DoubleClick",false))
                            {
                                if(Math.abs(System.currentTimeMillis()-LastClickTime)>250)
                                {
                                    LastClickTime = System.currentTimeMillis();
                                    return;
                                }
                            }
                            Object obj = view.getTag();
                            String ClassName = obj.getClass().getName();
                            if(view.getContext().getClass().getSimpleName().equals("MultiForwardActivity"))
                            {
                                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext(),AlertDialog.THEME_HOLO_LIGHT);
                                LinearLayout ll = new LinearLayout(view.getContext());
                                ll.setOrientation(LinearLayout.VERTICAL);
                                String sender = MField.GetField(obj,obj.getClass() ,"senderuin", String.class);
                                TextView vv = new TextView(view.getContext());
                                vv.setTextColor(Color.BLACK);
                                vv.setTextSize(16);
                                vv.setText("发送者:"+sender);
                                ll.addView(vv);
                                int isTroop = MField.GetField(obj,obj.getClass() ,"istroop", int.class);
                                if(isTroop==1)
                                {
                                    String Troop = MField.GetField(obj,obj.getClass() ,"frienduin", String.class);
                                    TextView vv22 = new TextView(view.getContext());
                                    vv22.setTextColor(Color.BLACK);
                                    vv22.setTextSize(16);
                                    vv22.setText("群号:"+Troop);
                                    ll.addView(vv22);
                                }
                                builder.setView(ll);
                                builder.setTitle("转发的信息");
                                builder.setPositiveButton("关闭",null);
                                builder.show();
                                return;
                            }
                            if(ClassName.contains("MessageForText") || ClassName.contains("MessageForLongTextMsg") ||ClassName.contains("MessageForFoldMsg"))
                            {

                                ArrayList AtList1 = MField.GetField(obj,obj.getClass(),"atInfoTempList",ArrayList.class);
                                ArrayList AtList2 = MField.GetField(obj,obj.getClass(),"atInfoList",ArrayList.class);
                                String mStr = MField.GetField(obj,obj.getClass(),"extStr",String.class);
                                JSONObject mJson = new JSONObject(mStr);
                                mStr = mJson.optString("troop_at_info_list");
                                ArrayList AtList3 = MMethod.CallMethod(null, MClass.loadClass("com.tencent.mobileqq.data.MessageForText"),"getTroopMemberInfoFromExtrJson",ArrayList.class,new Class[]{String.class},mStr);
                                if(AtList1==null)AtList1=AtList2;
                                if(AtList1==null)AtList1=AtList3;
                                String nowMsg =  MField.GetField(obj,obj.getClass(),"msg",String.class);
                                QQMessage.Message_Send_Text(MHookEnvironment.CurrentSession,nowMsg,AtList1);
                            }
                            else if(ClassName.contains("MessageForPic"))
                            {
                                QQMessage.Message_Send_Pic(MHookEnvironment.CurrentSession,obj);
                            }
                            else if(ClassName.contains("MessageForPtt"))
                            {

                                String pttPath = MMethod.CallMethod(obj,obj.getClass(),"getLocalFilePath",String.class,new Class[0],new Object[0]);
                                QQMessage.Group_Send_Ptt(MHookEnvironment.CurrentSession,pttPath);
                            }
                            else if(ClassName.contains("MessageForMixedMsg"))
                            {
                                QQMessage.Message_Send_Mix(MHookEnvironment.CurrentSession,obj);
                            }
                            else if(ClassName.contains("MessageForReplyText"))
                            {
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
                                mMethod.invoke(Call, MHookEnvironment.AppInterface,obj , MHookEnvironment.CurrentSession,0,0,false);
                            }
                            else if(ClassName.contains("MessageForScribble")) {
                                Object WillingSend = MessageRecoreFactory.CopyToTYMessage(obj);
                                BaseCall.AddAndSendMsg(WillingSend);
                            }
                            else if(ClassName.contains("MessageForMarketFace"))
                            {

                                Object WillsendMsg = MessageRecoreFactory.CopyToMacketFaceMessage(obj);
                                BaseCall.AddAndSendMsg(WillsendMsg);
                            }
                            else if(ClassName.contains("MessageForArkApp"))
                            {
                                Object  ArkAppMsg= MField.GetField(obj,obj.getClass(),"ark_app_message", MClass.loadClass("com.tencent.mobileqq.data.ArkAppMessage"));
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
                            }
                            else if(ClassName.contains("MessageForStructing") ||ClassName.contains("MessageForTroopPobing"))
                            {
                                Object Structing = MField.GetField(obj,obj.getClass(),"structingMsg", MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"));
                                String xml= MMethod.CallMethod(Structing, MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"),"getXml",String.class,new Class[0],new Object[0]);
                                Method BuildStructMsg = MMethod.FindMethod("com.tencent.mobileqq.structmsg.TestStructMsg","a",
                                        MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg"),new Class[]{String.class});
                                Object msgData = BuildStructMsg.invoke(null,new Object[]{xml});
                                //获取发送xml消息的方法
                                Method SendStructMsg = MMethod.FindMethod("com.tencent.mobileqq.activity.ChatActivityFacade","a",
                                        void.class,new Class[]{
                                                MClass.loadClass("com.tencent.mobileqq.app.QQAppInterface"),
                                                MClass.loadClass("com.tencent.mobileqq.activity.aio.SessionInfo"),
                                                MClass.loadClass("com.tencent.mobileqq.structmsg.AbsStructMsg")
                                        });
                                //调用发送消息的方法
                                SendStructMsg.invoke(null,new Object[]{MHookEnvironment.AppInterface, MHookEnvironment.CurrentSession,msgData});
                            }
                            else if(ClassName.contains("MessageForTroopEffectPic"))
                            {
                                int Type = MField.GetField(obj,"effectId",int.class);
                                String Path = MMethod.CallMethod(obj,"getFilePath",String.class,new Class[]{String.class},"chatimg");
                                QQMessage.Message_Send_Effect_Pic(BaseInfo.GetCurrentGroupUin(),Path,Type-40000);
                            }
                            else if(ClassName.contains("MessageForAniSticker"))
                            {

                                //Object sSend = MessageRecoreFactory.Build_CopyToAntSticker(obj);
                                int ServID = MField.GetField(obj,"sevrId",int.class);
                                ServID = QQTools.DecodeAntEmoCode(ServID);
                                QQMessage.Message_Send_Sticker(ServID,MHookEnvironment.CurrentSession);
                            }
                            else if(ClassName.contains("MessageForArkFlashChat"))
                            {
                                Object WillSend = MessageRecoreFactory.Build_FlashChat(obj);
                                BaseCall.AddAndSendMsg(WillSend);
                            }else if (ClassName.contains("MessageForShortVideo")) {
                                QQMessage.QQ_Forward_ShortVideo(MHookEnvironment.CurrentSession,obj);
                            }else if (ClassName.contains("MessageForPokeEmo")){
                                QQMessage.Copy_And_Send_PokeMsg(obj);
                            }
                        }
                        catch (Throwable e)
                        {
                            MLogCat.Print_Error("RepeatError",e);
                            //MLogcat.e("Repeat Error",Log.getStackTraceString(e));
                        }

                    });

                    imageButton.setOnLongClickListener(new Handler_Repeat_Icon_Long_Click());
                    RL.addView(imageButton,llparam);
                }
                else
                {
                    imageButton.setVisibility(View.VISIBLE);
                    imageButton.setTag(ChatMsg); //设置消息对象
                }

                if(istroop==1)
                {
                    String TroopUin =  MField.GetField(ChatMsg, ChatMsg.getClass(),"frienduin", String.class);//是否为群组
                    List<String> l = MConfig.Get_List("Main","Repeat","Black");
                    if(l.contains(TroopUin))
                    {
                        imageButton.setVisibility(View.GONE);
                        return;
                    }

                }

                //设置按钮位置
                RelativeLayout.LayoutParams llparam = (RelativeLayout.LayoutParams) imageButton.getLayoutParams();
                View mView = null;


                if(MessageRecordName.contains("MessageForPic"))
                {
                    mView = GetViewName("RelativeLayout",RL);

                }
                else if(MessageRecordName.contains("MessageForText")||
                        MessageRecordName.contains("MessageForLongTextMsg") ||
                        MessageRecordName.contains("MessageForFoldMsg"))
                {
                    mView = GetViewName("ETTextView",RL);
                }
                else if(MessageRecordName.contains("MessageForPtt"))
                {
                    mView = GetViewName("BreathAnimationLayout",RL);
                }
                else if(MessageRecordName.contains("MessageForMixedMsg"))
                {
                    mView = GetViewName("MixedMsgLinearLayout",RL);
                }
                else if(MessageRecordName.contains("MessageForReplyText"))
                {
                    mView = GetViewName("SelectableLinearLayout",RL);
                }
                else if(MessageRecordName.contains("MessageForScribble"))
                {
                    mView = GetViewName("RelativeLayout",RL);
                }
                else if(MessageRecordName.contains("MessageForMarketFace"))
                {
                    mView = GetViewName("RelativeLayout",RL);
                }
                else if(MessageRecordName.contains("MessageForArkApp"))
                {
                    mView = GetViewName("ArkAppRootLayout",RL);
                }
                else if(MessageRecordName.contains("MessageForStructing"))
                {
                    mView = GetViewName("RelativeLayout",RL);
                }
                else if(MessageRecordName.contains("MessageForTroopPobing"))
                {
                    mView = GetViewName("LinearLayout",RL);
                }
                else if(MessageRecordName.contains("MessageForTroopEffectPic"))
                {
                    mView = GetViewName("RelativeLayout",RL);
                }
                else if(MessageRecordName.contains("MessageForAniSticker"))
                {
                    mView = GetViewName("FrameLayout",RL);
                }
                else if (MessageRecordName.contains("MessageForArkFlashChat"))
                {
                    mView = GetViewName("ArkAppRootLayout",RL);
                }
                else if (MessageRecordName.contains("MessageForShortVideo"))
                {
                    mView = GetViewName("RelativeLayout",RL);
                }else if (MessageRecordName.contains("MessageForPokeEmo")){
                    mView = GetViewName("RelativeLayout",RL);
                }
                if(mView!=null)
                {
                    if(MConfig.Get_Long("Main","Repeat","Style",0)==0)
                    {
                        if((isSendFromLocal && !RL.getContext().getClass().getSimpleName().equals("MultiForwardActivity")) ||
                                (istroop==1000 && isSendFromLocal))
                        {
                            llparam.removeRule(RelativeLayout.ALIGN_RIGHT);
                            llparam.removeRule(RelativeLayout.ALIGN_TOP);
                            llparam.removeRule(RelativeLayout.ALIGN_LEFT);

                            llparam.addRule(RelativeLayout.ALIGN_TOP,mView.getId());
                            llparam.addRule(RelativeLayout.ALIGN_LEFT,mView.getId());
                            ViewGroup.MarginLayoutParams mLParam = llparam;
                            mLParam.leftMargin=0+ Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","r1",-5));
                            mLParam.topMargin = 0+ Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","r2",-5));
                        }else
                        {
                            llparam.removeRule(RelativeLayout.ALIGN_RIGHT);
                            llparam.removeRule(RelativeLayout.ALIGN_TOP);
                            llparam.removeRule(RelativeLayout.ALIGN_LEFT);
                            llparam.addRule(RelativeLayout.ALIGN_TOP,mView.getId());
                            llparam.addRule(RelativeLayout.ALIGN_RIGHT,mView.getId());
                            ViewGroup.MarginLayoutParams mLParam = llparam;
                            mLParam.rightMargin=0+ Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","r1",-5));
                            mLParam.topMargin =0+ Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","r2",-5));
                        }
                    }else if (MConfig.Get_Long("Main","Repeat","Style",0)==1)
                    {
                        if((isSendFromLocal && !RL.getContext().getClass().getSimpleName().equals("MultiForwardActivity")) ||
                                (istroop==1000 && isSendFromLocal))
                        {
                            llparam.removeRule(RelativeLayout.ALIGN_RIGHT);
                            llparam.removeRule(RelativeLayout.ALIGN_TOP);
                            llparam.removeRule(RelativeLayout.ALIGN_LEFT);

                            llparam.addRule(RelativeLayout.ALIGN_BOTTOM,mView.getId());
                            llparam.addRule(RelativeLayout.ALIGN_LEFT,mView.getId());
                            ViewGroup.MarginLayoutParams mLParam = llparam;
                            mLParam.leftMargin=0+ Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","r1",-5));
                            mLParam.bottomMargin = 0+ Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","r2",-5));
                        }else
                        {
                            llparam.removeRule(RelativeLayout.ALIGN_RIGHT);
                            llparam.removeRule(RelativeLayout.ALIGN_TOP);
                            llparam.removeRule(RelativeLayout.ALIGN_LEFT);
                            llparam.addRule(RelativeLayout.ALIGN_BOTTOM,mView.getId());
                            llparam.addRule(RelativeLayout.ALIGN_RIGHT,mView.getId());
                            ViewGroup.MarginLayoutParams mLParam = llparam;
                            mLParam.rightMargin=0+ Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","r1",-5));
                            mLParam.bottomMargin =0+ Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","r2",-5));
                        }
                    }else if (MConfig.Get_Long("Main","Repeat","Style",0)==2)
                    {
                        if((isSendFromLocal && !RL.getContext().getClass().getSimpleName().equals("MultiForwardActivity")) ||
                                (istroop==1000 && isSendFromLocal))
                        {
                            llparam.removeRule(RelativeLayout.ALIGN_RIGHT);
                            llparam.removeRule(RelativeLayout.ALIGN_TOP);
                            llparam.removeRule(RelativeLayout.ALIGN_LEFT);

                            llparam.addRule(RelativeLayout.ALIGN_LEFT,mView.getId());
                            //llparam.addRule(RelativeLayout.CENTER_VERTICAL,1);
                            RL.requestLayout();

                            int AddedLength = mView.getTop();
                            AddedLength += mView.getHeight()/2- Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","Size",35))/2;

                            int OffsetV = Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","Size",35));

                            ViewGroup.MarginLayoutParams mLParam = llparam;
                            mLParam.leftMargin=0+ Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","r1",-5))-OffsetV;
                            mLParam.topMargin = 0+ Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","r2",-5))+AddedLength;
                        }else
                        {
                            llparam.removeRule(RelativeLayout.ALIGN_RIGHT);
                            llparam.removeRule(RelativeLayout.ALIGN_TOP);
                            llparam.removeRule(RelativeLayout.ALIGN_LEFT);

                            llparam.addRule(RelativeLayout.ALIGN_RIGHT,mView.getId());
                            //llparam.addRule(RelativeLayout.CENTER_VERTICAL,1);
                            RL.requestLayout();
                            mView.requestLayout();
                            int AddedLength = mView.getTop();
                            AddedLength += mView.getHeight()/2- Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","Size",35))/2;

                            int OffsetV = Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","Size",35));
                            ViewGroup.MarginLayoutParams mLParam = llparam;
                            mLParam.rightMargin=0+ Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","r1",-5))-OffsetV;
                            mLParam.topMargin =0+ Utils.dip2px(RL.getContext(),MConfig.Get_Long("Main","Repeat","r2",-5))+AddedLength;
                        }
                    }

                    imageButton.setLayoutParams(llparam);
                }

            }
            else
            {
                ImageButton imageButton =RL.findViewById(23333);
                if(imageButton!=null)
                {
                    imageButton.setVisibility(View.GONE);
                }
            }

        }
        catch (Exception e)
        {
            MLogCat.Print_Error("Repeat",e);
        }
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
}
