package com.nulp.daliavskyimusic.logicComponents;

import android.content.Context;

import com.nulp.daliavskyimusic.logicComponents.parser.ItemInform;
import com.nulp.daliavskyimusic.uiComponents.MusicItemAdapter;

import java.util.ArrayList;
import java.util.List;

public class MediaPlayerList {
    public MediaPlayerList() {
        list = new ArrayList<>();
        mia = null;
    }

    private MusicItemAdapter mia;
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