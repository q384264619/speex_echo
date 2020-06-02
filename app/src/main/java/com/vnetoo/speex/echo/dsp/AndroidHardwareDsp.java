package com.vnetoo.speex.echo.dsp;

import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AutomaticGainControl;
import android.media.audiofx.NoiseSuppressor;
import android.util.Log;

import com.vnetoo.speex.echo.AudioGlobal;

public class AndroidHardwareDsp implements  AudioDsp{
    private static final String TAG = "AndroidHardwareDsp";
    private int audioSession;
    private AcousticEchoCanceler echoCanceler;//回声消除器
    private NoiseSuppressor noiseSuppressor;//噪声抑制器
    private AutomaticGainControl gainControl;//自动增益器

    private static  AndroidHardwareDsp hardDsp = null;

   static class AndroidAudioFactory  implements  Factory{

       @Override
       public AudioDsp create(int audioSession, int sample, int channel) {
           return new AndroidHardwareDsp(audioSession);
       }
   }

   public static final AndroidHardwareDsp getInstance(){
       synchronized (AndroidHardwareDsp.class){

           if(hardDsp == null){
               hardDsp = new AndroidHardwareDsp(AudioGlobal.audioSession);
           }
       }
       return hardDsp;
   }


    public AndroidHardwareDsp(int audioSession) {
        this.audioSession = audioSession;
    }

    @Override
    public boolean isEnableVad() {
        return false;
    }

    @Override
    public void enableVad(boolean enable) {

    }

    @Override
    public boolean isEnableDenoiseSuppression() {
        return noiseSuppressor == null ? false:noiseSuppressor.getEnabled();
    }

    @Override
    public void enableDenoiseSuppression(boolean enable) {
       if(audioSession == -1)return;
        if(noiseSuppressor==null){
            noiseSuppressor = NoiseSuppressor.create(audioSession);
        }
        if(noiseSuppressor!=null){
          int result=  noiseSuppressor.setEnabled(enable);
          if(result == NoiseSuppressor.SUCCESS){
              Log.i(TAG,"噪声抑制操作结果 :"+result);
          }
        }
    }

    @Override
    public boolean isEnableAudiomicGainControl() {
        return  gainControl == null ? false:gainControl.getEnabled();
    }

    @Override
    public void enableAudiomicGainControl(boolean enable) {
        if(audioSession == -1)return;
        if(gainControl==null){
            gainControl = AutomaticGainControl.create(audioSession);
        }
        if(gainControl!=null){
            int result=  gainControl.setEnabled(enable);
            if(result == AutomaticGainControl.SUCCESS){
                Log.i(TAG,"自动增益操作结果 :"+result);
            }
        }
    }

    @Override
    public boolean isEnableEcho() {
        return echoCanceler == null ? false:echoCanceler.getEnabled();
    }

    @Override
    public void enableEcho(boolean enable) {
        if(audioSession == -1)return;
        if(echoCanceler==null){
            echoCanceler = AcousticEchoCanceler.create(audioSession);
        }
        if(echoCanceler!=null){
            int result=  echoCanceler.setEnabled(enable);
            if(result == AcousticEchoCanceler.SUCCESS){
                Log.i(TAG,"回声消除操作结果 :"+result);
            }
        }
    }

    @Override
    public int getDelayTimeInMs() {
        return 0;
    }

    @Override
    public void setDelayTimeInMs(int delaytimeMs) {

    }

    @Override
    public int ProcessAudio(byte[] mic,int offset, int len) {
        return 0;
    }

    @Override
    public void ProcessReverseAudio(byte[] play,int offset, int len) {

    }

    @Override
    public void destroy() {
        if(echoCanceler!=null){
            echoCanceler.release();
        }
        if(gainControl!= null){
            gainControl.release();
        }
        if(noiseSuppressor!=null){
            noiseSuppressor.release();
        }
        echoCanceler = null;
        gainControl  = null;
        noiseSuppressor= null;

        hardDsp = null;
    }
}
