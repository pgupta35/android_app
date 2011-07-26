package com.zabiroid;

import java.util.ArrayList;
import java.util.List;

import com.zabiroid.R;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class HostListAdapter extends ArrayAdapter<Host>{

	private LayoutInflater inflater;
	private TextView hostNameView;
	private ImageView triggerView;
	private TextView errorNumView;
	
	
	public HostListAdapter(Context context, ArrayList<Host> objects) {
		super(context, 0, objects);
		inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		View view = convertView;
		if (convertView == null) {
			view = inflater.inflate(R.layout.host, null);
		}
		
		Host host = this.getItem(position);
		
		if( host != null){
			final String hostid = host.getHostId();
			final String hostname = host.getHostName();
			int errornum = host.getErrorNum();
			String hoststatus = host.getHostStatus();
			hostNameView = (TextView)view.findViewById(R.id.host_name);
			triggerView = (ImageView)view.findViewById(R.id.fire_image);
			errorNumView = (TextView)view.findViewById(R.id.error_num);
			
			if (hoststatus.equals("1")) {
				hostNameView.setTextColor(Color.RED);
			}
			hostNameView.setText(hostname);
			errorNumView.setText(Integer.toString(errornum));
		
			triggerView.setOnClickListener(new View.OnClickListener(){
				
				public void onClick(View v) {
					Intent intent = new Intent(v.getContext(),TriggerActivity.class);
					intent.putExtra("hostid", hostid);
					intent.putExtra("hostname", hostname);
					v.getContext().startActivity(intent);
				}
			});
		}
		return view;
	}
}