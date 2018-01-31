package com.delarosa.notimedia.model.Entitys;

public class NotificationManagerEntity {

    private int duration, id, status;
    private String date;

    public NotificationManagerEntity() {
    }

    public NotificationManagerEntity(int _duration, int _id, int _status, String _date) {

        duration = _duration;
        id = _id;
        status = _status;
        date = _date;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public int getDuration() {
        return duration;
    }

    public void setDuration(int duration) {
        this.duration = duration;
    }
}

