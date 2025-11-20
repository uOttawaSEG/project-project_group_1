package com.example.otams;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
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

import java.util.ArrayList;
import java.util.List;

public class ViewSessions extends AppCompatActivity {

    private UserDao userDao;

    private TutorDao tutorDao;

    private TutorAvailabilityDao tutorAvailabilityDao;

    LinearLayout layout;

    private String studentEmail;

    private String courseCode;

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
        layout = findViewById(R.id.layoutViewSessions);
        tutorDao = db.tutorDao();
        tutorAvailabilityDao = db.tutorAvailabilityDao();
        studentEmail = getIntent().getStringExtra("email");


        EditText editCourseCode = findViewById(R.id.enterCourseCode);
        Button btnCourseCode = findViewById(R.id.btnCourseCode);

        btnCourseCode.setOnClickListener(v -> {
            courseCode = editCourseCode.getText().toString().trim();
            loadSessions();
        });

        Button btnQuitViewSessions = findViewById(R.id.btnQuitViewSessions);
        btnQuitViewSessions.setOnClickListener(v -> {
            Intent intent = new Intent(ViewSessions.this, MainActivity4.class);
            startActivity(intent);
        });
    }

    private void loadSessions() {
        List<TutorEntity> allTutors = tutorDao.getAllTutors();
        List<TutorEntity> validTutors = new ArrayList<TutorEntity>();
        for (int n = 0; n < allTutors.size(); n++) {
            TutorEntity tutor = allTutors.get(n);
            if (tutor.coursesOffered.contains(courseCode)) {
                validTutors.add(tutor);
            }
        }
        for (int n = 0; n < validTutors.size(); n++) {
            TutorEntity tutor = validTutors.get(n);
            String tutorID = tutor.userId;
            String email = tutorDao.getTutorEmail(tutorID);
            List<TutorAvailabilityEntity> listAvailabilities = tutorAvailabilityDao.getFutureAvailabilities(email);
            for (int i = 0; i < listAvailabilities.size(); i++) {
                TutorAvailabilityEntity slot = listAvailabilities.get(i);
                if (slot.studentEmail == null && !slot.requestStatus.equals("REJECTED")) {
                    showAvailabilities(listAvailabilities.get(i), email);
                }
                else {
                    continue;
                }
            }
        }
    }

    private void showAvailabilities(TutorAvailabilityEntity slot, String email) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_slot, layout, false);
        layout.addView(itemView);
        UserEntity tutor = userDao.findByEmail(email);
        TutorEntity TA = tutorDao.findByEmail(email);
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
        viewEmail.setText(email);
        viewDate.setText(slot.date);
        String time = slot.startTime + "-" + slot.endTime;
        viewTime.setText(time);
        viewCourse.setText(listCourses);
    }
}