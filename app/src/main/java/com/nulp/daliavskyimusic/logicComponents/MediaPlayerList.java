package com.nulp.daliavskyimusic.logicComponents;

import android.content.Context;

import com.nulp.daliavskyimusic.logicComponents.parser.ItemInform;
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
    private List<ItemInform> list;
    private int currentPlay = -1;

    public List<ItemInform> getList() {
        return list;
    }

    public int getCurrentPlayIndex() {
        return currentPlay;
    }

    public ItemInform getCurrentPlayItem() {
        return list.get(currentPlay);
    }

    public void setCurrentPlay(int currentPlay) {
        this.currentPlay = currentPlay;
    }

    public void nextPlay() {
        this.currentPlay++;
        if(currentPlay==list.size()){
            currentPlay=0;
        }
    }

    public void prevPlay() {
        this.currentPlay--;
        if(currentPlay==-1){
            currentPlay=list.size()-1;
        }
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
