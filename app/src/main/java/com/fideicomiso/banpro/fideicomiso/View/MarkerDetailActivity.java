package com.fideicomiso.banpro.fideicomiso.View;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.widget.TextView;

import com.fideicomiso.banpro.fideicomiso.R;

public class MarkerDetailActivity extends Activity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_detail);



        // Extraer lat. y lng.
        Intent intent = getIntent();
        String latlng = String.format(
                getString(R.string.marker_detail_latlng),
                intent.getDoubleExtra(MarkersActivity.EXTRA_LATITUD, 0),
                intent.getDoubleExtra(MarkersActivity.EXTRA_LONGITUD, 0));

        // Poblar
        TextView coordenadas = (TextView) findViewById(R.id.tv_latlng);
        coordenadas.setText(latlng);

    }


}
