package com.example.otams;

import static org.junit.Assert.*;

import android.content.Context;
import android.util.Log;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.test.core.app.ApplicationProvider;
import androidx.test.ext.junit.runners.AndroidJUnit4;

import com.example.otams.data.AppDatabase;
import com.example.otams.data.RegistrationRequestDao;
import com.example.otams.model.RegistrationRequestEntity;
import com.example.otams.model.RequestStatus;
import com.example.otams.model.UserRole;

import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;

import java.io.IOException;
import java.util.Arrays;

/**
 * DbPart1EndToEndTest
 *
 * Purpose: Validate OTAMS Deliverable 2 - Part 1 (Database)
 * Verifies:
 *   - RegistrationRequest insertion
 *   - Status updates (PENDING -> APPROVED / REJECTED)
 *   - Data integrity using in-memory Room DB
 *
 * Note: Runs only in androidTest environment; not part of production app.
 */

import org.junit.Ignore;

@Ignore("Temporarily disabled for Deliverable 3")
@RunWith(AndroidJUnit4.class)
public class DbPart1EndToEndTest {

    private static final String TAG = "OTAMS-DBTEST";
    private AppDatabase db;
    private RegistrationRequestDao dao;

    @Before
    public void createDb() {
        Context ctx = ApplicationProvider.getApplicationContext();

        db = Room.inMemoryDatabaseBuilder(ctx, AppDatabase.class)
                .allowMainThreadQueries()
                .build();
        dao = db.registrationRequestDao();
    }

    @After
    public void closeDb() throws IOException {
        db.close();
    }

    @Test
    public void endToEnd_registration_request_flow() {
        logHeader("OTAMS Deliverable 2 - Part 1 (DB) – End-to-End Test");


        long studentReqId = dao.insert(newStudentReq(
                "stu@uottawa.ca", "Stu", "Dent", "6130000000", "CS"
        ));
        assertTrue(studentReqId > 0);


        long tutorReqId = dao.insert(newTutorReq(
                "tut@uottawa.ca", "Tu", "Tor", "6131111111", "MSc",
                new String[]{"SEG2105", "CSI2372"}
        ));
        assertTrue(tutorReqId > 0);


        assertEquals(RequestStatus.PENDING, dao.getStatusByEmail("stu@uottawa.ca"));
        assertEquals(RequestStatus.PENDING, dao.getStatusByEmail("tut@uottawa.ca"));


        dao.updateStatus(studentReqId, RequestStatus.APPROVED, "admin-uuid", System.currentTimeMillis());
        dao.updateStatus(tutorReqId, RequestStatus.REJECTED, "admin-uuid", System.currentTimeMillis());


        assertEquals(RequestStatus.APPROVED, dao.getStatusByEmail("stu@uottawa.ca"));
        assertEquals(RequestStatus.REJECTED, dao.getStatusByEmail("tut@uottawa.ca"));


        printResultRow("stu@uottawa.ca",
                dao.findByEmail("stu@uottawa.ca").status,
                dao.findByEmail("stu@uottawa.ca").role.name());

        printResultRow("tut@uottawa.ca",
                dao.findByEmail("tut@uottawa.ca").status,
                dao.findByEmail("tut@uottawa.ca").role.name());

        logLine("All assertions passed ✓");
    }

    // ---------- helpers ----------

    private RegistrationRequestEntity newStudentReq(String email, String first, String last,
                                                    String phone, String programOfStudy) {
        RegistrationRequestEntity r = new RegistrationRequestEntity();
        r.email = email;
        r.firstName = first;
        r.lastName = last;
        r.role = UserRole.STUDENT;
        r.phone = phone;
        r.programOfStudy = programOfStudy;
        r.passwordHash = "hash-student";
        r.status = RequestStatus.PENDING;
        r.createdAtEpochMs = System.currentTimeMillis();
        return r;
    }

    private RegistrationRequestEntity newTutorReq(String email, String first, String last,
                                                  String phone, String highestDegree,
                                                  String[] courses) {
        RegistrationRequestEntity r = new RegistrationRequestEntity();
        r.email = email;
        r.firstName = first;
        r.lastName = last;
        r.role = UserRole.TUTOR;
        r.phone = phone;
        r.highestDegree = highestDegree;
        r.coursesOffered = Arrays.asList(courses);
        r.passwordHash = "hash-tutor";
        r.status = RequestStatus.PENDING;
        r.createdAtEpochMs = System.currentTimeMillis();
        return r;
    }

    private void logHeader(String title) {
        Log.i(TAG, "============================================");
        Log.i(TAG, title);
        Log.i(TAG, "============================================");
        Log.i(TAG, String.format("%-28s | %-10s | %-8s", "email", "status", "role"));
        Log.i(TAG, "--------------------------------------------");
    }

    private void printResultRow(String email, RequestStatus status, String role) {
        Log.i(TAG, String.format("%-28s | %-10s | %-8s", email, status.name(), role));
    }

    private void logLine(@NonNull String msg) {
        Log.i(TAG, msg);
    }
}
