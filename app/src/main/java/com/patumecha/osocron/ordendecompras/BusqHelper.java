package com.patumecha.osocron.ordendecompras;

import java.util.List;
import java.util.Vector;

/**
 * Created by osocron on 20/08/14.
 */
public class BusqHelper {

    private static List<ProdBusq> cart;

    public static List<ProdBusq> getCartBusq() {
        if(cart == null) {
            cart = new Vector<ProdBusq>();
        }
        return cart;
    }

    public static  List<ProdBusq> addCart(String Clave, String Descripcio){
        if(cart == null){
            cart = new Vector<ProdBusq>();
            cart.add(new ProdBusq(Clave, Descripcio));
        }
        else{
            cart.add(new ProdBusq(Clave, Descripcio));
        }
        return cart;
    }

    public static ProdBusq getProduct(Integer index){
        return cart.get(index);
    }

    public static void clearAll(){
        if(!cart.isEmpty()){
        cart.clear();
        }
    }
}
