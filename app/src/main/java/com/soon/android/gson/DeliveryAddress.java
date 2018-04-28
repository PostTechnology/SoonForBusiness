package com.soon.android.gson;

/**
 * Created by LYH on 2018/4/25.
 */

public class DeliveryAddress {


    /**
     * name : 小明
     * tel : 11111111111
     * location : 某某大学
     * doorNum : 123楼
     */

    private String name;
    private long tel;
    private String location;
    private String doorNum;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getTel() {
        return tel;
    }

    public void setTel(long tel) {
        this.tel = tel;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getDoorNum() {
        return doorNum;
    }

    public void setDoorNum(String doorNum) {
        this.doorNum = doorNum;
    }
}
