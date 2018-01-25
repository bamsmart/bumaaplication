package com.buma.designapp;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.AttributeSet;
import android.util.Log;
import android.view.InflateException;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.buma.session.SessionManager;
import com.buma.utils.ImageLoader;
import com.buma.utils.LazyAdapterMeetViewSubDetail;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GoldenRulesMeetViewSubDetail extends Activity {
    public static final String KEY_MEETVIEWSUBDETAIL = "ObjectGoldenRules";
    public static final String KEY_CHECKPOINTID = "CheckPointID";
    public static final String KEY_POINT = "Point";
    public static final String KEY_KPI = "KPI";
    static final String URL = "http://202.158.42.254/androidserv/services/GoldenRules.asmx?WSDL";
    static final Class<?>[] constructorSignature = new Class[]{Context.class,
            AttributeSet.class};
    private static String NmUser, Deskripsi;
    private static String UserID, TaskID;
    private static String ActivityID;
    private static String Topik;
    private static String SOAP_ACTION = "http://tempuri.org/MeetViewSubDetail";
    private static String NAME_SPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "MeetViewSubDetail";
    final ArrayList<HashMap<String, String>> EmpList = new ArrayList<HashMap<String, String>>();
    public ImageLoader imageLoader;
    LazyAdapterMeetViewSubDetail adapter;
    SessionManager session;
    // Attributes
    private TextView TvNmUser, TvDeskripsi;
    private TextView TvNmPIC, TvPositionPIC;
    private TextView TvTopik;
    private ImageView thumb_image;
    private String PositionId, mySite, myPositionId;
    private String currentDay, NmPIC, Position;
    private String ImgURL;
    private ListView list;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_golden_rules_meet_view_sub_detail);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color=\"#c65910\">"
                + "BUMA Golden <b>Rules<b>" + "</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#f79e21")));

        Intent intent = getIntent();
        currentDay = intent.getExtras().getString("currentDay");
        PositionId = intent.getExtras().getString("PositionId");
        myPositionId = intent.getExtras().getString("myPositionId");
        mySite = intent.getExtras().getString("mySite");
        NmUser = intent.getExtras().getString("NmUser");
        Deskripsi = intent.getExtras().getString("Deskripsi");
        NmPIC = intent.getExtras().getString("NmPIC");
        Position = intent.getExtras().getString("Position");
        ImgURL = intent.getExtras().getString("Images");
        Topik = intent.getExtras().getString("Topik");

        TvNmUser = (TextView) findViewById(R.id.GrTvUserSubDetail);
        TvNmUser.setText(Html.fromHtml(intent.getExtras().getString("NmUser")));
        TvDeskripsi = (TextView) findViewById(R.id.GrTvDeskripsiSubDetail);
        TvDeskripsi.setText(Html.fromHtml(intent.getExtras().getString(
                "Deskripsi")));
        TvNmPIC = (TextView) findViewById(R.id.gr_lst_fullname_sub_detail);
        TvNmPIC.setText(Html.fromHtml(intent.getExtras().getString("NmPIC")));
        TvPositionPIC = (TextView) findViewById(R.id.gr_lst_position_sub_detail);
        TvPositionPIC.setText(Html.fromHtml(intent.getExtras().getString(
                "Position")));

        thumb_image = (ImageView) findViewById(R.id.gr_lst_photo_sub_detail);

        imageLoader = new ImageLoader(getApplicationContext());
        imageLoader.DisplayImage(intent.getExtras().getString("Images")
                .toString(), thumb_image);

        TvTopik = (TextView) findViewById(R.id.gr_lst_topik_sub_detail);
        TvTopik.setText(Html.fromHtml(intent.getExtras().getString("Topik")));

        TaskID = intent.getExtras().getString("TaskId");
        ActivityID = intent.getExtras().getString("ActivityID");

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        if (session.isLoggedIn()) {
            HashMap<String, String> user = session.getUserDetails();
            UserID = user.get(SessionManager.KEY_EMPLOYEEID).toString();
        }
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);

        new LoadSubDetail().execute();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent myIntent = new Intent();
                myIntent.putExtra("currentDay", currentDay);
                myIntent.putExtra("mySite", mySite);
                myIntent.putExtra("myPositionId", myPositionId);
                myIntent.putExtra("NmUser", NmUser);
                myIntent.putExtra("Deskripsi", Deskripsi);
                // =======================================
                myIntent.putExtra("PositionId", PositionId);
                myIntent.putExtra("Name", NmPIC);
                myIntent.putExtra("Sec", Position);
                myIntent.putExtra("Images", ImgURL);
                myIntent.setClass(getApplicationContext(),
                        GoldenRulesMeetViewDetail.class);
                NavUtils.navigateUpTo(this, myIntent);
                return true;
            case R.id.action_home:
                Intent intent = new Intent();
                intent.putExtra("NmUser", NmUser);
                intent.putExtra("mySite", mySite);
                intent.putExtra("myPositionId", myPositionId);
                System.out.println(" mv subdetail " + mySite + " " + myPositionId);
                intent.setClass(getApplicationContext(), TabGoldenRules.class);
                startActivity(intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();

        inflater.inflate(R.menu.gc_menu_actions, menu);
        return true;
    }

    class LoadSubDetail extends AsyncTask<String, String, String> {

        @Override
        protected String doInBackground(String... params) {
            try {
                // Initialize soap request + add parameters
                SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);

                // Use this to add parameters
                request.addProperty("ActivityID", ActivityID); // positionID
                String mth = Deskripsi.split(" ")[5]
                        + "/"
                        + GoldenRulesMeetViewFillData.MothNumb(Deskripsi
                        .split(" ")[6]) + "/" + Deskripsi.split(" ")[7];
                // ====parameters===========
                request.addProperty("UserID", UserID);
                request.addProperty("Month", mth);
                request.addProperty("TaskID", TaskID);

                SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                        SoapEnvelope.VER11);

                envelope.setOutputSoapObject(request);
                envelope.dotNet = true;

                try {
                    HttpTransportSE androidHttpTransport = new HttpTransportSE(
                            URL);
                    androidHttpTransport.call(SOAP_ACTION, envelope);
                    SoapObject result = (SoapObject) envelope.getResponse();
                    for (int i = 0; i < result.getPropertyCount(); i++) {
                        SoapObject obj = (SoapObject) result.getProperty(i);

                        HashMap<String, String> map = new HashMap<String, String>();

                        map.put(KEY_CHECKPOINTID,
                                obj.getProperty("CheckPointID").toString());
                        map.put(KEY_POINT, obj.getProperty("Point").toString());
                        map.put(KEY_KPI, obj.getProperty("KPI").toString());
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
                    list = (ListView) findViewById(R.id.gr_user_list_view_sub_detail);

                    adapter = new LazyAdapterMeetViewSubDetail(
                            GoldenRulesMeetViewSubDetail.this, EmpList);

                    list.setAdapter(adapter);

                    list.setOnItemClickListener(new OnItemClickListener() {
                        public void onItemClick(AdapterView<?> parent,
                                                View view, int position, long id) {

                            View listView = list.getChildAt(position);
                            CheckBox cb = (CheckBox) listView
                                    .findViewById(R.id.gr_lst_sub_checkbox);

                            if (!cb.isChecked()) {
                                Intent myIntent = new Intent();
                                myIntent.putExtra("currentDay", currentDay);
                                myIntent.putExtra("mySite", mySite);
                                myIntent.putExtra("myPositionId", myPositionId);
                                // =============================================
                                myIntent.putExtra("NmUser", NmUser);
                                myIntent.putExtra("Deskripsi", Deskripsi);
                                // =============================================
                                myIntent.putExtra("NmPIC", NmPIC);
                                myIntent.putExtra("Position", Position);
                                myIntent.putExtra("Images", ImgURL);
                                // =============================================
                                myIntent.putExtra("PositionId", PositionId);
                                myIntent.putExtra("TaskId", TaskID);
                                myIntent.putExtra("ActivityID", ActivityID);
                                myIntent.putExtra("Topik", Topik);
                                myIntent.putExtra(
                                        "CheckPointID",
                                        EmpList.get(position).get(
                                                KEY_CHECKPOINTID));
                                // ==============================================
                                myIntent.setClass(
                                        GoldenRulesMeetViewSubDetail.this,
                                        GoldenRulesMeetViewFillData.class);
                                startActivity(myIntent);
                            } else {
                                Toast.makeText(
                                        getApplicationContext(),
                                        "Anda telah mengisikan kpi point ini !",
                                        Toast.LENGTH_LONG).show();
                            }
                        }
                    });
                }
            });
        }
    }

    class MenuColorFix implements LayoutInflater.Factory {
        @Override
        public View onCreateView(String name, Context context,
                                 AttributeSet attrs) {
            if (name.equalsIgnoreCase("com.android.internal.view.menu.ListMenuItemView")) {
                try {
                    Class<? extends ViewGroup> clas = context.getClassLoader()
                            .loadClass(name).asSubclass(ViewGroup.class);
                    Constructor<? extends ViewGroup> cons = clas
                            .getConstructor(constructorSignature);
                    final ViewGroup view = cons.newInstance(context, attrs);

                    new Handler().post(new Runnable() {
                        @Override
                        public void run() {
                            view.setBackgroundColor(Color.MAGENTA);
                        }
                    });

                    return view;

                } catch (InflateException e) {
                } catch (ClassNotFoundException e) {
                } catch (NoSuchMethodException e) {
                    e.printStackTrace();
                } catch (IllegalArgumentException e) {
                    e.printStackTrace();
                } catch (InstantiationException e) {
                    e.printStackTrace();
                } catch (IllegalAccessException e) {
                    e.printStackTrace();
                } catch (InvocationTargetException e) {
                    e.printStackTrace();
                }
            }
            return null;
        }
    }
}
