package edu.gcc.gravy;

import org.junit.jupiter.api.Test;

import javax.security.auth.login.AccountNotFoundException;
import java.security.NoSuchAlgorithmException;

import static org.junit.jupiter.api.Assertions.*;

class ProfileFileManagerTest {

    @Test
    void saveProfile() throws NoSuchAlgorithmException {
        ProfileFileManager manager = ProfileFileManager.getInstance();

        Profile test1 = new Profile();
        test1.setName("Joe Smith");
        test1.setHashedPassword("Ooof");
        test1.setMajor("Electrical Engineering");
        //test1.addCourse(new Course(123, "testCourse","T3$T", 2));
        //test1.addCourse(new Course(321, "courseTest","T3$T", 4));
        test1.setPreferences(true, false, true);

        assertTrue(manager.SaveProfile("Test1", test1));
    }

    @Test
    void loadProfile() throws NoSuchAlgorithmException, AccountNotFoundException {
        ProfileFileManager manager = ProfileFileManager.getInstance();

        Profile test1 = new Profile();
        test1.setName("Joe Smith");
        test1.setHashedPassword("Ooof");
        test1.setMajor("Electrical Engineering");
        //test1.addCourse(new Course(123, "testCourse","T3$T", 2));
        //test1.addCourse(new Course(321, "courseTest","T3$T", 4));
        test1.setPreferences(true, false, true);

        assertTrue(manager.SaveProfile("Test1", test1));

        assertThrows(AccountNotFoundException.class , () -> manager.LoadProfile("Test1", "Oops"));
        Profile loaded = manager.LoadProfile("Test1", "Ooof");
        assertEquals(loaded.getName(), test1.getName());
        assertEquals(loaded.getHashedPassword(), test1.getHashedPassword());
        assertEquals(loaded.getMajor(), test1.getMajor());
        assertEquals(loaded.getGradYear(), test1.getGradYear());

    }
}