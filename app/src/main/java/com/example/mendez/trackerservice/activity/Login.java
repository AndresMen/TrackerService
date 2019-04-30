package com.example.mendez.trackerservice.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v7.app.AlertDialog;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.trackerservice.widget.BaseActivity;
import com.example.mendez.trackerservice.config.Constantes;
import com.example.mendez.trackerservice.R;
import com.example.mendez.trackerservice.networking.VolleySingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
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

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class Login extends BaseActivity implements View.OnClickListener {

    private TextInputEditText ema,pas;
    TextView log,obp;
    private static final String TAG = "EmailPassword";
    private FirebaseAuth mAuth;
    SharedPreferences settings;
    SharedPreferences.Editor editor;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login2);
        ema=(TextInputEditText) findViewById(R.id.email);
        pas=(TextInputEditText) findViewById(R.id.edt_reg_password);
        mAuth = FirebaseAuth.getInstance();
        log=(TextView) findViewById(R.id.tv_signin);
        log.setOnClickListener(this);
        obp=(TextView)findViewById(R.id.tv_forgot_password);
        obp.setOnClickListener(this);
        settings = getSharedPreferences(Constantes.PREFS_NAME, 0);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_signin:
                signIn(ema.getText().toString(), pas.getText().toString());
                break;
            case R.id.tv_forgot_password:
                recpas();
                break;
        }
    }


    public void recpas(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(Login.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.recpass, null);
        dialogBuilder.setView(dialogView);

        final EditText edt = (EditText) dialogView.findViewById(R.id.edtcor);

        dialogBuilder.setTitle("Escriba correo");
        dialogBuilder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                final ProgressDialog progressDialog = new ProgressDialog(Login.this);
                progressDialog.setMessage("verifying..");
                progressDialog.show();
                if (!Objects.equals(edt.getText().toString(), "")){
                   mAuth.sendPasswordResetEmail(edt.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                       @Override
                       public void onComplete(@NonNull Task<Void> task) {
                            if (task.isSuccessful()){
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(),"Se envio con exito al correo "+edt.getText().toString(),Toast.LENGTH_SHORT).show();
                            }  else{
                                progressDialog.dismiss();
                                Toast.makeText(getBaseContext(),"Error correo no existe ",Toast.LENGTH_SHORT).show();
                            }
                       }
                   }).addOnFailureListener(new OnFailureListener() {
                       @Override
                       public void onFailure(@NonNull Exception e) {
                           progressDialog.dismiss();
                           Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_SHORT).show();
                           Log.e("Error f",e.toString());
                       }
                   });
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
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn:" + email);
        if (!validateForm()) {
            return;
        }
        showProgressDialog();
        // [START sign_in_with_email]
        mAuth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = mAuth.getCurrentUser();
                            //updateUI(user);
                            if (user.isEmailVerified()){
                                obtener_datos(user.getUid());
                            }else{
                                Toast.makeText(getBaseContext(),"Verfique su email",Toast.LENGTH_SHORT).show();
                            }
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(Login.this, "Authentication failed.",
                                    Toast.LENGTH_SHORT).show();
                            hideProgressDialog();
                        }

                        // [START_EXCLUDE]
                        if (!task.isSuccessful()) {
                            Toast.makeText(Login.this, "auth_failed.",
                                    Toast.LENGTH_SHORT).show();
                        }

                        // [END_EXCLUDE]
                    }
                });
        // [END sign_in_with_email]
    }


    public void obtener_datos(final String key){
        DatabaseReference ref= FirebaseDatabase.getInstance().getReference("autos").child(key).child("personal");
        Log.e("key",key);
        ref.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot!=null){
                    FirebaseUser user=mAuth.getCurrentUser();
                    editor = settings.edit();
                    editor.putString("id",key);
                    editor.putString("nom", String.valueOf(dataSnapshot.child("user").getValue()));
                    editor.putString("mov", String.valueOf(dataSnapshot.child("movil").getValue()));
                    editor.putString("pla", String.valueOf(dataSnapshot.child("placa").getValue()));
                    editor.putString("ema",user.getEmail());
                    editor.apply();
                    editar(user.getUid(), FirebaseInstanceId.getInstance().getToken());

                    Log.e("key 2",key);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                hideProgressDialog();
            }
        });
    }
    private boolean validateForm() {
        boolean valid = true;

        String email = ema.getText().toString();
        if (TextUtils.isEmpty(email)) {
            ema.setError("Required.");
            valid = false;
        } else {
            ema.setError(null);
        }

        String password = pas.getText().toString();
        if (TextUtils.isEmpty(password)) {
            pas.setError("Required.");
            valid = false;
        } else {
            pas.setError(null);
        }

        return valid;
    }

    public void editar(String tku,String tkfcm) {
        Log.e("veer","entro guardar");
        Log.e("token_usu",tku);
        Log.e("correo",tkfcm);

        HashMap<String, String> map = new HashMap<>();// Mapeo previo
        //tabla usuario
        map.put("token_usu",tku);
        map.put("tokenfcm", tkfcm);


        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.UP_TF,
                        jobject,
                        new Response.Listener<JSONObject>() {
                            @Override
                            public void onResponse(JSONObject response) {
                                // Procesar la respuesta del servidor
                                Log.e("puto","PRECEOSA   ");
                                procesarRespuestaed(response);

                                Log.e("puto","respuetsa -"+response);
                            }
                        },
                        new Response.ErrorListener() {
                            @Override
                            public void onErrorResponse(VolleyError error) {
                                Log.e("error", String.valueOf(error));
                                hideProgressDialog();
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
    private void procesarRespuestaed(JSONObject response) {
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
                            getApplicationContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    final FirebaseUser userre=FirebaseAuth.getInstance().getCurrentUser();
                    final String path;
                    if (userre != null) {
                        path = getString(R.string.firebase_path) + "/" + userre.getUid();
                        DatabaseReference refe = FirebaseDatabase.getInstance().getReference().child(path + "/personal");
                        HashMap<String,String> map = new HashMap<String, String>();// Mapeo previo
                        map.put("user",settings.getString("nom","error"));
                        map.put("movil",settings.getString("mov","error"));
                        map.put("placa",settings.getString("pla","error"));
                        map.put("tkfcm", FirebaseInstanceId.getInstance().getToken());
                        refe.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                hideProgressDialog();
                                if (task.isSuccessful()){
                                    control(userre);
                                }else{
                                    Toast.makeText(getBaseContext(), "Error al ingresar", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });

                    }

                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            getApplicationContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    hideProgressDialog();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void control(final FirebaseUser user){

        String path = getString(R.string.firebase_path) + "/" + user.getUid();
        final HashMap<String,Boolean> mapcon = new HashMap<String, Boolean>();// Mapeo previo

        final DatabaseReference refgetconuser=FirebaseDatabase.getInstance().getReference().child(path+"/control").child(FirebaseInstanceId.getInstance().getToken());
        refgetconuser.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()){
                    exist(user);
                }else {
                    mapcon.put("estado",true);
                    refgetconuser.setValue(mapcon);
                    startActivity(new Intent(getBaseContext(),Inicio.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
                    finish();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }
    public void exist(final FirebaseUser user){
        String path = getString(R.string.firebase_path) + "/" + user.getUid();
        DatabaseReference refgetcon=FirebaseDatabase.getInstance().getReference().child(path+"/control");

        refgetcon.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.exists()) {
                    for (DataSnapshot dataChildren:dataSnapshot.getChildren()){
                        setc(dataChildren,user);
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                Log.e("error",databaseError.toString());
            }
        });

    }
    public void setc(DataSnapshot dataSnapshot,FirebaseUser us){
        String path = getString(R.string.firebase_path) + "/" + us.getUid();
        DatabaseReference refcon=FirebaseDatabase.getInstance().getReference().child(path+"/control").child(dataSnapshot.getKey());
        HashMap<String,Boolean> mapcon = new HashMap<String, Boolean>();// Mapeo previo
        if (dataSnapshot.getKey().equals(FirebaseInstanceId.getInstance().getToken())){
            mapcon.put("estado",true);
            refcon.setValue(mapcon);
            startActivity(new Intent(getBaseContext(),Inicio.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK));
            finish();
        }else{
            mapcon.put("estado",false);
            refcon.setValue(mapcon);
        }

    }
}
