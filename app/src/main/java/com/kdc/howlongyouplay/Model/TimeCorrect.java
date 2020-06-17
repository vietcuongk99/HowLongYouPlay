package com.kdc.howlongyouplay.Model;

public class TimeCorrect {
    private int hour;
    private int minute;
    private int second;

    public TimeCorrect(int hour, int minute, int second) {
        this.hour = hour;
        this.minute = minute;
        this.second = second;
    }

    public int getHour() {
        return hour;
    }

    public void setHour(int hour) {
        this.hour = hour;
    }

    public int getMinute() {
        return minute;
    }

    public void setMinute(int minute) {
        this.minute = minute;
    }

    public int getSecond() {
        return second;
    }

    public void setSecond(int second) {
        this.second = second;
    }

    public void correctTimeInput() {
        if (second > 59) {

            minute = minute + second/60;
            second = second%60;

            if (minute > 59) {
                hour = hour + minute/60;
                minute = minute%60;

            }
        } else {

            if (minute > 59) {
                hour = hour + minute/60;
                minute = minute%60;
            }
        }
    }
}
