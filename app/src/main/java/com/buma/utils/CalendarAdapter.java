package com.buma.utils;

import android.content.Context;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.buma.designapp.GoldenRulesMonthly;
import com.buma.designapp.R;
import com.buma.session.SessionManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

public class CalendarAdapter extends BaseAdapter {
    public static final String KEY_INDICATOR = "ObjectGoldenRules";
    public static final String KEY_KPI = "KPI";
    static final int FIRST_DAY_OF_WEEK = 0; // Sunday = 0, Monday = 1
    static final String URL = "http://202.158.42.254/androidserv/services/GoldenRules.asmx?WSDL";
    private static String SOAP_ACTION = "http://tempuri.org/CountDataMonitor";
    private static String NAME_SPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "CountDataMonitor";
    final ArrayList<HashMap<String, String>> EmpList = new ArrayList<HashMap<String, String>>();
    // references to our items
    public String[] days;
    SessionManager session;
    private Context mContext;
    private java.util.Calendar month;
    private Calendar selectedDate;
    private ArrayList<String> items;
    private String nik, bln, thn;

    public CalendarAdapter(Context c, Calendar monthCalendar) {
        month = monthCalendar;
        nik = GoldenRulesMonthly.Nik;
        bln = String.valueOf(month.get(Calendar.MONTH) + 1);
        thn = String.valueOf(month.get(Calendar.YEAR));
        getIndicator();
        selectedDate = (Calendar) monthCalendar.clone();
        mContext = c;
        month.set(Calendar.DAY_OF_MONTH, 1);
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
        if (convertView == null) { // if it's not recycled, initialize some
            // attributes
            LayoutInflater vi = (LayoutInflater) mContext
                    .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = vi.inflate(R.layout.calendar_item, null);

        }
        dayView = (TextView) v.findViewById(R.id.date);

        // disable empty days from the beginning
        if (days[position].equals("")) {
            dayView.setClickable(false);
            dayView.setFocusable(false);
        } else {
            // mark current day as focused
            if (month.get(Calendar.YEAR) >= selectedDate.get(Calendar.YEAR)
                    && month.get(Calendar.MONTH) >= selectedDate
                    .get(Calendar.MONTH)
                    && Integer.parseInt(days[position]) <= selectedDate
                    .get(Calendar.DAY_OF_MONTH)) {
                v.setBackgroundResource(R.drawable.item_bg_red);

                for (int idx = 0; idx < EmpList.size(); idx++) {
                    int tgl = Integer.parseInt(EmpList.get(idx).get("Tgl"));
                    String bgColor = EmpList.get(idx).get("Background");
                    String tglStr = "" + tgl;
                    if (tglStr.equals(days[position])) {
                        if (bgColor.equalsIgnoreCase("yellow")) {
                            v.setBackgroundResource(R.drawable.item_bg_yellow);
                        } else {
                            v.setBackgroundResource(R.drawable.item_bg_green);
                        }
                    }
                }
            } else if (month.get(Calendar.YEAR) >= selectedDate
                    .get(Calendar.YEAR)
                    && month.get(Calendar.MONTH) >= selectedDate
                    .get(Calendar.MONTH)
                    && Integer.parseInt(days[position]) > selectedDate
                    .get(Calendar.DAY_OF_MONTH)) {
                v.setBackgroundResource(R.drawable.list_item_background);
                dayView.setClickable(false);
                dayView.setFocusable(false);
            }
        }
        dayView.setText(days[position]);

        String date = days[position];

        if (date.length() == 1) {
            date = "0" + date;
        }

        String monthStr = "" + (month.get(Calendar.MONTH) + 1);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }
        return v;
    }

    public void refreshDays() {
        // clear items
        items.clear();

        int lastDay = month.getActualMaximum(Calendar.DAY_OF_MONTH);

        int firstDay = month.get(Calendar.DAY_OF_WEEK);

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
        for (int i = j - 1; i < days.length; i++) {
            days[i] = "" + dayNumber;
            dayNumber++;
        }
    }

    public void getIndicator() {
        try {
            // Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);

            // Use this to add parameters
            request.addProperty("UserID", nik);
            request.addProperty("mySite", GoldenRulesMonthly.mySite);
            request.addProperty("month", bln);
            request.addProperty("year", thn);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL, 500000);

                // this is the actual part that will call the webservice
                androidHttpTransport.call(SOAP_ACTION, envelope);
                SoapObject result = (SoapObject) envelope.getResponse();
                for (int i = 0; i < result.getPropertyCount(); i++) {
                    SoapObject obj = (SoapObject) result.getProperty(i);
                    HashMap<String, String> map = new HashMap<String, String>();

                    map.put("Tgl", obj.getProperty("Tgl").toString());
                    map.put("Background", obj.getProperty("ColorIndicator")
                            .toString());
                    // adding HashList to ArrayList
                    EmpList.add(map);
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
            ex.getStackTrace();
        }
    }
}
