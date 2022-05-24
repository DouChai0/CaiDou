package com.ldq.connect.QQUtils;

import com.ldq.Utils.MLogCat;
import com.ldq.Utils.MMethod;

import java.util.ArrayList;
import java.util.List;

public class GuildUtils {
    public static class GuildInfo{
        public String GuildID;
        public String GuildName;
        public String Creator;
    }
    public static ArrayList<GuildInfo> GetGuildList(){
        try {
            Object IGpsServer = QQGuild_Utils.GetIGpsManager();
            List GuildList = MMethod.CallMethod(IGpsServer,"getGuildList",List.class,new Class[0]);
            ArrayList<GuildInfo> ret = new ArrayList<>();
            for(Object o : GuildList){
                GuildInfo NewInfo = new GuildInfo();
                NewInfo.GuildID = MMethod.CallMethod(o,"getGuildID",String.class,new Class[0]);
                NewInfo.GuildName = MMethod.CallMethod(o,"getGuildName",String.class,new Class[0]);
                NewInfo.Creator = MMethod.CallMethod(o,"getCreatorId",String.class,new Class[0]);

                ret.add(NewInfo);
            }
            return ret;
        } catch (Exception e) {
            return new ArrayList<>();
        }
    }
    public static class ChannelInfo{
        public String ChannelUin;
        public String ChannelName;
    }
    public static ArrayList<ChannelInfo> GetChannelInfo(String GuildID){
        try{
            Object IGpsServer = QQGuild_Utils.GetIGpsManager();
            List ChannelInfos = MMethod.CallMethod(IGpsServer,"getChannelList",List.class,new Class[]{String.class},GuildID);
            ArrayList<ChannelInfo> infoReturn = new ArrayList<>();
            for(Object ChanneItem : ChannelInfos){
                ChannelInfo NewInfoItem = new ChannelInfo();
                NewInfoItem.ChannelName = MMethod.CallMethod(ChanneItem,"getChannelName",String.class,new Class[0]);
                NewInfoItem.ChannelUin = MMethod.CallMethod(ChanneItem,"getChannelUin",String.class,new Class[0]);

                infoReturn.add(NewInfoItem);
            }
            return infoReturn;
        }catch (Exception e){
            MLogCat.Print_Error("GetChannelInfo",e);
            return new ArrayList<>();
        }
    }
    public static String GetGuildName(String GuildID){
        ArrayList<GuildInfo> infos = GetGuildList();
        for (GuildInfo info : infos){
            if (info.GuildID.equals(GuildID))return info.GuildName;
        }
        return GuildID;
    }
}
