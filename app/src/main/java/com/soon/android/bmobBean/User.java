package com.soon.android.bmobBean;

import cn.bmob.v3.BmobUser;

/**
 * Created by 84975 on 2018/2/5.
 */

public class User extends BmobUser {

    private Integer id;

    private String nick;

    private Boolean sex;

    private Boolean VIP;

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getNick() {
        return nick;
    }

    public void setNick(String nick) {
        this.nick = nick;
    }

    public Boolean getSex() {
        return sex;
    }

    public void setSex(Boolean sex) {
        this.sex = sex;
    }

    public Boolean getVIP() {
        return VIP;
    }

    public void setVIP(Boolean VIP) {
        this.VIP = VIP;
    }
}
