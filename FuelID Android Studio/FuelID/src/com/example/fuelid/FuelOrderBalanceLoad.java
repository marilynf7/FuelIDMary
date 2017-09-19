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

public class FuelOrderBalanceLoad extends Activity {

    StrictMode.ThreadPolicy policy = new StrictMode.ThreadPolicy.Builder().permitAll().build();
    private static final Pattern KEYCODE_PATTERN = Pattern.compile("KEYCODE_(\\w)");
    private static final int REQUEST_CODE = 1234;
    public int COUNTER =0;
    public int GLOBALCOUNTER =0;
    public String USER,BRANCHID,STATIONCODE,COMPANY,STATION,CARTYPE,FUELCOST,LOCATION_SCENARIO,TANKID,TIPOUSUARIO,USERNAME,GASTYPE,MAXLITER;
    public String Scanning="";
    public int execute=0;
    public int timerstarted=0;
    public Boolean timercompleted=false;
    public Boolean automaticodometer=false;
    public Boolean automaticcost=false;
    final Context context = this;
    public String[] estaciones;
    final String PREFS_NAME = "MyPrefsFile";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        FontsOverride.setDefaultFont(this, "MONOSPACE", "fonts/MuseoSans300.otf");
        setContentView(R.layout.loginscreenbalance);
        ActionBar actionBar = getActionBar();
        actionBar.hide();
        actionBar.setSubtitle("Saldo tanque");
        actionBar.setTitle("FuelID");
        actionBar.setBackgroundDrawable(new ColorDrawable(getResources().getColor(R.color.Crimson)));
        actionBar.setDisplayShowTitleEnabled(false);  // required to force redraw, without, gray color
        actionBar.setDisplayShowTitleEnabled(true);
        actionBar.setDisplayHomeAsUpEnabled(false);
        actionBar.show();

        Sentry.getContext().setUser(
                new UserBuilder().setEmail("FuelOrderBalanceLoad").build()
        );
    }
    //Ejecuta validacion con lector de barra
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
                        StationCheck();
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
                    StationCheck();
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

    //Verifica si usuario esta autorizado a usar la estacion
    public void StationCheck(){
        TextView fuelstationid = (TextView)findViewById(R.id.fuelstationid);
        Boolean succesfull = false;
        StrictMode.setThreadPolicy(policy);
        try{
            String result = null;
            InputStream is = null;
            String v1 = "'"+Scanning+"'";
            String v2 = "'"+USER+"'";
            String v3 = "'"+LOCATION_SCENARIO+"'";
            SimpleDateFormat sdfa = new SimpleDateFormat("yyyy-MM-dd");
            ArrayList<NameValuePair> nameValuePairs = new ArrayList<NameValuePair>();
            nameValuePairs.add(new BasicNameValuePair("CODE",v1));
            nameValuePairs.add(new BasicNameValuePair("USER",v2));
            nameValuePairs.add(new BasicNameValuePair("SCENARIO",v3));
            //apartir de aqui carga ruta
            try
            {
                HttpClient httpclient = new DefaultHttpClient();
                HttpPost httppost = new HttpPost(MainMenu.URL+"checkFuelIDStation.php");
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
                Toast.makeText(getApplicationContext(), "No se encuentra conectado a la red.Verifique conexión de VPN.", Toast.LENGTH_SHORT).show();
                Sentry.capture(e);
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
                Sentry.capture(e);
            }
            int counter=0;
            //parse json data
            try{
                //    databaseHelper.deleteAllData();

                JSONArray jArray = new JSONArray(result);
                for(int i=0;i<jArray.length();i++)
                {
                    try{
                        JSONObject no = jArray.getJSONObject(i);

                        String description=no.getString("TankBranchDescription");
                        STATION=description;
                        String fuelID=no.getString("TankBranchID");
                        BRANCHID=fuelID;

                        fuelstationid.setText(STATION);

                        String tankid=no.getString("TankBranchParentTank");
                        TANKID=tankid;
                        String gas=no.getString("GasType");
                        GASTYPE=gas;
                        String cost=no.getString("FuelCurrentCost");
                        FUELCOST=cost;

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
        //	Cursor x =databaseHelper.getAllUserPermissionLines(Scanning);
        if(succesfull){
            fuelstationid.setBackgroundResource(R.drawable.edittext_green);
            COUNTER++;
        }else{
            Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
            // Vibrate for 400 milliseconds
            v.vibrate(400);
            fuelstationid.setBackgroundResource(R.drawable.edittext_red);
            Toast.makeText(getBaseContext(), "Estación no reconocida.Intente de nuevo.", Toast.LENGTH_LONG).show();
        }

    }

    //Confirma restricciones para que el usuario avance
    public void onSave(View view) {
        if(execute==0){
            if(COUNTER==0){
                Toast.makeText(getApplicationContext(), "Verifique su usuario para avanzar.", Toast.LENGTH_SHORT).show();
                Intent intentemoslo = new Intent(getApplicationContext() ,VoiceHelper.class);
                intentemoslo.putExtra("voz","Verifique su usuario para avanzar");
                startService(intentemoslo);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 400 milliseconds
                v.vibrate(400);
            }
            if(COUNTER==1){
                Toast.makeText(getApplicationContext(), "Verifique la estación para avanzar.", Toast.LENGTH_SHORT).show();
                Intent intentemoslo = new Intent(getApplicationContext() ,VoiceHelper.class);
                intentemoslo.putExtra("voz","Verifique la estación para avanzar");
                startService(intentemoslo);
                Vibrator v = (Vibrator) getSystemService(Context.VIBRATOR_SERVICE);
                // Vibrate for 400 milliseconds
                v.vibrate(400);
            }
            if(COUNTER==2){
                /*Intent intento = new Intent(getApplicationContext(), FuelSummary.class);
                intento.putExtra("usuario",USER);
                intento.putExtra("tipousuario",TIPOUSUARIO);
                intento.putExtra("estacion",STATION);
                intento.putExtra("compania",COMPANY);
                intento.putExtra("marca",CARTYPE);
                intento.putExtra("costo",FUELCOST);
                intento.putExtra("loc",LOCATION_SCENARIO);
                intento.putExtra("tankid",TANKID);
                intento.putExtra("branchid",BRANCHID);
                intento.putExtra("gastype",GASTYPE);
                intento.putExtra("maxliter",MAXLITER);
                int i = (int) (new Date().getTime()/1000);
                intento.putExtra("transactionid",i+"");
                startActivity(intento);
                this.finish();*/
                Intent intento = new Intent(getApplicationContext(), FuelTank.class);
                intento.putExtra("idtank",TANKID);
                intento.putExtra("company",COMPANY);
                startActivity(intento);
            }
        }}

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
