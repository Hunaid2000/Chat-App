package com.ass2.i192008_i192043;


public class playlist {
    private String name;
    private String desc;
    private String IconUrl;

    public playlist(String name, String desc, String iconUrl) {
        this.name = name;
        this.desc = desc;
        IconUrl = iconUrl;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDesc() {
        return desc;
    }

    public void setDesc(String desc) {
        this.desc = desc;
    }

    public String getIconUrl() {
        return IconUrl;
    }

    public void setIconUrl(String iconUrl) {
        IconUrl = iconUrl;
    }
}
