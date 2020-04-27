package com.kdc.howlongyouplay;

// khai báo lớp GameLog


public class GameLog {
    private String game_title;
    private String play_time;
    private String userid;
    private String id_log;
    private String year;

    public GameLog() {

    }


    public GameLog(String game_title, String year) {
        this.game_title = game_title;
        this.year  = year;
    }



    public String getGame_title() {
        return game_title;
    }

    public String getPlay_time() {
        return play_time;
    }

    public String getUserid() {
        return userid;
    }

    public String getLogID() {
        return id_log;
    }

    public String getYear() {
        return year;
    }

    public void setGame_title(String game_title) {
        this.game_title = game_title;
    }

    public void setPlay_time(String play_time) {
        this.play_time = play_time;
    }

    public void setUserid(String userid) {
        this.userid = userid;
    }

    public void setLogID(String id_log) {
        this.id_log = id_log;
    }

    public void setYear(String year) {
        this.year = year;
    }
}
