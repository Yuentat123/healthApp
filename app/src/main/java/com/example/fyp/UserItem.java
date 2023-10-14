package com.example.fyp;

public class UserItem {
    String bloodpressure;
    String date;
    String heartrate;
    String bodytemperature;
    String status;

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getBodytemperature() {
        return bodytemperature;
    }

    public void setBodytemperature(String bodytemperature) {
        this.bodytemperature = bodytemperature;
    }

    String key;

    public String getHeartrate() {
        return heartrate;
    }

    public void setHeartrate(String heartrate) {
        this.heartrate = heartrate;
    }

    private long timestamp;
    public UserItem(){}

    public String getDate() {
        return date;
    }

    public String getKey() {
        return key;
    }

    public void setKey(String key) {
        this.key = key;
    }

    public long getTimestamp() {
        return timestamp;
    }

    public void setTimestamp(long timestamp) {
        this.timestamp = timestamp;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public UserItem(String bloodpressure,String date,String heartrate,String bodytemperature,String status,long timestamp){
        this.date=date;
        this.bloodpressure=bloodpressure;
        this.heartrate=heartrate;
        this.bodytemperature=bodytemperature;
        this.status=status;
        this.timestamp = System.currentTimeMillis();
    }

    public String getBloodpressure() {
        return bloodpressure;
    }

    public void setBlood_pressure(String blood_pressure) {
        this.bloodpressure = bloodpressure;
    }
}
