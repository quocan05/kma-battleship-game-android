package edu.utep.cs4330.battleship.dto.response;

public class PlaceShipRequest {
    private String board;
    private Integer id;

    public PlaceShipRequest() {
    }

    public PlaceShipRequest(String board, Integer id) {
        this.board = board;
        this.id = id;
    }

    public String getBoard() {
        return board;
    }

    public void setBoard(String board) {
        this.board = board;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }
}
