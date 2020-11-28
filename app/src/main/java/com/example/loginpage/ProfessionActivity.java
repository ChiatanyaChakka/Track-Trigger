package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

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

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class ProfessionActivity extends AppCompatActivity {

    private DrawerLayout navDrawer;
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth auth;
    private EditText searchbar;
    SimpleCustomAdapter adapter;
    ArrayList<String> profcattitles;
    private ListView profcatlist;
    FloatingActionButton addnewprofitem;
    HashMap<String, Boolean> stringBooleanHashMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profession);

        addnewprofitem = (FloatingActionButton) findViewById(R.id.addnewprofcat) ;

        auth = FirebaseAuth.getInstance();

        //Temp
        profcattitles = new ArrayList<>();
        stringBooleanHashMap = new HashMap<>();
        profcattitles.add("Homework one");
        stringBooleanHashMap.put("Homework one", true);
        profcattitles.add("HomeWork two");
        stringBooleanHashMap.put("HomeWork two", false);

        //Temp


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
                        else{
                            profcattitles.add(input.getText().toString());
                            alertDialog.dismiss();
                            adapter.notifyDataSetChanged();
                        }
                    }
                });
            }
        });

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
                    auth.signOut();
                    Toast.makeText(getApplicationContext(),"Signing out...", Toast.LENGTH_SHORT).show();
                    Intent intent = new Intent(getApplicationContext(),MainActivity.class);
                    startActivity(intent);
                    finish();
                }
                return true;
            }
        });
        //Navigation code end

        //ListViewCode start

        profcatlist = findViewById(R.id.profcatlistview);
        adapter = new SimpleCustomAdapter(this, R.layout.row_for_profcategory, profcattitles);
        profcatlist.setAdapter(adapter);

        //ListViewCode end

        //Seach bar start
        searchbar = findViewById(R.id.searchbar);
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
    }
}