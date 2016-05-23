package com.patumecha.osocron.ordendecompras;


import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import java.util.Set;

import android.app.ActionBar;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothClass;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.support.v13.app.FragmentPagerAdapter;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.support.v4.view.ViewPager;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Toast;

public class searchActivity extends FragmentActivity{

    String TabFragmentB;
    String TabFragmentA;
    String TabFragmentC;

    private static String fstTimeFlag = "0";

    private static searchActivity mySearchActivity;

    private static final String vendSerie = "A";

    public void setTabFragmentB(String t){
        TabFragmentB = t;
    }


    public String getTabFragmentB(){
        return TabFragmentB;
    }


    public void setTabFragmentA(String t){
        TabFragmentA = t;
    }


    public String getTabFragmentA(){
        return TabFragmentA;
    }


    public void setTabFragmentC(String t){
        TabFragmentC = t;
    }


    public String getTabFragmentC(){
        return TabFragmentC;
    }


    /**
     * The {@link android.support.v4.view.PagerAdapter} that will provide
     * fragments for each of the sections. We use a
     * {@link FragmentPagerAdapter} derivative, which will keep every
     * loaded fragment in memory. If this becomes too memory intensive, it
     * may be best to switch to a
     * {@link android.support.v13.app.FragmentStatePagerAdapter}.
     */
    SectionsPagerAdapter mSectionsPagerAdapter;

    /**
     * The {@link ViewPager} that will host the section contents.
     */
    ViewPager mViewPager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search);

        mySearchActivity = this;
        // Create the adapter that will return a fragment for each of the three
        // primary sections of the activity.
        mSectionsPagerAdapter = new SectionsPagerAdapter(getFragmentManager());

        // Set up the ViewPager with the sections adapter.
        mViewPager = (ViewPager) findViewById(R.id.pager);
        mViewPager.setAdapter(mSectionsPagerAdapter);


        final ActionBar actionBar = getActionBar();

        if(fstTimeFlag.equals("0")){
            Intent myIntent = new Intent(searchActivity.this, BusquedaClientes.class);
            searchActivity.this.startActivity(myIntent);
            fstTimeFlag = "1";
        }


        // Specify that tabs should be displayed in the action bar.
        actionBar.setNavigationMode(ActionBar.NAVIGATION_MODE_TABS);

        mViewPager.setOnPageChangeListener(new ViewPager.SimpleOnPageChangeListener() {
                    @Override
                    public void onPageSelected(int position) {
                        // When swiping between pages, select the
                        // corresponding tab.
                        getActionBar().setSelectedNavigationItem(position);
                    }
                });


        // Create a tab listener that is called when the user changes tabs.
        ActionBar.TabListener tabListener = new ActionBar.TabListener() {

            public void onTabSelected(ActionBar.Tab tab, FragmentTransaction ft) {
                // show the given tab
                mViewPager.setCurrentItem(tab.getPosition());
            }

            public void onTabUnselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // hide the given tab
            }

            public void onTabReselected(ActionBar.Tab tab, FragmentTransaction ft) {
                // probably ignore this event
            }
        };

        // Add 3 tabs, specifying the tab's text and TabListener

        actionBar.addTab(actionBar.newTab().setText("Search").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("View").setTabListener(tabListener));
        actionBar.addTab(actionBar.newTab().setText("Order").setTabListener(tabListener));


    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if ((keyCode == KeyEvent.KEYCODE_BACK)) {
            AlertDialog.Builder alertbox = new AlertDialog.Builder(searchActivity.this);
            alertbox.setTitle("Desea salir de la aplicación?");
            alertbox.setMessage("(se borrara el contenido de la orden)");
            alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {

                    Intent intent = new Intent(searchActivity.this, LoginActivity.class);
                    intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
                    intent.putExtra("EXIT", true);
                    startActivity(intent);

                }
            });

            alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface arg0, int arg1) {
                    // Nothing will be happened when clicked on no button

                    // of Dialog

                }
            });

            alertbox.show();
        }
        return super.onKeyDown(keyCode, event);
    }

    public void setCurrentItem (int item, boolean smoothScroll) {
        mViewPager.setCurrentItem(item, smoothScroll);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.options_menu, menu);
        return true;

    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        switch (item.getItemId()) {

            case R.id.action_settings1:
                // Busqueda de Pedidos
                AlertDialog.Builder alertbox = new AlertDialog.Builder(searchActivity.this);
                alertbox.setTitle("Quieres buscar una orden?");
                alertbox.setMessage("(se borrara el contenido actual de la orden)");
                alertbox.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        try {
                            String TabOfFragmentC = getTabFragmentC();
                            orderView orderFragment = (orderView) getFragmentManager().findFragmentByTag(TabOfFragmentC);
                            orderFragment.clearOrder();
                            searchOrders();
                        }catch (Exception e){
                            searchOrders();
                            e.printStackTrace();
                        }
                    }
                });

                alertbox.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

                alertbox.show();

                return true;

            case R.id.action_settings2:
                // Cambiar de Cliente
                AlertDialog.Builder alertbox2 = new AlertDialog.Builder(searchActivity.this);
                alertbox2.setTitle("Quieres cambiar de cliente?");
                alertbox2.setMessage("(se borrara el contenido de la orden)");
                alertbox2.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {
                        changeClient();
                    }
                });

                alertbox2.setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface arg0, int arg1) {

                    }
                });

                alertbox2.show();
                return true;

            default:
                return super.onOptionsItemSelected(item);
        }
    }

    /**
     * A {@link FragmentPagerAdapter} that returns a fragment corresponding to
     * one of the sections/tabs/pages.
     */
    public class SectionsPagerAdapter extends FragmentPagerAdapter {

        public SectionsPagerAdapter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {

            switch (position) {
                case 0:
                    return new searchProducts();
                case 1:
                    return new viewProducts();
                case 2:
                    return new orderView();
            }

            return null;

        }

        @Override
        public int getCount() {
            // Show 3 total pages.
            return 3;
        }

        @Override
        public CharSequence getPageTitle(int position) {
            Locale l = Locale.getDefault();
            switch (position) {
                case 0:
                    return getString(R.string.title_section1).toUpperCase(l);
                case 1:
                    return getString(R.string.title_section2).toUpperCase(l);
                case 2:
                    return getString(R.string.title_section3).toUpperCase(l);
            }
            return null;
        }
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            return inflater.inflate(R.layout.fragment_search, container, false);
        }
    }

    public Cliente retrieveExtras() {
        Bundle extras = getIntent().getExtras();
        Cliente clienteToSearchFragment = null;
        if (extras != null) {
            String nombreCliente = extras.getString("nombreCliente");
            String direccionCliente = extras.getString("direccionCliente");
            String claveCliente = extras.getString("claveCliente");
            String nivel = extras.getString("nivel");
            double descuento = extras.getDouble("descuento");
            String telefono1 = extras.getString("telefono1");
            String telefono2 = extras.getString("telefono2");
            clienteToSearchFragment = new Cliente(nombreCliente,direccionCliente,claveCliente,nivel,descuento,telefono1,telefono2);
        }
        return clienteToSearchFragment;
    }

    public void searchOrders(){
        Intent myIntent = new Intent(this,BusquedaOrden.class);
        myIntent.putExtra("search","orders");
        startActivityForResult(myIntent,1);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data){

       // TODO Auto-generated method stub
       super.onActivityResult(requestCode, resultCode, data);
       if(resultCode == RESULT_OK){
           if(requestCode == 1){
               ArrayList<Final_order> orderToOrderView = new ArrayList<Final_order>();
               Bundle extras = data.getExtras();
               if(extras != null){
                   orderToOrderView = extras.getParcelableArrayList("orders");
               }
               String TabOfFragmentA = getTabFragmentA();
               searchProducts searchProductsFragment = (searchProducts) getFragmentManager().findFragmentByTag(TabOfFragmentA);
               searchProductsFragment.getOrdersFromActivity(orderToOrderView);
           }
       }

    }

    public static searchActivity getInstance(){
        return  mySearchActivity;
    }

    public void changeClient(){
        try {
            String TabOfFragmentC = getTabFragmentC();
            orderView orderFragment = (orderView) getFragmentManager().findFragmentByTag(TabOfFragmentC);
            orderFragment.clearOrder();
            orderFragment.deactivateActFlag();
            String TabOfFragmentA = getTabFragmentA();
            searchProducts searchFragment = (searchProducts) getFragmentManager().findFragmentByTag(TabOfFragmentA);
            searchFragment.setClientBool();
            Intent myIntent = new Intent(searchActivity.this, BusquedaClientes.class);
            startActivity(myIntent);
        }catch (Exception e){
            try {
                String TabOfFragmentA = getTabFragmentA();
                searchProducts searchFragment = (searchProducts) getFragmentManager().findFragmentByTag(TabOfFragmentA);
                searchFragment.setClientBool();
            }catch(Exception e2){
                e2.printStackTrace();
            }
                Intent myIntent = new Intent(searchActivity.this, BusquedaClientes.class);
                startActivity(myIntent);
            e.printStackTrace();
        }
    }

    public String getVendSerie(){
        return vendSerie;
    }

}
