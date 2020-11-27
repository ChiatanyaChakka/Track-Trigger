package com.example.loginpage;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.ContentResolver;
import android.content.Intent;
import android.media.Image;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.provider.ContactsContract;
import android.view.View;
import android.webkit.MimeTypeMap;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.OnProgressListener;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

public class newApplicanceActivity extends AppCompatActivity {

    private static final int PICK_IMAGE_REQUEST =123 ;
    private ImageView applianceimage;
    private EditText nameofitem;
    private EditText nameofcategory;
    private EditText statusofappliance;
    private Button choose;
    private Button upload;
    private Button create;
    private StorageReference mStorageRef;
    private DatabaseReference mDatabaseRef;
    private Uri mImageUri;
    String nameappli;
    String cateappli;
    String statappli;
    private StorageTask mUploadTask;
   // private Uri uri;

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
        mStorageRef= FirebaseStorage.getInstance().getReference("uploads");
        mDatabaseRef= FirebaseDatabase.getInstance().getReference("Uploads");
        choose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                openFileChooser();
            }
        });
        upload.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                    uploadFile();

            }
        });
    }

    private void uploadFile() {
        if (mImageUri != null) {
            StorageReference fileReference = mStorageRef.child(nameofitem.getText() + "." + getFileExtension(mImageUri));
            mUploadTask = fileReference.putFile(mImageUri).addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                    Toast.makeText(newApplicanceActivity.this, "Upload Successful", Toast.LENGTH_LONG).show();
                    Upload up = new Upload(nameofitem.getText().toString().trim(),taskSnapshot.getMetadata().getReference().getDownloadUrl().toString());

                    mDatabaseRef.child(nameofitem.getText().toString()).setValue(up);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {

                    Toast.makeText(newApplicanceActivity.this, e.getMessage(), Toast.LENGTH_SHORT).show();
                }
            });
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
