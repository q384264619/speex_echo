package com.vnetoo.speex.echo;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Context;
import android.media.AudioManager;
import android.media.audiofx.AcousticEchoCanceler;
import android.media.audiofx.AudioEffect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.CompoundButton;
import android.widget.SeekBar;
import android.widget.Switch;
import android.widget.TextView;

import com.vnetoo.speex.echo.device.AudioDevice;
import com.vnetoo.speex.echo.device.AudioRecordDataCallBack;
import com.vnetoo.speex.echo.dsp.AudioDsp;
import com.vnetoo.speex.echo.dsp.SimpleAudioDspFactory;
import com.vnetoo.speex.echo.dsp.SpeexAudioDsp;

import java.util.Arrays;

public class MainActivity extends AppCompatActivity implements AudioRecordDataCallBack {

    private AudioQuality defaultQuality = AudioQuality.DEFAULT_AUDIO_QUALITY;

    AudioDsp speexDsp = null;

    private AudioDevice audioDevice;

    private Handler mHandler = new Handler();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        defaultQuality = AudioGlobal.quality;

        speexDsp = SimpleAudioDspFactory.create(SimpleAudioDspFactory.SPEEX, defaultQuality);
        //record
        Switch st = findViewById(R.id.switch3);
        st.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (audioDevice == null) {
                    audioDevice = new AudioDevice();
                    audioDevice.setAudioRecordDataCallBack(MainActivity.this);
                }
                if (isChecked) {
                    try {
                        audioDevice.startRecorder();
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } else {
                    audioDevice.stopRecorder();
                }
            }
        });

        // echo
        st = findViewById(R.id.switch4);
        st.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (speexDsp == null) {
                    return;
                }
                speexDsp.enableEcho(isChecked);
            }
        });

        //vad
        st = findViewById(R.id.switch6);
        st.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (speexDsp == null) {
                    return;
                }
                speexDsp.enableVad(isChecked);

            }
        });
        //noise
        st = findViewById(R.id.switch7);
        st.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                speexDsp.enableDenoiseSuppression(isChecked);
            }
        });

        //agc
        st = findViewById(R.id.switch8);
        st.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                speexDsp.enableAudiomicGainControl(isChecked);

            }
        });
        st = findViewById(R.id.switch_play);
        st.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (audioDevice == null) {
                    audioDevice = new AudioDevice();
                    audioDevice.setAudioRecordDataCallBack(MainActivity.this);
                }
                if (isChecked) {
                    audioDevice.startAudioTrackPlay();
                } else {
                    audioDevice.stopAudioTrack();
                }
            }
        });

        SeekBar sb = findViewById(R.id.agc_value);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (speexDsp == null) {
                    return;
                }
                if (speexDsp instanceof SpeexAudioDsp) {
                    SpeexAudioDsp dsp = (SpeexAudioDsp) speexDsp;
                    dsp.getSpeex().setAudioPreProcessAttr(SpeexEcho.SPEEX_PREPROCESS_SET_AGC_LEVEL, progress * 8000);
                }

            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                TextView tv = findViewById(R.id.agc_level);
                tv.setText("AGC等级(" + (seekBar.getProgress() * 8000) + ")");
            }
        });
        sb = findViewById(R.id.denoise_value);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (speexDsp == null) {
                    return;
                }
                //noiseSuppress的值可以控制减除的噪声强度，负值越小，噪声去除的强度越大，
                //
                //同时会造成原声的失真，需要作出权衡


                if (speexDsp instanceof SpeexAudioDsp) {
                    SpeexAudioDsp dsp = (SpeexAudioDsp) speexDsp;
                    dsp.getSpeex().setAudioPreProcessAttr(SpeexEcho.SPEEX_PREPROCESS_SET_NOISE_SUPPRESS, progress * -10);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                TextView tv = findViewById(R.id.denoise_level);
                tv.setText("降噪DB(" + (seekBar.getProgress() * -10) + "db)");
            }
        });

        sb = findViewById(R.id.vad_start_value);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (speexDsp == null) {
                    return;
                }
                if (speexDsp instanceof SpeexAudioDsp) {
                    SpeexAudioDsp dsp = (SpeexAudioDsp) speexDsp;
                    dsp.getSpeex().setAudioPreProcessAttr(SpeexEcho.SPEEX_PREPROCESS_SET_PROB_START, progress);
                }


            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {

                TextView tv = findViewById(R.id.vad_start);
                tv.setText("静音检百分比(" + (seekBar.getProgress()) + "%)");
            }
        });
        sb = findViewById(R.id.vad_start_c_value);
        sb.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if (speexDsp == null) {
                    return;
                }

                if (speexDsp instanceof SpeexAudioDsp) {
                    SpeexAudioDsp dsp = (SpeexAudioDsp) speexDsp;
                    dsp.getSpeex().setAudioPreProcessAttr(SpeexEcho.SPEEX_PREPROCESS_SET_PROB_CONTINUE, progress);
                }
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {

            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                TextView tv = findViewById(R.id.vad_start_c);
                tv.setText("静音检测连续百分比(" + (seekBar.getProgress()) + "%)");
            }
        });


        ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.RECORD_AUDIO}, 0);

        udpate();
    }

    private void udpate() {
        if (speexDsp instanceof SpeexAudioDsp) {
            SpeexAudioDsp dsp = (SpeexAudioDsp) speexDsp;


            Switch st = findViewById(R.id.switch6); // vad
            st.setChecked(speexDsp.isEnableVad());
            st = findViewById(R.id.switch8); // agc
            st.setChecked(dsp.isEnableAudiomicGainControl());
            st = findViewById(R.id.switch7);
            st.setChecked(speexDsp.isEnableDenoiseSuppression());//denoise;
            SeekBar sb = findViewById(R.id.denoise_value);
            int value = dsp.getSpeex().getAudioPreProcessAttr(SpeexEcho.SPEEX_PREPROCESS_GET_ECHO_SUPPRESS);
            Log.e("petter", "SPEEX_PREPROCESS_GET_ECHO_SUPPRESS=>" + value);
            sb.setProgress((value * -1) / 10);
            sb = findViewById(R.id.agc_value);
            value = dsp.getSpeex().getAudioPreProcessAttr(SpeexEcho.SPEEX_PREPROCESS_GET_AGC_LEVEL);
            if (1174011904 == value) {
                value = 0;
            }
            Log.e("petter", "SPEEX_PREPROCESS_GET_AGC_LEVEL=>" + value);
            sb.setProgress((int) value);

            sb = findViewById(R.id.vad_start_value);
            value = dsp.getSpeex().getAudioPreProcessAttr(SpeexEcho.SPEEX_PREPROCESS_GET_PROB_START);
            Log.e("petter", "SPEEX_PREPROCESS_GET_PROB_START=>" + value);
            sb.setProgress(value);

            sb = findViewById(R.id.vad_start_c_value);
            value = dsp.getSpeex().getAudioPreProcessAttr(SpeexEcho.SPEEX_PREPROCESS_GET_PROB_CONTINUE);
            Log.e("petter", "SPEEX_PREPROCESS_GET_PROB_CONTINUE=>" + value);
            sb.setProgress(value);
        }
    }

    @Override
    public void onAudioRecordDataCallBack(byte[] data, int len) {
            if(audioDevice!=null){
               final  byte[] temp = Arrays.copyOf(data,len);
               final  int size = len;
               //模拟300ms延时
                mHandler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        audioDevice.write(temp,0,size);
                    }
                },300);

            }
    }
}
