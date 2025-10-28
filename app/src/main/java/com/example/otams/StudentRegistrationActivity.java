package com.example.otams;

import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.example.otams.data.AppDatabase;
import com.example.otams.repository.UserRepository;
import com.example.otams.repository.UserRepository.Result;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class StudentRegistrationActivity extends AppCompatActivity {
    private AppDatabase db;
    private UserRepository repo;
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private static final String TAG = "StudentRegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_registration);

        db = ((App) getApplication()).getDb();
        repo = new UserRepository(db);

        // UI elements
        EditText firstName = findViewById(R.id.firstName);
        EditText lastName  = findViewById(R.id.lastName);
        EditText email     = findViewById(R.id.email);
        EditText password  = findViewById(R.id.password);
        EditText phone     = findViewById(R.id.phone);
        EditText program   = findViewById(R.id.program);

        Button btnRegister = findViewById(R.id.registerButton);
        Button btnBack     = findViewById(R.id.btnBackStudent);

        // Register button
        btnRegister.setOnClickListener(v -> {
            String fn = firstName.getText().toString().trim();
            String ln = lastName.getText().toString().trim();
            String em = email.getText().toString().trim();
            String pw = password.getText().toString().trim();
            String ph = phone.getText().toString().trim();
            String prog = program.getText().toString().trim();

            if (fn.isEmpty() || ln.isEmpty() || em.isEmpty() || pw.isEmpty() || ph.isEmpty() || prog.isEmpty()) {
                Toast.makeText(this, "Please fill all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            io.execute(() -> {
                //  Save registration as a pending request, not a full user account
                Result<Long> result = repo.createStudentRegistrationRequest(fn, ln, em, pw, ph, prog);

                runOnUiThread(() -> {
                    if (result.success) {
                        //   Reflect that it’s awaiting admin approval
                        Toast.makeText(this, 
                            "Registration request submitted for approval!", 
                            Toast.LENGTH_LONG).show();
                        Log.i(TAG, "Student registration request submitted: " + em);
                        finish(); // go back to main screen
                    } else {
                        Toast.makeText(this, 
                            "Registration failed: " + result.message, 
                            Toast.LENGTH_LONG).show();
                        Log.e(TAG, "Registration failed for " + em + ": " + result.message);
                    }
                });
            });
        });

        // Back button
        btnBack.setOnClickListener(v -> finish()); // just close current activity
    }
}
