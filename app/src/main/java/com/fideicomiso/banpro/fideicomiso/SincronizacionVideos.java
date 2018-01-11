package com.fideicomiso.banpro.fideicomiso;

        import android.content.Context;
        import android.graphics.Bitmap;
        import android.os.AsyncTask;
        import android.os.Environment;
        import android.util.Log;

        import java.io.DataOutputStream;
        import java.io.File;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.FileOutputStream;
        import java.io.IOException;
        import java.io.InputStream;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.ProtocolException;
        import java.net.URL;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.Random;

/**
 * Se encarga de la sincronizacion de imagenes al servidor
 * @version 1.0
 */
public class SincronizacionVideos extends AsyncTask<Void, Void, Integer> {
    /**
     * Constante que la sincronización va tomar como codigo resultado, cuando la sincronizacion se crea
     */
    public static final int SINCRONIZACIONCREADA=1;
    /**
     * Constante que la sincronización va tomar como codigo resultado, cuando la sincronización esta en progreso
     */
    public static final int ENPROGRESO=2;
    /**
     * Constante que la sincronización va tomar como codigo resultado, cuando la imagen ya se subio
     */
    public static final int IMAGENSUBIDA=3;
    /**
     * Constante que la sincronización va tomar como codigo resultado, cuando ocurre un error en la
     * sincronización
     */
    public static final int ERROR=4;
    /**
     * Ruta web a la que se va a realizar la sincronización
     */
    public static final String UPLOAD_URL = "http://www.deltacopiers.com/dtracking/movil/cargar_media/";
    /**
     * Nombre del campo del identificador de la gestion en el servicio web
     */
    public static final String GESTION="gestion";
    /**
     * Nombre del campo de la variable en el web service
     */
    public static final String VARIABLE="variable";
    /**
     * Nombre del campo de la variable imagen en el web service
     */
    public static final String IMAGEN="imagen";

    /**
     * Codigo del resultado que arrojo la sincronizacion
     */
    private int codigoResultado;
    /**
     * Escucha eventos cuando finaliza la sincronizacion
     */
    private ListenerSincronizacionImagenes listenerSincronizacionImagenes;
    /**
     * Titulo del campo
     */

    private String ruta;
    private String longitud;
    private String latitud;
    private String fecha;
    private String usuario;
    private String punto;
    private Context context;

    private String titulo;
    public SincronizacionVideos(Context context, String ruta, String longitud, String latitud, String fecha, String usuario , String punto , ListenerSincronizacionImagenes listenerSincronizacionImagenes) {

        this.ruta=ruta;
        this.longitud=longitud;
        this.latitud=latitud;
        this.fecha=fecha;
        this.usuario=usuario;
        this.punto=punto;
        this.context = context;
        this.listenerSincronizacionImagenes=listenerSincronizacionImagenes;

    }

    /**
     * Antes de ejecutar la tarea
     */
    protected void onPreExecute() {

    }

    @Override
    protected Integer doInBackground(Void... voids) {
        ConexionHttp conexionHttp=null;
        try {
            conexionHttp=new ConexionHttp();
        } catch (MalformedURLException e) {
            return ERROR;
        } catch (FileNotFoundException e) {
            return ERROR;
        }
        return conexionHttp.enviarInformacion();

    }

    /**
     * Despues de ejecutar la tarea
     * @param result  el codigo con el que termino la carga
     */
    protected void onPostExecute(Integer result) {
        this.codigoResultado=result;
        Log.e("CODIGO", Integer.toString(codigoResultado));
        listenerSincronizacionImagenes.enSincronizacionFinalizada(codigoResultado,titulo);

    }



    /**
     * Ruta de la imagen a sincronizar
     */
    public String getRutaImagen() {
        return this.ruta;
    }

    /**
     * Codigo del resultado que arrojo la sincronizacion
     */
    public int getCodigoResultado() {
        return codigoResultado;
    }


    public class ConexionHttp implements Runnable {
        /**
         * URL a la que se va a conectar para solicitar el servicio
         */
        private URL connectURL;
        /**
         * Archivo que contiene la imagen
         */
        private FileInputStream fileInputStream = null;

        public ConexionHttp() throws MalformedURLException, FileNotFoundException {
            connectURL  = new URL(AppConfig.URL_REGISTER_VIDEO);
            fileInputStream = new FileInputStream(getRutaImagen());
        }

        /**
         * Envia la informacion al servidor
         */
        public int enviarInformacion() {
            String lineEnd = "\r\n";
            String twoHyphens = "--";
            String boundary = "*****";
            String Tag = "fSnd";
            try {
                Conexion conexion = new Conexion(context, "Delta", null, 3);

                String[] datos = new String[2];
                datos[0] = "usu_token";

                ArrayList rutas = conexion.searchRegistration("usuarios", datos, null, "1", " DESC");
                if (rutas.size() > 0) {
                    HashMap codDoc = (HashMap) rutas.get(0);
                    String token = codDoc.get("usu_token").toString();

                    Log.e(Tag, "Starting Http File Sending to URL");
                    codigoResultado = ENPROGRESO;
                    // Open a HTTP connection to the URL
                    HttpURLConnection conn = (HttpURLConnection) connectURL.openConnection();

                    // Allow Inputs
                    conn.setDoInput(true);

                    // Allow Outputs
                    conn.setDoOutput(true);

                    // Don't use a cached copy.
                    conn.setUseCaches(false);

                    // Use a post method.
                    conn.setRequestMethod("POST");

                    conn.setRequestProperty("Connection", "Keep-Alive");
                    conn.setRequestProperty("Authorization", "Token " + token);

                    conn.setRequestProperty("Content-Type", "multipart/form-data;boundary=" + boundary);

                    DataOutputStream dos = new DataOutputStream(conn.getOutputStream());


                    dos.writeBytes(twoHyphens + boundary + lineEnd);
                    dos.writeBytes("Content-Disposition: form-data; name=\"punto\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(punto);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"fecha\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(fecha);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"usuario\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(usuario);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"latitude\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(latitud);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data; name=\"longitude\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(longitud);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);



                    dos.writeBytes("Content-Disposition: form-data; name=\"video\";filename=\"" + "imagen.mp4" + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);

                    Log.e(Tag, "Headers are written");

                    // create a buffer of maximum size
                    int bytesAvailable = fileInputStream.available();

                    int maxBufferSize = 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[] buffer = new byte[bufferSize];

                    // read file and write it into form...
                    int bytesRead = fileInputStream.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStream.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStream.read(buffer, 0, bufferSize);
                    }
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);

                    // close streams
                    fileInputStream.close();

                    dos.flush();

                    Log.e(Tag, "File Sent, Response: " + String.valueOf(conn.getResponseMessage()));

                    //  imprimirBody(conn);
                    InputStream is = conn.getInputStream();

                    // retrieve the response from server
                    int ch;

                    StringBuffer b = new StringBuffer();
                    while ((ch = is.read()) != -1) {
                        b.append((char) ch);
                    }
                    String s = b.toString();
                    Log.e("Response", s);
                    dos.close();
                    if ((conn.getResponseCode() == 200 || conn.getResponseCode() == 201) && !s.equals("")) {
                        return IMAGENSUBIDA;
                    } else {
                        return ERROR;
                    }
                }
                else
                {
                    return ERROR;
                }
            } catch (MalformedURLException ex) {
                Log.e(Tag, "URL error: " + ex.getMessage(), ex);
                return ERROR;
            } catch (FileNotFoundException ioe) {
                Log.e(Tag, "File not found: " + ioe.getMessage(), ioe);
                return ERROR;
            } catch (ProtocolException e) {
                Log.e("error protocolo",e.getMessage());
                return ERROR;
            } catch (IOException e) {
                Log.e("IO Error",e.getMessage());
                return ERROR;
            }
        }

        @Override
        public void run() {
            // TODO Auto-generated method stub
        }
    }

    /**
     * Permite comunicar la sincronizacion con quien lo llama
     */
    public interface ListenerSincronizacionImagenes
    {
        /**
         * En caso de que la sincronizacion haya sido finalizado, bien o mal
         * @param codigo  el codigo que arrojo la sincronizacion
         * @param titulo  el titulo del campo que se sincronizo
         */
        void enSincronizacionFinalizada(int codigo, String titulo);
    }


}
