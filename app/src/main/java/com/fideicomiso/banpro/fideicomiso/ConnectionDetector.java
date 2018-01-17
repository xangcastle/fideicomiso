package com.fideicomiso.banpro.fideicomiso;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class ConnectionDetector
{

	private Context _context;

	public ConnectionDetector(Context context) {
		this._context = context;
	}

	public boolean checkMobileInternetConnWifi()
    {

		ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

		if (connectivity != null)
        {

			NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
			
			if (info != null)
            {
				if (info.isConnected())
                {
					return true;
				}
			}
		}
		return false;
	}
    public boolean checkMobileInternetConn()
    {
        ConnectivityManager connectivity = (ConnectivityManager) _context.getSystemService(Context.CONNECTIVITY_SERVICE);

        if (connectivity != null)
        {
            //Get network info - Mobile internet access
            NetworkInfo info = connectivity.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);

            if (info != null)
            {
                //Look for whether device is currently connected to Mobile internet
                if (info.isConnected())
                {
                    return true;
                }
            }
        }
        return false;
    }
    public Boolean connectionVerification()
    {
        if(checkMobileInternetConnWifi()||checkMobileInternetConn())
        {
           return true ;
        }
        else
        {
            return false ;
        }
    }


}
