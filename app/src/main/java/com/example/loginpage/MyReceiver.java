package com.example.loginpage;

import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.provider.Telephony;
import android.telephony.SmsManager;
import android.util.Log;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MyReceiver extends BroadcastReceiver {

    GmailSender sender;
    private FirebaseUser user;

    @Override
    public void onReceive(Context context, Intent intent) {
        user = FirebaseAuth.getInstance().getCurrentUser();
        String phonenum = intent.getExtras().getString("phone num");
        String docList = intent.getExtras().getString("description");
        String username = user.getDisplayName();
        if(username.equals(""))
            username = "User";

        //Sending SMS
        SmsManager smsManager = SmsManager.getDefault();
        try {
            smsManager.sendTextMessage(phonenum, null,
                    "Dear "+username+",\n"+"Your event is due in 1 hour. Here is the custom message you have given:\n"+docList, null, null);
        } catch (Exception e) {
            Toast.makeText(context, "SMS failed!", Toast.LENGTH_LONG).show();
        }
        //SMS sending Done


        //Mail sending
        sender = new GmailSender("trackandtriggerr@gmail.com", "OOP@@T&T");
        new MyReceiver.MyAsyncClass(docList, username).execute();
    }
    class MyAsyncClass extends AsyncTask<Void, Void, Void> {
            String docList;
            String username;
            MyAsyncClass(String docs, String username){
                docList = docs;
                this.username = username;
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
                            "Dear "+username+",\n"+"Your meeting is due in one hour. Here is the custom message you have given:\n"+docList,
                            "trackandtriggerr@gmail.com", user.getEmail());
                    Log.d("send", "done");
                } catch (Exception ex) {
                    Log.d("exceptionsending", ex.toString());
                }
                return null;
            }

            @Override

            protected void onPostExecute(Void result) {
                super.onPostExecute(result);

            }
        }
}
