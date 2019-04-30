package com.example.mendez.trackerservice.networking;

import android.Manifest;
import android.app.Service;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.IBinder;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.util.Log;

import com.example.mendez.trackerservice.R;
import com.example.mendez.trackerservice.activity.Inicio;
import com.example.mendez.trackerservice.config.Constantes;
import com.firebase.geofire.GeoFire;
import com.firebase.geofire.GeoLocation;
import com.firebase.geofire.core.GeoHash;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.PendingResult;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.common.api.Status;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResult;
import com.google.android.gms.location.LocationSettingsStatusCodes;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.Arrays;
import java.util.HashMap;
import java.util.Map;

public class gac_service extends Service implements  GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        LocationListener {
    private static final String TAG = gac_service.class.getSimpleName();
     GoogleApiClient mLocationClient;
    LocationRequest mLocationRequest;

    GeoFire geoFire;
    String id;
     LocationSettingsRequest mLocationSettingsRequest;
    public static Status status;
    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferences settings = getSharedPreferences(Constantes.PREFS_NAME, 0);
        id = settings.getString("id", "error");
        buildGoogleApiClient();
        createLocationRequest();
        mLocationClient.connect();
        buildLocationSettingsRequest();
        checkLocationSettings();
        //Make it stick to the notification panel so it is less prone to get cancelled by the Operating System.


    }

    private synchronized void buildGoogleApiClient() {
        mLocationClient = new GoogleApiClient.Builder(this)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API)
                .build();
    }

    private void createLocationRequest() {
        mLocationRequest = new LocationRequest()
                .setInterval(1000)
                .setFastestInterval(1000/2)
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
    }
    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    /*
     * LOCATION CALLBACKS
     */
    @Override
    public void onConnected(Bundle dataBundle) {
        startLocationUpdates();


    }
    public void checkLocationSettings() {
        PendingResult<LocationSettingsResult> result =
                LocationServices.SettingsApi.checkLocationSettings(
                        mLocationClient, mLocationSettingsRequest
                );

        result.setResultCallback(new ResultCallback<LocationSettingsResult>() {
            @Override
            public void onResult(@NonNull LocationSettingsResult result) {
                status = result.getStatus();
                if (Inicio.acti){
                    switch (status.getStatusCode()) {
                        case LocationSettingsStatusCodes.SUCCESS:
                            Log.d("aviso", "Los ajustes de ubicación satisfacen la configuración.");
                            startLocationUpdates();
                            break;
                        case LocationSettingsStatusCodes.RESOLUTION_REQUIRED:
                                sendBroadcast(new Intent().setAction("bcNewMessage").putExtra("loc","f"));
                            break;
                        case LocationSettingsStatusCodes.SETTINGS_CHANGE_UNAVAILABLE:
                            Log.d("aviso", "Los ajustes de ubicación no son apropiados.");
                            break;

                    }
                }
            }
        });
    }
    public void startLocationUpdates() {
    if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
        // TODO: Consider calling
        //    ActivityCompat#requestPermissions
        // here to request the missing permissions, and then overriding
        //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
        //                                          int[] grantResults)
        // to handle the case where the user grants the permission. See the documentation
        // for ActivityCompat#requestPermissions for more details.

        Log.d(TAG, "== Error On onConnected() Permission not granted");
        //Permission not granted by user so cancel the further execution.

        return;
    }
    LocationServices.FusedLocationApi.requestLocationUpdates(mLocationClient, mLocationRequest, this);

    Log.d(TAG, "Connected to Google API");
}
/*
     * Called by Location Services if the connection to the
     * location client drops because of an error.
     */
    @Override
    public void onConnectionSuspended(int i) {
        Log.d(TAG, "Connection suspended");
    }

    //to get the location change
    @Override
    public void onLocationChanged(Location location) {
        Log.d(TAG, "Location changed");


        if (location != null) {
            Log.d(TAG, "== location != null");

            //Send result to activities
            sendMessageToUI(String.valueOf(location.getLatitude()),String.valueOf(location.getLongitude()),String.valueOf(location.getBearing()));
           }
    }

    private void sendMessageToUI(String lat, String lng, final String br) {
        final String path = getString(R.string.firebase_path_users);

        Log.d(TAG, "Sending info...");

       // sendBroadcast(new Intent().setAction("bcNewMessage").putExtra("loc",lat+","+lng));
        DatabaseReference ref = FirebaseDatabase.getInstance().getReference(path).child(id);
       Double x = Double.parseDouble(lat);
        Double y = Double.parseDouble(lng);
        //Log.e("locat",String.valueOf(x)+" --- "+String.valueOf(y));
        geoFire = new GeoFire(ref);
        /*geoFire.setLocation(String.valueOf(id), new GeoLocation(x, y), new GeoFire.CompletionListener() {
            @Override
            public void onComplete(String key, DatabaseError error) {
                HashMap<String ,Object>mapaa = new HashMap<String, Object>();
                DatabaseReference reference=FirebaseDatabase.getInstance().getReference(path+"/rot");
               mapaa.put("rota",br);
                reference.child(String.valueOf(id)).updateChildren(mapaa);
            }
        });*/
        //String itemId = ref.child("items").getKey();

        GeoHash geoHash = new GeoHash(new GeoLocation(x, y));
        Map<String, Object> updates = new HashMap<>();
        updates.put("rota", br);
        updates.put("g", geoHash.getGeoHashString());
        updates.put("l", Arrays.asList(x, y));
        ref.updateChildren(updates);
        //sendBroadcast(new Intent().setAction("NewLocation").putExtra("loc",lat+","+lng+","+br));

        //reference.updateChildrenAsync(map);
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.d(TAG, "Failed to connect to Google API");

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.e("servicio","cerrado");
        LocationServices.FusedLocationApi
                .removeLocationUpdates(mLocationClient, this);
        mLocationClient.disconnect();
    }
    private void buildLocationSettingsRequest() {
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder();
        builder.addLocationRequest(mLocationRequest)
                .setAlwaysShow(true);
        mLocationSettingsRequest = builder.build();
    }
}