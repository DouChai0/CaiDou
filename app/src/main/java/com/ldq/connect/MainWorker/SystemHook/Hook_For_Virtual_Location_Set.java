package com.ldq.connect.MainWorker.SystemHook;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.view.View;
import android.widget.TextView;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.GlobalConfig;
import com.ldq.connect.HookConfig.MConfig;

import java.nio.channels.FileChannel;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XposedHelpers;

public class Hook_For_Virtual_Location_Set {
    public static void Start() throws Exception{
        XposedHelpers.findAndHookMethod(MClass.loadClass("com.tencent.mobileqq.activity.QQMapActivity"), "onFetchDataSuceeded", new XC_MethodHook() {
            @Override
            protected void afterHookedMethod(MethodHookParam param) throws Throwable {
                super.afterHookedMethod(param);
                Activity act = (Activity) param.thisObject;
                int button_id = act.getResources().getIdentifier("ivTitleBtnRightText","id","com.tencent.mobileqq");
                TextView view = act.findViewById(button_id);
                view.setOnLongClickListener(v -> {
                    new AlertDialog.Builder(act,3)
                            .setTitle("选择操作项目")
                            .setItems(new String[]{"设置当前位置为虚拟位置", "重置虚拟位置"}, (dialog, which) -> {
                                try {
                                    if (which == 0){
                                        Object mapView = MField.GetField(act,"mapView");
                                        Object TencentMap = MMethod.CallMethod(mapView,"getMap",MClass.loadClass("com.tencent.tencentmap.mapsdk.maps.TencentMap"),new Class[0]);
                                        Object CameraPos = MMethod.CallMethod(TencentMap,"getCameraPosition",MClass.loadClass("com.tencent.tencentmap.mapsdk.maps.model.CameraPosition"),new Class[0]);
                                        Object LatLng = MField.GetField(CameraPos,"target");
                                        double Longitude = MMethod.CallMethod(LatLng,"getLongitude",double.class,new Class[0]);
                                        double Altitude = MMethod.CallMethod(LatLng,"getLatitude",double.class,new Class[0]);

                                        String Longi = Double.toString(Longitude);
                                        String Altit = Double.toString(Altitude);
                                        GlobalConfig.Put_String("Longitude",Longi);
                                        GlobalConfig.Put_String("Latitude",Altit);
                                    }else if (which == 1){
                                        GlobalConfig.Put_String("Longitude","");
                                        GlobalConfig.Put_String("Latitude","");
                                    }



                                } catch (Exception e) {
                                    MLogCat.Print_Error("LocationSelect",e);
                                }
                            }).show();
                    return true;
                });
            }
        });
    }
}
