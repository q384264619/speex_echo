package com.vnetoo.speex.echo.util;

import java.io.IOException;
import java.io.PipedInputStream;
import java.io.PipedOutputStream;

public class EchoFixedFrameStream {
    private int frameSize = AudioSampleUtil.FRAME_SIZE;

    private PipedInputStream pipedInputStream;
    private PipedOutputStream pipedOutputStream;

    public EchoFixedFrameStream(int frameSize) {
        this.frameSize = frameSize;
        initInnerBuffer();
    }

    public EchoFixedFrameStream() {
        initInnerBuffer();
    }

    private void initInnerBuffer() {
        pipedInputStream = new PipedInputStream(frameSize * 20);
        try {
            pipedOutputStream = new PipedOutputStream(pipedInputStream);
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    public boolean write(byte[] data, int len) throws IOException {
        pipedOutputStream.write(data, 0, len);
        return true;
    }

    public boolean readAudioFrame(byte[] output) throws IOException {
        int counts = pipedInputStream.read(output, 0, frameSize);
        if (counts < 0) {
            return false;
        }
        return true;
    }

    public void close(){
        try {
            pipedInputStream.close();
            pipedOutputStream.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
