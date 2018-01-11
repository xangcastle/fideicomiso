package com.fideicomiso.banpro.fideicomiso;

import android.location.Location;
import android.location.LocationListener;
import android.os.Bundle;


/**
 * Created by root on 10/01/18.
 */

public class CamLocationListener implements LocationListener {
    VideoFrame mainActivity;

    public VideoFrame getMainActivity() {
        return mainActivity;
    }

    public void setMainActivity(VideoFrame mainActivity) {
        this.mainActivity = mainActivity;
    }

    @Override
    public void onLocationChanged(Location loc) {
        // Este mŽtodo se ejecuta cada vez que el GPS recibe nuevas coordenadas
        // debido a la detecci—n de un cambio de ubicacion
        loc.getLatitude();
        loc.getLongitude();
        String Text = "Mi ubicaci—n actual es: " + "\n Lat = "
                + loc.getLatitude() + "\n Long = " + loc.getLongitude();

        this.mainActivity.setLocation(loc);
    }

    @Override
    public void onProviderDisabled(String provider) {
        // Este mŽtodo se ejecuta cuando el GPS es desactivado

    }

    @Override
    public void onProviderEnabled(String provider) {
        // Este mŽtodo se ejecuta cuando el GPS es activado

    }

    @Override
    public void onStatusChanged(String provider, int status, Bundle extras) {
        // Este mŽtodo se ejecuta cada vez que se detecta un cambio en el
        // status del proveedor de localizaci—n (GPS)
        // Los diferentes Status son:
        // OUT_OF_SERVICE -> Si el proveedor esta fuera de servicio
        // TEMPORARILY_UNAVAILABLE -> Temp˜ralmente no disponible pero se
        // espera que este disponible en breve
        // AVAILABLE -> Disponible
    }
}