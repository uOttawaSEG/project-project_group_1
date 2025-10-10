/**
 * UserDao defines database operations for the "users" table.
 * It provides methods to insert, lookup, and existence-check operations.
 * Room generates the implementation automatically at compile time.
 */

package com.example.otams.data;

import androidx.room.Dao;
import androidx.room.Insert;
import androidx.room.OnConflictStrategy;
import androidx.room.Query;

import com.example.otams.model.UserEntity;

import java.util.List;

@Dao
public interface UserDao {
    @Insert(onConflict = OnConflictStrategy.ABORT)
    void insert(UserEntity user);

    @Query("SELECT * FROM users WHERE email = :email LIMIT 1")
    UserEntity findByEmail(String email);

    @Query("SELECT EXISTS(SELECT 1 FROM users WHERE email = :email)")
    boolean emailExists(String email);

    @Query("SELECT * FROM users ORDER BY email ASC")
    List<UserEntity> getAllUsers();
}
