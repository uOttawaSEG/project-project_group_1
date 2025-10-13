package com.example.otams;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;


import androidx.appcompat.app.AppCompatActivity;


public class WelcomeActivity extends AppCompatActivity {


    TextView welcomeText;
    Button logoutBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.welcomeactivity);


        welcomeText = findViewById(R.id.welcomeText);
        logoutBtn = findViewById(R.id.logoutBtn);


        String role = getIntent().getStringExtra("role");
        welcomeText.setText(getString(R.string.welcome_role, role));


        logoutBtn.setOnClickListener(v -> {
            Intent intent = new Intent(this, com.example.otams.LoginActivity.class);
            startActivity(intent);
            finish();
        });
    }
}

