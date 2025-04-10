package com.example.assignment1;

public class Product {
    private String name;
    private int quantity;
    private double price;
    private String type;
    private String imgName;

    public Product(String name, int quantity, double price, String type, String imgName) {
        this.name = name;
        this.quantity = quantity;
        this.price = price;
        this.type = type;
        this.imgName = imgName;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getType() {
        return type;
    }

    public void setType(String type) {
        this.type = type;
    }

    public String getImgName() {
        return imgName;
    }

    public void setImgName(String imgName) {
        this.imgName = imgName;
    }
}
