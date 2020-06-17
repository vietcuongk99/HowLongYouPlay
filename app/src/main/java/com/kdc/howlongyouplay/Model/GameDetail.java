package com.kdc.howlongyouplay.Model;

// khai báo lớp GameDetail


import java.util.HashMap;

public class GameDetail {
    private String game_title;
    private String year;
    private String id_game;
    private String img_url;
    private String icon_url;
    private String pulisher;
    private String developer;
    private String playable_device;
    private String genre;
    private String finished_time;

    public GameDetail() {

    }


    public GameDetail(String game_title, String year) {
        this.game_title = game_title;
        this.year  = year;
    }


    public String getGame_title() {
        return game_title;
    }

    public void setGame_title(String game_title) {
        this.game_title = game_title;
    }

    /*
    public String getId_log() {
        return id_log;
    }

    public void setId_log(String id_log) {
        this.id_log = id_log;
    }

     */

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

    public String getPulisher() {
        return pulisher;
    }

    public void setPulisher(String pulisher) {
        this.pulisher = pulisher;
    }

    public String getDeveloper() {
        return developer;
    }

    public void setDeveloper(String developer) {
        this.developer = developer;
    }

    public String getPlayable_device() {
        return playable_device;
    }

    public void setPlayable_device(String playable_device) {
        this.playable_device = playable_device;
    }

    public String getGenre() {
        return genre;
    }

    public void setGenre(String genre) {
        this.genre = genre;
    }

    public String getIcon_url() {
        return icon_url;
    }

    public void setIcon_url(String icon_url) {
        this.icon_url = icon_url;
    }

    public String getFinished_time() {
        return finished_time;
    }

    public void setFinished_time(String finished_time) {
        this.finished_time = finished_time;
    }

}
