package com.example.diegogutierrez.sensor;

import com.github.mikephil.charting.charts.BarChart;
import com.github.mikephil.charting.data.BarData;
import com.github.mikephil.charting.data.BarDataSet;
import com.github.mikephil.charting.data.BarEntry;
import com.github.mikephil.charting.formatter.IndexAxisValueFormatter;

import java.util.ArrayList;
import java.util.Collections;

public class Statistics {

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

    public HistogramData getHistogramData(ArrayList<Double> data, int n) {

        double nWidth = 1 / n;

        ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
        ArrayList<String> labels = new ArrayList<String>();

        ArrayList<Double> zScores = getZScores(data);

        int lowest = (int) Math.floor(zScores.get(0) / nWidth);
        int highest = (int) Math.ceil(zScores.get(zScores.size() - 1) / n);

        int[] values = new int[highest - lowest];

        for(float i = 0; i < zScores.size(); i++) {
            int index = (int) i;
            int lower = (int) Math.floor(zScores.get(index) / nWidth);
            values[lower - lowest]++;
        }

        for(float j = 0; j < values.length; j++) {
            int index = (int) j;
            entries.add(new BarEntry(j, values[index]));
            labels.add((lowest + nWidth * j) + "");
        }

        HistogramData histogramData = new HistogramData(entries, labels);
        return histogramData;

    }

}