package com.example.kums.lotto10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
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

public class LoginActivity extends AppCompatActivity implements View.OnClickListener{


    private ProgressDialog progressDialog;
    private FirebaseAuth firebaseAuth;
    private EditText emailLogin,passwordLogin;
    private TextView loginButton;
    private TextView createAccount,gustLogin;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature( Window.FEATURE_NO_TITLE );

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        setContentView(R.layout.activity_login);
        firebaseAuth=FirebaseAuth.getInstance();
        if (firebaseAuth.getCurrentUser() !=null)
        {
            startActivity(new Intent(getApplicationContext(),MainActivity.class));
        }
        progressDialog=new ProgressDialog(this);
        emailLogin=(EditText)findViewById(R.id.login_email_id_txt);
        passwordLogin=(EditText)findViewById(R.id.login_pwd_id_txt);
        createAccount=(TextView)findViewById(R.id.create_account);
        createAccount.setOnClickListener(this);
        loginButton=(TextView)findViewById(R.id.login_btn);
        loginButton.setOnClickListener(this);
        gustLogin=(TextView)findViewById(R.id.login_gust_login);
        gustLogin.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {

        if (v==loginButton)
        {
            userLogin();
        }
        if (v==createAccount)
        {
            startActivity(new Intent(getApplicationContext(),UserRegisterActivity.class));
            finish();
        }
        if (v == gustLogin)
        {
            SharedPreferences sharedPreferences=this.getSharedPreferences(LoginOptions.mypreference,MODE_PRIVATE);
            SharedPreferences.Editor editor=sharedPreferences.edit();
            editor.putInt(LoginOptions.SHAR_VALUE,2);
            editor.commit();
            startActivity(new Intent(this,GustLogin.class));
            finish();
        }
    }

    private void userLogin() {

        String emailId=emailLogin.getText().toString().trim();
        String password=passwordLogin.getText().toString().trim();

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
        progressDialog.setMessage("User Logging.........");
        progressDialog.show();

        firebaseAuth.signInWithEmailAndPassword(emailId,password).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {

                progressDialog.dismiss();
                if (task.isSuccessful())
                {
                    finish();
                    startActivity(new Intent(getApplicationContext(),MainActivity.class));
                }
                else
                {
                   Toast.makeText(getApplicationContext(),"Invalid User Name And Password",Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    protected void onPause() {
        super.onPause();

    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
