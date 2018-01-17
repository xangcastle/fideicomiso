package com.fideicomiso.banpro.fideicomiso;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.AlarmManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Build;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Random;


public class VisitaActivity extends AppCompatActivity  {
    private ProgressDialog pDialog;
    private SessionManager session;
    private ImageView imageView;
    private ImageView imageView2;
    private Bitmap imagen;
    private Bitmap imagen2;
    int cara = 0 ;
    String idPunto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_visita);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn())
            logoutUser();

        if(versionDispositivo()>=23)
        {
            if (android.support.v4.app.ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                android.support.v4.app.ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.VIBRATE, Manifest.permission.RECEIVE_BOOT_COMPLETED,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},
                        0);
            } else
            {
                android.support.v4.app.ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE, Manifest.permission.RECEIVE_BOOT_COMPLETED,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE,Manifest.permission.RECORD_AUDIO},
                        0);
            }
        }

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        imageView = (ImageView) findViewById(R.id.imageview);
        imageView2 = (ImageView) findViewById(R.id.imageview2);
        imageView.getLayoutParams().height = 300;
        imageView.getLayoutParams().width = 200;
        imageView2.getLayoutParams().height = 300;
        imageView2.getLayoutParams().width = 200;


        Bundle extras = getIntent().getExtras();

        if(extras != null)
            idPunto = extras.getString("ID");

        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(view.getContext());

                builder
                        .setMessage("Desea terminar Visita?")
                        .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText comentario = (EditText) findViewById(R.id.comment_visita);

                                Bundle extras = getIntent().getExtras();
                                String id__Punto = "";
                                String ruta ="";
                                if (extras != null) {
                                    id__Punto = extras.getString("ID");
                                    ruta = extras.getString("ruta");

                                }
                                String path = Environment.getExternalStorageDirectory().toString()+"/fideicomiso/cedulas/";
                                OutputStream fOut = null;
                                long time = System.currentTimeMillis();
                                File f = new File(path);
                                if (!f.exists()){
                                    f.mkdirs();
                                }
                                String ruta_cedula = saveImage(imagen,path,id__Punto+"_cedula_" + time + ".jpg");
                                String ruta_cedula2 = saveImage(imagen2,path,id__Punto+"_cedula2_" + time + ".jpg");




                                GPSTracker gps = new GPSTracker(getApplicationContext());

                                // check if GPS enabled
                                if (gps.canGetLocation()) {

                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    Date date = new Date();
                                    String fecha = dateFormat.format(date);


                                    EditText nombre = (EditText) findViewById(R.id.nombre);
                                    EditText ncedula = (EditText) findViewById(R.id.cedula);
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
                                    data[6][1] = ruta_cedula;
                                    data[7][0] = "casa";
                                    data[7][1] = "";
                                    data[8][0] = "tipo";
                                    data[8][1] = "1";
                                    data[9][0] = "comentario";
                                    data[9][1] = comentario.getText().toString();
                                    data[10][0] = "estado";
                                    data[10][1] = "1";
                                    data[11][0] = "ncedula";
                                    data[11][1] = ncedula.getText().toString().trim().toUpperCase();
                                    data[12][0] = "nombre";
                                    data[12][1] = nombre.getText().toString().trim().toUpperCase();
                                    data[13][0] = "cedula2";
                                    data[13][1] = ruta_cedula2;

                                    Conexion conexion = new Conexion(getApplicationContext(), "Delta3", null, 3);
                                    long respuesta = conexion.insertRegistration("registros", data);

                                    String[][] datos = new String[1][2];
                                    datos[0][0] = "estado";
                                    datos[0][1] = "1";

                                    respuesta =  conexion.update("puntos",datos, " id =  "+id__Punto);

                                    if(Build.VERSION.SDK_INT>=19) {
                                        Intent alarmIntent = new Intent(getApplicationContext(), SincronizacionBroadcast.class);
                                        PendingIntent pendingIntent = PendingIntent.getBroadcast(getApplicationContext(), 0, alarmIntent, 0);
                                        AlarmManager manager = (AlarmManager) getApplicationContext().getSystemService(Context.ALARM_SERVICE);
                                        manager.setExact(AlarmManager.RTC_WAKEUP, System.currentTimeMillis() + LoginActivity.INTERVALOTIEMPOSINCRONIZACION, pendingIntent);
                                    }
                                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                    startActivity(intent);
                                    finish();

                                } else {
                                    gps.showSettingsAlert();
                                }





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
        });

        //======== codigo nuevo ========
        Button boton = (Button) findViewById(R.id.btnTomaFoto);
        boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Abre la camara para tomar la foto
                Intent chooseImageIntent = ImagePicker.getPickImageIntent(v.getContext());
                startActivityForResult(chooseImageIntent, 1);
            }

        });

        //======== codigo nuevo ========
        Button boton2 = (Button) findViewById(R.id.btnTomaFoto2);

        boton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {

                Intent chooseImageIntent = ImagePicker.getPickImageIntent(v.getContext());
                startActivityForResult(chooseImageIntent, 2);
            }

        });
    }



    @SuppressWarnings("StatementWithEmptyBody")

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        //getMenuInflater().inflate(R.menu.menu_main, menu);//Menu Resource, Menu
        // Inflate the menu items for use in the action bar
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.menu_main, menu);
        return super.onCreateOptionsMenu(menu);
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


    public int versionDispositivo() {
        int version = 0;
        version = android.os.Build.VERSION.SDK_INT;
        return version;
    }

    private void logoutUser() {
        session.setLogin(false, 0);

        // Launching the login activity
        Intent intent = new Intent(VisitaActivity.this, LoginActivity.class);
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
                    Toast.makeText(VisitaActivity.this, "Permission is denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                //return; // delete.
        }
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {

            if (resultCode == RESULT_OK) {

                if (requestCode == 1 )
                {
                    imagen = ImagePicker.getImageFromResult(this, resultCode, data);
                    imageView.setImageBitmap(imagen);
                }
                else if (requestCode == 2 )
                {
                    imagen2 = ImagePicker.getImageFromResult(this, resultCode, data);
                    imageView2.setImageBitmap(imagen2);
                 }
                else
                    {
                        super.onActivityResult(requestCode, resultCode, data);
                    }


        }
    }
}





