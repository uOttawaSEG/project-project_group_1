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
import com.example.otams.model.RegistrationRequestEntity;
import com.example.otams.model.RequestStatus;
import com.example.otams.model.UserRole;
import com.example.otams.repository.UserRepository;

import java.util.List;

public class ViewPendingRequests extends AppCompatActivity {

    private AppDatabase db;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_pending_requests);
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = ((App) getApplication()).getDb();
        layout = findViewById(R.id.layout);
        loadPendingRequests();

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ViewPendingRequests.this, MainActivity2.class);
            startActivity(intent);

        });


    }

    private void loadPendingRequests() {
        List<RegistrationRequestEntity> pendingRequests = db.registrationRequestDao().getPendingRequests();
        for (int i = 0; i < pendingRequests.size(); i++) {
            showRequest(pendingRequests.get(i));
        }
    }

    public void approveRequest(RegistrationRequestEntity request, View itemView) {

        UserRepository userRepo = new UserRepository(db);

        request.status = RequestStatus.APPROVED;
        request.reviewedByAdminUserId = "adminId";
        request.reviewedAtEpochMs = System.currentTimeMillis();

        db.registrationRequestDao().updateStatus(
                request.id,
                RequestStatus.APPROVED,
                "adminId",
                System.currentTimeMillis()
        );

        if (request.role == UserRole.STUDENT) {
            UserRepository.Result<String> result = userRepo.registerStudent(
                    request.firstName,
                    request.lastName,
                    request.email,
                    request.rawPassword,
                    request.phone,
                    request.programOfStudy
            );
        }

        if (request.role == UserRole.TUTOR) {
            UserRepository.Result<String> result = userRepo.registerTutor(
                    request.firstName,
                    request.lastName,
                    request.email,
                    request.rawPassword,
                    request.phone,
                    request.highestDegree,
                    request.coursesOffered
            );
        }
        layout.removeView(itemView);
    }


    public void rejectRequest(RegistrationRequestEntity request, View itemView) {
        UserRepository userRepository = new UserRepository(db);
        request.status = RequestStatus.REJECTED;
        request.reviewedAtEpochMs = System.currentTimeMillis();

        db.registrationRequestDao().updateStatus(
                request.id,
                RequestStatus.APPROVED,
                "adminId",
                System.currentTimeMillis()
        );

        layout.removeView(itemView);
    }
    private void showRequest(RegistrationRequestEntity request) {

        View itemView = LayoutInflater.from(this).inflate(R.layout.activity_request_item, layout, false);
        layout.addView(itemView);

        Button buttonAccept = itemView.findViewById(R.id.buttonAccept);
        Button buttonReject = itemView.findViewById(R.id.buttonReject);

        buttonAccept.setOnClickListener(v -> approveRequest(request, itemView));
        buttonReject.setOnClickListener(v -> rejectRequest(request, itemView));

        TextView viewFirstName = itemView.findViewById(R.id.viewFirstName);
        TextView viewLastName = itemView.findViewById(R.id.viewLastName);
        TextView viewEmail = itemView.findViewById(R.id.viewEmail);
        TextView viewRole = itemView.findViewById(R.id.viewRole);
        TextView viewPhone = itemView.findViewById(R.id.viewPhone);
        TextView viewProgram = itemView.findViewById(R.id.viewProgram);
        TextView viewDegree = itemView.findViewById(R.id.viewDegree);
        TextView viewCourses = itemView.findViewById(R.id.viewCourses);

        if (request.role == UserRole.STUDENT) {
            viewDegree.setText("N/A");
            viewCourses.setText("N/A");
            viewFirstName.setText(request.firstName);
            viewLastName.setText(request.lastName);
            viewEmail.setText(request.email);
            viewRole.setText("Student");
            viewPhone.setText(request.phone);
            viewProgram.setText(request.programOfStudy);
        }

        if (request.role == UserRole.TUTOR) {
            String courses = "";
            for (int n = 0; n < request.coursesOffered.size(); n++) {
                courses = courses + request.coursesOffered.get(n);
                courses = courses + " ";
            }
            viewProgram.setText("N/A");
            viewFirstName.setText(request.firstName);
            viewLastName.setText(request.lastName);
            viewEmail.setText(request.email);
            viewRole.setText("Tutor");
            viewPhone.setText(request.phone);
            viewCourses.setText(courses);
            viewDegree.setText(request.highestDegree);
        }



    }


}