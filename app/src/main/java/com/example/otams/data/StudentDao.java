/**
 * StudentDao provides database access methods for the "students" table.
 * It currently defines an insert operation to add new StudentEntity records
 * into the local Room database.
 */

package com.example.otams.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;

import com.example.otams.model.StudentEntity;

import java.util.List;
import androidx.room.Query;

@Dao
public interface StudentDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(StudentEntity s);

    @Query("SELECT * FROM students ORDER BY userId")
    List<StudentEntity> getAllStudents();
}