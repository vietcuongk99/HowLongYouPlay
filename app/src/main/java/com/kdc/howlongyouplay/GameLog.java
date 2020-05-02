package com.kdc.howlongyouplay;

// khai báo lớp GameLog


public class GameLog {
    private String game_title;
    private String played_time;
    private String userid;
    private String id_log;
    private String year;
    private String id_game;

    public GameLog() {

    }


    public GameLog(String game_title, String year) {
        this.game_title = game_title;
        this.year  = year;
    }

    public GameLog(String game_title, String played_time, String year) {
        this.game_title = game_title;
        this.played_time  = played_time;
        this.year = year;
    }

    public String getGame_title() {
        return game_title;
    }

    public void setGame_title(String game_title) {
        this.game_title = game_title;
    }

    public String getPlayed_time() {
        return played_time;
    }

    public void setPlayed_time(String played_time) {
        this.played_time = played_time;
    }

    public String getUserid() {
        return userid;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public String getId_log() {
        return id_log;
    }

    public void setId_log(String id_log) {
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
}
