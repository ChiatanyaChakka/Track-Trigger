package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ExpandableListAdapter;
import android.widget.ExpandableListView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class Appliances extends AppCompatActivity {
    private ExpandableListView expandableListView;
    private ExpandableListAdapter adapter;
    private List<String> arraylist;
    private HashMap<String, AppliancesData> hashMap;
    private int lastExpandedPosition = -1;
    private FloatingActionButton addnewappliance;
    private DatabaseReference rootref, appliancesref;
    private FirebaseAuth auth;
    private StorageReference storageRef;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_appliances);
        addnewappliance = (FloatingActionButton) findViewById(R.id.newappliancebutton);
        expandableListView = findViewById(R.id.applianceslistexpandable);

        arraylist = new ArrayList<>();
        hashMap = new HashMap<>();
        auth = FirebaseAuth.getInstance();
        rootref = FirebaseDatabase.getInstance().getReference();
        appliancesref = rootref.child("Appliances").child(auth.getCurrentUser().getUid());

        appliancesref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot snap: snapshot.getChildren()){
                    try {
                        HashMap<String, String> temp = new HashMap<>();
                        arraylist.add(snap.getKey());
                        temp = (HashMap<String, String>) snap.getValue();
                        AppliancesData appliancesData = new AppliancesData(
                                temp.get("title").toString(),
                                temp.get("status").toString(),
                                temp.get("category").toString(),
                                temp.get("imageUri").toString()
                        );
                        hashMap.put(temp.get("title").toString(), appliancesData);
                    }catch (Exception e){
                        Toast.makeText(getApplicationContext(), e.getMessage(), Toast.LENGTH_LONG).show();
                    }
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new CustomAdapterForExpandable(this, arraylist, hashMap);
        expandableListView.setAdapter(adapter);
        expandableListView.setOnGroupExpandListener(new ExpandableListView.OnGroupExpandListener() {
            @Override
            public void onGroupExpand(int groupPosition) {
                if(lastExpandedPosition != -1 && groupPosition != lastExpandedPosition){
                    expandableListView.collapseGroup(lastExpandedPosition);
                }
                lastExpandedPosition = groupPosition;

            }
        });
        addnewappliance.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(getApplicationContext(), newApplicanceActivity.class);
                startActivity(i);
                finish();
            }
        });
    }
}