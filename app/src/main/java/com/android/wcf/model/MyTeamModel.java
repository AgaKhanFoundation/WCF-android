package com.android.wcf.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Malik Khoja on 5/2/2017.
 */

public class MyTeamModel {

    String teamName = "";
    String teamProfileLink = "";
    String totalTeamMembers = "";
    String totalTeamMoney = "";
    String totalActualMiles = "";
    String totalExpectedMiles = "";
    String totalActualMilestone = "";
    String totalExpectedMilestone = "";
    List<LeaderBoardModel> arrLeaderBoard = new ArrayList<>();


    public String getTeamName() {
        return teamName;
    }

    public void setTeamName(String name) {
        this.teamName = name;
    }

    public String getTeamProfileLink() {
        return teamProfileLink;
    }

    public void setTeamProfileLink(String teamProfileLink) {
        this.teamProfileLink = teamProfileLink;
    }

    public String getTotalTeamMembers() {
        return totalTeamMembers;
    }

    public void setTotalTeamMembers(String totalTeamMembers) {
        this.totalTeamMembers = totalTeamMembers;
    }

    public String getTotalTeamMoney() {
        return totalTeamMoney;
    }

    public void setTotalTeamMoney(String totalTeamMoney) {
        this.totalTeamMoney = totalTeamMoney;
    }

    public String getTotalActualMiles() {
        return totalActualMiles;
    }

    public void setTotalActualMiles(String totalActualMiles) {
        this.totalActualMiles = totalActualMiles;
    }

    public String getTotalExpectedMiles() {
        return totalExpectedMiles;
    }

    public void setTotalExpectedMiles(String totalExpectedMiles) {
        this.totalExpectedMiles = totalExpectedMiles;
    }

    public String getTotalActualMilestone() {
        return totalActualMilestone;
    }

    public void setTotalActualMilestone(String totalActualMilestone) {
        this.totalActualMilestone = totalActualMilestone;
    }

    public String getTotalExpectedMilestone() {
        return totalExpectedMilestone;
    }

    public void setTotalExpectedMilestone(String totalExpectedMilestone) {
        this.totalExpectedMilestone = totalExpectedMilestone;
    }

    public List<LeaderBoardModel> getArrLeaderBoard() {
        return arrLeaderBoard;
    }

    public void setArrLeaderBoard(List<LeaderBoardModel> arrLeaderBoard) {
        this.arrLeaderBoard = arrLeaderBoard;
    }
}
