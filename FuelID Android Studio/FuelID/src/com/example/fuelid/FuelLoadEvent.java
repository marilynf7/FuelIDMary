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
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.os.SystemClock;
import android.os.Vibrator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
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
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

//import io.sentry.context.Context;
import io.sentry.Sentry;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;

public class FuelLoadEvent extends Activity {
	
	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	private static final Pattern KEYCODE_PATTERN = Pattern.compile("KEYCODE_(\\w)");
	private static final int REQUEST_CODE = 1234;
	private long mLastClickTime = 0;
	private ProgressDialog progressDialog;
	public int COUNTER =0;
	public int GLOBALCOUNTER =0;
	public String PLATE,USER,STATION,COMPANY,OLDODOMETER,BRANCHID,GASTYPE,FUELCOST,TANKID,LOCATION_SCENARIO,USERTYPE,NEWODOMETER,TRANSACTIONID,MAXLITER;
	
	public Double GOAL;
	public String Scanning="";
	public int execute=0;
	public int timerstarted=0;
	public Boolean timercompleted=false;
	public Boolean terminado=false;
	public Boolean MENOSDE1000KM=false;
	final   Context context = this;
	final String PREFS_NAME = "MyPrefsFile";
    private UserPermissionsDatabaseHelper databaseHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/MuseoSans300.otf");	
    setContentView(R.layout.fuelload);
	ActionBar actionBar = getActionBar();
	actionBar.hide();
	
	// more stuff here...

	    actionBar.setSubtitle("Carga de transaccion");
	    actionBar.setTitle("FuelID");
	    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Crimson)));
	    actionBar.setDisplayShowTitleEnabled(false);  // required to force redraw, without, gray color
	    actionBar.setDisplayShowTitleEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(false);
	    actionBar.show(); 
	    
		Spinner spinner = (Spinner) findViewById(R.id.fueltype);
		// Create an ArrayAdapter using the string array and a default spinner layout
		ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(this,
		R.array.fueltypes, android.R.layout.simple_spinner_item);
		// Specify the layout to use when the list of choices appears
		adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
		// Apply the adapter to the spinner
		spinner.setAdapter(adapter);
		
	    Intent intent = getIntent();
			PLATE=intent.getStringExtra("placa");
			USER=intent.getStringExtra("usuario");
			STATION=intent.getStringExtra("estacion");
			COMPANY=intent.getStringExtra("compania");
			OLDODOMETER=intent.getStringExtra("odometro");
			GOAL=intent.getDoubleExtra("meta",0);
			LOCATION_SCENARIO=intent.getStringExtra("loc");
			FUELCOST=intent.getStringExtra("costo");
			TANKID=intent.getStringExtra("tankid");
			BRANCHID=intent.getStringExtra("branchid");
			GASTYPE=intent.getStringExtra("gastype");
			TRANSACTIONID=intent.getStringExtra("transactionid");
			MAXLITER=intent.getStringExtra("maxliter");
			
			USERTYPE=intent.getStringExtra("usertype");
			   if(USERTYPE.equals("1")){
		  	NEWODOMETER=intent.getStringExtra("nuevoodometro");
		    EditText odometer = (EditText) this.findViewById(R.id.odometer);
		    odometer.setText(NEWODOMETER);
		  	odometer.setEnabled(false);
			   }
			
			TextView plate = (TextView)findViewById(R.id.plate);
			plate.setText(PLATE);
			TextView remainingamount = (TextView)findViewById(R.id.remainingamount);
			remainingamount.setText(GOAL+" litros de saldo disponible.");
			try{
				Sentry.getContext().setUser(
						new UserBuilder().setEmail(USER).build()
				);
			}
			catch(Exception e){
				Log.e("ERROR SENTRY", e.toString());
				Sentry.getContext().setUser(
						new UserBuilder().setEmail("FuelLoadEvent").build()
				);
				Sentry.capture(e);
			}
		}
	

	
	 public void onSave(View view) {  
		  if (SystemClock.elapsedRealtime() - mLastClickTime < 1000){
	            return;
	        }
	        mLastClickTime = SystemClock.elapsedRealtime();
	        
		    Boolean TERMINUS=false;
		    EditText liter = (EditText) this.findViewById(R.id.liters);
		   // EditText costperliter = (EditText) this.findViewById(R.id.costperliter);
		    EditText odometer = (EditText) this.findViewById(R.id.odometer);
		    Spinner spinner = (Spinner) findViewById(R.id.fueltype);
		    
		    boolean CumpleCombustible=false;
		    boolean CumpleOdometro=false;
		    boolean CumpleLitros=false;
		    boolean CumpleCostoporLitros=false;
		    boolean CumpleOdometroActual=false;
		    boolean CumpleLitrosMax=false;
		   
		    Double Liter =0.0;
		    Double Odometer =0.0;
		    Double OdometerAvailable =0.0;
		    Double maxLiter =0.0;
		    try{
		 	 Liter = Double.parseDouble(liter.getText().toString());}catch(Exception e){Sentry.capture(e);}
		 	Double LiterAvailable = GOAL;
		 	if(Liter<LiterAvailable){CumpleCombustible=true;}
		    try{
			 Odometer = Double.parseDouble(odometer.getText().toString());}catch(Exception e){Sentry.capture(e);}
		    try{
		    OdometerAvailable = Double.parseDouble(OLDODOMETER);}catch(Exception e){Sentry.capture(e);}
		    try{
			maxLiter = Double.parseDouble(MAXLITER);}catch(Exception e){Sentry.capture(e);}
		    
		    
		 	if((Odometer>OdometerAvailable&&Odometer-OdometerAvailable <10000)||USERTYPE.equals("1")){CumpleOdometro=true;}
		
		 	
		 
		
			
			if(this.hasContent(liter)==true){CumpleLitros=true;
			if(Liter<maxLiter){CumpleLitrosMax=true;}
			}
	      	if(this.hasContent(odometer)){CumpleOdometroActual=true;}
	    
		    
	 		if(CumpleLitros&&CumpleOdometroActual&&CumpleCombustible==true&&CumpleOdometro==true&&CumpleLitrosMax==true){	
	 		//	databaseHelper.UpdateScannedItem(edittext.getText().toString().trim(),ID);
	 			
	 			try{
	 			
	 			 	String result = null;
	 			 	InputStream is = null;
	 			 	
	            
	                NumberFormat nf = NumberFormat.getInstance();
	                nf.setMaximumFractionDigits(0);// set as you need
	        		SimpleDateFormat sd = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	        		SimpleDateFormat sd2 = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
		    		String dat = sd.format(new Date()); 
		    		
	 			 	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
	 			    nameValuePairs.add(new BasicNameValuePair("Placa","'"+PLATE+"'"));
	 			    nameValuePairs.add(new BasicNameValuePair("TankID","'"+TANKID+"'"));
	 			    nameValuePairs.add(new BasicNameValuePair("BranchID","'"+BRANCHID+"'"));
	 			    nameValuePairs.add(new BasicNameValuePair("IDFactura","'"+TRANSACTIONID+"'"));
	 			  	nameValuePairs.add(new BasicNameValuePair("TipoCombustible","'"+spinner.getSelectedItem().toString()+"'"));
	 				nameValuePairs.add(new BasicNameValuePair("Combustible","'"+liter.getText().toString()+"'"));
	 			  	
	 			 	Double costLiter = Double.parseDouble(FUELCOST);
	 			 	Double liters = Double.parseDouble(liter.getText().toString());
	 			 	Double monto=costLiter*liters;
	 			    nameValuePairs.add(new BasicNameValuePair("Odometro","'"+odometer.getText().toString()+"'"));
	 				nameValuePairs.add(new BasicNameValuePair("Monto","'"+monto.toString()+"'"));
	 			    nameValuePairs.add(new BasicNameValuePair("Fecha","'"+dat+"'"));
	 			    nameValuePairs.add(new BasicNameValuePair("Usuario","'"+USER+"'"));
	 			    nameValuePairs.add(new BasicNameValuePair("FechaModificacion","'"+dat+"'")); 
	 			    nameValuePairs.add(new BasicNameValuePair("Location_Scenario","'"+LOCATION_SCENARIO+"'"));
	 			
	 			    StrictMode.setThreadPolicy(policy); 
	 			    
	 			    //http post
	 			    try{
	 			        	        HttpClient httpclient = new DefaultHttpClient();
	 			        	        HttpPost httppost = new HttpPost(MainMenu.URL+"insertFuelInvoice.php");
	 			        	        httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
	 			        	        HttpResponse response = httpclient.execute(httppost); 
	 			        	        HttpEntity entity = response.getEntity();
	 			        	        is = entity.getContent();
	 			        	        Log.e("log_tag", "connection success ");
	 			        	        TERMINUS=true;
	 			        	      //  Toast.makeText(getApplicationContext(), "pass", Toast.LENGTH_SHORT).show();
	 			        	   }
	 			        	catch(Exception e)
	 			        	{
	 			        	     TERMINUS=false;
	 			        	        Log.e("log_tag", "Error in http connection "+e.toString());
	 			        	        Toast.makeText(getApplicationContext(), "Error de conexión", Toast.LENGTH_SHORT).show();
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
	 			        	            //	JSONObject json_data = new JSONObject(result);
	 			        	            	
	 			        	                //CharSequence w= (CharSequence) json_data.get("re");
	 			        	  			//    Toast toasta=Toast.makeText(getApplicationContext(), w, 2000);
	 							  	      //  toasta.setGravity(Gravity.CENTER,0,0);
	 							  	    //    toasta.show();	
	 			        	     }
	 			        	catch(Exception e)
	 			        	   {
	 			        		
	 			        	        Log.e("log_tag", "Error parsing data "+e.toString());
								   Sentry.capture(e);
	 			        	     //   Toast.makeText(getApplicationContext(), "JsonArray fail", Toast.LENGTH_SHORT).show();
	 			        	    }}catch(Exception e){Sentry.capture(e);}
	 					        	
	 			if(TERMINUS){       
	 	      	Toast.makeText(getApplicationContext(), "Consumo registrado.", Toast.LENGTH_SHORT).show();	 
				Intent intentemoslo = new Intent(getApplicationContext() ,VoiceHelper.class);
   			intentemoslo.putExtra("voz","Consumo registrado");  
   			try {
  	     		
   			} catch (Exception e) {
   			    e.printStackTrace();
				Sentry.capture(e);
   			}
   			startService(intentemoslo);	
   		
   			}
   			
   			if(TERMINUS){     
   		
   			
   				 finish(); 
   			  
   			  }else{
   				 new AlertDialog.Builder(context)
   			    .setTitle("Error de conexión")
   			    .setMessage(" Verifique que esté conectado a la red e intente de nuevo.")
   			    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
   			        public void onClick(DialogInterface dialog, int which) { 
   			            // continue with delete
   			        }
   			     })
   			    .setNegativeButton("CANCELAR", new DialogInterface.OnClickListener() {
   			        public void onClick(DialogInterface dialog, int which) { 
   			            // do nothing
   			        }
   			     })
   			    .setIcon(android.R.drawable.ic_dialog_alert)
   			     .show();
   				 
   				   Intent intentemoslo = new Intent(getApplicationContext() ,VoiceHelper.class);
	     			   intentemoslo.putExtra("voz","Error de conexión. Verifique que esté conectado a la red e intente de nuevo.");  
	     			   startService(intentemoslo);	
	     				Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
	                	// Vibrate for 400 milliseconds
	                	v.vibrate(400);
   			  }
   			
	 		}else{
	 		     String message ="Se han encontrado los siguientes errores:";
	 		     if(CumpleLitros==false){message+="\n"+"Debe digitar el monto en litros para avanzar.";}
	 		 
	 		     if(CumpleOdometroActual==false){message+="\n"+"Debe digitar el odometro correcto para avanzar.";}
	 		     if(CumpleCombustible==false){message+="\n"+"Cantidad de combustible excede presupuesto.";}
	 		     if(CumpleOdometro==false){message+="\n"+"Odómetro incorrecto.";}
	 		    if(CumpleLitrosMax==false){message+="\n"+"Cantidad de combustible excede capacidad del tanque.";}
	 		    
	 		
				 new AlertDialog.Builder(context)
	   			    .setTitle("Error de ingreso")
	   			    .setMessage(message)
	   			    .setPositiveButton("OK", new DialogInterface.OnClickListener() {
	   			        public void onClick(DialogInterface dialog, int which) { 
	   			            // continue with delete
	   			        }
	   			     })
	   			    .setIcon(android.R.drawable.ic_dialog_alert)
	   			     .show();
	 		}
	   
	 }
	 
	 
	 
	  private class LoadFuelData extends AsyncTask<Void, Integer, Void>  
	    {  
	        //Before running code in separate thread  
	        @Override  
	        protected void onPreExecute()  
	        {  
	        	progressDialog = ProgressDialog.show(FuelLoadEvent.this,"Cargando...",  
	        		    "Guardando información, favor espere...", false, false);
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
	
	     	
	      	
	     
	            return null;  
	        }  
	  
	        //Update the progress  
	        @Override  
	        protected void onProgressUpdate(Integer... values)  
	        {  
	            //set the current progress of the progress dialog  
	            progressDialog.setProgress(values[0]);  
	  
	        }  
	  
	        //after executing the code in the thread  
	        @Override  
	        protected void onPostExecute(Void result)  
	        {  
	            //close the progress dialog  
	            progressDialog.dismiss();  
	            //initialize the View  

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
	 
}	
	 