package com.example.loginpage;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.provider.ContactsContract;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

public class newApplicanceActivity extends AppCompatActivity {

    private ImageView applianceimage;
    private EditText nameofitem;
    private EditText nameofcategory;
    private EditText statusofappliance;
    private Button choose;
    private Button upload;
    private Button create;
    String nameappli;
    String cateappli;
    String statappli;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_applicance);

        applianceimage = (ImageView) findViewById(R.id.applianceimage);
        nameofitem = (EditText) findViewById(R.id.nameofnewappliance);
        nameofcategory = (EditText) findViewById(R.id.categoryofappliance);
        choose = (Button) findViewById(R.id.choosefile);
        upload = (Button) findViewById(R.id.upload);
        create = (Button) findViewById(R.id.create);
    }
}