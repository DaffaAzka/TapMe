package com.dest.tapme.models;

public class Medal {
    String id;
    String medalRich;
    String medalKing;
    String medalOneDay;
    String medalHighScore;
    String medalHighPoint;

    public Medal() {
    }

    public Medal(String id, String medalRich, String medalKing, String medalOneDay, String medalHighScore, String medalHighPoint) {
        this.id = id;
        this.medalRich = medalRich;
        this.medalKing = medalKing;
        this.medalOneDay = medalOneDay;
        this.medalHighScore = medalHighScore;
        this.medalHighPoint = medalHighPoint;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getMedalRich() {
        return medalRich;
    }

    public void setMedalRich(String medalRich) {
        this.medalRich = medalRich;
    }

    public String getMedalKing() {
        return medalKing;
    }

    public void setMedalKing(String medalKing) {
        this.medalKing = medalKing;
    }

    public String getMedalOneDay() {
        return medalOneDay;
    }

    public void setMedalOneDay(String medalOneDay) {
        this.medalOneDay = medalOneDay;
    }

    public String getMedalHighScore() {
        return medalHighScore;
    }

    public void setMedalHighScore(String medalHighScore) {
        this.medalHighScore = medalHighScore;
    }

    public String getMedalHighPoint() {
        return medalHighPoint;
    }

    public void setMedalHighPoint(String medalHighPoint) {
        this.medalHighPoint = medalHighPoint;
    }
}
