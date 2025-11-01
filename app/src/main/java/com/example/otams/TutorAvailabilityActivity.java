package com.example.otams;

import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.os.Bundle;
import android.widget.Button;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.example.otams.data.AppDatabase;
import com.example.otams.data.TutorAvailabilityDao;
import com.example.otams.model.TutorAvailabilityEntity;
import com.example.otams.App;
import com.google.android.material.floatingactionbutton.FloatingActionButton;

import java.util.Calendar;
import java.util.List;

public class TutorAvailabilityActivity extends AppCompatActivity {

    private TutorAvailabilityDao dao;
    private TutorAvailabilityAdapter adapter;
    private String tutorEmail;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tutor_availability);

        AppDatabase db = ((App) getApplication()).getDb();
        dao = db.tutorAvailabilityDao();


        tutorEmail = getIntent().getStringExtra("email");
        if (tutorEmail == null) {

            tutorEmail = "tutor@otams.ca";
        }

        RecyclerView rv = findViewById(R.id.rvAvailability);
        rv.setLayoutManager(new LinearLayoutManager(this));
        adapter = new TutorAvailabilityAdapter(new TutorAvailabilityAdapter.OnDeleteClickListener() {
            @Override
            public void onDelete(TutorAvailabilityEntity e) {
                dao.delete(e);
                loadData();
            }
        });
        rv.setAdapter(adapter);

        Button btnAdd = findViewById(R.id.btnAddSlot);
        btnAdd.setOnClickListener(v -> showAddDialog());

        Button btnBack = findViewById(R.id.btnBack);
        btnBack.setOnClickListener(v -> finish());

        loadData();
    }

    private void loadData() {
        List<TutorAvailabilityEntity> list = dao.getSlotsForTutor(tutorEmail);
        adapter.submitList(list);
    }

    private void showAddDialog() {
        final Calendar now = Calendar.getInstance();

        DatePickerDialog datePickerDialog = new DatePickerDialog(
                this,
                (view, year, month, dayOfMonth) -> {
                    String dateStr = String.format("%04d-%02d-%02d", year, month + 1, dayOfMonth);


                    Calendar picked = Calendar.getInstance();
                    picked.set(year, month, dayOfMonth, 0, 0, 0);
                    if (picked.before(Calendar.getInstance())) {
                        Toast.makeText(this, "Date already passed", Toast.LENGTH_SHORT).show();
                        return;
                    }

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

                                            if (endStr.compareTo(startStr) <= 0) {
                                                Toast.makeText(this, "End must be after start", Toast.LENGTH_SHORT).show();
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

                                            dao.insert(e);
                                            loadData();

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

    private int to30(int minute) {
        // 0~29 -> 0, 30~59 -> 30
        if (minute < 30) return 0;
        return 30;
    }
}

