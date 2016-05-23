package com.patumecha.osocron.ordendecompras;

import java.util.List;
import java.util.Vector;

/**
 * Created by osocron on 24/07/14.
 */
public class ordenComprasHelper {

    private static List<Product> cart;

    public static List<Product> getCart() {
        if(cart == null) {
            cart = new Vector<Product>();
        }
        return cart;
    }

    public static  List<Product> addCart(String nombreProducto, String claveProducto, Integer cantidad, Double precio_men, Double precio_may, Double precio_esp, Integer cantidad1, Integer cantidad2, Integer cantidad3, String barras1,String subClaveProducto, String descripProducto, String descrDetaProducto, double existenciasProducto, String uMedida, String piezas, String nivel, double desc){
        if(cart == null){
            cart = new Vector<Product>();
            cart.add(new Product(nombreProducto, claveProducto, cantidad, precio_men, precio_may, precio_esp, cantidad1, cantidad2, cantidad3, barras1, subClaveProducto, descripProducto, descrDetaProducto, existenciasProducto, uMedida, piezas, nivel, desc));
        }
        else{
            if(!isOnList(nombreProducto,descripProducto,descrDetaProducto)) {
                cart.add(new Product(nombreProducto, claveProducto, cantidad, precio_men, precio_may, precio_esp, cantidad1, cantidad2, cantidad3, barras1, subClaveProducto, descripProducto, descrDetaProducto, existenciasProducto, uMedida, piezas, nivel,desc));
            }
        }
        return cart;
    }

    public static  Boolean isOnList(String nombreProducto,String descripProducto, String descrDetaProducto){
        Boolean mflag = false;
        if(cart == null){
            return false;
        }
        else{
            for (Product aCart : cart) {
                if (nombreProducto.equals(aCart.nombreProducto) && descripProducto.equals(aCart.descripProducto) && descrDetaProducto.equals(aCart.descrDetaProducto)) {
                    mflag = true;
                }
            }
        }
        return mflag;
    }

    public static  List<Product> removeFromCart(Integer index){
        if(cart != null){
            cart.remove(cart.get(index));
        }

        return cart;
    }

    public static List<Product> changeCantidad(Integer index, Integer cantidad){
        Product curProduct = cart.get(index);
        curProduct.setCantidad(cantidad);
        return cart;
    }

    public static List<Product> changeNivel(Integer index, String nivel){
        Product curProduct = cart.get(index);
        curProduct.setNivel(nivel);
        return cart;
    }

    public static List<Product> changeDesc(Integer index, double desc){
        Product curProduct = cart.get(index);
        curProduct.setDesc(desc);
        return cart;
    }


    public static Double getImporte(){

        Double mImporte = 0.0;
        Product curProduct;
        Integer curCantidad;
        Double curPrecio = null;

        if(cart.size()>0) {

            for (Product aCart : cart) {

                curProduct = aCart;
                curCantidad = curProduct.cantidad;
                String TabOfFragmentA = searchActivity.getInstance().getTabFragmentA();
                searchProducts fragmentA = (searchProducts) searchActivity.getInstance().getFragmentManager().findFragmentByTag(TabOfFragmentA);
                Cliente curCliente = fragmentA.getCliente();

                if (curProduct.nivel.equals("1")) {
                    curPrecio = curProduct.precioMen;
                } else if (curProduct.nivel.equals("2")) {
                    curPrecio = curProduct.precioMay;
                } else if (curProduct.nivel.equals("3")) {
                    curPrecio = curProduct.precioEsp;
                }

                double curDesc = (((curProduct.desc + curCliente.descuento) * (curCantidad * curPrecio)) / 100);

                mImporte += ((double) Math.round(((curCantidad * curPrecio) - curDesc) * 100) / 100);

            }
        }

        return mImporte;
    }

    public static void clearAll(){
        if(!cart.isEmpty()){
            cart.clear();
        }
    }
}
