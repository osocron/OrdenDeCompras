package com.patumecha.osocron.ordendecompras;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by osocron on 1/10/14.
 */
public class Final_order implements Parcelable{

    public String empresa;
    public String serie;
    public String numero;
    public String claveCliente;
    public String vendedor;
    public String fecha;
    public String prodClave;
    public String prodSubClave;
    public String prodUMedida;
    public String clienteNivel;
    public double prodPrecio;
    public double prodCantidad;
    public double descuento;
    public double importe;
    public int orden;

    public Final_order(String empresa, String serie, String numero, String claveCliente, String vendedor, String fecha, String prodClave, String prodSubClave, String prodUMedida, String clienteNivel, double prodPrecio, double prodCantidad, double descuento, double importe, int orden){
        this.empresa = empresa;
        this.serie = serie;
        this.numero = numero;
        this.claveCliente = claveCliente;
        this.vendedor = vendedor;
        this.fecha = fecha;
        this.prodClave = prodClave;
        this.prodSubClave = prodSubClave;
        this.prodUMedida = prodUMedida;
        this.clienteNivel = clienteNivel;
        this.prodPrecio = prodPrecio;
        this.prodCantidad = prodCantidad;
        this.descuento = descuento;
        this.importe = importe;
        this.orden = orden;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(empresa);
        dest.writeString(serie);
        dest.writeString(numero);
        dest.writeString(claveCliente);
        dest.writeString(vendedor);
        dest.writeString(fecha);
        dest.writeString(prodClave);
        dest.writeString(prodSubClave);
        dest.writeString(prodUMedida);
        dest.writeString(clienteNivel);
        dest.writeDouble(prodPrecio);
        dest.writeDouble(prodCantidad);
        dest.writeDouble(descuento);
        dest.writeDouble(importe);
        dest.writeInt(orden);
    }

    private Final_order (Parcel in){
        this.empresa = in.readString();
        this.serie = in.readString();
        this.numero = in.readString();
        this.claveCliente = in.readString();
        this.vendedor = in.readString();
        this.fecha = in.readString();
        this.prodClave = in.readString();
        this.prodSubClave = in.readString();
        this.prodUMedida = in.readString();
        this.clienteNivel = in.readString();
        this.prodPrecio = in.readDouble();
        this.prodCantidad = in.readDouble();
        this.descuento = in.readDouble();
        this.importe = in.readDouble();
        this.orden = in.readInt();
    }

    public static final Parcelable.Creator<Final_order> CREATOR = new Parcelable.Creator<Final_order>(){

        @Override
        public Final_order createFromParcel(Parcel source) {
            return new Final_order(source);
        }

        @Override
        public Final_order[] newArray(int size) {
            return new Final_order[size];
        }
    };
}
