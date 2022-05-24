package com.ldq.connect.MainWorker.ProxyHook;

import android.content.Context;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;

import java.lang.reflect.Field;
import java.lang.reflect.Method;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedBridge;

public class Hook_PbSendMsgPack {
    public static void Start() throws Exception {
        Method m = MMethod.FindMethod(MClass.loadClass("com.tencent.mobileqq.app.MessageHandler"), "a",void.class,new Class[]{
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"), MClass.loadClass("msf.msgsvc.msg_svc$PbSendMsgReq"),
                long.class,int.class, MClass.loadClass("com.tencent.mobileqq.app.BusinessObserver"),boolean.class
        });
        XposedBridge.hookMethod(m, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                try{

                    String TroopUin = MField.GetField(param.args[0],"frienduin",String.class);
                    int istroop = MField.GetField(param.args[0],"istroop",int.class);
                    Object ResvAttr = param.args[1];
                    Object msg_body = MField.GetField(ResvAttr,"msg_body");

                    Object rich_text = MField.GetField(msg_body,"rich_text");

                    //Object Attr = MField.GetField(rich_text,"attr");









                    Object elems = MField.GetField(rich_text,"elems");





                    List Value = MField.GetField(elems,"value");
                   // MLogCat.Print_Debug("6666666666666666666666");

                    Object Elem = Value.get(0);
                    Object ExtraInfo = MField.GetField(Elem,"extra_info");
                    MMethod.CallMethod(ExtraInfo,"setHasFlag",void.class,new Class[]{boolean.class},true);

                    Object ByteMicro = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro"),new Class[]{byte[].class},"你是傻逼".getBytes());
                    MMethod.CallMethod(MField.GetField(ExtraInfo,"bytes_sender_title"),"set",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro")},ByteMicro);


                    PrintHas(Elem);
                    //ByteMicro = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro"),new Class[]{byte[].class},TroopTitleSet.getBytes());
                    //MMethod.CallMethod(MField.GetField(ExtraInfo,"bytes_nick"),"set",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro")},ByteMicro);

                    /*

                    String TroopTitleSet = TroopConfig.GetString("DefTitleSet");
                    if(!TextUtils.isEmpty(TroopConfig.GetString("TitleSet-"+TroopUin)))
                    {
                        TroopTitleSet = TroopConfig.GetString("TitleSet-"+TroopUin);
                    }
                    if(!TextUtils.isEmpty(TroopTitleSet))
                    {
                        Object Elem = MClass.CallConstrutor(MClass.loadClass("tencent.im.msg.im_msg_body$Elem"),new Class[0]);
                        Object ExtraInfo = MField.GetField(Elem,"extra_info");
                        MMethod.CallMethod(ExtraInfo,"setHasFlag",void.class,new Class[]{boolean.class},true);
                        Object ByteMicro = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro"),new Class[]{byte[].class},TroopTitleSet.getBytes());
                        MMethod.CallMethod(MField.GetField(ExtraInfo,"bytes_sender_title"),"set",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro")},ByteMicro);
                        //ByteMicro = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro"),new Class[]{byte[].class},TroopTitleSet.getBytes());
                        //MMethod.CallMethod(MField.GetField(ExtraInfo,"bytes_nick"),"set",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro")},ByteMicro);
                        Value.add(Elem);
                    }
                    else
                    {
                        String s = JavaPlugin.CallForDIYSet(TroopUin,istroop,1);
                        if(!TextUtils.isEmpty(s))
                        {
                            Object Elem = MClass.CallConstrutor(MClass.loadClass("tencent.im.msg.im_msg_body$Elem"),new Class[0]);
                            Object ExtraInfo = MField.GetField(Elem,"extra_info");
                            MMethod.CallMethod(ExtraInfo,"setHasFlag",void.class,new Class[]{boolean.class},true);
                            Object ByteMicro = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro"),new Class[]{byte[].class},s.getBytes());
                            MMethod.CallMethod(MField.GetField(ExtraInfo,"bytes_sender_title"),"set",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro")},ByteMicro);
                            //ByteMicro = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro"),new Class[]{byte[].class},TroopTitleSet.getBytes());
                            //MMethod.CallMethod(MField.GetField(ExtraInfo,"bytes_nick"),"set",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro")},ByteMicro);
                            Value.add(Elem);
                        }
                    }

                     */
                    /*




                    int TroopLevel = (int) TroopConfig.GetLong("DefLevelSet");
                    if((int) TroopConfig.GetLong("LevelSet-"+TroopUin)!=0)
                    {
                        TroopLevel = (int) TroopConfig.GetLong("LevelSet-"+TroopUin);
                    }


                    int SetID1 = (int) TroopConfig.GetLong("TroopTipSet1-"+TroopUin);
                    int SetID2 = (int) TroopConfig.GetLong("TroopTipSet2-"+TroopUin);
                    int SetID3 = (int) TroopConfig.GetLong("TroopTipSet3-"+TroopUin);

                    int SetVIPID3 = (int) TroopConfig.GetLong("DefVipSet");
                    if((int) TroopConfig.GetLong("TroopVIPSet4-"+TroopUin)!=0)
                    {
                        SetVIPID3 = (int) TroopConfig.GetLong("TroopVIPSet4-"+TroopUin);
                    }

                    int SetFONTID = (int) TroopConfig.GetLong("DefFontSet");
                    if((int) TroopConfig.GetLong("TroopFontSet4-"+TroopUin)!=0)
                    {
                        SetFONTID = (int) TroopConfig.GetLong("TroopFontSet4-"+TroopUin);
                    }

                    int BubbleID = (int) TroopConfig.GetLong("DefBubbleSet");
                    if((int) TroopConfig.GetLong("TroopBubbleSet4-"+TroopUin)!=0)
                    {
                        BubbleID = (int) TroopConfig.GetLong("TroopBubbleSet4-"+TroopUin);
                    }


                    try{
                        String str = JavaPlugin.CallForDIYSet(TroopUin,istroop,2);
                        if(str!=null)
                        {
                            int GetBubbleID = Integer.parseInt(str);
                            if(GetBubbleID!=0)BubbleID = GetBubbleID;
                        }

                        str = JavaPlugin.CallForDIYSet(TroopUin,istroop,3);
                        if(str!=null)
                        {
                            int GetFontID = Integer.parseInt(str);
                            if(GetFontID!=0)SetFONTID = GetFontID;
                        }
                    }catch (Exception e)
                    {
                        MLogCat.Print_Error("CallToDiyPlugin",e);
                    }

                    if(TroopLevel !=0 || SetID1!=0 || SetID2!=0 || SetID3!=0 || SetVIPID3!=0 ||SetFONTID!=0 || BubbleID!=0) {


                        if (!param.args[0].getClass().getName().contains("MessageForLongText")) {
                            Object ElemColor = MClass.CallConstrutor(MClass.loadClass("tencent.im.msg.im_msg_body$Elem"), new Class[0]);
                            Object flags = MField.GetField(ElemColor, "elem_flags2");
                            MMethod.CallMethod(flags, "setHasFlag", void.class, new Class[]{boolean.class}, true);


                            if( BubbleID!=0)
                            {
                                Value.add(ElemColor);
                                MMethod.CallMethod(MField.GetField(flags, "uint32_color_text_id"), "set", void.class, new Class[]{int.class}, BubbleID);
                                Value.add(ElemColor);
                                Value.add(ElemColor);
                                Value.add(ElemColor);
                                MField.SetField(param.args[0],"vipBubbleID",(long)BubbleID);
                            }
                            else
                            {
                                MMethod.CallMethod(MField.GetField(flags, "uint32_color_text_id"), "set", void.class, new Class[]{int.class}, 0);
                                Value.add(ElemColor);
                            }

                        }



                        Object ValueSet = Value.get(1);
                        Object FlagObjSet = MField.GetField(ValueSet,"general_flags");
                        //MMethod.CallMethod(MField.GetField(FlagObjSet,"uint32_bubble_diy_text_id"),"set",void.class,new Class[]{int.class},2423);


                        Object ValueObj = MClass.CallConstrutor(MClass.loadClass("tencent.im.msg.im_msg_body$Elem"),new Class[0]);




                        Object FlagObj = MField.GetField(ValueObj,"general_flags");
                        if( BubbleID!=0)
                        {
                            MMethod.CallMethod(MField.GetField(FlagObj,"uint32_bubble_diy_text_id"),"set",void.class,new Class[]{int.class},BubbleID);
                        }

                        //MMethod.CallMethod(MField.GetField(FlagObj,"uint32_bubble_sub_id"),"set",void.class,new Class[]{int.class},8888);



                        MMethod.CallMethod(FlagObj, "setHasFlag", void.class, new Class[]{boolean.class}, true);
                        //Object Reserve = MField.GetField(FlagObj,"bytes_pb_reserve");
                        Object NewInstance = MClass.CallConstrutor(MClass.loadClass("tencent.im.msg.hummer.resv.generalflags$ResvAttr"),new Class[0]);





                        Object LevelClass = MClass.CallConstrutor(MClass.loadClass("tencent.im.troop.honor.troop_honor$GroupUserCardHonor"), new Class[0]);
                        MMethod.CallMethod(LevelClass, "setHasFlag", void.class, new Class[]{boolean.class}, true);

                        if (TroopLevel != 0) {
                            MMethod.CallMethod(MField.GetField(LevelClass, "level"), "set", void.class, new Class[]{int.class}, TroopLevel);
                        }
                        Object IDArray = MField.GetField(LevelClass, "id");
                        ArrayList list = new ArrayList();
                        if (SetID1 != 0) {
                            list.add(SetID1);
                        }
                        if (SetID2 != 0) {
                            list.add(SetID2);
                        }
                        if (SetID3 != 0) {
                            list.add(SetID3);
                        }
                        MField.SetField(IDArray, "value", list);
                        Object ByteMicro = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro"), new Class[]{byte[].class},
                                new Object[]{MMethod.CallMethod(LevelClass, "toByteArray", byte[].class, new Class[0])});
                        MMethod.CallMethod(MField.GetField(NewInstance,"bytes_hudong_mark"),"set",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro")},ByteMicro);


                        if (SetVIPID3 != 0)
                        {
                            MMethod.CallMethod(MField.GetField(NewInstance,"uint32_user_bigclub_flag"),"set",void.class,new Class[]{int.class},3);
                            MMethod.CallMethod(MField.GetField(NewInstance,"uint32_user_bigclub_level"),"set",void.class,new Class[]{int.class},8);

                            //MMethod.CallMethod(MField.GetField(NewInstance,"uint32_vip_level"),"set",void.class,new Class[]{int.class},64);
                            //MMethod.CallMethod(MField.GetField(NewInstance,"uint32_vip_type"),"set",void.class,new Class[]{int.class},339);


                            MMethod.CallMethod(MField.GetField(NewInstance,"uint32_nameplate"),"set",void.class,new Class[]{int.class},SetVIPID3);
                            MMethod.CallMethod(MField.GetField(NewInstance,"uint32_nameplate_vip_type"),"set",void.class,new Class[]{int.class},3);
                        }

                        if(SetFONTID!=0)
                        {
                            if (!param.args[0].getClass().getName().contains("MessageForLongText")) {
                                //MMethod.CallMethod(MField.GetField(NewInstance,"uint32_req_font_effect_id"),"set",void.class,new Class[]{int.class},0);

                                MMethod.CallMethod(MField.GetField(NewInstance,"uint32_mobile_custom_font"),"set",void.class,new Class[]{int.class},SetFONTID);
                                //MMethod.CallMethod(MField.GetField(NewInstance,"uint32_rich_card_name_ver"),"set",void.class,new Class[]{int.class},1);

                                Object VIPInfo22 = MField.GetField(NewInstance,"bytes_user_vip_info");
                                Object Dialogue22 = MergeToObject(VIPInfo22,"gxh_message.Dialogue");
                                //MMethod.CallMethod(MField.GetField(Dialogue22,"diyfontid"),"set",void.class,new Class[]{int.class},0);
                                ByteMicro = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro"),new Class[]{byte[].class},
                                        new Object[]{MMethod.CallMethod(Dialogue22,"toByteArray",byte[].class,new Class[0])});
                                MMethod.CallMethod(MField.GetField(NewInstance,"bytes_user_vip_info"),"set",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro")},ByteMicro);
                                MMethod.CallMethod(param.args[0],"saveExtInfoToExtStr",void.class,new Class[]{String.class,String.class},
                                        "vip_font_id",""+SetFONTID
                                );
                            }





                        }





                        MMethod.CallMethod(MField.GetField(NewInstance,"uint32_req_font_effect_id"),"set",void.class,new Class[]{int.class},2000);
                        //MMethod.CallMethod(MField.GetField(NewInstance,"uint32_rich_card_name_ver"),"set",void.class,new Class[]{int.class},1);



                        ByteMicro = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro"),new Class[]{byte[].class},
                                new Object[]{MMethod.CallMethod(NewInstance,"toByteArray",byte[].class,new Class[0])});
                        MMethod.CallMethod(MField.GetField(FlagObj,"bytes_pb_reserve"),"set",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro")},ByteMicro);


                        Value.add(ValueObj);
                    }
                    */

                }catch (Throwable th)
                {
                    MLogCat.Print_Error("TitleHook",th);
                }





            }


        });


        /*
        Method mm = MMethod.FindMethod("com.tencent.mobileqq.troop.data.TroopMessageProcessor","a",void.class,new Class[]{
                MClass.loadClass("msf.msgcomm.msg_comm$Msg"),
                MClass.loadClass("com.tencent.qphone.base.remote.FromServiceMsg"),
                MClass.loadClass("msf.onlinepush.msg_onlinepush$PbPushMsg"),
                boolean.class
        });
        XposedBridge.hookMethod(mm, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Object o = param.args[0];
                Object ComMsg = MField.GetField(o,"msg_body");
                Object RichText = MField.GetField(ComMsg,"rich_text");
                Object elems = MField.GetField(RichText,"elems");
                List Value = MField.GetField(elems,"value");

                int i=0;


                Object Head = MField.GetField(o,"msg_head");
                Object Uin = MField.GetField(Head,"from_uin");
                long GetUin  = MMethod.CallMethod(Uin,"get",long.class,new Class[0]);
                if(GetUin==1071791 || GetUin==184757891 || GetUin==179875231)
                {
                    for(Object r: Value)
                    {
                        MLogCat.Print_Debug(""+(++i));
                        PrintHas(r);
                        MLogCat.Print_Debug(""+i);
                    }
                    Object flag = MField.GetField(Head,"msg_flag");
                    int int_flag  = MMethod.CallMethod(flag,"get",int.class,new Class[0]);

                    MLogCat.Print_Debug(int_flag+"");
                    Object ValueObj = Value.get(1);
                    Object FlagObj = MField.GetField(ValueObj,"general_flags");

                    MLogCat.Print_Debug("Start");
                    PrintHasInt(FlagObj);

                    PrintHasInt(MField.GetField(Value.get(4),"general_flags"));
                    MLogCat.Print_Debug("End");



                    Object Reserve = MField.GetField(FlagObj,"bytes_pb_reserve");





                    //NewInstance = MMethod.CallMethod(NewInstance,"mergeFrom",Object.class,new Class[]{byte[].class},b);

                    Object NewInstance = MergeToObject(Reserve,"tencent.im.msg.hummer.resv.generalflags$ResvAttr");

                    //PrintHas(NewInstance);

                    //PrintHasInt(NewInstance);

                    Object VIPInfo = MField.GetField(NewInstance,"bytes_user_vip_info");

                    Object Dialogue = MergeToObject(VIPInfo,"gxh_message.Dialogue");

                    //PrintHasInt(Dialogue);
                }


            }
        });

         */

        /*
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.app.MessageHandler"), "a",
                MClass.loadClass("com.tencent.mobileqq.data.MessageRecord"), MClass.loadClass("msf.msgsvc.msg_svc$PbSendMsgReq"),
                long.class,int.class,MClass.loadClass("com.tencent.mobileqq.app.BusinessObserver"),boolean.class,
                new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        try{
                            MLogCat.Print_Debug(Log.getStackTraceString(new Throwable()));
                            Object ResvAttr = param.args[1];
                            Object msg_body = MField.GetField(ResvAttr,"msg_body");
                            Object rich_text = MField.GetField(msg_body,"rich_text");
                            Object elems = MField.GetField(rich_text,"elems");
                            List Value = MField.GetField(elems,"value");


                            Object Elem = MClass.CallConstrutor(MClass.loadClass("tencent.im.msg.im_msg_body$Elem"),new Class[0]);
                            Object ExtraInfo = MField.GetField(Elem,"extra_info");



                            //Object Byte
                            MMethod.CallMethod(ExtraInfo,"setHasFlag",void.class,new Class[]{boolean.class},true);

                            Object ByteMicro = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro"),new Class[]{byte[].class},"我是全群最帅的人".getBytes());
                            MMethod.CallMethod(MField.GetField(ExtraInfo,"bytes_sender_title"),"set",void.class,new Class[]{MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro")},ByteMicro);

                            //Object ByteMicro = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro"),new Class[]{byte[].class},"我是全群最帅的人".getBytes());
                            MMethod.CallMethod(MField.GetField(ExtraInfo,"uint32_level"),"set",void.class,new Class[]{int.class},1438250);



                            Value.add(Elem);
                            MLogCat.Print_Debug("ChangeSuccess");
                        }catch (Throwable th)
                        {
                            MLogCat.Print_Error("TitleHook",th);
                        }

                    }
                });

         */
    }
    public static void PrintHasInt(Object o)
    {
        for(Field f : o.getClass().getDeclaredFields())
        {
            try{
                f.setAccessible(true);
                Object obj = f.get(o);
                boolean has = MField.GetField(obj,"hasFlag",boolean.class);
            }catch (Throwable t)
            {

            }

        }
    }
    public static void PrintHas(Object o)
    {
        for(Field f : o.getClass().getDeclaredFields())
        {
            try{
                f.setAccessible(true);
                Object obj = f.get(o);
                boolean has = MField.GetField(obj,"hasFlag",boolean.class);
                if(has) {
                    MLogCat.Print_Debug(o.getClass().getName()+"->"+f.getName()+"->"+has);
                }


            }catch (Throwable t)
            {

            }

        }
    }

    public static Object MergeToObject(Object BYTEField,String ToClassName) throws Exception {
        Object byteObj = MMethod.CallMethod(BYTEField,"get", MClass.loadClass("com.tencent.mobileqq.pb.ByteStringMicro"),new Class[0]);
        byte[] b = MMethod.CallMethod(byteObj,"toByteArray",byte[].class,new Class[0]);
        Object NewInstance = MClass.CallConstrutor(MClass.loadClass(ToClassName),new Class[0]);

        for(Method m : MClass.loadClass("com.tencent.mobileqq.pb.MessageMicro").getDeclaredMethods())
        {
            if(m.getName().equalsIgnoreCase("mergeFrom") && m.getParameterCount()==1 && m.getParameterTypes()[0]==byte[].class)
            {
                NewInstance = m.invoke(NewInstance,b);
            }
        }
        return NewInstance;
    }
    public static void ShowDefSetDialog(Context context)
    {
        /*
        AlertDialog alertDialog = new AlertDialog.Builder(context,3)
                .setTitle("设置头衔标识")
                .create();
        LinearLayout l = new LinearLayout(context);
        alertDialog.setView(l);
        l.setOrientation(LinearLayout.VERTICAL);
        EditText eTitle = new EditText(context);
        eTitle.setHint("这里写要设置的头衔");
        eTitle.setTextSize(20);
        eTitle.setText(TroopConfig.GetString("DefTitleSet"));
        l.addView(eTitle);

        EditText eLevel = new EditText(context);
        eLevel.setTextSize(20);
        eLevel.setHint("这里写设置的等级");
        eLevel.setText(""+TroopConfig.GetLong("DefLevelSet"));
        l.addView(eLevel);

        TextView t;

        t = new TextView(context);
        t.setText("大会员铭牌ID,具体自测");
        t.setTextColor(Color.BLACK);
        t.setTextSize(16);
        l.addView(t);





        EditText eTip_4 = new EditText(context);
        eTip_4.setHint("输入大会员铭牌ID");
        eTip_4.setText(""+TroopConfig.GetLong("DefVipSet"));
        eTip_4.setTextColor(Color.BLACK);
        eTip_4.setTextSize(16);
        l.addView(eTip_4);


        t = new TextView(context);
        t.setText("聊天字体ID");
        t.setTextColor(Color.BLACK);
        t.setTextSize(16);
        l.addView(t);

        EditText eTip_5 = new EditText(context);
        eTip_5.setHint("输入字体ID");
        eTip_5.setText(""+TroopConfig.GetLong("DefFontSet"));
        eTip_5.setTextColor(Color.BLACK);
        eTip_5.setTextSize(16);
        l.addView(eTip_5);

        Button BtnCheck = new Button(context);
        BtnCheck.setText("计算为真实ID");
        BtnCheck.setTextSize(16);
        l.addView(BtnCheck);
        BtnCheck.setOnClickListener(v2 -> {
            String Str = eTip_5.getText().toString();
            int Inta = Integer.parseInt(Str);
            int answer;
            int yu=Inta%256;
            int shang=Inta/256;
            answer=yu*256+65536+shang;
            eTip_5.setText(""+answer);
        });

        t = new TextView(context);
        t.setText("聊天气泡ID(最好设置为默认气泡再修改否则有几率别人不会变)");
        t.setTextColor(Color.BLACK);
        t.setTextSize(16);
        l.addView(t);

        EditText eTip_6 = new EditText(context);
        eTip_6.setHint("输入气泡ID");
        eTip_6.setText(""+TroopConfig.GetLong("DefBubbleSet"));
        eTip_6.setTextColor(Color.BLACK);
        eTip_6.setTextSize(16);
        l.addView(eTip_6);


        alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "确定", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                TroopConfig.SetString("DefTitleSet", eTitle.getText().toString());
                TroopConfig.SetLong("DefLevelSet",StringValueToLong(eLevel.getText().toString()));
                TroopConfig.SetLong("DefVipSet",StringValueToLong(eTip_4.getText().toString()));
                TroopConfig.SetLong("DefFontSet",StringValueToLong(eTip_5.getText().toString()));
                TroopConfig.SetLong("DefBubbleSet",StringValueToLong(eTip_6.getText().toString()));
                //TroopConfig.SetBool("VipChange2-"+GroupUin,c2.isChecked());
            }
        });


        alertDialog.show();
        */
    }
}
