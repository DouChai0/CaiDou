package com.ldq.connect.MainWorker.WidgetHook;

import android.content.Context;
import android.view.View;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.FloatWindow.EmoHelper;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_Guild_Msg_Long_Click {
    public static void Start(){
        try{
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.guild.chatpie.msgviewbuild.builder.BaseGuildMsgViewBuild"), "a",
                    MClass.loadClass("com.tencent.mobileqq.utils.dialogutils.QQCustomMenu"),
                    MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"), View.class,
                    new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            Object MenuItem = param.args[0];
                            Context ctx = Utils.GetThreadActivity();
                            int FaceID = ctx.getResources().getIdentifier("forward_dialog_new_edit_emoji", "drawable", ctx.getPackageName());
                            if(param.args[1].getClass().getName().contains("MessageForPic")){

                                MMethod.CallMethod(MenuItem,"a",void.class,new Class[]{
                                        int.class,String.class,int.class
                                },1596,"分类保存", FaceID);
                            }
                        }
                    }
            );
            XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.guild.chatpie.msgviewbuild.builder.GuildPicBuilder"), "b",
                    int.class, Context.class, MClass.loadClass("com.tencent.mobileqq.data.ChatMessage"), new XC_MethodHook() {
                        @Override
                        protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                            super.beforeHookedMethod(param);
                            int MenuID = (int) param.args[0];
                            if(MenuID == 1596){
                                param.setResult(true);
                                EmoHelper.ShowSavePICDialog("","http://gchat.qpic.cn/gchatpic_new/0/0-0-"+ MField.GetField(param.args[2],"md5",String.class)+"/0?term=2",
                                        MField.GetField(param.args[2],"md5",String.class)
                                );
                            }
                        }
                    }
            );


        }catch (Throwable th){

        }

    }
}
