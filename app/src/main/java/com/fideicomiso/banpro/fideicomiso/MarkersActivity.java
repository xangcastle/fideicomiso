package com.fideicomiso.banpro.fideicomiso;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Location;
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
    private Button btn_iniciar_visita ;
    private Button btn_rechazar_visita ;
    HashMap codDoc;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_markers);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        SupportMapFragment mapFragment = (SupportMapFragment)
                getSupportFragmentManager().findFragmentById(R.id.map);

        mapFragment.getMapAsync(this);


    }



    @Override
    public void onMapReady(GoogleMap googleMap) {
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

            Conexion conexion = new Conexion(getApplicationContext(), "Delta2", null, 3);
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

        LatLng destino = new LatLng(latitudeDestino,longitudeDestino);
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


        googleMap.moveCamera(CameraUpdateFactory.newLatLng(destino));
        // Eventos
        googleMap.setOnMarkerClickListener(this);
        googleMap.setOnMarkerDragListener(this);
        googleMap.setOnInfoWindowClickListener(this);

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

        map.moveCamera(CameraUpdateFactory.newLatLngZoom(origen, 15));
        map.setOnCameraChangeListener(new GoogleMap.OnCameraChangeListener() {
            @Override
            public void onCameraChange(CameraPosition position) {

                map.moveCamera(CameraUpdateFactory.newLatLngZoom(origen, map.getCameraPosition().zoom));
                //mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(india, 12));

            }

        });


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
        if (marker.equals(markerDestino)) {

            PuntoDetalleDialogFragment.newInstance(marker.getTitle(),
                    texto)
                    .show(getSupportFragmentManager(), null);
        }
    }

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }

    public double CalculationByDistance(LatLng StartP, LatLng EndP) {
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

}
