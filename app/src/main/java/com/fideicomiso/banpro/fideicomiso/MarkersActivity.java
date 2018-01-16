package com.fideicomiso.banpro.fideicomiso;
        import android.app.AlertDialog;
        import android.content.DialogInterface;
        import android.content.Intent;
        import android.graphics.Color;
        import android.os.AsyncTask;
        import android.os.Bundle;
        import android.support.v7.app.AppCompatActivity;
        import android.util.Log;
        import android.view.View;
        import android.widget.Button;
        import com.google.android.gms.maps.CameraUpdateFactory;
        import com.google.android.gms.maps.GoogleMap;
        import com.google.android.gms.maps.OnMapReadyCallback;
        import com.google.android.gms.maps.SupportMapFragment;
        import com.google.android.gms.maps.model.CameraPosition;
        import com.google.android.gms.maps.model.LatLng;
        import com.google.android.gms.maps.model.Marker;
        import com.google.android.gms.maps.model.MarkerOptions;
        import com.google.android.gms.maps.model.PolylineOptions;
        import org.json.JSONObject;
        import java.io.BufferedReader;
        import java.io.IOException;
        import java.io.InputStream;
        import java.io.InputStreamReader;
        import java.net.HttpURLConnection;
        import java.net.URL;
        import java.text.DecimalFormat;
        import java.util.ArrayList;
        import java.util.HashMap;
        import java.util.List;

public class MarkersActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnMarkerClickListener,
        GoogleMap.OnMarkerDragListener,
        GoogleMap.OnInfoWindowClickListener {

    public static final String EXTRA_LATITUD = "LATITUD";
    public static final String EXTRA_LONGITUD = "LONGITUD";
    private Marker markerDestino;
    private Marker markerOrigen;
    private double longitudeDestino;
    private double latitudeDestino;
    private double latitudeOrigen ;
    private double longitudeOrigen;
    private GoogleMap map;
    private String texto ;
    private String id__Punto;
    private Button iniciar;

    HashMap codDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);

        Bundle extras = getIntent().getExtras();
        if(extras != null)
            id__Punto = extras.getString("ID");

        iniciar = (Button) findViewById(R.id.iniciar);
        iniciar.setOnClickListener(new View.OnClickListener()
        {
            @Override
            public void onClick(View v) {

                AlertDialog.Builder builder = new AlertDialog.Builder(v.getContext());

                builder
                        .setMessage("Desea iniciar ?")
                        .setPositiveButton("Si",  new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                                Intent intent = new Intent(getApplicationContext(),GrabarAudioActivity.class);
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



    @Override
    public void onMapReady(GoogleMap googleMap) {
        try
        {
            map = googleMap;

            Bundle extras = getIntent().getExtras();
            if(extras != null)
            {
                id__Punto= extras.getString("ID");
                String[] datos = new String[11];
                datos[0] = "id";
                datos[1] = "departamento";
                datos[2] = "municipio";
                datos[3] = "barrio";
                datos[4] = "comarca";
                datos[5] = "comunidad";
                datos[6] = "direccion";
                datos[7] = "suvecion";
                datos[8] = "contactos";
                datos[9] = "longitude";
                datos[10] = "latitude";

                Conexion conexion = new Conexion(getApplicationContext(), "Delta3", null, 3);
                ArrayList puntos =  conexion.searchRegistration("puntos", datos, " id =  "+id__Punto, null, " DESC");
                codDoc = (HashMap) puntos.get(0);
                HashMap<String, String> map1 = new HashMap<String, String>();
                String longitude  = codDoc.get("longitude").toString();
                String latitude   = codDoc.get("latitude").toString();

                texto = "  \n longitud : "+codDoc.get("longitude").toString()+" \n  latitud : "+ codDoc.get("latitude").toString()+" \n departamento : "+ codDoc.get("departamento").toString()+"\n municipio : "+ codDoc.get("municipio").toString()+" \n comarca : "+ codDoc.get("comarca").toString()+" \n comunidad :"+ codDoc.get("comunidad").toString()+" \n direccion : "+ codDoc.get("direccion").toString()+"\n suvecion : "+ codDoc.get("suvecion").toString()+" \n  contactos :"+ codDoc.get("contactos").toString();



                longitudeDestino  = Double.parseDouble(longitude);
                latitudeDestino   = Double.parseDouble(latitude);

            }

            GPSTracker gps = new GPSTracker(getApplicationContext());

            // check if GPS enabled
            if(gps.canGetLocation()){

                latitudeOrigen = gps.getLatitude();
                longitudeOrigen = gps.getLongitude();


            }else{
                // can't get location
                // GPS or Network is not enabled
                // Ask user to enable GPS/network in settings
                gps.showSettingsAlert();
            }

            final LatLng destino = new LatLng(latitudeDestino,longitudeDestino);
            final LatLng origen  = new LatLng(latitudeOrigen,longitudeOrigen);

            // Markers

            texto = "distancia :"+CalculationByDistance(destino,origen) +" KM "+texto;

            markerOrigen = googleMap.addMarker(new MarkerOptions()
                    .position(origen)
                    .title("Usted esta aqui")
            );

            markerDestino = googleMap.addMarker(
                    new MarkerOptions()
                            .position(destino)
                            .title(codDoc.get("direccion").toString())
            );

            String url = obtenerDireccionesURL(destino, origen);
            DownloadTask downloadTask = new DownloadTask();
            downloadTask.execute(url);

            googleMap.moveCamera(CameraUpdateFactory.newLatLng(destino));
            // Eventos
            googleMap.setOnMarkerClickListener(this);
            googleMap.setOnMarkerDragListener(this);
            googleMap.setOnInfoWindowClickListener(this);


            map.moveCamera(CameraUpdateFactory.newLatLngZoom(origen, 8));
            map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
                @Override
                public void onCameraChange(CameraPosition position) {

                    map.moveCamera(CameraUpdateFactory.newLatLngZoom(destino, map.getCameraPosition().zoom));

                }

            });
        }
        catch (Exception e){}


    }

    @Override
    public boolean onMarkerClick(final Marker marker) {


        return false;
    }

    @Override
    public void onMarkerDragStart(Marker marker) {

    }

    @Override
    public void onMarkerDrag(Marker marker) {

    }

    @Override
    public void onMarkerDragEnd(Marker marker) {

    }

    @Override
    public void onInfoWindowClick(Marker marker) {
        try
        {
            if (marker.equals(markerDestino)) {

                PuntoDetalleDialogFragment.newInstance(marker.getTitle(),
                        texto)
                        .show(getSupportFragmentManager(), null);
            }
        }catch (Exception e)
        {

        }

    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    private String obtenerDireccionesURL(LatLng origin,LatLng dest){
        String url = "";
        try
        {
            String str_origin = "origin="+origin.latitude+","+origin.longitude;

            String str_dest = "destination="+dest.latitude+","+dest.longitude;

            String sensor = "sensor=false";

            String parameters = str_origin+"&"+str_dest+"&"+sensor;

            String output = "json";

             url = "https://maps.googleapis.com/maps/api/directions/"+output+"?"+parameters;

        }catch (Exception e){}


        return url;
    }
    private class DownloadTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... url)
        {

            String data = "";

            try{
                data = downloadUrl(url[0]);
            }catch(Exception e){
                //  Log.d("ERROR AL OBTENER INFO DEL WS",e.toString());
            }
            return data;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            ParserTask parserTask = new ParserTask();

            parserTask.execute(result);
        }
    }

    private String downloadUrl(String strUrl) throws IOException {
        String data = "";
        InputStream iStream = null;
        HttpURLConnection urlConnection = null;
        try{
            URL url = new URL(strUrl);

            // Creamos una conexion http
            urlConnection = (HttpURLConnection) url.openConnection();

            // Conectamos
            urlConnection.connect();

            // Leemos desde URL
            iStream = urlConnection.getInputStream();

            BufferedReader br = new BufferedReader(new InputStreamReader(iStream));

            StringBuffer sb = new StringBuffer();

            String line = "";
            while( ( line = br.readLine()) != null){
                sb.append(line);
            }

            data = sb.toString();

            br.close();

        }catch(Exception e){
            Log.d("Exception", e.toString());
        }finally{
            iStream.close();
            urlConnection.disconnect();
        }
        return data;
    }
    private class ParserTask extends AsyncTask<String, Integer, List<List<HashMap<String,String>>> > {
        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... jsonData) {

            JSONObject jObject;
            List<List<HashMap<String, String>>> routes = null;

            try{
                jObject = new JSONObject(jsonData[0]);
                DirectionsJSONParser parser = new DirectionsJSONParser();

                routes = parser.parse(jObject);
            }catch(Exception e){
                e.printStackTrace();
            }
            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> result) {
            ArrayList<LatLng> points = null;
            PolylineOptions lineOptions = null;
            MarkerOptions markerOptions = new MarkerOptions();

            for(int i=0;i<result.size();i++){
                points = new ArrayList<LatLng>();
                lineOptions = new PolylineOptions();

                List<HashMap<String, String>> path = result.get(i);

                for(int j=0;j<path.size();j++){
                    HashMap<String,String> point = path.get(j);

                    double lat = Double.parseDouble(point.get("lat"));
                    double lng = Double.parseDouble(point.get("lng"));
                    LatLng position = new LatLng(lat, lng);

                    points.add(position);
                }

                lineOptions.addAll(points);
                lineOptions.width(4);
                lineOptions.color(Color.rgb(0,0,255));
            }
            if(lineOptions!=null) {
                map.addPolyline(lineOptions);
            }
        }
    }
    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
        try {
            int Radius = 6371;// radio de la tierra en  kilómetros
            double lat1 = StartP.latitude;
            double lat2 = EndP.latitude;
            double lon1 = StartP.longitude;
            double lon2 = EndP.longitude;
            double dLat = Math.toRadians(lat2 - lat1);
            double dLon = Math.toRadians(lon2 - lon1);
            double a = Math.sin(dLat / 2) * Math.sin(dLat / 2)
                    + Math.cos(Math.toRadians(lat1))
                    * Math.cos(Math.toRadians(lat2)) * Math.sin(dLon / 2)
                    * Math.sin(dLon / 2);
            double c = 2 * Math.asin(Math.sqrt(a));
            double valueResult = Radius * c;
            double km = valueResult / 1;
            DecimalFormat newFormat = new DecimalFormat("####");
            int kmInDec = Integer.valueOf(newFormat.format(km));
            double meter = valueResult % 1000;
            int meterInDec = Integer.valueOf(newFormat.format(meter));
            Log.i("Radius Value", "" + valueResult + "   KM  " + kmInDec
                    + " Meter   " + meterInDec);

            return Radius * c;
        }
        catch (Exception e)
        {
            return 1 ;
        }
    }

}

