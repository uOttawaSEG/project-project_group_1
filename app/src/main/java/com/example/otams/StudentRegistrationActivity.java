package com.example.otams;


import android.content.Intent;
import android.os.Bundle;
import android.util.Patterns;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

import java.util.HashMap;


public class StudentRegistrationActivity extends AppCompatActivity {


    EditText firstName, lastName, email, password, phone, program;
    Button registerButton;

    public static HashMap<String, Student> students = new HashMap();


    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);


        firstName = findViewById(R.id.firstName);
        lastName = findViewById(R.id.lastName);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        phone = findViewById(R.id.phone);
        program = findViewById(R.id.program);
        registerButton = findViewById(R.id.registerButton);


        registerButton.setOnClickListener(v -> {
            String f = firstName.getText().toString().trim();
            String l = lastName.getText().toString().trim();
            String e = email.getText().toString().trim();
            String p = password.getText().toString().trim();
            String ph = phone.getText().toString().trim();
            String pr = program.getText().toString().trim();


            if(f.isEmpty() || l.isEmpty() || e.isEmpty() || p.isEmpty() || ph.isEmpty() || pr.isEmpty()){
                Toast.makeText(this, "All fields are required", Toast.LENGTH_SHORT).show();
                return;
            }


            if(!Patterns.EMAIL_ADDRESS.matcher(e).matches()){
                Toast.makeText(this, "Invalid email format", Toast.LENGTH_SHORT).show();
                return;
            }


            if(p.length() < 6){
                Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                return;
            }


            students.put(e, new Student(f, l, e, p, ph, pr));
            Toast.makeText(this, "Registered Successfully!", Toast.LENGTH_SHORT).show();


            Intent intent = new Intent(this, com.example.otams.LoginActivity.class);
            startActivity(intent);
            finish();


        });
    }
}
