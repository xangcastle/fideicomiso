package com.fideicomiso.banpro.fideicomiso.View;

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
import com.fideicomiso.banpro.fideicomiso.Clases.Conexion;
import com.fideicomiso.banpro.fideicomiso.Clases.ConnectionDetector;
import com.fideicomiso.banpro.fideicomiso.Clases.RuntimePermissionsActivity;
import com.fideicomiso.banpro.fideicomiso.Clases.SessionManager;
import com.fideicomiso.banpro.fideicomiso.Controller.AppConfig;
import com.fideicomiso.banpro.fideicomiso.Controller.AppController;
import com.fideicomiso.banpro.fideicomiso.R;
import com.fideicomiso.banpro.fideicomiso.Sincronizar.SincronizacionBroadcast;
import com.fideicomiso.banpro.fideicomiso.Sincronizar.SincronizacionService;
import com.robohorse.gpversionchecker.GPVersionChecker;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class LoginActivity extends RuntimePermissionsActivity {
    private Button btn_login;
    private EditText txt_usuario;
    private EditText txt_password;
    private SessionManager session;
    private ProgressDialog pDialog;
    private static final int REQUEST_PERMISSIONS = 20;
    public static final long INTERVALOTIEMPOSINCRONIZACION = 65000;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        btn_login = (Button)findViewById(R.id.btnLogin);
        txt_usuario = (EditText)findViewById(R.id.email);
        txt_password = (EditText)findViewById(R.id.password);
        session = new SessionManager(getApplicationContext());


        if(versionDispositivo()>=23)
        {
            LoginActivity.super.requestAppPermissions(new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.INTERNET,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.LOCATION_HARDWARE,Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,Manifest.permission.READ_LOGS,Manifest.permission.ACCESS_COARSE_LOCATION}, R.string.runtime_permissions_txt
                    , REQUEST_PERMISSIONS);
        }
        new GPVersionChecker.Builder(this).create();

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
                ConnectionDetector conDec = new  ConnectionDetector(getApplicationContext());
                if(conDec.connectionVerification())
                {
                    Conexion conexion = new Conexion(getApplicationContext(), "Delta3", null, 3);
                    Boolean elimianados = conexion.deleteTabla();
                    if(!elimianados)
                        return ;
                }
                checkLogin(usuario, password);
            }
        });
        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);
    }

    @Override
    public void onPermissionsGranted(int requestCode) {
        if(requestCode == -1)
        {
            Toast.makeText(this, "Debe Aceptar todos los permisos o la App no funcionara adecuadamente", Toast.LENGTH_LONG).show();
            LoginActivity.super.requestAppPermissions(new
                            String[]{Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.INTERNET,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.RECEIVE_BOOT_COMPLETED, Manifest.permission.LOCATION_HARDWARE,Manifest.permission.RECORD_AUDIO,Manifest.permission.ACCESS_FINE_LOCATION,Manifest.permission.ACCESS_LOCATION_EXTRA_COMMANDS,Manifest.permission.READ_LOGS,Manifest.permission.ACCESS_COARSE_LOCATION}, R.string.runtime_permissions_txt
                    , REQUEST_PERMISSIONS);
        }
       else
           {
               Toast.makeText(this, "Todos los permisos aceptados", Toast.LENGTH_LONG).show();

           }
    }

    private void checkLogin(final String usuario, final String password)
    {
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
                    String[][] data = new String[3][2];
                    data[0][0] = "id";
                    data[0][1] = id;
                    data[1][0] = "username";
                    data[1][1] = username;
                    data[2][0] = "nombre";
                    data[2][1] = nombre;

                    Conexion conexion = new Conexion(getApplicationContext(), "Delta3", null, 3);
                    long respuesta = conexion.insertRegistration("usuarios", data);

                    session.setLogin(true,Integer.parseInt(id));
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
                    intent.putExtra("respuesta",response);
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
