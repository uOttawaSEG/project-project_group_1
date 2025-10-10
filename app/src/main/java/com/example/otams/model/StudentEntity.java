/**
 * StudentEntity represents a student record in the local Room database.
 * It defines the "students" table, which has a foreign key relationship
 * with the "users" table (UserEntity). Each student entry references
 * a corresponding user by userId, and will be automatically deleted if
 * the related user is removed (due to ON DELETE CASCADE).
 */

package com.example.otams.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "students",
        foreignKeys = @ForeignKey(
                entity = UserEntity.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = CASCADE
        )
)
public class StudentEntity {
    @PrimaryKey @NonNull
    public String userId; // same as user ID
    @NonNull public String programOfStudy;
}
