package com.buma.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.buma.designapp.GoldenRulesMeetView;
import com.buma.designapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class LazyAdapterMeetView extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;

    public LazyAdapterMeetView(Activity a, ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        imageLoader = new ImageLoader(activity.getApplicationContext());
    }

    public int getCount() {
        return data.size();
    }

    public Object getItem(int position) {
        return position;
    }

    public long getItemId(int position) {
        return position;
    }

    public View getView(int position, View convertView, ViewGroup parent) {
        View vi = convertView;
        if (convertView == null)
            vi = inflater.inflate(R.layout.activity_golden_rules_list_pattern, null);

        TextView Name = (TextView) vi.findViewById(R.id.gr_lst_fullname); // fullname
        TextView Position = (TextView) vi.findViewById(R.id.gr_lst_position); // position
        ImageView thumb_image = (ImageView) vi.findViewById(R.id.gr_lst_photo); // thumb image

        HashMap<String, String> emp = new HashMap<String, String>();
        emp = data.get(position);

        // Setting all values in listview
        Name.setText(emp.get(GoldenRulesMeetView.KEY_NAME));
        Position.setText(emp.get(GoldenRulesMeetView.KEY_SEC));
        imageLoader.DisplayImage(emp.get(GoldenRulesMeetView.KEY_THUMB_URL), thumb_image);
        return vi;
    }
}