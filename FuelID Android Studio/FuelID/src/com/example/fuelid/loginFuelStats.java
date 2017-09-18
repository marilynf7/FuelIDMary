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
import android.os.Handler;
import android.os.StrictMode;
import android.util.Log;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

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
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Timer;
import java.util.TimerTask;
import java.util.regex.Pattern;

/**
 * Created by Julio Castro on 18/9/2017.
 */

public class loginFuelStats extends Activity {

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
    public String PLATE,COMPANY,USER,AMOUNT,SIZE,TANKNAME,LASTUPLOAD,MONTH,YEAR,COMBUSTIBLE,RECCORIDO,RENDIMIENTO,META;
    public Timer timer = new Timer();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/MuseoSans300.otf");
        setContentView(R.layout.login_fuel_stats);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        actionBar.setSubtitle("Rendimiento de la unidad");
        actionBar.setTitle("FuelID");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Crimson)));
        actionBar.setDisplayShowTitleEnabled(false);  // required to force redraw, without, gray color
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.show();
        Intent intent = getIntent();
        PLATE=intent.getStringExtra("plate");
        COMPANY=intent.getStringExtra("company");
        USER = intent.getStringExtra("user");
        Log.e("PLATE ----------- ",PLATE);
        Log.e("COMPANY ----------- ",COMPANY);
        Log.e("USER ----------- ",USER);
        TextView plate = (TextView) findViewById(R.id.platestat);
        plate.setText(PLATE);
        new LoadStatsData().execute();
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
        switch (item.getItemId()) {
            case R.id.refresh:
                new LoadStatsData().execute();
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



    private class LoadStatsData extends AsyncTask<Void, Integer, Void>
    {
        ProgressDialog dialog;
        @Override
        protected void onPreExecute()
        {
                if(!((Activity) context).isFinishing())
                {
                    dialog = ProgressDialog.show(loginFuelStats.this, "",
                            "Cargando. por favor espere...", true);
                    dialog.show();
                }
        }
        @Override
        protected Void doInBackground(Void... params)
        {
            StrictMode.setThreadPolicy(policy);
            SimpleDateFormat sdfM = new SimpleDateFormat("MM");
            SimpleDateFormat sdfY = new SimpleDateFormat("yyyy");

            MONTH = sdfM.format(new Date());
            YEAR = sdfY.format(new Date());
            try{
                String result = null;
                InputStream is = null;
                String v1 = "'"+MONTH+"'";
                String v2 = "'"+YEAR+"'";
                String v3 = "'"+COMPANY+"'";
                String v4 = "'"+PLATE+"'";
                ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
                nameValuePairs.add(new BasicNameValuePair("month",v1));
                nameValuePairs.add(new BasicNameValuePair("year",v2));
                nameValuePairs.add(new BasicNameValuePair("company",v3));
                nameValuePairs.add(new BasicNameValuePair("plate",v4));
                try
                {
                    HttpClient httpclient = new DefaultHttpClient();
                    HttpPost httppost = new HttpPost(MainMenu.URL+"getLastFuelPerformanceUnit.php");
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
                                String comb=no.getString("COMBUSTIBLE");
                                String reco=no.getString("RECORRIDO");
                                String rendi=no.getString("RENDIMIENTO");
                                String met=no.getString("METAREND");
                                COMBUSTIBLE =comb;
                                RECCORIDO = reco;
                                RENDIMIENTO = rendi;
                                META = met;
                                if(RENDIMIENTO.length()>4){
                                    RENDIMIENTO=RENDIMIENTO.substring(0,4);
                                }
                                if(META.length()>4){
                                    META=META.substring(0,4);
                                }
                                Log.e("COMB-",COMBUSTIBLE);
                                Log.e("RECORR-",RECCORIDO);
                                Log.e("RENDIMI-",RENDIMIENTO);
                                Log.e("META-",META);
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
            TextView plate = (TextView) findViewById(R.id.resStat);
            plate.setText("Rendimiento: "+RENDIMIENTO+"% - Meta: "+META+"%");
            try{
                Double metad = Double.parseDouble(META);
                Double rendid = Double.parseDouble(RENDIMIENTO);
                ImageView image = (ImageView) findViewById(R.id.iconstat);
                TextView vere = (TextView) findViewById(R.id.vere);
                if(metad-rendid>0){
                    image.setImageResource(R.drawable.error3);
                    vere.setText("No se ha cumplido la meta!");
                }
                else {
                    vere.setText("Se ha cumplido la meta!");
                    image.setImageResource(R.drawable.check2);
                }
            }
            catch (Exception e){Log.e("Dialog",e.toString());}
            try {
                dialog.cancel();
            }
            catch(Exception E){Log.e("Dialog",E.toString());}
        }
    }

    public void back2 (View view){
        onBackPressed();
    }
}
