package edu.utep.cs4330.battleship.dto.response;

import com.google.gson.internal.LinkedTreeMap;

import java.util.Date;

public class HistoryResponse {

    Boolean isWinner;
    String opponentName;
    String date;


    public String getOpponentName() {
        return opponentName;
    }

    public void setOpponentName(String opponentName) {
        this.opponentName = opponentName;
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public Boolean getWinner() {
        return isWinner;
    }

    public void setWinner(Boolean winner) {
        isWinner = winner;
    }

    public void convertLinkedTree(LinkedTreeMap<String, String> treeMap) {
        Boolean isWinner = Boolean.valueOf(String.valueOf(treeMap.get("isWinner")));
        setWinner(isWinner);
        String opponentName = String.valueOf(treeMap.get("opponentName"));
        setOpponentName(opponentName);
        String date = String.valueOf(treeMap.get("date"));
        setDate(date);
    }


}
