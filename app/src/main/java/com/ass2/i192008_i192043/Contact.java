package com.ass2.i192008_i192043;

public class Contact extends User{
    String status;
    String lastSeen;

    Contact(){
    }

    public void setUser(User user){
        this.firstName = user.firstName;
        this.lastName = user.lastName;
        this.email = user.email;
        this.password = user.password;
        this.bio = user.bio;
        this.profileUrl = user.profileUrl;
        this.gender = user.gender;
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
}
