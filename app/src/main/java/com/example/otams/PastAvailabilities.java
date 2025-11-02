package com.example.otams;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.EdgeToEdge;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.graphics.Insets;
import androidx.core.view.ViewCompat;
import androidx.core.view.WindowInsetsCompat;

import com.example.otams.data.AppDatabase;
import com.example.otams.data.TutorAvailabilityDao;
import com.example.otams.data.UserDao;
import com.example.otams.model.TutorAvailabilityEntity;
import com.example.otams.model.UserEntity;

import java.util.List;

public class PastAvailabilities extends AppCompatActivity {
    private String tutorEmail;

    private UserDao userDao;

    private TutorAvailabilityDao tutorDao;

    private AppDatabase db;

    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_past_availabilities);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = ((App) getApplication()).getDb();
        tutorDao = db.tutorAvailabilityDao();
        userDao = db.userDao();
        layout = findViewById(R.id.layoutPast);
        tutorEmail = getIntent().getStringExtra("email");

        Button btnQuit = findViewById(R.id.btnQuit);
        btnQuit.setOnClickListener(v -> {
            Intent intent = new Intent(PastAvailabilities.this, TutorAvailabilityActivity.class);
            intent.putExtra("email", tutorEmail);
            startActivity(intent);
        });




    }

    private void loadPastAvailabilities() {
        List<TutorAvailabilityEntity> pastAvailabilities = tutorDao.getPastAvailabilities(tutorEmail);
        for (int n = 0; n < pastAvailabilities.size(); n++) {
            showPastAvailability(pastAvailabilities.get(n));
        }
    }

    private void showPastAvailability(TutorAvailabilityEntity pastAvailability) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_past_availability, layout, false);
        layout.addView(itemView);

        String studentEmail = pastAvailability.studentEmail;

        if (studentEmail != null) {
            UserEntity student = userDao.findByEmail(studentEmail);

            TextView viewFirstName = itemView.findViewById(R.id.pastFName);
            TextView viewLastName = itemView.findViewById(R.id.pastFName);
            TextView viewEmail = itemView.findViewById(R.id.pastEmail);
            TextView viewDate = itemView.findViewById(R.id.pastDate);
            TextView viewTime = itemView.findViewById(R.id.pastTime);
            TextView viewStatus = itemView.findViewById(R.id.pastStatus);


            viewStatus.setText("SESSION EXPIRED");
            viewFirstName.setText(student.firstName);
            viewLastName.setText(student.lastName);
            viewEmail.setText(student.email);
            viewDate.setText(pastAvailability.date);
            viewTime.setText(pastAvailability.endTime);
        }
        else {

            TextView viewFirstName = itemView.findViewById(R.id.pastFName);
            TextView viewLastName = itemView.findViewById(R.id.pastFName);
            TextView viewEmail = itemView.findViewById(R.id.pastEmail);
            TextView viewDate = itemView.findViewById(R.id.pastDate);
            TextView viewTime = itemView.findViewById(R.id.pastTime);
            TextView viewStatus = itemView.findViewById(R.id.pastStatus);

            viewStatus.setText("SESSION EXPIRED");
            viewFirstName.setText("NO STUDENT HAS REGISTERED TO THIS SLOT");
            viewLastName.setText("NO STUDENT HAS REGISTERED TO THIS SLOT");
            viewEmail.setText("NO STUDENT HAS REGISTERED TO THIS SLOT");
            viewDate.setText(pastAvailability.date);
            viewTime.setText(pastAvailability.endTime);
        }
    }
}