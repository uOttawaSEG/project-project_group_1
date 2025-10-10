/**
 * UserEntity represents a user record stored in the local Room database.
 * It defines the structure of the "users" table, including fields such as
 * ID, name, email, password hash, phone number, and user role.
 * The @Entity annotation marks this class as a database entity for Room,
 * with "users" as the table name and a unique index on the email field.
 * Each instance of UserEntity corresponds to a single row in the "users" table.
 */

package com.example.otams.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Index;
import androidx.room.PrimaryKey;

@Entity(
        tableName = "users",
        indices = {@Index(value = {"email"}, unique = true)}
)
public class UserEntity {
    @PrimaryKey @NonNull
    public String id; // UUID

    public String firstName;
    public String lastName;
    @NonNull public String email;        // 唯一
    @NonNull public String passwordHash; // 不存明文
    public String phone;
    @NonNull public String role;         // "ADMIN" | "STUDENT" | "TUTOR"
}