package com.patumecha.osocron.ordendecompras;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.SystemClock;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.AutoCompleteTextView;
import android.widget.ListView;
import android.widget.TextView;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BusquedaOrden extends Activity {

    private static BusquedaOrden myBusquedaOrden;

    private String nombreCliente = "";
    private String direccion = "";
    private String telefono1 = "";
    private String telefono2 = "";

    private Cliente clientToActivity;

    AutoCompleteTextView ordenesAutoComplete;
    TextView ordenesTextView;

    orderHelper myOrderHelper = new orderHelper();
    Final_order_helper myFinalOrderHelper = new Final_order_helper();

    private BusqOrdenClientBaseAdapter myClientBaseAdapter;
    private BusqClientOrdersBaseAdapter myClientOrdersBaseAdapter;

    Httppostaux postAux;

    private ProgressDialog pDialog;
    private Dialog detaDialog;

    public BusquedaOrden() {
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_orden);

        myBusquedaOrden = this;

        postAux = new Httppostaux();

        ordenesAutoComplete = (AutoCompleteTextView)findViewById(R.id.ordenesAutoCompleteTextView);
        ordenesTextView = (TextView)findViewById(R.id.busqOrdenTextView);

        List<Cliente> mClientList = busqClientsInOrderAct.getCartCliente();
        List<Order> mOredrList = myOrderHelper.getCartOrder();

        myClientBaseAdapter = new BusqOrdenClientBaseAdapter(mClientList,getLayoutInflater());
        myClientOrdersBaseAdapter = new BusqClientOrdersBaseAdapter(mOredrList,getLayoutInflater());

        ordenesAutoComplete.setAdapter(myClientBaseAdapter);
        ordenesAutoComplete.setThreshold(1);

        ordenesAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String mcodigo = ordenesAutoComplete.getText().toString();
                    if (checkLoginData(mcodigo)) {
                        new asynclogin().execute(mcodigo);

                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

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

        protected void onPreExecute() {

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

            Log.e("onPostExecute", "" + result);

            if (result.equals("ok")) {
                myClientBaseAdapter.notifyDataSetChanged();
            }
        }
    }

    public boolean getResult(String codigo) {

        ArrayList<NameValuePair> postparameters2send = new ArrayList<NameValuePair>();

        postparameters2send.add(new BasicNameValuePair("codigo", codigo));

        final JSONArray jdata = postAux.getserverdata(postparameters2send, GlobalVars.INET_ADDRESS+"search_order.php");

        if (jdata != null && jdata.length() > 0) {

            final JSONObject[] json_data = new JSONObject[1];

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    busqClientsInOrderAct.clearAll();

                    for(int i=0;i<jdata.length();i++)
                    {
                        try {
                            json_data[0] =jdata.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            busqClientsInOrderAct.addCart(json_data[0].getString("nombre"), json_data[0].getString("direccion"), json_data[0].getString("cliente"), "", 0, json_data[0].getString("telefono1"), json_data[0].getString("telefono2"));
                            myClientBaseAdapter.notifyDataSetChanged();
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                    }
                }
            });

            return true;


        } else {
            Log.e("JSON", "Error");
            return false;
        }

    }

    class asynclogin2 extends AsyncTask<String, String, String> {

        String code;

        protected void onPreExecute() {
            pDialog = new ProgressDialog(BusquedaOrden.this);
            pDialog.setMessage("Buscando ordenes anteriores...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
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
            pDialog.dismiss();
            Log.e("onPostExecute", "" + result);

            if (result.equals("ok")) {

                //Create a Dialog
                myClientOrdersBaseAdapter.notifyDataSetChanged();

                detaDialog = new Dialog(BusquedaOrden.this);
                detaDialog.setTitle("Ordenes encontradas:");
                detaDialog.setContentView(R.layout.deta_prod_list);

                ListView detaListView = (ListView)detaDialog.findViewById(R.id.detaListView);

                detaListView.setAdapter(myClientOrdersBaseAdapter);

                detaListView.setItemsCanFocus(true);

                detaDialog.show();


            }
        }
    }

    public boolean getResult2(String codigo) {

        ArrayList<NameValuePair> postparameters2send = new ArrayList<NameValuePair>();

        postparameters2send.add(new BasicNameValuePair("codigo", codigo));

        JSONArray jdata = postAux.getserverdata(postparameters2send, GlobalVars.INET_ADDRESS+"search_order.php");

        SystemClock.sleep(120);

        if (jdata != null && jdata.length() >= 1) {

            JSONObject json_data;

            try {

                try {
                    myOrderHelper.clearAll();
                }catch(Exception e){
                    e.printStackTrace();
                }

                for(int i=0;i<jdata.length();i++)
                {
                    json_data=jdata.getJSONObject(i);
                    myOrderHelper.addCart(json_data.getString("numero"),json_data.getString("fecha"),json_data.getString("serie"));
                }

            } catch (JSONException e) {
                Log.e("log_tag","Error parsing data"+e.toString());
            }
            return true;


        } else {
            Log.e("JSON", "Error");
            return false;
        }

    }

    class asynclogin3 extends AsyncTask<String, String, String> {

        String code;

        protected void onPreExecute() {

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

            Log.e("onPostExecute", "" + result);

            if (result.equals("ok")) {
                sendTheArray();
            }
        }
    }

    public boolean getResult3(String codigo) {

        ArrayList<NameValuePair> postparameters2send = new ArrayList<NameValuePair>();

        postparameters2send.add(new BasicNameValuePair("codigo", codigo));

        JSONArray jdata = postAux.getserverdata(postparameters2send, GlobalVars.INET_ADDRESS+"get_order.php");


        if (jdata != null && jdata.length() > 0) {

            JSONObject json_data;

            try {
                try {
                    myFinalOrderHelper.clearAll();
                }catch(Exception e){
                    e.printStackTrace();
                }
                for(int i=0;i<jdata.length();i++)
                {
                    json_data=jdata.getJSONObject(i);
                    myFinalOrderHelper.addCart(json_data.getString("empresa"), json_data.getString("serie"), json_data.getString("numero"),json_data.getString("cliente"),json_data.getString("vendedor"), json_data.getString("fecha"), json_data.getString("clave"), json_data.getString("subclave"), json_data.getString("umedida"), json_data.getString("nivel"), Double.valueOf(json_data.getString("precio")), Double.valueOf(json_data.getString("cantidad")), Double.valueOf(json_data.getString("descuento1")), Double.valueOf(json_data.getString("importe")), Integer.valueOf(json_data.getString("orden")));
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

        protected void onPreExecute() {
            pDialog = new ProgressDialog(BusquedaOrden.this);
            pDialog.setMessage("Buscando cliente...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }

        @Override
        protected String doInBackground(String... params) {

            code = params[0];

            if (getResult4(code)) {
                return "ok";
            } else {
                return "err";
            }
        }

        protected void onPostExecute(String result) {
            pDialog.dismiss();
            Log.e("onPostExecute", "" + result);

            if (result.equals("ok")) {
                new asynclogin2().execute(nombreCliente);
            }
        }
    }

    public boolean getResult4(String codigo) {

        ArrayList<NameValuePair> postparameters2send = new ArrayList<NameValuePair>();

        postparameters2send.add(new BasicNameValuePair("codigo", codigo));

        JSONArray jdata = postAux.getserverdata(postparameters2send, GlobalVars.INET_ADDRESS+"search_clientes_codigo.php");


        if (jdata != null && jdata.length() > 0) {

            JSONObject json_data;

            try {

                json_data=jdata.getJSONObject(0);
                if(json_data.getString("clave").equals("01000001")){
                    clientToActivity = new Cliente(nombreCliente,direccion,json_data.getString("clave"),json_data.getString("nivel"),Double.valueOf(json_data.getString("descuento")),telefono1,telefono2);
                }else {
                    clientToActivity = new Cliente(json_data.getString("nombre"), json_data.getString("direccion"), json_data.getString("clave"), json_data.getString("nivel"), Double.valueOf(json_data.getString("descuento")), json_data.getString("telefono1"), json_data.getString("telefono2"));
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

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.busqueda_orden, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    public static BusquedaOrden getInstance(){
        return  myBusquedaOrden;
    }

    public void searchOrder(String nombreCliente, String clienteClave, String direccion, String telefono1, String telefono2){
        //Search the order
        new asynclogin4().execute(clienteClave);
        this.nombreCliente = nombreCliente;
        this.direccion = direccion;
        this.telefono1 = telefono1;
        this.telefono2 = telefono2;
    }

    public void getOrders(String numero){
        //Get the orders
        detaDialog.dismiss();
        new asynclogin3().execute(numero);
    }

    public void sendTheArray(){
        ArrayList<Final_order> arrayToSend = myFinalOrderHelper.getCartOrder();
        Intent myIntent = getIntent();
        String msg = myIntent.getStringExtra("search");
        if(msg.contentEquals("orders")) {
            Bundle bundle = new Bundle();
            bundle.putParcelableArrayList("orders", arrayToSend);
            myIntent.putExtras(bundle);
            myIntent.putExtra("search","ok");
            setResult(RESULT_OK,myIntent);
            finish();
        }
    }

    public Cliente getCliente(){
        return clientToActivity;
    }
}

