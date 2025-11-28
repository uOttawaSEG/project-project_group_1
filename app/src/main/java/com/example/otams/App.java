/**
 * App is the Application entry point for the OTAMS project.
 * It initializes a single global Room database instance when the app starts
 * and seeds a default administrator account if none exists.
 *
 * The database is built using Room.databaseBuilder() with main-thread access
 * temporarily enabled for demo purposes. The admin account ("admin@otams.ca")
 * is created with a salted, hashed password (Admin#123)using PasswordHasher.
 *
 * Typical usage:
 *   AppDatabase db = App.getDb();
 *   UserDao dao = db.userDao();
 *   // access or query data globally
 */

package com.example.otams;

import android.app.Application;

import androidx.room.Room;

import com.example.otams.data.AppDatabase;
import com.example.otams.data.UserDao;
import com.example.otams.model.UserEntity;
import com.example.otams.util.PasswordHasher;

import java.util.UUID;

public class App extends Application {
    private static AppDatabase DB;

    //Initialize the database
    @Override
    public void onCreate() {
        super.onCreate();
        DB = Room.databaseBuilder(
                            getApplicationContext(),
                            AppDatabase.class,
                            "otams_db"
                )

                // deliverable 3
                .allowMainThreadQueries()
                .addMigrations(
                        AppDatabase.MIGRATION_1_2,
                        AppDatabase.MIGRATION_2_3,
                        AppDatabase.MIGRATION_3_4,
                        AppDatabase.MIGRATION_4_5,
                        AppDatabase.MIGRATION_5_6,
                        AppDatabase.MIGRATION_6_7
                )

                .build();
        seedAdminIfNeeded();
    }

    public static AppDatabase getDb() { return DB; }

    //Pre-set administrator account
    private void seedAdminIfNeeded() {
        UserDao userDao = DB.userDao();
        final String adminEmail = "admin@otams.ca";
        if (!userDao.emailExists(adminEmail)) {
            UserEntity admin = new UserEntity();
            admin.id = UUID.randomUUID().toString();
            admin.firstName = "System";
            admin.lastName = "Admin";
            admin.email = adminEmail;
            admin.passwordHash = PasswordHasher.hash("Admin#123");
            admin.phone = "0000000000";
            admin.role = "ADMIN";
            userDao.insert(admin);
        }
    }
}