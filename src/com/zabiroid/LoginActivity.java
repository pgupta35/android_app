package com.zabiroid;

import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.NameValuePair;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.methods.HttpUriRequest;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.zabiroid.R;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class LoginActivity extends Activity {
	
	private static final int SHOW_EDITOR = 0;
	private static final String PREFERENCE_KEY = "AuthData";
	SharedPreferences authData;
    /** Called when the activity is first created. */
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getWindow().requestFeature(Window.FEATURE_LEFT_ICON);
        authData = getSharedPreferences(PREFERENCE_KEY, Activity.MODE_APPEND);
        if (authData.getString("AuthToken", "No Data") != "No Data"){
        	Intent intent = new Intent(this,HostListActivity.class);
        	startActivity(intent);
        }
        
        setContentView(R.layout.login);
        getWindow().setFeatureDrawableResource( Window.FEATURE_LEFT_ICON, R.drawable.zabbix );
        setTitle(R.string.title_login);
        final CheckBox checkBox = (CheckBox)findViewById(R.id.https_check);
        checkBox.setChecked(false);
        checkBox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener(){

			
			public void onCheckedChanged(CompoundButton arg0, boolean arg1) {
				if ( checkBox.isChecked() != false ) {
					checkBox.setChecked(true);	
				}
				else {
					checkBox.setChecked(false);
				}
				
			}
        	
        });
        Button button = (Button)findViewById(R.id.login_button);
        button.setOnClickListener(new View.OnClickListener(){
        	
        	public void onClick(View v) {
        		Intent intent = new Intent(LoginActivity.this, HostListActivity.class);
        		EditText hostText = (EditText)findViewById(R.id.Hostname);
        		EditText editText = (EditText)findViewById(R.id.account_name);
        		EditText passText = (EditText)findViewById(R.id.password);
        		String host = hostText.getText().toString();
        		String account_name = editText.getText().toString();
        		String pass = passText.getText().toString();
        		CheckBox checkBox = (CheckBox)findViewById(R.id.https_check);
        		Boolean https = checkBox.isChecked();
        		host = host.trim();
        		host = host.replaceAll("[ ]", "");
        		host = host.replaceAll("[　]", "");
        		account_name = account_name.trim();
        		pass = pass.trim();
        		if(host.length() != 0 && account_name.length() != 0 && pass.length() != 0){
        			if(host.length()>8){
        				if(host.substring(0,7).equals("http://")){
        					host = host.substring(7);
        				}else if(host.substring(0,8).equals("https://")){
        					host = host.substring(8);
        				}
        			}
        			
        			ZabbixApiAccess zabbix = new ZabbixApiAccess(host,https);
        			String auth_key = null;
        			try {
        				
        				auth_key = zabbix.zabbixAuthenticate(account_name, pass);
        				authData = getSharedPreferences(PREFERENCE_KEY, Activity.MODE_APPEND);
            			authData.edit().putString("AuthToken", auth_key).commit();
            			authData.edit().putString("URI", zabbix.getUri()).commit();
        			} catch (IOException e) {
        				e.printStackTrace();
        				auth_key = "error";
        				Toast.makeText(LoginActivity.this,R.string.connection_error,Toast.LENGTH_LONG).show();
        			}
        		
        			
        		
        			if ( auth_key != "error" ){
        				startActivityForResult(intent, SHOW_EDITOR);
        			
        			}else {
        				Toast.makeText(LoginActivity.this,R.string.auth_failure,Toast.LENGTH_LONG).show();
        			}
        		}else{
        			Toast.makeText(LoginActivity.this,R.string.empty_error,Toast.LENGTH_LONG).show();
        		}
 
        		
        	}
        });
        
    }
    
    public void onStart() {
    	super.onStart();
        authData = getSharedPreferences(PREFERENCE_KEY, Activity.MODE_APPEND);
        if (authData.getString("AuthToken", "No Data") != "No Data"){
        	finish();
        }
    }   
    
    
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
    	if(requestCode == SHOW_EDITOR) {
    		if(resultCode == RESULT_OK) {
    			TextView textView = (TextView)findViewById(R.id.account_name);
    			textView.setText(data.getCharSequenceExtra("NAME"));
    		}
    	}
    }
}