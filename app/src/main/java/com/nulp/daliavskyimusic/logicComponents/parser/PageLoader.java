package com.nulp.daliavskyimusic.logicComponents.parser;

import java.util.List;

public class PageLoader {
    private int page;
    private String[] args;
    private String url;
    private String lastFirst = "";
    private boolean isEnd;

    public boolean isEnd() {
        return isEnd;
    }

    public PageLoader(String url,String[] args) {
        this.args = args;
        this.url = url;
        page = 1;
        isEnd = false;
    }
    public int loadTo(List<SongInformation> dest){
        String[] _args = new String[args.length+2];
        System.arraycopy(args,0,_args,0,args.length);
        _args[args.length]="page";
        _args[args.length+1]=String.valueOf(page);
        List<SongInformation> _list = MusicLoader.getData(url,_args);
        if(_list.size()==0)return -1;
        if(_list.get(0).getSong_href().equals(lastFirst)){
            isEnd = true;
            return 0;
        }
        lastFirst = _list.get(0).getSong_href();
        page++;
        dest.addAll(_list);
        return _list.size();
    }
}
