package edu.utep.cs4330.battleship.dto.history;

public class HistoryResult {
    private String date;
    private String result;

    public HistoryResult(String date, String result) {
        this.date = date;
        this.result = result;
    }

    public HistoryResult() {
    }

    public String getDate() {
        return date;
    }

    public void setDate(String date) {
        this.date = date;
    }

    public String getResult() {
        return result;
    }

    public void setResult(String result) {
        this.result = result;
    }

    @Override
    public String toString() {
        return "HistoryResult{" +
                "date='" + date + '\'' +
                ", result='" + result + '\'' +
                '}';
    }
}
