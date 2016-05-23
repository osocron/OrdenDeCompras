package com.patumecha.osocron.ordendecompras;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.InputType;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.math.BigDecimal;
import java.util.List;


/**
 * Created by osocron on 19/07/14.
 */
public class viewProducts extends Fragment {

    TextView text_code,text_sub_clave,text_descripcio,text_prec_men,text_existen;
    EditText edit_code,edit_sub_clave,edit_barras,edit_descripcio,edit_prec,edit_existen;
    String descr_cort, descrip, descrip_deta, prec_men, prec_may, prec_esp,cantidad, cantidad1, cantidad2, cantidad3, uMedida, piezas, nivel;

    Button pedidosButton;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.view_products, container, false);

        String myTag = getTag();

        ((searchActivity)getActivity()).setTabFragmentB(myTag);

        text_code = (TextView)rootView.findViewById(R.id.codigoTextView);
        text_sub_clave = (TextView)rootView.findViewById(R.id.subClaveTextView);
        text_descripcio = (TextView)rootView.findViewById(R.id.descripTextView);
        text_prec_men = (TextView)rootView.findViewById(R.id.precioMenTextView);
        text_existen = (TextView)rootView.findViewById(R.id.existenTextView);
        edit_code = (EditText)rootView.findViewById(R.id.codigoEditText);
        edit_sub_clave = (EditText)rootView.findViewById(R.id.subClaveEditText);
        edit_descripcio = (EditText)rootView.findViewById(R.id.descripEditText);
        edit_prec = (EditText)rootView.findViewById(R.id.precioMenEditText);
        edit_barras = (EditText)rootView.findViewById(R.id.barrasEditText);
        edit_existen = (EditText)rootView.findViewById(R.id.existencEditText);
        pedidosButton = (Button) rootView.findViewById(R.id.pedidosButton);

        edit_code.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String TabOfFragmentA = ((searchActivity) getActivity()).getTabFragmentA();

                searchProducts fragmentA = (searchProducts) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentA);

                fragmentA.searchAgain(edit_code.getText().toString(), edit_descripcio.getText().toString());

            }
        });

        edit_descripcio.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String descrToSend = edit_descripcio.getText().toString();
                try {
                    descrToSend = descrToSend.substring(0, 12);
                }catch(Exception e){
                    e.printStackTrace();
                }
                ((searchActivity)getActivity()).setCurrentItem (0, true);
                String TabOfFragmentA = ((searchActivity) getActivity()).getTabFragmentA();
                searchProducts fragmentA = (searchProducts) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentA);
                fragmentA.searchSubstring(descrToSend);
            }
        });

        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {

        super.onActivityCreated(savedInstanceState);

        pedidosButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                List<Cliente> clientList = theClientHelper.getCartCliente();
                Cliente clientFromActivity = clientList.get(0);

                //Hacer algo para enviar informacion al listview del fragmento 2;
                if(edit_code.getText().length() == 0 || edit_prec.getText().length() == 0){
                    Toast toast = Toast.makeText(getActivity(), "Faltan datos necesarios, favor de buscar el artículo de nuevo!", Toast.LENGTH_LONG);
                    toast.show();
                }
                else {

                    Boolean mflag;

                    String TabOfFragmentC = ((searchActivity) getActivity()).getTabFragmentC();

                    orderView fragmentC = (orderView) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentC);

                    mflag = fragmentC.isOnTheList(descr_cort, descrip, descrip_deta);

                    final Double mExistencias = Double.valueOf(edit_existen.getText().toString());

                    if (mExistencias == 0){

                        Toast toast1 = Toast.makeText(getActivity(), "Error: Existencias en 0!", Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.TOP, 10, 180);
                        toast1.show();

                    }

                    else if(!mflag) {

                        if(uMedida.equals("C") && clientFromActivity.nivel.equals("1")){
                            AlertDialog.Builder alertBoxUmedida = new AlertDialog.Builder(getActivity());
                            alertBoxUmedida.setTitle(descr_cort+" "+descrip+" "+descrip_deta);
                            alertBoxUmedida.setMessage("Este artículo se vende por pieza o por caja, escoja una opción:");

                            alertBoxUmedida.setPositiveButton("Piezas", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showAlertBox(mExistencias,"1","1");
                                }
                            })
                                    .setNegativeButton("Caja", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            showAlertBox(mExistencias,"3","2");
                                        }
                                    });
                            alertBoxUmedida.show();
                        }
                        else if(uMedida.equals("P") && clientFromActivity.nivel.equals("1")){
                            showAlertBox(mExistencias,"1","3");
                        }
                        else if(uMedida.equals("C") && (clientFromActivity.nivel.equals("2") || clientFromActivity.nivel.equals("3"))){
                            AlertDialog.Builder alertBoxUmedida = new AlertDialog.Builder(getActivity());
                            alertBoxUmedida.setTitle(descr_cort+" "+descrip+" "+descrip_deta);
                            alertBoxUmedida.setMessage("Este artículo se vende por pieza o por caja, escoja una opción:");

                            alertBoxUmedida.setPositiveButton("Piezas", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    showAlertBox2(mExistencias,"1");
                                }
                            })
                                    .setNegativeButton("Caja", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            showAlertBox(mExistencias,"3","2");
                                        }
                                    });
                            alertBoxUmedida.show();
                        }
                        else if(uMedida.equals("P") && (clientFromActivity.nivel.equals("2") || clientFromActivity.nivel.equals("3"))){
                            showAlertBox2(mExistencias,"2");
                        }
                    }
                    else{
                        Toast toast1 = Toast.makeText(getActivity(), "Error: El artículo ya ha sido ingresado a la lista!", Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.TOP, 10, 180);
                        toast1.show();
                    }
                }
            }
        });

    }

    public void b_updateText(String[] t){
        List<Cliente> clientList = theClientHelper.getCartCliente();
        Cliente clientFromActivity = null;
        int curSize = clientList.size();
        if(curSize >= 1) {
            clientFromActivity = clientList.get(0);
        }
        edit_code.setText(t[0]);
        edit_sub_clave.setText(t[1]);
        if(!t[2].equals("--")) {
            edit_barras.setText(t[2]);
        }
        else if(!t[3].equals("--")){
            edit_barras.setText(t[3]);
        }
        else if(!t[4].equals("--")){
            edit_barras.setText(t[4]);
        }
        else{
            edit_barras.setText("--");
        }
        descr_cort = t[5];
        descrip = t[6];
        edit_descripcio.setText(t[5]+t[6]+" "+t[7]);
        descrip_deta = t[7];
        prec_men = t[8];
        prec_may = t[9];
        prec_esp = t[10];
        String mPrecio = null;
        if(clientFromActivity.nivel.equals("1")){
            mPrecio = t[8];
        }
        else if(clientFromActivity.nivel.equals("2")){
            mPrecio = t[9];
        }
        else if(clientFromActivity.nivel.equals("3")){
            mPrecio = t[10];
        }
        BigDecimal BigPrecio = new BigDecimal(mPrecio).setScale(2, BigDecimal.ROUND_HALF_UP);
        edit_prec.setText(BigPrecio.toString());
        edit_existen.setText(t[11]);
        uMedida = t[12];
        piezas = t[13];
        cantidad1 = t[14];
        cantidad2 = t[15];
        cantidad3 = t[16];
    }

    public void updateFromListView(String[] t){

        edit_code.setText(t[0]);
        edit_sub_clave.setText(t[1]);
        edit_barras.setText(t[2]);
        descr_cort = t[3];
        descrip = t[4];
        edit_descripcio.setText(t[3]+t[4]+" "+t[5]);
        descrip_deta = t[5];
        String mPrecio = null;
        if(t[16].equals("1")){
            mPrecio = t[6];
        }
        else if(t[16].equals("2")){
            mPrecio = t[7];
        }
        else if(t[16].equals("3")){
            mPrecio = t[8];
        }
        BigDecimal BigPrecio = new BigDecimal(mPrecio).setScale(2, BigDecimal.ROUND_HALF_UP);
        edit_prec.setText(BigPrecio.toString());
        edit_existen.setText(t[9]);
        cantidad = t[10];
        cantidad1 = t[11];
        cantidad2 = t[12];
        cantidad3 = t[13];
        uMedida = t[14];
        piezas = t[15];
        nivel = t[16];
    }

    public void updatePrecio(String precio){
        BigDecimal BigPrecio = new BigDecimal(Double.valueOf(precio)).setScale(2, BigDecimal.ROUND_HALF_UP);
        edit_prec.setText(BigPrecio.toString());
    }

    public void showAlertBox(final Double mExistencias, final String nivel, final String forExisten){

        AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
        alertbox.setMessage("Favor de ingresar la cantidad de artículos que desea:");
        alertbox.setTitle(descr_cort+" "+descrip+" "+descrip_deta);

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);

        alertbox.setView(input);

        alertbox.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                try {

                    //1=se vende por caja pero eligieron piezas, 2=se vende por caja y eligen caja, 3=vende por pieza
                    double newExist = mExistencias;

                    //1=se vende por caja pero eligieron piezas, 2=se vende por pieza
                    if(forExisten.equals("1")){
                        newExist = mExistencias * Double.valueOf(piezas);
                    }

                    Integer mCantidad = Integer.parseInt(input.getText().toString());

                    if (mCantidad > newExist) {

                        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);

                        Toast toast1 = Toast.makeText(getActivity(), "Error: El pedido excede la cantidad en existencias!", Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.TOP, 10, 180);
                        toast1.show();

                    } else {

                        String TabOfFragmentC = ((searchActivity) getActivity()).getTabFragmentC();

                        orderView fragmentC = (orderView) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentC);

                        fragmentC.updateListView(descr_cort, edit_code.getText().toString(), mCantidad, Double.valueOf(prec_men), Double.valueOf(prec_may),Double.valueOf(prec_esp), Integer.parseInt(cantidad1), Integer.parseInt(cantidad2), Integer.parseInt(cantidad3), edit_barras.getText().toString(), edit_sub_clave.getText().toString(), descrip, descrip_deta, Double.valueOf(edit_existen.getText().toString()), uMedida, piezas, nivel,0);

                        fragmentC.updateImporte();

                        ((searchActivity) getActivity()).setCurrentItem(2, true);

                    }


                } catch (NumberFormatException nfe) {
                    System.out.println("Error al leer la cantidad " + nfe);
                }

            }
        })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                    }
                });
        alertbox.show();
    }

    public void showAlertBox2(final Double mExistencias, final String forExisten){
        AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
        alertbox.setMessage("Precios por cantidades:\n\nCantidad 1: de 1 a "+cantidad1+" -- $"+prec_men+"\nCantidad 2: de "+String.valueOf(Integer.parseInt(cantidad1)+1)+" a "+cantidad2+" -- $"+prec_may+"\nCantidad 3: de "+String.valueOf(Integer.parseInt(cantidad2)+1)+" a "+cantidad3+" -- $"+prec_esp+"\n\nFavor de ingresar la cantidad de artículos que desea:");
        alertbox.setTitle(descr_cort+" "+descrip+" "+descrip_deta);

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertbox.setView(input);

        alertbox.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                try {

                    double newExist = mExistencias;

                    //1=se vende por caja pero eligieron piezas, 2=se vende por pieza
                    if(forExisten.equals("1")){
                        newExist = mExistencias * Double.valueOf(piezas);
                    }

                    Integer mCantidad = Integer.parseInt(input.getText().toString());

                    if (mCantidad > newExist) {

                        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);

                        Toast toast1 = Toast.makeText(getActivity(), "Error: El pedido excede la cantidad en existencias!", Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.TOP, 10, 180);
                        toast1.show();

                    } else {

                        String TabOfFragmentC = ((searchActivity) getActivity()).getTabFragmentC();

                        orderView fragmentC = (orderView) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentC);

                        String opcion;

                        if(mCantidad <= Integer.parseInt(cantidad1)){
                            opcion = "1";
                            updatePrecio(prec_men);
                        }
                        else if(mCantidad <= Integer.parseInt(cantidad2)){
                            opcion = "2";
                            updatePrecio(prec_may);
                        }
                        else {
                            opcion = "3";
                            updatePrecio(prec_esp);
                        }

                        fragmentC.updateListView(descr_cort, edit_code.getText().toString(), mCantidad, Double.valueOf(prec_men), Double.valueOf(prec_may), Double.valueOf(prec_esp), Integer.parseInt(cantidad1), Integer.parseInt(cantidad2), Integer.parseInt(cantidad3), edit_barras.getText().toString(), edit_sub_clave.getText().toString(), descrip, descrip_deta, Double.valueOf(edit_existen.getText().toString()), uMedida, piezas, opcion,0);

                        fragmentC.updateImporte();

                        ((searchActivity) getActivity()).setCurrentItem(2, true);

                    }


                } catch (NumberFormatException nfe) {
                    System.out.println("Error al leer la cantidad " + nfe);
                }

            }
        })
                .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog

                    }
                });
        alertbox.show();
    }

    public void clearFields(){
        try {
            edit_code.setText("");
            edit_sub_clave.setText("");
            edit_descripcio.setText("");
            edit_prec.setText("");
            edit_barras.setText("");
            edit_existen.setText("");
        }catch(Exception e){
            e.printStackTrace();
        }
    }

}

