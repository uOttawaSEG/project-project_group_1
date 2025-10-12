package com.example.otams;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.view.View;
import android.widget.ArrayAdapter;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;


import com.example.otams.data.AppDatabase;
import com.example.otams.model.StudentEntity;
import com.example.otams.model.TutorEntity;
import com.example.otams.model.UserEntity;
import com.example.otams.repository.UserRepository;
import com.example.otams.repository.UserRepository.Result;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.Arrays;


public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private UserRepository repo;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private String lastStudentEmail;
    private String lastTutorEmail;
    private final String studentPwd = "Student#123";
    private final String tutorPwd   = "Tutor#123";

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = ((App) getApplication()).getDb();
        repo = new UserRepository(db);

        // Handle system bars
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // Buttons and fields
        Button btnRegisterStudent = findViewById(R.id.btnRegisterStudent);
        Button btnRegisterTutor   = findViewById(R.id.btnRegisterTutor);
        Button loginAcc = findViewById(R.id.login);
        EditText email = findViewById(R.id.email);
        EditText password = findViewById(R.id.password);

        // Spinner setup


        // Register student button
        btnRegisterStudent.setOnClickListener(v -> runDbTask(this::registerStudent));

        // Register Tutor button
        btnRegisterTutor.setOnClickListener(v -> {
            runDbTask(this::registerTutor);
            new Handler(Looper.getMainLooper()).postDelayed(() -> {
                Intent intent = new Intent(MainActivity.this, TutorRegistrationActivity.class);
                startActivity(intent);
            }, 500); // half-second delay
        });



        // Login button
        loginAcc.setOnClickListener(v -> {
            String mail = email.getText().toString().trim();
            String pass = password.getText().toString().trim();

            if (mail.isEmpty() || pass.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            runDbTask(() -> {
                // Assuming you add this method to UserRepository
                UserEntity user = repo.findByEmail(mail);

                runOnUiThread(() -> {
                    if (user != null) {
                        Toast.makeText(MainActivity.this, "Sign in successful", Toast.LENGTH_LONG).show();

                        Intent intent;
                        String role = user.role;
                        if (role.equalsIgnoreCase("ADMIN")) {
                            intent = new Intent(MainActivity.this, MainActivity2.class);
                        } else if (role.equalsIgnoreCase("TUTOR")) {
                            intent = new Intent(MainActivity.this, MainActivity3.class);
                        } else {
                            intent = new Intent(MainActivity.this, MainActivity4.class);
                        }

                        intent.putExtra("firstname", user.firstName);
                        intent.putExtra("email", user.email);
                        intent.putExtra("role", user.role);
                        startActivity(intent);
                    } else {
                        Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_LONG).show();
                    }
                });
            });
        });
    }

    private void registerStudent() {
        String ts = new SimpleDateFormat("HHmmss", Locale.US).format(new Date());
        lastStudentEmail = "student_" + ts + "@otams.ca";

        Result<String> r = repo.registerStudent(
                "Tom", "Lee", lastStudentEmail, studentPwd, "+1-555-0101", "Computer Science"
        );

        if (r.success) {
            Log.i(TAG, r.data + ' ' + lastStudentEmail);
        } else {
            Log.e(TAG, "Error message: Something went wrong!");
        }
    }
    private void registerTutor() {
        String ts = new SimpleDateFormat("HHmmss", Locale.US).format(new Date());
        lastTutorEmail = "tutor_" + ts + "@otams.ca";

        Result<String> r = repo.registerTutor(
                "Ann",
                "Wang",
                lastTutorEmail,
                tutorPwd,
                "+1-555-0102",
                "MSc",
                Arrays.asList("CSI2132", "MAT1322")
        );

        if (r.success) {
            Log.i(TAG, r.data + ' ' + lastTutorEmail);
        } else {
            Log.e(TAG, "Error message: Something went wrong!");
        }
    }



    private void runDbTask(Runnable r) {
        io.execute(() -> {
            try {
                r.run();
            } catch (Throwable t) {
                Log.e(TAG, t.getMessage());
            }
        });
    }
}
