package com.nulp.daliavskyimusic;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

public class InfoMusicActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_info_music);
        getSupportActionBar().hide();
    }

    @Override
    public void finish() {
        super.finish();
        overridePendingTransition(R.anim.second_anim,R.anim.first_anim_out);
    }
}
