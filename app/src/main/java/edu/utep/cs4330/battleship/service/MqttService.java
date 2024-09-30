//package edu.utep.cs4330.battleship.service;
//
//import android.app.Service;
//import android.content.Intent;
//import android.os.IBinder;
//import android.util.Log;
//
//import androidx.annotation.Nullable;
//
//import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
//import org.eclipse.paho.client.mqttv3.MqttCallback;
//import org.eclipse.paho.client.mqttv3.MqttMessage;
//
//public class MqttService extends Service implements MqttCallback {
//    private static final String MQTT_BROADCAST_ACTION = "edu.utep.cs4330.battleship.MQTT_MESSAGE_ARRIVED";
//    private static final String MQTT_TOPIC = "battleship/";
//    private MqttHandler mqttHandler;
//
//    @Override
//    public void onCreate() {
//        super.onCreate();
//        mqttHandler = MqttHandler.getInstance();
//        mqttHandler.getClient().setCallback(this);
//    }
//
//    @Override
//    public int onStartCommand(Intent intent, int flags, int startId) {
//        // Đăng ký để bắt đầu kết nối hoặc tái kết nối MQTT
//        mqttHandler.connect();
//        return START_STICKY;
//    }
//    @Override
//    public void onDestroy() {
//        super.onDestroy();
//        mqttHandler.disconnect();
//    }
//
//    @Nullable
//    @Override
//    public IBinder onBind(Intent intent) {
//        return null;
//    }
//
//    @Override
//    public void connectionLost(Throwable cause) {
//        Log.d("MqttService", "Connection lost");
//    }
//
//    @Override
//    public void messageArrived(String topic, MqttMessage message) throws Exception {
//        Log.d("MqttService", "Message arrived: " + message.toString());
//        // Gửi broadcast khi có tin nhắn đến
//        Intent intent = new Intent(MQTT_BROADCAST_ACTION);
//        intent.putExtra("message", message.toString());
//        sendBroadcast(intent);
//    }
//
//    @Override
//    public void deliveryComplete(IMqttDeliveryToken token) {
//        Log.d("MqttService", "Message delivered");
//
//    }
//}
