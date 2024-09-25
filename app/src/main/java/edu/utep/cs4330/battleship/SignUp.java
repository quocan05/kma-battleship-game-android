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

public class SignUp extends Activity {
    Button signup;

    EditText edtUsernameSignUp, edtPasswordSignUp;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        //Buttons
        signup = (Button) findViewById(R.id.signuptrigger);
        edtPasswordSignUp = findViewById(R.id.usernamesignup);
        edtUsernameSignUp = findViewById(R.id.passwordsignup);
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
                    Intent i = new Intent(SignUp.this, Login.class);
                    startActivity(i);

                    //if done, terminate and go login
                    finish();
                }
            }
        });
    }
}
