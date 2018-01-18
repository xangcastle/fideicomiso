package com.fideicomiso.banpro.fideicomiso;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.util.Log;

public class SessionManager {
    private static String TAG = SessionManager.class.getSimpleName();


    SharedPreferences pref;

    Editor editor;
    Context _context;


    int PRIVATE_MODE = 0;

    private static final String PREF_NAME = "AndroidHiveLogin";

    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String USU_ID = "usu_id";

    public SessionManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setLogin(boolean isLoggedIn,Integer id_usu) {

        editor.putBoolean(KEY_IS_LOGGED_IN, isLoggedIn);
        editor.putInt(USU_ID, id_usu);



        editor.commit();

        Log.d(TAG, "User login session modified!");
    }

    public boolean isLoggedIn(){

        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }
    public Integer get_user(){

        return pref.getInt(USU_ID,0);
    }
}
