package com.ass2.i192008_i192043;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

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

    Bitmap userImg;

    public Bitmap getUserImg() {
        return userImg;
    }

    public void setUserImg(Bitmap userImg) {
        this.userImg = userImg;
    }

    public static User currentUser;

    public User() {
    }

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

    public void SetUserFromURl(String path){
        Thread thread = new Thread(new Runnable() {
            @Override
            public void run() {
                try  {
                    InputStream in =null;
                    Bitmap bmp=null;
                    int responseCode = -1;
                    try{
                        URL url = new URL(path);
                        HttpURLConnection con = (HttpURLConnection)url.openConnection();
                        con.setDoInput(true);
                        con.connect();
                        responseCode = con.getResponseCode();
                        // write the response code to the log
                        Log.d("Response Code", "Response Code: " + responseCode);
                        if(responseCode == HttpURLConnection.HTTP_OK)
                        {
                            in = con.getInputStream();
                            bmp = BitmapFactory.decodeStream(in);
                            in.close();
                            User.getCurrentUser().setUserImg(bmp);
                            System.out.println("Sucess");
                        }
                    }
                    catch(Exception ex){
                        Log.e("Exception",ex.toString());
                        System.out.println("Exception :");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        });
        thread.start();

    }
}
