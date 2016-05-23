package com.patumecha.osocron.ordendecompras;

import java.util.List;
import java.util.Vector;

/**
 * Created by osocron on 27/08/14.
 */
public class BusqClientHelper {

    private static List<Cliente> cart;

    public static List<Cliente> getCartCliente() {
        if(cart == null) {
            cart = new Vector<Cliente>();
        }
        return cart;
    }

    public static  List<Cliente> addCart(String nombre, String direccion, String claveCliente, String nivel, double descuento, String telefono1, String telefono2){
        if(cart == null){
            cart = new Vector<Cliente>();
            cart.add(new Cliente(nombre,direccion,claveCliente,nivel,descuento,telefono1,telefono2));
        }
        else{
            if(!isOnList(nombre,direccion,telefono1)) {
                cart.add(new Cliente(nombre, direccion, claveCliente, nivel, descuento, telefono1, telefono2));
            }
        }
        return cart;
    }

    public static  Boolean isOnList(String nombreCliente, String direccionCliente, String telefono1){
        Boolean mflag = false;
        if(cart == null){
            return false;
        }
        else{
            for (Cliente aCart : cart) {
                if (nombreCliente.equals(aCart.nombre) && direccionCliente.equals(aCart.direccion) && telefono1.equals(aCart.telefono1)) {
                    mflag = true;
                }
            }
        }
        return mflag;
    }

    public static void clearAll(){
        if(!cart.isEmpty()){
            cart.clear();
        }
    }
}
