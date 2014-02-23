package com.example.finalproject;

public class ShoppingItem {
	String mDate;
	String mItem;
	float mPrice;
	float mQuantity;
	
	public ShoppingItem() {
		mDate = "";
		mItem = "";
		mPrice = 0;
		mQuantity = 0;
	}
	
	public ShoppingItem(String date, String item, float price, float quantity) {
		mDate = date;
		mItem = item;
		mPrice = price;
		mQuantity = quantity;
	}
	
	public void setPrice(float price) {
		mPrice = price;
	}
}
