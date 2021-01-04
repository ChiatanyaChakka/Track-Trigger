package com.example.loginpage;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class ProfileActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private DatabaseReference rootRef, userRef, userNameRef;
    private TextView name, email, phone, profession;
    private ArrayList<String> userDetails;
    private DrawerLayout navDrawer;
    private ActionBarDrawerToggle toggle;
    private View confirmationDialogView;
    private Button confirm, cancel;
    private TextView logoutMsg;
    private AlertDialog.Builder builder;
    private AlertDialog logoutDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);
        confirmationDialogView = getLayoutInflater().inflate(R.layout.action_confirmation_dialogue, null);

        auth = FirebaseAuth.getInstance();
        FirebaseUser user = auth.getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users").child(user.getUid());
        userNameRef = rootRef.child("Usernames").child(user.getUid());

        name = findViewById(R.id.ProfileName);
        profession = findViewById(R.id.ProfileProfession);
        phone = findViewById(R.id.ProfileNumber);
        email = findViewById(R.id.ProfileEmail);

        userNameRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()) {
                    name.setText(snapshot.getValue(String.class));
                } else {
                    name.setText(user.getDisplayName());
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        email.setText(user.getEmail());

        userDetails = new ArrayList<>();

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for (DataSnapshot snap : snapshot.getChildren()) {
                    userDetails.add(snap.getValue(String.class));
                }

                profession.setText(userDetails.get(1));
                phone.setText(userDetails.get(0));
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        //Dialog for logout confirmation
        builder = new AlertDialog.Builder(ProfileActivity.this);
        builder.setCancelable(false);
        builder.setView(confirmationDialogView);
        confirm = confirmationDialogView.findViewById(R.id.confirmAction);
        cancel = confirmationDialogView.findViewById(R.id.cancelAction);
        logoutMsg = confirmationDialogView.findViewById(R.id.confirmMsg);
        logoutDialog = builder.create();
        logoutDialog.setCanceledOnTouchOutside(false);

        logoutMsg.setText("Do you really want to logout?");
        confirm.setText("Confirm Logout");

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                auth.signOut();
                logoutDialog.cancel();
                Toast.makeText(getApplicationContext(), "Signing out...", Toast.LENGTH_SHORT).show();
                Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                startActivity(intent);
                finish();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logoutDialog.cancel();
            }
        });
        //dialog for logout confirmation

        //Navigation Bar code start
        NavigationView navigationView;
        navigationView = findViewById(R.id.lisofitems);
        navDrawer = (DrawerLayout) findViewById(R.id.ProfileDrawer);
        toggle = new ActionBarDrawerToggle(this, navDrawer, R.string.open, R.string.close);
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.trigger){
                    Intent i = new Intent(getApplicationContext(), TriggerActivity.class);
                    startActivity(i);
                    finish();
                }
                else if (id == R.id.dashboard){
                    Intent dashboard = new Intent(getApplicationContext(), DashBoard.class);
                    startActivity(dashboard);
                    finish();
                }
                else if (id == R.id.logout){
                    logoutDialog.show();
                }else if (id == R.id.profile){
                    Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(profile);
                    finish();
                }
                return true;
            }
        });
        //Navigation bar code end

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }
}