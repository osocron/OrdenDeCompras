package com.patumecha.osocron.ordendecompras;

/**
 * Created by osocron on 30/09/14.
 */
public class Order {

    public String numero;
    public String fecha;
    public String serie;

    public Order(String numero, String fecha, String serie){

        this.numero = numero;
        this.fecha = fecha;
        this.serie = serie;
    }
}
