package com.fideicomiso.banpro.fideicomiso.View;

import android.app.AlertDialog;
import android.os.StatFs;
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
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageButton;
import android.widget.TextView;
import android.widget.Toast;

import com.coremedia.iso.boxes.Container;
import com.fideicomiso.banpro.fideicomiso.Clases.SessionManager;
import com.fideicomiso.banpro.fideicomiso.R;
import com.fideicomiso.banpro.fideicomiso.Sincronizar.EliminarMultimedia;
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
    private SessionManager session;
    private Button btn_iniciar_visita ;
    private Button btn_rechazar_visita ;
    private Button btn_no_estaba_visita ;
    private Boolean cargaPrevia = false;

    private String id__Punto;
    private String ruta ="";
    private File audioPrevio;
    private String ruta_previa ="";


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_grabar_audio);

        session = new SessionManager(getApplicationContext());
        if (!session.isLoggedIn())
            logoutUser();






        grabar     = (ImageButton) findViewById(R.id.record);
        reproducir = (ImageButton) findViewById(R.id.play);
        detener    = (ImageButton) findViewById(R.id.stop);
        pausar     = (ImageButton) findViewById(R.id.pause);
        resume     = (ImageButton)findViewById(R.id.resume);
        delete     = (ImageButton)findViewById(R.id.cancel);
        dataFiles  = new ArrayList<String>();
        estado_grabacion = (TextView) findViewById(R.id.estado_grabacion);


        reproducir.setEnabled(false);
        pausar.setEnabled(false);
        detener.setEnabled(false);
        delete.setEnabled(false);
        resume.setEnabled(false);

        Bundle extras = getIntent().getExtras();
        if(extras != null){
            id__Punto = extras.getString("ID");
            ruta_previa = extras.getString("ruta");
            cargarAudioPrevio(ruta_previa);
        }

        detener.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder
                        .setMessage("Desea detener la grabación?")
                        .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                try
                                {
                                    estado_grabacion.setText("Grabación Finalizada");
                                    reproducir.setEnabled(true);
                                    reproducir.setImageResource(R.drawable.play);
                                    grabar.setEnabled(false);
                                    grabar.setImageResource(R.drawable.record2);
                                    pausar.setEnabled(false);
                                    pausar.setImageResource(R.drawable.pause2);
                                    detener.setEnabled(false);
                                    detener.setImageResource(R.drawable.stop2);
                                    delete.setEnabled(true);
                                    delete.setImageResource(R.drawable.cancel);
                                    resume.setEnabled(false);
                                    detener("");
                                    String p = Environment.getExternalStorageDirectory()
                                            .getPath()+"/fideicomiso/"+id__Punto+"_"+System.currentTimeMillis()+".mp4";
                                    Boolean armarAudio = mergeMediaFiles(true ,dataFiles,p);
                                    ruta = p;
                                    if(armarAudio)
                                    {
                                        reproducirAudioFinal(p);
                                    }
                                }catch (Exception e)
                                {

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
                try
                {
                    estado_grabacion.setText("Pausar");
                    reproducir.setEnabled(false);
                    reproducir.setImageResource(R.drawable.play2);
                    grabar.setEnabled(false);
                    grabar.setImageResource(R.drawable.record2);
                    pausar.setEnabled(false);
                    pausar.setImageResource(R.drawable.pause2);
                    detener.setEnabled(false);
                    detener.setImageResource(R.drawable.stop2);
                    delete.setEnabled(false);
                    delete.setImageResource(R.drawable.cancel2);
                    resume.setEnabled(true);
                    resume.setImageResource(R.drawable.resume);
                    detener("");
                }catch (Exception e)
                {

                }
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
                eliminar(v);
            }
        });


        btn_no_estaba_visita= (Button)findViewById(R.id.noEstabaVicita);
        btn_no_estaba_visita.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                if(!ruta.equals(""))
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    builder
                            .setMessage("No se pudo realizar visita ?")
                            .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    Intent intent = new Intent(getApplicationContext(), NoVisitaActivity.class);
                                    intent.putExtra("ID",id__Punto);
                                    intent.putExtra("ruta",ruta);
                                    intent.putExtra("rechazo",false);
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
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    builder
                            .setMessage("Debe grabar audio para continuar?")
                            .setNegativeButton("Entiendo", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }
            }
        });

        btn_rechazar_visita  = (Button)findViewById(R.id.rechazarVicita);
        btn_rechazar_visita.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){

                if(!ruta.equals(""))
                {

                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    builder
                            .setMessage("No abrir Cuenta ?")
                            .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {
                                    EliminarMultimedia eli = new EliminarMultimedia(getApplicationContext());
                                    eli.execute();
                                    Intent intent = new Intent(getApplicationContext(), NoVisitaActivity.class);
                                    intent.putExtra("ID",id__Punto);
                                    intent.putExtra("ruta",ruta);
                                    intent.putExtra("rechazo",true);
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
                else
                    {
                        AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                        builder
                                .setMessage("Debe grabar audio para continuar?")
                                .setNegativeButton("Entiendo", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog,int id) {
                                        dialog.cancel();
                                    }
                                })
                                .show();
                    }
            }
        });


        btn_iniciar_visita = (Button)findViewById(R.id.iniciarVicita);
        btn_iniciar_visita.setOnClickListener(new View.OnClickListener(){
            public void onClick(View view){


                if(!ruta.equals(""))
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    builder
                            .setMessage("Abrir Cuenta ?")
                            .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int id) {

                                    EliminarMultimedia eli = new EliminarMultimedia(getApplicationContext());
                                    eli.execute();
                                    Intent intent = new Intent(getApplicationContext(), VisitaActivity.class);
                                    intent.putExtra("ID",id__Punto);
                                    intent.putExtra("ruta",ruta);
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
                else
                {
                    AlertDialog.Builder builder = new AlertDialog.Builder(view.getContext());

                    builder
                            .setMessage("Debe grabar audio para continuar")
                            .setNegativeButton("Entiendo", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog,int id) {
                                    dialog.cancel();
                                }
                            })
                            .show();
                }



            }
        });

        StatFs stat = new StatFs(Environment.getExternalStorageDirectory().getPath());
        long bytesAvailable = (long)stat.getBlockSize() * (long)stat.getAvailableBlocks();
        long megAvailable = bytesAvailable / (1024 * 1024);
        if(megAvailable <15)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

            builder
                    .setMessage("Memoria llena")
                    .setNegativeButton("Entiendo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }

        try {
            if (ruta_previa == null) {
                File f = new File(Environment.getExternalStorageDirectory()
                        .getPath() + "/fideicomiso");

                ArrayList<File> archivos = archivosCompatibles(f.listFiles(), id__Punto + "_");

                if (archivos.size() > 0) {
                    audioPrevio = archivos.get(archivos.size() - 1);
                    if (audioPrevio.exists()) {
                        AlertDialog.Builder builder = new AlertDialog.Builder(GrabarAudioActivity.this);

                        builder
                                .setMessage("Existe un Audio Previamente grabado para la Visita " + id__Punto + " Desea usarlo ? .Si es una nueva visita al mismo punto no lo debe usar ")
                                .setPositiveButton("Si Usar", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int id) {
                                        cargarAudioPrevio(audioPrevio.getAbsolutePath());
                                        cargaPrevia = true;
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
        }
        catch (Exception e)
        {
            
        }


    }
    public ArrayList<File> archivosCompatibles(File[] archivos,String name) {
        ArrayList<File> archivosC = new ArrayList<File>();
        for (File f : archivos)
            if (f.getName().toLowerCase().startsWith(name)) {
                archivosC.add(f);
            }

        return archivosC;
    }

    public void cargarAudioPrevio(String r)
    {
        if(r!= null && !r.equals(""))
        {
            estado_grabacion.setText("Grabación Finalizada");
            reproducir.setEnabled(true);
            reproducir.setImageResource(R.drawable.play);
            grabar.setEnabled(false);
            grabar.setImageResource(R.drawable.record2);
            pausar.setEnabled(false);
            pausar.setImageResource(R.drawable.pause2);
            detener.setEnabled(false);
            detener.setImageResource(R.drawable.stop2);
            delete.setEnabled(true);
            delete.setImageResource(R.drawable.cancel);
            resume.setEnabled(false);
            this.ruta = r ;
            player = new MediaPlayer();
            player.setOnCompletionListener(this);
            try {
                if(r != "")
                    player.setDataSource(r);
                else
                    player.setDataSource(archivo.getAbsolutePath());
            } catch (IOException e) {
            }
            try {
                player.prepare();
            } catch (IOException e) {
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
        grabar.setEnabled(false);
        grabar.setImageResource(R.drawable.record2);
        reproducir.setEnabled(true);
        reproducir.setImageResource(R.drawable.play);
        pausar.setEnabled(false);
        pausar.setImageResource(R.drawable.pause2);
        detener.setEnabled(false);
        detener.setImageResource(R.drawable.stop2);
        delete.setEnabled(true);
        delete.setImageResource(R.drawable.cancel);
        resume.setEnabled(false);
        resume.setImageResource(R.drawable.resume2);
    }


    public void grabar() {

        try {
            recorder = new MediaRecorder();
            recorder.setAudioSource(MediaRecorder.AudioSource.MIC);
            recorder.setOutputFormat(MediaRecorder.OutputFormat.THREE_GPP);
            recorder.setAudioEncoder(MediaRecorder.AudioEncoder.AMR_NB);
            File path = new File(Environment.getExternalStorageDirectory()
                    .getPath() + "/fideicomiso");

            if (!path.exists()) {
                path.mkdirs();
            }

            String name = "temporal" + System.currentTimeMillis() + ".mp4";
            archivo = new File(path, name); // the File to save , append increasing numeric counter to prevent files from getting overwritten.
            try {
                FileOutputStream fOut = new FileOutputStream(archivo);
                fOut.flush(); // Not really required
                fOut.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            dataFiles.add(path + "/" + name);
            recorder.setOutputFile(archivo.getAbsolutePath());
            try {
                recorder.prepare();
            } catch (IOException e) {
                String l = e.getMessage();
            }
            recorder.start();
            estado_grabacion.setText("Grabando...");

            grabar.setEnabled(false);
            grabar.setImageResource(R.drawable.record2);
            pausar.setEnabled(true);
            pausar.setImageResource(R.drawable.pause);
            detener.setEnabled(true);
            detener.setImageResource(R.drawable.stop);
            resume.setEnabled(false);
            resume.setImageResource(R.drawable.resume2);
        }catch (Exception e)
        {
            AlertDialog.Builder builder = new AlertDialog.Builder(getApplicationContext());

            builder
                    .setMessage("Memoria insuficiente , verifique que tenga espacio para poder grabar")

                    .setNegativeButton("Entiendo", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog,int id) {
                            dialog.cancel();
                        }
                    })
                    .show();
        }


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
    public void reproducirAudioFinal(String path )
    {
        player = new MediaPlayer();
        player.setOnCompletionListener(this);
        try {
                player.setDataSource(path);

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
        grabar.setImageResource(R.drawable.record2);
        reproducir.setEnabled(false);
        reproducir.setImageResource(R.drawable.play2);
        pausar.setEnabled(false);
        pausar.setImageResource(R.drawable.pause2);
        detener.setEnabled(false);
        detener.setImageResource(R.drawable.stop2);
        delete.setEnabled(false);
        delete.setImageResource(R.drawable.cancel2);
        resume.setEnabled(false);
        resume.setImageResource(R.drawable.resume2);
    }

    public void reanudar() {
        grabar();
    }

    public void eliminar(View v) {

        AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

        builder
                .setMessage("Desea eliminar la grabación ? ")
                .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        estado_grabacion.setText("");
                        for (Object row : dataFiles) {
                            File file = new File(row.toString());
                            if(file.exists())
                            {
                                boolean deleted = file.delete();
                            }
                        }
                        if(!ruta.equals(""))
                        {
                            File file = new File(ruta);
                            if(file.exists() && !cargaPrevia)
                            {
                                boolean deleted = file.delete();
                            }
                        }
                        dataFiles  = new ArrayList<String>();
                        ruta ="";
                        grabar.setEnabled(true);
                        grabar.setImageResource(R.drawable.record);
                        reproducir.setEnabled(false);
                        reproducir.setImageResource(R.drawable.play2);
                        pausar.setEnabled(false);
                        pausar.setImageResource(R.drawable.pause2);
                        detener.setEnabled(false);
                        detener.setImageResource(R.drawable.stop2);
                        delete.setEnabled(false);
                        delete.setImageResource(R.drawable.cancel2);
                        resume.setEnabled(false);
                        resume.setImageResource(R.drawable.resume2);

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
