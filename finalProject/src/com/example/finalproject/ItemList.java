package com.example.finalproject;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.provider.Settings;
import android.support.v4.view.MenuItemCompat;
import android.util.Log;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.ActionBar.OnNavigationListener;
import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.actionbarsherlock.app.SherlockFragmentActivity;
import com.actionbarsherlock.view.Menu;
import com.actionbarsherlock.view.MenuInflater;
import com.actionbarsherlock.view.MenuItem;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesClient;
import com.google.android.gms.location.LocationClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.gson.Gson;
import com.parse.FindCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;


public class ItemList extends SherlockFragmentActivity implements 
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	LocationListener,
	AddItemDialogFragment.NoticeDialogListener{

	protected Menu mMenu;
	protected ListView mListView;
	private LocationClient mLocationClient;
	private LocationRequest mLocationRequest;
	public static Location mLocation;
	private String mAddress;
	
	protected static final String ITEM_CLASS = "Item";
	protected static final String ITEM_KEY_NAME = "name";
	protected static final String ITEM_KEY_PRICE = "price";
	protected static final String ITEM_KEY_QUANTITY = "quantity";
	protected static final String ITEM_KEY_LOCATION = "location";
	protected static final String ITEM_KEY_DATE = "date";
	
	protected static final String LOCATION_CLASS = "Location";
	protected static final String LOCATION_KEY_PLACE = "place";
	protected static final String LOCATION_KEY_GEOPOINT = "point";
	
	protected static final String USER_CLASS = "User";
	protected static final String USER_KEY_FAVORITES = "favorites";

	protected boolean mFavorites = false;
	
	ParseQueryAdapter<ParseObject> mItemAdapter;
	ArrayAdapter<String> mLocationAdapter;
	SpinnerAdapter mSpinnerAdapter;
	ActionBar mActionBar;
	Spinner mSpinner;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "RCIm6xY3zgCw0sguu3OtbE0e1aIdd7dBjqhnNrQV", "kwjP5rVdVPPBlSf3PljblCeewigUmTUbto0GKwG4");
		setContentView(R.layout.item_list);
		mActionBar = getSupportActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		mItemAdapter =
				  new ParseQueryAdapter<ParseObject>(this, new ParseQueryAdapter.QueryFactory<ParseObject>() {
				    public ParseQuery<ParseObject> create() {
				      // Here we can configure a ParseQuery to our heart's desire.
				      ParseQuery query = new ParseQuery(ItemList.ITEM_CLASS);
				      return query;
				    }
				  }) {
			@Override
			public View getItemView(ParseObject object, View v, ViewGroup parent) {
			  if (v == null) {
			    v = View.inflate(getContext(), R.layout.item_view, null);
			  }
			 
			  // Take advantage of ParseQueryAdapter's getItemView logic for
			  // populating the main TextView/ImageView.
			  // The IDs in your custom layout must match what ParseQueryAdapter expects
			  // if it will be populating a TextView or ImageView for you.
			  super.getItemView(object, v, parent);
			 
			  // Do additional configuration before returning the View.
			  TextView itemName = (TextView) v.findViewById(R.id.textView_name);
			  TextView priceView = (TextView) v.findViewById(R.id.textView_price);
			  TextView unitPriceView = (TextView) v.findViewById(R.id.textView_pricePerUnit);
			  List<Double> priceList = object.getList(ITEM_KEY_PRICE);
			  Number price = priceList.get(priceList.size()-1);
		      itemName.setText(object.getString(ItemList.ITEM_KEY_NAME));
			  priceView.setText(Double.toString(price.doubleValue()));
			  unitPriceView.setText(Double.toString(price.doubleValue() / object.getDouble(ITEM_KEY_QUANTITY)));
			  return v;
			}
			
		};
		
		//mItemAdapter.setTextKey(ItemList.ITEM_KEY_NAME);
		
		mListView = (ListView) findViewById(R.id.itemlist);
		mListView.setAdapter(mItemAdapter);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				ParseObject object = mItemAdapter.getItem(position);
				ShoppingItem item = new ShoppingItem(object.getString(ITEM_KEY_DATE), object.getString(ITEM_KEY_NAME), 
						(float)object.getDouble(ITEM_KEY_PRICE), (float)object.getDouble(ITEM_KEY_QUANTITY), object.getParseGeoPoint(ITEM_KEY_LOCATION));
				Intent intent = new Intent(getApplicationContext(), ItemDetails.class);
				intent.putExtra(ITEM_CLASS, new Gson().toJson(item));
				startActivity(intent);
			}
			
		});
		
		mLocationRequest = LocationRequest.create();
		mLocationClient = new LocationClient(this, this, this);
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = this.getSupportMenuInflater();
		inflater.inflate(R.menu.mainmenu, menu);
		mSpinner = new Spinner(getSupportActionBar().getThemedContext());
		MenuItem item = menu.add(Menu.NONE, Menu.NONE, 0, "");
		item.setActionView(mSpinner);
		return true;
	}
	
	@Override
	public boolean onPrepareOptionsMenu(Menu menu) {
		//menu.getItem(0).setTitle(getMenuTitleChange());
		this.mMenu = menu;
		ItemList.mLocation = this.mLocationClient.getLastLocation();
		if(ItemList.mLocation == null) {
			return true;
		}
		ParseGeoPoint point = new ParseGeoPoint(ItemList.mLocation.getLatitude(), ItemList.mLocation.getLongitude());
		ParseQuery<ParseObject> query = ParseQuery.getQuery(LOCATION_CLASS);
		query.whereWithinKilometers(LOCATION_KEY_GEOPOINT, point, 0.1);
		List<ParseObject> nearLocationList = null;
		try {
			nearLocationList = query.find();
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		   
        Log.d("location", "Retrieved " + nearLocationList.size() + " locations");
        ArrayList<String> nearPlaces = new ArrayList<String>();
        nearPlaces.add("Select Location");
        for(ParseObject location : nearLocationList) {
        	nearPlaces.add(location.getString(LOCATION_KEY_PLACE));
        }
        nearPlaces.add("Other");
        nearPlaces.add("Add Location");
        mLocationAdapter = new ArrayAdapter<String>(getBaseContext(), android.R.layout.simple_spinner_item, nearPlaces);
        mLocationAdapter.setDropDownViewResource(R.layout.sherlock_spinner_dropdown_item);
        
		      
		/*
		mLocationAdapter =
				  new ParseQueryAdapter<ParseObject>(this, new ParseQueryAdapter.QueryFactory<ParseObject>() {
				    public ParseQuery<ParseObject> create() {
				      // Here we can configure a ParseQuery to our heart's desire.
				      ParseQuery query = new ParseQuery(ItemList.LOCATION_CLASS);
				      return query;
				    }
				  });
		mLocationAdapter.setTextKey(ItemList.LOCATION_KEY_PLACE);
        mSpinner.setAdapter(mLocationAdapter);
        */
		OnNavigationListener mOnNavigationListener = new OnNavigationListener() 
		   {
		      // Get the same strings provided for the drop-down's ArrayAdapter
		      //String[] strings = getResources().getStringArray(R.array.nav_list);
		 
		      @Override
		      public boolean onNavigationItemSelected(int position, long itemId) 
		      {
		         switch (position)
		         {
		             case 0:
		            	 Log.d("menu", (String) mLocationAdapter.getItem(0));
		            	 break;
		          }
		                   
		          return true;
		        }
		     };
		    
	    mSpinner.setAdapter(mLocationAdapter); 
	    Log.d("Item", (String) mLocationAdapter.getItem(0));
		mActionBar.setListNavigationCallbacks(mLocationAdapter, mOnNavigationListener);
		
		return super.onPrepareOptionsMenu(menu);
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_additem:
			addItem();
			break;
		case R.id.menu_favorites:
			this.mFavorites = !this.mFavorites;

			break;
		}
		return super.onOptionsItemSelected(item);
	}
	
	@Override
	protected void onStart() {
		super.onStart();
		// Connect the client.
		mLocationClient.connect();
	}
	
	@Override
    protected void onStop() {
        // If the client is connected
        if (mLocationClient.isConnected()) {
            /*
             * Remove location updates for a listener.
             * The current Activity is the listener, so
             * the argument is "this".
             */
            mLocationClient.removeLocationUpdates(this);
        }
        /*
         * After disconnect() is called, the client is
         * considered "dead".
         */
        mLocationClient.disconnect();
        super.onStop();
    }
	
	
	
	
	
	private String getMenuTitleChange() {
		return null;
		//TODO
	}
	
	@Override
	public void onLocationChanged(Location location) {
		mLocation = location;
	}

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		mLocationClient.requestLocationUpdates(mLocationRequest, this);		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
	
	public void addItem() {
		AddItemDialogFragment addItemDialog = new AddItemDialogFragment();
		addItemDialog.show(getSupportFragmentManager(), "dialog");
	}
	
	@Override
	public void onDialogPositiveClick(SherlockDialogFragment dialog, ShoppingItem sItem) {
		if(sItem.name != null && sItem.price > 0 && sItem.quantity > 0 && sItem.location != null) {
			ParseObject itemEntry = new ParseObject(ItemList.ITEM_CLASS);
			itemEntry.put(ItemList.ITEM_KEY_NAME, sItem.name);
			itemEntry.add(ItemList.ITEM_KEY_PRICE, sItem.price);
			itemEntry.put(ItemList.ITEM_KEY_QUANTITY, sItem.quantity);
			itemEntry.put(ItemList.ITEM_KEY_LOCATION, sItem.location);
			itemEntry.add(ItemList.ITEM_KEY_DATE, sItem.date);
			try {
				itemEntry.save();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			mListView.setAdapter(mItemAdapter);
		}
	}

	@Override
	public void onDialogNegativeClick(SherlockDialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}

}
