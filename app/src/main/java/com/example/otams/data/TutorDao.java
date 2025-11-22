/**
 * TutorDao provides database access methods for the "tutors" table.
 * It currently defines an insert operation to add new TutorEntity records
 * into the local Room database.
 */

package com.example.otams.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.example.otams.model.StudentEntity;
import com.example.otams.model.TutorEntity;

import java.util.List;
import androidx.room.Query;

@Dao
public interface TutorDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(TutorEntity t);

    @Query("SELECT * FROM tutors ORDER BY userId")
    List<TutorEntity> getAllTutors();

    @Query("SELECT t.* FROM tutors t " +
            "INNER JOIN users u ON u.id = t.userId " +
            "WHERE u.email = :email LIMIT 1")
    TutorEntity findByEmail(String email);

    @Query("SELECT u.email FROM users u " +
            "INNER JOIN tutors t ON u.id = t.userId " +
            "WHERE t.userId = :userId LIMIT 1")
    String getTutorEmail(String userId);



    @Query("SELECT * FROM tutors WHERE userId = :userId")
    TutorEntity findByUserId(String userId);

}