package edu.utep.cs4330.battleship.dto.object;

import com.google.gson.internal.LinkedTreeMap;

public class Position {
    private Integer x;

    /**Contains y-coordinate of the place, 0-based index*/
    private Integer y ;

    public Position(Integer x, Integer y) {
        this.x = x;
        this.y = y;
    }

    public Position() {
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


    public void convertLinkedTree(LinkedTreeMap<String,String> treeMap){
        Integer x = Integer.valueOf(String.valueOf(treeMap.get("x")).charAt(0))-48;
        setX(x);
        Integer y = Integer.valueOf(String.valueOf(treeMap.get("y")).charAt(0))-48;
        setY(y);
    }
}
