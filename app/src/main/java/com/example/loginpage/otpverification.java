package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;

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

    EditText otpphone = (EditText) findViewById(R.id.OTPTextFieldPhone);
    EditText otpmail = (EditText) findViewById(R.id.OTPTextFieldMail);
    Button genphone = (Button) findViewById(R.id.OTPGeneratorPhone);
    Button genmail = (Button) findViewById(R.id.OTPGeneratorMail);
    Button verphone = (Button) findViewById(R.id.VerifyPhone);
    Button vermail = (Button) findViewById(R.id.VerifyMail);
    TextView phonenum = (TextView) findViewById(R.id.phoneNumber);
    TextView mail = (TextView) findViewById(R.id.Mail);

    private static int OTPphone;
    private static int OTPmail;
    private static boolean phonedone = false;
    private static boolean maildone = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

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
                        requestPermissions(new String[]{Manifest.permission.SEND_SMS}, 1);
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