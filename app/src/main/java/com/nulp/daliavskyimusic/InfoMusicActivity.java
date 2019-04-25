package com.nulp.daliavskyimusic;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.makeramen.roundedimageview.RoundedImageView;
import com.nulp.daliavskyimusic.logicComponents.MediaPlayerList;

public class InfoMusicActivity extends AppCompatActivity {

    public static Intent getIntent(Activity ctx, MediaPlayerList mpl){
        Intent i = new Intent(ctx,InfoMusicActivity.class);
        i.putExtra("mpl",mpl);
        return i;
    }

    private void change(){

        ((TextView)findViewById(R.id.music_item_song_name)).setText(mp.getCurrentPlayItem().getSong_name());
        ((TextView)findViewById(R.id.music_item_author_name)).setText(mp.getCurrentPlayItem().getAuthor_name());
        Glide
                .with(getApplicationContext())
                .load(mp.getCurrentPlayItem().getImage_href())
                .placeholder(R.drawable.face)
                .into((RoundedImageView)findViewById(R.id.select_item_image));
    }
    MediaPlayerList mp = null;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_music);
        getSupportActionBar().hide();
        mp = (MediaPlayerList)getIntent().getExtras().get("mpl");
        mp.setCurrentPlay(mp.getCurrentPlayIndex()+1);
        change();
    }

    @Override
    public void finish() {
        getIntent().putExtra("curent_play",mp.getCurrentPlayIndex());
        setResult(RESULT_OK,getIntent());
        super.finish();
    }
}
