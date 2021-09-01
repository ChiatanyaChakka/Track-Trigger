package com.example.loginpage;

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

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.regex.Pattern;

public class SigninPage extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private EditText phone;
    private EditText Username;
    private Button verify;

    private boolean verified;

    private DatabaseReference rootRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_page);

        rootRef = FirebaseDatabase.getInstance().getReference();

        ActivityCompat.requestPermissions(SigninPage.this, new String[]{Manifest.permission.SEND_SMS}, 1);
        Spinner spinner = findViewById(R.id.profspin);
        String[] professions = new String[]{"Default", "Working Professional", "Student", "Home Maker"};
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
                if (!usernameOk(text_Username)) {
                    Toast.makeText(SigninPage.this, "Enter a valid, professional Username!", Toast.LENGTH_SHORT).show();
                } else if (TextUtils.isEmpty(text_email) || TextUtils.isEmpty(text_password) || TextUtils.isEmpty(text_phone) || TextUtils.isEmpty(text_Username)) {
                    Toast.makeText(SigninPage.this, "Empty Credentials!", Toast.LENGTH_SHORT).show();
                } else if (text_password.length() < 6) {
                    Toast.makeText(SigninPage.this, "Password too short", Toast.LENGTH_SHORT).show();
                } else if (text_phone.length() < 10) {
                    Toast.makeText(SigninPage.this, "Invalid mobile number", Toast.LENGTH_SHORT).show();
                } else {
                    Intent i = new Intent(SigninPage.this, MailVerification.class);
                    i.putExtra("PhoneNumber", text_phone);
                    i.putExtra("EmailID", text_email);
                    i.putExtra("profession", spinner.getSelectedItem().toString());
                    i.putExtra("username", text_Username);

                    FirebaseAuth.getInstance().createUserWithEmailAndPassword(text_email, text_password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
                        @Override
                        public void onSuccess(AuthResult authResult) {
                            startActivity(i);
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Toast.makeText(SigninPage.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                            System.out.println(e.getMessage());
                        }
                    });
                }
            }

            private boolean usernameOk(String text_username) {
                verified = true;
                Pattern pattern = Pattern.compile("([A-Z][a-z]\\s)*([A-Z][a-z]+)");
                if (!Pattern.matches(String.valueOf(pattern), text_username)) {
                    verified = false;
                }
                return verified;
            }
        });
    }
}
