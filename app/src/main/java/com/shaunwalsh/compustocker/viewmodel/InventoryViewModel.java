package com.shaunwalsh.compustocker.viewmodel;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.ViewModel;

import com.shaunwalsh.compustocker.model.Category;
import com.shaunwalsh.compustocker.model.InventoryItem;
import com.shaunwalsh.compustocker.repository.InventoryRepository;

import java.util.ArrayList;
import java.util.List;

public class InventoryViewModel extends ViewModel {

    private final InventoryRepository repository = new InventoryRepository();

    private final MutableLiveData<List<InventoryItem>> items =
            new MutableLiveData<>(new ArrayList<>());
    private final MutableLiveData<String> message = new MutableLiveData<>();

    public LiveData<List<InventoryItem>> getItems() {
        return items;
    }

    public LiveData<String> getMessage() {
        return message;
    }

    public void loadItems() {
        items.setValue(repository.getAllItems());
    }

    public List<Category> getCategories() {
        return repository.getAllCategories();
    }

    public boolean addItem(String name,
                           String partNumber,
                           String manufacturer,
                           String description,
                           String quantityText,
                           String minQuantityText,
                           String priceText,
                           int categoryId) {

        if (name == null || name.trim().isEmpty() || partNumber == null || partNumber.trim().isEmpty()) {
            message.setValue("Name and Part Number are required");
            return false;
        }

        int quantity;
        int minQuantity;
        double price;
        try {
            quantity = Integer.parseInt(quantityText.trim());
            minQuantity = Integer.parseInt(minQuantityText.trim());
            price = Double.parseDouble(priceText.trim());
        } catch (NumberFormatException e) {
            message.setValue("Quantity, min quantity, and price must be valid numbers");
            return false;
        }

        if (quantity < 0 || minQuantity < 0 || price < 0) {
            message.setValue("Quantity, min quantity, and price cannot be negative");
            return false;
        }

        InventoryItem item = new InventoryItem();
        item.setName(name.trim());
        item.setPartNumber(partNumber.trim());
        item.setManufacturer(manufacturer == null ? "" : manufacturer.trim());
        item.setDescription(description == null ? "" : description.trim());
        item.setQuantity(quantity);
        item.setMinQuantity(minQuantity);
        item.setPrice(price);
        item.setCategoryId(categoryId);

        long newId = repository.addItem(item);
        if (newId <= 0) {
            message.setValue("Could not save item");
            return false;
        }

        message.setValue("Item added successfully");
        loadItems();
        return true;
    }

    public void deleteItem(InventoryItem item) {
        repository.deleteItem(item.getId());
        message.setValue(item.getName() + " deleted");
        loadItems();
    }
}