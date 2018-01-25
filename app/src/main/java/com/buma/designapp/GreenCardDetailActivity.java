package com.buma.designapp;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuItem;
import android.view.ViewConfiguration;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.buma.db.DbGreenCard;
import com.buma.session.SessionManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.MarshalBase64;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.lang.reflect.Field;
import java.util.HashMap;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
public class GreenCardDetailActivity extends Activity {
    // Server
    private static String SOAP_ACTION = "http://tempuri.org/SyncDataGreenCard";
    private static String NAME_SPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "SyncDataGreenCard";
    private static String URL = "http://202.158.42.254/androidserv/services/transactions.asmx?WSDL";
    byte[] byteImage = null;
    Uri imageUri;
    // Session Manager Class
    SessionManager session;
    private DbGreenCard oprDatabase = null;
    private SQLiteDatabase db = null;
    private Cursor dbCursor = null;
    private String _id = "";
    private String _noreg = "";
    private TextView tvjenis, tvtempat, tvwaktu, tvbahaya, tvrencana, tvstatus;
    private String txjenis, txtempat, txwaktu, txbahaya, txrencana, txdanger, txstatus;
    private String nik, observer, jabatan, departemen, email;
    private ProgressDialog pDialog;
    // ImageView
    private ImageView imgView;
    private AlertDialog.Builder dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_green_card_detail);

        dialog = new AlertDialog.Builder(this);
        // =======================================
        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color=\"#ffffff\">"
                + "<b> Detail Data Green Card<b>" + "</font>"));
        // actionBar.setBackgroundDrawable(new
        // ColorDrawable(R.drawable.item_bg_green));
        Resources res = getResources();
        Bitmap b = BitmapFactory.decodeResource(res, R.drawable.item_bg_green);
        BitmapDrawable bgColor = new BitmapDrawable(res, b);
        actionBar.setBackgroundDrawable(bgColor);
        try {
            ViewConfiguration config = ViewConfiguration.get(this);
            Field menuKeyField = ViewConfiguration.class
                    .getDeclaredField("sHasPermanentMenuKey");
            if (menuKeyField != null) {
                menuKeyField.setAccessible(true);
                menuKeyField.setBoolean(config, false);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        session = new SessionManager(getApplicationContext());

        oprDatabase = new DbGreenCard(this);
        db = oprDatabase.getWritableDatabase();
        oprDatabase.createTable(db);

        Intent sender = getIntent();
        _id = sender.getExtras().getString("GREENCARD_ID").toString();

        tvjenis = (TextView) findViewById(R.id.greencard_tvjenis);
        tvtempat = (TextView) findViewById(R.id.greencard_tvtempat);
        tvwaktu = (TextView) findViewById(R.id.greencard_tvwaktu);
        tvbahaya = (TextView) findViewById(R.id.greencard_tvbahaya);
        tvrencana = (TextView) findViewById(R.id.greencard_tvperbaikan);
        tvstatus = (TextView) findViewById(R.id.greencard_tvstatus);
        imgView = (ImageView) findViewById(R.id.imgViewer);

        dbCursor = oprDatabase
                .selectGreenCard(
                        db,
                        " SELECT danger_type,place,time,detail,planning,danger,flag,image_data,noreg FROM tb_greenCard Where _id = '"
                                + _id + "'");
        dbCursor.moveToFirst();

        txjenis = dbCursor.getString(0);
        txtempat = dbCursor.getString(1);
        txwaktu = dbCursor.getString(2);
        txbahaya = dbCursor.getString(3);
        txrencana = dbCursor.getString(4);
        txdanger = dbCursor.getString(5);
        txstatus = dbCursor.getString(6);
        byteImage = dbCursor.getBlob(dbCursor.getColumnIndex("image_data"));

        tvjenis.setText(Html.fromHtml((txjenis)));
        tvtempat.setText(Html.fromHtml((txtempat)));
        tvwaktu.setText(Html.fromHtml((txwaktu)));
        tvbahaya.setText(Html.fromHtml((txbahaya)));
        tvrencana.setText(Html.fromHtml((txrencana)));
        tvstatus.setText(Html.fromHtml((txstatus)));
        setImage(byteImage);

        if (txstatus.equalsIgnoreCase("SAVED")) {
            /*
			 * btnEdit.setEnabled(true); btnDelete.setEnabled(true);
			 * btnSend.setEnabled(true);
			 */
        } else {
			/*
			 * btnEdit.setEnabled(false); btnDelete.setEnabled(false);
			 * btnSend.setEnabled(false);
			 */
        }
    }

    public void setImage(byte[] byteImage) {
        imgView.setImageBitmap(BitmapFactory.decodeByteArray(byteImage, 0,
                byteImage.length));
    }

    @Override
    protected void onDestroy() {
        dbCursor.close();
        db.close();
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {

            case android.R.id.home:
                Intent i = new Intent();
                i.setClass(getApplicationContext(), ListGreenCard.class);
                startActivity(i);
                break;

            case R.id.action_edit:
                Intent myIntent = getIntent();
                myIntent.putExtra("status", "edit");
                myIntent.putExtra("_id", _id);
                myIntent.putExtra("jenis", txjenis);
                myIntent.putExtra("place", txtempat);
                myIntent.putExtra("time", txwaktu);
                myIntent.putExtra("detail", txbahaya);
                myIntent.putExtra("plan", txrencana);
                myIntent.putExtra("danger", txdanger);
                myIntent.putExtra("flag", txstatus);
                myIntent.putExtra("image", byteImage);
                myIntent.setClass(getApplicationContext(), GreenCardActivity.class);
                startActivity(myIntent);
                break;
            case R.id.action_delete:
                if (!_id.isEmpty()) {
                    if (txstatus.equalsIgnoreCase("SAVED") || (txstatus.equalsIgnoreCase("CLOSED") || (txstatus.equalsIgnoreCase("DELETED")))) {
                        dialog.setTitle("Konfirmasi");
                        dialog.setMessage("Anda yakin akan menghapus data " + _id
                                + " ini ?");
                        dialog.setNegativeButton("Cancel", null);
                        dialog.setPositiveButton("Ok",
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int arg1) {
                                        oprDatabase.deleteGreenCard(db, _id);
                                        Intent myIntent = getIntent();
                                        myIntent.setClass(getApplicationContext(),
                                                ListGreenCard.class);
                                        finish();
                                    }
                                });
                        dialog.show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Anda tidak dianjurkan untuk menghapus data ini! \n " +
                                "Anda diharapkan mengikuti perkembangan ticket anda , \n data dapat dihapus jika status masih 'SAVED' atau jika sudah 'CLOSED' ", Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.action_send:
                if (!_id.isEmpty()) {
                    if (txstatus.equalsIgnoreCase("SAVED")) {
                        dialog.setTitle("Konfirmasi");
                        dialog.setMessage("Anda yakin akan mengirimkan data ini ke server???");
                        dialog.setNegativeButton("Cancel", null);
                        dialog.setPositiveButton("Ok",
                                new AlertDialog.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int arg1) {

                                        session.checkLogin();
                                        if (session.isLoggedIn()) {
                                            new SendingData().execute();

                                        } else {
                                            Intent myIntent = getIntent();
                                            myIntent.putExtra("LogFrom", "GreenCard");
                                            myIntent.putExtra("Id", _id);
                                            myIntent.setClass(getApplicationContext(),
                                                    LoginActivity.class);
                                            startActivity(myIntent);
                                        }
                                    }
                                });
                        dialog.show();
                    } else {
                        Toast.makeText(getApplicationContext(), "Anda telah mengirimkan data ini sebelumnya! "
                                , Toast.LENGTH_LONG).show();
                    }
                }
                break;
            case R.id.action_logout:
                dialog.setTitle("Konfirmasi");
                dialog.setMessage("Apakah anda yakin akan logout???");
                dialog.setNegativeButton("Cancel", null);
                dialog.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        session.logoutUser();
                    }
                });
                dialog.show();
                break;
            default:
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.crud_menu_actions, menu);
        return true;
    }

    class SendingData extends AsyncTask<String, String, String> {

        /**
         * Before starting background thread Show Progress Dialog
         */
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(GreenCardDetailActivity.this);
            pDialog.setMessage("Sending ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        /**
         * getting Inbox JSON
         */
        protected String doInBackground(String... args) {
            try {
                Thread.sleep(800);
                // ===========================================
                HashMap<String, String> user = session.getUserDetails();
                nik = user.get(SessionManager.KEY_EMPLOYEEID).toString();
                email = user.get(SessionManager.KEY_EMAIL).toString();
                observer = user.get(SessionManager.KEY_NAME).toString();
                jabatan = user.get(SessionManager.KEY_SEC).toString();
                departemen = user.get(SessionManager.KEY_DEPT).toString();

                SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);

                if (!_id.isEmpty()) {
                    if (txstatus.equalsIgnoreCase("SAVED")) {

                        _noreg = nik.substring(5, 8) + _id
                                + txwaktu.substring(11, 16).replace(":", "");
                        System.out.println("No Reg : " + _noreg);
                        request.addProperty("id", _id);
                        request.addProperty("nik", nik);
                        request.addProperty("name", observer);
                        request.addProperty("sec", jabatan);
                        request.addProperty("dept", departemen);
                        request.addProperty("email", email);
                        request.addProperty("jenis", txjenis);
                        request.addProperty("place", txtempat);
                        request.addProperty("time", txwaktu);
                        request.addProperty("detail", txbahaya);
                        request.addProperty("planning", txrencana);
                        request.addProperty("danger", txdanger);
                        request.addProperty("stsreport", txstatus);
                        request.addProperty("img", byteImage);

                        SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                                SoapEnvelope.VER11);
                        new MarshalBase64().register(envelope);
                        envelope.setOutputSoapObject(request);
                        envelope.dotNet = true;

                        try {
                            HttpTransportSE androidHttpTransport = new HttpTransportSE(
                                    URL);

                            androidHttpTransport.call(SOAP_ACTION, envelope);

                            SoapObject result = (SoapObject) envelope.bodyIn;

                            if (result.getProperty(0).toString()
                                    .equals("SUKSES")) {

                                runOnUiThread(new Runnable() {

                                    @Override
                                    public void run() {
                                        Toast.makeText(getApplicationContext(),
                                                "Data berhasil diupload ke server!",
                                                Toast.LENGTH_LONG).show();
                                    }
                                });


                                String[] data = new String[]{_id, "REPORTED",
                                        _noreg};

                                oprDatabase.updateFlagGreenCard(db, data);
                                Intent myIntent = getIntent();
                                myIntent.setClass(getApplicationContext(),
                                        ListGreenCard.class);
                                startActivity(myIntent);
                            } else {
                                Toast.makeText(getApplicationContext(),
                                        " Error Result :" + result.toString(),
                                        Toast.LENGTH_LONG).show();
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else {
                        Toast.makeText(getApplicationContext(),
                                " Data telah diuplad sebelumnya",
                                Toast.LENGTH_LONG).show();
                    }
                } else {
                    Toast.makeText(getApplicationContext(),
                            "Silahkan pilih data yang ingin diupload!",
                            Toast.LENGTH_LONG).show();
                }
                finish();
                // ===========================================
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        /**
         * After completing background task Dismiss the progress dialog
         **/
        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {

                }
            });
        }
    }
}
