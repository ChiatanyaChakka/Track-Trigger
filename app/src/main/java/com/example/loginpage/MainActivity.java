package com.example.loginpage;

import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class MainActivity extends AppCompatActivity {

    private EditText username;
    private EditText password;
    private Button login;
    private Button test;

    private String txt_email;

    private FirebaseAuth auth;
    private DatabaseReference rootRef, emailRef;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);


        username = findViewById(R.id.Username);
        password = findViewById(R.id.editTextTextPassword);
        login = findViewById(R.id.LoginButton);

        auth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        test = findViewById(R.id.testButton);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent p = new Intent(MainActivity.this, DashBoard.class);
                startActivity(p);
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_username = username.getText().toString();
                emailRef = rootRef.child("Users").child(txt_username).child("email");

                emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        txt_email = snapshot.getValue(String.class);
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                String txt_password = password.getText().toString();
                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(MainActivity.this, "Fill the required credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    logIn(txt_email, txt_password);
                }
            }
        });
    }

    private void logIn(String email, String password) {
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                Toast.makeText(MainActivity.this, "Welcome!" + email, Toast.LENGTH_SHORT).show();
                Intent p = new Intent(MainActivity.this, DashBoard.class);
                startActivity(p);
                finish();
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                Toast.makeText(MainActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });

//        auth.signInWithEmailAndPassword(email, password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
//            @Override
//            public void onComplete(@NonNull Task<AuthResult> task) {
//                if(task.isSuccessful()) {
//                    Toast.makeText(MainActivity.this, "Welcome!"+email, Toast.LENGTH_SHORT).show();
//                    Intent p = new Intent(MainActivity.this, DashBoard.class);
//                    startActivity(p);
//                    finish();
//                }
//                else{
//                    Toast.makeText(MainActivity.this, "Failure!", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });

    }

    public void SignUpRedirect(View view) {
        Intent intent = new Intent(this, SigninPage.class);
        startActivity(intent);
    }
}