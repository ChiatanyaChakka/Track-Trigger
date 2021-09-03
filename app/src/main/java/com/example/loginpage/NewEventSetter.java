package com.example.loginpage;

import android.Manifest;
import android.app.AlarmManager;
import android.app.DatePickerDialog;
import android.app.PendingIntent;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class NewEventSetter extends AppCompatActivity {
    TextView dateview;
    TextView timeview;
    Button timepicker;
    Button datepicker;
    Calendar eventTime, trigTime;
    Button save;
    private DatabaseReference rootref, userref, triggerref;
    private FirebaseAuth auth;
    private ArrayList<String> userdetails;
    private HashMap<String, Object> eventdetails;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_event_setter);
        ActivityCompat.requestPermissions(NewEventSetter.this, new String[]{Manifest.permission.SEND_SMS}, 1);

        timeview = findViewById(R.id.timeview);
        dateview = findViewById(R.id.dateview);
        datepicker = findViewById(R.id.datepicker);
        timepicker = findViewById(R.id.timepicker);
        eventTime = Calendar.getInstance();
        trigTime = Calendar.getInstance();
        userdetails = new ArrayList<String>();
        eventdetails = new HashMap<String, Object>();

        rootref = FirebaseDatabase.getInstance().getReference();
        auth = FirebaseAuth.getInstance();
        triggerref = rootref.child("Trigger").child(auth.getCurrentUser().getUid());
        userref = rootref.child("Users").child(auth.getCurrentUser().getUid());
        userref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    userdetails.add(snap.getValue().toString());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        datepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                DatePickerDialog datePickerDialog = new DatePickerDialog(NewEventSetter.this,
                        new DatePickerDialog.OnDateSetListener() {
                            @Override
                            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                                dateview.setText("" + dayOfMonth + " - " + (month + 1) + " - " + year + "");
                                eventTime.set(Calendar.DAY_OF_MONTH, dayOfMonth);
                                eventTime.set(Calendar.MONTH, month);
                                eventTime.set(Calendar.YEAR, year);
                            }
                        }, eventTime.get(Calendar.YEAR), eventTime.get(Calendar.MONTH), eventTime.get(Calendar.DAY_OF_MONTH));
                datePickerDialog.show();
            }
        });

        timepicker.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                TimePickerDialog timePickerDialog = new TimePickerDialog(NewEventSetter.this, new TimePickerDialog.OnTimeSetListener() {
                    @Override
                    public void onTimeSet(TimePicker view, int hourOfDay, int minute) {
                        timeview.setText(hourOfDay + ":" + minute);
                        eventTime.set(Calendar.MINUTE, minute);
                        eventTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
                    }
                }, eventTime.get(Calendar.HOUR_OF_DAY), eventTime.get(Calendar.MINUTE), false);

                timePickerDialog.show();
            }
        });

        save = findViewById(R.id.save);
        save.setOnClickListener(new View.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.KITKAT)
            @Override
            public void onClick(View v) {
                EditText desbox = findViewById(R.id.description);
                String description = desbox.getText().toString();

                trigTime = (Calendar) eventTime.clone();
                trigTime.add(Calendar.HOUR_OF_DAY, -1);
                System.out.println(trigTime.getTime() + " " + eventTime.getTime());

                AlarmManager alarmManager = (AlarmManager) getSystemService(ALARM_SERVICE);
                Intent i = new Intent(getApplication(), MyReceiver.class);
                i.putExtra("phone num", userdetails.get(0));
                i.putExtra("description", description);
                i.putExtra("event time", eventTime);
                i.putExtra("register time", Calendar.getInstance());
                PendingIntent broadcast = PendingIntent.getBroadcast(getApplicationContext(), 55, i, PendingIntent.FLAG_UPDATE_CURRENT);
                alarmManager.setExact(AlarmManager.RTC_WAKEUP, trigTime.getTimeInMillis(), broadcast);
                Toast.makeText(getApplicationContext(), "Alarm set successfully!", Toast.LENGTH_LONG).show();

                String time = eventTime.get(Calendar.HOUR_OF_DAY) + ":" + eventTime.get(Calendar.MINUTE);
                String date = eventTime.get(Calendar.DAY_OF_MONTH) + "-" + (eventTime.get(Calendar.MONTH) + 1) + "-" + eventTime.get(Calendar.YEAR);
                eventdetails.put("Time", time);
                eventdetails.put("Date", date);
                eventdetails.put("Description", description);
//                triggerref.child(date+" "+time).updateChildren(eventdetails);
                triggerref.child(String.valueOf(Calendar.getInstance().getTimeInMillis())).updateChildren(eventdetails);
            }
        });
        Button done = findViewById(R.id.donebutton);
        done.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Intent i = new Intent(NewEventSetter.this, TriggerActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}