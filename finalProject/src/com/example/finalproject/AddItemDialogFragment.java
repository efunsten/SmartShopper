package com.example.finalproject;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
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

public class AddItemDialogFragment extends SherlockDialogFragment{

	EditText mItemText;
	EditText mPriceText;
	EditText mQuantityText;
	TextView mDateView;
	
	public interface NoticeDialogListener {
        public void onDialogPositiveClick(SherlockDialogFragment dialog, ShoppingItem sItem);
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
		View modifyView = inflater.inflate(R.layout.dialog_additem, null);
		builder.setView(modifyView);
		mDateView = (TextView) modifyView.findViewById(R.id.textView_date);
		String date = new SimpleDateFormat("MM/dd/yyyy", Locale.US).format(new Date());
		mDateView.setText(date);
		mItemText = (EditText) modifyView.findViewById(R.id.editText_item);
		mPriceText = (EditText) modifyView.findViewById(R.id.editText_price);
		mQuantityText = (EditText) modifyView.findViewById(R.id.editText_quantity);
		
		builder.setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
				
				@Override
				public void onClick(DialogInterface dialog, int which) {
					
					mListener.onDialogPositiveClick(AddItemDialogFragment.this, 
							new ShoppingItem(mDateView.getText().toString(), 
											 mItemText.getText().toString(), 
											 Float.parseFloat(mPriceText.getText().toString()),
											 Float.parseFloat(mQuantityText.getText().toString()),
											 new ParseGeoPoint(ItemList.mLocation.getLatitude(), ItemList.mLocation.getLongitude())));
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
