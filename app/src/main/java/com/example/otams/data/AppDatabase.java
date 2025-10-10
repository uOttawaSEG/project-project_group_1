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
        entities = {UserEntity.class, StudentEntity.class, TutorEntity.class},
        version = 1,
        exportSchema = false
)
@TypeConverters({Converters.class})
public abstract class AppDatabase extends RoomDatabase {
    public abstract UserDao userDao();
    public abstract StudentDao studentDao();
    public abstract TutorDao tutorDao();
}
