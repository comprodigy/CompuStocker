package com.shaunwalsh.compustocker.repository;

import com.shaunwalsh.compustocker.model.Category;
import com.shaunwalsh.compustocker.model.InventoryItem;
import com.shaunwalsh.compustocker.services.DatabaseManager;

import java.util.List;

public class InventoryRepository {

    public List<InventoryItem> getAllItems() {
        return DatabaseManager.getDbHelper().getAllItems();
    }

    public List<Category> getAllCategories() {
        return DatabaseManager.getDbHelper().getAllCategories();
    }

    public long addItem(InventoryItem item) {
        return DatabaseManager.getDbHelper().addInventoryItem(item);
    }

    public void deleteItem(int itemId) {
        DatabaseManager.getDbHelper().deleteItem(itemId);
    }
}