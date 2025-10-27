/**
 * AppDatabase is the Room database entry point for the app.
 * It declares the schema (UserEntity, StudentEntity, TutorEntity) and the
 * database version, registers custom type converters, and exposes DAO
 * accessors (userDao, studentDao, tutorDao) for data operations.
 *
 * Room generates the concrete implementation at compile time (e.g., AppDatabase_Impl).
 * Obtain a single instance via Room.databaseBuilder(...) and use the DAOs to
 * perform queries, inserts, updates, and deletes.
 */

package com.example.otams.data;

import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.TypeConverters;

import com.example.otams.model.StudentEntity;
import com.example.otams.model.TutorEntity;
import com.example.otams.model.UserEntity;
import com.example.otams.util.Converters;

@Database(
        entities = {
                UserEntity.class, StudentEntity.class, TutorEntity.class,
                com.example.otams.model.RegistrationRequestEntity.class
        },
        version = 3, // Changed from 2 to 3
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract StudentDao studentDao();
    public abstract TutorDao tutorDao();

    //new addition:
    public abstract com.example.otams.data.RegistrationRequestDao registrationRequestDao();

    //new addition:
    public static final androidx.room.migration.Migration MIGRATION_1_2 =
            new androidx.room.migration.Migration(1, 2) {
                @Override
                public void migrate(@androidx.annotation.NonNull androidx.sqlite.db.SupportSQLiteDatabase db) {
                    db.execSQL("CREATE TABLE IF NOT EXISTS `registration_requests` (" +
                            "`id` INTEGER PRIMARY KEY AUTOINCREMENT NOT NULL, " +
                            "`email` TEXT NOT NULL, `firstName` TEXT NOT NULL, `lastName` TEXT NOT NULL, " +
                            "`role` TEXT NOT NULL, `phone` TEXT, `programOfStudy` TEXT, `highestDegree` TEXT, " +
                            "`coursesOffered` TEXT, " +
                            "`passwordHash` TEXT NOT NULL, " +
                            "`status` TEXT NOT NULL, `reviewedByAdminUserId` TEXT, `reviewedAtEpochMs` INTEGER, " +
                            "`createdAtEpochMs` INTEGER NOT NULL)");
                    db.execSQL("CREATE INDEX IF NOT EXISTS `index_registration_requests_email` ON `registration_requests` (`email`)");
                    db.execSQL("CREATE INDEX IF NOT EXISTS `index_registration_requests_status` ON `registration_requests` (`status`)");
                }
            };

    public static final androidx.room.migration.Migration MIGRATION_2_3 =
            new androidx.room.migration.Migration(2, 3) {
                @Override
                public void migrate(@androidx.annotation.NonNull androidx.sqlite.db.SupportSQLiteDatabase db) {
                    // Add the new rawPassword column
                    db.execSQL("ALTER TABLE registration_requests ADD COLUMN rawPassword TEXT");
                }
            };
}
