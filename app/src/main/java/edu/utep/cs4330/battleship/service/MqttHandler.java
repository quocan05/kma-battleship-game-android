package edu.utep.cs4330.battleship.service;
import android.util.Log;

import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;
import org.eclipse.paho.client.mqttv3.persist.MemoryPersistence;

import java.util.concurrent.CountDownLatch;


public class MqttHandler  implements MqttCallback{
    private static MqttHandler instance;
    private MqttClient client;
    private static final String username = "hoang";
    private static final String password = "100702";
    private static final String TAG = "MQTT_CLIENT";
    private static final String brokeUrl = "tcp://13.212.249.56:1883";

    public MqttHandler() {
        connect();
    }

    public void connect() {
        try {
            // Set up the persistence layer
            MemoryPersistence persistence = new MemoryPersistence();

            // Initialize the MQTT client
            client = new MqttClient(brokeUrl, TAG, persistence);

            // Set up the connection options
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(true);
            connectOptions.setUserName(username);
            connectOptions.setPassword(password.toCharArray());
            connectOptions.setAutomaticReconnect(true);
            connectOptions.setConnectionTimeout(10); // thời gian timeout kết nối
            connectOptions.setKeepAliveInterval(20);
//            connectOptions.getConnectionTimeout(60);

            // Connect to the broker
            client.connect(connectOptions);
            client.setCallback(this);
            if(client.isConnected()){
                Log.d("MQTT","SUCCESS");
            }else {
                Log.d("MQTT","FAIL");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException  e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, Object message) {
        try {
            if (client.isConnected()) {
                Gson gson = new Gson();
                MqttMessage mqttMessage = new MqttMessage(gson.toJson(message).getBytes());
                client.publish(topic, mqttMessage);
            } else {
                connect();
                Gson gson = new Gson();
                MqttMessage mqttMessage = new MqttMessage(gson.toJson(message).getBytes());
                client.publish(topic, mqttMessage);
            }
        } catch (  MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            if (client.isConnected()) {
                client.subscribe(topic);
            } else {
                connect();
                client.subscribe(topic);
            }
        } catch (  MqttException e) {
            e.printStackTrace();
        }
    }


    public static MqttHandler getInstance(){
        if(instance==null){
            instance = new MqttHandler();
        }
        return  instance;
    }

    public MqttClient getClient() {
        return client;
    }

    public  Boolean isConnected(){
        return client.isConnected();
    }

    @Override
    public void connectionLost(Throwable cause) {
        Log.d(TAG,"Khog yeu");
    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Log.d(TAG, "Message arrived from topic: " + topic);
        Log.d(TAG, "Message content: " + new String(message.getPayload()));
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {
        Log.d(TAG,"Co LUON");
    }
}