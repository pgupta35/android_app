package com.zabiroid;

import java.io.IOException;
import java.util.ArrayList;

import com.zabiroid.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

public class TriggerActivity extends Activity {
	
	ZabbixApiAccess zabbix;
	
	String authToken;
	String uri;
	ListView list;
	TriggerListAdapter adapter;

	
	
	
	private static final String PREFERENCE_KEY = "AuthData";
	SharedPreferences authData;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.trigger_list);
        setTitle(R.string.title_trigger_list);
        
        authData = getSharedPreferences(PREFERENCE_KEY, MODE_PRIVATE);
        authToken = authData.getString("AuthToken", "No Data");
        uri = authData.getString("URI", "No Data");
    
        Intent intent = getIntent();
        String hostId = intent.getStringExtra("hostid");
        String hostName = intent.getStringExtra("hostname");        
		zabbix = new ZabbixApiAccess(uri,authToken);
		ArrayList<Trigger> eventList = null;
		try {
			eventList = zabbix.getTriggerList(hostId, 20);
		} catch (IOException e) {
			// TODO 自動生成された catch ブロック
			e.printStackTrace();
			Toast.makeText(this,R.string.connection_error,Toast.LENGTH_LONG).show();
			Trigger error = new Trigger();
			error.setDescription("Connection Error!");
			eventList = new ArrayList<Trigger>();
			eventList.add(error);
			adapter = new TriggerListAdapter(this, eventList);
			list= (ListView)findViewById(R.id.triggerlistview);
		    list.setAdapter(adapter);
		}
		
		if( eventList != null && eventList.size() != 0) {
			adapter = new TriggerListAdapter(this, eventList);		    
			list= (ListView)findViewById(R.id.triggerlistview);
		    
			list.setAdapter(adapter);
		
		}else{
			Trigger error = new Trigger();
			error.setDescription("No Trigger!");
			eventList = new ArrayList<Trigger>();
			eventList.add(error);
			adapter = new TriggerListAdapter(this, eventList);
			list= (ListView)findViewById(R.id.triggerlistview);
		    list.setAdapter(adapter);
		}
    }
}
