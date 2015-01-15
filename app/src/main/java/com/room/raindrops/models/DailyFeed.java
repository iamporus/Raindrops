package com.room.raindrops.models;

public class DailyFeed {
    private long cityId;
    private String date;
    private String min_temp;
    private String max_temp;

    public DailyFeed(long cityId, String date, String min_temp, String max_temp) {
        this.cityId = cityId;
        this.date = date;
        this.min_temp = min_temp;
        this.max_temp = max_temp;
    }

    public long getCityId() {
        return cityId;
    }

    public void setCityId(long cityId) {
        this.cityId = cityId;
    }

    public String getMax_temp() {
        return max_temp;
    }

    public void setMax_temp(String max_temp) {
        this.max_temp = max_temp;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getMin_temp() {
        return min_temp;
    }

    public void setMin_temp(String min_temp) {
        this.min_temp = min_temp;
    }

    @Override
    public String toString() {
        return "DailyFeed{" +
                "cityId=" + cityId +
                ", date='" + date + '\'' +
                ", min_temp='" + min_temp + '\'' +
                ", max_temp='" + max_temp + '\'' +
                '}';
    }
}