package com.example.mendez.trackerservice.activity;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.example.mendez.trackerservice.config.Constantes;
import com.example.mendez.trackerservice.R;
import com.example.mendez.trackerservice.networking.VolleySingleton;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.HashMap;
import java.util.Map;

public class edtda extends AppCompatActivity implements View.OnClickListener {
    FirebaseAuth mAuth;
    FirebaseUser user;
    ProgressDialog progressDialog;
    SharedPreferences settings;
    EditText edt_reg_email,edt_reg_password,edt_reg_confirmpassword;
    String x;
    LinearLayout llema,llpss,llcpss;
    View vem,vpas,vcpass;
    TextView btnm,tex;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edtda2);

        settings = getSharedPreferences(Constantes.PREFS_NAME, 0);

        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        llema=(LinearLayout)findViewById(R.id.llema);
        llpss=(LinearLayout)findViewById(R.id.llpass);
        llcpss=(LinearLayout)findViewById(R.id.llcpass);
        vem=(View)findViewById(R.id.vem);
        vpas=(View)findViewById(R.id.vpss);
        vcpass=(View)findViewById(R.id.vcpss);
        btnm=(TextView)findViewById(R.id.tv_mod);
        btnm.setOnClickListener(this);
        tex=(TextView)findViewById(R.id.tex);
        x=getIntent().getStringExtra("x");
        if (x!=null){
            if (x.equals("1")){
                tex.setText("para modificar el correo escriba el nuevo correo");
                llpss.setVisibility(View.GONE);
                llcpss.setVisibility(View.GONE);
                vpas.setVisibility(View.GONE);
                vcpass.setVisibility(View.GONE);
            }else{
                if (x.equals("2")){
                    tex.setText("para modificar contraseña escriba la nueva contraseña");
                    llema.setVisibility(View.GONE);
                    vem.setVisibility(View.GONE);
                }
            }
        }
    }


    public void reaute(){
        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(edtda.this);
        LayoutInflater inflater = getLayoutInflater();
        final View dialogView = inflater.inflate(R.layout.recpass, null);
        dialogBuilder.setView(dialogView);

        final EditText pss=(EditText)dialogView.findViewById(R.id.rtpass);
        dialogBuilder.setTitle("Escriba correo");
        dialogBuilder.setPositiveButton("Enviar", new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int whichButton) {
                if (!pss.getText().toString().equals("")) {
                    progressDialog = new ProgressDialog(edtda.this);
                    progressDialog.setMessage("verifying..");
                    progressDialog.show();
                    AuthCredential credential = EmailAuthProvider
                            .getCredential(user.getEmail(), pss.getText().toString());

                    user.reauthenticate(credential)
                            .addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        Log.d("reaute", "User re-authenticated.");
                                        if(llema.isShown()){
                                            user.updateEmail(edt_reg_email.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                    @Override
                                                    public void onComplete(@NonNull Task<Void> task) {
                                                        editar(user.getUid(),edt_reg_email.getText().toString().trim());
                                                    }
                                                });
                                        }else{
                                            user.updatePassword(edt_reg_password.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    Toast.makeText(getBaseContext(),"Se cambio el password",Toast.LENGTH_SHORT).show();
                                                }
                                            });
                                        }
                                    }else{
                                        Toast.makeText(getBaseContext(),"Error al reautemticar",Toast.LENGTH_SHORT).show();
                                    }
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
public boolean veriema(){
    boolean isvalid_details=true;
    if(edt_reg_email.getText().toString().trim().equalsIgnoreCase("") || edt_reg_email.getText().toString().trim().length()==0){
        isvalid_details=false;
        Log.e("ERROR",edt_reg_email.getText().toString());
        edt_reg_email.setError("Rellene");
    }
    else if(TextUtils.isEmpty(edt_reg_email.getText().toString())&&android.util.Patterns.EMAIL_ADDRESS.matcher(edt_reg_email.getText().toString()).matches()) {
        isvalid_details=false;
        Log.e("ERROR",edt_reg_email.getText().toString()+"  2");
        edt_reg_email.setError("No valido");
    }

        return isvalid_details;
}
public boolean verps(){
    boolean isvalid_details=true;
    if(edt_reg_password.getText().toString().trim().length() == 0) {
        isvalid_details=false;
        Log.e("ERROR",edt_reg_password.getText().toString());
        edt_reg_password.setError("Rellene");
    }else if(!edt_reg_password.getText().toString().trim().matches("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$")) {
        isvalid_details=false;
        Log.e("ERROR",edt_reg_password.getText().toString()+"  2");
        edt_reg_password.setError("El password debe contener ya sea letras y numeros");
    }
    else if (edt_reg_password.getText().toString().trim().length() < 6 || edt_reg_password.getText().toString().trim().length() > 32) {
        isvalid_details=false;
        Log.e("ERROR",edt_reg_password.getText().toString()+"  3");
        edt_reg_password.setError("Mas de seis, menor a treinta y dos caracteres");
    }else if(edt_reg_password.getText().toString().trim().length() > 32) {
        isvalid_details=false;
        Log.e("ERROR",edt_reg_password.getText().toString()+"  4");
        edt_reg_password.setError("Menor a treinta y dos caracteres");
    }
    else if(edt_reg_confirmpassword.getText().toString().trim().length() == 0) {
        isvalid_details=false;
        Log.e("ERRORcp",edt_reg_confirmpassword.getText().toString());
        edt_reg_confirmpassword.setError("Rellene");
    }
    else if(!edt_reg_confirmpassword.getText().toString().trim().matches("^(?=.*[a-zA-Z])(?=.*[0-9])[a-zA-Z0-9]+$")){
        isvalid_details=false;
        Log.e("ERRORcp",edt_reg_confirmpassword.getText().toString()+"  2");
        edt_reg_confirmpassword.setError("El password debe contener ya sea letars y numeros o carateres");
    }
    else if(edt_reg_confirmpassword.getText().toString().trim().length() < 6 || edt_reg_confirmpassword.getText().toString().trim().length() > 32) {
        isvalid_details=false;
        Log.e("ERRORcp",edt_reg_confirmpassword.getText().toString()+"  3");
        edt_reg_confirmpassword.setError("Mas de seis, menor a treinta y dos caracteres");
    }
    else if(edt_reg_confirmpassword.getText().toString().trim().length() > 32) {
        isvalid_details=false;
        Log.e("ERRORcp",edt_reg_confirmpassword.getText().toString()+"  4");
        edt_reg_confirmpassword.setError("Menor a treinta y dos caracteres");
    }
    else if(!edt_reg_confirmpassword.getText().toString().trim().equals(edt_reg_password.getText().toString().trim())) {
        isvalid_details=false;
        Log.e("ERRORcp",edt_reg_confirmpassword.getText().toString()+"  5");
        edt_reg_password.setError("No coincide el password");
    }
    return isvalid_details;
}
    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.tv_mod:

                if (llema.isShown()){
                    if (veriema()){
                        reaute();
                    }
                }else{
                    if (llpss.isShown()&llcpss.isShown()){
                        if (verps()){
                            reaute();
                        }
                    }
                }

                break;
        }
    }
    public void editar(String tku,String cor) {
        Log.e("veer","entro guardar");
        Log.e("token_usu",tku);
        Log.e("correo",cor);

        HashMap<String, String> map = new HashMap<>();// Mapeo previo
        //tabla usuario
        map.put("token_usu",tku);
        map.put("correo", cor);


        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.UP_CU,
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
                                progressDialog.dismiss();
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

                        SharedPreferences settings = getSharedPreferences(Constantes.PREFS_NAME, 0);
                        final SharedPreferences.Editor editor = settings.edit();
                        editor.putString("ema",edt_reg_email.getText().toString());
                        editor.apply();



                    progressDialog.dismiss();
                    break;

                case "2":
                    // Mostrar mensaje
                    Toast.makeText(
                            getApplicationContext(),
                            mensaje,
                            Toast.LENGTH_LONG).show();
                    // Enviar código de falla
                    // getApplication().setResult(Activity.RESULT_CANCELED);
                    // Terminar actividad
                    progressDialog.dismiss();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

}
