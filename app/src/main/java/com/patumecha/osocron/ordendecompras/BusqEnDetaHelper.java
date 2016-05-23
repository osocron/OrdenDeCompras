package com.patumecha.osocron.ordendecompras;

import java.util.List;
import java.util.Vector;

/**
 * Created by osocron on 22/08/14.
 */
public class BusqEnDetaHelper {

    private static List<ProdBusqDeta> cart;

    public static List<ProdBusqDeta> getCartBusqDeta() {
        if(cart == null) {
            cart = new Vector<ProdBusqDeta>();
        }
        return cart;
    }

    public static  List<ProdBusqDeta> addCart(String descrDeta, String subClave){
        if(cart == null){
            cart = new Vector<ProdBusqDeta>();
            cart.add(new ProdBusqDeta(descrDeta,subClave));
        }
        else{
            cart.add(new ProdBusqDeta(descrDeta,subClave));
        }
        return cart;
    }

    public static ProdBusqDeta getProductDeta(Integer index){
        return cart.get(index);
    }

    public static void clearAll(){
        if(!cart.isEmpty()){
            cart.clear();
        }
    }

}
