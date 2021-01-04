package com.example.loginpage;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.core.content.FileProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.squareup.picasso.Picasso;
import com.squareup.picasso.Target;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class CustomAdapterForExpandable extends BaseExpandableListAdapter {
    private Context context;
    private List<String> titles;
    private HashMap<String, AppliancesData> hashMap;
    private String activity;
    private View deleteConfirmView;

    public CustomAdapterForExpandable(Context context, List<String> titles, HashMap<String, AppliancesData> hashMap, View view) {
        this.context = context;
        this.titles = titles;
        this.hashMap = hashMap;
        this.deleteConfirmView = view;
        if (context.toString().contains("Appliances")) {
            this.activity = "Appliances";
        } else {
            this.activity = "Maintainance";
        }
    }

    @Override
    public int getGroupCount() {
        return this.titles.size();
    }

    @Override
    public int getChildrenCount(int groupPosition) {
        return 1;
    }

    @Override
    public Object getGroup(int groupPosition) {
        return this.titles.get(groupPosition);
    }

    @Override
    public Object getChild(int groupPosition, int childPosition) {
        return this.hashMap.get(this.titles.get(groupPosition));
    }

    @Override
    public long getGroupId(int groupPosition) {
        return groupPosition;
    }

    @Override
    public long getChildId(int groupPosition, int childPosition) {
        return childPosition;
    }

    @Override
    public boolean hasStableIds() {
        return false;
    }

    @Override
    public View getGroupView(int groupPosition, boolean isExpanded, View convertView, ViewGroup parent) {

        AppliancesData appliancesData = (AppliancesData) getChild(groupPosition, 0);

        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            convertView = inflater.inflate(R.layout.parents_expandable, null);
        }

        ImageView imageView = (ImageView) convertView.findViewById(R.id.itemimageparent);
        TextView title = (TextView) convertView.findViewById(R.id.nameofitemparent);
        TextView category = (TextView) convertView.findViewById(R.id.categoryofitemparent);

        title.setText(appliancesData.getTitle());
        category.setText(appliancesData.getCategory());
        Picasso.get().load(appliancesData.getImageUri()).into(imageView);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final AppliancesData appliancesData = (AppliancesData) getChild(groupPosition, childPosition);

        if (convertView == null) {
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.children_expandable, null);
        }

        CircleImageView circleImageView = convertView.findViewById(R.id.itemimagechild);
        TextView name = (TextView) convertView.findViewById(R.id.titlechild);
        TextView category = (TextView) convertView.findViewById(R.id.categorychild);
        TextView status = (TextView) convertView.findViewById(R.id.statusornotesofchild);
        Button delete = (Button) convertView.findViewById(R.id.delete);
        ImageButton sharingbox = (ImageButton) convertView.findViewById(R.id.sharingbox);

        name.setText(appliancesData.getTitle());
        category.setText(appliancesData.getCategory());
        status.setText(appliancesData.getDescription());
        Picasso.get().load(appliancesData.getImageUri()).into(circleImageView);

        sharingbox.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Picasso.get().load(appliancesData.getImageUri()).into(new Target() {
                    @Override
                    public void onBitmapLoaded(Bitmap bitmap, Picasso.LoadedFrom from) {
                        Intent share = new Intent(Intent.ACTION_SEND);
                        share.setType("image/*");
                        share.putExtra(Intent.EXTRA_TEXT,
                                "Name: "+appliancesData.getTitle()+"\n"+
                                        "Category of the item: "+appliancesData.getCategory()+"\n"+
                                "Status/Notes: "+appliancesData.getDescription()+""
                                );
                        share.putExtra(Intent.EXTRA_SUBJECT, "Appliance Details from Track and Trigger");
                        share.putExtra(Intent.EXTRA_STREAM, getLocalBitmapUri(bitmap));
                        share.addFlags(Intent.FLAG_GRANT_READ_URI_PERMISSION);
                        context.startActivity(Intent.createChooser(share, "Share your appliance"));
                    }

                    @Override
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) {
                    }

                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) {
                    }
                });
            }
        });

        DatabaseReference reference = FirebaseDatabase.getInstance().getReference();
        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        DatabaseReference ref = reference.child(activity).child(user.getUid());

        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        TextView deleteMsg = deleteConfirmView.findViewById(R.id.confirmMsg);
        Button confirm = deleteConfirmView.findViewById(R.id.confirmAction);
        Button cancel = deleteConfirmView.findViewById(R.id.cancelAction);
        builder.setView(deleteConfirmView);
        builder.setCancelable(false);
        AlertDialog deleteDialog = builder.create();
        deleteDialog.setCanceledOnTouchOutside(false);

        deleteMsg.setText("Do you really want to delete this entry?");
        confirm.setText("Confirm Delete");

        confirm.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ref.child(appliancesData.getTitle()).removeValue();
                deleteDialog.cancel();
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.cancel();
            }
        });

        deleteDialog.setOnCancelListener(new DialogInterface.OnCancelListener() {
            @Override
            public void onCancel(DialogInterface dialog) {

            }
        });

        delete.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteDialog.show();
                System.out.println(context.getApplicationContext().toString());
            }
        });

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        convertView.startAnimation(animation);
        return convertView;
    }

    public Uri getLocalBitmapUri(Bitmap bmp) {
        Uri bmpUri = null;
        try {
            File file =  new File(context.getExternalFilesDir(Environment.DIRECTORY_PICTURES), "share_image_" + System.currentTimeMillis() + ".png");
            FileOutputStream out = new FileOutputStream(file);
            bmp.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.close();
            bmpUri = FileProvider.getUriForFile(context, context.getApplicationContext().getPackageName()+".provider", file);;
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bmpUri;
    }
    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}