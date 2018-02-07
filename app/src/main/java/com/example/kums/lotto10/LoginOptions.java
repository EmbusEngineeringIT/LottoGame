package com.example.kums.lotto10;

import android.app.ProgressDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.FacebookSdk;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.google.android.gms.auth.api.Auth;
import com.google.android.gms.auth.api.signin.GoogleSignIn;
import com.google.android.gms.auth.api.signin.GoogleSignInAccount;
import com.google.android.gms.auth.api.signin.GoogleSignInOptions;
import com.google.android.gms.auth.api.signin.GoogleSignInResult;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.ApiException;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FacebookAuthProvider;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GoogleAuthProvider;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.Signature;
import java.util.ArrayList;
import java.util.Arrays;

import es.dmoral.toasty.Toasty;

public class LoginOptions extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout signInButton ,fabBookLogin,gustLogin;
    public static final int RC_SIGN_IN=3;
    private GoogleApiClient googleApiClient;
    private FirebaseAuth mAuth;
    private ProgressDialog progressDialog;
    private CallbackManager callbackManager;
    public static final String SHAR_VALUE="123";
    private static int LOGINOPTION=0;
    private SharedPreferences sharedPreferences;
    public static final String mypreference = "mypref";
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature( Window.FEATURE_NO_TITLE );

        getWindow().setFlags( WindowManager.LayoutParams.FLAG_FULLSCREEN,
                WindowManager.LayoutParams.FLAG_FULLSCREEN );
        mAuth=FirebaseAuth.getInstance();
        FacebookSdk.sdkInitialize(getApplicationContext());
        setContentView(R.layout.activity_login_options);

        sharedPreferences=this.getSharedPreferences(mypreference,MODE_PRIVATE);

      //  updateUIMethod();
        progressDialog=new ProgressDialog(this);
        progressDialog.setMessage("Please Wait Logging ...");
        progressDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        progressDialog.setCancelable(false);
        //Gmail Login
        signInButton=(LinearLayout)findViewById(R.id.g_mail_sign);
        signInButton.setOnClickListener(this);
        GoogleSignInOptions gso = new GoogleSignInOptions.Builder(GoogleSignInOptions.DEFAULT_SIGN_IN)
                .requestIdToken(getString(R.string.default_web_client_id))
                .requestEmail()
                .build();

        FirebaseUser firebaseUser=mAuth.getCurrentUser();
        if(firebaseUser !=null)
        {
            updateUIMethod();
        }

        googleApiClient=new GoogleApiClient.Builder(this)
                .enableAutoManage(this, new GoogleApiClient.OnConnectionFailedListener() {
            @Override
            public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                Toasty.error(getApplicationContext(),"Something Wrong See Properly", Toast.LENGTH_SHORT).show();
            }
        }).addApi(Auth.GOOGLE_SIGN_IN_API,gso).build();

        //FaceBook Login

        callbackManager=CallbackManager.Factory.create();
        fabBookLogin=(LinearLayout)findViewById(R.id.fb_sign_in);
        fabBookLogin.setOnClickListener(this);

        gustLogin=(LinearLayout)findViewById(R.id.gust_login);
        gustLogin.setOnClickListener(this);
    }

    private void signIn()
    {
        Intent signInIntent = Auth.GoogleSignInApi.getSignInIntent(googleApiClient);
        startActivityForResult(signInIntent, RC_SIGN_IN);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        // Result returned from launching the Intent from GoogleSignInApi.getSignInIntent(...);
        if (requestCode == RC_SIGN_IN) {
            Task<GoogleSignInAccount> task = GoogleSignIn.getSignedInAccountFromIntent(data);
            try {
                progressDialog.show();
                // Google Sign In was successful, authenticate with Firebase
                GoogleSignInAccount account = task.getResult(ApiException.class);
                firebaseAuthWithGoogle(account);
            } catch (ApiException e) {
                // Google Sign In failed, update UI appropriately
                //Log.w(TAG, "Google sign in failed", e);
                // ...
            }
        }
        else
        {
            callbackManager.onActivityResult(requestCode,resultCode,data);
        }
    }

    private void firebaseAuthWithGoogle(GoogleSignInAccount account) {
        AuthCredential credential = GoogleAuthProvider.getCredential(account.getIdToken(), null);
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                       //     Log.d(TAG, "signInWithCredential:success");
                            FirebaseUser user = mAuth.getCurrentUser();

                            if (mAuth.getCurrentUser()!=null)
                            {
                                //Toasty.success(getApplicationContext(),"User Name :"+mAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                                updateUIMethod();
                                //startActivity(new Intent(getApplicationContext(),GameHome.class));
                            }
                        }
                        else
                            {
                        }
                    }
                });
    }

    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.g_mail_sign:
                sharedPreferenceLoginOption();
                signIn();
                break;

            case R.id.fb_sign_in:
                sharedPreferenceLoginOption();
                faceBookLoginFunction();
                break;

            case R.id.gust_login:
                gustSharedPrefence();
                startActivity(new Intent(this,LoginActivity.class));
                finish();
                break;
        }
    }

    public void sharedPreferenceLoginOption()
    {
        LOGINOPTION=0;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(SHAR_VALUE,LOGINOPTION);
        editor.commit();
    }

    public void gustSharedPrefence()
    {
        LOGINOPTION=1;
        SharedPreferences.Editor editor=sharedPreferences.edit();
        editor.putInt(SHAR_VALUE,LOGINOPTION);
        editor.commit();
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (mAuth.getCurrentUser()!=null)
        {
            updateUIMethod();
        }
    }

    public void updateUIMethod()
    {
        progressDialog.dismiss();
        Intent intent=new Intent(LoginOptions.this,MainActivity.class);
        startActivity(intent);
        finish();
    }

    public void faceBookLoginFunction()
    {
        progressDialog.show();
        LoginManager.getInstance().logInWithReadPermissions(LoginOptions.this, Arrays.asList("email","public_profile"));
        LoginManager.getInstance().registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                handleFacebookAccessToken(loginResult.getAccessToken());
            }

            @Override
            public void onCancel() {

            }

            @Override
            public void onError(FacebookException error) {

            }
        });
    }

    private void handleFacebookAccessToken(AccessToken token) {
      //  Log.d(TAG, "handleFacebookAccessToken:" + token);

        AuthCredential credential = FacebookAuthProvider.getCredential(token.getToken());
        mAuth.signInWithCredential(credential)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful())
                        {
                            progressDialog.dismiss();
                            FirebaseUser user = mAuth.getCurrentUser();
                           if (mAuth.getCurrentUser()!=null)
                            {
                                updateUIMethod();
                            }
                        } else {
                        }
                    }
                });
    }
}
