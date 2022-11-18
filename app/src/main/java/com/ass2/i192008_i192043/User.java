package com.ass2.i192008_i192043;

public class User {
    String firstName;
    String lastName;
    String email;
    String password;
    String bio;
    String profileUrl;
    String gender;

    public static User currentUser;

     static public User getCurrentUser(){
        return currentUser;
    }
    static public void setCurrentUser(User instance){
        currentUser = instance;
    }

    public void setFirstName(String firstName) {
        this.firstName = firstName;
    }

    public void setLastName(String lastName) {
        this.lastName = lastName;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public void setBio(String bio) {
        this.bio = bio;
    }

    public void setProfileUrl(String profileUrl) {
        this.profileUrl = profileUrl;
    }

    public void setGender(String gender) {
        this.gender = gender;
    }

    public String getFirstName() {
        return firstName;
    }

    public String getLastName() {
        return lastName;
    }

    public String getEmail() {
        return email;
    }

    public String getPassword() {
        return password;
    }

    public String getBio() {
        return bio;
    }

    public String getProfileUrl() {
        return profileUrl;
    }

    public String getGender() {
        return gender;
    }


    // getter and setter

}
