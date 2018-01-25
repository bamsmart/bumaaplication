package com.buma.designapp;


import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Build;
import android.os.Bundle;
import android.text.Html;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;

import com.buma.utils.SpinnerNavItem;
import com.buma.utils.TitleNavigationAdapter;

import java.util.ArrayList;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressWarnings("deprecation")
public class TabGreenCard extends TabActivity implements ActionBar.OnNavigationListener {
    // Title navigation Spinner data
    private ArrayList<SpinnerNavItem> navSpinner;

    // Navigation adapter
    private TitleNavigationAdapter adapter;

    // Refresh menu item
    private MenuItem refreshMenuItem;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_green_card);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        // Hide the action bar title
        actionBar.setDisplayShowTitleEnabled(false);

        // Enabling Spinner dropdown navigation
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_LIST);

        // Spinner title navigation data
        navSpinner = new ArrayList<SpinnerNavItem>();
        navSpinner.add(new SpinnerNavItem("Galleries", R.drawable.gallery_icon));
        navSpinner
                .add(new SpinnerNavItem("Camera", R.drawable.camera_icon));

        // title drop down adapter
        adapter = new TitleNavigationAdapter(getApplicationContext(),
                navSpinner);

        // assigning the spinner navigation
        actionBar.setListNavigationCallbacks(adapter, this);

        // Changing the action bar icon
        // actionBar.setIcon(R.drawable.ico_actionbar);

        actionBar.setTitle(Html.fromHtml("<font color=\"#fff\">" + "GreenCard<b>" + "</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color.BLACK));

        TabHost tabHost = getTabHost();

        // Tab for Photos
        TabSpec inputspec = tabHost.newTabSpec("Input");
        inputspec.setIndicator("Input", getResources().getDrawable(R.drawable.icon_input_greencard_tab));
        Intent greenCardIntent = new Intent(this, GreenCardActivity.class);
        greenCardIntent.putExtra("status", "baru");
        inputspec.setContent(greenCardIntent);

        // Tab for Songs
        TabSpec listspec = tabHost.newTabSpec("Data");
        // setting Title and Icon for the Tab
        listspec.setIndicator("Data", getResources().getDrawable(R.drawable.icon_list_greencard_tab));
        Intent listGreenCardIntent = new Intent(this, ListGreenCard.class);
        listspec.setContent(listGreenCardIntent);


        // Adding all TabSpec to TabHost
        tabHost.addTab(inputspec); // Adding photos tab
        tabHost.addTab(listspec); // Adding songs tab
    }

    /**
     * On selecting action bar icons
     */
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Take appropriate action for each action item click
        switch (item.getItemId()) {
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.activity_main_actions, menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onNavigationItemSelected(int itemPosition, long itemId) {
        switch (itemPosition) {
            case 0:
                //GreenCardActivity gallery = new GreenCardActivity();
                //gallery.onCreate(Bundle);
                //gallery.GalleryOpen();
                break;
            case 1:
                //GreenCardActivity camera = new GreenCardActivity();
                //camera.CameraOn();
            default:
                break;
        }
        return false;
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

            GreenCardDataActivity data = new GreenCardDataActivity();
            data.TampilkanData();
        }
    }

}
