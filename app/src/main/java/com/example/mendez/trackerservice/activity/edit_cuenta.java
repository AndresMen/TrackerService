package com.example.mendez.trackerservice.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;

import com.example.mendez.trackerservice.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class edit_cuenta extends AppCompatActivity implements View.OnClickListener {

    FirebaseAuth mAuth;
    FirebaseUser user;
    EditText edt_reg_email,edt_reg_password,edt_reg_confirmpassword;
    ImageView ivema,ivpass;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_cuenta2);
        setTitle("Editar cuenta");


        mAuth=FirebaseAuth.getInstance();
        user=mAuth.getCurrentUser();

        ivema=(ImageView)findViewById(R.id.ivema);
        ivpass=(ImageView)findViewById(R.id.ivpas);
        ivema.setOnClickListener(this);
        ivpass.setOnClickListener(this);

        edt_reg_email=(EditText)findViewById(R.id.editemail);
        edt_reg_email.setText(user.getEmail());

    }


    @Override
    public void onClick(View v) {
        Intent nep=new Intent(getBaseContext(),edtda.class);
        switch (v.getId()){
            case R.id.ivema:
                //Toast.makeText(getBaseContext(),"Clik en editar email",Toast.LENGTH_SHORT).show();
                nep.putExtra("x","1");
                startActivity(nep);
                finish();
                break;
            case R.id.ivpas:
                //Toast.makeText(getBaseContext(),"Clik en editar password",Toast.LENGTH_SHORT).show();
                nep.putExtra("x","2");
                startActivity(nep);
                finish();
                break;
        }
    }
}
