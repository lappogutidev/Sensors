package com.example.diegogutierrez.sensor;

import com.github.mikephil.charting.data.BarEntry;

import java.util.ArrayList;

public class HistogramData {

    private ArrayList<BarEntry> entries = new ArrayList<BarEntry>();
    private ArrayList<String>   labels  = new ArrayList<String>();

    public HistogramData(ArrayList<BarEntry> entries, ArrayList<String>   labels) {
        this.entries = entries;
        this.labels = labels;
    }

    public ArrayList<BarEntry> getEntries() {
        return entries;
    }

    public ArrayList<String> getLabels() {
        return labels;
    }

}
