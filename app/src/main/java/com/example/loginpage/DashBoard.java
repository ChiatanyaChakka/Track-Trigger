package com.example.loginpage;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Objects;

public class DashBoard extends AppCompatActivity {
    private DrawerLayout navDrawer;
    private ActionBarDrawerToggle toggle;
    private LinearLayout imagelayout;
    private NavigationView navigationView;
    private Button GroceryButton, ApplianceButton, HomeMaintButton;
    private Button[] customButton;
    private Button[] professionalButton;
    private int customButtonNumber;

    private FirebaseAuth auth;
    private DatabaseReference rootRef, userRef, customSectionRef, profSectionRef;

    private String profession, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dash_board);
        navDrawer = (DrawerLayout) findViewById(R.id.dash);
        imagelayout = (LinearLayout) findViewById(R.id.imagelayout);
        navDrawer = findViewById(R.id.dash);
        navigationView = findViewById(R.id.lisofitems);

        createButtons();

        auth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        userRef = rootRef.child("Users").child(auth.getCurrentUser().getUid());
        customSectionRef = rootRef.child("DashBoard").child(auth.getCurrentUser().getUid());

        customSectionRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                int i=0;
                for (DataSnapshot snap : snapshot.getChildren()) {
                    customButton[i++].setText(Objects.requireNonNull(snap.getValue(String.class)));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {
            }
        });

        userRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                ArrayList<String> userDetails = new ArrayList<>();
                for (DataSnapshot details : snapshot.getChildren()) {
                    userDetails.add(details.getValue().toString());
                }
                phone = userDetails.get(0);
                profession = userDetails.get(1);

                profSectionRef = rootRef.child("DashBoard").child("Professional");
                profSectionRef.child(profession).addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        int i = 0;
                        for (DataSnapshot snap : snapshot.getChildren()) {
                            professionalButton[i++].setText(snap.getValue().toString());
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });

                if (profession.equals("Default")) {
                    imagelayout.setBackgroundResource(R.drawable.defautlwallpaper);
                } else if (profession.equals("Working Professional")) {
                    imagelayout.setBackgroundResource(R.drawable.workingprofwallpaper);
                } else if (profession.equals("Student")) {
                    imagelayout.setBackgroundResource(R.drawable.studentwallpaper);
                } else {
                    imagelayout.setBackgroundResource(R.drawable.homemakerwallpaper);
                }
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
                if (id == R.id.trigger) {
                    Intent i = new Intent(getApplicationContext(), TriggerActivity.class);
                    startActivity(i);
                    finish();
                } else if (id == R.id.dashboard) {
                    Intent dashboard = new Intent(getApplicationContext(), DashBoard.class);
                    startActivity(dashboard);
                    finish();
                } else if (id == R.id.logout) {
                    auth.signOut();
                    Toast.makeText(DashBoard.this, "Signing out...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(), MainActivity.class);
                    startActivity(intent);
                    finish();
                }else if (id == R.id.profile){
                    Intent profile = new Intent(getApplicationContext(), ProfileActivity.class);
                    startActivity(profile);
                    finish();
                }
                return true;
            }
        });

        GroceryButton = findViewById(R.id.GroceriesButton);
        GroceryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashBoard.this, Groceries.class);
                startActivity(i);
                finish();
            }
        });

        ApplianceButton = findViewById(R.id.ApplianceButton);
        ApplianceButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashBoard.this, Appliances.class);
                startActivity(i);
                finish();
            }
        });

        HomeMaintButton = findViewById(R.id.ApplianceButton);
        HomeMaintButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(DashBoard.this, Appliances.class);
                startActivity(i);
                finish();
            }
        });
    }

    private void createButtons() {

        customButton = new Button[3];
        customButton[0] = findViewById(R.id.Custom1);
        customButton[1] = findViewById(R.id.Custom2);
        customButton[2] = findViewById(R.id.Custom3);

        for (Button b : customButton) {
            b.setOnClickListener(listenerCustom);
        }

        professionalButton = new Button[3];
        professionalButton[0] = findViewById(R.id.Professional1);
        professionalButton[1] = findViewById(R.id.Professional2);
        professionalButton[2] = findViewById(R.id.Professional3);

        for (Button b : professionalButton) {
            b.setOnClickListener(listenerProfessional);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    View.OnClickListener listenerProfessional = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(DashBoard.this, ProfessionActivity.class);
            int id = v.getId();

            switch (id) {
                case R.id.Professional1:
                    intent.putExtra("buttonNumber", 1);
                    break;
                case R.id.Professional2:
                    intent.putExtra("buttonNumber", 2);
                    break;
                case R.id.Professional3:
                    intent.putExtra("buttonNumber", 3);
                    break;
            }

            startActivity(intent);
            finish();
        }
    };

    View.OnClickListener listenerCustom = new View.OnClickListener() {
        @Override
        public void onClick(View v) {
            Intent intent = new Intent(DashBoard.this, CustomActivity.class);
            int id = v.getId();

            switch (id) {
                case R.id.Custom1:
                    intent.putExtra("buttonNumber", 1);
                    customButtonNumber = 1;
                    break;
                case R.id.Custom2:
                    intent.putExtra("buttonNumber", 2);
                    customButtonNumber = 2;
                    break;
                case R.id.Custom3:
                    intent.putExtra("buttonNumber", 3);
                    customButtonNumber = 3;
                    break;
            }

            if (((Button) v).getText().equals("Add\nCustom\nButton")) {
                AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(DashBoard.this);
                dialogBuilder.setTitle("Add Custom Category");
                View dialogView = getLayoutInflater().inflate(R.layout.dialog_for_adding, null);
                dialogBuilder.setView(dialogView);
                AlertDialog dialog = dialogBuilder.create();
                dialog.setCanceledOnTouchOutside(false);

                TextView view = dialogView.findViewById(R.id.DialogText);
                view.setText("Enter new Category Name");
                final EditText input = dialogView.findViewById(R.id.newitemname);
                Button create = dialogView.findViewById(R.id.create);
                Button cancel = dialogView.findViewById(R.id.cancel);
                dialog.show();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        dialog.dismiss();
                    }
                });
                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        String s = input.getText().toString();
                        System.out.println(s);
                        customSectionRef.child(String.valueOf(customButtonNumber)).setValue(s);
                        if (s.equals("") || s == null) {
                            Toast.makeText(getApplicationContext(), "Please enter some data", Toast.LENGTH_SHORT).show();
                        } else {
                            dialog.dismiss();
                        }
                    }
                });
            } else {
                startActivity(intent);
                finish();
            }
        }
    };

}