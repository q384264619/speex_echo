package com.vnetoo.speex.echo.device;

import android.media.AudioFormat;
import android.media.AudioManager;
import android.media.AudioRecord;
import android.media.AudioTrack;
import android.media.MediaRecorder;
import android.util.Log;

import com.vnetoo.speex.echo.AudioGlobal;
import com.vnetoo.speex.echo.AudioQuality;
import com.vnetoo.speex.echo.BuildConfig;
import com.vnetoo.speex.echo.dsp.AudioDsp;
import com.vnetoo.speex.echo.dsp.AudioDspUtil;
import com.vnetoo.speex.echo.dsp.SimpleAudioDspFactory;


/**
 * 同时作为播放器与录音器，
 * 为了可以处理回声消除，将两者放到一起
 */
public class AudioDevice  implements Runnable {

    private static AudioDevice mStance;

    public static final AudioDevice getInstance() {
        synchronized (AudioDevice.class) {
            if (mStance == null) {
                mStance = new AudioDevice();
            }
        }
        return mStance;
    }

    // record
    private AudioRecord mAudioRecord;
    private AudioQuality audioQuality;
    private AudioRecordDataCallBack audioRecordDataCallBack;
    private int bufferSize;
    protected int mAudioSource = MediaRecorder.AudioSource.VOICE_COMMUNICATION;
    private boolean isAudioRecordConfiged = false;
    private volatile boolean isRecordStart = false;

    private static final String TAG = "AudioDevice";

    // audioTrack
    private AudioTrack audioPlayer;
    private int playMinBufferSize;
    private boolean isPlayRuning = false;
    private boolean isPlayerConfiged = false;

    //audioDsp
    private int bytesPerFrame = 0;
    private AudioDsp echoHandler;


    // play and Record thread
    private Thread mThread = null;

    public AudioDevice() {
        audioQuality = AudioGlobal.quality;
        bytesPerFrame = AudioDspUtil.getFrameSizeInBytes(AudioGlobal.quality.samplingRate, AudioGlobal.quality.chanel);

    }

    private void initAudioRecorder() {
        bufferSize = AudioDspUtil.getMinSizeInBytesForRecordBuffer(audioQuality.samplingRate);
        mAudioSource = AudioGlobal.currentEchotype == SimpleAudioDspFactory.HARD ? MediaRecorder.AudioSource.VOICE_COMMUNICATION:(BuildConfig.DEBUG? MediaRecorder.AudioSource.MIC: MediaRecorder.AudioSource.VOICE_COMMUNICATION);
        mAudioRecord = new AudioRecord(mAudioSource, audioQuality.samplingRate, audioQuality.chanel == 1 ? AudioFormat.CHANNEL_IN_MONO : AudioFormat.CHANNEL_IN_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, bufferSize);
        AudioGlobal.audioSession = mAudioRecord.getAudioSessionId();


        //得到echo
        echoHandler = SimpleAudioDspFactory.create(AudioGlobal.currentEchotype, audioQuality.samplingRate, audioQuality.chanel);

        isAudioRecordConfiged = true;
    }

    private void initAudioTrack() {
        playMinBufferSize = AudioDspUtil.getMinSizeInBytesForPlayBuffer(audioQuality.samplingRate);
        audioPlayer = new AudioTrack(AudioManager.STREAM_MUSIC, audioQuality.samplingRate,
                audioQuality.chanel == 1 ? AudioFormat.CHANNEL_OUT_MONO : AudioFormat.CHANNEL_OUT_STEREO,
                AudioFormat.ENCODING_PCM_16BIT, playMinBufferSize,
                AudioTrack.MODE_STREAM);
        audioPlayer.setVolume(1);

    }


    private void start() {
        if (mThread == null || !mThread.isAlive()) {
            mThread = new Thread(this);
            mThread.setDaemon(true);
            mThread.setName("audio Recorder");
            mThread.start();
        }
    }



    public void startRecorder() throws Exception {
        if (isRecordStart) return;
        if (!isAudioRecordConfiged) {
            initAudioRecorder();
        }
        mAudioRecord.startRecording();
        isRecordStart= true;
        Log.i(TAG,"startRecorder");
        start();
    }


    public void setAudioRecordDataCallBack(AudioRecordDataCallBack audioRecordDataCallBack) {
            this.audioRecordDataCallBack = audioRecordDataCallBack;
    }


    public void stopRecorder() throws IllegalStateException {
        if (!isRecordStart || !isAudioRecordConfiged) return;

        mAudioRecord.stop();
        Log.i(TAG,"stopRecorder");
        isRecordStart = false;
    }


    public AudioQuality getAudioQuality() {
        return audioQuality;
    }


    public void release() {
        if (isRecordStart) {
            mAudioRecord.stop();
            isRecordStart = true;
            mAudioRecord.release();
            mAudioRecord = null;
            isAudioRecordConfiged = false;
            AudioGlobal.audioSession = -1;
        }
    }

    @Override
    public void run() {

        long commonSleepTime = AudioGlobal.FRAME_SIZE - 1;//10ms

        int readCounts = 0;

        byte[] recordCache = new byte[bytesPerFrame];

        byte[] playBufferCache = new byte[bytesPerFrame];
        boolean isWriteToHAL = false;
        int echoResult = 0;
        long t_capture=0,t_render = 0,t_analyze=0,t_process=0,delay=0;
        long dealStarttime = 0;
        long dealEndtime = 0;
        try {
            while (isPlayRuning || isRecordStart) {
                if(commonSleepTime>0){
                    Thread.sleep(commonSleepTime);
                }
                dealStarttime = System.currentTimeMillis();
                if (isPlayRuning) {
                    if (CirCleBufer.getUsedSize() >= bytesPerFrame) {
                        //从循环buffer读取一帧数据
                        CirCleBufer.read(playBufferCache, bytesPerFrame);
                        //只有开启录音时，才处理
                        if(isRecordStart && echoHandler!=null){
                            t_analyze = System.currentTimeMillis();
                            //作为far-end参数波型
                            echoHandler.ProcessReverseAudio(playBufferCache, 0, bytesPerFrame);

                            t_render = System.currentTimeMillis()+5;
                        }
                        //送入声卡播放
                        if(audioPlayer!=null)
                        audioPlayer.write(playBufferCache, 0, bytesPerFrame);
                        isWriteToHAL = true;
                    } else {
                        isWriteToHAL = false;
                    }
                }

                if (isRecordStart) {
                    t_capture = System.currentTimeMillis();
                    //从mic中取出一帧录音
                    readCounts = mAudioRecord.read(recordCache, 0, bytesPerFrame);
                    if (readCounts == bytesPerFrame) {
                        if(echoHandler!=null){
                            t_process =  System.currentTimeMillis()+5;
                            //计算延时
                            delay = (t_render - t_analyze) + (t_process - t_capture) +AudioGlobal.webDelayPack;

                            echoHandler.setDelayTimeInMs((int) delay);
                            //作为near-end处理回
                            echoResult=echoHandler.ProcessAudio(recordCache,0,bytesPerFrame);

                            if(echoResult == AudioDsp.CODE_VAD){
                               // Log.i(TAG,"vad~~~~~~~~~~~~~~~~~~");
                                continue;
                            }
                        }

                        if (audioRecordDataCallBack != null) {
                            audioRecordDataCallBack.onAudioRecordDataCallBack(recordCache, bytesPerFrame);
                        }
                    }

                }
                dealEndtime = System.currentTimeMillis()-dealStarttime;

                if(dealEndtime<AudioGlobal.FRAME_SIZE){
                    commonSleepTime =  AudioGlobal.FRAME_SIZE -dealEndtime-1;
                }
                if(commonSleepTime<=0){
                    commonSleepTime = 0;
                }

            }
        } catch (InterruptedException e) {
            e.printStackTrace();
            release();//释放录音器
            stopAudioTrack();//关闭播放器
        }

        mStance = null;
        if (echoHandler != null) {
            echoHandler.destroy();
        }
        CirCleBufer.setEmpty();
        if(echoHandler!=null){
            echoHandler.destroy();
        }
        AudioGlobal.audioSession = -1;
        echoHandler = null;
        Log.e(TAG, "audioDevice destroyed");
    }

    // player
    public void startAudioTrackPlay() {
        if (isPlayRuning) return;
        isPlayRuning = true;
        if (!isPlayerConfiged) {
            initAudioTrack();
        }
        audioPlayer.play();
        CirCleBufer.setEmpty();
        start();
    }

    public void write(byte[] data, int offset, int len) {
        if(!isPlayRuning)return;
        if (CirCleBufer.getFreeSize() < bytesPerFrame) {
            Log.e(TAG, "circleBuffer is Full now");
            return;
        }

        CirCleBufer.write(data, len);
    }

    public void stopAudioTrack() {
        if (!isPlayRuning) return;
        isPlayRuning = false;
        if (isPlayerConfiged) {
            if (audioPlayer != null) {
                audioPlayer.stop();
                audioPlayer.release();
            }
        }
        audioPlayer = null;
        isPlayerConfiged = false;

    }

}
