package com.example.loginpage;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;

import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;

public class LinkAccounts extends AppCompatActivity {
    private static final int RC_SIGN_IN = 123;
    private ArrayList<String> signInMethods;
    private LinearLayout choices;
    private Button[] methods;

    private FirebaseAuth auth;
    private DatabaseReference rootRef, emailRef;
    CallbackManager callbackManager;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions gso;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_link_accounts);

        Intent intent = new Intent(LinkAccounts.this,MainActivity.class);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        callbackManager = CallbackManager.Factory.create();
        auth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();

        Bundle intentBundle = getIntent().getExtras();
        Bundle bundle = intentBundle.getBundle("bundle");
        signInMethods = bundle.getStringArrayList("key");

        choices = findViewById(R.id.choiceLayout);
        methods = new Button[signInMethods.size()];

        for (int i = 0; i < signInMethods.size(); i++) {
            methods[i] = new Button(this);
            String methodName = getMethodName(signInMethods.get(i));
            methods[i].setText(methodName);
            methods[i].setOnClickListener(new View.OnClickListener() {
                                              @Override
                                              public void onClick(View v) {
                                                  intent.putExtra("method", methodName);
                                                  startActivity(intent);
                                              }
                                          });
            choices.addView(methods[i]);
        }

    }

    private void LoginWith(String methodName) {
        if (methodName.equals("Login with Email")){
            ;
        }else if (methodName.equals("Google")){
            Intent signInIntent = googleSignInClient.getSignInIntent();
            startActivityForResult(signInIntent, RC_SIGN_IN);
        }else if (methodName.equals("Facebook")){
            ;
        }
    }

    private String getMethodName(String s) {
        if (s.equals("password")){
            return "Login with Email";
        }else{
            return s;
        }
    }
}