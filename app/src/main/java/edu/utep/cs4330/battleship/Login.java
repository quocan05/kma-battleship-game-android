package edu.utep.cs4330.battleship;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

/**
 * Created by Gerardo Cervantes and Eric Torres on 4/17/2017.
 */

public class Login extends Activity {
    Button login;
    Button signup;

    EditText edtUsername, edtPassword;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

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
                    Toast.makeText(Login.this, "username:"+username+", password: "+password, Toast.LENGTH_SHORT).show();

                    //ex go to main menu
                    Intent i = new Intent(Login.this, MainMenu.class);
                    startActivity(i);
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
}
