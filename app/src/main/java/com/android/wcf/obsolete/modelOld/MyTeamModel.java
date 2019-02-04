package com.android.wcf.obsolete.modelOld;
/**
 * Copyright © 2017 Aga Khan Foundation
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * 1. Redistributions of source code must retain the above copyright notice,
 *    this list of conditions and the following disclaimer.
 *
 * 2. Redistributions in binary form must reproduce the above copyright notice,
 *    this list of conditions and the following disclaimer in the documentation
 *    and/or other materials provided with the distribution.
 *
 * 3. The name of the author may not be used to endorse or promote products
 *    derived from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE AUTHOR "AS IS" AND ANY EXPRESS OR IMPLIED
 * WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED WARRANTIES OF
 * MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE DISCLAIMED. IN NO
 * EVENT SHALL THE AUTHOR BE LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL,
 * SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO,
 * PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS;
 * OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY,
 * WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR
 * OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF
 * ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 **/

import java.util.ArrayList;
import java.util.List;

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
