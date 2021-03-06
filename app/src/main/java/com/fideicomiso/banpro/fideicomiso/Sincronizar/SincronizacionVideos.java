package com.fideicomiso.banpro.fideicomiso.Sincronizar;

        import android.content.Context;
        import android.os.AsyncTask;
        import android.util.Log;

        import com.fideicomiso.banpro.fideicomiso.Clases.Conexion;
        import com.fideicomiso.banpro.fideicomiso.Controller.AppConfig;

        import java.io.DataOutputStream;
        import java.io.FileInputStream;
        import java.io.FileNotFoundException;
        import java.io.IOException;
        import java.io.InputStream;
        import java.net.HttpURLConnection;
        import java.net.MalformedURLException;
        import java.net.ProtocolException;
        import java.net.URL;


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
    private String ncedula;
    private String nombre;
    private String longitud;
    private String latitud;
    private String fecha;
    private String usuario;
    private String punto;
    private String tipo;
    private String comentario;
    private String ruta_imagen_cedula;
    private String ruta_imagen_casa;
    private String _ruta_cedula2;
    private Context context;

    private String id_punto;
    public SincronizacionVideos(Context context, String ruta, String longitud, String latitud, String fecha, String usuario , String punto ,String id_punto,String _tipo ,String _comentario,String _ruta_imagen_cedula,String _ruta_imagen_casa,String _ruta_cedula2,String ncedula_,String nombre_,ListenerSincronizacionImagenes listenerSincronizacionImagenes) {

        this.ruta=ruta;
        this.longitud=longitud;
        this.latitud=latitud;
        this.fecha=fecha;
        this.usuario=usuario;
        this.punto=punto;
        this.context = context;
        this.listenerSincronizacionImagenes=listenerSincronizacionImagenes;
        this.id_punto =punto;
        this.tipo =_tipo;
        this.comentario =_comentario;
        this.ruta_imagen_cedula =_ruta_imagen_cedula ;
        this.ruta_imagen_casa = _ruta_imagen_casa;
        this._ruta_cedula2 = _ruta_cedula2;
        this.ncedula = ncedula_;
        this.nombre  = nombre_;
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
        try
        {
            Conexion conexion = new Conexion(this.context , "Delta3", null, 3);

            if(this.codigoResultado == 3)
            {
                String[][] datos = new String[1][2];
                datos[0][0] = "estado";
                datos[0][1] = "3";
                long respuesta =  conexion.update("puntos",datos, " id =  "+this.id_punto);
                respuesta =  conexion.update("registros",datos, " punto =  "+this.id_punto);
            }
            else
            {
                String[][] datos = new String[1][2];
                datos[0][0] = "estado";
                datos[0][1] = "1";

                long respuesta =  conexion.update("registros",datos, " punto =  "+this.id_punto);
            }
        }catch (Exception e)
        {

        }


        listenerSincronizacionImagenes.enSincronizacionFinalizada(codigoResultado,this.id_punto);

    }



    /**
     * Ruta de la imagen a sincronizar
     */
    public String getRutaImagen() {
        return this.ruta;
    }
    public String getRutaCedula() {
        return this.ruta_imagen_cedula;
    }
    public String getRutaVivienda() {
        return this.ruta_imagen_casa;
    }
    public String getRutaCedula2() {
        return this._ruta_cedula2;
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
        private FileInputStream fileInputStreamCasa = null;
        private FileInputStream fileInputStreamCedula = null;
        private FileInputStream fileInputStreamCedula2 = null;



        public ConexionHttp() throws MalformedURLException, FileNotFoundException {
            connectURL  = new URL(AppConfig.URL_REGISTER_VIDEO);
            if(getRutaImagen() != null && !getRutaImagen().equals(""))
               fileInputStream = new FileInputStream(getRutaImagen());
            if(getRutaVivienda() != null && !getRutaVivienda().equals(""))
                fileInputStreamCasa = new FileInputStream(getRutaVivienda());
            if(getRutaCedula() != null && !getRutaCedula().equals(""))
                fileInputStreamCedula = new FileInputStream(getRutaCedula());
            if(getRutaCedula2() != null && !getRutaCedula2().equals(""))
                fileInputStreamCedula2 = new FileInputStream(getRutaCedula2());



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

                  
                    dos.writeBytes("Content-Disposition: form-data; name=\"ncedula\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(ncedula);
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    dos.writeBytes("Content-Disposition: form-data;Content-Type:text/plain;charset=UTF-8; name=\"nombre\"" + lineEnd);
                    dos.writeBytes(lineEnd);
                    byte [] content_bytes2 = nombre.getBytes("UTF-8");
                    dos.write(content_bytes2);
                    dos.writeBytes(" ");
                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + lineEnd);

                    if(comentario != null)
                    {
                        dos.writeBytes("Content-Disposition: form-data;Content-Type:text/plain;charset=UTF-8; name=\"comentario\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                        byte [] content_bytes = comentario.getBytes("UTF-8");
                        dos.write(content_bytes);
                        dos.writeBytes(" ");
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                    }

                    if(tipo != null)
                    {
                        dos.writeBytes("Content-Disposition: form-data; name=\"tipo\"" + lineEnd);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(tipo);
                        dos.writeBytes(lineEnd);
                        dos.writeBytes(twoHyphens + boundary + lineEnd);
                    }

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

                    if(ruta!=null && !ruta.equals(""))
                    {
                        dos.writeBytes("Content-Disposition: form-data; name=\"video\";filename=\"" + "audio.mp4" + "\"" + lineEnd);
                        dos.writeBytes(lineEnd);


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
                            fileInputStream.close();
                    }

                if(_ruta_cedula2!=null&& !_ruta_cedula2.equals(""))
                {
                    dos.writeBytes("Content-Disposition: form-data; name=\"cedula2\";filename=\"" + "cedula2.png" + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);


                    // create a buffer of maximum size
                    int bytesAvailable = fileInputStreamCedula2.available();

                    int maxBufferSize = 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[] buffer = new byte[bufferSize];

                    // read file and write it into form...
                    int bytesRead = fileInputStreamCedula2.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStreamCedula2.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStreamCedula2.read(buffer, 0, bufferSize);
                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    fileInputStreamCedula2.close();
                }


                if(ruta_imagen_cedula!=null&& !ruta_imagen_cedula.equals(""))
                {
                    dos.writeBytes("Content-Disposition: form-data; name=\"cedula\";filename=\"" + "cedula.png" + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);


                    // create a buffer of maximum size
                    int bytesAvailable = fileInputStreamCedula.available();

                    int maxBufferSize = 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[] buffer = new byte[bufferSize];

                    // read file and write it into form...
                    int bytesRead = fileInputStreamCedula.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStreamCedula.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStreamCedula.read(buffer, 0, bufferSize);
                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    fileInputStreamCedula.close();
                }

                if(ruta_imagen_casa!=null&& !ruta_imagen_casa.equals(""))
                {
                    dos.writeBytes("Content-Disposition: form-data; name=\"vivienda\";filename=\"" + "vivienda.jpg" + "\"" + lineEnd);
                    dos.writeBytes(lineEnd);


                    // create a buffer of maximum size
                    int bytesAvailable = fileInputStreamCasa.available();

                    int maxBufferSize = 1024;
                    int bufferSize = Math.min(bytesAvailable, maxBufferSize);
                    byte[] buffer = new byte[bufferSize];

                    // read file and write it into form...
                    int bytesRead = fileInputStreamCasa.read(buffer, 0, bufferSize);

                    while (bytesRead > 0) {
                        dos.write(buffer, 0, bufferSize);
                        bytesAvailable = fileInputStreamCasa.available();
                        bufferSize = Math.min(bytesAvailable, maxBufferSize);
                        bytesRead = fileInputStreamCasa.read(buffer, 0, bufferSize);
                    }

                    dos.writeBytes(lineEnd);
                    dos.writeBytes(twoHyphens + boundary + twoHyphens + lineEnd);
                    fileInputStreamCasa.close();
                }

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
         * @param id_punto  el titulo del campo que se sincronizo
         */
        void enSincronizacionFinalizada(int codigo, String id_punto);
    }


}
