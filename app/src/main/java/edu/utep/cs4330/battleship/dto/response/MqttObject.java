package edu.utep.cs4330.battleship.dto.response;

public class MqttObject {
    private String message;
    private Object data;

    public MqttObject(String message, Object data) {
        this.message = message;
        this.data = data;
    }

    public MqttObject() {
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

    public void setData(Object data) {
        this.data = data;
    }
}
