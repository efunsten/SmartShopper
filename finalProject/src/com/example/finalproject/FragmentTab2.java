package com.example.finalproject;


import org.achartengine.GraphicalView;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.actionbarsherlock.app.SherlockFragment;
 
public class FragmentTab2 extends SherlockFragment {

	public GraphicalView mChartView;
	private AverageTemperatureChart mChart = new AverageTemperatureChart();
	
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragmenttab2, container, false);
        
       
        LinearLayout layout = (LinearLayout) rootView.findViewById(R.id.chart);
        mChartView = mChart.execute(getActivity());
        layout.addView(mChartView, new LayoutParams(LayoutParams.FILL_PARENT, LayoutParams.FILL_PARENT));
      
        return rootView;
    }
 
}