package edu.utep.cs4330.battleship.dto.request;


import com.google.gson.internal.LinkedTreeMap;

public class RoomRequest {
    Integer userId;
    Integer opponentId;
    Boolean isWinner;

    public RoomRequest(Integer userId, Integer opponentId,Boolean isWinner) {
        this.userId = userId;
        this.opponentId = opponentId;
        this.isWinner = isWinner;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public Integer getOpponentId() {
        return opponentId;
    }

    public void setOpponentId(Integer opponentId) {
        this.opponentId = opponentId;
    }

    public Boolean getWinner() {
        return isWinner;
    }

    public void setWinner(Boolean winner) {
        isWinner = winner;
    }
}
