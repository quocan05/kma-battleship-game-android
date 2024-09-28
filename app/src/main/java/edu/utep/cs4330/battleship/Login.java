package edu.utep.cs4330.battleship;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.gson.Gson;
import com.google.gson.internal.LinkedTreeMap;

import org.eclipse.paho.client.mqttv3.MqttClient;

import java.io.IOException;

import edu.utep.cs4330.battleship.common.Common;
import edu.utep.cs4330.battleship.common.Constants;
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
 * Created by Gerardo Cervantes and Eric Torres on 4/17/2017.
 */

public class Login extends Activity {
    Button login;
    Button signup;

    EditText edtUsername, edtPassword;
    private BEService beService;
    private Gson gson;
    private UserSingleton userSingleton;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        beService = BEService.getInstance();
        gson = new Gson();
        //Activity entrance and exit animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);

        //Buttons
        login = (Button) findViewById(R.id.login);
        signup = (Button) findViewById(R.id.signup);

        edtUsername = findViewById(R.id.username);
        edtPassword =findViewById(R.id.password);

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //check logic login
                String username = edtUsername.getText().toString().trim();
                String password = edtPassword.getText().toString().trim();

                // Check if both fields are filled in
                if (username.isEmpty() || password.isEmpty()) {
                    // Show a toast message if either field is empty
                    Toast.makeText(Login.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed to the next activity if both fields are filled
//                    Toast.makeText(Login.this, "username:"+username+", password: "+password, Toast.LENGTH_SHORT).show();
//
//                    //ex go to main menu
//                    Intent i = new Intent(Login.this, MainMenu.class);
//                    startActivity(i);
                    handleLogin(username,password);
                }

            }
        });

        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(Login.this, SignUp.class);
                startActivity(i);
            }
        });
    }

    public void handleLogin(String username,String password){
        LoginRequest loginRequest = new LoginRequest(username, password);
        Request request = Common.getRequest(loginRequest,Constants.LOGIN,Constants.POST);

        beService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LoginError", "Request failed", e);
                Toast.makeText(Login.this, "WRONG USERNAME OR PASSWORD", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // handle response
                    String responseData = response.body().string();
                    BEResponse beResponse = gson.fromJson(responseData,BEResponse.class);
                    userSingleton = UserSingleton.getInstance();
                    LinkedTreeMap<String,String> linkedTreeMap = (LinkedTreeMap) beResponse.getData();
                    Integer id = Integer.valueOf(String.valueOf(linkedTreeMap.get("id")).charAt(0))-48;
                    userSingleton.setId(id);
                    userSingleton.setName(linkedTreeMap.get("name"));
////                    Toast.makeText(Login.this, "username:"+username+", password: "+password, Toast.LENGTH_SHORT).show();
//                    Toast.makeText(Login.this,"SUCCESS LOGIN",Toast.LENGTH_SHORT).show();
                    //ex go to main menu
                    Intent i = new Intent(Login.this, MainMenu.class);
                    startActivity(i);
                    MqttHandler mqttHandler = MqttHandler.getInstance();
                    mqttHandler.subscribe("battleship/"+ id);
                }
            }
        });
    }
}
