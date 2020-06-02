package com.vnetoo.speex.echo;

class SpeexEcho {

    public static native int aecInit(int frame_size, int filter_length, int sampling_rate) throws IllegalStateException;

    public static native  int aec_set_enable_echo(boolean enable);

    public static native int aec_set_feature(int key,int value);

    public static native int aec_set_other_feature(int key,int  value);

    public static native int aec_set_other_feature(int key,float value);

    public static native float aec_get_other_float_feature(int key);

    public static native int  aec_get_other_int_feature(int key);

    public static native int aec_process(byte[] near, byte[] far,byte[] out);

    public static  native int ProcessStream(byte[] data,int offset, int len);

    public static  native int ProcessReverseStream(byte[] far_end,int offset, int len);




    public static native void destory();

    // public static native int aec_get_feature(int key);

    /**
     * Set preprocessor denoiser state
     */
    public static final int SPEEX_PREPROCESS_SET_DENOISE = 0;
    /**
     * Get preprocessor denoiser state
     */
    public static final int SPEEX_PREPROCESS_GET_DENOISE = 1;

    /**
     * Set preprocessor Automatic Gain Control state
     */
    public static final int SPEEX_PREPROCESS_SET_AGC = 2;
    /**
     * Get preprocessor Automatic Gain Control state
     */
    public static final int SPEEX_PREPROCESS_GET_AGC = 3;

    /**
     * Set preprocessor Voice Activity Detection state
     */
    public static final int SPEEX_PREPROCESS_SET_VAD = 4;
    /**
     * Get preprocessor Voice Activity Detection state
     */
    public static final int SPEEX_PREPROCESS_GET_VAD = 5;

    /**
     * Set preprocessor Automatic Gain Control level (float)
     */
    public static final int SPEEX_PREPROCESS_SET_AGC_LEVEL = 6;
    /**
     * Get preprocessor Automatic Gain Control level (float)
     */
    public static final int SPEEX_PREPROCESS_GET_AGC_LEVEL = 7;

    /**
     * Set preprocessor dereverb state
     */
    public static final int SPEEX_PREPROCESS_SET_DEREVERB = 8;
    /**
     * Get preprocessor dereverb state
     */
    public static final int SPEEX_PREPROCESS_GET_DEREVERB = 9;

    /**
     * Set preprocessor dereverb level
     */
    public static final int SPEEX_PREPROCESS_SET_DEREVERB_LEVEL = 10;
    /**
     * Get preprocessor dereverb level
     */
    public static final int SPEEX_PREPROCESS_GET_DEREVERB_LEVEL = 11;

    /**
     * Set preprocessor dereverb decay
     */
    public static final int SPEEX_PREPROCESS_SET_DEREVERB_DECAY = 12;
    /**
     * Get preprocessor dereverb decay
     */
    public static final int SPEEX_PREPROCESS_GET_DEREVERB_DECAY = 13;

    /**
     * Set probability required for the VAD = to go from silence to voice
     */
    public static final int SPEEX_PREPROCESS_SET_PROB_START = 14;
    /**
     * Get probability required for the VAD = to go from silence to voice
     */
    public static final int SPEEX_PREPROCESS_GET_PROB_START = 15;

    /**
     * Set probability required for the VAD = to stay in the voice state (integer percent)
     */
    public static final int SPEEX_PREPROCESS_SET_PROB_CONTINUE = 16;
    /**
     * Get probability required for the VAD = to stay in the voice state (integer percent)
     */
    public static final int SPEEX_PREPROCESS_GET_PROB_CONTINUE = 17;

    /**
     * Set maximum attenuation of the noise in dB = (negative number)
     */
    public static final int SPEEX_PREPROCESS_SET_NOISE_SUPPRESS = 18;
    /**
     * Get maximum attenuation of the noise in dB = (negative number)
     */
    public static final int SPEEX_PREPROCESS_GET_NOISE_SUPPRESS = 19;

    /**
     * Set maximum attenuation of the residual echo in dB = (negative number)
     */
    public static final int SPEEX_PREPROCESS_SET_ECHO_SUPPRESS = 20;
    /**
     * Get maximum attenuation of the residual echo in dB = (negative number)
     */
    public static final int SPEEX_PREPROCESS_GET_ECHO_SUPPRESS = 21;

    /**
     * Set maximum attenuation of the residual echo in dB = when near end is active (negative number)
     */
    public static final int SPEEX_PREPROCESS_SET_ECHO_SUPPRESS_ACTIVE = 22;
    /**
     * Get maximum attenuation of the residual echo in dB = when near end is active (negative number)
     */
    public static final int SPEEX_PREPROCESS_GET_ECHO_SUPPRESS_ACTIVE = 23;

    /**
     * Set the corresponding echo canceller state so that residual echo suppression can be performed (NULL = for no residual echo suppression)
     */
    public static final int SPEEX_PREPROCESS_SET_ECHO_STATE = 24;
    /**
     * Get the corresponding echo canceller state
     */
    public static final int SPEEX_PREPROCESS_GET_ECHO_STATE = 25;

    /**
     * Set maximal gain increase in dB/second (int32)
     */
    public static final int SPEEX_PREPROCESS_SET_AGC_INCREMENT = 26;

    /**
     * Get maximal gain increase in dB/second (int32)
     */
    public static final int SPEEX_PREPROCESS_GET_AGC_INCREMENT = 27;

    /**
     * Set maximal gain decrease in dB/second (int32)
     */
    public static final int SPEEX_PREPROCESS_SET_AGC_DECREMENT = 28;

    /**
     * Get maximal gain decrease in dB/second (int32)
     */
    public static final int SPEEX_PREPROCESS_GET_AGC_DECREMENT = 29;

    /**
     * Set maximal gain in dB = (int32)
     */
    public static final int SPEEX_PREPROCESS_SET_AGC_MAX_GAIN = 30;

    /**
     * Get maximal gain in dB = (int32)
     */
    public static final int SPEEX_PREPROCESS_GET_AGC_MAX_GAIN = 31;

    /*  Can't set loudness */
    /**
     * Get loudness
     */
    public static final int SPEEX_PREPROCESS_GET_AGC_LOUDNESS = 33;

    /*  Can't set gain */
    /**
     * Get current gain (int32 percent)
     */
    public static final int SPEEX_PREPROCESS_GET_AGC_GAIN = 35;

    /*  Can't set spectrum size */
    /**
     * Get spectrum size for power spectrum (int32)
     */
    public static final int SPEEX_PREPROCESS_GET_PSD_SIZE = 37;

    /*  Can't set power spectrum */
    /**
     * Get power spectrum (int32[] of squared values)
     */
    public static final int SPEEX_PREPROCESS_GET_PSD = 39;

    /*  Can't set noise size */
    /**
     * Get spectrum size for noise estimate (int32)
     */
    public static final int SPEEX_PREPROCESS_GET_NOISE_PSD_SIZE = 41;

    /*  Can't set noise estimate */
    /**
     * Get noise estimate (int32[] of squared values)
     */
    public static final int SPEEX_PREPROCESS_GET_NOISE_PSD = 43;

    /* Can't set speech probability */
    /**
     * Get speech probability in last frame (int32).
     */
    public static final int SPEEX_PREPROCESS_GET_PROB = 45;

    /**
     * Set preprocessor Automatic Gain Control level (int32)
     */
    public static final int SPEEX_PREPROCESS_SET_AGC_TARGET = 46;
    /**
     * Get preprocessor Automatic Gain Control level (int32)
     */
    public static final int SPEEX_PREPROCESS_GET_AGC_TARGET = 47;

}
