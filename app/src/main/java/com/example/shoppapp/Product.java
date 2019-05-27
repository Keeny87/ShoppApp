package com.example.shoppapp;

public class Product {

    private String name;
    private String brand;
    private String location;
    private String store;
    private String regularPrice;

    public Product() {
    }

    public Product(String name, String brand, String location, String store, String regularPrice) {
        this.name = name;
        this.brand = brand;
        this.location = location;
        this.store = store;
        this.regularPrice = regularPrice;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getBrand() {
        return brand;
    }

    public void setBrand(String brand) {
        this.brand = brand;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public String getStore() {
        return store;
    }

    public void setStore(String store) {
        this.store = store;
    }

    public String getRegularPrice() {
        return regularPrice;
    }

    public void setRegularPrice(String regularPrice) {
        this.regularPrice = regularPrice;
    }

    public String toString(){
        return name + " " + brand;
    }
}
