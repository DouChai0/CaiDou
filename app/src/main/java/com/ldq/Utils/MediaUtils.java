package com.ldq.Utils;

import android.media.MediaCodec;
import android.media.MediaExtractor;
import android.media.MediaFormat;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.nio.ByteBuffer;

public class MediaUtils {
    static MediaExtractor mExtractor;

    static MediaCodec mDecoder;
    public synchronized static void MP3ToPCM(String Source,String dest)
    {
        try{
            mExtractor = new MediaExtractor();
            mExtractor.setDataSource(Source);
            mExtractor.selectTrack(0);
            MediaFormat inputFormat = mExtractor.getTrackFormat(0);


            if(!inputFormat.getString(MediaFormat.KEY_MIME).startsWith("audio"))return;


            mDecoder = MediaCodec.createDecoderByType(inputFormat.getString(MediaFormat.KEY_MIME));



            mDecoder.configure(inputFormat,null,null,0);
            mDecoder.start();

            FileOutputStream fOut = new FileOutputStream(dest);
            BufferedOutputStream buffWrite = new BufferedOutputStream(fOut);

            //编码过程

            MediaCodec.BufferInfo info = new MediaCodec.BufferInfo(); //缓冲区
            info.size = 4096*16; //设置output buffer 的大小
            while (true) {
                int inIndex = mDecoder.dequeueInputBuffer(5000);
                if (inIndex >= 0) {
                    ByteBuffer buffer =  mDecoder.getInputBuffer(inIndex);
                    //从MediaExtractor中读取一帧待解的数据
                    int sampleSize = mExtractor.readSampleData(buffer, 0);
                    //小于0 代表所有数据已读取完成
                    if (sampleSize < 0) {
                        mDecoder.queueInputBuffer(inIndex, 0, 0, 0, MediaCodec.BUFFER_FLAG_END_OF_STREAM);
                    } else {
                        //插入一帧待解码的数据
                        mDecoder.queueInputBuffer(inIndex, 0, sampleSize, mExtractor.getSampleTime(), 0);
                        //MediaExtractor移动到下一取样处
                        mExtractor.advance();
                    }
                }
                int outIndex = mDecoder.dequeueOutputBuffer(info, 5000);
                switch (outIndex) {
                    case MediaCodec.INFO_OUTPUT_FORMAT_CHANGED:
                        //  MediaFormat format = mDecoder.getOutputFormat();
                        //  audioTrack.setPlaybackRate(format.getInteger(MediaFormat.KEY_SAMPLE_RATE));
                        break;
                    case MediaCodec.INFO_TRY_AGAIN_LATER:
                        break;
                    default:
                        ByteBuffer outBuffer = mDecoder.getOutputBuffer(outIndex);
                        //BufferInfo内定义了此数据块的大小
                        final byte[] chunk = new byte[info.size];
                        //  createFileWithByte(chunk);
                        //将Buffer内的数据取出到字节数组中
                        outBuffer.get(chunk);
                        //数据取出后一定记得清空此Buffer MediaCodec是循环使用这些Buffer的，不清空下次会得到同样的数据
                        outBuffer.clear();
                        try {
                            //TODO 在这里处理解码后的数据
                            //将解码出来的PCM数据IO流存入本地文件。
                            //fos.write(chunk);
                            int vnum;
                            if (chunk.length % (4096*16) == 0) {

                                vnum = chunk.length / (4096*16);
                            } else {
                                vnum = chunk.length / (4096*16) + 1;
                            }
                            byte[] bytes = new byte[4096*16];
                            for (int v = 0; v < vnum; v++) {
                                if (v != vnum - 1) {
                                    //1 初始数据 2  从元数据的起始位置开始 3 目标数组 4 目标数组的开始起始位置 5  要copy的数组的长度
                                    System.arraycopy(chunk, v * (4096*16), bytes, 0, (4096*16));
                                    buffWrite.write(bytes);
                                } else {
                                    System.arraycopy(chunk, v * 4096, bytes, 0, chunk.length - v * (4096*16));
                                    buffWrite.write(bytes,0,chunk.length - v * (4096*16));
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        //此操作一定要做，不然MediaCodec用完所有的Buffer后 将不能向外输出数据

                        mDecoder.releaseOutputBuffer(outIndex, false);
                        break;
                }
                if (info.flags != 0) {
                    MLogCat.Print_Info("MP3ToPCM","ConvertToFile"+dest);
                    break;
                }
            }
            buffWrite.close();
        }catch (Exception e)
        {
            MLogCat.Print_Error("MP3ToPCM",e);
        }

    }
}
