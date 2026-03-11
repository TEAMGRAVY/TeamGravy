package edu.gcc.gravy;

import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;

/**
 * @author: goettelsg24
 */

public class Main {

    public static ArrayList<Section> allSections;

    public static void main(String[] args) {
        allSections = (new JSONReader()).readJSON();
    }

    public static void run() {
        // Load data - initial reading of the csv or JSON file or database
        // Create Search object
        // Get all user I/O
        // Apply filters
        // Display results
    }

}

