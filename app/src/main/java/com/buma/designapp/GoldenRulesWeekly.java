package com.buma.designapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.TextView;

import com.buma.utils.LazyAdapterWeekly;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.HashMap;

public class GoldenRulesWeekly extends Activity {

    public static final String KEY_WEEKLY = "ObjectGoldenRules";
    public static final String KEY_ACTIVITY = "Activity";
    public static final String KEY_DATES = "ActitvityID";
    private static final String URL = "http://202.158.42.254/androidserv/services/GoldenRules.asmx?WSDL";
    private static String SOAP_ACTION = "http://tempuri.org/GoldenRulesWeekly";
    private static String NAME_SPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "GoldenRulesWeekly";
    final ArrayList<HashMap<String, String>> EmpList = new ArrayList<HashMap<String, String>>();
    public Calendar month;
    public String NmUser, myPositionId, mySite;
    ListView list;
    LazyAdapterWeekly adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_golden_rules_weekly);

        Intent intent = getIntent();
        NmUser = intent.getExtras().getString("NmUser").toString();
        myPositionId = intent.getExtras().getString("myPositionId").toString();
        mySite = intent.getExtras().getString("mySite").toString();
        month = Calendar.getInstance();

        TextView title = (TextView) findViewById(R.id.gr_bottom_weekly);
        title.setText(Html.fromHtml("W"
                + new GregorianCalendar().get(Calendar.WEEK_OF_YEAR)
                + " : "
                + android.text.format.DateFormat.format("MMMM yyyy",
                Calendar.getInstance())));
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        findViewById(R.id.weekly_footer).setVisibility(View.GONE);
        new LoadWeekly().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.golden_rules_weekly, menu);
        return true;
    }

    class LoadWeekly extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            for (int i = 0; i < 7; i++) {
                try {
                    SoapObject req = new SoapObject(NAME_SPACE, METHOD_NAME);
                    req.addProperty("Hari", String.valueOf(i));

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);

                    envelope.setOutputSoapObject(req);
                    envelope.dotNet = true;
                    try {
                        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                                URL, 500000);
                        androidHttpTransport.call(SOAP_ACTION, envelope);
                        SoapObject result = (SoapObject) envelope.getResponse();

                        HashMap<String, String> map = new HashMap<String, String>();
                        for (int j = 0; j < result.getPropertyCount(); j++) {
                            SoapObject obj = (SoapObject) result.getProperty(j);
                            map.put(KEY_ACTIVITY, obj.getProperty("Dates")
                                    .toString());
                            map.put(KEY_DATES, obj.getProperty("Dates")
                                    .toString());
                        }
                        EmpList.add(map);
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                } catch (Exception ex) {
                    Log.e("Error", ex.getMessage());
                    ex.getStackTrace();
                }
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.weekly_footer).setVisibility(View.VISIBLE);
            runOnUiThread(new Runnable() {
                public void run() {
                    list = (ListView) findViewById(R.id.gr_lst_weekly);
                    list.setItemsCanFocus(true);
                    list.callOnClick();
                    adapter = new LazyAdapterWeekly(GoldenRulesWeekly.this, EmpList);

                    list.setAdapter(adapter);
                    list.setClickable(true);
                    list.setOnItemClickListener(new OnItemClickListener() {
                        @Override
                        public void onItemClick(AdapterView<?> parent, View vi,
                                                int position, long id) {
                            Intent intent = new Intent();
                            intent.putExtra("NmUser", NmUser);
                            intent.putExtra("mySite", mySite);
                            intent.putExtra("myPositionId", myPositionId);
                            intent.putExtra(
                                    "date",
                                    android.text.format.DateFormat.format("yyyy-MM",
                                            month)
                                            + "-"
                                            + EmpList.get(position).get(KEY_DATES));
                            intent.putExtra("tahun", android.text.format.DateFormat
                                    .format("yyyy", month));
                            intent.putExtra("bulan",
                                    android.text.format.DateFormat.format("MM", month));
                            intent.putExtra("tanggal",
                                    EmpList.get(position).get(KEY_DATES));
                            intent.setClass(getApplicationContext(),
                                    GoldenRulesMeetView.class);
                            setResult(RESULT_OK, intent);
                            startActivity(intent);
                        }
                    });
                }
            });
        }
    }
}
