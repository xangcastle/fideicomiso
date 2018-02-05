package com.fideicomiso.banpro.fideicomiso.View;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
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


public class Dashboard extends Activity  implements SearchView.OnQueryTextListener{
    ListView listView;
    private ProgressDialog pDialog;
    private RequestQueue requestQueue;
    private TextView mensaje ;


    ArrayList<Punto> arrList;
    SearchView search;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);
        new GPVersionChecker.Builder(this).create();
        session = new SessionManager(getApplicationContext());
        if(!session.get_reenviar())
        {
            Conexion conexion = new Conexion(getApplicationContext(), "Delta3", null, 3);
            String[][] datos = new String[1][2];
            datos[0][0] = "estado";
            datos[0][1] = "0";
            long respuesta = conexion.update("registros", datos, " fecha = '2018-01-22' " );
            session.set_reenviar(true);
        }

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
        if(extras != null)
        {
            response = extras.getString("respuesta");
            res = registrarData(response);
        }


        try{
            ArrayList puntos = consultarPuntosBD();
            for (int i = 0; i < puntos.size(); i++) {
                HashMap codDoc = (HashMap) puntos.get(i);
                arrList.add(new Punto(codDoc.get("id").toString(),codDoc.get("departamento").toString(),codDoc.get("municipio").toString(),codDoc.get("barrio").toString(),codDoc.get("comarca").toString(),codDoc.get("comunidad").toString(),codDoc.get("contactos").toString(),codDoc.get("direccion").toString(),codDoc.get("suvecion").toString()));
            }
        } catch ( Exception e) {
            e.printStackTrace();
        }


         search = (SearchView) findViewById(R.id.search);
         mensaje =(TextView) findViewById(R.id.emptyMensaje);
        if(!arrList.isEmpty()){

            listView.setAdapter(new AdapterPuntosPendientes(arrList, Dashboard.this));
            listView.setTextFilterEnabled(true);
            mensaje.setVisibility(View.INVISIBLE);
            search.setVisibility(View.VISIBLE);
        }
        else
            {
                search.setVisibility(View.INVISIBLE);
                mensaje.setVisibility(View.VISIBLE);
            }

        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Punto item = (Punto) parent.getItemAtPosition(position);
                String id_item = item.getId();

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
        setupSearchView();
        requestQueue = Volley.newRequestQueue(this);
    }
    private  ArrayList consultarPuntosBD()
    {
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

        Conexion conexion = new Conexion(getApplicationContext(), "Delta3", null, 3);
        ArrayList puntos =  conexion.searchRegistration("puntos", datos, " estado = 0 ", null, " DESC");
        return puntos;
    }
    private void setupSearchView() {
        search .setIconifiedByDefault(false);
        search.setOnQueryTextListener(this);
        search.setSubmitButtonEnabled(true);
        search.setQueryHint("Buscar");
        search.setFocusable(false);
        search.setFocusableInTouchMode(true);
    }



    public boolean registrarDataCausales(String response)
    {
        try
        {
            JSONObject jObj = new JSONObject(response);
            JSONArray causales = jObj.getJSONArray("causales");
            Conexion conexion = new Conexion(getApplicationContext(), "Delta3", null, 3);
            if(conexion.eliminarCausalesSinc())
            {
                String nameTable ="";
                for(int i = 0 ; i<causales.length();i++)
                {
                    nameTable = causales.getJSONObject(i).getString("aplicacion");
                    if(nameTable.equals("0"))
                        nameTable = "tipos_no_pudo" ;
                    else if(nameTable.equals("1"))
                        nameTable = "tipos_no_abrir" ;
                    String[][] data = new String[1][2];
                    data[0][0] = "nombre";
                    data[0][1] = causales.getJSONObject(i).getString("nombre");

                    long respuesta = conexion.insertRegistration(nameTable, data);
                }
            }
            return true ;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    public boolean registrarData(String response)
    {
        try
        {
            JSONObject jObj = new JSONObject(response);
            Conexion conexion = new Conexion(getApplicationContext(), "Delta3", null, 3);
            if(conexion.eliminarPuntosSinc())
            {
                JSONArray puntos = jObj.getJSONArray("puntos");
                for(int i = 0 ; i<puntos.length();i++)
                {
                    String[][] data = new String[11][2];
                    data[0][0] = "id";
                    data[0][1] = puntos.getJSONObject(i).getString("id");
                    data[1][0] = "departamento";
                    data[1][1] = puntos.getJSONObject(i).getString("departamento");
                    data[2][0] = "municipio";
                    data[2][1] = puntos.getJSONObject(i).getString("municipio");
                    data[3][0] = "barrio";
                    data[3][1] = puntos.getJSONObject(i).getString("barrio");
                    data[4][0] = "comarca";
                    data[4][1] = puntos.getJSONObject(i).getString("comarca");
                    data[5][0] = "comunidad";
                    data[5][1] = puntos.getJSONObject(i).getString("comunidad");
                    data[6][0] = "direccion";
                    data[6][1] = puntos.getJSONObject(i).getString("direccion");
                    data[7][0] = "suvecion";
                    data[7][1] = puntos.getJSONObject(i).getString("suvecion");
                    data[8][0] = "contactos";
                    data[8][1] = puntos.getJSONObject(i).getString("contactos");
                    data[9][0] = "longitude";
                    data[9][1] = puntos.getJSONObject(i).getString("longitude");
                    data[10][0] = "latitude";
                    data[10][1] = puntos.getJSONObject(i).getString("latitude");

                    long respuesta = conexion.insertRegistration("puntos", data);
                }
            }
            return true ;
        }
        catch (Exception e)
        {
            return false;
        }
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_principal, menu);
        return super.onCreateOptionsMenu(menu);
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId()==R.id.visitas)
        {
            Intent intent = new Intent(Dashboard.this, RegistradosActivity.class);
            startActivity(intent);
            return true ;
        }
        ConnectionDetector conDec = new  ConnectionDetector(getApplicationContext());
        if(conDec.connectionVerification()){

        switch (item.getItemId()) {
            case R.id.logout :
                android.app.AlertDialog.Builder builder2 = new android.app.AlertDialog.Builder(Dashboard.this);
                builder2
                        .setMessage("Esta seguro que desea cerrar Sesion?")
                        .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                logoutUser();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                        .show();

                return true ;
            case R.id.sincronizar :
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(Dashboard.this);
                builder
                        .setMessage("Esta seguro que desea sincronizar ?")
                        .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                pDialog.setMessage("Sincronizando...");
                                showDialog();
                                SessionManager session = new SessionManager(getApplicationContext());
                                sincronizar(session.get_user()+"");
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                        .show();

                return true ;
            case R.id.sincronizarCausales :
                android.app.AlertDialog.Builder builder3 = new android.app.AlertDialog.Builder(Dashboard.this);
                builder3
                        .setMessage("Esta seguro que desea sincronizar Causales?")
                        .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                pDialog.setMessage("Sincronizando Causales...");
                                showDialog();
                                SessionManager session = new SessionManager(getApplicationContext());
                                sincronizarCausales();
                            }
                        })
                        .setNegativeButton("No", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog,int id) {
                                dialog.cancel();
                            }
                        })
                        .show();
                return true;
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
        Intent intent = new Intent(Dashboard.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }



    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
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




    private void sincronizar(final String usuario)
    {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_PUNTOS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                consultarPuntos(response);
                    hideDialog();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Toast.makeText(getApplicationContext(),
                        R.string.message_error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("usuario", usuario);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }

    private void sincronizarCausales()
    {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_CAUSALES, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                registrarDataCausales(response);
                hideDialog();

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                hideDialog();
                Toast.makeText(getApplicationContext(),
                        R.string.message_error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }


    /**
     * Consulta las gestiones
     *
     * @param response arreglo de json que contiene la informacion para construir los formularios
     */
    public void consultarPuntos(String response) {
        Boolean res = registrarData(response);
        hideDialog();
        if(res)
        {
            ArrayList puntos = consultarPuntosBD();
            arrList = new ArrayList<Punto>();
            for (int i = 0; i < puntos.size(); i++) {
                HashMap codDoc = (HashMap) puntos.get(i);
                arrList.add(new Punto(codDoc.get("id").toString(),codDoc.get("departamento").toString(),codDoc.get("municipio").toString(),codDoc.get("barrio").toString(),codDoc.get("comarca").toString(),codDoc.get("comunidad").toString(),codDoc.get("contactos").toString(),codDoc.get("direccion").toString(),codDoc.get("suvecion").toString()));
            }
            listView.setAdapter(new AdapterPuntosPendientes(arrList, Dashboard.this));
            if(!arrList.isEmpty()){
                mensaje.setVisibility(View.INVISIBLE);
                search.setVisibility(View.VISIBLE);
            }
            else
            {
                search.setVisibility(View.INVISIBLE);
                mensaje.setVisibility(View.VISIBLE);
            }
        }
    }
}


