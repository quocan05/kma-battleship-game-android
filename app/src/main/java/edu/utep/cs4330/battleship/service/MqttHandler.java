package edu.utep.cs4330.battleship.service;

import android.util.Log;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;
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

import edu.utep.cs4330.battleship.dto.response.MqttObject;


public class MqttHandler extends ViewModel implements MqttCallback {
//    private static MqttHandler instance;
    private MqttClient client;
    private static final String username = "hoang";
    private static final String password = "100702";
    private static final String TAG = "MQTT_CLIENT";
    private static final String brokeUrl = "tcp://13.212.249.56:1883";
    private MutableLiveData<MqttObject> mqttMessageLiveData = new MutableLiveData<MqttObject>();

    public LiveData<MqttObject> getMqttMessage() {
        return mqttMessageLiveData;
    }


    public MqttHandler() {
        // Khởi tạo MQTT Client
        connect();
    }

    public void connect() {
        try {
            // Set up the persistence layer
            MemoryPersistence persistence = new MemoryPersistence();

            // Initialize the MQTT client
            client = new MqttClient(brokeUrl, MqttClient.generateClientId(), persistence);

            // Set up the connection options
            MqttConnectOptions connectOptions = new MqttConnectOptions();
            connectOptions.setCleanSession(false);
            connectOptions.setUserName(username);
            connectOptions.setPassword(password.toCharArray());
            connectOptions.setAutomaticReconnect(true);
            // Connect to the broker
            client.setCallback(this);
            client.connect(connectOptions);
            if (client.isConnected()) {
                Log.d("MQTT", "SUCCESS");
            } else {
                Log.d("MQTT", "FAIL");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void disconnect() {
        try {
            if (client.isConnected()) {
                client.disconnect();
            }
        } catch (MqttException e) {
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
                Log.d(TAG, "FAIL CONNECTED");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void subscribe(String topic) {
        try {
            if (client.isConnected()) {
                client.subscribe(topic);
            } else {
                Log.d(TAG, "FAIL CONNECTED");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    public void unsubscribe(String topic){
        try {
            if (client.isConnected()) {
                client.unsubscribe(topic);
            } else {
                Log.d(TAG, "FAIL CONNECTED");
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }


    public MqttClient getClient() {
        return client;
    }

    public Boolean isConnected() {
        return client.isConnected();
    }
    @Override
    protected void onCleared() {
        super.onCleared();
        // Hủy kết nối khi ViewModel bị hủy
        try {
            if (client != null && client.isConnected()) {
                client.disconnect();
            }
        } catch (MqttException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void connectionLost(Throwable cause) {

    }

    @Override
    public void messageArrived(String topic, MqttMessage message) throws Exception {
        Gson  gson = new Gson();
        MqttObject mqttObject = gson.fromJson(new String(message.toString()),MqttObject.class);
        mqttMessageLiveData.postValue(mqttObject);
    }

    @Override
    public void deliveryComplete(IMqttDeliveryToken token) {

    }
}