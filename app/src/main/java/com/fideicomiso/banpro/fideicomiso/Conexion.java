package com.fideicomiso.banpro.fideicomiso;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.provider.BaseColumns;
import android.util.Log;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.HashMap;


public class Conexion extends SQLiteOpenHelper {

    private String db_path;
    private String db_name;
    private SQLiteDatabase db;
    private Context context;
    private File path;


    public void crearTabla() {
        try {
            openDataBase();
            db.execSQL("CREATE TABLE IF NOT EXISTS 'usuarios' ('_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'id' INTEGER, 'username' TEXT ,'nombre' TEXT );");
            db.execSQL("CREATE TABLE IF NOT EXISTS 'puntos' ('_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'departamento' TEXT , 'municipio' TEXT ,'barrio' TEXT, 'comarca' TEXT, 'comunidad' TEXT ,'direccion' TEXT ,'suvecion' INTEGER,'id' TEXT, 'contactos' TEXT , 'longitude' TEXT,'latitude' TEXT,'estado' INTEGER DEFAULT  0);");
            db.execSQL("CREATE TABLE IF NOT EXISTS 'contactos' ('_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'nombre' TEXT , 'referencia' TEXT ,'punto' INTEGER);");
            db.execSQL("CREATE TABLE IF NOT EXISTS 'registros' ('_id' INTEGER PRIMARY KEY AUTOINCREMENT, 'longitud' TEXT , 'latitud' TEXT ,'fecha' TEXT ,'ruta' TEXT , 'punto' INTEGER ,'usuario' INTEGER ,'estado' INTEGER DEFAULT  0);");
        } catch (Exception e) {
            Log.e("", e.toString());
        }
    }


    public void deleteTabla() {
        try {
            openDataBase();
            db.execSQL("DELETE FROM 'usuarios'");
            db.execSQL("DELETE FROM 'puntos'");
            db.execSQL("DELETE FROM 'contactos'");
          } catch (Exception e) {
            Log.e("", e.toString());
        }
    }

    public Conexion(Context _context, String name, SQLiteDatabase.CursorFactory factory, int version) {
        super(_context, name, factory, version);
        context = _context;
        path = context.getDatabasePath(name);
        db_path = path.getPath();
        db_name = name;
        try {
            createDataBase();
            db = this.getWritableDatabase();
        } catch (Exception e) {
            Log.e("", e.toString());
        }


    }

    @Override
    public void onCreate(SQLiteDatabase db) {

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }

    private boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String myPath = db_path;
            checkDB = SQLiteDatabase.openDatabase(myPath, null, SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {
            Log.w("Base de datos no Creada", e.toString());
        }

        if (checkDB != null) {

            checkDB.close();
        }
        return (checkDB != null);
    }


    public void createDataBase() throws IOException {
        boolean dbExist = checkDataBase();
        if (!dbExist) {
            // Llamando a este método se crea la base de datos vacía en la ruta
            // por defecto del sistema de nuestra aplicación por lo que
            // podremos sobreescribirla con nuestra base de datos.
            this.getReadableDatabase();

            try {
                CopyDataBaseFromAsset();
                //copyDataBase();

            } catch (IOException e) {

                throw new Error("Error copiando database");
            }

        }
        crearTabla();

    }


    public void CopyDataBaseFromAsset() throws IOException {

        Log.e("sample", "Starting copying");
        String outputFileName = path + "";
        //File databaseFile = new File( "/data/data/com.copy.copydatabasefromasset/databases");
        File databaseFile = new File(context.getFilesDir().getPath());
        // check if databases folder exists, if not create one and its subfolders
        if (!databaseFile.exists()) {
            databaseFile.mkdir();
        }

        OutputStream out = new FileOutputStream(outputFileName);

        byte[] buffer = new byte[1024];
        int length;

            /*InputStream in  = context.getAssets().open(db_name);
            while ((length = in.read(buffer))>0){
                out.write(buffer,0,length);
            }*/
        Log.e("sample", "Completed");
        out.flush();
        out.close();
        //in.close();

    }

    public long update(String nameTable, String[][] data,String where)
    {

        try {
            openDataBase();
            ContentValues values = new ContentValues();

            for (String[] aData : data) {
                values.put(aData[0], aData[1]);
            }

            long res = db.update(nameTable, values,where,null);
            return res;
        } catch (Exception e) {
            return -1;
        } finally {
            closeBD();

        }
    }

    public long insertRegistration(String nameTable, String[][] data) {
        try {
            openDataBase();
            ContentValues values = new ContentValues();
            //values.put("_id",_ID);
            for (String[] aData : data) {
                values.put(aData[0], aData[1]);
            }

            String query;


            long res = db.insert(nameTable, null, values);

            Log.w("", "" + res);
            return res;
        } catch (Exception e) {
            return -1;
        } finally {
            closeBD();

        }


    }

    public long deleteRegistration(String nameTable, String where) {
        try {

            long res = db.delete(nameTable, where, null);

            Log.w("", "" + res);
            return res;
        } catch (Exception e) {
            return -1;
        } finally {
            closeBD();

        }
    }

    public int getTypeData(Cursor cursor, String column) {
        int columnType;
        int index = cursor.getColumnIndex(column);
        try {
            cursor.getInt(index);
            columnType = 1;

        } catch (Exception ignore) {

            try {
                cursor.getString(index);
                columnType = 3;

            } catch (Exception ignore1) {

                try {
                    cursor.getFloat(index);
                    columnType = 2;

                } catch (Exception ignore2) {

                    try {
                        cursor.getBlob(index);

                        columnType = 4;

                    } catch (Exception ignore3) {

                        columnType = 0;
                    }
                }
            }
        }
        return columnType;
    }


    public ArrayList searchRegistration(String nameTable, String[] fields, String where, String top, String order) {
        openDataBase();
        String respuesta = "";
        Cursor cursor = null;
        if (order == null || order == "") {
            order = " ASC";
        }

        try {
            if (top != null) {
                cursor = db.query(nameTable, fields, where, null, null, null, BaseColumns._ID + " " + order, top);

            } else {
                cursor = db.query(nameTable, fields, where, null, null, null, BaseColumns._ID + " " + order);
            }


        } catch (Exception e) {
            Log.e("Error en QUERY", "" + e.toString());
        }

        ArrayList result = new ArrayList();
        int index;
        int type;
        cursor.moveToNext();
        if (cursor.moveToFirst()) {
            try {
                while (!cursor.isAfterLast()) {
                    //ArrayList data = new ArrayList();
                    HashMap data = new HashMap();
                    for (int i = 0; i < cursor.getColumnCount(); i++) {
                        index = cursor.getColumnIndex(fields[i]);
                        int currentapiVersion = android.os.Build.VERSION.SDK_INT;

                        try {
                            if (currentapiVersion >= android.os.Build.VERSION_CODES.HONEYCOMB) {
                                type = cursor.getType(index);
                            } else {
                                type = getTypeData(cursor, fields[i]);
                            }
                        } catch (Exception e) {
                            type = getTypeData(cursor, fields[i]);
                        }

                        switch (type) {
                            case Cursor.FIELD_TYPE_BLOB:
                                data.put(fields[i], cursor.getBlob(index));
                                break;
                            case Cursor.FIELD_TYPE_FLOAT:
                                data.put(fields[i], cursor.getFloat(index));
                                break;
                            case Cursor.FIELD_TYPE_INTEGER:
                                data.put(fields[i], cursor.getInt(index));
                                break;
                            case Cursor.FIELD_TYPE_NULL:
                                data.put(fields[i], "");
                                break;
                            case Cursor.FIELD_TYPE_STRING:
                                data.put(fields[i], cursor.getString(index));
                                respuesta += cursor.getString(index);
                                break;
                        }
                    }
                    respuesta += "\n";
                    result.add(data);
                    cursor.moveToNext();
                }
            } catch (Exception e) {
                String l = e.getMessage();
            }

        }
        return result;//respuesta ;//
    }


    /**
     * Este metodo permite finalizar la coneccion de la base de datos actual
     */
    public void closeBD() {
        close();
    }

    /**
     * Este metodo permite finalizar la coneccion de la base de datos actual
     */

    private void openDataBase() throws SQLException {
        String path = db_path;
        db = SQLiteDatabase.openDatabase(path, null, SQLiteDatabase.NO_LOCALIZED_COLLATORS | SQLiteDatabase.CREATE_IF_NECESSARY);
    }


}
