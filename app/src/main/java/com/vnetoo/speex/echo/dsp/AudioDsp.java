package com.vnetoo.speex.echo.dsp;

public interface AudioDsp {

    int CODE_SUCCESS = 0;
    int CODE_VAD = 1;
    int CODE_ERROR = -1;

     interface Factory {
        AudioDsp create(int audioSession, int sample, int channel);
    }
    /***
     * 是否已经开启静音检测
     * @return
     */
    boolean isEnableVad();

    /***
     * 是否开启静音检测
     * @param enable true：开启，false:禁止
     */
    void enableVad(boolean enable);

    /***
     * 是否已经开启噪声抑制
     * @return true:已经开启，false:未开启
     */
    boolean isEnableDenoiseSuppression();

    /***
     * 是否开启噪声抑制
     * @param enable true:开启，false:禁止
     */
    void enableDenoiseSuppression(boolean enable);

    /***
     * 是否开启自动增益
     * @return true:开启 false:禁止
     */
    boolean isEnableAudiomicGainControl();

    /***
     * 是否开启自动增益
     * @param enable true:开启 false:禁止
     */
    void enableAudiomicGainControl(boolean enable);

    /**
     * 是否开启回声消除
     * @return  true:开启 false:禁止
     */
    boolean isEnableEcho();

    /**
     * 是否开启回声消除
     * @param  enable true:开启 false:禁止
     */
    void enableEcho(boolean enable);

    /**
     * 得到当前回声消除延时
     * @return 延时时长 ms
     */
    int getDelayTimeInMs();

    /***
     * 设置当前回声消除延时
     * @param delaytimeMs ms
     */
    void setDelayTimeInMs(int delaytimeMs);


    /**
     * 处理mic输入
     * @param mic
     * @param len
     * @return int CODE_SUCCESS = 0;
     *     int CODE_VAD = 1;
     *     int CODE_ERROR = -1;
     */
    int  ProcessAudio(byte[] mic, int offset, int len);

    void ProcessReverseAudio(byte[] play, int offset, int len);

    void destroy();
}
