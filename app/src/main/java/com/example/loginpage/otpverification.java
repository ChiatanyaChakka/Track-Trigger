package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.app.PendingIntent;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsManager;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Random;

public class otpverification extends AppCompatActivity {

    private EditText otpphone;
    private EditText otpmail;
    Button genphone;
    Button genmail;
    Button verphone;
    Button vermail;
    TextView phonenum;
    TextView mail;

    private static int OTPphone;
    private static int OTPmail;
    private static boolean phonedone;
    private static boolean maildone;

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

        Bundle userinfo = getIntent().getExtras();
        String phone = userinfo.getString("PhoneNumber");
        String mailid = userinfo.getString("EmailID");
        phonenum.setText(phone);
        mail.setText(mailid);


        genphone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {

                    if (checkSelfPermission(Manifest.permission.SEND_SMS) == PackageManager.PERMISSION_GRANTED) {
                        OTPphone = genOTP();
                        String message = "Never share your One-Time-Password with Anyone.\nYour OTP is " + Integer.toString(OTPphone);
                        SmsManager smsManager = SmsManager.getDefault();
                        smsManager.sendTextMessage(phone, null, message, null, null);
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
    }


    private int genOTP(){
        return (int)(Math.random() * (1000000 - 100000 + 1) + 100000);
    }
}