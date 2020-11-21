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

import com.google.android.material.navigation.NavigationView;
import android.view.View;
import android.widget.Button;
import android.widget.TimePicker;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

public class DashBoard extends AppCompatActivity {
    private DrawerLayout navDrawer;
    private ActionBarDrawerToggle toggle;
    private Button testbutton;
    LinearLayout imagelayout;
    private NavigationView navigationView;
    private Button logout;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        navDrawer = findViewById(R.id.dash);
        imagelayout = findViewById(R.id.imagelayout);
        navigationView = findViewById(R.id.lisofitems);

        //Bundle prof = getIntent().getExtras();
        String profession = "Working Professional";//prof.getString("Profession");

        if(profession.equals("Profession")){ imagelayout.setBackgroundResource(R.drawable.defautlwallpaper);}
        else if(profession.equals("Working Professional")){ imagelayout.setBackgroundResource(R.drawable.workingprofwallpaper);}
        else if(profession.equals("Student")){ imagelayout.setBackgroundResource(R.drawable.studentwallpaper);}
        else{ imagelayout.setBackgroundResource(R.drawable.homemakerwallpaper);}


        auth = FirebaseAuth.getInstance();

        logout = findViewById(R.id.logout);
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
                    Intent i = new Intent(getApplicationContext(), NewEventSetter.class);
                    startActivity(i);
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
                auth.signOut();
                Toast.makeText(DashBoard.this,"Signing out...", Toast.LENGTH_SHORT).show();

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