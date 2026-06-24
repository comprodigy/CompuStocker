package com.shaunwalsh.compustocker.model;

public class InventoryItem {
    private int id;
    private String name;
    private String partNumber;
    private int categoryId;
    private int quantity;
    private int minquantity;
    private double price;
    private String description;
    private String manufacturer;

    public InventoryItem() {}

    // Full constructor, getters and setters...
    public int getId() { return id; }
    public void setId(int id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getPartNumber() { return partNumber; }
    public void setPartNumber(String partNumber) { this.partNumber = partNumber; }

    public int getCategoryId() { return categoryId; }
    public void setCategoryId(int categoryId) { this.categoryId = categoryId; }

    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public int getMinquantity() { return minquantity; }
    public void setMinQuantity(int value) { minquantity = value; }
    public double getPrice() { return price; }
    public void setPrice(double price) { this.price = price; }

    public String getDescription() { return description;}
    public void setDescription(String desc) { this.description = desc;}

    public String getManufacturer() { return manufacturer; }
    public void setManufacturer(String manufacturer) { this.manufacturer = manufacturer; }
}
