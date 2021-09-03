package com.example.loginpage;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.telephony.SmsManager;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Calendar;

public class MyReceiver extends BroadcastReceiver {

    GmailSender sender;
    private FirebaseUser user;
    private DatabaseReference rootRef, userNameRef;
    private String username;
    private String phonenum, customNote;
    private Calendar triggerTime, registerTime, eventTime;
    private String body;


    @Override
    public void onReceive(Context context, Intent intent) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        phonenum = intent.getStringExtra("phone num");
        customNote = intent.getStringExtra("description");
        eventTime = (Calendar) intent.getSerializableExtra("event time");
        registerTime = (Calendar) intent.getSerializableExtra("register time");

        body = "Your event is due in 1 hour. Event details:\n\n" + "Event Time: " + eventTime.getTime() + "\nPersonal Note: " + customNote;
        body = body + "\nThis event was registered with us at: " + registerTime.getTime();

//        System.out.println(triggerTime + " printing calendars in MyReceiver" + registerTime);


        rootRef = FirebaseDatabase.getInstance().getReference();
        userNameRef = rootRef.child("Usernames").child(user.getUid());

        userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    username = snapshot.getValue(String.class);
                } else {
                    username = user.getDisplayName();
                }
                body = "Dear " + username + ",\n" + body;
                sendAlert(context);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void sendAlert(Context context) {

        //Sending SMS
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(phonenum, null, body, null, null);
            System.out.println("SMS sent in MyReceiver to " + phonenum);
        } catch (Exception e) {
            Toast.makeText(context, "SMS failed!", Toast.LENGTH_LONG).show();
        }
        //SMS sending Done


        //Mail sending
        sender = new GmailSender();
        new MyReceiver.MyAsyncClass(body).execute();

    }

    class MyAsyncClass extends AsyncTask<Void, Void, Void> {
        String body;

        MyAsyncClass(String body) {
            this.body = body;
        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected Void doInBackground(Void... mApi) {
            try {

                // Add subject, Body, your mail Id, and receiver mail Id.
                sender.sendMail("Track and Trigger app -- Alert",
                        body,
                        "trackandtriggerr@gmail.com", user.getEmail());
                System.out.println("mail sent MyReceiver");
            } catch (Exception e) {
                System.out.println(e.getMessage() + " mail not sent in MyReceiver " + e.toString());
            }
            return null;
        }

        @Override
        protected void onPostExecute(Void result) {
            super.onPostExecute(result);

        }
    }
}
