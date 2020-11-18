package com.example.loginpage;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
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
import com.google.firebase.auth.UserInfo;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.security.AuthProvider;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

public class MainActivity extends AppCompatActivity {

    private static final int RC_SIGN_IN =122 ;

    private EditText username;
    private EditText password;
    private Button login;
    private Button test;
    private ImageView facebook;
    private ImageView google;

    private String txt_email;

    private FirebaseAuth auth;
    private DatabaseReference rootRef, emailRef;
    CallbackManager callbackManager;
    private GoogleSignInClient googleSignInClient;
    private GoogleSignInOptions gso;
    private AuthCredential credential, prevCredential;

    @RequiresApi(api = Build.VERSION_CODES.LOLLIPOP)
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        prevCredential = null;

        facebook = findViewById(R.id.Facebook);
        google = findViewById(R.id.Google);
        username = findViewById(R.id.Username);
        password = findViewById(R.id.editTextTextPassword);
        login = findViewById(R.id.LoginButton);

        gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();
        googleSignInClient = GoogleSignIn.getClient(this, gso);
        callbackManager = CallbackManager.Factory.create();
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
                String txt_username = username.getText().toString();
                emailRef = rootRef.child("Users").child(txt_username).child("email");

                emailRef.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        txt_email = snapshot.getValue(String.class);
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

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {
                    }
                });
            }
        });
    }

    private void SignInWithCredential(AuthCredential currentCredential){
        auth.signInWithCredential(currentCredential).addOnSuccessListener(this, new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                FirebaseUser user = auth.getCurrentUser();

                HashMap<String,Object> map = new HashMap<>();
                map.put("email", user.getEmail());
                map.put("username", user.getDisplayName());
                if (user.getPhoneNumber() != null) {
                    map.put("phone", user.getPhoneNumber());
                }else {
                    AlertDialog.Builder phoneRequest = new AlertDialog.Builder(MainActivity.this);
                    View view = getLayoutInflater().inflate(R.layout.phone_request_dialogue,null);
                    EditText getPhone = view.findViewById(R.id.phone);
                    Button ok = view.findViewById(R.id.ok_button);
                    phoneRequest.setView(view);
                    AlertDialog dialog = phoneRequest.create();
                    dialog.setCanceledOnTouchOutside(false);
                    dialog.show();
                    ok.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View v) {
                            String phone = getPhone.getText().toString();
                            if (phone.isEmpty()){
                                Toast.makeText(getApplicationContext(),"Please enter your phone number to continue", Toast.LENGTH_SHORT).show();
                            }
                            else {
                                map.put("phone", phone);
                                dialog.dismiss();

                                map.put("profession", "Profession");
                                map.put("UID", user.getUid());

                                rootRef.child("Users").child(user.getDisplayName()).setValue(map);

                                Intent intent =new Intent(MainActivity.this,DashBoard.class);
                                startActivity(intent);
                                finish();
                            }
                        }
                    });
                }
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
//
//                                AlertDialog.Builder signInoptions = new AlertDialog.Builder(MainActivity.this);
//                                View view = getLayoutInflater().inflate(R.layout.sign_in_options,null);
//                                TextView[] options = new TextView[signInMethods.size()];
//                                LinearLayout layout = findViewById(R.id.methodsLayout);
//
//                                for (int i = 0; i < signInMethods.size(); i++) {
//                                    options[i] = new TextView(MainActivity.this);
//                                    options[i].setText(signInMethods.get(i));
//                                    options[i].setTextSize(30);
//                                    layout.addView(options[i]);
//                                }
//                                Button ok = new Button(MainActivity.this);
//                                ok.setText("OK");
//                                ok.setTextSize(30);
//                                layout.addView(ok);
//                                signInoptions.setView(view);
//                                AlertDialog dialog = signInoptions.create();
//                                dialog.setCanceledOnTouchOutside(false);
//                                dialog.show();
//                                ok.setOnClickListener(new View.OnClickListener() {
//                                    @Override
//                                    public void onClick(View v) {
//                                        dialog.dismiss();
//                                    }
//                                });

/*                                Bundle intentBundle = new Bundle();
                                intentBundle.putStringArrayList("key",signInMethods);

                                Intent i = new Intent(MainActivity.this, LinkAccounts.class);
                                i.putExtra("bundle", intentBundle);
                                startActivity(i);*/
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
                                Toast.makeText(MainActivity.this,task.getException().getMessage(),Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                Toast.makeText(MainActivity.this, "Authentication Success!", Toast.LENGTH_SHORT).show();

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