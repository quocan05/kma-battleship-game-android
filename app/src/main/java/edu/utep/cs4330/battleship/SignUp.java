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

import java.io.IOException;

import edu.utep.cs4330.battleship.common.Common;
import edu.utep.cs4330.battleship.common.Constants;
import edu.utep.cs4330.battleship.dto.UserSingleton;
import edu.utep.cs4330.battleship.dto.request.LoginRequest;
import edu.utep.cs4330.battleship.dto.response.BEResponse;
import edu.utep.cs4330.battleship.service.BEService;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.MediaType;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by Gerardo Cervantes and Eric Torres on 4/17/2017.
 */

public class SignUp extends Activity {
    Button signup;

    EditText edtUsernameSignUp, edtPasswordSignUp;
    private BEService beService;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);
        beService = BEService.getInstance();
        //Buttons
        signup = (Button) findViewById(R.id.signuptrigger);
        edtPasswordSignUp = findViewById(R.id.passwordsignup);
        edtUsernameSignUp = findViewById(R.id.usernamesignup);
        signup.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String username = edtUsernameSignUp.getText().toString().trim();
                String password = edtPasswordSignUp.getText().toString().trim();

                // Check if both fields are filled in
                if (username.isEmpty() || password.isEmpty()) {
                    // Show a toast message if either field is empty
                    Toast.makeText(SignUp.this, "Please enter both username and password", Toast.LENGTH_SHORT).show();
                } else {
                    // Proceed to the next activity if both fields are filled
                    handleSignUp(username,password);
                }
            }
        });
    }

    private void handleSignUp(String username, String password) {
        LoginRequest loginRequest = new LoginRequest(username, password);
        Gson gson = new Gson();
        Request request = Common.getRequest(loginRequest,Constants.REGISTER,Constants.POST);

        beService.getClient().newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                Log.e("LoginError", "Request failed", e);
//                Toast.makeText(Lou.this, "WRONG USERNAME OR PASSWORD", Toast.LENGTH_SHORT).show();

            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                if (response.isSuccessful()) {
                    // handle response
                    String responseData = response.body().string();
                    BEResponse beResponse = gson.fromJson(responseData, BEResponse.class);
                    if(beResponse.getStatus()){
                        Intent i = new Intent(SignUp.this, Login.class);
                        startActivity(i);

                        //if done, terminate and go login
                        finish();
                    }
                }
            }
        });
    }
}
