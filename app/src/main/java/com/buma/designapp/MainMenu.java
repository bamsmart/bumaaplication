package com.buma.designapp;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewConfiguration;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.buma.internet.ConnectionDetector;
import com.buma.session.AlertDialogManager;
import com.buma.session.SessionManager;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

public class MainMenu extends Activity {

    // XML node keys
    static final String KEY_TAG = "menuitem"; // parent node
    static final String KEY_ID = "id";
    static final String KEY_MENU = "menu";
    static final String KEY_ICON = "icon";

    String MESSAGE;
    ListView list;
    BinderData adapter = null;

    List<HashMap<String, String>> menuDataCollection;

    ProgressDialog pDialog;
    AlertDialogManager alert = new AlertDialogManager();
    AlertDialog.Builder dialog;

    SessionManager session;
    Boolean isInternetPresent = false;
    Boolean isConnectToServer = false;

    ConnectionDetector cd;

    // inisialisasi saat program diload
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main_menu);

        dialog = new AlertDialog.Builder(this);
        cd = new ConnectionDetector(getApplicationContext());
        // ========================================================
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
        // ===================================================
        session = new SessionManager(getApplicationContext());
        try {

            DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
                    .newInstance();
            DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
            Document doc = docBuilder.parse(getAssets().open("menuhome.xml"));

            menuDataCollection = new ArrayList<HashMap<String, String>>();

            // normalize text representation
            doc.getDocumentElement().normalize();

            NodeList weatherList = doc.getElementsByTagName("menuitem");

            HashMap<String, String> map = null;

            for (int i = 0; i < weatherList.getLength(); i++) {
                map = new HashMap<String, String>();

                Node firstWeatherNode = weatherList.item(i);

                if (firstWeatherNode.getNodeType() == Node.ELEMENT_NODE) {

                    Element firstWeatherElement = (Element) firstWeatherNode;
                    // -------
                    NodeList idList = firstWeatherElement
                            .getElementsByTagName(KEY_ID);
                    Element firstIdElement = (Element) idList.item(0);
                    NodeList textIdList = firstIdElement.getChildNodes();
                    //
                    map.put(KEY_ID, textIdList.item(0).getNodeValue()
                            .trim());

                    //
                    NodeList cityList = firstWeatherElement
                            .getElementsByTagName(KEY_MENU);
                    Element firstCityElement = (Element) cityList.item(0);
                    NodeList textCityList = firstCityElement.getChildNodes();
                    //
                    map.put(KEY_MENU, textCityList.item(0)
                            .getNodeValue().trim());

                    //
                    NodeList iconList = firstWeatherElement
                            .getElementsByTagName(KEY_ICON);
                    Element firstIconElement = (Element) iconList.item(0);
                    NodeList textIconList = firstIconElement.getChildNodes();
                    //
                    map.put(KEY_ICON, textIconList.item(0)
                            .getNodeValue().trim());

                    // Add to the Arraylist
                    menuDataCollection.add(map);
                }
            }

            // set data to adapter
            BinderData bindingData = new BinderData(this, menuDataCollection);

            list = (ListView) findViewById(R.id.menulist);

            list.setAdapter(bindingData);

            list.setOnItemClickListener(new OnItemClickListener() {
                public void onItemClick(AdapterView<?> parent, View view,
                                        int position, long id) {

                    Intent myIntent = new Intent();
                    if (menuDataCollection.get(position).get(KEY_MENU)
                            .equals("Green Card")) {
                        myIntent.putExtra("status", "baru");
                        myIntent.setClass(MainMenu.this,
                                GreenCardActivity.class);
                        startActivity(myIntent);

                    } else if (menuDataCollection.get(position).get(KEY_MENU)
                            .equals("Golden Rules")) {
                        new LoadConnecting().execute();
                    } else if (menuDataCollection.get(position).get(KEY_MENU)
                            .equals("Coaching Monitoring")) {
                    }
                }
            });
        } catch (IOException ex) {
            Log.e("Error", ex.getMessage());
        } catch (Exception ex) {
            Log.e("Error", "Loading exception");
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
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
                return true;
        }
        return false;
    }

    // Do connecting with asynctask
    class LoadConnecting extends AsyncTask<String, String, String> {
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pDialog = new ProgressDialog(MainMenu.this);
            pDialog.setMessage("Connecting ...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(false);
            pDialog.show();
        }

        protected String doInBackground(String... args) {
            try {
                Thread.sleep(10);
                isInternetPresent = cd.isConnectingToInternet();
                if (isInternetPresent) {
                    MESSAGE = "SUKSES";
                } else {
                    MESSAGE = "Connection Failed !!!";
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
                        Intent myIntent = new Intent();
                        if (session.isLoggedIn()) {
                            myIntent.setClass(MainMenu.this,
                                    HomeGoldenRules.class);
                            startActivity(myIntent);
                        } else {
                            myIntent.putExtra("LogFrom", "GoldenRules");
                            myIntent.putExtra("status", "baru");
                            myIntent.putExtra("Id", "4");
                            myIntent.setClass(MainMenu.this,
                                    LoginActivity.class);
                            startActivity(myIntent);
                        }
                    } else {
                        Toast.makeText(getApplicationContext(), MESSAGE,
                                Toast.LENGTH_LONG).show();
                    }
                }
            });
        }
    }

}
