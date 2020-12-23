package com.example.loginpage;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ContentResolver;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import java.util.HashMap;

public class newApplicanceActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST = 123;
    private ImageView applianceimage;
    private EditText nameofitem;
    private EditText nameofcategory;
    private EditText statusofappliance;
    private Button choose;
    private Button create;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef, rootref, applianceref;
    private Uri mImageUri;
    private FirebaseAuth auth;
    private FirebaseUser user;
    private StorageTask mUploadTask;
    private ProgressDialog progressDialog;
    // private Uri uri;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_applicance);
        ActivityCompat.requestPermissions(newApplicanceActivity.this,
                new String[]{Manifest.permission.READ_EXTERNAL_STORAGE, Manifest.permission.WRITE_EXTERNAL_STORAGE},
                1);
        auth = FirebaseAuth.getInstance();
        user = FirebaseAuth.getInstance().getCurrentUser();
        applianceimage = (ImageView) findViewById(R.id.applianceimage);
        nameofitem = (EditText) findViewById(R.id.nameofnewappliance);
        nameofcategory = (EditText) findViewById(R.id.categoryofappliance);
        statusofappliance = (EditText) findViewById(R.id.statusofappliance);
        choose = (Button) findViewById(R.id.choosefile);
        create = (Button) findViewById(R.id.CreateAppliance);
        mStorageRef= FirebaseStorage.getInstance().getReference("uploads");
        rootref = FirebaseDatabase.getInstance().getReference();
        //rootref.child("Appliances").setValue("hello");
        applianceref = rootref.child("Appliances");
        mDatabaseRef = applianceref.child(user.getUid());
        progressDialog = new ProgressDialog(newApplicanceActivity.this);
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });

        create.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                progressDialog.setMessage("Please wait for upload to finish...");
                progressDialog.setCanceledOnTouchOutside(false);
                progressDialog.setCancelable(false);
                progressDialog.show();
                uploadFile();
            }
        });

        progressDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {
                Intent intent = new Intent(getApplicationContext(), Appliances.class);
                startActivity(intent);
                finish();
            }
        });
    }




    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(nameofitem.getText().toString() + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Upload up = new Upload(nameofitem.getText().toString().trim(), taskSnapshot.getMetadata().getReference().getDownloadUrl().toString(),
                            statusofappliance.getText().toString(), nameofcategory.getText().toString());

                    StorageReference storageReference = FirebaseStorage.getInstance().getReference().child("uploads/"+nameofitem.getText().toString() + "." + getFileExtension(mImageUri));
                    storageReference.getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                        @Override
                        public void onSuccess(Uri uri) {
                            System.out.println(uri + " thi is random");
                            HashMap<String, Object> map = new HashMap<>();
                            map.put("imageUri", uri.toString());
                            map.put("category", nameofcategory.getText().toString());
                            map.put("title", nameofitem.getText().toString());
                            map.put("status", statusofappliance.getText().toString());
                            mDatabaseRef.child(nameofitem.getText().toString()).setValue(map).addOnSuccessListener(new OnSuccessListener<Void>() {
                                @Override
                                public void onSuccess(Void aVoid) {
                                    Toast.makeText(newApplicanceActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                                    progressDialog.cancel();
                                }
                            });
                        }

                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {

                        }
                    });
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(newApplicanceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
        }
        else{
            Toast.makeText(getApplicationContext(), "this is a sample toast", Toast.LENGTH_LONG).show();
        }
    }
    private String getFileExtension(Uri uri) {
        ContentResolver cR=getContentResolver();
        MimeTypeMap mime=MimeTypeMap.getSingleton();
        return mime.getExtensionFromMimeType(cR.getType(uri));
    }

    private void openFileChooser() {
        Intent intent =new Intent();
        intent.setType("image/*");
        intent.setAction(Intent.ACTION_GET_CONTENT);
        startActivityForResult(intent,PICK_IMAGE_REQUEST);
    }
    @Override
    protected void onActivityResult(int requestCode,int resultCode,Intent data) {

        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode==PICK_IMAGE_REQUEST && resultCode==RESULT_OK && data!=null && data.getData()
                !=null) {
            mImageUri = data.getData();
            Picasso.get().load(mImageUri).into(applianceimage);
        }
        }
    }
