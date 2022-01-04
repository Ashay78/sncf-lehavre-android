package com.gcousin.sncf;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;

public class MainActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    /**
     * Start list activity
     * @param v
     */
    public void startListStart(View v) {
        Intent intentList = new Intent(this, ListActivity.class);
        Bundle data = new Bundle();
        data.putInt("type", 0);
        intentList.putExtras(data);
        startActivity(intentList);
    }

    /**
     * Start list activity
     * @param v
     */
    public void startListEnd(View v) {
        Intent intentList = new Intent(this, ListActivity.class);
        Bundle data = new Bundle();
        data.putInt("type", 1);
        intentList.putExtras(data);
        startActivity(intentList);
    }
}