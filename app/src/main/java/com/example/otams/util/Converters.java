/**
 * Converters provides custom type conversion methods for Room.
 * It converts between a List<String> in Java and a comma-separated String
 * in the SQLite database, allowing Room to store and retrieve string lists.
 */

package com.example.otams.util;

import androidx.room.TypeConverter;

import com.example.otams.model.UserRole;


import java.util.ArrayList;
import java.util.List;

public class Converters {
    @TypeConverter
    public static String fromList(List<String> list) {
        if (list == null || list.isEmpty()) return "";
        StringBuilder sb = new StringBuilder();
        for (String s : list) {
            if (s == null) continue;
            String v = s.trim();
            if (v.isEmpty()) continue;
            if (sb.length() > 0) sb.append(",");
            sb.append(v);
        }
        return sb.toString();
    }

    @TypeConverter
    public static List<String> toList(String csv) {
        List<String> out = new ArrayList<>();
        if (csv == null || csv.trim().isEmpty()) return out;
        String[] parts = csv.split(",");
        for (String p : parts) {
            String v = p == null ? "" : p.trim();
            if (!v.isEmpty() && !out.contains(v)) out.add(v);
        }
        return out;
    }
    // new addition:
    @TypeConverter
    public static String fromRequestStatus(com.example.otams.model.RequestStatus s) {
        return s == null ? null : s.name();
    }

    @TypeConverter
    public static com.example.otams.model.RequestStatus toRequestStatus(String v) {
        return v == null ? null : com.example.otams.model.RequestStatus.valueOf(v);
    }

    @TypeConverter
    public static String fromUserRole(UserRole r) {
        return r == null ? null : r.name();
    }

    @TypeConverter
    public static UserRole toUserRole(String v) {
        return v == null ? null : UserRole.valueOf(v);
    }
}
