package com.buma.designapp;

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
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.buma.utils.ImageLoader;
import com.buma.utils.LazyAdapterMeetViewDetail;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GoldenRulesMeetViewDetail extends Activity {
    public static final String KEY_MEETVIEWDETAIL = "ObjectGoldenRules"; // parent
    public static final String KEY_TAKSID = "TaskID";
    public static final String KEY_ACTIVITYID = "ActitvityID";
    public static final String KEY_ACTIVITYTASK = "ActivityTask";
    static final String URL = "http://202.158.42.254/androidserv/services/GoldenRules.asmx?WSDL";
    // untuk MeetViewDetail
    private static String SOAP_ACTION = "http://tempuri.org/MeetViewDetail";
    private static String NAME_SPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "MeetViewDetail";
    final ArrayList<HashMap<String, String>> EmpList = new ArrayList<HashMap<String, String>>();
    public ImageLoader imageLoader;
    public Calendar month;
    LazyAdapterMeetViewDetail adapter;
    // Attributes
    private TextView TvNmUser, TvDeskripsi;
    private TextView TvNmPIC, TvPositionPIC;
    private ImageView thumb_image;
    private String currentDay, PositionId, NmUser, Deskripsi, NmPIC, Position, myPositionId, mySite;
    private String ImgURL = "";
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_golden_rules_meet_view_detail);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color=\"#c65910\">" + "BUMA Golden <b>Rules<b>" + "</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.parseColor("#f79e21")));
        Intent intent = getIntent();

        month = Calendar.getInstance();

        currentDay = intent.getExtras().getString("currentDay");
        PositionId = intent.getExtras().getString("PositionId");
        mySite = intent.getExtras().getString("mySite");
        myPositionId = intent.getExtras().getString("myPositionId");
        NmUser = intent.getExtras().getString("NmUser").toString();
        Deskripsi = intent.getExtras().getString("Deskripsi").toString();
        NmPIC = intent.getExtras().getString("Name").toString();
        Position = intent.getExtras().getString("Sec").toString();

        TvNmUser = (TextView) findViewById(R.id.GrTvUserDetail);
        TvNmUser.setText(Html.fromHtml(intent.getExtras().getString("NmUser")));
        TvDeskripsi = (TextView) findViewById(R.id.GrTvDeskripsiDetail);
        TvDeskripsi.setText(Html.fromHtml(intent.getExtras().getString(
                "Deskripsi")));

        TvNmPIC = (TextView) findViewById(R.id.gr_lst_fullname);
        TvNmPIC.setText(Html.fromHtml(intent.getExtras().getString("Name")));
        TvPositionPIC = (TextView) findViewById(R.id.gr_lst_position);
        TvPositionPIC.setText(Html
                .fromHtml(intent.getExtras().getString("Sec")));

        ImgURL = intent.getExtras().getString("Images");
        thumb_image = (ImageView) findViewById(R.id.gr_lst_photo);

        imageLoader = new ImageLoader(getApplicationContext());
        imageLoader.DisplayImage(ImgURL, thumb_image);
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        new LoadMeetView().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:

                Intent intent = new Intent();
                String day = Deskripsi.split(" ")[5];
                if (day.length() == 1) {
                    day = "0" + day;
                }

                intent.putExtra("NmUser", NmUser);
                intent.putExtra("myPositionId", myPositionId);
                intent.putExtra("mySite", mySite);
                System.out.println("meetviewdetail " + PositionId + " " + mySite);
                intent.putExtra("date", android.text.format.DateFormat.format("yyyy-MM", month) + "-" + day);
                intent.putExtra("tahun", android.text.format.DateFormat.format("yyyy", month));
                intent.putExtra("bulan", android.text.format.DateFormat.format("MM", month));
                intent.putExtra("tanggal", day);
                intent.setClass(getApplicationContext(), GoldenRulesMeetView.class);
                setResult(RESULT_OK, intent);
                NavUtils.navigateUpTo(this, intent);
                return true;
            case R.id.action_home:
                Intent i = new Intent();
                i.putExtra("NmUser", NmUser);
                i.putExtra("mySite", mySite);
                i.putExtra("myPositionId", myPositionId);
                i.setClass(getApplicationContext(), TabGoldenRules.class);
                startActivity(i);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.golden_rules_meet_view_detail, menu);

        return true;
    }

    class LoadMeetView extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {

                Intent intent = getIntent();
                // Initialize soap request + add parameters
                SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);

                // Use this to add parameters
                request.addProperty("PositionID",
                        intent.getExtras().getString("PositionId")); // positionID

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);

                envelope.setOutputSoapObject(request);
                envelope.dotNet = true;

                try {
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

                    // this is the actual part that will call the webservice
                    androidHttpTransport.call(SOAP_ACTION, envelope);
                    SoapObject result = (SoapObject) envelope.getResponse();
                    for (int i = 0; i < result.getPropertyCount(); i++) {
                        SoapObject obj = (SoapObject) result.getProperty(i);

                        // creating new HashMap
                        HashMap<String, String> map = new HashMap<String, String>();

                        // adding each child node to HashMap key => value
                        map.put(KEY_TAKSID, obj.getProperty("TaskID").toString());
                        map.put(KEY_ACTIVITYID, obj.getProperty("ActivityID")
                                .toString());
                        map.put(KEY_ACTIVITYTASK, obj.getProperty("ActivityTask")
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
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            findViewById(R.id.loadingPanel).setVisibility(View.GONE);
            runOnUiThread(new Runnable() {
                public void run() {
                    list = (ListView) findViewById(R.id.gr_user_list_view_detail);

                    // Getting adapter by passing xml data ArrayList
                    adapter = new LazyAdapterMeetViewDetail(GoldenRulesMeetViewDetail.this, EmpList);

                    list.setAdapter(adapter);

                    list.setOnItemClickListener(new OnItemClickListener() {

                        public void onItemClick(AdapterView<?> parent, View view,
                                                int position, long id) {

                            Intent myIntent = new Intent();
                            // ====================================================================
                            myIntent.putExtra("currentDay", currentDay);
                            myIntent.putExtra("NmUser", NmUser);
                            myIntent.putExtra("mySite", mySite);
                            myIntent.putExtra("Deskripsi", Deskripsi);
                            // ====================================================================
                            myIntent.putExtra("NmPIC", NmPIC);
                            myIntent.putExtra("Position", Position);
                            myIntent.putExtra("Images", ImgURL);
                            // ====================================================================
                            myIntent.putExtra("PositionId", PositionId);
                            myIntent.putExtra("mySite", mySite);
                            myIntent.putExtra("myPositionId", myPositionId);
                            myIntent.putExtra("TaskId",
                                    EmpList.get(position).get(KEY_TAKSID));
                            myIntent.putExtra("ActivityID",
                                    EmpList.get(position).get(KEY_ACTIVITYID));
                            myIntent.putExtra("Topik",
                                    EmpList.get(position).get(KEY_ACTIVITYTASK));
                            // ====================================================================
                            myIntent.setClass(GoldenRulesMeetViewDetail.this,
                                    GoldenRulesMeetViewSubDetail.class);
                            startActivity(myIntent);
                        }
                    });
                }
            });
        }

    }

}
