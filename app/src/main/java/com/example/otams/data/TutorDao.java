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
}