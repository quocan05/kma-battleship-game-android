package edu.utep.cs4330.battleship.dto.request;

public class NewRoomRequest {
    private Integer userId;
    private Integer opponentId;

    public NewRoomRequest(Integer userId, Integer opponentId) {
        this.userId = userId;
        this.opponentId = opponentId;
    }

    public NewRoomRequest() {
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
}
