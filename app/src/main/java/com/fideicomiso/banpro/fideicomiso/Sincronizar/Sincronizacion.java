package com.fideicomiso.banpro.fideicomiso.Sincronizar;

import android.content.Context;

import com.fideicomiso.banpro.fideicomiso.Clases.Conexion;

/**
 * Created by root on 10/01/18.
 */

public class Sincronizacion implements SincronizacionVideos.ListenerSincronizacionImagenes{
    Context context;
    public void sincronizacionVideo(Context context, String ruta, String longitud, String latitud, String fecha, String usuario , String punto ,String _id,String _tipo ,String _comentario,String _ruta_imagen_cedula,String _ruta_imagen_casa,String _ruta_cedula2 ,String ncedula ,String nombre)
    {
        this.context = context;
        SincronizacionVideos sincronizacionImagenes = new SincronizacionVideos(context,ruta,  longitud,  latitud,  fecha,  usuario , punto,_id,_tipo,_comentario,_ruta_imagen_cedula,_ruta_imagen_casa, _ruta_cedula2,ncedula,nombre,this);
        sincronizacionImagenes.execute();
    }
    @Override
    public void enSincronizacionFinalizada(int codigo, String id_punto) {


    }
}
