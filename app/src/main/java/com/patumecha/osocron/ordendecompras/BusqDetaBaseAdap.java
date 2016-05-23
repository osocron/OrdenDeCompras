package com.patumecha.osocron.ordendecompras;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import java.util.List;

/**
 * Created by osocron on 22/08/14.
 */
public class BusqDetaBaseAdap extends BaseAdapter {

    private List<ProdBusqDeta> mProductList;
    private LayoutInflater mInflater;

    BusqDetaBaseAdap (List<ProdBusqDeta> list, LayoutInflater inflater) {
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

        if (convertView == null) {
            convertView = mInflater.inflate(R.layout.busq_prod_deta_layout,null);
            convertView.setClickable(true);
            convertView.setFocusable(true);

            item = new ViewItem();

            item.productDetaDescrip = (TextView) convertView.findViewById(R.id.busqDetTextView);

            convertView.setTag(item);
        } else {
            item = (ViewItem) convertView.getTag();
        }

        final ProdBusqDeta curProduct = mProductList.get(position);

        item.productDetaDescrip.setText(curProduct.descrDeta);

        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String TabOfFragmentA = ((searchActivity)v.getContext()).getTabFragmentA();

                searchProducts fragmentA = (searchProducts)((searchActivity) v.getContext()).getFragmentManager().findFragmentByTag(TabOfFragmentA);

                fragmentA.searchOnDetaQuery(position);
            }
        });


        return convertView;
    }

    private class ViewItem {
        TextView productDetaDescrip;
    }
}
