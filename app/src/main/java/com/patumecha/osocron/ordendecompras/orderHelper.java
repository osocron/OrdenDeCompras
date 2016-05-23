package com.patumecha.osocron.ordendecompras;

import java.util.List;
import java.util.Vector;

/**
 * Created by osocron on 30/09/14.
 */
public class orderHelper {

    private static List<Order> cart;
    private static String vendSerie = searchActivity.getInstance().getVendSerie();

    public static List<Order> getCartOrder() {
        if(cart == null) {
            cart = new Vector<Order>();
        }
        return cart;
    }

    public static  List<Order> addCart(String numero, String fecha, String serie){
        if(cart == null){
            cart = new Vector<Order>();
            cart.add(new Order(numero,fecha,serie));
        }
        else{
            if(serie.equals(vendSerie)) {
                cart.add(new Order(numero, fecha, serie));
            }
        }
        return cart;
    }

    public static void clearAll(){
        if(!cart.isEmpty()){
            cart.clear();
        }
    }
}
