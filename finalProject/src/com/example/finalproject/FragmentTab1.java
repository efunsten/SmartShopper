package com.example.finalproject;

import android.content.Intent;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;

import com.actionbarsherlock.app.SherlockFragment;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
 
public class FragmentTab1 extends SherlockFragment {
	public View rootView;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        rootView = inflater.inflate(R.layout.fragmenttab1, container, false);
        
        ItemDetails.mItem.getParseObject(ItemList.ITEM_KEY_LOCATION)
		.fetchIfNeededInBackground(new GetCallback<ParseObject>() {
	        public void done(ParseObject object, ParseException e) {
	            ItemDetails.mStore = object;
	            ItemDetails.mStoreName = ItemDetails.mStore.getString(ItemList.LOCATION_KEY_PLACE);
	            TextView store = (TextView) rootView.findViewById(R.id.textView_title);
	            store.setText(ItemDetails.mStoreName);
	          }
	      });
        
    	TableRow firstRow = (TableRow) View.inflate(getSherlockActivity(), R.layout.table_row1, null);
    	TableLayout table1 = (TableLayout) rootView.findViewById(R.id.tableLayout1);
        table1.addView(firstRow);
        
        TableRow row;
        TextView date;
        TextView price;
        TextView pricePerUnit;
        int count = 0;
        for(int i=ItemDetails.mDates.size() - 1; i >= 0 && count < 5; i--) {
        	row = (TableRow) View.inflate(getSherlockActivity(), R.layout.table_row1, null);
            date = (TextView) row.findViewById(R.id.row_date);
            price = (TextView) row.findViewById(R.id.row_price);
            pricePerUnit = (TextView) row.findViewById(R.id.row_priceperunit);
            date.setText(ItemDetails.mDates.get(i));
            Number priceVal = ItemDetails.mPrices.get(i);
            price.setText(Double.toString(priceVal.doubleValue()));
            pricePerUnit.setText(Double.toString(priceVal.doubleValue() / ItemDetails.mQuantity));
            table1.addView(row);
            count++;
        }
        
        firstRow = (TableRow) View.inflate(getSherlockActivity(), R.layout.table_row2, null);
    	TableLayout table2 = (TableLayout) rootView.findViewById(R.id.tableLayout2);
        table2.addView(firstRow);
        
        TextView store;
        TextView quantity;
        
        count = 0;
        /*
        for(int i=ItemDetails.mDates.size() - 1; i >= 0 && count < 5; i--) {
        	row = (TableRow) View.inflate(getSherlockActivity(), R.layout.table_row2, null);
            store = (TextView) row.findViewById(R.id.row_store);
            quantity = (TextView) row.findViewById(R.id.row_quantity);
            price = (TextView) row.findViewById(R.id.row_price2);
            pricePerUnit = (TextView) row.findViewById(R.id.row_priceperunit2);
            store.setText();
            quantity.setText();
            Number priceVal = ItemDetails.mPrices.get(i);
            price.setText(Double.toString(priceVal.doubleValue()));
            pricePerUnit.setText(Double.toString(priceVal.doubleValue() / ItemDetails.mQuantity));
            table1.addView(row);
            count++;
        }*/
        
        
        return rootView;
    }
}