package com.example.mendez.trackerservice.activity;

import android.Manifest;
import android.app.Activity;
import android.app.NotificationManager;
import android.content.BroadcastReceiver;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.IntentSender;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.database.sqlite.SQLiteDatabase;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.RequiresApi;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.CompoundButton;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.example.mendez.trackerservice.config.Constantes;
import com.example.mendez.trackerservice.database.BD;
import com.example.mendez.trackerservice.R;
import com.example.mendez.trackerservice.util.Utils;
import com.example.mendez.trackerservice.dialog.diafragsms;
import com.example.mendez.trackerservice.networking.gac_service;
import com.example.mendez.trackerservice.fragment.iniciocon;
import com.example.mendez.trackerservice.fragment.map;
import com.example.mendez.trackerservice.dialog.mapdialogfragment;
import com.example.mendez.trackerservice.fragment.message;
import com.example.mendez.trackerservice.fragment.viajes_fragment;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import java.util.HashMap;

import static android.Manifest.permission.ACCESS_COARSE_LOCATION;
import static android.Manifest.permission.ACCESS_FINE_LOCATION;
import static android.Manifest.permission.SYSTEM_ALERT_WINDOW;

public class Inicio extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener,iniciocon.OnFragmentInteractionListener,
        map.OnFragmentInteractionListener,viajes_fragment.OnFragmentInteractionListener,message.OnFragmentInteractionListener, CompoundButton.OnCheckedChangeListener, View.OnClickListener {
    private static final int PERMISSIONS_REQUEST = 1;
    private final int MIS_PERMISOS = 100;
    public static boolean acti=false;
    HashMap<String,Object> hashMap;
    HashMap<String,Object> hashMapdialo;
    String xm;
    TextView tes;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    String sww;

    public static Bundle noda,avbun;
    String idviaj,tkfcm;
    String nomh,emah;
    TextView nomhe,emahe;
    Bundle bundle = new Bundle();
    String sms,nom;
    FirebaseAuth mAuth;
    FirebaseUser user;
    NotificationManager nm;
    static String ns = Context.NOTIFICATION_SERVICE;
    Switch button;
    NavigationView navigationView;
    public static final int REQUEST_CHECK_SETTINGS = 2;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inicio);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        validaPermisos();

        nm=(NotificationManager)getSystemService(ns);
        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();
         settings = getSharedPreferences(Constantes.PREFS_NAME, 0);
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        navigationView = (NavigationView) findViewById(R.id.nav_view);
        View hView = navigationView.getHeaderView(0);

        nomhe=(TextView)hView.findViewById(R.id.tv_nom);
        nomhe.setOnClickListener(this);
        emahe=(TextView)hView.findViewById(R.id.textViewema);
        emahe.setOnClickListener(this);
        navigationView.setNavigationItemSelectedListener(this);

        ActionBar actionBar = getSupportActionBar();
        actionBar.setCustomView(R.layout.swi_tool);
        actionBar.setDisplayOptions(ActionBar.DISPLAY_SHOW_HOME | ActionBar.DISPLAY_SHOW_CUSTOM);
        tes=(TextView)findViewById(R.id.tes);
        button = (Switch) findViewById(R.id.switchForActionBar);

        sww=settings.getString("switch","2");
        if(sww.equals("1")){
            tes.setText("Conectado");
            button.setChecked(true);
        }else {
            if (sww.equals("0")){
                tes.setText("Desconectado");
                button.setChecked(false);
            }
        }

        button.setOnCheckedChangeListener(this);
        // Check GPS is enabled
        LocationManager lm = (LocationManager) getSystemService(LOCATION_SERVICE);
        if (!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            Toast.makeText(this, "Please enable location services", Toast.LENGTH_SHORT).show();
            //finish();
        }

        // Check location permission is granted - if it is, start
        // the service, otherwise request the permission
        int permission = ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION);
        if (permission == PackageManager.PERMISSION_GRANTED) {
            startTrackerService();
        } else {
            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.ACCESS_FINE_LOCATION},
                    PERMISSIONS_REQUEST);
        }

        if (savedInstanceState == null) {
            navigationView.setCheckedItem(R.id.in);
            displaySelectedScreen(R.id.in);
        }
        Intent intent = getIntent();
       xm=intent.getStringExtra("map");
        noda=intent.getBundleExtra("noda");
        //avbun=intent.getBundleExtra("bun");
    if (xm!=null) {
        if (xm.equals("1")){
            navigationView.setCheckedItem(R.id.map);
            displaySelectedScreen(R.id.map);
        }else{
            if (xm.equals("2")){
                navigationView.setCheckedItem(R.id.mes);
                DatabaseReference sm= FirebaseDatabase.getInstance().getReference("autos/"+user.getUid()+"/viaje/smscli");
                sm.removeValue();
                displaySelectedScreen(R.id.mes);

            }
        }

    }
        if (noda != null) {
            for (String key : noda.keySet()) {
                Log.e("datos_"+key, String.valueOf(noda.get(key)));
            }
            if (noda.get("av")==null) {
                revvi();
                /*String[] latlongo = String.valueOf(noda.get("mapo")).split(",");
                String[] latlongd = String.valueOf(noda.get("mapd")).split(",");
                hashMap = new HashMap<>();
                hashMap.put("lat", latlongo[0]);
                hashMap.put("lng", latlongo[1]);
                hashMapdialo = new HashMap<>();
                hashMapdialo.put("lat", latlongd[0]);
                hashMapdialo.put("lng", latlongd[1]);
                idviaj = String.valueOf(noda.get("idvi"));
                tkfcm = String.valueOf(noda.get("tckcli"));
                if (noda.get("token_cli")!=null){
                    Log.e("cliente", String.valueOf(noda.get("token_cli")));
                }
                bundle.putSerializable("mapo", hashMap);
                bundle.putSerializable("mapd", hashMapdialo);
                bundle.putString("idvi", idviaj);
                bundle.putString("tkfc", tkfcm);

                editor = settings.edit();
                editor.putString("lato", latlongo[0]);
                editor.putString("lngo", latlongo[1]);
                editor.putString("latd", latlongd[0]);
                editor.putString("lngd", latlongd[1]);
                editor.putString("idivi", idviaj);
                editor.putString("tkfcm", tkfcm);
                editor.putString("pre", "1");
                editor.apply();

                final mapdialogfragment mapdi = new mapdialogfragment();
                mapdi.setArguments(bundle);
                mapdi.show(getSupportFragmentManager(), "Dialogo");*/
            }else {
               // if (avbun != null) {
                    if (String.valueOf(noda.get("av")).equals("m")) {
                       /* DatabaseReference sm= FirebaseDatabase.getInstance().getReference("autos/"+user.getUid()+"/viaje/smscli");
                        sm.removeValue();
                        sms = noda.get("sms").toString();
                        recus(noda.get("tokenus").toString());*/
                       editor=settings.edit();
                       editor.putString("notm","1");
                       editor.apply();
                       smsg(1);
                    }
               // }
            }

        }


        Log.e("tkfcm", FirebaseInstanceId.getInstance().getToken());
        Utils.startPowerSaverIntent(Inicio.this);


    }
    public void loadSharedPrefs(String ... prefs) {
        // Logging messages left in to view Shared Preferences. I filter out all logs except for ERROR; hence why I am printing error messages.
        Log.i("Loading Shared Prefs", "-----------------------------------");
        Log.i("----------------", "---------------------------------------");
        for (String pref_name: prefs) {
            SharedPreferences preference = getSharedPreferences(pref_name, MODE_PRIVATE);
            for (String key : preference.getAll().keySet()) {
                Log.i(String.format("Shared Preference : %s - %s", pref_name, key), preference.getString(key, "error!"));
            }
            Log.i("----------------", "---------------------------------------");
        }
        Log.i("Finished Shared Prefs", "----------------------------------");
    }
    public void recus(final String tu){

       DatabaseReference reference=FirebaseDatabase.getInstance().getReference().child("clientes").child(tu);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                 nom=String.valueOf(dataSnapshot.child("nombre").getValue());
                editor=settings.edit();
                editor.putString("nomcli",nom);
                editor.apply();
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            Fragment f = getSupportFragmentManager().findFragmentById(R.id.conten);
            if (!(f instanceof map)) {
                navigationView.setCheckedItem(R.id.map);
                displaySelectedScreen(R.id.map);
            }else{
                //finish();
                super.onBackPressed();
            }
        }
    }

    @Override
    protected void onStart() {
        super.onStart();
        String path = getString(R.string.firebase_path) + "/" + user.getUid();
        DatabaseReference refgetcon=FirebaseDatabase.getInstance().getReference().child(path+"/control").child(FirebaseInstanceId.getInstance().getToken());
        refgetcon.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    Boolean x=Boolean.parseBoolean(String.valueOf(dataSnapshot.child("estado").getValue()));
                    if (!x){
                        signOut();
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error",databaseError.toString());
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();

        nomh=settings.getString("nom","error");
        emah=settings.getString("ema","error");
        nomhe.setText(nomh);
        emahe.setText(emah);


            if (!settings.getString("pre","error").equals("1")){
                revvi();
            }else{
                if (settings.getString("notm","error").equals("error")){
                    smsg(2);
                }

            }
            acti=true;
        registerReceiver(this.broadCastPosition, new IntentFilter("bcNewMessage"));
        loadSharedPrefs(Constantes.PREFS_NAME);
    }

    @Override
    protected void onPause() {
        super.onPause();
        acti=false;
       unregisterReceiver(broadCastPosition);
    }

    public void  revvi(){
        DatabaseReference datos= FirebaseDatabase.getInstance().getReference("autos/"+user.getUid()+"/viaje");
        datos.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.e("datasnapshot", String.valueOf(dataSnapshot.getValue()));
                if (dataSnapshot.exists()){
                    if (nm!=null){
                        nm.cancelAll();
                    }
                    editor=settings.edit();
                    editor.putString("lato",String.valueOf(dataSnapshot.child("lato").getValue()));
                    editor.putString("lngo",String.valueOf(dataSnapshot.child("lono").getValue()));
                    editor.putString("latd",String.valueOf(dataSnapshot.child("latd").getValue()));
                    editor.putString("lngd",String.valueOf(dataSnapshot.child("lond").getValue()));
                    editor.putString("idivi",String.valueOf(dataSnapshot.child("idvi").getValue()));
                    editor.putString("tkfcm",String.valueOf(dataSnapshot.child("tkfcm").getValue()));
                    editor.putString("tus",String.valueOf(dataSnapshot.child("tokcli").getValue()));
                    editor.putString("nomcli",String.valueOf(dataSnapshot.child("nomcli").getValue()));
                    editor.putString("numcli",String.valueOf(dataSnapshot.child("numcli").getValue()));
                    editor.putString("pre","1");
                    editor.apply();
                    //recus(String.valueOf(dataSnapshot.child("tokcli").getValue()));
                    hashMap = new HashMap<>();
                    hashMap.put("lat", String.valueOf(dataSnapshot.child("lato").getValue()));
                    hashMap.put("lng", String.valueOf(dataSnapshot.child("lono").getValue()));
                    hashMapdialo = new HashMap<>();
                    hashMapdialo.put("lat", String.valueOf(dataSnapshot.child("latd").getValue()));
                    hashMapdialo.put("lng", String.valueOf(dataSnapshot.child("lond").getValue()));
                    bundle.putSerializable("mapo", hashMap);
                    bundle.putSerializable("mapd", hashMapdialo);
                    bundle.putString("idvi",String.valueOf(dataSnapshot.child("idvi").getValue()));
                    bundle.putString("tkfc",String.valueOf(dataSnapshot.child("tkfcm").getValue()));
                    final mapdialogfragment mapdi = new mapdialogfragment();
                    mapdi.setArguments(bundle);
                    mapdi.show(getSupportFragmentManager(), "Dialogo");
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error",databaseError.toString());
            }
        });
    }
    public void smsg(final int t){
        final String[] x = new String[1];
        x[0]="";
        final DatabaseReference sm= FirebaseDatabase.getInstance().getReference("autos/"+user.getUid()+"/viaje/smscli");
        sm.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    for (DataSnapshot dataChildren:dataSnapshot.getChildren()){
                        setMsg(dataChildren);
                        Log.e("valor", String.valueOf(dataChildren.getValue()));
                        Log.e("valorkey",dataChildren.getKey());
                        x[0] = x[0] +dataChildren.child("sms").getValue()+"\n";
                    }
                    switch (t){
                        case 1:
                            navigationView.setCheckedItem(R.id.mes);
                            editor=settings.edit();
                            editor.putString("notm","error");
                            editor.apply();
                            displaySelectedScreen(R.id.mes);
                            break;
                        case 2:
                            Fragment f =getSupportFragmentManager().findFragmentById(R.id.conten);
                            if (f instanceof message){ // do something with f ((CustomFragmentClass) f).doSomething();
                                // Toast.makeText(getBaseContext(),"fragment visible",Toast.LENGTH_SHORT).show();
                                try {
                                    navigationView.setCheckedItem(R.id.mes);
                                    displaySelectedScreen(R.id.mes);
                                }catch (Exception e){
                                    Toast.makeText(getBaseContext(),"Error",Toast.LENGTH_SHORT).show();
                                    Log.e("error",e.toString());
                                }
                            }else{
                                //Toast.makeText(getBaseContext(),"fragment no visible",Toast.LENGTH_SHORT).show();
                                Bundle b=new Bundle();
                                b.putString("nsm",x[0]);
                                final diafragsms diafragsms = new diafragsms();
                                diafragsms.setArguments(b);
                                diafragsms.show(getSupportFragmentManager(), "Dialogo SMS");
                            }
                            break;
                    }
                    sm.removeValue();
                    x[0]="";
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("Error",databaseError.toString());
            }
        });
    }
    public void setMsg(DataSnapshot dataSnapshot){
        Log.e("data", String.valueOf(dataSnapshot.getValue()));
        Log.e("key",dataSnapshot.getKey());
        nm.cancelAll();
        BD base=new BD(getBaseContext(),"baseSms",null,1);
        final SQLiteDatabase bd=base.getWritableDatabase();
        ContentValues registro = new ContentValues();
        registro.put("sms", String.valueOf(dataSnapshot.child("sms").getValue()));
        registro.put("fecha",String.valueOf(dataSnapshot.child("fecha").getValue()));
        registro.put("hora",String.valueOf(dataSnapshot.child("hora").getValue()));
        registro.put("nombre",String.valueOf(dataSnapshot.child("nombrecli").getValue()));
        registro.put("tipo","1");
        bd.insert("sms", null, registro);
    }

   /* @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.inicio, menu);
        return true;
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        switch (id){
            case R.id.action_settings:
                break;
        }

        return super.onOptionsItemSelected(item);
    }*/
    private void displaySelectedScreen(int itemid){
        Fragment fragment=null;

        switch (itemid){
            case R.id.in:
                fragment=new iniciocon();
                setTitle("Inicio");
                break;
            case R.id.map:
               fragment=new map();
                setTitle("Mapa");
                break;
            case R.id.via:
                fragment=new viajes_fragment();
                setTitle("Viajes");
                break;
            case R.id.mes:
                fragment=new message();
                setTitle("Mensajes");
                break;
            case R.id.logout:
                signOut();
                break;
        }
        if (fragment!=null){
            FragmentTransaction ft=getSupportFragmentManager().beginTransaction();
            ft.setCustomAnimations(R.anim.enter, R.anim.exit, R.anim.pop_enter, R.anim.pop_exit);
            ft.replace(R.id.conten,fragment);
            ft.commit();
        }
        DrawerLayout drawer=(DrawerLayout)findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        displaySelectedScreen(item.getItemId());
        return true;
    }
    private void startTrackerService() {
        if(sww.equals("1")){
            startService(new Intent(this, gac_service.class));
            }

        //finish();
    }

    private void signOut() {
        BD base = new BD(getBaseContext(), "baseSms", null, 1);
        final SQLiteDatabase bd = base.getWritableDatabase();

        final AlertDialog.Builder alertOpcion = new AlertDialog.Builder(Inicio.this);
        alertOpcion.setTitle("Aviso");
        alertOpcion.setMessage("Saldra de su cuenta, se borraran todos sus datos  ");
        alertOpcion.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
            }
        });
        alertOpcion.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                bd.delete("sms",null,null);
                bd.close();

                String id = settings.getString("id", "error");
                DatabaseReference esta= FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path)+"/"+id+"/estado");
                HashMap<String,String> mapa = new HashMap<String, String>();// Mapeo previo
                mapa.put("estado","Desconectado");
                esta.setValue(mapa);
                FirebaseAuth.getInstance().signOut();
                stopService(new Intent(Inicio.this,gac_service.class));
                startActivity(new Intent(getBaseContext(),MainActivity.class));
                finish();
            }
        });
       alertOpcion.show();
    }
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        if (requestCode == PERMISSIONS_REQUEST && grantResults.length == 1
                && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
            // Start the service when the permission is granted
            startTrackerService();
        }
        if (requestCode==MIS_PERMISOS){
            if(grantResults.length==2 && grantResults[0]== PackageManager.PERMISSION_GRANTED && grantResults[1]==PackageManager.PERMISSION_GRANTED){//el dos representa los 2 permisos
                Toast.makeText(getBaseContext(),"Permisos aceptados",Toast.LENGTH_SHORT).show();
                //btnf.setEnabled(true);
            }
        }else{
            solicitarPermisosManual();
        }
    }


    private void solicitarPermisosManual() {
        final CharSequence[] opciones={"si","no"};
        final AlertDialog.Builder alertOpciones=new AlertDialog.Builder(Inicio.this);//estamos en fragment
        alertOpciones.setTitle("¿Desea configurar los permisos de forma manual?");
        alertOpciones.setItems(opciones, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                if (opciones[i].equals("si")){
                    Intent intent=new Intent();
                    intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                    Uri uri=Uri.fromParts("package",getApplicationContext().getPackageName(),null);
                    intent.setData(uri);
                    startActivity(intent);
                }else{
                    Toast.makeText(getBaseContext(),"Los permisos no fueron aceptados",Toast.LENGTH_SHORT).show();
                    dialogInterface.dismiss();
                }
            }
        });

        alertOpciones.show();
    }
    private void cargarDialogoRecomendacion() {
        AlertDialog.Builder dialogo = new AlertDialog.Builder(Inicio.this);
        dialogo.setTitle("Permisos Desactivados");
        dialogo.setMessage("Debe aceptar los permisos para el correcto funcionamiento de la App");

        dialogo.setPositiveButton("Aceptar", new DialogInterface.OnClickListener() {
            @RequiresApi(api = Build.VERSION_CODES.M)
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION, SYSTEM_ALERT_WINDOW}, 100);
            }
        });
        dialogo.show();
    }
    private boolean validaPermisos() {

        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.M) {
            return true;
        }

        if ((checkSelfPermission(ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED) & (checkSelfPermission(ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) ) {
            return true;
        }

        if ((shouldShowRequestPermissionRationale(ACCESS_FINE_LOCATION)) & (shouldShowRequestPermissionRationale(ACCESS_COARSE_LOCATION)) ) {
            cargarDialogoRecomendacion();
        } else {
            requestPermissions(new String[]{ACCESS_FINE_LOCATION, ACCESS_COARSE_LOCATION}, 100);
        }

        return false;
    }

    @Override
    public void onFragmentInteraction(Uri uri) {

    }

    @Override
    public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

       String id = settings.getString("id", "error");
         DatabaseReference esta= FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path)+"/"+id+"/estado");
         HashMap<String,String> mapa = new HashMap<String, String>();// Mapeo previo
         editor = settings.edit();
        if (isChecked){
            startService(new Intent(this,gac_service.class));
           Toast.makeText(getBaseContext(),"Checked",Toast.LENGTH_SHORT).show();
           tes.setText("Conectado");
           mapa.put("estado","Desocupado");
            editor.putString("switch","1");
            editor.apply();
        }else{
            if (settings.getString("lato","error").equals("error")){
                Toast.makeText(getBaseContext(),"Not checked",Toast.LENGTH_SHORT).show();
                tes.setText("Desconcetado");
                mapa.put("estado","Desconectado");
                editor.putString("switch","0");
                editor.apply();
                stopService(new Intent(this,gac_service.class));
            }

        }
        esta.setValue(mapa);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_nom:
                startActivity(new Intent(getBaseContext(),edit_perfil.class));
                break;
            case R.id.textViewema:
                startActivity(new Intent(getBaseContext(),edit_cuenta.class));
                break;
        }
    }
    BroadcastReceiver broadCastPosition = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String avis = intent.getStringExtra("loc");
            if (avis != null ) {
                if (avis.equals("f")){
                    try {
                        gac_service.status.startResolutionForResult(Inicio.this,
                                REQUEST_CHECK_SETTINGS);
                    } catch (IntentSender.SendIntentException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        switch (requestCode) {
            case REQUEST_CHECK_SETTINGS:
                switch (resultCode) {
                    case Activity.RESULT_OK:
                        Log.d("aviso", "El usuario permitió el cambio de ajustes de ubicación. "+requestCode+" __ "+ resultCode);
                        //processLastLocation();
                        //startLocationUpdates();
                        //unregisterReceiver(broadCastPosition);
                        break;
                    case Activity.RESULT_CANCELED:
                        String id = settings.getString("id", "error");
                        DatabaseReference esta= FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path)+"/"+id+"/estado");
                        HashMap<String,String> mapa = new HashMap<String, String>();// Mapeo previo
                        editor = settings.edit();
                        Log.d("Aviso", "El usuario no permitió el cambio de ajustes de ubicación");
                        tes.setText("Desconcetado");
                        mapa.put("estado","Desconectado");
                        editor.putString("switch","0");
                        editor.apply();
                        esta.setValue(mapa);
                        stopService(new Intent(this,gac_service.class));
                        button.setChecked(false);
                      // unregisterReceiver(broadCastPosition);
                        break;
                }
                break;
        }
        Log.e("error","requestcode "+requestCode+ " resultcode "+resultCode);
    }

}
