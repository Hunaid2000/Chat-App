package com.ass2.i192008_i192043;

public class RegistrationUtils {

    // Input field show be valid, if it is not empty
    // Password should be at least 3 characters long
    // Phone number should at_least 5 characters long and start with 0
    public static boolean validateUserInput(String username, String password, String phoneNumber) {
       if(username.length() > 0 && password.length() > 2 && phoneNumber.length() > 4) {
           if (phoneNumber.charAt(0) == '0') {
               return true;
           }
       }
       return false;
    }
}
