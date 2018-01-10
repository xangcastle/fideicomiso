package com.deltacopiers.banpro.fideicomiso;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.AdapterView;
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

        String[] datos = new String[11];
        datos[0] = "id";
        datos[1] = "departamento";
        datos[2] = "municipio";
        datos[3] = "barrio";
        datos[4] = "comarca";
        datos[5] = "comunidad";
        datos[6] = "direccion";
        datos[7] = "suvecion";
        datos[8] = "contactos";
        datos[9] = "longitude";
        datos[10] = "latitude";


        Conexion conexion = new Conexion(getApplicationContext(), "Delta", null, 3);
        ArrayList puntos =  conexion.searchRegistration("puntos", datos, null, null, " DESC");

        try{

            for (int i = 0; i < puntos.size(); i++) {
                HashMap codDoc = (HashMap) puntos.get(i);
                HashMap<String, String> map1 = new HashMap<String, String>();
                map1.put("id", codDoc.get("id").toString());
                map1.put("name", codDoc.get("departamento").toString());
                map1.put("url", codDoc.get("direccion").toString());
                arrList.add(map1);
            }
        } catch ( Exception e) {
            e.printStackTrace();
        }


        if(!arrList.isEmpty()){
            ListAdapter adapter = new SimpleAdapter( this, arrList,
                    R.layout.list_item, new String[] { "id", "name", "url" },
                    new int[] { R.id.wid, R.id.name, R.id.url });

            listView.setAdapter(adapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
                String id_item = item.get("id");
                Intent intent = new Intent(dashboard.this,CameraAppActivity.class);
                intent.putExtra("ID",id_item);
                //based on item add info to intent
                startActivity(intent);
            }
        });
    }


}


