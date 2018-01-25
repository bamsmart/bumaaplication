package com.buma.designapp;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.buma.session.SessionManager;
import com.buma.utils.LazyAdapterMeetView;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint({"ShowToast", "SimpleDateFormat"})
public class GoldenRulesMeetView extends Activity {
    // XML node keys
    public static final String KEY_MEETVIEW = "ObjectGoldenRules";
    public static final String KEY_POSISTIONID = "PositionID";
    public static final String KEY_NIK = "Nik";
    public static final String KEY_NAME = "Name";
    public static final String KEY_SEC = "Sec";
    public static final String KEY_THUMB_URL = "Images";
    // Attribute User
    static final int PICK_DATE_REQUEST = 1;
    static final String URL = "http://202.158.42.254/androidserv/services/GoldenRules.asmx?WSDL";
    public static int day;
    private static String SOAP_ACTION = "http://tempuri.org/MeetView";
    private static String NAME_SPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "MeetView";
    final ArrayList<HashMap<String, String>> EmpList = new ArrayList<HashMap<String, String>>();
    ListView list;
    LazyAdapterMeetView adapter;
    SessionManager session;
    private TextView TvNmUser, TvDeskripsi;
    private String NmUser, Deskripsi;
    private String dates, tanggal;
    private String mySite, myPositionId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_golden_rules_meet_view);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color=\"#c65910\">"
                + "BUMA Golden <b>Rules<b>" + "</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#f79e21")));
        Intent intent = getIntent();

        TvNmUser = (TextView) findViewById(R.id.GrTvUser);
        TvNmUser.setText(Html.fromHtml(intent.getExtras().getString("NmUser")
                .toString()));
        NmUser = intent.getExtras().getString("NmUser").toString();
        myPositionId = intent.getExtras().getString("myPositionId").toString();
        mySite = intent.getExtras().getString("mySite").toString();
        TvDeskripsi = (TextView) findViewById(R.id.GrTvDeskripsi);

        tanggal = intent.getExtras().getString("tanggal").toString();
        dates = intent.getExtras().getString("date").toString();
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Calendar c = Calendar.getInstance();
        try {
            c.setTime(df.parse(dates));
        } catch (ParseException e1) {
            e1.printStackTrace();
        }
        day = c.get(Calendar.DAY_OF_WEEK);

        Deskripsi = "Hari ini adalah hari "
                + getDay(day)
                + " "
                + tanggal
                + " "
                + getMonth((Integer.parseInt(intent.getExtras()
                .getString("bulan").toString())))
                + " "
                + intent.getExtras().getString("date").toString()
                .substring(0, 4);
        Deskripsi = Deskripsi + " , anda diharapkan bertemu dengan : ";
        TvDeskripsi.setText(Html.fromHtml(Deskripsi));
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        new LoadView().execute();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == PICK_DATE_REQUEST) {
            if (resultCode == RESULT_OK) {
                // Toast.makeText(getApplicationContext(),
                // data.getStringExtra("date"), Toast.LENGTH_SHORT).show();
                // String[] dateArr = data.getStringExtra("date").split("-");
                // DatePicker dp = (DatePicker)findViewById(R.id.datePicker1);
                // dp.updateDate(Integer.parseInt(dateArr[0]),
                // Integer.parseInt(dateArr[1]), Integer.parseInt(dateArr[2]));
            }
        }
    }

    public String getDay(int arg) {
        String hari = "";
        switch (arg) {
            case 1:
                hari = "Minggu";
                break;
            case 2:
                hari = "Senin";
                break;
            case 3:
                hari = "Selasa";
                break;
            case 4:
                hari = "Rabu";
                break;
            case 5:
                hari = "Kamis";
                break;
            case 6:
                hari = "Jum'at";
                break;
            case 7:
                hari = "Sabtu";
                break;
            default:
                hari = "Minggu";
                break;
        }
        return hari;
    }

    public String getMonth(int arg) {
        String bulan = "";
        switch (arg) {
            case 1:
                bulan = "Januari";
                break;
            case 2:
                bulan = "Februari";
                break;
            case 3:
                bulan = "Maret";
                break;
            case 4:
                bulan = "April";
                break;
            case 5:
                bulan = "Mei";
                break;
            case 6:
                bulan = "Juni";
                break;
            case 7:
                bulan = "Juli";
                break;
            case 8:
                bulan = "Agustus";
                break;
            case 9:
                bulan = "September";
                break;
            case 10:
                bulan = "Oktober";
                break;
            case 11:
                bulan = "November";
                break;
            case 12:
                bulan = "Desember";
                break;
            default:
                bulan = "Januari";
                break;
        }
        return bulan;
    }

    // inisiate optionmenu
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.golden_rules_meet_view, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.putExtra("NmUser", NmUser);
                intent.putExtra("mySite", mySite);
                intent.putExtra("myPositionId", myPositionId);
                intent.setClass(getApplicationContext(), TabGoldenRules.class);

                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class LoadView extends AsyncTask<String, String, String> {
        protected String doInBackground(String... args) {
            try {
                Thread.sleep(10);
                try {
                    SoapObject req = new SoapObject(NAME_SPACE, METHOD_NAME);

                    // sending paratemeter
                    req.addProperty("currentDay", day);
                    req.addProperty("myPositionId", myPositionId);
                    req.addProperty("mySite", mySite);
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);

                    envelope.setOutputSoapObject(req);
                    envelope.dotNet = true;

                    try {
                        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                                URL);

                        // call webservices
                        androidHttpTransport.call(SOAP_ACTION, envelope);
                        SoapObject result = (SoapObject) envelope.getResponse();

                        SoapObject o = (SoapObject) result.getProperty(0);
                        if (o.getProperty("Name").toString().equals("-")) {
                            HashMap<String, String> map = new HashMap<String, String>();

                            map.put(KEY_POSISTIONID, "None");
                            map.put(KEY_NAME, "None");
                            map.put(KEY_SEC, "--------------------");
                            map.put(KEY_THUMB_URL, "");
                            EmpList.add(map);
                        } else {
                            for (int i = 0; i < result.getPropertyCount(); i++) {

                                SoapObject obj = (SoapObject) result
                                        .getProperty(i);

                                // creating new HashMap
                                HashMap<String, String> map = new HashMap<String, String>();

                                map.put(KEY_POSISTIONID,
                                        obj.getProperty("PositionID")
                                                .toString());
                                map.put(KEY_NAME, obj.getProperty("Name")
                                        .toString());
                                map.put(KEY_SEC, obj.getProperty("Sec")
                                        .toString());
                                map.put(KEY_THUMB_URL, obj
                                        .getProperty("Images").toString());
                                EmpList.add(map);
                            }
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception ex) {
                    Log.e("Error", "Loading exception");
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            runOnUiThread(new Runnable() {
                public void run() {
                    list = (ListView) findViewById(R.id.gr_user_list_view);

                    adapter = new LazyAdapterMeetView(GoldenRulesMeetView.this,
                            EmpList);

                    list.setAdapter(adapter);

                    list.setOnItemClickListener(new OnItemClickListener() {

                        public void onItemClick(AdapterView<?> parent, View vi,
                                                int position, long id) {

                            if (!EmpList.get(0).get("Name").equals("None")) {
                                Intent myIntent = new Intent();
                                myIntent.putExtra("NmUser", NmUser);
                                myIntent.putExtra("Deskripsi", Deskripsi);
                                myIntent.putExtra("currentDay", "" + day);
                                // ======================================================================
                                myIntent.putExtra(
                                        "PositionId",
                                        EmpList.get(position).get(
                                                KEY_POSISTIONID));
                                myIntent.putExtra("myPositionId", myPositionId);
                                myIntent.putExtra("mySite", mySite);
                                myIntent.putExtra("Name", EmpList.get(position)
                                        .get(KEY_NAME));
                                myIntent.putExtra("Sec", EmpList.get(position)
                                        .get(KEY_SEC));
                                myIntent.putExtra("Images",
                                        EmpList.get(position)
                                                .get(KEY_THUMB_URL));
                                myIntent.setClass(GoldenRulesMeetView.this,
                                        GoldenRulesMeetViewDetail.class);
                                startActivity(myIntent);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        "Anda tidak memiliki jadwal . ",
                                        Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            });
        }
    }
}
