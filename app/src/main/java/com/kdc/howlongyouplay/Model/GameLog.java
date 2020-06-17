package com.kdc.howlongyouplay.Model;

import java.util.HashMap;
import java.util.List;

public class GameLog {

    private String user_id;
    private String game_title;
    private String image_url;
    private String icon_url;
    private HashMap<String, Object> records;
    private String year;
    private String id_game;

    public GameLog() {

    }

    public String getGame_title() {
        return game_title;
    }

    public void setGame_title(String game_title) {
        this.game_title = game_title;
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


    public HashMap<String, Object> getRecords() {
        return records;
    }

    public void setRecords(HashMap<String, Object> records) {
        this.records = records;
    }

    public String getImage_url() {
        return image_url;
    }

    public void setImage_url(String image_url) {
        this.image_url = image_url;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }
}
