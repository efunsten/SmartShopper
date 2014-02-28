package com.example.finalproject;

import com.parse.Parse;
import com.parse.ParseAnalytics;
import com.parse.ParseObject;

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

public class ItemDetails extends SherlockFragmentActivity implements 
		GooglePlayServicesClient.ConnectionCallbacks,
		GooglePlayServicesClient.OnConnectionFailedListener{

	ActionBar.Tab Tab1, Tab2;
	Fragment fragmentTab1 = new FragmentTab1();
	Fragment fragmentTab2 = new FragmentTab2();
	private LocationClient mLocationClient;
	private Location mLocation;
	ShoppingItem mItem = null;
	
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		ActionBar actionBar = getSupportActionBar();
		actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);
		actionBar.setDisplayShowTitleEnabled(false);
		
		Tab1 = actionBar.newTab().setText("Tab1");
		Tab2 = actionBar.newTab().setText("Tab2");
		
		Tab1.setTabListener(new TabListener(fragmentTab1));
		Tab2.setTabListener(new TabListener(fragmentTab2));
		
		actionBar.addTab(Tab1);
		actionBar.addTab(Tab2);
		
		mLocationClient = new LocationClient(this, this, this);
		Parse.initialize(this, "RCIm6xY3zgCw0sguu3OtbE0e1aIdd7dBjqhnNrQV", "kwjP5rVdVPPBlSf3PljblCeewigUmTUbto0GKwG4");
		Intent intent = getIntent();
		Bundle extras = intent.getExtras();
		if(extras != null) {
			mItem = new Gson().fromJson(extras.getString(ItemList.ITEM_CLASS), ShoppingItem.class);
			if(mItem != null)
				Log.d("Item", mItem.name);
		}
	}
	
	public void addItem(View view) {
		/**/
	}
	
	public void getLocation(View view) {
		this.mLocation = mLocationClient.getLastLocation();
		(new GetAddressTask(this)).execute(this.mLocation);
		
	}
	
	private class GetAddressTask extends AsyncTask<Location, Void, String> {

		Context mContext;
		
	    public GetAddressTask(Context context) {
	        super();
	        mContext = context;
	    }

	    @Override
        protected String doInBackground(Location... params) {
            Geocoder geocoder =
                    new Geocoder(mContext, Locale.getDefault());
            // Get the current location from the input parameter list
            Location loc = params[0];
            // Create a list to contain the result address
            List<Address> addresses = null;
            try {
                /*
                 * Return 1 address.
                 */
                addresses = geocoder.getFromLocation(loc.getLatitude(),
                        loc.getLongitude(), 1);
            } catch (IOException e1) {
            Log.e("LocationSampleActivity",
                    "IO Exception in getFromLocation()");
            e1.printStackTrace();
            return ("IO Exception trying to get address");
            } catch (IllegalArgumentException e2) {
            // Error message to post in the log
            String errorString = "Illegal arguments " +
                    Double.toString(loc.getLatitude()) +
                    " , " +
                    Double.toString(loc.getLongitude()) +
                    " passed to address service";
            Log.e("LocationSampleActivity", errorString);
            e2.printStackTrace();
            return errorString;
            }
            // If the reverse geocode returned an address
            if (addresses != null && addresses.size() > 0) {
                // Get the first address
                Address address = addresses.get(0);
                /*
                 * Format the first line of address (if available),
                 * city, and country name.
                 */
                String addressText = String.format(
                        "%s, %s, %s",
                        // If there's a street address, add it
                        address.getMaxAddressLineIndex() > 0 ?
                                address.getAddressLine(0) : "",
                        // Locality is usually a city
                        address.getLocality(),
                        // The country of the address
                        address.getCountryName());
                // Return the text
                return addressText;
            } else {
                return "No address found";
            }
        }
		
		@Override
		protected void onPostExecute(String address) {
			TextView myAddress = (TextView) findViewById(R.id.gps_address);
			myAddress.setText(address);
		}
		
		
	}
	
	public void startScan(View view) {
		Intent intent = new Intent("com.google.zxing.client.android.SCAN");
		intent.putExtra("com.google.zxing.client.android.SCAN.SCAN_MODE", "ALL_CODE_TYPES");
		startActivityForResult(intent, 0);
	}
	
	public void onActivityResult(int requestCode, int resultCode, Intent intent) {
		if(requestCode == 0) {
			if(resultCode == RESULT_OK) {
				String contents = intent.getStringExtra("SCAN_RESULT");
				String format = intent.getStringExtra("SCAN_RESULT_FORMAT");
				TextView myCode = (TextView)findViewById(R.id.scan_code);
				myCode.setText(contents);
			}
			else if(resultCode == RESULT_CANCELED) {
				
			}
		}
	}
	
	public void startJokes(View view) {
		Intent intent = new Intent("edu.calpoly.android.lab4.AdvancedJokeList");
		try {
		    startActivity(intent);
		} 
		catch (ActivityNotFoundException e) {
		    Toast.makeText(this, 
		        "No APP", 
		        Toast.LENGTH_SHORT).show();
		}
	}
	
	@Override
    protected void onStart() {
        super.onStart();
        // Connect the client.
        mLocationClient.connect();
    }
    
    @Override
    protected void onStop() {
        // Disconnecting the client invalidates it.
        mLocationClient.disconnect();
        super.onStop();
    }

	@Override
	public void onConnectionFailed(ConnectionResult arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onConnected(Bundle arg0) {
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onDisconnected() {
		// TODO Auto-generated method stub
		
	}
}
