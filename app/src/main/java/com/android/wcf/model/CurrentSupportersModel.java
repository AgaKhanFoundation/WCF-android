package com.android.wcf.model;

/**
 * Created by Malik Khoja on 5/8/2017.
 */

public class CurrentSupportersModel {

    String name = "";
    String currentMoney = "";
    String pledged = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getCurrentMoney() {
        return currentMoney;
    }

    public void setCurrentMoney(String currentMoney) {
        this.currentMoney = currentMoney;
    }

    public String getPledged() {
        return pledged;
    }

    public void setPledged(String pledged) {
        this.pledged = pledged;
    }
}
