/**
 * Copyright (C) 2009 - 2013 SC 4ViewSoft SRL
 *  
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *  
 *      http://www.apache.org/licenses/LICENSE-2.0
 *  
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package com.example.finalproject;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.achartengine.ChartFactory;
import org.achartengine.GraphicalView;
import org.achartengine.chart.PointStyle;
import org.achartengine.chart.TimeChart;
import org.achartengine.model.TimeSeries;
import org.achartengine.model.XYMultipleSeriesDataset;
import org.achartengine.model.XYSeries;
import org.achartengine.renderer.XYMultipleSeriesRenderer;
import org.achartengine.renderer.XYSeriesRenderer;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Paint.Align;

/**
 * Average temperature demo chart.
 */
public class PriceChart {
 
	private XYMultipleSeriesDataset mDataset;
    private XYMultipleSeriesRenderer mRenderer;
    List<double[]> values = new ArrayList<double[]>();
    private GraphicalView mChartView;
    private TimeSeries time_series;
	
  public GraphicalView execute(Context context) {
	  mDataset = new XYMultipleSeriesDataset();
      mRenderer = new XYMultipleSeriesRenderer();
      mRenderer.setAxisTitleTextSize(16);
      mRenderer.setChartTitleTextSize(20);
      mRenderer.setLabelsTextSize(15);
      mRenderer.setLegendTextSize(15);
      mRenderer.setPointSize(3f);
      
      XYSeriesRenderer r = new XYSeriesRenderer();
      r.setColor(Color.GREEN);
      r.setPointStyle(PointStyle.CIRCLE);
      r.setFillPoints(true);
      mRenderer.addSeriesRenderer(r);
      mRenderer.setPanEnabled(true, true);
      mRenderer.setZoomEnabled(true, true);
      mRenderer.setXTitle("Time");
      mRenderer.setYTitle("Price ($)");
      mRenderer.setZoomButtonsVisible(true);
      mRenderer.setShowGrid(true);
      mRenderer.setApplyBackgroundColor(true);
      mRenderer.setBackgroundColor(Color.BLACK);
      
      time_series = new TimeSeries(ItemDetails.mStoreName);

      mDataset.addSeries(time_series);

      fillData();
      
      mChartView = ChartFactory.getTimeChartView(context, mDataset, mRenderer,
              "MM/dd/yyyy");

      return mChartView;
  }
  
  private void fillData() {
      int i = 0;
      for(String date : ItemDetails.mDates) {
    	  try {
    		Number y = ItemDetails.mPrices.get(i);
			time_series.add(new SimpleDateFormat("MM/dd/yyyy").parse(date), y.doubleValue());
    	  } catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
    	  }
    	  i++;
      }
  }

}
