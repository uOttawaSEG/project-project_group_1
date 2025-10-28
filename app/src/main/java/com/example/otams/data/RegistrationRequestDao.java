package com.example.otams.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.Query;

import com.example.otams.model.RegistrationRequestEntity;
import com.example.otams.model.RequestStatus;

import java.util.List;

@Dao
public interface RegistrationRequestDao {

    @Insert
    long insert(RegistrationRequestEntity req);

    @Query("SELECT * FROM registration_requests WHERE email=:email LIMIT 1")
    RegistrationRequestEntity findByEmail(String email);

    @Query("SELECT status FROM registration_requests WHERE email=:email LIMIT 1")
    RequestStatus getStatusByEmail(String email);

    @Query("UPDATE registration_requests SET status=:newStatus, reviewedByAdminUserId=:adminId, reviewedAtEpochMs=:ts WHERE id=:id")
    void updateStatus(long id, RequestStatus newStatus, String adminId, long ts);

    @Query("SELECT * FROM registration_requests WHERE status = 'PENDING'")
    List<RegistrationRequestEntity> getPendingRequests();

    @Query("SELECT * FROM registration_requests WHERE status = 'REJECTED'")
    List<RegistrationRequestEntity> getRejectedRequests();
}
