package com.emojis.kavisherlock.wokringwithsms;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

/**
 * Created by harryarakkal on 12/5/16.
 */

public class InboxAdapter extends ArrayAdapter {

    public InboxAdapter(Context context, int resource) {
        super(context, resource);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        // Get the data item for this position
        Text text = (Text) getItem(position);
        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.text_item, parent, false);
        }
        // Lookup view for data population
        TextView textView = (TextView) convertView.findViewById(R.id.text_preview);
        textView.setText(text.text);
        return convertView;
    }
}
