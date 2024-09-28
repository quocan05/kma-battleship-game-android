package edu.utep.cs4330.battleship.common;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;
import java.util.Objects;

import edu.utep.cs4330.battleship.dto.request.LoginRequest;
import edu.utep.cs4330.battleship.dto.response.MqttResponse;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;

public class Common {
    public static String convertToJsonString(Object object) {
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static MqttResponse convertStringJsonToMqttObject(String json) {
        Gson gson = new Gson();
        return gson.fromJson(json, MqttResponse.class);
    }

    public static <T> T convertMapToObject(LinkedHashMap<String, Object> map, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if (map.containsKey(field.getName())) {
                        field.set(instance, map.get(field.getName()));
                    }
                } catch (Exception e) {
                }

            }
            return instance;
        } catch (Exception e) {
        }
        return null;
    }

    public static <T> T convertMapToObject(LinkedTreeMap<String, Object> map, Class<T> clazz) {
        try {
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                try {
                    field.setAccessible(true);
                    if (map.containsKey(field.getName())) {
                        field.set(instance, map.get(field.getName()));
                    }
                } catch (Exception e) {
                }

            }
            return instance;
        } catch (Exception e) {
        }
        return null;
    }

    public static Integer convertStringToInteger(String id) {
        return Integer.valueOf(id.split(".")[0]);
    }

    public static Request getRequest(Object object, String api, String HttpMethod) {
        Gson gson = new Gson();
        String json = null;
        if (object != null) {
            json = gson.toJson(object);
        }

        if (Objects.equals(HttpMethod, "POST")) {
            RequestBody body = RequestBody.create(MediaType.get("application/json; charset=utf-8"), json);
            return new Request.Builder()
                    .url(Constants.HTTP_ADDRESS + api)
                    .post(body)
                    .build();
        } else {
            return new Request.Builder()
                    .url(Constants.HTTP_ADDRESS + api)
                    .get()
                    .build();
        }

    }
}
