package edu.utep.cs4330.battleship;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.util.LinkedHashMap;
import java.util.Objects;

import edu.utep.cs4330.battleship.common.Common;
import edu.utep.cs4330.battleship.dto.object.Position;
import edu.utep.cs4330.battleship.dto.response.MqttObject;
import edu.utep.cs4330.battleship.service.MqttHandler;
import edu.utep.cs4330.battleship.service.MusicService;

/**
 * Created by Gerardo Cervantes and Eric Torres on 4/17/2017.
 */

public class MainMenu extends Activity {
    Button single;
    Button multi;
    Intent intentThemeSound;
    private MqttHandler  mqttHandler;
    private String MQTT_TAG = "MQTT";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);
        mqttHandler = MqttHandler.getInstance();
        //Activity entrance and exit animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //active theme sound
        startReadingNetworkMessages();

        intentThemeSound = new Intent(this, MusicService.class);
        startService(intentThemeSound);
        //Buttons
        single = (Button) findViewById(R.id.single);
        multi = (Button) findViewById(R.id.multi);

        single.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenu.this, PlaceShipsActivity.class); //Intent i = new Intent(getApplicationContext(), PlaceShipsActivity.class);
                startActivity(i);
            }
        });

        multi.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainMenu.this, ConnectionActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intentThemeSound);
    }

    private void showNotification(String message) {
        // Hiển thị thông báo (ví dụ: sử dụng Toast hoặc AlertDialog)
        Toast.makeText(this, "Thông báo: " + message, Toast.LENGTH_SHORT).show();
    }


    void startReadingNetworkMessages() {
        Thread readMessages = new Thread(new Runnable() {
            public void run() {
                while (true) {
                    mqttHandler.getClient().setCallback(new MqttCallback() {
                        @Override
                        public void connectionLost(Throwable cause) {

                        }
                        @Override
                        public void messageArrived(String topic, MqttMessage message) throws Exception {
                            MqttObject mqttObject = Common.convertStringJsonToMqttObject(new String(message.getPayload()));
                            Log.d(MQTT_TAG, mqttObject.getMessage());
                            if (Objects.equals(mqttObject.getMessage(),NetworkAdapter.NEW_GAME)) {
                                Log.d(MQTT_TAG, "New game requested, dialog given with yes or no options to accept or reject request"); //should send accept message message and reset game
                                resetPromptDialog(getString(R.string.reset_game_connected_prompt), new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {

                                        if (NetworkAdapter.hasConnection()) {
                                            NetworkAdapter.writeAcceptNewGameMessage();
                                            NetworkAdapter.writeStopReadingMessage();
                                        }
//                                        segueToPlaceShipsActivity();
                                    }
                                }, new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int which) {
                                        new Thread(new Runnable() {
                                            @Override
                                            public void run() {
                                                NetworkAdapter.writeRejectNewGameMessage();
                                            }
                                        }).start();
                                    }
                                });
                            } else if (Objects.equals(mqttObject.getMessage(),NetworkAdapter.REJECT_NEW_GAME_REQUEST)) {

                            } else if (Objects.equals(mqttObject.getMessage(),NetworkAdapter.ACCEPT_NEW_GAME_REQUEST)) {
                                Log.d(MQTT_TAG, "Accepted new game request");  //should send accept message message

                                if (NetworkAdapter.hasConnection()) {
                                    NetworkAdapter.writeStopReadingMessage();
                                }
                            }
                        }

                        @Override
                        public void deliveryComplete(IMqttDeliveryToken token) {

                        }
                    });

                }
            }
        });
        readMessages.start();
    }
    public void resetPromptDialog(final String message, final DialogInterface.OnClickListener acceptListener, final DialogInterface.OnClickListener rejectListener) {
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                AlertDialog alertDialog = new AlertDialog.Builder(MainMenu.this).create();
                alertDialog.setTitle(getString(R.string.reset_game_title));
                alertDialog.setMessage(message);//(getString(R.string.reset_game_prompt)

                //Yes button, and listener for if button is pressed
                alertDialog.setButton(AlertDialog.BUTTON_NEGATIVE, "YES", acceptListener);

                //No button
                alertDialog.setButton(AlertDialog.BUTTON_POSITIVE, "NO", rejectListener);
                alertDialog.show();
            }
        });
    }
}
