package com.patumecha.osocron.ordendecompras;

import java.util.ArrayList;

/**
 * Created by osocron on 1/10/14.
 */
public class Final_order_helper {

    private static ArrayList<Final_order> cart;

    public static ArrayList<Final_order> getCartOrder() {
        if(cart == null) {
            cart = new ArrayList<Final_order>();
        }
        return cart;
    }

    public static  ArrayList<Final_order> addCart(String empresa, String serie, String numero, String claveCliente, String vendedor, String fecha, String prodClave, String prodSubClave, String prodUMedida, String clienteNivel, double prodPrecio, double prodCantidad, double descuento, double importe, int orden){
        if(cart == null){
            cart = new ArrayList<Final_order>();
            cart.add(new Final_order(empresa,serie,numero,claveCliente,vendedor,fecha,prodClave,prodSubClave,prodUMedida,clienteNivel,prodPrecio,prodCantidad,descuento,importe,orden));
        }
        else{
            cart.add(new Final_order(empresa,serie,numero,claveCliente,vendedor,fecha,prodClave,prodSubClave,prodUMedida,clienteNivel,prodPrecio,prodCantidad,descuento,importe,orden));
        }
        return cart;
    }

    public static void clearAll(){
        if(!cart.isEmpty()){
            cart.clear();
        }
    }

}
