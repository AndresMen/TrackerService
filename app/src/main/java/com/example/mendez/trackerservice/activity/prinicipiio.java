package com.example.mendez.trackerservice.activity;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.example.mendez.trackerservice.R;
import com.example.mendez.trackerservice.config.Constantes;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;


public class prinicipiio extends AppCompatActivity {

    SharedPreferences settings;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_prinicipiio);
        settings = getSharedPreferences(Constantes.PREFS_NAME, 0);
    }

    @Override
    protected void onStart() {
        super.onStart();
        FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
        if (currentUser!=null) {
            Intent us=new Intent(getBaseContext(), Inicio.class);
            Bundle msg=this.getIntent().getExtras();


            if (msg!=null) {

                if (msg.get("mapo")!=null|msg.get("av")!=null){
                msg = this.getIntent().getExtras();
                us.putExtra("noda", msg);
                Log.e("avisopr", String.valueOf(msg));
                Log.e("avisoprin", String.valueOf(getIntent().getExtras()));
                Log.e("avisopr mapo", String.valueOf(msg.get("mapo")));
                Log.e("avisopr mapd", String.valueOf(msg.get("mapd")));
                Log.e("avisopr tkfcmcli", String.valueOf(msg.get("tckcli")));
               /* }
                else{
                    msg = getIntent().getExtras();
                    us.putExtra("bun",msg);
                    Log.e("bus",msg.toString());
                    Log.e("busmes",String.valueOf(msg.get("av")));*/
                }
            }else{
                Log.e("bundle","vacio");
            }

            startActivity(us);
            finish();
        }else{
            startActivity(new Intent(getBaseContext(),MainActivity.class));
            finish();
        }

    }

}
