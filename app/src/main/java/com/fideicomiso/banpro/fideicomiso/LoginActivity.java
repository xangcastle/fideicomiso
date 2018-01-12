package com.fideicomiso.banpro.fideicomiso;

import android.Manifest;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.support.v4.app.ActivityCompat;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends Activity {
    private Button btn_login;
    private EditText txt_usuario;
    private EditText txt_password;
    private SessionManager session;
    private ProgressDialog pDialog;
    public static final long INTERVALOTIEMPOSINCRONIZACION = 60000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = (Button)findViewById(R.id.btnLogin);
        txt_usuario = (EditText)findViewById(R.id.email);
        txt_password = (EditText)findViewById(R.id.password);
        session = new SessionManager(getApplicationContext());


        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_FINE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    0);
        }

        if (android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.READ_EXTERNAL_STORAGE))
        {
            android.support.v4.app.ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.VIBRATE, Manifest.permission.RECEIVE_BOOT_COMPLETED,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        } else
        {
            android.support.v4.app.ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE, Manifest.permission.RECEIVE_BOOT_COMPLETED,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                    0);
        }
        if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                Manifest.permission.ACCESS_COARSE_LOCATION)) {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        } else {

            // Camera permission has not been granted yet. Request it directly.
            ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                    0);
        }

        if (session.isLoggedIn()) {
            if (!isMyServiceRunning(SincronizacionService.class)){ //método que determina si el servicio ya está corriendo o no
                Intent serv = new Intent(getApplicationContext(),SincronizacionService.class); //serv de tipo Intent
                getApplicationContext().startService(serv); //ctx de tipo Context
            }
            if(Build.VERSION.SDK_INT>=19) {
                Intent alarmIntent = new Intent(getApplicationContext(), SincronizacionBroadcast.class);
                PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);
                AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + LoginActivity.INTERVALOTIEMPOSINCRONIZACION, pendingIntent);
            }
            Intent intent = new Intent(LoginActivity.this, Dashboard.class);
            startActivity(intent);
            finish();
        }



        btn_login.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                String usuario = txt_usuario.getText().toString();
                String password = txt_password.getText().toString();
                pDialog.setMessage("Cargando datos ...");
                showDialog();
                checkLogin(usuario, password);
            }
        });
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
    }

    private void checkLogin(final String usuario, final String password) {
        // Tag used to cancel the request
        String tag_string_req = "req_login";
        StringRequest strReq = new StringRequest(Request.Method.POST,
                AppConfig.URL_LOGIN, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {

                try {
                    JSONObject jObj = new JSONObject(response);

                    String id = jObj.getString("id");
                    String username = jObj.getString("username");
                    String nombre = jObj.getString("nombre");
                    JSONArray puntos = jObj.getJSONArray("puntos");
                    session.setLogin(true,Integer.parseInt(id));

                    String[][] data = new String[3][2];
                    data[0][0] = "id";
                    data[0][1] = id;
                    data[1][0] = "username";
                    data[1][1] = username;
                    data[2][0] = "nombre";
                    data[2][1] = nombre;



                    Conexion conexion = new Conexion(getApplicationContext(), "Delta", null, 3);
                    //conexion.deleteTabla();
                    long respuesta = conexion.insertRegistration("usuarios", data);

                    for(int i = 0 ; i<puntos.length();i++)
                    {
                        data = new String[11][2];
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

                        respuesta = conexion.insertRegistration("puntos", data);
                    }
                    if (!isMyServiceRunning(SincronizacionService.class)){ //método que determina si el servicio ya está corriendo o no
                        Intent serv = new Intent(getApplicationContext(),SincronizacionService.class); //serv de tipo Intent
                        getApplicationContext().startService(serv); //ctx de tipo Context
                    }
                    if(Build.VERSION.SDK_INT>=19) {
                        Intent alarmIntent = new Intent(getApplicationContext(), SincronizacionBroadcast.class);
                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);
                        AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                        manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + LoginActivity.INTERVALOTIEMPOSINCRONIZACION, pendingIntent);
                    }
                    hideDialog();
                    // Launch main activity
                    Intent intent = new Intent(LoginActivity.this, Dashboard.class);
                    startActivity(intent);
                    finish();


                } catch (JSONException e) {
                    // JSON error
                    hideDialog();
                    e.printStackTrace();
                    Toast.makeText(getApplicationContext(),
                            R.string.message_error, Toast.LENGTH_LONG).show();

                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                Toast.makeText(getApplicationContext(),
                        R.string.message_error, Toast.LENGTH_LONG).show();
            }
        }) {

            @Override
            protected Map<String, String> getParams() {
                // Posting parameters to login url
                Map<String, String> params = new HashMap<String, String>();
                params.put("usuario", usuario);
                params.put("password", password);
                return params;
            }

        };

        // Adding request to request queue
        AppController.getInstance().addToRequestQueue(strReq, tag_string_req);
    }
    private void showDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hideDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public int versionDispositivo()
    {
        int version = 0;
        version = android.os.Build.VERSION.SDK_INT;
        return version;
    }

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE))
        {
            if (serviceClass.getName().equals(service.service.getClassName()))
            {
                return true;
            }
        } return false;
    }

}
