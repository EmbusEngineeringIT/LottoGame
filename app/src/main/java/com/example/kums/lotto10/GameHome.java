package com.example.kums.lotto10;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;

import es.dmoral.toasty.Toasty;

public class GameHome extends AppCompatActivity {

    private Button logout;
    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_home);
        mAuth=FirebaseAuth.getInstance();
        logout=(Button)findViewById(R.id.g_mail_logout);
       // authStateListener= (FirebaseAuth.AuthStateListener) FirebaseAuth.getInstance();
       /* authStateListener=new FirebaseAuth.AuthStateListener() {
            @Override
            public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {


            }
        };*/

        logout.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //Toasty.success(getApplicationContext(),"User Name :"+firebaseAuth.getCurrentUser().getDisplayName(), Toast.LENGTH_SHORT).show();
                signOut();
                if (mAuth.getCurrentUser()==null)
                {
                    Intent intent=new Intent(GameHome.this,LoginOptions.class);
                    startActivity(intent);
                }
            }
        });

    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    public void signOut()
    {
        mAuth.signOut();
    }
}
