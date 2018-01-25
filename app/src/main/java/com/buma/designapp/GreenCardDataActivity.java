package com.buma.designapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import android.widget.Toast;

import com.buma.db.DbActivity;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

public class GreenCardDataActivity extends Activity {
    //untuk sync
    private static String SOAP_ACTION = "http://tempuri.org/insertSyncData";
    private static String NAME_SPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "insertSyncData";
    private static String URL = "http://172.16.0.20/androidserv/services/transactions.asmx?WSDL";
    private DbActivity oprDatabase = null;
    private SQLiteDatabase db = null;
    private Cursor dbCursor = null;
    private Button btnhapusemua;
    private Button btnhapus;
    private Button btnedit;
    private Button btnsync;
    private Button btnrefresh;
    private String _id, judul, tanggal, lokasi, deskripsi, status;

    @Override
    protected void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
        TampilkanData();
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_local_list);

        oprDatabase = new DbActivity(this);
        db = oprDatabase.getWritableDatabase();
        oprDatabase.createTable(db);
        TampilkanData();
        final AlertDialog.Builder dialog = new AlertDialog.Builder(this);

        btnhapusemua = (Button) findViewById(R.id.btnhapusemua);
        btnhapusemua.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.setTitle("Konfirmasi");
                dialog.setMessage("Anda yakin akan menghapus seluruh data?");
                dialog.setNegativeButton("Cancel", null);
                dialog.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        oprDatabase.deleteAllActivity(db);
                        TampilkanData();
                    }
                });
                dialog.show();
            }
        });

        btnedit = (Button) findViewById(R.id.btnedit);
        btnedit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!_id.isEmpty()) {
                    dbCursor = oprDatabase.selectBiodata(db, "SELECT _id , judul, tanggal,lokasi,deskripsi,status FROM tb_activity WHERE _id ='" + _id + "'");
                    dbCursor.moveToFirst();

                    _id = dbCursor.getString(0);
                    judul = dbCursor.getString(1);
                    tanggal = dbCursor.getString(2);
                    lokasi = dbCursor.getString(3);
                    deskripsi = dbCursor.getString(4);
                    status = dbCursor.getString(5);
                    TampilkanEditActivity();
                }
            }
        });
        btnhapus = (Button) findViewById(R.id.btnhapus);
        btnhapus.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (!_id.isEmpty()) {
                    dialog.setTitle("Konfirmasi");
                    dialog.setMessage("Anda yakin akan menghapus data " + _id + " ini ?");
                    dialog.setNegativeButton("Cancel", null);
                    dialog.setPositiveButton("Ok", new AlertDialog.OnClickListener() {
                        public void onClick(DialogInterface dialog, int arg1) {
                            oprDatabase.deleteActivity(db, _id);
                            TampilkanData();
                            judul = "";
                            tanggal = "";
                            lokasi = "";
                            deskripsi = "";
                        }
                    });
                    dialog.show();
                }
            }
        });

        btnrefresh = (Button) findViewById(R.id.btnrefresh);
        btnrefresh.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                TampilkanData();
            }
        });

        btnsync = (Button) findViewById(R.id.btnSync);
        btnsync.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Initialize soap request + add parameters
                SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);

                if (!_id.isEmpty()) {
                    dbCursor = oprDatabase.selectBiodata(db, "Select judul, tanggal,lokasi,deskripsi,status FROM tb_activity WHERE _id ='" + _id + "'");
                    dbCursor.moveToFirst();

                    //Use this to add parameters
                    request.addProperty("title", dbCursor.getString(0));
                    request.addProperty("dates", dbCursor.getString(1));
                    request.addProperty("locations", dbCursor.getString(2));
                    request.addProperty("descriptions", dbCursor.getString(3));
                    request.addProperty("status", dbCursor.getString(4));

                    //Declare the version of the SOAP request
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(SoapEnvelope.VER11);

                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = true;

                    try {
                        HttpTransportSE androidHttpTransport = new HttpTransportSE(URL);

                        //this is the actual part that will call the webservice
                        androidHttpTransport.call(SOAP_ACTION, envelope);

                        // Get the SoapResult from the envelope body.
                        SoapObject result = (SoapObject) envelope.bodyIn;

                        if (result.getProperty(0).toString().equals("SUKSES")) {
                            //Get the first property and change the label text
                            Toast.makeText(getApplicationContext(), "Data berhasil diupload ke server!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(getApplicationContext(), " Error Result :" + result.toString(), Toast.LENGTH_LONG).show();
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        });
    }

    public void TampilkanData() {
        TableLayout TL = (TableLayout) findViewById(R.id.tableLayout);
        TL.removeAllViews();

        dbCursor = oprDatabase.selectBiodata(db, "SELECT * FROM tb_activity");
        dbCursor.moveToFirst();
        int jml_baris = dbCursor.getCount();

        if (jml_baris == 0) return;

        int kol_id = dbCursor.getColumnIndex("_id");
        int kol_judul = dbCursor.getColumnIndex("judul");
        int kol_tanggal = dbCursor.getColumnIndex("tanggal");
        int kol_lokasi = dbCursor.getColumnIndex("lokasi");
        int kol_deskripsi = dbCursor.getColumnIndex("deskripsi");
        int indeks = 1;
        String[][] data = new String[jml_baris][5];

        data[0][0] = dbCursor.getString(kol_id);
        data[0][1] = dbCursor.getString(kol_judul);
        data[0][2] = dbCursor.getString(kol_tanggal);
        data[0][3] = dbCursor.getString(kol_lokasi);
        data[0][4] = dbCursor.getString(kol_deskripsi);

        if (dbCursor != null) {
            while (dbCursor.moveToNext()) {
                data[indeks][0] = dbCursor.getString(kol_id);
                data[indeks][1] = dbCursor.getString(kol_judul);
                data[indeks][2] = dbCursor.getString(kol_tanggal);
                data[indeks][3] = dbCursor.getString(kol_lokasi);
                data[indeks][4] = dbCursor.getString(kol_deskripsi);
                indeks++;
            }
        }

        TableLayout.LayoutParams ParameterTableLayout = new TableLayout.LayoutParams(TableLayout.LayoutParams.WRAP_CONTENT, TableLayout.LayoutParams.WRAP_CONTENT);
        for (int awal = 0; awal < jml_baris; awal++) {
            TableRow TR = new TableRow(this);
            TR.setBackgroundColor(Color.WHITE);
            TR.setLayoutParams(ParameterTableLayout);
            TableRow.LayoutParams ParameterTableRow = new TableRow.LayoutParams(TableRow.LayoutParams.WRAP_CONTENT, TableRow.LayoutParams.WRAP_CONTENT);
            ParameterTableRow.setMargins(1, 1, 1, 1);

            final CheckBox chk = new CheckBox(this);
            chk.setTag(data[awal][0]);

            TR.addView(chk, ParameterTableRow);
            chk.setOnClickListener(new OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (chk.isChecked())
                        _id = chk.getTag().toString();
                    else
                        _id = "";
                }
            });

            for (int kolom = 0; kolom < 5; kolom++) {
                TextView TV = new TextView(this);
                TV.setText(data[awal][kolom]);
                TV.setTextColor(Color.BLACK);
                TV.setPadding(1, 4, 1, 4);
                TV.setGravity(Gravity.LEFT);
                TV.setBackgroundColor(Color.WHITE);
                TR.addView(TV, ParameterTableRow);
            }
            TL.addView(TR);
        }
    }

    private void TampilkanEditActivity() {
        Intent intentInputActivity = new Intent(this, GreenCardInput.class);
        intentInputActivity.putExtra("status", "edit");
        intentInputActivity.putExtra("_id", _id);
        intentInputActivity.putExtra("judul", judul);
        intentInputActivity.putExtra("tanggal", tanggal);
        intentInputActivity.putExtra("lokasi", lokasi);
        intentInputActivity.putExtra("deskripsi", deskripsi);
        intentInputActivity.putExtra("status", status);

        _id = "";
        judul = "";
        tanggal = "";
        lokasi = "";
        deskripsi = "";

        startActivity(intentInputActivity);
    }

    public void onDestroy() {
        super.onDestroy();
        dbCursor.close();
        db.close();
    }
}
