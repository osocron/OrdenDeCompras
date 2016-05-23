package com.patumecha.osocron.ordendecompras;

import android.app.*;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/**
 * ${PACKAGE_NAME} Created by osocron on 19/07/14.
 */
public class searchProducts extends Fragment {

    private String finalDescr;
    private static String clienteNombre;
    private static String OrderNum = "      ";
    private String codigoToDeta;
    private Dialog detaDialog;

    private static Boolean clientBool = false;

    private BusqBaseAdap myBaseAdapter;
    private BusqDetaBaseAdap myDetaBaseAdapter;
    private static ArrayList<Integer> cantArray = new ArrayList<Integer>();
    private static ArrayList<Double> descArray = new ArrayList<Double>();

    private int mCont = 0;

    AutoCompleteTextView codeEditText;
    TextView codeTextView;
    Button queryButton, scanButton, fastAddItem;
    Httppostaux post;

    String URL_connect = GlobalVars.INET_ADDRESS+"BusqArticulos.php";

    String Query_Clave, Query_sub_clave, Query_barras1, Query_barras2, Query_barras3, Query_descrip_cort, Query_descripcio, Query_descrip_deta, Query_prec_men, Query_prec_may, Query_prec_esp, Query_cantidad1, Query_cantidad2, Query_cantidad3, Query_existen, Query_umedida, Query_piezas;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.search_products, container, false);

        String myTag = getTag();

        ((searchActivity) getActivity()).setTabFragmentA(myTag);

        post = new Httppostaux();

        codeTextView = (TextView) rootView.findViewById(R.id.codeTextView);
        queryButton = (Button) rootView.findViewById(R.id.queryButton);
        scanButton = (Button) rootView.findViewById(R.id.scanButton);
        fastAddItem = (Button) rootView.findViewById(R.id.fastAddItemButton);
        codeEditText = (AutoCompleteTextView) rootView.findViewById(R.id.codeEditText);

        //Retrieve Client Info and set the name on top of actiity:
        try {
            if (!clientBool) {
                Cliente clientFromActivity = searchActivity.getInstance().retrieveExtras();
                try {
                    theClientHelper.clearAll();
                }catch(Exception e2){
                    e2.printStackTrace();
                }
                theClientHelper.addCart(clientFromActivity.nombre,clientFromActivity.direccion,clientFromActivity.claveCliente,clientFromActivity.nivel,clientFromActivity.descuento,clientFromActivity.telefono1,clientFromActivity.telefono2);
                clienteNombre = clientFromActivity.nombre;
            }

        } catch (Exception e) {
            e.printStackTrace();
        }

        //Set Client name
        codeTextView.setText(clienteNombre);

        //Create adapters for auto complete textview and pop up dialog
        List<ProdBusq> mProdList = BusqHelper.getCartBusq();
        List<ProdBusqDeta> mDetaProdList = BusqEnDetaHelper.getCartBusqDeta();

        myBaseAdapter = new BusqBaseAdap(mProdList, getActivity().getLayoutInflater());
        myDetaBaseAdapter = new BusqDetaBaseAdap(mDetaProdList, getActivity().getLayoutInflater());

        codeEditText.setAdapter(myBaseAdapter);
        codeEditText.setThreshold(2);

        codeEditText.addTextChangedListener(new TextWatcher() {

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String mcodigo = codeEditText.getText().toString();
                    if (checkLoginData(mcodigo)) {
                        new asynclogin3().execute(mcodigo);
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }

            }
        });


        queryButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String codigo = codeEditText.getText().toString();

                if (checkLoginData(codigo)) {

                    new asynclogin().execute(codigo);

                } else {

                    err_login();

                }

            }
        });

        scanButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                    intent.putExtra("SCAN_MODE", "PRODUCT_MODE");
                    intent.putExtra("SAVE_HISTORY", false);
                    startActivityForResult(intent, 0);
                } catch (Exception e) {
                    Toast myToast = Toast.makeText(getActivity(), "Favor de instalar Barcode Scanner", Toast.LENGTH_LONG);
                    myToast.show();
                }
            }
        });

        fastAddItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String codigo = codeEditText.getText().toString();

                if (checkLoginData(codigo)) {

                    new asynclogin2().execute(codigo);

                } else {

                    err_login();

                }

            }
        });

        return rootView;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == 0) {
            if (resultCode == Activity.RESULT_OK) {
                String contents = data.getStringExtra("SCAN_RESULT"); //this is the result
                if (checkLoginData(contents)) {
                    new asynclogin().execute(contents);
                } else {

                    err_login();

                }
            } else if (resultCode == Activity.RESULT_CANCELED) {
                // Handle cancel
                Toast toast = Toast.makeText(getActivity(), "Scan was Cancelled!", Toast.LENGTH_LONG);
                toast.show();
            }
        }
    }

    public void err_login() {

        Toast toast1 = Toast.makeText(getActivity(), "Error: Codigo no en catálogo o producto con detallados!", Toast.LENGTH_SHORT);
        toast1.setGravity(Gravity.TOP, 10, 180);
        toast1.show();

    }

    public boolean checkLoginData(String codigo) {
        if (codigo.equals("")) {
            Log.e("Login ui", "Error de codigo");
            return false;
        } else {

            return true;
        }
    }

    class asynclogin extends AsyncTask<String, String, String> {

        String code;

        ProgressDialog pDialog;

        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Buscando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            code = params[0];

            if (getResult(code)) {
                return "ok";
            } else {
                return "err";
            }
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            Log.e("onPostExecute", "" + result);

            if (result.equals("ok")) {

                codeEditText.setText("");

                String[] textPassToB1 = {Query_Clave, Query_sub_clave, Query_barras1, Query_barras2, Query_barras3, Query_descrip_cort, Query_descripcio, Query_descrip_deta, Query_prec_men, Query_prec_may, Query_prec_esp, Query_existen, Query_umedida, Query_piezas, Query_cantidad1, Query_cantidad2, Query_cantidad3};

                String TabOfFragmentB = ((searchActivity) getActivity()).getTabFragmentB();

                viewProducts fragmentB = (viewProducts) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentB);

                fragmentB.b_updateText(textPassToB1);

                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(codeEditText.getWindowToken(), 0);

                ((searchActivity) getActivity()).setCurrentItem(1, true);

            } else {
                err_login();
            }
        }
    }

    public boolean getResult(String codigo) {

        String queryStatus = "";

        ArrayList<NameValuePair> postparameters2send = new ArrayList<NameValuePair>();

        postparameters2send.add(new BasicNameValuePair("codigo", codigo));

        JSONArray jdata = post.getserverdata(postparameters2send, URL_connect);

        SystemClock.sleep(50);

        if (jdata != null && jdata.length() > 0) {

            JSONObject json_data;

            try {

                json_data = jdata.getJSONObject(0);

                queryStatus = json_data.getString("success");

                Log.e("loginstatus", "logstatus= " + queryStatus);

            } catch (JSONException e) {

                e.printStackTrace();

            }

            if (queryStatus.equals("0")) {
                Log.e("loginstatus", "invalido");
                return false;
            } else {
                Log.e("loginstatus", "valido");
                //Do Magic from here on!
                try {
                    json_data = jdata.getJSONObject(1);
                    Query_Clave = json_data.getString("clave");
                    try {
                        Query_sub_clave = json_data.getString("subclave");
                    } catch (Exception e) {
                        Query_sub_clave = "--";
                    }
                    Query_barras1 = json_data.getString("barras1");
                    Query_barras2 = json_data.getString("barras2");
                    Query_barras3 = json_data.getString("barras3");
                    Query_descrip_cort = json_data.getString("descrgruma");
                    Query_descripcio = json_data.getString("descripcio");
                    try {
                        Query_descrip_deta = json_data.getString("descripciodeta");
                    } catch (Exception e) {
                        Query_descrip_deta = "--";
                    }
                    Query_umedida = json_data.getString("umedida");
                    Query_piezas = json_data.getString("piezas");
                    json_data = jdata.getJSONObject(2);
                    Query_prec_men = json_data.getString("menudeo");
                    Query_prec_may = json_data.getString("mayoreo");
                    Query_prec_esp = json_data.getString("especial");
                    Query_cantidad1 = json_data.getString("cantidad1");
                    Query_cantidad2 = json_data.getString("cantidad2");
                    Query_cantidad3 = json_data.getString("cantidad3");
                    json_data = jdata.getJSONObject(3);
                    Query_existen = json_data.getString("existenact");
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                return true;
            }

        } else {
            Log.e("JSON", "Error");
            return false;
        }


    }

    class asynclogin2 extends AsyncTask<String, String, String> {

        String code;

        ProgressDialog pDialog1;

        protected void onPreExecute() {
            pDialog1 = new ProgressDialog(getActivity());
            pDialog1.setMessage("Buscando...");
            pDialog1.setIndeterminate(false);
            pDialog1.setCancelable(true);
            pDialog1.show();
        }

        @Override
        protected String doInBackground(String... params) {

            code = params[0];

            if (getResult(code)) {
                return "ok";
            } else {
                return "err";
            }
        }

        protected void onPostExecute(String result) {

            pDialog1.dismiss();

            Log.e("onPostExecute", "" + result);

            if (result.equals("ok")) {

                String[] textPassToB1 = {Query_Clave, Query_sub_clave, Query_barras1, Query_barras2, Query_barras3, Query_descrip_cort, Query_descripcio, Query_descrip_deta, Query_prec_men, Query_prec_may, Query_prec_esp, Query_existen, Query_umedida, Query_piezas, Query_cantidad1, Query_cantidad2, Query_cantidad3};

                String TabOfFragmentB = ((searchActivity) getActivity()).getTabFragmentB();

                viewProducts fragmentB = (viewProducts) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentB);

                fragmentB.b_updateText(textPassToB1);

                InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                mgr.hideSoftInputFromWindow(codeEditText.getWindowToken(), 0);

                Boolean mflag;

                ((searchActivity) getActivity()).setCurrentItem(2, true);

                String TabOfFragmentC = ((searchActivity) getActivity()).getTabFragmentC();

                orderView fragmentC = (orderView) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentC);

                mflag = fragmentC.isOnTheList(Query_descrip_cort, Query_descripcio, Query_descrip_deta);

                List<Cliente> clientList = theClientHelper.getCartCliente();
                Cliente clientFromActivity = clientList.get(0);

                if (!mflag) {

                    if (Query_umedida.equals("C") && clientFromActivity.nivel.equals("1")) {
                        AlertDialog.Builder alertBoxUmedida = new AlertDialog.Builder(getActivity());
                        alertBoxUmedida.setTitle(Query_descrip_cort + " " + Query_descripcio + " " + Query_descrip_deta);
                        alertBoxUmedida.setMessage("Este artículo se vende por pieza o por caja, escoja una opción:");

                        alertBoxUmedida.setPositiveButton("Piezas", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                showAlertBox("1", "1");
                            }
                        })
                                .setNegativeButton("Caja", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showAlertBox("3", "2");
                                    }
                                });
                        alertBoxUmedida.show();
                    } else if (Query_umedida.equals("P") && clientFromActivity.nivel.equals("1")) {
                        showAlertBox("1", "3");
                    } else if (Query_umedida.equals("C") && (clientFromActivity.nivel.equals("2") || clientFromActivity.nivel.equals("3"))) {
                        AlertDialog.Builder alertBoxUmedida = new AlertDialog.Builder(getActivity());
                        alertBoxUmedida.setTitle(Query_descrip_cort + " " + Query_descripcio + " " + Query_descrip_deta);
                        alertBoxUmedida.setMessage("Este artículo se vende por pieza o por caja, escoja una opción:");

                        alertBoxUmedida.setPositiveButton("Piezas", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                String opcion = "1";
                                showAlertBox2(opcion);
                            }
                        })
                                .setNegativeButton("Caja", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        showAlertBox("3", "2");
                                    }
                                });
                        alertBoxUmedida.show();
                    } else if (Query_umedida.equals("P") && (clientFromActivity.nivel.equals("2") || clientFromActivity.nivel.equals("3"))) {
                        String opcion = "2";
                        showAlertBox2(opcion);
                    }


                } else {
                    Toast toast1 = Toast.makeText(getActivity(), "Error: El artículo ya ha sido ingresado a la lista!", Toast.LENGTH_LONG);
                    toast1.setGravity(Gravity.TOP, 10, 180);
                    toast1.show();
                }

            } else {
                err_login();
            }

        }

    }

    class asynclogin3 extends AsyncTask<String, String, String> {

        String code;

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            code = params[0];

            if (getResult2(code)) {
                return "ok";
            } else {
                return "err";
            }
        }

        protected void onPostExecute(String result) {

            Log.e("onPostExecute", "" + result);

            if (result.equals("ok")) {
                myBaseAdapter.notifyDataSetChanged();
            }
        }
    }

    public boolean getResult2(String codigo) {

        //Mysql creation of a table :) INSERT INTO descrprod (clave,descripcio) SELECT clave, CONCAT(descrgruma,descripcio) FROM articulo;

        ArrayList<NameValuePair> postparameters2send = new ArrayList<NameValuePair>();

        postparameters2send.add(new BasicNameValuePair("codigo", codigo));

        JSONArray jdata = post.getserverdata(postparameters2send, GlobalVars.INET_ADDRESS+"search.php");

        SystemClock.sleep(120);

        if (jdata != null && jdata.length() > 0) {

            JSONObject json_data;

            try {
                BusqHelper.clearAll();

                for (int i = 0; i < jdata.length(); i++) {
                    json_data = jdata.getJSONObject(i);
                    BusqHelper.addCart(json_data.getString("clave"), json_data.getString("descripcio"));
                }

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return true;


        } else {
            Log.e("JSON", "Error");
            return false;
        }

    }

    class asynclogin4 extends AsyncTask<String, String, String> {

        String code;

        ProgressDialog pDialog2;

        protected void onPreExecute() {
            pDialog2 = new ProgressDialog(getActivity());
            pDialog2.setMessage("Buscando en detallados...");
            pDialog2.setIndeterminate(false);
            pDialog2.setCancelable(true);
            pDialog2.show();
        }

        @Override
        protected String doInBackground(String... params) {

            code = params[0];

            if (getResult3(code)) {
                return "ok";
            } else {
                return "err";
            }
        }

        protected void onPostExecute(String result) {
            pDialog2.dismiss();
            Log.e("onPostExecute", "" + result);

            if (result.equals("ok")) {

                //Create a Dialog
                myDetaBaseAdapter.notifyDataSetChanged();

                detaDialog = new Dialog(getActivity());
                detaDialog.setTitle(finalDescr);
                detaDialog.setContentView(R.layout.deta_prod_list);

                ListView detaListView = (ListView) detaDialog.findViewById(R.id.detaListView);

                detaListView.setAdapter(myDetaBaseAdapter);

                detaListView.setItemsCanFocus(true);

                detaDialog.show();

            } else {

                new asynclogin().execute(code);

            }
        }
    }

    public boolean getResult3(String codigo) {

        ArrayList<NameValuePair> postparameters2send = new ArrayList<NameValuePair>();

        postparameters2send.add(new BasicNameValuePair("codigo", codigo));

        JSONArray jdata = post.getserverdata(postparameters2send, GlobalVars.INET_ADDRESS+"search_deta.php");

        SystemClock.sleep(120);

        if (jdata != null && jdata.length() >= 1) {

            JSONObject json_data;

            try {

                BusqEnDetaHelper.clearAll();

                for (int i = 0; i < jdata.length(); i++) {
                    json_data = jdata.getJSONObject(i);
                    BusqEnDetaHelper.addCart(json_data.getString("descripcio"), json_data.getString("subclave"));
                }

            } catch (JSONException e) {
                Log.e("log_tag", "Error parsing data" + e.toString());
            }
            return true;


        } else {
            Log.e("JSON", "Error");
            return false;
        }

    }

    class asynclogin5 extends AsyncTask<String, String, String> {

        String code;

        ProgressDialog pDialog3;

        protected void onPreExecute() {
            pDialog3 = new ProgressDialog(getActivity());
            pDialog3.setMessage("Buscando...");
            pDialog3.setIndeterminate(false);
            pDialog3.setCancelable(true);
            pDialog3.show();
        }

        @Override
        protected String doInBackground(String... params) {

            code = params[0];

            if (getResult(code)) {
                return "ok";
            } else {
                return "err";
            }
        }

        protected void onPostExecute(String result) {

            pDialog3.dismiss();

            Log.e("onPostExecute", "" + result);

            List<Cliente> clientList = theClientHelper.getCartCliente();
            Cliente clientFromActivity = clientList.get(0);

            if (result.equals("ok")) {

                double descuentoFinal = descArray.get(mCont) - clientFromActivity.descuento;

                String prodNivel;
                if (Integer.valueOf(clientFromActivity.nivel) > 1 && cantArray.get(mCont) <= Double.valueOf(Query_cantidad1)) {
                    prodNivel = "1";
                } else if (Integer.valueOf(clientFromActivity.nivel) > 1 && cantArray.get(mCont) <= Double.valueOf(Query_cantidad2)) {
                    prodNivel = "2";
                } else if (Integer.valueOf(clientFromActivity.nivel) > 1 && cantArray.get(mCont) > Double.valueOf(Query_cantidad2)) {
                    prodNivel = "3";
                } else {
                    prodNivel = "1";
                }

                String TabOfFragmentC = ((searchActivity) getActivity()).getTabFragmentC();

                orderView fragmentC = (orderView) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentC);

                String barras;

                if (!Query_barras1.equals("--")) {
                    barras = Query_barras1;
                } else if (!Query_barras2.equals("--")) {
                    barras = Query_barras2;
                } else if (!Query_barras3.equals("--")) {
                    barras = Query_barras3;
                } else {
                    barras = "--";
                }

                fragmentC.updateListView(Query_descrip_cort, Query_Clave, cantArray.get(mCont), Double.valueOf(Query_prec_men), Double.valueOf(Query_prec_may), Double.valueOf(Query_prec_esp), Integer.parseInt(Query_cantidad1), Integer.parseInt(Query_cantidad2), Integer.parseInt(Query_cantidad3), barras, Query_sub_clave, Query_descripcio, Query_descrip_deta, Double.valueOf(Query_existen), Query_umedida, Query_piezas, prodNivel, descuentoFinal);

                fragmentC.updateImporte();

                mCont += 1;

            } else {
                err_login();
            }
        }
    }

    public void searchResultQuery(Integer index, String finalDescrip) {

        this.finalDescr = finalDescrip;

        this.codigoToDeta = BusqHelper.getProduct(index).Clave;

        if (checkLoginData(codigoToDeta)) {

            new asynclogin4().execute(codigoToDeta);


        } else {

            err_login();

        }
    }

    public void searchOnDetaQuery(Integer index) {

        String codigoFinal = codigoToDeta + BusqEnDetaHelper.getProductDeta(index).subClave;

        if (checkLoginData(codigoFinal)) {

            detaDialog.dismiss();

            new asynclogin().execute(codigoFinal);

        } else {

            err_login();

        }

    }

    public void searchAgain(String reSearchCode, String prodDescr) {

        codigoToDeta = reSearchCode;

        finalDescr = prodDescr;

        if (checkLoginData(codigoToDeta)) {

            new asynclogin4().execute(codigoToDeta);

        } else {

            err_login();

        }

    }

    public void showAlertBox(final String opcionNivel, final String forExisten) {

        AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
        alertbox.setMessage("Favor de ingresar la cantidad de artículos que desea:");
        alertbox.setTitle(Query_descrip_cort + " " + Query_descripcio + " " + Query_descrip_deta);

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertbox.setView(input);

        alertbox.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                try {
                    Double mExistencias = Double.valueOf(Query_existen);

                    //1=se vende por caja pero eligieron piezas, 2=se vende por caja y eligen caja, 3=vende por pieza
                    if (forExisten.equals("1")) {
                        mExistencias = mExistencias * Double.valueOf(Query_piezas);
                    }

                    Integer mCantidad = Integer.parseInt(input.getText().toString());

                    if (mExistencias == 0) {

                        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);

                        Toast toast1 = Toast.makeText(getActivity(), "Error: Existencias en 0!", Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.TOP, 10, 180);
                        toast1.show();

                        ((searchActivity) getActivity()).setCurrentItem(1, true);

                    } else if (mCantidad > mExistencias) {

                        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);

                        Toast toast1 = Toast.makeText(getActivity(), "Error: El pedido excede la cantidad en existencias!", Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.TOP, 10, 180);
                        toast1.show();

                        ((searchActivity) getActivity()).setCurrentItem(1, true);

                    } else {

                        String TabOfFragmentC = ((searchActivity) getActivity()).getTabFragmentC();

                        orderView fragmentC = (orderView) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentC);

                        String barras;

                        if (!Query_barras1.equals("--")) {
                            barras = Query_barras1;
                        } else if (!Query_barras2.equals("--")) {
                            barras = Query_barras2;
                        } else if (!Query_barras3.equals("--")) {
                            barras = Query_barras3;
                        } else {
                            barras = "--";
                        }

                        fragmentC.updateListView(Query_descrip_cort, Query_Clave, mCantidad, Double.valueOf(Query_prec_men), Double.valueOf(Query_prec_may), Double.valueOf(Query_prec_esp), Integer.parseInt(Query_cantidad1), Integer.parseInt(Query_cantidad2), Integer.parseInt(Query_cantidad3), barras, Query_sub_clave, Query_descripcio, Query_descrip_deta, Double.valueOf(Query_existen), Query_umedida, Query_piezas, opcionNivel, 0);

                        fragmentC.updateImporte();

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

    public void showAlertBox2(final String opcion) {

        AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
        alertbox.setMessage("Precios por cantidades:\n\nCantidad 1: de 1 a " + Query_cantidad1 + " -- $" + Query_prec_men + "\nCantidad 2: de " + String.valueOf(Integer.parseInt(Query_cantidad1) + 1) + " a " + Query_cantidad2 + " -- $" + Query_prec_may + "\nCantidad 3: de " + String.valueOf(Integer.parseInt(Query_cantidad2) + 1) + " a " + Query_cantidad3 + " -- $" + Query_prec_esp + "\n\nFavor de ingresar la cantidad de artículos que desea:");
        alertbox.setTitle(Query_descrip_cort + " " + Query_descripcio + " " + Query_descrip_deta);

        final EditText input = new EditText(getActivity());
        input.setInputType(InputType.TYPE_CLASS_NUMBER);
        alertbox.setView(input);

        alertbox.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {

                try {
                    Double mExistencias = Double.valueOf(Query_existen);

                    //1=se vende por caja pero eligieron piezas, 2=se vende por pieza
                    if (opcion.equals("1")) {
                        mExistencias = mExistencias * Double.valueOf(Query_piezas);
                    }

                    Integer mCantidad = Integer.parseInt(input.getText().toString());

                    if (mExistencias == 0) {

                        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);

                        Toast toast1 = Toast.makeText(getActivity(), "Error: Existencias en 0!", Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.TOP, 10, 180);
                        toast1.show();

                        ((searchActivity) getActivity()).setCurrentItem(1, true);

                    } else if (mCantidad > mExistencias) {

                        InputMethodManager mgr = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
                        mgr.hideSoftInputFromWindow(input.getWindowToken(), 0);

                        Toast toast1 = Toast.makeText(getActivity(), "Error: El pedido excede la cantidad en existencias!", Toast.LENGTH_LONG);
                        toast1.setGravity(Gravity.TOP, 10, 180);
                        toast1.show();

                        ((searchActivity) getActivity()).setCurrentItem(1, true);

                    } else {

                        String TabOfFragmentC = ((searchActivity) getActivity()).getTabFragmentC();

                        orderView fragmentC = (orderView) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentC);

                        String barras;

                        String opcion;

                        if (!Query_barras1.equals("--")) {
                            barras = Query_barras1;
                        } else if (!Query_barras2.equals("--")) {
                            barras = Query_barras2;
                        } else if (!Query_barras3.equals("--")) {
                            barras = Query_barras3;
                        } else {
                            barras = "--";
                        }

                        String TabOfFragmentB = ((searchActivity) getActivity()).getTabFragmentB();

                        viewProducts fragmentB = (viewProducts) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentB);

                        if (mCantidad <= Integer.parseInt(Query_cantidad1)) {
                            opcion = "1";
                            fragmentB.updatePrecio(Query_prec_men);
                        } else if (mCantidad <= Integer.parseInt(Query_cantidad2)) {
                            opcion = "2";
                            fragmentB.updatePrecio(Query_prec_may);
                        } else {
                            opcion = "3";
                            fragmentB.updatePrecio(Query_prec_esp);
                        }

                        fragmentC.updateListView(Query_descrip_cort, Query_Clave, mCantidad, Double.valueOf(Query_prec_men), Double.valueOf(Query_prec_may), Double.valueOf(Query_prec_esp), Integer.parseInt(Query_cantidad1), Integer.parseInt(Query_cantidad2), Integer.parseInt(Query_cantidad3), barras, Query_sub_clave, Query_descripcio, Query_descrip_deta, Double.valueOf(Query_existen), Query_umedida, Query_piezas, opcion, 0);

                        fragmentC.updateImporte();

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

    public void getOrdersFromActivity(ArrayList<Final_order> myOrders) {
        //Retrieve order from searchActivity from order search
        try {
            ((searchActivity) getActivity()).setCurrentItem(2, true);
            mCont = 0;
            try {
                cantArray.clear();
                descArray.clear();
            } catch (Exception e) {
                e.printStackTrace();
            }
            Cliente clientFromActivity = BusquedaOrden.getInstance().getCliente();
            try {
                theClientHelper.clearAll();
            }catch(Exception e2){
                e2.printStackTrace();
            }
            theClientHelper.addCart(clientFromActivity.nombre,clientFromActivity.direccion,clientFromActivity.claveCliente,clientFromActivity.nivel,clientFromActivity.descuento,clientFromActivity.telefono1,clientFromActivity.telefono2);
            for (Final_order curOrder : myOrders) {
                if (curOrder.prodSubClave.equals("--")) {
                    new asynclogin5().execute(curOrder.prodClave);
                } else {
                    new asynclogin5().execute(curOrder.prodClave + curOrder.prodSubClave);
                }
                cantArray.add((int) curOrder.prodCantidad);
                descArray.add(curOrder.descuento);
            }
            Final_order tempOrder = myOrders.get(0);
            OrderNum = tempOrder.numero;
            clienteNombre = clientFromActivity.nombre;
            codeTextView.setText(clienteNombre);
            clientBool = true;

            String TabOfFragmentC = ((searchActivity) getActivity()).getTabFragmentC();
            orderView fragmentC = (orderView) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentC);
            fragmentC.changeButtonToAct();
            fragmentC.activateActFlag();

        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public Cliente getCliente() {
        List<Cliente> clientList = theClientHelper.getCartCliente();
        Cliente clientFromActivity = clientList.get(0);
        return clientFromActivity;
    }

    public void setClientBool() {
        clientBool = false;
    }

    public String getOrderNum() {
        return OrderNum;
    }

    public void searchSubstring(String substring) {
        codeEditText.setText(substring);
        try {
            new asynclogin3().execute(substring);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}