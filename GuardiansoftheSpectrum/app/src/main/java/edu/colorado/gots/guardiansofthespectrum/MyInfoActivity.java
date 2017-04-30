package edu.colorado.gots.guardiansofthespectrum;

import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.components.Description;
import com.github.mikephil.charting.components.Legend;
import com.github.mikephil.charting.components.XAxis;
import com.github.mikephil.charting.components.YAxis;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.utils.ColorTemplate;

import java.util.ArrayList;
import java.util.List;

// Local visualization is done using a free tool MPAnroidChart
// https://github.com/PhilJay/MPAndroidChart
// Bar chart code is following ListViewBarChartActivity.java

public class MyInfoActivity extends BaseActivity {
    private ListView my_listview;
    protected Typeface mTfLight;
    private  Typeface mTypeFaceLight;
    CSVFileManager csvManager;
    Button graphs_button;
    Button button_hardware;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_myinfo);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager
                .LayoutParams.FLAG_FULLSCREEN);

        // Setting up buttons.
        graphs_button = (Button) findViewById(R.id.button_graphs);
        graphs_button.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                ReadingGraphFragment GraphDialog = new ReadingGraphFragment();
                GraphDialog.show(getSupportFragmentManager(), "Graph_dialog");
            }
        });
        button_hardware = (Button) findViewById(R.id.button_hardware);
        button_hardware.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MyInfoActivity.this, HardwareInfo.class);
                startActivity(intent);
            }
        });

        // Bar graphs are set up within the Listview.
        // Data for the bars is generated in generate.Data()
        // Once the graphs are ready, they need to go into the ChartDataAdapter to set them into
        // the listview.
        my_listview = (ListView) findViewById(R.id.id_list_view);
        ArrayList<BarData> list = new ArrayList<BarData>();
        list.add(generateData(1, "Effective Cell Connection Strength in dBm."));
        list.add(generateData(2, "Effective WiFi Connection Strength in dBm."));
        ChartDataAdapter my_adapter = new ChartDataAdapter(getApplicationContext(), list);
        my_listview.setAdapter(my_adapter);
    }



    private class ChartDataAdapter extends ArrayAdapter<BarData>{
        public ChartDataAdapter(Context context, List<BarData> objects){
            super(context, 0, objects);
        }
        @Override
        public View getView(int position, View convertView, ViewGroup parent){
            BarData data = getItem(position);
            ViewHolder holder = null;
            if (convertView == null) {
                holder = new ViewHolder();
                convertView = LayoutInflater.from(getContext()).inflate(
                        R.layout.list_item_barchart, null);
                holder.chart = (BarChart) convertView.findViewById(R.id.chart);
                convertView.setTag(holder);
            }
            else {
                holder = (ViewHolder) convertView.getTag();
            }
            // apply styling.
            // MPAndroid charts does not support X and Y axis labels.
            // "About Graphs" button explains Y - signal strength, X - time of measurement.
            data.setValueTypeface(mTfLight);
            data.setValueTextColor(Color.BLACK);

            Description descriptionX = new Description();
            descriptionX.setText("Time");
            holder.chart.setDescription(descriptionX);
            holder.chart.getDescription().setEnabled(true);

//            Description descriptionY = new Description();
//            descriptionY.setText("Signal Strength");
//            holder.chart.setDescription(descriptionY);
//            holder.chart.getDescription().setEnabled(true);


            Legend legend = holder.chart.getLegend();
            legend.setFormSize(10f); // set the size of the legend forms/shapes
            legend.setForm(Legend.LegendForm.CIRCLE); // set what type of form/shape should be used
            legend.setTextSize(12f);
            legend.setTextColor(Color.BLACK);
            legend.setXEntrySpace(5f); // set the space between the legend entries on the x-axis
            legend.setYEntrySpace(5f); // set the space between the legend entries on the y-axis


            holder.chart.setDrawGridBackground(false);
            XAxis xAxis = holder.chart.getXAxis();
            //xAxis.setLabelRotationAngle(-45);
            holder.chart.getXAxis().setPosition(XAxis.XAxisPosition.TOP);
            holder.chart.getXAxis().setPosition(XAxis.XAxisPosition.BOTTOM);
            xAxis.setTypeface(mTfLight);
            xAxis.setDrawGridLines(false);
            xAxis.setGranularity(1f); // one hour
            holder.chart.setDragEnabled(true);
            holder.chart.setScaleEnabled(true);
            holder.chart.setHighlightPerDragEnabled(true);
            holder.chart.setBackgroundColor(Color.WHITE);
            holder.chart.setNoDataText("No Data");

            YAxis leftAxis = holder.chart.getAxisLeft();
            leftAxis.setTypeface(mTfLight);
            leftAxis.setLabelCount(5, false);
            leftAxis.setSpaceTop(15f);
            leftAxis.setPosition(YAxis.YAxisLabelPosition.INSIDE_CHART);
            leftAxis.setTextColor(ColorTemplate.getHoloBlue());
            leftAxis.setDrawGridLines(true);
            leftAxis.setGranularityEnabled(true);

            leftAxis.setYOffset(-9f);
            leftAxis.setTextColor(Color.rgb(0, 0, 255));

            YAxis rightAxis = holder.chart.getAxisRight();
            rightAxis.setEnabled(false);

            // set data
            holder.chart.setData(data);
            holder.chart.setFitBars(true);

            // do not forget to refresh the chart
            holder.chart.invalidate();
            holder.chart.animateY(700);

            return convertView;
        }
        private class ViewHolder {

            BarChart chart;
        }
    }

    private BarData generateData(int cnt, String str) {

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        String data = "";
        String time = "";
        //float time_float = 0;
        int time_int = 0;
        float dbm; // cell signal strength.
        String ssid = ""; //service identifier
        float rssi; // wifi signal strength
        int count = 0;

        // Getting data from the CSV file written by CSVFileManager.java
        csvManager = new CSVFileManager(getApplicationContext());
        List<CSVFileManager.CSVEntry> csvData = csvManager.readData().getAllData();
        for (CSVFileManager.CSVEntry e : csvData) {
            time += String.format("", e.getTime());
            ssid = String.format("", e.getSsid());
            //time_float = Float.parseFloat(time);


            // Both signal strengths will be in the negative, which makes it hard to show in a bar
            // chart format. Because of this, we are adding values to the data and making a note
            // of that in MyInfoActivity "About Graphs" button.
            dbm = e.getDbm() - (-150);
            rssi = e.getRssi() - (-127);

            if (cnt == 2){
                    entries.add(new BarEntry(count, rssi));
            }
            else {
                    entries.add(new BarEntry(count, dbm));
            }
            count++;
        }
        BarDataSet d = new BarDataSet(entries, str);
        d.setColors(ColorTemplate.getHoloBlue());
        d.setBarShadowColor(Color.rgb(203, 203, 203));

        BarData cd = new BarData(d);
        return cd;
    }
}