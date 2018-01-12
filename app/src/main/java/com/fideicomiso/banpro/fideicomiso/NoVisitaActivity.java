package com.fideicomiso.banpro.fideicomiso;

import android.Manifest;
import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v4.app.ActivityCompat;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.pkmmte.view.CircularImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Locale;

public class NoVisitaActivity extends AppCompatActivity
        implements View.OnClickListener{

    private Uri mImageCaptureUri;
    private ImageView mImageView;
    private static final int PICK_FROM_CAMERA = 1;
    private static final int CROP_FROM_CAMERA = 2;
    private static final int PICK_FROM_FILE = 3;

    private ArrayList<String> imagesPathList;
    private Bitmap yourbitmap;
    private final int PICK_IMAGE_MULTIPLE =1;
    private Boolean btn_cam = Boolean.FALSE ;
    private ProgressDialog pDialog;
    private SessionManager session;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_no_visita);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn())
            logoutUser();

        pDialog = new ProgressDialog(this);
        pDialog.setCancelable(false);

        if(versionDispositivo()>=23)
        {
            if (ActivityCompat.shouldShowRequestPermissionRationale(this,
                    Manifest.permission.READ_EXTERNAL_STORAGE))
            {
                ActivityCompat.requestPermissions(this,
                        new String[]{Manifest.permission.VIBRATE, Manifest.permission.RECEIVE_BOOT_COMPLETED,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
            } else
            {
                ActivityCompat.requestPermissions(this, new String[]{Manifest.permission.VIBRATE, Manifest.permission.RECEIVE_BOOT_COMPLETED,Manifest.permission.ACCESS_NETWORK_STATE,Manifest.permission.INTERNET,Manifest.permission.READ_EXTERNAL_STORAGE,Manifest.permission.CAMERA,Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        0);
            }
        }
        final String [] items			= new String [] {getString(R.string.capture),getString(R.string.picture)};
        ArrayAdapter<String> adapter	= new ArrayAdapter<String> (this, android.R.layout.select_dialog_item,items);
        AlertDialog.Builder builder		= new AlertDialog.Builder(this);
        builder.setTitle(R.string.btn_picture_product);
        builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
            public void onClick( DialogInterface dialog, int item ) { //pick from camera
                btn_cam = Boolean.FALSE;
                if (item == 0) {
                    Intent intent 	 = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);

                    mImageCaptureUri = Uri.fromFile(new File(Environment.getExternalStorageDirectory(),
                            "tmp_avatar_" + String.valueOf(System.currentTimeMillis()) + ".jpg"));

                    intent.putExtra(android.provider.MediaStore.EXTRA_OUTPUT, mImageCaptureUri);

                    try {
                        intent.putExtra("return-data", true);

                        startActivityForResult(intent, PICK_FROM_CAMERA);
                    } catch (ActivityNotFoundException e) {
                        e.printStackTrace();
                    }
                } else { //pick from file
                    Intent intent = new Intent();

                    intent.setType("image/*");
                    intent.setAction(Intent.ACTION_GET_CONTENT);

                    startActivityForResult(Intent.createChooser(intent, "Complete action using"), PICK_FROM_FILE);
                }
            }
        } );

        final AlertDialog dialog = builder.create();


        mImageView		= (ImageView) findViewById(R.id.iv_photo);

        mImageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.show();
            }
        });
        Button btnRegister= (Button)findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(getApplicationContext());

                builder
                        .setMessage("Desea terminar esta visita ?")
                        .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText comentario        = (EditText) findViewById(R.id.comment_visita);
                                CircularImageView perfil     = (CircularImageView) findViewById(R.id.iv_photo);
                                Bitmap bitmap = ((BitmapDrawable)perfil.getDrawable()).getBitmap();

                                String path = Environment.getExternalStorageDirectory().toString();
                                OutputStream fOut = null;
                                long time= System.currentTimeMillis();
                                File file = new File(path+"/fideicomizo", "Fideicomizo"+time+".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                                try {
                                    fOut = new FileOutputStream(file);
                                    bitmap.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                                    fOut.flush(); // Not really required
                                    fOut.close(); // do not forget to close the stream
                                    MediaStore.Images.Media.insertImage(getContentResolver(),file.getAbsolutePath(),file.getName(),file.getName());

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }


                                GPSTracker gps = new GPSTracker(getApplicationContext());

                                // check if GPS enabled
                                if(gps.canGetLocation()){

                                    double latitude = gps.getLatitude();
                                    double longitude = gps.getLongitude();
                                    SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
                                    Date date = new Date();
                                    String fecha = dateFormat.format(date);
                                    Bundle extras = getIntent().getExtras();
                                    String id__Punto ="";
                                    if(extras != null) {
                                        id__Punto = extras.getString("ID");

                                    }
                                    SessionManager session = new SessionManager(getApplicationContext());
                                    String[][] data = new String[11][2];
                                    data[0][0] = "longitud";
                                    data[0][1] = ""+longitude;
                                    data[1][0] = "latitud";
                                    data[1][1] = ""+latitude;
                                    data[2][0] = "fecha";
                                    data[2][1] = fecha;
                                    data[3][0] = "ruta";
                                    data[3][1] = "";
                                    data[4][0] = "punto";
                                    data[4][1] =  id__Punto;
                                    data[5][0] = "usuario";
                                    data[5][1] = ""+session.get_user();
                                    data[6][0] = "cedula";
                                    data[6][1] = "";
                                    data[7][0] = "casa";
                                    data[7][1] =  path+"/fideicomizo/Fideicomizo"+time+".jpg";
                                    data[8][0] = "tipo";
                                    data[8][1] = "0";
                                    data[9][0] = "comentario";
                                    data[9][1] = comentario.getText().toString();
                                    data[10][0] = "estado";
                                    data[10][1] ="1";

                                    Conexion conexion = new Conexion(getApplicationContext(), "Delta", null, 3);
                                    long respuesta = conexion.insertRegistration("registros", data);
                                    Intent intent = new Intent( getApplicationContext(),Dashboard.class);
                                    startActivity(intent);

                                }else{
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
    }


    private File savebitmap(String filename) {
        String extStorageDirectory = Environment.getExternalStorageDirectory().toString();
        OutputStream outStream = null;

        File file = new File(filename + ".png");
        if (file.exists()) {
            file.delete();
            file = new File(extStorageDirectory, filename + ".png");
            Log.e("file exist", "" + file + ",Bitmap= " + filename);
        }
        try {
            // make a new bitmap from your file
            Bitmap bitmap = BitmapFactory.decodeFile(file.getName());

            outStream = new FileOutputStream(file);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, outStream);
            outStream.flush();
            outStream.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        Log.e("file", "" + file);
        return file;

    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode != RESULT_OK) return;

        if(requestCode == PICK_IMAGE_MULTIPLE && btn_cam)
        {
            imagesPathList = new ArrayList<String>();
            String[] imagesPath = data.getStringExtra("data").split("\\|");

            for (int i=0;i<imagesPath.length;i++){
                imagesPathList.add(imagesPath[i]);
                yourbitmap = BitmapFactory.decodeFile(imagesPath[i]);

                CircularImageView circularImageView = new CircularImageView(this);
                circularImageView.setBorderColor(getResources().getColor(R.color.btn_register));

                circularImageView.setBorderWidth(10);
                circularImageView.setImageBitmap(yourbitmap);
                circularImageView.setSelectorColor(getResources().getColor(R.color.btn_register));
                circularImageView.setSelectorStrokeColor(getResources().getColor(R.color.btn_register));
                circularImageView.setSelectorStrokeWidth(10);
                circularImageView.addShadow();
            }
        }
        else
        {
            switch (requestCode) {
                case PICK_FROM_CAMERA:
                    doCrop();

                    break;

                case PICK_FROM_FILE:
                    mImageCaptureUri = data.getData();

                    doCrop();

                    break;

                case CROP_FROM_CAMERA:
                    Bundle extras = data.getExtras();

                    if (extras != null) {
                        Bitmap photo = extras.getParcelable("data");

                        mImageView.setImageBitmap(photo);
                    }

                    File f = new File(mImageCaptureUri.getPath());

                    if (f.exists()) f.delete();

                    break;

            }
        }

    }

    private void doCrop() {
        final ArrayList<CropOption> cropOptions = new ArrayList<CropOption>();

        Intent intent = new Intent("com.android.camera.action.CROP");
        intent.setType("image/*");

        List<ResolveInfo> list = getPackageManager().queryIntentActivities( intent, 0 );

        int size = list.size();

        if (size == 0) {
            Toast.makeText(this, "Can not find image crop app", Toast.LENGTH_SHORT).show();

            return;
        } else {
            intent.setData(mImageCaptureUri);

            intent.putExtra("outputX", 200);
            intent.putExtra("outputY", 200);
            intent.putExtra("aspectX", 1);
            intent.putExtra("aspectY", 1);
            intent.putExtra("scale", true);
            intent.putExtra("return-data", true);

            if (size == 1) {
                Intent i 		= new Intent(intent);
                ResolveInfo res	= list.get(0);

                i.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                startActivityForResult(i, CROP_FROM_CAMERA);
            } else {
                for (ResolveInfo res : list) {
                    final CropOption co = new CropOption();

                    co.title 	= getPackageManager().getApplicationLabel(res.activityInfo.applicationInfo);
                    co.icon		= getPackageManager().getApplicationIcon(res.activityInfo.applicationInfo);
                    co.appIntent= new Intent(intent);

                    co.appIntent.setComponent( new ComponentName(res.activityInfo.packageName, res.activityInfo.name));

                    cropOptions.add(co);
                }

                CropOptionAdapter adapter = new CropOptionAdapter(getApplicationContext(), cropOptions);

                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(this);
                builder.setTitle("Choose Crop App");
                builder.setAdapter( adapter, new DialogInterface.OnClickListener() {
                    public void onClick( DialogInterface dialog, int item ) {
                        startActivityForResult( cropOptions.get(item).appIntent, CROP_FROM_CAMERA);
                    }
                });

                builder.setOnCancelListener( new DialogInterface.OnCancelListener() {
                    @Override
                    public void onCancel( DialogInterface dialog ) {

                        if (mImageCaptureUri != null ) {
                            getContentResolver().delete(mImageCaptureUri, null, null );
                            mImageCaptureUri = null;
                        }
                    }
                } );

                android.app.AlertDialog alert = builder.create();

                alert.show();
            }
        }
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





    public int versionDispositivo()
    {
        int version = 0;
        version = android.os.Build.VERSION.SDK_INT;
        return version;
    }

    private void logoutUser() {
        session.setLogin(false,0);

        // Launching the login activity
        Intent intent = new Intent(NoVisitaActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public void onClick(View v) {

    }
}