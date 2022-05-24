package com.ldq.connect.MainWorker.ProxyHook;

import com.ldq.Utils.DataUtils;
import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_ExitAndKick_Log;
import com.ldq.connect.MainWorker.ProxyHandler.Handler_TroopExit_Tip;
import com.ldq.connect.QQUtils.TroopManager;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_Troop_Event_Push {
    public static void Start() throws ClassNotFoundException {
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.app.handler.receivesuccess.OnlinePushPbPushTransMsg"),
                "a",MClass.loadClass("com.tencent.mobileqq.app.MessageHandler"),
                MClass.loadClass("com.tencent.qphone.base.remote.ToServiceMsg"),
                MClass.loadClass("com.tencent.qphone.base.remote.FromServiceMsg"), new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        String CMDLine = MMethod.CallMethod(param.args[2],"getServiceCmd",String.class,new Class[0]);
                        if(!"OnlinePush.PbPushTransMsg".equalsIgnoreCase(CMDLine))return;
                        try{
                            Object Trans = MClass.CallConstrutor(MClass.loadClass("com.tencent.pb.onlinepush.OnlinePushTrans$PbMsgInfo"),
                                    new Class[0]);
                            byte[] wupBuffer = MMethod.CallMethod(param.args[2],"getWupBuffer",byte[].class,new Class[0]);
                            byte[] bArr = new byte[wupBuffer.length-4];
                            System.arraycopy(wupBuffer,4,bArr,0,bArr.length);

                            MMethod.CallMethod(Trans,"mergeFrom",MClass.loadClass("com.tencent.mobileqq.pb.MessageMicro"),
                                    new Class[]{byte[].class},bArr);

                            int Msg_Type = (int) Hook_Request_Join_Troop.DecodePBLongField(MField.GetField(Trans,"msg_type"));

                            if(Msg_Type==34)
                            {

                                byte[] arr = MField.GetField(MField.GetField(MField.GetField(Trans,"msg_data"),"value"),"bytes",byte[].class);
                                //MLogCat.Print_Debug("Online Push:"+DataUtils.bytesToHex(arr));
                                long TroopUin = DataUtils.getLongData(arr,0);
                                byte b = arr[4];
                                long UserUin = DataUtils.getLongData(arr,5);
                                byte Type = arr[9];
                                long OPUin = DataUtils.getLongData(arr,10);

                                //MLogCat.Print_Debug("TROOPUIN:"+TroopUin+"  USERUIN:"+UserUin + "   OPUIN:"+OPUin);
                                Handler_TroopExit_Tip._caller(String.valueOf(TroopUin),String.valueOf(UserUin),Type,String.valueOf(OPUin));
                                Handler_ExitAndKick_Log._caller(String.valueOf(TroopUin),String.valueOf(UserUin),Type == 2 ?"0" : String.valueOf(OPUin));

                            }
                        }catch (Throwable th)
                        {
                            MLogCat.Print_Error("DecodeOnlinePush",th);
                        }



                    }
                }
        );

        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.app.message.SystemMessageProcessor"), "a",
                int.class, MClass.loadClass("tencent.mobileim.structmsg.structmsg$StructMsg"), int.class, new XC_MethodHook() {
                    @Override
                    protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                        super.beforeHookedMethod(param);
                        Object StructMsg = param.args[1];
                        Object msg = MField.GetField(StructMsg,"msg");
                        Object troop_manager_code = MField.GetField(msg,"action_uin");
                        long Manager_Code = MField.GetField(troop_manager_code,"value",long.class);
                        Object req_uin = MField.GetField(StructMsg,"req_uin");
                        long UserUin =  MField.GetField(req_uin,"value",long.class);
                        Object group_code = MField.GetField(msg,"group_code");
                        long TroopUin = MField.GetField(group_code,"value",long.class);

                        Object msg_type = MField.GetField(StructMsg,"msg_type");
                        int msgtype = MField.GetField(msg_type,"value",int.class);

                        //MLogCat.Print_Debug("Type:"+msgtype+"  Manager:"+Manager_Code+"  Troop:"+TroopUin+"  UserUin:"+UserUin);

                        if (!TroopManager.IsInTroop(String.valueOf(TroopUin),String.valueOf(UserUin)))return;
                        if (Manager_Code > 0){
                            if (!TroopManager.IsInTroop(String.valueOf(TroopUin),String.valueOf(Manager_Code))){
                                Manager_Code = 1;
                            }
                        }

                        Handler_TroopExit_Tip._caller(String.valueOf(TroopUin),String.valueOf(UserUin),3,String.valueOf(Manager_Code));

                        Handler_ExitAndKick_Log._caller(String.valueOf(TroopUin),String.valueOf(UserUin),String.valueOf(Manager_Code));

                    }
                }
        );
    }
}
