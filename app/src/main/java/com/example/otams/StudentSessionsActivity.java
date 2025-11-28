package com.example.otams;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.RatingBar;


import androidx.appcompat.app.AppCompatActivity;

import com.example.otams.data.AppDatabase;
import com.example.otams.data.TutorAvailabilityDao;
import com.example.otams.data.TutorDao;
import com.example.otams.data.UserDao;
import com.example.otams.model.TutorAvailabilityEntity;
import com.example.otams.model.TutorEntity;
import com.example.otams.model.UserEntity;

import java.time.Duration;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
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

    //Cancel function
    private void attemptCancel(TutorAvailabilityEntity session){

        switch (session.requestStatus) {
            case "PENDING":
                performCancel(session);
                break;
            case "ACCEPTED" :
                if (canCancelAcceptedSession(session)){
                    performCancel(session);
                } else {
                    Toast.makeText(this, "You can only cancel approved sessions at least 24 hours before the start time.", Toast.LENGTH_LONG).show();
                }
                break;
            default:
                Toast.makeText(this, "This session cannot be cancelled.", Toast.LENGTH_LONG).show();
                break;
        }

    }
    // Cancel helper method
    private boolean canCancelAcceptedSession(TutorAvailabilityEntity session){
        String timeStr = session.date + " " + session.startTime;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime sessionstartTime = LocalDateTime.parse(timeStr, formatter);

        LocalDateTime now = LocalDateTime.now();
        Duration timeDiff = Duration.between(now, sessionstartTime);
        long diffinHours = timeDiff.toHours();

        return diffinHours >= 24;
    }
    //reset fields + Update database
    private void performCancel(TutorAvailabilityEntity session) {
        //Reset
        session.studentEmail = null;
        session.requestStatus = "NONE";

        // Update database
        new Thread( () -> {
            availabilityDao.update(session);

            runOnUiThread(() -> {
                loadSessions();
                Toast.makeText(this, "Session is cancelled. " , Toast.LENGTH_SHORT).show();
            });

        }).start();

    }

    //Unit test: 24-hours cancellation rule
    public static boolean testCanCancel(String date, String startTime, LocalDateTime now) {
        try {
            String timeStr = date + " " + startTime;
            DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
            LocalDateTime sessionStart = LocalDateTime.parse(timeStr, formatter);

            Duration diff = Duration.between(now, sessionStart);
            return diff.toHours() >= 24;

        } catch (Exception e) {
            return false;
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
        RatingBar ratingBar = item.findViewById(R.id.ratingBar);

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

        // use button to cancel the session
        Button btnCancel = item.findViewById(R.id.btnCancelSession);
        btnCancel.setOnClickListener(v -> {
            attemptCancel(session);
        });

        //set cancel button visibility
        switch (session.requestStatus) {
            case "PENDING" :
            case "ACCEPTED" :
                btnCancel.setVisibility(View.VISIBLE);
                btnCancel.setEnabled(true);
                break;
            default:
                btnCancel.setVisibility(View.GONE);
                break;
        }

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm");
        LocalDateTime sessionEndTime = LocalDateTime.parse(session.date + " " + session.endTime, formatter);

        if (sessionEndTime.isBefore(LocalDateTime.now())) {
            ratingBar.setIsIndicator(false); // allow rating
            if (session.rating != null) {
                ratingBar.setRating(session.rating);
            }
            ratingBar.setOnRatingBarChangeListener((bar, rating, fromUser) -> {
                if (fromUser) {
                    session.rating = (int) rating;
                    new Thread(() -> availabilityDao.update(session)).start();
                    Toast.makeText(this, "Rated " + (int) rating + " stars!", Toast.LENGTH_SHORT).show();
                }
            });
        } else {
            ratingBar.setIsIndicator(true); // future sessions cannot be rated
            ratingBar.setRating(session.rating != null ? session.rating : 0);
        }
    }
}
