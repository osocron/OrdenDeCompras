package com.patumecha.osocron.ordendecompras;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.ListFragment;
import android.app.ProgressDialog;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Looper;
import android.os.SystemClock;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Toast;
import com.zebra.sdk.comm.BluetoothConnection;
import com.zebra.sdk.comm.Connection;
import com.zebra.sdk.comm.ConnectionException;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * Created by osocron on 19/07/14.
 */
public class orderView extends ListFragment {

    private List<Product> mProductList;
    private customBaseAdapter myBaseAdapter;
    private EditText importeEditText;
    private ProgressDialog pDialog;
    private String orderNumb,IVA;
    Button endOrderButton;
    BluetoothAdapter bluetoothAdapter;
    private static Boolean actFlag= false;
    Httppostaux post;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.order_view, container, false);

        String myTag = getTag();

        ((searchActivity)getActivity()).setTabFragmentC(myTag);

        post = new Httppostaux();

        Button importeButton = (Button) rootView.findViewById(R.id.importeButton);
        endOrderButton = (Button) rootView.findViewById(R.id.endOrdenButton);
        importeEditText = (EditText)rootView.findViewById(R.id.importeEditText);

        mProductList = ordenComprasHelper.getCart();

        bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();

        if(actFlag){
            changeButtonToAct();
        }

        importeButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                ((searchActivity) getActivity()).setCurrentItem(0, true);
            }
        });

        endOrderButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if(endOrderButton.getText().equals("Actualizar Pedido")){
                    //Udate record from here on
                    AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
                    alertbox.setMessage("¿Que desea hacer con la orden?");
                    alertbox.setTitle("Atención!");

                    alertbox.setPositiveButton("Actualizar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            printTicket("2");
                        }
                    });
                    //Delete Record
                    alertbox.setNegativeButton("Borrar", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {

                            AlertDialog.Builder alertbox2 = new AlertDialog.Builder(getActivity());
                            alertbox2.setMessage("¿Está seguro que desea borrar la orden?");
                            alertbox2.setTitle("Atención!");

                            alertbox2.setPositiveButton("Borrar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    try {
                                        new HttpAsyncTask().execute(GlobalVars.INET_ADDRESS+"deleteRecord.php");
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    changeButtonToNorm();
                                }
                            });

                            alertbox2.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {

                                }
                            });

                            alertbox2.show();

                        }
                    });

                    alertbox.show();

                }else {
                    AlertDialog.Builder alertbox = new AlertDialog.Builder(getActivity());
                    alertbox.setMessage("¿Esta seguro de haber terminado de modificar la orden?");
                    alertbox.setTitle("Atención!");

                    alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            //Enviar el pedido
                            printTicket("1");
                        }
                    });

                    alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                        public void onClick(DialogInterface arg0, int arg1) {
                            // Nothing will happen when clicked on button
                        }
                    });

                    alertbox.show();
                }

            }
        });

        return rootView;
    }

    private String CheckBlueToothState(){
        if (bluetoothAdapter == null){
            //stateBluetooth.setText("Bluetooth NOT support");
            return "0";
        }else{
            if (bluetoothAdapter.isEnabled()){
                if(bluetoothAdapter.isDiscovering()){
                    //stateBluetooth.setText("Bluetooth is currently in device discovery process.");
                    return "1";
                }else{
                    //stateBluetooth.setText("Bluetooth is Enabled.");
                    //btnListPairedDevices.setEnabled(true);
                    return "2";
                }
            }else{
                //stateBluetooth.setText("Bluetooth is NOT Enabled!");
                return "4";
            }
        }
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState){

        super.onActivityCreated(savedInstanceState);

        ListView listViewCatalog = getListView();

        myBaseAdapter = new customBaseAdapter(mProductList,getActivity().getLayoutInflater());

        setListAdapter(myBaseAdapter);

        listViewCatalog.setItemsCanFocus(true);


    }

    private class HttpAsyncTask extends AsyncTask<String, Void, String> {
        protected void onPreExecute() {
            pDialog = new ProgressDialog(getActivity());
            pDialog.setMessage("Enviando...");
            pDialog.setIndeterminate(false);
            pDialog.setCancelable(true);
            pDialog.show();
        }
        @Override
        protected String doInBackground(String... urls) {

            String result;

            List<Product> mProdListToSend = ordenComprasHelper.getCart();

            result =  POST(urls[0], mProdListToSend);

            return result;
        }
        // onPostExecute displays the results of the AsyncTask.
        @Override
        protected void onPostExecute(String result) {
            pDialog.dismiss();
            Toast toast1 = Toast.makeText(getActivity(),result, Toast.LENGTH_LONG);
            toast1.show();
            if(actFlag){
                deactivateActFlag();
            }
            searchActivity.getInstance().changeClient();

        }
    }

    public String POST(String url, List<Product> productListToSend){
        InputStream inputStream;
        String result = "";
        try {

            SystemClock.sleep(50);
            // 1. create HttpClient
            HttpClient httpclient = new DefaultHttpClient();

            // 2. make POST request to the given URL
            HttpPost httpPost = new HttpPost(url);

            String json;

            // 3. build jsonArray
            JSONArray ja = new JSONArray();

            String vendSerie = searchActivity.getInstance().getVendSerie();

            String TabOfFragmentA = searchActivity.getInstance().getTabFragmentA();
            searchProducts fragmentA = (searchProducts)searchActivity.getInstance().getFragmentManager().findFragmentByTag(TabOfFragmentA);

            Cliente clientFromActivity = fragmentA.getCliente();

            String orderNum = fragmentA.getOrderNum();

            for(Product curProd : productListToSend) {

                Double curPrecio = null;
                // build jsonObject
                JSONObject jsonObject = new JSONObject();

                jsonObject.put("empresa", "0014");
                jsonObject.put("serie", vendSerie);
                jsonObject.put("numero",orderNum);
                jsonObject.put("cliente", clientFromActivity.claveCliente);
                jsonObject.put("vendedor", "0001");
                jsonObject.put("clave", curProd.claveProducto);
                jsonObject.put("subclave", curProd.subClaveProducto);
                jsonObject.put("uMedida", curProd.uMedida);
                jsonObject.put("nivel", clientFromActivity.nivel);


                if(curProd.nivel.equals("1")) {
                    curPrecio = curProd.precioMen;
                }
                else if(curProd.nivel.equals("2")){
                    curPrecio = curProd.precioMay;
                }
                else if(curProd.nivel.equals("3")){
                    curPrecio = curProd.precioEsp;
                }
                jsonObject.put("precio", curPrecio);
                jsonObject.put("cantidad", curProd.cantidad);
                jsonObject.put("descuento1", (clientFromActivity.descuento + curProd.desc));

                double curDesc = (((curProd.desc+ clientFromActivity.descuento)*(curProd.cantidad*curPrecio))/100);
                Double importe = ((curProd.cantidad * curPrecio)-curDesc);
                BigDecimal BigImporte = new BigDecimal(importe).setScale(2, BigDecimal.ROUND_HALF_UP);
                jsonObject.put("importe", String.valueOf(BigImporte));

                String nombre = replaceLetters(clientFromActivity.nombre);
                jsonObject.put("nombre",nombre);
                String direccion = replaceLetters(clientFromActivity.direccion);
                jsonObject.put("direccion",direccion);

                jsonObject.put("telefono1", clientFromActivity.telefono1);
                jsonObject.put("telefono2", clientFromActivity.telefono2);

                ja.put(jsonObject);
            }


            // 4. convert JSONObject to JSON to String
            json = ja.toString();

            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("json", json));
            try {
                httpPost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
            } catch (UnsupportedEncodingException e1) {
                // TODO Auto-generated catch block
                e1.printStackTrace();
            }

            // 8. Execute POST request to the given URL
            HttpResponse httpResponse = httpclient.execute(httpPost);

            // 9. receive response as inputStream
            inputStream = httpResponse.getEntity().getContent();

            // 10. convert inputstream to string
            if(inputStream != null)
                result = convertInputStreamToString(inputStream);

            else
                result = "Did not work!";

        } catch (Exception e) {
            e.printStackTrace();
        }

        // 11. return result
        return result;
    }

    private static String convertInputStreamToString(InputStream inputStream) throws IOException {
        BufferedReader bufferedReader = new BufferedReader( new InputStreamReader(inputStream));
        String line;
        String result = "";
        while((line = bufferedReader.readLine()) != null)
            result += line;

        inputStream.close();

        return result;

    }

    class asynclogin extends AsyncTask<String, String, String> {

        String code;

        protected void onPreExecute() {

        }

        @Override
        protected String doInBackground(String... params) {

            code = params[0];

            return getResult(code);
        }

        protected void onPostExecute(String result) {

            Log.e("onPostExecute", "" + result);

            if(result.equals("numero")) {
                orderNumb = String.format("%6s", orderNumb).replace(' ', ' ');
            }

        }
    }

    public String getResult(String codigo) {

        //Mysql creation of a table :) INSERT INTO descrprod (clave,descripcio) SELECT clave, CONCAT(descrgruma,descripcio) FROM articulo;

        String result = "";

        ArrayList<NameValuePair> postparameters2send = new ArrayList<NameValuePair>();

        postparameters2send.add(new BasicNameValuePair("codigo", codigo));

        JSONArray jdata = null;

        if(codigo.equals("numero")) {
            jdata = post.getserverdata(postparameters2send,GlobalVars.INET_ADDRESS+"get_num_order.php");
        }
        else if(codigo.equals("IVA")){
            jdata = post.getserverdata(postparameters2send,GlobalVars.INET_ADDRESS+"getIVA.php");
        }

        SystemClock.sleep(50);

        if (jdata != null && jdata.length() > 0) {

            JSONObject json_data;

            try {

                json_data = jdata.getJSONObject(0);
                if(codigo.equals("nuero")) {
                    orderNumb = json_data.getString("numero");
                    result = "numero";
                }
                else if(codigo.equals("IVA")){
                    IVA = json_data.getString("IVA");
                    result = "IVA";
                }

                Log.e("loginstatus", "Numero Orden = " + orderNumb);

            } catch (JSONException e) {
                e.printStackTrace();
            }
            return result;

        } else {
            Log.e("JSON", "Error");
            return "error";
        }

    }

    public void updateListView(String nombreProducto, String claveProducto, Integer cantidad, Double precio_men, Double precio_may, Double precio_esp, Integer cantidad1, Integer cantidad2, Integer cantidad3, String barras1,String subClaveProducto, String descripProducto, String descrDetaProducto, double existenciasProducto, String uMedida, String piezas, String nivel, double desc){
         ordenComprasHelper.addCart(nombreProducto, claveProducto, cantidad, precio_men, precio_may, precio_esp, cantidad1, cantidad2, cantidad3, barras1,subClaveProducto,descripProducto,descrDetaProducto,existenciasProducto, uMedida, piezas, nivel, desc);
         myBaseAdapter.notifyDataSetChanged();
    }

    public void updateListFromRemove(Integer index){
        ordenComprasHelper.removeFromCart(index);
        myBaseAdapter.notifyDataSetChanged();
    }

    public void updateCantidad(Integer index, Integer cantidad){
        ordenComprasHelper.changeCantidad(index,cantidad);
        myBaseAdapter.notifyDataSetChanged();
    }

    public void updateImporte(){
        Double curImporte = ordenComprasHelper.getImporte();
        BigDecimal BigImporte = new BigDecimal(curImporte).setScale(2,BigDecimal.ROUND_HALF_UP);
        importeEditText.setText("$"+ BigImporte.toString());
    }

    public void updateNivel(Integer index, String nivel, String precio){
        ordenComprasHelper.changeNivel(index,nivel);
        myBaseAdapter.notifyDataSetChanged();
        String TabOfFragmentB = ((searchActivity) getActivity()).getTabFragmentB();
        viewProducts fragmentB = (viewProducts) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentB);
        fragmentB.updatePrecio(precio);
    }

    public void updateDesc(Integer index, double desc){
        ordenComprasHelper.changeDesc(index,desc);
        myBaseAdapter.notifyDataSetChanged();
        updateImporte();
    }

    public Boolean isOnTheList(String nombreProducto,String descripProducto, String descrDetaProducto){
        return ordenComprasHelper.isOnList(nombreProducto, descripProducto, descrDetaProducto);
    }

    public void clearOrder(){
        ordenComprasHelper.clearAll();
        updateImporte();
        String TabOfFragmentB = ((searchActivity) getActivity()).getTabFragmentB();
        viewProducts fragmentB = (viewProducts) getActivity().getFragmentManager().findFragmentByTag(TabOfFragmentB);
        fragmentB.clearFields();
        myBaseAdapter.notifyDataSetChanged();
    }

    public String replaceLetters(String word){
        word = word.toUpperCase();
        word = word.replaceAll("Ñ","N");
        word = word.replaceAll("Á","A");
        word = word.replaceAll("É","E");
        word = word.replaceAll("Í","I");
        word = word.replaceAll("Ó","O");
        word = word.replaceAll("Ú","U");
        word = word.replaceAll("Ü","U");
        return word;
    }

    public void changeButtonToAct(){
        endOrderButton.setText("Actualizar Pedido");
    }

    public void changeButtonToNorm(){
        endOrderButton.setText("Finalizar Pedido");
    }

    public void activateActFlag(){
        actFlag = true;
    }

    public void deactivateActFlag(){
        actFlag=false;
    }

    private String getBTMajorDeviceClass(int major){
        switch(major){
            case BluetoothClass.Device.Major.AUDIO_VIDEO:
                return "AUDIO_VIDEO";
            case BluetoothClass.Device.Major.COMPUTER:
                return "COMPUTER";
            case BluetoothClass.Device.Major.HEALTH:
                return "HEALTH";
            case BluetoothClass.Device.Major.IMAGING:
                return "PRINTER";
            case BluetoothClass.Device.Major.MISC:
                return "MISC";
            case BluetoothClass.Device.Major.NETWORKING:
                return "NETWORKING";
            case BluetoothClass.Device.Major.PERIPHERAL:
                return "PERIPHERAL";
            case BluetoothClass.Device.Major.PHONE:
                return "PHONE";
            case BluetoothClass.Device.Major.TOY:
                return "TOY";
            case BluetoothClass.Device.Major.UNCATEGORIZED:
                return "UNCATEGORIZED";
            case BluetoothClass.Device.Major.WEARABLE:
                return "AUDIO_VIDEO";
            default: return "unknown!";
        }
    }

    public void printTicket(final String task) {

        new asynclogin().execute("numero");
        new asynclogin().execute("IVA");

        final ArrayAdapter<String> btArrayAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_list_item_1);
        final ArrayList<btDevice> btDeviceArray = new ArrayList<btDevice>();

        BluetoothAdapter mBluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
        Set<BluetoothDevice> pairedDevices = mBluetoothAdapter.getBondedDevices();

        String btState = CheckBlueToothState();

        if (pairedDevices.size() > 0 && btState.equals("2")) {

            for (BluetoothDevice device : pairedDevices) {

                String deviceBTName = device.getName();
                String deviceBTAddress = device.getAddress();
                String deviceBTMajorClass = getBTMajorDeviceClass(device.getBluetoothClass().getMajorDeviceClass());
                btDevice curDevice = new btDevice(deviceBTName, deviceBTMajorClass, deviceBTAddress);
                btDeviceArray.add(curDevice);
                btArrayAdapter.add(curDevice.name + "\n" + curDevice.MajorClass);

            }


            final Dialog btDialog = new Dialog(getActivity());
            btDialog.setTitle("Dispositivos Bluetooth:");
            btDialog.setContentView(R.layout.bt_layout_list);

            ListView btListView = (ListView) btDialog.findViewById(R.id.btListView);
            btListView.setAdapter(btArrayAdapter);
            btListView.setItemsCanFocus(true);
            btDialog.show();

            btListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                @Override
                public void onItemClick(AdapterView<?> parent, View view, int position, long id) {

                    btDevice curDevice = btDeviceArray.get(position);
                    String address = curDevice.address;

                    String TabOfFragmentA = searchActivity.getInstance().getTabFragmentA();
                    searchProducts fragmentA = (searchProducts)searchActivity.getInstance().getFragmentManager().findFragmentByTag(TabOfFragmentA);
                    String orderNum = fragmentA.getOrderNum();

                    btDialog.dismiss();

                    if (task.equals("1")) {
                        try {
                            List<Product> toBtList = ordenComprasHelper.getCart();
                            sendCpclOverBluetooth(address, orderNumb, IVA, toBtList);
                            SystemClock.sleep(4000);
                            new HttpAsyncTask().execute(GlobalVars.INET_ADDRESS+"orden_compras.php");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (task.equals("2")) {
                        try {
                            List<Product> toBtList = ordenComprasHelper.getCart();
                            sendCpclOverBluetooth(address, orderNum, IVA, toBtList);
                            SystemClock.sleep(4000);
                            new HttpAsyncTask().execute(GlobalVars.INET_ADDRESS+"updateRecord.php");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        changeButtonToNorm();
                    }
                }
            });
        }else{
            Toast myToast = Toast.makeText(getActivity(),"Favor de verificar que la conexión Bluetooth este correctamente configurada!",Toast.LENGTH_LONG);
            myToast.show();
        }
    }

    public static void sendCpclOverBluetooth(final String theBtMacAddress, final String orderNum,final String mIVA, final List<Product> mProdListToSend) {

        new Thread(new Runnable() {
            public void run() {
                //try {

                    // Instantiate connection for given Bluetooth&reg; MAC Address.
                    Connection thePrinterConn = new BluetoothConnection(theBtMacAddress);

                    // Initialize
                    Looper.prepare();
                    // Open the connection - physical connection is established here.
                try {
                    thePrinterConn.open();
                } catch (ConnectionException e) {
                    Toast myToast = Toast.makeText(searchActivity.getInstance(),"No se pudo establecer conexión con la impresora",Toast.LENGTH_LONG);
                    myToast.show();
                    e.printStackTrace();
                }

                String cpclData;

                    cpclData = "! UF header.fmt\r\n"
                                + orderNum + "\r\n";

                    String TabOfFragmentA = searchActivity.getInstance().getTabFragmentA();
                    searchProducts fragmentA = (searchProducts)searchActivity.getInstance().getFragmentManager().findFragmentByTag(TabOfFragmentA);
                    Cliente clientFromActivity = fragmentA.getCliente();

                    for(Product curProd : mProdListToSend) {

                        Double curPrecio = null;
                        String mClave = curProd.claveProducto;
                        String mDescrProd = curProd.descripProducto;
                        String mNombProd = curProd.nombreProducto;
                        if(mNombProd.length() > 12){
                            mNombProd = mNombProd.substring(0,12);
                        }
                        if(mDescrProd.length() > 12){
                            mDescrProd = mDescrProd.substring(0,12);
                        }
                        String mDescrip = mNombProd+mDescrProd+" "+curProd.descrDetaProducto;
                        if(mDescrip.length() > 30) {
                            mDescrip = mDescrip.substring(0, 30);
                        }
                        if(curProd.nivel.equals("1")) {
                            curPrecio = curProd.precioMen;
                        }
                        else if(curProd.nivel.equals("2")){
                            curPrecio = curProd.precioMay;
                        }
                        else if(curProd.nivel.equals("3")){
                            curPrecio = curProd.precioEsp;
                        }
                        String mCantidad = String.valueOf(curProd.cantidad);
                        String mDescuento = String.valueOf((clientFromActivity.descuento + curProd.desc));
                        String mPrecio = String.valueOf(curPrecio);
                        mPrecio = String.format("%12s",mPrecio).replace(' ',' ');
                        mDescuento = String.format("%10s",mDescuento).replace(' ',' ');
                        mCantidad = String.format("%10s",mCantidad).replace(' ',' ');

                        double curDesc = (((curProd.desc+ clientFromActivity.descuento)*(curProd.cantidad*curPrecio))/100);
                        Double importe = ((curProd.cantidad * curPrecio)-curDesc);
                        BigDecimal BigImporte = new BigDecimal(importe).setScale(2, BigDecimal.ROUND_HALF_UP);
                        String mImporte = String.valueOf (BigImporte);
                        mImporte = String.format("%12s",mImporte).replace(' ',' ');

                        cpclData = cpclData+"! U1 SETLP 7 0 12\r\n" +
                                "    " +mClave+"  "+mDescrip+"\r\n" +
                                "! U1 SETLP 7 0 20\r\n" +
                                mPrecio+mDescuento+mCantidad+mImporte+"\r\n" +
                                "\r\n";
                    }

                    Double curImporte = ordenComprasHelper.getImporte();
                    BigDecimal BigImporte = new BigDecimal(curImporte).setScale(2,BigDecimal.ROUND_HALF_UP);
                    Double iva = Double.valueOf(mIVA);
                    Double total = curImporte + (curImporte * (iva/100));
                    Double ivaAlone = curImporte * (iva/100);
                    BigDecimal BigIVA= new BigDecimal(ivaAlone).setScale(2, BigDecimal.ROUND_HALF_UP);
                    BigDecimal BigTotal = new BigDecimal(total).setScale(2, BigDecimal.ROUND_HALF_UP);
                    String mImportes = String.valueOf(BigImporte);
                    cpclData = cpclData+"! U1 SETLP 7 0 35\r\n" +
                            "                  SUBTOTAL        :   "+mImportes+"\r\n" +
                            "                  IVA("+String.valueOf(iva)+"%)      :   "+String.valueOf(BigIVA)+"\r\n" +
                            "                  TOTAL           :   "+String.valueOf(BigTotal)+"\r\n" +
                            "! UF bottom.fmt\r\n" +
                            "! UF header.fmt\r\n"
                            + orderNum + "\r\n";

                for(Product curProd : mProdListToSend) {

                    Double curPrecio = null;
                    String mClave = curProd.claveProducto;
                    String mDescrProd = curProd.descripProducto;
                    String mNombProd = curProd.nombreProducto;
                    if(mNombProd.length() > 12){
                        mNombProd = mNombProd.substring(0,12);
                    }
                    if(mDescrProd.length() > 12){
                        mDescrProd = mDescrProd.substring(0,12);
                    }
                    String mDescrip = mNombProd+mDescrProd+" "+curProd.descrDetaProducto;
                    if(mDescrip.length() > 30) {
                        mDescrip = mDescrip.substring(0, 30);
                    }
                    if(curProd.nivel.equals("1")) {
                        curPrecio = curProd.precioMen;
                    }
                    else if(curProd.nivel.equals("2")){
                        curPrecio = curProd.precioMay;
                    }
                    else if(curProd.nivel.equals("3")){
                        curPrecio = curProd.precioEsp;
                    }
                    String mCantidad = String.valueOf(curProd.cantidad);
                    String mDescuento = String.valueOf((clientFromActivity.descuento + curProd.desc));
                    String mPrecio = String.valueOf(curPrecio);
                    mPrecio = String.format("%12s",mPrecio).replace(' ',' ');
                    mDescuento = String.format("%10s",mDescuento).replace(' ',' ');
                    mCantidad = String.format("%10s",mCantidad).replace(' ',' ');

                    double curDesc = (((curProd.desc+ clientFromActivity.descuento)*(curProd.cantidad*curPrecio))/100);
                    Double importe = ((curProd.cantidad * curPrecio)-curDesc);
                    BigDecimal BigImportes = new BigDecimal(importe).setScale(2, BigDecimal.ROUND_HALF_UP);
                    String mImporte = String.valueOf (BigImportes);
                    mImporte = String.format("%12s",mImporte).replace(' ',' ');


                    cpclData = cpclData+"! U1 SETLP 7 0 12\r\n" +
                            "    " +mClave+"  "+mDescrip+"\r\n" +
                            "! U1 SETLP 7 0 20\r\n" +
                            mPrecio+mDescuento+mCantidad+mImporte+"\r\n" +
                            "\r\n";
                }

                cpclData = cpclData+"! U1 SETLP 7 0 35\r\n" +
                        "                  SUBTOTAL        :   "+mImportes+"\r\n" +
                        "                  IVA("+String.valueOf(iva)+"%)      :   "+String.valueOf(BigIVA)+"\r\n" +
                        "                  TOTAL           :   "+String.valueOf(BigTotal)+"\r\n" +
                        "! UF bottom.fmt\r\n";

                try {
                    thePrinterConn.write(cpclData.getBytes());
                } catch (ConnectionException e) {
                    Toast myToast = Toast.makeText(searchActivity.getInstance(),"No se pudieron escribir los datos",Toast.LENGTH_LONG);
                    myToast.show();
                    e.printStackTrace();
                }

                // Make sure the data got to the printer before closing the connection
                try {
                    Thread.sleep(500);
                } catch (InterruptedException e) {
                    Toast myToast = Toast.makeText(searchActivity.getInstance(),"Error al inizializar thread sleep",Toast.LENGTH_LONG);
                    myToast.show();
                    e.printStackTrace();
                }

                // Close the connection to release resources.
                try {
                    thePrinterConn.close();
                } catch (ConnectionException e) {
                    Toast myToast = Toast.makeText(searchActivity.getInstance(),"No se pudo cerrar la conexión",Toast.LENGTH_LONG);
                    myToast.show();
                    e.printStackTrace();
                }

                Looper.myLooper().quit();

            }
        }).start();
    }

}
