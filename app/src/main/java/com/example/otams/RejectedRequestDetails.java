package com.example.otams;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.otams.data.AppDatabase;
import com.example.otams.model.RegistrationRequestEntity;
import com.example.otams.model.RequestStatus;

import java.util.List;

public class RejectedRequestDetails extends AppCompatActivity {

    private AppDatabase db;
    private RegistrationRequestEntity currentRequest;

    @Override
    protected void onCreate(Bundle savedInstanceState){
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rejected_request_details);

        db = ((App) getApplication()).getDb();

        int reqId = getIntent().getIntExtra("requestId", -1);

        if(reqId == -1){
            Toast.makeText(this, "Error: no request found.", Toast.LENGTH_SHORT).show();
            finish();
            return;

        }

        currentRequest = null;
        List<RegistrationRequestEntity> rejectedRequests = db.registrationRequestDao().getRejectedRequests();
        for(RegistrationRequestEntity r : rejectedRequests){
            if(r.id == reqId){
                currentRequest = r;
                break;
            }
        }

        if(currentRequest == null){
            Toast.makeText(this, "Request missing from database.", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        TextView txtName = findViewById(R.id.textName);
        TextView txtEmail = findViewById(R.id.textEmail);
        TextView txtRole = findViewById(R.id.textRole);
        Button btnApprove = findViewById(R.id.btnApprove);
        Button btnBack = findViewById(R.id.btnBack);

        txtName.setText(currentRequest.firstName + " " + currentRequest.lastName);
        txtEmail.setText(currentRequest.email);
        txtRole.setText(String.valueOf(currentRequest.role));

        btnApprove.setOnClickListener(v -> {
            if (currentRequest.status == RequestStatus.APPROVED){
                Toast.makeText(this, "Already approved earlier.", Toast.LENGTH_SHORT).show();
                return;
            }

            long now = System.currentTimeMillis();
            String adminId = "admin_user_1";
            db.registrationRequestDao().updateStatus(currentRequest.id, RequestStatus.APPROVED, adminId, now);

            Toast.makeText(this, "User moved to approved list.", Toast.LENGTH_SHORT).show();
            finish();
        });

        btnBack.setOnClickListener(v-> finish());
    }

}
