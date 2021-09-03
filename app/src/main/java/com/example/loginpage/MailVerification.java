package com.example.loginpage;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.UserProfileChangeRequest;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class MailVerification extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference rootRef;

    private ProgressDialog pDialog;
    private AlertDialog alertDialog;

    private String profession;
    private String phone;
    private String mail;
    private String username;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_otpverification);

        // Referencing the variables..
        Button generateMailLink = findViewById(R.id.LinkGenerator);
        TextView mailText = findViewById(R.id.Mail);
        Button redirect = findViewById(R.id.RegisterAndRedirectToDashbard);

        auth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        AlertDialog.Builder builder = new AlertDialog.Builder(MailVerification.this);
        builder.setCancelable(true);
        builder.setTitle("Sign-in error");
        builder.setMessage("Please verify your mail and try again! Note that accounts which are not verified are not allowed usage and might be deleted within 24 hours of creation.");
        alertDialog = builder.create();

        Bundle userinfo = getIntent().getExtras();
        phone = userinfo.getString("PhoneNumber");
        mail = userinfo.getString("EmailID");
        mailText.setText(mail);
        profession = userinfo.getString("profession");
        username = userinfo.getString("username");

        generateMailLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                pDialog = new ProgressDialog(MailVerification.this);
                pDialog.setMessage("Please wait...");
                pDialog.show();

                Objects.requireNonNull(auth.getCurrentUser()).sendEmailVerification().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        pDialog.cancel();
                        if (!(task.isSuccessful())) {
                            Toast.makeText(MailVerification.this, Objects.requireNonNull(task.getException()).getMessage(), Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });

        redirect.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Objects.requireNonNull(auth.getCurrentUser()).reload().addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (auth.getCurrentUser().isEmailVerified()) {
                            registerUser(phone, profession, username);
                        } else {
                            alertDialog.show();

                            Toast.makeText(MailVerification.this, "Please verify your mail and try again!", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        });
    }

    private void registerUser(String phone, String profession, String username) {
        HashMap<String, Object> map = new HashMap<>();
        map.put("phone", phone);
        map.put("profession", profession);
        rootRef.child("Users").child(auth.getCurrentUser().getUid()).setValue(map);

        UserProfileChangeRequest build = new UserProfileChangeRequest.Builder().setDisplayName(username).build();
        auth.getCurrentUser().updateProfile(build).addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                System.out.println(auth.getCurrentUser().getDisplayName());

                createDataBase();

                Intent i = new Intent(MailVerification.this, DashBoard.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void createDataBase() {
        DatabaseReference dashBoard = rootRef.child("DashBoard").child(auth.getCurrentUser().getUid());
        for (int i = 1; i <= 3; i++) {
            dashBoard.child(String.valueOf(i)).setValue("Add\nCustom\nButton");
        }
        rootRef.child("Usernames").child(auth.getCurrentUser().getUid()).setValue(username);
    }
}