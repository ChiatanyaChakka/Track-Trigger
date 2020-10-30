package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class SigninPage extends AppCompatActivity {
    private EditText email;
    private EditText password;
    private EditText phone;
    private EditText Username;
    private Button register;
    private FirebaseAuth auth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signin_page);
        email=findViewById(R.id.editTextTextEmailAddress2);
        password=findViewById(R.id.editTextTextPassword3);
                phone=findViewById(R.id.editTextPhone);
                Username=findViewById(R.id.editTextTextPersonName);
                register=findViewById(R.id.RegisterButton);
                auth=FirebaseAuth.getInstance();
register.setOnClickListener(new View.OnClickListener() {
    @Override
    public void onClick(View v) {
        String text_email=email.getText().toString();
        String text_password=password.getText().toString();
        String text_phone=phone.getText().toString();
        String text_Username=Username.getText().toString();
        if(TextUtils.isEmpty(text_email)|| TextUtils.isEmpty(text_password)||TextUtils.isEmpty(text_phone)||TextUtils.isEmpty(text_Username)){
           Toast.makeText( SigninPage.this ,"Empty Credentials!", Toast.LENGTH_SHORT).show();
        }
        else if(text_password.length()<6)
        {
            Toast.makeText( SigninPage.this ,"Password too short", Toast.LENGTH_SHORT).show();
        }
        else if(text_phone.length()<10)
        {
            Toast.makeText( SigninPage.this ,"Invaild mobile number", Toast.LENGTH_SHORT).show();
        }
        else{
         registerUser(text_email,text_password);
        }
    }
});
    }

    private void registerUser(String email, String password) {
        auth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(SigninPage.this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if(task.isSuccessful()){
                    Toast.makeText( SigninPage.this ,"Successful!", Toast.LENGTH_SHORT).show();
                }
                else{
                    Toast.makeText( SigninPage.this ,"Failed!", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}
