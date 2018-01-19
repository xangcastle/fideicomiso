package com.fideicomiso.banpro.fideicomiso.Clases;

import java.io.Serializable;

/**
 * Created by root on 18/01/18.
 */

public class Registro implements Serializable {

    private String id;
    private String longitud;
    private String latitud;
    private String fecha;
    private String ruta;
    private String punto;
    private String estado;
    private String ncedula;
    private String nombre;
    private String comentarios;
    private String cedula1;
    private String cedula2;
    private String vivienda;
    private String tipo;
    private String usuario;

    public Registro(String id,String longitud,String latitud,String fecha ,String  ruta,String punto,String estado,String ncedula ,String nombre,String comentarios,String cedula1 ,String cedula2,String vivienda,String tipo,String usuario) {
        this.id = id;
        this.longitud = longitud;
        this.latitud = latitud;
        this.fecha = fecha;
        this.ruta = ruta;
        this.punto = punto;
        this.estado = estado;
        this.ncedula =ncedula;
        this.nombre =nombre;
        this.comentarios = comentarios;
        this.cedula1 = cedula1;
        this.cedula2 = cedula2;
        this.vivienda = vivienda;
        this.tipo    =  tipo;
        this.usuario    =  usuario;
    }

    public  void setEstado(String estado)
    {
         this.estado = estado;
    }

    public  String getUsuario()
    {
        return this.usuario;
    }

    public  String getRuta()
    {
        return this.ruta;
    }
    public  String getVivienda()
    {
        return this.vivienda;
    }
    public String getCedula1()
    {
        return this.cedula1;
    }
    public String getCedula2()
    {
        return this.cedula2;
    }
    public String getComentarios()
    {
        return this.comentarios;
    }
    public String getId()
    {
        return this.id;
    }
    public String getLongitud()
    {
        return this.longitud;
    }
    public String getLatitud()
    {
        return this.latitud;
    }
    public String getFecha()
    {
        return this.fecha;
    }
    public String getPunto()
    {
        return this.punto;
    }
    public String getEstado()
    {
        return this.estado;
    }
    public String getNombre()
    {
        return this.nombre;
    }
    public String getNcedula()
    {
        return this.ncedula;
    }
    public String getTipo()
    {
        return this.tipo;
    }
}

