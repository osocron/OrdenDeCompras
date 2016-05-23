package com.patumecha.osocron.ordendecompras;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.text.InputType;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.List;

/**
 * Created by osocron on 24/07/14.
 */
public class customBaseAdapter extends BaseAdapter {

    private List<Product> mProductList;
    private LayoutInflater mInflater;
    private double mPrecio;
    private static Cliente curCliente;

    public customBaseAdapter(List<Product> list, LayoutInflater inflater) {
        mProductList = list;
        mInflater = inflater;
    }

    @Override
    public int getCount() {
        return mProductList.size();
    }

    @Override
    public Object getItem(int position) {
        return mProductList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewItem item;

        List<Cliente> clientList = theClientHelper.getCartCliente();
        final Cliente curCliente = clientList.get(0);


        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.list_layout,null);
            convertView.setClickable(true);
            convertView.setFocusable(true);
            convertView.setLongClickable(true);
            convertView.setBackgroundResource(android.R.drawable.btn_default_small);

            item = new ViewItem();

            item.productoNombre = (TextView) convertView.findViewById(R.id.productTextView);
            item.productClave = (TextView) convertView.findViewById(R.id.claveProductoTextView);

            item.cantidad = (EditText) convertView.findViewById(R.id.cantidadEditText);

            item.menosButton = (Button) convertView.findViewById(R.id.menosButton);
            item.masButton = (Button) convertView.findViewById(R.id.masButton);

            item.descuento = (TextView) convertView.findViewById(R.id.descuentoTextView);
            item.precioProd = (TextView) convertView.findViewById(R.id.precioProdTextView);
            item.preImporte = (TextView) convertView.findViewById(R.id.preImportetextView);

            convertView.setTag(item);
        } else {
            item = (ViewItem) convertView.getTag();
        }

        final Product curProduct = mProductList.get(position);


        item.productoNombre.setText(curProduct.nombreProducto + curProduct.descripProducto + " " + curProduct.descrDetaProducto);
        item.productClave.setText(curProduct.barras1);
        item.cantidad.setText(Integer.toString(curProduct.cantidad));

        item.descuento.setText("Desc.: "+(curCliente.descuento+curProduct.desc));


        if(curProduct.nivel.equals("1")){
            mPrecio = curProduct.precioMen;
        }
        else if(curProduct.nivel.equals("2")){
            mPrecio = curProduct.precioMay;
        }
        else if(curProduct.nivel.equals("3")){
            mPrecio = curProduct.precioEsp;
        }
        BigDecimal BigPrecio = new BigDecimal(mPrecio).setScale(2,BigDecimal.ROUND_HALF_UP);
        Double preImport = ((curProduct.cantidad*mPrecio)-(((curCliente.descuento+curProduct.desc)/100)*(curProduct.cantidad*mPrecio)));
        BigDecimal BigImport = new BigDecimal(preImport).setScale(2,BigDecimal.ROUND_HALF_UP);
        item.precioProd.setText("Precio: $"+BigPrecio.toString());
        item.preImporte.setText("Importe: $"+String.valueOf(BigImport));

        item.menosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentCantidad = Integer.parseInt(item.cantidad.getText().toString());

                if(currentCantidad != 0) {

                    currentCantidad = currentCantidad - 1;

                    item.cantidad.setText(Integer.toString(currentCantidad));

                    String TabOfFragmentC = ((searchActivity)v.getRootView().getContext()).getTabFragmentC();

                    orderView fragmentC = (orderView)((searchActivity) v.getRootView().getContext()).getFragmentManager().findFragmentByTag(TabOfFragmentC);

                    fragmentC.updateCantidad(position,Integer.parseInt(item.cantidad.getText().toString()));

                    if(curCliente.nivel.equals("2")||curCliente.nivel.equals("3")) {

                        if (currentCantidad <= curProduct.cantidad1) {
                            fragmentC.updateNivel(position,"1",String.valueOf(curProduct.precioMen));
                        } else if (currentCantidad <= curProduct.cantidad2) {
                            fragmentC.updateNivel(position,"2",String.valueOf(curProduct.precioMay));
                        } else {
                            fragmentC.updateNivel(position,"3",String.valueOf(curProduct.precioMay));
                        }
                    }

                    fragmentC.updateImporte();
                }
            }
        });

        item.masButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                int currentCantidad = Integer.parseInt(item.cantidad.getText().toString());

                double realExistenc = curProduct.existenciasProducto;

                if(curProduct.uMedida.equals("C")&&(curProduct.nivel.equals("1")||curProduct.nivel.equals("2"))){
                    realExistenc = curProduct.existenciasProducto * Double.valueOf(curProduct.piezas);
                }

                if(curProduct.cantidad < realExistenc) {

                    currentCantidad = currentCantidad + 1;

                    item.cantidad.setText(Integer.toString(currentCantidad));

                    item.cantidad.setText(Integer.toString(currentCantidad));

                    String TabOfFragmentC = ((searchActivity) v.getRootView().getContext()).getTabFragmentC();

                    orderView fragmentC = (orderView) ((searchActivity) v.getRootView().getContext()).getFragmentManager().findFragmentByTag(TabOfFragmentC);

                    fragmentC.updateCantidad(position, Integer.parseInt(item.cantidad.getText().toString()));

                    if(curCliente.nivel.equals("2")||curCliente.nivel.equals("3")) {

                        if (currentCantidad <= curProduct.cantidad1) {
                            fragmentC.updateNivel(position,"1",String.valueOf(curProduct.precioMen));
                        } else if (currentCantidad <= curProduct.cantidad2) {
                            fragmentC.updateNivel(position,"2",String.valueOf(curProduct.precioMay));
                        } else {
                            fragmentC.updateNivel(position,"3",String.valueOf(curProduct.precioEsp));
                        }
                    }

                    fragmentC.updateImporte();
                }

            }
        });



        convertView.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(final View v) {

                AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                alertbox.setMessage("Elija una opción:");
                alertbox.setTitle("¡Atención!");

                alertbox.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        String TabOfFragmentC = ((searchActivity)v.getRootView().getContext()).getTabFragmentC();

                        orderView fragmentC = (orderView)((searchActivity) v.getRootView().getContext()).getFragmentManager().findFragmentByTag(TabOfFragmentC);

                        fragmentC.updateListFromRemove(position);

                        fragmentC.updateImporte();

                    }
                })
                        .setNegativeButton("Descuento", new DialogInterface.OnClickListener() {
                            public void onClick(DialogInterface dialog, int id) {
                                // User cancelled the dialog
                                AlertDialog.Builder alertbox = new AlertDialog.Builder(v.getRootView().getContext());
                                alertbox.setMessage("Favor de ingresar el descuento al artículo:");
                                alertbox.setTitle(curProduct.nombreProducto+" "+curProduct.descripProducto+" "+curProduct.descrDetaProducto);

                                final EditText input = new EditText(v.getRootView().getContext());
                                input.setRawInputType(InputType.TYPE_CLASS_NUMBER | InputType.TYPE_NUMBER_FLAG_DECIMAL);

                                alertbox.setView(input);

                                alertbox.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        //Do something
                                        double newDesc = Double.valueOf(input.getText().toString());

                                        if(newDesc > 100){
                                            newDesc = 0.0;
                                            Toast myToast = Toast.makeText(searchActivity.getInstance(),"El descuento no puede ser mayor a 100",Toast.LENGTH_LONG);
                                            myToast.show();
                                        }

                                        String TabOfFragmentC = ((searchActivity)v.getRootView().getContext()).getTabFragmentC();

                                        orderView fragmentC = (orderView)((searchActivity) v.getRootView().getContext()).getFragmentManager().findFragmentByTag(TabOfFragmentC);

                                        fragmentC.updateDesc(position, newDesc);

                                        Double preImport = ((curProduct.cantidad*mPrecio)-(((curCliente.descuento+curProduct.desc)/100)*(curProduct.cantidad*mPrecio)));
                                        BigDecimal BigImport = new BigDecimal(preImport).setScale(2,BigDecimal.ROUND_HALF_UP);

                                        item.descuento.setText("Desc.: "+(curCliente.descuento+curProduct.desc));

                                        item.preImporte.setText("Importe: $"+String.valueOf(BigImport));
                                    }
                                })
                                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                            public void onClick(DialogInterface dialog, int id) {
                                                // User cancelled the dialog

                                            }
                                        });
                                alertbox.show();

                            }
                        });
                alertbox.show();
                return false;
            }
        });

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String[] arrayToFragmentB = {curProduct.claveProducto, curProduct.subClaveProducto, curProduct.barras1, curProduct.nombreProducto, curProduct.descripProducto, curProduct.descrDetaProducto, String.valueOf(curProduct.precioMen), String.valueOf(curProduct.precioMay), String.valueOf(curProduct.precioEsp), String.valueOf(curProduct.existenciasProducto), String.valueOf(curProduct.cantidad), String.valueOf(curProduct.cantidad1), String.valueOf(curProduct.cantidad2), String.valueOf(curProduct.cantidad3), curProduct.uMedida, curProduct.piezas, curProduct.nivel};

                String TabOfFragmentB = ((searchActivity) v.getRootView().getContext()).getTabFragmentB();

                viewProducts fragmentB = (viewProducts) ((searchActivity) v.getRootView().getContext()).getFragmentManager().findFragmentByTag(TabOfFragmentB);

                fragmentB.updateFromListView(arrayToFragmentB);

            }
        });



        return convertView;
    }

    private class ViewItem {
        TextView productoNombre;
        TextView productClave;
        EditText cantidad;
        Button masButton, menosButton;
        TextView descuento;
        TextView precioProd;
        TextView preImporte;
    }

}
