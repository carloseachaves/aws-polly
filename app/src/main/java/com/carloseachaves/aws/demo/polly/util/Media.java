package com.carloseachaves.aws.demo.polly.util;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.util.Log;
import java.io.IOException;

/**
 * Created by carloseachaves on 24/06/2017.
 */

public class Media {

    private static final String TAG = Media.class.getName();
    private MediaInterface mediaInterface;
    private MediaPlayer mediaPlayer;

    public Media(MediaInterface mediaInterface){
        this.mediaInterface = mediaInterface;
    }

    private void setupNewMediaPlayer() {

        mediaPlayer = new MediaPlayer();

        mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.release();
                mediaPlayer = null;
                mediaInterface.callbackMedia();
            }
        });

        mediaPlayer.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });

        mediaPlayer.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                mp.release();
                mediaPlayer = null;
                mediaInterface.callbackMedia();
                return false;
            }
        });

    }

    public void play(String url){
        // Create a media player to play the synthesized audio stream.
        if (mediaPlayer == null || mediaPlayer.isPlaying()) {
            setupNewMediaPlayer();
        }
        mediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);

        try {
            mediaPlayer.setDataSource(url);
        } catch (IOException e) {
            Log.e(TAG, "Unable to set data source for the media player! " + e.getMessage());
        }

        mediaPlayer.prepareAsync();
    }
}
