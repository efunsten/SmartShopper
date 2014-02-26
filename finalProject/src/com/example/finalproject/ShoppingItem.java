package com.example.finalproject;

import android.location.Location;

public class ShoppingItem {
	String date;
	String name;
	float price;
	float quantity;
	Location location;
	
	public ShoppingItem() {
		date = "";
		name = "";
		price = 0;
		quantity = 0;
		location = null;
	}
	
	public ShoppingItem(String date, String name, float price, float quantity,Location location) {
		this.date = date;
		this.name = name;
		this.price = price;
		this.quantity = quantity;
		this.location = location;
	}
	
	public void setPrice(float price) {
		this.price = price;
	}
}
