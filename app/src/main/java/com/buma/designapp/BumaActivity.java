package com.buma.designapp;

import android.app.Activity;
import android.os.Bundle;
import android.view.Menu;

public class BumaActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_buma);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.buma, menu);
        return true;
    }

}
