
package com.buma.utils;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.buma.designapp.R;

import java.util.ArrayList;
import java.util.Calendar;

public class WeeklyCalendarAdapter extends BaseAdapter {
    static final int FIRST_DAY_OF_WEEK = 0; // Sunday = 0, Monday = 1
    // references to our items
    public String[] days;
    private Context mContext;
    private java.util.Calendar week;
    private Calendar selectedDate;
    private ArrayList<String> items;

    public WeeklyCalendarAdapter(Context c, Calendar weekCalendar) {
        week = weekCalendar;
        selectedDate = (Calendar) weekCalendar.clone();
        mContext = c;
        week.set(Calendar.DAY_OF_WEEK_IN_MONTH, 1);
        this.items = new ArrayList<String>();
        refreshDays();
    }

    public void setItems(ArrayList<String> items) {
        for (int i = 0; i != items.size(); i++) {
            if (items.get(i).length() == 1) {
                items.set(i, "0" + items.get(i));
            }
        }
        this.items = items;
    }

    public int getCount() {
        return days.length;
    }

    public Object getItem(int position) {
        return null;
    }

    public long getItemId(int position) {
        return 0;
    }

    // create a new view for each item referenced by the Adapter
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        TextView dayView;
        if (convertView == null) {  // if it's not recycled, initialize some attributes
            LayoutInflater vi = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.weekly_calendar_item, null);

        }
        dayView = (TextView) v.findViewById(R.id.weekly_date);

        // disable empty days from the beginning
        if (days[position].equals("")) {
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else {
            // mark current day as focused
            if (week.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) && week.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) && Integer.parseInt(days[position]) < selectedDate.get(Calendar.DAY_OF_MONTH)) {
                v.setBackgroundResource(R.drawable.item_bg_green);
            } else if (week.get(Calendar.YEAR) == selectedDate.get(Calendar.YEAR) && week.get(Calendar.MONTH) == selectedDate.get(Calendar.MONTH) && days[position].equals("" + selectedDate.get(Calendar.DAY_OF_MONTH))) {
                v.setBackgroundResource(R.drawable.item_bg_red);
            } else if (week.get(Calendar.YEAR) >= selectedDate.get(Calendar.YEAR) && week.get(Calendar.MONTH) >= selectedDate.get(Calendar.MONTH) && Integer.parseInt(days[position]) > selectedDate.get(Calendar.DAY_OF_MONTH)) {
                v.setBackgroundResource(R.drawable.list_item_background);
            }
        }
        dayView.setText(days[position]);

        // create date string for comparison
        String date = days[position];

        if (date.length() == 1) {
            date = "0" + date;
        }
        String monthStr = "" + (week.get(Calendar.WEEK_OF_MONTH) + 1);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }
        // show icon if date is not empty and it exists in the items array
        ImageView iw = (ImageView) v.findViewById(R.id.weekly_date_icon);
        if (date.length() > 0 && items != null && items.contains(date)) {
            iw.setVisibility(View.INVISIBLE);
        } else {
            iw.setVisibility(View.INVISIBLE);
        }
        return v;
    }

    public void refreshDays() {
        // clear items
        items.clear();
        int lastDay = 17;// week.getActualMaximum(Calendar.DAY_OF_WEEK);
        System.out.println(" last Day" + lastDay);
        int firstDay = 2; //(int)week.get(Calendar.DAY_OF_WEEK);
        System.out.println(" last Day" + firstDay);
        // figure size of the array
        if (firstDay == 1) {
            days = new String[lastDay + (FIRST_DAY_OF_WEEK * 6)];
        } else {
            days = new String[lastDay + firstDay - (FIRST_DAY_OF_WEEK + 1)];
        }

        int j = FIRST_DAY_OF_WEEK;

        // populate empty days before first real day
        if (firstDay > 1) {
            for (j = 0; j < firstDay - FIRST_DAY_OF_WEEK; j++) {
                days[j] = "";
            }
        } else {
            for (j = 0; j < FIRST_DAY_OF_WEEK * 6; j++) {
                days[j] = "";
            }
            j = FIRST_DAY_OF_WEEK * 6 + 1; // sunday => 1, monday => 7
        }

        // populate days
        int dayNumber = 1;
        System.out.println(" Exe : " + (j - 1));
        System.out.println(" Days Length : " + days.length);
        for (int i = 3; i < days.length; i++) {
            System.out.println(" Test : " + (j - 1));
            days[i] = "" + dayNumber;
            dayNumber++;
        }
    }
}