package com.ass2.i192008_i192043;

import static org.junit.Assert.*;

public class RegistrationUtilsTest extends RegistrationUtils {

        @org.junit.Test
        public void testEmpty() {
            assertFalse(RegistrationUtils.validateUserInput("", "", ""));
        }

        @org.junit.Test
        public void testPhoneNumber() {
                assertTrue(RegistrationUtils.validateUserInput("username", "password", "0123456789"));
                assertFalse(RegistrationUtils.validateUserInput("username", "password", "1234"));
                assertFalse(RegistrationUtils.validateUserInput("username", "password", "034"));
        }

        @org.junit.Test
        public void testPassword() {
                assertFalse(RegistrationUtils.validateUserInput("username", "pa", "0123456789"));
                assertTrue(RegistrationUtils.validateUserInput("username", "password", "0123456789"));
        }

}