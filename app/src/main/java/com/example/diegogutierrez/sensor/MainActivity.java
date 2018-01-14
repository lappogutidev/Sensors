package com.example.diegogutierrez.sensor;

import android.app.Activity;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TableLayout;
import android.widget.TextView;
import android.hardware.Sensor;
import android.hardware.SensorEvent;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.charts.LineChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.data.Entry;
import com.github.mikephil.charting.data.LineData;
import com.github.mikephil.charting.data.LineDataSet;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Random;

/**
 * Created by diegogutierrez on 1/12/18.
 */


public class MainActivity extends Activity implements SensorEventListener {

    private Sensor accelerometer;
    private double xAcceleration = 0d;
    private double max = 0d;
    private ArrayList<Double> xAccelerationArray = new ArrayList<Double>();
    private SensorManager sensorManager;
    private TextView value1, label1, value2, label2, value3, label3;
    private int count;
    private BarChart chart1, chart2, chart3;
    private Button refreshButton;
    private TableLayout tableLayout;
    private ArrayList<Double> test = new ArrayList<Double>();

    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        // load the layout
        setContentView(R.layout.activity_main);

        test.add(6d);
        test.add(2d);
        test.add(1d);
        test.add(4d);
        test.add(5d);
        test.add(3d);

        chart1 = findViewById(R.id.chart1);
        chart2 = findViewById(R.id.chart2);
        chart3 = findViewById(R.id.chart3);

        refreshButton = findViewById(R.id.refreshButton);

        value1 = findViewById(R.id.value1);
        value2 = findViewById(R.id.value2);
        value3 = findViewById(R.id.value3);

        label1 = findViewById(R.id.label1);
        label2 = findViewById(R.id.label2);
        label3 = findViewById(R.id.label3);

        tableLayout = findViewById(R.id.tableLayout);

        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        accelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, accelerometer, 1000);

        label1.setText("X Acceleration");

//        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
//
//        Random random = new Random();
//        for(float i = 0; i < 20; i++) {
//            float value = random.nextFloat() * 100;
//            entries.add(new BarEntry(i, value));
//        }
//
//        BarDataSet dataSet = new BarDataSet(entries, "X Acceleration");
//        BarData barData = new BarData(dataSet);
//        chart1.setData(barData);
//        chart1.setFitBars(true);

    }

    @Override
    public void onSensorChanged(SensorEvent event) {
        xAcceleration = event.values[0];
        if (xAcceleration > max) {
            max = xAcceleration;
        }
        xAccelerationArray.add(xAcceleration);
        count++;
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }

    public static int inbetween(float lower, float upper, ArrayList<Double> data) {
        int count = 0;
        for(int i = 0; i < data.size(); i++) {
            if (lower <= data.get(i) && data.get(i) < upper) {
                count++;
            }
        }
        return count;
    }

    public double getMean(ArrayList<Double> data) {
        int count = data.size();
        double total = 0;
        for(double i : data) {
            total += i;
        }
        return total/count;
    }

    public double getMedian(ArrayList<Double> data) {
        int size = data.size();
        double median = data.get(size / 2);
        if(size % 2 == 0) {
            int upper = (size + 1) / 2;
            int lower = (size - 1) / 2;

            median = (data.get(upper) + data.get(lower)) / 2;
        }
        return median;
    }

    public double getStandardDeviation(ArrayList<Double> data) {
        double mean = getMean(data);
        int size = data.size();
        double variance = 0;
        for(double i : data) {
            variance += Math.pow((i - mean),2);
        }
        variance /= size;
        double standardDeviation = Math.pow(variance, 0.5);
        return standardDeviation;
    }

    public double getStandardDeviation(ArrayList<Double> data, double mean) {
        int size = data.size();
        double variance = 0;
        for(double i : data) {
            variance += Math.pow((i - mean),2);
        }
        variance /= size;
        double standardDeviation = Math.pow(variance, 0.5);
        return standardDeviation;
    }

    public double getZScore(ArrayList<Double> data, double value) {
        double mean = getMean(data);
        double standardDeviation = getStandardDeviation(data);
        double zScore = (value - mean) / standardDeviation;
        return zScore;
    }

    public double getZScore(double mean, double standardDeviation, double value) {
        double zScore = (value - mean) / standardDeviation;
        return zScore;
    }

    public ArrayList<Double> getZScores(ArrayList<Double> data) {
        int size = data.size();
        double mean = getMean(data);
        double standardDeviation = getStandardDeviation(data, mean);
        ArrayList<Double> zScores = new ArrayList<Double>();
        for(double i : data) {
            zScores.add(getZScore(mean,standardDeviation,i));
        }
        Collections.sort(zScores);
        return zScores;
    }

    public int[] getCount(ArrayList<Double> sortedZScores, int spacing) {

        double space = 1d / spacing;

        int lowest = (int) Math.floor(sortedZScores.get(0) / space);
        int highest = (int) Math.ceil(sortedZScores.get(sortedZScores.size() - 1) / space);

        int[] values = new int[highest - lowest];

        for(double i : sortedZScores) {
            int lower = (int) Math.floor(i / space);
            values[lower - lowest]++;
        }

        return values;
    }

    public String printArray(int[] array) {
        String response = "";
        for(int i = 0; i < array.length; i++) {
            response += "\n" + array[i];
        }
        return response;
    }

    public void createBar(BarChart chart, int[] data, double lowest, int spacing) {

        double space = 1 / spacing;

        chart.invalidate();

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        ArrayList<String> labels = new ArrayList<String>();

        for(float i = 0; i < data.length; i++) {
            int index = (int) i;
            int lower = (int) Math.floor(data[index] / space);
            labels.add(i+"");
            entries.add(new BarEntry(i, data[index]));

        }

        BarDataSet dataSet = new BarDataSet(entries, "X Acceleration");
        BarData barData = new BarData(dataSet);
        chart.setData(barData);
        chart.getXAxis().setValueFormatter(new IndexAxisValueFormatter(labels));
        chart.setFitBars(true);

    }

    public double round(int places, double value) {
        long number = (long) Math.pow(10, places);
        return Math.round (value * number) / number;
    }

    public void refresh(View view) {

        int round = 2;

        ArrayList<Double> using = xAccelerationArray;

        Collections.sort(using);

        double mean = getMean(using);
        double median = getMedian(using);
        double standardDeviation = getStandardDeviation(using);

        ArrayList<Double> zScores = getZScores(using);

        int[] array = getCount(zScores, 2);

        createBar(chart1, array, array[0], 2);
        DecimalFormat df = new DecimalFormat("###.###");

        label1.setText("mean: ");
        value1.setText("" + df.format(mean));

        label2.setText("median: ");
        value2.setText("" + df.format(median));

        label3.setText("standard deviation: ");
        value3.setText("" + df.format(standardDeviation));

    }

}
