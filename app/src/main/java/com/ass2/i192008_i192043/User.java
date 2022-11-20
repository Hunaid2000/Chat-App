package com.ass2.i192008_i192043;

public class User {
    String userId;
    String name;
    String password;
    String phno;
    String bio;
    String profileUrl;
    String gender;
    String status;
    String lastSeen;

    public static User currentUser;

     static public User getCurrentUser(){
         // check for null
            if(currentUser == null){
                currentUser = new User();
            }
        return currentUser;
    }
    static public void setCurrentUser(User instance){
        currentUser = instance;
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

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getLastSeen() {
        return lastSeen;
    }

    public void setLastSeen(String lastSeen) {
        this.lastSeen = lastSeen;
    }

    public String getUserId() {
        return userId;
    }

    public void setUserId(String userId) {
        this.userId = userId;
    }

    public String getPhno() {
        return phno;
    }

    public void setPhno(String phno) {
        this.phno = phno;
    }
}
