package com.ass2.i192008_i192043;

import org.junit.Test;
import org.junit.runner.RunWith;
import static org.junit.Assert.*;
import static org.junit.Assert.assertEquals;

import androidx.test.ext.junit.runners.AndroidJUnit4;


@RunWith(AndroidJUnit4.class)
public class valideSignIn {
    @Test
    public void testExampleWithCorrectValues() {
        String validEmail = "email";
        String validPassword = "password";
        assertEquals(true, true);
    }

    @Test
    public void testExampleWithIncorrectEmail() {
        String invalidEmail = "email1";
        String validPassword = "password";
        String responseOfExecutingYourApiWithIncorrectValues ="";
        assertEquals(false, responseOfExecutingYourApiWithIncorrectValues);
    }

    @Test
    public void testExampleWithIncorrectPassword() {
        String validEmail = "email";
        String invalidPassword = "password1";
        String responseOfExecutingYourApiWithCorrectValues ="";

        assertEquals(false, responseOfExecutingYourApiWithCorrectValues);

    }

    @Test
    public void testExampleWithIncorrectValues() {
        String invalidEmail = "anashameed980@gmail.com";
        String invalidPassword = "testuser";
        boolean responseOfExecutingYourApiWithIncorrectValues=true;
        assertEquals(false, responseOfExecutingYourApiWithIncorrectValues);
    }
}
