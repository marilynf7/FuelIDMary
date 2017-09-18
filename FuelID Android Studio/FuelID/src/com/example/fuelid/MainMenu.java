package com.example.fuelid;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.lang.reflect.Field;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.Normalizer;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.logixsoft.fuelid.R;



import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;

import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.Fragment;
import android.app.FragmentManager;
import android.support.v4.app.ActionBarDrawerToggle;
import android.support.v4.widget.DrawerLayout;
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

public class MainMenu extends Activity {
	
	StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
	private static final Pattern KEYCODE_PATTERN = Pattern.compile("KEYCODE_(\\w)");
	private static final int REQUEST_CODE = 1234;
	public  static String URL="http://gps.mifibra.net/FUELIDPHP/";
	final Context context = this;
	public String[] estaciones;
	final String PREFS_NAME = "MyPrefsFile";
    private UserPermissionsDatabaseHelper databaseHelper;
	@Override
	protected void onCreate(Bundle savedInstanceState) {
	super.onCreate(savedInstanceState);

	  FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/MuseoSans300.otf");
		
	setContentView(R.layout.fuelmainmenu);
	ActionBar actionBar = getActionBar();
	actionBar.hide();
	
	// more stuff here...
	    actionBar.setSubtitle("Menú principal");
	    actionBar.setTitle("FuelID");
	    actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Crimson)));
		   actionBar.setDisplayShowTitleEnabled(false);  // required to force redraw, without, gray color
	    actionBar.setDisplayShowTitleEnabled(true);
	    actionBar.setDisplayHomeAsUpEnabled(false);
	    actionBar.show(); 
		}

	 public void onTransaction(View viewf) {
	   Intent intento = new Intent(getApplicationContext(), FuelOrderLoad.class);
	    startActivity(intento);	 
	 }
	 public void onTank(View view) {  
		  Intent intento = new Intent(getApplicationContext(), FuelOrderBalanceLoad.class);
		  startActivity(intento);
	 }
	 public void onRestriction(View view) {  
		 Intent intento = new Intent(getApplicationContext(), FuelLimitLoad.class);
         startActivity(intento);
	 }
	 public void onStats(View view) {
		 Intent intento = new Intent(getApplicationContext(), loginStats.class);
		 startActivity(intento);
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


}	
	 