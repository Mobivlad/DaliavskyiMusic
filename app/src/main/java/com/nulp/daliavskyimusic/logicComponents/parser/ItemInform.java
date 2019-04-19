package com.nulp.daliavskyimusic.logicComponents.parser;

public class ItemInform {
    private String image_href;
    private String len;
    private String author_name;
    private String author_href;
    private String song_name;
    private String song_href;
    private String song_url;

    public String getImage_href() {
        return image_href;
    }

    public void setImage_href(String image_href) {
        this.image_href = image_href;
    }

    public String getLen() {
        return len;
    }

    public void setLen(String len) {
        this.len = len;
    }

    public String getAuthor_name() {
        return author_name;
    }

    public void setAuthor_name(String author_name) {
        this.author_name = author_name;
    }

    public String getAuthor_href() {
        return author_href;
    }

    public void setAuthor_href(String author_href) {
        this.author_href = author_href;
    }

    public String getSong_name() {
        return song_name;
    }

    public void setSong_name(String song_name) {
        this.song_name = song_name;
    }

    public String getSong_href() {
        return song_href;
    }

    public void setSong_href(String song_href) {
        this.song_href = song_href;
    }

    public String getSong_url() {
        return song_url;
    }

    public void setSong_url(String song_url) {
        this.song_url = song_url;
    }

    public ItemInform(String image_href, String len, String author_name, String author_href, String song_name, String song_href, String song_url) {
        this.image_href = image_href;
        this.len = len;
        this.author_name = author_name;
        this.author_href = author_href;
        this.song_name = song_name;
        this.song_href = song_href;
        this.song_url = song_url;
    }



    @Override
    public String toString() {
        return author_name + " - " + song_name + "( "+ song_href + " )";
    }
}
