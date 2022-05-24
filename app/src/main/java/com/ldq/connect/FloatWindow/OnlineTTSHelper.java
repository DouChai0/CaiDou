package com.ldq.connect.FloatWindow;

import android.text.TextUtils;

import com.ldq.Utils.AES;
import com.ldq.Utils.DataUtils;
import com.ldq.Utils.HttpUtils;
import com.ldq.Utils.MLogCat;

import org.json.JSONObject;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

public class OnlineTTSHelper {
    public final static String SpeakerMaps = "{\"data\":[{\"name\":\"阳哥（野哥）\",\"style\":\"稳重磁性、诙谐幽默\",\"id\":\"65270\",\"desc\":\"杠精配音、短视频、娱乐\"},{\"name\":\"情感-天明\",\"style\":\"亲切温和、自然流畅\",\"id\":\"20079\",\"desc\":\"声音可随文本情感变化而改变\"},{\"name\":\"小俊\",\"style\":\"激情力度、年轻时尚\",\"id\":\"65070\",\"desc\":\"广告促销\"},{\"name\":\"宝哥\",\"style\":\"明星模仿、诙谐幽默\",\"id\":\"65230\",\"desc\":\"短视频、娱乐\"},{\"name\":\"一菲\",\"style\":\"温柔甜美、自然流畅\",\"id\":\"20073\",\"desc\":\"游戏/影视解说、教育培训、有声阅读、情感文章、旁白介绍、客服彩铃\"},{\"name\":\"小鹏\",\"style\":\"稳重磁性、自然流畅\",\"id\":\"20082\",\"desc\":\"短视频解说、有声阅读、专题宣传\"},{\"name\":\"管哥\",\"style\":\"稳重磁性、自然流畅\",\"id\":\"20081\",\"desc\":\"经管类文章、旁白介绍、广播通知、解说等\"},{\"name\":\"天哥\",\"style\":\"大气浑厚、稳重磁性\",\"id\":\"20078\",\"desc\":\"有声阅读、情感文章、游戏/影视解说\"},{\"name\":\"宣哥\",\"style\":\"大气浑厚、稳重磁性\",\"id\":\"20067\",\"desc\":\"专题宣传、教育培训、旁白\"},{\"name\":\"小露\",\"style\":\"温柔甜美、自然流畅\",\"id\":\"20070\",\"desc\":\"旁白解说、教育培训、情感文章、客服彩铃\"},{\"name\":\"小媛\",\"style\":\"亲切温和、自然流畅\",\"id\":\"60100\",\"desc\":\"中文&英语\"},{\"name\":\"一峰\",\"style\":\"稳重磁性、自然流畅\",\"id\":\"20072\",\"desc\":\"新闻播报、广播通知、专题宣传、教育培训、旁白介绍\"},{\"name\":\"超哥\",\"style\":\"大气浑厚、自然流畅\",\"id\":\"20051\",\"desc\":\"新闻播报、纪录片、广告、有声内容\"},{\"name\":\"千雪\",\"style\":\"亲切温和、自然流畅\",\"id\":\"20068\",\"desc\":\"旁白解说、教育培训、有声阅读、\"},{\"name\":\"水哥\",\"style\":\"稳重磁性、自然流畅\",\"id\":\"20071\",\"desc\":\"短视频、游戏/影视解说、有声阅读\"},{\"name\":\"小晚\",\"style\":\"亲切温和、自然流畅、轻声耳语\",\"id\":\"20069\",\"desc\":\"短视频、有声阅读、情感文章\"},{\"name\":\"百合仙子\",\"style\":\"成熟知性、亲切温和\",\"id\":\"62060\",\"desc\":\"课件、新闻、有声文章\"},{\"name\":\"小晨\",\"style\":\"温柔甜美、年轻时尚\",\"id\":\"20054\",\"desc\":\"短视频解说、广告、专题、语音客服\"},{\"name\":\"小果\",\"style\":\"成熟知性、自然流畅\",\"id\":\"20065\",\"desc\":\"新闻播报、公众号、课件讲解\"},{\"name\":\"小英\",\"style\":\"成熟知性、亲切温和\",\"id\":\"65040\",\"desc\":\"课件、新闻、有声文章\"},{\"name\":\"小洋\",\"style\":\"大气浑厚、稳重磁性\",\"id\":\"65010\",\"desc\":\"专题、彩铃、广告促销\"},{\"name\":\"小雅\",\"style\":\"亲切温和、自然流畅\",\"id\":\"20062\",\"desc\":\"情感文章、语音客服、有声阅读\"},{\"name\":\"飞碟哥\",\"style\":\"饱满活泼、诙谐幽默\",\"id\":\"20061\",\"desc\":\"短视频、游戏解说\"},{\"name\":\"Lindsay\",\"style\":\"温柔甜美、亲切温和\",\"id\":\"20066\",\"desc\":\"语音播报、课件、新闻\"},{\"name\":\"七哥\",\"style\":\"自然流畅、年轻时尚\",\"id\":\"20053\",\"desc\":\"短视频、旁白解说、课件播报\"},{\"name\":\"程程\",\"style\":\"激情力度、年轻时尚\",\"id\":\"65080\",\"desc\":\"广告促销\"},{\"name\":\"小师\",\"style\":\"温柔甜美、自然流畅\",\"id\":\"20055\",\"desc\":\"交互客服、短视频解说\"},{\"name\":\"小燕\",\"style\":\"亲切温和\",\"id\":\"60020\",\"desc\":\"广告促销、专题、彩铃\"},{\"name\":\"刚哥\",\"style\":\"稳重磁性、亲切温和\",\"id\":\"20052\",\"desc\":\"专题宣传、纪录片、有声内容\"},{\"name\":\"小薛\",\"style\":\"年轻时尚、饱满活泼\",\"id\":\"65320\",\"desc\":\"广告、有声文章\"},{\"name\":\"小光\",\"style\":\"自然流畅、年轻时尚\",\"id\":\"65110\",\"desc\":\"广告、专题、课件\"},{\"name\":\"小桃丸\",\"style\":\"可爱甜美、明星模仿\",\"id\":\"60120\",\"desc\":\"声似明星、童声、阅读\"},{\"name\":\"小芳\",\"style\":\"可爱甜美、自然流畅\",\"id\":\"62020\",\"desc\":\"儿童读物、课件\"},{\"name\":\"楠楠\",\"style\":\"呆萌可爱、自然流畅\",\"id\":\"60130\",\"desc\":\"童声、阅读\"},{\"name\":\"韦香主\",\"style\":\"稳重磁性、亲切温和\",\"id\":\"62070\",\"desc\":\"专题、广告、有声文章\"},{\"name\":\"萌小新\",\"style\":\"呆萌可爱、明星模仿\",\"id\":\"60170\",\"desc\":\"声似明星、童声、阅读\"},{\"name\":\"玲姐姐\",\"style\":\"温柔甜美、明星模仿\",\"id\":\"60140\",\"desc\":\"短视频、娱乐\"},{\"name\":\"小南\",\"style\":\"亲切温和\",\"id\":\"65340\",\"desc\":\"课件、新闻、有声文章\"},{\"name\":\"小峰\",\"style\":\"稳重磁性\",\"id\":\"60030\",\"desc\":\"新闻、课件\"},{\"name\":\"瑶瑶\",\"style\":\"温柔甜美、年轻时尚\",\"id\":\"65360\",\"desc\":\"专题、广告、有声文章\"},{\"name\":\"小宇\",\"style\":\"稳重磁性\",\"id\":\"15675\",\"desc\":\"广告、专题、课件\"},{\"name\":\"老马\",\"style\":\"稳重磁性、亲切温和\",\"id\":\"60150\",\"desc\":\"专题、纪录片\"},{\"name\":\"小华\",\"style\":\"稳重磁性\",\"id\":\"62010\",\"desc\":\"广告、专题、课件\"},{\"name\":\"John\",\"style\":\"大气浑厚、稳重磁性\",\"id\":\"69010\",\"desc\":\"(英文语种)课件、阅读\"},{\"name\":\"Catherine\",\"style\":\"成熟知性、亲切温和\",\"id\":\"69020\",\"desc\":\"（英文语种）英文课件、阅读\"},{\"name\":\"Steve\",\"style\":\"稳重磁性\",\"id\":\"69030\",\"desc\":\"(英文语种)课件\"},{\"name\":\"陕西小莹\",\"style\":\"亲切温和、淳朴方言\",\"id\":\"68080\",\"desc\":\"陕西方言\"},{\"name\":\"台湾玉儿\",\"style\":\"温柔甜美、淳朴方言\",\"id\":\"68120\",\"desc\":\"台湾方言\"},{\"name\":\"东北晓倩\",\"style\":\"亲切温和、淳朴方言\",\"id\":\"68040\",\"desc\":\"东北方言\"},{\"name\":\"山东小东\",\"style\":\"亲切温和、淳朴方言\",\"id\":\"20074\",\"desc\":\"山东方言\"}]}";
    private final static String JSONReplace = "{\"req\":\"88888888\"}";
    public static String GetVoiceDownloadLink(int VoiceID,String TTSText)
    {
        try{
            String PostData = "";
            JSONObject mJson = new JSONObject();
            mJson.put("channel","40000001");
            mJson.put("synth_text_hash_code",Get_Enc_HashCode(TTSText));
            PostData = mJson.toString();
            PostData = JSONReplace.replace("88888888",Encode(PostData));


            String SignReturn = HttpUtils.PostDataPY("http://www.peiyinge.com/web-server/1.0/works_synth_sign",PostData.getBytes());

            if(!TextUtils.isEmpty(SignReturn))
            {
                JSONObject mReturnJSON = new JSONObject(SignReturn);
                String body = mReturnJSON.getString("body");
                body=Decode(body);
                mReturnJSON = new JSONObject(body);
                String TimeStamp = mReturnJSON.getString("time_stamp");
                String Sign = mReturnJSON.getString("sign_text");

                String URL = "http://proxyweb.peiyinge.com/synth?uid=&ts="
                        +TimeStamp
                        +"&sign="+Sign
                        +"&vid="+VoiceID
                        +"&f=v2&cc=0000&sid=12345678123456781234567812345678&volume=0&speed=0&content="
                        + URLEncoder.encode(TTSText)
                        +"&listen=0";
                return URL;

            }
            return "";


        }catch (Exception ex)
        {
            MLogCat.Print_Error("GetOnlingTTSError",ex);
            return "";
        }


    }
    private static String Decode(String SignText)
    {
        try {
            return AES.Decrypt(SignText,"G%.g7\"Y&Nf^40Ee<");
        } catch (Exception e) {
            e.printStackTrace();
            return "Error:\n"+e;
        }
    }
    private static String Encode(String SignText)
    {
        try {
            return AES.Encrypt(SignText,"G%.g7\"Y&Nf^40Ee<");
        } catch (Exception e) {
            e.printStackTrace();
            return "Error:\n"+e;
        }
    }
    private static String Get_Enc_HashCode(String Text)
    {
        return DataUtils.getDataMD5(Text.getBytes(StandardCharsets.UTF_8)).toLowerCase();
    }
}
