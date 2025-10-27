package com.example.otams.model;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.room.*;
import java.util.List;

@Entity(tableName = "registration_requests",
        indices = {@Index("email"), @Index("status")})
public class RegistrationRequestEntity {
    @PrimaryKey(autoGenerate = true)
    public long id;

    @NonNull public String email;
    @NonNull public String firstName;
    @NonNull public String lastName;

    @NonNull public UserRole role;           // STUDENT / TUTOR
    @Nullable public String phone;

    // student-only
    @Nullable public String programOfStudy;

    // tutor-only
    @Nullable public String highestDegree;
    @Nullable public List<String> coursesOffered;


    @NonNull public String passwordHash;

    public String rawPassword;

    @NonNull public RequestStatus status = RequestStatus.PENDING;
    @Nullable public String reviewedByAdminUserId;
    @Nullable public Long reviewedAtEpochMs;

    @NonNull public long createdAtEpochMs;
}
