package com.fideicomiso.banpro.fideicomiso.View;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Environment;
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

import com.android.volley.RequestQueue;
import com.fideicomiso.banpro.fideicomiso.Adapter.AdapterPuntosRegistrados;
import com.fideicomiso.banpro.fideicomiso.Clases.Conexion;
import com.fideicomiso.banpro.fideicomiso.Clases.ConnectionDetector;
import com.fideicomiso.banpro.fideicomiso.Clases.Registro;
import com.fideicomiso.banpro.fideicomiso.Clases.SessionManager;
import com.fideicomiso.banpro.fideicomiso.R;
import com.fideicomiso.banpro.fideicomiso.Sincronizar.SincronizacionVideos;
import java.util.ArrayList;
import java.util.HashMap;

public class RegistradosActivity extends AppCompatActivity implements SearchView.OnQueryTextListener, SincronizacionVideos.ListenerSincronizacionImagenes{
    ListView listView;
    private ProgressDialog pDialog;
    private RequestQueue requestQueue;
    Registro item;



    ArrayList<Registro> arrList;
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

        listView = (ListView) findViewById(R.id.listview);
        LinearLayout cont = (LinearLayout) findViewById(R.id.empty);
        listView.setEmptyView(cont);

        Boolean c = cargarData();
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

        setupSearchView();
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener(){
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                ConnectionDetector conDec = new  ConnectionDetector(getApplicationContext());
                if(conDec.connectionVerification())
                {
                    item = (Registro) parent.getItemAtPosition(position);
                    if(!item.getEstado().equals("ENVIADO"))
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                        builder
                                .setMessage("Desea Sincronizar este registro ?")
                                .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        pDialog.setMessage("Sincronizando ..."+item.getPunto());
                                        showDialog();
                                        SincronizacionVideos sincronizacionImagenes = new SincronizacionVideos(getApplication(),item.getRuta(), item.getLongitud(),  item.getLatitud(), item.getFecha(),  item.getUsuario() , item.getPunto(),item.getPunto(),item.getTipo(),item.getComentarios(),item.getCedula1(),item.getVivienda(), item.getCedula2(),item.getNcedula(),item.getNombre(),RegistradosActivity.this);

                                        sincronizacionImagenes.execute();
                                    }
                                })
                                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    }

                }
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());
                    builder
                            .setMessage("Debe tener Internet para sincronizar")
                            .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }




            }
        });

    }

    private Boolean cargarData()
    {
        arrList = new ArrayList<Registro>();
        String[] datos = new String[16];
        datos[0] = "_id";
        datos[1] = "longitud";
        datos[2] = "latitud";
        datos[3] = "fecha";
        datos[4] = "ruta";
        datos[5] = "punto";
        datos[6] = "estado";
        datos[7] = "comentario";
        datos[8] = "casa";
        datos[9] = "tipo";
        datos[10] = "cedula";
        datos[11] = "cedula2";
        datos[12] = "ncedula";
        datos[13] = "nombre";
        datos[14] = "ncedula";
        datos[14] = "usuario";


        Conexion conexion = new Conexion(getApplicationContext(), "Delta3", null, 3);
        ArrayList puntos =  conexion.searchRegistration("registros", datos, null, null, " DESC");

        try{

            for (int i = 0; i < puntos.size(); i++) {
                HashMap codDoc = (HashMap) puntos.get(i);
                Registro p = new Registro(codDoc.get("_id").toString(),codDoc.get("longitud").toString(),codDoc.get("latitud").toString(),codDoc.get("fecha").toString(),codDoc.get("ruta").toString(),codDoc.get("punto").toString(),codDoc.get("estado").toString(),codDoc.get("ncedula").toString(),codDoc.get("nombre").toString(),codDoc.get("comentario").toString(),codDoc.get("cedula").toString(),codDoc.get("cedula2").toString(),codDoc.get("casa").toString(),codDoc.get("tipo").toString(),codDoc.get("usuario").toString());
                arrList.add(p);
            }
            listView.setAdapter(new AdapterPuntosRegistrados(arrList, RegistradosActivity.this));
            if(!arrList.isEmpty()){
                mensaje.setVisibility(View.INVISIBLE);
                search.setVisibility(View.VISIBLE);
            }
            else
            {
                search.setVisibility(View.INVISIBLE);
                mensaje.setVisibility(View.VISIBLE);
            }
            return true;
        } catch ( Exception e) {
            e.printStackTrace();
            return  false;
        }
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

    @Override
    public void enSincronizacionFinalizada(int codigo, String id_punto) {
        if(codigo == 3)
        {
                Conexion conexion = new Conexion(getApplicationContext() , "Delta3", null, 3);
                String[][] datos = new String[1][2];
                datos[0][0] = "estado";
                datos[0][1] = "3";
                long respuesta =  conexion.update("puntos",datos, " id =  "+id_punto);
                respuesta =  conexion.update("registros",datos, " punto =  "+id_punto);
                cargarData();
        }
        hideDialog();
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }
}
