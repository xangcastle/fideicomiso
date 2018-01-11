package com.fideicomiso.banpro.fideicomiso;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.widget.Toast;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;


public class SincronizacionService extends Service {

    private Looper mServiceLooper;
    private ServiceHandler mServiceHandler;

    @Override
    public void onCreate() {
        // To avoid cpu-blocking, we create a background handler to run our service
        HandlerThread thread = new HandlerThread("fideicomiso", Process.THREAD_PRIORITY_BACKGROUND);
        // start the new handler thread
        thread.start();

        mServiceLooper = thread.getLooper();
        // start the service using the background handler
        mServiceHandler = new ServiceHandler(mServiceLooper);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Toast.makeText(this, "onStartCommand", Toast.LENGTH_SHORT).show();

        // call a new service handler. The service ID can be used to identify the service
        Message message = mServiceHandler.obtainMessage();
        message.arg1 = startId;
        mServiceHandler.sendMessage(message);

        return START_STICKY;
    }

    protected void showToast(final String msg){
        //gets the main thread
        Handler handler = new Handler(Looper.getMainLooper());
        SimpleDateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd", Locale.getDefault());
        Date date = new Date();
        String fecha = dateFormat.format(date);

        String[] datos = new String[7];
        datos[0] = "longitud";
        datos[1] = "latitud";
        datos[2] = "fecha";
        datos[3] = "ruta";
        datos[4] = "punto";
        datos[5] = "usuario";
        datos[6] = "_id";

        Conexion conexion = new Conexion(getApplicationContext(), "Delta", null, 3);
        ArrayList puntos =  conexion.searchRegistration("registros", datos, null, null, " DESC");
        TareaSincronizar obj = new TareaSincronizar(puntos,fecha);
        handler.post(obj);
    }


    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    // Object responsible for
    private final class ServiceHandler extends Handler {

        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            // Well calling mServiceHandler.sendMessage(message); from onStartCommand,
            // this method will be called.

            // Add your cpu-blocking activity here
            try {
                Thread.sleep(5000);
            } catch (InterruptedException e) {
                Thread.currentThread().interrupt();
            }
            showToast("Finishing , id: " + msg.arg1);
            // the msg.arg1 is the startId used in the onStartCommand, so we can track the running sevice here.
            stopSelf(msg.arg1);
        }
    }

    public class TareaSincronizar implements Runnable {
        private  ArrayList datos;
        private  String fecha;
        public TareaSincronizar(ArrayList _data , String _fecha) {
            this.datos = _data;
            this.fecha = _fecha;
        }


        @Override
        public void run() {


            try{

                for (int i = 0; i < datos.size(); i++) {
                    HashMap codDoc = (HashMap) datos.get(i);
                    Sincronizacion sincronizacion = new Sincronizacion();
                    sincronizacion.sincronizacionVideo(getApplicationContext(),
                            codDoc.get("ruta").toString(),
                            codDoc.get("longitud").toString(),
                            codDoc.get("latitud").toString(),
                            codDoc.get("fecha").toString(),
                            codDoc.get("usuario").toString(),
                            codDoc.get("punto").toString(),
                            codDoc.get("_id").toString()
                            );
                }
            }catch ( Exception e) {
                e.printStackTrace();
            }


            Toast.makeText(getApplicationContext(), "Sincronización en proceso", Toast.LENGTH_SHORT).show();

        }
    }
}