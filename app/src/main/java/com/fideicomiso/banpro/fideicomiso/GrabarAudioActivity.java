package com.fideicomiso.banpro.fideicomiso;

import android.app.AlertDialog;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.media.MediaPlayer;
import android.media.MediaRecorder;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.googlecode.mp4parser.authoring.Movie;
import com.googlecode.mp4parser.authoring.Track;
import com.googlecode.mp4parser.authoring.builder.DefaultMp4Builder;
import com.googlecode.mp4parser.authoring.container.mp4.MovieCreator;
import com.googlecode.mp4parser.authoring.tracks.AppendTrack;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.nio.channels.FileChannel;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;


public class GrabarAudioActivity extends AppCompatActivity implements MediaPlayer.OnCompletionListener {
    MediaRecorder recorder;
    MediaPlayer player;
    File archivo;
    ImageButton grabar, pausar, detener , reproducir,resume,delete;
    TextView estado_grabacion;
    ArrayList dataFiles ;
    String idPunto;
    private SessionManager session;
    private Button btn_iniciar_visita ;
    private Button btn_rechazar_visita ;
    private String id__Punto;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabar_audio);

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
        Bundle extras = getIntent().getExtras();
        if(extras != null)
            id__Punto = extras.getString("ID");


        grabar     = (ImageButton) findViewById(R.id.record);
        reproducir = (ImageButton) findViewById(R.id.play);
        detener    = (ImageButton) findViewById(R.id.stop);
        pausar     = (ImageButton) findViewById(R.id.pause);
        resume     = (ImageButton)findViewById(R.id.resume);
        delete     = (ImageButton)findViewById(R.id.cancel);
        dataFiles  = new ArrayList<String>();
        estado_grabacion = (TextView) findViewById(R.id.estado_grabacion);


        if(extras != null)
            idPunto = extras.getString("ID");

        detener.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

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
                String p = Environment.getExternalStorageDirectory()
                        .getPath()+"/fideicomiso/"+idPunto+System.currentTimeMillis()+".mp4";
                Boolean armarAudio = mergeMediaFiles(true ,dataFiles,p);

                if(armarAudio)
                 detener(p);


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

                resume.setEnabled(true);
                resume.setVisibility(View.VISIBLE);
                detener("");
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

        btn_rechazar_visita  = (Button)findViewById(R.id.rechazarVicita);
        btn_rechazar_visita.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){
                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder
                        .setMessage("No aceptación de entrevista?")
                        .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getApplicationContext(), NoVisitaActivity.class);
                                intent.putExtra("ID",id__Punto);
                                startActivity(intent);
                                finish();

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


        btn_iniciar_visita = (Button)findViewById(R.id.iniciarVicita);
        btn_iniciar_visita.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                builder
                        .setMessage("Aceptación de entrevista ?")
                        .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getApplicationContext(), VisitaActivity.class);
                                intent.putExtra("ID",id__Punto);
                                startActivity(intent);
                                finish();
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
        Intent intent = new Intent(GrabarAudioActivity.this, LoginActivity.class);
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
                    Toast.makeText(GrabarAudioActivity.this, "Permission is denied.", Toast.LENGTH_SHORT).show();
                    finish();
                }
                //return; // delete.
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
                .getPath()+"/fideicomiso");

        if (!path.exists()){
            path.mkdirs();
        }

        String name = "temporal"+System.currentTimeMillis()+".mp4";
        archivo = new File(path , name); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
        try {
            FileOutputStream  fOut = new FileOutputStream(archivo);
            fOut.flush(); // Not really required
            fOut.close();
        }
        catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
            dataFiles.add(path+"/"+name);
        recorder.setOutputFile(archivo.getAbsolutePath());
        try {
            recorder.prepare();
        } catch (IOException e)
        {
            String l = e.getMessage();
        }
        recorder.start();
        estado_grabacion.setText("Grabando");

        grabar.setEnabled(false);
        grabar.setVisibility(View.INVISIBLE);

        pausar.setEnabled(true);
        pausar.setVisibility(View.VISIBLE);

        detener.setEnabled(true);
        detener.setVisibility(View.VISIBLE);

        resume.setEnabled(false);
        resume.setVisibility(View.INVISIBLE);
    }

    public void detener(String path ) {
        recorder.stop();
        recorder.release();
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        try {
            if(path != "")
                player.setDataSource(path);
            else
                player.setDataSource(archivo.getAbsolutePath());
        } catch (IOException e) {
        }
        try {
            player.prepare();
        } catch (IOException e) {
        }


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
