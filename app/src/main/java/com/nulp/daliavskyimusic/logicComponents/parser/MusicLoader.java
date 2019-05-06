package com.nulp.daliavskyimusic.logicComponents.parser;

import org.jsoup.Connection;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class MusicLoader {
    public static List<SongInformation> getData(String href, String[] args){
        Document doc = null;
        final String base = "https://z1.fm";
        List<SongInformation> items = new ArrayList<>();
        try {
            Connection connection = Jsoup.connect(href).data(args);
            connection.userAgent("Mozilla/5.0 (Linux; Android 7.0; SM-G930V Build/NRD90M) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/59.0.3071.125 Mobile Safari/537.36");
            doc = connection.get();
        } catch (IOException e) {
            e.printStackTrace();
            return items;
        }
        Elements list = doc.getElementsByClass("tracks-item");
        for(int i=0;i<list.size();i++) {
            Elements song_info = list.get(i).children();
            String img_href = "no_image";
            if(!song_info.get(0).children().toString().equals("")){
                img_href = base + song_info.get(0).child(0).attr("src");
            }
            String song_name = song_info.get(1).child(0).child(0).text();
            String author_name =  song_info.get(1).child(0).child(1).text();
            String len = song_info.get(2).text();
            String author_href = base + song_info.get(3).child(3).attr("href");
            String song_href = base + song_info.get(3).child(2).attr("href");
            String download_href = song_href.replace("song","download");
            items.add(new SongInformation(img_href,len,author_name,author_href,song_name,song_href,download_href));
        }
        return items;
    }
}