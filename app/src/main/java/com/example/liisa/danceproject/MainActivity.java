package com.example.liisa.danceproject;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.View;
import android.widget.Button;

public class MainActivity extends FragmentActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        Button createWorkoutButton = findViewById(R.id.create_workout);
        createWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchRecordActivity();
            }
        });

        Button selectWorkoutButton = findViewById(R.id.select_workout);
        selectWorkoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                launchSelectWorkoutActivity();
            }
        });
    }

    private void launchRecordActivity() {
        Intent intent = new Intent(this, RecordWorkoutActivity.class);
        startActivity(intent);
    }

    private void launchSelectWorkoutActivity() {
        Intent intent = new Intent(this, RecordWorkoutActivity.class);
        startActivity(intent);
    }
}
