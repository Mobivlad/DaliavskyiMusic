package com.nulp.daliavskyimusic;

import android.media.AudioManager;
import android.media.MediaPlayer;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.nulp.daliavskyimusic.logicComponents.MediaPlayerList;
import com.nulp.daliavskyimusic.uiComponents.MainActivity;

import java.io.IOException;
import android.os.Handler;

import rm.com.audiowave.AudioWaveView;

public class MusicPlayer {
    public static Handler h = new Handler();
    public static MainActivity mainActivity;
    public static InfoMusicActivity infoActivity;
    public static MediaPlayerList mpl;
    public static MediaPlayer mp;
    public static boolean isPause = true;
    public static boolean start = false;
    private static Runnable runnuble;

    public static void updateMediaPlayer(){
        if(mp!=null)mp.stop();
        showButton(true);
        mp = new MediaPlayer();
        mp.setOnPreparedListener(mp -> {
            mp.start();
            isPause = false;
            ((ImageButton)mainActivity.findViewById(R.id.button_play)).setImageResource(R.drawable.pause);
            if(infoActivity!=null){
                infoActivity.change();
                SeekBar waveView = infoActivity.findViewById(R.id.wave);
                waveView.setMax(mp.getDuration());
                TextView tv = ((TextView)infoActivity.findViewById(R.id.full_len));
                tv.setText(String.format("%02d:%02d", mp.getDuration()/1000/60,mp.getDuration()/1000%60));
            }
            showButton(false);
        });
        mp.setOnCompletionListener(mp -> {
            mpl.nextPlay();
            updateMediaPlayer();
            mainActivity.changeSelections();
            mainActivity.setPlayerInfo();
        });

        mp.setAudioStreamType(AudioManager.STREAM_MUSIC);
        try {
            mp.setDataSource(MusicPlayer.mpl.getCurrentPlayItem().getSong_url());
            mp.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
    public static void playMediaPlayer(){
        mp.start();
    }
    public static void pauseMediaPlayer(){
        mp.pause();
    }

    private static void showButton(boolean front){
        if(front){
            ((ProgressBar)mainActivity.findViewById(R.id.progressBar)).setVisibility(View.VISIBLE);
            ((ImageButton)mainActivity.findViewById(R.id.button_play)).setVisibility(View.INVISIBLE);
            if(infoActivity!=null){
                ((ProgressBar)infoActivity.findViewById(R.id.progressBar2)).setVisibility(View.VISIBLE);
                ((ImageButton)infoActivity.findViewById(R.id.button_play2)).setVisibility(View.INVISIBLE);
            }
        } else {
            ((ProgressBar)mainActivity.findViewById(R.id.progressBar)).setVisibility(View.INVISIBLE);
            ((ImageButton)mainActivity.findViewById(R.id.button_play)).setVisibility(View.VISIBLE);
            if(infoActivity!=null){
                ((ProgressBar)infoActivity.findViewById(R.id.progressBar2)).setVisibility(View.INVISIBLE);
                ((ImageButton)infoActivity.findViewById(R.id.button_play2)).setVisibility(View.VISIBLE);
            }
        }
    }
    public static void Cyrcle(){
            if(infoActivity!=null){
                TextView tv = ((TextView)infoActivity.findViewById(R.id.current_len));
                tv.setText(String.format("%02d:%02d", mp.getCurrentPosition()/1000/60,mp.getCurrentPosition()/1000%60));
                //AudioWaveView waveView = infoActivity.findViewById(R.id.wave);
                SeekBar waveView = infoActivity.findViewById(R.id.wave);
                waveView.setProgress(mp.getCurrentPosition());
            }
            runnuble = new Runnable(){
                @Override
                public void run() {
                    Cyrcle();
                }
            };
            h.postDelayed(runnuble, 100);
    }
}
