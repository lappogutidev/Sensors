package com.example.diegogutierrez.sensor;

import android.app.Activity;

import android.hardware.SensorEventListener;
import android.hardware.SensorManager;

import android.os.Bundle;

import android.view.View;

import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;

public class MainActivity extends Activity implements SensorEventListener {

    private Sensor accelerometer;
    private double xAcceleration = 0d;

    private ArrayList<Double> xAccelerationArray = new ArrayList<Double>();
    private SensorManager sensorManager;

    private BarChart chart1, chart2, chart3;

    private Statistics statistics = new Statistics();

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // load the layout
        setContentView(R.layout.activity_main);

        chart1 = findViewById(R.id.chart1);
        chart2 = findViewById(R.id.chart2);
        chart3 = findViewById(R.id.chart3);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, 1000);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        xAcceleration = event.values[0];
        xAccelerationArray.add(xAcceleration);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public void refresh(View view) {

        HistogramData histogramData = statistics.getHistogramData(xAccelerationArray, 1);

        chart1.invalidate();

        BarDataSet dataSet = new BarDataSet(histogramData.getEntries(), "X Acceleration");
        BarData barData = new BarData(dataSet);
        chart1.setData(barData);
        chart1.setFitBars(true);
        chart1.getXAxis().setValueFormatter(new IndexAxisValueFormatter(histogramData.getLabels()));

    }

}
