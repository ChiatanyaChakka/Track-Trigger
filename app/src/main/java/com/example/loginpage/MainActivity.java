package com.example.loginpage;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.login.LoginResult;
import com.facebook.login.LoginManager;

import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInClient;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthUserCollisionException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;
import com.google.firebase.auth.SignInMethodQueryResult;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN =122 ;

    private EditText email;
    private EditText password;
    private Button login;
    private Button test;
    private ImageView facebook;
    private ImageView google;
    private AlertDialog dialog;
    private AlertDialog.Builder builder;
    private View view;
    private EditText phone;
    private Button okButton;
    private Spinner spinner;

    private FirebaseAuth auth;
    private DatabaseReference userDetailRef;
    private HashMap<String,Object> map;
    private CallbackManager callbackManager;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions gso;
    private AuthCredential credential, prevCredential;
    private FirebaseUser user;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prevCredential = null;

        facebook = findViewById(R.id.Facebook);
        google = findViewById(R.id.Google);
        email = findViewById(R.id.Email);
        password = findViewById(R.id.editTextTextPassword);
        login = findViewById(R.id.LoginButton);

        view = getLayoutInflater().inflate(R.layout.details_request_dialogue,null);

        spinner = view.findViewById(R.id.profession);
        String[] professionsList = new String[]{"Default","Working Professional","Student","Home Maker"};
        ArrayAdapter<String> arrayAdapter = new ArrayAdapter<>(this,
                R.layout.spinner_textdef, professionsList);
        arrayAdapter.setDropDownViewResource(R.layout.spinner_textdef);
        spinner.setAdapter(arrayAdapter);

        builder = new AlertDialog.Builder(MainActivity.this);
        phone = view.findViewById(R.id.phone);
        okButton = view.findViewById(R.id.ok_button);
        builder.setView(view);
        builder.setCancelable(false);
        dialog = builder.create();
        dialog.setCanceledOnTouchOutside(false);


        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        callbackManager = CallbackManager.Factory.create();
        auth = FirebaseAuth.getInstance();
        userDetailRef = FirebaseDatabase.getInstance().getReference().child("Users");
        map = new HashMap<>();

        test = findViewById(R.id.testButton);
        test.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent p = new Intent(MainActivity.this, DashBoard.class);
                p.putExtra("Profession", "Profession");
                startActivity(p);
            }
        });

        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                credential = FacebookAuthProvider.getCredential(loginResult.getAccessToken().getToken());
                Toast.makeText(MainActivity.this, "Credential fetched!", Toast.LENGTH_SHORT).show();
                if(prevCredential != null) {
                    SignInWithCredential(credential, prevCredential);
                }else {
                    SignInWithCredential(credential);
                }
            }

            @Override
            public void onCancel() {
            }

            @Override
            public void onError(FacebookException error) {
            }
        });

        google.setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View view) {
                Intent signInIntent = googleSignInClient.getSignInIntent();
                startActivityForResult(signInIntent, RC_SIGN_IN);
            }
        });

        facebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                LoginManager.getInstance().logInWithReadPermissions(MainActivity.this, Arrays.asList("public_profile", "email"));
            }
        });

        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_email = email.getText().toString();
                String txt_password = password.getText().toString();

                if (TextUtils.isEmpty(txt_email) || TextUtils.isEmpty(txt_password)) {
                    Toast.makeText(MainActivity.this, "Fill the required credentials!", Toast.LENGTH_SHORT).show();
                } else {
                    credential = EmailAuthProvider.getCredential(txt_email, txt_password);
                    if(prevCredential != null) {
                        SignInWithCredential(credential, prevCredential);
                    }else {
                        SignInWithCredential(credential);
                    }
                }
            }
        });

        okButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String txt_phone = phone.getText().toString();
                String profession = spinner.getSelectedItem().toString();

                if (txt_phone.equals("") || txt_phone == null || !txt_phone.matches("\\d{10}")){
                    Toast.makeText(getApplicationContext(),"Please enter your 10-digit phone number to continue", Toast.LENGTH_SHORT).show();
                }
                else {
                    map.put("phone", txt_phone);
                    Toast.makeText(MainActivity.this, "dialog dismissed", Toast.LENGTH_SHORT).show();
                    map.put("profession", profession);
                    userDetailRef.child(user.getUid()).setValue(map);
                    dialog.dismiss();
                }
            }
        });

        dialog.setOnDismissListener(new DialogInterface.OnDismissListener() {
            @Override
            public void onDismiss(DialogInterface dialog) {
                Intent intent = new Intent(MainActivity.this,DashBoard.class);
                startActivity(intent);
                finish();
            }
        });
    }

    private void SignInWithCredential(AuthCredential currentCredential){
        auth.signInWithCredential(currentCredential).addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                user = auth.getCurrentUser();
                userDetailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.child(user.getUid()).exists()){
                            Intent intent = new Intent(MainActivity.this,DashBoard.class);
                            startActivity(intent);
                            finish();
                        }else {
                            dialog.show();
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });
            }
        }).addOnFailureListener(this, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                FirebaseAuthUserCollisionException exception = ((FirebaseAuthUserCollisionException) e);
                String errorCode = exception.getErrorCode();

                if (errorCode.equals("ERROR_ACCOUNT_EXISTS_WITH_DIFFERENT_CREDENTIAL")) {
                    String email = exception.getEmail();
                    prevCredential = currentCredential;
//                    This can be used to get provider of currentCredential. To block the user from using it next time.
//                    String provider = currentCredential.getProvider();

                    Toast.makeText(MainActivity.this,errorCode,Toast.LENGTH_SHORT).show();

                    auth.fetchSignInMethodsForEmail(email).addOnCompleteListener(new OnCompleteListener<SignInMethodQueryResult>() {
                        @Override
                        public void onComplete(@NonNull Task<SignInMethodQueryResult> task) {
                            if (task.isSuccessful()){
                                SignInMethodQueryResult result = task.getResult();
                                List<String> methods = result.getSignInMethods();
                                ArrayList<String> signInMethods = new ArrayList<>(methods);
                            }else {
                                Toast.makeText(MainActivity.this, task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            }
        });
    }

    private void SignInWithCredential(AuthCredential currentCredential, AuthCredential previousCredential) {
        auth.signInWithCredential(currentCredential).addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = auth.getCurrentUser();
                    user.linkWithCredential(previousCredential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()){
                                Toast.makeText(MainActivity.this,"Account Linking Successful!", Toast.LENGTH_SHORT).show();
                            }else {
                                Toast.makeText( MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                Toast.makeText(MainActivity.this, "Authentication Success!", Toast.LENGTH_SHORT).show();
                prevCredential = null;

                Intent intent =new Intent(MainActivity.this,DashBoard.class);
                startActivity(intent);
                finish();
            }
        });
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = auth.getCurrentUser();
        if(currentUser!=null){
            Intent intent =new Intent(MainActivity.this,DashBoard.class);
            startActivity(intent);
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                GoogleSignInAccount account = task.getResult(ApiException.class);
                credential = GoogleAuthProvider.getCredential(Objects.requireNonNull(account).getIdToken(), null);
                if (prevCredential != null) {
                    SignInWithCredential(credential, prevCredential);
                }else{
                    SignInWithCredential(credential);
                }
            } catch (ApiException e) {
                Toast.makeText(this,e.getMessage(),Toast.LENGTH_SHORT).show();
            }
        }else{
            callbackManager.onActivityResult(requestCode, resultCode, data);
        }
    }

    public void SignUpRedirect(View view) {
        Intent intent = new Intent(this, SigninPage.class);
        startActivity(intent);
    }
}