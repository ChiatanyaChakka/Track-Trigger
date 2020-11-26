package com.example.loginpage;

import android.content.Context;
import android.widget.ArrayAdapter;

import androidx.annotation.NonNull;

import java.util.List;

public class AdapterForAppliances  extends ArrayAdapter<String> {
    private List<String> time;
    Context context;
    int layout;
    private List<String> date;
    private  List<String> description;

    public AdapterForAppliances(@NonNull Context context, int resource, List<String> time, List<String> date, List<String> description) {
        super(context, resource, time);
        this.time = time;
        this.layout = resource;
        this.date = date;
        this.description = description;
    }
}
