package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

public class DashBoard extends AppCompatActivity {
    private DrawerLayout navDrawer;
    private ActionBarDrawerToggle toggle;
    private Button testbutton;
    private Button logout;
    LinearLayout imagelayout;
    private NavigationView navigationView;

    private FirebaseAuth auth;
    private DatabaseReference userRef;

    private String profession, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        navDrawer = findViewById(R.id.dash);
        imagelayout = findViewById(R.id.imagelayout);
        navigationView = findViewById(R.id.lisofitems);
        logout = findViewById(R.id.logout);

        auth = FirebaseAuth.getInstance();
        userRef = FirebaseDatabase.getInstance().getReference().child("Users").child(auth.getCurrentUser().getUid());

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> userDetails = new ArrayList<>();
                for (DataSnapshot details: snapshot.getChildren()){
                    userDetails.add(details.getValue().toString());
                }
                phone = userDetails.get(0);
                profession = userDetails.get(1);

                if(profession.equals("Default")){ imagelayout.setBackgroundResource(R.drawable.defautlwallpaper);}
                else if(profession.equals("Working Professional")){ imagelayout.setBackgroundResource(R.drawable.workingprofwallpaper);}
                else if(profession.equals("Student")){ imagelayout.setBackgroundResource(R.drawable.studentwallpaper);}
                else{ imagelayout.setBackgroundResource(R.drawable.homemakerwallpaper);}
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        navDrawer = findViewById(R.id.dash);
        toggle = new ActionBarDrawerToggle(this, navDrawer, R.string.open, R.string.close);
        navDrawer.addDrawerListener(toggle);
        toggle.syncState();
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        navigationView.setNavigationItemSelectedListener(new NavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                int id = item.getItemId();
                if(id == R.id.trigger){
//                    Intent i = new Intent(getApplicationContext(), NewEventSetter.class);
//                    startActivity(i);
                }
                else if (id == R.id.dashboard){
                    Intent dashboard = new Intent(getApplicationContext(), DashBoard.class);
                    startActivity(dashboard);
                }
                return true;
            }
        });

        testbutton = findViewById(R.id.button10);
        testbutton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashBoard.this, Groceries.class);
                startActivity(i);
            }
        });

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Toast.makeText(DashBoard.this,"Signing out...", Toast.LENGTH_SHORT).show();
                auth.signOut();

                Intent intent = new Intent(DashBoard.this,MainActivity.class);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true ;
        return super.onOptionsItemSelected(item);
    }
}