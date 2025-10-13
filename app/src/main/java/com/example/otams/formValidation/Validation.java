package com.example.otams.formValidation;

public class Validation {

    public static String checkName(String name) {
        if (name == null) {
            return "Please enter a name.";
        }
        if (!(name.trim().length() >= 2)) {
            return "Name must have at least 2 characters.";
        }
        return null;
    }

    public static String checkEmail(String email) {
        if (email == null) {
            return "Please enter an email address.";
        }
        email = email.trim();
        if (!(email.contains("@"))) {
            return "Email must contain '@'.";
        }
        String username = email.substring(0, email.indexOf("@"));
        if (username.isEmpty() || username.length() > 64) {
            return "Email username part is invalid (should be 1-64 characters).";
        }
        if (username.charAt(0) == '.' || username.charAt(username.length() - 1) == '.' || username.contains("..")
                || username.contains("@") || username.contains("(") || username.contains(")")) {
            return "Email username contains invalid characters or formatting.";
        }
        return null;
    }

    public static String checkPhone(String phone) {
        if (phone == null) {
            return "Please enter a phone number.";
        }
        phone = phone.trim().replace("-", "").replace(" ", "");
        if (phone.length() != 10) {
            return "Phone number must have 10 digits (format: XXX-XXX-XXXX).";
        }
        for (int i = 0; i < phone.length(); i++) {
            if (!Character.isDigit(phone.charAt(i))) {
                return "Phone number can only contain digits (format: XXX-XXX-XXXX).";
            }
        }
        return null;
    }

    public static String checkPassword(String password) {
        if (password == null) {
            return "Please enter a password.";
        }
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        if (password.length() > 64) {
            return "Password cannot exceed 64 characters.";
        }
        if (password.contains(" ")) {
            return "Password cannot contain spaces.";
        }
        boolean specialCharacter = false;
        for (int n = 0; n < password.trim().length(); n++) {
            char c = password.charAt(n);
            if (!Character.isLetterOrDigit(c)) {
                specialCharacter = true;
                break;
            }
        }
        if (!specialCharacter) {
            return "Password must contain at least one special character (e.g., @, #, $).";
        }
        return null;
    }

    public static String checkProgramOfStudy(String programOfStudy) {
        if (programOfStudy == null || programOfStudy.trim().isEmpty()) {
            return "Program of study cannot be empty. Please enter your program.";
        }
        return null;
    }

    public static String checkHighestDegree(String highestDegree) {
        if (highestDegree == null || highestDegree.trim().isEmpty()) {
            return "Highest degree cannot be empty. Please enter your degree.";
        }
        return null;
    }

    public static String checkCourse(String course) {
        if (course == null || course.trim().isEmpty()) {
            return "Course field cannot be empty. Please enter at least one course.";
        }
        return null;
    }

}
