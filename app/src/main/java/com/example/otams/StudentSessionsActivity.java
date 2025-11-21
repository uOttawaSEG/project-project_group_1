package com.example.otams;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.otams.data.AppDatabase;
import com.example.otams.data.TutorAvailabilityDao;
import com.example.otams.data.TutorDao;
import com.example.otams.data.UserDao;
import com.example.otams.model.TutorAvailabilityEntity;
import com.example.otams.model.TutorEntity;
import com.example.otams.model.UserEntity;
import java.util.List;

public class StudentSessionsActivity extends AppCompatActivity {

    private TutorAvailabilityDao availabilityDao;
    private TutorDao tutorDao;
    private String studentEmail;

    private UserDao userDao;

    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_student_sessions);

        AppDatabase db = ((App) getApplication()).getDb();
        availabilityDao = db.tutorAvailabilityDao();
        tutorDao = db.tutorDao();
        userDao = db.userDao();

        layout = findViewById(R.id.layoutSessions);

        studentEmail = getIntent().getStringExtra("email");

        loadSessions();

        Button btnQuitViewSessions = findViewById(R.id.btnQuitPreviousSessions);
        btnQuitViewSessions.setOnClickListener(v -> {
            Intent intent = new Intent(StudentSessionsActivity.this, MainActivity4.class);
            intent.putExtra("email", studentEmail);
            startActivity(intent);
        });
    }

    private void loadSessions() {
        layout.removeAllViews();

        List<TutorAvailabilityEntity> sessions =
                availabilityDao.getSessionsForStudent(studentEmail);

        for (TutorAvailabilityEntity session : sessions) {
            showSession(session);
        }
    }

    private void showSession(TutorAvailabilityEntity session) {
        View item = LayoutInflater.from(this)
                .inflate(R.layout.item_student_session, layout, false);
        layout.addView(item);

        TextView viewDate = item.findViewById(R.id.viewDate);
        TextView viewTime = item.findViewById(R.id.viewTime);
        TextView viewTutor = item.findViewById(R.id.viewTutor);
        TextView viewCourse = item.findViewById(R.id.viewCourse);
        TextView viewStatus = item.findViewById(R.id.viewStatus);

        UserEntity tutorUser = userDao.findByEmail(session.tutorEmail);
        TutorEntity tutor = tutorDao.findByEmail(session.tutorEmail);

        viewDate.setText(session.date);
        viewTime.setText(session.startTime + " - " + session.endTime);
        viewTutor.setText("Tutor: " + tutorUser.firstName + " " + tutorUser.lastName);
        viewCourse.setText("Course: " + tutor.coursesOffered.get(0));
        viewStatus.setText(session.requestStatus);


        // Color
        switch (session.requestStatus) {
            case "PENDING":
                viewStatus.setTextColor(0xFFFF9800); // orange
                break;
            case "ACCEPTED":
                viewStatus.setTextColor(0xFF4CAF50); // green
                break;
            case "REJECTED":
                viewStatus.setTextColor(0xFFF44336); // red
                break;
        }
    }
}
