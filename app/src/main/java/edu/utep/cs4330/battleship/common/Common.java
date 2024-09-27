package edu.utep.cs4330.battleship.common;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.lang.reflect.Field;
import java.util.LinkedHashMap;

import edu.utep.cs4330.battleship.dto.response.MqttResponse;

public class Common {
    public static String convertToJsonString(Object object){
        Gson gson = new Gson();
        return gson.toJson(object);
    }

    public static MqttResponse convertStringJsonToMqttObject(String json){
        Gson gson = new Gson();
        return gson.fromJson(json, MqttResponse.class);
    }

    public static <T> T convertMapToObject(LinkedHashMap<String, Object> map, Class<T> clazz)   {
        try{
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                try{
                    field.setAccessible(true);
                    if (map.containsKey(field.getName())) {
                        field.set(instance, map.get(field.getName()));
                    }
                }catch (Exception e ){
                }

            }
            return instance;
        }
        catch (Exception e ){
        }
        return null;
    }

    public static <T> T convertMapToObject(LinkedTreeMap<String, Object> map, Class<T> clazz)   {
        try{
            T instance = clazz.getDeclaredConstructor().newInstance();

            for (Field field : clazz.getDeclaredFields()) {
                try{
                    field.setAccessible(true);
                    if (map.containsKey(field.getName())) {
                        field.set(instance, map.get(field.getName()));
                    }
                }catch (Exception e ){
                }

            }
            return instance;
        }
        catch (Exception e ){
        }
        return null;
    }

    public static Integer convertStringToInteger(String id){
        return Integer.valueOf(id.split(".")[0]);
    }
}
