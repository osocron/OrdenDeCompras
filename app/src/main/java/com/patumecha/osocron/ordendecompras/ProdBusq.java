package com.patumecha.osocron.ordendecompras;

/**
 * Created by osocron on 20/08/14.
 */
public class ProdBusq {

    String Clave;
    String Descripcio;

    public ProdBusq(String Clave, String Descripcio){
        this.Clave = Clave;
        this.Descripcio = Descripcio;
    }

    public String getClave(){
        return Clave;
    }
}
