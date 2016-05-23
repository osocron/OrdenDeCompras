package com.patumecha.osocron.ordendecompras;

/**
 * Created by osocron on 24/07/14.
 */
public class Product {
    public String nombreProducto;
    public String claveProducto;
    public String subClaveProducto;
    public String descripProducto;
    public String descrDetaProducto;
    public double existenciasProducto;
    public int cantidad;
    public double precioMen;
    public double precioMay;
    public double precioEsp;
    public int cantidad1;
    public int cantidad2;
    public int cantidad3;
    public String barras1;
    public String uMedida;
    public String piezas;
    public String nivel;
    public double desc;


    public Product(String nombre, String clave,int cantidad, double precioMen, double precioMay, double precioEsp, int cantidad1, int cantidad2, int cantidad3, String barras1, String subClaveProducto, String descripProducto, String descrDetaProducto, double existenciasProducto, String uMedida, String piezas, String nivel, double desc) {
        this.nombreProducto = nombre;
        this.claveProducto = clave;
        this.cantidad = cantidad;
        this.precioMen = precioMen;
        this.precioMay = precioMay;
        this.precioEsp = precioEsp;
        this.cantidad1 = cantidad1;
        this.cantidad2 = cantidad2;
        this.cantidad3 = cantidad3;
        this.barras1 = barras1;
        this.subClaveProducto = subClaveProducto;
        this.descripProducto = descripProducto;
        this.descrDetaProducto = descrDetaProducto;
        this.existenciasProducto = existenciasProducto;
        this.uMedida = uMedida;
        this.piezas = piezas;
        this.nivel = nivel;
        this.desc = desc;
    }

    public void setCantidad(Integer cantidad){
        this.cantidad = cantidad;
    }

    public void setNivel(String nivel){
        this.nivel = nivel;
    }

    public void setDesc (Double desc){
        this.desc = desc;
    }

}
