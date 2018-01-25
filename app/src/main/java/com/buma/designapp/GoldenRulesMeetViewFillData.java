package com.buma.designapp;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.ActionBar.LayoutParams;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.PopupWindow;
import android.widget.TextView;
import android.widget.Toast;

import com.buma.session.SessionManager;
import com.buma.utils.ImageLoader;
import com.buma.utils.LazyAdapterMeetViewFillData;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GoldenRulesMeetViewFillData extends Activity {
    public static final String KEY_MEETVIEWSUBDETAIL = "ObjectGoldenRules"; // parentsubdetail
    public static final String KEY_QUESTIONID = "QuestionID";
    public static final String KEY_QUESTION = "Question";
    public static final String KEY_TOOLS = "Tools";
    public static final String KEY_HOWTOCHECK = "HowToCheck";
    static final String URL = "http://202.158.42.254/androidserv/services/GoldenRules.asmx?WSDL";
    // untuk MeetViewSubDetail
    private static String SOAP_ACTION = "http://tempuri.org/MeetViewFillData";
    private static String NAME_SPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "MeetViewFillData";
    //untuk view Forms/Tools
    private static String vSOAP_ACTION = "http://tempuri.org/MeetViewGuidance";
    private static String vNAME_SPACE = "http://tempuri.org/";
    private static String vMETHOD_NAME = "MeetViewGuidance";
    // Save Data
    private static String sSOAP_ACTION = "http://tempuri.org/SaveToSubMonitoring";
    private static String sNAME_SPACE = "http://tempuri.org/";
    private static String sMETHOD_NAME = "SaveToSubMonitoring";
    private static String sURL = "http://202.158.42.254/androidserv/services/GoldenRules.asmx?WSDL";
    final ArrayList<HashMap<String, String>> EmpList = new ArrayList<HashMap<String, String>>();
    public ImageLoader imageLoader;
    LazyAdapterMeetViewFillData adapter;
    SessionManager session;
    // Attributes
    private TextView TvNmUser;
    private TextView TvTopik;
    private TextView TvHeader;
    private String PositionId;
    private String currentDay, Nik, NmUser, Deskripsi;
    private String NmPIC, Position, mySite, myPositionId;
    private String Topik;
    private String ImgURL;
    private String TaskID;
    @SuppressWarnings("unused")
    private String ActivityID, CheckPointID, QuestionID;
    private String MESSAGE;
    private ListView list;
    private PopupWindow popUp;
    View.OnTouchListener popUpListener = new View.OnTouchListener() {
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            popUp.dismiss();
            return true;
        }
    };
    private ProgressDialog pDialog;
    private Calendar month;

    public static String MothNumb(String arg) {
        String bln = "";
        if (arg.equalsIgnoreCase("Januari")) {
            bln = "01";
        } else if (arg.equalsIgnoreCase("Februari")) {
            bln = "02";
        } else if (arg.equalsIgnoreCase("Maret")) {
            bln = "03";
        } else if (arg.equalsIgnoreCase("April")) {
            bln = "04";
        } else if (arg.equalsIgnoreCase("Mei")) {
            bln = "05";
        } else if (arg.equalsIgnoreCase("Juni")) {
            bln = "06";
        } else if (arg.equalsIgnoreCase("Juli")) {
            bln = "07";
        } else if (arg.equalsIgnoreCase("Agustus")) {
            bln = "08";
        } else if (arg.equalsIgnoreCase("September")) {
            bln = "09";
        } else if (arg.equalsIgnoreCase("Oktober")) {
            bln = "10";
        } else if (arg.equalsIgnoreCase("November")) {
            bln = "11";
        } else if (arg.equalsIgnoreCase("Desember")) {
            bln = "12";
        }
        return bln;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_golden_rules_meet_view_fill_data);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#f79e21")));

        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM
                | ActionBar.DISPLAY_HOME_AS_UP | ActionBar.DISPLAY_SHOW_HOME
                | ActionBar.DISPLAY_SHOW_TITLE);
        View view = View.inflate(getApplicationContext(),
                R.layout.actionbardata, null);
        actionBar.setCustomView(view);

        TvHeader = (TextView) findViewById(R.id.tv_header_title);
        TvHeader.setText(Html.fromHtml("<font color=\"#c65910\">"
                + "BUMA Golden <b>Rules<b>" + "</font>"));
        Intent it = getIntent();
        // ===================================================
        currentDay = it.getExtras().getString("currentDay");
        PositionId = it.getExtras().getString("PositionId");
        mySite = it.getExtras().getString("mySite");
        myPositionId = it.getExtras().getString("myPositionId");
        NmUser = it.getExtras().getString("NmUser");
        Deskripsi = it.getExtras().getString("Deskripsi");
        NmPIC = it.getExtras().getString("NmPIC");
        Position = it.getExtras().getString("Position");
        ImgURL = it.getExtras().getString("Images");
        Topik = it.getExtras().getString("Topik");
        TaskID = it.getExtras().getString("TaskId");
        ActivityID = it.getExtras().getString("ActivityID");
        CheckPointID = it.getExtras().getString("CheckPointID");
        QuestionID = it.getExtras().getString("QuestionID");
        // =======================================================
        TvNmUser = (TextView) findViewById(R.id.GrTvUserFillData);
        TvTopik = (TextView) findViewById(R.id.gr_lst_topik_fill_data);
        // ===========================================================
        TvNmUser.setText(Html.fromHtml(NmUser));
        TvTopik.setText(Html.fromHtml(Topik));
        month = Calendar.getInstance();

        session = new SessionManager(getApplicationContext());
        session.checkLogin();
        if (session.isLoggedIn()) {
            HashMap<String, String> user = session.getUserDetails();
            Nik = user.get(SessionManager.KEY_EMPLOYEEID).toString();
        }

        ImageButton btnSave = (ImageButton) findViewById(R.id.btn_save_fill_data);
        btnSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (validate()) {
                    new LoadSaving().execute();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Pastikan semua item telah diisi.",
                            Toast.LENGTH_LONG).show();
                }

            }
        });
        findViewById(R.id.loadingPanel).setVisibility(View.VISIBLE);
        new LoadQuestions().execute();
    }

    protected boolean validate() {
        boolean retVal = false;
        for (int i = 0; i < list.getAdapter().getCount(); i++) {
            View listView = list.getChildAt(i);
            EditText comment = (EditText) listView
                    .findViewById(R.id.gr_lst_fill_kpi);
            if (!comment.getText().toString().equals("")) {
                retVal = true;
            }
        }
        return retVal;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater()
                .inflate(R.menu.golden_rules_meet_view_fill_data, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent it = new Intent();
                String day = Deskripsi.split(" ")[5];
                if (day.length() == 1) {
                    day = "0" + day;
                }
                it.putExtra("ActivityID", ActivityID);
                it.putExtra("PositionId", PositionId);
                it.putExtra("mySite", mySite);
                it.putExtra("myPositionId", myPositionId);
                it.putExtra("Topik", Topik);
                it.putExtra("Deskripsi", Deskripsi);
                it.putExtra("Position", Position);
                it.putExtra("Images", ImgURL);
                it.putExtra("NmPIC", NmPIC);
                it.putExtra("NmUser", NmUser);
                it.putExtra("date",
                        android.text.format.DateFormat.format("yyyy-MM", month)
                                + "-" + day);
                it.putExtra("tahun",
                        android.text.format.DateFormat.format("yyyy", month));
                it.putExtra("bulan",
                        android.text.format.DateFormat.format("MM", month));
                it.putExtra("tanggal", day);
                it.setClass(getApplicationContext(),
                        GoldenRulesMeetViewSubDetail.class);
                setResult(RESULT_OK, it);
                NavUtils.navigateUpTo(this, it);
                return true;
            case R.id.btn_save_fill_data:
                if (validate()) {
                    new LoadSaving().execute();
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Pastikan semua item telah diisi .", Toast.LENGTH_LONG)
                            .show();
                }
                return true;
            case R.id.action_help:
                Help();
        }
        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("deprecation")
    public void Help() {
        LayoutInflater inflaterLayout = (LayoutInflater) getBaseContext()
                .getSystemService(LAYOUT_INFLATER_SERVICE);
        View dialog = inflaterLayout.inflate(R.layout.layout_help, null);
        popUp = new PopupWindow(dialog, LayoutParams.WRAP_CONTENT,
                LayoutParams.WRAP_CONTENT);
        try {
            // Initialize soap request + add parameters
            SoapObject request = new SoapObject(vNAME_SPACE, vMETHOD_NAME);

            // Use this to add parameters
            request.addProperty("ActivityID", ActivityID);
            request.addProperty("CheckPointID", CheckPointID);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);

            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

                // this is the actual part that will call the webservice
                androidHttpTransport.call(vSOAP_ACTION, envelope);
                SoapObject result = (SoapObject) envelope.getResponse();
                for (int i = 0; i < result.getPropertyCount(); i++) {
                    SoapObject obj = (SoapObject) result.getProperty(i);

                    TextView tvTools = (TextView) dialog.findViewById(R.id.gr_lst_desc_tools);
                    TextView tvHowToCheck = (TextView) dialog.findViewById(R.id.gr_lst_desc_howtocheck);
                    tvTools.setText(Html.fromHtml(obj.getProperty("Tools").toString()));
                    tvHowToCheck.setText(Html.fromHtml(obj.getProperty("HowToCheck").toString()));
                }
            } catch (Exception e) {
                e.printStackTrace();
            }

        } catch (Exception ex) {
            Log.e("Error", ex.getMessage());
            ex.getStackTrace();
        }

        popUp.setBackgroundDrawable(new BitmapDrawable());
        popUp.setOutsideTouchable(true);
        if (!popUp.isShowing()) {
            popUp.showAsDropDown(list, 40, 30);
        } else {
            popUp.dismiss();
        }
        popUp.setOnDismissListener(new PopupWindow.OnDismissListener() {

            @Override
            public void onDismiss() {
                popUp.dismiss();

            }
        });

        ImageView img = (ImageView) dialog.findViewById(R.id.action_closed);
        img.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                popUp.dismiss();
            }
        });
    }

    class LoadSaving extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GoldenRulesMeetViewFillData.this);
            pDialog.setMessage("Saving ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // get Internet status
            try {
                Thread.sleep(500);
                for (int i = 0; i < list.getAdapter().getCount(); i++) {
                    View listView = list.getChildAt(i);
                    EditText comment = (EditText) listView
                            .findViewById(R.id.gr_lst_fill_kpi);

                    SoapObject req = new SoapObject(sNAME_SPACE, sMETHOD_NAME);

                    String mth = Deskripsi.split(" ")[5] + "/" + MothNumb(Deskripsi.split(" ")[6])
                            + "/" + Deskripsi.split(" ")[7];

                    req.addProperty("UserID", Nik);
                    req.addProperty("Month", mth);
                    req.addProperty("TaskID", TaskID);
                    req.addProperty("currentDay", currentDay);
                    req.addProperty("ActivityID", ActivityID);
                    req.addProperty("CheckPointID", CheckPointID);
                    req.addProperty("QuestionID", EmpList.get(i).get(KEY_QUESTIONID).toString());
                    req.addProperty("KPI", comment.getText().toString());

                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);
                    new MarshalBase64().register(envelope);
                    envelope.setOutputSoapObject(req);
                    envelope.dotNet = true;

                    try {
                        HttpTransportSE androidHttpTransport = new HttpTransportSE(
                                sURL);

                        androidHttpTransport.call(sSOAP_ACTION, envelope);

                        SoapObject result = (SoapObject) envelope.bodyIn;
                        if (result.getProperty(0).toString().equals("SUKSES")) {
                            MESSAGE = "SUKSES";
                        } else {
                            MESSAGE = "GAGAL";
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

            return null;
        }

        protected void onPostExecute(String file_url) {
            pDialog.dismiss();
            runOnUiThread(new Runnable() {
                public void run() {
                    if (MESSAGE.equals("SUKSES")) {
                        Intent myIntent = getIntent();
                        // ====================================================================
                        myIntent.putExtra("NmUser", NmUser);
                        myIntent.putExtra("Deskripsi", Deskripsi);
                        // ====================================================================
                        myIntent.putExtra("currentDay", currentDay);
                        myIntent.putExtra("NmPIC", NmPIC);
                        myIntent.putExtra("Position", Position);
                        myIntent.putExtra("Images", ImgURL);
                        // ====================================================================
                        myIntent.putExtra("mySite", mySite);
                        myIntent.putExtra("myPositionId", myPositionId);
                        myIntent.putExtra("PositionId", PositionId);
                        myIntent.putExtra("TaskId", TaskID);
                        myIntent.putExtra("ActivityID", ActivityID);
                        myIntent.putExtra("Topik", Topik);

                        Toast.makeText(getApplicationContext(),
                                "Berhasil menyimpan ... ", Toast.LENGTH_SHORT)
                                .show();

                        myIntent.setClass(getApplicationContext(),
                                GoldenRulesMeetViewSubDetail.class);

                        startActivity(myIntent);
                        // ============================================================
                    } else {
                        Toast.makeText(getApplicationContext(),
                                MESSAGE + " menyimpan data!!!",
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

    class LoadQuestions extends AsyncTask<String, String, String> {
        @Override
        protected String doInBackground(String... params) {
            try {
                // Initialize soap request + add parameters
                SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);

                // Use this to add parameters
                request.addProperty("ActivityID", ActivityID);
                request.addProperty("CheckPointID", CheckPointID);

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
                        map.put(KEY_QUESTIONID, obj.getProperty("QuestionID").toString());
                        map.put(KEY_QUESTION, obj.getProperty("Question")
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
                    list = (ListView) findViewById(R.id.gr_user_list_view_fill_data);

                    // Getting adapter by passing xml data ArrayList
                    adapter = new LazyAdapterMeetViewFillData(GoldenRulesMeetViewFillData.this, EmpList);

                    list.setAdapter(adapter);
                }
            });
        }

    }
}
