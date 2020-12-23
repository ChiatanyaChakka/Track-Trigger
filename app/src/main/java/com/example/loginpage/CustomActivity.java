package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class CustomActivity extends AppCompatActivity {

    private DrawerLayout navDrawer;
    private ActionBarDrawerToggle toggle;
    private EditText searchbar;
    SimpleCustomAdaptercust adapter;
    ArrayList<String> custcattitles;
    private FloatingActionButton addnewcustitem;
    private ListView custcatlist;
    HashMap<String, Boolean> stringBooleanHashMapcust;
    private TextView emptyMsg;
    private int customButtonNumber;
    private FirebaseAuth auth;
    private DatabaseReference rootRef, categoryRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_custom);

        addnewcustitem = (FloatingActionButton) findViewById(R.id.addNewCustomItem);
        emptyMsg = findViewById(R.id.EmptyMessage);

        Bundle intentBundle = getIntent().getExtras();
        customButtonNumber = intentBundle.getInt("buttonNumber");

        auth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        categoryRef = rootRef.child("DashBoard").child(auth.getCurrentUser().getUid()+" "+"custom").child(String.valueOf(customButtonNumber));
        searchbar = findViewById(R.id.searchbarcust);

        //Temp
        custcattitles = new ArrayList<>();
        stringBooleanHashMapcust = new HashMap<>();
        //Temp
        custcatlist = findViewById(R.id.custcatlistview);

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stringBooleanHashMapcust.clear();
                custcattitles.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    custcattitles.add(snap.getKey());
                    stringBooleanHashMapcust.put(snap.getKey(), snap.getValue(boolean.class));
                }
                if (custcattitles.isEmpty()) {
                    emptyMsg.setVisibility(View.VISIBLE);
                } else {
                    emptyMsg.setVisibility(View.INVISIBLE);
                }
                adapter = new SimpleCustomAdaptercust(CustomActivity.this, R.layout.row_for_profcategory, custcattitles);
                custcatlist.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addnewcustitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(CustomActivity.this);
                View dialogview = getLayoutInflater().inflate(R.layout.dialog_for_adding, null);

                final EditText input = dialogview.findViewById(R.id.newitemname);
                Button create = dialogview.findViewById(R.id.create);
                Button cancel = dialogview.findViewById(R.id.cancel);

                builder.setView(dialogview);
                AlertDialog alertDialog = builder.create();
                alertDialog.setCanceledOnTouchOutside(false);
                alertDialog.show();

                cancel.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        alertDialog.dismiss();
                    }
                });
                create.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if(input.getText().toString().equals("")){
                            Toast.makeText(getApplicationContext(), "Please enter some data", Toast.LENGTH_SHORT).show();
                        }
                        else{
                            custcattitles.add(input.getText().toString());
                            stringBooleanHashMapcust.put(input.getText().toString(),false);
                            adapter.notifyDataSetChanged();
                            HashMap<String,Object> map = new HashMap<>();
                            map.put(String.valueOf(customButtonNumber),stringBooleanHashMapcust);
                            categoryRef.getParent().updateChildren(map);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        //Navigation code start
        NavigationView navigationView;
        navigationView = findViewById(R.id.lisofitems);
        navDrawer = (DrawerLayout) findViewById(R.id.custdrawer);
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
                    auth.signOut();
                    Toast.makeText(getApplicationContext(),"Signing out...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
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
        //Navigation code end

        //ListViewCode start

        //ListViewCode end

        //Seach bar start
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {CustomActivity.this.adapter.getFilter().filter(s); }
            @Override
            public void afterTextChanged(Editable s) { }
        });
        //Search bar end

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(toggle.onOptionsItemSelected(item))
            return true;
        return super.onOptionsItemSelected(item);
    }

    private class SimpleCustomAdaptercust extends ArrayAdapter<String> {
        Context context;
        int layout;
        List<String> itemslist;

        public SimpleCustomAdaptercust(@NonNull Context context, int resource, List<String> itemslist) {
            super(context, resource, itemslist);
            this.itemslist = itemslist;
            this.context = context;
            this.layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHoldercust mainholderprof = null;
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(layout, parent, false);
                ViewHoldercust viewHolderprof = new ViewHoldercust();
                viewHolderprof.itemnameprof = (TextView) convertView.findViewById(R.id.titleprofcat);
                viewHolderprof.checkBox = (CheckBox) convertView.findViewById(R.id.checkboxprofcat);
                viewHolderprof.delete = (Button) convertView.findViewById(R.id.delete);
                viewHolderprof.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stringBooleanHashMapcust.put(getItem(position), viewHolderprof.checkBox.isChecked());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(String.valueOf(customButtonNumber), stringBooleanHashMapcust);
                        categoryRef.getParent().updateChildren(map);
                        notifyDataSetChanged();
                    }
                });
                viewHolderprof.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        custcattitles.remove(viewHolderprof.itemnameprof.getText().toString());
                        stringBooleanHashMapcust.remove(viewHolderprof.itemnameprof.getText().toString());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(String.valueOf(customButtonNumber), stringBooleanHashMapcust);
                        categoryRef.getParent().updateChildren(map);
                        notifyDataSetChanged();
                    }
                });
                convertView.setTag(viewHolderprof);
            }
            mainholderprof = (ViewHoldercust) convertView.getTag();
            mainholderprof.itemnameprof.setText(getItem(position));
            mainholderprof.checkBox.setChecked(stringBooleanHashMapcust.get(getItem(position)));
            return convertView;
        }
    }

    class ViewHoldercust{
        TextView itemnameprof;
        CheckBox checkBox;
        Button delete;
    }
}