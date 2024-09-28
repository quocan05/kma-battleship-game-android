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



public class MqttHandler  {
    private static MqttHandler instance;
    private MqttClient client;
    private static final String username = "hoang";
    private static final String password = "100702";
    private static final String TAG = "MQTT_CLIENT";
    private static final String brokeUrl = "tcp://13.212.249.56:1883";

    private MqttHandler() {
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

            // Connect to the broker
            client.connect(connectOptions);
            client.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {

                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    Log.d("MQTT","Message arrived from topic: " + topic);
                    Log.d("MQTT","Message content: " + new String(message.getPayload()));
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            client.disconnect();
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void publish(String topic, Object message) {
        try {
            Gson gson = new Gson();
            MqttMessage mqttMessage = new MqttMessage(gson.toJson(message).getBytes());
            client.publish(topic, mqttMessage);
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            client.subscribe(topic);

        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public static MqttHandler getInstance(){
        if(instance==null){
            instance =  new MqttHandler();
        }
        return instance;
    }

    public MqttClient getClient() {
        return client;
    }

}