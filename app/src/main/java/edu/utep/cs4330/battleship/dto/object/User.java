package edu.utep.cs4330.battleship.dto.object;

public class User {
    private Integer id;
    private String username;
    private String name;
    public User() {
    }

    public User(Integer id, String username, String name) {
        this.id = id;
        this.username = username;
        this.name = name;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
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

    @Override
    public String toString() {
        return name;
    }
}
