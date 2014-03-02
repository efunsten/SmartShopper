package com.example.finalproject;

import com.parse.ParseGeoPoint;


public class ShoppingLocation{
	String place;
	String address;
	ParseGeoPoint location;
	
	public ShoppingLocation() {
		place = null;
		address = null;
		location = null;
	}
	
	public ShoppingLocation(String place, String address, ParseGeoPoint location) {
		this.place = place;
		this.address = address;
		this.location = location;
	}
	
}
