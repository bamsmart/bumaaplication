package com.buma.utils;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.buma.designapp.GoldenRulesWeekly;
import com.buma.designapp.HomeGoldenRules;
import com.buma.designapp.R;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

@SuppressLint("SimpleDateFormat")
public class LazyAdapterWeekly extends BaseAdapter {

    static final String URL = "http://202.158.42.254/androidserv/services/GoldenRules.asmx?WSDL";
    private static LayoutInflater inflater = null;
    private static String SOAP_ACTION = "http://tempuri.org/CountDataMonitor";
    private static String NAME_SPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "CountDataMonitor";
    final ArrayList<HashMap<String, String>> EmpList = new ArrayList<HashMap<String, String>>();
    public ImageLoader imageLoader;
    public Calendar month;
    private Activity activity;
    private Button Tanggal;
    private ArrayList<HashMap<String, String>> data;
    private String nik, bln, thn;

    public LazyAdapterWeekly(Activity a, ArrayList<HashMap<String, String>> d) {
        month = Calendar.getInstance();
        nik = HomeGoldenRules.nik;
        bln = String.valueOf(month.get(Calendar.MONTH) + 1);
        thn = String.valueOf(month.get(Calendar.YEAR));
        GetIndicator(nik, bln, thn);
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
            vi = inflater.inflate(R.layout.golden_rules_weekly_pattern, null);

        ImageView side_color = (ImageView) vi.findViewById(R.id.weekly_side_color); // side color

        TextView Activity = (TextView) vi.findViewById(R.id.gr_title_activities); //
        Tanggal = (Button) vi.findViewById(R.id.gr_btn_tanggal); //

        HashMap<String, String> emp = new HashMap<String, String>();
        emp = data.get(position);

        Tanggal.setText(emp.get(GoldenRulesWeekly.KEY_DATES));
        Tanggal.setBackgroundResource(R.drawable.bg_btn_dates);
        Tanggal.setTextColor(Color.BLACK);

        SimpleDateFormat df = new SimpleDateFormat("dd");
        Calendar c = Calendar.getInstance();
        String date = df.format(c.getTime());

        String monthStr = "" + (month.get(Calendar.MONTH) + 1);
        if (monthStr.length() == 1) {
            monthStr = "0" + monthStr;
        }

        if (Integer.parseInt(emp.get(GoldenRulesWeekly.KEY_DATES)) <= Integer.parseInt(date)) {

            Activity.setText("Tidak ada aktivitas");
            Tanggal.setBackgroundResource(R.drawable.bg_btn_dates_red);
            Tanggal.setTextColor(Color.WHITE);
            side_color.setBackgroundResource(R.drawable.weekly_side_red_background);
            vi.setBackgroundResource(R.drawable.weekly_bg_red);

            for (int idx = 0; idx < EmpList.size(); idx++) {
                int tgl = Integer.parseInt(EmpList.get(idx).get("Tgl"));
                String bgColor = EmpList.get(idx).get("Background");
                String tglStr = "" + tgl;
                if (tglStr.equalsIgnoreCase(emp.get(GoldenRulesWeekly.KEY_DATES))) {
                    if (bgColor.equalsIgnoreCase("yellow")) {
                        Activity.setText("sebagian aktivitas selesai");
                        Tanggal.setBackgroundResource(R.drawable.bg_btn_dates_yellow);
                        vi.setBackgroundResource(R.drawable.weekly_bg_yellow);
                        side_color.setBackgroundResource(R.drawable.weekly_side_yellow_background);
                    } else {
                        Activity.setText("Semua aktivitas selesai");
                        Tanggal.setBackgroundResource(R.drawable.bg_btn_dates_green);
                        vi.setBackgroundResource(R.drawable.weekly_bg_green);
                        side_color.setBackgroundResource(R.drawable.weekly_side_green_background);
                    }
                }
            }
        } else {
            Activity.setText("Aktivitas yang direncanakan");
            vi.setBackgroundResource(R.drawable.weekly_bg_grey);
            side_color.setBackgroundResource(R.drawable.weekly_side_grey_background);
        }


        return vi;
    }

    public void GetIndicator(String UserId, String Month, String Year) {
        try {
            // Initialize soap request + add parameters
            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);

            // Use this to add parameters
            request.addProperty("UserID", UserId);
            request.addProperty("mySite", HomeGoldenRules.StrJobsite);
            request.addProperty("month", Month);
            request.addProperty("year", Year);

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