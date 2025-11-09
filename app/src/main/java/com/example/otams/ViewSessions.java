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
import com.example.otams.data.TutorDao;
import com.example.otams.data.UserDao;
import com.example.otams.model.TutorAvailabilityEntity;
import com.example.otams.model.TutorEntity;
import com.example.otams.model.UserEntity;

import java.util.List;

public class ViewSessions extends AppCompatActivity {

    private UserDao userDao;

    private TutorDao tutorDao;

    private TutorAvailabilityDao tutorAvailabilityDao;

    LinearLayout layout;

    private String studentEmail;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_sessions);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        AppDatabase db = ((App) getApplication()).getDb();
        userDao = db.userDao();
        tutorDao = db.tutorDao();
        tutorAvailabilityDao = db.tutorAvailabilityDao();
        layout = findViewById(R.id.layoutViewSessions);
        studentEmail = getIntent().getStringExtra("email");

        Button btnQuit = findViewById(R.id.btnQuitViewSessions);
        btnQuit.setOnClickListener(v -> {
            Intent intent = new Intent(ViewSessions.this, MainActivity4.class);
            startActivity(intent);
        });

        loadSessions();
    }

    private void loadSessions() {
        List<TutorEntity> allTutors = tutorDao.getAllTutors();
        for (int n = 0; n < allTutors.size(); n++) {
            TutorEntity tutor = allTutors.get(n);
            String tutorID = tutor.userId;
            String email = tutorDao.getTutorEmail(tutorID);
            List<TutorAvailabilityEntity> listAvailabilities = tutorAvailabilityDao.getFutureAvailabilities(email);
            for (int i = 0; i < listAvailabilities.size(); i++) {
                TutorAvailabilityEntity slot = listAvailabilities.get(i);
                if (slot.studentEmail == null && !slot.requestStatus.equals("REJECTED")) {
                    showAvailabilities(listAvailabilities.get(i), tutorID);
                }
                else {
                    continue;
                }
            }
        }
    }

    private void showAvailabilities(TutorAvailabilityEntity slot, String tutorID) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_slot, layout, false);
        layout.addView(itemView);
        String tutorEmail = tutorDao.getTutorEmail(tutorID);
        UserEntity tutor = userDao.findByEmail(tutorEmail);
        TutorEntity TA = tutorDao.findByEmail(tutorEmail);

        Button btnAdd = itemView.findViewById(R.id.btnAddSession);
        btnAdd.setOnClickListener(v -> {
            if (TutorAvailabilityEntity.autoApproval == true) {
                slot.requestStatus = "ACCEPTED";
            }
            else if (TutorAvailabilityEntity.autoApproval == false) {
                slot.requestStatus = "PENDING";
            }
            slot.studentEmail = studentEmail;
            tutorAvailabilityDao.update(slot);
            layout.removeView(itemView);
        });

        List<String> courses = TA.coursesOffered;
        String listCourses = "";
        for (int i = 0; i < courses.size(); i++) {
            listCourses = listCourses + courses.get(i);
        }

        TextView viewFirstName = itemView.findViewById(R.id.slotFName);
        TextView viewLastName = itemView.findViewById(R.id.slotLName);
        TextView viewEmail = itemView.findViewById(R.id.slotEmail);
        TextView viewDate = itemView.findViewById(R.id.slotDate);
        TextView viewTime = itemView.findViewById(R.id.slotTime);
        TextView viewCourse = itemView.findViewById(R.id.slotCourse);

        viewFirstName.setText(tutor.firstName);
        viewLastName.setText(tutor.lastName);
        viewEmail.setText(tutorEmail);
        viewDate.setText(slot.date);
        String time = slot.startTime + "-" + slot.endTime;
        viewTime.setText(time);
        viewCourse.setText(listCourses);
    }
}