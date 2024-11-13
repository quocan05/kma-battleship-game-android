package edu.utep.cs4330.battleship;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Build;
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
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

import java.io.IOException;
import java.util.LinkedList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

import edu.utep.cs4330.battleship.common.Common;
import edu.utep.cs4330.battleship.common.Constants;
import edu.utep.cs4330.battleship.dto.object.User;
import edu.utep.cs4330.battleship.dto.UserSingleton;
import edu.utep.cs4330.battleship.dto.request.NewGameRequest;
import edu.utep.cs4330.battleship.dto.response.BEResponse;
import edu.utep.cs4330.battleship.dto.response.MqttObject;
import edu.utep.cs4330.battleship.dto.response.NewGameResponse;
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

    private Spinner userSpinner;
    private ArrayAdapter<User> userAdapter;
    private String MQTT_TAG = "MQTT_TAG";
    private MqttHandler mqttHandler;
    private BEService beService;
    private Gson gson;
    private UserSingleton userSingleton;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        gson = new Gson();
        userSingleton = UserSingleton.getInstance();
        beService = BEService.getInstance();
        mqttHandler = new ViewModelProvider(this).get(MqttHandler.class);
        mqttHandler.subscribe(getTopic(userSingleton.getId()));
        NetworkAdapter.setSocket();


        handleActive(userSingleton.getId());
        setContentView(R.layout.activity_connection);
        userSpinner = (Spinner) findViewById(R.id.list);
        List<User> users = new LinkedList<>();
        handleRefresh(userSingleton.getId());

        startReadingNetworkMessages();
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    public void connect(View view) {
        Thread connect = new Thread(new Runnable() {
            @Override
            public void run() {
                User user = (User) userSpinner.getSelectedItem();
                NetworkAdapter.writeNewGameMessage(getTopic(user.getId()));
            }
        });
        connect.start();
    }

    @Override
    public void onPause() {
        super.onPause();
//        unregisterReceiver(receiver);
    }

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
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                        userList = userList.stream()
                                .filter(user -> user.getId() != null && !user.getId().equals(userSingleton.getId()))
                                .collect(Collectors.toList());
                    }
                    List<User> finalUserList = userList;
                    runOnUiThread(() -> {
                        userAdapter = new ArrayAdapter<User>(ConnectionActivity.this, R.layout.spinner, finalUserList) {
                            @Override
                            public View getView(int position, View convertView, ViewGroup parent) {
                                View view = convertView != null ? convertView : LayoutInflater.from(getContext()).inflate(R.layout.spinner, parent, false);

                                // Lấy User tại vị trí hiện tại
                                User user = getItem(position);

                                // Gán ID và tên cho TextView
                                TextView userId = view.findViewById(R.id.user_id);
                                TextView userName = view.findViewById(R.id.user_name);

                                userId.setText(String.valueOf(position + 1));  // Gán ID
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


    public void handleActive(Integer id) {

        Request request = Common.getRequest(null, Constants.GET_ACTIVE + "/" + id, Constants.POST);

        beService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LoginError", "Request failed", e);
                Toast.makeText(ConnectionActivity.this, "WRONG USERNAME OR PASSWORD", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                System.out.println("hoang");
            }
        });
    }

    public void handleUnactive(Integer id) {

        Request request = Common.getRequest(null, Constants.UNACTIVE + "/" + id, Constants.POST);

        beService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LoginError", "Request failed", e);
                Toast.makeText(ConnectionActivity.this, "WRONG USERNAME OR PASSWORD", Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
            }
        });
    }


    private List<User> convertListUser(List<Object> list) {
        List<User> users = new LinkedList<>();
        for (Object object : list) {
            LinkedTreeMap<String, String> linkedTreeMap = (LinkedTreeMap) object;
            Integer id = Integer.valueOf(String.valueOf(linkedTreeMap.get("id")).charAt(0)) - 48;
            User user = new User(id, linkedTreeMap.get("username"));
            users.add(user);
        }
        return users;
    }


    void startReadingNetworkMessages() {
        mqttHandler.getMqttMessage().observe(ConnectionActivity.this, new Observer<MqttObject>() {
            @Override
            public void onChanged(MqttObject mqttObject) {
                Log.d(MQTT_TAG, mqttObject.getMessage());
                if (Objects.equals(mqttObject.getMessage(), NetworkAdapter.NEW_GAME)) {
                    Log.d(MQTT_TAG, "New game requested, dialog given with yes or no options to accept or reject request"); //should send accept message message and reset game
                    resetPromptDialog(mqttObject.getUsername() + " " + getString(R.string.reset_game_connected_prompt), new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            NetworkAdapter.writeAcceptNewGameMessage(getTopic(mqttObject.getUserId()));
                            segueToPlaceShipsActivity(mqttObject.getUserId());
                        }
                    }, new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface dialog, int which) {
                            new Thread(new Runnable() {
                                @Override
                                public void run() {
                                    NetworkAdapter.writeRejectNewGameMessage(getTopic(mqttObject.getUserId()));
                                }
                            }).start();
                        }
                    });
                } else if (Objects.equals(mqttObject.getMessage(), NetworkAdapter.REJECT_NEW_GAME_REQUEST)) {
                    Toast.makeText(ConnectionActivity.this, mqttObject.getUsername() + " rejected your request", Toast.LENGTH_SHORT).show();
                } else if (Objects.equals(mqttObject.getMessage(), NetworkAdapter.ACCEPT_NEW_GAME_REQUEST)) {
                    Log.d(MQTT_TAG, "Accepted new game request");  //should send accept message message
                    Toast.makeText(ConnectionActivity.this, mqttObject.getUsername() + " accepted your request", Toast.LENGTH_SHORT).show();
                    segueToPlaceShipsActivity(mqttObject.getUserId());
                }
            }
        });

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

    @Override
    protected void onDestroy() {

        super.onDestroy();
//        this.mqttHandler.disconnect();
    }

    private void segueToPlaceShipsActivity(Integer opponentId) {
        final Context activity = this;
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Intent intent = new Intent(activity, PlaceShipsActivity.class);
                intent.putExtra("opponent_user", opponentId);
                startActivity(intent);
            }
        });
    }


    @Override
    protected void onStop() {
        super.onStop();
        handleUnactive(userSingleton.getId());
    }


    @Override
    protected void onRestart() {
        super.onRestart();
        handleActive(userSingleton.getId());
    }

    private String getTopic(Integer opponentId) {
        return "battleship/" + opponentId;
    }


}
