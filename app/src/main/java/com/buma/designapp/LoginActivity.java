package com.buma.designapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.buma.entity.User;
import com.buma.internet.ConnectionDetector;
import com.buma.session.AlertDialogManager;
import com.buma.session.SessionManager;

import org.ksoap2.SoapEnvelope;
import org.ksoap2.serialization.SoapObject;
import org.ksoap2.serialization.SoapSerializationEnvelope;
import org.ksoap2.transport.HttpTransportSE;

import java.util.ArrayList;
import java.util.List;

public class LoginActivity extends Activity implements OnItemSelectedListener {
    static final String URL = "http://202.158.42.254/androidserv/services/transactions.asmx?WSDL";
    // untuk authentications
    private static String SOAP_ACTION = "http://tempuri.org/detailUser";
    private static String NAME_SPACE = "http://tempuri.org/";
    private static String METHOD_NAME = "detailUser";
    // flag for Internet connection status
    Boolean isInternetPresent = false;
    // Connection detector class
    ConnectionDetector cd;
    List<User> list = new ArrayList<User>();
    // Alert Manager Dialog
    AlertDialogManager alert = new AlertDialogManager();
    // Session Manager Class
    SessionManager session;
    private EditText txusername;
    private EditText txpassword;
    private Spinner spinner;
    private Button btnlogin;
    private ProgressDialog pDialog;
    private String _id = "";
    private String MESSAGE;
    private String NIK, NAMA, EMAIL, DEPT, SEC;
    private String LogFrom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // creating connection detector class instance
        cd = new ConnectionDetector(getApplicationContext());
        // creating session class instance
        session = new SessionManager(getApplicationContext());

        spinner = (Spinner) findViewById(R.id.domains);

        Intent intent = getIntent();
        String LogFrom = intent.getExtras().getString("LogFrom");

        if (session.isLoggedIn()) {
            if (LogFrom.equals("GoldenRules")) {
                Intent sender = new Intent(this, HomeGoldenRules.class);
                startActivity(sender);
            } else {
                Intent sender = new Intent(this, MainMenu.class);
                startActivity(sender);
            }
        } else {
            Intent sender = getIntent();
            _id = sender.getExtras().getString("Id").toString();
        }

        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(
                this, R.array.domains_array,
                android.R.layout.simple_dropdown_item_1line);
        spinner.setAdapter(adapter);

        txusername = (EditText) findViewById(R.id.txusername);
        txpassword = (EditText) findViewById(R.id.txpassword);

        btnlogin = (Button) findViewById(R.id.btnLogin);

        // Login button click event
        btnlogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                new LoadAuthenticating().execute();
            }
        });
    }

    @SuppressWarnings("deprecation")
    public void showAlertDialog(Context context, String title, String message,
                                Boolean status) {
        AlertDialog alertDialog = new AlertDialog.Builder(context).create();

        // Setting Dialog Title
        alertDialog.setTitle(title);

        // Setting Dialog Message
        alertDialog.setMessage(message);

        // Setting alert dialog icon
        alertDialog.setIcon((status) ? R.drawable.success : R.drawable.fail);

        // Setting OK Button
        alertDialog.setButton("OK", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int which) {
            }
        });

        // Showing Alert Message
        alertDialog.show();
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int pos,
                               long id) {
        // TODO Auto-generated method stub

    }

    @Override
    public void onNothingSelected(AdapterView<?> arg0) {
        // TODO Auto-generated method stub

    }

    class LoadAuthenticating extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(LoginActivity.this);
            pDialog.setMessage("Authenticating ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            // get Internet status
            try {
                Thread.sleep(10);
                isInternetPresent = cd.isConnectingToInternet();

                if (isInternetPresent) {

                    // Get usrname, password from EditText
                    String username = txusername.getText().toString();
                    String password = txpassword.getText().toString();
                    String domain = spinner.getSelectedItem().toString();

                    // Initialize soap request + add parameters
                    SoapObject request = new SoapObject(NAME_SPACE, METHOD_NAME);

                    // Use this to add parameters
                    request.addProperty("usrDomain", username); // username
                    request.addProperty("passDomain", password); // password
                    request.addProperty("domainName", domain); // domain

                    // Declare the version of the SOAP request
                    SoapSerializationEnvelope envelope = new SoapSerializationEnvelope(
                            SoapEnvelope.VER11);

                    envelope.setOutputSoapObject(request);
                    envelope.dotNet = true;
                    if ((!username.equalsIgnoreCase(""))
                            && (!password.equalsIgnoreCase(""))) {
                        try {
                            HttpTransportSE androidHttpTransport = new HttpTransportSE(
                                    URL);

                            // this is the actual part that will call the
                            // webservice
                            androidHttpTransport.call(SOAP_ACTION, envelope);

                            // Get the SoapResult from the envelope body.
                            SoapObject result = (SoapObject) envelope
                                    .getResponse();
                            // ================================================================
                            Intent intent = getIntent();
                            LogFrom = intent.getExtras().getString("LogFrom");
                            // ================================================================
                            for (int i = 0; i < result.getPropertyCount(); i++) {
                                SoapObject obj = (SoapObject) result
                                        .getProperty(i);

                                if (obj.getProperty("title").toString()
                                        .equalsIgnoreCase("-")) {
                                    MESSAGE = "Username / Password salah";
                                } else {
                                    MESSAGE = "SUKSES";
                                    NIK = obj.getProperty("employeeid")
                                            .toString();
                                    NAMA = obj.getProperty("nama").toString();
                                    EMAIL = obj.getProperty("email").toString();
                                    DEPT = obj.getProperty("dept").toString();
                                    SEC = obj.getProperty("title").toString();

                                }
                                // =============================================================
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                            MESSAGE = "Login failed..";
                        }
                    } else {
                        MESSAGE = "Username / Password tidak boleh kosong";
                    }
                } else {
                    MESSAGE = "No Internet Connection";
                }
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            return null;
        }

        protected void onPostExecute(String file_url) {
            // dismiss the dialog after getting all products
            pDialog.dismiss();
            // updating UI from Background Thread
            runOnUiThread(new Runnable() {
                public void run() {
                    if (MESSAGE.equals("SUKSES")) {
                        Intent myIntent = new Intent();
                        // =================GREENCARD============================
                        if (LogFrom.equals("GreenCard")) {
                            session.createLoginSession(NIK, EMAIL, NAMA, SEC,
                                    DEPT);

                            myIntent.putExtra("GREENCARD_ID", _id);
                            myIntent.setClass(getApplicationContext(),
                                    GreenCardDetailActivity.class);
                            startActivity(myIntent);
                            // =================GOLDENRULES============================
                        } else if (LogFrom.equals("GoldenRules")) {
                            session.createLoginSession(NIK, EMAIL, NAMA, SEC,
                                    DEPT);

                            myIntent.putExtra("GOLDENRULES", _id);
                            myIntent.setClass(getApplicationContext(),
                                    HomeGoldenRules.class);
                            startActivity(myIntent);
                        }
                        // ============================================================
                    } else {
                        Toast.makeText(getApplicationContext(), MESSAGE,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }
}
