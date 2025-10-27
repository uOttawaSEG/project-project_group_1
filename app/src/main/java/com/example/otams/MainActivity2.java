package com.example.otams;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;

public class MainActivity2 extends AppCompatActivity {
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2); // Make sure this layout exists

        Button buttonReturn = findViewById(R.id.logout);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity2.this, MainActivity.class);
                startActivity(intent);
                finish(); // optional, removes MainActivity2 from back stack
            }
        });

        // UI elements
        Button buttonRequests = findViewById(R.id.btnPendingRequests);

        // View Pending Requests button
        buttonRequests.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, ViewPendingRequests.class);
            startActivity(intent);
        });

        Button btnRejected = findViewById(R.id.btnRejected);
        btnRejected.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity2.this, ViewRejectedRequests.class);
            startActivity(intent);
        });

    }




}