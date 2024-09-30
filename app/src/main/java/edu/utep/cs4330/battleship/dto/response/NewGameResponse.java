package edu.utep.cs4330.battleship.dto.response;

import com.google.gson.internal.LinkedTreeMap;

public class NewGameResponse {
    private Integer id;
    private String username;

    public NewGameResponse() {
    }

    public NewGameResponse(Integer id, String username) {
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

    public void convertToNewGameResponse(LinkedTreeMap<String,String> treeMap){
        Integer id = Integer.valueOf(String.valueOf(treeMap.get("id")).charAt(0))-48;
        setId(id);
        setUsername(treeMap.get("username"));
    }
}
