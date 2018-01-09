package com.deltacopiers.banpro.fideicomiso;

import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SimpleAdapter;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;

public class dashboard extends AppCompatActivity {
    private TextView txt_viewtext;
    ListView listView;

    ArrayList<HashMap<String, String>> arrList;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Snackbar.make(view, "Replace with your own action", Snackbar.LENGTH_LONG)
                        .setAction("Action", null).show();
            }
        });

        listView = (ListView) findViewById(R.id.listview);
        arrList = new ArrayList<HashMap<String, String>>();


       


        if(!arrList.isEmpty()){
            ListAdapter adapter = new SimpleAdapter( this, arrList,
                    R.layout.list_item, new String[] { "id", "name", "url" },
                    new int[] { R.id.wid, R.id.name, R.id.url });

            listView.setAdapter(adapter);
        }
    }


}


