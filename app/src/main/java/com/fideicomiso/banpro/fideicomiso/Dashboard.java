package com.fideicomiso.banpro.fideicomiso;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.SimpleAdapter;


import java.util.ArrayList;
import java.util.HashMap;

public class Dashboard extends Activity {
    ListView listView;
    ListAdapter adapter;

    ArrayList<HashMap<String, String>> arrList;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }

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


        Conexion conexion = new Conexion(getApplicationContext(), "Delta2", null, 3);
        ArrayList puntos =  conexion.searchRegistration("puntos", datos, " estado = 0 ", null, " DESC");

        try{

            for (int i = 0; i < puntos.size(); i++) {
                HashMap codDoc = (HashMap) puntos.get(i);
                HashMap<String, String> map1 = new HashMap<String, String>();
                map1.put("id", codDoc.get("id").toString());
                map1.put("name", codDoc.get("contactos").toString());
                map1.put("url", codDoc.get("direccion").toString());
                arrList.add(map1);
            }
        } catch ( Exception e) {
            e.printStackTrace();
        }


        if(!arrList.isEmpty()){
                    adapter = new SimpleAdapter( this, arrList,
                    R.layout.list_item, new String[] { "id", "name", "url" },
                    new int[] { R.id.wid, R.id.name, R.id.url });

            listView.setAdapter(adapter);
        }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                HashMap<String, String> item = (HashMap<String, String>) parent.getItemAtPosition(position);
                String id_item = item.get("id");

                Intent intent = null;
                ConnectionDetector conDec = new  ConnectionDetector(getApplicationContext());
                if(conDec.connectionVerification())
                {
                    intent = new Intent(Dashboard.this,MarkersActivity.class);
                }
                else
                    {
                        intent = new Intent(Dashboard.this,GrabarAudioActivity.class);
                    }
                intent.putExtra("ID",id_item);
                startActivity(intent);
            }
        });

        SearchView search = (SearchView) findViewById(R.id.search);


        search.setOnQueryTextListener(new SearchView.OnQueryTextListener() {

            @Override
            public boolean onQueryTextSubmit(String query) {
                ((SimpleAdapter)Dashboard.this.adapter).getFilter().filter(query);
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                ((SimpleAdapter)Dashboard.this.adapter).getFilter().filter(newText);

                return false;
            }


        });


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {

        switch (item.getItemId()) {
            case R.id.logout :
                logoutUser();
                return true ;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
    private void logoutUser() {
        session.setLogin(false,0);
        Intent intent = new Intent(Dashboard.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }




}


