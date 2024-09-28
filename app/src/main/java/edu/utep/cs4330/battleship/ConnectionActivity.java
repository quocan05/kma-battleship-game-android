package edu.utep.cs4330.battleship;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.Network;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;

import edu.utep.cs4330.battleship.common.Common;
import edu.utep.cs4330.battleship.common.Constants;
import edu.utep.cs4330.battleship.dto.object.User;
import edu.utep.cs4330.battleship.dto.UserSingleton;
import edu.utep.cs4330.battleship.dto.request.NewGameRequest;
import edu.utep.cs4330.battleship.dto.response.BEResponse;
import edu.utep.cs4330.battleship.dto.response.MqttObject;
import edu.utep.cs4330.battleship.service.BEService;
import edu.utep.cs4330.battleship.service.MqttHandler;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Request;
import okhttp3.Response;

/**
 * Created by Gerardo Cervantes and Eric Torres.
 */
public class ConnectionActivity extends AppCompatActivity {

    private final IntentFilter intentFilter = new IntentFilter();


    /**
     * Spinner that displays all devices that are within wifi-direct range
     */
//    private Spinner deviceSpinner;
    private Spinner userSpinner;

    /**
     * Contains spinner information
     */
//    private ArrayAdapter<CustomDevice> deviceAdapter;
    private ArrayAdapter<User> userAdapter;
    private String MQTT_TAG = "MQTT_TAG";

    /**
     * Receives broadcasts
     */
    private BroadcastReceiver receiver;


    final private int port = 7070;

    private final CustomDevice NO_DEVICES_FOUND = new CustomDevice(null);

    private MqttHandler mqttHandler;
    private BEService beService;
    private Gson gson;
    private UserSingleton userSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        ////

        super.onCreate(savedInstanceState);
        gson = new Gson();
        mqttHandler = MqttHandler.getInstance();
        userSingleton = UserSingleton.getInstance();
        beService = BEService.getInstance();
        setContentView(R.layout.activity_connection);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);



        userSpinner = (Spinner) findViewById(R.id.list);
        List<User> users = new LinkedList<>();
        this.handleRefresh(userSingleton.getId());

        final ConnectionActivity activity = this;
        mqttHandler.subscribe("battleship/"+userSingleton.getId());
        startReadingNetworkMessages();

        receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {

                String action = intent.getAction();
                if (WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION.equals(action)) {
                    // Determine if Wifi P2P mode is enabled or not, alert
                    // the Activity.
                    int state = intent.getIntExtra(WifiP2pManager.EXTRA_WIFI_STATE, -1);
                    if (state == WifiP2pManager.WIFI_P2P_STATE_ENABLED) {
                        activity.setIsWifiP2pEnabled(true);
                    } else {
                        activity.setIsWifiP2pEnabled(false);
                        shootAlert("Go to settings to enable wifi-direct?");
                    }
                } else if (WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION.equals(action)) {

                    // TODO:
                    Log.d("wifiMe", "P2P peers changed");

                } else if (WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION.equals(action)) {

                    // Connection state changed!  We should probably do something about
                    // that.

//                    NetworkInfo networkInfo = intent
//                            .getParcelableExtra(WifiP2pManager.EXTRA_NETWORK_INFO);
//
//                    if (networkInfo.isConnected()) {
//
//                        mManager.requestConnectionInfo(mChannel, new WifiP2pManager.ConnectionInfoListener() {
//                            @Override
//                            public void onConnectionInfoAvailable(final WifiP2pInfo info) {
//
//                                if (!info.groupFormed) {
//                                    return;
//                                }
//                                new Thread(new Runnable() {
//                                    @Override
//                                    public void run() {
//                                        if (info.isGroupOwner) {
//                                            createServer();
//                                        } else {
//                                            createClient(info.groupOwnerAddress);
//                                        }
//
//                                        Intent i = new Intent(activity, PlaceShipsActivity.class);
//                                        startActivity(i);
//                                    }
//                                }).start();
//                            }
//                        });
//                    }
                } else if (WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION.equals(action)) {
//
//                    if (mManager != null) {
//                    }
                }
            }
        };

    }


    /**
     * Called when p2p wifi is enabled/disabled
     */
    public void setIsWifiP2pEnabled(boolean enabled) {

    }

    /**
     * register the BroadcastReceiver with the intent values to be matched
     */
    @Override
    public void onResume() {
        super.onResume();
        //receiver = new WifiDirectBroadcastReceiver(mManager, mChannel, this);
        registerReceiver(receiver, intentFilter);
    }

    public void connect(View view) {
        Thread connect = new Thread(new Runnable() {
            @Override
            public void run() {
                User user =(User) userSpinner.getSelectedItem();
                MqttObject mqttObject = new MqttObject(NetworkAdapter.NEW_GAME,new NewGameRequest(user.getId(),user.getUsername()));
                mqttHandler.publish("battleship/"+user.getId(),mqttObject);
            }
        });
        connect.start();
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

//    @Override
//    public void onPeersAvailable(WifiP2pDeviceList peerList) {
//        Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
//        Log.d("wifiMe", "Found a change in the device list");
//
//        if (refreshedPeers.isEmpty()) {
//            deviceAdapter.clear();
//            deviceAdapter.add(NO_DEVICES_FOUND);
//        } else {
//            deviceAdapter.clear();
//            for (WifiP2pDevice device : peerList.getDeviceList()) {
//                deviceAdapter.add(new CustomDevice(device));
//            }
//        }
//        Log.d("wifiMe", "Devices updates");
//        deviceAdapter.notifyDataSetChanged();
//    }

    public void refresh(View view) {
        handleRefresh(userSingleton.getId());
    }

    public void turnOn(View view) {

    }


    private void toast(final String s) {
        final Context context = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(context, s, Toast.LENGTH_SHORT).show();
            }
        });

    }

    //makes and displays an AlertDialog
    protected void shootAlert(String msg) {
        AlertDialog.Builder build = new AlertDialog.Builder(this);
        build.setMessage(msg);
        build.setCancelable(true);

        build.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Intent intent = new Intent(Settings.ACTION_WIFI_SETTINGS);
                startActivity(intent);
            }
        });

        build.setNegativeButton("No", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                //cancel
                dialog.cancel();
            }
        });

        AlertDialog alert = build.create();
        alert.show();
    }


    public void handleRefresh(Integer id) {

        Request request = Common.getRequest(null, Constants.GET_ACTIVE, Constants.GET);

        beService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LoginError", "Request failed", e);
                Toast.makeText(ConnectionActivity.this, "WRONG USERNAME OR PASSWORD", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // handle response
                    String responseData = response.body().string();
                    BEResponse beResponse = gson.fromJson(responseData, BEResponse.class);
                    List<Object> list = (List) beResponse.getData();
                    List<User> userList = convertListUser(list);

                    runOnUiThread(() -> {
                        userAdapter = new ArrayAdapter<User>(ConnectionActivity.this, R.layout.spinner, userList) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = convertView != null ? convertView : LayoutInflater.from(getContext()).inflate(R.layout.spinner, parent, false);

                                // Lấy User tại vị trí hiện tại
                                User user = getItem(position);

                                // Gán ID và tên cho TextView
                                TextView userId = view.findViewById(R.id.user_id);
                                TextView userName = view.findViewById(R.id.user_name);

                                userId.setText(String.valueOf(position+1));  // Gán ID
                                userName.setText(user.getUsername());         // Gán tên

                                return view;
                            }

                            @Override
                            public View getDropDownView(int position, View convertView, ViewGroup parent) {
                                return getView(position, convertView, parent);
                            }
                        };

                        userSpinner.setAdapter(userAdapter); // Set adapter cho Spinner
                    });
                }
            }
        });
    }


    private List<User> convertListUser(List<Object> list) {
        List<User> users = new LinkedList<>();
        for (Object object : list) {
            LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap) object;
            Integer id = Integer.valueOf(String.valueOf(linkedTreeMap.get("id")).charAt(0)) - 48;
            User user = new User(id, linkedTreeMap.get("username"), linkedTreeMap.get("name"));
            users.add(user);
        }
        return users;
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
                AlertDialog alertDialog = new AlertDialog.Builder(ConnectionActivity.this).create();
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
