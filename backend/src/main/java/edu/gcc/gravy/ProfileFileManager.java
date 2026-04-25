package edu.gcc.gravy;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import javax.security.auth.login.AccountNotFoundException;
import java.io.*;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.List;

/**
 * Manages saved schedule files in place of a database.
 */
public class ProfileFileManager {

    private static ProfileFileManager manager = null;

    private ProfileFileManager() {
    }

    public static ProfileFileManager getInstance() {
        if (manager == null) {
            manager = new ProfileFileManager();
        }
        return manager;
    }

    // Saves the schedule object by converting it to a smaller format
    // and then writing it to a json file
    public boolean SaveProfile(String fileName, Profile object) {
        Gson gson = new GsonBuilder()
                .setPrettyPrinting()
                .create();
        try (FileWriter writer = new FileWriter(fileName)) {
            gson.toJson(object, writer);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return false;
    }

    public Profile LoadProfile(String fileName, String password) throws AccountNotFoundException {
        Gson gson = new Gson();
        try (FileReader reader = new FileReader(fileName)) {
            Profile loaded = gson.fromJson(reader, Profile.class);
            if (loaded.getHashedPassword().equals(HashUtils.sha256(password))) {
                return loaded;
            } else {
                throw new AccountNotFoundException("Account does not exist.");
            }
        } catch (IOException e) {
            e.printStackTrace();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
        return null;
    }
}