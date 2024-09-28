package edu.utep.cs4330.battleship.dto.object;

public class Position {
    private Integer x = 0;

    /**Contains y-coordinate of the place, 0-based index*/
    private Integer y = 0;

    public Position(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Integer getX() {
        return x;
    }

    public void setX(Integer x) {
        this.x = x;
    }

    public Integer getY() {
        return y;
    }

    public void setY(Integer y) {
        this.y = y;
    }
}
