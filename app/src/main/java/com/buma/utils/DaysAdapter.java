
package com.buma.utils;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buma.designapp.R;

import java.util.ArrayList;

public class DaysAdapter extends BaseAdapter {

    private Activity activity;
    private ArrayList<String> daystring;

    public DaysAdapter(Activity activity, ArrayList<String> daystring) {
        super();
        this.activity = activity;
        this.daystring = daystring;
    }

    public void setItems(ArrayList<String> items) {

    }

    public int getCount() {
        return daystring.size();
    }

    public String getItem(int position) {
        return daystring.get(position);
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder view;
        LayoutInflater inflator = activity.getLayoutInflater();

        if (convertView == null) {
            view = new ViewHolder();
            convertView = inflator.inflate(R.layout.days_item, null);

            view.daytext = (TextView) convertView.findViewById(R.id.days_item);

            convertView.setTag(view);
        } else {
            view = (ViewHolder) convertView.getTag();

        }
        view.daytext.setText(daystring.get(position));

        return convertView;
    }

    public static class ViewHolder {
        public TextView daytext;
    }


}