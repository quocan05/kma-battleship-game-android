package edu.utep.cs4330.battleship.dto;

import edu.utep.cs4330.battleship.dto.object.User;

public class UserSingleton {
    private static UserSingleton instance;
    private String username;
    private Integer id;

    public UserSingleton() {}

    public static synchronized UserSingleton getInstance() {
        if (instance == null) {
            instance = new UserSingleton();
        }
        return instance;
    }

    public static void setInstance(UserSingleton instance) {
        UserSingleton.instance = instance;
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

    public User convertUser(){
        return new User(getId(),getUsername());
    }
}
