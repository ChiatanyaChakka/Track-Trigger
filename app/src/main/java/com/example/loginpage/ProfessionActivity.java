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

public class ProfessionActivity extends AppCompatActivity {

    private DrawerLayout navDrawer;
    private ActionBarDrawerToggle toggle;
    private EditText searchbar;
    SimpleCustomAdapter adapter;
    ArrayList<String> profcattitles;
    private ListView profcatlist;
    private FloatingActionButton addnewprofitem;
    HashMap<String, Boolean> stringBooleanHashMap;
    private TextView emptyMessage;

    private int professionalButtonNumber;
    private FirebaseAuth auth;
    private DatabaseReference rootRef, categoryRef;

    private View confirmationDialogView;
    private Button confirm, cancel;
    private TextView logoutMsg;
    private android.app.AlertDialog.Builder builder;
    private android.app.AlertDialog logoutDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profession);

        System.out.println("Entered professional activity");
        confirmationDialogView = getLayoutInflater().inflate(R.layout.action_confirmation_dialogue, null);

        addnewprofitem = (FloatingActionButton) findViewById(R.id.addnewprofcat);
        searchbar = findViewById(R.id.searchbar);
        profcatlist = findViewById(R.id.profcatlistview);
        emptyMessage = findViewById(R.id.EmptyMessage);

        Bundle intentBundle = getIntent().getExtras();
        professionalButtonNumber = intentBundle.getInt("buttonNumber");

        auth = FirebaseAuth.getInstance();
        rootRef = FirebaseDatabase.getInstance().getReference();
        categoryRef = rootRef.child("DashBoard").child(auth.getCurrentUser().getUid()+" "+"professional").child(String.valueOf(professionalButtonNumber));

        //Temp
        profcattitles = new ArrayList<>();
        stringBooleanHashMap = new HashMap<>();
        //Temp

        categoryRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                stringBooleanHashMap.clear();
                profcattitles.clear();
                for (DataSnapshot snap : snapshot.getChildren()) {
                    profcattitles.add(snap.getKey());
                    stringBooleanHashMap.put(snap.getKey(), snap.getValue(boolean.class));
                }
                if (profcattitles.isEmpty()) {
                    emptyMessage.setVisibility(View.VISIBLE);
                } else {
                    emptyMessage.setVisibility(View.INVISIBLE);
                }
                adapter = new SimpleCustomAdapter(ProfessionActivity.this, R.layout.row_for_profcategory, profcattitles);
                profcatlist.setAdapter(adapter);
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        addnewprofitem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AlertDialog.Builder builder = new AlertDialog.Builder(ProfessionActivity.this);
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
                        else {
                            profcattitles.add(input.getText().toString());
                            stringBooleanHashMap.put(input.getText().toString(), false);
                            adapter.notifyDataSetChanged();
                            HashMap<String, Object> map = new HashMap<>();
                            map.put(String.valueOf(professionalButtonNumber), stringBooleanHashMap);
                            categoryRef.getParent().updateChildren(map);
                            alertDialog.dismiss();
                        }
                    }
                });
            }
        });

        //Dialog for logout confirmation
        builder = new android.app.AlertDialog.Builder(ProfessionActivity.this);
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

        //Navigation code start
        NavigationView navigationView;
        navigationView = findViewById(R.id.lisofitems);
        navDrawer = (DrawerLayout) findViewById(R.id.profdrawer);
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
        //Navigation code end

        //ListViewCode start

        //ListViewCode end

        //Seach bar start
        searchbar.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {ProfessionActivity.this.adapter.getFilter().filter(s); }
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

    private class SimpleCustomAdapter extends ArrayAdapter<String>{
        Context context;
        int layout;
        List<String> itemslist;

        public SimpleCustomAdapter(@NonNull Context context, int resource, List<String> itemslist) {
            super(context, resource, itemslist);
            this.itemslist = itemslist;
            this.context = context;
            this.layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolderprof mainholderprof = null;
            if(convertView == null){
                convertView = LayoutInflater.from(getContext()).inflate(layout, parent, false);
                ViewHolderprof viewHolderprof = new ViewHolderprof();
                viewHolderprof.itemnameprof = (TextView) convertView.findViewById(R.id.titleprofcat);
                viewHolderprof.checkBox = (CheckBox) convertView.findViewById(R.id.checkboxprofcat);
                viewHolderprof.delete = (Button) convertView.findViewById(R.id.delete);
                viewHolderprof.checkBox.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        stringBooleanHashMap.put(getItem(position), viewHolderprof.checkBox.isChecked());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(String.valueOf(professionalButtonNumber), stringBooleanHashMap);
                        categoryRef.getParent().updateChildren(map);
                        notifyDataSetChanged();
                    }
                });
                viewHolderprof.delete.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        profcattitles.remove(viewHolderprof.itemnameprof.getText().toString());
                        stringBooleanHashMap.remove(viewHolderprof.itemnameprof.getText().toString());
                        HashMap<String, Object> map = new HashMap<>();
                        map.put(String.valueOf(professionalButtonNumber), stringBooleanHashMap);
                        categoryRef.getParent().updateChildren(map);
                        notifyDataSetChanged();
                    }
                });
                convertView.setTag(viewHolderprof);
            }
            mainholderprof = (ViewHolderprof) convertView.getTag();
            mainholderprof.itemnameprof.setText(getItem(position));
            mainholderprof.checkBox.setChecked(stringBooleanHashMap.get(getItem(position)));
            return convertView;
        }
    }

    class ViewHolderprof{
        TextView itemnameprof;
        CheckBox checkBox;
        Button delete;
    }
}