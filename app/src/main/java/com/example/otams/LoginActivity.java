package com.example.otams;


import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;


import androidx.appcompat.app.AppCompatActivity;


public class LoginActivity extends AppCompatActivity {


    EditText loginEmail, loginPassword;
    Button loginBtn;


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.loginactivity);


        loginEmail = findViewById(R.id.loginEmail);
        loginPassword = findViewById(R.id.loginPassword);
        loginBtn = findViewById(R.id.loginBtn);


        loginBtn.setOnClickListener(v ->{
            String email = loginEmail.getText().toString().trim();
            String password = loginPassword.getText().toString().trim();


            if(email.isEmpty() || password.isEmpty()){
                Toast.makeText(this,"Please enter email and password", Toast.LENGTH_SHORT).show();
                return;
            }


            Student s = MainActivity7.students.get(email);


            if(s == null || !s.password.equals(password))
            {
                Toast.makeText(this, "Invalid credentials" , Toast.LENGTH_SHORT).show();


            }


            else
            {
                Intent intent = new Intent(this, WelcomeActivity.class);
                intent.putExtra("role", "Student");
                startActivity(intent);
                finish();
            }
        });


    }
}

