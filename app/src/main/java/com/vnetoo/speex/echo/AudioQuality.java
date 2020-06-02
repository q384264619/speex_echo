package com.vnetoo.speex.echo;

/**
 * Created by win7 on 2017-11-06.
 */

public class AudioQuality {

    /**
     * Default audio stream quality.
     */
    public final static AudioQuality DEFAULT_AUDIO_QUALITY = new AudioQuality(16000, 32000,1);

    /**
     * Represents a quality for a video stream.
     */
    public AudioQuality() {
    }

    /**
     * Represents a quality for an audio stream.
     *
     * @param samplingRate The sampling rate
     * @param bitRate      The bitrate in bit per seconds
     */
    public AudioQuality(int samplingRate, int bitRate, int chanel) {
        this.samplingRate = samplingRate;
        this.bitRate = bitRate;
        this.chanel = chanel;
    }

    public int samplingRate = 0;
    public int bitRate = 0;
    public int chanel = 1;

    public boolean equals(AudioQuality quality) {
        if (quality == null) return false;
        return (quality.samplingRate == this.samplingRate &
                quality.bitRate == this.bitRate);
    }

    public AudioQuality clone() {
        return new AudioQuality(samplingRate, bitRate,chanel);
    }

    public static AudioQuality parseQuality(String str) {
        AudioQuality quality = DEFAULT_AUDIO_QUALITY.clone();
        if (str != null) {
            String[] config = str.split("-");
            try {
                quality.bitRate = Integer.parseInt(config[0]) * 1000; // conversion to bit/s
                quality.samplingRate = Integer.parseInt(config[1]);
            } catch (IndexOutOfBoundsException ignore) {
            }
        }
        return quality;
    }

}
