package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.drawerlayout.widget.DrawerLayout;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.android.material.navigation.NavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Groceries extends AppCompatActivity {

    ListView groceries;
    ArrayList<String> items;
    EditText search;
    FloatingActionButton addnew;
    SimpleViewAdapter adapter;
    private DrawerLayout navDrawer;
    private ActionBarDrawerToggle toggle;
    private FirebaseAuth auth;
    private Button save;

    private HashMap<String,Integer> databaseImage;

    private FirebaseUser user;
    private DatabaseReference rootRef, currentUserGroceriesRef, groceriesRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceries);
        search = findViewById(R.id.search);
        auth = FirebaseAuth.getInstance();
        save = findViewById(R.id.saveButton);

        items = new ArrayList<String>();
        databaseImage = new HashMap<>();

        user = FirebaseAuth.getInstance().getCurrentUser();
        rootRef = FirebaseDatabase.getInstance().getReference();
        groceriesRef = rootRef.child("Groceries");
        currentUserGroceriesRef = groceriesRef.child(user.getUid());

        //Navigation Bar code start
        NavigationView navigationView = findViewById(R.id.navigationview);
        navDrawer = (DrawerLayout) findViewById(R.id.groc);
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
//                    Intent i = new Intent(getApplicationContext(), NewEventSetter.class);
//                    startActivity(i);
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
        //Navigation bar code end
        save.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                HashMap<String,Object > map = new HashMap<>();
                map.put(user.getUid(),databaseImage);   //need to give second argument as a map. key itemName and value itemCount.
                groceriesRef.updateChildren(map);
            }
        });



        groceries = findViewById(R.id.Groceries);
        adapter = new SimpleViewAdapter(this, R.layout.simple_row, items);

        //Search bar started
        search.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) { }
            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) { Groceries.this.adapter.getFilter().filter(s); }
            @Override
            public void afterTextChanged(Editable s) { }});
        //Search bar ended

        addnew = findViewById(R.id.addnew);
        addnew.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showcreatedialog();
                adapter.notifyDataSetChanged();
            }
        });
        groceries.setAdapter(adapter);

        currentUserGroceriesRef.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                items.clear();
                for (DataSnapshot groceriesMap: snapshot.getChildren()) {
                    databaseImage.put(groceriesMap.getKey(), Integer.parseInt(groceriesMap.getValue().toString()));
                    items.add(groceriesMap.getKey());
                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void showcreatedialog() {

        final AlertDialog.Builder alert = new AlertDialog.Builder(Groceries.this);
        View dialogView = getLayoutInflater().inflate(R.layout.dialog_for_adding, null);

        final EditText input = dialogView.findViewById(R.id.newitemname);
        Button create = dialogView.findViewById(R.id.create);
        Button cancel = dialogView.findViewById(R.id.cancel);

        alert.setView(dialogView);
        AlertDialog alertDialog = alert.create();
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
                    items.add(input.getText().toString());
                    databaseImage.put(input.getText().toString(),0);
                    alertDialog.dismiss();
                    save.performClick();
                }
            }
        });
    }

    private class SimpleViewAdapter  extends ArrayAdapter<String> {
        Context context;
        List<String> items;
        int layout;

        SimpleViewAdapter(Context context,int resource, List<String> items){
            super(context, resource, items);
            this.context = context;
            this.items = items;
            layout = resource;
        }

        @NonNull
        @Override
        public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
            ViewHolder mainviewholder = null;
            if(convertView == null) {
                LayoutInflater inflater = LayoutInflater.from(getContext());
                convertView = inflater.inflate(layout, parent, false);
                ViewHolder viewHolder = new ViewHolder();
                viewHolder.decrease = (Button) convertView.findViewById(R.id.less);
                viewHolder.increase = (Button) convertView.findViewById(R.id.more);
                viewHolder.quantity = (TextView) convertView.findViewById(R.id.quantity);
                viewHolder.itemname = (TextView) convertView.findViewById(R.id.itemtitle);
                viewHolder.increase.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int count;
                        count = Integer.parseInt(viewHolder.quantity.getText().toString());
                        count += 1;
                        viewHolder.quantity.setText(Integer.toString(count));
                        databaseImage.remove(getItem(position));
                        databaseImage.put(getItem(position),count);
                    }
                });
                viewHolder.decrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int count;
                        count = Integer.parseInt(viewHolder.quantity.getText().toString());
                        count -= 1;
                        viewHolder.quantity.setText(Integer.toString(count));
                        databaseImage.remove(getItem(position));
                        databaseImage.put(getItem(position),count);
                    }
                });
                convertView.setTag(viewHolder);
            }
            mainviewholder = (ViewHolder) convertView.getTag();
            mainviewholder.itemname.setText(getItem(position));
            mainviewholder.quantity.setText(databaseImage.get(getItem(position)).toString());
            return convertView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.sharing){
            save.performClick();
            Intent share = new Intent(Intent.ACTION_SEND);
            share.putExtra(Intent.EXTRA_TEXT, createtextforsending());
            share.putExtra(Intent.EXTRA_SUBJECT, "All your Groceries");
            share.setType("text/plain");
            try {
                startActivity(Intent.createChooser(share, "Send mail..."));
                Toast.makeText(getApplicationContext(), "Sharing...", Toast.LENGTH_SHORT).show();
                finish();
            } catch (android.content.ActivityNotFoundException ex) {
                Toast.makeText(getApplicationContext(), "There is no email client installed.", Toast.LENGTH_SHORT).show();
            }

        }

        if(toggle.onOptionsItemSelected(item))
            return true ;
        return super.onOptionsItemSelected(item);
    }

    private String createtextforsending() {
        StringBuffer buffer = new StringBuffer();
        buffer.append("//Start of list//\n");
        for(Map.Entry<String,Integer> item: databaseImage.entrySet()){
            buffer.append(item.getKey()+" : "+item.getValue()+"\n");
        }
        buffer.append("//End of List//");
        return new String(buffer);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.overflow_for_groceries, menu);
        return true;
    }

    class ViewHolder{
        TextView itemname;
        Button decrease;
        Button increase;
        TextView quantity;
    }


}