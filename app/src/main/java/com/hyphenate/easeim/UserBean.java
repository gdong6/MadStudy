package com.hyphenate.easeim;

public class UserBean {
    private String nickName;
    private int img;

    public void setImg(int img) {
        this.img = img;
    }

    public int getImg() {
        return img;
    }

    public UserBean(String nickName, int img) {
        this.nickName = nickName;
        this.img = img;
    }

    public void setNickName(String nickName) {
        this.nickName = nickName;
    }

    public String getNickName() {
        return nickName;
    }
}
