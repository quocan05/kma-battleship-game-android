package edu.utep.cs4330.battleship;

import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.p2p.WifiP2pDevice;
import android.net.wifi.p2p.WifiP2pDeviceList;
import android.net.wifi.p2p.WifiP2pManager;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import java.io.IOException;
import java.net.InetAddress;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import edu.utep.cs4330.battleship.common.Common;
import edu.utep.cs4330.battleship.dto.UserSingleton;
import edu.utep.cs4330.battleship.dto.request.LoginRequest;
import edu.utep.cs4330.battleship.dto.response.BEResponse;
import edu.utep.cs4330.battleship.service.BEService;
import edu.utep.cs4330.battleship.service.MqttHandler;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Gerardo Cervantes and Eric Torres.
 */
public class ConnectionActivity extends AppCompatActivity implements WifiP2pManager.PeerListListener {

    private final IntentFilter intentFilter = new IntentFilter();


    /**
     * Spinner that displays all devices that are within wifi-direct range
     */
    private Spinner deviceSpinner;

    /**
     * Contains spinner information
     */
    private ArrayAdapter<CustomDevice> deviceAdapter;

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
        beService = BEService.getInstance();
        setContentView(R.layout.activity_connection);
        deviceSpinner = (Spinner) findViewById(R.id.DeviceSpinner);
        //createServer();
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_STATE_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_PEERS_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_CONNECTION_CHANGED_ACTION);
        intentFilter.addAction(WifiP2pManager.WIFI_P2P_THIS_DEVICE_CHANGED_ACTION);


        List<CustomDevice> items = new LinkedList<>();
        items.add(NO_DEVICES_FOUND);
        deviceAdapter = new ArrayAdapter<>(this, R.layout.spinner, R.id.list, items);

        deviceSpinner.setAdapter(deviceAdapter);


        final ConnectionActivity activity = this;


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


            }
        });
        connect.start();
    }

    /**
     * Blocks calling thread and creates server, unblocked when there is a connection
     */
    private boolean createServer() {
        NetworkAdapter.setSocket();
        return true;
    }

    private boolean createClient(InetAddress address) {
        NetworkAdapter.setSocket();
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onPeersAvailable(WifiP2pDeviceList peerList) {
        Collection<WifiP2pDevice> refreshedPeers = peerList.getDeviceList();
        Log.d("wifiMe", "Found a change in the device list");

        if (refreshedPeers.isEmpty()) {
            deviceAdapter.clear();
            deviceAdapter.add(NO_DEVICES_FOUND);
        } else {
            deviceAdapter.clear();
            for (WifiP2pDevice device : peerList.getDeviceList()) {
                deviceAdapter.add(new CustomDevice(device));
            }
        }
        Log.d("wifiMe", "Devices updates");
        deviceAdapter.notifyDataSetChanged();
    }

    public void refresh(View view) {
        mqttHandler.publish("hoang/dz", "Hoang dep trai qua ");
    }

    public void turnOn(View view) {
        mqttHandler.subscribe("hoang/dz");
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

}
