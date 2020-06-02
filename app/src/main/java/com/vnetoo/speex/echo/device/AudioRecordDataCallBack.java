package com.vnetoo.speex.echo.device;

public interface AudioRecordDataCallBack {
    void onAudioRecordDataCallBack(byte[] data, int len);
}
