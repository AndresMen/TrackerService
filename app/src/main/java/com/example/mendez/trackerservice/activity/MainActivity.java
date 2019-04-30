package com.example.mendez.trackerservice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

import com.example.mendez.trackerservice.R;
import com.example.mendez.trackerservice.activity.Crear_cuenta;
import com.example.mendez.trackerservice.activity.Login;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {

    TextView log,crea;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main2);
        log=(TextView) findViewById(R.id.log);
        log.setOnClickListener(this);
        crea=(TextView) findViewById(R.id.reg);
        crea.setOnClickListener(this);
    }
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.log:
               startActivity(new Intent(getBaseContext(),Login.class));
                break;

            case R.id.reg:
                startActivity(new Intent(getBaseContext(),Crear_cuenta.class));
                break;
        }
    }
}