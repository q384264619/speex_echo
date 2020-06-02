package com.vnetoo.speex.echo.dsp;

import com.vnetoo.speex.echo.AudioGlobal;
import com.vnetoo.speex.echo.SpeexDsp;

public class SpeexAudioDsp implements AudioDsp {
    private SpeexDsp mAudioDsp;
    private static SpeexAudioDsp mInstance;

    public SpeexDsp getSpeex() {
        return mAudioDsp;
    }

    static class  SpeexAudioDspFactory implements  Factory
    {

        @Override
        public AudioDsp create(int audioSession, int sample, int channel) {
            return SpeexAudioDsp.getInstance(sample,channel);
        }
    }

    private static AudioDsp getInstance(int sample, int channel) {
        synchronized (SpeexAudioDsp.class){
            if(mInstance == null){
                mInstance = new SpeexAudioDsp(sample,channel);
            }
        }
        return mInstance;
    }

    SpeexAudioDsp(int sample, int channel){
        mAudioDsp = new SpeexDsp();
        int frameBytes = AudioDspUtil.getFrameSizeInBytes(sample,channel);
        //这个函数中320为每一个数据包的帧数，5000即为尾音长度，这个参数的设置将影响回声消除模块的处理效果。如果在一个房间里，最好是发射时间的1/3
        mAudioDsp.initSpeexDsp(frameBytes,frameBytes* AudioGlobal.speexDefaultFilterSize,sample);
    }

    @Override
    public boolean isEnableVad() {
        return mAudioDsp.isEnableVad();
    }

    @Override
    public void enableVad(boolean enable) {
        mAudioDsp.setEnableVad(enable);
    }

    @Override
    public boolean isEnableDenoiseSuppression() {
        return mAudioDsp.isEnableDenoise();
    }

    @Override
    public void enableDenoiseSuppression(boolean enable) {
        mAudioDsp.setEnableDenoise(enable);
    }

    @Override
    public boolean isEnableAudiomicGainControl() {
        return mAudioDsp.isEnableAGC();
    }

    @Override
    public void enableAudiomicGainControl(boolean enable) {
        mAudioDsp.setEnableAGC(enable);
    }

    @Override
    public boolean isEnableEcho() {
        return mAudioDsp.isEnableEcho();
    }

    @Override
    public void enableEcho(boolean enable) {
        mAudioDsp.setEnableEcho(enable);
    }

    @Override
    public int getDelayTimeInMs() {
        return 0;
    }

    @Override
    public void setDelayTimeInMs(int delaytimeMs) {

    }

    @Override
    public int ProcessAudio(byte[] mic, int offset, int len) {
        return mAudioDsp.ProcessStream(mic,offset,len);
    }

    @Override
    public void ProcessReverseAudio(byte[] play, int offset, int len) {
            mAudioDsp.ProcessReverseStream(play,offset,len);
    }

    @Override
    public void destroy() {
        if(mAudioDsp!=null){
            mAudioDsp.release();
        }
        mAudioDsp = null;
    }
}
