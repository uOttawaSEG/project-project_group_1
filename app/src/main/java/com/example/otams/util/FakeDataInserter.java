package com.example.otams.util;

import com.example.otams.data.TutorAvailabilityDao;
import com.example.otams.data.AppDatabase;
import com.example.otams.model.TutorAvailabilityEntity;
import com.example.otams.data.StudentDao;
import com.example.otams.data.TutorDao;
import com.example.otams.data.UserDao;
import com.example.otams.model.StudentEntity;
import com.example.otams.model.TutorEntity;
import com.example.otams.model.UserEntity;
import com.example.otams.util.PasswordHasher;

import java.util.concurrent.Executors;
import java.util.Arrays;
import java.util.UUID;


//To be used for testing purposes only

public class FakeDataInserter {

    // Pass the already initialized database
    public static void insertFakeSessions(AppDatabase db) {
        TutorAvailabilityDao dao = db.tutorAvailabilityDao();
        UserDao userDao = db.userDao();
        TutorDao tutorDao = db.tutorDao();
        StudentDao studentDao = db.studentDao();

        Executors.newSingleThreadExecutor().execute(() -> {

            // --- Tutor User ---
            if (userDao.findByEmail("j@gmail.com") == null) {
                String tutorId = UUID.randomUUID().toString();
                UserEntity tutorUser = new UserEntity();
                tutorUser.id = tutorId;
                tutorUser.firstName = "John";
                tutorUser.lastName = "Doe";
                tutorUser.email = "j@gmail.com";
                tutorUser.passwordHash = PasswordHasher.hash("password123!");
                ;
                tutorUser.role = "TUTOR";

                // Insert user
                userDao.insert(tutorUser);

                // Insert tutor entity
                TutorEntity tutor = new TutorEntity();
                tutor.userId = tutorId;
                tutor.highestDegree = "Masters";
                tutor.coursesOffered = Arrays.asList("Math 101", "Physics 201");
                tutorDao.insert(tutor);
            }

            // --- Student User ---
            if (userDao.findByEmail("d@gmail.com") == null) {
                String studentId = UUID.randomUUID().toString();
                UserEntity studentUser = new UserEntity();
                studentUser.id = studentId;
                studentUser.firstName = "Diana";
                studentUser.lastName = "Smith";
                studentUser.email = "d@gmail.com";
                studentUser.passwordHash = PasswordHasher.hash("password123");
                studentUser.role = "STUDENT";

                // Insert user
                userDao.insert(studentUser);

                // Insert student entity
                StudentEntity student = new StudentEntity();
                student.userId = studentId;
                student.programOfStudy = "Computer Science";
                studentDao.insert(student);
            }

            // YO Watch out: this will just delete all the tutor session with this speicifc email ive been using
            // for testing purposes
            dao.getSlotsForTutor("j@gmail.com").forEach(dao::delete);

            // Past sessions
            TutorAvailabilityEntity session1 = new TutorAvailabilityEntity();
            session1.tutorEmail = "j@gmail.com";
            session1.studentEmail = "d@gmail.com";
            session1.date = "2025-11-25";
            session1.startTime = "10:00";
            session1.endTime = "11:00";
            session1.autoApprove = false;
            session1.requestStatus = "ACCEPTED";
            dao.insert(session1);

            TutorAvailabilityEntity session2 = new TutorAvailabilityEntity();
            session2.tutorEmail = "j@gmail.com";
            session2.studentEmail = "d@gmail.com";
            session2.date = "2025-11-26";
            session2.startTime = "14:00";
            session2.endTime = "15:00";
            session2.autoApprove = false;
            session2.requestStatus = "ACCEPTED";
            dao.insert(session2);

            // Future sessions
            TutorAvailabilityEntity session3 = new TutorAvailabilityEntity();
            session3.tutorEmail = "j@gmail.com";
            session3.studentEmail = "d@gmail.com";
            session3.date = "2025-12-25";
            session3.startTime = "09:00";
            session3.endTime = "10:00";
            session3.autoApprove = false;
            session3.requestStatus = "ACCEPTED";
            dao.insert(session3);

            TutorAvailabilityEntity session4 = new TutorAvailabilityEntity();
            session4.tutorEmail = "j@gmail.com";
            session4.studentEmail = "d@gmail.com";
            session4.date = "2025-12-28";
            session4.startTime = "16:00";
            session4.endTime = "17:00";
            session4.autoApprove = false;
            session4.requestStatus = "ACCEPTED";
            dao.insert(session4);
        });
    }
}
