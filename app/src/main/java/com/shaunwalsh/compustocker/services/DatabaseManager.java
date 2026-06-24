package com.shaunwalsh.compustocker.services;

import android.content.Context;
import java.util.List;

import com.shaunwalsh.compustocker.model.InventoryItem;
import com.shaunwalsh.compustocker.model.Category;

public class DatabaseManager {

    private static DatabaseHelper dbHelper;
    private static boolean isInitialized = false;

    public static void initialize(Context context) {
        if (dbHelper == null) {
            dbHelper = new DatabaseHelper(context);
        }
    }

    public static DatabaseHelper getDbHelper() {
        return dbHelper;
    }

    //Seed initial data (only runs once)
    public static void seedInitialData() {
        if (isInitialized) return;

        DatabaseHelper db = getDbHelper();

        // Check if categories already exist
        if (db.getAllCategories().isEmpty()) {
            // Top Level Categories
            Category ram = new Category();
            ram.setName("System Memory");
            ram.setParentId(0);
            ram.setDescription("RAM Modules");
            db.addCategory(ram);

            Category cpu = new Category();
            cpu.setName("Processors");
            cpu.setParentId(0);
            cpu.setDescription("CPUs");
            db.addCategory(cpu);

            Category gpu = new Category();
            gpu.setName("Graphics Cards");
            gpu.setParentId(0);
            gpu.setDescription("GPUs");
            db.addCategory(gpu);

            // Subcategory example
            Category ddr5 = new Category();
            ddr5.setName("DDR5");
            ddr5.setParentId(1); // Assuming System Memory got ID 1
            ddr5.setDescription("DDR5 RAM");
            db.addCategory(ddr5);
        }

        isInitialized = true;
    }
}