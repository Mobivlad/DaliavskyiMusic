package com.nulp.daliavskyimusic;

import android.app.Activity;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ImageButton;
import android.widget.PopupMenu;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nulp.daliavskyimusic.logicComponents.MediaPlayerList;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import rm.com.audiowave.AudioWaveView;
import wseemann.media.FFmpegMediaMetadataRetriever;

public class InfoMusicActivity extends AppCompatActivity {

    public static Intent getIntent(Activity ctx){
        return new Intent(ctx,InfoMusicActivity.class);
    }

    public void change(){
        ((TextView)findViewById(R.id.music_item_song_name)).setText(mp.getCurrentPlayItem().getSong_name());
        ((TextView)findViewById(R.id.music_item_author_name)).setText(mp.getCurrentPlayItem().getAuthor_name());
        Glide
                .with(getApplicationContext())
                .load(mp.getCurrentPlayItem().getImage_href())
                .placeholder(R.drawable.face)
                .into((RoundedImageView)findViewById(R.id.select_item_image));
        if(MusicPlayer.isPause){
            ((ImageButton)findViewById(R.id.button_play2)).setImageResource(R.drawable.play);
        }
        else {
            ((ImageButton)findViewById(R.id.button_play2)).setImageResource(R.drawable.pause);
        }
    }
    MediaPlayerList mp = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_music);
        getSupportActionBar().hide();
        mp = MusicPlayer.mpl;
        MusicPlayer.infoActivity = this;
        if(!MusicPlayer.start){
            MusicPlayer.Cyrcle();
            MusicPlayer.start=true;
        }
        change();
        SeekBar waveView = findViewById(R.id.wave);
        waveView.setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                if(fromUser)MusicPlayer.mp.seekTo(progress);
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
                MusicPlayer.isPause = true;
            }

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
                MusicPlayer.isPause = false;
            }
        });
        waveView.setMax(MusicPlayer.mp.getDuration());
        ((TextView)findViewById(R.id.full_len)).setText(String.format("%02d:%02d", MusicPlayer.mp.getDuration()/1000/60,MusicPlayer.mp.getDuration()/1000%60));

    }

    public void showPopup(View v){
        PopupMenu p = new PopupMenu(InfoMusicActivity.this,v);
        p.setOnMenuItemClickListener(item -> {
            switch (item.getItemId()){
                case R.id.author_href:
                    open(MusicPlayer.mpl.getCurrentPlayItem().getSong_href());
                    return true;
                case R.id.song_href:
                    open(MusicPlayer.mpl.getCurrentPlayItem().getAuthor_href());
                    return true;
                case R.id.download:
                    open(MusicPlayer.mpl.getCurrentPlayItem().getSong_url());
                    return true;
            }
            return false;
        });
        p.inflate(R.menu.poput_menu);
        p.show();
    }

    @Override
    public void finish() {
        super.finish();
        setResult(RESULT_OK,getIntent());
        MusicPlayer.infoActivity = null;
    }

    public void clickAction(View view) {
        switch (view.getId()){
            case R.id.button_play2:
                if(MusicPlayer.isPause){
                    ((ImageButton) view).setImageResource(R.drawable.pause);
                    MusicPlayer.playMediaPlayer();
                }
                else {
                    ((ImageButton) view).setImageResource(R.drawable.play);
                    MusicPlayer.pauseMediaPlayer();
                }
                MusicPlayer.isPause=!MusicPlayer.isPause;
                break;
            case R.id.button_next2:
                MusicPlayer.mpl.nextPlay();
                break;
            case R.id.button_back2:
                MusicPlayer.mpl.prevPlay();
                break;
        }
        switch (view.getId()){
            case R.id.button_next2:
            case R.id.button_back2:
                MusicPlayer.isPause = false;
                MusicPlayer.updateMediaPlayer();
                change();
        }
    }
    private void share(String s){
        Intent waIntent = new Intent(Intent.ACTION_SEND);
        waIntent.setType("text/plain");
        waIntent.putExtra(Intent.EXTRA_TEXT, s);//
        startActivity(Intent.createChooser(waIntent, "Поширити посилання з"));
    }
    private void open(String s){
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setData(Uri.parse(s));
        startActivity(i);
    }
}
