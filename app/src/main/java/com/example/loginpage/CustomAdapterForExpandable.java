package com.example.loginpage;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.Drawable;
import android.net.Uri;
import android.os.Environment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.core.content.FileProvider;

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

    public CustomAdapterForExpandable(Context context, List<String> titles, HashMap<String, AppliancesData> hashMap) {
        this.context = context;
        this.titles = titles;
        this.hashMap = hashMap;
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
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), appliancesData.getImageUri());
        Picasso.get().load(appliancesData.getImageUri()).into(imageView);
        return convertView;
    }

    @Override
    public View getChildView(int groupPosition, int childPosition, boolean isLastChild, View convertView, ViewGroup parent) {
        final AppliancesData appliancesData = (AppliancesData) getChild(groupPosition, childPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.children_expandable, null);
        }
        CircleImageView circleImageView = convertView.findViewById(R.id.itemimagechild);
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), appliancesData.getImageUri());
        TextView name = (TextView) convertView.findViewById(R.id.titlechild);
        TextView category = (TextView) convertView.findViewById(R.id.categorychild);
        TextView status = (TextView) convertView.findViewById(R.id.statusornotesofchild);
        name.setText(appliancesData.getTitle());
        category.setText(appliancesData.getCategory());
        status.setText(appliancesData.getDescription());
        Picasso.get().load(appliancesData.getImageUri()).into(circleImageView);

        ImageButton sharingbox = (ImageButton) convertView.findViewById(R.id.sharingbox);
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
                    public void onBitmapFailed(Exception e, Drawable errorDrawable) { }
                    @Override
                    public void onPrepareLoad(Drawable placeHolderDrawable) { }
                });
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