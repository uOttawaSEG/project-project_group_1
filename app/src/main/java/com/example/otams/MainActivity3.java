package com.example.otams;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MainActivity3 extends AppCompatActivity {
    private String tutorEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main3); // Make sure this layout exists

        tutorEmail = getIntent().getStringExtra("email");

        Button buttonReturn = findViewById(R.id.logout);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity3.this, MainActivity.class);
                startActivity(intent);
                finish(); // optional, removes MainActivity2 from back stack
            }
        });

        Button btnManageAvailability = findViewById(R.id.btnManageAvailability);
        btnManageAvailability.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(MainActivity3.this, TutorAvailabilityActivity.class);

                i.putExtra("email", tutorEmail);
                startActivity(i);
            }
        });
    }
}