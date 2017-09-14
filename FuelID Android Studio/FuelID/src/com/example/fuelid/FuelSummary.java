package com.example.fuelid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
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
import android.os.Vibrator;
import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.app.ProgressDialog;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class FuelSummary extends Activity {
	
	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	private static final Pattern KEYCODE_PATTERN = Pattern.compile("KEYCODE_(\\w)");
	private static final int REQUEST_CODE = 1234;
	public int COUNTER =0;
	public int GLOBALCOUNTER =0;
	public String PLATE,USER,STATION,COMPANY,BRAND,GOAL,CONSUMPTION,ODOMETER,FECHA,FUELCOST,TANKID,LOCATION_SCENARIO,USERTYPE,NEWODOMETER,GASTYPE,BRANCHID,MAXLITER,TRANSACTIONID;
	public double AVAILABLE;
	public String Scanning="";

	public int execute=0;
	
	public int timerstarted=0;
	public Boolean timercompleted=false;
	public Boolean serverqueried=false;
	final Context context = this;
	final String PREFS_NAME = "MyPrefsFile";
	  private ProgressDialog progressDialog;
    private UserPermissionsDatabaseHelper databaseHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	  FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/MuseoSans300.otf");
		
		
	setContentView(R.layout.fueldata);
	ActionBar actionBar = getActionBar();
	actionBar.hide();
	
	// more stuff here...

	    actionBar.setSubtitle("Disponible");
	    actionBar.setTitle("Fuel ID");
	    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Crimson)));
	    actionBar.setDisplayShowTitleEnabled(false);  // required to force redraw, without, gray color
	    actionBar.setDisplayShowTitleEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(false);
	    actionBar.show(); 
	    
	    Intent intent = getIntent();
		PLATE=intent.getStringExtra("placa");
		USER=intent.getStringExtra("usuario");
		USERTYPE=intent.getStringExtra("tipousuario");
		STATION=intent.getStringExtra("estacion");
		COMPANY=intent.getStringExtra("compania");
		BRAND=intent.getStringExtra("marca");
		MAXLITER=intent.getStringExtra("maxliter");
		LOCATION_SCENARIO=intent.getStringExtra("loc");
		FUELCOST=intent.getStringExtra("costo");
		TANKID=intent.getStringExtra("tankid");
		BRANCHID=intent.getStringExtra("branchid");
		GASTYPE=intent.getStringExtra("gastype");
		TRANSACTIONID=intent.getStringExtra("transactionid");
		
	    TextView plate = (TextView)findViewById(R.id.plate);
	    plate.setText(PLATE);
	  	TextView username = (TextView)findViewById(R.id.username);
	  	username.setText("Usuario:"+USER);
	  	TextView fuelstationid = (TextView)findViewById(R.id.fuelstationid);
	  	fuelstationid.setText(STATION);
	  	TextView autobrand = (TextView)findViewById(R.id.autobrand);
	  	autobrand.setText(BRAND);
	
		new LoadFuelData().execute();  
		
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
	 
	 
	  private class LoadFuelData extends AsyncTask<Void, Integer, Void>  
	    {  
	        //Before running code in separate thread  
	        @Override  
	        protected void onPreExecute()  
	        {  
	        	progressDialog = ProgressDialog.show(FuelSummary.this,"Cargando...",  
	        		    "Cargando información, favor espere...", false, false);
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
		    		
        	    	
        	    	String v1 = "'"+COMPANY+"'";
        	    	String v2 = "'"+MONTH+"'";
        	    	String v3 = "'"+YEAR+"'";
        	    	String v4 = "'"+PLATE+"'";
        		

        		
        	    	ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
        	      	nameValuePairs.add(new BasicNameValuePair("company",v1));
        	      	nameValuePairs.add(new BasicNameValuePair("month",v2));
        	      	nameValuePairs.add(new BasicNameValuePair("year",v3));
         	      	nameValuePairs.add(new BasicNameValuePair("plate",v4));
      
        	//apartir de aqui carga ruta
        		try
        		{
        	    	        HttpClient httpclient = new DefaultHttpClient();
        	    	        HttpPost httppost = new HttpPost(MainMenu.URL+"checkAvailableFuel.php");
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
        	    		    String placa=no.getString("Placa");
        	    		    try{
        	    		    PLATE=placa;
        	    		    String goal=no.getString("Meta");
        	    		    GOAL=goal;
        	    		    String consumption=no.getString("Consumo");
        	    		    CONSUMPTION=consumption;
        	    		    String date=no.getString("Fecha");
        	    		    FECHA=date;
        	    		    String odometer=no.getString("Odometro");
        	                ODOMETER=odometer;
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
	            progressDialog.setProgress(values[0]);  
	  
	        }  
	  
	        //after executing the code in the thread  
	        @Override  
	        protected void onPostExecute(Void result)  
	        {  
	            //close the progress dialog  
	            progressDialog.dismiss();  
	            //initialize the View  
	            try{
	          	TextView Disponible = (TextView)findViewById(R.id.available);
	            Double goal =0.0;
	          	try{
	           goal = Double.parseDouble(GOAL);
	            }catch(Exception e){}
	        	Double consumption =0.0;
	        	try{
	        	consumption = Double.parseDouble(CONSUMPTION);}catch(Exception e){}
	          	double available= (goal-consumption);
	        	DecimalFormat f = new DecimalFormat("##.00");
	        	
	         
	          	AVAILABLE=available;
	          	Disponible.setText(AVAILABLE+" L");
	          	TextView Fecha = (TextView)findViewById(R.id.date);
	          	Fecha.setText(" "+FECHA.substring(0, 10));}catch(Exception e){}
	            if(USERTYPE.equals("0")){}else{
	            new QueryRecorridoExternal().execute();}
	        }
	    }
	  
	 
	 
	 public void onSave(View view) {  
		 if(serverqueried||USERTYPE.equals("0")){
	     	TextView Disponible = (TextView)findViewById(R.id.available);
		    if(Disponible.getText().toString().equals("...")){
		    Toast.makeText(getApplicationContext(), "Información de combustible disponible no encontrada.Refresque la pantalla.", Toast.LENGTH_SHORT).show();
		    }else{
		    Intent intento = new Intent(getApplicationContext(), FuelLoadEvent.class);
		    intento.putExtra("placa",PLATE);
		    intento.putExtra("usuario",USER);
		    intento.putExtra("estacion",STATION);
		    intento.putExtra("compania",COMPANY);
		    intento.putExtra("odometro",ODOMETER);
		    intento.putExtra("usertype",USERTYPE);
		    
		    if(USERTYPE.equals("1")){
		    intento.putExtra("nuevoodometro",NEWODOMETER);}
		    intento.putExtra("maxliter",MAXLITER);
		    intento.putExtra("meta",AVAILABLE);
		    intento.putExtra("costo",FUELCOST);
		    intento.putExtra("loc",LOCATION_SCENARIO);
		    intento.putExtra("tankid",TANKID);
		    intento.putExtra("branchid",BRANCHID);
		    intento.putExtra("gastype",GASTYPE);
		
		    intento.putExtra("transactionid",TRANSACTIONID);
		    
		    startActivity(intento);	  
		    }
		    }
			}	
	 
	 public double RoundTwoDecimals(double d){
		 DecimalFormat two = new DecimalFormat("#.##");
		 return Double.valueOf(two.format(d));
		 
	 }
	 
	  @Override
	  public void onBackPressed() {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
		builder.setMessage("¿Esta seguro de que desea salir? ")
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

	  private class QueryRecorridoExternal extends AsyncTask<Void, Integer, Void>  
	    {  
	        //Before running code in separate thread  
	        @Override  
	        protected void onPreExecute()  
	        {  
	       // 	progressDialog = ProgressDialog.show(FuelSummary.this,"Cargando...",  
	      //  		    "Cargando información, favor espere...", false, false);
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
	            String recorrido = "";
	            Double odometro=0.0;
	            Double odometroviejo=0.0;
	        	 StrictMode.setThreadPolicy(policy); 
	        	 try {

	 	            // SET CONNECTIONSTRING
	 	            Class.forName("net.sourceforge.jtds.jdbc.Driver").newInstance();
	 	            String username = "logixsoft";
	 	            String password = "Sx#45$lent";
	 	            String database = "/DFS";
	 	            String ip = "138.94.58.169:1434";
	 	            Connection DbConn = DriverManager.getConnection("jdbc:jtds:sqlserver://"+ip+database+";user=" + username + ";password=" + password+";");

	 	            Log.w("Connection","open");
	 	            Statement stmt = DbConn.createStatement();
	 	            String placa =PLATE;
	 	            
	 	           SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
	 	            Date startDate = df.parse(FECHA);
	 	       	SimpleDateFormat sd = new SimpleDateFormat("dd/MM/yyyy HH:mm:ss"); 
	 	            String inicio=sd.format(startDate);   
	 	            //"28/02/2017 00:00";
	 	            
	 	   
	    		String dat = sd.format(new Date()); 
	 	            
	 	            String fin=dat;
	 	           String query="SELECT SUM(distancia) AS 'RECORRIDO' FROM REGISTRO r JOIN VEHICULO v ON r.ID_VEHICULO = v.ID_VEHICULO WHERE PLACA = '"+placa+"' AND FECHA_HORA BETWEEN '"+inicio+"' and '"+fin+"'";
		 	         
	 	            ResultSet reset = stmt.executeQuery(query);
	 	           while (reset.next()) {
	 	               recorrido=reset.getString("RECORRIDO");
	 	            }
	 	          try{
	 				 odometro = Double.parseDouble(recorrido);}catch(Exception e){}
		          try{
		 		     odometroviejo = Double.parseDouble(ODOMETER);}catch(Exception e){}
		
		          serverqueried=true;
		          try{
		          NEWODOMETER=(odometro+odometroviejo)+"";
		          }catch(Exception e){
		        	USERTYPE="0";
		        	  
		          }
	 	            DbConn.close();

	 	        } catch (Exception e)
	 	        {
	 	            Log.e("Error connection","-" + e.getMessage()+"-"+e.toString());
	 	        }
	     	
	    //    	    Log.e("--*RESULTADOS: ",error);
	            return null;  
	        }  
	  
	        //Update the progress  
	        @Override  
	        protected void onProgressUpdate(Integer... values)  
	        {  
	            //set the current progress of the progress dialog  
	      //      progressDialog.setProgress(values[0]);  
	  
	        }  
	  
	        //after executing the code in the thread  
	        @Override  
	        protected void onPostExecute(Void result)  
	        {  
	            //close the progress dialog  
	         //   progressDialog.dismiss();  
	            //initialize the View    
	        }
	        
	      
	    
	    }
	  
	  
	  
	 
	 
}	
	 