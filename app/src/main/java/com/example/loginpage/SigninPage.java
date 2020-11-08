package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import android.Manifest;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;

public class SigninPage extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private EditText phone;
    private EditText Username;
    private Button verify;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_page);
        ActivityCompat.requestPermissions(SigninPage.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        Spinner spinner = findViewById(R.id.profspin);
        String[] professions = new String[]{"Profession", "Working Professional", "Student", "Home Maker"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_textdef, professions);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_textdef);
        spinner.setAdapter(arrayAdapter);
        email = findViewById(R.id.editTextTextEmailAddress2);
        password = findViewById(R.id.editTextTextPassword3);
        phone = findViewById(R.id.editTextPhone);
        Username = findViewById(R.id.editTextTextPersonName);
        verify = findViewById(R.id.CredentialVerificationButton);
        verify.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String text_email = email.getText().toString();
                String text_password = password.getText().toString();
                String text_phone = phone.getText().toString();
                String text_Username = Username.getText().toString();
                if (TextUtils.isEmpty(text_email) || TextUtils.isEmpty(text_password) || TextUtils.isEmpty(text_phone) || TextUtils.isEmpty(text_Username)) {
                    Toast.makeText(SigninPage.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                } else if (text_password.length() < 6) {
                    Toast.makeText(SigninPage.this, "Password too short", Toast.LENGTH_SHORT).show();
                } else if (text_phone.length() < 10) {
                    Toast.makeText(SigninPage.this, "Invaild mobile number", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(SigninPage.this, otpverification.class);
                    i.putExtra("PhoneNumber", text_phone);
                    i.putExtra("EmailID", text_email);
                    i.putExtra("profession", spinner.getSelectedItem().toString());
                    i.putExtra("password", text_password);
                    i.putExtra("username", text_Username);
                    startActivity(i);

                }
            }
        });
    }

}
