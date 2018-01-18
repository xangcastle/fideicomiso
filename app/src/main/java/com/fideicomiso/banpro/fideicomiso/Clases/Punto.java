package com.fideicomiso.banpro.fideicomiso;

import android.content.ContentValues;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by root on 17/01/18.
 */

public class Punto implements Serializable {

    private String latitud;
    private String longitud;
    private String id;
    private String name;
    private String contactos;
    private String direccion;
    private String suvecion;


    public Punto(String id,String latitud ,String longitud ,String name,String contactos,String direccion ,String suvecion) {
        this.id = id;
        this.latitud = latitud;
        this.longitud = longitud;
        this.name = name;
        this.contactos = contactos;
        this.direccion =direccion;
        this.suvecion =suvecion;
    }

    public String getSuvecion()
    {
        return this.suvecion;
    }

    /**
     * Genera una fecha en formato String YYYY-MM-DD HH:MM:SS desde un calendar
     *
     * @param calendar el calendar a convertir
     * @return una cadena con el formato YYYY-MM-DD HH:MM:SS
     */
    public static String generarFechaDesdeCalendar(Calendar calendar) {
        String fecha = "";
        if (calendar != null) {
            int anio = calendar.get(Calendar.YEAR);
            fecha += anio + "-";
            int mes = calendar.get(Calendar.MONTH);
            if (mes < 10) {
                fecha += "0" + mes + "-";
            } else {
                fecha += mes + "-";
            }
            int dia = calendar.get(Calendar.DAY_OF_MONTH);
            if (dia < 10) {
                fecha += "0" + dia + " ";
            } else {
                fecha += dia + " ";
            }
            int hora = calendar.get(Calendar.HOUR_OF_DAY);
            if (hora < 10) {
                fecha += "0" + hora + ":";
            } else {
                fecha += hora + ":";
            }
            int minu = calendar.get(Calendar.MINUTE);
            if (minu < 10) {
                fecha += "0" + minu + ":";
            } else {
                fecha += minu + ":";
            }
            int segun = calendar.get(Calendar.SECOND);
            if (segun < 10) {
                fecha += "0" + segun;
            } else {
                fecha += segun;
            }

        }
        return fecha;
    }
}

