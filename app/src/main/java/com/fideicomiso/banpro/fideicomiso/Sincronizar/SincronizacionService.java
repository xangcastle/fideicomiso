package com.fideicomiso.banpro.fideicomiso.Sincronizar;

import android.app.Service;
import android.content.Intent;
import android.os.*;
import android.os.Process;
import android.widget.Toast;

import com.fideicomiso.banpro.fideicomiso.Clases.Conexion;

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

        String[] datos = new String[14];
        datos[0] = "longitud";
        datos[1] = "latitud";
        datos[2] = "fecha";
        datos[3] = "ruta";
        datos[4] = "punto";
        datos[5] = "usuario";
        datos[6] = "_id";
        datos[7] = "tipo";
        datos[8] = "comentario";
        datos[9] = "cedula";
        datos[10] = "casa";
        datos[11] = "cedula2";
        datos[12] = "ncedula";
        datos[13] = "nombre";


        Conexion conexion = new Conexion(getApplicationContext(), "Delta3", null, 3);
        ArrayList puntos =  conexion.searchRegistration("registros", datos," estado = 1 or  estado = 2  ", null, " DESC");
        TareaSincronizar obj = new TareaSincronizar(puntos);
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
          //  stopSelf(msg.arg1);
        }
    }

    public class TareaSincronizar implements Runnable {
        private  ArrayList datos;
        private  String fecha;
        private  Conexion conexion;
        public TareaSincronizar(ArrayList _data) {
            this.datos = _data;
            this.conexion  = new Conexion(getApplicationContext(), "Delta3", null, 3);
        }


        @Override
        public void run() {
            try{
                for (int i = 0; i < datos.size(); i++) {
                    HashMap codDoc = (HashMap) datos.get(i);
                    Sincronizacion sincronizacion = new Sincronizacion();
                    sincronizacion.sincronizacionVideo(getApplicationContext(),
                            ((codDoc.get("ruta").toString()=="")?null:codDoc.get("ruta").toString()),
                            codDoc.get("longitud").toString(),
                            codDoc.get("latitud").toString(),
                            codDoc.get("fecha").toString(),
                            codDoc.get("usuario").toString(),
                            codDoc.get("punto").toString(),
                            codDoc.get("_id").toString(),
                            codDoc.get("tipo").toString(),
                            codDoc.get("comentario").toString(),
                            ((codDoc.get("cedula").toString()=="")?null:codDoc.get("cedula").toString()),
                            ((codDoc.get("casa").toString()=="")?null:codDoc.get("casa").toString()),
                            ((codDoc.get("cedula2").toString()=="")?null:codDoc.get("cedula2").toString()),
                            codDoc.get("ncedula").toString(),
                            codDoc.get("nombre").toString()
                    );
                    Toast.makeText(getApplicationContext(), "Sincronización en proceso punto :"+codDoc.get("punto").toString(), Toast.LENGTH_SHORT).show();
                    String[][] datos = new String[1][2];
                    datos[0][0] = "estado";
                    datos[0][1] = "3";
                    long respuesta =  conexion.update("registros",datos, " id =  "+ codDoc.get("punto").toString());
                }
            }catch ( Exception e) {
                e.printStackTrace();
            }
        }
    }
}
