package com.kdc.howlongyouplay;

import java.util.List;

public class Log {

    private String user_id;
    private String game_title;
    private List<String> id_log;
    private String year;
    private String id_game;
    private String img_url;
    private String icon_url;

    public String getGame_title() {
        return game_title;
    }

    public void setGame_title(String game_title) {
        this.game_title = game_title;
    }

    public List<String> getId_log() {
        return id_log;
    }

    public void setId_log(List<String> id_log) {
        this.id_log = id_log;
    }

    public String getYear() {
        return year;
    }

    public void setYear(String year) {
        this.year = year;
    }

    public String getId_game() {
        return id_game;
    }

    public void setId_game(String id_game) {
        this.id_game = id_game;
    }

    public String getImg_url() {
        return img_url;
    }

    public void setImg_url(String img_url) {
        this.img_url = img_url;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }
}
