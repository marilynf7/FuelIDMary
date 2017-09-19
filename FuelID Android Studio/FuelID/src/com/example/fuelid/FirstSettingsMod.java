package com.example.fuelid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONException;
import org.json.JSONObject;

import com.logixsoft.fuelid.R;



/*import com.example.fuelid.R;
import com.example.fuelid.R.id;
import com.example.fuelid.R.layout;
import com.example.fuelid.R.string;*/

import android.app.ActionBar;
import android.app.Activity;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import io.sentry.Sentry;
import io.sentry.android.AndroidSentryClientFactory;
import io.sentry.context.Context;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;

public class FirstSettingsMod extends Activity{
	  private Settings SettingsHelper;
      StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
   
	  public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);	
		  FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/MuseoSans300.otf");
		  Sentry.getContext().setUser(
			  new UserBuilder().setEmail("First_Settings").build()
		  );
	    
		setContentView(R.layout.licensescreen);
		Intent intent = getIntent();
		SettingsHelper = new Settings(this); 
		
		ActionBar actionBar = getActionBar();
	    actionBar.hide();
	    actionBar.setSubtitle("Registro de Licencia " );
	
	    actionBar.setTitle("FuelID");
	    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Crimson)));
		   actionBar.setDisplayShowTitleEnabled(false);  // required to force redraw, without, gray color
	    actionBar.setDisplayShowTitleEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(false);
	    actionBar.show();
		  android.content.Context ctx = this.getApplicationContext();

		  // Use the Sentry DSN (client key) from the Project Settings page on Sentry
		  String sentryDsn = "https://c68a8179ace94e9d936f56cddd7c5f53:69791b9f61754e44958eab1bc6076eb3@sentry.io/218195";
		  Sentry.init(sentryDsn, new AndroidSentryClientFactory(ctx));

}
	public void onBack(View view) {
	
	}
	public void onSave(View view) {


		EditText Usuario = (EditText)findViewById(R.id.user);
		String usuario= Usuario.getText().toString();
	
		EditText Company = (EditText)findViewById(R.id.company);
		String company= Company.getText().toString();
		
		EditText License = (EditText)findViewById(R.id.license);
		String license= License.getText().toString();
		
		Boolean autorizado=false;
		
		if(this.hasContent(usuario)==true&&this.hasContent(company)==true&&this.hasContent(license)==true){
		
		 	String v1 = "'"+usuario+"'";
			String v2 = "'"+company+"'";
			SimpleDateFormat sdfa = new SimpleDateFormat("yyyy-MM-dd");
			SimpleDateFormat sdfc = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
			String v3 = "'"+license+"'"; 
			String fecha=sdfc.format(new Date());
	        String v4 = "'"+fecha+"'"; 
	        
			try{
			 	 String result = null;
			 	InputStream is = null;
	
			 	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
			 	
			 	SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM");

			  	nameValuePairs.add(new BasicNameValuePair("user",v1));
			 	nameValuePairs.add(new BasicNameValuePair("company",v2));
			 	nameValuePairs.add(new BasicNameValuePair("license",v3));
				nameValuePairs.add(new BasicNameValuePair("date",v4));
		
					            StrictMode.setThreadPolicy(policy); 
			        	//http post
			        	try{
			        	        HttpClient httpclient = new DefaultHttpClient();
			        	        HttpPost httppost = new HttpPost(MainMenu.URL+"registerLicenseFuelID.php");
			        	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
			        	        HttpResponse response = httpclient.execute(httppost); 
			        	        HttpEntity entity = response.getEntity();
			        	        is = entity.getContent();
			        	        
			        	        
			        	        Log.e("log_tag", "connection success ");
			        	      //  Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_SHORT).show();
			        	   }
			      
			        	catch(Exception e)
			        	{
			        	        Log.e("log_tag", "Error in http connection "+e.toString());
			        	        Toast.makeText(getApplicationContext(), getString(R.string.connectionfail), Toast.LENGTH_SHORT).show();
								Sentry.capture(e);

			        	}
			        	//convert response to string
			        	try{
			        	        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"iso-8859-1"),8);
			        	        StringBuilder sb = new StringBuilder();
			        	        String line = null;
			        	        while ((line = reader.readLine()) != null) 
			        	        {
			        	                sb.append(line + "\n");
			        	        }
			        	        is.close();

			        	        result=sb.toString();
			        	}
			        	catch(Exception e)
			        	{
			        	       Log.e("log_tag", "Error converting result "+e.toString());
								Sentry.capture(e);
			           	}
			        	try{
			        	            	JSONObject json_data = new JSONObject(result);
			        	                CharSequence w= (CharSequence) json_data.get("re");
			        	                if(w.equals("Ingreso autorizado")){autorizado=true;}
			        	  			    Toast toasta=Toast.makeText(getApplicationContext(), w, 2000);
							  	        toasta.setGravity(Gravity.CENTER,0,0);
							  	       // toasta.show();	
			        	     }
			        	catch(JSONException e)
			        	   {
			        	        Log.e("log_tag", "Error parsing data "+e.toString());
			        	        Toast.makeText(getApplicationContext(), "JsonArray fail", Toast.LENGTH_SHORT).show();
							   	Sentry.capture(e);
			        	    }}catch(Exception e){
								Sentry.capture(e);
								}
			
			if(autorizado){
			SettingsHelper.deleteSettings();
			SettingsHelper.saveSettings(usuario,company,license,fecha);
			Intent i = new Intent(FirstSettingsMod.this, MainMenu.class);
            startActivity(i);
			finish();	
			}else{
				 Toast.makeText(getApplicationContext(), getString(R.string.invalidlicense), Toast.LENGTH_LONG).show();			
			}}
			
			
		else{		
			 Toast.makeText(getApplicationContext(), getString(R.string.fillup), Toast.LENGTH_LONG).show();	
		}

	}

	private boolean hasContent(String datos) {
	       return (datos.trim().length() > 0);
	}
}
