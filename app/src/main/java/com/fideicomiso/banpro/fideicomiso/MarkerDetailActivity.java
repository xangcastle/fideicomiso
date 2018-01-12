package com.herprogramacion.googlemapsenandroid;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.TextView;

public class MarkerDetailActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_marker_detail);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

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

    @Override
    public boolean onSupportNavigateUp() {
        onBackPressed();
        return super.onSupportNavigateUp();
    }
}
