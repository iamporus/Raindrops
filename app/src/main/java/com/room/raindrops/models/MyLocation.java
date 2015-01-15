package com.room.raindrops.models;

public class MyLocation {
    private long id;
    private String name;
    private String country;
    private String temperature;
    private String message;

    public MyLocation(long id, String name, String country) {
        this.id = id;
        this.name = name;
        this.country = country;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public String getCountry() {
        return country;
    }

    public void setCountry(String country) {
        this.country = country;
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getTemperature() {
        return temperature;
    }

    public void setTemperature(String temperature) {
        this.temperature = temperature;
    }

    @Override
    public String toString() {
        return "MyLocation{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", temperature='" + temperature + '\'' +
                '}';
    }
}
