package com.fideicomiso.banpro.fideicomiso;

import android.app.Activity;
import android.os.Bundle;


public class Camera extends Activity  {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_camera);

       // if(versionDispositivo()>=23)

        if (null == savedInstanceState) {
            getFragmentManager().beginTransaction()
                    .replace(R.id.container, VideoFrame.newInstance())
                    .commit();
        }
    }

    public int versionDispositivo()
    {
        int version = 0;
        version = android.os.Build.VERSION.SDK_INT;
        return version;
    }



}


