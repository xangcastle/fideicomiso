package com.fideicomiso.banpro.fideicomiso;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.Log;

/**
 * Created by root on 10/01/18.
 */

public class Sincronizacion implements SincronizacionVideos.ListenerSincronizacionImagenes{
    Context context;
    public void sincronizacionVideo(Context context, String ruta, String longitud, String latitud, String fecha, String usuario , String punto ,String _id )
    {
        this.context = context;
        SincronizacionVideos sincronizacionImagenes = new SincronizacionVideos(context,ruta,  longitud,  latitud,  fecha,  usuario , punto,_id, this);
        sincronizacionImagenes.execute();
    }
    @Override
    public void enSincronizacionFinalizada(int codigo, String id_punto) {
      int cod= codigo;
        Conexion conexion = new Conexion(this.context , "Delta", null, 3);
        long respuesta =  conexion.deleteRegistration("registros", " punto = "+id_punto);
             respuesta =  conexion.deleteRegistration("puntos", "id = "+id_punto);
    }
}
