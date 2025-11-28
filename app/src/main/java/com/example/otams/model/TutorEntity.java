/**
 * TutorEntity represents information of tutor users
 * in the database and is an extension table that inherits UserEntity
 */

package com.example.otams.model;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.ForeignKey;
import androidx.room.PrimaryKey;
import androidx.room.TypeConverters;

import com.example.otams.util.Converters;

import java.util.List;

import static androidx.room.ForeignKey.CASCADE;

@Entity(
        tableName = "tutors",
        foreignKeys = @ForeignKey(
                entity = UserEntity.class,
                parentColumns = "id",
                childColumns = "userId",
                onDelete = CASCADE
        )
)
@TypeConverters(Converters.class)
public class TutorEntity {
    @PrimaryKey @NonNull
    public String userId; // same as user.ID

    @NonNull public String highestDegree;
    @NonNull public List<String> coursesOffered; // at least 1 course
    @NonNull public double averageRating = 0.0;
}
