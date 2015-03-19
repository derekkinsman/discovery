package com.siberia.discovery.util;

import android.content.Context;
import android.net.nsd.NsdServiceInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

public class ServiceAdapter extends ArrayAdapter<NsdServiceInfo> {
    public ServiceAdapter(Context context) {
        super(context, 0);
    }

    @Override
    public void add(NsdServiceInfo serviceInfo) {
        final String newAddress = serviceInfo.getHost().getHostAddress();

        for(int i = 0; i < getCount(); i++) {
            NsdServiceInfo item = getItem(i);
            String addr = item.getHost().getHostAddress();
            if(addr.equals(newAddress)) {
                if(!item.equals(serviceInfo)) {
                    remove(item);
                    break;
                }
                return;
            }
        }

        super.add(serviceInfo);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        Tag tag;
        if(convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(android.R.layout.simple_list_item_2, parent, false);
            tag = new Tag();
            tag.text1 = (TextView) convertView.findViewById(android.R.id.text1);
            tag.text2 = (TextView) convertView.findViewById(android.R.id.text2);
            convertView.setTag(tag);
        } else {
            tag = (Tag) convertView.getTag();
        }

        final NsdServiceInfo data = getItem(position);
        tag.text1.setText(data.getServiceName());
        tag.text2.setText(String.format("%s:%d", data.getHost().getHostAddress(), data.getPort()));

        return convertView;
    }

    private class Tag {
        public TextView text1;
        public TextView text2;
    }
}
