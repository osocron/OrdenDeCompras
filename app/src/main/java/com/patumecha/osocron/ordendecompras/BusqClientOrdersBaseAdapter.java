package com.patumecha.osocron.ordendecompras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by osocron on 22/09/14.
 */
public class BusqClientOrdersBaseAdapter extends BaseAdapter {

    //Adapter for the Dialog after user clicks on a client on the search orders Activity

    private List<Order> mOrderList;
    private LayoutInflater mInflater;

    BusqClientOrdersBaseAdapter  (List<Order> list, LayoutInflater inflater) {
        mOrderList = list;
        mInflater = inflater;
    }

    @Override
    public int getCount() {
        return mOrderList.size();
    }

    @Override
    public Object getItem(int position) {
        return mOrderList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {

        final ViewItem item;

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.busq_prod_deta_layout,null);
            convertView.setClickable(true);
            convertView.setFocusable(true);

            item = new ViewItem();

            item.OrderDescrip = (TextView) convertView.findViewById(R.id.busqDetTextView);

            convertView.setTag(item);
        } else {
            item = (ViewItem) convertView.getTag();
        }

        final Order curOrder = mOrderList.get(position);

        item.OrderDescrip.setText(curOrder.serie+"  "+curOrder.numero+"      "+curOrder.fecha);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Do something with the order
                BusquedaOrden.getInstance().getOrders(curOrder.numero);
            }
        });

        return convertView;
    }

    private class ViewItem {
        TextView OrderDescrip;
    }
}