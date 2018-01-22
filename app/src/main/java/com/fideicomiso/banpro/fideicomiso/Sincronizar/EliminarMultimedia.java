package com.fideicomiso.banpro.fideicomiso.Sincronizar;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Environment;

import com.fideicomiso.banpro.fideicomiso.Clases.Conexion;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * Created by root on 22/01/18.
 */

public class EliminarMultimedia extends AsyncTask<Void, Void, Integer> {
    public Context context ;
    public EliminarMultimedia(Context context) {
        this.context = context;
    }
    @Override
    protected Integer doInBackground(Void... voids) {
        try
        {
            String[] datos = new String[5];
            datos[0] = "cedula";
            datos[1] = "cedula2";
            datos[2] = "casa";
            datos[3] = "ruta";
            datos[4] = "id";
            Conexion conexion = new Conexion(this.context, "Delta3", null, 3);
            ArrayList puntos =  conexion.searchRegistration("registros", datos," estado = 3  ", null, " DESC");
            File f = new File(Environment.getExternalStorageDirectory()
                    .getPath() + "/fideicomiso");
            for (Object data : puntos) {
                HashMap codDoc = (HashMap) data;
                File ruta = new File(codDoc.get("ruta").toString());
                File casa = new File(codDoc.get("casa").toString());
                File cedula = new File(codDoc.get("cedula").toString());
                File cedula2 = new File(codDoc.get("cedula2").toString());

                if(ruta.exists())
                {
                    boolean deleted = ruta.delete();
                }
                if(casa.exists())
                {
                    boolean deleted = casa.delete();
                }
                if(cedula.exists())
                {
                    boolean deleted = casa.delete();
                }
                if(cedula2.exists())
                {
                    boolean deleted = casa.delete();
                }

                ArrayList<File> fls = archivosCompatibles(f.listFiles(),codDoc.get("id").toString()+"_");
                for (File file : fls)
                {
                    if(file.exists())
                    {
                        boolean deleted = file.delete();
                    }
                }
            }

            ArrayList<File> files = archivosCompatibles(f.listFiles(),"temporal");


            for (File file : files)
            {
                if(file.exists())
                {
                    boolean deleted = file.delete();
                }
            }

        }catch (Exception e)
        {

        }


        return 1;
    }

    public ArrayList<File> archivosCompatibles(File[] archivos,String name) {
        ArrayList<File> archivosC = new ArrayList<File>();
        for (File f : archivos)
                if (f.getName().toLowerCase().startsWith(name)) {
                    archivosC.add(f);
                }

        return archivosC;
    }
}
