/**
 * UserRepository coordinates user-related data operations across multiple DAOs.
 *
 * ────────────  SUMMARY OF CHANGES FOR DELIVERABLE 2  ────────────
 *  • Added RegistrationRequestDao & model imports
 *  • Registration now saves new Students/Tutors as "pending" requests
 *    instead of immediately creating real UserEntity entries.
 *  • Added helper methods to query/update registration request status.
 *  • Existing registerStudent()/registerTutor() kept for later (when admin approves).
 * ─────────────────────────────────────────────────────────────────
 */
package com.example.otams.repository;

import androidx.annotation.Nullable;

import com.example.otams.data.AppDatabase;
import com.example.otams.data.StudentDao;
import com.example.otams.data.TutorDao;
import com.example.otams.data.UserDao;
import com.example.otams.model.StudentEntity;
import com.example.otams.model.TutorEntity;
import com.example.otams.model.UserEntity;
import com.example.otams.util.PasswordHasher;

//  NEW ADDITIONS FOR DELIVERABLE 2
import com.example.otams.data.RegistrationRequestDao;
import com.example.otams.model.RegistrationRequestEntity;
import com.example.otams.model.RequestStatus;
import com.example.otams.model.UserRole;

import java.util.List;
import java.util.UUID;

public class UserRepository {

    private final AppDatabase db;
    private final UserDao userDao;
    private final StudentDao studentDao;
    private final TutorDao tutorDao;

    //  NEW DAO for registration requests
    private final RegistrationRequestDao requestDao;

    public UserRepository(AppDatabase db) {
        this.db = db;
        this.userDao = db.userDao();
        this.studentDao = db.studentDao();
        this.tutorDao = db.tutorDao();

        //  NEW INITIALIZATION
        this.requestDao = db.registrationRequestDao();
    }

    /** Checks whether a user email already exists in main users table. */
    public boolean emailExists(String email) {
        return userDao.emailExists(email);
    }

    
    //  STUDENT REGISTRATION REQUEST  
    
    public Result<Long> createStudentRegistrationRequest(
            String first, String last, String email,
            String rawPassword, String phone, String programOfStudy) {

        if (emailExists(email)) {
            return Result.error("EMAIL_EXISTS", "This email is already registered");
        }

        try {
            RegistrationRequestEntity r = new RegistrationRequestEntity();
            r.firstName = first;
            r.lastName = last;
            r.email = email;
            r.phone = phone;
            r.role = UserRole.STUDENT;
            r.programOfStudy = programOfStudy;
            r.passwordHash = PasswordHasher.hash(rawPassword);
            r.status = RequestStatus.PENDING; //  new field
            r.createdAtEpochMs = System.currentTimeMillis();

            long reqId = requestDao.insert(r);
            return Result.ok(reqId);
        } catch (Exception e) {
            return Result.error("DB_ERROR", e.getMessage());
        }
    }

    
    //  TUTOR REGISTRATION REQUEST 
    
    public Result<Long> createTutorRegistrationRequest(
            String first, String last, String email,
            String rawPassword, String phone,
            String highestDegree, List<String> courses) {

        if (emailExists(email)) {
            return Result.error("EMAIL_EXISTS", "This email is already registered");
        }
        if (courses == null || courses.isEmpty()) {
            return Result.error("VALIDATION", "At least one course is required");
        }

        try {
            RegistrationRequestEntity r = new RegistrationRequestEntity();
            r.firstName = first;
            r.lastName = last;
            r.email = email;
            r.phone = phone;
            r.role = UserRole.TUTOR;
            r.highestDegree = highestDegree;
            r.coursesOffered = courses;
            r.passwordHash = PasswordHasher.hash(rawPassword);
            r.status = RequestStatus.PENDING; //  new field
            r.createdAtEpochMs = System.currentTimeMillis();

            long reqId = requestDao.insert(r);
            return Result.ok(reqId);
        } catch (Exception e) {
            return Result.error("DB_ERROR", e.getMessage());
        }
    }

    
    //  ORIGINAL FULL REGISTRATION FOR ADMIN APPROVAL phase
    
    public Result<String> registerStudent(String first, String last, String email,
                                          String rawPassword, String phone,
                                          String programOfStudy) {
        if (emailExists(email)) {
            return Result.error("EMAIL_EXISTS", "This email is already registered");
        }
        try {
            String uid = UUID.randomUUID().toString();

            UserEntity u = new UserEntity();
            u.id = uid;
            u.firstName = first;
            u.lastName = last;
            u.email = email;
            u.passwordHash = PasswordHasher.hash(rawPassword);
            u.phone = phone;
            u.role = "STUDENT";

            StudentEntity s = new StudentEntity();
            s.userId = uid;
            s.programOfStudy = programOfStudy;

            db.runInTransaction(() -> {
                userDao.insert(u);
                studentDao.insert(s);
            });

            return Result.ok(uid);
        } catch (Exception e) {
            return Result.error("DB_ERROR", e.getMessage());
        }
    }

    public Result<String> registerTutor(String first, String last, String email,
                                        String rawPassword, String phone,
                                        String highestDegree, List<String> courses) {
        if (emailExists(email)) {
            return Result.error("EMAIL_EXISTS", "This email is already registered");
        }
        if (courses == null || courses.isEmpty()) {
            return Result.error("VALIDATION", "At least one course is required");
        }
        try {
            String uid = UUID.randomUUID().toString();

            UserEntity u = new UserEntity();
            u.id = uid;
            u.firstName = first;
            u.lastName = last;
            u.email = email;
            u.passwordHash = PasswordHasher.hash(rawPassword);
            u.phone = phone;
            u.role = "TUTOR";

            TutorEntity t = new TutorEntity();
            t.userId = uid;
            t.highestDegree = highestDegree;
            t.coursesOffered = courses;

            db.runInTransaction(() -> {
                userDao.insert(u);
                tutorDao.insert(t);
            });

            return Result.ok(uid);
        } catch (Exception e) {
            return Result.error("DB_ERROR", e.getMessage());
        }
    }

    
    //  LOGIN & PASSWORD VERIFICATION
    
    @Nullable
    public UserEntity findByEmail(String email) {
        return userDao.findByEmail(email);
    }

    public boolean verifyPassword(String email, String rawPassword) {
        UserEntity u = userDao.findByEmail(email);
        return (u != null && PasswordHasher.matches(rawPassword, u.passwordHash));
    }

    
    //  REQUEST STATUS QUERIES / ADMIN UPDATES (Deliverable 2)
    
    @Nullable
    public RequestStatus getRequestStatusByEmail(String email) {
        return requestDao.getStatusByEmail(email);
    }

    public void markRequestApproved(long requestId, String adminUserId) {
        requestDao.updateStatus(
                requestId,
                RequestStatus.APPROVED,
                adminUserId,
                System.currentTimeMillis());
    }

    public void markRequestRejected(long requestId, String adminUserId) {
        requestDao.updateStatus(
                requestId,
                RequestStatus.REJECTED,
                adminUserId,
                System.currentTimeMillis());
    }

    
    //  GENERIC RESULT WRAPPER
    
    public static class Result<T> {
        public final boolean success;
        public final T data;
        public final String code;
        public final String message;

        private Result(boolean s, T d, String c, String m) {
            success = s; data = d; code = c; message = m;
        }
        public static <T> Result<T> ok(T data) {
            return new Result<>(true, data, null, null);
        }
        public static <T> Result<T> error(String code, String msg) {
            return new Result<>(false, null, code, msg);
        }
    }
}
