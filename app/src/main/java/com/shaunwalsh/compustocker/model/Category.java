package com.shaunwalsh.compustocker.model;

public class Category {
    private int id;
    private  String name;
    private int parentId;
    private String description;

    public Category() {}

    public Category(String name, int parentId)
    {
        this.name = name;
        this.parentId = parentId;
    }

    public int getId() {return id;}
    public void setId(int id) {this.id = id;}

    public String getName() {return name;}
    public void setName(String name) {this.name = name;}

    public int getParentId() {return parentId;}
    public void setParentId(int id) {this.parentId = id;}

    public String getDescription() {return description;}
    public void setDescription(String desc) {this.description = desc;}

    public boolean isTopLevel()
    {
        return parentId <= 0;
    }
}
