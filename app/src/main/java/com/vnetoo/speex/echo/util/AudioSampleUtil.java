package com.vnetoo.speex.echo.util;

public class AudioSampleUtil {
    public static final int FRAME_SIZE = 10; // 10ms
    /**
     * 移动设置，只用考虑单声道
     * 根据指定的频率与帧大小，计算出每帧包含的字节数
     * @param sampleRate  8000 16000 32000  //本应用使用16000
     * @param frameSize 每帧大小一般为10ms 20ms
     * @return 每帧包含的字节数
     */
    public static int getFrameBytes(int sampleRate,int frameSize){
          //计算每毫秒采集样本数量
         int samplesPerMs = (int) (sampleRate /1000.0);
         int bytesPerSample =  1 * 2; // channelcount* bytePerChanel
         return samplesPerMs*frameSize*bytesPerSample;//计算出每帧包含的字节数
    }
}
