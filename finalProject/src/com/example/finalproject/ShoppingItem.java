package com.example.finalproject;

import com.parse.ParseGeoPoint;


public class ShoppingItem {
	String date;
	String name;
	float price;
	float quantity;
	ParseGeoPoint location;
	
	public ShoppingItem() {
		date = "";
		name = "";
		price = 0;
		quantity = 0;
		location = null;
	}
	
	public ShoppingItem(String date, String name, float price, float quantity,ParseGeoPoint location) {
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
