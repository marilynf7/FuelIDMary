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
	public String IDTANK,COMPANY,AMOUNT,SIZE,TANKNAME,LASTUPLOAD;
    public  Timer timer = new Timer();
	public int PRIM = 0;
    
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);
	  FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/MuseoSans300.otf");
	setContentView(R.layout.fuelidtank);
	ActionBar actionBar = getActionBar();
	actionBar.hide();
	    actionBar.setSubtitle("Saldo tanque");
	    actionBar.setTitle("FuelID");
	    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Crimson)));
	    actionBar.setDisplayShowTitleEnabled(false);  // required to force redraw, without, gray color
	    actionBar.setDisplayShowTitleEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(false);
	    actionBar.show();
		Intent intent = getIntent();
		IDTANK=intent.getStringExtra("idtank");
        COMPANY=intent.getStringExtra("company");
		Log.e("IDTANK ----------- ",IDTANK);
        Log.e("COMPANY ----------- ",COMPANY);
	    SwitchVisualization(true);
	}
	
	  @Override
	 public boolean onCreateOptionsMenu(Menu menu) {
	        return super.onCreateOptionsMenu(menu);
	    }
	   

	 @Override
	 public boolean onOptionsItemSelected(MenuItem item) {
	            return super.onOptionsItemSelected(item);
	    }
   
	    private boolean hasContent(EditText et) {
		       return (et.getText().toString().trim().length() > 0);
		}

	 @Override
	    public void onBackPressed() {
			AlertDialog.Builder builder = new AlertDialog.Builder(this);
  		builder.setMessage("¿Esta seguro de que desea salir? ")
  		   .setCancelable(false)
  		   .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
  		       public void onClick(DialogInterface dialog, int id) {
  		    	 SwitchVisualization(false);
				 PRIM = 0;
  		   		 //this.finalize();
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
			ProgressDialog dialog;
	        @Override
	        protected void onPreExecute()  
	        {
				if(PRIM <= 0) {
					PRIM=1;
					if(!((Activity) context).isFinishing())
					{
						dialog = ProgressDialog.show(FuelTank.this, "",
								"Cargando. por favor espere...", true);
						dialog.show();
					}
				}
	        }
	        @Override  
	        protected Void doInBackground(Void... params)  
	        {
	        	StrictMode.setThreadPolicy(policy);
	     		try{
					String result = null;
					InputStream is = null;
					String v1 = "'"+IDTANK+"'";
					String v2 = "'"+COMPANY+"'";
					ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
					nameValuePairs.add(new BasicNameValuePair("tankid",v1));
					nameValuePairs.add(new BasicNameValuePair("company",v2));
					try
					{
								HttpClient httpclient = new DefaultHttpClient();
								HttpPost httppost = new HttpPost(MainMenu.URL+"getFuelTankAmount.php");
								httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
								HttpResponse response = httpclient.execute(httppost);
								HttpEntity entity = response.getEntity();
								is = entity.getContent();
								Log.e("log_tag", "connection success ");
					}
				catch(Exception e)
					{
								Log.e("log_tag", "Error in http connection "+e.toString());
					}
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
								Log.e("RESULT",result);
					}
					catch(Exception e)
					{
							   Log.e("log_tag", "Error converting result "+e.toString());
					}
					int counter=0;
					try{
						JSONArray jArray = new JSONArray(result);
						for(int i=0;i<jArray.length();i++)
						{
							try
							{
								JSONObject no =jArray.getJSONObject(i);
								try{
									String am=no.getString("TankAmount");
									String max=no.getString("TankSize");
									String nam=no.getString("TankDescription");
									String last=no.getString("LastUpdate");
									AMOUNT =am;
									SIZE = max;
									TANKNAME = nam;
									LASTUPLOAD = last;
									Log.e("A-",AMOUNT);
									Log.e("S-",SIZE);
									Log.e("TN-",TANKNAME);
									Log.e("LU-",LASTUPLOAD);
								}catch(Exception e){
									Log.e("ERROR OBJ",e.toString());
								}
								counter++;
							}catch(Exception e)
							{
							}
						}
					}
					catch(Exception e)
					{
							Log.e("ERROR1", "Error parsing data "+e.toString()+Integer.toString(counter));
					}
      			}catch(Exception e)
      	    	{
      		        Log.e("log_tag", "Active el VPN para aplicar esta opción.");
      			}
	            return null;  
	        }
	        @Override  
	        protected void onProgressUpdate(Integer... values)
	        {
	        }
	        @Override
	        protected void onPostExecute(Void result)  
	        {
	            Double maxval =0.0;
	          	try{
	            	maxval = Double.parseDouble(SIZE);
	          	}catch(Exception e){}
				Double current =0.0;
	        	try{
	        		current = Double.parseDouble(AMOUNT);
				}catch(Exception e){}
				Double percentage = 0.0;
				try{
					percentage = current*100/maxval;
				}catch(Exception e){}
	        	DecimalFormat f = new DecimalFormat("##.00");
	        	LinearLayout Tanque = (LinearLayout)findViewById(R.id.bigtank);
	         	TextView Tankheader = (TextView)findViewById(R.id.tankname);
	          	Tankheader.setText(TANKNAME);
	         	TextView CurrentAmount = (TextView)findViewById(R.id.currenttank);
	         	CurrentAmount.setText(f.format(current)+"L/"+f.format(percentage)+"%");
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
				try {
					dialog.cancel();
				}
				catch(Exception E){Log.e("Dialog",E.toString());}
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
	 		    },10,10000);
			}
			else{
			}
		  }
}	
	 