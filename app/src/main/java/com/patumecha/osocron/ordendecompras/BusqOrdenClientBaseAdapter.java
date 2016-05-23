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
 * Created by osocron on 22/09/14.
 */
public class BusqOrdenClientBaseAdapter extends BaseAdapter implements Filterable {

    //Adapter for the AutoCompleteTextView in the Order Activity

    private List<Cliente> mClientList;
    private LayoutInflater mInflater;

    BusqOrdenClientBaseAdapter(List<Cliente> list, LayoutInflater inflater) {
        mClientList = list;
        mInflater = inflater;
    }

    @Override
    public int getCount() {
        return mClientList.size();
    }

    @Override
    public Object getItem(int position) {
        return mClientList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewItem item;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.busq_client_layout,null);
            convertView.setClickable(true);
            convertView.setFocusable(true);

            item = new ViewItem();

            item.clienteDescrip = (TextView) convertView.findViewById(R.id.busqClientTextView);

            convertView.setTag(item);

        } else {
            item = (ViewItem) convertView.getTag();
        }

        final Cliente curClient = mClientList.get(position);

        final String finalDescr = curClient.nombre;

        item.clienteDescrip.setText(finalDescr);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                BusquedaOrden.getInstance().searchOrder(curClient.nombre,curClient.claveCliente,curClient.direccion,curClient.telefono1,curClient.telefono2);
            }
        });

        return convertView;
    }

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

    private class ViewItem {
        TextView clienteDescrip;
    }
}

