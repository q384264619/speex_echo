//
// Created by vnetoo on 2020/5/8.
//

#include <stdlib.h>
#include <assert.h>
#include <jni.h>
#include <string.h>
#include <pthread.h>

#include "include/audio_echo.h"

#define ClassName "com/vnetoo/speex/echo/SpeexEcho"

#define EchoPk(method) Java_com_vnetoo_speex_echo_SpeexEcho_##method

static SpeexEcho echo;

static short *out;

int debug = 1;

inline void throwException(JNIEnv *env, const char *clasz, const char *errorMsg);

void throwException(JNIEnv *env, const char *clasz, const char *errorMsg) {
    jclass newExcCls;

    (*env)->ExceptionDescribe(env);

    (*env)->ExceptionClear(env);

    newExcCls = (*env)->FindClass(env, clasz);
    if (newExcCls != NULL) {
        (*env)->ThrowNew(env, newExcCls, errorMsg);
    }

}


JNIEXPORT jint JNICALL
EchoPk(aecInit)(JNIEnv *env, jclass jclasz, jint frameSize, jint filterSize, jint sampleRate) {

    int result = echo_init(&echo, frameSize, filterSize, sampleRate);

    if (result == ECHO_ERROR) {

        throwException(env, "java/lang/IllegalStateException", "init aec error");

    }

    echo.frame_size = frameSize;
    LOGI("speex jni init aec success");

    return result;
}

JNIEXPORT jint JNICALL
EchoPk(aec_1set_1feature)(JNIEnv *env, jclass jclasz, jint key, jint value) {
    if (echo.aec_init_flag) {
        return echo_set_ctl(&echo, key, value);
    }
    return ECHO_ERROR;
}


JNIEXPORT jint JNICALL
EchoPk(aec_1set_1other_1feature__II)(JNIEnv *env, jclass clazz,
                                     jint key, jint value) {
    if (echo.aec_init_flag) {
        int v = value;
        return echo_set_preprocess_ctl(&echo, key, &v);
    }
    return ECHO_ERROR;
}

JNIEXPORT jint JNICALL
EchoPk(aec_1set_1other_1feature__IF)(JNIEnv *env, jclass clazz,
                                     jint key, jfloat value) {
    if (echo.aec_init_flag) {
        float v = value;
        return echo_set_preprocess_ctl(&echo, key, &v);
    }
    return ECHO_ERROR;
}

JNIEXPORT jint JNICALL
EchoPk(aec_1process)(JNIEnv *env, jclass clazz, jbyteArray near,
                     jbyteArray far,jbyteArray out) {
    jbyteArray ret = NULL;
    jbyte *nearData = (*env)->GetByteArrayElements(env, near, JNI_FALSE);
    jbyte *fearData = (*env)->GetByteArrayElements(env, far, JNI_FALSE);
    jbyte *outData = (*env)->GetByteArrayElements(env, out, JNI_FALSE);
    int result = 0;
    bool needRet = false;
    if(echo.enable_echo == 1){
        result= echo_sync_process(&echo, nearData, fearData, outData, echo.frame_size);
        LOGI("echo canncel :%d", result );
    }
    else{
        result = echo_denoise_only(&echo,nearData);
        LOGI("echo_denoise_only:%d", result );
    }
    (*env)->ReleaseByteArrayElements(env, near, nearData, JNI_FALSE);
    (*env)->ReleaseByteArrayElements(env, far, fearData, JNI_FALSE);
    (*env)->ReleaseByteArrayElements(env, out, outData, JNI_FALSE);
    return result;
}

JNIEXPORT void JNICALL
EchoPk(destory)(JNIEnv *env, jclass clazz) {
    // pthread_mutex_lock(&(echo.mutex));
    echo_distroy(&echo);
    free(out);
    // pthread_mutex_unlock(&(echo.mutex));
    out = NULL;
}

JNIEXPORT jint JNICALL
EchoPk(aec_1set_1enable_1echo)(JNIEnv *env, jclass clazz,
                               jboolean enable) {
    pthread_mutex_lock(&(echo.mutex));
    echo.enable_echo = enable ? 1 : 0;
    LOGI("echo.enable_echo=%d", echo.enable_echo);
    pthread_mutex_unlock(&(echo.mutex));
    return ECHO_OK;
}

JNIEXPORT jfloat JNICALL
EchoPk(aec_1get_1other_1float_1feature)(JNIEnv *env, jclass clazz,

                                        jint key) {
    pthread_mutex_lock(&(echo.mutex));
    float value = 0;
    echo_get_preprocess_ctl(&(echo),key,&value);
    pthread_mutex_unlock(&(echo.mutex));
    return value;
}

JNIEXPORT jint JNICALL
EchoPk(aec_1get_1other_1int_1feature)(JNIEnv *env, jclass clazz,
                                      jint key) {
    pthread_mutex_lock(&(echo.mutex));
    int  value = 0;
    echo_get_preprocess_ctl(&(echo),key,&value);
    pthread_mutex_unlock(&(echo.mutex));
    return value;
}

JNIEXPORT jint JNICALL
Java_com_vnetoo_speex_echo_SpeexEcho_ProcessStream(JNIEnv *env, jclass clazz, jbyteArray data,
                                                   jint offset, jint len) {


    jbyte *nearData = (*env)->GetByteArrayElements(env, data, JNI_FALSE);
        short*  out = (short*)malloc(len);
        memset(out,0,len);
        echo_asyn_process_near(&echo,nearData,out);
        memcpy(nearData,out,len);
        free(out);

    (*env)->ReleaseByteArrayElements(env, data, nearData, JNI_FALSE);
    return 0;
}

JNIEXPORT jint JNICALL
Java_com_vnetoo_speex_echo_SpeexEcho_ProcessReverseStream(JNIEnv *env, jclass clazz,
                                                          jbyteArray far_end, jint offset,
                                                          jint len) {

    jbyte *nearData = (*env)->GetByteArrayElements(env, far_end, JNI_FALSE);

        echo_asyn_process_far(&echo,nearData);

    (*env)->ReleaseByteArrayElements(env, far_end, nearData, JNI_FALSE);
    return 0;
}