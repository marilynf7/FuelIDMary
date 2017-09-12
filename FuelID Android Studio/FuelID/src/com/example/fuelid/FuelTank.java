package com.example.fuelid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.text.DecimalFormat;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.entity.UrlEncodedFormEntity;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;


import com.bimsdk.usb.UsbConnect;
import com.bimsdk.usb.io.OnDataReceive;
import com.logixsoft.fuelid.R;


import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Configuration;
import android.content.res.TypedArray;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.Typeface;
import android.graphics.drawable.ColorDrawable;
import android.net.ParseException;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.Handler;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.Vibrator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
import android.text.InputType;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.Chronometer;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

public class FuelTank extends Activity {
	
	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	private static final Pattern KEYCODE_PATTERN = Pattern.compile("KEYCODE_(\\w)");
	private static final int REQUEST_CODE = 1234;
    public double buenas=0;
	final Context context = this;
	public String[] estaciones;
	private ProgressDialog progressDialog;
	final String PREFS_NAME = "MyPrefsFile";
    private UserPermissionsDatabaseHelper databaseHelper;
    public double LOWERV,CENTERV,UPPERV;
  
    public String TANKNAME,MAXVAL,MAXANALOG,CURRENTANALOG,LASTUPLOAD;
	
	
	   public  Timer timer = new Timer();
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	  FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/MuseoSans300.otf");
		
	setContentView(R.layout.fuelidtank);
	ActionBar actionBar = getActionBar();
	actionBar.hide();
	
	// more stuff here...

	    actionBar.setSubtitle("Saldo tanque");
	    actionBar.setTitle("FuelID");
	    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Crimson)));
	    actionBar.setDisplayShowTitleEnabled(false);  // required to force redraw, without, gray color
	    actionBar.setDisplayShowTitleEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(false);
	    actionBar.show(); 

	    Intent intent = getIntent();
	    SwitchVisualization(true);


		}
	
	  @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	        // Inflate the menu items for use in the action bar
	        MenuInflater inflater = getMenuInflater();
	        inflater.inflate(R.menu.refresh
	        ,menu);
	        return super.onCreateOptionsMenu(menu);
	    }
	   

	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	        // Handle presses on the action bar items

	        switch (item.getItemId()) {
	            case R.id.refresh:
	            new LoadFuelData().execute();   	
	            default:
	            return super.onOptionsItemSelected(item);
	        }
	    }
	 
	
   
	    private boolean hasContent(EditText et) {
		       return (et.getText().toString().trim().length() > 0);
		}
 

	 @Override
	    public void onBackPressed() {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
  		builder.setMessage("¿Esta seguro de que desea salir? Perderá la información digitada.")
  		   .setCancelable(false)
  		   .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
  		       public void onClick(DialogInterface dialog, int id) {
  		    	 SwitchVisualization(false);
  		   		finish();
  		   			 }
  		   })
  		   .setNegativeButton("No", new DialogInterface.OnClickListener() {
  		       public void onClick(DialogInterface dialog, int id) {
  		     	
  		    	   
  		       }
  		   });
  		AlertDialog alert = builder.create();
  		alert.show();
      	
      	
	    }
	 
		
  	
	  private class LoadFuelData extends AsyncTask<Void, Integer, Void>  
	    {  
	        //Before running code in separate thread  
	        @Override  
	        protected void onPreExecute()  
	        {  
	        //	progressDialog = ProgressDialog.show(FuelTank.this,"Cargando...",  
	        	//	    "Cargando información, favor espere...", false, false);
	        }  
	  
	        //The code to be executed in a background thread.  
	        @Override  
	        protected Void doInBackground(Void... params)  
	        {  
	            /* This is just a code that delays the thread execution 4 times, 
	             * during 850 milliseconds and updates the current progress. This 
	             * is where the code that is going to be executed on a background 
	             * thread must be placed. 
	             */  
	        	 StrictMode.setThreadPolicy(policy); 
	     		try{
      			
      			String result = null;
      	    	InputStream is = null;
      	    	SimpleDateFormat sdfM = new SimpleDateFormat("MM");
      	    	SimpleDateFormat sdfY = new SimpleDateFormat("yyyy");
          		
      	    	String MONTH = sdfM.format(new Date()); 
      	    	String YEAR = sdfY.format(new Date()); 
		    		
      	    	
      	    	String v1 = "'"+"CODIGO"+"'";
      	    	String v2 = "'"+"LICENCIA"+"'";
      	  	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
    	      	nameValuePairs.add(new BasicNameValuePair("codigo",v1));
    	      	nameValuePairs.add(new BasicNameValuePair("licencia",v2));

      		
      	    
      	   
      	
    
      	//apartir de aqui carga ruta
      		try
      		{
      	    	        HttpClient httpclient = new DefaultHttpClient();
      	    	        HttpPost httppost = new HttpPost(MainMenu.URL+"checkTankStatus.php");
      	    	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
      	    	        HttpResponse response = httpclient.execute(httppost); 
      	    	        HttpEntity entity = response.getEntity();
      	    	        is = entity.getContent();
      	    	        Log.e("log_tag", "connection success ");
      	    	     //   Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_SHORT).show();
      		}
      	catch(Exception e)
      		{
      	    	        Log.e("log_tag", "Error in http connection "+e.toString());
      	    	//        Toast.makeText(getApplicationContext(), "Connection fail", Toast.LENGTH_SHORT).show();
      	  //  	  	Toast.makeText(getApplicationContext(), "No se encuentra conectado a la red.Verifique conexión de VPN.", Toast.LENGTH_SHORT).show();
      		}
      	    	//convert response to string
      		try{
      			
      	    	        BufferedReader reader = new BufferedReader(new InputStreamReader(is,"ISO-8859-1"),8);
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
      		}
      	int counter=0;
      	    	//parse json data
      	    	try{
      	     

      	    	 	JSONArray jArray = new JSONArray(result);
      	    		for(int i=0;i<jArray.length();i++)  
      	            {
      	    			try{
      	    		   	JSONObject no = jArray.getJSONObject(i);
      	    		    String placa=no.getString("TankID");
      	    		    try{
      	    		    TANKNAME=placa;
      	    
      	    		    String goal=no.getString("TankSize");
      	    		MAXVAL=goal;
      	  	    String maxana=no.getString("TankAnalog");
  	    		      MAXANALOG=maxana;
      	    		  String consumption=no.getString("analog1");
      	    		  CURRENTANALOG=consumption;
      	    		  String date=no.getString("created_on");
      	    		  LASTUPLOAD=date;

      	    		    }catch(Exception e){}
      		            counter++;
      		   
      	    			}catch(Exception e)
      		          	{ 
      		          	}
      		            }
      	    	}
      	    	catch(JSONException e)
      	    	{
      	    	        Log.e("log_tag", "Error parsing data "+e.toString()+Integer.toString(counter));
      	     	} catch (IllegalArgumentException e) {
      				// TODO Auto-generated catch block
      				e.printStackTrace();
      			} 	
      			}catch(Exception e)
      	    	{
      		        Log.e("log_tag", "Active el VPN para aplicar esta opción.");
      		}
	     	
	      	
	     
	            return null;  
	        }  
	  
	        //Update the progress  
	        @Override  
	        protected void onProgressUpdate(Integer... values)  
	        {  
	            //set the current progress of the progress dialog  
	   //         progressDialog.setProgress(values[0]);  
	  
	        }  
	  
	        //after executing the code in the thread  
	        @Override  
	        protected void onPostExecute(Void result)  
	        {  
	            //close the progress dialog  
	     //       progressDialog.dismiss();  
	            //initialize the View  


	            Double maxval =0.0;
	          	try{
	            maxval = Double.parseDouble(MAXVAL);}catch(Exception e){}
	          	

	        	Double maxanalog =10000000.0;
	        	try{
	        	maxanalog = Double.parseDouble(MAXANALOG);}catch(Exception e){}
	        	Double current =1500.0;
	        	try{
	        	current = Double.parseDouble(CURRENTANALOG);
	        		}catch(Exception e){}
	        	
	          	double available= (current*maxval)/maxanalog;
	         
	          	
	         	double percentage= (current)/maxanalog;
	        	DecimalFormat f = new DecimalFormat("##.00");
	        	
	        	LinearLayout Tanque = (LinearLayout)findViewById(R.id.bigtank);
	        	
	         	TextView Tankheader = (TextView)findViewById(R.id.tankname);
	          	Tankheader.setText("Saldo disponible "+TANKNAME);
	         	TextView CurrentAmount = (TextView)findViewById(R.id.currenttank);
	         	CurrentAmount.setText(f.format(available)+"L/"+f.format(percentage*100)+"%");
	         	TextView date = (TextView)findViewById(R.id.date);
	          	date.setText(LASTUPLOAD);
	        	
	        	if(percentage==0){Tanque.setBackgroundResource(R.drawable.tankdiagram0);}
	        	else if(percentage>0 &&percentage<=0.12){Tanque.setBackgroundResource(R.drawable.tankdiagram1);}
	        	else if(percentage>0.12&&percentage<=0.24){Tanque.setBackgroundResource(R.drawable.tankdiagram2);}
	          	else if(percentage>0.24&&percentage<=0.36){Tanque.setBackgroundResource(R.drawable.tankdiagram3);}
	          	else if(percentage>0.36&&percentage<=0.48){Tanque.setBackgroundResource(R.drawable.tankdiagram4);}
	          	else if(percentage>0.48&&percentage<=0.6){Tanque.setBackgroundResource(R.drawable.tankdiagram5);}
	          	else if(percentage>0.6&&percentage<=0.72){Tanque.setBackgroundResource(R.drawable.tankdiagram6);}
	          	else if(percentage>0.72&&percentage<=0.84){Tanque.setBackgroundResource(R.drawable.tankdiagram7);}
	          	else if(percentage>0.84&&percentage<=0.96){Tanque.setBackgroundResource(R.drawable.tankdiagram8);}
	          	else if(percentage>0.96){Tanque.setBackgroundResource(R.drawable.tankdiagram9);}

	        }
	        
	      
	    
	    }

	  public void SwitchVisualization(Boolean state){
			
			if(state){
				 final Handler handler = new Handler();
	 		
	 		
	 		    timer.scheduleAtFixedRate(new TimerTask() {
	 		        @Override
	 		        public void run() {
	 		            handler.post(new Runnable() {
	 		                public void run() {
	 		            	  new LoadFuelData().execute();   	 	
	 		                	
	 		                }
	 		            });
	 		        }
	 		    },10000,10000);	
			}
			
			else{
	  	
			}
  
		  }
	    
	  



}	
	 