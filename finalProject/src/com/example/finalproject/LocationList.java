package com.example.finalproject;

import java.util.ArrayList;
import java.util.List;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.AdapterView.OnItemClickListener;

import com.actionbarsherlock.app.ActionBar;
import com.actionbarsherlock.app.SherlockActivity;

import com.parse.Parse;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseQueryAdapter;

public class LocationList extends SherlockActivity {

	private ListView mListView;
	private ParseQueryAdapter<ParseObject> mLocationAdapter;
	
	@Override
    public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		Parse.initialize(this, "RCIm6xY3zgCw0sguu3OtbE0e1aIdd7dBjqhnNrQV", "kwjP5rVdVPPBlSf3PljblCeewigUmTUbto0GKwG4");
		setContentView(R.layout.location_list);
		
		
		//mItemAdapter.setTextKey(ItemList.ITEM_KEY_NAME);
		
		mListView = (ListView) findViewById(R.id.locationlist);
		mListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				ParseObject object = mLocationAdapter.getItem(position);
				ItemList.mParseLocation = object;
				ItemList.mNewLocations.add(0, object);
				setResult(RESULT_OK);
				finish();
			}
			
		});
		updateLocationListAdapter();
	}
	
	protected void updateLocationListAdapter() {
		
		mLocationAdapter =
				  new ParseQueryAdapter<ParseObject>(this, new ParseQueryAdapter.QueryFactory<ParseObject>() {
				    public ParseQuery<ParseObject> create() {
				      // Here we can configure a ParseQuery to our heart's desire.
					      ParseQuery query = new ParseQuery(ItemList.LOCATION_CLASS);
					      return query;
					    }
				  }) {
				@Override
				public View getItemView(ParseObject object, View v, ViewGroup parent) {
				  if (v == null) {
				    v = View.inflate(getContext(), R.layout.location_view, null);
				  }

				 
				  // Do additional configuration before returning the View.
					  TextView locationName = (TextView) v.findViewById(R.id.textView_place);
					  TextView addressView = (TextView) v.findViewById(R.id.textView_address);
				      locationName.setText(object.getString(ItemList.LOCATION_KEY_PLACE));
					  addressView.setText(object.getString(ItemList.LOCATION_KEY_ADDRESS));
					  return v;
					}
					
				};
	    mListView.setAdapter(mLocationAdapter);
	}
	
	
}
