package com.example.mendez.trackerservice.fragment;

import android.app.ProgressDialog;
import android.content.ActivityNotFoundException;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.trackerservice.util.AnimationMarker;
import com.example.mendez.trackerservice.database.BD;
import com.example.mendez.trackerservice.config.Constantes;
import com.example.mendez.trackerservice.util.LatlngInterpolator;
import com.example.mendez.trackerservice.activity.MainActivity;
import com.example.mendez.trackerservice.networking.PathJSONParser;
import com.example.mendez.trackerservice.R;
import com.example.mendez.trackerservice.activity.Inicio;
import com.example.mendez.trackerservice.networking.gac_service;
import com.example.mendez.trackerservice.adapter.infomarker;
import com.example.mendez.trackerservice.networking.VolleySingleton;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.GeoQuery;
import com.firebase.geofire.GeoQueryEventListener;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.UiSettings;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.iid.FirebaseInstanceId;

import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;



/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link map.OnFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class map extends Fragment implements OnMapReadyCallback, View.OnClickListener {

    private OnFragmentInteractionListener mListener;
    private GoogleMap mMap;
    MapView mapView;
    String id, nom, pla;
    private Marker mi,orige,desti;
    Polyline polyline1,polyline2;
    HashMap<String,Object>hmap,hmapd;

    LinearLayout lliv;
    TextView tviv;
    TextView btniv;

    boolean od=false;
    ProgressDialog loading = null;
    String tkcf;
     String idvi;
    JSONObject mensaje =null;
    LinearLayout fabnc2,opciones,llam,nav;


    //Marker marker1,marker2;
    String lato,lngo,latd,lngd;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    LatlngInterpolator latlngInterpolator=new LatlngInterpolator.Linear();

    public map() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
            View view =inflater.inflate(R.layout.fragment_map2, container, false);
        settings = getActivity().getSharedPreferences(Constantes.PREFS_NAME, 0);
        id = settings.getString("id", "error");
        nom = settings.getString("nomcli", "error");
        pla = settings.getString("pla", "error");
        lato= settings.getString("lato", "error");
        lngo= settings.getString("lngo", "error");
        latd= settings.getString("latd", "error");
        lngd= settings.getString("lngd", "error");

        lliv=(LinearLayout)view.findViewById(R.id.lliv);
        tviv=(TextView)view.findViewById(R.id.cliiv);
        tviv.setText(settings.getString("nomcli","error"));
        btniv=(TextView)view.findViewById(R.id.btniv);
        btniv.setOnClickListener(this);
        fabnc2=(LinearLayout) view.findViewById(R.id.notclie);
        opciones=(LinearLayout)view.findViewById(R.id.opcines_id);
        llam=(LinearLayout)view.findViewById(R.id.llamar_id);
        llam.setOnClickListener(this);
        nav=(LinearLayout)view.findViewById(R.id.navegar_id);
        nav.setOnClickListener(this);
        fabnc2.setOnClickListener(this);
            mapView=(MapView)view.findViewById(R.id.mapView);
        mapView.onCreate(savedInstanceState);

        mapView.onResume();// needed to get the map to display immediately
        //mapFragment.getMapAsync(this);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);


        if (lato.equals("error")|lngo.equals("error")){
           hmap=null;
        }else{
            hmap=new HashMap<String,Object>();
            hmap.put("lat",lato);
            hmap.put("lng",lngo);
        }
        if (latd.equals("error")|lngd.equals("error")){
            hmapd=null;
        }else{
            hmapd=new HashMap<String,Object>();
            hmapd.put("lat",latd);
            hmapd.put("lng",lngd);
        }
         idvi = settings.getString("idivi", "error");
         tkcf = settings.getString("tkfcm", "error");

        return view;
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentInteraction(uri);
        }
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnFragmentInteractionListener) {
            mListener = (OnFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        LatLng latLng=new LatLng(-22.012954, -63.678465);
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, 16));
        subscribeToUpdates();
        setupGoogleMapScreenSettings(mMap);
        infomarker in=new infomarker(getContext());
        mMap.setInfoWindowAdapter(in);
    }

    private void subscribeToUpdates() {
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference().child("users").child(id);
                ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                geo();
                if(!od){
                    if (polyline1 == null&hmap!=null&hmapd!=null&mi!=null) {
                        if (settings.getString("esbtn","error").equals("1")){
                            lliv.setVisibility(View.VISIBLE);
                            opciones.setVisibility(View.VISIBLE);
                            getdirec(hmap, hmapd, 2);
                            }else{
                            HashMap<String,Object>miub=new HashMap<>();
                            miub.put("lat",String.valueOf(mi.getPosition().latitude));
                            miub.put("lng",String.valueOf(mi.getPosition().longitude));
                            getdirec(miub,hmap,1);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error",databaseError.toString());
            }
        });

    }

    public void geo(){
        final DatabaseReference refloc = FirebaseDatabase.getInstance().getReference().child("users").child("loc");

      /*  final GeoFire geoFire=new GeoFire(refloc);

      geoFire.getLocation(id, new LocationCallback() {
            @Override
            public void onLocationResult(String key, GeoLocation location) {
                if (location!=null){
                    final LatLng latLng =new LatLng(location.latitude,location.longitude);

                            Log.e("mi",latLng.toString());
                    if (mi == null){
                        mi=mMap.addMarker((new MarkerOptions().title("Placa: "+String.valueOf(pla)).position(latLng).icon(BitmapDescriptorFactory.fromResource(R.mipmap.icon_car))));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                    }else{
                        //mi.setPosition(latLng);

                        Log.e("paso","paso");
                       rotanim(latLng);
                       // mi.setIcon(BitmapDescriptorFactory.defaultMarker(Color.WHITE));
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error",String.valueOf(databaseError));
            }
        });*/
        final DatabaseReference refrot = FirebaseDatabase.getInstance().getReference().child("users").child(id);
        refrot.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //mi.setPosition(latLng);
                //Log.e("rotac",dataSnapshot.getValue().toString());
                // mi.setIcon(BitmapDescriptorFactory.defaultMarker(Color.WHITE))
                if (dataSnapshot.exists()){

                    final LatLng latLng =new LatLng(Double.parseDouble(String.valueOf(dataSnapshot.child("l").child("0").getValue())),Double.parseDouble(String.valueOf(dataSnapshot.child("l").child("1").getValue())));

                    if (mi == null){
                        mi=mMap.addMarker((new MarkerOptions().title("Placa: "+String.valueOf(pla)).position(latLng).icon(BitmapDescriptorFactory.fromResource(R.drawable.map_lux))));
                        mMap.animateCamera(CameraUpdateFactory.newLatLngZoom(latLng,16));
                    }else{
                        //mi.setPosition(latLng);

                        //Log.e("paso","paso");
                        AnimationMarker.animateMarkerToICS(mi,latLng,latlngInterpolator,String.valueOf(dataSnapshot.child("rota").getValue()));
                        // mi.setIcon(BitmapDescriptorFactory.defaultMarker(Color.WHITE));
                    }
                }else{
                    Toast.makeText(getContext(), "No tiene ninguna ubicacion guardada", Toast.LENGTH_SHORT).show();
                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error",String.valueOf(databaseError));
            }
        });
    }



    public void dis(HashMap<String,Object>orig){
        LatLng latLng =new LatLng(Double.parseDouble(String.valueOf(orig.get("lat"))),Double.parseDouble(String.valueOf(orig.get("lng"))));
            DatabaseReference ref = FirebaseDatabase.getInstance().getReference("users");

            GeoFire geoFire = new GeoFire(ref);
            GeoQuery geoQuery=geoFire.queryAtLocation(new GeoLocation(latLng.latitude,latLng.longitude),0.05);
            geoQuery.addGeoQueryEventListener(new GeoQueryEventListener() {
                @Override
                public void onKeyEntered(String key, GeoLocation location) {
                    Log.e("keygeo",key);
                    if (key.equals(id)){
                        if(!od) {
                            lliv.setVisibility(View.VISIBLE);
                            opciones.setVisibility(View.VISIBLE);
                            od=true;
                        }
                    }
                }

                @Override
                public void onKeyExited(String key) {

                }

                @Override
                public void onKeyMoved(String key, GeoLocation location) {

                }

                @Override
                public void onGeoQueryReady() {

                }

                @Override
                public void onGeoQueryError(DatabaseError error) {
                Log.e("Error",error.toString());
                }
            });

        }

    private void setupGoogleMapScreenSettings(GoogleMap mMap) {
        mMap.setBuildingsEnabled(false);
        mMap.setIndoorEnabled(false);
        mMap.setTrafficEnabled(false);
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setCompassEnabled(true);
        mUiSettings.setMapToolbarEnabled(false);
        mUiSettings.setMyLocationButtonEnabled(true);
        mUiSettings.setScrollGesturesEnabled(true);
        mUiSettings.setZoomGesturesEnabled(true);
        mUiSettings.setTiltGesturesEnabled(true);
        mUiSettings.setRotateGesturesEnabled(true);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.btniv:
                Log.e("paso",btniv.getText().toString());
                if (btniv.getText().toString().equals("INICIAR VIAJE")){
                polyline1.remove();
                orige.remove();

                if (hmap!=null&hmapd!=null&mi!=null) {
                    Log.e("hmap",hmap.toString());
                    Log.e("hmapd",hmapd.toString());
                    //clendes(hmap,hmapd);
                    getdirec(hmap,hmapd,2);
                    Log.e("paso des","pas");
                    editor=settings.edit();
                    editor.putString("esbtn","1");
                    editor.apply();
                }
                }else{
                    if (btniv.getText().toString().equals("DEJAR CLIENTE")) {
                        loading = ProgressDialog.show(getContext(), "Subiendo...", "Espere por favor...", false, false);

                        DatabaseReference esta = FirebaseDatabase.getInstance().getReference("autos/" + id + "/estado");
                        HashMap<String, String> mapa = new HashMap<String, String>();// Mapeo previo
                        mapa.put("estado", "Desocupado");
                        esta.setValue(mapa).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    if (polyline1!=null){
                                        polyline1.remove();
                                    }
                                    polyline2.remove();
                                    desti.remove();
                                    guardarvia("Completado", idvi);
                                    editor=settings.edit();
                                    editor.putString("esbtn","0");
                                    editor.apply();
                                }
                            }
                        });

                    }
                }
                break;

            case R.id.notclie:
                mesa();
                break;
            case R.id.llamar_id:
                break;
            case R.id.navegar_id:
                String x=settings.getString("nave","error");
                if (!x.equals("error")){
                    String []ubi= x.split(",");
                    startNavigateActivity(Double.parseDouble(ubi[0]),Double.parseDouble(ubi[1]));
                }else{
                    Toast.makeText(getContext(), "Error al navegar", Toast.LENGTH_SHORT).show();
                }
                break;
        }
    }
    private void startNavigateActivity(double lat, double lon) {
        try {
            String uri = String.format("http://maps.google.com/maps?daddr=%s,%s", Double.toString(lat), Double.toString(lon));
            Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(uri));
            startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // can't start activity
            Log.e("Error-Nav",e.toString());
        }
    }
    public void guardarvia(String est,String ide) {
        Log.e("veer","entro guardar");

        Log.e("estado",est);
        Log.e("idevi", ide);

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        //tabla usuario
        map.put("estado",est);
        map.put("id", ide);

        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.UP_VI_RE,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                Log.e("puto","PRECEOSA   ");
                                procesarRespuesta(response);

                                Log.e("puto","respuetsa -"+response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("error", String.valueOf(error));
                                loading.dismiss();
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }
    private void procesarRespuesta(JSONObject response) {
        Log.e("puto","ENTRA PROESA   ");
        try {
            // Obtener estado
            String estado = response.getString("estado");
            Log.e("puto","esatso  -"+estado);
            // Obtener mensaje
            String mensaje = response.getString("msg");
            Log.e("puto","mensaje -"+mensaje);

            switch (estado) {
                case "1":
                    // Mostrar mensaje
                    Toast.makeText(
                            getContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    loading.dismiss();
                    lliv.setVisibility(View.GONE);
                    opciones.setVisibility(View.GONE);

                    DatabaseReference datos= FirebaseDatabase.getInstance().getReference("autos/"+id+"/viaje");
                    datos.removeValue();
                    notif("Viaje completado","av",null,2);
                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            getContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    // getApplication().setResult(Activity.RESULT_CANCELED);
                    // Terminar actividad
                    loading.dismiss();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void notif(final String msg, String av, String tkus, final int t) {
        Log.e("veer","entro guardar");
        Log.e("tokenfcm",tkcf);
        Log.e("msg", "Se rechazo tu peticion");

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        //tabla usuario
        map.put("tokenfcm",tkcf);//token fcm destino
        map.put("msg", msg);//mensaje
        map.put("av",av);//identificador
        map.put("tokenus",tkus);//token del que manda


        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.ENV_NOT,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                Log.e("puto","PRECEOSA   ");


                                Log.e("puto","respuetsa -"+response);
                                try {
                                    // Obtener estado
                                    String estado = response.getString("estado");
                                    Log.e("puto","esatso  -"+estado);
                                    // Obtener mensaje
                                    String mensaje = response.getString("msg");
                                    Log.e("puto","mensaje -"+mensaje);

                                    switch (estado) {
                                        case "1":
                                            // Mostrar mensaje
                                            switch (t){
                                                case 1:
                                                    BD base=new BD(getContext(),"baseSms",null,1);
                                                    final SQLiteDatabase bd=base.getWritableDatabase();
                                                    Toast.makeText(
                                                            getContext(),
                                                            mensaje,
                                                            Toast.LENGTH_LONG).show();
                                                    String[]mens=msg.split("%");
                                                    ContentValues registro = new ContentValues();
                                                    registro.put("sms",mens[0]);
                                                    registro.put("fecha",mens[1]);
                                                    registro.put("hora",mens[2]);
                                                    registro.put("nombre",nom);
                                                    registro.put("tipo","2");
                                                    Date d = new Date();
                                                    SimpleDateFormat fec = new SimpleDateFormat("yyyy-MM-dd");
                                                    String fecha = fec.format(d);
                                                    Date h = new Date();
                                                    SimpleDateFormat hor = new SimpleDateFormat("HH:mm:ss");
                                                    String hora = hor.format(h);
                                                    bd.insert("sms", null, registro);

                                                    HashMap<String,String>mapasms=new HashMap<>();
                                                    DatabaseReference reference= FirebaseDatabase.getInstance().getReference().child("autos").child(id).child("viaje").child("smsau").push();
                                                    mapasms.put("fecha",fecha);
                                                    mapasms.put("hora",hora);
                                                    mapasms.put("sms",mens[0]);
                                                    mapasms.put("nombre",settings.getString("nom","error"));
                                                    mapasms.put("placa",settings.getString("pla","error"));
                                                    reference.setValue(mapasms);
                                                    break;
                                                    case 2:
                                                        Toast.makeText(
                                                                getContext(),
                                                                mensaje,
                                                                Toast.LENGTH_LONG).show();
                                                        //loading.dismiss();
                                                        Constantes con=new Constantes();
                                                        con.termi(getActivity());
                                                        //Constantes.termi(getContext());
                                                        startActivity(new Intent(getContext(),Inicio.class).putExtra("map","1").setFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));

                                                        break;
                                            }

                                            break;

                                        case "2":
                                            // Mostrar mensaje
                                            Toast.makeText(
                                                    getContext(),
                                                    mensaje,
                                                    Toast.LENGTH_LONG).show();
                                            // Enviar código de falla
                                            // getApplication().setResult(Activity.RESULT_CANCELED);
                                            // Terminar actividad
                                            //loading.dismiss();
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("error", String.valueOf(error));
                                //loading.dismiss();
                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }
    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        void onFragmentInteraction(Uri uri);
    }
    public void getdirec(final HashMap<String,Object>orig, final HashMap<String,Object> dest, final int x) {
        Log.e("veer","entro guardar");
        Log.e("origen",""+orig);
        Log.e("destino",""+dest);
        Log.e("tipo",String.valueOf(x));

        HashMap<String, String> map = new HashMap<>();// Mapeo previo
        //tabla usuario
        map.put("origen", orig.get("lat") + "," + orig.get("lng"));
        map.put("destino",  dest.get("lat")+","+ dest.get("lng"));
        Log.e("mapaor",String.valueOf(orig.get("lat")) + "," +  String.valueOf(orig.get("lng")));
        Log.e("mapade",String.valueOf(dest.get("lat")) + "," +  String.valueOf(dest.get("lng")));
        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor

        VolleySingleton.getInstance(getContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.DIR,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                Log.e("puto","PRECEOSA   ");

                                Log.e("puto","respuetsa -"+response);
                                try {
                                    // Obtener estado
                                    String estado = response.getString("estado");
                                    Log.e("puto","esatso  -"+estado);
                                    // Obtener mensaje
                                    switch (estado) {
                                        case "1":
                                            // Mostrar
                                            mensaje = response.getJSONObject("msg");
                                            List<List<HashMap<String, String>>> routes = null;
                                            JSONObject infor=null;
                                            switch (x){
                                                case 1:
                                                    try {
                                                        orige = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(String.valueOf(orig.get("lat"))),Double.parseDouble(String.valueOf(orig.get("lng"))))));
                                                        orige.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ub__pin_pickup));
                                                        desti = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(String.valueOf(dest.get("lat"))),Double.parseDouble(String.valueOf(dest.get("lng"))))));
                                                        desti.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ub__pin_destination));
                                                        PathJSONParser parser = new PathJSONParser();
                                                        routes = parser.parse(mensaje);
                                                        infor =parser.infor(mensaje);
                                                       // Log.e("routes",""+infor);
                                                        Log.e("routes","paso marker orige");
                                                        //Log.e("routes",""+infor.getString("duration"));
                                                        ArrayList<LatLng> points = null;
                                                        PolylineOptions polyLineOptions = null;

                                                        // traversing through routes
                                                        for (int i = 0; i < routes.size(); i++) {
                                                            points = new ArrayList<LatLng>();
                                                            polyLineOptions = new PolylineOptions();
                                                            List<HashMap<String, String>> path = routes.get(i);


                                                            for (int j = 0; j < path.size(); j++) {
                                                                HashMap<String, String> point = path.get(j);

                                                                double lat = Double.parseDouble(point.get("lat"));
                                                                double lng = Double.parseDouble(point.get("lng"));
                                                                LatLng position = new LatLng(lat, lng);
                                                                points.add(position);

                                                            }
                                                            polyLineOptions.addAll(points);
                                                            polyLineOptions.width(5);
                                                            polyLineOptions.color(Color.CYAN);
                                                        }

                                                        orige.setTitle(infor.getString("start_address")+"-"+infor.getString("end_address"));
                                                        orige.setSnippet(infor.getString("time")+","+infor.getString("distance"));
                                                        desti.setTitle(infor.getString("start_address")+"-"+infor.getString("end_address"));
                                                        desti.setSnippet(infor.getString("time")+","+infor.getString("distance"));

                                                        polyline1 = mMap.addPolyline(polyLineOptions);
                                                        dis(orig);
                                                        editor=settings.edit();
                                                        editor.putString("nave",String.valueOf(orig.get("lat"))+","+String.valueOf(orig.get("lng")));
                                                        editor.apply();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;

                                                case 2:
                                                    try {
                                                        orige = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(String.valueOf(orig.get("lat"))),Double.parseDouble(String.valueOf(orig.get("lng"))))));
                                                        orige.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ub__pin_pickup));

                                                        desti = mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(String.valueOf(dest.get("lat"))),Double.parseDouble(String.valueOf(dest.get("lng"))))));
                                                       desti.setIcon(BitmapDescriptorFactory.fromResource(R.drawable.ub__pin_destination));
                                                        PathJSONParser parser = new PathJSONParser();
                                                        routes = parser.parse(mensaje);
                                                        infor =parser.infor(mensaje);
                                                        //Log.e("routes",""+infor);
                                                        Log.e("routes","paso marker dest");
                                                        //Log.e("routes",""+infor.getString("duration"));
                                                        ArrayList<LatLng> points = null;
                                                        PolylineOptions polyLineOptions = null;

                                                        // traversing through routes
                                                        for (int i = 0; i < routes.size(); i++) {
                                                            points = new ArrayList<LatLng>();
                                                            polyLineOptions = new PolylineOptions();
                                                            List<HashMap<String, String>> path = routes.get(i);


                                                            for (int j = 0; j < path.size(); j++) {
                                                                HashMap<String, String> point = path.get(j);

                                                                double lat = Double.parseDouble(point.get("lat"));
                                                                double lng = Double.parseDouble(point.get("lng"));
                                                                LatLng position = new LatLng(lat, lng);
                                                                points.add(position);

                                                            }
                                                            polyLineOptions.addAll(points);
                                                            polyLineOptions.width(5);
                                                            polyLineOptions.color(Color.YELLOW);
                                                        }
                                                        orige.setTitle(infor.getString("start_address")+"-"+infor.getString("end_address"));
                                                        orige.setSnippet(infor.getString("time")+","+infor.getString("distance"));

                                                        desti.setTitle(infor.getString("start_address")+"-"+infor.getString("end_address"));
                                                        desti.setSnippet(infor.getString("time")+","+infor.getString("distance"));
                                                        polyline2 = mMap.addPolyline(polyLineOptions);
                                                        dis(hmapd);
                                                        btniv.setText("DEJAR CLIENTE");

                                                        editor=settings.edit();
                                                        editor.putString("nave",String.valueOf(dest.get("lat"))+","+String.valueOf(dest.get("lng")));
                                                        editor.apply();
                                                    } catch (Exception e) {
                                                        e.printStackTrace();
                                                    }
                                                    break;

                                                default:
                                                    Toast.makeText(getContext(),"Error case",Toast.LENGTH_LONG).show();
                                                    break;
                                            }

                                            //Log.e("mensa",String.valueOf(mensaje));
                                            break;

                                        case "2":
                                            // Mostrar mensaje
                                            Toast.makeText(
                                                    getContext(),
                                                    "Error",
                                                    Toast.LENGTH_LONG).show();
                                            break;
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                }
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("error", String.valueOf(error));

                            }
                        }

                ) {
                    @Override
                    public Map<String, String> getHeaders() {
                        Map<String, String> headers = new HashMap<String, String>();
                        headers.put("Content-Type", "application/json; charset=utf-8");
                        headers.put("Accept", "application/json");
                        return headers;
                    }

                    @Override
                    public String getBodyContentType() {
                        return "application/json; charset=utf-8" + getParamsEncoding();
                    }
                }
        );
    }
    public void mesa(){
        final CharSequence[] opcion={"Estoy a dos minutos","Estoy afuera","En 5 minutos me retiro","Otro"};
        final AlertDialog.Builder alertOpcion=new AlertDialog.Builder(getContext());
        alertOpcion.setTitle("MENSAJES");
        //alertOpcion.setMessage("Seleccione mensaje:");
        alertOpcion.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                Date d = new Date();
                SimpleDateFormat fec = new SimpleDateFormat("yyyy-MM-dd");
                String fecha = fec.format(d);
                Date h = new Date();
                SimpleDateFormat hor = new SimpleDateFormat("HH:mm:ss");
                String hora = hor.format(h);
                switch (i){

                    case 0:
                        Toast.makeText(getContext(), opcion[i].toString(), Toast.LENGTH_SHORT).show();
                        notif(opcion[i].toString()+"%"+fecha+"%"+hora,"m",id,1);
                        break;
                    case 1:
                        Toast.makeText(getContext(), opcion[i].toString(), Toast.LENGTH_SHORT).show();
                        notif(opcion[i].toString()+"%"+fecha+"%"+hora,"m",id,1);
                        break;
                    case 2:
                        Toast.makeText(getContext(), opcion[i].toString(), Toast.LENGTH_SHORT).show();
                        notif(opcion[i].toString()+"%"+fecha+"%"+hora,"m",id,1);
                        break;
                    case 3:
                        mesaot();
                        break;

                }
            }
        });
        alertOpcion.show();
    }
    public void mesaot() {
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getContext());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.layout_message, null);
        dialogBuilder.setView(dialogView);
        final EditText edt = (EditText) dialogView.findViewById(R.id.et_me);
        dialogBuilder.setTitle("Mensaje");
        dialogBuilder.setMessage("Mande mensaje personalizado");
        dialogBuilder.setPositiveButton("Mandar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!Objects.equals(edt.getText().toString(), "")){
                    //Toast.makeText(getContext(), edt.getText().toString(), Toast.LENGTH_SHORT).show();
                    Date d = new Date();
                    SimpleDateFormat fec = new SimpleDateFormat("yyyy-MM-dd");
                    String fecha = fec.format(d);
                    Date h = new Date();
                    SimpleDateFormat hor = new SimpleDateFormat("HH:mm:ss");
                    String hora = hor.format(h);
                    if(!edt.getText().toString().trim().isEmpty()){
                        notif(edt.getText().toString()+"%"+fecha+"%"+hora,"m",id,1);

                    }

                }

            }
        });
        dialogBuilder.setNegativeButton("Cancelar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                dialog.dismiss();
            }
        });
        AlertDialog b = dialogBuilder.create();
        b.show();
    }


    @Override
    public void onStart() {
        super.onStart();
        FirebaseUser user= FirebaseAuth.getInstance().getCurrentUser();
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
    private void signOut() {
        String id = settings.getString("id", "error");
        DatabaseReference esta= FirebaseDatabase.getInstance().getReference(getString(R.string.firebase_path)+"/"+id+"/estado");
        HashMap<String,String> mapa = new HashMap<String, String>();// Mapeo previo
        mapa.put("estado","Desconectado");
        esta.setValue(mapa);
        FirebaseAuth.getInstance().signOut();
        getActivity().stopService(new Intent(getContext(),gac_service.class));
        startActivity(new Intent(getContext(),MainActivity.class));
        getActivity().finish();
    }
}
