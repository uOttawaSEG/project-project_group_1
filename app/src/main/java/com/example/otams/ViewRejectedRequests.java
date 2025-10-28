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

public class ViewRejectedRequests extends AppCompatActivity {
    private AppDatabase db;
    LinearLayout layout;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        EdgeToEdge.enable(this);
        setContentView(R.layout.activity_view_rejected_requests);

        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main), (v, insets) -> {
            Insets systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars());
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom);
            return insets;
        });

        db = ((App) getApplication()).getDb();
        layout = findViewById(R.id.layout);

        loadRejectedRequests();

        Button btnLogout = findViewById(R.id.btnLogout);
        btnLogout.setOnClickListener(v -> {
            Intent intent = new Intent(ViewRejectedRequests.this, MainActivity2.class);
            startActivity(intent);
        });
    }
    private void loadRejectedRequests() {
        List<RegistrationRequestEntity> rejectedRequests =
                db.registrationRequestDao().getRejectedRequests();

        for (int i = 0; i < rejectedRequests.size(); i++) {
            showRequest(rejectedRequests.get(i));
        }

    }

    private void showRequest(RegistrationRequestEntity request) {

        View itemView = LayoutInflater.from(this)
                .inflate(R.layout.activity_rejected_request_item, layout, false);
        layout.addView(itemView);


        TextView name = itemView.findViewById(R.id.textName);
        TextView email = itemView.findViewById(R.id.textEmail);
        TextView role = itemView.findViewById(R.id.textRole);

        name.setText(request.firstName + " " + request.lastName);
        email.setText(request.email);
        role.setText(request.role == UserRole.STUDENT ? "Student" : "Tutor");

        Button btnReapprove = itemView.findViewById(R.id.btnReapprove);
        btnReapprove.setOnClickListener(v -> reacceptRequest(request, itemView));

    }

    public void reacceptRequest(RegistrationRequestEntity request, View itemView) {

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
//    private void showRejectedRequest(RegistrationRequestEntity request) {
//        View itemView = LayoutInflater.from(this)
//                .inflate(R.layout.activity_rejected_request_item, layout, false);
//        layout.addView(itemView);
//
//        TextView name = itemView.findViewById(R.id.textName);
//        TextView email = itemView.findViewById(R.id.textEmail);
//        TextView role = itemView.findViewById(R.id.textRole);
//        TextView reason = itemView.findViewById(R.id.textReason);
//        Button details = itemView.findViewById(R.id.buttonDetails);
//
//        // Display info
//        name.setText(request.firstName + " " + request.lastName);
//        email.setText(request.email);
//        role.setText(request.role == UserRole.STUDENT ? "Student" : "Tutor");
//
//
////        details.setOnClickListener(v -> {
////            Intent intent = new Intent(ViewRejectedRequests.this, RejectedRequestDetails.class);
////            intent.putExtra("requestId", request.id);
////            startActivity(intent);
////        });
//    }








}
