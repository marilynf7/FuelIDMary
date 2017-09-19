package com.example.fuelid;

import android.app.ActionBar;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.os.StrictMode;
import android.os.Vibrator;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
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
import java.text.Normalizer;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


//import io.sentry.context.Context;
import io.sentry.Sentry;
import io.sentry.event.BreadcrumbBuilder;
import io.sentry.event.UserBuilder;


/**
 * Created by Julio Castro on 18/9/2017.
 */


public class loginStats extends Activity {

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    private static final Pattern KEYCODE_PATTERN = Pattern.compile("KEYCODE_(\\w)");
    public int COUNTER =0;
    public String USER,PLATE,COMPANY,CARTYPE,LOCATION_SCENARIO,TANKID,TIPOUSUARIO,USERNAME,MAXLITER;
    public String Scanning="";
    public int execute=0;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/MuseoSans300.otf");
        setContentView(R.layout.loginstats);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        actionBar.setSubtitle("Consulta de rendimiento");
        actionBar.setTitle("FuelID");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Crimson)));
        actionBar.setDisplayShowTitleEnabled(false);
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.show();
        Sentry.getContext().setUser(
                new UserBuilder().setEmail("loginStats").build()
        );
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        execute=1;
        String key = KeyEvent.keyCodeToString(keyCode);
        Matcher matcher = KEYCODE_PATTERN.matcher(key);
        if (matcher.matches()) {
            Scanning=Scanning+matcher.group(1);
        }
        new CountDownTimer(1000, 1000) {
            public void onTick(long millisUntilFinished) {
            }
            public void onFinish() {
                if(execute==1){
                    execute=0;
                    if(COUNTER==0){
                        UserCheck();
                    }
                    else if(COUNTER==1){
                        UnitCheck();
                    }
                    Scanning="";
                }
            }
        }.start();
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.addcontact,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case R.id.scan:
                if(execute==0){
                    try{
                        Intent intent = new Intent("com.google.zxing.client.android.SCAN");
                        //intent.putExtra("SCAN_MODE", "QR_CODE_MODE");
                        startActivityForResult(intent, 0);
                    }catch(Exception e){
                        Toast.makeText(getApplicationContext(), "Debe instalar ZXING para utilizar esta opción.", Toast.LENGTH_SHORT).show();
                        Sentry.capture(e);
                    }
                    return true;
                }else{
                }
            default:
                return super.onOptionsItemSelected(item);
        }
    }

    //Se usa con lector de camara
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 0) {
            if (resultCode == RESULT_OK) {
                final String  contents = data.getStringExtra("SCAN_RESULT"); // This will contain your scan result
                String format = data.getStringExtra("SCAN_RESULT_FORMAT");
                Scanning=contents;
                execute=0;
                if(COUNTER==0){
                    UserCheck();
                }
                else if(COUNTER==1){
                    UnitCheck();
                }
                Scanning="";
            }}}

    //Verifica codigo de barra si existe en base de datos
    public void UserCheck(){
        TextView user = (TextView)findViewById(R.id.user);
        StrictMode.setThreadPolicy(policy);
        Boolean succesfull=false;
        try{
            String result = null;
            InputStream is = null;
            String v1 = "'"+Scanning+"'";
            SimpleDateFormat sdfa = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("codigo",v1));
            nameValuePairs.add(new BasicNameValuePair("licencia","'asc891n213'"));
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(MainMenu.URL+"checkFuelIDUser.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                Log.e("log_tag", "connection success ");
            }
            catch(Exception e)
            {
                Log.e("log_tag", "Error in http connection "+e.toString());
                Toast.makeText(getApplicationContext(), "No se encuentra conectado a la red.Verifique conexión de VPN.", Toast.LENGTH_SHORT).show();
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
                        String usuario=no.getString("FUELUSERID");
                        USER=usuario;
                        String nombreusuario=no.getString("USERNAME");
                        USERNAME=nombreusuario;
                        String datos=no.getString("DATA");
                        TIPOUSUARIO=datos;
                        user.setText(nombreusuario);
                        String loc=no.getString("LOCATION_SCENARIO");
                        LOCATION_SCENARIO=loc;
                        COMPANY=loc;
                        counter++;
                        succesfull=true;
                    }catch(Exception e)
                    {
                        Sentry.capture(e);
                    }
                    try{
                        Sentry.getContext().setUser(
                                new UserBuilder().setEmail(USER).build()
                        );
                    }
                    catch(Exception e){
                        Log.e("ERROR SENTRY", e.toString());
                        Sentry.getContext().setUser(
                                new UserBuilder().setEmail("loginStats").build()
                        );
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
        if(succesfull){
            user.setBackgroundResource(R.drawable.edittext_green);
            COUNTER++;
        }else{
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(400);
            user.setBackgroundResource(R.drawable.edittext_red);
            Toast.makeText(getBaseContext(), "Usuario no existente o no tiene permisos para accesar esta operación.", Toast.LENGTH_LONG).show();
        }
    }

    //Verifica si unidad existe en base de datos
    public void UnitCheck(){
        TextView fleetid = (TextView)findViewById(R.id.fleetid);
        StrictMode.setThreadPolicy(policy);
        Boolean succesfull = false;
        try{
            String result = null;
            InputStream is = null;
            String v1 = "'"+Scanning.replace(" ", "")+"'";
            String v2 = "'"+LOCATION_SCENARIO+"'";
            SimpleDateFormat sdfa = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("barcode",v1));
            nameValuePairs.add(new BasicNameValuePair("company",v2));
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(MainMenu.URL+"checkFleetIDUnit.php");
                httppost.setEntity(new UrlEncodedFormEntity(nameValuePairs));
                HttpResponse response = httpclient.execute(httppost);
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                Log.e("log_tag", "connection success ");
            }
            catch(Exception e)
            {
                Log.e("log_tag", "Error in http connection "+e.toString());
                Toast.makeText(getApplicationContext(), "No se encuentra conectado a la red.Verifique conexión de VPN.", Toast.LENGTH_SHORT).show();
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
                        String placa=   Normalizer.normalize(no.getString("Placa"), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                        PLATE=placa;
                        fleetid.setText(placa);
                        String brand=   Normalizer.normalize(no.getString("Marca"), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                        String model=   Normalizer.normalize(no.getString("Modelo"), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                        String year=   Normalizer.normalize(no.getString("A\u00f1o"), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                        CARTYPE=brand+" "+model+" "+year;
                        String maxliter="";
                        try{
                            maxliter =   Normalizer.normalize(no.getString("CapacidadTanque"), Normalizer.Form.NFD).replaceAll("[^\\p{ASCII}]", "");
                        }
                        catch(Exception e){Sentry.capture(e);}
                        MAXLITER=maxliter;
                        counter++;
                        succesfull=true;
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
        if(succesfull){
            fleetid.setBackgroundResource(R.drawable.edittext_green);
            COUNTER++;
        }else{
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            v.vibrate(400);
            fleetid.setBackgroundResource(R.drawable.edittext_red);
            Toast.makeText(getBaseContext(), "Unidad no encontrada.Intente de nuevo.", Toast.LENGTH_LONG).show();
        }
    }

    public void onSave(View view) {
        if(execute==0){
            if(COUNTER==0){
                Toast.makeText(getApplicationContext(), "Verifique su usuario para avanzar.", Toast.LENGTH_SHORT).show();
                Intent intentemoslo = new Intent(getApplicationContext() ,VoiceHelper.class);
                intentemoslo.putExtra("voz","Verifique su usuario para avanzar");
                startService(intentemoslo);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(400);
            }
            if(COUNTER==1){
                Toast.makeText(getApplicationContext(), "Verifique la estación para avanzar.", Toast.LENGTH_SHORT).show();
                Intent intentemoslo = new Intent(getApplicationContext() ,VoiceHelper.class);
                intentemoslo.putExtra("voz","Verifique la estación para avanzar");
                startService(intentemoslo);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                v.vibrate(400);
            }
            if(COUNTER==2){
                Intent intento = new Intent(getApplicationContext(), loginFuelStats.class);
                intento.putExtra("plate",PLATE);
                intento.putExtra("company",COMPANY);
                intento.putExtra("car",CARTYPE);
                intento.putExtra("user",USER);
                startActivity(intento);
            }
        }
    }

    @Override
    public void onBackPressed() {
        execute=0;
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("¿Esta seguro de que desea salir? ")
                .setCancelable(false)
                .setPositiveButton("Sí", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        execute=0;

                        finish();
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        execute=0;

                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }
}