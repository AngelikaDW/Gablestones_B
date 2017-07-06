package com.aleaf.gablestones;

import android.content.Context;
import android.support.annotation.LayoutRes;
import android.support.annotation.NonNull;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by angelika on 06/07/2017.
 */

public class MyStonesAdapter extends ArrayAdapter<String> {

    private final Context context;
    //private final String[] values;
    private final ArrayList<String> addresslist;



    public MyStonesAdapter(Context context, ArrayList<String> addresslist) {
        super(context, R.layout.list_item, addresslist);
        this.context = context;
        //this.values = values;
        this.addresslist = addresslist;

    }

    public View getView(int position, View convertView, ViewGroup parent) {

        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View listItem = inflater.inflate(R.layout.list_item, parent, false);
        TextView catTextView = (TextView) listItem.findViewById(R.id.category);
        TextView streetTextView = (TextView) listItem.findViewById(R.id.street);

        catTextView.setText(addresslist.get(position));
        streetTextView.setText(addresslist.get(position));

        return listItem;
    }
}
