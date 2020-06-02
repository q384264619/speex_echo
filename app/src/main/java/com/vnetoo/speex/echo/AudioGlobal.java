package com.vnetoo.speex.echo;

import com.vnetoo.speex.echo.dsp.SimpleAudioDspFactory;

import static com.vnetoo.speex.echo.dsp.SimpleAudioDspFactory.SPEEX;


public class AudioGlobal {

    public static int speexDefaultFilterSize = 120; //100ms

    /**
     * 当前录音的audioSessionID
     */
    public static int audioSession = -1;

    /**
     * 当前生效的音频处理器
     */
    public static @SimpleAudioDspFactory.AudioDspFactory
    int currentEchotype =   SPEEX;

    // for echo 默认一帧处理时长为10ms
    public static final int FRAME_SIZE = 10; // 10ms

    //当前录音播放使用的配制
    public static AudioQuality  quality = AudioQuality.DEFAULT_AUDIO_QUALITY;

    public static int webDelayPack = 0;//webRTC延时补偿



    public static final String SP_KEY_VAD = "IsEnableVad";
    public static final String SP_KEY_DENOISE = "IsEnableDenoise";
    public static final String SP_KEY_GAIN = "IsEnableAutomaticGain";
    public static final String SP_KEY_ECHO = "IsEnableECHO";
    public static final String SP_KEY_ECHS = "IsEnableECHS";

    public static final String SP_KEY_VAD_LEVEL = "VadLevel";

    public static final String SP_KEY_GAIN_LEVEL = "GainLevel";

    public static final String SP_KEY_DENOISE_LEVEL = "DenoiseLevel";

    public static final String SP_KEY_ROUTE_MODE = "routeMode";

    public static final String SP_KEY_DELAY_EXTRAS = "delay_extras";

    public static final String SP_KEY_ENABLE_WEBRTC = "useWEBRTC";
}
