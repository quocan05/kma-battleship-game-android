package edu.utep.cs4330.battleship.dto.response;

public class NewGameResponse {
    private Integer id;
    private String name;
    private Boolean status;

    public NewGameResponse() {
    }

    public NewGameResponse(Integer id, String name, Boolean status) {
        this.id = id;
        this.name = name;
        this.status = status;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getStatus() {
        return status;
    }

    public void setStatus(Boolean status) {
        this.status = status;
    }
}
