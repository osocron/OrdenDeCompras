package com.patumecha.osocron.ordendecompras;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.*;
import org.apache.http.NameValuePair;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;


public class BusquedaClientes extends Activity {

    private static BusquedaClientes myBusquedaClientes;

    AutoCompleteTextView clientesAutoComplete;
    TextView titleClientTextView;
    Button addClient;

    private BusqClientBaseAdapter myBaseAdapter;

    Httppostaux post;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_busqueda_clientes);

        myBusquedaClientes = this;

        post = new Httppostaux();

        clientesAutoComplete = (AutoCompleteTextView)findViewById(R.id.clientesAutoCompleteTextView);
        titleClientTextView = (TextView)findViewById(R.id.titleClienteTextView);
        addClient = (Button)findViewById(R.id.addClientButton);

        List<Cliente> mClientList = BusqClientHelper.getCartCliente();

        myBaseAdapter = new BusqClientBaseAdapter(mClientList,getLayoutInflater());

        clientesAutoComplete.setAdapter(myBaseAdapter);
        clientesAutoComplete.setThreshold(2);

        clientesAutoComplete.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                try {
                    String mcodigo = clientesAutoComplete.getText().toString();
                    if (checkLoginData(mcodigo)) {
                        new asynclogin().execute(mcodigo);
                    }
                }catch(Exception e){
                    e.printStackTrace();
                }
            }
        });

        addClient.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(final View v) {
                AlertDialog.Builder dialog = new AlertDialog.Builder(v.getContext());
                dialog.setTitle("Ingresar datos");

                Context context = v.getContext();
                LinearLayout layout = new LinearLayout(context);
                layout.setOrientation(LinearLayout.VERTICAL);

                final TextView nombreTextView = new TextView(context);
                nombreTextView.setText("Nombre:");
                layout.addView(nombreTextView);

                final EditText nombreEditText = new EditText(context);
                nombreEditText.setHint("Nombre");
                layout.addView(nombreEditText);

                final TextView direccionTextView = new TextView(context);
                direccionTextView.setText("Direccion:");
                layout.addView(direccionTextView);

                final EditText direccionEditText = new EditText(context);
                direccionEditText.setHint("Direccion");
                layout.addView(direccionEditText);

                final TextView tel1TextView = new TextView(context);
                tel1TextView.setText("Telefono1:");
                layout.addView(tel1TextView);

                final EditText tel1EditText = new EditText(context);
                tel1EditText.setHint("Telefono");
                tel1EditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                layout.addView(tel1EditText);

                final TextView tel2TextView = new TextView(context);
                tel2TextView.setText("Telefono2:");
                layout.addView(tel2TextView);

                final EditText tel2EditText = new EditText(context);
                tel2EditText.setHint("Telefono");
                tel2EditText.setInputType(InputType.TYPE_CLASS_NUMBER);
                layout.addView(tel2EditText);

                dialog.setView(layout);

                dialog.setPositiveButton("Agregar", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Double desc = 0.0;
                        Intent myIntent = new Intent(v.getContext(),searchActivity.class);
                        myIntent.putExtra("nombreCliente",nombreEditText.getText().toString());
                        myIntent.putExtra("direccionCliente",direccionEditText.getText().toString());
                        myIntent.putExtra("claveCliente","01000001");
                        myIntent.putExtra("nivel","1");
                        myIntent.putExtra("descuento",desc);
                        myIntent.putExtra("telefono1",tel1EditText.getText().toString());
                        myIntent.putExtra("telefono2",tel2EditText.getText().toString());
                        v.getContext().startActivity(myIntent);
                    }
                })
                        .setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                dialog.show();
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
                myBaseAdapter.notifyDataSetChanged();
            }
        }
    }

    public boolean getResult(String codigo) {

        ArrayList<NameValuePair> postparameters2send = new ArrayList<NameValuePair>();

        postparameters2send.add(new BasicNameValuePair("codigo", codigo));

        final JSONArray jdata = post.getserverdata(postparameters2send, GlobalVars.INET_ADDRESS+"search_clients.php");

        if (jdata != null && jdata.length() > 0) {

            final JSONObject[] json_data = new JSONObject[1];

            runOnUiThread(new Runnable() {
                @Override
                public void run() {

                    BusqClientHelper.clearAll();

                    for(int i=0;i<jdata.length();i++)
                    {
                        try {
                            json_data[0] =jdata.getJSONObject(i);
                        } catch (JSONException e) {
                            e.printStackTrace();
                        }
                        try {
                            BusqClientHelper.addCart(json_data[0].getString("nombre"), json_data[0].getString("direccion"), json_data[0].getString("clave"), json_data[0].getString("nivel"), Double.valueOf(json_data[0].getString("descuento")), json_data[0].getString("telefono1"), json_data[0].getString("telefono2"));
                            myBaseAdapter.notifyDataSetChanged();
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


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.busqueda_clientes, menu);
        return true;
    }


    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {
            case R.id.action_settings:
                // Busqueda de Pedidos
                searchActivity.getInstance().searchOrders();
                finish();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    public static BusquedaClientes getInstance(){
        return  myBusquedaClientes;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            try {
                searchActivity.getInstance().finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            try {
                BusquedaOrden.getInstance().finish();
            } catch (Exception e) {
                e.printStackTrace();
            }
            this.finish();
        }
        return super.onKeyDown(keyCode, event);
    }

}
