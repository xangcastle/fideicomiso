package com.fideicomiso.banpro.fideicomiso;

import android.Manifest;
import android.annotation.TargetApi;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;
import com.pkmmte.view.CircularImageView;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class VisitaActivity extends AppCompatActivity  implements MediaPlayer.OnCompletionListener {
    private ProgressDialog pDialog;
    private SessionManager session;
    private ImageView imageView;
    private ImageView imageView2;
    private Bitmap imagen;
    private Bitmap imagen2;
    int cara = 0 ;
    MediaRecorder recorder;
    MediaPlayer player;
    File archivo;
    ImageButton grabar, pausar, detener , reproducir,resume,delete;
    TextView estado_grabacion;
    ArrayList dataFiles ;
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

        grabar     = (ImageButton) findViewById(R.id.record);
        reproducir = (ImageButton) findViewById(R.id.play);
        detener    = (ImageButton) findViewById(R.id.stop);
        pausar     = (ImageButton) findViewById(R.id.pause);
        resume     = (ImageButton)findViewById(R.id.resume);
        delete     = (ImageButton)findViewById(R.id.resume);
        dataFiles  = new ArrayList<String>();
        estado_grabacion = (TextView) findViewById(R.id.estado_grabacion);
        Bundle extras = getIntent().getExtras();

        if(extras != null)
            idPunto = extras.getString("ID");

        detener.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                detener();

            }
        });

        grabar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
              grabar();
            }
        });

        pausar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                detener();
            }
        });

        reproducir.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                reproducir();
            }
        });

        resume.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                reanudar();
            }
        });

        delete.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {
                eliminar();
            }
        });


        Button btnRegister = (Button) findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            public void onClick(View view) {
                android.app.AlertDialog.Builder builder = new android.app.AlertDialog.Builder(view.getContext());

                builder
                        .setMessage("Desea continuar para grabar video ?")
                        .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                EditText comentario = (EditText) findViewById(R.id.comment_visita);



                                String path = Environment.getExternalStorageDirectory().toString()+"/fideicomizo/";
                                OutputStream fOut = null;
                                long time = System.currentTimeMillis();
                                File f = new File(path);
                                if (!f.exists()){
                                    f.mkdirs();
                                }
                                File file = new File(path , "fideicomizo" + time + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
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



                                File file2 = new File(path , "fideicomizo2" + time + ".jpg"); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
                                try {
                                    fOut = new FileOutputStream(file2);
                                    imagen2.compress(Bitmap.CompressFormat.JPEG, 85, fOut); // saving the Bitmap to a file compressed as a JPEG with 85% compression rate
                                    fOut.flush(); // Not really required
                                    fOut.close(); // do not forget to close the stream
                                    MediaStore.Images.Media.insertImage(getContentResolver(), file2.getAbsolutePath(), file2.getName(), file2.getName());

                                } catch (FileNotFoundException e) {
                                    e.printStackTrace();
                                } catch (IOException e) {
                                    e.printStackTrace();
                                }



                                Bundle extras = getIntent().getExtras();
                                String id__Punto ="";
                                if(extras != null)
                                    id__Punto = extras.getString("ID");

                               /* Intent intent = new Intent( getApplicationContext(),Camera_view.class);
                                intent.putExtra("cedula",path+"fideicomizo"+time+".jpg");
                                intent.putExtra("cedula2",path+"fideicomizo2"+time+".jpg");
                                intent.putExtra("comentario",comentario.getText().toString());
                                intent.putExtra("ID",id__Punto);

                                startActivity(intent);*/



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
                cara = 1 ;
                Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
                if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
                    startActivityForResult(takePictureIntent, 1);
                }
            }

        });

        //======== codigo nuevo ========
        Button boton2 = (Button) findViewById(R.id.btnTomaFoto2);

        boton2.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                cara = 2 ;
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
        if (requestCode == 1||requestCode==100 ) {
            if (resultCode == RESULT_OK) {
                Bundle extras = data.getExtras();
                if (cara == 1 )
                {
                    imagen = (Bitmap) extras.get("data");
                    imageView.setImageBitmap(imagen);
                }
                else if (cara == 2 )
                {
                    imagen2 = (Bitmap) extras.get("data");
                    imageView2.setImageBitmap(imagen2);
                }

            }
        }
    }

    @Override
    public void onCompletion(MediaPlayer mp) {
        grabar.setEnabled(true);
        detener.setEnabled(true);
        reproducir.setEnabled(true);
    }


    public void grabar() {
        recorder = new MediaRecorder();
        recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
        recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
        recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
        File path = new File(Environment.getExternalStorageDirectory()
                .getPath()+"fideicomiso/");

        if (!path.exists()){
            path.mkdirs();
        }
        try {
            archivo = File.createTempFile("temporal"+System.currentTimeMillis(), ".mp4", path);
            dataFiles.add(path+"temporal"+System.currentTimeMillis()+".mp4");
        } catch (IOException e) {
        }
        recorder.setOutputFile(archivo.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IOException e) {
        }
        recorder.start();
        estado_grabacion.setText("Grabando");

        grabar.setEnabled(false);
        grabar.setVisibility(View.INVISIBLE);

        pausar.setEnabled(true);
        pausar.setVisibility(View.VISIBLE);

        detener.setEnabled(true);
        detener.setVisibility(View.VISIBLE);

        resume.setEnabled(true);
        resume.setVisibility(View.VISIBLE);
    }

    public void detener() {
        recorder.stop();
        recorder.release();
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        try {
            player.setDataSource(archivo.getAbsolutePath());
        } catch (IOException e) {
        }
        try {
            player.prepare();
        } catch (IOException e) {
        }


        reproducir.setEnabled(true);
        reproducir.setVisibility(View.VISIBLE);

        grabar.setEnabled(false);
        grabar.setVisibility(View.INVISIBLE);

        pausar.setEnabled(false);
        pausar.setVisibility(View.INVISIBLE);

        detener.setEnabled(false);
        detener.setVisibility(View.INVISIBLE);

        delete.setEnabled(true);
        delete.setVisibility(View.VISIBLE);

        resume.setEnabled(false);
        resume.setVisibility(View.INVISIBLE);



        mergeMediaFiles(true ,dataFiles,Environment.getExternalStorageDirectory()
                .getPath()+"fideicomiso/"+idPunto+System.currentTimeMillis()+".mp4");
    }

    public void reproducir() {
        player.start();
        grabar.setEnabled(false);
        detener.setEnabled(false);
        reproducir.setEnabled(false);
    }

    public void reanudar() {
        grabar();
    }

    public void eliminar() {
        recorder.stop();
        recorder.release();
        for (Object row : dataFiles) {
            File file = new File(row.toString());
            if(file.exists())
            {
                boolean deleted = file.delete();
            }
        }
        grabar.setEnabled(true);
        grabar.setVisibility(View.VISIBLE);

        reproducir.setEnabled(false);
        reproducir.setVisibility(View.INVISIBLE);

        grabar.setEnabled(false);
        grabar.setVisibility(View.INVISIBLE);

        pausar.setEnabled(false);
        pausar.setVisibility(View.INVISIBLE);

        detener.setEnabled(false);
        detener.setVisibility(View.INVISIBLE);

        delete.setEnabled(false);
        delete.setVisibility(View.INVISIBLE);

        resume.setEnabled(false);
        resume.setVisibility(View.INVISIBLE);
    }


    public static boolean mergeMediaFiles(boolean isAudio, ArrayList sourceFiles, String targetFile) {
        try {
            String mediaKey = isAudio ? "soun" : "vide";
            List<Movie> listMovies = new ArrayList<>();
            for (Object row : sourceFiles) {
                listMovies.add(MovieCreator.build(row.toString()));
            }
            List<Track> listTracks = new LinkedList<>();
            for (Movie movie : listMovies) {
                for (Track track : movie.getTracks()) {
                    if (track.getHandler().equals(mediaKey)) {
                        listTracks.add(track);
                    }
                }
            }
            Movie outputMovie = new Movie();
            if (!listTracks.isEmpty()) {
                outputMovie.addTrack(new AppendTrack(listTracks.toArray(new Track[listTracks.size()])));
            }
            Container container = new DefaultMp4Builder().build(outputMovie);
            FileChannel fileChannel = new RandomAccessFile(String.format(targetFile), "rw").getChannel();
            container.writeContainer(fileChannel);
            fileChannel.close();
            return true;
        }
        catch (IOException e) {
            return false;
        }
    }
}





