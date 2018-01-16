package com.fideicomiso.banpro.fideicomiso;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;


public class NoVisitaActivity extends AppCompatActivity {

    private ProgressDialog pDialog;
    private SessionManager session;
    private ImageView imageView;
    private Bitmap imagen;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_visita);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn())
            logoutUser();

        if(versionDispositivo()>=23)
        {
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
        }

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);



        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(view.getContext());

                builder
                        .setMessage("Desea terminar esta visita ?")
                        .setPositiveButton("Si", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText comentario = (EditText) findViewById(R.id.comment_visita);



                                String path = Environment.getExternalStorageDirectory().toString()+"/fideicomiso/";
                                OutputStream fOut = null;
                                long time = System.currentTimeMillis();
                                File f = new File(path);
                                if (!f.exists()){
                                    f.mkdirs();
                                }
                                File file = new File(path , "fideicomiso" + time + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                                try {
                                    fOut = new FileOutputStream(file);
                                    imagen.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                                    fOut.flush(); // Not really required
                                    fOut.close(); // do not forget to close the stream
                                    MediaStore.Images.Media.insertImage(getContentResolver(), file.getAbsolutePath(), file.getName(), file.getName());

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                GPSTracker gps = new GPSTracker(getApplicationContext());

                                // check if GPS enabled
                                if (gps.canGetLocation()) {

                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    Date date = new Date();
                                    String fecha = dateFormat.format(date);
                                    Bundle extras = getIntent().getExtras();
                                    String id__Punto = "";
                                    if (extras != null) {
                                        id__Punto = extras.getString("ID");

                                    }
                                    SessionManager session = new SessionManager(getApplicationContext());
                                    String[][] data = new String[11][2];
                                    data[0][0] = "longitud";
                                    data[0][1] = "" + longitude;
                                    data[1][0] = "latitud";
                                    data[1][1] = "" + latitude;
                                    data[2][0] = "fecha";
                                    data[2][1] = fecha;
                                    data[3][0] = "ruta";
                                    data[3][1] = "";
                                    data[4][0] = "punto";
                                    data[4][1] = id__Punto;
                                    data[5][0] = "usuario";
                                    data[5][1] = "" + session.get_user();
                                    data[6][0] = "cedula";
                                    data[6][1] = "";
                                    data[7][0] = "casa";
                                    data[7][1] = path + "fideicomiso" + time + ".jpg";
                                    data[8][0] = "tipo";
                                    data[8][1] = "0";
                                    data[9][0] = "comentario";
                                    data[9][1] = comentario.getText().toString();
                                    data[10][0] = "estado";
                                    data[10][1] = "1";

                                    Conexion conexion = new Conexion(getApplicationContext(), "Delta2", null, 3);
                                    long respuesta = conexion.insertRegistration("registros", data);

                                    String[][] datos = new String[1][2];
                                    datos[0][0] = "estado";
                                    datos[0][1] = "1";

                                    respuesta =  conexion.update("puntos",datos, " id =  "+id__Punto);

                                    Intent intent = new Intent(getApplicationContext(), Dashboard.class);
                                    startActivity(intent);

                                } else {
                                    gps.showSettingsAlert();
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
        });

        imageView = (ImageView) findViewById(R.id.imageview);






        //======== codigo nuevo ========
        Button boton = (Button) findViewById(R.id.btnTomaFoto);
        //Si no existe crea la carpeta donde se guardaran las fotos
        //accion para el boton
        boton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                //Abre la camara para tomar la foto
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
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


    public int versionDispositivo() {
        int version = 0;
        version = android.os.Build.VERSION.SDK_INT;
        return version;
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
                Bundle extras = data.getExtras();
                imagen = (Bitmap) extras.get("data");
                imageView.setImageBitmap(imagen);
            }
        }
    }
}

