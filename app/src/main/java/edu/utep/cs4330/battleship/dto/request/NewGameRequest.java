package edu.utep.cs4330.battleship.dto.request;

import com.google.gson.internal.LinkedTreeMap;

public class NewGameRequest {
    private Integer id;
    private String username;

    public NewGameRequest() {
    }

    public NewGameRequest(Integer id, String username) {
        this.id = id;
        this.username = username;
    }

    public Integer getId() {
        return id;
    }

    public void setId(Integer id) {
        this.id = id;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }


    public void convertToGameRequest( LinkedTreeMap<String,String> linkedTreeMap){
        Integer id = Integer.valueOf(String.valueOf(linkedTreeMap.get("id")).charAt(0))-48;
        setId(id);
        setUsername(linkedTreeMap.get("username"));
    }
}
