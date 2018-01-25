package com.buma.designapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.text.Html;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.TextView;

import com.buma.session.SessionManager;
import com.buma.utils.CalendarAdapter;
import com.buma.utils.DaysAdapter;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;
import java.util.Random;

public class GoldenRulesMonthly extends Activity {
    public static String Nik;
    public static String NmUser, mySite, myPositionId;
    public Calendar month;
    public CalendarAdapter adapter;
    public DaysAdapter daysadapter;
    public Handler handler;
    public ArrayList<String> items;
    public ArrayList<String> days;
    public Runnable calendarUpdater = new Runnable() {
        @Override
        public void run() {
            items.clear();
            for (int i = 0; i < 31; i++) {
                Random r = new Random();

                if (r.nextInt(10) > 6) {
                    items.add(Integer.toString(i));
                }
            }
            adapter.setItems(items);
            adapter.notifyDataSetChanged();
        }
    };
    SessionManager session;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_golden_rules_monthly);
        Intent intent = getIntent();

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        if (session.isLoggedIn()) {
            HashMap<String, String> user = session.getUserDetails();
            Nik = user.get(SessionManager.KEY_EMPLOYEEID).toString();
        }

        NmUser = intent.getExtras().getString("NmUser").toString();
        myPositionId = intent.getExtras().getString("myPositionId").toString();
        mySite = intent.getExtras().getString("mySite").toString();

        daystring();
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        findViewById(R.id.header).setVisibility(View.GONE);
        new LoadMonthly().execute();
    }

    public void refreshCalendar() {
        TextView title = (TextView) findViewById(R.id.title);

        adapter.refreshDays();
        adapter.notifyDataSetChanged();
        handler.post(calendarUpdater);

        title.setText(android.text.format.DateFormat.format("MMMM yyyy", month));
    }

    public void onNewIntent(Intent intent) {
        String date = intent.getStringExtra("date");
        String[] dateArr = date.split("-");
        month.set(Integer.parseInt(dateArr[0]), Integer.parseInt(dateArr[1]),
                Integer.parseInt(dateArr[2]));
    }

    public void daystring() {
        days = new ArrayList<String>();
        days.add("Mi");
        days.add("Sn");
        days.add("Se");
        days.add("Ra");
        days.add("Ka");
        days.add("Ju");
        days.add("Sa");
    }

    class LoadMonthly extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            daysadapter = new DaysAdapter(GoldenRulesMonthly.this, days);

            month = Calendar.getInstance();
            items = new ArrayList<String>();
            adapter = new CalendarAdapter(GoldenRulesMonthly.this, month);
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.header).setVisibility(View.VISIBLE);
            runOnUiThread(new Runnable() {
                public void run() {
                    GridView gridviewdays = (GridView) findViewById(R.id.gridviewdays);
                    gridviewdays.setAdapter(daysadapter);
                    GridView gridview = (GridView) findViewById(R.id.gridview);
                    gridview.setAdapter(adapter);

                    handler = new Handler();
                    handler.post(calendarUpdater);

                    TextView title = (TextView) findViewById(R.id.month_title);
                    title.setText(Html.fromHtml("W"
                            + new GregorianCalendar().get(Calendar.WEEK_OF_YEAR) + " : "
                            + android.text.format.DateFormat.format("MMMM yyyy", month)));

                    gridview.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent, View v,
                                                int position, long id) {
                            TextView date = (TextView) v.findViewById(R.id.date);
                            if (date instanceof TextView && !date.getText().equals("")) {

                                Intent intent = new Intent();
                                String day = date.getText().toString();
                                if (day.length() == 1) {
                                    day = "0" + day;
                                }
                                intent.putExtra("NmUser", NmUser);
                                intent.putExtra("mySite", mySite);
                                intent.putExtra("myPositionId", myPositionId);
                                intent.putExtra(
                                        "date",
                                        android.text.format.DateFormat.format("yyyy-MM",
                                                month) + "-" + day);
                                intent.putExtra("tahun", android.text.format.DateFormat
                                        .format("yyyy", month));
                                intent.putExtra("bulan",
                                        android.text.format.DateFormat.format("MM", month));
                                intent.putExtra("tanggal", day);
                                intent.setClass(getApplicationContext(),
                                        GoldenRulesMeetView.class);
                                setResult(RESULT_OK, intent);
                                startActivity(intent);
                            }
                        }
                    });
                }
            });
        }
    }
}
