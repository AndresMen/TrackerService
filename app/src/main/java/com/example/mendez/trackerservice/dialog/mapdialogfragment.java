package com.example.mendez.trackerservice.dialog;

import android.app.Activity;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.location.Address;
import android.location.Geocoder;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AnimationUtils;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.trackerservice.R;
import com.example.mendez.trackerservice.activity.Inicio;
import com.example.mendez.trackerservice.config.Constantes;
import com.example.mendez.trackerservice.networking.VolleySingleton;
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
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;

public class mapdialogfragment extends DialogFragment implements OnMapReadyCallback {
    GoogleMap mMap;
    HashMap<String, Object> mapo;
    HashMap<String, Object> mapd;
    ProgressBar mProgressBar;
    CountDownTimer mCountDownTimer;
    Context mcontext;
    String tkcf = null;
    final int[] i = {0};

    ProgressDialog loading = null;
    SharedPreferences settings;
    String id = null;
    TextView ori,des,can,ace;
    MapView mapView;
    //Marker mi;

    View viewcircle;
    SharedPreferences.Editor editor;
    public mapdialogfragment() {
        // Required empty public constructor
        setRetainInstance(true);

    }


    public AlertDialog createDia(Bundle saved) {

        settings = getActivity().getSharedPreferences(Constantes.PREFS_NAME, 0);
        Geocoder geocoder = new Geocoder(getActivity(), Locale.getDefault());

        mapo = (HashMap<String, Object>) getArguments().getSerializable("mapo");
        mapd = (HashMap<String, Object>) getArguments().getSerializable("mapd");

        List<Address> addresseso;
        List<Address> addressesd;
        String addresso = null;
        String addressd = null;
        try {
            addresseso = geocoder.getFromLocation(Double.parseDouble(String.valueOf(mapo.get("lat"))), Double.parseDouble(String.valueOf(mapo.get("lng"))), 1);
            addressesd = geocoder.getFromLocation(Double.parseDouble(String.valueOf(mapd.get("lat"))), Double.parseDouble(String.valueOf(mapd.get("lng"))), 1);
            addresso = addresseso.get(0).getAddressLine(0);

            addressd = addressesd.get(0).getAddressLine(0);
        } catch (IOException e) {
            e.printStackTrace();
        }


        final String id = settings.getString("id", "error");
        final String idvi = getArguments().getString("idvi");
        tkcf = getArguments().getString("tkfc");
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());

        LayoutInflater inflater = getActivity().getLayoutInflater();

        View v = inflater.inflate(R.layout.dialog_map2, null);
        viewcircle=(View)v.findViewById(R.id.vicir);

        mapView=(MapView)v.findViewById(R.id.fragment1);
        mapView.onCreate(saved);

        mapView.onResume();// needed to get the map to display immediately
        //mapFragment.getMapAsync(this);
        try {
            MapsInitializer.initialize(getActivity().getApplicationContext());
        } catch (Exception e) {
            e.printStackTrace();
        }
        mapView.getMapAsync(this);

        ori=(TextView)v.findViewById(R.id.origen);
        des=(TextView)v.findViewById(R.id.destino);
        can=(TextView)v.findViewById(R.id.txt_can);
        ace=(TextView)v.findViewById(R.id.txt_ace);
        builder.setView(v);
       // builder.setTitle("Aviso");
        if (addresso != null & addressd != null) {
           // builder.setMessage("Un cliente esta requiriendo su servicio\n Se encuentra en " + addresso + "\n y va a " + addressd);
        ori.setText(addresso);
        des.setText(addressd);
        }
        builder.setCancelable(false);
        ace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Inicio.noda=null;
                Intent m = new Intent(getActivity(), Inicio.class);
                m.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK);
                m.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                m.putExtra("map", "1");
                // m.putExtra("mapo",mapo);
                //m.putExtra("mapd",mapd);
                startActivity(m);
                Log.e("dialo", "pas por qui");

                DatabaseReference esta = FirebaseDatabase.getInstance().getReference("autos/" + id + "/estado");
                HashMap<String, String> mapa = new HashMap<String, String>();// Mapeo previo
                mapa.put("estado", "Ocupado");
                esta.setValue(mapa).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            notif("Se acepto tu peticion", "n");
                        }
                    }
                });
                mCountDownTimer.cancel();
            }
        });
        can.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loading = ProgressDialog.show(getContext(), "Subiendo...", "Espere por favor...", false, false);
                Inicio.noda=null;
                DatabaseReference esta = FirebaseDatabase.getInstance().getReference("autos/" + id + "/estado");
                HashMap<String, String> mapa = new HashMap<String, String>();// Mapeo previo
                mapa.put("estado", "Desocupado");
                esta.setValue(mapa).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        if (task.isSuccessful()) {
                            guardarvia("Rechazado", idvi);
                            notif("Se rechazo tu peticion", "av");
                            Constantes.termi((Activity)mcontext);
                            DatabaseReference datos= FirebaseDatabase.getInstance().getReference("autos/"+id+"/viaje");
                            datos.removeValue();
                            mCountDownTimer.cancel();
                        }
                    }
                });
                dismiss();
            }
        });

        mProgressBar = (ProgressBar) v.findViewById(R.id.progressbar);
        mProgressBar.setProgress(i[0]);
        viewcircle.setAnimation(AnimationUtils.loadAnimation(getContext(),R.anim.dmasa));

        mCountDownTimer = new CountDownTimer(60000, 1000) {

            @Override
            public void onTick(long millisUntilFinished) {
                Log.v("Log_tag", "Tick of Progress" + i[0] + millisUntilFinished);
                i[0]++;
                mProgressBar.setProgress(i[0] * 100 / (60000 / 1000));

            }

            @Override
            public void onFinish() {
                //Do what you want
                i[0]++;
                mProgressBar.setProgress(100);

                DatabaseReference esta = FirebaseDatabase.getInstance().getReference("autos/" + id + "/estado");
                HashMap<String, String> mapa = new HashMap<String, String>();// Mapeo previo
                mapa.put("estado", "Desocupado");
                esta.setValue(mapa);
                dismiss();
                Inicio.noda=null;
                Constantes.termi((Activity)mcontext);
                DatabaseReference datos= FirebaseDatabase.getInstance().getReference("autos/"+id+"/viaje");
                datos.removeValue();
                }
        };
        mCountDownTimer.start();

        return builder.create();
    }


    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        return createDia(savedInstanceState);
    }


    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;
        UiSettings mUiSettings = mMap.getUiSettings();
        mUiSettings.setZoomControlsEnabled(false);
        mUiSettings.setMapToolbarEnabled(false);
        Marker marker1= mMap.addMarker(new MarkerOptions().position(new LatLng(Double.parseDouble(String.valueOf(mapo.get("lat"))),Double.parseDouble(String.valueOf(mapo.get("lng"))))).title("Ubicacion cliente").icon(BitmapDescriptorFactory.fromResource(R.drawable.ub__partner_funnel_helix_pin)));
        /*LatLngBounds.Builder builder = new LatLngBounds.Builder(); //the include method will calculate the min and max bound.
        builder.include(marker1.getPosition());
       // builder.include(mi.getPosition());
        LatLngBounds bounds = builder.build();
        int width = getActivity().getResources().getDisplayMetrics().widthPixels;
        int height = getActivity().getResources().getDisplayMetrics().heightPixels;
        int padding = (int) (width * 0.10);
        // offset from edges of the map 10% of screen
        CameraUpdate cu = CameraUpdateFactory.newLatLngBounds(bounds, width/2, height/2, padding);
        Log.e("dimension",width+"///"+height);
        mMap.animateCamera(cu);*/
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(new LatLng(marker1.getPosition().latitude,marker1.getPosition().longitude),14));

    }

    @Override
    public void onDestroyView() {
        Dialog dialog = getDialog();
        if (dialog != null && getRetainInstance()) {
            dialog.setDismissMessage(null);
            Log.e("pasooo","ondes1");
        }
        super.onDestroyView();
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
        VolleySingleton.getInstance(mcontext).addToRequestQueue(
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
                            mcontext,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                            loading.dismiss();
                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            mcontext,
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
    public void notif(String msg,String av) {
        Log.e("veer","entro guardar");
        id=settings.getString("id","error");
        Log.e("tokenfcm",tkcf);
        Log.e("msg", "Se rechazo tu peticion");

        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        //tabla usuario
        map.put("tokenfcm",tkcf);
        map.put("msg", msg);
        map.put("av",av);
        map.put("tokenus",id);

        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(mcontext).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.ENV_NOT,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                Log.e("puto","PRECEOSA   ");
                                procesarRespuestanot(response);

                                Log.e("puto","respuetsa -"+response);
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
    private void procesarRespuestanot(JSONObject response) {
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
                            mcontext,
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    //loading.dismiss();

                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            mcontext,
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

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mcontext=context;
    }


}
