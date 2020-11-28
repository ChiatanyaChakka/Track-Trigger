package com.example.loginpage;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.BaseExpandableListAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.squareup.picasso.Picasso;

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
        AppliancesData appliancesData = (AppliancesData) getChild(groupPosition, childPosition);
        if(convertView == null){
            LayoutInflater inflater = (LayoutInflater) this.context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

            convertView = inflater.inflate(R.layout.children_expandable, null);
        }
        CircleImageView circleImageView = convertView.findViewById(R.id.itemimagechild);
        //Bitmap bitmap = BitmapFactory.decodeResource(context.getResources(), appliancesData.getImageUri());
        Picasso.get().load(appliancesData.getImageUri()).into(circleImageView);
        TextView name = (TextView) convertView.findViewById(R.id.titlechild);
        TextView category = (TextView) convertView.findViewById(R.id.categorychild);
        TextView status = (TextView) convertView.findViewById(R.id.statusornotesofchild);
        name.setText(appliancesData.getTitle());
        category.setText(appliancesData.getCategory());
        status.setText(appliancesData.getDescription());

        Animation animation = AnimationUtils.loadAnimation(context, android.R.anim.fade_in);
        convertView.startAnimation(animation);
        return convertView;
    }


    @Override
    public boolean isChildSelectable(int groupPosition, int childPosition) {
        return true;
    }

}