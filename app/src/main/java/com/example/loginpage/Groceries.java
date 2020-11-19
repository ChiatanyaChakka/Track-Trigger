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

import java.util.ArrayList;
import java.util.List;

public class Groceries extends AppCompatActivity {

    ListView groceries;
    ArrayList<String> items;
    EditText search;
    FloatingActionButton addnew;
    SimpleViewAdapter adapter;
    private DrawerLayout navDrawer;
    private ActionBarDrawerToggle toggle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_groceries);
        search = findViewById(R.id.search);

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

        //Navigation bar code end

        addItemstoList();

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

                //TODO: Add the new items to database
                adapter.notifyDataSetChanged();
            }
        });
        groceries.setAdapter(adapter);
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
                if(input.getText().toString() == ""){
                    Toast.makeText(getApplicationContext(), "Please enter some data", Toast.LENGTH_SHORT).show();
                }
                else{
                    items.add(input.getText().toString());
                    //TODO: Add a 0 to the quantity arraylist too
                    //TODO: Add this new element to the database hashmap
                    alertDialog.dismiss();
                }
            }
        });
    }

    private void addItemstoList() {
        items = new ArrayList<String>();

        items.add("Tomato");
        items.add("Banana");
        items.add("Watermelon");
        items.add("Sugar");
        items.add("Snakes");
        items.add("High");
        //TODO: Add Method here to add items from database.. For now some dummy items are added.
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
                    }
                });
                viewHolder.decrease.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        int count;
                        count = Integer.parseInt(viewHolder.quantity.getText().toString());
                        count -= 1;
                        viewHolder.quantity.setText(Integer.toString(count));
                    }
                });
                convertView.setTag(viewHolder);
            }
            mainviewholder = (ViewHolder) convertView.getTag();
            mainviewholder.itemname.setText(getItem(position));
            //TODO: In the next commented we need to add quantities from the hashmap
            //mainviewholder.quantity.setText();
            return convertView;
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        if(item.getItemId() == R.id.sharing){
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
        for(String item: items){
            buffer.append(item+"\n");
            //TODO: Add quantities too
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