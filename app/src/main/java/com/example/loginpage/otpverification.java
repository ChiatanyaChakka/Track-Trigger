package com.example.loginpage;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

public class otpverification extends AppCompatActivity {

    private EditText otpphone;
    private EditText otpmail;
    Button genphone;
    Button genmail;
    Button verphone;
    Button vermail;
    TextView phonenum;
    TextView mail;
    Button redirect;
    String profession;
    GmailSender sender;

    private static int OTPphone;
    private static int OTPmail;
    private static boolean phonedone;
    private static boolean maildone;
    String phone;
    String mailid;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);
        otpphone = findViewById(R.id.OTPTextFieldPhone);

        // Referencing the variables..
        otpphone = findViewById(R.id.OTPTextFieldPhone);
        otpmail = findViewById(R.id.OTPTextFieldMail);
        genphone = findViewById(R.id.OTPGeneratorPhone);
        genmail = findViewById(R.id.OTPGeneratorMail);
        verphone = findViewById(R.id.VerifyPhone);
        vermail = findViewById(R.id.VerifyMail);
        phonenum = findViewById(R.id.phoneNumber);
        mail = findViewById(R.id.Mail);
        phonedone = false;
        maildone = false;
        redirect = findViewById(R.id.dashboardredirect);
        sender = new GmailSender("trackandtriggerr@gmail.com", "OOP@@T&T");

        Bundle userinfo = getIntent().getExtras();
        phone = userinfo.getString("PhoneNumber");
        mailid = userinfo.getString("EmailID");
        phonenum.setText(phone);
        mail.setText(mailid);
        profession = userinfo.getString("profession");


        genphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        OTPphone = genOTP();
                        String message = "Never share your One-Time-Password with anyone.\nYour OTP for Track and Trigger is " + Integer.toString(OTPphone);
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phone, "Track&Trigger", message, null, null);
                        Toast.makeText(getApplicationContext(), "OTP Generated Successfully", Toast.LENGTH_SHORT).show();
                    }
                    else{
                        ActivityCompat.requestPermissions(otpverification.this, new String[]{Manifest.permission.SEND_SMS}, 1);
                    }
                }
            }
        });

        verphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enterotp = otpphone.getText().toString();
                if(enterotp == null){
                    Toast.makeText(getApplicationContext(), "Field is Empty!", Toast.LENGTH_SHORT).show();
                }
                else if(enterotp.matches("\\d+")){
                    if(OTPphone == Integer.parseInt(enterotp)){
                        phonedone = true;
                        Toast.makeText(getApplicationContext(), "Verified successfully", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(getApplicationContext(), "Failed! Try Again", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(getApplicationContext(), "Please Enter numbers only", Toast.LENGTH_SHORT).show();
                }
            }
        });

        genmail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OTPmail = genOTP();

                new MyAsyncClass().execute();
                // This code is for sharing... Not required for now
                /*Intent emailintent = new Intent(Intent.ACTION_SEND);
                emailintent.setData(Uri.parse("mailto:"));
                emailintent.setType("text/plain");
                emailintent.putExtra(Intent.EXTRA_EMAIL, mailid);
                emailintent.putExtra(Intent.EXTRA_SUBJECT, "OTP for Track and Trigger");
                emailintent.putExtra(Intent.EXTRA_TEXT, "Never share your One-Time-Password with anyone\nYour OTP for track and trigger is "+Integer.toString(OTPmail));
                emailintent.putExtra(Intent.EXTRA_TITLE, "Track and Trigger");
                try {
                    startActivity(Intent.createChooser(emailintent, "Send mail..."));
                    finish();
                } catch (android.content.ActivityNotFoundException ex) {
                    Toast.makeText(otpverification.this, "There is no email client installed.", Toast.LENGTH_SHORT).show();
                }*/
            }
        });

        vermail.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String enterotp = otpmail.getText().toString();
                if(enterotp == null){
                    Toast.makeText(otpverification.this, "Field is Empty!", Toast.LENGTH_SHORT).show();
                }
                else if(enterotp.matches("\\d+")){
                    if(OTPmail == Integer.parseInt(enterotp)){
                        maildone = true;
                        Toast.makeText(otpverification.this, "Verified successfully", Toast.LENGTH_LONG).show();
                    }
                    else{
                        Toast.makeText(otpverification.this, "Failed! Try Again", Toast.LENGTH_LONG).show();
                    }
                }
                else{
                    Toast.makeText(otpverification.this, "Please Enter numbers only", Toast.LENGTH_SHORT).show();
                }
            }
        });

        redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(phonedone == false){
                    Toast.makeText(getApplicationContext(), "Please verify your Phone number", Toast.LENGTH_LONG).show();
                }
                else if(maildone == false){
                    Toast.makeText(getApplicationContext(), "Please verify your Gmail", Toast.LENGTH_LONG).show();
                }
                else{
                    Intent i =  new Intent(otpverification.this, DashBoard.class);
                    i.putExtra("Profession", profession);
                    startActivity(i);
                }
            }
        });
    }

    private int genOTP(){
        return (int)(Math.random() * (1000000 - 100000 + 1) + 100000);
    }

    class MyAsyncClass extends AsyncTask<Void, Void, Void> {

        ProgressDialog pDialog;

        @Override
        protected void onPreExecute() {

            super.onPreExecute();
            pDialog = new ProgressDialog(otpverification.this);
            pDialog.setMessage("Please wait...");
            pDialog.show();

        }

        @Override

        protected Void doInBackground(Void... mApi) {
            try {

                // Add subject, Body, your mail Id, and receiver mail Id.
                sender.sendMail("OTP--Track and Trigger app",
                        "Never share your One-Time-Password with anyone\nYour OTP for verification of the mail is"+Integer.toString(OTPmail),
                        "trackandtriggerr@gmail.com",mailid);
                Log.d("send", "done");
            }
            catch (Exception ex) {
                Log.d("exceptionsending", ex.toString());
            }
            return null;
        }

        @Override

        protected void onPostExecute(Void result) {

            super.onPostExecute(result);
            pDialog.cancel();

            Toast.makeText(otpverification.this, "Email Sent successfully", Toast.LENGTH_SHORT).show();

        }
    }
}