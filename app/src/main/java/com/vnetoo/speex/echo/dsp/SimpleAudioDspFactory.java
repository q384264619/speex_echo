package com.vnetoo.speex.echo.dsp;

import androidx.annotation.IntDef;

import com.vnetoo.speex.echo.AudioGlobal;
import com.vnetoo.speex.echo.AudioQuality;
import com.vnetoo.speex.echo.BuildConfig;

import java.util.HashMap;


public class SimpleAudioDspFactory {

    @IntDef(value = {HARD, WEBRTC,SPEEX})
    public @interface AudioDspFactory {

    }

    public static final int HARD = 0;
    public static final int WEBRTC = 1;
    public static final int SPEEX = 2;
    private static HashMap<Integer, AudioDsp.Factory> factorys;

    static {
        factorys = new HashMap<>();
        factorys.put(HARD, new AndroidHardwareDsp.AndroidAudioFactory());
        factorys.put(SPEEX, new SpeexAudioDsp.SpeexAudioDspFactory());
//        factorys.put(
//                WEBRTC, new WebRTCAudioDsp.WebRTCAudioDspFactory());
    }

    /**
     * 得到回声消除器，所以实例实现为单例
     *
     * @param type
     * @param quality
     * @return
     */
    public static AudioDsp create(@AudioDspFactory int type, AudioQuality quality) {
        AudioDsp.Factory factory1 = factorys.get(type);
        if (factory1 != null) {
            int AudioSession = AudioGlobal.audioSession;
            if (AudioSession == 0) {
                factory1 = factorys.get(WEBRTC);
            }
            return factory1.create(AudioSession, quality.samplingRate, quality.chanel);
        }
        return null;
    }

    /**
     * 得到回声消除器，所以实例实现为单例
     *
     * @param type
     * @return
     */
    public static AudioDsp create(@AudioDspFactory int type, int sample, int channel) {
        if (BuildConfig.DEBUG) {
            AudioDsp.Factory factory1 = factorys.get(type);
            if (factory1 != null) {

                int AudioSession = AudioGlobal.audioSession;
                if (AudioSession <= 0) {
                    factory1 = factorys.get(SPEEX);
                }

                return factory1.create(AudioSession, sample, channel);
            }
        }
        else{
            AudioDsp.Factory factory1 = factorys.get(SPEEX);
            return factory1.create(0,sample,channel);
        }
        return null;
    }
}
