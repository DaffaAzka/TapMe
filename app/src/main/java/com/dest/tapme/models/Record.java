package com.dest.tapme.models;

public class Record {
    String id;
    Integer point;
    Integer score;

    public Record() { }

    public Record(String id, Integer point, Integer score) {
        this.id = id;
        this.point = point;
        this.score = score;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Integer getPoint() {
        return point;
    }

    public void setPoint(Integer point) {
        this.point = point;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }
}
