package com.example.finalproject;

import java.util.ArrayList;
import java.util.Arrays;
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
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TableLayout;
import android.widget.TableRow;
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
import com.parse.GetCallback;
import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;
import com.parse.SaveCallback;


public class ItemList extends SherlockFragmentActivity implements 
	GooglePlayServicesClient.ConnectionCallbacks,
	GooglePlayServicesClient.OnConnectionFailedListener,
	LocationListener,
	AddItemDialogFragment.NoticeDialogListener,
	AddLocationDialogFragment.NoticeDialogListener{

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
	protected static final String ITEM_KEY_ID = "objectId";
	
	protected static final String LOCATION_CLASS = "Location";
	protected static final String LOCATION_KEY_PLACE = "place";
	protected static final String LOCATION_KEY_GEOPOINT = "point";
	protected static final String LOCATION_KEY_ADDRESS = "address";
	
	protected static final String USER_CLASS = "User";
	protected static final String USER_KEY_FAVORITES = "favorites";

	protected boolean mFavorites = false;
	
	protected static final int NEW_LOCATION = 0;
	public static ParseObject mParseLocation = null;
	ParseQueryAdapter<ParseObject> mItemAdapter;
	ArrayAdapter<String> mLocationAdapter;
	SpinnerAdapter mSpinnerAdapter;
	ActionBar mActionBar;
	Spinner mSpinner;
	List<ParseObject> mLocationList = null;
	public static List<ParseObject> mNewLocations = null;
	List<String> mFavoriteList = null;
	
	public static String mItemName = null;
	public static String mQuantity = null;
	public static String mItemId = null;
	
	protected double mPriceColor;
	protected double mUnitPriceColor;
	protected TextView mPriceView;
	protected TextView mUnitPriceView;
	
	public ShoppingItem mSItem;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "RCIm6xY3zgCw0sguu3OtbE0e1aIdd7dBjqhnNrQV", "kwjP5rVdVPPBlSf3PljblCeewigUmTUbto0GKwG4");
		setContentView(R.layout.item_list);
		mActionBar = getSupportActionBar();
		mActionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);
		mActionBar.setDisplayShowTitleEnabled(false);
		mActionBar.setDisplayShowHomeEnabled(false);
		
		mListView = (ListView) findViewById(R.id.itemlist);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				ItemDetails.mItem = mItemAdapter.getItem(position);		
				Intent intent = new Intent(getApplicationContext(), ItemDetails.class);
				startActivity(intent);
			}
			
		});
		
		mLocationRequest = LocationRequest.create();
		mLocationClient = new LocationClient(this, this, this);
		mNewLocations = new ArrayList<ParseObject>();
		ItemDetails.mStoreList = new ArrayList<ParseObject>();
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
		ArrayList<String> nearPlaces = new ArrayList<String>();
		if(mLocationList != null) {
			mLocationList.clear();
		}
		this.mMenu = menu;
		if(mLocationClient != null && mLocationClient.isConnected()) {
			ItemList.mLocation = this.mLocationClient.getLastLocation();
		}
		
		if(ItemList.mLocation != null) {
			(new GetAddressTask(this)).execute(mLocation);
			ParseGeoPoint point = new ParseGeoPoint(ItemList.mLocation.getLatitude(), ItemList.mLocation.getLongitude());
			ParseQuery<ParseObject> query = ParseQuery.getQuery(LOCATION_CLASS);
			query.whereWithinKilometers(LOCATION_KEY_GEOPOINT, point, 0.5);
			
			try {
				mLocationList = query.find();
			} catch (ParseException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			   
	        Log.d("location", "Retrieved " + mLocationList.size() + " locations");
	        
	        for(ParseObject location : mLocationList) {
	        	nearPlaces.add(location.getString(LOCATION_KEY_PLACE));
	        }
		}
		for(ParseObject location : mNewLocations) {
			mLocationList.add(0, location);
			nearPlaces.add(0, location.getString(LOCATION_KEY_PLACE));
		}
		
		if(mNewLocations != null) {
			ItemDetails.mStoreList.addAll(mNewLocations);
		}
		if(mLocationList != null) {
			ItemDetails.mStoreList.addAll(mLocationList);
		}
		
        if(mLocationList == null || mLocationList.size() == 0) {
        	nearPlaces.add("Select Location");
        	menu.getItem(0).setEnabled(false);
        }
        else {
        	menu.getItem(0).setEnabled(true);
        }
        if(this.mFavorites) {
        	menu.getItem(1).setIcon(R.drawable.rate_star_med_on);
        }
        else {
        	menu.getItem(1).setIcon(R.drawable.rate_star_med_off);
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
		    	  
		    	  if(position == mLocationAdapter.getCount()-1) {
		           	  addLocation();
		          }
		          else if(position == mLocationAdapter.getCount()-2) {
		         	  Intent intent = new Intent(getApplicationContext(), LocationList.class);
		        	  startActivityForResult(intent, NEW_LOCATION);
		          }
		          else if(mLocationList != null && !mLocationList.isEmpty()) {
		    		  mParseLocation = mLocationList.get(position);  
		    		  updateItemListAdapter();
		    	  }

		          return true;
		     }
		   };
		    
	    mSpinner.setAdapter(mLocationAdapter); 
	    Log.d("Item", (String) mLocationAdapter.getItem(0));
		mActionBar.setListNavigationCallbacks(mLocationAdapter, mOnNavigationListener);

		return super.onPrepareOptionsMenu(menu);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		//mLocationClient.connect();
		
		if(requestCode == NEW_LOCATION) {
			if(resultCode == RESULT_OK) {
				supportInvalidateOptionsMenu();
			}
			else if(resultCode == RESULT_CANCELED) {
				
			}
		}
	}
	
	protected void updateItemListAdapter() {

		mFavoriteList = Login.mCurrentUser.getList(ItemList.USER_KEY_FAVORITES);

		mItemAdapter =
				  new ParseQueryAdapter<ParseObject>(this, new ParseQueryAdapter.QueryFactory<ParseObject>() {
				    public ParseQuery<ParseObject> create() {
				      // Here we can configure a ParseQuery to our heart's desire.
					      ParseQuery query = new ParseQuery(ItemList.ITEM_CLASS);
					      query.whereEqualTo(ITEM_KEY_LOCATION, mParseLocation);
					      if(mFavorites && mFavoriteList != null) {
					    	  query.whereContainedIn(ItemList.ITEM_KEY_ID, mFavoriteList);
					      }
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
				  //super.getItemView(object, v, parent);
				 
				  // Do additional configuration before returning the View.
				  	  CheckBox checkBox = (CheckBox) v.findViewById(R.id.checkBox_favorite);
				  	  String id = object.getObjectId();
				  	  checkBox.setTag(id);
				  	  Log.d("Check", (String) checkBox.getTag());
				  	  if(mFavoriteList != null)
				  		  checkBox.setChecked(mFavoriteList.contains(id));
				  	  
					  TextView itemName = (TextView) v.findViewById(R.id.textView_name);
					  TextView priceView = (TextView) v.findViewById(R.id.textView_price);
					  TextView unitPriceView = (TextView) v.findViewById(R.id.textView_pricePerUnit);
					  List<Double> priceList = object.getList(ITEM_KEY_PRICE);
					  Number price = priceList.get(priceList.size()-1);
					  Double quantity = object.getDouble(ITEM_KEY_QUANTITY);
					  String name = object.getString(ItemList.ITEM_KEY_NAME);
				      itemName.setText(name + " (" + Double.toString(quantity) + ")");
					  priceView.setText(Double.toString(price.doubleValue()));
					  unitPriceView.setText(String.format("%.2f", price.doubleValue() / quantity));
					  Button update = (Button) v.findViewById(R.id.button_update);
					  update.setTag(new Update(name, Double.toString(quantity), id));
					  
					  mPriceColor = price.doubleValue();
					  mUnitPriceColor = price.doubleValue() / quantity;
					  mPriceView = priceView;
					  mUnitPriceView = unitPriceView;
					  
					  ParseQuery<ParseObject> query = ParseQuery.getQuery(ItemList.ITEM_CLASS);
				        query.whereEqualTo(ItemList.ITEM_KEY_NAME, name);
				        query.whereContainedIn(ItemList.ITEM_KEY_LOCATION, ItemDetails.mStoreList);
				        query.setLimit(5);
				        try {
							List<ParseObject> itemList = query.find();
							doneColor(itemList);
						} catch (ParseException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}

					  return v;
					}
					
				};
	    mListView.setAdapter(mItemAdapter);
	}
	
	public void doneColor(List<ParseObject> itemList) {
    	double minPrice = mPriceColor, minUnitPrice = mUnitPriceColor;
       
    	for(ParseObject item : itemList) {
    		List<Double> priceList = item.getList(ITEM_KEY_PRICE);
    		Number price = priceList.get(priceList.size()-1);
    		Double quantity = item.getDouble(ITEM_KEY_QUANTITY);
    		if(price.doubleValue() < minPrice) {
    			minPrice = price.doubleValue();
    		}
    		if(price.doubleValue()/quantity < minUnitPrice) {
    			minUnitPrice = price.doubleValue()/quantity;
    		}
        }          
    	if(mPriceColor <= minPrice) {
    		mPriceView.setTextColor(Color.GREEN);
    	}
    	else {
    		mPriceView.setTextColor(Color.RED);
    	}
    	if(mUnitPriceColor <= minUnitPrice) {
    		mUnitPriceView.setTextColor(Color.GREEN);
    	}
    	else {
    		mUnitPriceView.setTextColor(Color.RED);
    	}
    }
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch(item.getItemId()) {
		case R.id.menu_additem:
			addItem();
			break;
		case R.id.menu_favorites:
			this.mFavorites = !this.mFavorites;
			if(this.mFavorites) {
				item.setIcon(R.drawable.rate_star_med_on);
			}
			else {
				item.setIcon(R.drawable.rate_star_med_off);
			}
			
			updateItemListAdapter();
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
	
	public void addLocation() {
		AddLocationDialogFragment addLocationDialog = new AddLocationDialogFragment();
		addLocationDialog.show(getSupportFragmentManager(), "dialog");
	}
	
	public void addFavorite(View view) {
		CheckBox checkbox = (CheckBox)view;
		String itemId = (String) checkbox.getTag();
		Log.d("Check", "Check: " + itemId);
		if(checkbox.isChecked()) {
			Login.mCurrentUser.addUnique(USER_KEY_FAVORITES, itemId);
		}
		else {
			Login.mCurrentUser.removeAll(USER_KEY_FAVORITES, Arrays.asList(itemId));
		}
		Login.mCurrentUser.saveInBackground();
	}
	
	public void updateItem(View view) {
		Button update = (Button) view;
		Update info = (Update) update.getTag();
		ItemList.mItemName = info.name;
		ItemList.mQuantity = info.quantity;
		ItemList.mItemId = info.id;
		AddItemDialogFragment addItemDialog = new AddItemDialogFragment();
		addItemDialog.show(getSupportFragmentManager(), "dialog");
	}
	
	@Override
	public void onDialogPositiveClick(SherlockDialogFragment dialog, ShoppingItem sItem, boolean update) {
		if(sItem.name != null && sItem.price > 0 && sItem.quantity > 0 && sItem.location != null) {
			if(update) {
				ParseQuery<ParseObject> query = ParseQuery.getQuery(ItemList.ITEM_CLASS);
				this.mSItem = sItem;
				query.getInBackground(ItemList.mItemId, new GetCallback<ParseObject>() {
				  public void done(ParseObject itemEntry, ParseException e) {
				    if (e == null) {			      
				    	itemEntry.put(ItemList.ITEM_KEY_NAME, mSItem.name);
						itemEntry.add(ItemList.ITEM_KEY_PRICE, mSItem.price);
						itemEntry.put(ItemList.ITEM_KEY_QUANTITY, mSItem.quantity);
						itemEntry.add(ItemList.ITEM_KEY_DATE, mSItem.date);
						itemEntry.saveInBackground(new SaveCallback() {
							   public void done(ParseException e) {
							     if (e == null) {
							    	 mListView.setAdapter(mItemAdapter);
							     }
							     else {
							    	 Log.d("Error", e.getMessage() + " " + Integer.toString(e.getCode()));
							     }
							   }
						});	      
				    }
				  }
				});
			}
			else {
				ParseObject itemEntry = new ParseObject(ItemList.ITEM_CLASS);
				itemEntry.put(ItemList.ITEM_KEY_NAME, sItem.name);
				itemEntry.add(ItemList.ITEM_KEY_PRICE, sItem.price);
				itemEntry.put(ItemList.ITEM_KEY_QUANTITY, sItem.quantity);
				itemEntry.put(ItemList.ITEM_KEY_LOCATION, mParseLocation);
				itemEntry.add(ItemList.ITEM_KEY_DATE, sItem.date);
				itemEntry.saveInBackground(new SaveCallback() {
					   public void done(ParseException e) {
					     if (e == null) {
					    	 mListView.setAdapter(mItemAdapter);
					     }
					     else {
					    	 Log.d("Error", e.getMessage() + " " + Integer.toString(e.getCode()));
					     }
					   }
				});
			}
		}
	}

	@Override
	public void onDialogNegativeClick(SherlockDialogFragment dialog) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDialogPositiveClick(SherlockDialogFragment dialog,
			ShoppingLocation sLocation) {
		if(sLocation.place != null & sLocation.address != null && sLocation.location != null) {
			ParseObject locationEntry = new ParseObject(ItemList.LOCATION_CLASS);
			locationEntry.put(ItemList.LOCATION_KEY_PLACE, sLocation.place);
			locationEntry.put(ItemList.LOCATION_KEY_ADDRESS, sLocation.address);
			locationEntry.put(ItemList.LOCATION_KEY_GEOPOINT, sLocation.location);
			mParseLocation = locationEntry;
			mNewLocations.add(mParseLocation);
			
			locationEntry.saveInBackground(new SaveCallback() {
			   public void done(ParseException e) {
			     if (e == null) {
			    	 supportInvalidateOptionsMenu();
			     }
			   }
			});
			
		}
	}
	
	private class Update {
		public String name, quantity, id;
		
		public Update(String name, String quantity, String id) {
			this.name = name;
			this.quantity = quantity;
			this.id = id;
		}
	}
}
