package com.patumecha.osocron.ordendecompras;

/**
 * Created by osocron on 27/08/14.
 */

public class Cliente {

    public String nombre;
    public String direccion;
    public String claveCliente;
    public String nivel;
    double descuento;
    public String telefono1;
    public String telefono2;

    public Cliente(String nombre, String direccion, String claveCliente, String nivel, double descuento, String telefono1, String telefono2){

        this.nombre = nombre;
        this.direccion = direccion;
        this.claveCliente = claveCliente;
        this.nivel = nivel;
        this.descuento = descuento;
        this.telefono1 = telefono1;
        this.telefono2 = telefono2;
    }
}

