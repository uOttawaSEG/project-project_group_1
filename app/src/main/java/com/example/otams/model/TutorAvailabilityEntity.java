package com.example.otams.model;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.Entity;
import androidx.room.PrimaryKey;

@Entity(tableName = "tutor_availability")
public class TutorAvailabilityEntity {

    // Will revert back to false when the app closes
    public static boolean autoApproval = false;

    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull
    public String tutorEmail;

    //YYYY-MM-DD eg: 2025-01-01
    @NonNull
    public String date;

    // 30min
    @NonNull
    public String startTime;

    @NonNull
    public String endTime;

    public boolean autoApprove;


    // NONE, PENDING, ACCEPTED, REJECTED
    @NonNull
    public String requestStatus = "NONE";

    @Nullable
    public String studentEmail;

    @Nullable
    public Integer rating;

}