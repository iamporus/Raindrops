package com.room.raindrops.models;

public class CityLocal {

    private long id;
    private String name;
    private double lat;
    private double lon;
    private String countryCode;

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

    public double getLat() {
        return lat;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public double getLon() {
        return lon;
    }

    public void setLon(double lon) {
        this.lon = lon;
    }

    public String getCountryCode() {
        return countryCode;
    }

    public void setCountryCode(String countryCode) {
        this.countryCode = countryCode;
    }

    public CityLocal(String id, String name, String lat, String lon,
                     String countryCode) {
        super();
        this.id = Long.parseLong(id);
        this.name = name;
        this.lat = Double.parseDouble(lat);
        this.lon = Double.parseDouble(lon);
        this.countryCode = countryCode;
    }

    @Override
    public String toString() {
        return "CityLocal [id=" + id + ", name=" + name + ", lat=" + lat
                + ", lon=" + lon + ", countryCode=" + countryCode + "]";
    }
}
