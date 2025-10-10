package com.example.otams;

import android.os.Bundle;
import android.widget.Button;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.otams.data.AppDatabase;
import com.example.otams.model.StudentEntity;
import com.example.otams.model.TutorEntity;
import com.example.otams.model.UserEntity;
import com.example.otams.repository.UserRepository;
import com.example.otams.repository.UserRepository.Result;

import java.text.SimpleDateFormat;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * Minimal Dev Console for Part A verification
 */
public class DevConsoleActivity extends AppCompatActivity {

    private TextView tvLog;
    private AppDatabase db;
    private UserRepository repo;
    private final ExecutorService io = Executors.newSingleThreadExecutor();

    private String lastStudentEmail;
    private String lastTutorEmail;
    private final String studentPwd = "Student#123";
    private final String tutorPwd   = "Tutor#123";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dev_console);

        tvLog = findViewById(R.id.tvLog);

        // Your project uses Application singleton (App.java builds Room)
        db = ((App) getApplication()).getDb();
        repo = new UserRepository(db);

        Button btnCheckAdmin      = findViewById(R.id.btnCheckAdmin);
        Button btnRegisterStudent = findViewById(R.id.btnRegisterStudent);
        Button btnRegisterTutor   = findViewById(R.id.btnRegisterTutor);
        Button btnCheckDup        = findViewById(R.id.btnCheckDup);
        Button btnLoginOk         = findViewById(R.id.btnLoginOk);
        Button btnLoginFail       = findViewById(R.id.btnLoginFail);
        Button btnReadRoles       = findViewById(R.id.btnReadRoles);
        Button btnDumpAll         = findViewById(R.id.btnDumpAll);
        Button btnClearDb         = findViewById(R.id.btnClearDb);

        btnCheckAdmin.setOnClickListener(v -> runDbTask(this::checkAdmin));
        btnRegisterStudent.setOnClickListener(v -> runDbTask(this::registerStudent));
        btnRegisterTutor.setOnClickListener(v -> runDbTask(this::registerTutor));
        btnCheckDup.setOnClickListener(v -> runDbTask(this::checkDuplicate));
        btnLoginOk.setOnClickListener(v -> runDbTask(this::verifyPasswordOk));
        btnLoginFail.setOnClickListener(v -> runDbTask(this::verifyPasswordFail));
        btnReadRoles.setOnClickListener(v -> runDbTask(this::readRoles));
        btnDumpAll.setOnClickListener(v -> runDbTask(this::dumpAll));
        btnClearDb.setOnClickListener(v -> runDbTask(this::clearDb));
    }

    private void runDbTask(Runnable r) {
        io.execute(() -> {
            try {
                r.run();
            } catch (Throwable t) {
                log("ERROR: " + t.getMessage());
            }
        });
    }

    // 1) Verify admin seeded
    private void checkAdmin() {
        boolean exists = repo.emailExists("admin@otams.ca");
        log("Admin exists? " + exists + "  (expected: true)");
        UserEntity admin = repo.findByEmail("admin@otams.ca");
        if (admin != null) {
            log("Admin => " + safeName(admin) + " | email=" + admin.email + " | role=" + admin.role);
        }
    }

    // 2) Register STUDENT  (first,last,email,rawPwd,phone,program)
    private void registerStudent() {
        String ts = new SimpleDateFormat("HHmmss", Locale.US).format(new Date());
        lastStudentEmail = "student_" + ts + "@otams.ca";

        Result<String> r = repo.registerStudent(
                "Tom",
                "Lee",
                lastStudentEmail,
                studentPwd,
                "+1-555-0101",
                "Computer Science"
        );

        if (r.success) {
            log("Register Student: SUCCESS, userId=" + r.data + ", email=" + lastStudentEmail);
        } else {
            log("Register Student: FAIL, code=" + r.code + ", msg=" + r.message);
        }
    }

    // 3) Register TUTOR (first,last,email,rawPwd,phone,degree,courses)
    private void registerTutor() {
        String ts = new SimpleDateFormat("HHmmss", Locale.US).format(new Date());
        lastTutorEmail = "tutor_" + ts + "@otams.ca";

        Result<String> r = repo.registerTutor(
                "Ann",
                "Wang",
                lastTutorEmail,
                tutorPwd,
                "+1-555-0102",
                "MSc",
                Arrays.asList("CSI2132", "MAT1322")
        );

        if (r.success) {
            log("Register Tutor: SUCCESS, userId=" + r.data + ", email=" + lastTutorEmail);
        } else {
            log("Register Tutor: FAIL, code=" + r.code + ", msg=" + r.message);
        }
    }

    // 4) Duplicate email check (use last tutor)
    private void checkDuplicate() {
        if (lastTutorEmail == null) {
            log("Duplicate check: please press 'Register TUTOR' first.");
            return;
        }
        boolean exists = repo.emailExists(lastTutorEmail);
        log("emailExists(" + lastTutorEmail + "): " + exists + "  (expected: true)");
    }

    // 5) Verify password OK
    private void verifyPasswordOk() {
        if (lastStudentEmail == null) {
            log("Verify Password (OK): please press 'Register STUDENT' first.");
            return;
        }
        boolean ok = repo.verifyPassword(lastStudentEmail, studentPwd);
        log("verifyPassword(student OK): " + ok + "  (expected: true)");
    }

    // 6) Verify password FAIL
    private void verifyPasswordFail() {
        if (lastStudentEmail == null) {
            log("Verify Password (FAIL): please press 'Register STUDENT' first.");
            return;
        }
        boolean ok = repo.verifyPassword(lastStudentEmail, "Wrong#999");
        log("verifyPassword(student FAIL): " + ok + "  (expected: false)");
    }

    // 7) Read roles (admin + last created student/tutor)
    private void readRoles() {
        StringBuilder sb = new StringBuilder("Roles:\n");

        UserEntity admin = repo.findByEmail("admin@otams.ca");
        if (admin != null) sb.append("ADMIN  : ").append(admin.email).append(" | ").append(admin.role).append("\n");

        if (lastStudentEmail != null) {
            UserEntity st = repo.findByEmail(lastStudentEmail);
            if (st != null) sb.append("STUDENT: ").append(st.email).append(" | ").append(st.role).append("\n");
        }
        if (lastTutorEmail != null) {
            UserEntity tt = repo.findByEmail(lastTutorEmail);
            if (tt != null) sb.append("TUTOR  : ").append(tt.email).append(" | ").append(tt.role).append("\n");
        }
        log(sb.toString().trim());
    }

    // 8) Dump ALL rows from three tables (uses your getAll*() DAOs)
    private void dumpAll() {
        List<UserEntity> users = db.userDao().getAllUsers();
        List<StudentEntity> students = db.studentDao().getAllStudents();
        List<TutorEntity> tutors = db.tutorDao().getAllTutors();

        StringBuilder sb = new StringBuilder();
        sb.append("--- USERS (").append(users.size()).append(") ---\n");
        for (UserEntity u : users) {
            sb.append(u.id).append(" | ")
                    .append(safeName(u)).append(" | ")
                    .append(u.email).append(" | ")
                    .append(u.role).append("\n");
        }

        sb.append("\n--- STUDENTS (").append(students.size()).append(") ---\n");
        for (StudentEntity s : students) {
            sb.append("userId=").append(s.userId)
                    .append(" | program=").append(s.programOfStudy).append("\n");
        }

        sb.append("\n--- TUTORS (").append(tutors.size()).append(") ---\n");
        for (TutorEntity t : tutors) {
            sb.append("userId=").append(t.userId)
                    .append(" | degree=").append(t.highestDegree)
                    .append(" | courses=").append(t.coursesOffered).append("\n");
        }

        log(sb.toString());
    }

    // 9) Clear DB (danger)
    private void clearDb() {
        db.clearAllTables();
        lastStudentEmail = null;
        lastTutorEmail = null;
        log("CLEAR Database: done. (All tables are empty now)");
    }

    private String safeName(UserEntity u) {
        String f = u.firstName == null ? "" : u.firstName;
        String l = u.lastName  == null ? "" : u.lastName;
        return (f + " " + l).trim();
    }

    private void log(String msg) {
        runOnUiThread(() -> {
            CharSequence old = tvLog.getText();
            tvLog.setText((old == null ? "" : old + "\n") + msg);
        });
    }
}
