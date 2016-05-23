package com.patumecha.osocron.ordendecompras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.List;

/**
 * Created by osocron on 20/08/14.
 */

//Base adapter created for handling search results from articulos on de data base.

public class BusqBaseAdap extends BaseAdapter implements Filterable{
    //Cretion of a list of Products objects. This will contain the products inside the dropdown menu from the autocomplete textview.
    private List<ProdBusq> mProductList;
    private LayoutInflater mInflater;
    //Constructor of the base adapter
    BusqBaseAdap (List<ProdBusq> list, LayoutInflater inflater) {
        mProductList = list;
        mInflater = inflater;
    }
    //Auto-implemented methods
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
    //Method used to handle individual rows inside the drop down menu
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewItem item;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.busq_prod_layout,null);
            convertView.setClickable(true);
            convertView.setFocusable(true);

            item = new ViewItem();

            item.productoDescrip = (TextView) convertView.findViewById(R.id.busqProdTextView);

            convertView.setTag(item);
        } else {
            item = (ViewItem) convertView.getTag();
        }
        //Here we access a single Product object
        final ProdBusq curProduct = mProductList.get(position);

        final String finalDescr = curProduct.Descripcio;
        //The textview in each row will contain the description of the product
        item.productoDescrip.setText(finalDescr);

        //Each time the user touches one of the items on the dropdown menu the base adapter will call the searchResultQuery method from the searchProducts fragment
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String TabOfFragmentA = ((searchActivity)v.getRootView().getContext()).getTabFragmentA();

                searchProducts fragmentA = (searchProducts)((searchActivity) v.getRootView().getContext()).getFragmentManager().findFragmentByTag(TabOfFragmentA);

                fragmentA.searchResultQuery(position,finalDescr);
            }
        });

        return convertView;
    }

    //Necessary filtering for the prediction in the autocomplete textview.
    @Override
    public Filter getFilter() {

        Filter filter = new Filter() {

            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

            return new FilterResults();

            }
            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {

            }
        };

        return filter;
    }

    //Creation of the proprties of each item inside the view.
    private class ViewItem {
        TextView productoDescrip;
    }
}
