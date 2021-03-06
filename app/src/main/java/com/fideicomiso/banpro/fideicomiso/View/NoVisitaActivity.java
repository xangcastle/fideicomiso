package com.fideicomiso.banpro.fideicomiso.View;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.location.LocationManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.content.Intent;
import android.os.StatFs;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.Toast;

import com.fideicomiso.banpro.fideicomiso.Clases.Conexion;
import com.fideicomiso.banpro.fideicomiso.Clases.GPSTracker;
import com.fideicomiso.banpro.fideicomiso.Clases.ImagePicker;
import com.fideicomiso.banpro.fideicomiso.Clases.SessionManager;
import com.fideicomiso.banpro.fideicomiso.R;
import com.fideicomiso.banpro.fideicomiso.Sincronizar.Sincronizacion;
import com.fideicomiso.banpro.fideicomiso.Sincronizar.SincronizacionBroadcast;

public class NoVisitaActivity extends AppCompatActivity implements AdapterView.OnItemSelectedListener {

    private ProgressDialog pDialog;
    private SessionManager session;
    private ImageView imageView;
    private Bitmap imagen = null;
    Boolean rechazo = false;
    String ruta = "";
    String id__Punto="";
    String comentario = "";
    AlertDialog alert = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_visita);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn())
            logoutUser();




        if(Build.VERSION.SDK_INT<=23) {
            LocationManager locationManager = (LocationManager) getSystemService(LOCATION_SERVICE);
            /****Mejora****/
            if (!locationManager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                AlertNoGps();
            }
            /********/
        }

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        Bundle extras = getIntent().getExtras();
        if (extras != null) {
            id__Punto = extras.getString("ID");
            ruta = extras.getString("ruta");
            rechazo = extras.getBoolean("rechazo");
        }



        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

               if(imagen == null || comentario.equals("Seleccione una causal") || comentario.equals("") )
               {
                   AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                   builder
                           .setMessage("Debe tomar una foto y seleccionar una causal ")
                           .setNegativeButton("Entiendo", new DialogInterface.OnClickListener() {
                               @Override
                               public void onClick(DialogInterface dialog,int id) {
                                   dialog.cancel();
                               }
                           })
                           .show();
               }
               else
                   {
                       StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
                       long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
                       long megAvailable = bytesAvailable / (1024 * 1024);
                       if(megAvailable <15)
                       {
                           AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

                           builder
                                   .setMessage("La memoria del telefono esta llena")
                                   .setNegativeButton("Entiendo", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog,int id) {
                                           dialog.cancel();
                                       }
                                   })
                                   .show();
                       }
                       else {
                           android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(view.getContext());

                           builder
                                   .setMessage("Desea terminar esta visita ?")
                                   .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int id) {

                                           String vivienda = "";
                                           try {
                                               String path = Environment.getExternalStorageDirectory().toString() + "/fideicomiso/vivienda/";
                                               OutputStream fOut = null;
                                               long time = System.currentTimeMillis();
                                               File f = new File(path);
                                               if (!f.exists()) {
                                                   f.mkdirs();
                                               }


                                               vivienda = saveImage(imagen, path, id__Punto + "_vivienda_" + time + ".jpg");

                                           } catch (Exception e) {
                                               AlertDialog.Builder builder = new AlertDialog.Builder(getApplication());

                                               builder
                                                       .setMessage("Memoria insuficiente , verifique que tenga espacio para poder grabar")

                                                       .setNegativeButton("Entiendo", new DialogInterface.OnClickListener() {
                                                           @Override
                                                           public void onClick(DialogInterface dialog, int id) {
                                                               dialog.cancel();
                                                           }
                                                       })
                                                       .show();

                                           }
                                           if (vivienda.equals("")) {
                                               return;
                                           }

                                           GPSTracker gps = new GPSTracker(getApplicationContext());

                                           // check if GPS enabled
                                           if (gps.canGetLocation()) {

                                               double latitude = gps.getLatitude();
                                               double longitude = gps.getLongitude();
                                               if (latitude ==0|| latitude ==0.0 || longitude ==0.0||longitude ==0)
                                               {
                                                   AlertDialog.Builder builder = new AlertDialog.Builder(NoVisitaActivity.this);
                                                   builder
                                                           .setMessage("Toda la información esta bien , pero no podemos ubicar coordenadas , necesitamos que salga a un lugar descubierto y de click nuevamente en el boton registrar")

                                                           .setNegativeButton("Entiendo", new DialogInterface.OnClickListener() {
                                                               @Override
                                                               public void onClick(DialogInterface dialog, int id) {
                                                                   dialog.cancel();
                                                               }
                                                           })
                                                           .show();
                                               }
                                               else
                                                   {
                                                       SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault());
                                                       Date date = new Date();
                                                       String fecha = dateFormat.format(date);


                                                       SessionManager session = new SessionManager(getApplicationContext());
                                                       String[][] data = new String[14][2];
                                                       data[0][0] = "longitud";
                                                       data[0][1] = "" + longitude;
                                                       data[1][0] = "latitud";
                                                       data[1][1] = "" + latitude;
                                                       data[2][0] = "fecha";
                                                       data[2][1] = fecha;
                                                       data[3][0] = "ruta";
                                                       data[3][1] = ruta;
                                                       data[4][0] = "punto";
                                                       data[4][1] = id__Punto;
                                                       data[5][0] = "usuario";
                                                       data[5][1] = "" + session.get_user();
                                                       data[6][0] = "cedula";
                                                       data[6][1] = "";
                                                       data[7][0] = "casa";
                                                       data[7][1] = vivienda;
                                                       data[8][0] = "tipo";
                                                       data[8][1] = ((rechazo)?"0":"2");
                                                       data[9][0] = "comentario";
                                                       data[9][1] = comentario;
                                                       data[10][0] = "estado";
                                                       data[10][1] = "1";
                                                       data[11][0] = "ncedula";
                                                       data[11][1] = "";
                                                       data[12][0] = "nombre";
                                                       data[12][1] = "";
                                                       data[13][0] = "cedula2";
                                                       data[13][1] = "";

                                                       Conexion conexion = new Conexion(getApplicationContext(), "Delta3", null, 3);
                                                       long respuesta = conexion.insertRegistration("registros", data);

                                                       String[][] datos = new String[1][2];
                                                       datos[0][0] = "estado";
                                                       datos[0][1] = "1";

                                                       respuesta = conexion.update("puntos", datos, " id =  " + id__Punto);
                                                       if (Build.VERSION.SDK_INT >= 19) {
                                                           Intent alarmIntent = new Intent(getApplicationContext(), SincronizacionBroadcast.class);
                                                           PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);
                                                           AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                                           manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + LoginActivity.INTERVALOTIEMPOSINCRONIZACION, pendingIntent);
                                                       }
                                                       Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                                       startActivity(intent);
                                                       finish();
                                                   }

                                           } else {
                                               AlertNoGps();
                                               //gps.showSettingsAlert();
                                           }

                                       }
                                   })
                                   .setNegativeButton("No", new DialogInterface.OnClickListener() {
                                       @Override
                                       public void onClick(DialogInterface dialog, int id) {
                                           dialog.cancel();
                                       }
                                   })
                                   .show();
                       }
                   }
            }
        });

        imageView = (ImageView) findViewById(R.id.imageview);
        imageView.getLayoutParams().height = 300;
        imageView.getLayoutParams().width = 300;


        Button boton = (Button) findViewById(R.id.btnTomaFoto);


        String table = "" ;
        if(rechazo)
        {
            boton.setText("Foto con el Cliente");
            table = "tipos_no_abrir";
        }
        else
            {
                boton.setText("Foto Casa");
                table = "tipos_no_pudo";
            }


        Spinner spinner = (Spinner) findViewById(R.id.spinner);
        spinner.setOnItemSelectedListener(this);
        String[] datos = new String[1];
        datos[0] = "nombre";

        Conexion conexion = new Conexion(getApplicationContext(), "Delta3", null, 3);
        ArrayList result =   conexion.searchRegistration(table, datos,"", null, " DESC");

        ArrayList<String> options = new ArrayList<String>();

        options.add("Seleccione una causal");
        for (int i = 0; i < result.size(); i++) {
            HashMap codDoc = (HashMap) result.get(i);
            options.add(codDoc.get("nombre").toString());
        }
        final ArrayAdapter<String> spinnerArrayAdapter = new ArrayAdapter<String>(
                this,R.layout.spinner_item,options);

        spinnerArrayAdapter.setDropDownViewResource(R.layout.spinner_item);
        spinner.setAdapter(spinnerArrayAdapter);



        boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Abre la camara para tomar la foto
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(v.getContext());
                startActivityForResult(chooseImageIntent, 1);
            }

        });
    }
    private String saveImage(Bitmap thumbnail,String path ,String name)  {
        String respuesta = "" ;
        try {
            if (thumbnail != null) {
                ByteArrayOutputStream bytes = new ByteArrayOutputStream();
                thumbnail.compress(Bitmap.CompressFormat.PNG, 100, bytes);

                File destination = new File(path + name);
                FileOutputStream fo;
                respuesta = destination.getPath();
                destination.createNewFile();
                fo = new FileOutputStream(destination);
                fo.write(bytes.toByteArray());
                fo.close();
            }
        }
        catch (IOException e)
        {

        }
        return respuesta;
    }





    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
    }

    private void logoutUser() {
        session.setLogin(false, 0);

        // Launching the login activity
        Intent intent = new Intent(NoVisitaActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @TargetApi(23)
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case 100:
                if (grantResults.length > 0
                        || grantResults[0] == PackageManager.PERMISSION_GRANTED
                        || grantResults[1] == PackageManager.PERMISSION_GRANTED) {
                    /* User checks permission. */

                } else {
                    Toast.makeText(NoVisitaActivity.this, "Permission is denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                //return; // delete.
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1||requestCode==100 ) {
            if (resultCode == RESULT_OK) {
                imagen = ImagePicker.getImageFromResult(this, resultCode, data);
                imageView.setImageBitmap(imagen);
            }
        }
    }


    private void AlertNoGps() {
        try {
            final AlertDialog.Builder builder = new AlertDialog.Builder(NoVisitaActivity.this);
            builder.setMessage("El sistema GPS esta desactivado, ¿Desea activarlo?")
                    .setCancelable(false)
                    .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                        public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                        }
                    })
                    .setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                            dialog.cancel();
                        }
                    });
            alert = builder.create();
            alert.show();
        }
        catch (Exception e)
        {
            Toast.makeText(NoVisitaActivity.this, "Verifique GPS.", Toast.LENGTH_SHORT).show();
        }

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle item selection
        switch (item.getItemId()) {
            case android.R.id.home:
                Intent intent = new Intent(getApplicationContext(), GrabarAudioActivity.class);
                intent.putExtra("ruta",this.ruta);
                intent.putExtra("ID",this.id__Punto);
                startActivity(intent);
                finish();
                break;
            default:
                return super.onOptionsItemSelected(item);
        }
        return true;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
        comentario = parent.getItemAtPosition(position).toString();
    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {

    }
}

