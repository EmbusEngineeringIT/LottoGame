package com.example.kums.lotto10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;

public class UserRegisterActivity extends AppCompatActivity implements View.OnClickListener {

    private EditText emailText,passwordText;
    private TextView regsiterButton;
    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private TextView haveAcccount;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature( Window.FEATURE_NO_TITLE );

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_user_register);
        firebaseAuth=FirebaseAuth.getInstance();
        progressDialog=new ProgressDialog(this);
        emailText=(EditText)findViewById(R.id.email_id_txt);
        passwordText=(EditText)findViewById(R.id.pwd_id_txt);
        haveAcccount=(TextView)findViewById(R.id.have_account);
        haveAcccount.setOnClickListener(this);
        regsiterButton=(TextView)findViewById(R.id.register_btn);
        regsiterButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        if (v==regsiterButton)
        {
            registerUser();
        }
        if (v==haveAcccount)
        {
            startActivity(new Intent(getApplicationContext(),LoginActivity.class));
            finish();
        }
    }

    private void registerUser()
    {



        String emailId=emailText.getText().toString().trim();
        String password=passwordText.getText().toString().trim();

        if (TextUtils.isEmpty(emailId))
        {
            Toast.makeText(this,"Email Id is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        if (TextUtils.isEmpty(password))
        {
            Toast.makeText(this,"Password is empty",Toast.LENGTH_SHORT).show();
            return;
        }
        progressDialog.setCancelable(false);
        progressDialog.setMessage("Registering User.........");
        progressDialog.show();

        firebaseAuth.createUserWithEmailAndPassword(emailId,password).addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful())
                {
                    progressDialog.dismiss();
                    finish();
                    startActivity(new Intent(getApplicationContext(),ProfileActivity.class));
                }
                else
                {
                    progressDialog.dismiss();
                    Toast.makeText(getApplicationContext(),"Registration Failure",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }
}
