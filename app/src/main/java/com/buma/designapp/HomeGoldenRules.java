package com.buma.designapp;

import android.app.Activity;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buma.session.SessionManager;
import com.buma.utils.ImageLoader;
import com.buma.utils.LazyAdapterMeetViewDetail;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.HashMap;

public class HomeGoldenRules extends Activity {
    public static final String KEY_HOME = "ObjectGoldenRules"; // parent
    public static final String KEY_POSITIONID = "PositionID";
    public static final String KEY_NIK = "Nik";
    public static final String KEY_NAME = "Name";
    public static final String KEY_POSITION = "Sec";
    public static final String KEY_SITE = "Site";
    public static final String KEY_LEFT = "LastLeft";
    static final String URL = "http://202.158.42.254/androidserv/services/GoldenRules.asmx?WSDL";
    public static String StrJobsite;
    public static String nik;
    // untuk MeetViewDetail
    private static String SOAP_ACTION = "http://tempuri.org/WelcomeUser";
    private static String NAME_SPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "WelcomeUser";
    public ImageLoader imageLoader;
    LazyAdapterMeetViewDetail adapter;
    SessionManager session;
    private Button BtnActivities;
    @SuppressWarnings("unused")
    private Button BtnEscalation;
    @SuppressWarnings("unused")
    private Button BtnHelp;
    private TextView TvFullname, TvPosition, TvJobsite;
    private String StrFullName, StrPosition, ImgUrl;
    private String myPositionId;
    private ImageView ImgUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home_golden_rules);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        findViewById(R.id.gr_btn_activities).setEnabled(false);
        new LoadHomeGoldenRules().execute();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.home_golden_rules, menu);
        return false;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MainMenu.class);
                setResult(RESULT_OK, intent);
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    class LoadHomeGoldenRules extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... arg0) {
            try {
                session = new SessionManager(getApplicationContext());
                session.checkLogin();
                if (session.isLoggedIn()) {

                    HashMap<String, String> user = session.getUserDetails();
                    nik = user.get(SessionManager.KEY_EMPLOYEEID).toString();

                    try {
                        SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);
                        request.addProperty("Nik", nik);
                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                                SoapEnvelope.VER11);

                        envelope.setOutputSoapObject(request);
                        envelope.dotNet = true;

                        try {
                            HttpTransportSE androidHttpTransport = new HttpTransportSE(
                                    URL, 500000);
                            androidHttpTransport.call(SOAP_ACTION, envelope);
                            SoapObject result = (SoapObject) envelope.getResponse();

                            for (int i = 0; i < 1; i++) {
                                SoapObject obj = (SoapObject) result.getProperty(i);
                                myPositionId = obj.getProperty("PositionID").toString();
                                StrFullName = obj.getProperty("Name").toString();
                                StrPosition = obj.getProperty("Sec").toString();
                                StrJobsite = obj.getProperty("Site").toString();
                                ImgUrl = obj.getProperty("Images").toString();

                                TvFullname = (TextView) findViewById(R.id.gr_text_fullname);
                                TvPosition = (TextView) findViewById(R.id.gr_text_position);
                                TvJobsite = (TextView) findViewById(R.id.gr_text_jobsite);
                                ImgUser = (ImageView) findViewById(R.id.gr_list_image);
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                    } catch (Exception ex) {
                        Log.e("Error", ex.getMessage());
                        ex.getStackTrace();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Belum Login",
                            Toast.LENGTH_LONG).show();

                }

            } catch (NullPointerException e) {
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), MainMenu.class);
                startActivity(intent);
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            findViewById(R.id.gr_btn_activities).setEnabled(true);
            findViewById(R.id.gr_btn_activities).setVisibility(View.SOUND_EFFECTS_ENABLED);

            runOnUiThread(new Runnable() {
                public void run() {
                    TvFullname.setText(Html.fromHtml(StrFullName));
                    TvPosition.setText(Html.fromHtml(StrPosition));
                    TvJobsite.setText(Html.fromHtml(StrJobsite));
                    imageLoader = new ImageLoader(getApplicationContext());
                    imageLoader.DisplayImage(ImgUrl, ImgUser);

                    BtnActivities = (Button) findViewById(R.id.gr_btn_activities);
                    BtnActivities.setOnClickListener(new View.OnClickListener() {

                        @Override
                        public void onClick(View v) {
                            Intent myIntent = new Intent();
                            myIntent.putExtra("mySite", StrJobsite);
                            myIntent.putExtra("myPositionId", myPositionId);
                            myIntent.putExtra("NmUser", StrFullName);
                            myIntent.setClass(HomeGoldenRules.this,
                                    TabGoldenRules.class);

                            startActivity(myIntent);
                        }
                    });

                    BtnEscalation = (Button) findViewById(R.id.gr_btn_escalation);
                    BtnHelp = (Button) findViewById(R.id.gr_btn_help);
                }
            });
        }
    }

}
