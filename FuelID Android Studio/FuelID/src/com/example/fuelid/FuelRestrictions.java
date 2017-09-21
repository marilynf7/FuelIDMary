package com.example.fuelid;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.logixsoft.fuelid.R;

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

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;

//import io.sentry.context.Context;
import io.sentry.Sentry;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;

/**
 * Created by Julio Castro on 14/9/2017.
 */

public class FuelRestrictions extends Activity {

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    private static final Pattern KEYCODE_PATTERN = Pattern.compile("KEYCODE_(\\w)");
    private static final int REQUEST_CODE = 1234;
    public int COUNTER =0;
    public int GLOBALCOUNTER =0;
    public String PLATE,USER,STATION,COMPANY,BRAND,GOAL,CONSUMPTION,ODOMETER,FECHA,FECHALU,FUELCOST,TANKID,LOCATION_SCENARIO,USERTYPE,NEWODOMETER,GASTYPE,BRANCHID,MAXLITER,TRANSACTIONID;
    public double AVAILABLE;
    public String Scanning="";
    public boolean EXISTRES = false;

    public int execute=0;

    public int timerstarted=0;
    public Boolean timercompleted=false;
    public Boolean serverqueried=false;
    public boolean EDITANDO = false;
    final Context context = this;
    final String PREFS_NAME = "MyPrefsFile";
    private ProgressDialog progressDialog;
    private UserPermissionsDatabaseHelper databaseHelper;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/MuseoSans300.otf");


        setContentView(R.layout.fuelrestrictions);
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
        PLATE=intent.getStringExtra("plate");
        COMPANY=intent.getStringExtra("company");
        BRAND=intent.getStringExtra("car");
        USER=intent.getStringExtra("user");

        Spinner spinner = (Spinner) findViewById(R.id.tclist);
        ArrayAdapter<String> adapter;
        List<String> list;

        list = new ArrayList<String>();
        list.add("DIESEL");
        list.add("REGULAR");
        list.add("SUPER");
        adapter = new ArrayAdapter<String>(getApplicationContext(),
                android.R.layout.simple_spinner_item, list);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinner.setAdapter(adapter);

        TextView plate = (TextView)findViewById(R.id.plate);
        plate.setText(PLATE);
        TextView car = (TextView)findViewById(R.id.car);
        car.setText(BRAND);
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
            progressDialog = ProgressDialog.show(FuelRestrictions.this,"Cargando...",
                    "Cargando información, favor espere...", false, false);
            EditText Disponible = (EditText) findViewById(R.id.limite);
            Button but = (Button) findViewById(R.id.btneditSave);
            but.setText("Editar");
            Disponible.setFocusable(false);
            Disponible.setClickable(false);
            Disponible.setLongClickable(false);
            EDITANDO = false;
            Log.e("CERRADO","---------------");
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
                    Sentry.capture(e);
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
                    Log.e("RESULT TODO",result);
                }
                catch(Exception e)
                {
                    Log.e("log_tag", "Error converting result "+e.toString());
                    Sentry.capture(e);
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
                                String dateLU=no.getString("FechaLU");
                                FECHALU=dateLU;
                                String odometer=no.getString("Odometro");
                                ODOMETER=odometer;
                                EXISTRES = true;
                            }catch(Exception e){Sentry.capture(e);}
                            counter++;

                        }catch(Exception e)
                        {
                            Sentry.capture(e);
                        }
                    }
                }
                catch(JSONException e)
                {
                    Log.e("log_tag", "Error parsing data "+e.toString()+Integer.toString(counter));
                    Sentry.capture(e);
                    try{
                        JSONObject no =  new JSONObject(result);
                        String res=no.getString("re");
                        if(res.equals("Record is not available")){
                            EXISTRES = false;
                        }
                    }
                    catch(Exception x){
                        Log.e("ERROR C2",x.toString());
                        Sentry.capture(e);
                    }
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Sentry.capture(e);
                }
            }catch(Exception e)
            {
                Log.e("log_tag", "Active el VPN para aplicar esta opción.");
                Sentry.capture(e);
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
            progressDialog.dismiss();
            if(EXISTRES) {
                try {
                    Spinner spinner = (Spinner) findViewById(R.id.tclist);
                    TextView tctext = (TextView) findViewById(R.id.tctext);
                    spinner.setVisibility(View.GONE);
                    tctext.setVisibility(View.GONE);
                    EditText Disponible = (EditText) findViewById(R.id.limite);
                    Double goal = 0.0;
                    try {
                        goal = Double.parseDouble(GOAL);
                    } catch (Exception e) {
                        Sentry.capture(e);
                    }
                    Double consumption = 0.0;
                    try {
                        consumption = Double.parseDouble(CONSUMPTION);
                    } catch (Exception e) {
                        Sentry.capture(e);
                    }
                    double available = (goal - consumption);
                    DecimalFormat f = new DecimalFormat("##.00");
                    AVAILABLE = goal;
                    Disponible.setText(String.valueOf(AVAILABLE));
                    Disponible.setClickable(false);
                    Disponible.setFocusableInTouchMode(false);
                    TextView Fecha = (TextView) findViewById(R.id.date);
                    //Fecha.setText("Ultima transacción " + FECHA.substring(0, 10));
                } catch (Exception e) {
                    Sentry.capture(e);
                }
            }
            else{
                Spinner spinner = (Spinner) findViewById(R.id.tclist);
                TextView tctext = (TextView) findViewById(R.id.tctext);
                spinner.setVisibility(View.VISIBLE);
                tctext.setVisibility(View.VISIBLE);
                EditText Disponible = (EditText) findViewById(R.id.limite);
                Disponible.setText("");
                Disponible.setClickable(false);
                Disponible.setFocusableInTouchMode(false);
                TextView Fecha = (TextView) findViewById(R.id.date);
                Fecha.setText("No se ha registrado restricción para este período.");
                Button but = (Button) findViewById(R.id.btneditSave);
                but.setText("Agregar");
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

    public void saveLoad(View view){
        if(EDITANDO){
           try{
               EditText Disponible = (EditText) findViewById(R.id.limite);
               if(Disponible.getText().toString().length()>0){
                   if(!EXISTRES){
                       try {
                           Spinner spinner = (Spinner) findViewById(R.id.tclist);
                           new InsertFuelData(Disponible.getText().toString(),spinner.getSelectedItem().toString()).execute();
                       }
                       catch(Exception e){
                           Log.e("Error EJ",e.toString());
                           Sentry.capture(e);
                       }
                   }
                   else{
                       try {
                           new UpdateFuelData(Disponible.getText().toString()).execute();
                       }
                       catch(Exception e){
                           Log.e("Error EJ2",e.toString());
                           Sentry.capture(e);
                       }
                   }
                   Button but = (Button) findViewById(R.id.btneditSave);
                   but.setText("Editar");
                   Disponible.setFocusable(false);
                   Disponible.setClickable(false);
                   Disponible.setLongClickable(false);
                   EDITANDO = false;
                   Log.e("CERRADO","---------------");
               }
               else{
                   Toast.makeText(getApplicationContext(), "Debe ingresar un monto para la restricción.", Toast.LENGTH_SHORT).show();
               }
           }
           catch (Exception e){
               Log.e("ERROR CAMPO 1",e.toString());
               Sentry.capture(e);
           }
        }
        else{
            try{
                EditText Disponible = (EditText) findViewById(R.id.limite);
                Button but = (Button) findViewById(R.id.btneditSave);
                but.setText("Guardar");
                Disponible.setFocusable(true);
                Disponible.setClickable(true);
                Disponible.setFocusableInTouchMode(true);
                Disponible.setLongClickable(true);
                EDITANDO = true;
                Log.e("ABIERTO","---------------");
            }
            catch (Exception e){
                Log.e("ERROR CAMPO",e.toString());
                Sentry.capture(e);
            }
        }
    }

    private class UpdateFuelData extends AsyncTask<Void, Integer, Void>
    {
        String RES = "0";
        String newVal="";
        public UpdateFuelData(String newValue){
            super();
            newVal = newValue;
            String RES = "0";
        }
        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(FuelRestrictions.this,"Actualizando...",
                    "Actualizando información, favor espere...", false, false);
            EditText Disponible = (EditText) findViewById(R.id.limite);
            Button but = (Button) findViewById(R.id.btneditSave);
            but.setEnabled(false);
            Disponible.setEnabled(false);
            Log.e("ONPRE","---------------");
        }
        @Override
        protected Void doInBackground(Void... params)
        {
            StrictMode.setThreadPolicy(policy);
            try{
                String result = null;
                InputStream is = null;

                String v1 = "'"+COMPANY+"'";
                String v2 = "'"+FECHALU+"'";
                String v3 = "'"+newVal+"'";
                String v4 = "'"+PLATE+"'";

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("company",v1));
                nameValuePairs.add(new BasicNameValuePair("date",v2));
                nameValuePairs.add(new BasicNameValuePair("newval",v3));
                nameValuePairs.add(new BasicNameValuePair("plate",v4));
                Log.e("VALUES",nameValuePairs.toString());
                try
                {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(MainMenu.URL+"updateAvailableFuel.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    Log.e("log_tag", "connection success ");
                }
                catch(Exception e)
                {
                    Log.e("log_tag", "Error in http connection "+e.toString());
                    Sentry.capture(e);
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
                    Log.e("RESULT", result);
                }
                catch(Exception e)
                {
                    Log.e("log_tag", "Error converting result "+e.toString());
                    Sentry.capture(e);
                }
                int counter=0;
                try{
                    JSONArray jArray = new JSONArray(result);
                    for(int i=0;i<jArray.length();i++)
                    {
                        try{
                            JSONObject no = jArray.getJSONObject(i);
                            try{
                                RES = no.getString("res");
                            }catch(Exception e){Sentry.capture(e);}
                            counter++;
                        }catch(Exception e)
                        {
                            Sentry.capture(e);
                        }
                    }
                }
                catch(JSONException e)
                {
                    Log.e("log_tag", "Error parsing data "+e.toString()+Integer.toString(counter));
                    Sentry.capture(e);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Sentry.capture(e);
                }
            }catch(Exception e)
            {
                Log.e("log_tag", "Active el VPN para aplicar esta opción.");
                Sentry.capture(e);
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            progressDialog.setProgress(values[0]);
        }
        @Override
        protected void onPostExecute(Void result)
        {
            EditText Disponible = (EditText) findViewById(R.id.limite);
            Button but = (Button) findViewById(R.id.btneditSave);
            but.setEnabled(true);
            Disponible.setEnabled(true);
            progressDialog.dismiss();
            Log.e("RES-",RES);
            try{
                if(RES.equals("1")) {
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Restricción actualizada", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error al actualizar, intentelo de nuevo en unos minutos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            catch(Exception e){
                Log.e("ERROR POST",e.toString());
                Sentry.capture(e);
            }
        }
    }


    private class InsertFuelData extends AsyncTask<Void, Integer, Void>
    {
        String RES = "0";
        String newVal="";
        String TC="";
        public InsertFuelData(String newValue, String inTC){
            super();
            newVal = newValue;
            TC = inTC;
            String RES = "0";
        }
        @Override
        protected void onPreExecute()
        {
            progressDialog = ProgressDialog.show(FuelRestrictions.this,"Agregando...",
                    "Agregando información, favor espere...", false, false);
            EditText Disponible = (EditText) findViewById(R.id.limite);
            Button but = (Button) findViewById(R.id.btneditSave);
            but.setEnabled(false);
            Disponible.setEnabled(false);
            Log.e("ONPREI","---------------");
        }
        @Override
        protected Void doInBackground(Void... params)
        {
            StrictMode.setThreadPolicy(policy);
            try{
                String result = null;
                InputStream is = null;
                SimpleDateFormat sdfM = new SimpleDateFormat("MM");
                SimpleDateFormat sdfY = new SimpleDateFormat("yyyy");

                String MONTH = sdfM.format(new Date());
                String YEAR = sdfY.format(new Date());

                String v1 = "'"+PLATE+"'";
                String v2 = "'"+YEAR+"'";
                String v3 = "'"+MONTH+"'";
                String v4 = "'"+TC+"'";
                String v5 = "'"+"0"+"'";
                String v6 = "'"+newVal+"'";
                String v7 = "'"+USER+"'";
                String v8 = "'"+COMPANY+"'";

                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("plate",v1));
                nameValuePairs.add(new BasicNameValuePair("year",v2));
                nameValuePairs.add(new BasicNameValuePair("month",v3));
                nameValuePairs.add(new BasicNameValuePair("tc",v4));
                nameValuePairs.add(new BasicNameValuePair("reco",v5));
                nameValuePairs.add(new BasicNameValuePair("newval",v6));
                nameValuePairs.add(new BasicNameValuePair("user",v7));
                nameValuePairs.add(new BasicNameValuePair("company",v8));
                Log.e("VALUES",nameValuePairs.toString());
                try
                {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(MainMenu.URL+"insertAvailableFuel.php");
                    httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                    HttpResponse response = httpclient.execute(httppost);
                    HttpEntity entity = response.getEntity();
                    is = entity.getContent();
                    Log.e("log_tag", "connection success ");
                }
                catch(Exception e)
                {
                    Log.e("log_tag", "Error in http connection "+e.toString());
                    Sentry.capture(e);
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
                    Log.e("RESULT", result);
                }
                catch(Exception e)
                {
                    Log.e("log_tag", "Error converting result "+e.toString());
                    Sentry.capture(e);
                }
                int counter=0;
                try{
                    JSONArray jArray = new JSONArray(result);
                    for(int i=0;i<jArray.length();i++)
                    {
                        try{
                            JSONObject no = jArray.getJSONObject(i);
                            try{
                                RES = no.getString("res");
                            }catch(Exception e){Sentry.capture(e);}
                            counter++;
                        }catch(Exception e)
                        {
                            Sentry.capture(e);
                        }
                    }
                }
                catch(JSONException e)
                {
                    Log.e("log_tag", "Error parsing data "+e.toString()+Integer.toString(counter));
                    Sentry.capture(e);
                } catch (IllegalArgumentException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    Sentry.capture(e);
                }
            }catch(Exception e)
            {
                Log.e("log_tag", "Active el VPN para aplicar esta opción.");
                Sentry.capture(e);
            }
            return null;
        }
        @Override
        protected void onProgressUpdate(Integer... values)
        {
            progressDialog.setProgress(values[0]);
        }
        @Override
        protected void onPostExecute(Void result)
        {
            EditText Disponible = (EditText) findViewById(R.id.limite);
            Button but = (Button) findViewById(R.id.btneditSave);
            but.setEnabled(true);
            Disponible.setEnabled(true);
            progressDialog.dismiss();
            Log.e("RES-",RES);
            try{
                if(RES.equals("1")) {
                    Spinner spinner = (Spinner) findViewById(R.id.tclist);
                    TextView tctext = (TextView) findViewById(R.id.tctext);
                    spinner.setVisibility(View.GONE);
                    tctext.setVisibility(View.GONE);
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Restricción creada", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
                else{
                    runOnUiThread(new Runnable() {
                        public void run() {
                            Toast.makeText(getApplicationContext(), "Error al agregar, intentelo de nuevo en unos minutos", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            }
            catch(Exception e){
                Log.e("ERROR POST",e.toString());
                Sentry.capture(e);
            }
        }
    }
}
