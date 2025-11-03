package com.example.otams;

import android.os.Bundle;
import androidx.appcompat.app.AppCompatActivity;
import android.content.Intent;
import android.view.View;
import android.widget.Button;


public class MainActivity4 extends AppCompatActivity {

    private String studentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main4); // Make sure this layout exists

        Button buttonReturn = findViewById(R.id.logout);
        buttonReturn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity4.this, MainActivity.class);
                startActivity(intent);
                finish(); // optional, removes MainActivity2 from back stack
            }
        });

        studentEmail = getIntent().getStringExtra("email");

        Button btnViewSessions = findViewById(R.id.btnViewSessions);
        btnViewSessions.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity4.this, ViewSessions.class);
            intent.putExtra("email", studentEmail);
            startActivity(intent);
        });

    }
}