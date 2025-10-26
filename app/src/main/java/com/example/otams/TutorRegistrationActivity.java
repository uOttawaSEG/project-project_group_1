package com.example.otams;

import android.os.Bundle;
import android.util.Log;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;

import com.example.otams.data.AppDatabase;
import com.example.otams.formValidation.Validation;
import com.example.otams.repository.UserRepository;
import com.example.otams.repository.UserRepository.Result;

import java.util.Arrays;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class TutorRegistrationActivity extends AppCompatActivity {

    private AppDatabase db;
    private UserRepository repo;
    private final ExecutorService io = Executors.newSingleThreadExecutor();
    private static final String TAG = "TutorRegistrationActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_registration);

        // db + repository setup
        db = ((App) getApplication()).getDb();
        repo = new UserRepository(db);

        // input fields
        EditText etFirstName = findViewById(R.id.etFirstName);
        EditText etLastName = findViewById(R.id.etLastName);
        EditText etEmail = findViewById(R.id.etEmail);
        EditText etPassword = findViewById(R.id.etPassword);
        EditText etPhone = findViewById(R.id.etPhone);
        EditText etHighestDegree = findViewById(R.id.etHighestDegree);
        EditText etCoursesOffered = findViewById(R.id.etCoursesOffered);

        // buttons
        Button btnBack = findViewById(R.id.btnBack);
        Button btnSubmitTutor = findViewById(R.id.btnSubmitTutor);

        btnBack.setOnClickListener(v -> finish());

        btnSubmitTutor.setOnClickListener(v -> {
            String firstName = etFirstName.getText().toString().trim();
            String lastName = etLastName.getText().toString().trim();
            String email = etEmail.getText().toString().trim();
            String password = etPassword.getText().toString().trim();
            String phone = etPhone.getText().toString().trim();
            String highestDegree = etHighestDegree.getText().toString().trim();
            String coursesStr = etCoursesOffered.getText().toString().trim();

            String err;
            if ((err = Validation.checkName(firstName)) != null) { showError(err); return; }
            if ((err = Validation.checkName(lastName)) != null) { showError(err); return; }
            if ((err = Validation.checkEmail(email)) != null) { showError(err); return; }
            if ((err = Validation.checkPassword(password)) != null) { showError(err); return; }
            if ((err = Validation.checkPhone(phone)) != null) { showError(err); return; }
            if ((err = Validation.checkHighestDegree(highestDegree)) != null) { showError(err); return; }
            if ((err = Validation.checkCourse(coursesStr)) != null) { showError(err); return; }

            List<String> courses = Arrays.asList(coursesStr.split("\\s*,\\s*"));

            io.execute(() -> {
                try {
                    //  changed: save as registration request (pending)
                    Result<Long> result = repo.createTutorRegistrationRequest(
                            firstName,
                            lastName,
                            email,
                            password,
                            phone,
                            highestDegree,
                            courses
                    );

                    runOnUiThread(() -> {
                        if (result.success) {
                            //  changed message
                            Toast.makeText(this,
                                    "Registration request submitted for approval!",
                                    Toast.LENGTH_LONG).show();
                            finish();
                        } else {
                            Toast.makeText(this,
                                    "Registration failed: " + result.message,
                                    Toast.LENGTH_LONG).show();
                        }
                    });
                } catch (Exception e) {
                    Log.e(TAG, "Error registering tutor", e);
                    runOnUiThread(() ->
                            Toast.makeText(this,
                                    "An error occurred during registration.",
                                    Toast.LENGTH_LONG).show()
                    );
                }
            });
        });
    }

    private void showError(String msg) {
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();
    }
}
