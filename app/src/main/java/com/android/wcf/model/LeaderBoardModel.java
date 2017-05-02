package com.android.wcf.model;

/**
 * Created by Malik Khoja on 5/2/2017.
 */

public class LeaderBoardModel {

    String name = "";
    String profileLink = "";
    String totalMiles = "";
    String totalMoneyRaised = "";

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getProfileLink() {
        return profileLink;
    }

    public void setProfileLink(String profileLink) {
        this.profileLink = profileLink;
    }

    public String getTotalMiles() {
        return totalMiles;
    }

    public void setTotalMiles(String totalMiles) {
        this.totalMiles = totalMiles;
    }

    public String getTotalMoneyRaised() {
        return totalMoneyRaised;
    }

    public void setTotalMoneyRaised(String totalMoneyRaised) {
        this.totalMoneyRaised = totalMoneyRaised;
    }
}
