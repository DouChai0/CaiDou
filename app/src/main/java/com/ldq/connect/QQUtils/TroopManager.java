package com.ldq.connect.QQUtils;

import android.text.TextUtils;
import android.util.Log;

import com.ldq.Utils.MClass;
import com.ldq.Utils.MField;
import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;
import com.ldq.connect.JavaPlugin.JavaPluginUtils;
import com.ldq.connect.MHookEnvironment;

import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.ArrayList;
import java.util.List;

import de.robv.android.xposed.XposedHelpers;

public class TroopManager {
    public static String GetMemberName(String Groupuin,String UserUin) {
        try {
            if(MHookEnvironment.AppInterface==null)return UserUin;
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.utils.ContactUtils","b",String.class,new Class[]{
                    MClass.loadClass("com.tencent.common.app.AppInterface"),
                    String.class,
                    String.class
            });
            String mSStr = (String) CallMethod.invoke(null, MHookEnvironment.AppInterface,Groupuin,UserUin);
            return mSStr;
        }catch (Throwable e)
        {
            MLogCat.Print_Error("GetTroopMemberName", Log.getStackTraceString(e));
            return "";
        }
    }
    public static String GetMemberTrueName(String GroupUin,String UserUin)
    {
        try{
            ArrayList<JavaPluginUtils.GroupMemberInfo> info = JavaPluginUtils.GetGroupMemberList(GroupUin);
            for(JavaPluginUtils.GroupMemberInfo user : info)
            {
                if(user.UserUin.equals(UserUin))
                {
                    return user.UserName;
                }
            }
            return "";
        }catch (Exception ex)
        {
            return "";
        }
    }
    public static void Group_Kick(String GroupUin,String UserUin,boolean isBan) {
        try{
            Object ManagerObject = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler"),
                    new Class[]{MClass.loadClass("com.tencent.common.app.AppInterface")},MHookEnvironment.AppInterface
            );
            ArrayList KickList = new ArrayList();
            KickList.add(Long.parseLong(UserUin));
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler","a",void.class,new Class[]{
                    long.class,
                    List.class,
                    boolean.class,
                    boolean.class
            });
            CallMethod.invoke(ManagerObject,
                    Long.parseLong(GroupUin),KickList,isBan,false
            );
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("KickError",th);
        }
    }
    public static void Group_Kick(String GroupUin,String[] UserUin,boolean isBan) {
        try{
            Object ManagerObject = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler"),
                    new Class[]{MClass.loadClass("com.tencent.common.app.AppInterface")},MHookEnvironment.AppInterface
            );
            ArrayList KickList = new ArrayList();
            for(String Uin :UserUin)KickList.add(Long.parseLong(Uin));
            Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.TroopMemberMngHandler","a",void.class,new Class[]{
                    long.class,
                    List.class,
                    boolean.class,
                    boolean.class
            });
            CallMethod.invoke(ManagerObject,
                    Long.parseLong(GroupUin),KickList,isBan,false
            );
        }
        catch (Throwable th)
        {
            MLogCat.Print_Error("KickError",th);
        }
    }

    public static void Group_ChangeName(String GroupUin,String UserUin,String ChangeName) throws Exception {
        Object mCallObj = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.troop.handler.TroopMemberCardHandler"),new Class[]{MClass.loadClass("com.tencent.common.app.AppInterface")},MHookEnvironment.AppInterface);
        Object TroopCardObj = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.data.troop.TroopMemberCardInfo"),new Class[0],new Object[0]);
        MField.SetField(TroopCardObj,TroopCardObj.getClass(),"name",ChangeName);
        MField.SetField(TroopCardObj,TroopCardObj.getClass(),"troopuin",GroupUin);

        MField.SetField(TroopCardObj,TroopCardObj.getClass(),"memberuin",UserUin);
        MField.SetField(TroopCardObj,TroopCardObj.getClass(),"email","");
        MField.SetField(TroopCardObj,TroopCardObj.getClass(),"memo","");
        MField.SetField(TroopCardObj,TroopCardObj.getClass(),"tel","");
        ArrayList mList = new ArrayList();
        ArrayList mList2 = new ArrayList();
        mList.add(TroopCardObj);
        mList2.add(1);
        Method CallMethod = MMethod.FindMethod("com.tencent.mobileqq.troop.handler.TroopMemberCardHandler","a",void.class,new Class[]{
                String.class,
                ArrayList.class,
                ArrayList.class
        });
        CallMethod.invoke(mCallObj,GroupUin,mList,mList2);
    }
    public static void Group_Change_Title(String GroupUin,String UserUin,String mTitle) throws Exception {
        Object mProxy = Proxy.newProxyInstance(MHookEnvironment.mLoader, new Class[]{MClass.loadClass("mqq.observer.BusinessObserver")}, (proxy, method, args) -> null);
        MMethod.CallMethod(null,MClass.loadClass("com.tencent.biz.troop.EditUniqueTitleActivity"),"a",void.class,new Class[]{
                        MHookEnvironment.AppInterface.getClass(),String.class,String.class,String.class,MClass.loadClass("mqq.observer.BusinessObserver")
                },
                new Object[]{
                        MHookEnvironment.AppInterface,GroupUin,UserUin,mTitle,mProxy
                }
        );
    }
    public static void Group_Forbidden(String GroupUin, String UserUin, int time){
        try{
            if(MHookEnvironment.AppInterface==null)MHookEnvironment.AppInterface = BaseInfo.GetAppInterface();
            Object TroopGagManager = MMethod.CallMethod(MHookEnvironment.AppInterface,MHookEnvironment.AppInterface.getClass(),"getManager",
                    XposedHelpers.findClass("mqq.manager.Manager",MHookEnvironment.mLoader),
                    new Class[]{int.class},
                    new Object[]{MField.GetField(null,MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"TROOP_GAG_MANAGER",1)});
            if(UserUin.isEmpty())
            {
                Group_Forbidden_All(GroupUin,time!=0);
            }
            else
            {
                MMethod.CallMethod(TroopGagManager,TroopGagManager.getClass(),"a",boolean.class,new Class[]{String.class,String.class,long.class},new Object[]{
                        GroupUin,UserUin,time
                });
            }
        }catch (Exception ex)
        {
            MLogCat.Print_Error("TroopGag",ex);
        }

    }
    public static void Group_Forbidden_All(String Group,boolean isban) throws Exception {
        if(Group.contains("&")){
            String[] GuildCut = Group.split("&");

            QQGuild_Manager.Guild_ForbiddenAll(GuildCut[0],isban ? 3600 * 24 * 30 : 0);
            return;
        }
        Object TroopGagManager = MMethod.CallMethod(MHookEnvironment.AppInterface,MHookEnvironment.AppInterface.getClass(),"getBusinessHandler",
                XposedHelpers.findClass("com.tencent.mobileqq.app.BusinessHandler",MHookEnvironment.mLoader),
                new Class[]{String.class},
                new Object[]{MField.GetField(null,MClass.loadClass("com.tencent.mobileqq.app.BusinessHandlerFactory"),"TROOP_GAG_HANDLER",1)});
        MMethod.CallMethod(TroopGagManager,TroopGagManager.getClass(),"a",void.class,new Class[]{String.class,long.class},new Object[]{
                Group,isban ? 268435455 : 0
        });
    }
    public static boolean IsGroupAdmin(String GroupUin) throws Exception {
        Object TroopGagManager = MMethod.CallMethod(MHookEnvironment.AppInterface,MHookEnvironment.AppInterface.getClass(),"getManager",
                XposedHelpers.findClass("mqq.manager.Manager",MHookEnvironment.mLoader),
                new Class[]{int.class},
                new Object[]{MField.GetField(null,MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"TROOP_GAG_MANAGER",1)});
        return MMethod.CallMethod(TroopGagManager,TroopGagManager.getClass(),"a|c",boolean.class,new Class[]{String.class},new Object[]{
                GroupUin
        });
    }
    public static boolean IsGroupOwner(String GroupUin) throws Exception {
        Object TroopGagManager = MMethod.CallMethod(MHookEnvironment.AppInterface,MHookEnvironment.AppInterface.getClass(),"getManager",
                XposedHelpers.findClass("mqq.manager.Manager",MHookEnvironment.mLoader),
                new Class[]{int.class},
                new Object[]{MField.GetField(null,MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"TROOP_GAG_MANAGER",1)});
        return MMethod.CallMethod(TroopGagManager,TroopGagManager.getClass(),"b|d",boolean.class,new Class[]{String.class},new Object[]{
                GroupUin
        });
    }
    public static boolean IsGroupAdmin(String GroupUin,String UserUin) throws Exception {
        Object TroopManager = MMethod.CallMethod(MHookEnvironment.AppInterface, MHookEnvironment.AppInterface.getClass(), "getManager",
                XposedHelpers.findClass("mqq.manager.Manager", MHookEnvironment.mLoader),
                new Class[]{int.class},
                new Object[]{MField.GetField(null, MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"), "TROOP_MANAGER", 1)});
        Object TroopInfo = MMethod.CallMethod(TroopManager, "b|g", MClass.loadClass("com.tencent.mobileqq.data.troop.TroopInfo"),
                new Class[]{String.class}, GroupUin
        );
        String mAdmin = MField.GetField(TroopInfo, TroopInfo.getClass(), "Administrator", String.class);
        if(!TextUtils.isEmpty(mAdmin))
        {
            String[] mStr =mAdmin.split("\\|");
            for(String ThAdminUin : mStr)
            {
                if(ThAdminUin.equalsIgnoreCase(UserUin))
                {
                    return true;
                }
            }
        }
        String Owner = MField.GetField(TroopInfo, TroopInfo.getClass(), "troopowneruin", String.class);
        if(UserUin.equalsIgnoreCase(Owner))return true;

        return false;
    }
    public static ArrayList Group_GetList() {
        try{
            Object TroopManager = MMethod.CallMethod(MHookEnvironment.AppInterface,MHookEnvironment.AppInterface.getClass(),"getManager",
                    MClass.loadClass("mqq.manager.Manager"),
                    new Class[]{int.class},
                    new Object[]{MField.GetField(null,MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"TROOP_MANAGER",int.class)}
            );
            ArrayList MyList = MMethod.CallMethod(TroopManager,TroopManager.getClass(),"a|g", ArrayList.class,new Class[0],new Object[0]);
            return MyList;
        }
        catch (Throwable e)
        {
            MLogCat.Print_Error("GetGroupList",e);
            return null;
        }
    }
    public static boolean IsInTroop(String TroopUin,String UserUin){
        try{
            ArrayList<JavaPluginUtils.GroupMemberInfo> infos = JavaPluginUtils.GetGroupMemberList(TroopUin);
            for (JavaPluginUtils.GroupMemberInfo info : infos){
                if (info.UserUin.equals(UserUin))return true;
            }
        }catch (Exception e){
        }

        return false;
    }
    public static ArrayList Group_GetUserList(String GroupUin) {
        try{
            Object TroopManager = MMethod.CallMethod(MHookEnvironment.AppInterface,MHookEnvironment.AppInterface.getClass(),"getManager",
                    MClass.loadClass("mqq.manager.Manager"),
                    new Class[]{int.class},
                    new Object[]{MField.GetField(null,MClass.loadClass("com.tencent.mobileqq.app.QQManagerFactory"),"TROOP_MANAGER",int.class)}
            );
            ArrayList MyList = MMethod.CallMethod(TroopManager,TroopManager.getClass(),"b|w", List.class,new Class[]{String.class},new Object[]{GroupUin});
            return MyList;
        }
        catch (Throwable e)
        {
            MLogCat.Print_Error("GetGroupMemberList",e);
            return null;
        }
    }
    public static String Group_GetUserTitle(String GroupUin,String UserUin){
        try{
            ArrayList mUserList = Group_GetUserList(GroupUin);
            for(Object Item : mUserList){
                String Uin = MField.GetField(Item, "memberuin",String.class);
                if(UserUin.equals(Uin))return MField.GetField(Item,"mUniqueTitle",String.class);
            }
            return "";
        }catch (Exception e){
            MLogCat.Print_Error("GetGroupMemberTitle",e);
            return "";
        }

    }
    public static String GetTroopName(String GroupUin) {
        try{
            String mStr = MMethod.CallMethod(null,MClass.loadClass("com.tencent.mobileqq.utils.ContactUtils"),
                    "a",new Class[]{
                            MClass.loadClass("com.tencent.common.app.AppInterface"),
                            String.class,
                            boolean.class
                    },MHookEnvironment.AppInterface,GroupUin,true
            );
            return mStr;
        }catch (Exception ex)
        {
            return GroupUin;
        }

    }
    public static ArrayList GetForbiddenList(String GroupUin) {
        try {
            Object Manager = MClass.CallConstrutor(MClass.loadClass("com.tencent.mobileqq.troop.utils.TroopGagMgr"),new Class[]{MHookEnvironment.AppInterface.getClass()},MHookEnvironment.AppInterface);
            ArrayList TheList = MMethod.CallMethod(Manager,"a|b",ArrayList.class,new Class[]{String.class,boolean.class},GroupUin,true);

            return TheList==null? new ArrayList():TheList;
        } catch (Exception e) {
            MLogCat.Print_Error("GetTroopGagList",e);
            return new ArrayList();
        }
    }





}
