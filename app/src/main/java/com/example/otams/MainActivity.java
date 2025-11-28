package com.example.otams;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import android.util.Log;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.otams.data.AppDatabase;
import com.example.otams.model.UserEntity;
import com.example.otams.model.RequestStatus;
import com.example.otams.repository.UserRepository;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class MainActivity extends AppCompatActivity {
    private AppDatabase db;
    private UserRepository repo;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_main);

        db = ((App) getApplication()).getDb();
        repo = new UserRepository(db);

        com.example.otams.util.FakeDataInserter.insertFakeSessions(db);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        // UI elements
        Button btnRegisterStudent = findViewById(R.id.btnRegisterStudent);
        Button btnRegisterTutor   = findViewById(R.id.btnRegisterTutor);
        Button loginAcc           = findViewById(R.id.login);
        EditText emailField       = findViewById(R.id.email);
        EditText passwordField    = findViewById(R.id.password);

        // Register buttons
        btnRegisterStudent.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, StudentRegistrationActivity.class);
            startActivity(intent);
        });

        btnRegisterTutor.setOnClickListener(v -> {
            Intent intent = new Intent(MainActivity.this, TutorRegistrationActivity.class);
            startActivity(intent);
        });

        // Login button
        loginAcc.setOnClickListener(v -> {
            String email = emailField.getText().toString().trim();
            String password = passwordField.getText().toString().trim();

            if (email.isEmpty() || password.isEmpty()) {
                Toast.makeText(MainActivity.this, "Please enter all fields", Toast.LENGTH_SHORT).show();
                return;
            }

            io.execute(() -> {
                try {
                    // Step 1: Check if this email has a pending registration request
                    RequestStatus status = repo.getRequestStatusByEmail(email);
                    UserEntity user = repo.findByEmail(email);

                    runOnUiThread(() -> {
                        // Step 2: Handle based on account existence & status
                        if (user != null) {
                            // Approved and in main users table → normal login flow
                            if (repo.verifyPassword(email, password)) {
                                Toast.makeText(MainActivity.this, "Approved – Welcome!", Toast.LENGTH_LONG).show();

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
                                Toast.makeText(MainActivity.this, "Invalid email or password", Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // No approved account yet → check registration request
                            if (status == null) {
                                Toast.makeText(MainActivity.this,
                                        "No account found. Please register first.",
                                        Toast.LENGTH_LONG).show();
                            } else if (status == RequestStatus.PENDING) {
                                Toast.makeText(MainActivity.this,
                                        "Pending approval. Please wait for admin approval.",
                                        Toast.LENGTH_LONG).show();
                            } else if (status == RequestStatus.REJECTED) {
                                Toast.makeText(MainActivity.this,
                                        "Rejected – contact admin at 613-555-1234 for assistance.",
                                        Toast.LENGTH_LONG).show();
                            } else {
                                // This should rarely happen
                                Toast.makeText(MainActivity.this,
                                        "Unknown account status. Please contact support.",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error during login: ", e);
                    runOnUiThread(() -> Toast.makeText(MainActivity.this,
                            "An error occurred. Please try again later.",
                            Toast.LENGTH_LONG).show());
                }
            });
        });
    }
}
