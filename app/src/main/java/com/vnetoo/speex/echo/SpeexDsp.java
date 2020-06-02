package com.vnetoo.speex.echo;

import android.util.Log;

import androidx.annotation.IntDef;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

public class SpeexDsp {
    public static final int SUCCESS = 0;
    public static final int FAILURE = -1;
    public static final int VAD = 1;

    @IntDef(value = {SUCCESS,FAILURE,VAD})
    @Retention(RetentionPolicy.SOURCE)
    @interface Result{

    }

    private boolean enableAGC = false;
    private boolean enableVad = false;
    private boolean enableEcho = false;
    private boolean enableDenoise = false;
    private boolean isConfiged;
    private int fillterSize = 0;
    static{
        System.loadLibrary("speexecho");
    }
    /** Creates a new echo canceller state
     * @param frameSize Number of samples to process at one time (should correspond to 10-20 ms)
     * @param filterSize Number of samples of echo to cancel (should generally correspond to 100-500 ms)
     * @return Newly-created echo canceller state
     */
    public void initSpeexDsp(int frameSize,int filterSize,int sampleRate){
        //SPEEX_ECHO_GET_IMPULSE_RESPONSE_SIZE
        this.fillterSize = filterSize;
        SpeexEcho.aecInit(frameSize,filterSize,sampleRate);
        //关闭混响
        SpeexEcho.aec_set_other_feature(SpeexEcho.SPEEX_PREPROCESS_SET_DEREVERB,0);
        SpeexEcho.aec_set_other_feature(SpeexEcho.SPEEX_PREPROCESS_SET_DEREVERB_DECAY,0.0f);
        SpeexEcho.aec_set_other_feature(SpeexEcho.SPEEX_PREPROCESS_SET_DEREVERB_LEVEL,0.0f);

        //设置静音检测相关数据
        SpeexEcho.aec_set_other_feature(SpeexEcho.SPEEX_PREPROCESS_SET_PROB_START,80);
        SpeexEcho.aec_set_other_feature(SpeexEcho.SPEEX_PREPROCESS_SET_PROB_CONTINUE,65);


        this.enableAGC = SpeexEcho.aec_get_other_int_feature(SpeexEcho.SPEEX_PREPROCESS_GET_AGC) == 1? true:false;

        this.enableVad = SpeexEcho.aec_get_other_int_feature(SpeexEcho.SPEEX_PREPROCESS_GET_VAD) == 1? true:false;

        this.enableDenoise = SpeexEcho.aec_get_other_int_feature(SpeexEcho.SPEEX_PREPROCESS_GET_DENOISE) == 1? true:false;
        isConfiged = true;
    }


    public @Result  int aec_process(byte[] mic,byte[] playBack,byte[] outPut){
        if(mic == null || playBack == null || outPut == null) return FAILURE;
        int result = SpeexEcho.aec_process(mic,playBack,outPut);
        if(enableVad){
            if(result == 0){
                Log.e("petter","audio data is vad");
                return VAD;
            }
        }
        return SUCCESS;
    }


    public void release(){
        SpeexEcho.destory();
        isConfiged = false;

    }

    public boolean isEnableAGC() {
        return enableAGC;
    }

    public void setEnableAGC(boolean enableAGC) {
        this.enableAGC = enableAGC;
        if(isConfiged){
            SpeexEcho.aec_set_other_feature(SpeexEcho.SPEEX_PREPROCESS_SET_AGC,enableAGC?1:0);
            Log.d("petter","SPEEX_PREPROCESS_SET_AGC=>"+enableAGC);
        }
    }

    public boolean isEnableVad() {
        return enableVad;
    }

    public void setEnableVad(boolean enableVad) {
        this.enableVad = enableVad;
        if(isConfiged){
            SpeexEcho.aec_set_other_feature(SpeexEcho.SPEEX_PREPROCESS_SET_VAD,enableVad?1:0);
            Log.d("petter","SPEEX_PREPROCESS_SET_VAD=>"+enableVad);
        }
    }

    public boolean isEnableEcho() {
        return enableEcho;
    }

    public void setEnableEcho(boolean enableEcho) {
        this.enableEcho = enableEcho;
        if(isConfiged){
            SpeexEcho.aec_set_enable_echo(enableEcho?true:false);
            Log.d("petter","echo =>"+enableEcho);
        }
    }

    public boolean isEnableDenoise() {
        return enableDenoise;
    }

    public void setEnableDenoise(boolean enableDenoise) {
        this.enableDenoise = enableDenoise;
        if(isConfiged){
            SpeexEcho.aec_set_other_feature(SpeexEcho.SPEEX_PREPROCESS_SET_DENOISE,enableDenoise?1:0);
            Log.d("petter","SPEEX_PREPROCESS_SET_DENOISE =>"+enableDenoise);
        }
    }


    public int getAudioPreProcessAttr(int key){
        if(!isConfiged)return 0;
        return SpeexEcho.aec_get_other_int_feature(key);
    }
    public float getAudioPreProcessAttrFloat(int key){
        if(!isConfiged)return 0;
        return SpeexEcho.aec_get_other_float_feature(key);
    }


    public void setAudioPreProcessAttr(int key,int value){
        if(!isConfiged)return ;
        SpeexEcho.aec_set_other_feature(key,value);
    }
    public void setAudioPreProcessAttr(int key,float value){
        if(!isConfiged)return;
        SpeexEcho.aec_set_other_feature(key,value);
    }

    public  int ProcessStream(byte[] data,int offset, int len){
            return  SpeexEcho.ProcessStream(data,offset,len);
    }

    public  int ProcessReverseStream(byte[] far_end,int offset, int len){
        return SpeexEcho.ProcessReverseStream(far_end,offset,len);
    }

}
