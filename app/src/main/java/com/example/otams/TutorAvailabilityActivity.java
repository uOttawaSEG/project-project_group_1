package com.example.otams;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.otams.data.AppDatabase;
import com.example.otams.data.TutorAvailabilityDao;
import com.example.otams.data.TutorDao;
import com.example.otams.data.UserDao;
import com.example.otams.model.TutorAvailabilityEntity;
import com.example.otams.App;
import com.example.otams.model.TutorEntity;
import com.example.otams.model.UserEntity;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class TutorAvailabilityActivity extends AppCompatActivity {

    private TutorAvailabilityDao dao;

    private UserDao userDao;

    private TutorDao tutorDao;

    private String tutorEmail;

    LinearLayout layout;


    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_availability);

        AppDatabase db = ((App) getApplication()).getDb();
        dao = db.tutorAvailabilityDao();
        userDao = db.userDao();
        tutorDao = db.tutorDao();
        layout = findViewById(R.id.layoutCurrent);

        tutorEmail = getIntent().getStringExtra("email");
        if (tutorEmail == null) {

            tutorEmail = "tutor@otams.ca";
        }

        Button btnPast = findViewById(R.id.btnPast);
        btnPast.setOnClickListener(v -> {
            Intent intent = new Intent(TutorAvailabilityActivity.this, PastAvailabilities.class);
            intent.putExtra("email", tutorEmail);
            startActivity(intent);
        });

        // Pick availability slot on Calendar button
        Button btnAdd = findViewById(R.id.btnAddSlot);
        btnAdd.setOnClickListener(v -> showAddDialog());

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> {
            Intent intent = new Intent(TutorAvailabilityActivity.this, MainActivity3.class);
            intent.putExtra("email", tutorEmail);
            startActivity(intent);
        });
        // TODO auto-approve functionalities
        // When clicked, requestStatus of TutorAvailabilityEntity goes from NONE -> ACCEPTED automatically
        // After it is clicked, all future slots should automatically become ACCEPTED, the button only needs to be clicked once
        // The autoApprove variable of TutorAvailabilityEntity -> true
        // You will have to add a new condition for the display of the slots below (if autoApprove == true and studentEmail == null)
        // Use TutorAvailabilityActivity, TutorAvailabilityEntity and TutorAvailabilityDao as reference
        // Use the update method of TutorAvailabilityDao (dao.update)
        // Create a student and make it apply for slots, if the button is clicked it's status should automatically be ACCEPTED, not PENDING
        // OPTIONAL: add functionality and text to turn auto-approve ON and OFF
        // 我知道你能行! 六六六你真牛！
        Button btnAutoApprove = findViewById(R.id.btnAutoApprove);
        if (TutorAvailabilityEntity.autoApproval == false) {
            btnAutoApprove.setText("ENABLE AUTO-APPROVE");
        }
        else if (TutorAvailabilityEntity.autoApproval == true) {
            btnAutoApprove.setText("DISABLE AUTO-APPROVE");
        }
        btnAutoApprove.setOnClickListener(v -> {
            if (TutorAvailabilityEntity.autoApproval == false) {
                btnAutoApprove.setText("DISABLE AUTO-APPROVE");
                enableAutoApprove();
            }
            else if (TutorAvailabilityEntity.autoApproval == true) {
                btnAutoApprove.setText("ENABLE AUTO-APPROVE");
                disableAutoApprove();
            }
        });
        layout.removeAllViews();
        loadCurrentAvailabilities();
    }

    private void enableAutoApprove() {
        TutorAvailabilityEntity.autoApproval = true;
        List<TutorAvailabilityEntity> currentAvailabilities = dao.getFutureAvailabilities(tutorEmail);
        for (int n = 0; n < currentAvailabilities.size(); n++) {
            TutorAvailabilityEntity slot = currentAvailabilities.get(n);
            if (!slot.requestStatus.equals("ACCEPTED")) {
                slot.requestStatus = "ACCEPTED";
                dao.update(slot);
            }
        }
        layout.removeAllViews();
        loadCurrentAvailabilities();
    }

    private void disableAutoApprove() {
        TutorAvailabilityEntity.autoApproval = false;
    }

    private void loadCurrentAvailabilities() {
        layout.removeAllViews();
        List<TutorAvailabilityEntity> currentAvailabilities = dao.getFutureAvailabilities(tutorEmail);
        for (int n = 0; n < currentAvailabilities.size(); n++) {
            showCurrentAvailabilities(currentAvailabilities.get(n));
        }
    }

    private void showCurrentAvailabilities(TutorAvailabilityEntity currentAvailability) {
        View itemView = LayoutInflater.from(this).inflate(R.layout.item_tutor_availability, layout, false);
        layout.addView(itemView);



        // TODO Button Approve Request
        // currentAvailability requestStatus from PENDING -> ACCEPTED (all uppercase)
        // Check TutorAvailabilityEntity and TutorAvailabilityDao for help
        // Go to the student page to send a request to test if it works
        // An availability slot is 30 minutes
        Button btnApproveRequest = itemView.findViewById(R.id.btnApproveRequest);
        btnApproveRequest.setOnClickListener(v -> {
            if (currentAvailability.studentEmail != null && currentAvailability.requestStatus.equals("ACCEPTED")) {
                Toast.makeText(TutorAvailabilityActivity.this, "The student is already accepted!", Toast.LENGTH_SHORT).show();
            }
            else if (currentAvailability.studentEmail != null) {
                currentAvailability.requestStatus = "ACCEPTED";
                dao.update(currentAvailability);
                layout.removeAllViews();
                loadCurrentAvailabilities();
            }
            else {
                Toast.makeText(TutorAvailabilityActivity.this, "There is no student to accept!", Toast.LENGTH_SHORT).show();
            }
        });

        // TODO Button Reject Request
        // Clear the availability slot of it's associated student and reset status to NONE
        // Check TutorAvailabilityEntity and TutorAvailabilityDao for help
        // Go to the student page to send a request to test if it works
        // No student information should be displayed anymore
        // An availability slot is 30 minutes
        Button btnRejectRequest = itemView.findViewById(R.id.btnRejectRequest);
        btnRejectRequest.setOnClickListener(v -> {
            if (currentAvailability.studentEmail != null) {
                currentAvailability.requestStatus = "REJECTED";
                currentAvailability.studentEmail = null;
                dao.update(currentAvailability);
                layout.removeAllViews();
                loadCurrentAvailabilities();
            }
            else {
                Toast.makeText(TutorAvailabilityActivity.this, "There is no student to reject!", Toast.LENGTH_SHORT).show();
            }

        });

        // TODO Button Delete Slot
        // Just delete the slot
        // Check TutorAvailabilityEntity and TutorAvailabilityDao for help
        Button btnDelete = itemView.findViewById(R.id.btnDelete);
        btnDelete.setOnClickListener(v -> {
            dao.delete(currentAvailability);
            layout.removeAllViews();
            loadCurrentAvailabilities();
            Toast.makeText(TutorAvailabilityActivity.this, "Slot deleted", Toast.LENGTH_SHORT).show();

        });

        String studentEmail = currentAvailability.studentEmail;
        TutorEntity tutor = tutorDao.findByEmail(tutorEmail);
        List<String> courses = tutor.coursesOffered;
        String listCourses = "";
        for (int i = 0; i < courses.size(); i++) {
            listCourses = listCourses + courses.get(i);
        }
        if (studentEmail == null && currentAvailability.requestStatus.equals("ACCEPTED")) {
            TextView viewFirstName = itemView.findViewById(R.id.currentFName);
            TextView viewLastName = itemView.findViewById(R.id.currentLName);
            TextView viewEmail = itemView.findViewById(R.id.currentEmail);
            TextView viewDate = itemView.findViewById(R.id.currentDate);
            TextView viewTime = itemView.findViewById(R.id.currentTime);
            TextView viewRequestStatus = itemView.findViewById(R.id.currentStatus);
            TextView viewCourse = itemView.findViewById(R.id.currentCourse);

            viewFirstName.setText("NO STUDENT IS REGISTERED TO THIS TIME SLOT");
            viewLastName.setText("NO STUDENT IS REGISTERED TO THIS TIME SLOT");
            viewEmail.setText("NO STUDENT IS REGISTERED TO THIS TIME SLOT");
            viewDate.setText(currentAvailability.date);
            String time = currentAvailability.startTime + "-" + currentAvailability.endTime;
            viewTime.setText(time);
            viewRequestStatus.setText("ACCEPTED");
            viewCourse.setText(listCourses);
        }

         else if (studentEmail == null && (currentAvailability.requestStatus.equals("NONE") || currentAvailability.requestStatus.equals("REJECTED"))) {
            TextView viewFirstName = itemView.findViewById(R.id.currentFName);
            TextView viewLastName = itemView.findViewById(R.id.currentLName);
            TextView viewEmail = itemView.findViewById(R.id.currentEmail);
            TextView viewDate = itemView.findViewById(R.id.currentDate);
            TextView viewTime = itemView.findViewById(R.id.currentTime);
            TextView viewRequestStatus = itemView.findViewById(R.id.currentStatus);
            TextView viewCourse = itemView.findViewById(R.id.currentCourse);

            viewFirstName.setText("NO STUDENT IS REGISTERED TO THIS TIME SLOT");
            viewLastName.setText("NO STUDENT IS REGISTERED TO THIS TIME SLOT");
            viewEmail.setText("NO STUDENT IS REGISTERED TO THIS TIME SLOT");
            viewDate.setText(currentAvailability.date);
            String time = currentAvailability.startTime + "-" + currentAvailability.endTime;
            viewTime.setText(time);
            viewRequestStatus.setText("NONE");
            viewCourse.setText(listCourses);
        }

        else {
            UserEntity student = userDao.findByEmail(studentEmail);

            TextView viewFirstName = itemView.findViewById(R.id.currentFName);
            TextView viewLastName = itemView.findViewById(R.id.currentLName);
            TextView viewEmail = itemView.findViewById(R.id.currentEmail);
            TextView viewDate = itemView.findViewById(R.id.currentDate);
            TextView viewTime = itemView.findViewById(R.id.currentTime);
            TextView viewRequestStatus = itemView.findViewById(R.id.currentStatus);
            TextView viewCourse = itemView.findViewById(R.id.currentCourse);

            viewFirstName.setText(student.firstName);
            viewLastName.setText(student.lastName);
            viewEmail.setText(student.email);
            viewDate.setText(currentAvailability.date);
            String time = currentAvailability.startTime + "-" + currentAvailability.endTime;
            viewTime.setText(time);
            viewRequestStatus.setText(currentAvailability.requestStatus);
            viewCourse.setText(listCourses);
        }

    }

    // Pick availability slot on Calendar
    private void showAddDialog() {
        final Calendar now = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String dateStr = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);

                    // start time
                    TimePickerDialog startTimeDialog = new TimePickerDialog(
                            this,
                            (view1, hourOfDay, minute) -> {
                                int startMin = to30(minute);
                                final String startStr = String.format("%02d:%02d", hourOfDay, startMin);

                                // end time
                                TimePickerDialog endTimeDialog = new TimePickerDialog(
                                        this,
                                        (view2, endHour, endMinute) -> {
                                            int endMin = to30(endMinute);
                                            final String endStr = String.format("%02d:%02d", endHour, endMin);

                                            Calendar selectedTime = Calendar.getInstance();
                                            selectedTime.set(year, month, dayOfMonth, hourOfDay, startMin, 0);

                                            if (selectedTime.before(now)) {
                                                Toast.makeText(this, "Selected date/time has already passed", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            if (endStr.compareTo(startStr) <= 0) {
                                                Toast.makeText(this, "End must be after start", Toast.LENGTH_SHORT).show();
                                                return;
                                            }


                                            // Each availability slot must be 30 minutes
                                            if (isThirtyMinutes(hourOfDay, endHour, startMin, endMin) == false) {
                                                Toast.makeText(this, "Each availability slot must be exactly 30 minutes.", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            // double check
                                            List<TutorAvailabilityEntity> overlaps =
                                                    dao.findOverlapping(tutorEmail, dateStr, startStr, endStr);
                                            if (overlaps != null && !overlaps.isEmpty()) {
                                                Toast.makeText(this, "Overlapping slot", Toast.LENGTH_SHORT).show();
                                                return;
                                            }

                                            // save
                                            TutorAvailabilityEntity e = new TutorAvailabilityEntity();
                                            e.tutorEmail = tutorEmail;
                                            e.date = dateStr;
                                            e.startTime = startStr;
                                            e.endTime = endStr;
                                            e.autoApprove = false;

                                            if (TutorAvailabilityEntity.autoApproval == true) {
                                                e.requestStatus = "ACCEPTED";
                                            }

                                            dao.insert(e);
                                            layout.removeAllViews();
                                            loadCurrentAvailabilities();
                                        },
                                        now.get(Calendar.HOUR_OF_DAY),
                                        now.get(Calendar.MINUTE),
                                        true
                                );
                                endTimeDialog.show();

                            },
                            now.get(Calendar.HOUR_OF_DAY),
                            now.get(Calendar.MINUTE),
                            true
                    );
                    startTimeDialog.show();

                },
                now.get(Calendar.YEAR),
                now.get(Calendar.MONTH),
                now.get(Calendar.DAY_OF_MONTH)
        );

        datePickerDialog.show();
    }

    private boolean isThirtyMinutes(int startHour, int endHour, int startMinute, int endMinute) {
        if ((endHour * 60 + endMinute) - (startHour * 60 + startMinute) == 30) {
            return true;
        }
        else {
            return false;
        }
    }

    private int to30(int minute) {
        // 0~29 -> 0, 30~59 -> 30
        if (minute < 30) return 0;
        return 30;
    }

}

