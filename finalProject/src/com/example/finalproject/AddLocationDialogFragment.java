package com.example.finalproject;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.util.Log;
import android.view.ContextThemeWrapper;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockActivity;
import com.actionbarsherlock.app.SherlockDialogFragment;
import com.parse.ParseGeoPoint;

public class AddLocationDialogFragment extends SherlockDialogFragment{

	EditText mPlaceText;
	EditText mAddressText;
	Geocoder mGeocoder;
	
	public interface NoticeDialogListener {
        public void onDialogPositiveClick(SherlockDialogFragment dialog, ShoppingLocation sLocation);
        public void onDialogNegativeClick(SherlockDialogFragment dialog);
    }
	
	NoticeDialogListener mListener;
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		try {
            // Instantiate the NoticeDialogListener so we can send events to the host
            mListener = (NoticeDialogListener) activity;
        } catch (ClassCastException e) {
            // The activity doesn't implement the interface, throw exception
            throw new ClassCastException(activity.toString()
                    + " must implement NoticeDialogListener");
        }
	}

	@Override
	public Dialog onCreateDialog(Bundle savedInstanceState) {
		AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
				//new ContextThemeWrapper(getActivity(), R.style.CustomDialog));
		builder.setInverseBackgroundForced(true);
		LayoutInflater inflater = getSherlockActivity().getLayoutInflater();
		View modifyView = inflater.inflate(R.layout.dialog_addlocation, null);
		builder.setView(modifyView);
		
		mPlaceText = (EditText) modifyView.findViewById(R.id.editText_place);
		mAddressText = (EditText) modifyView.findViewById(R.id.editText_address);
		Log.d("Location", ItemList.mLocation.toString());
		mAddressText.setText(GetAddressTask.mAddress);
		mGeocoder = new Geocoder(getActivity());
	
		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					  
					List<Address> addresses = null;
					double latitude = 200, longitude = 200;
					
					try {
						addresses = mGeocoder.getFromLocationName(mAddressText.getText().toString(), 1);
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					if(addresses == null || addresses.size() > 0) {
					    latitude = addresses.get(0).getLatitude();
					    longitude = addresses.get(0).getLongitude();
					}
					
					try {
						mListener.onDialogPositiveClick(AddLocationDialogFragment.this, 
								new ShoppingLocation(mPlaceText.getText().toString(), 
												 mAddressText.getText().toString(), 
												 new ParseGeoPoint(latitude, longitude)));
					} catch(Exception e) {
						
					}
					
				}
			})
			.setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					// TODO Auto-generated method stub
					
				}
			});
			return builder.create();
	}
	
}
