package com.room.raindrops.models;

import java.util.Arrays;

public class List
{
    private String id;

//    private Clouds clouds;

    private String dt;

    private String humidity;

    private String pressure;

    private String speed;

    private String snow;

    private String deg;

    private Sys sys;

    private String name;

    private Weather[] weather;

    private Temp temp;

    private Main main;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public Sys getSys() {
        return sys;
    }

    public void setSys(Sys sys) {
        this.sys = sys;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Main getMain() {
        return main;
    }

    public void setMain(Main main) {
        this.main = main;
    }

//    public Clouds getClouds() {
//        return clouds;
//    }

//    public void setClouds(Clouds clouds) {
//        this.clouds = clouds;
//    }

    public String getDt ()
    {
        return dt;
    }

    public void setDt (String dt)
    {
        this.dt = dt;
    }

    public String getHumidity ()
    {
        return humidity;
    }

    public void setHumidity (String humidity)
    {
        this.humidity = humidity;
    }

    public String getPressure ()
    {
        return pressure;
    }

    public void setPressure (String pressure)
    {
        this.pressure = pressure;
    }

    public String getSpeed ()
    {
        return speed;
    }

    public void setSpeed (String speed)
    {
        this.speed = speed;
    }

    public String getSnow ()
    {
        return snow;
    }

    public void setSnow (String snow)
    {
        this.snow = snow;
    }

    public String getDeg ()
    {
        return deg;
    }

    public void setDeg (String deg)
    {
        this.deg = deg;
    }

    public Weather[] getWeather ()
    {
        return weather;
    }

    public void setWeather (Weather[] weather)
    {
        this.weather = weather;
    }

    public Temp getTemp ()
    {
        return temp;
    }

    public void setTemp (Temp temp)
    {
        this.temp = temp;
    }

    @Override
    public String toString() {
        return "List{" +
                "id='" + id + '\'' +
//                ", clouds='" + clouds + '\'' +
                ", dt='" + dt + '\'' +
                ", humidity='" + humidity + '\'' +
                ", pressure='" + pressure + '\'' +
                ", speed='" + speed + '\'' +
                ", snow='" + snow + '\'' +
                ", deg='" + deg + '\'' +
                ", sys=" + sys +
                ", name='" + name + '\'' +
                ", weather=" + Arrays.toString(weather) +
                ", temp=" + temp +
                ", main=" + main +
                '}';
    }
}
