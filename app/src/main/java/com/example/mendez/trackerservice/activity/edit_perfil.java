package com.example.mendez.trackerservice.activity;

import android.app.DatePickerDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.drawable.BitmapDrawable;
import android.media.ExifInterface;
import android.media.MediaScannerConnection;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.design.widget.TextInputEditText;
import android.support.v4.content.FileProvider;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RelativeLayout;
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
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.iid.FirebaseInstanceId;
import com.squareup.picasso.Callback;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class edit_perfil extends AppCompatActivity implements View.OnClickListener {



    // [START declare_auth]
    private FirebaseAuth mAuth;
    // [END declare_auth]

    ProgressDialog progressDialog=null;

    private FirebaseUser user;
//end

    int RegisterStep = 0;
    ImageView icon_pending_first,icon_pending_fir_thd,icon_pending_sec_thd,iv_user_photo,iv_auto_photo;

    RelativeLayout layout_next,layout_back;
    View register_step_one,register_step_two,register_step_three;

    LinearLayout layout_header_step_first,layout_header_step_secound,layout_header_step_thd;
    boolean isPersonalDetailsSelected=false,isVehicleDetailsSelected=false,isLegalDetailsSelected=false;
    Boolean SecoundStepValidation = false;
    Boolean ThrdStepValidation = false;
    TextInputEditText edt_reg_name,edt_reg_username,edt_reg_mobile,edt_reg_dob,edt_reg_address;
    RadioButton genmas,genfem;
    TextInputEditText edt_reg_carmake,edt_reg_camodel,edt_reg_cartype,edt_reg_carnumber,edt_reg_seating_capacity;
    TextInputEditText edt_reg_driving_license,edt_reg_license_exp_date,edt_reg_license_plate,edt_reg_insuarance;
    private static final String CERO = "0";
    private static final String GUION = "-";

    final int mes = 1;// c.get(Calendar.MONTH);
    final int dia = 1;// c.get(Calendar.DAY_OF_MONTH);
    final int anio = 2000;//c.get(Calendar.YEAR);

    TextView tv_signin_register,tv_imau;
    ProgressDialog loading = null;
    LinearLayout em,pss,cpss;
    View vem,vpss,vcpss;
    private static final String CARPETA_PRINCIPAL = "misImagenesApp/";//directorio principal
    private static final String CARPETA_IMAGEN = "imagenes";//carpeta donde se guardan las fotos
    private static final String DIRECTORIO_IMAGEN = CARPETA_PRINCIPAL + CARPETA_IMAGEN;//ruta carpeta de directorios
    Uri imageUri;
    File fileImagen;

    String path,imagen,imagen1;
    final int COD_SELECCIONA=10;
    final int COD_FOTO=20;
    final int COD_SELECCIONA1=30;
    final int COD_FOTO1=40;
    Bitmap bitmap,bitmap1;
    String imgdir,imgdiraut;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_perfil2);

        // [START initialize_auth]
        mAuth = FirebaseAuth.getInstance();
        // [END initialize_auth]
        isPersonalDetailsSelected=false;
        isVehicleDetailsSelected=false;
        isLegalDetailsSelected=false;

        register_step_one = (View) findViewById(R.id.register_step_one);
        register_step_two = (View) findViewById(R.id.register_step_two);
        register_step_three = (View) findViewById(R.id.register_step_three);

        layout_header_step_first = (LinearLayout)findViewById(R.id.layout_header_step_first);
        layout_header_step_secound = (LinearLayout)findViewById(R.id.layout_header_step_secound);
        layout_header_step_thd = (LinearLayout)findViewById(R.id.layout_header_step_thd);

        icon_pending_first = (ImageView)findViewById(R.id.icon_pending_first);
        icon_pending_fir_thd = (ImageView)findViewById(R.id.icon_pending_fir_thd);
        icon_pending_sec_thd = (ImageView)findViewById(R.id.icon_pending_sec_thd);

        //step1 Personal details
        iv_user_photo=(ImageView)findViewById(R.id.iv_user_photo);
        iv_user_photo.setOnClickListener(this);
        edt_reg_name=(TextInputEditText)findViewById(R.id.edt_reg_name);
        edt_reg_username=(TextInputEditText)findViewById(R.id.edt_reg_username);
        edt_reg_mobile=(TextInputEditText)findViewById(R.id.edt_reg_mobile);
        edt_reg_dob=(TextInputEditText)findViewById(R.id.edt_reg_dob);
        edt_reg_dob.setOnClickListener(this);
        genmas=(RadioButton) findViewById(R.id.radio_masculino);
        genfem=(RadioButton) findViewById(R.id.radio_femenino);
        edt_reg_address=(TextInputEditText)findViewById(R.id.edt_reg_address);

        //step2 Vehicle details
        edt_reg_carmake=(TextInputEditText)findViewById(R.id.edt_reg_carmake);
        edt_reg_camodel=(TextInputEditText)findViewById(R.id.edt_reg_camodel);
        edt_reg_cartype=(TextInputEditText)findViewById(R.id.edt_reg_cartype);
        edt_reg_cartype.setOnClickListener(this);
        edt_reg_carnumber=(TextInputEditText)findViewById(R.id.edt_reg_carnumber);
        edt_reg_seating_capacity=(TextInputEditText)findViewById(R.id.edt_reg_seating_capacity);

        //step3 Legacy details
        edt_reg_driving_license=(TextInputEditText)findViewById(R.id.edt_reg_driving_license);
        edt_reg_license_exp_date=(TextInputEditText)findViewById(R.id.edt_reg_license_exp_date);
        edt_reg_license_exp_date.setOnClickListener(this);
        edt_reg_license_plate=(TextInputEditText)findViewById(R.id.edt_reg_license_plate);
        edt_reg_insuarance=(TextInputEditText)findViewById(R.id.edt_reg_insuarance);

        iv_auto_photo=(ImageView)findViewById(R.id.iv_auto_photo);
        iv_auto_photo.setOnClickListener(this);
        tv_imau=(TextView)findViewById(R.id.tv_imau);
        tv_imau.setOnClickListener(this);

        layout_back=(RelativeLayout) findViewById(R.id.layout_back);
        layout_back.setOnClickListener(this);

        layout_next = (RelativeLayout)findViewById(R.id.layout_next);
        layout_next.setOnClickListener(this);

        tv_signin_register = (TextView)findViewById(R.id.tv_signin_register);
        tv_signin_register.setOnClickListener(this);
        tv_signin_register.setText("EDITAR");

        em=(LinearLayout)findViewById(R.id.llema);
        em.setVisibility(View.GONE);
        pss=(LinearLayout)findViewById(R.id.llpass);
        pss.setVisibility(View.GONE);
        cpss=(LinearLayout)findViewById(R.id.llcpass);
        cpss.setVisibility(View.GONE);
        vem=(View)findViewById(R.id.vem);
        vem.setVisibility(View.GONE);
        vpss=(View)findViewById(R.id.vpss);
        vpss.setVisibility(View.GONE);
        vcpss=(View)findViewById(R.id.vcpss);
        vcpss.setVisibility(View.GONE);
        cargar();


    }
    public boolean PersonalDetailValidation(){
        boolean isvalid_details=true;
        if(edt_reg_name.getText().toString().trim().equalsIgnoreCase("") || edt_reg_name.getText().toString().trim().length()==0){
            isvalid_details=false;
            Log.e("ERRORna",edt_reg_name.getText().toString());
            edt_reg_name.setError("Rellene");
        } else if(edt_reg_name.getText().toString().length() != 0 && edt_reg_name.getText().toString().length() < 4){
            isvalid_details=false;
            Log.e("ERRORna",edt_reg_name.getText().toString()+"  2");
            edt_reg_name.setError("Mas de cuatro caracteres");
        }
        else if(edt_reg_name.getText().toString().length() != 0 && edt_reg_name.getText().toString().length() > 30){
            isvalid_details=false;
            Log.e("ERRORna",edt_reg_name.getText().toString()+"  3");
            edt_reg_name.setError("No mas de treinta caracteres");
        }
        else if(edt_reg_username.getText().toString().trim().equalsIgnoreCase("") || edt_reg_username.getText().toString().trim().length()==0) {
            isvalid_details=false;
            Log.e("ERRORus",edt_reg_username.getText().toString());
            edt_reg_username.setError("Rellene");
        }
        else if(edt_reg_username.getText().toString().length() != 0 && edt_reg_username.getText().toString().length() < 4){
            isvalid_details=false;
            Log.e("ERRORus",edt_reg_username.getText().toString()+"  2");
            edt_reg_username.setError("Mas de cuatro caracteres");
        }
        else if(edt_reg_username.getText().toString().length() != 0 && edt_reg_username.getText().toString().length() > 30){
            isvalid_details=false;
            Log.e("ERROR",edt_reg_username.getText().toString()+"  3");
            edt_reg_username.setError("No mas de treinta caracteres");
        }
        else if(!isValidUserName(edt_reg_username.getText().toString())){
            isvalid_details=false;
            Log.e("ERROR",edt_reg_username.getText().toString()+"  4");
            edt_reg_username.setError("No valido");
        }
        else if(edt_reg_mobile.getText().toString().trim().equalsIgnoreCase("") || edt_reg_mobile.getText().toString().trim().length()==0){
            isvalid_details=false;
            Log.e("ERROR",edt_reg_mobile.getText().toString());
            edt_reg_mobile.setError("Rellene");
        }
        else if(edt_reg_mobile.getText().toString().trim().length() < 7) {
            isvalid_details=false;
            Log.e("ERROR",edt_reg_mobile.getText().toString()+"  2");
            edt_reg_mobile.setError("Mas de siete caracteres");
        }

        else if(edt_reg_dob.getText().toString().trim().equalsIgnoreCase("")||edt_reg_dob.getText().toString().trim().length()==0){
            isvalid_details=false;
            Log.e("ERRORcp",edt_reg_dob.getText().toString());
            edt_reg_dob.setError("Rellene");
        }
        else if(edt_reg_address.getText().toString().trim().equalsIgnoreCase("")||edt_reg_address.getText().toString().trim().length()==0){
            isvalid_details=false;
            Log.e("ERRORcp",edt_reg_address.getText().toString());
            edt_reg_address.setError("Rellene");
        }
        else if(edt_reg_address.getText().toString().trim().matches("^[0-9]*$")){
            isvalid_details=false;
            Log.e("ERRORcp",edt_reg_address.getText().toString()+"  2");
            edt_reg_address.setError("La dirección debe ser caracteres y dígitos");
        }else if (iv_user_photo.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.avatar).getConstantState())){
            isvalid_details=false;
            Log.e("Errorim","Falta imagen");
            Toast.makeText(getBaseContext(),"Coloque una imagen",Toast.LENGTH_LONG).show();
        }

        return isvalid_details;
    }
    public boolean VehicleDetailValidation() {

        boolean isvalid_details = true;

        if(edt_reg_carmake.getText().toString().trim().equalsIgnoreCase("") || edt_reg_carmake.getText().toString().trim().length()==0){
            //Toast.makeText(RegisterActivity.this,"Error",Toast.LENGTH_LONG).show();
            isvalid_details=false;
            edt_reg_carmake.setError("Rellene");
        }
        else if(edt_reg_carmake.getText().toString().trim().matches("^[0-9]*$")){
            isvalid_details=false;
            edt_reg_carmake.setError("No valido");
        }
        else if(edt_reg_camodel.getText().toString().trim().equalsIgnoreCase("") || edt_reg_camodel.getText().toString().trim().length()==0){
            isvalid_details=false;
            edt_reg_camodel.setError("Rellene");
        }
        else if(edt_reg_cartype.getText().toString().trim().equalsIgnoreCase("") || edt_reg_cartype.getText().toString().trim().length()==0){
            isvalid_details=false;
            edt_reg_cartype.setError("Rellene");
        }
        else if(edt_reg_carnumber.getText().toString().trim().equalsIgnoreCase("") || edt_reg_carnumber.getText().toString().trim().length()==0){
            isvalid_details=false;
            edt_reg_carnumber.setError("Rellene");
        }
        else if(!isValidUserName(edt_reg_carnumber.getText().toString())){
            isvalid_details=false;
            edt_reg_carnumber.setError("No valido");
        }
        else if(edt_reg_seating_capacity.getText().toString().trim().equalsIgnoreCase("")||edt_reg_seating_capacity.getText().toString().trim().length()==0){
            isvalid_details=false;
            edt_reg_seating_capacity.setError("Rellene");
        }else if(!edt_reg_seating_capacity.getText().toString().trim().matches("^[0-9]*$")){
            isvalid_details=false;
            edt_reg_seating_capacity.setError("No validos");
        }else if (iv_auto_photo.getDrawable().getConstantState().equals(getResources().getDrawable(R.drawable.avauto).getConstantState())){
            isvalid_details=false;
            Log.e("Errorim","Falta imagende auto");
            Toast.makeText(getBaseContext(),"Coloque una imagen auto",Toast.LENGTH_LONG).show();
        }
        SecoundStepValidation = isvalid_details;

        return isvalid_details;
    }
    public boolean isValidLegalDetails() {
        Log.d("isLegalDetailsSelected", "isLegalDetailsSelected = " + isLegalDetailsSelected);
        boolean isvalid_details = true;

        if(edt_reg_driving_license.getText().toString().trim().equalsIgnoreCase("") || edt_reg_driving_license.getText().toString().trim().length()==0){
            isvalid_details=false;
            edt_reg_driving_license.setError("Rellene");
        }
        else if(edt_reg_driving_license.getText().toString().trim().length()>16){
            isvalid_details=false;
            edt_reg_driving_license.setError("No mayor a 16 caracteres");
        }else if(edt_reg_license_plate.getText().toString().trim().equalsIgnoreCase("") || edt_reg_license_plate.getText().toString().trim().length()==0){
            isvalid_details=false;
            edt_reg_license_plate.setError("Rellene");
        }
        else if(!isValidUserName(edt_reg_license_plate.getText().toString())){
            isvalid_details=false;
            edt_reg_license_plate.setError("No valido");
        }
        else if(edt_reg_insuarance.getText().toString().trim().equalsIgnoreCase("")||edt_reg_insuarance.getText().toString().trim().length()==0){
            isvalid_details=false;
            edt_reg_insuarance.setError("Rellene");
        }
        else if(!isValidUserName(edt_reg_insuarance.getText().toString())){
            isvalid_details=false;
            edt_reg_insuarance.setError("No valido");
        }
        ThrdStepValidation = isvalid_details;

        return isvalid_details;
    }

    public static boolean isValidUserName(String str) {
        boolean isValid = false;
        String expression = "^[a-z_A-Z0-9]*$";
        CharSequence inputStr = str;
        Pattern pattern = Pattern.compile(expression);
        Matcher matcher = pattern.matcher(inputStr);
        if(matcher.matches())
        {
            isValid = true;
        }
        return isValid;
    }
    private void tomarFotografia(int z) {
        File miFile=new File(Environment.getExternalStorageDirectory(),DIRECTORIO_IMAGEN);
        boolean isCreada=miFile.exists();

        if(!isCreada){
            isCreada=miFile.mkdirs();
        }

        if(isCreada){

            Long consecutivo= System.currentTimeMillis()/1000;
            String nombre=consecutivo.toString()+".jpg";
            path=Environment.getExternalStorageDirectory()+File.separator+DIRECTORIO_IMAGEN
                    +File.separator+nombre;//indicamos la ruta de almacenamiento


            fileImagen=new File(path);
            //Picasso.with(getBaseContext()).load("file:" + fileImagen).into(imgFoto);

            Intent intent=new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
            //intent.putExtra("orientation","portrait");
            intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));

            ////
            if(Build.VERSION.SDK_INT>=Build.VERSION_CODES.N)
            {
                String authorities=getApplicationContext().getPackageName()+".provider";
                imageUri= FileProvider.getUriForFile(getApplicationContext(),authorities,fileImagen);

                intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            }else
            {
                intent.putExtra(MediaStore.EXTRA_OUTPUT, Uri.fromFile(fileImagen));
            }
            if (z==1){
                startActivityForResult(intent,COD_FOTO);
            }
            if (z==2){
                startActivityForResult(intent,COD_FOTO1);
            }

        }
    }
    private void cargarImagen(int z) {
        if (z==1){
        Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
        startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA);
        }
        if (z==2){
            Intent intent=new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent.createChooser(intent,"Seleccione la Aplicación"),COD_SELECCIONA1);
        }
    }
    public Bitmap getProportionalBitmap(Bitmap bitmap,int newDimensionXorY,String XorY) {
        if (bitmap == null) {
            return null;
        }

        float xyRatio = 0;
        int newWidth = 0;
        int newHeight = 0;

        if (XorY.toLowerCase().equals("x")) {
            xyRatio = (float) newDimensionXorY / bitmap.getWidth();
            newHeight = (int) (bitmap.getHeight() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newDimensionXorY, newHeight, true);
        } else if (XorY.toLowerCase().equals("y")) {
            xyRatio = (float) newDimensionXorY / bitmap.getHeight();
            newWidth = (int) (bitmap.getWidth() * xyRatio);
            bitmap = Bitmap.createScaledBitmap(
                    bitmap, newWidth, newDimensionXorY, true);
        }
        return bitmap;
    }
    private Bitmap rotarBitmap(String Url, Bitmap bitmap) {
        Log.e("passss","paso por aqui");
        try {
            ExifInterface exifInterface = new ExifInterface(Url);
            int orientacion = exifInterface.getAttributeInt(
                    ExifInterface.TAG_ORIENTATION, 1);
            Matrix matrix = new Matrix();

            if (orientacion == 6) {
                matrix.postRotate(90);
            } else if (orientacion == 3) {
                matrix.postRotate(180);
            } else if (orientacion == 8) {
                matrix.postRotate(270);
            }
            Log.e("ro","rrortr");
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(),
                    bitmap.getHeight(), matrix, true); // rotating bitmap
        } catch (Exception e) {
            // TODO:
            Log.e("ro",""+e);
        }
        return bitmap;
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        switch (requestCode){
            case COD_SELECCIONA:
                if (resultCode==RESULT_OK) {
                    Uri miPath = data.getData();

                    iv_user_photo.setImageURI(miPath);


                    try {
                        bitmap = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), miPath);

                        //imgFoto.setImageBitmap(bitmap);
                        bitmap = getProportionalBitmap(bitmap, 720, "x");

                        ;
                        //Log.e("path",miPath.getEncodedPath());
                        iv_user_photo.setImageBitmap(bitmap);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                break;
            case COD_FOTO:
                if (resultCode==RESULT_OK){
                    MediaScannerConnection.scanFile(getBaseContext(), new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Path",""+path);
                                }
                            });

                    bitmap= BitmapFactory.decodeFile(path);
                    bitmap=rotarBitmap(""+path,bitmap);
                    //imgFoto.setImageBitmap(bitmap);

                    bitmap=getProportionalBitmap(bitmap,720,"x");
                    iv_user_photo.setImageBitmap(bitmap);
                }

                break;
            case COD_SELECCIONA1:
                if (resultCode==RESULT_OK) {
                    Uri miPath = data.getData();

                    iv_auto_photo.setImageURI(miPath);


                    try {
                        bitmap1 = MediaStore.Images.Media.getBitmap(getApplicationContext().getContentResolver(), miPath);

                        //imgFoto.setImageBitmap(bitmap);
                        bitmap1 = getProportionalBitmap(bitmap1, 720, "x");

                        ;
                        //Log.e("path",miPath.getEncodedPath());
                        iv_auto_photo.setImageBitmap(bitmap1);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                    tv_imau.setVisibility(View.GONE);
                }
                break;
            case  COD_FOTO1:
                if (resultCode==RESULT_OK){
                    MediaScannerConnection.scanFile(getBaseContext(), new String[]{path}, null,
                            new MediaScannerConnection.OnScanCompletedListener() {
                                @Override
                                public void onScanCompleted(String path, Uri uri) {
                                    Log.i("Path",""+path);
                                }
                            });

                    bitmap1= BitmapFactory.decodeFile(path);
                    bitmap1=rotarBitmap(""+path,bitmap1);
                    //imgFoto.setImageBitmap(bitmap);

                    bitmap1=getProportionalBitmap(bitmap1,720,"x");
                    iv_auto_photo.setImageBitmap(bitmap1);
                    tv_imau.setVisibility(View.GONE);
                }
                break;
            default:
                Log.e("er","no paso");
                break;

        }


    }
    public void esti(final int x){
        final CharSequence[] opcion={"Camara","Galeria"};
        final AlertDialog.Builder alertOpcion=new AlertDialog.Builder(edit_perfil.this);
        alertOpcion.setTitle("Seleccione tipo de unidad");
        alertOpcion.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {

                if (opcion[i].toString().equals("Camara")){
                    tomarFotografia(x);
                }
                if (opcion[i].toString().equals("Galeria")){
                    cargarImagen(x);
                }
            }
        });
        alertOpcion.show();
    }
    public String getStringImagen(Bitmap bmp){
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        bmp.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        byte[] imageBytes = baos.toByteArray();
        return Base64.encodeToString(imageBytes, Base64.DEFAULT);
    }

    @Override
    public void onClick(View v) {
        switch(v.getId()){
            case R.id.iv_user_photo:

                esti(1);

                break;
            case R.id.iv_auto_photo:
                esti(2);
                break;
            case R.id.tv_imau:
                esti(2);
                break;
            case R.id.edt_reg_dob:

                obtenerFecha(edt_reg_dob);

                break;

            case R.id.edt_reg_license_exp_date:

                obtenerFecha(edt_reg_license_exp_date);

                break;

            case R.id.edt_reg_cartype:

                cartype();

                break;

            case R.id.layout_back:
                int height = (int) getResources().getDimension(R.dimen.height_25);
                isLegalDetailsSelected=false;
                isVehicleDetailsSelected=false;

                RegisterStep = RegisterStep - 1;
                Log.d("RegisterStep","RegisterStep = "+RegisterStep+"=="+SecoundStepValidation);
                if(RegisterStep == 0) {
                    layout_header_step_first.setVisibility(View.VISIBLE);
                    layout_header_step_secound.setVisibility(View.GONE);
                    layout_header_step_thd.setVisibility(View.GONE);

                    Animation anv= AnimationUtils.loadAnimation(getBaseContext(),R.anim.pop_enter);
                    register_step_one.setAnimation(anv);
                    register_step_one.setVisibility(View.VISIBLE);
                    Animation ang= AnimationUtils.loadAnimation(getBaseContext(),R.anim.pop_exit);
                    register_step_two.setAnimation(ang);
                    register_step_two.setVisibility(View.GONE);
                    register_step_three.setVisibility(View.GONE);
                    layout_next.setVisibility(View.VISIBLE);
                    layout_back.setVisibility(View.GONE);


                    if(SecoundStepValidation){
                        Picasso.with(edit_perfil.this)
                                .load(R.drawable.icon_done)
                                .resize(height, height)
                                .into(icon_pending_first);
                    }else{
                        Picasso.with(edit_perfil.this)
                                .load(R.drawable.icon_pending)
                                .resize(height, height)
                                .into(icon_pending_first);
                    }
                    if(ThrdStepValidation){
                        Picasso.with(edit_perfil.this)
                                .load(R.drawable.icon_done)
                                .resize(height, height)
                                .into(icon_pending_fir_thd);
                    }else{
                        Picasso.with(edit_perfil.this)
                                .load(R.drawable.icon_pending)
                                .resize(height, height)
                                .into(icon_pending_fir_thd);
                    }

                }else if(RegisterStep == 1){
                    layout_header_step_first.setVisibility(View.GONE);
                    layout_header_step_secound.setVisibility(View.VISIBLE);
                    layout_header_step_thd.setVisibility(View.GONE);

                    register_step_one.setVisibility(View.GONE);
                    Animation anv= AnimationUtils.loadAnimation(getBaseContext(),R.anim.pop_enter);
                    register_step_two.setAnimation(anv);
                    register_step_two.setVisibility(View.VISIBLE);
                    Animation ang= AnimationUtils.loadAnimation(getBaseContext(),R.anim.pop_exit);
                    register_step_three.setAnimation(ang);
                    register_step_three.setVisibility(View.GONE);

                    layout_next.setVisibility(View.VISIBLE);
                    layout_back.setVisibility(View.VISIBLE);

                    if(ThrdStepValidation){
                        Picasso.with(edit_perfil.this)
                                .load(R.drawable.icon_done)
                                .resize(height, height)
                                .into(icon_pending_sec_thd);
                    }else{
                        Picasso.with(edit_perfil.this)
                                .load(R.drawable.icon_pending)
                                .resize(height, height)
                                .into(icon_pending_sec_thd);
                    }

                }else if(RegisterStep == 2){
                    layout_header_step_first.setVisibility(View.GONE);
                    layout_header_step_secound.setVisibility(View.GONE);
                    layout_header_step_thd.setVisibility(View.VISIBLE);

                    register_step_one.setVisibility(View.GONE);
                    register_step_two.setVisibility(View.GONE);
                    register_step_three.setVisibility(View.VISIBLE);
                    layout_next.setVisibility(View.GONE);
                    layout_back.setVisibility(View.VISIBLE);

                }
                break;

            case R.id.layout_next:
                boolean firstStep = PersonalDetailValidation();
                boolean secoundStep = VehicleDetailValidation();
                boolean thdStep = isValidLegalDetails();

                isVehicleDetailsSelected=true;
                Log.d("firstStap","firstStap = "+firstStep+"=="+secoundStep+"=="+RegisterStep);
                if(RegisterStep == 0) {
                    if (firstStep) {
                        layout_header_step_first.setVisibility(View.GONE);
                        layout_header_step_secound.setVisibility(View.VISIBLE);
                        layout_header_step_thd.setVisibility(View.GONE);

                        Animation ang= AnimationUtils.loadAnimation(getBaseContext(),R.anim.exit);
                        register_step_one.setAnimation(ang);
                        register_step_one.setVisibility(View.GONE);
                        Animation anv= AnimationUtils.loadAnimation(getBaseContext(),R.anim.enter);
                        register_step_two.setAnimation(anv);
                        register_step_two.setVisibility(View.VISIBLE);
                        register_step_three.setVisibility(View.GONE);

                        layout_next.setVisibility(View.VISIBLE);
                        layout_back.setVisibility(View.VISIBLE);
                        RegisterStep = RegisterStep + 1;
                        if(secoundStep){
                            Picasso.with(edit_perfil.this)
                                    .load(R.drawable.icon_done)
                                    .into(icon_pending_first);
                        }else{
                            Picasso.with(edit_perfil.this)
                                    .load(R.drawable.icon_pending)
                                    .into(icon_pending_first);
                        }
                        if(thdStep){
                            Picasso.with(edit_perfil.this)
                                    .load(R.drawable.icon_done)
                                    .into(icon_pending_fir_thd);
                        }else{
                            Picasso.with(edit_perfil.this)
                                    .load(R.drawable.icon_pending)
                                    .into(icon_pending_fir_thd);
                        }
                    }
                }else if(RegisterStep == 1) {
                    if (firstStep && secoundStep) {
                        RegisterStep = RegisterStep + 1;
                        layout_header_step_first.setVisibility(View.GONE);
                        layout_header_step_secound.setVisibility(View.GONE);
                        layout_header_step_thd.setVisibility(View.VISIBLE);

                        register_step_one.setVisibility(View.GONE);
                        Animation ang= AnimationUtils.loadAnimation(getBaseContext(),R.anim.exit);
                        register_step_two.setAnimation(ang);
                        register_step_two.setVisibility(View.GONE);
                        Animation anv= AnimationUtils.loadAnimation(getBaseContext(),R.anim.enter);
                        register_step_three.setAnimation(anv);
                        register_step_three.setVisibility(View.VISIBLE);

                        layout_back.setVisibility(View.VISIBLE);
                        layout_next.setVisibility(View.GONE);
                        if(thdStep){
                            Picasso.with(edit_perfil.this)
                                    .load(R.drawable.icon_done)
                                    .into(icon_pending_fir_thd);
                        }else{
                            Picasso.with(edit_perfil.this)
                                    .load(R.drawable.icon_pending)
                                    .into(icon_pending_fir_thd);
                        }
                        isLegalDetailsSelected=true;
                    }
                }
                break;
            case R.id.tv_signin_register:
                boolean firstStep1 = PersonalDetailValidation();
                boolean secoundStep1 = VehicleDetailValidation();
                boolean thdStep1 = isValidLegalDetails();

                if(firstStep1 && secoundStep1 && thdStep1) {
                    progressDialog = new ProgressDialog(edit_perfil.this);
                    progressDialog.setMessage("verifying..");
                    progressDialog.show();
                    String g = null;
                    if (genfem.isChecked()){
                        g="Femenino";
                    }else {
                        if (genmas.isChecked()){
                            g="Masculino";
                        }
                    }
                    editar(user.getUid(),edt_reg_name.getText().toString(),g,edt_reg_username.getText().toString(),edt_reg_address.getText().toString(),edt_reg_mobile.getText().toString(),edt_reg_dob.getText().toString(),edt_reg_driving_license.getText().toString(),edt_reg_license_exp_date.getText().toString(),edt_reg_seating_capacity.getText().toString(),edt_reg_carnumber.getText().toString(),edt_reg_camodel.getText().toString(),edt_reg_carmake.getText().toString(),edt_reg_cartype.getText().toString(),edt_reg_license_plate.getText().toString(),edt_reg_insuarance.getText().toString());
                }
                break;
        }
    }
    private void obtenerFecha(final TextInputEditText tx){
        DatePickerDialog recogerFecha = new DatePickerDialog(this, new DatePickerDialog.OnDateSetListener() {
            @Override
            public void onDateSet(DatePicker view, int year, int month, int dayOfMonth) {
                view.updateDate(1999,01,01);
                //Esta variable lo que realiza es aumentar en uno el mes ya que comienza desde 0 = enero
                final int mesActual = month + 1;
                //Formateo el día obtenido: antepone el 0 si son menores de 10
                String diaFormateado = (dayOfMonth < 10)? CERO + String.valueOf(dayOfMonth):String.valueOf(dayOfMonth);
                //Formateo el mes obtenido: antepone el 0 si son menores de 10
                String mesFormateado = (mesActual < 10)? CERO + String.valueOf(mesActual):String.valueOf(mesActual);
                //Muestro la fecha con el formato deseado
                String fe=year + GUION + mesFormateado + GUION + diaFormateado;
                tx.setText(fe);

            }
        },anio, mes, dia);
        //Muestro el widget
        recogerFecha.show();
    }

    public void cartype(){
        final CharSequence[] opcion={"Vagoneta","Furgoneta"};
        final AlertDialog.Builder alertOpcion=new AlertDialog.Builder(edit_perfil.this);
        alertOpcion.setTitle("Seleccione tipo de auto");
        alertOpcion.setItems(opcion, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                edt_reg_cartype.setText(opcion[i].toString());
            }
        });
        alertOpcion.show();
    }

    public void cargar() {
        Log.e("veer","entro guardar");

user=mAuth.getCurrentUser();
        Log.e("token_usu", user.getUid());


        HashMap<String, String> map = new HashMap<>();// Mapeo previo

        //tabla usuario
        map.put("token_usu", user.getUid());

        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor
        loading = ProgressDialog.show(edit_perfil.this,"Cargando...","Espere por favor...",false,false);
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.OBT_US,
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
                        Map<String, String> headers = new HashMap<>();
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
            switch (estado) {
                case "1": // EXITO
                    Log.e("ver","paso");
                    JSONObject mensajes = response.getJSONObject("msg");
                    Log.e("ver","entra-caso1 -"+mensajes);

                    Log.e("ver","TAMAÑO"+mensajes.length());

                        String x=mensajes.getString("genero");
                        if (x.equals("Masculino")){
                            genmas.setChecked(true);
                        }else{
                            genfem.setChecked(true);
                        }
                        edt_reg_name.setText(mensajes.getString("nombre"));
                        edt_reg_username.setText(mensajes.getString("user"));
                        edt_reg_address.setText(mensajes.getString("direccion"));
                        edt_reg_mobile.setText(mensajes.getString("movil"));
                        edt_reg_dob.setText(mensajes.getString("fe_nac"));
                        edt_reg_driving_license.setText(mensajes.getString("licencia"));
                        edt_reg_license_exp_date.setText(mensajes.getString("ven_licencia"));
                        edt_reg_seating_capacity.setText(mensajes.getString("capacidad"));
                        edt_reg_carnumber.setText(mensajes.getString("numero"));
                        edt_reg_camodel.setText(mensajes.getString("modelo"));
                        edt_reg_carmake.setText(mensajes.getString("marca"));
                        edt_reg_cartype.setText(mensajes.getString("tipo"));
                        edt_reg_license_plate.setText(mensajes.getString("placa"));
                        edt_reg_insuarance.setText(mensajes.getString("num_seguro"));
                        imgdir=Constantes.IMG_US+"/"+mensajes.getString("imagen");
                        imgdiraut=Constantes.IMG_AU+"/"+mensajes.getString("imagen_auto");
                      /* Picasso.Builder  builder = new Picasso.Builder(this);
                        builder.listener(new Picasso.Listener() {
                            @Override
                            public void onImageLoadFailed(Picasso picasso, Uri uri, Exception exception) {
                                Log.e("PicassoError",exception.toString());
                            }
                        });
                        builder.build().load(imgdir).fit().into(iv_user_photo);*/

                    Picasso.with(edit_perfil.this).load(imgdir)
                            .error(R.drawable.avatar)
                            .placeholder(R.color.blue_ligth)
                            .fit()
                            .into(iv_user_photo);
                    Picasso.with(edit_perfil.this).load(imgdiraut)
                            .error(R.drawable.avauto)
                            .placeholder(R.color.yellow)
                            .into(iv_auto_photo, new Callback() {
                                @Override
                                public void onSuccess() {
                                    tv_imau.setVisibility(View.GONE);
                                }

                                @Override
                                public void onError() {
                                    tv_imau.setVisibility(View.VISIBLE);
                                }
                            });
                        Log.e("URL",imgdir);
                    Log.e("URLAu",imgdiraut);

                    loading.dismiss();
                    break;
                case "2": // FALLIDO

                    String mensaje2 = response.getString("mensaje");
                    Log.e("ver","entra-caso2 -"+mensaje2);
                    loading.dismiss();
                    break;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }

    }

    public void editar(String tku,String nomb,String gen,String use,String dir,String mov,String fenac,String lic,String ven_lic,String cap,String num,String mod,String mar,String tip,String pla,String num_se) {

        nomb=nomb.trim();
        use=use.trim();
        dir=dir.trim();
        mov=mov.trim();
        lic=lic.trim();
        cap=cap.trim();
        num=num.trim();
        mod=mod.trim();
        mar=mar.trim();
        tip=tip.trim();
        pla=pla.trim();
        num_se=num_se.trim();
        if (bitmap==null){
            BitmapDrawable drawable = (BitmapDrawable) iv_user_photo.getDrawable();
            bitmap=drawable.getBitmap();
        }

        if (bitmap1==null){
            BitmapDrawable drawable1 = (BitmapDrawable) iv_auto_photo.getDrawable();
            bitmap1=drawable1.getBitmap();
        }

        Log.e("veer","entro guardar");
        imagen = getStringImagen(bitmap);
        imagen1 = getStringImagen(bitmap1);
        Log.e("token_usu",tku);
        Log.e("nombre", nomb);
        Log.e("genero",gen);
        Log.e("user", use);
        Log.e("direccion", dir);
        Log.e("movil", mov);
        Log.e("fe_nac", fenac);
        Log.e("licencia", lic);
        Log.e("ven_licencia", ven_lic);
        Log.e("imagen",imagen);
        //tabla auto
        Log.e("capacidad",cap);
        Log.e("numero",num);
        Log.e("modelo",mod);
        Log.e("marca",mar);
        Log.e("tipo",tip);
        Log.e("placa",pla);
        Log.e("num_seguro",num_se);
        Log.e("imagenau",imagen1);
        HashMap<String, String> map = new HashMap<>();// Mapeo previo
        //tabla usuario
        map.put("token_usu",tku);
        map.put("nombre", nomb);
        map.put("genero",gen);
        map.put("user", use);
        map.put("direccion", dir);
        map.put("movil", mov);
        map.put("fe_nac", fenac);
        map.put("licencia", lic);
        map.put("ven_licencia", ven_lic);
        map.put("image",imagen);
        map.put("imgdir",imgdir);
        map.put("image_auto",imagen1);
        map.put("imgdirau",imgdiraut);
        //tabla auto
        map.put("capacidad",cap);
        map.put("numero",num);
        map.put("modelo",mod);
        map.put("marca",mar);
        map.put("tipo",tip);
        map.put("placa",pla);
        map.put("num_seguro",num_se);

        JSONObject jobject = new JSONObject(map);
        // Actualizar datos en el servidor
        VolleySingleton.getInstance(getBaseContext()).addToRequestQueue(
                new JsonObjectRequest(
                        Request.Method.POST,
                        Constantes.UP_US,
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
                    FirebaseUser userre=FirebaseAuth.getInstance().getCurrentUser();
                    final String path;
                    if (userre != null) {
                        path = getString(R.string.firebase_path) + "/" + userre.getUid();
                        SharedPreferences settings = getSharedPreferences(Constantes.PREFS_NAME, 0);
                        final SharedPreferences.Editor editor = settings.edit();
                        editor.putString("id",userre.getUid());
                        editor.putString("nom",edt_reg_username.getText().toString());
                        editor.putString("pla",edt_reg_license_plate.getText().toString());
                        editor.apply();
                        DatabaseReference refp= FirebaseDatabase.getInstance().getReference(path+"/personal");
                        HashMap<String,String> map = new HashMap<String, String>();// Mapeo previo
                        map.put("user",edt_reg_username.getText().toString());
                        map.put("movil",edt_reg_mobile.getText().toString());
                        map.put("placa",edt_reg_license_plate.getText().toString());
                        map.put("tkfcm", FirebaseInstanceId.getInstance().getToken());
                        refp.setValue(map).addOnCompleteListener(new OnCompleteListener<Void>() {
                            @Override
                            public void onComplete(@NonNull Task<Void> task) {
                                if (task.isSuccessful()){
                                    finish();
                                }else{
                                    Toast.makeText(getBaseContext(), "Error al guardar usuario", Toast.LENGTH_SHORT).show();
                                }
                            }
                        });
                    }
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
