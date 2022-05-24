package com.ldq.connect.MainWorker.SystemHook;

import android.location.Criteria;
import android.location.GpsStatus;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemClock;
import android.telephony.CellLocation;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.util.Log;

import androidx.annotation.NonNull;

import com.ldq.Utils.MLogCat;
import com.ldq.Utils.Utils;
import com.ldq.connect.HookConfig.GlobalConfig;
import com.ldq.connect.HookConfig.MConfig;
import com.ldq.connect.InitForEnvironment;

import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XC_MethodHook;
import de.robv.android.xposed.XC_MethodReplacement;
import de.robv.android.xposed.XposedBridge;
import de.robv.android.xposed.XposedHelpers;

public class Hook_Virtual_Location {

    public static Location getLocation(){
        String Longitude = GlobalConfig.Get_String("Longitude");
        String Altitude = GlobalConfig.Get_String("Latitude");
        if (TextUtils.isEmpty(Longitude) && TextUtils.isEmpty(Altitude)) return null;


        double l1 = Double.parseDouble(Longitude);
        double l2 = Double.parseDouble(Altitude);
        Location CachedLocationInfo;

        CachedLocationInfo = new Location("gps");
        CachedLocationInfo.setAccuracy(.1f);
        double[] gg = GCJ02ToWGS84(l1,l2);

        CachedLocationInfo.setLatitude(gg[0]);
        CachedLocationInfo.setAltitude(gg[1]);
        CachedLocationInfo.setTime(System.currentTimeMillis());
        CachedLocationInfo.setElapsedRealtimeNanos(SystemClock.elapsedRealtimeNanos());
        MLogCat.Print_Debug(Log.getStackTraceString(new Throwable()));
        return CachedLocationInfo;
    }
    public static void StartHook(){
        XposedHelpers.findAndHookMethod(WifiManager.class, "isWifiEnabled", XC_MethodReplacement.returnConstant(false));
        XposedHelpers.findAndHookMethod(WifiManager.class, "getWifiState", XC_MethodReplacement.returnConstant(0));
        XposedHelpers.findAndHookMethod(WifiInfo.class, "getMacAddress", XC_MethodReplacement.returnConstant("00-00-00-00-00-00-00-00"));
        XposedHelpers.findAndHookMethod(WifiInfo.class, "getSSID", XC_MethodReplacement.returnConstant((Object) null));
        XposedHelpers.findAndHookMethod(WifiInfo.class, "getBSSID", XC_MethodReplacement.returnConstant("00-00-00-00-00-00-00-00"));
        XposedHelpers.findAndHookMethod(LocationManager.class, "getBestProvider", Criteria.class, Boolean.TYPE, XC_MethodReplacement.returnConstant("gps"));
        XposedHelpers.findAndHookMethod(WifiManager.class, "getWifiState", XC_MethodReplacement.returnConstant(1));
        XposedHelpers.findAndHookMethod(WifiManager.class, "isWifiEnabled", XC_MethodReplacement.returnConstant(true));
        XposedHelpers.findAndHookMethod(LocationManager.class, "addNmeaListener", GpsStatus.NmeaListener.class, XC_MethodReplacement.returnConstant(false));
        XposedHelpers.findAndHookMethod(TelephonyManager.class, "getCellLocation", XC_MethodReplacement.returnConstant((Object) null));
        XposedHelpers.findAndHookMethod(PhoneStateListener.class, "onCellLocationChanged", CellLocation.class, XC_MethodReplacement.returnConstant((Object) null));
        XposedHelpers.findAndHookMethod(TelephonyManager.class, "getNeighboringCellInfo", XC_MethodReplacement.returnConstant(new ArrayList()));
        XposedHelpers.findAndHookMethod(WifiManager.class, "getScanResults", XC_MethodReplacement.returnConstant(new ArrayList()));

        XposedHelpers.findAndHookMethod(LocationManager.class, "getLastKnownLocation", String.class, new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Location location = getLocation();
                if (location != null) param.setResult(location);
            }
        });

        XposedBridge.hookAllMethods(LocationManager.class, "requestSingleUpdate", new XC_MethodHook() {
            /* class com.bug.zqq.LocationHook.AnonymousClass4 */

            public void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                final Location locationRR = getLocation();
                if (locationRR != null) {
                    Object[] objArr = methodHookParam.args;
                    int i = 0;
                    for (Object obj : objArr) {
                        if (obj instanceof LocationListener) {
                            final LocationListener locationListener = (LocationListener) obj;
                            locationListener.onLocationChanged(locationRR);

                            methodHookParam.args[i] = new LocationListener() {
                                /* class com.bug.zqq.LocationHook.AnonymousClass4.AnonymousClass1 */

                                public void onLocationChanged(Location location) {
                                    locationListener.onLocationChanged(locationRR);
                                }

                                public void onStatusChanged(String str, int i, Bundle bundle) {
                                    locationListener.onStatusChanged(str, i, bundle);
                                }

                                public void onProviderEnabled(String str) {
                                    locationListener.onProviderEnabled(str);
                                }

                                public void onProviderDisabled(String str) {
                                    locationListener.onProviderDisabled(str);
                                }
                            };
                        }
                        i++;
                    }
                }
            }
        });


        XposedBridge.hookAllMethods(LocationManager.class, "requestLocationUpdates", new XC_MethodHook() {
            @Override
            protected void beforeHookedMethod(MethodHookParam param) throws Throwable {
                super.beforeHookedMethod(param);
                Location locationaa = getLocation();
                if(locationaa!=null){
                    for(int i=0;i<param.args.length;i++){
                        if( param.args[i] instanceof LocationListener){
                            LocationListener listener = (LocationListener) param.args[i];
                            listener.onLocationChanged(locationaa);
                            MLogCat.Print_Debug("Replace Success");
                            param.args[i] = new LocationListener() {
                                @Override
                                public void onLocationChanged(@NonNull Location location) {
                                    listener.onLocationChanged(locationaa);
                                }

                                @Override
                                public void onStatusChanged(String provider, int status, Bundle extras) {
                                    listener.onStatusChanged(provider, status, extras);
                                }

                                @Override
                                public void onProviderEnabled(@NonNull String provider) {
                                    listener.onProviderEnabled(provider);
                                }

                                @Override
                                public void onProviderDisabled(@NonNull String provider) {
                                    listener.onProviderDisabled(provider);
                                }
                            };
                        }


                    }
                }
            }
        });

        XposedBridge.hookAllMethods(LocationManager.class, "getProviders", new XC_MethodHook() {
            public void beforeHookedMethod(XC_MethodHook.MethodHookParam methodHookParam) throws Throwable {
                ArrayList arrayList = new ArrayList();
                arrayList.add("gps");
                methodHookParam.setResult(arrayList);
            }
        });

        XposedHelpers.findAndHookMethod(PhoneStateListener.class, "onCellInfoChanged", List.class, new XC_MethodHook() {
            /* class com.bug.zqq.LocationHook.AnonymousClass6 */

            public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                methodHookParam.args[0] = new ArrayList();
            }
        });
        XposedHelpers.findAndHookMethod(LocationManager.class, "addGpsStatusListener", GpsStatus.Listener.class, new XC_MethodHook() {
            public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                methodHookParam.setResult( null);
                if (methodHookParam.args[0] != null) {
                    XposedHelpers.callMethod(methodHookParam.args[0], "onGpsStatusChanged", 1);
                    XposedHelpers.callMethod(methodHookParam.args[0], "onGpsStatusChanged", 3);
                }
            }
        });

        XposedHelpers.findAndHookMethod(LocationManager.class, "getLastLocation", new XC_MethodHook() {
            /* class com.bug.zqq.LocationHook.AnonymousClass2 */

            public void beforeHookedMethod(MethodHookParam methodHookParam) throws Throwable {
                Location location = getLocation();
                if (location != null) {
                    methodHookParam.setResult(location);
                }
            }
        });





    }
    public static double[] GCJ02ToWGS84(double lon, double lat) {
        double d = 0.0000001;
        double longitude = lon;
        double latitude = lat;
        double[] transform;

        do {
            transform = EvilTransform.transform(latitude, longitude);
            latitude += lat - transform[0];
            longitude += lon - transform[1];
        } while (lon - transform[1] > d || lat - transform[0] > d);

        return new double[]{longitude, latitude};
    }

    static class EvilTransform {
        private final static double pi = 3.1415926535897932384626;
        private final static double a = 6378245.0;
        private final static double ee = 0.00669342162296594323;

        // World Geodetic System ==> Mars Geodetic System
        public static double[] transform(double wgLat, double wgLon) {
            double mgLat = 0;
            double mgLon = 0;
            if (outOfChina(wgLat, wgLon)) {
                mgLat = wgLat;
                mgLon = wgLon;
            } else {
                double dLat = transformLat(wgLon - 105.0, wgLat - 35.0);
                double dLon = transformLon(wgLon - 105.0, wgLat - 35.0);
                double radLat = wgLat / 180.0 * pi;
                double magic = Math.sin(radLat);
                magic = 1 - ee * magic * magic;
                double sqrtMagic = Math.sqrt(magic);
                dLat = (dLat * 180.0) / ((a * (1 - ee)) / (magic * sqrtMagic) * pi);
                dLon = (dLon * 180.0) / (a / sqrtMagic * Math.cos(radLat) * pi);
                mgLat = wgLat + dLat;
                mgLon = wgLon + dLon;
            }
            double[] point = {mgLat, mgLon};
            return point;
        }

        private static boolean outOfChina(double lat, double lon) {
            return !inChina(lat, lon);
        }

        static String a1 = "0000000000000000000000000000000000000" +
                "000000000000000000000000000000000000000001000000000" +
                 "00001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001010011100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111100110001000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001110000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111110000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000011111000111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001111110011000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001110000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001111000000000111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111100000000000010111110100000011000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111110000000001111111111111111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111111111000000111111111111111110000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000011111111111101111111111111111111111100000000000000000000000000000000000000000000000000000000000000000000000000000000000000011111111111111111111111111111111111111000000000000000000000000000000000000000000000000000000000000000000000000000000000000101111111111111111111111111111111111111110000000000000000000000000000000000000000000000000000000000000000000000000000000000011111111111111111111111111111111111111111100000000000000000000000000000000000000000000000000000000000000000000000000000000001111111111111111111111111111111111111111111100000000000000000000000000000000000000000000000000000000000000000000000000000000111111111111111111111111111111111111111111111000000000000000000000000000000000000000000000000000000000000000000000000000000001111111111111111111111111111111111111111111100000000000000000000000000000000000000000000000000000000000000000000000000000000011111111111111111111111111111111111111111111100000000000000000000000000000000000000000000000000000000000000000011110000000001111111111111111111111111111111111111111111110000000000000000000000000000000000000000000000000000000000011000011111100000000111111111111111111111111111111111111111111111100000000000000000000000000000000000000000000000000001111111100111111111100110111111111111111111111111111111111111111111111110000000000000000000000000000000000000000000000000001111111111111111111111111111111111111111111111111111111111111111111111111100000000000000000000000000000000000000000000000011111111111111111111111111111111111111111111111111111111111111111111111111111" +
                  "1111111111111111111111111000000000000000000000000011111111100000000000000000000000000000000000000000000000000011111111111111111111111111111110001111100000000000000000000000000111110000000000000000000000000000000000000000000000000000001111111111111111111111111111111000000000000000000000000000000000001110000000000000000000000000000000000000000000000000000000011111111111111111111111111111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001111111111111111111111111100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111111111111111111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000011111111111111111000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000111111111111111100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000011111111111111100000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000011111111111110000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000011111111111110000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000001111111111110000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000010110000000000000000000000";


        private static boolean inChina(double lat, double lon) {
            try {
                int var4 = (int) ((lon - 73.0D) / 0.5D);
                int var5 = (int) ((lat - 3.5D) / 0.5D);
                if (var5 >= 0 && var5 < 101 && var4 >= 0 && var4 < 124) {
                    int var6 = 124 * var5 + var4;
                    char var7 = (a1+ InitForEnvironment.a2 +InitForEnvironment.a3).charAt(var6);

                    return var7 == '1';
                } else {
                    return false;
                }
            } catch (Throwable var8) {
                var8.printStackTrace();
                return true;
            }
        }

        private static double transformLat(double x, double y) {
            double ret = -100.0 + 2.0 * x + 3.0 * y + 0.2 * y * y + 0.1 * x * y + 0.2 * Math.sqrt(Math.abs(x));
            ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
            ret += (20.0 * Math.sin(y * pi) + 40.0 * Math.sin(y / 3.0 * pi)) * 2.0 / 3.0;
            ret += (160.0 * Math.sin(y / 12.0 * pi) + 320 * Math.sin(y * pi / 30.0)) * 2.0 / 3.0;
            return ret;
        }

        private static double transformLon(double x, double y) {
            double ret = 300.0 + x + 2.0 * y + 0.1 * x * x + 0.1 * x * y + 0.1 * Math.sqrt(Math.abs(x));
            ret += (20.0 * Math.sin(6.0 * x * pi) + 20.0 * Math.sin(2.0 * x * pi)) * 2.0 / 3.0;
            ret += (20.0 * Math.sin(x * pi) + 40.0 * Math.sin(x / 3.0 * pi)) * 2.0 / 3.0;
            ret += (150.0 * Math.sin(x / 12.0 * pi) + 300.0 * Math.sin(x / 30.0 * pi)) * 2.0 / 3.0;
            return ret;
        }
    }
}
