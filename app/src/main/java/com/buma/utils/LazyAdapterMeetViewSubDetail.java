package com.buma.utils;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.CheckBox;

import com.buma.designapp.GoldenRulesMeetViewSubDetail;
import com.buma.designapp.R;

import java.util.ArrayList;
import java.util.HashMap;

public class LazyAdapterMeetViewSubDetail extends BaseAdapter {

    private static LayoutInflater inflater = null;
    public ImageLoader imageLoader;
    private Activity activity;
    private ArrayList<HashMap<String, String>> data;

    public LazyAdapterMeetViewSubDetail(Activity a,
                                        ArrayList<HashMap<String, String>> d) {
        activity = a;
        data = d;
        inflater = (LayoutInflater) activity
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
            vi = inflater.inflate(
                    R.layout.activity_golden_rules_list_pattern_sub_detail,
                    null);

        CheckBox point = (CheckBox) vi.findViewById(R.id.gr_lst_sub_checkbox); // Question

        HashMap<String, String> emp = new HashMap<String, String>();
        emp = data.get(position);

        // Setting all values in listview
        point.setText(emp.get(GoldenRulesMeetViewSubDetail.KEY_POINT));
        if (emp.get(GoldenRulesMeetViewSubDetail.KEY_KPI).toString().equals("-")) {
            point.setChecked(false);
        } else {
            point.setChecked(true);
        }
        return vi;
    }

    public String CheckList(String TEST) {

        return "";
    }

}