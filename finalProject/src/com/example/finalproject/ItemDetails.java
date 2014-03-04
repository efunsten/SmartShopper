package com.example.finalproject;

import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;
import com.parse.ParseException;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.gson.Gson;

import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.View;
import android.widget.EditText;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;
import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;

public class ItemDetails extends SherlockFragmentActivity{

	ActionBar.Tab Tab1, Tab2;
	Fragment fragmentTab1 = new FragmentTab1();
	Fragment fragmentTab2 = new FragmentTab2();
	public static ParseObject mItem = null;
	public static String mStoreName = null;
	public static String mItemName = null;
	public static Double mQuantity = null;
	public static List<String> mDates = null;
	public static List<Double> mPrices = null;
	public static ParseObject mStore = null;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "RCIm6xY3zgCw0sguu3OtbE0e1aIdd7dBjqhnNrQV", "kwjP5rVdVPPBlSf3PljblCeewigUmTUbto0GKwG4");
		setContentView(R.layout.item_details);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		
		
		Tab1 = actionBar.newTab().setText("Details");
		Tab2 = actionBar.newTab().setText("Graph");
		
		Tab1.setTabListener(new TabListener(fragmentTab1));
		Tab2.setTabListener(new TabListener(fragmentTab2));
		
		actionBar.addTab(Tab1);
		actionBar.addTab(Tab2);
		
		
		mPrices = mItem.getList(ItemList.ITEM_KEY_PRICE);
		mDates = mItem.getList(ItemList.ITEM_KEY_DATE);
		mItemName = mItem.getString(ItemList.ITEM_KEY_NAME);
		mQuantity = mItem.getDouble(ItemList.ITEM_KEY_QUANTITY);
		actionBar.setTitle(mItemName + " (" + Double.toString(mQuantity) + ")");
	}

}
