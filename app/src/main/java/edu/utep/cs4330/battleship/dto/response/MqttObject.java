package edu.utep.cs4330.battleship.dto.response;

public class MqttObject {
    private String message;
    private Object data;
    private Integer userId;
    private String username;
    public MqttObject() {
    }

    public MqttObject(String message, Integer userId, String username,Object data) {
        this.message = message;
        this.data = data;
        this.userId = userId;
        this.username = username;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String name) {
        this.username = name;
    }

    public String getMessage() {
        return message;
    }

    public void setMessage(String message) {
        this.message = message;
    }

    public Object getData() {
        return data;
    }

    public Integer getUserId() {
        return userId;
    }

    public void setUserId(Integer userId) {
        this.userId = userId;
    }

    public void setData(Object data) {
        this.data = data;
    }
}
