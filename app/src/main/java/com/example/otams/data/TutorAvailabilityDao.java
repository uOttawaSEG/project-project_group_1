package com.example.otams.data;

import androidx.room.Dao;
import androidx.room.Delete;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.otams.model.TutorAvailabilityEntity;

import java.util.List;

@Dao
public interface TutorAvailabilityDao {

    // Get all time periods of a tutor
    @Query("SELECT * FROM tutor_availability " +
            "WHERE tutorEmail = :email " +
            "ORDER BY date, startTime")
    List<TutorAvailabilityEntity> getSlotsForTutor(String email);

    // Insert new time period
    @Insert(onConflict = OnConflictStrategy.ABORT)
    long insert(TutorAvailabilityEntity slot);

    // delete time period
    @Delete
    void delete(TutorAvailabilityEntity slot);

    // Check if there are overlapping time slots on the same day.
    // NOT (:endTime <= startTime OR :startTime >= endTime)
    @Query("SELECT * FROM tutor_availability " +
            "WHERE tutorEmail = :email " +
            "AND date = :date " +
            "AND NOT (:endTime <= startTime OR :startTime >= endTime)")
    List<TutorAvailabilityEntity> findOverlapping(
            String email,
            String date,
            String startTime,
            String endTime
    );
}

