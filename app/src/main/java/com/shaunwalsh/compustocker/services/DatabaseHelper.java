package com.shaunwalsh.compustocker.services;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.shaunwalsh.compustocker.model.User;
import com.shaunwalsh.compustocker.model.Category;
import com.shaunwalsh.compustocker.model.InventoryItem;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "pc_inventory.db";
    private static final int DATABASE_VERSION = 1;

    // Table Names
    public static final String TABLE_USERS = "users";
    public static final String TABLE_CATEGORIES = "categories";
    public static final String TABLE_ITEMS = "inventory_items";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        // Users Table
        db.execSQL("CREATE TABLE " + TABLE_USERS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "username TEXT UNIQUE NOT NULL, " +
                "password TEXT NOT NULL, " +
                "full_name TEXT, " +
                "role TEXT)");

        // Categories Table (supports subcategories)
        db.execSQL("CREATE TABLE " + TABLE_CATEGORIES + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "parent_id INTEGER DEFAULT 0, " +
                "description TEXT)");

        // Inventory Items Table
        db.execSQL("CREATE TABLE " + TABLE_ITEMS + " (" +
                "id INTEGER PRIMARY KEY AUTOINCREMENT, " +
                "name TEXT NOT NULL, " +
                "part_number TEXT, " +
                "category_id INTEGER, " +
                "quantity INTEGER DEFAULT 0, " +
                "price REAL, " +
                "manufacturer TEXT, " +
                "description TEXT, " +
                "FOREIGN KEY(category_id) REFERENCES " + TABLE_CATEGORIES + "(id))");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USERS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_CATEGORIES);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        onCreate(db);
    }

    // ===================================================================
    // ======================== USER METHODS =============================
    // ===================================================================

    public long addUser(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("username", user.getUsername());
        values.put("password", user.getPassword());
        values.put("full_name", user.getFullName());
        values.put("role", user.getRole());

        return db.insert(TABLE_USERS, null, values);
    }

    public User getUser(String username, String password) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_USERS, null,
                "username=? AND password=?",
                new String[]{username, password},
                null, null, null);

        if (cursor != null && cursor.moveToFirst()) {
            User user = new User();
            user.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
            user.setUsername(cursor.getString(cursor.getColumnIndexOrThrow("username")));
            user.setPassword(cursor.getString(cursor.getColumnIndexOrThrow("password")));
            user.setFullName(cursor.getString(cursor.getColumnIndexOrThrow("full_name")));
            user.setRole(cursor.getString(cursor.getColumnIndexOrThrow("role")));
            cursor.close();
            return user;
        }
        if (cursor != null) cursor.close();
        return null;
    }

    // ===================================================================
    // ======================== CATEGORY METHODS =========================
    // ===================================================================

    public long addCategory(Category category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", category.getName());
        values.put("parent_id", category.getParentId());
        values.put("description", category.getDescription());

        return db.insert(TABLE_CATEGORIES, null, values);
    }

    public List<Category> getAllCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null, null, null, null, null, "name ASC");

        if (cursor.moveToFirst()) {
            do {
                Category cat = new Category();
                cat.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                cat.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                cat.setParentId(cursor.getInt(cursor.getColumnIndexOrThrow("parent_id")));
                cat.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                categories.add(cat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    public List<Category> getTopLevelCategories() {
        List<Category> categories = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_CATEGORIES, null,
                "parent_id = 0", null, null, null, "name ASC");

        if (cursor.moveToFirst()) {
            do {
                Category cat = new Category();
                cat.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                cat.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                cat.setParentId(0);
                cat.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                categories.add(cat);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return categories;
    }

    // ===================================================================
    // ======================== INVENTORY ITEM METHODS ===================
    // ===================================================================

    public long addInventoryItem(InventoryItem item) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("name", item.getName());
        values.put("part_number", item.getPartNumber());
        values.put("category_id", item.getCategoryId());
        values.put("quantity", item.getQuantity());
        values.put("price", item.getPrice());
        values.put("manufacturer", item.getManufacturer());
        values.put("description", item.getDescription());

        return db.insert(TABLE_ITEMS, null, values);
    }

    public List<InventoryItem> getAllItems() {
        List<InventoryItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null, null, null, null, null, "name ASC");

        if (cursor.moveToFirst()) {
            do {
                InventoryItem item = new InventoryItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                item.setPartNumber(cursor.getString(cursor.getColumnIndexOrThrow("part_number")));
                item.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                item.setManufacturer(cursor.getString(cursor.getColumnIndexOrThrow("manufacturer")));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public List<InventoryItem> getItemsByCategory(int categoryId) {
        List<InventoryItem> items = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, null,
                "category_id=?", new String[]{String.valueOf(categoryId)},
                null, null, "name ASC");

        if (cursor.moveToFirst()) {
            do {
                // Same mapping as above...
                InventoryItem item = new InventoryItem();
                item.setId(cursor.getInt(cursor.getColumnIndexOrThrow("id")));
                item.setName(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                item.setPartNumber(cursor.getString(cursor.getColumnIndexOrThrow("part_number")));
                item.setCategoryId(cursor.getInt(cursor.getColumnIndexOrThrow("category_id")));
                item.setQuantity(cursor.getInt(cursor.getColumnIndexOrThrow("quantity")));
                item.setPrice(cursor.getDouble(cursor.getColumnIndexOrThrow("price")));
                item.setManufacturer(cursor.getString(cursor.getColumnIndexOrThrow("manufacturer")));
                item.setDescription(cursor.getString(cursor.getColumnIndexOrThrow("description")));
                items.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return items;
    }

    public int updateItemQuantity(int itemId, int newQuantity) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put("quantity", newQuantity);

        return db.update(TABLE_ITEMS, values, "id=?", new String[]{String.valueOf(itemId)});
    }

    public int deleteItem(int itemId) {
        SQLiteDatabase db = this.getWritableDatabase();
        return db.delete(TABLE_ITEMS, "id=?", new String[]{String.valueOf(itemId)});
    }
}