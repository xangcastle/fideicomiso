package com.fideicomiso.banpro.fideicomiso.View;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.SearchView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.fideicomiso.banpro.fideicomiso.Adapter.AdapterPuntosPendientes;
import com.fideicomiso.banpro.fideicomiso.Adapter.AdapterPuntosRegistrados;
import com.fideicomiso.banpro.fideicomiso.Clases.Conexion;
import com.fideicomiso.banpro.fideicomiso.Clases.ConnectionDetector;
import com.fideicomiso.banpro.fideicomiso.Clases.Punto;
import com.fideicomiso.banpro.fideicomiso.Clases.SessionManager;
import com.fideicomiso.banpro.fideicomiso.Controller.AppConfig;
import com.fideicomiso.banpro.fideicomiso.Controller.AppController;
import com.fideicomiso.banpro.fideicomiso.R;
import com.robohorse.gpversionchecker.GPVersionChecker;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

public class RegistradosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener{
    ListView listView;
    private ProgressDialog pDialog;
    private RequestQueue requestQueue;


    ArrayList<Punto> arrList;
    SearchView search;
    private SessionManager session;
    private TextView mensaje ;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_registrados);
        session = new SessionManager(getApplicationContext());

        if (!session.isLoggedIn()) {
            logoutUser();
        }
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
        arrList = new ArrayList<Punto>();
        listView = (ListView) findViewById(R.id.listview);
        LinearLayout cont = (LinearLayout) findViewById(R.id.empty);
        listView.setEmptyView(cont);

        Bundle extras = getIntent().getExtras();
        String response ="";
        Boolean res =true;

        String[] datos = new String[12];
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
        datos[11] = "estado";

        Conexion conexion = new Conexion(getApplicationContext(), "Delta3", null, 3);
        ArrayList puntos =  conexion.searchRegistration("puntos", datos, " estado != 0 ", null, " DESC");

        try{

            for (int i = 0; i < puntos.size(); i++) {
                HashMap codDoc = (HashMap) puntos.get(i);
                Punto p = new Punto(codDoc.get("id").toString(),codDoc.get("departamento").toString(),codDoc.get("municipio").toString(),codDoc.get("barrio").toString(),codDoc.get("comarca").toString(),codDoc.get("comunidad").toString(),codDoc.get("contactos").toString(),codDoc.get("direccion").toString(),codDoc.get("suvecion").toString());
                p.setEStado(codDoc.get("estado").toString());
                arrList.add(p);
            }
        } catch ( Exception e) {
            e.printStackTrace();
        }


        search = (SearchView) findViewById(R.id.search);
        mensaje =(TextView) findViewById(R.id.emptyMensaje);
        if(!arrList.isEmpty()){

            listView.setAdapter(new AdapterPuntosRegistrados(arrList, RegistradosActivity.this));
            listView.setTextFilterEnabled(true);
            mensaje.setVisibility(View.INVISIBLE);
            search.setVisibility(View.VISIBLE);
        }
        else
        {
            search.setVisibility(View.INVISIBLE);
            mensaje.setVisibility(View.VISIBLE);
        }

       /* listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Punto item = (Punto) parent.getItemAtPosition(position);
                String id_item = item.getId();

                Intent intent = null;
                ConnectionDetector conDec = new  ConnectionDetector(getApplicationContext());
                intent = new Intent(RegistradosActivity.this,DetalleRegistroActivity.class);
                intent.putExtra("ID",id_item);
                startActivity(intent);
            }
        });*/
        setupSearchView();
        requestQueue = Volley.newRequestQueue(this);
    }

    private void setupSearchView() {
        search .setIconifiedByDefault(false);
        search.setOnQueryTextListener(this);
        search.setSubmitButtonEnabled(true);
        search.setQueryHint("Buscar");
        search.setFocusable(false);
        search.setFocusableInTouchMode(true);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        ConnectionDetector conDec = new  ConnectionDetector(getApplicationContext());
        if(conDec.connectionVerification()){
            switch (item.getItemId()) {
                case R.id.logout :
                    logoutUser();
                    return true ;
                default:
                    return super.onOptionsItemSelected(item);
            }
        }
        else
        {
            Toast.makeText(getApplicationContext(),
                    "Acción invalida no hay RED", Toast.LENGTH_LONG).show();
            return super.onOptionsItemSelected(item);
        }
    }
    private void logoutUser() {
        session.setLogin(false,0);
        Intent intent = new Intent(RegistradosActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }


    @Override
    public boolean onQueryTextSubmit(String query) {
        return false;
    }

    @Override
    public boolean onQueryTextChange(String newText) {
        if (TextUtils.isEmpty(newText)) {
            listView.clearTextFilter();
        } else {
            listView.setFilterText(newText);
        }

        return true;
    }
}
