package edu.utep.cs4330.battleship.dto.request;

public class NewGameRequest {
    private Integer id;
    private Integer name;

    public NewGameRequest(Integer id, Integer name) {
        this.id = id;
        this.name = name;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public Integer getName() {
        return name;
    }

    public void setName(Integer name) {
        this.name = name;
    }
}
