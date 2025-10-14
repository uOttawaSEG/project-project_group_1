package com.example.otams.formValidation;

public class Validation {

    // Checks if a name is valid
    public static String checkName(String name) {
        if (name == null) {
            return "Please enter a name.";
        }
        // A name must have a minimum of two characters
        if (!(name.trim().length() >= 2)) {
            return "Name must have at least 2 characters.";
        }
        return null;
    }

    // Checks the email address
    public static String checkEmail(String email) {
        if (email == null) {
            return "Please enter an email address.";
        }
        email = email.trim();
        // An email must have a "@"
        if (!(email.contains("@"))) {
            return "Email must contain '@'.";
        }
        String username = email.substring(0, email.indexOf("@"));
        // The email username cannot be more than 64 characters
        if (username.isEmpty() || username.length() > 64) {
            return "Email username part is invalid (should be 1-64 characters).";
        }
        // Email username cannot contain "." at the start or end, "..", "@", "(" and ")"
        if (username.charAt(0) == '.' || username.charAt(username.length() - 1) == '.' || username.contains("..")
                || username.contains("@") || username.contains("(") || username.contains(")")) {
            return "Email username contains invalid characters or formatting.";
        }
        return null;
    }

    // Checks the phone number format
    public static String checkPhone(String phone) {
        if (phone == null) {
            return "Please enter a phone number.";
        }
        phone = phone.trim().replace("-", "").replace(" ", "");
        // Phone number length must be 10
        if (phone.length() != 10) {
            return "Phone number must have 10 digits (format: XXX-XXX-XXXX).";
        }
        // A phone number can only have digits
        for (int i = 0; i < phone.length(); i++) {
            if (!Character.isDigit(phone.charAt(i))) {
                return "Phone number can only contain digits (format: XXX-XXX-XXXX).";
            }
        }
        return null;
    }

    // Checks if the password format is valid
    public static String checkPassword(String password) {
        if (password == null) {
            return "Please enter a password.";
        }
        // Password length must be at least 8 characters
        if (password.length() < 8) {
            return "Password must be at least 8 characters long.";
        }
        // Password length cannot be greater than 64 characters
        if (password.length() > 64) {
            return "Password cannot exceed 64 characters.";
        }
        // There cannot be a space in a password
        if (password.contains(" ")) {
            return "Password cannot contain spaces.";
        }
        // A password must have a special character
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

    // Check if the program of study is valid (not null)
    public static String checkProgramOfStudy(String programOfStudy) {
        if (programOfStudy == null || programOfStudy.trim().isEmpty()) {
            return "Program of study cannot be empty. Please enter your program.";
        }
        return null;
    }

    // Check if the highest degree is valid (not null)
    public static String checkHighestDegree(String highestDegree) {
        if (highestDegree == null || highestDegree.trim().isEmpty()) {
            return "Highest degree cannot be empty. Please enter your degree.";
        }
        return null;
    }

    // Check if the course is valid (not null)
    public static String checkCourse(String course) {
        if (course == null || course.trim().isEmpty()) {
            return "Course field cannot be empty. Please enter at least one course.";
        }
        return null;
    }

}
