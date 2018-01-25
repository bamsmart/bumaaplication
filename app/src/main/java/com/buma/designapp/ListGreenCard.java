package com.buma.designapp;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleCursorAdapter;

import com.buma.db.DbGreenCard;
import com.buma.session.SessionManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class ListGreenCard extends Activity {

    // Server
    private static String SOAP_ACTION = "http://tempuri.org/RefreshDataGreenCard";
    private static String NAME_SPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "RefreshDataGreenCard";
    private static String URL = "http://202.158.42.254/androidserv/services/transactions.asmx?WSDL";
    protected ListAdapter adapter;
    // Session Manager Class
    SessionManager session;
    private DbGreenCard oprDatabase = null;
    private SQLiteDatabase db = null;
    private Cursor dbCursor = null;
    private String _id = "";
    private String _noreg = "";
    private String nik, name, sec, dept, email, place, time, detail, planning,
            danger, flag;
    private ListView list;
    // Refresh menu item
    private MenuItem refreshMenuItem;

    @Override
    protected void onStart() {
        showData();
        super.onStart();
    }

    @Override
    protected void onResume() {
        showData();
        super.onResume();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        // TODO Auto-generated method stub
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.list_green_card);
        // =======================================
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color=\"#ffffff\">"
                + "<b>List Data Green Card<b>" + "</font>"));
        Resources res = getResources();
        Bitmap b = BitmapFactory.decodeResource(res, R.drawable.item_bg_green);
        BitmapDrawable bgColor = new BitmapDrawable(res, b);
        actionBar.setBackgroundDrawable(bgColor);

        oprDatabase = new DbGreenCard(this);
        db = oprDatabase.getWritableDatabase();
        oprDatabase.createTable(db);

        list = (ListView) findViewById(R.id.list_greencard);
        showData();

        list.setOnItemClickListener(new OnItemClickListener() {
            public void onItemClick(AdapterView<?> parent, View view,
                                    int position, long id) {

                Intent myIntent = new Intent();
                Cursor cursor = (Cursor) adapter.getItem(position);
                myIntent.putExtra("GREENCARD_ID",
                        "" + cursor.getString(cursor.getColumnIndex("_id"))
                                + "");
                myIntent.setClass(getApplicationContext(),
                        GreenCardDetailActivity.class);
                startActivity(myIntent);
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void showData() {
        dbCursor = oprDatabase.selectGreenCard(db,
                " SELECT _id,place,time,detail,flag FROM tb_greenCard ");

        adapter = new SimpleCursorAdapter(this,
                R.layout.list_green_card_pattern, dbCursor, new String[]{
                "place", "time", "detail", "flag"},
                new int[]{R.id.txplace, R.id.txtime, R.id.txdetail,
                        R.id.txstatus});
        list.setAdapter(adapter);
    }

    public void showEditActivity() {
        Intent editGreenCard = new Intent(this, GreenCardActivity.class);
        editGreenCard.putExtra("status", "edit");
        editGreenCard.putExtra("_id", _id);
        editGreenCard.putExtra("nik", nik);
        editGreenCard.putExtra("name", name);
        editGreenCard.putExtra("sec", sec);
        editGreenCard.putExtra("dept", dept);
        editGreenCard.putExtra("email", email);
        editGreenCard.putExtra("place", place);
        editGreenCard.putExtra("time", time);
        editGreenCard.putExtra("detail", detail);
        editGreenCard.putExtra("plan", planning);
        editGreenCard.putExtra("danger", danger);
        editGreenCard.putExtra("flag", flag);

        resetField();
        startActivity(editGreenCard);
    }

    public void resetField() {
        place = "";
        nik = "";
        name = "";
        sec = "";
        dept = "";
        email = "";
        place = "";
        time = "";
        detail = "";
        planning = "";
        danger = "";
        flag = "";
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.list_green_card, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent i = new Intent();
                i.putExtra("status", "baru");
                i.setClass(ListGreenCard.this, GreenCardActivity.class);
                startActivity(i);
                return true;
            case R.id.action_refresh:
                // refresh
                refreshMenuItem = item;
                // load the data from server
                new SyncData().execute();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public void TampilkanData() {
        dbCursor = oprDatabase.selectGreenCard(db,
                " SELECT noreg FROM tb_greenCard ");
        dbCursor.moveToFirst();

        while (dbCursor.isAfterLast() == false) {
            _noreg = dbCursor.getString(dbCursor.getColumnIndex("noreg"));

            SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);

            request.addProperty("noreg", _noreg);

            SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                    SoapEnvelope.VER11);
            envelope.setOutputSoapObject(request);
            envelope.dotNet = true;

            try {
                HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

                androidHttpTransport.call(SOAP_ACTION, envelope);

                SoapObject result = (SoapObject) envelope.bodyIn;

                String[] data = new String[]{_noreg,
                        result.getProperty(0).toString()};
                oprDatabase.updateStatusGreenCard(db, data);
            } catch (Exception e) {
                e.printStackTrace();
            }
            dbCursor.moveToNext();
        }
        showData();
    }

    public void onDestroy() {
        super.onDestroy();
        dbCursor.close();
        db.close();
    }

    /**
     * Async task to load the data from server
     **/
    private class SyncData extends AsyncTask<String, Void, String> {
        @Override
        protected void onPreExecute() {
            // set the progress bar view
            refreshMenuItem.setActionView(R.layout.action_progressbar);

            refreshMenuItem.expandActionView();
        }

        @Override
        protected String doInBackground(String... params) {
            // not making real request in this demo
            // for now we use a timer to wait for sometime
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            refreshMenuItem.collapseActionView();
            // remove the progress bar view
            refreshMenuItem.setActionView(null);
            TampilkanData();
        }
    }

}
