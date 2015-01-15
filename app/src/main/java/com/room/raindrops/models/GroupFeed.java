package com.room.raindrops.models;

import java.util.Arrays;

public class GroupFeed
{
    private String cnt;

    private List[] list;

    public String getCnt ()
    {
        return cnt;
    }

    public void setCnt (String cnt)
    {
        this.cnt = cnt;
    }

    public List[] getList ()
    {
        return list;
    }

    public void setList (List[] list)
    {
        this.list = list;
    }

    @Override
    public String toString() {
        return "GroupFeed{" +
                "cnt='" + cnt + '\'' +
                ", list=" + Arrays.toString(list) +
                '}';
    }
}