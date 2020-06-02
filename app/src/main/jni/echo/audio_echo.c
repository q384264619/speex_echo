//
// Created by vnetoo on 2020/5/8.
//

#include "include/audio_echo.h"

int echo_init(SpeexEcho *context, const unsigned short frameSize, const unsigned short filterSize,
              const unsigned short sampleRate) {

    pthread_mutex_lock(&(context->mutex));

    unsigned short fs = frameSize;
    if (fs <= 0) {
        return ECHO_ERROR;
    }

    unsigned short fl = filterSize;

    if (fl <= 0) {
        return ECHO_ERROR;
    }

    int mSampleRate = sampleRate;

    if(mSampleRate<=0){
        return ECHO_ERROR;
    }

    context->st = speex_echo_state_init(fs, fl);
    ASSERT(context->st != NULL, "init echo error");
    if (context->st == NULL) {
        LOGE("init echo error with frameSize:%d,filterSize:%d", fs, fl);
        return ECHO_ERROR;
    }
    context->den = speex_preprocess_state_init(fs, sampleRate);
    ASSERT(context->den != NULL, "init preprocess with sampleRate: %d, error", mSampleRate);

    int flag = -1;

    flag = speex_echo_ctl(context->st, SPEEX_ECHO_SET_SAMPLING_RATE, &mSampleRate);

    ASSERT(flag == 0, "set sampleRate for echo error");

    flag = speex_preprocess_ctl(context->den, SPEEX_PREPROCESS_SET_ECHO_STATE, context->st);

    ASSERT(flag == 0, "set  aec proprocess to st error")

    context->aec_init_flag = flag == 0 ? 1 : 0;
    pthread_mutex_unlock(&(context->mutex));
    LOGI("init speex aec success");
    return ECHO_OK;

}


int echo_set_ctl(SpeexEcho *context, const unsigned short key, int value) {
    ASSERT(context != NULL, "speexEcho is NULL")
    if (context != NULL && context->aec_init_flag == 1) {
        int result = speex_echo_ctl(context->st, key, &value);
        if (result == 0) {
            return ECHO_OK;
        } else {
            LOGE("set speex_echo_ctl(%d,%d) unknow key", key, value);
        }

    }
    return ECHO_ERROR;
}

int echo_get_ctl(SpeexEcho *context, const unsigned short key, void *result) {
    ASSERT(context != NULL, "speexEcho is NULL")
    if (context != NULL && context->aec_init_flag == 1) {
        int result = speex_echo_ctl(context->st, key, &result);
        if (result == 0) {
            return ECHO_OK;
        } else {
            LOGE("get speex_echo_ctl(%d,%d) unknow key", key, result);
        }

    }
    return ECHO_ERROR;
}

int echo_set_preprocess_ctl(SpeexEcho *context, const unsigned short key, void *value) {
    ASSERT(context != NULL, "speexEcho is NULL")
    if (context != NULL && context->aec_init_flag == 1) {
        pthread_mutex_lock(&(context->mutex));
        int result = speex_preprocess_ctl(context->den, key, value);
        if(key == SPEEX_PREPROCESS_SET_NOISE_SUPPRESS){
            int* v = (int*)value;
            LOGI("SPEEX_PREPROCESS_SET_NOISE_SUPPRESS=>%d result:%d",*v,result);

        }else if(key == SPEEX_PREPROCESS_SET_DENOISE){
            int* v = (int*)value;
            LOGI("SPEEX_PREPROCESS_SET_DENOISE=>%d result:%d",*v,result);
        }
        else if(key == SPEEX_PREPROCESS_SET_AGC){
            int* v = (int*)value;
            LOGI("SPEEX_PREPROCESS_SET_AGC=>%d result:%d",*v,result);
        }else if(key == SPEEX_PREPROCESS_SET_DEREVERB){
            int* v = (int*)value;
            LOGI("SPEEX_PREPROCESS_SET_DEREVERB=>%d result:%d",*v,result);
        }else if(key == SPEEX_PREPROCESS_SET_VAD){
            int* v = (int*)value;
            &context->enable_vad == *v;
            LOGI("SPEEX_PREPROCESS_SET_VAD=>%d result:%d",*v,result);
        }else if(key == SPEEX_PREPROCESS_SET_NOISE_SUPPRESS){
            int* v = (int*)value;
            LOGI("SPEEX_PREPROCESS_SET_NOISE_SUPPRESS=>%d result:%d",*v,result);
        }else if(key == SPEEX_PREPROCESS_SET_AGC_LEVEL){
            int* v = (int*)value;
            LOGI("SPEEX_PREPROCESS_SET_AGC_LEVEL=>%d result:%d",*v,result);
        }
        pthread_mutex_unlock(&(context->mutex));
        if (result == 0) {
            return ECHO_OK;
        } else {
            LOGE("set echo_set_preprocess_ctl(%d) unknow key", key);
        }
    }
    return ECHO_ERROR;
}

int echo_get_preprocess_ctl(SpeexEcho *context, const unsigned short key, void *result) {
    ASSERT(context != NULL, "speexEcho is NULL")
    if (context != NULL && context->aec_init_flag == 1) {
        int ret = speex_preprocess_ctl(context->den, key, result);
        if (ret == 0) {
            return ECHO_OK;
        } else {
            LOGE("get echo_set_preprocess_ctl(%d) unknow key", key);
        }
    }
    return ECHO_ERROR;
}

int echo_sync_process(const SpeexEcho *context, unsigned short *near, unsigned short *far,
                      unsigned short *out,size_t size) {
    ASSERT(context != NULL, "speexEcho is NULL")
    ASSERT(near != NULL, "mic record data is NULL")
    ASSERT(far != NULL, "playData is NULL")
    ASSERT(out != NULL, "outputBuffer is NULL")

    if (context != NULL && context->aec_init_flag == 1) {
        pthread_mutex_lock(&(context->mutex));
        // speex_preprocess_run(context->den, far);
        speex_echo_cancellation(context->st, near, far, out);
        int result = speex_preprocess_run(context->den, out);
        pthread_mutex_unlock(&(context->mutex));
        return result;
    }
    return ECHO_ERROR;

}

int echo_denoise_only(const SpeexEcho* context,unsigned short* near){
    ASSERT(near != NULL, "near data is NULL")
    pthread_mutex_lock(&(context->mutex));
    int result = speex_preprocess_run(context->den, near);
    pthread_mutex_unlock(&(context->mutex));
    return result;
}

int echo_asyn_process_near(const SpeexEcho* context,unsigned short* near,unsigned short *out){
    if (context != NULL && context->aec_init_flag == 1) {
        pthread_mutex_lock(&(context->mutex));
        speex_echo_capture(context->st,near,out);
        pthread_mutex_unlock(&(context->mutex));
        return ECHO_OK;
    }
    return ECHO_ERROR;
}

int echo_asyn_process_far(const SpeexEcho* context,unsigned short* far){
    if (context != NULL && context->aec_init_flag == 1) {
        pthread_mutex_lock(&(context->mutex));
        speex_echo_playback(context->st,far);
        pthread_mutex_unlock(&(context->mutex));
        return ECHO_OK;
    }
    return ECHO_ERROR;
}

void echo_distroy(SpeexEcho *context) {
    if (context != NULL && context->aec_init_flag == 1) {
        pthread_mutex_lock(&(context->mutex));
        speex_preprocess_state_destroy(context->den);
        speex_echo_state_destroy(context->st);
        context->aec_init_flag = 0;
        pthread_mutex_unlock(&(context->mutex));
        LOGE("release speex echo");
    }
}

