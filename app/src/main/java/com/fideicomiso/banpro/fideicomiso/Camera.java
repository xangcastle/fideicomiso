package com.fideicomiso.banpro.fideicomiso;

import android.app.Activity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.TextView;


public class Camera extends Activity  {
    public String id_Punto ;
    public TextView text;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

       // if(versionDispositivo()>=23)

        if (null == savedInstanceState) {
            VideoFrame vf = VideoFrame.newInstance();
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, vf)
                    .commit();
            Bundle extras = getIntent().getExtras();
            if(extras != null) {
                id_Punto= extras.getString("ID");
                text = (TextView)findViewById(R.id.id_punto);
                text.setText(id_Punto);
            }
        }

    }

    public int versionDispositivo()
    {
        int version = 0;
        version = android.os.Build.VERSION.SDK_INT;
        return version;
    }



}


