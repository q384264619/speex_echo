//
// Created by vnetoo on 2020/5/8.
//
#include  "echo_config.h"
#ifndef SPEEX_AUDIO_ECHO_H
#define SPEEX_AUDIO_ECHO_H


#define NN 128           //帧长      一般都是  80,160,320,
#define TAIL 1024        //尾长      一般都是  80*25 ,160*25 ,320*25

#define ECHO_ERROR -1
#define ECHO_OK 0

// defined speexEcho struct
typedef struct SpeexEcho{
    SpeexEchoState* st;
    SpeexPreprocessState* den;
    int aec_init_flag;
    pthread_mutex_t mutex;
    int enable_echo;
    int frame_size;
    int enable_vad;
}SpeexEcho;


/**
 * 初始化回声消除
 * @param context
 * @param frameSize
 * @param filterSize
 * @param sampleRate
 * @return
 */
int echo_init(SpeexEcho* context,const unsigned short frameSize ,const unsigned short filterSize,const unsigned short sampleRate);

/**
 * 设置回声消除参数
 * @param context
 * @param key
 * @param value
 * @ 0/1
 */
int echo_set_ctl(SpeexEcho* context,const unsigned short key , int value);

/**
 * 获取回声消除器的参数
 * @param context
 * @param key
 * @param result
 * @return
 */
int echo_get_ctl(SpeexEcho* context,const unsigned short key , void* result);

/***
 * 设置噪声参数
 * @param context
 * @param key
 * @param value
 * @return
 */
int echo_set_preprocess_ctl(SpeexEcho* context,const unsigned short key , void* value);



int echo_get_preprocess_ctl(SpeexEcho* context,const unsigned short key , void* value);

/***
 * 处理数据
 * @param context
 * @param near mic数据
 * @param far  播放数据
 * @param out  处理之后的数据
 * @return
 */
int echo_sync_process(const SpeexEcho* context,unsigned short* near,unsigned short* far, unsigned short* out,size_t size);

int echo_denoise_only(const SpeexEcho* context,unsigned short* near);


int echo_asyn_process_near(const SpeexEcho* context,unsigned short* near,unsigned short *out);

int echo_asyn_process_far(const SpeexEcho* context,unsigned short* far);

/**
 * 清除，释放
 */
void echo_distroy(SpeexEcho* context);


#endif //SPEEX_AUDIO_ECHO_H
