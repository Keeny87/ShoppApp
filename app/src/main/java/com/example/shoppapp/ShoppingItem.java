package com.example.shoppapp;

public class ShoppingItem {

    private String name;
    private String brand;
    private String location;
    private String store;
    private double price;
    private boolean onSale;
    private boolean inBasket;

    public ShoppingItem() {
        // Default constructor required for calls to DataSnapshot.getValue(User.class)
    }

    public ShoppingItem(String name, String brand, String location, String store, double price, boolean onSale, boolean inBasket) {
        this.name = name;
        this.brand = brand;
        this.location = location;
        this.store = store;
        this.price = price;
        this.onSale = onSale;
        this.inBasket = inBasket;
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

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public boolean isOnSale() {
        return onSale;
    }

    public void setOnSale(boolean onSale) {
        this.onSale = onSale;
    }

    public boolean isInBasket() {
        return inBasket;
    }

    public void setInBasket(boolean inBasket) {
        this.inBasket = inBasket;
    }

}
