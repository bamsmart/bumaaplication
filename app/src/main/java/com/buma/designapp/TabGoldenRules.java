package com.buma.designapp;

import android.annotation.TargetApi;
import android.app.ActionBar;
import android.app.TabActivity;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.NavUtils;
import android.text.Html;
import android.view.MenuItem;
import android.widget.TabHost;
import android.widget.TabHost.TabSpec;
import android.widget.TextView;

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressWarnings("deprecation")
public class TabGoldenRules extends TabActivity {
    private String myPositionId, NmUser, mySite;
    private TextView TvUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tab_golden_rules);

        ActionBar actionBar = getActionBar();
        actionBar.setDisplayHomeAsUpEnabled(true);
        actionBar.setTitle(Html.fromHtml("<font color=\"#c65910\">"
                + "BUMA Golden <b>Rules<b>" + "</font>"));
        actionBar.setBackgroundDrawable(new ColorDrawable(Color
                .parseColor("#f79e21")));

        Intent intent = getIntent();
        TvUser = (TextView) findViewById(R.id.gr_text_user);
        TvUser.setText(Html.fromHtml(intent.getExtras().getString("NmUser")));

        NmUser = intent.getExtras().getString("NmUser");
        myPositionId = intent.getExtras().getString("myPositionId");
        mySite = intent.getExtras().getString("mySite");

        // Resources res = getResources();
        TabHost tabHost = getTabHost();

        // Tab for weekly
        TabSpec inputspec = tabHost.newTabSpec("Weekly");

        inputspec
                .setIndicator(
                        "Weekly",
                        getResources().getDrawable(
                                R.drawable.icon_input_greencard_tab));
        Intent myWeek = new Intent(this, GoldenRulesWeekly.class);
        myWeek.putExtra("NmUser", NmUser);
        myWeek.putExtra("myPositionId", myPositionId);
        myWeek.putExtra("mySite", mySite);

        inputspec.setContent(myWeek);
        tabHost.addTab(inputspec);

        // Tab for monthly
        TabSpec listspec = tabHost.newTabSpec("Monthly");
        listspec.setIndicator("Monthly",
                getResources().getDrawable(R.drawable.icon_list_greencard_tab));
        Intent myMonth = new Intent(this, GoldenRulesMonthly.class);

        myMonth.putExtra("NmUser", NmUser);
        myMonth.putExtra("myPositionId", myPositionId);
        myMonth.putExtra("mySite", mySite);

        listspec.setContent(myMonth);
        tabHost.addTab(listspec);

        tabHost.setCurrentTab(1);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent();
                intent.setClass(getApplicationContext(), HomeGoldenRules.class);
                NavUtils.navigateUpTo(this, intent);
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
