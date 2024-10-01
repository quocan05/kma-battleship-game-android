package edu.utep.cs4330.battleship;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;

import edu.utep.cs4330.battleship.service.MusicService;

/**
 * Created by Gerardo Cervantes and Eric Torres on 4/17/2017.
 */

public class MainMenu extends Activity {
    Button single;
    Button multi;

    Button historyBtn;
    Intent intentThemeSound;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_menu);

        //Activity entrance and exit animation
        overridePendingTransition(android.R.anim.fade_in, android.R.anim.fade_out);
        //active theme sound

        intentThemeSound = new Intent(this, MusicService.class);
        startService(intentThemeSound);
        //Buttons
        single = (Button) findViewById(R.id.single);
        multi = (Button) findViewById(R.id.multi);
        historyBtn = findViewById(R.id.history);

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

        historyBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent i = new Intent(MainMenu.this, HistoryActivity.class);
                startActivity(i);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        stopService(intentThemeSound);
    }
}
