package com.vnetoo.speex.echo.dsp;

import android.media.AudioFormat;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.util.Log;

import com.vnetoo.speex.echo.AudioGlobal;


public class AudioDspUtil {
private static final String TAG = "AudioDspUtil";
    /**
     * 移动设置，只用考虑单声道
     * 根据指定的频率与帧大小，计算出每帧包含的字节数
     * @param sampleRate  8000 16000 32000  //本应用使用16000
     * @param frameSize 每帧大小一般为10ms 20ms
     * @return 每帧包含的字节数
     */
    public static int getFrameSizeInBytes(int sampleRate,int frameSize){
        frameSize = AudioGlobal.FRAME_SIZE;
        //计算每毫秒采集样本数量
        int samplesPerMs = (int) (sampleRate /1000.0);
        int bytesPerSample =  1 * 2; // channelcount* bytePerChanel
        int frameSizeInBytes = samplesPerMs*frameSize*bytesPerSample;//计算出每帧包含的字节数

        Log.i(TAG,"getFrameSizeInBytes:"+frameSizeInBytes);

        return frameSizeInBytes;
    }

    public static int getFrameSizeInShorts(int sample,int frameSize){
        frameSize = AudioGlobal.FRAME_SIZE;
        //计算每毫秒采集样本数量
        int samplesPerMs = (int) (sample /1000.0);
        return samplesPerMs*frameSize;//计算出每帧包含的字节数
    }

    /**
     * 得到录音缓存大小
     * 得到单声道，最小缓存
     * @param sample
     * @return
     */
    public static int getMinSizeInBytesForRecordBuffer(int sample){
        int recordBufferSize = AudioRecord.getMinBufferSize(sample, AudioFormat.CHANNEL_IN_MONO, AudioFormat.ENCODING_PCM_16BIT);
        int defaultFrameSize = getFrameSizeInBytes(sample, AudioGlobal.FRAME_SIZE);
        int left = recordBufferSize % defaultFrameSize;
        if(left!= 0 ){
            recordBufferSize = (recordBufferSize / defaultFrameSize +1) * defaultFrameSize;
        }
        return recordBufferSize;
    }

    /**
     *
     * 得到播放缓存大小
     * 得到单声道，最小缓存
     * @param sample
     * @return
     */
    public static int getMinSizeInBytesForPlayBuffer(int sample){
        int playBufferSize = AudioTrack.getMinBufferSize(sample, AudioFormat.CHANNEL_OUT_MONO, AudioFormat.ENCODING_PCM_16BIT);
        int defaultFrameSize = getFrameSizeInBytes(sample, AudioGlobal.FRAME_SIZE);
        int left = playBufferSize % defaultFrameSize;
        if(left!= 0 ){
            playBufferSize = (playBufferSize / defaultFrameSize +1) * defaultFrameSize;
        }

        return playBufferSize;
    }

    /**
     * 简单估算，不带mic缓冲的延时
     * @param sample
     * @return
     */
    public static int simpleEstimateDelayWithRecordBuffer(int sample){
         int recordBufferSamples = getMinSizeInBytesForRecordBuffer(sample)/2;
         int playSamplesPerMs =  sample/ 1000;
         int time =  recordBufferSamples / playSamplesPerMs ;
        Log.e(TAG,"simpleEstimateDelayWithRecordBuffer:"+time);
         return time;
    }

    /**
     * 简单估算
     * @param sample
     * @return
     */
    public static int simpleEstimateDelay(int sample){
        int recordBufferSamples = getMinSizeInBytesForRecordBuffer(sample)/2;
        int playBufferSamples = getMinSizeInBytesForPlayBuffer(sample)/2;
        int playSamplesPerMs =  sample/ 1000;
        int playBufferDelay =  recordBufferSamples / playSamplesPerMs + 2;
        int recordbufferDelay =  playBufferSamples / playSamplesPerMs + 2;
        Log.e(TAG,"simpleEstimateDelay:"+(playBufferDelay+recordbufferDelay));
        return playBufferDelay + recordbufferDelay ;
    }
}
