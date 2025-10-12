package com.example.otams.formValidation;

public class Validation {

    public static String checkName(String name) {
        if (name == null) {
            return "Please enter a name.";
        }
        if (!(name.trim().length() >= 2)) {
            return "A name must have at least two letters.";
        }
        return null;
    }

    public static String checkEmail(String email) {
        if (email == null) {
            return "Please enter an email";
        }
        email = email.trim();
        if (!(email.contains("@"))) {
            return "This email address is invalid.";
        }
        String username = email.substring(0, email.indexOf("@"));
        if (username.isEmpty() || username.length() > 64) {
            return "This email address is invalid.";
        }
        if (username.charAt(0) == '.' || username.charAt(username.length() - 1) == '.' || username.contains("..")
        || username.contains("@") || username.contains("(") || username.contains(")")) {
            return "This email address is invalid.";
        }
        return null;
    }

    public static String checkPhone(String phone) {
        if (phone == null) {
            return "Please enter a phone number.";
        }
        phone = phone.trim();
        phone = phone.replace("-", "");
        phone = phone.replace(" ", "");
        for (int i = 0; i < phone.length(); i++) {
            if (!(Character.isDigit(phone.charAt(i)))) {
                return "This is an invalid phone number.";
            }
        }
        if (!(phone.length() == 10)) {
            return "This is an invalid phone number.";
        }
        for (int n = 0; n < phone.length(); n++) {
            char character = phone.charAt(n);
            if (!Character.isDigit(character)) {
                return "This is an invalid phone number.";
            }
        }
        return null;
    }

    public static String checkPassword(String password) {
        if (password == null) {
            return "The password field cannot be empty.";
        }
        if (!(password.length() >= 8)) {
            return "The password is not long enough.";
        }
        if (!(password.length() <= 64)) {
            return "The password is too long.";
        }
        if (password.contains(" ")) {
            return "A password cannot contain a space.";
        }
        boolean specialCharacter = false;
        for (int n = 0; n < password.trim().length(); n++) {
            if (!(Character.isDigit(password.trim().charAt(n)) || Character.isLetter(password.trim().charAt(n)))) {
                specialCharacter = true;
                break;
            }
        }
        if (!specialCharacter) {
            return "The password requires a special character.";
        }
        return null;
    }

    public static String checkProgramOfStudy(String programOfStudy) {
        if (programOfStudy == null) {
            return "The program of study field cannot be empty.";
        }
        programOfStudy = programOfStudy.trim();
        if (programOfStudy.isEmpty()) {
            return "The program of study field cannot be empty.";
        }
        return null;
    }

    public static String checkHighestDegree(String highestDegree) {
        if (highestDegree == null) {
            return "The highest degree field cannot be empty.";
        }
        highestDegree = highestDegree.trim();
        if (highestDegree.isEmpty()) {
            return "The highest degree field cannot be empty.";
        }
        return null;
    }

    public static String checkCourse(String course) {
        if (course == null) {
            return "The course field cannot be empty.";
        }
        course = course.trim();
        if (course.isEmpty()) {
            return "The course field cannot be empty.";
        }
        return null;
    }


}
