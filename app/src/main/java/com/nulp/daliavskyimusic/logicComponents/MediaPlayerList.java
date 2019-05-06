package com.nulp.daliavskyimusic.logicComponents;

import android.content.Context;

import com.nulp.daliavskyimusic.logicComponents.parser.SongInformation;
import com.nulp.daliavskyimusic.uiComponents.MusicItemAdapter;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class MediaPlayerList implements Serializable{
    public MediaPlayerList() {
        list = new ArrayList<>();
        mia = null;
    }

    private transient MusicItemAdapter mia;
    private List<SongInformation> list;
    private int currentPlay = -1;

    public List<SongInformation> getList() {
        return list;
    }

    public int getCurrentPlayIndex() {
        return currentPlay;
    }

    public SongInformation getCurrentPlayItem() {
        return list.get(currentPlay);
    }

    public void setCurrentPlay(int currentPlay) {
        this.currentPlay = currentPlay;
        MusicPlayer.currentSong = list.get(currentPlay);
    }

    public void nextPlay() {
        this.currentPlay++;
        if(currentPlay==list.size()){
            currentPlay=0;
        }
        MusicPlayer.currentSong = list.get(currentPlay);
    }

    public void prevPlay() {
        this.currentPlay--;
        if(currentPlay==-1){
            currentPlay=list.size()-1;
        }
        MusicPlayer.currentSong = list.get(currentPlay);
    }

    public String[] getHrefArray(){
        String s[] = new String[list.size()];
        for(int i=0;i<list.size();i++){
            s[i]=list.get(i).getSong_href();
        }
        return s;
    }

    public boolean adapterIsNull(){
        return mia==null;
    }

    public void createAdapter(Context context){
        mia = new MusicItemAdapter(context,list);
    }

    public MusicItemAdapter getAdapter(){
        return mia;
    }
}
