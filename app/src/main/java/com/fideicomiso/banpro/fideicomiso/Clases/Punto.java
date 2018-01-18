package com.fideicomiso.banpro.fideicomiso.Clases;

import android.content.ContentValues;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

/**
 * Created by root on 17/01/18.
 */

public class Punto implements Serializable {

    private String departamento;
    private String municipio;
    private String barrio;
    private String comarca;
    private String comunidad;
    private String id;
    private String contactos;
    private String direccion;
    private String suvecion;
    private String estado;




    public Punto(String id,String departamento,String municipio,String barrio ,String  comarca,String comunidad,String contactos,String direccion ,String suvecion) {
        this.id = id;
        this.departamento = departamento;
        this.municipio = municipio;
        this.barrio = barrio;
        this.comarca = comarca;
        this.comunidad = comunidad;
        this.contactos = contactos;
        this.direccion =direccion;
        this.suvecion =suvecion;
    }

    public void setEStado(String estado)
    {
        this.estado = estado;
    }

    public String getEstado()
    {
        return this.estado;
    }

    public String getSuvecion()
    {
        return this.suvecion;
    }

    public String getId()
    {
        return this.id;
    }

    public String getContactos()
    {
        return this.contactos;
    }
    public String getDireccion()
    {
        return this.direccion;
    }
    public String getDepartamento()
    {
        return this.departamento;
    }
    public String getMunicipio()
    {
        return this.municipio;
    }

    public String getBarrio()
    {
        return this.barrio;
    }

    public String getComarca()
    {
        return this.comarca;
    }
    public String getComunidad()
    {
        return this.comunidad;
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

